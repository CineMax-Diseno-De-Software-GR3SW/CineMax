package com.cinemax.peliculas.controladores;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.cinemax.peliculas.modelos.entidades.Cartelera;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.persistencia.FuncionDAO;
import com.cinemax.peliculas.modelos.persistencia.PeliculaDAO;
import com.cinemax.peliculas.servicios.ServicioPelicula;

public class ControladorCartelera {
    private ServicioPelicula servicioPelicula;
    private Scanner scanner;
    private Cartelera cartelera;
    private FuncionDAO funcionDAO;        // Nuevo atributo
    private PeliculaDAO peliculaDAO;

    public ControladorCartelera() {
        this.servicioPelicula = new ServicioPelicula();
        this.scanner = new Scanner(System.in);
        this.cartelera = new Cartelera(new ArrayList<>());
        this.funcionDAO = new FuncionDAO();
        this.peliculaDAO = new PeliculaDAO();
    }

    public void iniciar() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n=== GESTIÓN DE CARTELERA ===");

            mostrarMenu();

            int opcion = leerOpcion();
            try {
                switch (opcion) {
                    case 1:
                        actualizarCartelera();
                        break;
                    case 2:
                        listarPeliculasEnCartelera();
                        break;
                    case 3:
                        buscarPeliculaPorTituloEnCartelera();
                        break;
                    case 4:
                        buscarPeliculaPorIdEnCartelera();
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

            if (continuar) {
                esperarEnter();
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("1. Actualizar la cartelera");
        System.out.println("2. Listar peliculas en la cartelera");
        System.out.println("3. Buscar pelicula en la cartelera por título");
        System.out.println("4. Buscar pelicula en la cartelera por ID");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private void esperarEnter() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void actualizarCartelera() {
        try {
            List<Integer> idsPeliculas = funcionDAO.listarIdsPeliculasDeFuncionesFuturas();
            List<Pelicula> nuevasPeliculas = new ArrayList<>();
            for (Integer id : idsPeliculas) {
                Pelicula p = peliculaDAO.buscarPorId(id);
                if (p != null && !nuevasPeliculas.contains(p)) {
                    nuevasPeliculas.add(p);
                }
            }
            cartelera.setPeliculas(nuevasPeliculas);
            System.out.println("Cartelera actualizada correctamente.");
        } catch (Exception e) {
            System.err.println("Error al actualizar la cartelera: " + e.getMessage());
        }
    }

    private void listarPeliculasEnCartelera() {
        List<Pelicula> lista = cartelera.getPeliculas();
        if (lista.isEmpty()) {
            System.out.println("No hay películas en la cartelera.");
            return;
        }
        System.out.println("Películas en cartelera:");
        for (Pelicula p : lista) {
            System.out.printf("%d - %s%n", p.getId(), p.getTitulo());
        }
    }

    private void buscarPeliculaPorTituloEnCartelera() {
        System.out.print("Ingrese el título o parte del título: ");
        String titulo = scanner.nextLine().trim().toLowerCase();
        List<Pelicula> resultados = new ArrayList<>();
        for (Pelicula p : cartelera.getPeliculas()) {
            if (p.getTitulo().toLowerCase().contains(titulo)) {
                resultados.add(p);
            }
        }
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron películas con ese título en la cartelera.");
        } else {
            System.out.println("Resultados:");
            for (Pelicula p : resultados) {
                System.out.printf("%d - %s%n", p.getId(), p.getTitulo());
            }
        }
    }

    private void buscarPeliculaPorIdEnCartelera() {
        System.out.print("Ingrese el ID de la película: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            for (Pelicula p : cartelera.getPeliculas()) {
                if (p.getId() == id) {
                    System.out.printf("Película encontrada: %d - %s%n", p.getId(), p.getTitulo());
                    return;
                }
            }
            System.out.println("No se encontró la película en la cartelera.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void cerrar() {
        if (scanner != null)
            scanner.close();
    }
}
