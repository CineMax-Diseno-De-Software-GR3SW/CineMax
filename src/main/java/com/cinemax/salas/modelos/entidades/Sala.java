package com.cinemax.salas.modelos.entidades;

/**
 * Entidad que representa una sala de cine.
 *
 * Atributos:
 * - id: identificador único de la sala.
 * - nombre: nombre descriptivo de la sala (ej. "Sala 1", "VIP Azul").
 * - capacidad: cantidad total de butacas disponibles.
 * - tipo: tipo de sala (NORMAL, VIP, etc.).
 * - estado: estado actual de la sala (DISPONIBLE, MANTENIMIENTO).
 *
 * Constructores:
 * - Sala(int, String, int, TipoSala, EstadoSala): inicializa todos los atributos.
 * - Sala(): constructor vacío para instanciación sin valores iniciales.
 *
 * Métodos:
 * - Getters y Setters para acceder y modificar los atributos.
 * - toString(): devuelve el nombre de la sala (útil para mostrar en listas o combos).
 */
public class Sala {

    /** Identificador único de la sala */
    private int id;

    /** Nombre de la sala  */
    private String nombre;

    /** Capacidad total de butacas en la sala */
    private int capacidad;

    /** Tipo de sala (NORMAL, VIP, etc.) */
    private TipoSala tipo;

    /** Estado actual de la sala (DISPONIBLE, MANTENIMIENTO) */
    private EstadoSala estado;

    /**
     * Constructor que inicializa todos los atributos de la sala.
     * @param id identificador único
     * @param nombre nombre de la sala
     * @param capacidad número total de butacas
     * @param tipo tipo de sala (NORMAL, VIP...)
     * @param estado estado actual de la sala
     */
    public Sala(int id, String nombre, int capacidad, TipoSala tipo, EstadoSala estado) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.tipo = tipo;
        this.estado = estado;
    }

    /** Constructor vacío para permitir creación sin valores iniciales */
    public Sala() { }

    // ===== GETTERS Y SETTERS =====

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public TipoSala getTipo() { return tipo; }
    public void setTipo(TipoSala tipo) { this.tipo = tipo; }

    public EstadoSala getEstado() { return estado; }
    public void setEstado(EstadoSala estado) { this.estado = estado; }

    /**
     * Representación textual de la sala.
     * @return el nombre de la sala
     */
    @Override
    public String toString() {
        return nombre;
    }
}
