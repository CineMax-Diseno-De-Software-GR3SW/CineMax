package com.cinemax.moduloboletos.Servicios.VentaDeBoletos;

import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Factura;
import java.util.List;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Boleto;

public interface ServicioGeneradorArchivo {
    void generarFacturaPDF(Factura factura);

    void generarBoletosPDF(List<Boleto> boletos);
}