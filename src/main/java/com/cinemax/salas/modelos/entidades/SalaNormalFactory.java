package com.cinemax.salas.modelos.entidades;

/**
 * Implementación concreta de {@link SalaFactory} para crear salas de tipo NORMAL.
 *
 * Propósito:
 * - Aplicar el patrón Factory Method para encapsular la creación de salas normales.
 * - Evitar que el código cliente (controladores) tenga que preocuparse por
 *   establecer manualmente el tipo de sala.
 *
 * Uso:
 * - Se instancia esta clase cuando el usuario selecciona crear una sala NORMAL.
 * - El método {@code crearSala} devuelve un objeto {@link Sala} ya configurado
 *   con el tipo {@link TipoSala#NORMAL}.
 */
public class SalaNormalFactory extends SalaFactory {

    /**
     * Crea una sala de tipo NORMAL.
     *
     * @param id identificador único de la sala (0 si es nueva y se genera en BD).
     * @param nombre nombre descriptivo de la sala.
     * @param capacidad cantidad total de butacas.
     * @param estado estado inicial de la sala.
     * @return una instancia de {@link Sala} con tipo {@link TipoSala#NORMAL}.
     */
    @Override
    public Sala crearSala(int id, String nombre, int capacidad, EstadoSala estado) {
        return new Sala(id, nombre, capacidad, TipoSala.NORMAL, estado);
    }
}
