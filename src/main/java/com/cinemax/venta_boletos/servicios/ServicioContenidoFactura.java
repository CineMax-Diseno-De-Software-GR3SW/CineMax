package com.cinemax.venta_boletos.servicios;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.cinemax.utilidades.estrategiaParaDocumentos.EstrategiaExportarPDF;
import com.cinemax.venta_boletos.modelos.entidades.Boleto;
import com.cinemax.venta_boletos.modelos.entidades.Cliente;
import com.cinemax.venta_boletos.modelos.entidades.Factura;
import com.cinemax.venta_boletos.modelos.entidades.Producto;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

/**
 * Implementación concreta del servicio para generar archivos PDF de facturas y
 * boletos
 * Utiliza la biblioteca Apache PDFBox para la creación de documentos PDF
 */
public class ServicioContenidoFactura {
    /**
     * Une la factura y los boletos en un solo PDF usando PDFBox.
     * @param facturaFile Archivo PDF de la factura
     * @param boletosFiles Lista de archivos PDF de los boletos
     * @param archivoSalida Archivo de salida combinado
     * @throws IOException Si ocurre un error de IO
     */
    public void unirPDFsFacturaYBoletos(File facturaFile, List<File> boletosFiles, File archivoSalida) throws IOException {
        List<File> archivos = new java.util.ArrayList<>();
        archivos.add(facturaFile);
        if (boletosFiles != null) archivos.addAll(boletosFiles);

        PDFMergerUtility merger = new PDFMergerUtility();
        for (File f : archivos) {
            merger.addSource(f);
        }
        merger.setDestinationFileName(archivoSalida.getAbsolutePath());
        merger.mergeDocuments(null);
    }

    // Margen utilizado en los documentos PDF (en puntos)
    private static final float MARGIN = 50;

    // Fuentes tipográficas utilizadas
    private static final PDType1Font FONT_BOLD = PDType1Font.HELVETICA_BOLD;
    private static final PDType1Font FONT_NORMAL = PDType1Font.HELVETICA;

    // Estructura de directorios para almacenamiento
    private static final String CARPETA_BASE = "PDFsGenerados_BoletoFactura";
    private static final String CARPETA_FACTURAS = CARPETA_BASE + File.separator + "FacturasGeneradas";
    private static final String CARPETA_BOLETOS = CARPETA_BASE + File.separator + "BoletosGenerados";

    // Información institucional del cine que aparece en los documentos
    private static final String[] CINE_INFO = {
            "CineMax",
            "Dirección: Av. Principal 123, Ciudad",
            "Tel: (555) 123-456"
    };

