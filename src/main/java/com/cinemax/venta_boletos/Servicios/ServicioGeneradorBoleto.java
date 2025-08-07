package com.cinemax.venta_boletos.Servicios;

import java.util.ArrayList;
import java.util.List;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.venta_boletos.Modelos.entidades.Boleto;
import com.cinemax.venta_boletos.Modelos.entidades.Producto;

/**
 * Servicio encargado de la generación de boletos en el sistema CineMax.
 * 
 * Esta clase implementa la lógica de negocio para crear boletos
 * asociando una función específica de película con las butacas seleccionadas.
 * 
 * Forma parte de la capa de servicios del módulo de venta de boletos,
 * encapsulando la creación de productos tipo boleto.
 * 
 * @author GR3SW
 * @version 1.0
 */
public class ServicioGeneradorBoleto {

    /**
     * Genera una lista de boletos basada en una función y las butacas seleccionadas.
     * 
     * Este método crea un boleto individual para cada butaca proporcionada,
     * asociándola con la función especificada. Cada boleto resultante contiene
     * la información completa de la función (película, horario, sala) y
     * la butaca específica asignada.
     * 
     * El método retorna una lista de productos (boletos) que puede ser
     * utilizada posteriormente para cálculos de precio y generación de facturas.
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
