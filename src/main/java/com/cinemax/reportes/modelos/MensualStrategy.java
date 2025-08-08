package com.cinemax.reportes.modelos;

import java.time.LocalDateTime;

public class MensualStrategy implements FrecuenciaStrategy {

    @Override
    public LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion) {
        return fechaGeneracion.plusMonths(1); // Incrementa un mes
    }
    
}
