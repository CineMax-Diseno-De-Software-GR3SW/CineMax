package com.cinemax.venta_boletos.servicios;

import java.util.List;

import com.cinemax.venta_boletos.modelos.entidades.Factura;
import com.cinemax.venta_boletos.modelos.entidades.Producto;

/**
 * Interfaz para el servicio de generación de archivos de venta.
 * 
 * Define las operaciones para:
 * - Generación de facturas en formato PDF
 * - Generación de boletos/entradas en formato PDF
 * 
 * Patrón de diseño: Strategy (permite múltiples implementaciones de generación)
 * 
 * @author [Tu nombre o equipo]
 * @version 1.0
 */
public interface ServicioGeneradorArchivo {

    /**
     * Genera un archivo PDF con los datos de la factura.
     * 
     * @param factura La factura a serializar en PDF. No debe ser nula.
     * @throws GeneracionArchivoException Si ocurre un error durante la generación
     */
    void generarFactura(Factura factura);

    /**
     * Genera archivos PDF para una lista de boletos/entradas.
     * 
     * @param boletos Lista de productos (boletos) a generar. No debe ser nula ni
     *                vacía.
     * @throws GeneracionArchivoException Si ocurre un error durante la generación
     */
    void generarBoletos(List<Producto> boletos);
}