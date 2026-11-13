package br.edu.petshop.servico;

import br.edu.petshop.dominio.Agendamento;
import br.edu.petshop.dominio.Pet;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.UnaryOperator;

public final class CatalogoServicos {
    private static final List<OpcaoServico> SERVICOS = List.of(
            new OpcaoServico("banho", new BanhoFactory()),
            new OpcaoServico("tosa", new TosaFactory()));

    private static final List<OpcaoAdicional> ADICIONAIS = List.of(
            new OpcaoAdicional("corte-unhas", "Corte de unhas", CorteDeUnhas.PRECO_ADICIONAL, CorteDeUnhas::new));

    private CatalogoServicos() {
    }

    public static List<OpcaoServico> servicos() {
        return SERVICOS;
    }

    public static List<OpcaoAdicional> adicionais() {
        return ADICIONAIS;
    }

    public static OpcaoServico buscarServico(String id) {
        return SERVICOS.stream()
                .filter(opcao -> opcao.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Serviço desconhecido."));
    }

    public static List<OpcaoAdicional> buscarAdicionais(List<String> ids) {
        return ids.stream().distinct().map(CatalogoServicos::buscarAdicional).toList();
    }

    private static OpcaoAdicional buscarAdicional(String id) {
        return ADICIONAIS.stream()
                .filter(opcao -> opcao.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Adicional desconhecido."));
    }

    private static UnaryOperator<Servico> personalizacao(List<OpcaoAdicional> adicionais) {
        return base -> {
            Servico decorado = base;
            for (OpcaoAdicional opcao : adicionais) {
                decorado = opcao.decorador().apply(decorado);
            }
            return decorado;
        };
    }

    public record OpcaoServico(String id, AgendamentoFactory fabrica) {
        public Servico orcar(List<OpcaoAdicional> adicionais) {
            return fabrica.montarServico(personalizacao(adicionais));
        }

        public Agendamento agendar(Pet pet, LocalDateTime horario, List<OpcaoAdicional> adicionais) {
            return fabrica.agendar(pet, horario, personalizacao(adicionais));
        }
    }

    public record OpcaoAdicional(String id, String nome, BigDecimal preco, UnaryOperator<Servico> decorador) {
    }
}
