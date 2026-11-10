package br.edu.petshop.servico;

public final class TosaFactory extends AgendamentoFactory {
    @Override
    protected Servico criarServico() {
        return new Tosa();
    }
}
