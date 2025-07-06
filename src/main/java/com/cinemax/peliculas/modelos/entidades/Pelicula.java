package com.cinemax.peliculas.modelos.entidades;

public class Pelicula {
    private int id;
    private String titulo;
    private String sinopsis;
    private int duracionMinutos;
    private int anio;
    private Idioma idioma;
    private String genero;
    private String imagenUrl;
    
    // Constructor vacío
    public Pelicula() {
    }
    
    // Constructor con parámetros
    public Pelicula(int id, String titulo, String sinopsis, int duracionMinutos, 
                   int anio, Idioma idioma, String genero,  String imagenUrl) {
        this.id = id;
        this.titulo = titulo;
        this.sinopsis = sinopsis;
        this.duracionMinutos = duracionMinutos;
        this.anio = anio;
        this.idioma = idioma;
        this.genero = genero;
        this.imagenUrl = imagenUrl;
    }

    // Constructor sin ID, para crear una nueva película en persistencia
    public Pelicula(String titulo, String sinopsis, int duracionMinutos, 
                   int anio, Idioma idioma, String genero,  String imagenUrl) {
        this.titulo = titulo;
        this.sinopsis = sinopsis;
        this.duracionMinutos = duracionMinutos;
        this.anio = anio;
        this.idioma = idioma;
        this.genero = genero;
        this.imagenUrl = imagenUrl;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public String getSinopsis() {
        return sinopsis;
    }
    
    public int getDuracionMinutos() {
        return duracionMinutos;
    }
    
    public int getAnio() {
        return anio;
    }
    
    public Idioma getIdioma() {
        return idioma;
    }
    
    public String getGenero() {
        return genero;
    }
    
    public String getImagenUrl() {
        return imagenUrl;
    }
    
    
    // Setters
    public void setId(int id) {
        this.id = id;
    }
    
    public void setTitulo(String titulo) {
        if (titulo != null && !titulo.trim().isEmpty()) {
            this.titulo = titulo;
        } else {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
    }
    
    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }
    
    public void setDuracionMinutos(int duracionMinutos) {
        if (duracionMinutos > 0) {
            this.duracionMinutos = duracionMinutos;
        } else {
            throw new IllegalArgumentException("La duración debe ser mayor a 0 minutos");
        }
    }
    
    public void setAnio(int anio) {
        if (anio >= 1888 && anio <= 2030) { // Primera película fue en 1888
            this.anio = anio;
        } else {
            throw new IllegalArgumentException("El año debe estar entre 1888 y 2030");
        }
    }
    
    public void setIdioma(Idioma idioma) {
        if (idioma != null) {
            this.idioma = idioma;
        } else {
            throw new IllegalArgumentException("El idioma no puede ser null");
        }
    }
    
    public void setGenero(String genero) {
        if (genero != null && !genero.trim().isEmpty()) {
            this.genero = genero.trim();
        } else {
            throw new IllegalArgumentException("El género no puede estar vacío");
        }
    }
    
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
    
    
    // Método de conveniencia para establecer idioma por código (para DAO)
    public void setIdiomaPorCodigo(String codigoIdioma) {
        if (codigoIdioma != null && !codigoIdioma.trim().isEmpty()) {
            this.idioma = Idioma.porCodigo(codigoIdioma);
        } else {
            throw new IllegalArgumentException("El código del idioma no puede estar vacío");
        }
    }


    

    @Override
    public String toString() {
        return "Pelicula{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", sinopsis='" + sinopsis + '\'' +
                ", duracionMinutos=" + duracionMinutos +
                ", anio=" + anio +
                ", idioma=" + (idioma != null ? idioma.getNombre() : "null") +
                ", genero='" + genero + '\'' +
                ", imagenUrl='" + imagenUrl + '\'' +
                '}';
    }
}
