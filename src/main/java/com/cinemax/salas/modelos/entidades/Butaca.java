package com.cinemax.salas.modelos.entidades;

/**
 * Entidad que representa una butaca en una sala de cine.
 *
 * Atributos principales:
 * - id: identificador único de la butaca
 * - idSala: referencia a la sala a la que pertenece
 * - fila: letra que indica la fila de ubicación
 * - columna: número o identificador de la columna en la fila
 * - estado: estado actual de la butaca (ej. DISPONIBLE, OCUPADA, RESERVADA)
 *
 * Esta clase funciona como un POJO (Plain Old Java Object):
 * solo almacena datos y proporciona getters/setters para manipularlos.
 */
public class Butaca {

    /** Identificador único de la butaca */
    private int id;

    /** ID de la sala a la que pertenece esta butaca */
    private int idSala;

    /** Letra que indica la fila donde se ubica la butaca */
    private String fila;

    /** Número o etiqueta de la columna dentro de la fila */
    private String columna;
    /** Número o etiqueta del estado de sala */

    private EstadoButaca estado;


    // ===== GETTERS Y SETTERS =====

    /** @return el identificador único de la butaca */
    public int getId() { return id; }

    /** @param id identificador único que se asigna a la butaca */
    public void setId(int id) { this.id = id; }

    /** @return el ID de la sala asociada */
    public int getIdSala() { return idSala; }

    /** @param idSala ID de la sala donde se encuentra la butaca */
    public void setIdSala(int idSala) { this.idSala = idSala; }

    /** @return la letra de la fila de la butaca */
    public String getFila() { return fila; }

    /** @param fila letra que indica la fila de la butaca */
    public void setFila(String fila) { this.fila = fila; }

    /** @return el número o etiqueta de columna de la butaca */
    public String getColumna() { return columna; }

    /** @param columna número o etiqueta de la columna de la butaca */
    public void setColumna(String columna) { this.columna = columna; }

    /** @return el estado actual de la butaca */
    public String getEstado() { return String.valueOf(estado); }
    /** @param estado estado que se asigna a la butaca */
    public void setEstado(String estado) { this.estado = EstadoButaca.valueOf(estado); }

}
