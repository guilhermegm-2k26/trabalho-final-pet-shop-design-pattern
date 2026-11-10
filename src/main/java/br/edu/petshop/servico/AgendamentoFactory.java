package br.edu.petshop.servico;

import br.edu.petshop.dominio.Agendamento;
import br.edu.petshop.dominio.Pet;
import java.time.LocalDateTime;
import java.util.function.UnaryOperator;

public abstract class AgendamentoFactory {
    public final Agendamento agendar(Pet pet, LocalDateTime horario, UnaryOperator<Servico> personalizar) {
        Servico servico = personalizar.apply(criarServico());
        return new Agendamento(pet, horario, servico);
    }

    protected abstract Servico criarServico();
}
