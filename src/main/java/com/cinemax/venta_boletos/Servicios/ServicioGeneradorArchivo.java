package com.cinemax.venta_boletos.Servicios;

import java.util.List;

import com.cinemax.venta_boletos.Modelos.Boleto;
import com.cinemax.venta_boletos.Modelos.Factura;

public interface ServicioGeneradorArchivo {
    void generarFacturaPDF(Factura factura);

    void generarBoletosPDF(List<Boleto> boletos);
}