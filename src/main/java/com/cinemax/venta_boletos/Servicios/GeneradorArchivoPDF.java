package com.cinemax.venta_boletos.Servicios;

import com.cinemax.venta_boletos.Modelos.Boleto;
import com.cinemax.venta_boletos.Modelos.Cliente;
import com.cinemax.venta_boletos.Modelos.Factura;
import com.cinemax.venta_boletos.Modelos.Producto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GeneradorArchivoPDF implements ServicioGeneradorArchivo {

    private static final float MARGIN = 50;
    private static final PDType1Font FONT_BOLD = PDType1Font.HELVETICA_BOLD;
    private static final PDType1Font FONT_NORMAL = PDType1Font.HELVETICA;
    private static final String CARPETA_BASE = "venta_boletos";

    private static final String CARPETA_FACTURAS = CARPETA_BASE + File.separator + "FacturasGeneradas";
    private static final String CARPETA_BOLETOS = CARPETA_BASE + File.separator + "BoletosGenerados";

    @Override
    public void generarFacturaPDF(Factura factura) {
        crearCarpetaSiNoExiste(CARPETA_FACTURAS);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float y = page.getMediaBox().getHeight() - MARGIN;

                // Título
                contentStream.setFont(FONT_BOLD, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("CINEMAX - FACTURA");
                contentStream.endText();

                y -= 40;
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

                // Datos cliente
                Cliente cliente = factura.getCliente();
                y -= 40;
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("Cliente: " + cliente.getNombre() + " " + cliente.getApellido());
                contentStream.endText();

                y -= 20;
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("Cédula: " + cliente.getIdCliente());
                contentStream.endText();

                // Boletos detalle
                y -= 40;
                contentStream.setFont(FONT_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("BOLETOS:");
                contentStream.endText();

                y -= 20;
                contentStream.setFont(FONT_NORMAL, 12);
                for (Producto producto : factura.getProductos()) {
                    if (producto instanceof Boleto) {
                        Boleto boleto = (Boleto) producto;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN + 10, y);
                        contentStream.showText("• " + boleto.getFuncion() + " - Butaca " + boleto.getButaca() +
                                ": $" + boleto.getPrecio());
                        contentStream.endText();
                        y -= 15;
                    }
                }

                y -= 20;
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
                    PDPage page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                        float y = page.getMediaBox().getHeight() - MARGIN;

                        contentStream.setFont(FONT_BOLD, 18);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        contentStream.showText("CINEMAX - BOLETO");
                        contentStream.endText();

                        y -= 40;
                        contentStream.setFont(FONT_NORMAL, 14);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        contentStream.showText("Función: " + boleto.getFuncion());
                        contentStream.endText();

                        y -= 20;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        contentStream.showText("Butaca: " + boleto.getButaca());
                        contentStream.endText();

                        y -= 20;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        contentStream.showText("Precio: $" + boleto.getPrecio());
                        contentStream.endText();

                        y -= 40;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        contentStream.showText("[CÓDIGO QR]");
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
            boolean creada = dir.mkdirs();
            if (creada) {
                System.out.println("Carpeta creada: " + carpeta);
            }
        }
    }
}
