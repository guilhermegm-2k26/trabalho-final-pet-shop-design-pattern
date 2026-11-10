package br.edu.petshop;

import br.edu.petshop.dominio.Agendamento;
import br.edu.petshop.dominio.Pet;
import br.edu.petshop.dominio.Tutor;
import br.edu.petshop.servico.AgendamentoFactory;
import br.edu.petshop.servico.BanhoFactory;
import br.edu.petshop.servico.CorteDeUnhas;
import br.edu.petshop.servico.Servico;
import br.edu.petshop.servico.TosaFactory;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.UnaryOperator;

public final class Main {
    private static final DateTimeFormatter FORMATO_HORARIO = DateTimeFormatter
            .ofPattern("dd/MM/uuuu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.println("Pet Shop | Novo agendamento");

            Tutor tutor = new Tutor(lerNome(entrada, "Nome do tutor: "));
            Pet pet = new Pet(lerNome(entrada, "Nome do pet: "), tutor);
            LocalDateTime horario = lerHorario(entrada);

            System.out.println("1 - Banho\n2 - Tosa");
            AgendamentoFactory fabrica = lerOpcao(entrada, "Serviço: ", "1", "2").equals("1")
                    ? new BanhoFactory() : new TosaFactory();

            UnaryOperator<Servico> personalizar = lerOpcao(entrada, "Incluir corte de unhas? (s/n): ", "s", "n")
                    .equalsIgnoreCase("s") ? CorteDeUnhas::new : UnaryOperator.identity();

            exibirResumo(fabrica.agendar(pet, horario, personalizar));
        } catch (NoSuchElementException ex) {
            System.out.println("\nAgendamento cancelado. A entrada foi encerrada.");
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
                return LocalDateTime.parse(entrada.nextLine().strip(), FORMATO_HORARIO);
            } catch (DateTimeParseException ex) {
                System.out.println("Data ou hora inválida. Exemplo: 20/11/2030 14:30.");
            }
        }
    }

    private static String lerOpcao(Scanner entrada, String pergunta, String primeira, String segunda) {
        while (true) {
            System.out.print(pergunta);
            String opcao = entrada.nextLine().strip();
            if (opcao.equalsIgnoreCase(primeira) || opcao.equalsIgnoreCase(segunda)) {
                return opcao;
            }
            System.out.printf("Escolha %s ou %s.%n", primeira, segunda);
        }
    }

    private static void exibirResumo(Agendamento agendamento) {
        NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
        System.out.println("\nAgendamento confirmado");
        System.out.println("Tutor: " + agendamento.pet().tutor().nome());
        System.out.println("Pet: " + agendamento.pet().nome());
        System.out.println("Horário: " + agendamento.horario().format(FORMATO_HORARIO));
        System.out.println("Serviço: " + agendamento.servico().descricao());
        System.out.println("Total: " + moeda.format(agendamento.servico().preco()));
    }
}
