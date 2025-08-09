package com.cinemax.salas.modelos.entidades;

/**
 * Enumeración que define los tipos de sala disponibles en el cine,
 * junto con un multiplicador de precio asociado.
 *
 * Valores:
 * - NORMAL: sala estándar sin recargo en el precio de la entrada.
 * - VIP: sala con mayor confort y un recargo del 50% sobre el precio base.
 *
 * Cada tipo de sala tiene asociado un multiplicador que puede usarse
 * para calcular precios de boletos u otros costos.
 */
public enum TipoSala {

    /** Sala normal - sin recargo (multiplicador = 1.0) */
    NORMAL(1.0),

    /** Sala VIP - con recargo del 50% (multiplicador = 1.50) */
    VIP(1.50);

    /** Factor multiplicador aplicado al precio base según el tipo de sala */
    private final double multiplicador;

    /**
     * Constructor del enum.
     * @param multiplicador valor multiplicador para ajustar precios.
     */
    TipoSala(double multiplicador) {
        this.multiplicador = multiplicador;
    }

    /**
     * Obtiene el multiplicador de precio asociado al tipo de sala.
     * @return multiplicador como valor decimal (ej. 1.0, 1.5).
     */
    public double getMultiplicador() {
        return multiplicador;
    }
}

