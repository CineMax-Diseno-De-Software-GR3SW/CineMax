package com.cinemax.reportes.modelos;

import java.time.LocalDateTime;

public class SemanalStrategy implements FrecuenciaStrategy {

    @Override
    public LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion) {
        return fechaGeneracion.plusWeeks(1); // Incrementa una semana
    }
    
}
