package com.cinemax.venta_boletos.modelos.entidades;

/**
 * Interfaz que define el contrato para el cálculo de impuestos en el sistema de venta de boletos.
 * 
 * Esta interfaz permite implementar diferentes estrategias de cálculo de impuestos
 * según las regulaciones fiscales aplicables (IVA, impuestos locales, etc.).
 * 
 * Uso típico:
 * 1. Implementar la interfaz con un algoritmo específico (ej: CalculadorIVA)
 * 2. Pasar el subtotal al método calcularImpuesto()
 * 3. Obtener el monto de impuesto calculado
 * 
 * @author GR3SW
 * @version 1.0
 */
public interface CalculadorImpuesto {
    
    /**
     * Calcula el monto de impuesto basado en un subtotal dado.
     * 
     * Cada implementación debe definir su propia lógica de cálculo
     * según el tipo de impuesto que representa (IVA, impuestos locales, etc.).
     * 
     * @param subTotal El subtotal sobre el cual calcular el impuesto
     * @return El monto de impuesto calculado
     */
    double calcularImpuesto(double subTotal);
}
