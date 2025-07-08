package com.cinemax.peliculas.controladores;

import com.cinemax.peliculas.modelos.entidades.FormatoFuncion;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.servicios.ServicioFuncion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.entidades.TipoEstreno;
import com.cinemax.peliculas.servicios.ServicioPelicula;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.servicios.ServicioSala;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ControladorFunciones {
    private ServicioFuncion servicioFuncion;
    private ServicioPelicula servicioPelicula;
    private ServicioSala servicioSala;
    private Scanner scanner;

    public ControladorFunciones() {
        this.servicioFuncion = new ServicioFuncion();
        this.servicioPelicula = new ServicioPelicula();
        this.servicioSala = new ServicioSala();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n=== GESTIÓN DE FUNCIONES ===");
            System.out.println("1. Programar nueva función");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            int opcion = leerOpcion();
            try {
                switch (opcion) {
                    case 1:
                        programarNuevaFuncion();
                        break;
                    case 0:
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void programarNuevaFuncion() throws SQLException {
        //Crear metodos para realizar validaciones
        //definir horario de trabajo (validacion)

        // Seleccionar película
        List<Pelicula> peliculas = servicioPelicula.listarTodasLasPeliculas();
        if (peliculas.isEmpty()) {
            System.out.println("No hay películas disponibles.");
            return;
        }
        System.out.println("Seleccione una película:");
        for (int i = 0; i < peliculas.size(); i++) {
            System.out.println((i + 1) + ". " + peliculas.get(i).getTitulo());
        }
        System.out.print("Opción: ");
        int peliculaIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (peliculaIdx < 0 || peliculaIdx >= peliculas.size()) {
            System.out.println("Selección inválida.");
            return;
        }
        Pelicula pelicula = peliculas.get(peliculaIdx);

        // Seleccionar sala
        List<Sala> salas = servicioSala.listarTodasLasSalas();
        if (salas.isEmpty()) {
            System.out.println("No hay salas disponibles.");
            return;
        }
        System.out.println("Seleccione una sala:");
        for (int i = 0; i < salas.size(); i++) {
            System.out.println((i + 1) + ". " + salas.get(i).getId());
        }
        System.out.print("Opción: ");
        int salaIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (salaIdx < 0 || salaIdx >= salas.size()) {
            System.out.println("Selección inválida.");
            return;
        }
        Sala sala = salas.get(salaIdx);

        // Fecha y hora de inicio
        System.out.print("Ingrese la fecha y hora de inicio (yyyy-MM-dd HH:mm): ");
        String fechaHoraInicioStr = scanner.nextLine().trim();
        LocalDateTime fechaHoraInicio = LocalDateTime.parse(fechaHoraInicioStr,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // Formato
        System.out.println("Seleccione el formato:");
        for (FormatoFuncion formato : FormatoFuncion.values()) {
            System.out.println((formato.ordinal() + 1) + ". " + formato.toString());
        }
        System.out.print("Opción: ");
        int formatoIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (formatoIdx < 0 || formatoIdx >= FormatoFuncion.values().length) {
            System.out.println("Selección inválida.");
            return;
        }
        FormatoFuncion formato = FormatoFuncion.values()[formatoIdx];

        // Tipo de estreno
        System.out.println("Seleccione el tipo de estreno:");
        for (TipoEstreno tipo : TipoEstreno.values()) {
            System.out.println((tipo.ordinal() + 1) + ". " + tipo.toString());
        }
        System.out.print("Opción: ");
        int tipoIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (tipoIdx < 0 || tipoIdx >= TipoEstreno.values().length) {
            System.out.println("Selección inválida.");
            return;
        }
        TipoEstreno tipoEstreno = TipoEstreno.values()[tipoIdx];

        // Llama al servicio para crear y validar la función (la duración es fija de 3
        // horas)
        Funcion nuevaFuncion = servicioFuncion.programarNuevaFuncion(
                pelicula, sala, fechaHoraInicio, formato, tipoEstreno);

        if (nuevaFuncion != null) {
            System.out.println("¡Función programada exitosamente!");
        } else {
            System.out.println("No se pudo programar la función. Verifique disponibilidad de la sala.");
        }

    }

    public void cerrar() {
        if (scanner != null)
            scanner.close();
    }
}
