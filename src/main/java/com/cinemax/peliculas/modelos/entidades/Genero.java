package com.cinemax.peliculas.modelos.entidades;

public enum Genero {
    ACCION("Acción"),
    COMEDIA("Comedia"),
    DRAMA("Drama"),
    TERROR("Terror"),
    CIENCIA_FICCION("Ciencia Ficción"),
    ANIMACION("Animación"),
    ROMANCE("Romance");
    
    private final String nombre;
    
    // Constructor del enum
    Genero(String nombre) {
        this.nombre = nombre;
    }
    
    // Getter para el nombre completo
    public String getNombre() {
        return nombre;
    }
    
    // Método para obtener por nombre
    public static Genero porNombre(String nombre) {
        for (Genero genero : values()) {
            if (genero.nombre.equalsIgnoreCase(nombre)) {
                return genero;
            }
        }
        throw new IllegalArgumentException("Nombre de género no válido: " + nombre);
    }
    
    // Método para validar si un string contiene géneros válidos separados por comas
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
    
    // Método para normalizar géneros (corrige formato y valida)
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
    
    // Método para obtener todos los géneros como string para mostrar al usuario
    public static String obtenerTodosLosGeneros() {
        StringBuilder sb = new StringBuilder();
        Genero[] generos = values();
        
        for (int i = 0; i < generos.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(generos[i].getNombre());
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return nombre;
    }
}
