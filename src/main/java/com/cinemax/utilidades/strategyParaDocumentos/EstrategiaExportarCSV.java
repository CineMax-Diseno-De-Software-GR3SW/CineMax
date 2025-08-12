package com.cinemax.utilidades.strategyParaDocumentos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.cinemax.reportes.modelos.entidades.ReporteGenerado;

/**
 * Implementación de la interfaz Exportable para generar reportes en formato CSV.
 * Esta clase sigue el patrón Strategy para permitir la exportación intercambiable
 * de datos a formato CSV (Comma Separated Values).
 * 
 * Proporciona dos métodos principales de exportación:
 * 1. Exportación de ReporteGenerado con datos predefinidos
 * 2. Exportación de listas de datos dinámicos en formato principal
 */
public class EstrategiaExportarCSV implements Exportable {
    
    /**
     * Exporta un ReporteGenerado específico a formato CSV con datos predefinidos.
     * Este método genera un archivo CSV con estructura fija que incluye:
     * - Encabezados de columnas
     * - Datos de ejemplo basados en la fecha del reporte
     * - Fila de totales
     * - Observaciones adicionales
     * 
     * @param reporte El objeto ReporteGenerado que contiene metadatos del reporte
     * @param archivo El archivo destino donde se guardará el CSV
     * @param datos Mapa con datos adicionales (no utilizado en esta implementación)
     */
    @Override
    public void exportar(ReporteGenerado reporte, File archivo, Map<String, Object> datos) throws Exception{
        // Usar BufferedWriter con try-with-resources para manejo eficiente de recursos
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            
            // ==================== ESCRIBIR ENCABEZADOS ====================
            writer.write("Fecha,Boletos Vendidos,Ingresos");
            writer.newLine();
            
            // ==================== ESCRIBIR DATOS DE EJEMPLO ====================
            // Los datos se generan basándose en la fecha del reporte
            // Día actual del reporte
            writer.write(
                    reporte.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ",125,,750.00");
            writer.newLine();
            
            // Día anterior al reporte
            writer.write(reporte.getFechaGeneracion().minusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + ",98,e,940.00");
            writer.newLine();
            
            // Dos días antes del reporte
            writer.write(reporte.getFechaGeneracion().minusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + ",156,,680.00");
            writer.newLine();
            
            // Tres días antes del reporte
            writer.write(reporte.getFechaGeneracion().minusDays(3).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + ",87,e,610.00");
            writer.newLine();
            
            // ==================== FILA DE TOTALES ====================
            writer.write("TOTAL,466,,980.00");
            writer.newLine();
            
            // ==================== SECCIÓN DE OBSERVACIONES ====================
            writer.newLine(); // Línea en blanco para separar datos de observaciones
            writer.write("Observaciones:");
            writer.newLine();
            writer.write("Este reporte muestra las ventas de boletos de cine en el periodo especificado.");
            writer.newLine();
            writer.write("Los ingresos están calculados en base al precio promedio de .00 por boleto.");
            writer.newLine();
            writer.write("Reporte generado automáticamente por el sistema CineMax.");
            
        } catch (IOException e) {
            // Manejo de errores de escritura del archivo
            System.out.println("Error al exportar el reporte a CSV: " + e.getMessage());
        }
    }
    
    /**
     * Exporta una lista de datos dinámicos a formato CSV con estructura flexible.
     * Este método es más versátil ya que puede manejar cualquier conjunto de datos
     * y genera un CSV con encabezados específicos para datos de ventas.
     * 
     * @param datos Lista de mapas donde cada mapa representa una fila de datos
     * @param destino Archivo donde se guardará el CSV generado
     * @param tituloReporte Título del reporte (no utilizado directamente en CSV)
     * @param infoExtra Información adicional del reporte (no utilizada en esta implementación)
     */
    public void exportarFormatoPrincipal(List<Map<String, Object>> datos, File destino, String tituloReporte,
            Map<String, Object> infoExtra) {
        try {
            // Usar StringBuilder para construcción eficiente del contenido CSV
            StringBuilder csv = new StringBuilder();
            
            // ==================== ESCRIBIR ENCABEZADOS ====================
            csv.append("Fecha,Tipo Sala,Formato,Boletos Vendidos,Ingresos\n");
            
            // ==================== PROCESAR CADA FILA DE DATOS ====================
            for (Map<String, Object> d : datos) {
                // Extraer valores del mapa de datos
                String fecha = d.get("fecha").toString();
                int boletosVendidos = (int) d.get("total_boletos_vendidos");
                double ingreso = (double) d.get("ingreso_total");
                String tipoSala = (String) d.get("tipos_sala");
                String formato = (String) d.get("formatos");
                
                // Aplicar valores por defecto para campos null
                if (tipoSala == null)
                    tipoSala = "Normal";
                if (formato == null)
                    formato = "2D";
                
                // Formatear la fila y agregar al StringBuilder
                // Utiliza formato específico: fecha, tipo de sala, formato, boletos, ingresos
                csv.append(String.format("%s,%s,%s,%d,%.2f\n",
                        fecha, tipoSala, formato, boletosVendidos, ingreso));
            }
            
            // ==================== ESCRIBIR ARCHIVO ====================
            // Usar NIO Files para escritura eficiente del archivo
            java.nio.file.Files.write(destino.toPath(), csv.toString().getBytes());
            
        } catch (IOException e) {
            // Manejo de errores de escritura del archivo
            System.out.println("Error al exportar el reporte a CSV: " + e.getMessage());
        }
    }


}