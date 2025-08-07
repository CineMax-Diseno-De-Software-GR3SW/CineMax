package com.cinemax.peliculas.modelos.entidades;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa una película en el sistema CineMax.
 *
 * <p>Esta clase encapsula toda la información relevante de una película,
 * incluyendo metadatos básicos, información de clasificación y recursos
 * multimedia asociados. Proporciona métodos para gestionar géneros de
 * manera flexible y validar la integridad de los datos.
 *
 * <p>Características principales:
 * <ul>
 *   <li>Gestión automática de géneros mediante enums</li>
 *   <li>Validaciones de integridad en setters</li>
 *   <li>Soporte para múltiples constructores</li>
 *   <li>Conversión bidireccional de géneros (enum ↔ string)</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public class Pelicula {
    /** Identificador único de la película */
    private int id;
    /** Título de la película */
    private String titulo;
    /** Sinopsis o descripción de la película */
    private String sinopsis;
    /** Duración de la película en minutos */
    private int duracionMinutos;
    /** Año de lanzamiento de la película */
    private int anio;
    /** Idioma principal de la película */
    private Idioma idioma;
    /** Lista de géneros asociados a la película */
    private List<Genero> generos;
    /** URL de la imagen promocional de la película */
    private String imagenUrl;
    
    /**
     * Constructor por defecto que inicializa una película vacía.
     * La lista de géneros se inicializa como una lista vacía.
     */
    public Pelicula() {
        this.generos = new ArrayList<>();
    }
    
    /**
     * Constructor completo para crear una película con todos los datos.
     *
     * @param id Identificador único de la película
     * @param titulo Título de la película, no puede ser null ni vacío
     * @param sinopsis Descripción de la película
     * @param duracionMinutos Duración en minutos, debe ser mayor a 0
     * @param anio Año de lanzamiento
     * @param idioma Idioma principal de la película
     * @param generos Lista de géneros, se crea una copia defensiva
     * @param imagenUrl URL de la imagen promocional
     */
    public Pelicula(int id, String titulo, String sinopsis, int duracionMinutos,
                   int anio, Idioma idioma, List<Genero> generos, String imagenUrl) {
        this.id = id;
        this.titulo = titulo;
        this.sinopsis = sinopsis;
        this.duracionMinutos = duracionMinutos;
        this.anio = anio;
        this.idioma = idioma;
        this.generos = generos != null ? new ArrayList<>(generos) : new ArrayList<>();
        this.imagenUrl = imagenUrl;
    }

    /**
     * Constructor para crear una nueva película sin ID.
     * Útil para persistencia cuando el ID será asignado automáticamente.
     *
     * @param titulo Título de la película, no puede ser null ni vacío
     * @param sinopsis Descripción de la película
     * @param duracionMinutos Duración en minutos, debe ser mayor a 0
     * @param anio Año de lanzamiento
     * @param idioma Idioma principal de la película
     * @param generos Lista de géneros, se crea una copia defensiva
     * @param imagenUrl URL de la imagen promocional
     */
    public Pelicula(String titulo, String sinopsis, int duracionMinutos,
                   int anio, Idioma idioma, List<Genero> generos, String imagenUrl) {
        this.titulo = titulo;
        this.sinopsis = sinopsis;
        this.duracionMinutos = duracionMinutos;
        this.anio = anio;
        this.idioma = idioma;
        this.generos = generos != null ? new ArrayList<>(generos) : new ArrayList<>();
        this.imagenUrl = imagenUrl;
    }
    
    // Getters

    /**
     * Obtiene el identificador único de la película.
     *
     * @return ID de la película
     */
    public int getId() {
        return id;
    }
    
    /**
     * Obtiene el título de la película.
     *
     * @return Título de la película
     */
    public String getTitulo() {
        return titulo;
    }
    
    /**
     * Obtiene la sinopsis de la película.
     *
     * @return Descripción de la película
     */
    public String getSinopsis() {
        return sinopsis;
    }
    
    /**
     * Obtiene la duración de la película en minutos.
     *
     * @return Duración en minutos
     */
    public int getDuracionMinutos() {
        return duracionMinutos;
    }
    
    /**
     * Obtiene el año de lanzamiento de la película.
     *
     * @return Año de lanzamiento
     */
    public int getAnio() {
        return anio;
    }
    
    /**
     * Obtiene el idioma principal de la película.
     *
     * @return Enum Idioma correspondiente
     */
    public Idioma getIdioma() {
        return idioma;
    }
    
    /**
     * Obtiene una copia defensiva de la lista de géneros.
     *
     * @return Nueva lista con los géneros de la película
     */
    public List<Genero> getGeneros() {
        return new ArrayList<>(generos);
    }
    
    /**
     * Obtiene la URL de la imagen promocional.
     *
     * @return URL de la imagen
     */
    public String getImagenUrl() {
        return imagenUrl;
    }
    
    /**
     * Alias para getImagenUrl() para compatibilidad.
     *
     * @return URL de la imagen
     * @deprecated Use {@link #getImagenUrl()} instead
     */
    @Deprecated
    public String getUrlImagen() {
        return imagenUrl;
    }

    /**
     * Obtiene los géneros como una cadena para compatibilidad.
     *
     * @return Géneros como string separados por comas
     * @deprecated Use {@link #getGenerosComoString()} instead
     */
    @Deprecated
    public String getGenero() {
        return getGenerosComoString();
    }

    // Setters

    /**
     * Establece el identificador de la película.
     *
     * @param id Nuevo identificador único
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Establece el título de la película con validación.
     *
     * @param titulo Nuevo título, no puede ser null ni vacío
     * @throws IllegalArgumentException Si el título es null o vacío
     */
    public void setTitulo(String titulo) {
        if (titulo != null && !titulo.trim().isEmpty()) {
            this.titulo = titulo;
        } else {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
    }
    
    /**
     * Establece la sinopsis de la película.
     *
     * @param sinopsis Nueva descripción de la película
     */
    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }
    
    /**
     * Establece la duración con validación de rango.
     *
     * @param duracionMinutos Nueva duración en minutos, debe ser mayor a 0
     * @throws IllegalArgumentException Si la duración no es positiva
     */
    public void setDuracionMinutos(int duracionMinutos) {
        if (duracionMinutos > 0) {
            this.duracionMinutos = duracionMinutos;
        } else {
            throw new IllegalArgumentException("La duración debe ser mayor a 0 minutos");
        }
    }
    
    /**
     * Establece el año de lanzamiento con validación de rango.
     *
     * @param anio Nuevo año, debe estar entre 1888 y 2030
     * @throws IllegalArgumentException Si el año está fuera del rango válido
     */
    public void setAnio(int anio) {
        if (anio >= 1888 && anio <= 2030) { // Primera película fue en 1888
            this.anio = anio;
        } else {
            throw new IllegalArgumentException("El año debe estar entre 1888 y 2030");
        }
    }
    
    /**
     * Establece el idioma de la película.
     *
     * @param idioma Nuevo idioma, no puede ser null
     * @throws IllegalArgumentException Si el idioma es null
     */
    public void setIdioma(Idioma idioma) {
        if (idioma != null) {
            this.idioma = idioma;
        } else {
            throw new IllegalArgumentException("El idioma no puede ser null");
        }
    }
    
    /**
     * Establece la lista de géneros creando una copia defensiva.
     *
     * @param generos Nueva lista de géneros, puede ser null (se crea lista vacía)
     */
    public void setGeneros(List<Genero> generos) {
        if (generos != null) {
            this.generos = new ArrayList<>(generos);
        } else {
            this.generos = new ArrayList<>();
        }
    }
    
    /**
     * Establece la URL de la imagen promocional.
     *
     * @param imagenUrl Nueva URL de la imagen
     */
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
    
    /**
     * Establece el idioma usando su código ISO.
     *
     * @param codigoIdioma Código ISO del idioma (ej: "es", "en")
     * @throws IllegalArgumentException Si el código es null, vacío o inválido
     */
    public void setIdiomaPorCodigo(String codigoIdioma) {
        if (codigoIdioma != null && !codigoIdioma.trim().isEmpty()) {
            this.idioma = Idioma.porCodigo(codigoIdioma);
        } else {
            throw new IllegalArgumentException("El código del idioma no puede estar vacío");
        }
    }
    
    /**
     * Agrega un género a la lista si no existe ya.
     *
     * @param genero Género a agregar, no puede ser null
     */
    public void agregarGenero(Genero genero) {
        if (genero != null && !this.generos.contains(genero)) {
            this.generos.add(genero);
        }
    }

    /**
     * Elimina un género de la lista.
     *
     * @param genero Género a eliminar
     */
    public void eliminarGenero(Genero genero) {
        this.generos.remove(genero);
    }
    
    /**
     * Convierte la lista de géneros a una cadena separada por comas.
     *
     * @return String con géneros separados por comas, vacío si no hay géneros
     */
    public String getGenerosComoString() {
        if (generos.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < generos.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(generos.get(i).getNombre());
        }
        return sb.toString();
    }
    
    /**
     * Establece los géneros a partir de una cadena separada por comas.
     *
     * <p>Este método limpia la lista actual y agrega los géneros encontrados
     * en la cadena. Los géneros inválidos son ignorados y se registra un
     * mensaje de error.
     *
     * @param generosString Cadena con géneros separados por comas
     */
    public void setGenerosPorString(String generosString) {
        this.generos.clear();
        if (generosString != null && !generosString.trim().isEmpty()) {
            String[] generosArray = generosString.split(",");
            for (String generoNombre : generosArray) {
                try {
                    Genero genero = Genero.porNombre(generoNombre.trim());
                    this.generos.add(genero);
                } catch (IllegalArgumentException e) {
                    // Ignorar géneros inválidos o lanzar excepción según la lógica de negocio
                    System.err.println("Género inválido: " + generoNombre.trim());
                }
            }
        }
    }

    /**
     * Proporciona una representación textual completa de la película.
     *
     * @return String con todos los datos de la película
     */
    @Override
    public String toString() {
        return "Pelicula{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", sinopsis='" + sinopsis + '\'' +
                ", duracionMinutos=" + duracionMinutos +
                ", anio=" + anio +
                ", idioma=" + (idioma != null ? idioma.getNombre() : "null") +
                ", generos=" + generos +
                ", imagenUrl='" + imagenUrl + '\'' +
                '}';
    }
}
