package com.cinemax.comun.servicios;

import java.util.List;

import com.cinemax.comun.modelos.entidades.CineMax;
import com.cinemax.comun.vistas.VistaCineMax;
import com.cinemax.peliculas.controladores.ControladorFunciones;
import com.cinemax.peliculas.controladores.ControladorPelicula;

public class ServicioCineMax {

    public int manejarInicio(CineMax cineMax, VistaCineMax vistaPaginaPrincipal) {
        List<String> opciones = cineMax.obtenerOpciones();
        int cerrado = 0;

        java.util.Scanner scanner = new java.util.Scanner(System.in);
        do{
            cerrado =vistaPaginaPrincipal.mostrar(opciones);
            switch (cerrado) {
                case 1:
                     int subopcion = 0;
                    do {
                        System.out.println("1. Gestión de películas");
                        System.out.println("2. Gestión de funciones");
                        System.out.println("3. Volver al menú principal");
                        System.out.print("Selecciona una opción: ");
                        try {
                            subopcion = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            subopcion = 0;
                        }
                        switch (subopcion) {
                            case 1:
                                ControladorPelicula controladorPelicula = new ControladorPelicula();
                                try {
                                    controladorPelicula.iniciar();
                                } catch (Exception e) {
                                    System.err.println("Error en el módulo de películas: " + e.getMessage());
                                    e.printStackTrace();
                                } finally {
                                    controladorPelicula.cerrar();
                                    System.out.println("Regresando al submenú...\n");
                                }
                                break;
                            case 2:
                                ControladorFunciones controladorFunciones = new ControladorFunciones();
                                try {
                                    controladorFunciones.iniciar();
                                } catch (Exception e) {
                                    System.err.println("Error en el módulo de funciones: " + e.getMessage());
                                    e.printStackTrace();
                                } finally {
                                    controladorFunciones.cerrar();
                                    System.out.println("Regresando al submenú...\n");
                                }
                                break;
                            case 3:
                                System.out.println("Volviendo al menú principal...\n");
                                break;
                            default:
                                System.out.println("Opción inválida, intenta de nuevo.");
                        }
                    } while (subopcion != 3);
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
