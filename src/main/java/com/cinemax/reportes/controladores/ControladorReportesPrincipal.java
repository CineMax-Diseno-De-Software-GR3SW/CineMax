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
import java.util.HashMap;
import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.chart.PieChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import com.cinemax.reportes.modelos.ReporteVentaDTO;
import com.cinemax.reportes.servicios.VentasService;
import com.cinemax.reportes.modelos.ReporteGenerado;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// Interfaz Strategy
interface ExportStrategy {
    void exportar(List<ReporteVentaDTO> datos, File destino, String tituloReporte, Map<String, Object> infoExtra)
            throws Exception;
}

// Implementación PDF
class ExportPDFStrategy implements ExportStrategy {
    @Override
    public void exportar(List<ReporteVentaDTO> datos, File destino, String tituloReporte, Map<String, Object> infoExtra)
            throws Exception {
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
                    ReporteVentaDTO d = datos.get(i);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(col1X + 5, yPosition - 40 - (i * rowHeight));
                    contentStream.showText(d.fecha);
                    contentStream.newLineAtOffset(col2X - col1X - 5, 0);
                    contentStream.showText(d.tipoBoleto + " " + d.formato);
                    contentStream.newLineAtOffset(col3X - col2X, 0);
                    contentStream.showText(String.valueOf(d.boletosVendidos));
                    contentStream.newLineAtOffset(col4X - col3X, 0);
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
                contentStream.showText("© 2024 CineMax - Sistema de Gestion de Reportes");
                contentStream.endText();
            }
            document.save(destino);
        }
    }
}

// Implementación CSV
class ExportCSVStrategy implements ExportStrategy {
    @Override
    public void exportar(List<ReporteVentaDTO> datos, File destino, String tituloReporte, Map<String, Object> infoExtra)
            throws Exception {
        StringBuilder csv = new StringBuilder();
        csv.append("Fecha,Tipo Boleto,Formato,Boletos Vendidos,Ingresos\n");

        for (ReporteVentaDTO d : datos) {
            csv.append(String.format("%s,%s,%s,%d,%.2f\n",
                    d.fecha, d.tipoBoleto, d.formato, d.boletosVendidos, d.ingresos));
        }

        java.nio.file.Files.write(destino.toPath(), csv.toString().getBytes());
    }
}

public class ControladorReportesPrincipal {

    @FXML
    private Button btnBack;
    @FXML
    private Button btnFiltrar;
    @FXML
    private Button btnConfirmar;

    @FXML
    private DatePicker dateDesde;
    @FXML
    private DatePicker dateHasta;
    @FXML
    private ChoiceBox<String> choiceHorario;
    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private PieChart pieChart;
    @FXML
    private TableView<ReporteGenerado> tablaReportes;
    @FXML
    private TableColumn<ReporteGenerado, String> colNombre;
    @FXML
    private TableColumn<ReporteGenerado, String> colTipo;
    @FXML
    private TableColumn<ReporteGenerado, LocalDateTime> colFecha;
    @FXML
    private TableColumn<ReporteGenerado, String> colDescripcion;
    @FXML
    private TableColumn<ReporteGenerado, Integer> colAcciones;

    private ObservableList<ReporteGenerado> reportesGenerados = FXCollections.observableArrayList();
    
    private VentasService ventasService = new VentasService();
    private Map<String, Object> datos = ventasService.getResumenDeVentas();;

    // Datos quemados para las gráficas
    private final List<ReporteVentaDTO> datosSimulados = Arrays.asList(
            new ReporteVentaDTO("2024-07-01", 80, 2400.0, "VIP", "3D"),
            new ReporteVentaDTO("2024-07-01", 40, 1200.0, "Normal", "2D"),
            new ReporteVentaDTO("2024-07-02", 60, 1800.0, "VIP", "2D"),
            new ReporteVentaDTO("2024-07-02", 38, 1140.0, "Normal", "3D"),
            new ReporteVentaDTO("2024-07-03", 90, 2700.0, "VIP", "3D"),
            new ReporteVentaDTO("2024-07-03", 66, 1980.0, "Normal", "2D"),
            new ReporteVentaDTO("2024-07-04", 75, 2250.0, "VIP", "2D"),
            new ReporteVentaDTO("2024-07-04", 45, 1350.0, "Normal", "3D"),
            new ReporteVentaDTO("2024-07-05", 85, 2550.0, "VIP", "3D"),
            new ReporteVentaDTO("2024-07-05", 55, 1650.0, "Normal", "2D"));

