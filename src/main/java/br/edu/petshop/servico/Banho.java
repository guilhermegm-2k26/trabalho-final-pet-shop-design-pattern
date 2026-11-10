package br.edu.petshop.servico;

import java.math.BigDecimal;

public final class Banho implements Servico {
    @Override
    public String descricao() {
        return "Banho";
    }

    @Override
    public BigDecimal preco() {
        return new BigDecimal("50.00");
    }
}
