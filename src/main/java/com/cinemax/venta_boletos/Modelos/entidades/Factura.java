package com.cinemax.venta_boletos.modelos.entidades;

import java.util.List;

public class Factura {
    private long codigoFactura;
    // private LocalDateTime fecha;
    private String fecha;
    private Cliente cliente;
    private List<Producto> productos;
    private double subTotal;
    private double total;

    public Factura() {

    }

    public Factura(long numeroFactura, String fechaEmision, Cliente cliente) {
        this.codigoFactura = numeroFactura;
        this.fecha = fechaEmision;
        this.cliente = cliente;
        // this.total = total;
    }

    public Factura(String fechaEmision, Cliente cliente, double subTotal, double total) {
        this.fecha = fechaEmision;
        this.cliente = cliente;
        this.subTotal = subTotal;
        this.total = total;
    }

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
        // this.subTotal = calculadorImpuesto.calcularImpuesto(productos);
        for (Producto producto : productos) {
            subTotal += producto.getPrecio();
        }
    }

    public void calcularTotal(CalculadorImpuesto calculadorImpuesto) {
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
