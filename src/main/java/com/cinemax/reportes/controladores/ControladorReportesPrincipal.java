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
import java.net.URL;
import javafx.scene.control.ButtonType;

// Interfaz Strategy para la exportación de reportes
interface ExportStrategy {
    void exportar(List<ReporteVentaDTO> datos, File destino, String tituloReporte, Map<String, Object> infoExtra)
            throws Exception;
}

// Implementación de ExportStrategy para PDF
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

// Implementación de ExportStrategy para CSV
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

    // Datos simulados para las gráficas
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
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Configurar formato de fecha en la tabla
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaGeneracion"));
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

        // Configurar columna de acciones
        colAcciones.setCellFactory(col -> new TableCell<ReporteGenerado, Integer>() {
            private final Button btnDescargar = new Button("📄");
            private final Button btnVer = new Button("👁");
            private final Button btnEliminar = new Button("🗑");
            private final HBox buttons = new HBox(5);

            {
                btnDescargar.setTooltip(new Tooltip("Descargar"));
                btnVer.setTooltip(new Tooltip("Ver previsualización"));
                btnEliminar.setTooltip(new Tooltip("Eliminar"));

                buttons.getChildren().addAll(btnVer, btnDescargar, btnEliminar);

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
                    mostrarAlerta("Éxito", "Reporte eliminado de la lista.");
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });

        tablaReportes.setItems(reportesGenerados);
    }

    private void cargarReportesSimulados() {
        reportesGenerados.addAll(reportesSimulados);
    }

    private void inicializarGraficasVacias() {
        if (barChart != null) {
            barChart.getData().clear();
            barChart.setTitle("Seleccione filtros y presione 'Filtrar' para ver datos");
        }
        if (pieChart != null) {
            pieChart.getData().clear();
            pieChart.setTitle("Seleccione filtros y presione 'Filtrar' para ver datos");
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
            mostrarAlerta("Error de Navegación", "No se pudo cargar la pantalla de reportes programados.");
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
            mostrarAlerta("Error de Navegación", "No se pudo cargar la pantalla de login.");
        }
    }

    @FXML
    private void onBackAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Navegación", "No se pudo volver a la pantalla principal.");
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
            mostrarAlerta("Error", "Por favor seleccione las fechas de inicio y fin.");
            return;
        }

        try {
            // Obtener datos REALES de la base de datos con todos los filtros
            List<ReporteVentaDTO> datosFiltrados = ventasService.getVentasFiltradas(desde, hasta, sala, tipoBoleto, horario);

            if (datosFiltrados.isEmpty()) {
                mostrarAlerta("Sin Datos", "No hay datos para mostrar con los filtros seleccionados.");
                // Limpiar gráficas
                barChart.getData().clear();
                pieChart.getData().clear();
                barChart.setTitle("No hay datos para el período seleccionado");
                pieChart.setTitle("No hay datos para el período seleccionado");
                return;
            }

            // Actualizar gráficas con datos REALES
            actualizarGraficaBarras(datosFiltrados);
            actualizarGraficaPastel(datosFiltrados);

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

    private void actualizarGraficaBarras(List<ReporteVentaDTO> datos) {
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

    private void actualizarGraficaPastel(List<ReporteVentaDTO> datos) {
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
            mostrarAlerta("Error", "Por favor seleccione las fechas de inicio y fin.");
            return;
        }

        try {
            // Obtener datos REALES filtrados
            List<ReporteVentaDTO> datosFiltrados = ventasService.getVentasFiltradas(desde, hasta, sala, tipoBoleto, horario);

            if (datosFiltrados.isEmpty()) {
                mostrarAlerta("Sin Datos", "No hay datos para mostrar con los filtros seleccionados en la base de datos.");
                return;
            }

            // Mostrar previsualización del reporte con datos REALES
            mostrarPrevisualizacionReporte(datosFiltrados, true);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al obtener datos de la base de datos: " + e.getMessage());
        }
    }

    private void verPrevisualizacion(ReporteGenerado reporte) {
        // En una aplicación real, aquí cargarías los datos del reporte guardado
        // Por ahora, usamos datos simulados para la demostración
        mostrarPrevisualizacionReporte(datosSimulados, false);
    }

    private void descargarReporte(ReporteGenerado reporte) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte");

        // Configurar la extensión del archivo según el tipo de reporte
        if ("PDF".equals(reporte.getTipo())) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        } else if ("CSV".equals(reporte.getTipo())) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        }

        // Sugerir un nombre de archivo
        fileChooser.setInitialFileName(reporte.getNombre());

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                // Aquí deberías tener la lógica para generar y guardar el reporte
                // Por ahora, solo mostramos una alerta
                mostrarAlerta("Éxito", "Reporte " + reporte.getTipo() + " guardado en: " + file.getAbsolutePath());
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo guardar el reporte: " + e.getMessage());
            }
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
            headerBox.setStyle("-fx-background-color: #2B2B2B; -fx-border-color: #2B2B2B; -fx-border-width: 1px; -fx-padding: 20; -fx-border-radius: 5px;");

            Label titulo = new Label("REPORTE DE VENTAS - CINEMAX");
            titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1; -fx-alignment: center; -fx-pref-width: 760;");
            titulo.setMaxWidth(Double.MAX_VALUE);
            titulo.setAlignment(Pos.CENTER);

            // Información extra
            Label infoFecha = new Label("Período: " + dateDesde.getValue() + " - " + dateHasta.getValue());
            infoFecha.setStyle("-fx-text-fill: #bdc3c7;");

            headerBox.getChildren().addAll(titulo, infoFecha);

            // Contenido del reporte (tabla)
            TableView<ReporteVentaDTO> tablaPrevia = new TableView<>();
            tablaPrevia.setItems(FXCollections.observableArrayList(datos));
            tablaPrevia.setPrefHeight(250);

            // Columnas de la tabla de previsualización
            TableColumn<ReporteVentaDTO, String> colFechaPrev = new TableColumn<>("Fecha");
            colFechaPrev.setCellValueFactory(new PropertyValueFactory<>("fecha"));
            TableColumn<ReporteVentaDTO, String> colTipoBoletoPrev = new TableColumn<>("Tipo Boleto");
            colTipoBoletoPrev.setCellValueFactory(new PropertyValueFactory<>("tipoBoleto"));
            TableColumn<ReporteVentaDTO, String> colFormatoPrev = new TableColumn<>("Formato");
            colFormatoPrev.setCellValueFactory(new PropertyValueFactory<>("formato"));
            TableColumn<ReporteVentaDTO, Integer> colBoletosPrev = new TableColumn<>("Boletos Vendidos");
            colBoletosPrev.setCellValueFactory(new PropertyValueFactory<>("boletosVendidos"));
            TableColumn<ReporteVentaDTO, Double> colIngresosPrev = new TableColumn<>("Ingresos");
            colIngresosPrev.setCellValueFactory(new PropertyValueFactory<>("ingresos"));

            tablaPrevia.getColumns().addAll(colFechaPrev, colTipoBoletoPrev, colFormatoPrev, colBoletosPrev, colIngresosPrev);

            ScrollPane scrollPane = new ScrollPane(tablaPrevia);
            scrollPane.setFitToWidth(true);

            contenido.getChildren().addAll(headerBox, scrollPane);

            // Botones de acción
            if (permitirDescarga) {
                HBox botonesAccion = new HBox(10);
                botonesAccion.setAlignment(Pos.CENTER_RIGHT);

                ChoiceBox<String> choiceTipoExportacion = new ChoiceBox<>();
                choiceTipoExportacion.getItems().addAll("PDF", "CSV");
                choiceTipoExportacion.setValue("PDF");

                Button btnExportar = new Button("Exportar");
                btnExportar.setOnAction(e -> {
                    String tipo = choiceTipoExportacion.getValue();
                    exportarReporte(datos, tipo);
                    ventanaPrevia.close();
                });

                Button btnCancelar = new Button("Cancelar");
                btnCancelar.setOnAction(e -> ventanaPrevia.close());

                botonesAccion.getChildren().addAll(new Label("Exportar como:"), choiceTipoExportacion, btnExportar, btnCancelar);
                contenido.getChildren().add(botonesAccion);
            }

            Scene scene = new Scene(contenido, 800, 600);
            ventanaPrevia.setScene(scene);
            ventanaPrevia.initModality(Modality.APPLICATION_MODAL);
            ventanaPrevia.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo mostrar la previsualización: " + e.getMessage());
        }
    }

    private void exportarReporte(List<ReporteVentaDTO> datos, String tipo) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte");

        String titulo = "Reporte de Ventas (" + dateDesde.getValue() + " - " + dateHasta.getValue() + ")";
        fileChooser.setInitialFileName("reporte_ventas_" + LocalDate.now().toString());

        File file = null;
        ExportStrategy strategy = null;

        if ("PDF".equalsIgnoreCase(tipo)) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
            file = fileChooser.showSaveDialog(null);
            strategy = new ExportPDFStrategy();
        } else if ("CSV".equalsIgnoreCase(tipo)) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
            file = fileChooser.showSaveDialog(null);
            strategy = new ExportCSVStrategy();
        }

        if (file != null && strategy != null) {
            try {
                Map<String, Object> infoExtra = new HashMap<>();
                infoExtra.put("subtitulo", "Período: " + dateDesde.getValue() + " - " + dateHasta.getValue());
                strategy.exportar(datos, file, "REPORTE DE VENTAS - CINEMAX", infoExtra);
                mostrarAlerta("Éxito", "Reporte guardado exitosamente en: " + file.getAbsolutePath());
                
                // Guardar el reporte generado en la lista
                ReporteGenerado nuevoReporte = new ReporteGenerado(
                    reportesGenerados.size() + 1,
                    file.getName(),
                    tipo,
                    LocalDateTime.now(),
                    file.getAbsolutePath(),
                    "Reporte de ventas del " + dateDesde.getValue() + " al " + dateHasta.getValue()
                );
                reportesGenerados.add(nuevoReporte);
                
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "Error al exportar el reporte: " + e.getMessage());
            }
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