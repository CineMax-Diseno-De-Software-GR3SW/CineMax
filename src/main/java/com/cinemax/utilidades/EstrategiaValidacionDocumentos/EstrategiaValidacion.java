package com.cinemax.comun.EstrategiaValidacionDocumentos;

/**
 * Esta interfaz es parte del patrón de diseño Strategy, permitiendo intercambiar
 * algoritmos de validación en tiempo de ejecución según el tipo de documento
 * (cédula, pasaporte, RUC, etc.).
 * 
 * @author GR3SW
 * @version 1.0
 */
public interface EstrategiaValidacion {

    /**
     * Cada implementación define sus propias reglas de validación según
     * el tipo de documento que maneja. Este método es el punto de entrada
     * común para todas las estrategias de validación.
     * 
     * @param documento Número o código del documento a validar
     * @return true si el documento es válido según las reglas de la estrategia,
     *         false en caso contrario
     */
    boolean ejecutarEstrategia(String documento);
} 