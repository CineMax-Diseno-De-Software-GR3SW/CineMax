package com.cinemax.salas.modelos.entidades;

/**
 * Clase abstracta que define el patrón Factory para crear instancias de {@link Sala}.
 *
 * Propósito:
 * - Centralizar la lógica de creación de salas.
 * - Permitir que subclases (por ejemplo, {@link SalaVIPFactory} o {@link SalaNormalFactory})
 *   implementen la creación de salas con configuraciones específicas según su tipo.
 *
 * Uso:
 * - Se extiende esta clase y se implementa el método {@code crearSala}.
 * - Desde el controlador, se elige la fábrica adecuada según el tipo de sala requerido.
 */
public abstract class SalaFactory {

    /**
     * Método abstracto para crear una sala.
     *
     * @param id identificador único de la sala (0 si es nueva y se generará en BD).
     * @param nombre nombre descriptivo de la sala.
     * @param capacidad número total de butacas que tendrá la sala.
     * @param estado estado inicial de la sala (DISPONIBLE, MANTENIMIENTO...).
     * @return una nueva instancia de {@link Sala} según la implementación concreta.
     */
    public abstract Sala crearSala(int id, String nombre, int capacidad, EstadoSala estado);
}