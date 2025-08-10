package com.cinemax.reportes.modelos.entidades;

import java.time.LocalDateTime;

public interface EstrategiaDeFrecuencia {
    LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion);
}
