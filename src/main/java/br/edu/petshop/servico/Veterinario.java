package br.edu.petshop.servico;

import java.math.BigDecimal;

public final class Veterinario implements Servico {
    @Override
    public String descricao() {
        return "Veterinario";
    }

    @Override
    public BigDecimal preco() {
        return new BigDecimal("200.00");
    }
}
