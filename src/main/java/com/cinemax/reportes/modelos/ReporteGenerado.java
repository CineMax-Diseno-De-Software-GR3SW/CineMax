package com.cinemax.reportes.modelos;


import java.time.LocalDateTime;


public class ReporteGenerado {
    private String nombreReporte;
    private LocalDateTime fechaGeneracion; // Esta propiedad causa el problema
    private String estado;
    private String frecuencia;
    private String rutaArchivo;
    
    public ReporteGenerado(String nombreReporte, LocalDateTime fechaGeneracion, String estado, String rutaArchivo, String frecuencia) {
        this.nombreReporte = nombreReporte;
        this.fechaGeneracion = fechaGeneracion;
        this.estado = estado;
        this.rutaArchivo = rutaArchivo;
        this.frecuencia = frecuencia;
    }
    
    // Constructor vacío
    public ReporteGenerado() {
    }
    
    // Getters y Setters
    public String getNombreReporte() { 
        return nombreReporte; 
    }
    
    public void setNombreReporte(String nombreReporte) { 
        this.nombreReporte = nombreReporte; 
    }
    
    public LocalDateTime getFechaGeneracion() { 
        return fechaGeneracion; 
    }
    
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { 
        this.fechaGeneracion = fechaGeneracion; 
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getRutaArchivo() {
        return rutaArchivo;
    }
    
    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getFrecuencia() {
        return frecuencia;
    }
}
