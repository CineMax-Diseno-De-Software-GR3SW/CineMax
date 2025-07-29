package com.cinemax.reportes.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ChoiceBox;
import java.time.LocalDate;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import com.cinemax.reportes.modelos.ReporteVentaDTO;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import com.cinemax.reportes.modelos.persistencia.ReporteDAO;

// Interfaz Strategy
interface ExportStrategy {
    void exportar(List<ReporteVentaDTO> datos, File destino, String tituloReporte, Map<String, Object> infoExtra) throws Exception;
}

// Implementación PDF
class ExportPDFStrategy implements ExportStrategy {
    @Override
    public void exportar(List<ReporteVentaDTO> datos, File destino, String tituloReporte, Map<String, Object> infoExtra) throws Exception {
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
                // Información extra (opcional)
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
                float col2X = margin + 150;
                float col3X = margin + 250;
                // Headers
                contentStream.setFont(fontBold, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(col1X + 5, yPosition - 15);
                contentStream.showText("Fecha");
                contentStream.newLineAtOffset(col2X - col1X - 5, 0);
                contentStream.showText("Boletos");
                contentStream.newLineAtOffset(col3X - col2X, 0);
                contentStream.showText("Ingresos");
                contentStream.endText();
                // Datos
                contentStream.setFont(fontNormal, 10);
                int totalBoletos = 0;
                double totalIngresos = 0;
                for (int i = 0; i < datos.size(); i++) {
                    ReporteVentaDTO d = datos.get(i);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(col1X + 5, yPosition - 40 - (i * rowHeight));
                    contentStream.showText(d.fecha);
                    contentStream.newLineAtOffset(col2X - col1X - 5, 0);
                    contentStream.showText(String.valueOf(d.boletosVendidos));
                    contentStream.newLineAtOffset(col3X - col2X, 0);
                    contentStream.showText(String.format("$%.2f", d.ingresos));
                    contentStream.endText();
                    totalBoletos += d.boletosVendidos;
                    totalIngresos += d.ingresos;
                }
                // Fila de total
                contentStream.setFont(fontBold, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(col1X + 5, yPosition - 40 - (datos.size() * rowHeight));
                contentStream.showText("TOTAL:");
                contentStream.newLineAtOffset(col2X - col1X - 5, 0);
                contentStream.showText(String.valueOf(totalBoletos));
                contentStream.newLineAtOffset(col3X - col2X, 0);
                contentStream.showText(String.format("$%.2f", totalIngresos));
                contentStream.endText();
                // Pie de página
                contentStream.setFont(fontNormal, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, 50);
                contentStream.showText("© 2024 CineMax - Sistema de Gestion de Reportes");
                contentStream.endText();
            }
            document.save(destino);
        }
    }
}

// Implementación CSV (solo definida)
class ExportCSVStrategy implements ExportStrategy {
    @Override
    public void exportar(List<ReporteVentaDTO> datos, File destino, String tituloReporte, Map<String, Object> infoExtra) throws Exception {
        // Lógica pendiente
    }
}

public class ControladorReportesPrincipal {

    @FXML
    private Button btnBack;

    @FXML private DatePicker dateDesde;
    @FXML private DatePicker dateHasta;
    @FXML private ChoiceBox<String> choiceHorario;
    @FXML private BarChart<String, Number> barChart;
    @FXML private MenuButton menuExportar;
    @FXML private MenuItem menuExportarPDF;
    @FXML private MenuItem menuExportarCSV;

    private ReporteDAO reporteDAO = new ReporteDAO();

    @FXML
    private void initialize() {
        choiceHorario.getItems().addAll("Todos", "Matutino", "Nocturno");
        choiceHorario.setValue("Todos");

        // Configura acciones de exportación en los MenuItem
        if (menuExportarPDF != null) {
            menuExportarPDF.setOnAction(e -> onExportarPDF(null));
        }
        if (menuExportarCSV != null) {
            menuExportarCSV.setOnAction(e -> onExportarCSV(null));
        }
    }

    @FXML
    private void onBackAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToReporteProgramado(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/reportes/ModuloReportesProgramados.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaLogin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onFiltrar(ActionEvent event) {
        LocalDate desde = dateDesde.getValue();
        LocalDate hasta = dateHasta.getValue();
        String horario = choiceHorario.getValue();

        // Llama al DAO para obtener los datos filtrados
        List<ReporteVentaDTO> ventas = reporteDAO.obtenerVentas(desde, hasta, null, null, horario);

        // Si no hay datos reales, usa datos simulados para mostrar en la vista
        List<ReporteVentaDTO> datosParaMostrar = ventas.isEmpty() ? List.of(
            new ReporteVentaDTO("2024-07-01", 120, 3600.0),
            new ReporteVentaDTO("2024-07-02", 98, 2940.0),
            new ReporteVentaDTO("2024-07-03", 156, 4680.0)
        ) : ventas;


        // Actualiza el gráfico
        if (barChart != null) {
            barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (ReporteVentaDTO venta : datosParaMostrar) {
                series.getData().add(new XYChart.Data<>(venta.fecha, venta.boletosVendidos));
            }
            barChart.getData().add(series);
        }

        // Actualiza el label de totales
        double total = datosParaMostrar.stream().mapToDouble(v -> v.ingresos).sum();
    }

    // Métodos para exportar PDF y CSV
    @FXML
    private void onExportarPDF(ActionEvent event) {
        //exportarReporte(new ExportPDFStrategy(), "pdf");
    }

    @FXML
    private void onExportarCSV(ActionEvent event) {
        //exportarReporte(new ExportCSVStrategy(), "csv");
    }

    // private void exportarReporte(ExportStrategy strategy, String tipo) {
    //     try {
    //         // Usa los datos actualmente mostrados en la tabla para exportar
    //         List<ReporteVentaDTO> datos = tablaPreview.getItems();
    //         if (datos == null || datos.isEmpty()) {
    //             // Si la tabla está vacía, usa datos simulados
    //             datos = List.of(
    //                 new ReporteVentaDTO("2024-07-01", 120, 3600.0),
    //                 new ReporteVentaDTO("2024-07-02", 98, 2940.0),
    //                 new ReporteVentaDTO("2024-07-03", 156, 4680.0)
    //             );
    //         }
    //         FileChooser fileChooser = new FileChooser();
    //         fileChooser.setTitle("Guardar Reporte " + tipo.toUpperCase());
    //         fileChooser.setInitialFileName("reporte_ventas." + tipo);
    //         if (tipo.equals("pdf")) {
    //             fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
    //         } else if (tipo.equals("csv")) {
    //             fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));
    //         }
    //         Stage stage = (Stage) btnBack.getScene().getWindow();
    //         File archivo = fileChooser.showSaveDialog(stage);
    //         if (archivo != null) {
    //             Map<String, Object> infoExtra = new HashMap<>();
    //             infoExtra.put("subtitulo", "Reporte generado manualmente");
    //             strategy.exportar(datos, archivo, "REPORTE DE VENTAS - CINEMAX", infoExtra);
    //             mostrarAlerta("Éxito", "El reporte ha sido exportado correctamente.");
    //         }
    //     } catch (Exception e) {
    //         mostrarAlerta("Error", "No se pudo exportar el reporte: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    // }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}