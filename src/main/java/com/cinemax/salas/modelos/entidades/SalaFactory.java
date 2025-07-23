package com.cinemax.salas.modelos.entidades;

public abstract class SalaFactory {
    public abstract Sala crearSala(int id, String nombre, int capacidad, EstadoSala estado);
}