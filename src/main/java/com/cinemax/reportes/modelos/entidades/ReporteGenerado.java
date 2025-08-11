package com.cinemax.reportes.modelos.entidades;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Clase que representa un reporte generado en el sistema.
 * Contiene toda la información necesaria sobre un reporte:
 * identificación, metadatos, configuración y estado de ejecución.
 */
public class ReporteGenerado {
    // Identificador único del reporte
    private int id;
    
    // Nombre descriptivo del reporte
    private String nombre;
    
    // Tipo de reporte (ventas, estadísticas, etc.)
    private String tipo;
    
    // Estado actual del reporte (pendiente, ejecutado, error, etc.)
    private String estado;
    
    // Fecha y hora cuando se generó o se debe generar el reporte
    private LocalDateTime fechaGeneracion;
    
    // Ruta del sistema donde se almacena el archivo del reporte
    private String rutaArchivo;
    
    // Frecuencia de generación (diario, semanal, mensual, etc.)
    private String frecuencia;
    
    // Descripción detallada del propósito del reporte
    private String descripcion;
    
    // Configuraciones adicionales del reporte en formato clave-valor
    private Map<String, Object> configuracion;

    /**
     * Constructor completo para reportes con ID y metadatos básicos.
     * Utilizado principalmente para reportes ya existentes en el sistema.
     * 
     * @param id Identificador único del reporte
     * @param nombre Nombre descriptivo del reporte
     * @param tipo Categoría o tipo del reporte
     * @param fechaGeneracion Fecha cuando fue o será generado
     * @param rutaArchivo Ubicación del archivo en el sistema
     * @param descripcion Descripción detallada del reporte
     */
    public ReporteGenerado(int id, String nombre, String tipo, LocalDateTime fechaGeneracion, String rutaArchivo,
            String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.fechaGeneracion = fechaGeneracion;
        this.rutaArchivo = rutaArchivo;
        this.descripcion = descripcion;
    }

    /**
     * Constructor alternativo para reportes programados.
     * Utilizado para crear reportes que se ejecutarán de manera recurrente.
     * 
     * @param nombre Nombre descriptivo del reporte
     * @param estado Estado inicial del reporte
     * @param fechaGeneracion Fecha programada para la generación
     * @param rutaArchivo Ubicación donde se guardará el archivo
     * @param frecuencia Periodicidad de ejecución del reporte
     */
    public ReporteGenerado(String nombre, String estado, LocalDateTime fechaGeneracion, String rutaArchivo,
            String frecuencia) {
        this.nombre = nombre;
        this.estado = estado;
        this.fechaGeneracion = fechaGeneracion;
        this.rutaArchivo = rutaArchivo;
        this.frecuencia = frecuencia;
    }

    // ==================== GETTERS Y SETTERS ====================
    
    /**
     * Obtiene el identificador único del reporte.
     * @return ID del reporte
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el identificador único del reporte.
     * @param id Nuevo ID del reporte
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre descriptivo del reporte.
     * @return Nombre del reporte
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre descriptivo del reporte.
     * @param nombre Nuevo nombre del reporte
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el tipo o categoría del reporte.
     * @return Tipo del reporte
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo o categoría del reporte.
     * @param tipo Nuevo tipo del reporte
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene la fecha y hora de generación del reporte.
     * @return Fecha de generación
     */
    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    /**
     * Establece la fecha y hora de generación del reporte.
     * @param fechaGeneracion Nueva fecha de generación
     */
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    /**
     * Obtiene la ruta donde se almacena el archivo del reporte.
     * @return Ruta del archivo
     */
    public String getRutaArchivo() {
        return rutaArchivo;
    }

    /**
     * Establece la ruta donde se almacena el archivo del reporte.
     * @param rutaArchivo Nueva ruta del archivo
     */
    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    /**
     * Obtiene la frecuencia de generación del reporte.
     * @return Frecuencia (diario, semanal, mensual, etc.)
     */
    public String getFrecuencia() {
        return frecuencia;
    }

    /**
     * Establece la frecuencia de generación del reporte.
     * @param frecuencia Nueva frecuencia de generación
     */
    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    /**
     * Obtiene el estado actual del reporte.
     * @return Estado del reporte (pendiente, ejecutado, error, etc.)
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado actual del reporte.
     * @param estado Nuevo estado del reporte
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene la descripción detallada del reporte.
     * @return Descripción del reporte
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Obtiene el mapa de configuraciones adicionales del reporte.
     * @return Configuraciones en formato clave-valor
     */
    public  Map<String, Object> getConfiguracion() {
        return configuracion;
    }

    /**
     * Establece las configuraciones adicionales del reporte.
     * @param configuracion Mapa con las nuevas configuraciones
     */
    public void setConfiguracion(Map<String, Object> configuracion) {
        this.configuracion = configuracion;
    }

}