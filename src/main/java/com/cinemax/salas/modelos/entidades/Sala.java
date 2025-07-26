package com.cinemax.salas.modelos.entidades;

public class Sala {
    private int id;
    private String nombre;
    private int capacidad;
    private TipoSala tipo;
    private EstadoSala estado;

    public Sala(int id, String nombre, int capacidad, TipoSala tipo, EstadoSala estado) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.tipo = tipo;
        this.estado = estado;
    }

    public Sala() {
    }

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


    @Override
    public String toString() {
        return nombre;
    }
}
