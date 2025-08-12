package com.cinemax.reportes.modelos.entidades;

import java.time.LocalDateTime;

/**
 * Interfaz que define el contrato del patrón Strategy para el cálculo
 * de frecuencias de ejecución de reportes.
 * 
 * Esta interfaz permite implementar diferentes estrategias de cálculo
 * temporal (diaria, semanal, mensual, trimestral, anual) de manera
 * intercambiable, facilitando la extensión del sistema con nuevas
 * frecuencias sin modificar el código existente.
 */
public interface EstrategiaDeFrecuencia {
    
    /**
     * Calcula la siguiente fecha de ejecución basada en una fecha dada.
     * Cada implementación concreta definirá su propia lógica de cálculo
     * según el tipo de frecuencia que represente.
     * 
     * @param fechaGeneracion La fecha base desde la cual calcular la siguiente ejecución
     * @return LocalDateTime con la nueva fecha calculada según la estrategia específica
     */
    LocalDateTime calcularSiguiente(LocalDateTime fechaGeneracion);
}