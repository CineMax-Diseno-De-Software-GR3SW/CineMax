package com.cinemax.peliculas.modelos.entidades;

import java.util.List;

/**
 * Enumeración que representa los diferentes géneros cinematográficos disponibles
 * en el sistema CineMax.
 *
 * <p>Esta enumeración proporciona una lista predefinida de géneros de películas
 * con funcionalidades para validación, normalización y conversión de datos.
 * Facilita la categorización consistente de las películas en el sistema.
 *
 * <p>Géneros disponibles:
 * Acción, Comedia, Drama, Terror, Ciencia Ficción, Animación, Romance,
 * Documental, Aventura, Fantasía.
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public enum Genero {
    /** Género de acción */
    ACCION("Acción"),
    /** Género de comedia */
    COMEDIA("Comedia"),
    /** Género dramático */
    DRAMA("Drama"),
    /** Género de terror/horror */
    TERROR("Terror"),
    /** Género de ciencia ficción */
    CIENCIA_FICCION("Ciencia Ficción"),
    /** Género de animación */
    ANIMACION("Animación"),
    /** Género romántico */
    ROMANCE("Romance"),
    /** Género documental */
    DOCUMENTAL("Documental"),
    /** Género de aventuras */
    AVENTURA("Aventura"),
    /** Género de fantasía */
    FANTASIA("Fantasía");

    /** Nombre legible del género */
    private final String nombre;
    
    /**
     * Constructor del enum para inicializar el nombre del género.
     *
     * @param nombre Nombre legible del género cinematográfico
     */
    Genero(String nombre) {
        this.nombre = nombre;
    }
    
    /**
     * Obtiene el nombre completo y legible del género.
     *
     * @return Nombre del género con formato apropiado
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Busca un género por su nombre, ignorando mayúsculas y minúsculas.
     *
     * @param nombre Nombre del género a buscar
     * @return El enum Genero correspondiente
     * @throws IllegalArgumentException Si el nombre no corresponde a ningún género válido
     */
    public static Genero porNombre(String nombre) {
        for (Genero genero : values()) {
            if (genero.nombre.equalsIgnoreCase(nombre)) {
                return genero;
            }
        }
        throw new IllegalArgumentException("Nombre de género no válido: " + nombre);
    }
    
    /**
     * Valida si una cadena contiene géneros válidos separados por comas.
     *
     * @param generos Cadena con géneros separados por comas
     * @return true si todos los géneros son válidos, false en caso contrario
     */
    public static boolean validarGeneros(String generos) {
        if (generos == null || generos.trim().isEmpty()) {
            return false;
        }
        
        String[] generosArray = generos.split(",");
        for (String genero : generosArray) {
            try {
                porNombre(genero.trim());
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Normaliza una cadena de géneros, corrigiendo formato y validando contenido.
     *
     * <p>Este método toma una cadena con géneros separados por comas,
     * valida que todos sean géneros válidos y retorna una cadena
     * formateada correctamente.
     *
     * @param generos Cadena con géneros separados por comas
     * @return Cadena normalizada con géneros válidos y formato correcto
     * @throws IllegalArgumentException Si la cadena está vacía o contiene géneros inválidos
     */
    public static String normalizarGeneros(String generos) {
        if (generos == null || generos.trim().isEmpty()) {
            throw new IllegalArgumentException("Los géneros no pueden estar vacíos");
        }
        
        String[] generosArray = generos.split(",");
        StringBuilder resultado = new StringBuilder();
        
        for (int i = 0; i < generosArray.length; i++) {
            String generoLimpio = generosArray[i].trim();
            
            // Validar que el género existe
            Genero generoEnum = porNombre(generoLimpio);
            
            if (i > 0) {
                resultado.append(", ");
            }
            resultado.append(generoEnum.getNombre());
        }
        
        return resultado.toString();
    }
    
    /**
     * Obtiene una lista con todos los nombres de géneros disponibles.
     *
     * @return Lista de strings con los nombres de todos los géneros
     */
    public static List<String> obtenerTodosLosGeneros() {
        Genero[] generos = values();
        List<String> lista = new java.util.ArrayList<>();
        for (Genero genero : generos) {
            lista.add(genero.getNombre());
        }
        return lista;
    }
    
    /**
     * Retorna el nombre del género como representación textual.
     *
     * @return Nombre del género
     */
    @Override
    public String toString() {
        return nombre;
    }
}
