package com.cinemax.peliculas.modelos.entidades;

import java.util.List;

/**
 * Clase que representa la cartelera de un cine, conteniendo la lista de películas disponibles.
 *
 * <p>La cartelera es una entidad que agrupa todas las películas que están siendo
 * exhibidas en el cine en un momento determinado. Proporciona una vista centralizada
 * de la oferta cinematográfica disponible para los clientes.
 *
 * <p>Esta clase sirve como:
 * <ul>
 *   <li>Contenedor de películas disponibles</li>
 *   <li>Punto de acceso para obtener la programación cinematográfica</li>
 *   <li>Estructura para gestionar la oferta de entretenimiento</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public class Cartelera {

    /** Lista de películas disponibles en la cartelera */
    private List<Pelicula> peliculas;

    /**
     * Constructor que inicializa la cartelera con una lista de películas.
     *
     * @param peliculas Lista de películas que conforman la cartelera.
     *                  Puede ser null o vacía, representando una cartelera sin películas.
     */
    public Cartelera(List<Pelicula> peliculas) {
        this.peliculas = peliculas;
    }

    /**
     * Obtiene la lista de películas disponibles en la cartelera.
     *
     * @return Lista de películas, puede ser null si no se ha inicializado
     */
    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    /**
     * Establece la lista de películas para la cartelera.
     *
     * @param peliculas Nueva lista de películas para la cartelera.
     *                  Puede ser null para representar una cartelera vacía.
     */
    public void setPeliculas(List<Pelicula> peliculas) {
        this.peliculas = peliculas;
    }
}