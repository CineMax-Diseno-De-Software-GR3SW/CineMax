package com.cinemax.salas.modelos.entidades;

public class SalaNormalFactory extends SalaFactory {
    @Override
    public Sala crearSala(int id, String nombre, int capacidad, EstadoSala estado) {
        return new Sala(id, nombre, capacidad, TipoSala.NORMAL, estado);
    }
}