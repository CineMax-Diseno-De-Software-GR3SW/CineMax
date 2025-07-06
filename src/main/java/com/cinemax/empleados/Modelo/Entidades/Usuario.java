package com.cinemax.empleados.Modelo.Entidades;

import java.time.LocalDateTime;

public class Usuario {
    private Long id;
    private String nombreUsuario;
    private String correo;
    private String clave;
    private String nombreCompleto;
    private String cedula;
    private String celular;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimaModificacion;
    private Rol rol;

    // Constructor
    public Usuario() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaUltimaModificacion = LocalDateTime.now();
        this.activo = true;
    }

    public Usuario(String nombreUsuario, String correo, String clave, String nombreCompleto, 
                   String cedula, String celular, Rol rol) {
        this();
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.clave = clave;
        this.nombreCompleto = nombreCompleto;
        this.cedula = cedula;
        this.celular = celular;
        this.rol = rol;
    }

    // Métodos de negocio
    public void actualizarContacto(String nuevoCorreo, String nuevoCelular) {
        this.correo = nuevoCorreo;
        this.celular = nuevoCelular;
        this.fechaUltimaModificacion = LocalDateTime.now();
    }

    public void activar() {
        this.activo = true;
        this.fechaUltimaModificacion = LocalDateTime.now();
    }

    public void desactivar() {
        this.activo = false;
        this.fechaUltimaModificacion = LocalDateTime.now();
    }

    public boolean verificarClave(String ingresada) {
        return this.clave.equals(ingresada);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaUltimaModificacion() {
        return fechaUltimaModificacion;
    }

    public void setFechaUltimaModificacion(LocalDateTime fechaUltimaModificacion) {
        this.fechaUltimaModificacion = fechaUltimaModificacion;
    }

    public Rol getRol() {
        return rol;
    }

    public String getNombreRol() {
        return rol.getNombre();
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getDescripcionRol() {
        return rol.getNombre();
    }

    public  String toString(){
       return "Persona " + nombreCompleto + " cedula='" + cedula + '\'' + ", celular='" + celular + '\'' + ", activo=" + activo + ", fechaCreacion=" + fechaCreacion;
    }
}