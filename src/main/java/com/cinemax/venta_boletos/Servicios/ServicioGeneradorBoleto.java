package com.cinemax.venta_boletos.Servicios;

import java.util.ArrayList;
import java.util.List;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.venta_boletos.Modelos.Boleto;
import com.cinemax.venta_boletos.Modelos.Producto;

public class ServicioGeneradorBoleto {

    public List<Producto> generarBoleto(Funcion funcion, List<Butaca> butacas) {
        // Lógica para generar boletos
        List<Producto> boletos = new ArrayList<>();
        for (Butaca butaca : butacas) {
            Boleto boleto = new Boleto(funcion, butaca);
            boletos.add(boleto);
        }
        ServicioGeneradorArchivo generador = new GeneradorArchivoPDF();
        generador.generarBoletosPDF(boletos);

        return boletos;
    }

}
