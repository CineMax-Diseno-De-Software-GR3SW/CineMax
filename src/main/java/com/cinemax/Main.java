package com.cinemax;

import com.cinemax.comun.controladores.ControladorCineMax;
import com.cinemax.comun.modelos.entidades.CineMax;
import com.cinemax.comun.vistas.VistaCineMax;


public class Main{
    public static void main(String[] args) {
        VistaCineMax vistaCineMax = new VistaCineMax();
        CineMax cineMax = new CineMax();
        ControladorCineMax controladorCineMax = new ControladorCineMax(cineMax, vistaCineMax);
        
        // Iniciar la aplicación con el menú principal de CineMax
        try {
            controladorCineMax.mostrarPaginaPrincipal();
        } catch (Exception e) {
            System.err.println("Error crítico en la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\nGracias por usar CineMax!");
    }
}