package com.cinemax.utilidades.EstrategiaParaDocumentos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.cinemax.reportes.modelos.entidades.ReporteGenerado;

public class EstrategiaExportarCSV implements Exportable {

    @Override
    public void exportar(ReporteGenerado reporte, File archivo, Map<String, Object> datos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            // Encabezados
            writer.write("Fecha,Boletos Vendidos,Ingresos");
            writer.newLine();

            // Datos (igual que en el PDF)
            writer.write(
                    reporte.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ",125,,750.00");
            writer.newLine();
            writer.write(reporte.getFechaGeneracion().minusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + ",98,e,940.00");
            writer.newLine();
            writer.write(reporte.getFechaGeneracion().minusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + ",156,,680.00");
            writer.newLine();
            writer.write(reporte.getFechaGeneracion().minusDays(3).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + ",87,e,610.00");
            writer.newLine();

            // Total
            writer.write("TOTAL,466,,980.00");
            writer.newLine();

            // Observaciones
            writer.newLine();
            writer.write("Observaciones:");
            writer.newLine();
            writer.write("Este reporte muestra las ventas de boletos de cine en el periodo especificado.");
            writer.newLine();
            writer.write("Los ingresos están calculados en base al precio promedio de .00 por boleto.");
            writer.newLine();
            writer.write("Reporte generado automáticamente por el sistema CineMax.");
        } catch (IOException e) {
            System.out.println("Error al exportar el reporte a CSV: " + e.getMessage());
        }
    }

    public void exportarFormatoPrincipal(List<Map<String, Object>> datos, File destino, String tituloReporte,
            Map<String, Object> infoExtra) {
        try {
            StringBuilder csv = new StringBuilder();
            csv.append("Fecha,Tipo Sala,Formato,Boletos Vendidos,Ingresos\n");

            for (Map<String, Object> d : datos) {
                String fecha = d.get("fecha").toString();
                int boletosVendidos = (int) d.get("total_boletos_vendidos");
                double ingreso = (double) d.get("ingreso_total");
                String tipoSala = (String) d.get("tipos_sala");
                String formato = (String) d.get("formatos");

                // Usar valores por defecto si son null
                if (tipoSala == null)
                    tipoSala = "Normal";
                if (formato == null)
                    formato = "2D";

                csv.append(String.format("%s,%s,%s,%d,%.2f\n",
                        fecha, tipoSala, formato, boletosVendidos, ingreso));
            }

            java.nio.file.Files.write(destino.toPath(), csv.toString().getBytes());
        } catch (IOException e) {
            System.out.println("Error al exportar el reporte a CSV: " + e.getMessage());
        }
    }
}
