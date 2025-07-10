package com.cinemax.venta_boletos.Controladores.VentaDeBoletos;

import java.util.List;

import com.cinemax.venta_boletos.Modelos.VentaDeBoletos.Cliente;
import com.cinemax.venta_boletos.Modelos.VentaDeBoletos.Factura;
import com.cinemax.venta_boletos.Modelos.VentaDeBoletos.Producto;
import com.cinemax.venta_boletos.Servicios.VentaDeBoletos.ServicioFacturacion;

public class ControladorFacturacion {

    private ServicioFacturacion servicioFacturacion;

    public ControladorFacturacion() {
        this.servicioFacturacion = new ServicioFacturacion();
    }

    public Factura generarFactura(List<Producto> productos, Cliente cliente) {
        return servicioFacturacion.generarFactura(productos, cliente);
    }
}
