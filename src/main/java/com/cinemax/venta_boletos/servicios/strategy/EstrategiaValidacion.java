package com.cinemax.venta_boletos.servicios.strategy;

/**
 * Interfaz que define el contrato para las estrategias de validación de documentos.
 * 
 * Esta interfaz es parte del patrón de diseño Strategy, permitiendo intercambiar
 * algoritmos de validación en tiempo de ejecución según el tipo de documento
 * (cédula, pasaporte, RUC, etc.).
 * 
 * Implementaciones disponibles:
 * - EstrategiaCedulaValidacion: Validación de cédulas ecuatorianas
 * - EstrategiaPasaporteValidacion: Validación de formato de pasaportes
 * - EstrategiaRucValidacion: Validación de RUC ecuatoriano
 * 
 * @author GR3SW
 * @version 1.0
 */
public interface EstrategiaValidacion {

    /**
     * Ejecuta el algoritmo de validación específico para un tipo de documento.
     * 
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