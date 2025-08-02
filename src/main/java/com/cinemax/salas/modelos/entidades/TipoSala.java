package com.cinemax.salas.modelos.entidades;

public enum TipoSala {
    NORMAL(1.0),
    VIP(2.0);

    // Campo final (inmutable)
    private final double multiplicador;

    // Constructor (privado implícitamente)
    TipoSala(double multiplicador) {
        this.multiplicador = multiplicador;
    }

    // Método getter
    public double getMultiplicador() {
        return multiplicador;
    }
}
