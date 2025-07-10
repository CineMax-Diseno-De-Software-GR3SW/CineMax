package com.cinemax.venta_boletos.Controladores;

import java.util.List;

import com.cinemax.venta_boletos.Modelos.Cliente;
import com.cinemax.venta_boletos.Modelos.Factura;
import com.cinemax.venta_boletos.Modelos.Producto;
import com.cinemax.venta_boletos.Servicios.ServicioFacturacion;

public class ControladorFacturacion {

    private ServicioFacturacion servicioFacturacion;

    public ControladorFacturacion() {
        this.servicioFacturacion = new ServicioFacturacion();
    }

    public Factura generarFactura(List<Producto> productos, Cliente cliente) {
        return servicioFacturacion.generarFactura(productos, cliente);
    }
}
