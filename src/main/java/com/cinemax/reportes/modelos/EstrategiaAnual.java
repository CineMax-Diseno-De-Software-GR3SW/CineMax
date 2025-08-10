package com.cinemax.reportes.modelos;

import java.time.LocalDateTime;

public class EstrategiaAnual implements EstrategiaDeFrecuencia {

    @Override
    public LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion) {
        return fechaGeneracion.plusYears(1); // Incrementa un año
    }
    
}
