package com.cinemax.venta_boletos.servicios;
import java.util.List;

import com.cinemax.venta_boletos.modelos.entidades.Factura;
import com.cinemax.venta_boletos.modelos.entidades.Producto;


public interface ServicioGeneradorArchivo {
    void generarFacturaPDF(Factura factura);

    void generarBoletosPDF(List<Producto> boletos);
}