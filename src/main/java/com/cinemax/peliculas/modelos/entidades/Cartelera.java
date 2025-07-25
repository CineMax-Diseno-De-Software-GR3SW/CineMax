package com.cinemax.peliculas.modelos.entidades;

import java.util.List;

public class Cartelera {
    private List<Pelicula> peliculas;

    public Cartelera(List<Pelicula> peliculas) {
        this.peliculas = peliculas;
    }

    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    public void setPeliculas(List<Pelicula> peliculas) {
        this.peliculas = peliculas;
    }
}