package br.edu.petshop;

import br.edu.petshop.dominio.Agendamento;
import br.edu.petshop.dominio.Pet;
import br.edu.petshop.dominio.Tutor;
import br.edu.petshop.servico.CatalogoServicos;
import br.edu.petshop.servico.CatalogoServicos.OpcaoAdicional;
import br.edu.petshop.servico.CatalogoServicos.OpcaoServico;
import br.edu.petshop.servico.Servico;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class Main {
    private static final DateTimeFormatter FORMATO_HORARIO = DateTimeFormatter
            .ofPattern("dd/MM/uuuu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    public static void main(String[] args) {
        NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
        try (Scanner entrada = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.println("Pet Shop | Novo agendamento");

            Tutor tutor = new Tutor(lerNome(entrada, "Nome do tutor: "));
            Pet pet = new Pet(lerNome(entrada, "Nome do pet: "), tutor);
            LocalDateTime horario = lerHorario(entrada);

            OpcaoServico servico = lerServico(entrada, moeda);
            List<OpcaoAdicional> adicionais = lerAdicionais(entrada, moeda);
            exibirResumo(servico.agendar(pet, horario, adicionais), moeda);
        } catch (NoSuchElementException ex) {
            System.out.println("\nAgendamento cancelado. A entrada foi encerrada.");
        } catch (IllegalArgumentException ex) {
            System.out.println("\nAgendamento não confirmado. " + ex.getMessage());
        }
    }

    private static String lerNome(Scanner entrada, String pergunta) {
        while (true) {
            System.out.print(pergunta);
            String nome = entrada.nextLine().strip();
            if (!nome.isEmpty()) {
                return nome;
            }
            System.out.println("Informe um nome.");
        }
    }

    private static LocalDateTime lerHorario(Scanner entrada) {
        while (true) {
            System.out.print("Data e hora (dd/mm/aaaa hh:mm): ");
            try {
                LocalDateTime horario = LocalDateTime.parse(entrada.nextLine().strip(), FORMATO_HORARIO);
                Agendamento.validarHorario(horario);
                return horario;
            } catch (DateTimeParseException ex) {
                System.out.println("Data ou hora inválida. Exemplo: 20/11/2030 14:30.");
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private static OpcaoServico lerServico(Scanner entrada, NumberFormat moeda) {
        List<OpcaoServico> opcoes = CatalogoServicos.servicos();
        for (int i = 0; i < opcoes.size(); i++) {
            Servico servico = opcoes.get(i).orcar(List.of());
            System.out.printf("%d - %s (%s)%n", i + 1, servico.descricao(), moeda.format(servico.preco()));
        }

        while (true) {
            System.out.print("Serviço: ");
            String opcao = entrada.nextLine().strip();
            for (int i = 0; i < opcoes.size(); i++) {
                if (opcao.equals(Integer.toString(i + 1))) {
                    return opcoes.get(i);
                }
            }
            System.out.println("Escolha um dos serviços da lista.");
        }
    }

    private static List<OpcaoAdicional> lerAdicionais(Scanner entrada, NumberFormat moeda) {
        List<OpcaoAdicional> escolhidos = new ArrayList<>();
        for (OpcaoAdicional adicional : CatalogoServicos.adicionais()) {
            if (confirmarAdicional(entrada, adicional, moeda)) {
                escolhidos.add(adicional);
            }
        }
        return escolhidos;
    }

    private static boolean confirmarAdicional(Scanner entrada, OpcaoAdicional adicional, NumberFormat moeda) {
        while (true) {
            System.out.printf("Incluir %s (+ %s)? (s/n): ",
                    adicional.nome().toLowerCase(Locale.ROOT), moeda.format(adicional.preco()));
            String resposta = entrada.nextLine().strip();
            if (resposta.equalsIgnoreCase("s") || resposta.equalsIgnoreCase("n")) {
                return resposta.equalsIgnoreCase("s");
            }
            System.out.println("Digite s para incluir ou n para continuar.");
        }
    }

    private static void exibirResumo(Agendamento agendamento, NumberFormat moeda) {
        System.out.println("\nAgendamento confirmado");
        System.out.println("Tutor: " + agendamento.pet().tutor().nome());
        System.out.println("Pet: " + agendamento.pet().nome());
        System.out.println("Horário: " + agendamento.horario().format(FORMATO_HORARIO));
        System.out.println("Serviço: " + agendamento.servico().descricao());
        System.out.println("Total: " + moeda.format(agendamento.servico().preco()));
    }
}
