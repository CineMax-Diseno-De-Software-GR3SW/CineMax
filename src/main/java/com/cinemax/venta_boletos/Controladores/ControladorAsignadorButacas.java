package com.cinemax.venta_boletos.Controladores;

import java.util.ArrayList;
import java.util.List;

public class ControladorAsignadorButacas {
    public List<String> asignarButacas(String controladorDeConsultaSalas, String funcion, List<String> butacasOcupadas, int totalBoletos) {
        /*
         * LÓGICA GRUPO B
         */
        
        // iterar sobre el mapa de butacas de funcion buscando cuales coinciden con las ocupadas con respecto a su codigo alfanumérico
        // mostrar el mapa de butacas disponibles y ocupadas
        // seleccionar las butacas disponibles para asignar de acuerdo al total de boletos
        // devolver la lista de butacas asignadas

        List<String> butacas = new ArrayList<>();
        for (int i = 1; i <= totalBoletos; i++) butacas.add("F" + i);
        return butacas; // Retorna la lista de butacas asignadas
    }
}
