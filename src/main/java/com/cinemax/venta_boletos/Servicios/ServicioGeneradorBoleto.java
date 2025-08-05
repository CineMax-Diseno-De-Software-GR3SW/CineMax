package com.cinemax.venta_boletos.servicios;

import java.util.ArrayList;
import java.util.List;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.venta_boletos.modelos.entidades.Boleto;
import com.cinemax.venta_boletos.modelos.entidades.Producto;

public class ServicioGeneradorBoleto {

    public List<Producto> generarBoleto(Funcion funcion, List<Butaca> butacas) {
        // Lógica para generar boletos
        List<Producto> boletos = new ArrayList<>();
        for (Butaca butaca : butacas) {
            Boleto boleto = new Boleto(funcion, butaca);
            boletos.add(boleto);
        }

        return boletos;
    }

}
