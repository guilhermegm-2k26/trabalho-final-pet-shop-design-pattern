package br.edu.petshop.servico;

public final class VeterinarioFactory extends AgendamentoFactory {
    @Override
    protected Servico criarServico() {
        return new Veterinario();
    }
}
