package com.cinemax.salas.modelos.entidades;

/**
 * Enumeración que representa los posibles estados de una sala de cine.
 *
 * Valores posibles:
 * - DISPONIBLE: la sala está activa y se puede usar para funciones.
 * - MANTENIMIENTO: la sala está temporalmente fuera de servicio
 *   por reparaciones, limpieza, ajustes técnicos u otras razones.
 *
 * Este tipo se utiliza para indicar si una sala puede o no ser utilizada
 * en la programación de funciones.
 */
public enum EstadoSala {

    /** La sala está activa y puede recibir funciones y público */
    DISPONIBLE,

    /** La sala está en mantenimiento y no se puede usar temporalmente */
    MANTENIMIENTO
}
