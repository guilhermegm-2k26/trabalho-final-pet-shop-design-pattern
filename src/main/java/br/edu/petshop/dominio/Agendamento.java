package br.edu.petshop.dominio;

import br.edu.petshop.servico.Servico;
import java.time.LocalDateTime;
import java.util.Objects;

public record Agendamento(Pet pet, LocalDateTime horario, Servico servico) {
    public Agendamento {
        Objects.requireNonNull(pet, "Informe o pet.");
        Objects.requireNonNull(servico, "Escolha um serviço.");
        validarHorario(horario);
    }

    public static void validarHorario(LocalDateTime horario) {
        if (horario == null || !horario.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Escolha uma data e um horário futuros.");
        }
    }
}
