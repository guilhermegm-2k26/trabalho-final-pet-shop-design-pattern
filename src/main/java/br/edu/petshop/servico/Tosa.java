package br.edu.petshop.servico;

import java.math.BigDecimal;

public final class Tosa implements Servico {
    @Override
    public String descricao() {
        return "Tosa";
    }

    @Override
    public BigDecimal preco() {
        return new BigDecimal("70.00");
    }
}
