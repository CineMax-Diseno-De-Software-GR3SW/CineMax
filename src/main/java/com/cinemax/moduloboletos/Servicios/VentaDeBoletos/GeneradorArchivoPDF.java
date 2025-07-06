package com.cinemax.moduloboletos.Servicios.VentaDeBoletos;

import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GeneradorArchivoPDF implements ServicioGeneradorArchivo {
    @Override
    public void generarFacturaPDF(Factura factura) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Configuración inicial
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 700);

                // Encabezado
                contentStream.showText("CINEMAX - FACTURA");
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("No. Factura: " + factura.getCodigoFactura());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Fecha: " + factura.getFecha());
                contentStream.newLineAtOffset(0, -20);

                // Datos del cliente
                Cliente cliente = factura.getCliente();
                contentStream.showText("Cliente: " + cliente.getNombre() + " " + cliente.getApellido());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Cédula: " + cliente.getCedula());
                contentStream.newLineAtOffset(0, -30);

                // Detalle de boletos
                contentStream.showText("BOLETOS:");
                contentStream.newLineAtOffset(0, -20);

                for (Producto producto : factura.getProductos()) {
                    if (producto instanceof Boleto) {
                        Boleto boleto = (Boleto) producto;
                        contentStream.showText("• " + boleto.getFuncion() + " - Butaca " + boleto.getButaca() +
                                ": $" + boleto.getPrecio());
                        contentStream.newLineAtOffset(0, -15);
                    }
                }

                // Totales
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Subtotal: $" + factura.getSubTotal());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Total (IVA incluido): $" + factura.getTotal());

                contentStream.endText();
            }

            // Guardar el documento
            String fileName = "Factura_" + factura.getCodigoFactura() + ".pdf";
            document.save(fileName);
            System.out.println("Factura generada: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generarBoletosPDF(List<Boleto> boletos) {
        for (Boleto boleto : boletos) {
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 700);

                    // Encabezado
                    contentStream.showText("CINEMAX - BOLETO");
                    contentStream.newLineAtOffset(0, -30);

                    // Detalles del boleto
                    contentStream.showText("Función: " + boleto.getFuncion());
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("Butaca: " + boleto.getButaca());
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("Precio: $" + boleto.getPrecio());

                    // Código QR (simulado)
                    contentStream.newLineAtOffset(0, -30);
                    contentStream.showText("[CÓDIGO QR]");

                    contentStream.endText();
                }

                // Guardar el documento
                String fileName = "Boleto_" + boleto.getButaca() + ".pdf";
                document.save(fileName);
                System.out.println("Boleto generado: " + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}