package com.cinemax.salas.modelos.entidades;

import com.cinemax.salas.modelos.Butaca;

public class Sala {
    private int id;
    private int numeroFilas;
    private int numeroColumnas;
    private TipoSala tipoSala;
    private EstadoSala estadoSala;
    private Butaca[][] mapaDeButacas;

    private String nombre;
    private int capacidad;

    public Sala(int id, String nombre, int capacidad, TipoSala tipo, EstadoSala estado) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.tipoSala = tipo;
        this.estadoSala = estado;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }


    public Butaca[][] getMapaDeButacas() { return mapaDeButacas; }
    public void setMapaDeButacas(Butaca[][] mapaDeButacas) { this.mapaDeButacas = mapaDeButacas; }
    public int getNumeroFilas() { return numeroFilas; }
    public void setNumeroFilas(int numeroFilas) { this.numeroFilas = numeroFilas; }
    public int getNumeroColumnas() { return numeroColumnas; }
    public void setNumeroColumnas(int numeroColumnas) { this.numeroColumnas = numeroColumnas; }
    public TipoSala getTipoSala() { return tipoSala; }
    public void setTipoSala(TipoSala tipoSala) { this.tipoSala = tipoSala; }

    public EstadoSala getEstadoSala() { return estadoSala; }
    public void setEstadoSala(EstadoSala estadoSala) { this.estadoSala = estadoSala; }

    public EstadoSala getEstado() {
        return estadoSala;
    }
    public EstadoSala setEstado(EstadoSala estado) {
        this.estadoSala = estado;
        return this.estadoSala;
    }
}