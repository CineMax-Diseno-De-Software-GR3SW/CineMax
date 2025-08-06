package com.cinemax.salas.modelos.entidades;

public enum TipoSala {
    NORMAL(1.0), // Sala normal - sin recargo
    VIP(1.50); // Sala VIP - 50% de recargo

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
