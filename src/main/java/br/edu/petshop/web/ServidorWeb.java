package br.edu.petshop.web;

import static java.nio.charset.StandardCharsets.UTF_8;

import br.edu.petshop.dominio.Agendamento;
import br.edu.petshop.dominio.Pet;
import br.edu.petshop.dominio.Tutor;
import br.edu.petshop.servico.CatalogoServicos;
import br.edu.petshop.servico.CatalogoServicos.OpcaoAdicional;
import br.edu.petshop.servico.CatalogoServicos.OpcaoServico;
import br.edu.petshop.servico.Servico;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public final class ServidorWeb {
    private static final DateTimeFormatter FORMATO_HORARIO = DateTimeFormatter
            .ofPattern("dd/MM/uuuu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final Path PASTA_WEB = Path.of("web").toAbsolutePath().normalize();
    private static final int LIMITE_CORPO = 8192;
    private static final List<Agendamento> AGENDAMENTOS = new CopyOnWriteArrayList<>();

    private ServidorWeb() {
    }

    public static void main(String[] args) throws IOException {
        int porta = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        HttpServer servidor = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), porta), 0);
        servidor.createContext("/api/catalogo", tratar(ServidorWeb::catalogo));
        servidor.createContext("/api/orcamento", tratar(ServidorWeb::orcamento));
        servidor.createContext("/api/agendamentos", tratar(ServidorWeb::agendamentos));
        servidor.createContext("/", tratar(ServidorWeb::arquivo));
        servidor.start();

        if (!Files.isDirectory(PASTA_WEB)) {
            System.out.println("Aviso: pasta web/ não encontrada. Execute o servidor na raiz do repositório.");
        }
        System.out.println("Pet Shop web em http://localhost:" + servidor.getAddress().getPort()
                + " (Ctrl+C para encerrar)");
    }

    private static Resposta catalogo(HttpExchange troca) {
        exigir(troca, "/api/catalogo", "GET");
        String servicos = CatalogoServicos.servicos().stream()
                .map(opcao -> {
                    Servico servico = opcao.orcar(List.of());
                    return item(opcao.id(), servico.descricao(), servico.preco());
                })
                .collect(Collectors.joining(",", "[", "]"));
        String adicionais = CatalogoServicos.adicionais().stream()
                .map(opcao -> item(opcao.id(), opcao.nome(), opcao.preco()))
                .collect(Collectors.joining(",", "[", "]"));
        return Resposta.json(200, "{\"servicos\":" + servicos + ",\"adicionais\":" + adicionais + "}");
    }

    private static Resposta orcamento(HttpExchange troca) {
        exigir(troca, "/api/orcamento", "GET");
        Map<String, List<String>> parametros = lerParametros(troca.getRequestURI().getRawQuery());
        Servico servico = buscarServico(parametros).orcar(buscarAdicionais(parametros));
        return Resposta.json(200, "{\"servico\":" + aspas(servico.descricao())
                + ",\"total\":" + servico.preco().toPlainString() + "}");
    }

    private static Resposta agendamentos(HttpExchange troca) throws IOException {
        exigir(troca, "/api/agendamentos", "GET", "POST");
        if (troca.getRequestMethod().equals("GET")) {
            return Resposta.json(200, AGENDAMENTOS.stream()
                    .map(ServidorWeb::json)
                    .collect(Collectors.joining(",", "[", "]")));
        }

        byte[] corpo = troca.getRequestBody().readNBytes(LIMITE_CORPO + 1);
        if (corpo.length > LIMITE_CORPO) {
            throw new RequisicaoInvalida(413, "Os dados do agendamento são muito longos.");
        }
        Map<String, List<String>> parametros = lerParametros(new String(corpo, UTF_8));
        Tutor tutor = new Tutor(obrigatorio(parametros, "tutor", "Informe o nome do tutor."));
        Pet pet = new Pet(obrigatorio(parametros, "pet", "Informe o nome do pet."), tutor);
        LocalDateTime horario = lerHorario(obrigatorio(parametros, "horario", "Informe a data e o horário."));
        OpcaoServico servico = buscarServico(parametros);

        Agendamento agendamento = servico.agendar(pet, horario, buscarAdicionais(parametros));
        AGENDAMENTOS.add(agendamento);
        return Resposta.json(201, json(agendamento));
    }

    private static Resposta arquivo(HttpExchange troca) throws IOException {
        if (!troca.getRequestMethod().equals("GET")) {
            throw new RequisicaoInvalida(405, "Método não permitido.");
        }
        String caminho = troca.getRequestURI().getPath();
        if (caminho.endsWith("/")) {
            caminho += "index.html";
        }
        Path arquivo = PASTA_WEB.resolve(caminho.substring(1)).normalize();
        if (!arquivo.startsWith(PASTA_WEB) || !Files.isRegularFile(arquivo)) {
            return new Resposta(404, "text/plain; charset=utf-8", "Página não encontrada.".getBytes(UTF_8));
        }
        return new Resposta(200, tipoDoArquivo(arquivo), Files.readAllBytes(arquivo));
    }

    private static OpcaoServico buscarServico(Map<String, List<String>> parametros) {
        String id = obrigatorio(parametros, "servico", "Escolha um serviço.");
        return CatalogoServicos.buscarServico(id);
    }

    private static List<OpcaoAdicional> buscarAdicionais(Map<String, List<String>> parametros) {
        return CatalogoServicos.buscarAdicionais(parametros.getOrDefault("adicional", List.of()));
    }

    private static String obrigatorio(Map<String, List<String>> parametros, String nome, String mensagem) {
        List<String> valores = parametros.getOrDefault(nome, List.of());
        String valor = valores.isEmpty() ? "" : valores.get(0).strip();
        if (valor.isEmpty()) {
            throw new RequisicaoInvalida(400, mensagem);
        }
        return valor;
    }

    private static LocalDateTime lerHorario(String texto) {
        try {
            return LocalDateTime.parse(texto);
        } catch (DateTimeParseException ex) {
            throw new RequisicaoInvalida(400, "Data ou hora inválida.");
        }
    }

    private static Map<String, List<String>> lerParametros(String bruto) {
        Map<String, List<String>> parametros = new HashMap<>();
        if (bruto == null || bruto.isEmpty()) {
            return parametros;
        }
        for (String par : bruto.split("&")) {
            if (par.isEmpty()) {
                continue;
            }
            int igual = par.indexOf('=');
            String chave;
            String valor;
            try {
                chave = URLDecoder.decode(igual < 0 ? par : par.substring(0, igual), UTF_8);
                valor = igual < 0 ? "" : URLDecoder.decode(par.substring(igual + 1), UTF_8);
            } catch (IllegalArgumentException ex) {
                throw new RequisicaoInvalida(400, "Requisição inválida.");
            }
            parametros.computeIfAbsent(chave, k -> new ArrayList<>()).add(valor);
        }
        return parametros;
    }

    private static void exigir(HttpExchange troca, String caminho, String... metodos) {
        if (!troca.getRequestURI().getPath().equals(caminho)) {
            throw new RequisicaoInvalida(404, "Rota não encontrada.");
        }
        if (!List.of(metodos).contains(troca.getRequestMethod())) {
            throw new RequisicaoInvalida(405, "Método não permitido.");
        }
    }

    private static HttpHandler tratar(Rota rota) {
        return troca -> {
            Resposta resposta;
            try {
                resposta = rota.responder(troca);
            } catch (RequisicaoInvalida ex) {
                resposta = Resposta.json(ex.status, "{\"erro\":" + aspas(ex.getMessage()) + "}");
            } catch (IllegalArgumentException ex) {
                String mensagem = ex.getMessage() == null ? "Requisição inválida." : ex.getMessage();
                resposta = Resposta.json(400, "{\"erro\":" + aspas(mensagem) + "}");
            } catch (RuntimeException | IOException ex) {
                ex.printStackTrace();
                resposta = Resposta.json(500, "{\"erro\":\"Erro interno no servidor.\"}");
            }
            try {
                troca.getResponseHeaders().set("Content-Type", resposta.tipo());
                troca.getResponseHeaders().set("Cache-Control", "no-store");
                troca.sendResponseHeaders(resposta.status(), resposta.corpo().length);
                troca.getResponseBody().write(resposta.corpo());
            } finally {
                troca.close();
            }
        };
    }

    private static String json(Agendamento agendamento) {
        return "{\"tutor\":" + aspas(agendamento.pet().tutor().nome())
                + ",\"pet\":" + aspas(agendamento.pet().nome())
                + ",\"horario\":" + aspas(agendamento.horario().format(FORMATO_HORARIO))
                + ",\"servico\":" + aspas(agendamento.servico().descricao())
                + ",\"total\":" + agendamento.servico().preco().toPlainString() + "}";
    }

    private static String item(String id, String nome, BigDecimal preco) {
        return "{\"id\":" + aspas(id) + ",\"nome\":" + aspas(nome) + ",\"preco\":" + preco.toPlainString() + "}";
    }

    private static String aspas(String texto) {
        StringBuilder saida = new StringBuilder("\"");
        for (char c : texto.toCharArray()) {
            switch (c) {
                case '"' -> saida.append("\\\"");
                case '\\' -> saida.append("\\\\");
                case '\n' -> saida.append("\\n");
                case '\r' -> saida.append("\\r");
                case '\t' -> saida.append("\\t");
                default -> {
                    if (c < 0x20) {
                        saida.append(String.format("\\u%04x", (int) c));
                    } else {
                        saida.append(c);
                    }
                }
            }
        }
        return saida.append('"').toString();
    }

    private static String tipoDoArquivo(Path arquivo) {
        String nome = arquivo.getFileName().toString();
        String extensao = nome.substring(nome.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        return switch (extensao) {
            case "html" -> "text/html; charset=utf-8";
            case "css" -> "text/css; charset=utf-8";
            case "js" -> "text/javascript; charset=utf-8";
            case "svg" -> "image/svg+xml";
            case "png" -> "image/png";
            case "ico" -> "image/x-icon";
            default -> "application/octet-stream";
        };
    }

    @FunctionalInterface
    private interface Rota {
        Resposta responder(HttpExchange troca) throws IOException;
    }

    private record Resposta(int status, String tipo, byte[] corpo) {
        static Resposta json(int status, String corpo) {
            return new Resposta(status, "application/json; charset=utf-8", corpo.getBytes(UTF_8));
        }
    }

    private static final class RequisicaoInvalida extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private final int status;

        RequisicaoInvalida(int status, String mensagem) {
            super(mensagem);
            this.status = status;
        }
    }
}
