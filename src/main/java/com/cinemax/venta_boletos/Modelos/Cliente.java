package com.cinemax.venta_boletos.Modelos;

public class Cliente {
    private String idCliente;
    private String tipoDocumento;
    private String nombre;
    private String apellido;
    private String correoElectronico;

    public Cliente(String nombre, String apellido, String idCliente, String correoElectronico, String tipoDocumento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.idCliente = idCliente;
        this.correoElectronico = correoElectronico;
        this.tipoDocumento = tipoDocumento;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", idCliente='" + idCliente + '\'' +
                ", correoElectronico='" + correoElectronico + '\'' +
                ", tipoDocumento='" + tipoDocumento + '\'' +
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

    public String getIdCliente() {
        return idCliente;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
}
