package com.cinemax.reportes.modelos;

import java.time.LocalDateTime;

public class ReporteGenerado {
    private int id;
    private String nombre;
    private String tipo;
    private LocalDateTime fechaGeneracion;
    private String rutaArchivo;
    private String descripcion;
    
    public ReporteGenerado(int id, String nombre, String tipo, LocalDateTime fechaGeneracion, String rutaArchivo, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.fechaGeneracion = fechaGeneracion;
        this.rutaArchivo = rutaArchivo;
        this.descripcion = descripcion;
    }
    
    // Constructor vacío
    public ReporteGenerado() {
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() { 
        return nombre; 
    }
    
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public LocalDateTime getFechaGeneracion() { 
        return fechaGeneracion; 
    }
    
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { 
        this.fechaGeneracion = fechaGeneracion; 
    }
    
    public String getRutaArchivo() {
        return rutaArchivo;
    }
    
    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
