package com.cinemax.peliculas.modelos.entidades;

import java.util.ArrayList;
import java.util.List;

public class Pelicula {
    private int id;
    private String titulo;
    private String sinopsis;
    private int duracionMinutos;
    private int anio;
    private Idioma idioma;
    private List<Genero> generos;
    private String imagenUrl;
    
    // Constructor vacío
    public Pelicula() {
        this.generos = new ArrayList<>();
    }
    
    // Constructor con parámetros
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

    // Constructor sin ID, para crear una nueva película en persistencia
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
    
    public List<Genero> getGeneros() {
        return new ArrayList<>(generos);
    }
    
    public String getImagenUrl() {
        return imagenUrl;
    }
    
    public String getUrlImagen() {
        return imagenUrl;
    }

    public String getGenero() {
        return getGenerosComoString();
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
    
    public void setGeneros(List<Genero> generos) {
        if (generos != null) {
            this.generos = new ArrayList<>(generos);
        } else {
            this.generos = new ArrayList<>();
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
    
    // Método para agregar un género a la lista
    public void agregarGenero(Genero genero) {
        if (genero != null && !this.generos.contains(genero)) {
            this.generos.add(genero);
        }
    }
    
    // Método para remover un género de la lista
    public void removerGenero(Genero genero) {
        this.generos.remove(genero);
    }
    
    // Método para obtener los géneros como String separados por comas
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
    
    // Método para establecer géneros desde un String separado por comas
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