    // Datos simulados para reportes generados
    private final List<ReporteGenerado> reportesSimulados = Arrays.asList(
            new ReporteGenerado(1, "Reporte_Ventas_20241201_1430", "PDF", LocalDateTime.now().minusDays(2),
                    "C:/reportes/reporte_ventas_20241201.pdf", "Reporte de ventas del 01/12/2024 al 05/12/2024"),
            new ReporteGenerado(2, "Reporte_Ventas_20241128_0915", "CSV", LocalDateTime.now().minusDays(5),
                    "C:/reportes/reporte_ventas_20241128.csv", "Reporte de ventas del 25/11/2024 al 30/11/2024"),
            new ReporteGenerado(3, "Reporte_Ventas_20241125_1620", "PDF", LocalDateTime.now().minusDays(8),
                    "C:/reportes/reporte_ventas_20241125.pdf", "Reporte de ventas del 20/11/2024 al 25/11/2024"));

    @FXML
    private void initialize() {
        choiceHorario.getItems().addAll("Todos", "Matutino", "Nocturno");
        choiceHorario.setValue("Todos");

        // Configurar tabla de reportes
        configurarTablaReportes();

        // Cargar reportes simulados
        cargarReportesSimulados();

        // Inicializar gráficas vacías
        inicializarGraficasVacias();

        System.out.println("Total boletos: " + datos.get("total_boletos_vendidos"));
        System.out.println("Total facturas: " + datos.get("total_facturas"));
        System.out.println("Ingreso total: " + datos.get("ingreso_total"));
        System.out.println("Total funciones: " + datos.get("total_funciones"));
        System.out.println("Fecha inicio: " + datos.get("fecha_inicio"));
        System.out.println("Fecha fin: " + datos.get("fecha_fin"));
    }