    /**
     * Genera un documento PDF para una factura de venta
     * 
     * @param factura La factura con los datos a imprimir en el PDF
     */
    public void generarFactura(Factura factura) {
        // Asegura que exista la carpeta de destino
        crearCarpetaSiNoExiste(CARPETA_FACTURAS);

        try (PDDocument document = EstrategiaExportarPDF.crearDocumentoPDF()) {
            // Asegurarse de que solo haya una página y sea la usada
            while (document.getNumberOfPages() > 1) {
                document.removePage(1);
            }
            PDPage page = document.getPage(0);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Obtiene dimensiones de la página
                float pageWidth = page.getMediaBox().getWidth();
                float y = page.getMediaBox().getHeight() - MARGIN;

                // ===== ENCABEZADO =====
                // Título principal centrado
                String titulo = "CINEMAX - FACTURA";
                contentStream.setFont(FONT_BOLD, 20);
                float titleWidth = FONT_BOLD.getStringWidth(titulo) / 1000 * 20;
                contentStream.beginText();
                contentStream.newLineAtOffset((pageWidth - titleWidth) / 2, y);
                contentStream.showText(titulo);
                contentStream.endText();

                // Línea separadora gris
                y -= 30;
                contentStream.setStrokingColor(100, 100, 100);
                contentStream.moveTo(MARGIN, y);
                contentStream.lineTo(pageWidth - MARGIN, y);
                contentStream.stroke();

                // Información del cine (centrada)
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

                // ===== DATOS DE FACTURA =====
                // Número de factura
                y -= 30;
                contentStream.setFont(FONT_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("No. Factura: " + factura.getCodigoFactura());
                contentStream.endText();

                // Fecha de emisión
                y -= 20;
                contentStream.setFont(FONT_NORMAL, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("Fecha: " + factura.getFecha());
                contentStream.endText();

                // ===== DATOS DEL CLIENTE =====
                Cliente cliente = factura.getCliente();
                y -= 40;
                contentStream.setFont(FONT_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("DATOS DEL CLIENTE:");
                contentStream.endText();

                // Nombre y cédula del cliente
                y -= 20;
                contentStream.setFont(FONT_NORMAL, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText(
                        cliente.getNombre() + " " + cliente.getApellido() + " | Cédula: " + cliente.getIdCliente());
                contentStream.endText();

                // ===== DETALLE DE BOLETOS =====
                y -= 40;
                contentStream.setFont(FONT_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("DETALLE DE BOLETOS:");
                contentStream.endText();

                // Crear tabla de boletos y obtener la nueva posición Y
                y -= 30;
                y = generarTablaDetalleBoletosFactura(contentStream, factura.getProductos(), y, pageWidth);

                // ===== TOTALES =====
                // Línea separadora antes de totales
                y -= 15;
                contentStream.moveTo(MARGIN, y);
                contentStream.lineTo(pageWidth - MARGIN, y);
                contentStream.stroke();

                // Subtotal
                y -= 25;
                contentStream.setFont(FONT_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("Subtotal: $" + String.format("%.2f", factura.getSubTotal()));
                contentStream.endText();

                // Total con IVA
                y -= 15;
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, y);
                contentStream.showText("Total (IVA incluido): $" + String.format("%.2f", factura.getTotal()));
                contentStream.endText();
            }

            // Guarda el documento generado
            String fileName = CARPETA_FACTURAS + File.separator + "Factura_" + factura.getCodigoFactura() + ".pdf";
            EstrategiaExportarPDF.guardarPDF(document, new File(fileName));
            System.out.println("Factura generada: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera documentos PDF individuales para cada boleto en la lista
     * 
     * @param boletos Lista de boletos a imprimir
     */
    public void generarBoletos(List<Producto> boletos) {
        // Asegura que exista la carpeta de destino
        crearCarpetaSiNoExiste(CARPETA_BOLETOS);

        // Procesa cada boleto en la lista
        for (Producto producto : boletos) {
            if (producto instanceof Boleto) {
                Boleto boleto = (Boleto) producto;

                try (PDDocument document = EstrategiaExportarPDF.crearDocumentoPDF()) {
                    // Eliminar la página por defecto (A4) creada por EstrategiaExportarPDF
                    if (document.getNumberOfPages() > 0) {
                        document.removePage(0);
                    }
                    // Crea una página en tamaño ticket (298x420 puntos)
                    PDPage page = new PDPage(new PDRectangle(298, 420));
                    document.addPage(page);

                    try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                        // Obtiene dimensiones de la página
                        float pageWidth = page.getMediaBox().getWidth();
                        float y = page.getMediaBox().getHeight() - MARGIN;

                        // ===== ENCABEZADO =====
                        // Título centrado
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

                        // ===== INFORMACIÓN DEL CINE =====
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

                        // ===== DATOS DEL BOLETO =====
                        // Película
                        y -= 25;
                        contentStream.setFont(FONT_BOLD, 8);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        contentStream.showText("Película: " + formatearInformacionFuncion(boleto));
                        contentStream.endText();

                        // Sala y formato
                        y -= 20;
                        contentStream.setFont(FONT_NORMAL, 8);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        contentStream.showText("Sala: " + formatearInformacionSala(boleto));
                        contentStream.endText();

                        // Formato y tipo de estreno
                        y -= 15;
                        StringBuilder formatoInfo = new StringBuilder();
                        if (boleto.getFuncion() != null && boleto.getFuncion().getFormato() != null) {
                            String formato = boleto.getFuncion().getFormato().name();
                            // Convertir formato a texto amigable
                            switch (formato.toUpperCase()) {
                                case "DOS_D":
                                    formatoInfo.append("Formato: 2D");
                                    break;
                                case "TRES_D":
                                    formatoInfo.append("Formato: 3D");
                                    break;
                                default:
                                    formatoInfo.append("Formato: ").append(formato.replace("_", " "));
                                    break;
                            }
                        }
                        if (boleto.getFuncion() != null && boleto.getFuncion().getTipoEstreno() != null) {
                            if (formatoInfo.length() > 0) {
                                formatoInfo.append(" - ");
                            }
                            String tipoEstreno = boleto.getFuncion().getTipoEstreno().name();
                            // Convertir tipo de estreno a texto amigable
                            switch (tipoEstreno.toUpperCase()) {
                                case "PREESTRENO":
                                    formatoInfo.append("Pre-estreno");
                                    break;
                                case "ESTRENO":
                                    formatoInfo.append("Estreno");
                                    break;
                                default:
                                    formatoInfo.append(tipoEstreno.replace("_", " "));
                                    break;
                            }
                        }
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        contentStream.showText(formatoInfo.length() > 0 ? formatoInfo.toString() : "Formato no disponible");
                        contentStream.endText();

                        // Fecha y hora
                        y -= 15;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        contentStream.showText("Fecha y Hora: " + formatearFechaHora(boleto));
                        contentStream.endText();

                        // Butaca
                        y -= 15;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        String butacaInfo = "Butaca: ";
                        if (boleto.getButaca() != null && boleto.getButaca().getFila() != null && boleto.getButaca().getColumna() != null) {
                            butacaInfo += boleto.getButaca().getFila() + boleto.getButaca().getColumna();
                        } else {
                            butacaInfo += "No especificada";
                        }
                        contentStream.showText(butacaInfo);
                        contentStream.endText();

                        // Precio
                        y -= 20;
                        contentStream.setFont(FONT_BOLD, 8);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, y);
                        contentStream.showText("Precio: $" + String.format("%.2f", boleto.getPrecio()));
                        contentStream.endText();
                    }

                    // Guarda el documento generado con un nombre más descriptivo
                    String nombreArchivo = generarNombreArchivoBoleto(boleto);
                    String fileName = CARPETA_BOLETOS + File.separator + nombreArchivo;
                    EstrategiaExportarPDF.guardarPDF(document, new File(fileName));
                    System.out.println("Boleto generado: " + fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Genera una tabla con el detalle de boletos para la factura.
     *
     * @param contentStream Stream de contenido del PDF
     * @param productos Lista de productos (boletos)
     * @param yInicial Posición Y inicial
     * @param pageWidth Ancho de la página
     * @return Nueva posición Y después de la tabla
     */
    private float generarTablaDetalleBoletosFactura(PDPageContentStream contentStream, List<Producto> productos, float yInicial, float pageWidth) throws IOException {
        float y = yInicial;
        
    // Configurar encabezados de la tabla (separados y con anchos mejorados)
    String[] encabezados = {"Película", "Género", "Formato", "Tipo", "Sala", "Butaca", "Fecha", "Hora", "Precio"};
    // Suma total: 80+60+50+50+70+35+50+35+40 = 470 (A4 útil ~500-520)
    float[] anchosColumna = {80, 60, 50, 50, 70, 35, 50, 35, 40};
        
        // Escribir encabezados
        contentStream.setFont(FONT_BOLD, 9);
        float xInicial = MARGIN;
        float x = xInicial;
        
        for (int i = 0; i < encabezados.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(encabezados[i]);
            contentStream.endText();
            x += anchosColumna[i];
        }
        
        // Línea separadora debajo de encabezados
        y -= 8;
        contentStream.moveTo(MARGIN, y);
        contentStream.lineTo(pageWidth - MARGIN, y);
        contentStream.stroke();
        
        // Escribir datos de los boletos
        y -= 18;
        contentStream.setFont(FONT_NORMAL, 8);
        
        int contadorBoletos = 0;
        for (Producto producto : productos) {
            if (producto instanceof Boleto) {
                Boleto boleto = (Boleto) producto;
                contadorBoletos++;
                
                // Extraer datos del boleto
                String[] datosBoleto = extraerDatosBoletosParaTabla(boleto);
                
                // Verificar si necesitamos múltiples líneas para textos largos
                boolean necesitaSegundaLinea = false;
                String[] segundaLinea = new String[datosBoleto.length];
                
                x = xInicial;
                for (int i = 0; i < datosBoleto.length && i < anchosColumna.length; i++) {
                    String texto = datosBoleto[i];
                    
                    // Para película (índice 0), género (índice 1) y sala (índice 4), manejar textos largos
                    if ((i == 0 || i == 1 || i == 4) && texto.length() > getMaxCharsPorColumna(anchosColumna[i], 8)) {
                        int maxChars = getMaxCharsPorColumna(anchosColumna[i], 8);
                        String textoLinea1 = "";
                        String textoLinea2 = "";
                        if ((i == 1 || i == 4) && texto.contains(",")) { // Género o Sala con múltiples valores
                            int puntoCorte = texto.lastIndexOf(',', maxChars);
                            if (puntoCorte > 0) {
                                textoLinea1 = texto.substring(0, puntoCorte + 1).trim();
                                textoLinea2 = texto.substring(puntoCorte + 1).trim();
                                necesitaSegundaLinea = true;
                            } else {
                                textoLinea1 = texto.substring(0, Math.min(maxChars, texto.length()));
                                if (texto.length() > maxChars) {
                                    textoLinea2 = texto.substring(maxChars);
                                    necesitaSegundaLinea = true;
                                }
                            }
                        } else { // Película, Sala u otros textos largos
                            textoLinea1 = texto.substring(0, Math.min(maxChars, texto.length()));
                            if (texto.length() > maxChars) {
                                textoLinea2 = texto.substring(maxChars);
                                necesitaSegundaLinea = true;
                            }
                        }
                        contentStream.beginText();
                        contentStream.newLineAtOffset(x, y);
                        contentStream.showText(textoLinea1);
                        contentStream.endText();
                        segundaLinea[i] = textoLinea2;
                    } else {
                        // Texto normal que cabe en una línea
                        contentStream.beginText();
                        contentStream.newLineAtOffset(x, y);
                        contentStream.showText(texto);
                        contentStream.endText();
                    }
                    
                    x += anchosColumna[i];
                }
                
                y -= 16;
                
                // Si hay segunda línea, escribirla
                if (necesitaSegundaLinea) {
                    x = xInicial;
                    for (int i = 0; i < segundaLinea.length && i < anchosColumna.length; i++) {
                        if (segundaLinea[i] != null && !segundaLinea[i].isEmpty()) {
                            contentStream.beginText();
                            contentStream.newLineAtOffset(x, y);
                            contentStream.showText(segundaLinea[i]);
                            contentStream.endText();
                        }
                        x += anchosColumna[i];
                    }
                    y -= 16; // Espacio adicional por la segunda línea
                }
                
                // Agregar espacio extra cada 3 boletos para mayor legibilidad
                if (contadorBoletos % 3 == 0) {
                    y -= 5;
                }
            }
        }
        
    // Agregar espacio adicional después de la tabla
    y -= 20;
        
    return y;
    }

    /**
     * Calcula el número máximo de caracteres que caben en una columna.
     *
     * @param anchoColumna Ancho de la columna en puntos
     * @param tamanoFuente Tamaño de la fuente
     * @return Número aproximado de caracteres que caben
     */
    private int getMaxCharsPorColumna(float anchoColumna, int tamanoFuente) {
        try {
            // Estimar usando el ancho promedio de caracteres de Helvetica
            float anchoPromedioPorCaracter = FONT_NORMAL.getStringWidth("A") / 1000 * tamanoFuente;
            return (int) (anchoColumna / anchoPromedioPorCaracter) - 1; // -1 para margen de seguridad
        } catch (Exception e) {
            // Fallback: estimación conservadora
            return (int) (anchoColumna / (tamanoFuente * 0.6));
        }
    }

    /**
     * Extrae los datos de un boleto y los formatea para mostrar en la tabla.
     *
     * @param boleto El boleto a procesar
     * @return Array con los datos formateados para la tabla
     */
    private String[] extraerDatosBoletosParaTabla(Boleto boleto) {
    String[] datos = new String[9];
        
        // 0. Película
        if (boleto.getFuncion() != null && boleto.getFuncion().getPelicula() != null) {
            datos[0] = boleto.getFuncion().getPelicula().getTitulo();
        } else {
            datos[0] = "N/A";
        }
        
        // 1. Género
        if (boleto.getFuncion() != null && boleto.getFuncion().getPelicula() != null) {
            String genero = boleto.getFuncion().getPelicula().getGenerosComoString();
            datos[1] = (genero != null && !genero.isEmpty()) ? genero : "N/A";
        } else {
            datos[1] = "N/A";
        }
        
        // 2. Formato
        if (boleto.getFuncion() != null && boleto.getFuncion().getFormato() != null) {
            String formato = boleto.getFuncion().getFormato().name();
            switch (formato.toUpperCase()) {
                case "DOS_D":
                    datos[2] = "2D";
                    break;
                case "TRES_D":
                    datos[2] = "3D";
                    break;
                default:
                    datos[2] = formato.replace("_", " ");
                    break;
            }
        } else {
            datos[2] = "N/A";
        }
        
        // 3. Tipo
        if (boleto.getFuncion() != null && boleto.getFuncion().getTipoEstreno() != null) {
            String tipo = boleto.getFuncion().getTipoEstreno().name();
            switch (tipo.toUpperCase()) {
                case "ESTRENO":
                    datos[3] = "Estreno";
                    break;
                case "PREESTRENO":
                    datos[3] = "Pre-estreno";
                    break;
                default:
                    datos[3] = tipo.replace("_", " ");
                    break;
            }
        } else {
            datos[3] = "N/A";
        }
        
        // 4. Sala
        if (boleto.getFuncion() != null && boleto.getFuncion().getSala() != null) {
            String nombreSala = boleto.getFuncion().getSala().getNombre();
            String tipoSala = "";
            
            if (boleto.getFuncion().getSala().getTipo() != null) {
                String tipo = boleto.getFuncion().getSala().getTipo().toString();
                switch (tipo.toUpperCase()) {
                    case "NORMAL":
                        tipoSala = "Estándar";
                        break;
                    case "VIP":
                        tipoSala = "VIP";
                        break;
                    case "IMAX":
                        tipoSala = "IMAX";
                        break;
                    case "4DX":
                        tipoSala = "4DX";
                        break;
                    default:
                        tipoSala = tipo;
                        break;
                }
            }
            
            // Evitar duplicar "Sala"
            if (nombreSala.toLowerCase().startsWith("sala")) {
                datos[4] = nombreSala + (tipoSala.isEmpty() ? "" : " " + tipoSala);
            } else {
                datos[4] = "Sala " + nombreSala + (tipoSala.isEmpty() ? "" : " " + tipoSala);
            }
        } else {
            datos[4] = "N/A";
        }
        
        // 5. Butaca
        if (boleto.getButaca() != null && boleto.getButaca().getFila() != null && boleto.getButaca().getColumna() != null) {
            datos[5] = boleto.getButaca().getFila() + boleto.getButaca().getColumna();
        } else {
            datos[5] = "N/A";
        }
        // 6. Fecha
        if (boleto.getFuncion() != null && boleto.getFuncion().getFechaHoraInicio() != null) {
            java.time.format.DateTimeFormatter formatterFecha = 
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            datos[6] = boleto.getFuncion().getFechaHoraInicio().format(formatterFecha);
        } else {
            datos[6] = "N/A";
        }
        // 7. Hora
        if (boleto.getFuncion() != null && boleto.getFuncion().getFechaHoraInicio() != null) {
            java.time.format.DateTimeFormatter formatterHora = 
                java.time.format.DateTimeFormatter.ofPattern("HH:mm");
            datos[7] = boleto.getFuncion().getFechaHoraInicio().format(formatterHora);
        } else {
            datos[7] = "N/A";
        }
        // 8. Precio
        datos[8] = "$" + String.format("%.2f", boleto.getPrecio());
        return datos;
    }

    /**
     * Trunca el texto para que quepa en el ancho de la columna.
     *
     * @param texto El texto a truncar
     * @param anchoColumna Ancho disponible en la columna
     * @param tamanoFuente Tamaño de la fuente
     * @return Texto truncado si es necesario
     */
    private String truncarTextoParaColumna(String texto, float anchoColumna, int tamanoFuente) {
        if (texto == null) return "";
        
        try {
            float anchoTexto = FONT_NORMAL.getStringWidth(texto) / 1000 * tamanoFuente;
            if (anchoTexto <= anchoColumna) {
                return texto;
            }
            
            // Truncar progresivamente hasta que quepa
            int maxChars = (int) (texto.length() * anchoColumna / anchoTexto) - 3;
            if (maxChars > 0 && maxChars < texto.length()) {
                return texto.substring(0, maxChars) + "...";
            }
            
            return texto;
        } catch (Exception e) {
            return texto; // En caso de error, devolver el texto original
        }
    }

    /**
     * Formatea la información del boleto de manera legible para el usuario.
     *
     * @param boleto El boleto a formatear
     * @return String con información detallada y legible del boleto
     */
    private String formatearDetalleBoleto(Boleto boleto) {
        if (boleto == null) {
            return "Boleto no disponible";
        }

        StringBuilder detalle = new StringBuilder();
        
        // 1. Nombre de la película
        if (boleto.getFuncion() != null && boleto.getFuncion().getPelicula() != null) {
            detalle.append(boleto.getFuncion().getPelicula().getTitulo());
        } else {
            detalle.append("Película no disponible");
        }
        
        // 2. Género
        if (boleto.getFuncion() != null && boleto.getFuncion().getPelicula() != null) {
            String genero = boleto.getFuncion().getPelicula().getGenerosComoString();
            if (genero != null && !genero.isEmpty()) {
                detalle.append(" - ").append(genero);
            } else {
                detalle.append(" - Género no disponible");
            }
        } else {
            detalle.append(" - Género no disponible");
        }
        
        // 3. Formato (amigable para el cliente)
        if (boleto.getFuncion() != null && boleto.getFuncion().getFormato() != null) {
            String formato = boleto.getFuncion().getFormato().name();
            // Convertir formato a texto amigable
            switch (formato.toUpperCase()) {
                case "DOS_D":
                    detalle.append(" - 2D");
                    break;
                case "TRES_D":
                    detalle.append(" - 3D");
                    break;
                default:
                    detalle.append(" - ").append(formato.replace("_", " "));
                    break;
            }
        } else {
            detalle.append(" - Formato no disponible");
        }
        
        // 4. Tipo de estreno
        if (boleto.getFuncion() != null && boleto.getFuncion().getTipoEstreno() != null) {
            String tipo = boleto.getFuncion().getTipoEstreno().name();
            // Convertir tipo a texto amigable
            switch (tipo.toUpperCase()) {
                case "ESTRENO":
                    detalle.append(" - Estreno");
                    break;
                case "PREESTRENO":
                    detalle.append(" - Pre-estreno");
                    break;
                default:
                    detalle.append(" - ").append(tipo.replace("_", " "));
                    break;
            }
        } else {
            detalle.append(" - Tipo no disponible");
        }
        
        // 5. Sala (formato amigable para el cliente)
        if (boleto.getFuncion() != null && boleto.getFuncion().getSala() != null) {
            String nombreSala = boleto.getFuncion().getSala().getNombre();
            String tipoSala = "";
            
            if (boleto.getFuncion().getSala().getTipo() != null) {
                String tipo = boleto.getFuncion().getSala().getTipo().toString();
                // Convertir tipos de sala a formato amigable
                switch (tipo.toUpperCase()) {
                    case "NORMAL":
                        tipoSala = "Estándar";
                        break;
                    case "VIP":
                        tipoSala = "VIP";
                        break;
                    case "IMAX":
                        tipoSala = "IMAX";
                        break;
                    case "4DX":
                        tipoSala = "4DX";
                        break;
                    default:
                        tipoSala = tipo;
                        break;
                }
            }
            
            // Mostrar como "Sala [Nombre] [Tipo]" evitando duplicar "Sala"
            detalle.append(" - ");
            if (nombreSala.toLowerCase().startsWith("sala")) {
                // Si el nombre ya incluye "Sala", no duplicar
                detalle.append(nombreSala);
                if (!tipoSala.isEmpty()) {
                    detalle.append(" ").append(tipoSala);
                }
            } else {
                // Si no incluye "Sala", agregar el prefijo
                detalle.append("Sala ").append(nombreSala);
                if (!tipoSala.isEmpty()) {
                    detalle.append(" ").append(tipoSala);
                }
            }
        } else {
            detalle.append(" - Sala no disponible");
        }
        
        // 6. Fecha y 7. Hora (separadas)
        if (boleto.getFuncion() != null && boleto.getFuncion().getFechaHoraInicio() != null) {
            java.time.format.DateTimeFormatter formatterFecha = 
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            java.time.format.DateTimeFormatter formatterHora = 
                java.time.format.DateTimeFormatter.ofPattern("HH:mm");
            detalle.append(" - ").append(boleto.getFuncion().getFechaHoraInicio().format(formatterFecha));
            detalle.append(" - ").append(boleto.getFuncion().getFechaHoraInicio().format(formatterHora));
        } else {
            detalle.append(" - Fecha no disponible - Hora no disponible");
        }
        
        // 8. Precio unitario final
        detalle.append(" - $").append(String.format("%.2f", boleto.getPrecio()));
        
        return detalle.toString();
    }

    /**
     * Formatea la información de la función de manera legible para boletos individuales.
     *
     * @param boleto El boleto con la función a formatear
     * @return String con información detallada de la función
     */
    private String formatearInformacionFuncion(Boleto boleto) {
        if (boleto == null || boleto.getFuncion() == null) {
            return "Información no disponible";
        }

        StringBuilder info = new StringBuilder();
        
        // Título de la película
        if (boleto.getFuncion().getPelicula() != null) {
            info.append(boleto.getFuncion().getPelicula().getTitulo());
            
            // Agregar géneros si están disponibles
            if (boleto.getFuncion().getPelicula().getGenerosComoString() != null && 
                !boleto.getFuncion().getPelicula().getGenerosComoString().isEmpty()) {
                info.append(" (").append(boleto.getFuncion().getPelicula().getGenerosComoString()).append(")");
            }
            
            // Agregar duración si está disponible
            if (boleto.getFuncion().getPelicula().getDuracionMinutos() > 0) {
                info.append(" - ").append(boleto.getFuncion().getPelicula().getDuracionMinutos()).append(" min");
            }
        } else {
            info.append("Película no especificada");
        }
        
        return info.toString();
    }

    /**
     * Formatea la información de la sala de manera legible.
     *
     * @param boleto El boleto con la sala a formatear
     * @return String con información de la sala
     */
    private String formatearInformacionSala(Boleto boleto) {
        if (boleto == null || boleto.getFuncion() == null || boleto.getFuncion().getSala() == null) {
            return "Sala no especificada";
        }

        StringBuilder sala = new StringBuilder();
        String nombreSala = boleto.getFuncion().getSala().getNombre();
        
        if (boleto.getFuncion().getSala().getTipo() != null) {
            String tipo = boleto.getFuncion().getSala().getTipo().toString();
            // Convertir tipos de sala a formato amigable
            switch (tipo.toUpperCase()) {
                case "NORMAL":
                    sala.append("Sala Estándar");
                    break;
                case "VIP":
                    sala.append("Sala VIP");
                    break;
                case "IMAX":
                    sala.append("Sala IMAX");
                    break;
                case "4DX":
                    sala.append("Sala 4DX");
                    break;
                default:
                    sala.append("Sala ").append(tipo);
                    break;
            }
            
            if (!nombreSala.toLowerCase().contains("sala")) {
                sala.append(" ").append(nombreSala);
            }
        } else {
            sala.append(nombreSala);
        }
        
        return sala.toString();
    }

    /**
     * Formatea fecha y hora de manera legible.
     *
     * @param boleto El boleto con la fecha a formatear
     * @return String con fecha y hora formateadas
     */
    private String formatearFechaHora(Boleto boleto) {
        if (boleto == null || boleto.getFuncion() == null || boleto.getFuncion().getFechaHoraInicio() == null) {
            return "Horario no especificado";
        }

        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return boleto.getFuncion().getFechaHoraInicio().format(formatter);
    }

    /**
     * Genera un nombre descriptivo para el archivo de boleto.
     *
     * @param boleto El boleto para generar el nombre
     * @return String con el nombre del archivo
     */
    public String generarNombreArchivoBoleto(Boleto boleto) {
        if (boleto == null) {
            return "Boleto_Desconocido.pdf";
        }

        StringBuilder nombre = new StringBuilder("Boleto_");
        
        // Agregar información de la película (limitada para evitar nombres muy largos)
        if (boleto.getFuncion() != null && boleto.getFuncion().getPelicula() != null) {
            String titulo = boleto.getFuncion().getPelicula().getTitulo();
            // Limpiar caracteres especiales y limitar longitud
            titulo = titulo.replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll("\\s+", "_");
            if (titulo.length() > 20) {
                titulo = titulo.substring(0, 20);
            }
            nombre.append(titulo).append("_");
        }
        
        // Agregar butaca usando getters
        if (boleto.getButaca() != null) {
            if (boleto.getButaca().getFila() != null && boleto.getButaca().getColumna() != null) {
                nombre.append("Butaca_").append(boleto.getButaca().getFila()).append(boleto.getButaca().getColumna());
            } else {
                nombre.append("Butaca_Desconocida");
            }
        } else {
            nombre.append("Butaca_NoAsignada");
        }
        
        // Agregar fecha si está disponible
        if (boleto.getFuncion() != null && boleto.getFuncion().getFechaHoraInicio() != null) {
            java.time.format.DateTimeFormatter formatter = 
                java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy_HHmm");
            nombre.append("_").append(boleto.getFuncion().getFechaHoraInicio().format(formatter));
        }
        
        nombre.append(".pdf");
        
        return nombre.toString();
    }

    /**
     * Crea la estructura de directorios si no existe
     * 
     * @param carpeta Ruta completa de la carpeta a crear
     */
    private void crearCarpetaSiNoExiste(String carpeta) {
        File dir = new File(carpeta);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Carpeta creada: " + carpeta);
            }
        }
    }
}