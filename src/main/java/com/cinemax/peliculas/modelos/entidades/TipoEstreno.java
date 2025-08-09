package com.cinemax.peliculas.modelos.entidades;

import java.math.BigDecimal;

/**
 * Enumeración que representa los diferentes tipos de estreno disponibles
 * para las funciones cinematográficas en el sistema CineMax.
 *
 * <p>Esta enumeración define los tipos de estreno que pueden aplicarse
 * a las funciones, cada uno con un multiplicador de precio específico
 * que afecta el costo final del boleto.
 *
 * <p>Tipos de estreno disponibles:
 * <ul>
 *   <li>Estreno: Función de estreno regular con 75% de recargo</li>
 *   <li>Preestreno: Función de preestreno especial con 100% de recargo</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public enum TipoEstreno {
    /** Estreno regular - 75% de recargo sobre el precio base */
    ESTRENO(new BigDecimal("1.75")),

    /** Preestreno especial - 100% de recargo sobre el precio base */
    PREESTRENO(new BigDecimal("2.00"));

    /** Multiplicador de precio que se aplica al costo base del boleto */
    private final BigDecimal multiplicadorPrecio;

    /**
     * Constructor privado para inicializar cada tipo de estreno con su multiplicador.
     *
     * @param multiplicadorPrecio Factor multiplicativo que se aplicará al precio base.
     *                           Debe ser mayor que cero.
     */
    TipoEstreno(BigDecimal multiplicadorPrecio) {
        this.multiplicadorPrecio = multiplicadorPrecio;
    }

    /**
     * Obtiene el multiplicador de precio para este tipo de estreno.
     *
     * <p>Este multiplicador se utiliza en el cálculo del precio final
     * del boleto, aplicándose sobre el precio base junto con otros factores
     * como el día de la semana y formato de proyección.
     *
     * @return El multiplicador que se aplicará al precio base del boleto
     */
    public BigDecimal getMultiplicadorPrecio() {
        return multiplicadorPrecio;
    }

    /**
     * Convierte el tipo de estreno a su representación textual legible.
     *
     * @return Representación en cadena del tipo de estreno
     */
    @Override
    public String toString() {
        switch (this) {
            case ESTRENO: return "Estreno";
            case PREESTRENO: return "Preestreno";
            default: return super.toString();
        }
    }
}