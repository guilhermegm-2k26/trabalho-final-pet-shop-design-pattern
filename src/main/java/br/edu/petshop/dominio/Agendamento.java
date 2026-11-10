package br.edu.petshop.dominio;

import br.edu.petshop.servico.Servico;
import java.time.LocalDateTime;

public record Agendamento(Pet pet, LocalDateTime horario, Servico servico) {
}
