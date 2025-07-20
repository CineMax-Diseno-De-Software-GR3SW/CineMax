package com.cinemax.salas.modelos.entidades;

public class Butaca {
    private int id;
    private int idSala;
    private String fila;
    private String columna;
    private String estado;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdSala() { return idSala; }
    public void setIdSala(int idSala) { this.idSala = idSala; }

    public String getFila() { return fila; }
    public void setFila(String fila) { this.fila = fila; }

    public String getColumna() { return columna; }
    public void setColumna(String columna) { this.columna = columna; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

}
