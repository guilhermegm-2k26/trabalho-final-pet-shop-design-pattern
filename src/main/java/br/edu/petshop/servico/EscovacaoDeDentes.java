package br.edu.petshop.servico;

import java.math.BigDecimal;

public final class EscovacaoDeDentes extends ServicoComAdicional {
    public static final BigDecimal PRECO_ADICIONAL = new BigDecimal("12.00");

    public EscovacaoDeDentes(Servico servico) {
        super(servico);
    }

    @Override
    public String descricao() {
        return super.descricao() + " + escovação de dentes";
    }

    @Override
    public BigDecimal preco() {
        return super.preco().add(PRECO_ADICIONAL);
    }
}
