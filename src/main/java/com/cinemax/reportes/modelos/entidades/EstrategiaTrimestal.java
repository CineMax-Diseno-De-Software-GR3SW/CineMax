package com.cinemax.reportes.modelos.entidades;

import java.time.LocalDateTime;

/**
 * Implementación del patrón Strategy para frecuencia trimestral.
 * Esta clase define la lógica para calcular la siguiente ejecución
 * de un reporte que debe generarse cada tres meses.
 */
public class EstrategiaTrimestal implements EstrategiaDeFrecuencia {

    /**
     * Calcula la siguiente fecha de ejecución agregando 3 meses
     * a la fecha de generación actual.
     * 
     * @param fechaGeneracion La fecha base desde la cual calcular la siguiente ejecución
     * @return LocalDateTime con la fecha incrementada en 3 meses
     */
    @Override
    public LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion) {
        return fechaGeneracion.plusMonths(3); // Incrementa tres meses
    }
    
}