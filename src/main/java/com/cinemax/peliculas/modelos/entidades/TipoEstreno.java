package com.cinemax.peliculas.modelos.entidades;

import java.math.BigDecimal;

public enum TipoEstreno {
    ESTRENO(new BigDecimal("1.2")), // Estreno - 20% de recargo
    PREESTRENO(new BigDecimal("1.3")); // Preestreno - 30% de recargo

    private final BigDecimal multiplicadorPrecio;

    TipoEstreno(BigDecimal multiplicadorPrecio) {
        this.multiplicadorPrecio = multiplicadorPrecio;
    }

    /**
     * Obtiene el multiplicador de precio para este tipo de estreno (para implementación futura de boletos)
     * @return El multiplicador que se aplicará al precio base
     */
    public BigDecimal getMultiplicadorPrecio() {
        return multiplicadorPrecio;
    }

    @Override
    public String toString() {
        switch (this) {
            case ESTRENO: return "Estreno";
            case PREESTRENO: return "Preestreno";
            default: return super.toString();
        }
    }
}