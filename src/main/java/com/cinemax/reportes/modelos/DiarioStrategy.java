package com.cinemax.reportes.modelos;

import java.time.LocalDateTime;

public class DiarioStrategy implements FrecuenciaStrategy {

    @Override
    public LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion) {
        return fechaGeneracion.plusDays(1); // Incrementa un día
    }
}
