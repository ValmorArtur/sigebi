package com.biblioteca.model.enums;

public enum SituacaoExemplar {
    DISPONIVEL("Disponível"),
    EMPRESTADO("Emprestado"),
    RESERVADO("Reservado"),
    DANIFICADO("Danificado"),
    DESCONTINUADO("Descontinuado"), // perdido/descartado/inutilizável
    MANUTENCAO("Manutenção"), // perdido/descartado/inutilizável
    EXTRAVIADO("Extraviado"),
    HIGIENIZACAO("Higienização"),
    DOADO("Doado");

    private final String label;

    SituacaoExemplar(String label) { this.label = label; }

    public String getLabel() { return label; }
}
