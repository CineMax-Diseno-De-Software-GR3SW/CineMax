package com.cinemax.venta_boletos.Servicios;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.cinemax.venta_boletos.Modelos.CalculadorIVA;
import com.cinemax.venta_boletos.Modelos.CalculadorImpuesto;
import com.cinemax.venta_boletos.Modelos.Cliente;
import com.cinemax.venta_boletos.Modelos.Factura;
import com.cinemax.venta_boletos.Modelos.Producto;

public class ServicioFacturacion {

    CalculadorImpuesto calculadorImpuesto; // TODO: No debería ser un atributo de la clase, sino un parámetro del método
                                           // generarFactura

    public ServicioFacturacion() {
        this.calculadorImpuesto = new CalculadorIVA();
    }

    public Factura generarFactura(List<Producto> productos, Cliente cliente) {
        Factura factura = new Factura(
                generarCodigoFactura(),
                getFechaActual(),
                cliente);
        factura.setProductos(productos);
        factura.calcularSubTotal();
        factura.calcularTotal(calculadorImpuesto);
        ServicioGeneradorArchivo generador = new GeneradorArchivoPDF();
        generador.generarFacturaPDF(factura);
        return factura;
    }

    /**
     * Genera un código único para la factura
     */
    private long generarCodigoFactura() {
        return System.currentTimeMillis(); // Solo el número
    }

    /**
     * Obtiene la fecha actual formateada
     */
    private String getFechaActual() {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
    }

}
