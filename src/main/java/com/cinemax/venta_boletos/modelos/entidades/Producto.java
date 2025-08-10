package com.cinemax.venta_boletos.Modelos.entidades;

/**
 * Clase abstracta que representa un producto en el sistema CineMax.
 * 
 * Esta clase define la estructura base para todos los productos que pueden
 * ser vendidos en el cine (boletos, comida, bebidas, etc.).
 * 
 * 
 * @author GR3SW
 * @version 1.0
 */
public abstract class Producto {
    /** Precio del producto en la moneda local */
    private double precio;
    
    /**
     * Método abstracto que debe ser implementado por cada tipo de producto.
     * 
     * Este método define la lógica específica para calcular el precio
     * de cada tipo de producto (boletos, comida, etc.)
     * 
     * Implementa el patrón Template Method donde la estructura del
     * cálculo está definida pero la implementación específica queda
     * a cargo de las clases hijas.
     */
    public abstract void calcularPrecio();

    /**
     * Obtiene el precio actual del producto.
     * 
     * @return El precio del producto como valor double
     */
    public double getPrecio(){
        return precio;
    }

    /**
     * Establece el precio del producto.
     * 
     * Este método es utilizado generalmente después de llamar
     * al método calcularPrecio() para actualizar el precio
     * basado en los cálculos específicos de cada tipo de producto.
     * 
     * @param precio El nuevo precio a establecer para el producto
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
