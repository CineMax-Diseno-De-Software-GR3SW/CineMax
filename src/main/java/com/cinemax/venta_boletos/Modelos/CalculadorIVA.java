package com.cinemax.venta_boletos.Modelos;


public class CalculadorIVA implements CalculadorImpuesto {
    private static final double IVA_TASA = 0.12; // Tasa del IVA del 12%

    @Override
    public double calcularImpuesto(double subTotal) {
        return subTotal*IVA_TASA; // Calcula el IVA sobre el total de los productos
    }

}
