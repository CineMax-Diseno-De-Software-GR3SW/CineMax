package com.cinemax.venta_boletos.servicios;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.cinemax.venta_boletos.modelos.entidades.Boleto;
import com.cinemax.venta_boletos.modelos.entidades.Cliente;
import com.cinemax.venta_boletos.modelos.entidades.Factura;
import com.cinemax.venta_boletos.modelos.entidades.Producto;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ServicioGeneradorArchivoPDF implements ServicioGeneradorArchivo {

    private static final float MARGIN = 50;
    private static final PDType1Font FONT_BOLD = PDType1Font.HELVETICA_BOLD;
    private static final PDType1Font FONT_NORMAL = PDType1Font.HELVETICA;
    private static final String CARPETA_BASE = "PDFsGenerados_BoletoFactura";

    private static final String CARPETA_FACTURAS = CARPETA_BASE + File.separator + "FacturasGeneradas";
    private static final String CARPETA_BOLETOS = CARPETA_BASE + File.separator + "BoletosGenerados";

    // Información del cine
    private static final String[] CINE_INFO = {
            "CineMax - Donde vive el cine",
            "Dirección: Av. Principal 123, Ciudad",
            "Tel: (555) 123-456"
    };

    @Override
    public void generarFacturaPDF(Factura factura) {
        crearCarpetaSiNoExiste(CARPETA_FACTURAS);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float pageWidth = page.getMediaBox().getWidth();
                float y = page.getMediaBox().getHeight() - MARGIN;

                // Título principal centrado
                String titulo = "CINEMAX - FACTURA";
                contentStream.setFont(FONT_BOLD, 20);
                float titleWidth = FONT_BOLD.getStringWidth(titulo) / 1000 * 20;
                contentStream.beginText();
                contentStream.newLineAtOffset((pageWidth - titleWidth) / 2, y);
                contentStream.showText(titulo);
                contentStream.endText();

                // Línea separadora
                y -= 30;
                contentStream.setStrokingColor(100, 100, 100); // gris tenue
                contentStream.moveTo(MARGIN, y);
                contentStream.lineTo(pageWidth - MARGIN, y);
                contentStream.stroke();

                // Información CineMax centrada
                y -= 20;
                contentStream.setFont(FONT_NORMAL, 10);
                for (String info : CINE_INFO) {
                    float infoWidth = FONT_NORMAL.getStringWidth(info) / 1000 * 10;
                    contentStream.beginText();
                    contentStream.newLineAtOffset((pageWidth - infoWidth) / 2, y);
                    contentStream.showText(info);
                    contentStream.endText();
                    y -= 14;
                }

                // Otra línea separadora
                y -= 10;
                contentStream.moveTo(MARGIN, y);
                contentStream.lineTo(pageWidth - MARGIN, y);
                contentStream.stroke();

                // Datos Factura (alineados a la izquierda con margen)
                y -= 30;
                contentStream.setFont(FONT_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("No. Factura: " + factura.getCodigoFactura());
                contentStream.endText();

                y -= 20;
                contentStream.setFont(FONT_NORMAL, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("Fecha: " + factura.getFecha());
                contentStream.endText();

                // Datos Cliente
                Cliente cliente = factura.getCliente();
                y -= 40;
                contentStream.setFont(FONT_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("DATOS DEL CLIENTE:");
                contentStream.endText();

                y -= 20;
                contentStream.setFont(FONT_NORMAL, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText(
                        cliente.getNombre() + " " + cliente.getApellido() + " | Cédula: " + cliente.getIdCliente());
                contentStream.endText();

                // Detalle de Boletos
                y -= 40;
                contentStream.setFont(FONT_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("DETALLE DE BOLETOS:");
                contentStream.endText();

                y -= 20;
                contentStream.setFont(FONT_NORMAL, 12);
                for (Producto producto : factura.getProductos()) {
                    if (producto instanceof Boleto) {
                        Boleto boleto = (Boleto) producto;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN + 10, y);
                        contentStream.showText("• " + boleto.getFuncion() + " - Butaca " + boleto.getButaca() + ": $"
                                + boleto.getPrecio());
                        contentStream.endText();
                        y -= 15;
                    }
                }

                // Línea separadora antes de totales
                y -= 15;
                contentStream.moveTo(MARGIN, y);
                contentStream.lineTo(pageWidth - MARGIN, y);
                contentStream.stroke();

                // Totales
                y -= 25;
                contentStream.setFont(FONT_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("Subtotal: $" + factura.getSubTotal());
                contentStream.endText();

                y -= 15;
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("Total (IVA incluido): $" + factura.getTotal());
                contentStream.endText();
            }

            String fileName = CARPETA_FACTURAS + File.separator + "Factura_" + factura.getCodigoFactura() + ".pdf";
            document.save(fileName);
            System.out.println("Factura generada: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generarBoletosPDF(List<Producto> boletos) {
        crearCarpetaSiNoExiste(CARPETA_BOLETOS);

        for (Producto producto : boletos) {
            if (producto instanceof Boleto) {
                Boleto boleto = (Boleto) producto;

                try (PDDocument document = new PDDocument()) {
                    PDPage page = new PDPage(new PDRectangle(298, 420));
                    document.addPage(page);

                    try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                        float pageWidth = page.getMediaBox().getWidth();
                        float y = page.getMediaBox().getHeight() - MARGIN;

                        // Encabezado centrado
                        String titulo = "CINEMAX - BOLETO";
                        contentStream.setFont(FONT_BOLD, 18);
                        float titleWidth = FONT_BOLD.getStringWidth(titulo) / 1000 * 18;
                        contentStream.beginText();
                        contentStream.newLineAtOffset((pageWidth - titleWidth) / 2, y);
                        contentStream.showText(titulo);
                        contentStream.endText();

                        // Línea separadora
                        y -= 25;
                        contentStream.setStrokingColor(100, 100, 100);
                        contentStream.moveTo(MARGIN, y);
                        contentStream.lineTo(pageWidth - MARGIN, y);
                        contentStream.stroke();

                        // Info cine centrada
                        y -= 20;
                        contentStream.setFont(FONT_NORMAL, 9);
                        for (String info : CINE_INFO) {
                            float infoWidth = FONT_NORMAL.getStringWidth(info) / 1000 * 9;
                            contentStream.beginText();
                            contentStream.newLineAtOffset((pageWidth - infoWidth) / 2, y);
                            contentStream.showText(info);
                            contentStream.endText();
                            y -= 12;
                        }

                        // Línea separadora
                        y -= 10;
                        contentStream.moveTo(MARGIN, y);
                        contentStream.lineTo(pageWidth - MARGIN, y);
                        contentStream.stroke();

                        // Datos boleto alineados a la izquierda
                        y -= 25;
                        contentStream.setFont(FONT_BOLD, 8);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        contentStream.showText("Función: " + boleto.getFuncion());
                        contentStream.endText();

                        y -= 20;
                        contentStream.setFont(FONT_NORMAL, 8);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        contentStream.showText("Butaca: " + boleto.getButaca());
                        contentStream.endText();

                        y -= 20;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        contentStream.showText("Precio: $" + boleto.getPrecio());
                        contentStream.endText();
                    }

                    String fileName = CARPETA_BOLETOS + File.separator + "Boleto_" + boleto.getButaca() + ".pdf";
                    document.save(fileName);
                    System.out.println("Boleto generado: " + fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void crearCarpetaSiNoExiste(String carpeta) {
        File dir = new File(carpeta);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Carpeta creada: " + carpeta);
            }
        }
    }
}
