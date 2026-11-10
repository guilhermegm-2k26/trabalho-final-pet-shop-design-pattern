package br.edu.petshop.servico;

import java.math.BigDecimal;

public final class CorteDeUnhas extends ServicoComAdicional {
    public CorteDeUnhas(Servico servico) {
        super(servico);
    }

    @Override
    public String descricao() {
        return super.descricao() + " + corte de unhas";
    }

    @Override
    public BigDecimal preco() {
        return super.preco().add(new BigDecimal("15.00"));
    }
}
