package com.cinemax.peliculas.modelos.entidades;

import java.math.BigDecimal;

public enum FormatoFuncion {
    DOS_D(new BigDecimal("1.0")), // Formato estándar - sin recargo
    TRES_D(new BigDecimal("1.50")); // Formato 3D - 50% de recargo

    private final BigDecimal multiplicadorPrecio;

    FormatoFuncion(BigDecimal multiplicadorPrecio) {
        this.multiplicadorPrecio = multiplicadorPrecio;
    }

    /**
     * Obtiene el multiplicador de precio para este formato (para implementación futura de boletos)
     * @return El multiplicador que se aplicará al precio base
     */
    public BigDecimal getMultiplicadorPrecio() {
        return multiplicadorPrecio;
    }

    @Override
    public String toString() {
        switch (this) {
            case DOS_D: return "2D";
            case TRES_D: return "3D";
            default: return super.toString();
        }
    }

    public static FormatoFuncion fromString(String value) {
        switch (value) {
            case "2D": return DOS_D;
            case "3D": return TRES_D;
            default: throw new IllegalArgumentException("Formato desconocido: " + value);
        }
    }
}