package com.cinemax.salas.modelos.entidades;

public enum TipoSala {
    NORMAL(1),  // Constante con parámetro
    VIP(2);     // Última constante (sin coma) con punto y coma para indicar fin de constantes

    // Campo final (inmutable)
    private final int multiplicador;

    // Constructor (privado implícitamente)
    TipoSala(int multiplicador) {
        this.multiplicador = multiplicador;
    }

    // Método getter
    public int getMultiplicador() {
        return multiplicador;
    }
}