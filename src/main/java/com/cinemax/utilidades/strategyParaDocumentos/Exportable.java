package com.cinemax.utilidades.strategyParaDocumentos;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.cinemax.reportes.modelos.entidades.ReporteGenerado;

/**
 * Interfaz que define el contrato para estrategias de exportación de reportes.
 * Implementa el patrón Strategy para permitir diferentes formatos de exportación
 * (PDF, CSV, Excel, etc.) de manera intercambiable.
 */
public interface Exportable {
    
    /**
     * Exporta un reporte generado a un archivo específico con datos asociados.
     * 
     * @param reporte El objeto ReporteGenerado que contiene la información del reporte
     * @param archivo El archivo de destino donde se guardará la exportación
     * @param datos Mapa con datos adicionales necesarios para la exportación
     * @throws Exception Si ocurre algún error durante el proceso de exportación
     */
    void exportar(ReporteGenerado reporte, File archivo, Map<String, Object> datos) throws Exception;

    /**
     * Exporta datos en formato principal con información adicional.
     * Este método permite exportar listas de datos estructurados con título
     * y metadatos adicionales.
     * 
     * @param datos Lista de mapas que representan los datos a exportar
     * @param destino Archivo de destino para guardar la exportación
     * @param tituloReporte Título que aparecerá en el reporte exportado
     * @param infoExtra Mapa con información adicional como subtítulos, fechas, etc.
     * @throws Exception Si ocurre algún error durante el proceso de exportación
     */
    void exportarFormatoPrincipal(List<Map<String, Object>> datos, File destino, String tituloReporte,
            Map<String, Object> infoExtra) throws Exception;
}