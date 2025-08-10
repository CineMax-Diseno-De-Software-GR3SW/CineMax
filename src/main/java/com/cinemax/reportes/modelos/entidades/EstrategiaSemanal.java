package com.cinemax.reportes.modelos.entidades;

import java.time.LocalDateTime;

public class EstrategiaSemanal implements EstrategiaDeFrecuencia {

    @Override
    public LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion) {
        return fechaGeneracion.plusWeeks(1); // Incrementa una semana
    }
    
}
