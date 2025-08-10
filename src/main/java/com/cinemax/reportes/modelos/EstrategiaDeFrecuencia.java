package com.cinemax.reportes.modelos;

import java.time.LocalDateTime;

public interface EstrategiaDeFrecuencia {
    LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion);
}
