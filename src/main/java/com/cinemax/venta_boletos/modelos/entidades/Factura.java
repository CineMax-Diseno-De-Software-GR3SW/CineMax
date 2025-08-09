package com.cinemax.venta_boletos.modelos.entidades;

import java.util.List;

/**
 * Representa una factura en el sistema CineMax.
 * Esta clase encapsula la información de una transacción comercial,
 * incluyendo los datos del cliente, productos adquiridos y cálculos de totales.
 * 
 * Se utiliza para generar comprobantes de venta de boletos y productos del cine.
 * 
 * @author GR3SW
 * @version 1.0
 */
public class Factura {
    /** Código único identificador de la factura */
    private long codigoFactura;
    
    /** Fecha de emisión de la factura en formato String */
    private String fecha;
    
    /** Cliente asociado a esta factura */
    private Cliente cliente;
    
    /** Lista de productos incluidos en la factura */
    private List<Producto> productos;
    
    /** Subtotal de la factura (suma de precios sin impuestos) */
    private double subTotal;
    
    /** Total de la factura (subtotal + impuestos) */
    private double total;

    /**
     * Constructor por defecto.
     * Crea una factura vacía sin inicializar sus campos.
     */
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

    /**
     * Obtiene el código único de identificación de la factura.
     * 
     * @return El código de la factura
     */
    public long getCodigoFactura() {
        return codigoFactura;
    }

    /**
     * Obtiene la fecha de emisión de la factura.
     * 
     * @return La fecha de emisión como String
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Obtiene el cliente asociado a esta factura.
     * 
     * @return El objeto Cliente de la factura
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Obtiene el total de la factura incluyendo impuestos.
     * 
     * @return El monto total de la factura
     */
    public double getTotal() {
        return total;
    }

    /**
     * Establece la lista de productos incluidos en la factura.
     * 
     * @param productos Lista de productos a incluir en la factura
     */
    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    /**
     * Calcula el subtotal de la factura sumando el precio de todos los productos.
     * Este método itera sobre la lista de productos y acumula sus precios
     * en el atributo subTotal de la factura.
     */
    public void calcularSubTotal() {
        // this.subTotal = calculadorImpuesto.calcularImpuesto(productos); // Implementación comentada
        // Itera sobre todos los productos y suma sus precios
        for (Producto producto : productos) {
            subTotal += producto.getPrecio();
        }
    }

    /**
     * Calcula el total de la factura aplicando impuestos al subtotal.
     * 
     * @param calculadorImpuesto Implementación específica para calcular impuestos
     */
    public void calcularTotal(CalculadorImpuesto calculadorImpuesto) {
        // El total es la suma del subtotal más los impuestos calculados
        this.total = subTotal + calculadorImpuesto.calcularImpuesto(subTotal);
    }

    /**
     * Genera una representación en cadena de la factura con todos sus datos.
     * Incluye saltos de línea para mejor legibilidad del formato.
     * 
     * @return String con toda la información de la factura formateada
     */
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

    /**
     * Obtiene el subtotal de la factura sin impuestos.
     * 
     * @return El monto subtotal de la factura
     */
    public double getSubTotal() {
        return subTotal;
    }

    /**
     * Obtiene la lista de productos incluidos en la factura.
     * 
     * @return Lista de productos de la factura
     */
    public List<Producto> getProductos() {
        return this.productos;
    }
}
