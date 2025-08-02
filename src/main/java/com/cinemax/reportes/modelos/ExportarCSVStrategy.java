package com.cinemax.reportes.modelos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ExportarCSVStrategy implements Export {

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
}
