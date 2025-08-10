package com.cinemax.reportes.modelos;

import java.time.LocalDateTime;

public class EstrategiaMensual implements EstrategiaDeFrecuencia {

    @Override
    public LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion) {
        return fechaGeneracion.plusMonths(1); // Incrementa un mes
    }
    
}
