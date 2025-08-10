package com.cinemax.reportes.modelos.entidades;

import java.time.LocalDateTime;

public class EstrategiaMensual implements EstrategiaDeFrecuencia {

    @Override
    public LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion) {
        return fechaGeneracion.plusMonths(1); // Incrementa un mes
    }
    
}
