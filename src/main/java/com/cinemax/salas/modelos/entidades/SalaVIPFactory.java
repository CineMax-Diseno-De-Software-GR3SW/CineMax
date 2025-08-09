package com.cinemax.salas.modelos.entidades;

/**
 * Implementación concreta de {@link SalaFactory} para crear salas de tipo VIP.
 *
 * Propósito:
 * - Aplicar el patrón Factory Method para encapsular la creación de salas VIP.
 * - Evitar que el código cliente (como los controladores) tenga que asignar
 *   manualmente el tipo de sala al crearla.
 *
 * Uso:
 * - Se utiliza cuando el usuario selecciona crear una sala de tipo VIP.
 * - El método {@code crearSala} devuelve una instancia de {@link Sala} ya configurada
 *   con el tipo {@link TipoSala#VIP}.
 */
public class SalaVIPFactory extends SalaFactory {

    /**
     * Crea una sala de tipo VIP.
     *
     * @param id identificador único de la sala (0 si es nueva y se generará en BD).
     * @param nombre nombre descriptivo de la sala.
     * @param capacidad cantidad total de butacas.
     * @param estado estado inicial de la sala.
     * @return una instancia de {@link Sala} con tipo {@link TipoSala#VIP}.
     */
    @Override
    public Sala crearSala(int id, String nombre, int capacidad, EstadoSala estado) {
        return new Sala(id, nombre, capacidad, TipoSala.VIP, estado);
    }
}
