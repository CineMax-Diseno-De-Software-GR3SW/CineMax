package com.cinemax.peliculas.controladores;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.cinemax.peliculas.modelos.entidades.Genero;
import com.cinemax.peliculas.modelos.entidades.Idioma;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.servicios.ServicioPelicula;


public class ControladorPelicula {
    
    private ServicioPelicula servicioPelicula;
    private Scanner scanner;
    
    // Constructor
    public ControladorPelicula() {
        this.servicioPelicula = new ServicioPelicula();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Método principal que inicia el menú interactivo
     */
    public void iniciar() {
        System.out.println("=== SISTEMA DE GESTIÓN DE PELÍCULAS ===");
        System.out.println("Bienvenido al catálogo de películas\n");
        
        boolean continuar = true;
        
        while (continuar) {
            mostrarMenu();
            int opcion = leerOpcion();
            
            try {
                switch (opcion) {
                    case 1:
                        crearNuevaPelicula();
                        break;
                    case 2:
                        listarTodasLasPeliculas();
                        break;
                    case 3:
                        buscarPeliculaPorId();
                        break;
                    case 4:
                        buscarPeliculasPorTitulo();
                        break;
                    case 5:
                        actualizarPelicula();
                        break;
                    case 6:
                        eliminarPelicula();
                        break;
                    case 7:
                        mostrarEstadisticas();
                        break;
                    case 8:
                        verificarDuplicados();
                        break;
                    case 0:
                        continuar = false;
                        System.out.println("¡Gracias por usar el sistema! Hasta pronto.");
                        break;
                    default:
                        System.out.println("Opción no válida. Por favor, seleccione una opción del 0 al 8.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
            
            if (continuar) {
                esperarEnter();
            }
        }
    }
    
    /**
     * Muestra el menú principal
     */
    private void mostrarMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("              MENÚ PRINCIPAL");
        System.out.println("=".repeat(50));
        System.out.println("1. Agregar nueva película");
        System.out.println("2. Listar todas las películas");
        System.out.println("3. Buscar película por ID");
        System.out.println("4. Buscar películas por título");
        System.out.println("5. Actualizar película");
        System.out.println("6. Eliminar película");
        System.out.println("7. Ver estadísticas");
        System.out.println("8. Verificar duplicados");
        System.out.println("0. Salir");
        System.out.println("=".repeat(50));
        System.out.print("Seleccione una opción: ");
    }
    
    /**
     * Lee y valida la opción del menú
     */
    private int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1; // Opción inválida
        }
    }
    
    /**
     * Crea una nueva película
     */
    private void crearNuevaPelicula() throws SQLException {
        System.out.println("\nAGREGAR NUEVA PELÍCULA");
        System.out.println("-".repeat(30));
        
        try {
            System.out.print("Título: ");
            String titulo = scanner.nextLine().trim();
            
            System.out.print("Sinopsis: ");
            String sinopsis = scanner.nextLine().trim();
            
            System.out.print("Duración (minutos): ");
            int duracion = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Año: ");
            int anio = Integer.parseInt(scanner.nextLine().trim());
            
            Idioma idioma = seleccionarIdioma();
            
            String genero = seleccionarGeneros();
            
            System.out.print("URL de imagen (opcional): ");
            String imagenUrl = scanner.nextLine().trim();
            if (imagenUrl.isEmpty()) {
                imagenUrl = null;
            }
            
            Pelicula nuevaPelicula = servicioPelicula.crearPelicula(
                titulo, sinopsis, duracion, anio, idioma, genero, imagenUrl
            );
            
            System.out.println("Película creada exitosamente!");
            mostrarDetallePelicula(nuevaPelicula);
            
        } catch (NumberFormatException e) {
            System.err.println("Error: Debe ingresar números válidos para duración y año.");
        } catch (IllegalArgumentException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Lista todas las películas
     */
    private void listarTodasLasPeliculas() throws SQLException {
        System.out.println("\nLISTADO DE PELÍCULAS");
        System.out.println("-".repeat(30));
        
        List<Pelicula> peliculas = servicioPelicula.listarTodasLasPeliculas();
        
        if (peliculas.isEmpty()) {
            System.out.println("No hay películas registradas en el sistema.");
            return;
        }
        
        System.out.printf("%-4s %-30s %-6s %-8s %-15s %-20s%n", 
                         "ID", "TÍTULO", "AÑO", "DURACIÓN", "IDIOMA", "GÉNERO");
        System.out.println("-".repeat(90));
        
        for (Pelicula pelicula : peliculas) {
            System.out.printf("%-4d %-30s %-6d %-8d %-15s %-20s%n",
                pelicula.getId(),
                truncarTexto(pelicula.getTitulo(), 30),
                pelicula.getAnio(),
                pelicula.getDuracionMinutos(),
                pelicula.getIdioma() != null ? pelicula.getIdioma().getNombre() : "N/A",
                truncarTexto(pelicula.getGenero(), 20)
            );
        }
        
        System.out.println("\nTotal: " + peliculas.size() + " películas");
    }
    
    /**
     * Busca una película por ID
     */
    private void buscarPeliculaPorId() throws SQLException {
        System.out.println("\nBUSCAR PELÍCULA POR ID");
        System.out.println("-".repeat(30));
        
        try {
            System.out.print("Ingrese el ID de la película: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            
            Pelicula pelicula = servicioPelicula.buscarPeliculaPorId(id);
            
            if (pelicula != null) {
                System.out.println("Película encontrada:");
                mostrarDetallePelicula(pelicula);
            } else {
                System.out.println("No se encontró ninguna película con ID: " + id);
            }
            
        } catch (NumberFormatException e) {
            System.err.println("Error: Debe ingresar un número válido para el ID.");
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Busca películas por título
     */
    private void buscarPeliculasPorTitulo() throws SQLException {
        System.out.println("\nBUSCAR PELÍCULAS POR TÍTULO");
        System.out.println("-".repeat(35));
        
        try {
            System.out.print("Ingrese el título o parte del título: ");
            String titulo = scanner.nextLine().trim();
            
            List<Pelicula> peliculas = servicioPelicula.buscarPeliculasPorTitulo(titulo);
            
            if (peliculas.isEmpty()) {
                System.out.println("No se encontraron películas con el título: " + titulo);
                return;
            }
            
            System.out.println("Se encontraron " + peliculas.size() + " película(s):");
            System.out.println();
            
            for (Pelicula pelicula : peliculas) {
                mostrarDetallePelicula(pelicula);
                System.out.println("-".repeat(40));
            }
            
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza una película existente
     */
    private void actualizarPelicula() throws SQLException {
        System.out.println("\nACTUALIZAR PELÍCULA");
        System.out.println("-".repeat(25));
        
        try {
            System.out.print("Ingrese el ID de la película a actualizar: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            
            // Primero verificar que existe
            Pelicula peliculaExistente = servicioPelicula.buscarPeliculaPorId(id);
            if (peliculaExistente == null) {
                System.out.println("No se encontró ninguna película con ID: " + id);
                return;
            }
            
            System.out.println("Película actual:");
            mostrarDetallePelicula(peliculaExistente);
            System.out.println("\nIngrese los nuevos datos (presione Enter para mantener el valor actual):");
            
            System.out.print("Título [" + peliculaExistente.getTitulo() + "]: ");
            String titulo = scanner.nextLine().trim();
            if (titulo.isEmpty()) titulo = peliculaExistente.getTitulo();
            
            System.out.print("Sinopsis [" + truncarTexto(peliculaExistente.getSinopsis(), 50) + "...]: ");
            String sinopsis = scanner.nextLine().trim();
            if (sinopsis.isEmpty()) sinopsis = peliculaExistente.getSinopsis();
            
            System.out.print("Duración [" + peliculaExistente.getDuracionMinutos() + "]: ");
            String duracionStr = scanner.nextLine().trim();
            int duracion = duracionStr.isEmpty() ? peliculaExistente.getDuracionMinutos() : 
                          Integer.parseInt(duracionStr);
            
            System.out.print("Año [" + peliculaExistente.getAnio() + "]: ");
            String anioStr = scanner.nextLine().trim();
            int anio = anioStr.isEmpty() ? peliculaExistente.getAnio() : 
                      Integer.parseInt(anioStr);
            
            System.out.println("Idioma actual: " + 
                (peliculaExistente.getIdioma() != null ? peliculaExistente.getIdioma().getNombre() : "N/A"));
            System.out.print("¿Cambiar idioma? (s/N): ");
            String cambiarIdioma = scanner.nextLine().trim().toLowerCase();
            Idioma idioma = peliculaExistente.getIdioma();
            if (cambiarIdioma.equals("s") || cambiarIdioma.equals("si")) {
                idioma = seleccionarIdioma();
            }
            
            System.out.print("Género [" + peliculaExistente.getGenero() + "]: ");
            System.out.println("¿Cambiar géneros? (s/N): ");
            String cambiarGenero = scanner.nextLine().trim().toLowerCase();
            String genero = peliculaExistente.getGenero();
            if (cambiarGenero.equals("s") || cambiarGenero.equals("si")) {
                genero = seleccionarGeneros();
            }
            
            System.out.print("URL imagen [" + 
                (peliculaExistente.getImagenUrl() != null ? peliculaExistente.getImagenUrl() : "N/A") + "]: ");
            String imagenUrl = scanner.nextLine().trim();
            if (imagenUrl.isEmpty()) imagenUrl = peliculaExistente.getImagenUrl();
            
            servicioPelicula.actualizarPelicula(id, titulo, sinopsis, duracion, anio, idioma, genero, imagenUrl);
            
            System.out.println("Película actualizada exitosamente!");
            
            // Mostrar la película actualizada
            Pelicula peliculaActualizada = servicioPelicula.buscarPeliculaPorId(id);
            mostrarDetallePelicula(peliculaActualizada);
            
        } catch (NumberFormatException e) {
            System.err.println("Error: Debe ingresar números válidos.");
        } catch (IllegalArgumentException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Elimina una película
     */
    private void eliminarPelicula() throws SQLException {
        System.out.println("\nELIMINAR PELÍCULA");
        System.out.println("-".repeat(22));
        
        try {
            System.out.print("Ingrese el ID de la película a eliminar: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            
            // Primero verificar que existe y mostrar los datos
            Pelicula pelicula = servicioPelicula.buscarPeliculaPorId(id);
            if (pelicula == null) {
                System.out.println("No se encontró ninguna película con ID: " + id);
                return;
            }
            
            System.out.println("Película a eliminar:");
            mostrarDetallePelicula(pelicula);
            
            System.out.print("\n¿Está seguro que desea eliminar esta película? (s/N): ");
            String confirmacion = scanner.nextLine().trim().toLowerCase();
            
            if (confirmacion.equals("s") || confirmacion.equals("si")) {
                servicioPelicula.eliminarPelicula(id);
                System.out.println("Película eliminada exitosamente.");
            } else {
                System.out.println("Operación cancelada.");
            }
            
        } catch (NumberFormatException e) {
            System.err.println("Error: Debe ingresar un número válido para el ID.");
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Muestra estadísticas del catálogo
     */
    private void mostrarEstadisticas() throws SQLException {
        System.out.println("\nESTADÍSTICAS DEL CATÁLOGO");
        System.out.println("-".repeat(32));
        
        String estadisticas = servicioPelicula.obtenerEstadisticas();
        System.out.println(estadisticas);
    }
    
    /**
     * Verifica si existen películas duplicadas
     */
    private void verificarDuplicados() throws SQLException {
        System.out.println("\nVERIFICAR DUPLICADOS");
        System.out.println("-".repeat(25));
        
        System.out.print("Título: ");
        String titulo = scanner.nextLine().trim();
        
        try {
            System.out.print("Año: ");
            int anio = Integer.parseInt(scanner.nextLine().trim());
            
            boolean existe = servicioPelicula.existePeliculaDuplicada(titulo, anio);
            
            if (existe) {
                System.out.println("Ya existe una película con el título '" + titulo + "' del año " + anio);
            } else {
                System.out.println("No se encontraron duplicados para '" + titulo + "' (" + anio + ")");
            }
            
        } catch (NumberFormatException e) {
            System.err.println("Error: Debe ingresar un año válido.");
        }
    }
    
    /**
     * Permite al usuario seleccionar un idioma
     */
    private Idioma seleccionarIdioma() {
        System.out.println("Seleccione un idioma:");
        Idioma[] idiomas = Idioma.values();
        
        for (int i = 0; i < idiomas.length; i++) {
            System.out.println((i + 1) + ". " + idiomas[i].getNombre());
        }
        
        while (true) {
            try {
                System.out.print("Opción (1-" + idiomas.length + "): ");
                int opcion = Integer.parseInt(scanner.nextLine().trim());
                
                if (opcion >= 1 && opcion <= idiomas.length) {
                    return idiomas[opcion - 1];
                } else {
                    System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            }
        }
    }
    
    /**
     * Permite al usuario seleccionar uno o múltiples géneros
     */
    private String seleccionarGeneros() {
        System.out.println("Seleccione géneros (puede elegir múltiples):");
        Genero[] generos = Genero.values();
        
        // Mostrar todos los géneros disponibles
        for (int i = 0; i < generos.length; i++) {
            System.out.println((i + 1) + ". " + generos[i].getNombre());
        }
        
        System.out.println("\nIngrese los números separados por comas (ej: 1,4,7):");
        
        while (true) {
            try {
                System.out.print("Géneros: ");
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    System.out.println("Debe seleccionar al menos un género.");
                    continue;
                }
                
                // Procesar la entrada
                String[] numerosStr = input.split(",");
                StringBuilder generosSeleccionados = new StringBuilder();
                
                for (int i = 0; i < numerosStr.length; i++) {
                    int numero = Integer.parseInt(numerosStr[i].trim());
                    
                    if (numero >= 1 && numero <= generos.length) {
                        if (i > 0) {
                            generosSeleccionados.append(", ");
                        }
                        generosSeleccionados.append(generos[numero - 1].getNombre());
                    } else {
                        System.out.println("Número inválido: " + numero + ". Debe estar entre 1 y " + generos.length);
                        throw new NumberFormatException(); // Para reintentar
                    }
                }
                
                return generosSeleccionados.toString();
                
            } catch (NumberFormatException e) {
                System.out.println("Formato inválido. Use números separados por comas (ej: 1,4,7).");
            }
        }
    }
    private void mostrarDetallePelicula(Pelicula pelicula) {
        System.out.println("┌" + "─".repeat(48) + "┐");
        System.out.printf("│ ID: %-42d │%n", pelicula.getId());
        System.out.printf("│ Título: %-38s │%n", truncarTexto(pelicula.getTitulo(), 38));
        System.out.printf("│ Año: %-41d │%n", pelicula.getAnio());
        System.out.printf("│ Duración: %32d min │%n", pelicula.getDuracionMinutos());
        System.out.printf("│ Idioma: %-38s │%n", 
            pelicula.getIdioma() != null ? pelicula.getIdioma().getNombre() : "N/A");
        System.out.printf("│ Género: %-38s │%n", truncarTexto(pelicula.getGenero(), 38));
        System.out.println("│ Sinopsis:                                      │");
        
        // Dividir sinopsis en líneas de máximo 44 caracteres
        String sinopsis = pelicula.getSinopsis();
        if (sinopsis != null && !sinopsis.isEmpty()) {
            String[] palabras = sinopsis.split(" ");
            StringBuilder linea = new StringBuilder();
            
            for (String palabra : palabras) {
                if (linea.length() + palabra.length() + 1 <= 44) {
                    if (linea.length() > 0) linea.append(" ");
                    linea.append(palabra);
                } else {
                    System.out.printf("│ %-46s │%n", linea.toString());
                    linea = new StringBuilder(palabra);
                }
            }
            if (linea.length() > 0) {
                System.out.printf("│ %-46s │%n", linea.toString());
            }
        } else {
            System.out.printf("│ %-46s │%n", "N/A");
        }
        
        if (pelicula.getImagenUrl() != null && !pelicula.getImagenUrl().isEmpty()) {
            System.out.printf("│ URL: %-41s │%n", truncarTexto(pelicula.getImagenUrl(), 41));
        }
        
        System.out.println("└" + "─".repeat(48) + "┘");
    }
    
    /**
     * Trunca un texto a la longitud especificada
     */
    private String truncarTexto(String texto, int longitud) {
        if (texto == null) return "N/A";
        if (texto.length() <= longitud) return texto;
        return texto.substring(0, longitud - 3) + "...";
    }
    
    /**
     * Pausa la ejecución hasta que el usuario presione Enter
     */
    private void esperarEnter() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Cierra el scanner (llamar al finalizar la aplicación)
     */
    public void cerrar() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
