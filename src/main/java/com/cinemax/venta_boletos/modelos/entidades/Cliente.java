package com.cinemax.venta_boletos.Modelos.entidades;

/**
 * Entidad que representa un cliente en el sistema de venta de boletos de CineMax.
 * 
 * Esta clase modela la información básica de un cliente necesaria para:
 * - Identificación personal (nombre, apellido, documento)
 * - Validación de documentos (cédula, pasaporte, RUC)
 * - Comunicación (correo electrónico)
 * - Generación de facturas y boletos
 * 
 * Atributos principales:
 * - Identificación: ID del cliente y tipo de documento
 * - Datos personales: Nombre y apellido
 * - Contacto: Correo electrónico
 * 
 * Se integra con el sistema de validación de documentos mediante
 * las estrategias de validación (cédula, pasaporte, RUC).
 * 
 * @author CineMax Team
 * @version 1.0
 */
public class Cliente {
    /** Número de identificación del cliente (cédula, pasaporte, RUC, etc.) */
    private String idCliente;
    
    /** Tipo de documento de identificación (CEDULA, PASAPORTE, RUC) */
    private String tipoDocumento;
    
    /** Nombre(s) del cliente */
    private String nombre;
    
    /** Apellido(s) del cliente */
    private String apellido;
    
    /** Dirección de correo electrónico para comunicación y facturación */
    private String correoElectronico;

    /**
     * Constructor que inicializa un cliente con toda su información básica.
     * 
     * Crea una instancia de cliente con los datos necesarios para
     * identificación, contacto y facturación en el sistema.
     * 
     * @param nombre Nombre(s) del cliente
     * @param apellido Apellido(s) del cliente
     * @param idCliente Número de documento de identificación
     * @param correoElectronico Correo electrónico de contacto
     * @param tipoDocumento Tipo de documento (CEDULA, PASAPORTE, RUC)
     */
    public Cliente(String nombre, String apellido, String idCliente, String correoElectronico, String tipoDocumento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.idCliente = idCliente;
        this.correoElectronico = correoElectronico;
        this.tipoDocumento = tipoDocumento;
    }

    /**
     * Representación en cadena del cliente con toda su información.
     * 
     * Proporciona un formato legible de todos los datos del cliente
     * para debugging, logging y visualización en consola.
     * 
     * @return String con formato detallado del cliente
     */
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

    /**
     * Obtiene el nombre del cliente.
     * 
     * @return El nombre(s) del cliente
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del cliente.
     * 
     * @param nombre El nuevo nombre(s) del cliente
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el apellido del cliente.
     * 
     * @return El apellido(s) del cliente
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Establece el apellido del cliente.
     * 
     * @param apellido El nuevo apellido(s) del cliente
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene el ID de identificación del cliente.
     * 
     * @return El número de documento del cliente
     */
    public String getIdCliente() {
        return idCliente;
    }

    /**
     * Obtiene el tipo de documento del cliente.
     * 
     * @return El tipo de documento (CEDULA, PASAPORTE, RUC)
     */
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * Establece el ID de identificación del cliente.
     * 
     * @param idCliente El nuevo número de documento
     */
    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    /**
     * Obtiene el correo electrónico del cliente.
     * 
     * @return La dirección de correo electrónico
     */
    public String getCorreoElectronico() {
        return correoElectronico;
    }

    /**
     * Establece el correo electrónico del cliente.
     * 
     * @param correoElectronico La nueva dirección de correo electrónico
     */
    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    /**
     * Establece el tipo de documento del cliente.
     * 
     * @param tipoDocumento El nuevo tipo de documento (CEDULA, PASAPORTE, RUC)
     */
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
}
