package com.cinemax.venta_boletos.Modelos;

public class Cliente {
    private String nombre;
    private String apellido;
    private long idCliente;
    private String correoElectronico;

    public Cliente(String nombre, String apellido, long idCliente, String correoElectronico) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.idCliente = idCliente;
        this.correoElectronico = correoElectronico;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", idCliente='" + idCliente + '\'' +
                ", correoElectronico='" + correoElectronico + '\'' +
                '}';
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(long idCliente) {
        this.idCliente = idCliente;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }
}
