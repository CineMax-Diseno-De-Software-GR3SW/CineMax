package com.cinemax.reportes.modelos;

import java.time.LocalDateTime;

public interface FrecuenciaStrategy {
    LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion);
}
