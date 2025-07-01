package com.cinemax.moduloboletos.Servicios.VentaDeBoletos;

import java.util.ArrayList;
import java.util.List;

import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Boleto;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Producto;

public class ServicioGeneradorBoleto {

    public List<Producto> generarBoleto(String funcion, List<String> butacas) {
        // Lógica para generar boletos
        List<Producto> boletos = new ArrayList<>();
        for (String butaca : butacas) {
            Boleto boleto = new Boleto(funcion, butaca);
            boletos.add(boleto);
        }
        return boletos;
    }
    
}
