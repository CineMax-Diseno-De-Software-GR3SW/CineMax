package com.cinemax.venta_boletos.servicios;

import java.util.ArrayList;
import java.util.List;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.venta_boletos.modelos.entidades.Boleto;
import com.cinemax.venta_boletos.modelos.entidades.Producto;

/**
 * Esta clase implementa la lógica de negocio para crear boletos
 * asociando una función específica de película con las butacas seleccionadas.
 * 
 * @author GR3SW
 * @version 1.0
 */
public class ServicioGeneradorBoleto {

    /**
     * Este método crea un boleto individual para cada butaca proporcionada,
     * asociándola con la función especificada. 
     * 
     * @param funcion La función de película para la cual se generan los boletos
     * @param butacas Lista de butacas seleccionadas por el cliente
     * @return Lista de productos (boletos) generados, uno por cada butaca
     */
    public List<Producto> generarBoleto(Funcion funcion, List<Butaca> butacas) {
        // Inicializa la lista que contendrá todos los boletos generados
        List<Producto> boletos = new ArrayList<>();
        
        // Itera sobre cada butaca seleccionada para crear su boleto correspondiente
        for (Butaca butaca : butacas) {
            // Crea un nuevo boleto asociando la función con la butaca actual
            Boleto boleto = new Boleto(funcion, butaca);
            // Añade el boleto creado a la lista de productos
            boletos.add(boleto);
        }

        return boletos;
    }

}
