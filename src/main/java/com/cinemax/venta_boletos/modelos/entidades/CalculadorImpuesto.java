package com.cinemax.venta_boletos.modelos.entidades;

/**
 * Esta interfaz permite implementar diferentes estrategias de cálculo de impuestos
 * según las regulaciones fiscales aplicables (IVA, impuestos locales, etc.).
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
