package br.edu.petshop.servico;

public final class BanhoFactory extends AgendamentoFactory {
    @Override
    protected Servico criarServico() {
        return new Banho();
    }
}
