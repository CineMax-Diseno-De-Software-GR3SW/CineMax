package com.cinemax.salas.modelos.entidades;

/**
 * Enumeración que define los posibles estados de una butaca en una sala de cine.
 *
 * Valores posibles:
 * - DISPONIBLE: la butaca está libre y se puede reservar o vender.
 * - OCUPADA: la butaca ya está ocupada (venta o reserva confirmada).
 * - INHABILITADA: la butaca está fuera de servicio (mantenimiento, avería, etc.).
 *
 * Este tipo se utiliza para asignar y controlar el estado actual de cada butaca.
 */
public enum EstadoButaca {
    /** La butaca está libre para ser usada */
    DISPONIBLE,

    /** La butaca ya está ocupada por un cliente */
    OCUPADA,

    /** La butaca está fuera de servicio o no disponible */
    INHABILITADA
}
