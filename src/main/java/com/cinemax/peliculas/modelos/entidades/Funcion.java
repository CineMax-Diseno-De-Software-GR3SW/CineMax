package com.cinemax.peliculas.modelos.entidades;

import java.time.LocalDateTime;
import com.cinemax.salas.modelos.entidades.Sala;

public class Funcion {
    private int id;
    private Pelicula pelicula;
    private Sala sala;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private FormatoFuncion formato;
    private TipoEstreno tipoEstreno;

    public Funcion(int id, Pelicula pelicula, Sala sala, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, FormatoFuncion formato, TipoEstreno tipoEstreno) {
        this.id = id;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.formato = formato;
        this.tipoEstreno = tipoEstreno;
    }

    public int getId() { return id; }
    public Pelicula getPelicula() { return pelicula; }
    public Sala getSala() { return sala; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public FormatoFuncion getFormato() { return formato; }
    public TipoEstreno getTipoEstreno() { return tipoEstreno; }

    public void setId(int id) { this.id = id; }
    public void setPelicula(Pelicula pelicula) { this.pelicula = pelicula; }
    public void setSala(Sala sala) { this.sala = sala; }
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }
    public void setFormato(FormatoFuncion formato) { this.formato = formato; }
    public void setTipoEstreno(TipoEstreno tipoEstreno) { this.tipoEstreno = tipoEstreno; }
}