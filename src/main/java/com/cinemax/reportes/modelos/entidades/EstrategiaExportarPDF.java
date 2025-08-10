package com.cinemax.reportes.modelos.entidades;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class EstrategiaExportarPDF implements Exportable {

    // para generar el PDF
    @Override
    public void exportar(ReporteGenerado reporte, File archivo, Map<String, Object> datos) {
        try (PDDocument document = crearDocumentoPDF()) {

            try (PDPageContentStream contentStream = new PDPageContentStream(document, document.getPage(0))) {
                // Configurar fuente
                PDType1Font fontBold = PDType1Font.HELVETICA_BOLD;
                PDType1Font fontNormal = PDType1Font.HELVETICA;

                float margin = 50;
                float yPosition = document.getPage(0).getMediaBox().getHeight() - margin;
                float fontSize = 12;

                // Título principal
                contentStream.setFont(fontBold, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("REPORTE DE VENTAS - CINEMAX");
                contentStream.endText();

                yPosition -= 40;

                // Información del reporte
                contentStream.setFont(fontNormal, fontSize);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Nombre del Reporte: " + reporte.getNombre());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Fecha de Generacion: "
                        + reporte.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Estado: " + reporte.getEstado());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Generado el: "
                        + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                contentStream.endText();

                yPosition -= 100;

                // Subtítulo de la sección
                contentStream.setFont(fontBold, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("RESUMEN DE VENTAS");
                contentStream.endText();

                yPosition -= 30;

                // Título de sección: Ventas Generales
                contentStream.setFont(fontBold, 13);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Ventas Generales");
                contentStream.endText();

                yPosition -= 22; // Espacio entre título y tabla

                // Tabla de totales
                float tableWidth = 500;
                float rowHeight = 30;
                float colWidth = tableWidth / 6f;
                float tableX = margin;
                float tableY = yPosition;

                // Dibujar bordes de la tabla
                contentStream.setLineWidth(1);

                // Rectángulo exterior
                contentStream.addRect(tableX, tableY - rowHeight * 2, tableWidth, rowHeight * 2);
                contentStream.stroke();

                // Líneas verticales
                for (int i = 1; i < 6; i++) {
                    float x = tableX + i * colWidth;
                    contentStream.moveTo(x, tableY);
                    contentStream.lineTo(x, tableY - rowHeight * 2);
                    contentStream.stroke();
                }

                // Línea horizontal para separar encabezados y datos
                contentStream.moveTo(tableX, tableY - rowHeight);
                contentStream.lineTo(tableX + tableWidth, tableY - rowHeight);
                contentStream.stroke();

                // Encabezados centrados
                String[] headers = { "Total Boletos", "Total Facturas", "Total Ingresos", "Total Funciones",
                        "Fecha Inicio", "Fecha Fin" };
                contentStream.setFont(fontBold, 10);
                for (int i = 0; i < headers.length; i++) {
                    float textWidth = fontBold.getStringWidth(headers[i]) / 1000 * 10;
                    float cellCenter = tableX + i * colWidth + colWidth / 2;
                    float textX = cellCenter - textWidth / 2;
                    float textY = tableY - 10;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(textX, textY);
                    contentStream.showText(headers[i]);
                    contentStream.endText();
                }

                // Datos centrados (ajusta estos valores según tus datos reales)
                Object[] data = {
                        datos.get("total_boletos_vendidos"), // Total Boletos
                        datos.get("total_facturas"), // Total Facturas
                        datos.get("ingreso_total"), // Total Ingresos
                        datos.get("total_funciones"), // Total Funciones
                        datos.get("fecha_inicio"), // Fecha Inicio
                        datos.get("fecha_fin") // Fecha Fin
                };
                contentStream.setFont(fontNormal, 10);
                for (int i = 0; i < data.length; i++) {
                    float textWidth = fontNormal.getStringWidth(String.valueOf(data[i])) / 1000 * 10;
                    float cellCenter = tableX + i * colWidth + colWidth / 2;
                    float textX = cellCenter - textWidth / 2;
                    float textY = tableY - rowHeight - 10;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(textX, textY);
                    contentStream.showText(String.valueOf(data[i]));
                    contentStream.endText();
                }

                yPosition = tableY - rowHeight * 2 - 40;

                // Título de sección: Ventas por Película
                contentStream.setFont(fontBold, 13);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Ventas por Película");
                contentStream.endText();

                yPosition -= 22; // Espacio entre título y tabla

                // Configuración de la tabla
                float tableWidthPeliculas = 500;
                float rowHeightPeliculas = 25;
                float colWidthPeliculas = tableWidthPeliculas / 4f;
                float tableXPeliculas = margin;
                float tableYPeliculas = yPosition;

                // Datos ficticios
                String[][] peliculas = {
                        { "Barbie", "3", "320", ",600.00" },
                        { "Oppenheimer", "2", "210", ",300.00" },
                        { "Intensamente 2", "2", "180", ",400.00" },
                        { "Garfield", "1", "80", "e,400.00" }
                };
                int numFilasPeliculas = peliculas.length + 1; // +1 para encabezado

                // Dibujar bordes de la tabla
                contentStream.setLineWidth(1);
                contentStream.addRect(tableXPeliculas, tableYPeliculas - rowHeightPeliculas * numFilasPeliculas,
                        tableWidthPeliculas, rowHeightPeliculas * numFilasPeliculas);
                contentStream.stroke();

                // Líneas verticales
                for (int i = 1; i < 4; i++) {
                    float x = tableXPeliculas + i * colWidthPeliculas;
                    contentStream.moveTo(x, tableYPeliculas);
                    contentStream.lineTo(x, tableYPeliculas - rowHeightPeliculas * numFilasPeliculas);
                    contentStream.stroke();
                }

                // Líneas horizontales (opcional, para cada fila)
                for (int i = 1; i < numFilasPeliculas; i++) {
                    float y = tableYPeliculas - i * rowHeightPeliculas;
                    contentStream.moveTo(tableXPeliculas, y);
                    contentStream.lineTo(tableXPeliculas + tableWidthPeliculas, y);
                    contentStream.stroke();
                }

                // Encabezados centrados
                String[] headersPeliculas = { "Título", "Funciones", "Boletos Vendidos", "Ingresos" };
                contentStream.setFont(fontBold, 10);
                for (int i = 0; i < headersPeliculas.length; i++) {
                    float textWidth = fontBold.getStringWidth(headersPeliculas[i]) / 1000 * 10;
                    float cellCenter = tableXPeliculas + i * colWidthPeliculas + colWidthPeliculas / 2;
                    float textX = cellCenter - textWidth / 2;
                    float textY = tableYPeliculas - 15;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(textX, textY);
                    contentStream.showText(headersPeliculas[i]);
                    contentStream.endText();
                }

                // Filas de datos centradas
                contentStream.setFont(fontNormal, 10);
                for (int fila = 0; fila < peliculas.length; fila++) {
                    for (int col = 0; col < peliculas[fila].length; col++) {
                        float textWidth = fontNormal.getStringWidth(peliculas[fila][col]) / 1000 * 10;
                        float cellCenter = tableXPeliculas + col * colWidthPeliculas + colWidthPeliculas / 2;
                        float textX = cellCenter - textWidth / 2;
                        float textY = tableYPeliculas - rowHeightPeliculas * (fila + 1) - 15;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(textX, textY);
                        contentStream.showText(peliculas[fila][col]);
                        contentStream.endText();
                    }
                }

                yPosition = tableYPeliculas - rowHeightPeliculas * numFilasPeliculas - 40;

                // Observaciones
                contentStream.setFont(fontBold, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Observaciones:");
                contentStream.endText();

                yPosition -= 20;
                contentStream.setFont(fontNormal, 10);
                String[] observaciones = {
                        "• Este reporte muestra las ventas de boletos de cine en el periodo especificado.",
                        "• Los ingresos están calculados en base al precio promedio de .00 por boleto.",
                        "• Reporte generado automáticamente por el sistema CineMax."
                };
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                for (String obs : observaciones) {
                    contentStream.showText(obs);
                    contentStream.newLineAtOffset(0, -15);
                }
                contentStream.endText();

                // Pie de página
                contentStream.setFont(fontNormal, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, 50);
                contentStream.showText("© 2025 CineMax - Sistema de Gestion de Reportes");
                contentStream.endText();
            }

            // Guardar el documento
            guardarPDF(document, archivo);
            System.out.println("PDF generado exitosamente con PDFBox: " + archivo.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error al generar el PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void exportarFormatoPrincipal(List<Map<String, Object>> datos, File destino, String tituloReporte,
            Map<String, Object> infoExtra) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                PDType1Font fontBold = PDType1Font.HELVETICA_BOLD;
                PDType1Font fontNormal = PDType1Font.HELVETICA;
                float margin = 50;
                float yPosition = page.getMediaBox().getHeight() - margin;
                float fontSize = 12;

                // Título principal
                contentStream.setFont(fontBold, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(tituloReporte);
                contentStream.endText();
                yPosition -= 40;

                // Información extra
                if (infoExtra != null && infoExtra.containsKey("subtitulo")) {
                    contentStream.setFont(fontNormal, fontSize);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText((String) infoExtra.get("subtitulo"));
                    contentStream.endText();
                    yPosition -= 20;
                }

                // Subtítulo de la sección
                contentStream.setFont(fontBold, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("RESUMEN DE VENTAS");
                contentStream.endText();
                yPosition -= 30;

                // Tabla
                float tableWidth = 400;
                float rowHeight = 25;
                float col1X = margin;
                float col2X = margin + 100;
                float col3X = margin + 200;
                float col4X = margin + 300;

                // Headers
                contentStream.setFont(fontBold, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(col1X + 5, yPosition - 15);
                contentStream.showText("Fecha");
                contentStream.newLineAtOffset(col2X - col1X - 5, 0);
                contentStream.showText("Tipo");
                contentStream.newLineAtOffset(col3X - col2X, 0);
                contentStream.showText("Boletos");
                contentStream.newLineAtOffset(col4X - col3X, 0);
                contentStream.showText("Ingresos");
                contentStream.endText();

                // Datos
                contentStream.setFont(fontNormal, 10);
                int totalBoletos = 0;
                double totalIngresos = 0;

                for (int i = 0; i < datos.size(); i++) {
                    Map<String, Object> d = datos.get(i);

                    // Obtener valores del mapa
                    String fecha = d.get("fecha").toString();
                    int boletosVendidos = (int) d.get("total_boletos_vendidos");
                    double ingreso = (double) d.get("ingreso_total");
                    String tipoSala = (String) d.get("tipos_sala");
                    String formato = (String) d.get("formatos");

                    // Combinar tipo de sala y formato para mostrar como "tipo"
                    String tipoCompleto = (tipoSala != null ? tipoSala : "Normal") + " " +
                            (formato != null ? formato : "2D");

                    contentStream.beginText();
                    contentStream.newLineAtOffset(col1X + 5, yPosition - 40 - (i * rowHeight));
                    contentStream.showText(fecha);
                    contentStream.newLineAtOffset(col2X - col1X - 5, 0);
                    contentStream.showText(tipoCompleto);
                    contentStream.newLineAtOffset(col3X - col2X, 0);
                    contentStream.showText(String.valueOf(boletosVendidos));
                    contentStream.newLineAtOffset(col4X - col3X, 0);
                    contentStream.showText(String.format("$%.2f", ingreso));
                    contentStream.endText();

                    totalBoletos += boletosVendidos;
                    totalIngresos += ingreso;
                }

                // Fila de total
                contentStream.setFont(fontBold, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(col1X + 5, yPosition - 40 - (datos.size() * rowHeight));
                contentStream.showText("TOTAL:");
                contentStream.newLineAtOffset(col2X - col1X - 5, 0);
                contentStream.showText("");
                contentStream.newLineAtOffset(col3X - col2X, 0);
                contentStream.showText(String.valueOf(totalBoletos));
                contentStream.newLineAtOffset(col4X - col3X, 0);
                contentStream.showText(String.format("$%.2f", totalIngresos));
                contentStream.endText();

                // Pie de página
                contentStream.setFont(fontNormal, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, 50);
                contentStream.showText("© 2025 CineMax - Sistema de Gestion de Reportes");
                contentStream.endText();
            }
            document.save(destino);
        } catch (IOException e) {
            System.out.println("Error al exportar el reporte a PDF: " + e.getMessage());
        }
    }

    /**
     * Crea un nuevo documento PDF básico con una página A4
     * @return PDDocument nuevo documento PDF inicializado
     * @throws Exception si ocurre un error al crear el documento
     */
    public static PDDocument crearDocumentoPDF() throws Exception {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        return document;
    }
    
    /**
     * Guarda un documento PDF en la ruta especificada
     * @param document El documento PDF a guardar
     * @param archivo El archivo donde guardar el PDF
     * @throws Exception si ocurre un error al guardar el documento
     */
    public static void guardarPDF(PDDocument document, File archivo) throws Exception {
        document.save(archivo);
        System.out.println("PDF generado exitosamente: " + archivo.getAbsolutePath());
    }
    
}