    private void configurarTablaReportes() {
        // Configurar las columnas
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaGeneracion"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Configurar formato de fecha
        colFecha.setCellFactory(column -> new TableCell<ReporteGenerado, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            }
        });

        // Configurar columna de acciones con botón para abrir
        colAcciones.setCellFactory(column -> new TableCell<ReporteGenerado, Integer>() {
            private final Button btnAbrir = new Button("Abrir");

            {
                btnAbrir.getStyleClass().add("table-button");
                btnAbrir.setOnAction(event -> {
                    ReporteGenerado reporte = getTableView().getItems().get(getIndex());
                    abrirReporte(reporte);
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnAbrir);
                }
            }
        });

        tablaReportes.setItems(reportesGenerados);
    }

    private void cargarReportesSimulados() {
        reportesGenerados.clear();
        reportesGenerados.addAll(reportesSimulados);
    }

    private void inicializarGraficasVacias() {
        // Limpiar gráfica de barras
        if (barChart != null) {
            barChart.getData().clear();
            barChart.setTitle("Seleccione filtros y haga clic en 'Filtrar' para ver datos");
        }

        // Limpiar gráfica de pastel
        if (pieChart != null) {
            pieChart.getData().clear();
            pieChart.setTitle("Seleccione filtros y haga clic en 'Filtrar' para ver datos");
        }
    }

    private void abrirReporte(ReporteGenerado reporte) {
        try {
            // Mostrar previsualización del reporte sin opciones de descarga
            mostrarPrevisualizacionReporte(datosSimulados, false);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo mostrar la previsualización del reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToReporteProgramado(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/reportes/ModuloReportesProgramados.fxml"));
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

        if (desde == null || hasta == null) {
            mostrarAlerta("Error", "Por favor seleccione las fechas de inicio y fin");
            return;
        }

        // Usar datos simulados filtrados por fecha
        List<ReporteVentaDTO> datosFiltrados = filtrarDatosSimulados(desde, hasta, horario);

        // Actualizar gráficas con datos filtrados
        actualizarGraficaBarras(datosFiltrados);
        actualizarGraficaPastel(datosFiltrados);

        // Mostrar mensaje de confirmación
        mostrarAlerta("Filtros Aplicados",
                "Se han aplicado los filtros:\n" +
                        "• Desde: " + desde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                        "• Hasta: " + hasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                        "• Horario: " + horario + "\n\n" +
                        "Las gráficas ahora muestran los datos correspondientes.");
    }

    private List<ReporteVentaDTO> filtrarDatosSimulados(LocalDate desde, LocalDate hasta, String horario) {
        // Simular filtrado de datos (en realidad siempre devuelve los mismos datos)
        // En una implementación real, aquí se filtrarían los datos por fecha y horario
        return datosSimulados;
    }

    private void actualizarGraficaBarras(List<ReporteVentaDTO> datos) {
        if (barChart != null) {
            barChart.getData().clear();

            if (datos.isEmpty()) {
                barChart.setTitle("No hay datos para mostrar con los filtros seleccionados");
                return;
            }

            // Agrupar datos por fecha y tipo de boleto
            Map<String, Map<String, Integer>> datosAgrupados = new HashMap<>();

            for (ReporteVentaDTO venta : datos) {
                String fecha = venta.fecha;
                String tipo = venta.tipoBoleto;

                datosAgrupados.computeIfAbsent(fecha, k -> new HashMap<>());
                datosAgrupados.get(fecha).merge(tipo, venta.boletosVendidos, Integer::sum);
            }

            // Crear series para VIP y Normal
            XYChart.Series<String, Number> serieVIP = new XYChart.Series<>();
            serieVIP.setName("VIP");

            XYChart.Series<String, Number> serieNormal = new XYChart.Series<>();
            serieNormal.setName("Normal");

            // Agregar datos a las series
            for (String fecha : datosAgrupados.keySet()) {
                Map<String, Integer> tiposEnFecha = datosAgrupados.get(fecha);
                serieVIP.getData().add(new XYChart.Data<>(fecha, tiposEnFecha.getOrDefault("VIP", 0)));
                serieNormal.getData().add(new XYChart.Data<>(fecha, tiposEnFecha.getOrDefault("Normal", 0)));
            }

            // Agregar series al gráfico
            barChart.getData().addAll(serieVIP, serieNormal);

            // Actualizar título con información de los filtros
            LocalDate desde = dateDesde.getValue();
            LocalDate hasta = dateHasta.getValue();

            String titulo = "Ventas por Tipo de Boleto";
            if (desde != null && hasta != null) {
                titulo += " (" + desde.format(DateTimeFormatter.ofPattern("dd/MM")) +
                        " - " + hasta.format(DateTimeFormatter.ofPattern("dd/MM")) + ")";
            }
            barChart.setTitle(titulo);
        }
    }

    private void actualizarGraficaPastel(List<ReporteVentaDTO> datos) {
        if (pieChart != null) {
            pieChart.getData().clear();

            if (datos.isEmpty()) {
                pieChart.setTitle("No hay datos para mostrar con los filtros seleccionados");
                return;
            }

            // Agrupar datos por formato (2D vs 3D)
            Map<String, Integer> datosPorFormato = new HashMap<>();

            for (ReporteVentaDTO dato : datos) {
                datosPorFormato.merge(dato.formato, dato.boletosVendidos, Integer::sum);
            }

            // Agregar datos al gráfico de pastel (solo una vez)
            for (Map.Entry<String, Integer> entry : datosPorFormato.entrySet()) {
                pieChart.getData()
                        .add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
            }

            // Mostrar etiquetas dentro del círculo
            pieChart.setLabelsVisible(true);
            pieChart.setLabelLineLength(10);

            // Actualizar título con información de los filtros
            LocalDate desde = dateDesde.getValue();
            LocalDate hasta = dateHasta.getValue();

            String titulo = "Distribución 2D vs 3D";
            if (desde != null && hasta != null) {
                titulo += " (" + desde.format(DateTimeFormatter.ofPattern("dd/MM")) +
                        " - " + hasta.format(DateTimeFormatter.ofPattern("dd/MM")) + ")";
            }
            pieChart.setTitle(titulo);
        }
    }

    @FXML
    private void onConfirmarReporte(ActionEvent event) {
        LocalDate desde = dateDesde.getValue();
        LocalDate hasta = dateHasta.getValue();
        String horario = choiceHorario.getValue();

        if (desde == null || hasta == null) {
            mostrarAlerta("Error", "Por favor seleccione las fechas de inicio y fin");
            return;
        }

        // Verificar si hay datos para mostrar
        List<ReporteVentaDTO> datosFiltrados = filtrarDatosSimulados(desde, hasta, horario);
        if (datosFiltrados.isEmpty()) {
            mostrarAlerta("Sin Datos", "No hay datos para mostrar con los filtros seleccionados");
            return;
        }

        // Mostrar previsualización del reporte
        mostrarPrevisualizacionReporte(datosFiltrados, true);
    }

    @FXML
    void onBackAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarPrevisualizacionReporte(List<ReporteVentaDTO> datos, boolean permitirDescarga) {
        try {
            Stage ventanaPrevia = new Stage();
            ventanaPrevia.setTitle("Previsualización del Reporte - CineMax");
            ventanaPrevia.setResizable(true);

            VBox contenido = new VBox(15);
            contenido.setPadding(new Insets(20));
            contenido.setStyle("-fx-background-color: #2B2B2B;");

            // Header del reporte
            VBox headerBox = new VBox(10);
            headerBox.setStyle(
                    "-fx-background-color: #2B2B2B; -fx-border-color: #2B2B2B; -fx-border-width: 1px; -fx-padding: 20; -fx-border-radius: 5px;");

            Label titulo = new Label("REPORTE DE VENTAS - CINEMAX");
            titulo.setStyle(
                    "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1; -fx-alignment: center; -fx-pref-width: 760;");
            titulo.setMaxWidth(Double.MAX_VALUE);
            titulo.setAlignment(Pos.CENTER);

            LocalDate desde = dateDesde.getValue();
            LocalDate hasta = dateHasta.getValue();
            String horario = choiceHorario.getValue();

            Label fechaGen = new Label("Período: " + desde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    " - " + hasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            fechaGen.setStyle("-fx-font-size: 12px; -fx-text-fill: #ecf0f1;");

            Label horarioLabel = new Label("Horario: " + horario);
            horarioLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ecf0f1;");

            Label estado = new Label("Estado: Generado el "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            estado.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");

            headerBox.getChildren().addAll(titulo, fechaGen, horarioLabel, estado);

            // Contenido del reporte con gráficas
            VBox contenidoReporte = generarContenidoReporteCompleto(datos);

            HBox barraNota = new HBox();
            barraNota.setStyle(
                    "-fx-background-color: #2B2B2B; -fx-padding: 12 0 12 0; -fx-border-radius: 0; -fx-border-width: 0;");
            barraNota.setAlignment(Pos.CENTER_LEFT);
            barraNota.setMaxWidth(Double.MAX_VALUE);

            // Nota sobre el reporte
            Label notaReporte = new Label(
                    "📊 Este reporte incluye datos de ventas, gráficas de distribución y análisis detallado del período seleccionado.");
            notaReporte.setStyle(
                    "-fx-font-size: 12px; -fx-text-fill: #e67e22; -fx-font-style: italic; -fx-padding: 0 20 0 20;");
            notaReporte.setMaxWidth(Double.MAX_VALUE);

            barraNota.getChildren().add(notaReporte);

            // Botones
            HBox botonesBox = new HBox(10);
            botonesBox.setAlignment(Pos.CENTER);

            if (permitirDescarga) {
                Button btnDescargarPDF = new Button("📄 Descargar como PDF");
                btnDescargarPDF.setStyle(
                        "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
                btnDescargarPDF.setOnAction(e -> {
                    ventanaPrevia.close();
                    exportarReporte(new ExportPDFStrategy(), "pdf");
                });

                Button btnDescargarCSV = new Button("📊 Descargar como CSV");
                btnDescargarCSV.setStyle(
                        "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
                btnDescargarCSV.setOnAction(e -> {
                    ventanaPrevia.close();
                    exportarReporte(new ExportCSVStrategy(), "csv");
                });

                botonesBox.getChildren().addAll(btnDescargarPDF, btnDescargarCSV);
            }

            Button btnCerrar = new Button("Cerrar");
            btnCerrar.setStyle(
                    "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
            btnCerrar.setOnAction(e -> ventanaPrevia.close());

            botonesBox.getChildren().add(btnCerrar);

            // Lo que se va a mostrar en el componente
            ScrollPane scrollPane = new ScrollPane();
            VBox contenidoCompleto = new VBox(15);
            contenidoCompleto.getChildren().addAll(headerBox, contenidoReporte, barraNota);
            scrollPane.setContent(contenidoCompleto);
            scrollPane.setFitToWidth(true);

            contenido.getChildren().addAll(scrollPane, botonesBox);

            Scene escena = new Scene(contenido, 800, 700);
            ventanaPrevia.setScene(escena);
            ventanaPrevia.initModality(Modality.APPLICATION_MODAL);
            ventanaPrevia.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo mostrar la previsualización del reporte.");
        }
    }

    private VBox generarContenidoReporteCompleto(List<ReporteVentaDTO> datos) {
        VBox contenido = new VBox(20);
        contenido.setStyle(
                "-fx-background-color: #2B2B2B; -fx-border-color: #2B2B2B; -fx-border-width: 1px; -fx-padding: 20; -fx-border-radius: 5px;");

        // Sección de resumen de datos
        Label tituloSeccion = new Label("📊 RESUMEN DE VENTAS");
        tituloSeccion.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

        VBox tablaDatos = new VBox(5);
        tablaDatos.setStyle(
                "-fx-background-color: #2B2B2B; -fx-border-color: #ecf0f1; -fx-border-width: 1px; -fx-padding: 10; -fx-text-fill: #ecf0f1;");

        // Headers de la tabla
        HBox headerTabla = new HBox();
        headerTabla.setStyle("-fx-background-color: #3498db; -fx-padding: 8;");
        headerTabla.getChildren().addAll(
                crearCeldaTabla("Fecha", true),
                crearCeldaTabla("Tipo Boleto", true),
                crearCeldaTabla("Formato", true),
                crearCeldaTabla("Boletos", true),
                crearCeldaTabla("Ingresos", true));

        // Datos de la tabla
        VBox filasDatos = new VBox(2);
        int totalBoletos = 0;
        double totalIngresos = 0;

        for (ReporteVentaDTO dato : datos) {
            filasDatos.getChildren().add(crearFilaTablaCompleta(dato));
            totalBoletos += dato.boletosVendidos;
            totalIngresos += dato.ingresos;
        }

        // Fila de total
        HBox totalRow = new HBox();
        totalRow.setStyle("-fx-background-color: #2ecc71; -fx-padding: 8;");
        totalRow.getChildren().addAll(
                crearCeldaTabla("TOTAL:", true),
                crearCeldaTabla("", true),
                crearCeldaTabla("", true),
                crearCeldaTabla(String.valueOf(totalBoletos), true),
                crearCeldaTabla(String.format("$%.2f", totalIngresos), true));

        tablaDatos.getChildren().addAll(headerTabla, filasDatos, totalRow);

        // Sección de gráficas reales
        Label tituloGraficas = new Label("📈 GRÁFICAS DE ANÁLISIS");
        tituloGraficas.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

        VBox graficasBox = new VBox(15);
        graficasBox.setStyle("-fx-border-color: #2B2B2B; -fx-border-width: 1px; -fx-padding: 15;");

        // Gráfica de barras real
        VBox graficaBarras = new VBox(10);
        graficaBarras.setStyle("-fx-background-color: #2B2B2B; -fx-padding: 15; -fx-border-radius: 5px;");
        Label lblGraficaBarras = new Label("📊 Gráfica de Barras: Ventas por Tipo de Boleto (VIP vs Normal)");
        lblGraficaBarras.setStyle("-fx-font-weight: bold; -fx-text-fill: #ecf0f1; -fx-font-size: 14px;");

        BarChart<String, Number> barChartPreview = crearGraficaBarrasPreview(datos);
        barChartPreview.setPrefHeight(300);
        barChartPreview.setPrefWidth(600);

        graficaBarras.getChildren().addAll(lblGraficaBarras, barChartPreview);

        // Gráfica de pastel real
        VBox graficaPastel = new VBox(10);
        graficaPastel.setStyle("-fx-background-color: #2B2B2B; -fx-padding: 15; -fx-border-radius: 5px;");
        Label lblGraficaPastel = new Label("🥧 Gráfica de Pastel: Distribución de Boletos por Formato (2D vs 3D)");
        lblGraficaPastel.setStyle("-fx-font-weight: bold; -fx-text-fill: #ecf0f1; -fx-font-size: 14px;");

        PieChart pieChartPreview = crearGraficaPastelPreview(datos);
        pieChartPreview.setPrefHeight(300);
        pieChartPreview.setPrefWidth(400);

        graficaPastel.getChildren().addAll(lblGraficaPastel, pieChartPreview);

        graficasBox.getChildren().addAll(graficaBarras, graficaPastel);

        // Estadísticas adicionales
        Label tituloEstadisticas = new Label("📋 ESTADÍSTICAS ADICIONALES");
        tituloEstadisticas.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

        VBox estadisticasBox = new VBox(10);
        estadisticasBox.setStyle("-fx-border-color: #2B2B2B; -fx-border-width: 1px; -fx-padding: 15;");

        // Calcular estadísticas
        Map<String, Integer> boletosPorTipo = new HashMap<>();
        Map<String, Integer> boletosPorFormato = new HashMap<>();

        for (ReporteVentaDTO dato : datos) {
            boletosPorTipo.merge(dato.tipoBoleto, dato.boletosVendidos, Integer::sum);
            boletosPorFormato.merge(dato.formato, dato.boletosVendidos, Integer::sum);
        }

        estadisticasBox.getChildren().addAll(
                crearEstadistica("Total de Boletos Vendidos", String.valueOf(totalBoletos)),
                crearEstadistica("Total de Ingresos", String.format("$%.2f", totalIngresos)),
                crearEstadistica("Promedio por Boleto", String.format("$%.2f", totalIngresos / totalBoletos)),
                crearEstadistica("Boletos VIP", String.valueOf(boletosPorTipo.getOrDefault("VIP", 0))),
                crearEstadistica("Boletos Normal", String.valueOf(boletosPorTipo.getOrDefault("Normal", 0))),
                crearEstadistica("Boletos 2D", String.valueOf(boletosPorFormato.getOrDefault("2D", 0))),
                crearEstadistica("Boletos 3D", String.valueOf(boletosPorFormato.getOrDefault("3D", 0))));

        contenido.getChildren().addAll(tituloSeccion, tablaDatos, tituloGraficas, graficasBox, tituloEstadisticas,
                estadisticasBox);

        return contenido;
    }

    private HBox crearFilaTablaCompleta(ReporteVentaDTO dato) {
        HBox fila = new HBox();
        fila.setStyle("-fx-background-color: #2B2B2B; -fx-border-color: #2B2B2B; -fx-border-width: 0 0 1 0;");

        Label celdaFecha = crearCeldaTabla(dato.fecha, false);
        Label celdaTipo = crearCeldaTabla(dato.tipoBoleto, false);
        Label celdaFormato = crearCeldaTabla(dato.formato, false);
        Label celdaBoletos = crearCeldaTabla(String.valueOf(dato.boletosVendidos), false);
        Label celdaIngresos = crearCeldaTabla(String.format("$%.2f", dato.ingresos), false);

        fila.getChildren().addAll(celdaFecha, celdaTipo, celdaFormato, celdaBoletos, celdaIngresos);
        return fila;
    }

    private HBox crearEstadistica(String titulo, String valor) {
        HBox estadistica = new HBox(10);
        estadistica.setStyle("-fx-background-color: #2B2B2B; -fx-padding: 8; -fx-border-radius: 3px;");

        Label lblTitulo = new Label(titulo + ":");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-text-fill: #ecf0f1; -fx-min-width: 150;");

        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        estadistica.getChildren().addAll(lblTitulo, lblValor);
        return estadistica;
    }

    private BarChart<String, Number> crearGraficaBarrasPreview(List<ReporteVentaDTO> datos) {
        BarChart<String, Number> barChartPreview = new BarChart<>(new CategoryAxis(), new NumberAxis());
        barChartPreview.setTitle("Ventas por Tipo de Boleto (VIP vs Normal)");
        barChartPreview.setStyle("-fx-background-color: #2B2B2B; -fx-border-color: #2B2B2B; -fx-border-width: 1px;");

        // Configurar ejes
        CategoryAxis xAxis = (CategoryAxis) barChartPreview.getXAxis();
        NumberAxis yAxis = (NumberAxis) barChartPreview.getYAxis();
        xAxis.setLabel("Fecha");
        yAxis.setLabel("Cantidad de Boletos Vendidos");

        // Aplicar estilos a los ejes
        xAxis.setStyle("-fx-tick-label-fill: #ecf0f1; -fx-font-weight: bold;");
        yAxis.setStyle("-fx-tick-label-fill: #ecf0f1; -fx-font-weight: bold;");

        // Agrupar datos por fecha y tipo de boleto (solo VIP y Normal)
        Map<String, Map<String, Integer>> datosAgrupados = new HashMap<>();

        for (ReporteVentaDTO venta : datos) {
            String fecha = venta.fecha;
            String tipo = venta.tipoBoleto; // Solo VIP o Normal

            datosAgrupados.computeIfAbsent(fecha, k -> new HashMap<>());
            datosAgrupados.get(fecha).merge(tipo, venta.boletosVendidos, Integer::sum);
        }

        // Crear series para VIP y Normal
        XYChart.Series<String, Number> serieVIP = new XYChart.Series<>();
        serieVIP.setName("VIP");

        XYChart.Series<String, Number> serieNormal = new XYChart.Series<>();
        serieNormal.setName("Normal");

        // Agregar datos a las series
        for (String fecha : datosAgrupados.keySet()) {
            Map<String, Integer> tiposEnFecha = datosAgrupados.get(fecha);
            serieVIP.getData().add(new XYChart.Data<>(fecha, tiposEnFecha.getOrDefault("VIP", 0)));
            serieNormal.getData().add(new XYChart.Data<>(fecha, tiposEnFecha.getOrDefault("Normal", 0)));
        }

        // Agregar series al gráfico
        barChartPreview.getData().addAll(serieVIP, serieNormal);

        barChartPreview.applyCss();
        if (barChartPreview.lookup(".chart-title") != null)
            barChartPreview.lookup(".chart-title")
                    .setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 16px; -fx-font-weight: bold;");
        barChartPreview.lookupAll(".axis-label")
                .forEach(node -> node.setStyle("-fx-text-fill: #ecf0f1; -fx-font-weight: bold;"));
        barChartPreview.lookupAll(".chart-legend").forEach(node -> node.setStyle("-fx-text-fill: #ecf0f1;"));

        return barChartPreview;
    }

    private PieChart crearGraficaPastelPreview(List<ReporteVentaDTO> datos) {
        PieChart pieChartPreview = new PieChart();
        pieChartPreview.setTitle("Distribución de Boletos por Formato (2D vs 3D)");
        pieChartPreview.setStyle(
                "-fx-background-color: #2B2B2B; -fx-border-color: #ecf0f1; -fx-border-width: 1px;");

        // Agrupar datos por formato (2D vs 3D)
        Map<String, Integer> datosPorFormato = new HashMap<>();
        for (ReporteVentaDTO dato : datos) {
            datosPorFormato.merge(dato.formato, dato.boletosVendidos, Integer::sum);
        }
        for (Map.Entry<String, Integer> entry : datosPorFormato.entrySet()) {
            pieChartPreview.getData()
                    .add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
        }

        pieChartPreview.setLabelLineLength(10);
        pieChartPreview.setLabelsVisible(true);

        // Listener para forzar color blanco en las etiquetas cada vez que cambian
        pieChartPreview.getData().addListener((javafx.collections.ListChangeListener<PieChart.Data>) c -> {
            pieChartPreview.applyCss();
            pieChartPreview.lookupAll(".chart-pie-label")
                .forEach(node -> node.setStyle("-fx-text-fill: #ecf0f1; -fx-font-weight: bold;"));
        });

        // También forzar el color al crear el gráfico
        pieChartPreview.applyCss();
        if (pieChartPreview.lookup(".chart-title") != null)
            pieChartPreview.lookup(".chart-title")
                .setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 16px; -fx-font-weight: bold;");
        pieChartPreview.lookupAll(".chart-legend").forEach(node -> node.setStyle("-fx-text-fill: #ecf0f1;"));
        pieChartPreview.lookupAll(".chart-pie-label").forEach(node -> node.setStyle("-fx-text-fill: #ecf0f1; -fx-font-weight: bold;"));

        return pieChartPreview;
    }

    private Label crearCeldaTabla(String texto, boolean esHeader) {
        Label celda = new Label(texto);
        celda.setPrefWidth(120);
        celda.setMaxWidth(120);
        celda.setStyle(esHeader ? "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5; -fx-alignment: center;"
                : "-fx-text-fill: #ecf0f1; -fx-padding: 5; -fx-alignment: center;");
        return celda;
    }

    private void exportarReporte(ExportStrategy strategy, String tipo) {
        try {
            LocalDate desde = dateDesde.getValue();
            LocalDate hasta = dateHasta.getValue();
            String horario = choiceHorario.getValue();

            if (desde == null || hasta == null) {
                mostrarAlerta("Error", "Por favor seleccione las fechas antes de exportar");
                return;
            }

            // Usar datos simulados para exportar
            List<ReporteVentaDTO> datos = filtrarDatosSimulados(desde, hasta, horario);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte " + tipo.toUpperCase());
            fileChooser.setInitialFileName("reporte_ventas." + tipo);
            if (tipo.equals("pdf")) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
            } else if (tipo.equals("csv")) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));
            }
            Stage stage = (Stage) btnBack.getScene().getWindow();
            File archivo = fileChooser.showSaveDialog(stage);

            if (archivo != null) {
                Map<String, Object> infoExtra = new HashMap<>();
                infoExtra.put("subtitulo", "Reporte generado el "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

                strategy.exportar(datos, archivo, "REPORTE DE VENTAS - CINEMAX", infoExtra);

                // Agregar el nuevo reporte a la lista simulada
                ReporteGenerado nuevoReporte = new ReporteGenerado(
                        reportesGenerados.size() + 1,
                        "Reporte_Ventas_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")),
                        tipo.toUpperCase(),
                        LocalDateTime.now(),
                        archivo.getAbsolutePath(),
                        "Reporte de ventas del " + desde + " al " + hasta);
                reportesGenerados.add(0, nuevoReporte); // Agregar al inicio

                mostrarAlerta("Éxito", "El reporte ha sido exportado correctamente.");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo exportar el reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}