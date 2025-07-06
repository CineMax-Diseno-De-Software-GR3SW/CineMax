package com.cinemax.moduloboletos.Servicios.VentaDeBoletos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.CalculadorIVA;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.CalculadorImpuesto;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Cliente;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Factura;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Producto;

public class ServicioFacturacion {

    CalculadorImpuesto calculadorImpuesto;

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
        return factura;
    }

    /**
     * Genera un código único para la factura
     */
    private String generarCodigoFactura() {
        return "FAC-" + System.currentTimeMillis();
    }

    /**
     * Obtiene la fecha actual formateada
     */
    private String getFechaActual() {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
    }

}
