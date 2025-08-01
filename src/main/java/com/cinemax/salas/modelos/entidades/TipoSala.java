package com.cinemax.salas.modelos.entidades;

public enum TipoSala {
    NORMAL(1),
    VIP(2),;

    private static int multiplicador;

    TipoSala(int multiplicador) {
        this.multiplicador = multiplicador;
    }
    public int getMultiplicador() {
        return multiplicador;
    }
}
