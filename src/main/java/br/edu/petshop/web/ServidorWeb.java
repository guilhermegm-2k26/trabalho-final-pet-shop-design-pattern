package br.edu.petshop.web;

import static java.nio.charset.StandardCharsets.UTF_8;

import br.edu.petshop.dominio.Agendamento;
import br.edu.petshop.dominio.Pet;
import br.edu.petshop.dominio.Tutor;
import br.edu.petshop.servico.AgendamentoFactory;
import br.edu.petshop.servico.BanhoFactory;
import br.edu.petshop.servico.CorteDeUnhas;
import br.edu.petshop.servico.Servico;
import br.edu.petshop.servico.TosaFactory;
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
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Servidor da interface web, feito só com a biblioteca padrão do JDK.
 * Serve os arquivos da pasta {@code web/} e expõe uma API pequena que usa
 * as mesmas fábricas (Factory Method) e adicionais (Decorator) do terminal.
 */
public final class ServidorWeb {
    /** Serviços base exibidos na página. Um novo serviço entra aqui com a sua fábrica. */
    private static final List<OpcaoServico> SERVICOS = List.of(
            new OpcaoServico("banho", new BanhoFactory()),
            new OpcaoServico("tosa", new TosaFactory()));

    /** Adicionais exibidos na página. Um novo adicional entra aqui com o seu decorator. */
    private static final List<OpcaoAdicional> ADICIONAIS = List.of(
            new OpcaoAdicional("corte-unhas", "Corte de unhas", CorteDeUnhas::new));

    private static final DateTimeFormatter FORMATO_HORARIO = DateTimeFormatter
            .ofPattern("dd/MM/uuuu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final Path PASTA_WEB = Path.of("web").toAbsolutePath().normalize();
    private static final Pet PET_SIMULACAO = new Pet("Simulação", new Tutor("Simulação"));
    private static final Servico SERVICO_VAZIO = new Servico() {
        @Override
        public String descricao() {
            return "";
        }

        @Override
        public BigDecimal preco() {
            return BigDecimal.ZERO;
        }
    };
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
        System.out.println("Pet Shop web em http://localhost:" + porta + " (Ctrl+C para encerrar)");
    }

    private static Resposta catalogo(HttpExchange troca) {
        exigir(troca, "/api/catalogo", "GET");
        String servicos = SERVICOS.stream()
                .map(opcao -> {
                    Servico servico = simular(opcao, List.of());
                    return item(opcao.id(), servico.descricao(), servico.preco());
                })
                .collect(Collectors.joining(",", "[", "]"));
        String adicionais = ADICIONAIS.stream()
                .map(opcao -> item(opcao.id(), opcao.nome(), opcao.decorador().apply(SERVICO_VAZIO).preco()))
                .collect(Collectors.joining(",", "[", "]"));
        return Resposta.json(200, "{\"servicos\":" + servicos + ",\"adicionais\":" + adicionais + "}");
    }

    private static Resposta orcamento(HttpExchange troca) {
        exigir(troca, "/api/orcamento", "GET");
        Map<String, List<String>> parametros = lerParametros(troca.getRequestURI().getRawQuery());
        Servico servico = simular(buscarServico(parametros), buscarAdicionais(parametros));
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

        Map<String, List<String>> parametros = lerParametros(new String(troca.getRequestBody().readAllBytes(), UTF_8));
        Tutor tutor = new Tutor(obrigatorio(parametros, "tutor", "Informe o nome do tutor."));
        Pet pet = new Pet(obrigatorio(parametros, "pet", "Informe o nome do pet."), tutor);
        LocalDateTime horario = lerHorario(obrigatorio(parametros, "horario", "Informe a data e o horário."));
        OpcaoServico servico = buscarServico(parametros);

        Agendamento agendamento = servico.fabrica()
                .agendar(pet, horario, personalizacao(buscarAdicionais(parametros)));
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

    /** criarServico() é protegido, então a simulação passa pelo mesmo fluxo público do Factory Method. */
    private static Servico simular(OpcaoServico opcao, List<OpcaoAdicional> adicionais) {
        return opcao.fabrica().agendar(PET_SIMULACAO, LocalDateTime.now(), personalizacao(adicionais)).servico();
    }

    /** Envolve o serviço base com cada adicional escolhido, na ordem recebida (Decorator). */
    private static UnaryOperator<Servico> personalizacao(List<OpcaoAdicional> adicionais) {
        return servico -> {
            Servico decorado = servico;
            for (OpcaoAdicional adicional : adicionais) {
                decorado = adicional.decorador().apply(decorado);
            }
            return decorado;
        };
    }

    private static OpcaoServico buscarServico(Map<String, List<String>> parametros) {
        String id = obrigatorio(parametros, "servico", "Escolha um serviço.");
        return SERVICOS.stream()
                .filter(opcao -> opcao.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new RequisicaoInvalida(400, "Serviço desconhecido: " + id + "."));
    }

    private static List<OpcaoAdicional> buscarAdicionais(Map<String, List<String>> parametros) {
        List<OpcaoAdicional> escolhidos = new ArrayList<>();
        for (String id : parametros.getOrDefault("adicional", List.of())) {
            OpcaoAdicional adicional = ADICIONAIS.stream()
                    .filter(opcao -> opcao.id().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RequisicaoInvalida(400, "Adicional desconhecido: " + id + "."));
            if (!escolhidos.contains(adicional)) {
                escolhidos.add(adicional);
            }
        }
        return escolhidos;
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
            String chave = URLDecoder.decode(igual < 0 ? par : par.substring(0, igual), UTF_8);
            String valor = igual < 0 ? "" : URLDecoder.decode(par.substring(igual + 1), UTF_8);
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
                resposta = Resposta.json(400, "{\"erro\":\"Requisição inválida.\"}");
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

    private record OpcaoServico(String id, AgendamentoFactory fabrica) {
    }

    private record OpcaoAdicional(String id, String nome, UnaryOperator<Servico> decorador) {
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
        private final int status;

        RequisicaoInvalida(int status, String mensagem) {
            super(mensagem);
            this.status = status;
        }
    }
}
