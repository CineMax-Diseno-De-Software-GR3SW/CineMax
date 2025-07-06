package com.cinemax.empleados.Modelo.Entidades;

import java.util.Set;
import java.util.HashSet;

public class Rol {
    private Long id;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private Set<Permiso> permisos;

    // Constructor
    public Rol() {
        this.permisos = new HashSet<>();
        this.activo = true;
    }

    public Rol(String nombre, String descripcion) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Métodos de negocio
    public boolean tienePermiso(Permiso permiso) {
        return permisos.contains(permiso);
    }

    public void agregarPermiso(Permiso permiso) {
        this.permisos.add(permiso);
    }

    public void removerPermiso(Permiso permiso) {
        this.permisos.remove(permiso);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean estaActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Set<Permiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(Set<Permiso> permisos) {
        this.permisos = permisos;
    }

    public String toString(){
        return "Rol [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", activo=" + activo + ", permisos=" + permisos + "]";
    }
    
} 