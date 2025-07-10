package com.cinemax.venta_boletos.Modelos.VentaDeBoletos;

public abstract class Producto {
    private double precio;
    public abstract void calcularPrecio();

    public double getPrecio(){
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
