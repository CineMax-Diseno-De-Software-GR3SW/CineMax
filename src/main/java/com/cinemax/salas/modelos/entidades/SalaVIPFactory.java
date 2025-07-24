package com.cinemax.salas.modelos.entidades;

public class SalaVIPFactory extends SalaFactory {
    @Override
    public Sala crearSala(int id, String nombre, int capacidad, EstadoSala estado) {
        return new Sala(id, nombre, capacidad, TipoSala.VIP, estado);
    }
}