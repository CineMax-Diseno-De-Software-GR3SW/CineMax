package com.cinemax.venta_boletos.Servicios.VentaDeBoletos;

import com.cinemax.venta_boletos.Modelos.VentaDeBoletos.Factura;
import java.util.List;
import com.cinemax.venta_boletos.Modelos.VentaDeBoletos.Boleto;

public interface ServicioGeneradorArchivo {
    void generarFacturaPDF(Factura factura);

    void generarBoletosPDF(List<Boleto> boletos);
}