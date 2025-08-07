package com.cinemax.peliculas.modelos.entidades;

/**
 * Enumeración que representa los diferentes idiomas disponibles para las películas
 * en el sistema CineMax.
 *
 * <p>Esta enumeración maneja los idiomas soportados por el sistema, proporcionando
 * tanto el nombre completo del idioma como su código ISO estándar. Facilita
 * la internacionalización y estandarización del manejo de idiomas.
 *
 * <p>Idiomas soportados actualmente:
 * <ul>
 *   <li>Español (es)</li>
 *   <li>Inglés (en)</li>
 *   <li>Chino (zh)</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public enum Idioma {
    /** Idioma español con código ISO "es" */
    ESPANOL("Español", "es"),
    /** Idioma inglés con código ISO "en" */
    INGLES("Inglés", "en"),
    /** Idioma chino con código ISO "zh" */
    CHINO("Chino", "zh");
    
    /** Nombre completo del idioma */
    private final String nombre;
    /** Código ISO del idioma */
    private final String codigo;
    
    /**
     * Constructor del enum para inicializar nombre y código del idioma.
     *
     * @param nombre Nombre completo del idioma
     * @param codigo Código ISO de dos letras del idioma
     */
    Idioma(String nombre, String codigo) {
        this.nombre = nombre;
        this.codigo = codigo;
    }
    
    /**
     * Obtiene el nombre completo del idioma.
     *
     * @return Nombre del idioma con formato apropiado
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Obtiene el código ISO de dos letras del idioma.
     *
     * @return Código ISO del idioma
     */
    public String getCodigo() {
        return codigo;
    }
    
    /**
     * Busca un idioma por su código ISO, ignorando mayúsculas y minúsculas.
     *
     * @param codigo Código ISO del idioma a buscar
     * @return El enum Idioma correspondiente
     * @throws IllegalArgumentException Si el código no corresponde a ningún idioma válido
     */
    public static Idioma porCodigo(String codigo) {
        for (Idioma idioma : values()) {
            if (idioma.codigo.equalsIgnoreCase(codigo)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Código de idioma no válido: " + codigo);
    }
    
    /**
     * Busca un idioma por su nombre, ignorando mayúsculas y minúsculas.
     *
     * @param nombre Nombre del idioma a buscar
     * @return El enum Idioma correspondiente
     * @throws IllegalArgumentException Si el nombre no corresponde a ningún idioma válido
     */
    public static Idioma porNombre(String nombre) {
        for (Idioma idioma : values()) {
            if (idioma.nombre.equalsIgnoreCase(nombre)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Nombre de idioma no válido: " + nombre);
    }
    
    /**
     * Retorna el nombre del idioma como representación textual.
     *
     * @return Nombre del idioma
     */
    @Override
    public String toString() {
        return nombre;
    }
}
