package com.cinemax.reportes.modelos.entidades;

import java.time.LocalDateTime;

/**
 * Implementación del patrón Strategy para frecuencia diaria.
 * Esta clase define la lógica para calcular la siguiente ejecución
 * de un reporte que debe generarse diariamente.
 * 
 * NOTA: Actualmente configurado para incrementar 10 segundos en lugar de 1 día,
 * para propósitos de pruebas y desarrollo.
 */
public class EstrategiaDiaria implements EstrategiaDeFrecuencia {

    /**
     * Calcula la siguiente fecha de ejecución.
     * IMPORTANTE: Actualmente configurado para incrementar 10 segundos
     * en lugar de 1 día, para facilitar las pruebas.
     * 
     * @param fechaGeneracion La fecha base desde la cual calcular la siguiente ejecución
     * @return LocalDateTime con la fecha incrementada en 10 segundos
     */
    @Override
    public LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion) {
        return fechaGeneracion.plusSeconds(10); // Incrementa un día
    }
}