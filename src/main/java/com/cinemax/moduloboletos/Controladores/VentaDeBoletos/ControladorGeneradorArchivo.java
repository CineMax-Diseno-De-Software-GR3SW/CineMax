package com.cinemax.moduloboletos.Controladores.VentaDeBoletos;

import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Factura;
import com.cinemax.moduloboletos.Servicios.VentaDeBoletos.ServicioGeneradorArchivo;
import java.util.List;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Boleto;

public class ControladorGeneradorArchivo {
    private ServicioGeneradorArchivo servicioGeneradorArchivo;

    public ControladorGeneradorArchivo(ServicioGeneradorArchivo servicioGeneradorArchivo) {
        this.servicioGeneradorArchivo = servicioGeneradorArchivo;
    }

    public void generarFacturaPDF(Factura factura) {
        servicioGeneradorArchivo.generarFacturaPDF(factura);
    }

    public void generarBoletosPDF(List<Boleto> boletos) {
        servicioGeneradorArchivo.generarBoletosPDF(boletos);
    }
}