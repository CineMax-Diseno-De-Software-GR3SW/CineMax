package com.cinemax.venta_boletos.modelos.entidades;

/**
 * Implementación específica para el cálculo del Impuesto al Valor Agregado (IVA).
 * 
 * @author GR3SW
 * @version 1.0
 */
public class CalculadorIVA implements CalculadorImpuesto {

    private static final double IVA_TASA = 0.15;
    
    @Override
    public double calcularImpuesto(double subTotal) {
        // Aplicar la tasa del IVA al subtotal
        return subTotal * IVA_TASA;
    }

    public static double getIVA_TASA() {
        return IVA_TASA;
    }

}
