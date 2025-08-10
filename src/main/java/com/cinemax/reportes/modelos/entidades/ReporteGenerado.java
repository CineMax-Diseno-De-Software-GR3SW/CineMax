package com.cinemax.reportes.modelos.entidades;

import java.time.LocalDateTime;
import java.util.Map;

public class ReporteGenerado {
    private int id;
    private String nombre;
    private String tipo;
    private String estado;
    private LocalDateTime fechaGeneracion;
    private String rutaArchivo;
    private String frecuencia;
    private String descripcion;
    private Map<String, Object> configuracion;

    public ReporteGenerado(int id, String nombre, String tipo, LocalDateTime fechaGeneracion, String rutaArchivo,
            String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.fechaGeneracion = fechaGeneracion;
        this.rutaArchivo = rutaArchivo;
        this.descripcion = descripcion;
    }

    public ReporteGenerado(String nombre, String estado, LocalDateTime fechaGeneracion, String rutaArchivo,
            String frecuencia) {
        this.nombre = nombre;
        this.estado = estado;
        this.fechaGeneracion = fechaGeneracion;
        this.rutaArchivo = rutaArchivo;
        this.frecuencia = frecuencia;
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

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public  Map<String, Object> getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(Map<String, Object> configuracion) {
        this.configuracion = configuracion;
    }

}
