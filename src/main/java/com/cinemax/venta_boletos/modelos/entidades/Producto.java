package com.cinemax.venta_boletos.modelos.entidades;

/**
 * Clase abstracta que representa un producto en el sistema CineMax.
 * 
 * Esta clase define la estructura base para todos los productos que pueden
 * ser vendidos en el cine (boletos, comida, bebidas, etc.).
 * 
 * @author GR3SW
 * @version 1.0
 */
public abstract class Producto {
    private double precio;
    
    /**
     * Método abstracto que debe ser implementado por cada tipo de producto.
     * 
     * Este método define la lógica específica para calcular el precio
     * de cada tipo de producto (boletos, comida, etc.)
     */
    public abstract void calcularPrecio();

    public double getPrecio(){
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
