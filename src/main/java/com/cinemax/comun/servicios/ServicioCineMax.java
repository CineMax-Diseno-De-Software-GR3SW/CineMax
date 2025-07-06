package com.cinemax.comun.servicios;

import java.util.List;

import com.cinemax.comun.modelos.entidades.CineMax;
import com.cinemax.comun.vistas.VistaCineMax;
import com.cinemax.peliculas.controladores.ControladorPelicula;

public class ServicioCineMax {

    public int manejarInicio(CineMax cineMax, VistaCineMax vistaPaginaPrincipal) {
        List<String> opciones = cineMax.obtenerOpciones();
        int cerrado = 0;
        do{
            cerrado =vistaPaginaPrincipal.mostrar(opciones);
            switch (cerrado) {
                case 1:
                    // Gestión de películas - Delegamos al ControladorPelicula
                    ControladorPelicula controladorPelicula = new ControladorPelicula();
                    try {
                        controladorPelicula.iniciar();
                    } catch (Exception e) {
                        System.err.println("Error en el módulo de películas: " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        controladorPelicula.cerrar();
                        System.out.println("Regresando al menú principal...\n");
                    }
                    break;
                case 2:
                    //Aquí va la gestión de salas...
                    break;
                case 3:
                    //Aquí va la gestión de boletos...

                    
                    break;
                case 4:
                    //Aquí va la gestión de empleados...
                    break;
                case 5:
                    //Aquí va la gestión de reportes...
                    break;
                default:
                    break;
            }
        }while(cerrado != 6);
        return cerrado;
    }
}
