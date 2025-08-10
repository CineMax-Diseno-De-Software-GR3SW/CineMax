package com.cinemax.reportes.modelos.entidades;

import java.time.LocalDateTime;

public class EstrategiaDiaria implements EstrategiaDeFrecuencia {

    @Override
    public LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion) {
        return fechaGeneracion.plusSeconds(10); // Incrementa un día
    }
}
