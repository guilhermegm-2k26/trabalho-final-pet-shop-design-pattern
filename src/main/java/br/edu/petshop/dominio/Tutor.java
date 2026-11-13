package br.edu.petshop.dominio;

public record Tutor(String nome) {
    public Tutor {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe o nome do tutor.");
        }
        nome = nome.strip();
    }
}
