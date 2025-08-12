package com.cinemax.reportes.modelos.entidades;

import java.time.LocalDateTime;

/**
 * Implementación del patrón Strategy para frecuencia mensual.
 * Esta clase define la lógica para calcular la siguiente ejecución
 * de un reporte que debe generarse cada mes.
 */
public class EstrategiaMensual implements EstrategiaDeFrecuencia {

    /**
     * Calcula la siguiente fecha de ejecución agregando 1 mes
     * a la fecha de generación actual.
     * 
     * @param fechaGeneracion La fecha base desde la cual calcular la siguiente ejecución
     * @return LocalDateTime con la fecha incrementada en 1 mes
     */
    @Override
    public LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion) {
        return fechaGeneracion.plusMonths(1); // Incrementa un mes
    }
    
}