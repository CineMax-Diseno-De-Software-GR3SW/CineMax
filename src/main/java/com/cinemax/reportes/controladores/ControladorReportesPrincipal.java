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
import java.net.URL;


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
    private ChoiceBox<String> choiceSala;
    @FXML
    private ChoiceBox<String> choiceTipoBoleto;
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

    @FXML
    private void initialize() {
        // Configurar opciones de horario
        choiceHorario.getItems().addAll("Todos", "Matutino", "Nocturno");
        choiceHorario.setValue("Todos");

        // Configurar opciones de tipo de boleto
        choiceTipoBoleto.getItems().addAll("Todos", "VIP", "Normal");
        choiceTipoBoleto.setValue("Todos");

        // Cargar salas desde la base de datos
        cargarSalasDesdeBaseDatos();

        // Configurar tabla de reportes
        configurarTablaReportes();

        // Cargar reportes simulados (mantener para reportes anteriores)
        cargarReportesSimulados();

        // Inicializar gráficas vacías
        inicializarGraficasVacias();

        // Obtener datos reales del resumen
        Map<String, Object> datosReales = ventasService.getResumenDeVentas();
        System.out.println("=== DATOS REALES DE LA BASE DE DATOS ===");
        System.out.println("Total boletos: " + datosReales.get("total_boletos_vendidos"));
        System.out.println("Total facturas: " + datosReales.get("total_facturas"));
        System.out.println("Ingreso total: " + datosReales.get("ingreso_total"));
        System.out.println("Total funciones: " + datosReales.get("total_funciones"));
        System.out.println("Fecha inicio: " + datosReales.get("fecha_inicio"));
        System.out.println("Fecha fin: " + datosReales.get("fecha_fin"));
    }

    private void cargarSalasDesdeBaseDatos() {
        try {
            List<String> salas = ventasService.getSalasDisponibles();
            choiceSala.getItems().clear();
            choiceSala.getItems().addAll(salas);
            choiceSala.setValue("Todas");
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback en caso de error
            choiceSala.getItems().addAll("Todas", "Sala 1", "Sala 2", "Sala 3");
            choiceSala.setValue("Todas");
            mostrarAlerta("Advertencia", "No se pudieron cargar las salas desde la base de datos. Se usarán valores por defecto.");
        }
    }

    private void configurarTablaReportes() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaGeneracion"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Configurar columna de acciones
        colAcciones.setCellFactory(col -> new TableCell<ReporteGenerado, Integer>() {
            private final Button btnDescargar = new Button("📄");
            private final Button btnVer = new Button("👁");
            private final Button btnEliminar = new Button("🗑");

            {
                btnDescargar.setTooltip(new Tooltip("Descargar"));
                btnVer.setTooltip(new Tooltip("Ver previsualización"));
                btnEliminar.setTooltip(new Tooltip("Eliminar"));

                btnDescargar.setOnAction(e -> {
                    ReporteGenerado reporte = getTableView().getItems().get(getIndex());
                    descargarReporte(reporte);
                });

                btnVer.setOnAction(e -> {
                    ReporteGenerado reporte = getTableView().getItems().get(getIndex());
                    verPrevisualizacion(reporte);
                });

                btnEliminar.setOnAction(e -> {
                    ReporteGenerado reporte = getTableView().getItems().get(getIndex());
                    reportesGenerados.remove(reporte);
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5);
                    buttons.getChildren().addAll(btnVer, btnDescargar, btnEliminar);
                    setGraphic(buttons);
                }
            }
        });

        tablaReportes.setItems(reportesGenerados);
    }

    private void cargarReportesSimulados() {
        reportesGenerados.addAll(
            new ReporteGenerado(1, "Reporte_Ventas_20241201_1430", "PDF", LocalDateTime.now().minusDays(2),
                    "C:/reportes/reporte_ventas_20241201.pdf", "Reporte de ventas del 01/12/2024 al 05/12/2024"),
            new ReporteGenerado(2, "Reporte_Ventas_20241128_0915", "CSV", LocalDateTime.now().minusDays(5),
                    "C:/reportes/reporte_ventas_20241128.csv", "Reporte de ventas del 25/11/2024 al 30/11/2024"),
            new ReporteGenerado(3, "Reporte_Ventas_20241125_1620", "PDF", LocalDateTime.now().minusDays(8),
                    "C:/reportes/reporte_ventas_20241125.pdf", "Reporte de ventas del 20/11/2024 al 25/11/2024")
        );
    }

    private void inicializarGraficasVacias() {
        if (barChart != null) {
            barChart.setTitle("Seleccione filtros y presione 'Filtrar' para ver datos");
        }
        if (pieChart != null) {
            pieChart.setTitle("Seleccione filtros y presione 'Filtrar' para ver datos");
        }
    }

    @FXML
    private void onFiltrar(ActionEvent event) {
        LocalDate desde = dateDesde.getValue();
        LocalDate hasta = dateHasta.getValue();
        String horario = choiceHorario.getValue();
        String sala = choiceSala.getValue();
        String tipoBoleto = choiceTipoBoleto.getValue();

        if (desde == null || hasta == null) {
            mostrarAlerta("Error", "Por favor seleccione las fechas de inicio y fin");
            return;
        }

        try {
            // Obtener datos REALES de la base de datos con todos los filtros
            List<ReporteVentaDTO> datosFiltrados = ventasService.getVentasFiltradas(desde, hasta, sala, tipoBoleto, horario);
            
            if (datosFiltrados.isEmpty()) {
                mostrarAlerta("Sin Datos", "No hay datos para mostrar con los filtros seleccionados");
                // Limpiar gráficas
                barChart.getData().clear();
                pieChart.getData().clear();
                barChart.setTitle("No hay datos para el período seleccionado");
                pieChart.setTitle("No hay datos para el período seleccionado");
                return;
            }

            // Actualizar gráficas con datos REALES
            actualizarGraficaBarrasReal(datosFiltrados);
            actualizarGraficaPastelReal(datosFiltrados);

            // Mostrar mensaje de confirmación con estadísticas reales
            int totalBoletos = datosFiltrados.stream().mapToInt(v -> v.boletosVendidos).sum();
            double totalIngresos = datosFiltrados.stream().mapToDouble(v -> v.ingresos).sum();
            
            mostrarAlerta("Filtros Aplicados - Datos Reales",
                    "Se han aplicado los filtros:\n" +
                    "• Desde: " + desde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                    "• Hasta: " + hasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                    "• Horario: " + horario + "\n" +
                    "• Sala: " + sala + "\n" +
                    "• Tipo Boleto: " + tipoBoleto + "\n\n" +
                    "Resultados obtenidos de la base de datos:\n" +
                    "• Total boletos vendidos: " + totalBoletos + "\n" +
                    "• Total ingresos: $" + String.format("%.2f", totalIngresos));
                    
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al obtener datos de la base de datos: " + e.getMessage());
        }
    }

    private void actualizarGraficaBarrasReal(List<ReporteVentaDTO> datos) {
        if (barChart != null) {
            barChart.getData().clear();

            if (datos.isEmpty()) {
                barChart.setTitle("No hay datos para mostrar con los filtros seleccionados");
                return;
            }

            // Agrupar datos REALES por fecha y tipo de boleto
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

            String titulo = "Ventas por Tipo de Boleto - DATOS REALES";
            if (desde != null && hasta != null) {
                titulo += " (" + desde.format(DateTimeFormatter.ofPattern("dd/MM")) +
                        " - " + hasta.format(DateTimeFormatter.ofPattern("dd/MM")) + ")";
            }
            barChart.setTitle(titulo);
        }
    }

    private void actualizarGraficaPastelReal(List<ReporteVentaDTO> datos) {
        if (pieChart != null) {
            pieChart.getData().clear();

            if (datos.isEmpty()) {
                pieChart.setTitle("No hay datos para mostrar con los filtros seleccionados");
                return;
            }

            // Agrupar datos REALES por formato (2D vs 3D)
            Map<String, Integer> datosPorFormato = new HashMap<>();

            for (ReporteVentaDTO dato : datos) {
                String formato = dato.formato != null && !dato.formato.isEmpty() ? dato.formato : "2D";
                datosPorFormato.merge(formato, dato.boletosVendidos, Integer::sum);
            }

            // Agregar datos al gráfico de pastel
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

            String titulo = "Distribución por Formato - DATOS REALES";
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
        String sala = choiceSala.getValue();
        String tipoBoleto = choiceTipoBoleto.getValue();

        if (desde == null || hasta == null) {
            mostrarAlerta("Error", "Por favor seleccione las fechas de inicio y fin");
            return;
        }

        try {
            // Obtener datos REALES filtrados
            List<ReporteVentaDTO> datosFiltrados = ventasService.getVentasFiltradas(desde, hasta, sala, tipoBoleto, horario);
            
            if (datosFiltrados.isEmpty()) {
                mostrarAlerta("Sin Datos", "No hay datos para mostrar con los filtros seleccionados en la base de datos");
                return;
            }

            // Mostrar previsualización del reporte con datos REALES
            mostrarPrevisualizacionReporteReal(datosFiltrados, true);
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al obtener datos de la base de datos: " + e.getMessage());
        }
    }

    private void mostrarPrevisualizacionReporteReal(List<ReporteVentaDTO> datos, boolean permitirDescarga) {
        try {
            Stage ventanaPrevia = new Stage();
            ventanaPrevia.setTitle("Previsualización del Reporte - CineMax (DATOS REALES)");
            ventanaPrevia.setResizable(true);

            VBox contenido = new VBox(15);
            contenido.setPadding(new Insets(20));
            contenido.setStyle("-fx-background-color: #2B2B2B;");

            // Header del reporte
            VBox headerBox = new VBox(10);
            headerBox.setStyle(
                    "-fx-background-color: #2B2B2B; -fx-border-color: #2B2B2B; -fx-border-width: 1px; -fx-padding: 20; -fx-border-radius: 5px;");

            Label titulo = new Label("REPORTE DE VENTAS - CINEMAX (DATOS REALES)");
            titulo.setStyle(
                    "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1; -fx-alignment: center; -fx-pref-width: 760;");
            titulo.setMaxWidth(Double.MAX_VALUE);
            titulo.setAlignment(Pos.CENTER);

            LocalDate desde = dateDesde.getValue();
            LocalDate hasta = dateHasta.getValue();
            String horario = choiceHorario.getValue();
            String sala = choiceSala.getValue();
            String tipoBoleto = choiceTipoBoleto.getValue();

            Label fechaGen = new Label("Período: " + desde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    " - " + hasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            fechaGen.setStyle("-fx-font-size: 12px; -fx-text-fill: #ecf0f1;");

            Label horarioLabel = new Label("Horario: " + horario + " | Sala: " + sala + " | Tipo: " + tipoBoleto);
            horarioLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ecf0f1;");

            Label estado = new Label("Estado: Generado con datos reales el "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            estado.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");

            headerBox.getChildren().addAll(titulo, fechaGen, horarioLabel, estado);

            // Contenido del reporte con gráficas REALES
            VBox contenidoReporte = generarContenidoReporteCompletoReal(datos);

            HBox barraNota = new HBox();
            barraNota.setStyle(
                    "-fx-background-color: #2B2B2B; -fx-padding: 12 0 12 0; -fx-border-radius: 0; -fx-border-width: 0;");
            barraNota.setAlignment(Pos.CENTER_LEFT);
            barraNota.setMaxWidth(Double.MAX_VALUE);

            // Nota sobre el reporte
            Label notaReporte = new Label(
                    "📊 Este reporte incluye datos REALES obtenidos directamente de la base de datos PostgreSQL.");
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
                    exportarReporteReal(new ExportPDFStrategy(), "pdf", datos);
                });

                Button btnDescargarCSV = new Button("📊 Descargar como CSV");
                btnDescargarCSV.setStyle(
                        "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
                btnDescargarCSV.setOnAction(e -> {
                    ventanaPrevia.close();
                    exportarReporteReal(new ExportCSVStrategy(), "csv", datos);
                });

                botonesBox.getChildren().addAll(btnDescargarPDF, btnDescargarCSV);
            }

            Button btnCerrar = new Button("Cerrar");
            btnCerrar.setStyle(
                    "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
            btnCerrar.setOnAction(e -> ventanaPrevia.close());

            botonesBox.getChildren().add(btnCerrar);

            // Contenido completo
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

    // Método para generar contenido del reporte con datos reales
    private VBox generarContenidoReporteCompletoReal(List<ReporteVentaDTO> datos) {
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

        contenido.getChildren().addAll(tituloSeccion, tablaDatos, tituloEstadisticas, estadisticasBox);

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

    private Label crearCeldaTabla(String texto, boolean esHeader) {
        Label celda = new Label(texto);
        celda.setPrefWidth(120);
        celda.setMaxWidth(120);
        celda.setStyle(esHeader ? "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5; -fx-alignment: center;"
                : "-fx-text-fill: #ecf0f1; -fx-padding: 5; -fx-alignment: center;");
        return celda;
    }

    // Método para exportar reportes con datos reales
    private void exportarReporteReal(ExportStrategy strategy, String tipo, List<ReporteVentaDTO> datosReales) {
        try {
            LocalDate desde = dateDesde.getValue();
            LocalDate hasta = dateHasta.getValue();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte " + tipo.toUpperCase());
            fileChooser.setInitialFileName("reporte_ventas_real." + tipo);
            if (tipo.equals("pdf")) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
            } else if (tipo.equals("csv")) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));
            }
            Stage stage = (Stage) btnBack.getScene().getWindow();
            File archivo = fileChooser.showSaveDialog(stage);

            if (archivo != null) {
                Map<String, Object> infoExtra = new HashMap<>();
                infoExtra.put("subtitulo", "Reporte generado con datos reales el "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

                strategy.exportar(datosReales, archivo, "REPORTE DE VENTAS - CINEMAX (DATOS REALES)", infoExtra);

                // Agregar el nuevo reporte a la lista
                ReporteGenerado nuevoReporte = new ReporteGenerado(
                        reportesGenerados.size() + 1,
                        "Reporte_Ventas_Real_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")),
                        tipo.toUpperCase(),
                        LocalDateTime.now(),
                        archivo.getAbsolutePath(),
                        "Reporte con datos reales del " + desde + " al " + hasta);
                reportesGenerados.add(0, nuevoReporte);

                mostrarAlerta("Éxito", "El reporte con datos reales ha sido exportado correctamente.");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo exportar el reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Métodos auxiliares para la tabla de reportes
    private void descargarReporte(ReporteGenerado reporte) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar " + reporte.getTipo());
            fileChooser.setInitialFileName(reporte.getNombre() + "." + reporte.getTipo().toLowerCase());
            
            if (reporte.getTipo().equals("PDF")) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
            } else {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
            }
            
            Stage stage = (Stage) tablaReportes.getScene().getWindow();
            File archivo = fileChooser.showSaveDialog(stage);
            
            if (archivo != null) {
                // Simular descarga
                mostrarAlerta("Descarga", "Reporte descargado: " + archivo.getAbsolutePath());
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo descargar el reporte: " + e.getMessage());
        }
    }

    private void verPrevisualizacion(ReporteGenerado reporte) {
        try {
            // Obtener datos reales para la previsualización
            LocalDate desde = LocalDate.now().minusDays(7);
            LocalDate hasta = LocalDate.now();
            List<ReporteVentaDTO> datosReales = ventasService.getVentasFiltradas(desde, hasta, null, null, null);
            
            mostrarPrevisualizacionReporteReal(datosReales, false);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo mostrar la previsualización: " + e.getMessage());
        }
    }

    @FXML
    private void goToReportesProgramados(ActionEvent event) {
        try {
            URL fxmlUrl = getClass().getResource("/vistas/reportes/ModuloReportesProgramados.fxml");
            if (fxmlUrl == null) {
                throw new IOException("FXML file not found: /vistas/reportes/ModuloReportesProgramados.fxml");
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Reportes Programados");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Navegación");
            alert.setHeaderText("No se pudo cargar la vista de reportes programados");
            alert.setContentText("Asegúrese de que el archivo FXML exista en la ruta correcta.");
            alert.showAndWait();
        }
    }

    @FXML
    public void onBackAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
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
    @FXML
    public void onCerrarSesion(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaLogin.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Portal del Administrador");
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        mostrarAlerta("Error", "No se pudo cargar la pantalla de login: " + e.getMessage());
    }
}
}