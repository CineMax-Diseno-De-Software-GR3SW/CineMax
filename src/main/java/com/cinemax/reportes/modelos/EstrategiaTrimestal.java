package com.cinemax.reportes.modelos;

import java.time.LocalDateTime;

public class EstrategiaTrimestal implements EstrategiaDeFrecuencia {

    @Override
    public LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion) {
        return fechaGeneracion.plusMonths(3); // Incrementa tres meses
    }
    
}
