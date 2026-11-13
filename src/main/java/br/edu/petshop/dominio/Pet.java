package br.edu.petshop.dominio;

import java.util.Objects;

public record Pet(String nome, Tutor tutor) {
    public Pet {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe o nome do pet.");
        }
        Objects.requireNonNull(tutor, "Informe o tutor.");
    }
}
