package com.cinemax.moduloboletos.Controladores.VentaDeBoletos;

import java.util.List;

import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Cliente;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Factura;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Producto;
import com.cinemax.moduloboletos.Servicios.VentaDeBoletos.ServicioFacturacion;

public class ControladorFacturacion {

    private ServicioFacturacion servicioFacturacion;

    public ControladorFacturacion() {
        this.servicioFacturacion = new ServicioFacturacion();
    }

    public Factura generarFactura(List<Producto> productos, Cliente cliente) {
        return servicioFacturacion.generarFactura(productos, cliente);
    }
}
