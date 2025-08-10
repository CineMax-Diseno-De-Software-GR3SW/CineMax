package com.cinemax.venta_boletos.modelos.entidades;

import java.util.List;

/**
 * Esta clase encapsula la información de una transacción comercial,
 * incluyendo los datos del cliente, productos adquiridos y cálculos de totales.
 * 
 * Se utiliza para generar comprobantes de venta de boletos y productos del cine.
 * 
 * @author GR3SW
 * @version 1.0
 */
public class Factura {
    private long codigoFactura;
    
    private String fecha;
    
    private Cliente cliente;
    
    private List<Producto> productos;
    
    /** Subtotal de la factura (suma de precios sin impuestos) */
    private double subTotal;
    
    /** Total de la factura (subtotal + impuestos) */
    private double total;

    public Factura() {

    }

    /**
     * Constructor para crear una factura básica con datos mínimos.
     * 
     * @param numeroFactura Código único de identificación de la factura
     * @param fechaEmision Fecha en que se emitió la factura
     * @param cliente Cliente asociado a la factura
     */
    public Factura(long numeroFactura, String fechaEmision, Cliente cliente) {
        this.codigoFactura = numeroFactura;
        this.fecha = fechaEmision;
        this.cliente = cliente;
        // this.total = total; // Comentado: el total se calculará posteriormente
    }

    /**
     * Constructor para crear una factura con montos calculados.
     * 
     * @param fechaEmision Fecha en que se emitió la factura
     * @param cliente Cliente asociado a la factura
     * @param subTotal Subtotal de la factura sin impuestos
     * @param total Total de la factura incluyendo impuestos
     */
    public Factura(String fechaEmision, Cliente cliente, double subTotal, double total) {
        this.fecha = fechaEmision;
        this.cliente = cliente;
        this.subTotal = subTotal;
        this.total = total;
    }

    /**
     * Constructor completo para crear una factura con todos los datos.
     * 
     * @param numeroFactura Código único de identificación de la factura
     * @param fechaEmision Fecha en que se emitió la factura
     * @param cliente Cliente asociado a la factura
     * @param subTotal Subtotal de la factura sin impuestos
     * @param total Total de la factura incluyendo impuestos
     */
    public Factura(long numeroFactura, String fechaEmision, Cliente cliente, double subTotal, double total) {
        this.codigoFactura = numeroFactura;
        this.fecha = fechaEmision;
        this.cliente = cliente;
        this.subTotal = subTotal;
        this.total = total;
    }

    public long getCodigoFactura() {
        return codigoFactura;
    }

    public String getFecha() {
        return fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public double getTotal() {
        return total;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public void calcularSubTotal() {
        // this.subTotal = calculadorImpuesto.calcularImpuesto(productos); // Implementación comentada
        // Itera sobre todos los productos y suma sus precios
        for (Producto producto : productos) {
            subTotal += producto.getPrecio();
        }
    }

    public void calcularTotal(CalculadorImpuesto calculadorImpuesto) {
        // El total es la suma del subtotal más los impuestos calculados
        this.total = subTotal + calculadorImpuesto.calcularImpuesto(subTotal);
    }

    @Override
    public String toString() {
        return "Factura{" +
                "codigoFactura='" + codigoFactura + '\n' +
                ", fecha='" + fecha + '\n' +
                ", cliente=" + cliente + '\n' +
                ", productos=" + productos + '\n' +
                ", subTotal=" + subTotal + '\n' +
                ", total=" + total + '\n' +
                '}';
    }

    public double getSubTotal() {
        return subTotal;
    }

    public List<Producto> getProductos() {
        return this.productos;
    }
}
