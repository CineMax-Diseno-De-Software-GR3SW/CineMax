package com.cinemax.peliculas.modelos.entidades;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Enumeración que representa los días de la semana con sus respectivos
 * multiplicadores de precio para el sistema de cine.
 *
 * <p>Esta enumeración permite:
 * <ul>
 *   <li>Gestionar precios diferenciados por día de la semana</li>
 *   <li>Distinguir entre días laborables y fines de semana</li>
 *   <li>Obtener el día correspondiente a partir de una fecha</li>
 * </ul>
 *
 * <p>Los multiplicadores de precio se aplican de la siguiente manera:
 * <ul>
 *   <li>Lunes a Viernes: 1.50 (50% de recargo)</li>
 *   <li>Sábado y Domingo: 1.75 (75% de recargo)</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public enum DiaSemana {
    /** Lunes con 50% de recargo */
    LUNES(new BigDecimal("1.50")),
    /** Martes con 50% de recargo */
    MARTES(new BigDecimal("1.50")),
    /** Miércoles con 50% de recargo */
    MIERCOLES(new BigDecimal("1.50")),
    /** Jueves con 50% de recargo */
    JUEVES(new BigDecimal("1.50")),
    /** Viernes con 50% de recargo */
    VIERNES(new BigDecimal("1.50")),
    /** Sábado con 75% de recargo */
    SABADO(new BigDecimal("1.75")),
    /** Domingo con 75% de recargo */
    DOMINGO(new BigDecimal("1.75"));

    /** Multiplicador de precio para el día de la semana */
    private final BigDecimal precio;

    /**
     * Constructor privado para inicializar cada día con su multiplicador de precio.
     *
     * @param precio El multiplicador de precio para este día de la semana.
     *               No puede ser null y debe ser mayor que cero.
     */
    DiaSemana(BigDecimal precio) {
        this.precio = precio;
    }

    /**
     * Obtiene el multiplicador de precio para este día de la semana.
     *
     * @return El multiplicador de precio como BigDecimal
     */
    public BigDecimal getPrecio() {
        return precio;
    }

    /**
     * Obtiene el día de la semana correspondiente a una fecha y hora específica.
     *
     * <p>Este método mapea los días de la semana de Java (DayOfWeek)
     * a los valores de esta enumeración.
     *
     * @param fechaHora La fecha y hora para determinar el día de la semana.
     *                  No puede ser null.
     * @return El enum DiaSemana correspondiente al día de la fecha proporcionada
     * @throws IllegalArgumentException Si fechaHora es null
     * @throws IllegalStateException Si se encuentra un día de la semana no válido
     */
    public static DiaSemana obtenerDiaDeFecha(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha/hora no puede ser null");
        }

        DayOfWeek dayOfWeek = fechaHora.getDayOfWeek();
        
        switch (dayOfWeek) {
            case MONDAY: return LUNES;
            case TUESDAY: return MARTES;
            case WEDNESDAY: return MIERCOLES;
            case THURSDAY: return JUEVES;
            case FRIDAY: return VIERNES;
            case SATURDAY: return SABADO;
            case SUNDAY: return DOMINGO;
            default: throw new IllegalStateException("Día de la semana no válido: " + dayOfWeek);
        }
    }

    /**
     * Determina si el día corresponde a un fin de semana.
     *
     * @return true si es sábado o domingo, false si es día entre semana
     */
    public boolean esFinDeSemana() {
        return this == SABADO || this == DOMINGO;
    }

    /**
     * Retorna una representación legible del día de la semana en español.
     *
     * @return El nombre del día de la semana con acentos y formato apropiado
     */
    @Override
    public String toString() {
        switch (this) {
            case LUNES: return "Lunes";
            case MARTES: return "Martes";
            case MIERCOLES: return "Miércoles";
            case JUEVES: return "Jueves";
            case VIERNES: return "Viernes";
            case SABADO: return "Sábado";
            case DOMINGO: return "Domingo";
            default: return super.toString();
        }
    }
}
