package com.cinemax.venta_boletos.Modelos.entidades;

/**
 * Implementación específica para el cálculo del Impuesto al Valor Agregado (IVA).
 * 
 * Esta clase implementa la interfaz CalculadorImpuesto proporcionando la lógica
 * específica para calcular el IVA según la tasa vigebte.
 * 
 * Características principales:
 * - Tasa fija del IVA: 15% (0.15)
 * - Cálculo simple: subtotal × tasa_IVA
 * - Acceso público a la tasa para uso en otros componentes
 * - Implementación del patrón Strategy para cálculo de impuestos
 * 
 * @author GR3SW
 * @version 1.0
 */
public class CalculadorIVA implements CalculadorImpuesto {
    /** 
     * Tasa del IVA expresada como decimal (15% = 0.15).
     * Valor constante que representa la tasa fiscal vigente para servicios de entretenimiento.
     */
    private static final double IVA_TASA = 0.15;

    /**
     * Calcula el monto del IVA aplicando la tasa del 15% sobre el subtotal.
     * 
     * Implementa el método de la interfaz CalculadorImpuesto con la fórmula:
     * IVA = subtotal × 0.15
     * 
     * @param subTotal El subtotal sobre el cual calcular el IVA
     * @return El monto del IVA calculado (subtotal × 15%)
     */
    @Override
    public double calcularImpuesto(double subTotal) {
        // Aplicar la tasa del IVA al subtotal
        return subTotal * IVA_TASA;
    }

    /**
     * Obtiene la tasa del IVA para uso externo en otros componentes del sistema.
     * 
     * Método estático que permite acceder a la tasa sin necesidad de crear
     * una instancia de la clase. Útil para mostrar información de impuestos
     * en interfaces de usuario o para cálculos complementarios.
     * 
     * @return La tasa del IVA como decimal (0.12 para 12%)
     */
    public static double getIVA_TASA() {
        return IVA_TASA;
    }

}
