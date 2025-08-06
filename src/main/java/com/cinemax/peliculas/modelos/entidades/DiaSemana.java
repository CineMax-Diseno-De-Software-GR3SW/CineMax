package com.cinemax.peliculas.modelos.entidades;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public enum DiaSemana {
    LUNES(new BigDecimal("1.50")), // Lunes - 50% de recargo
    MARTES(new BigDecimal("1.50")),
    MIERCOLES(new BigDecimal("1.50")),
    JUEVES(new BigDecimal("1.50")),
    VIERNES(new BigDecimal("1.50")),
    SABADO(new BigDecimal("1.75")), // Sábado - 75% de recargo
    DOMINGO(new BigDecimal("1.75"));

    private final BigDecimal precio;

    DiaSemana(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    /**
     * Obtiene el día de la semana basado en una fecha/hora específica
     * @param fechaHora La fecha y hora para determinar el día
     * @return El enum DiaSemana correspondiente
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
     * Determina si es fin de semana (sábado o domingo)
     * @return true si es fin de semana, false si es día entre semana
     */
    public boolean esFinDeSemana() {
        return this == SABADO || this == DOMINGO;
    }

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
