package br.edu.petshop.servico;

import java.math.BigDecimal;

public abstract class ServicoComAdicional implements Servico {
    private final Servico servico;

    protected ServicoComAdicional(Servico servico) {
        this.servico = servico;
    }

    @Override
    public String descricao() {
        return servico.descricao();
    }

    @Override
    public BigDecimal preco() {
        return servico.preco();
    }
}
