package br.com.alura.consultor_tabela_fipe.model;

public record Dados(String codigo, String nome) {

    @Override
    public String toString() {
        return "código: " + codigo + " - " + nome;
    }
}
