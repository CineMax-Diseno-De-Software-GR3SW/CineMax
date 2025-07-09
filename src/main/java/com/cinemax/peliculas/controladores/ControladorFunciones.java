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
            System.out.println("2. Listar todas las funciones programadas");
            System.out.println("3. Editar funcion");
            System.out.println("4. Eliminar funcion");
            System.out.println("5. Consultar funciones de una sala");
            System.out.println("6. Consultar detalles de una función");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            int opcion = leerOpcion();
            try {
                switch (opcion) {
                    case 1:
                        programarNuevaFuncion();
                        break;
                    case 2:
                        listarTodasLasFunciones();
                        break;
                    case 3:
                        editarFuncion();
                        break;
                    case 4:
                        // eliminarFuncion();
                        break;
                    case 5:
                        mostrarFuncionesDeSala();
                        break;
                     case 6:
                        mostrarDetalleFuncion();
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
        Pelicula pelicula = seleccionarPelicula();
        if (pelicula == null)
            return;

        Sala sala = seleccionarSala();
        if (sala == null)
            return;

        mostrarFuncionesDeSala(sala);

        System.out.print("Ingrese la fecha y hora de inicio (yyyy-MM-dd HH:mm): ");
        String fechaHoraInicioStr = scanner.nextLine().trim();
        LocalDateTime fechaHoraInicio = LocalDateTime.parse(fechaHoraInicioStr,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        FormatoFuncion formato = seleccionarFormato();
        TipoEstreno tipoEstreno = seleccionarTipoEstreno();

        Funcion nuevaFuncion = servicioFuncion.programarNuevaFuncion(
                pelicula, sala, fechaHoraInicio, formato, tipoEstreno);

        if (nuevaFuncion != null) {
            System.out.println("¡Función programada exitosamente!");
        } else {
            System.out.println("No se pudo programar la función. Verifique disponibilidad de la sala.");
        }
    }

    private Pelicula seleccionarPelicula() throws SQLException {
        List<Pelicula> peliculas = servicioPelicula.listarTodasLasPeliculas();
        if (peliculas.isEmpty()) {
            System.out.println("No hay películas disponibles.");
            return null;
        }
        System.out.println("Seleccione una película:");
        for (int i = 0; i < peliculas.size(); i++) {
            System.out.println((i + 1) + ". " + peliculas.get(i).getTitulo());
        }
        while (true) {
            System.out.print("Opción: ");
            try {
                int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (idx >= 0 && idx < peliculas.size()) {
                    return peliculas.get(idx);
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Selección inválida. Intente nuevamente.");
        }
    }

    private Sala seleccionarSala() throws SQLException {
        List<Sala> salas = servicioSala.listarTodasLasSalas();
        if (salas.isEmpty()) {
            System.out.println("No hay salas disponibles.");
            return null;
        }
        System.out.println("Seleccione una sala:");
        for (int i = 0; i < salas.size(); i++) {
            System.out.println((i + 1) + ". " + salas.get(i).getId());
        }
        while (true) {
            System.out.print("Opción: ");
            try {
                int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (idx >= 0 && idx < salas.size()) {
                    return salas.get(idx);
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Selección inválida. Intente nuevamente.");
        }
    }

    private FormatoFuncion seleccionarFormato() {
        System.out.println("Seleccione el formato:");
        for (FormatoFuncion formato : FormatoFuncion.values()) {
            System.out.println((formato.ordinal() + 1) + ". " + formato.toString());
        }
        while (true) {
            System.out.print("Opción: ");
            try {
                int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (idx >= 0 && idx < FormatoFuncion.values().length) {
                    return FormatoFuncion.values()[idx];
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Selección inválida. Intente nuevamente.");
        }
    }

    private TipoEstreno seleccionarTipoEstreno() {
        System.out.println("Seleccione el tipo de estreno:");
        for (TipoEstreno tipo : TipoEstreno.values()) {
            System.out.println((tipo.ordinal() + 1) + ". " + tipo.toString());
        }
        while (true) {
            System.out.print("Opción: ");
            try {
                int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (idx >= 0 && idx < TipoEstreno.values().length) {
                    return TipoEstreno.values()[idx];
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Selección inválida. Intente nuevamente.");
        }
    }

    private void listarTodasLasFunciones() throws SQLException {
        System.out.println("\nLISTADO DE FUNCIONES");
        System.out.println("-".repeat(30));

        List<Funcion> funciones = servicioFuncion.listarTodasLasFunciones();

        if (funciones.isEmpty()) {
            System.out.println("No hay funciones registradas en el sistema.");
            return;
        }

        System.out.printf("%-4s %-25s %-8s %-8s %-10s %-10s %-10s%n",
                "ID", "PELÍCULA", "SALA", "INICIO", "FIN", "FORMATO", "ESTRENO");
        System.out.println("-".repeat(80));

        for (Funcion funcion : funciones) {
            System.out.printf("%-4d %-25s %-8s %-8s %-10s %-10s %-10s%n",
                    funcion.getId(),
                    funcion.getPelicula() != null ? funcion.getPelicula().getTitulo() : "N/A",
                    funcion.getSala() != null ? funcion.getSala().getNombre() : "N/A",
                    funcion.getFechaHoraInicio().toLocalTime(),
                    funcion.getFechaHoraFin().toLocalTime(),
                    funcion.getFormato().toString(),
                    funcion.getTipoEstreno().toString());
        }

        System.out.println("\nTotal: " + funciones.size() + " funciones");
    }

    private void editarFuncion() throws SQLException {
        System.out.println("\nEDITAR FUNCIÓN");
        System.out.println("-".repeat(25));

        listarTodasLasFunciones();

        System.out.print("Ingrese el ID de la función a editar: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        Funcion funcionExistente = servicioFuncion.buscarPorId(id);

        if (funcionExistente == null) {
            System.out.println("No se encontró ninguna función con ID: " + id);
            return;
        }

        System.out.println("Función actual:");
        mostrarDetalleFuncion(funcionExistente);

        System.out.println("\nIngrese los nuevos datos (presione Enter para mantener el valor actual):");

        Pelicula pelicula = seleccionarPelicula();
        if (pelicula == null)
            pelicula = funcionExistente.getPelicula();

        Sala sala = seleccionarSala();
        if (sala == null)
            sala = funcionExistente.getSala();

        mostrarFuncionesDeSala(sala);

        System.out.print("Fecha y hora de inicio ["
                + funcionExistente.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                + "]: ");
        String fechaHoraInicioStr = scanner.nextLine().trim();
        LocalDateTime fechaHoraInicio = fechaHoraInicioStr.isEmpty()
                ? funcionExistente.getFechaHoraInicio()
                : LocalDateTime.parse(fechaHoraInicioStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        FormatoFuncion formato = seleccionarFormato();
        if (formato == null)
            formato = funcionExistente.getFormato();

        TipoEstreno tipoEstreno = seleccionarTipoEstreno();
        if (tipoEstreno == null)
            tipoEstreno = funcionExistente.getTipoEstreno();

        try {
            servicioFuncion.editarFuncion(id, pelicula, sala, fechaHoraInicio, formato, tipoEstreno);
            System.out.println("¡Función editada exitosamente!");
        } catch (Exception e) {
            System.err.println("Error al editar función: " + e.getMessage());
        }
    }

    private void mostrarDetalleFuncion(Funcion funcionExistente) {
        System.out.println("Película actual: " + funcionExistente.getPelicula().getTitulo());
        System.out.println("Sala actual: " + funcionExistente.getSala().getNombre());
        System.out.println("Fecha y hora de inicio actual: "
                + funcionExistente.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        System.out.println("Formato actual: " + funcionExistente.getFormato());
        System.out.println("Tipo de estreno actual: " + funcionExistente.getTipoEstreno());
    }

    private void mostrarDetalleFuncion() throws SQLException {

        System.out.println("Ingresar id de la funcion a consultar: ");

        int opcion = leerOpcion();
        Funcion funcionExistente = servicioFuncion.buscarPorId(opcion);

        System.out.println("Película actual: " + funcionExistente.getPelicula().getTitulo());
        System.out.println("Sala actual: " + funcionExistente.getSala().getNombre());
        System.out.println("Fecha y hora de inicio actual: "
                + funcionExistente.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        System.out.println("Formato actual: " + funcionExistente.getFormato());
        System.out.println("Tipo de estreno actual: " + funcionExistente.getTipoEstreno());
    }

    private void mostrarFuncionesDeSala(Sala sala) throws SQLException {
        List<Funcion> funciones = servicioFuncion.listarFuncionesPorSala(sala.getId());
        if (funciones.isEmpty()) {
            System.out.println("No hay funciones programadas en la sala seleccionada.");
            return;
        }
        System.out.println("Funciones programadas en la sala " + sala.getNombre() + ":");
        for (Funcion funcion : funciones) {
            System.out.printf("ID: %d | Película: %s | Inicio: %s | Fin: %s | Formato: %s | Estreno: %s%n",
                    funcion.getId(),
                    funcion.getPelicula() != null ? funcion.getPelicula().getTitulo() : "N/A",
                    funcion.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    funcion.getFechaHoraFin().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    funcion.getFormato(),
                    funcion.getTipoEstreno());
        }
    }

    private void mostrarFuncionesDeSala() throws SQLException {
        System.out.println("Ingrese el id de la sala a consultar: ");
        int id = leerOpcion();

        List<Funcion> funciones = servicioFuncion.listarFuncionesPorSala(id);
        if (funciones.isEmpty()) {
            System.out.println("No hay funciones programadas en la sala seleccionada.");
            return;
        }
        System.out.println("Funciones programadas en la sala con id " + id);
        for (Funcion funcion : funciones) {
            System.out.printf("ID: %d | Película: %s | Inicio: %s | Fin: %s | Formato: %s | Estreno: %s%n",
                    funcion.getId(),
                    funcion.getPelicula() != null ? funcion.getPelicula().getTitulo() : "N/A",
                    funcion.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    funcion.getFechaHoraFin().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    funcion.getFormato(),
                    funcion.getTipoEstreno());
        }
    }

    public void cerrar() {
        if (scanner != null)
            scanner.close();
    }
}
