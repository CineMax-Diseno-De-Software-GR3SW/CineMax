package com.cinemax.peliculas.modelos.entidades;

public enum Idioma {
    ESPANOL("Español", "es"),
    INGLES("Ingles", "en"),
    CHINO("Chino", "zh");
    
    private final String nombre;
    private final String codigo;
    
    // Constructor del enum
    Idioma(String nombre, String codigo) {
        this.nombre = nombre;
        this.codigo = codigo;
    }
    
    // Getter para el nombre completo
    public String getNombre() {
        return nombre;
    }
    
    // Getter para el código ISO
    public String getCodigo() {
        return codigo;
    }
    
    // Método para obtener por código
    public static Idioma porCodigo(String codigo) {
        for (Idioma idioma : values()) {
            if (idioma.codigo.equalsIgnoreCase(codigo)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Código de idioma no válido: " + codigo);
    }
    
    // Método para obtener por nombre
    public static Idioma porNombre(String nombre) {
        for (Idioma idioma : values()) {
            if (idioma.nombre.equalsIgnoreCase(nombre)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Nombre de idioma no válido: " + nombre);
    }
    
    @Override
    public String toString() {
        return nombre;
    }
}
