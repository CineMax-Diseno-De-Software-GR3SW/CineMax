package com.cinemax.salas.modelos;

public class Butaca {
    private int id;
    private String fila;
    private EstadoButaca estado;
    private String columna;

    public Butaca(int id, String fila, EstadoButaca estado, String columna) {
        this.id = id;
        this.fila = fila;
        this.estado = estado;
        this.columna = columna;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFila() { return fila; }
    public void setFila(String fila) { this.fila = fila; }

    public EstadoButaca getEstado() { return estado; }
    public void setEstado(EstadoButaca estado) { this.estado = estado; }

    public String getColumna() { return columna; }
    public void setColumna(String columna) { this.columna = columna; }
}