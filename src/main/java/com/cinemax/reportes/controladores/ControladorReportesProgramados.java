package com.cinemax.reportes.controladores;

import java.io.IOException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

// Para la dependencia archivo pdf
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import com.cinemax.reportes.modelos.ReporteGenerado;
import com.cinemax.reportes.servicios.ReportesSchedulerService;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

// Para la dependencia de programado task

public class ControladorReportesProgramados {

    @FXML
    private Button btnBack;

    @FXML
    private ChoiceBox<String> choiceFrecuencia;

    @FXML
    private TableView<ReporteGenerado> tablaReportesGenerados;

    @FXML
    private TableColumn<ReporteGenerado, String> columnaNombre;

    @FXML
    private TableColumn<ReporteGenerado, String> columnaEstado;

    @FXML
    private TableColumn<ReporteGenerado, LocalDate> columnaFecha;

    @FXML
    private TableColumn<ReporteGenerado, Void> columnaAcciones;

    final ReportesSchedulerService schedulerService = ReportesSchedulerService.getInstance();

    @FXML
    private void initialize() {
        // Inicializar la tabla de reportes generados
        if (tablaReportesGenerados != null) {
            inicializarTablaReportes();
        }
        
        // Opciones para la frecuencia del reporte
        ObservableList<String> opcionesFrecuencia = FXCollections.observableArrayList(
                "Diario",
                "Semanal",
                "Mensual",
                "Trimestral",
                "Anual");

        // Asignar las opciones al ChoiceBox
        choiceFrecuencia.setItems(opcionesFrecuencia);

        // Establecer un valor por defecto
        choiceFrecuencia.setValue("Seleccione la Ejecucion");

        // Opcional: agregar listener para detectar cambios
        choiceFrecuencia.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Frecuencia seleccionada: " + newValue);
        });

        // Iniciar el programador de tareas
        schedulerService.iniciarScheduler();

        // Detiene el scheduler al cerrar la ventana
        Platform.runLater(() -> {
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setOnCloseRequest(event -> schedulerService.detenerScheduler());
        });
    }

    private void inicializarTablaReportes() {
        // Configurar las columnas
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombreReporte"));
        columnaFecha.setCellValueFactory(new PropertyValueFactory<>("fechaGeneracion"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Columna de acciones con botones
        columnaAcciones.setCellFactory(columna -> new TableCell<>() {
            private final Button btnVerPDF = new Button("Ver");
            private final Button btnDescargar = new Button("Descargar");
            private final HBox pane = new HBox(5, btnVerPDF, btnDescargar);

            {
                // Configurar estilos de botones
                btnVerPDF.getStyleClass().add("btn-small");
                btnDescargar.getStyleClass().add("btn-small");

                // Configurar acciones
                btnVerPDF.setOnAction(event -> {
                    ReporteGenerado reporte = getTableView().getItems().get(getIndex());
                    mostrarVistaPrevia(reporte);
                });

                btnDescargar.setOnAction(event -> {
                    ReporteGenerado reporte = getTableView().getItems().get(getIndex());
                    descargarReportePDF(reporte);
                });

                pane.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        schedulerService.getReportesEjecutados()
                .addListener((javafx.collections.ListChangeListener<ReporteGenerado>) change -> {
                    if (!schedulerService.getReportesPendientes().isEmpty()) {
                        choiceFrecuencia.setDisable(true);
                    } else {
                        choiceFrecuencia.setDisable(false);
                    }
                });

        // Verificación inicial (por si ya hay ejecutados al entrar)
        if (!schedulerService.getReportesPendientes().isEmpty()) {
            choiceFrecuencia.setDisable(true);
        } else {
            choiceFrecuencia.setDisable(false);
        }

        // Inicializar la tabla con una lista vacía (sin datos de ejemplo)
        tablaReportesGenerados.setItems(schedulerService.getReportesEjecutados());
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    void confirmarReporteProgramado(ActionEvent event) {
        // Validar que se haya seleccionado una frecuencia
        if (choiceFrecuencia.getValue() == null || choiceFrecuencia.getValue().equals("Seleccione la Ejecucion")) {
            mostrarAlerta("Error", "Se ha programado un reporte");
            return;
        }

        // Verificar si ya existe un reporte con la misma frecuencia
        String frecuenciaSeleccionada = choiceFrecuencia.getValue();
        if (existeReporteConFrecuencia(frecuenciaSeleccionada)) {
            mostrarAlerta("Error", "Ya existe un reporte programado con frecuencia " + frecuenciaSeleccionada + ".\n" +
                    "Solo puede haber una ejecución por cada tipo de frecuencia.");
            return;
        }

        // Mostrar ventana de previsualización
        mostrarVentanaPrevia();
    }

    /**
     * Verifica si ya existe un reporte programado con la misma frecuencia
     */
    private boolean existeReporteConFrecuencia(String frecuencia) {
        // Revisar en la tabla
        ObservableList<ReporteGenerado> reportes = tablaReportesGenerados.getItems();
        for (ReporteGenerado reporte : reportes) {
            if (reporte.getFrecuencia().equals(frecuencia)) {
                return true;
            }
        }
        // Revisar en la lista de pendientes
        for (ReporteGenerado reporte : schedulerService.getReportesPendientes()) {
            if (reporte.getFrecuencia().equals(frecuencia)) {
                return true;
            }
        }
        return false;
    }

    // Vista previa del Reporte PDF

    private void mostrarVentanaPrevia() {
        try {
            // Crear una nueva ventana (Stage)
            Stage ventanaPrevia = new Stage();
            ventanaPrevia.setTitle("Previsualización del Reporte PDF");
            ventanaPrevia.setResizable(true);

            // Crear el contenido principal
            VBox contenidoPrincipal = new VBox(10);
            contenidoPrincipal.setPadding(new Insets(15));
            contenidoPrincipal.setStyle("-fx-background-color: #f5f5f5;");

            // Header con información del reporte
            VBox headerBox = new VBox(5);
            headerBox.setStyle(
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1px; -fx-padding: 15; -fx-border-radius: 5px;");

            Label tituloReporte = new Label("REPORTE DE VENTAS - CINEMAX");
            tituloReporte.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            Label lblFechaGeneracion = new Label("Se ha agendado fecha de creacion");
            lblFechaGeneracion.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

            Label lblFrecuencia = new Label("Frecuencia: " + choiceFrecuencia.getValue());
            lblFrecuencia.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

            headerBox.getChildren().addAll(tituloReporte, lblFechaGeneracion, lblFrecuencia);

            // Contenido del reporte (simulado)
            VBox contenidoReporte = new VBox(10);
            contenidoReporte.setStyle(
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1px; -fx-padding: 20; -fx-border-radius: 5px;");

            // Sección de datos simulados
            Label tituloSeccion = new Label("📊 RESUMEN DE VENTAS");
            tituloSeccion.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

            // Tabla de datos simulados
            VBox tablaDatos = new VBox(5);
            tablaDatos.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 1px; -fx-padding: 10;");

            // Headers de la tabla
            HBox headerTabla = new HBox();
            headerTabla.setStyle("-fx-background-color: #3498db; -fx-padding: 8;");
            Label colFecha = crearCeldaTabla("Fecha", true);
            Label colBoletos = crearCeldaTabla("Boletos Vendidos", true);
            Label colIngresos = crearCeldaTabla("Ingresos", true);
            headerTabla.getChildren().addAll(colFecha, colBoletos, colIngresos);

            // Filas de datos de ejemplo
            VBox filasDatos = new VBox(2);

            // TODO: Verificar que no se repita la ejecucion del task
            String frecuenciaSeleccionada = choiceFrecuencia.getValue();
            String fechaEjecucion = schedulerService.calcularProximaEjecucion(LocalDateTime.now().toString(),
                    frecuenciaSeleccionada);

            filasDatos.getChildren().addAll(
                    crearFilaTabla(fechaEjecucion, "125", "String,750.00"),
                    crearFilaTabla(LocalDate.now().minusDays(1).toString(), "98", "private,940.00"),
                    crearFilaTabla(LocalDate.now().minusDays(2).toString(), "156", ",680.00"),
                    crearFilaTabla(LocalDate.now().minusDays(3).toString(), "87", "private,610.00"));

            // Total
            HBox totalRow = new HBox();
            totalRow.setStyle("-fx-background-color: #2ecc71; -fx-padding: 8;");
            Label totalLabel = crearCeldaTabla("TOTAL:", true);
            Label totalBoletos = crearCeldaTabla("466", true);
            Label totalIngresos = crearCeldaTabla(",980.00", true);
            totalRow.getChildren().addAll(totalLabel, totalBoletos, totalIngresos);

            tablaDatos.getChildren().addAll(headerTabla, filasDatos, totalRow);
            contenidoReporte.getChildren().addAll(tituloSeccion, tablaDatos);

            // Información adicional
            VBox infoAdicional = new VBox(5);
            infoAdicional.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 10; -fx-border-radius: 3px;");

            Label notaInfo = new Label(
                    "📝 Nota: Este es un ejemplo de cómo se verá el reporte cuando se genere automáticamente.");
            notaInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            notaInfo.setWrapText(true);

            Label proximaGeneracion = new Label("⏰ Próxima generación programada: " + fechaEjecucion);
            proximaGeneracion.setStyle("-fx-font-size: 11px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");

            infoAdicional.getChildren().addAll(notaInfo, proximaGeneracion);

            // Botones
            HBox botonesBox = new HBox(15);
            botonesBox.setAlignment(Pos.CENTER);
            botonesBox.setPadding(new Insets(15, 0, 5, 0));

            Button btnConfirmar = new Button("✅ Programar Reporte");
            btnConfirmar.setStyle(
                    "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 25; -fx-border-radius: 5px; -fx-background-radius: 5px;");

            btnConfirmar.setOnAction(e -> {
                agregarReporteATabla(fechaEjecucion);
                mostrarAlerta("✅ Reporte Programado",
                        "El reporte ha sido programado exitosamente.\n" +
                                "Se ejecutará cada: " + frecuenciaSeleccionada +
                                "\nPróxima ejecución: " + fechaEjecucion);
                ventanaPrevia.close();
            });

            Button btnCancelar = new Button("❌ Cancelar");
            btnCancelar.setStyle(
                    "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 25; -fx-border-radius: 5px; -fx-background-radius: 5px;");
            btnCancelar.setOnAction(e -> ventanaPrevia.close());

            botonesBox.getChildren().addAll(btnConfirmar, btnCancelar);

            // Scroll pane para contenido largo
            ScrollPane scrollPane = new ScrollPane();
            VBox contenidoCompleto = new VBox(10);
            contenidoCompleto.getChildren().addAll(headerBox, contenidoReporte, infoAdicional);
            scrollPane.setContent(contenidoCompleto);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent;");

            // Agregar todo al contenido principal
            contenidoPrincipal.getChildren().addAll(scrollPane, botonesBox);

            // Crear la escena y mostrar la ventana
            Scene escena = new Scene(contenidoPrincipal, 600, 500);
            ventanaPrevia.setScene(escena);
            ventanaPrevia.initModality(Modality.APPLICATION_MODAL);
            ventanaPrevia.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la previsualización del reporte.");
        }
    }

    // Métodos auxiliares para crear la tabla
    private Label crearCeldaTabla(String texto, boolean esHeader) {
        Label celda = new Label(texto);
        celda.setPrefWidth(180);
        celda.setMaxWidth(180);
        celda.setStyle(esHeader ? "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5; -fx-alignment: center;"
                : "-fx-text-fill: #2c3e50; -fx-padding: 5; -fx-alignment: center;");
        return celda;
    }

    private HBox crearFilaTabla(String fecha, String boletos, String ingresos) {
        HBox fila = new HBox();
        fila.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");

        Label celdaFecha = crearCeldaTabla(fecha, false);
        Label celdaBoletos = crearCeldaTabla(boletos, false);
        Label celdaIngresos = crearCeldaTabla(ingresos, false);

        fila.getChildren().addAll(celdaFecha, celdaBoletos, celdaIngresos);
        return fila;
    }

    /**
     * Agrega un nuevo reporte a la tabla con la frecuencia seleccionada
     */
    private void agregarReporteATabla(String fechaEjecucion) {
        String frecuencia = choiceFrecuencia.getValue();
        LocalDateTime fecha = LocalDateTime.parse(fechaEjecucion);

        ReporteGenerado nuevoReporte = new ReporteGenerado(
            0,
            "Reporte de Ventas - " + frecuencia,
            "Programado",
            fecha,
            "/reportes/ventas_" + frecuencia.toLowerCase() + "_" + fechaEjecucion.replace("-", "_") + ".pdf",
            frecuencia);

        schedulerService.getReportesPendientes().add(nuevoReporte);

        choiceFrecuencia.setValue("Seleccione la Ejecucion");
        choiceFrecuencia.setDisable(true); // <-- Deshabilita el ChoiceBox

        System.out.println("Reporte programado con frecuencia " + frecuencia + " para " + fechaEjecucion);
    }

    /**
     * Elimina un reporte programado de la tabla
     */
    public void eliminarReporteProgramado(ReporteGenerado reporte) {
        if (reporte == null) {
            mostrarAlerta("Error", "No se pudo identificar el reporte a eliminar.");
            return;
        }

        // Mostrar confirmación antes de eliminar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro que desea eliminar el reporte '" +
                reporte.getNombre() + "'?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Eliminar el reporte de la tabla
            tablaReportesGenerados.getItems().remove(reporte);
            mostrarAlerta("Reporte Eliminado", "El reporte ha sido eliminado exitosamente.");
        }
    }

    // Método para mostrar vista previa del reporte
    private void mostrarVistaPrevia(ReporteGenerado reporte) {
        try {
            Stage ventanaPrevia = new Stage();
            ventanaPrevia.setTitle("Vista Previa PDF - " + reporte.getNombre());
            ventanaPrevia.setResizable(true);

            VBox contenido = new VBox(15);
            contenido.setPadding(new Insets(20));
            contenido.setStyle("-fx-background-color: #f5f5f5;");

            // Header del reporte
            VBox headerBox = new VBox(10);
            headerBox.setStyle(
                    "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1px; -fx-padding: 20; -fx-border-radius: 5px;");

            Label titulo = new Label("REPORTE DE VENTAS - CINEMAX");
            titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            Label fechaGen = new Label("Fecha de Generación: "
                    + reporte.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            fechaGen.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

            Label estado = new Label("Estado: " + reporte.getEstado());
            estado.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");

            headerBox.getChildren().addAll(titulo, fechaGen, estado);

            // Contenido del reporte
            VBox contenidoReporte = generarContenidoReporte(reporte);

            // Nota sobre PDF
            Label notaPDF = new Label(
                    "📄 Nota: Al descargar se generará un archivo PDF con este contenido y formato profesional.");
            notaPDF.setStyle(
                    "-fx-font-size: 11px; -fx-text-fill: #e67e22; -fx-font-style: italic; -fx-background-color: #fdf2e9; -fx-padding: 10; -fx-border-radius: 5px;");
            notaPDF.setWrapText(true);

            // Botones
            HBox botonesBox = new HBox(10);
            botonesBox.setAlignment(Pos.CENTER);

            Button btnDescargarPDF = new Button("📄 Descargar como PDF");
            btnDescargarPDF.setStyle(
                    "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
            btnDescargarPDF.setOnAction(e -> {
                ventanaPrevia.close();
                descargarReportePDF(reporte);
            });

            Button btnEliminar = new Button("🗑️ Eliminar Reporte");
            btnEliminar.setStyle(
                    "-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
            btnEliminar.setOnAction(e -> {
                ventanaPrevia.close();
                eliminarReporteProgramado(reporte);
            });

            Button btnCerrar = new Button("Cerrar");
            btnCerrar.setStyle(
                    "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
            btnCerrar.setOnAction(e -> ventanaPrevia.close());

            botonesBox.getChildren().addAll(btnDescargarPDF, btnEliminar, btnCerrar);

            ScrollPane scrollPane = new ScrollPane();
            VBox contenidoCompleto = new VBox(15);
            contenidoCompleto.getChildren().addAll(headerBox, contenidoReporte, notaPDF);
            scrollPane.setContent(contenidoCompleto);
            scrollPane.setFitToWidth(true);

            contenido.getChildren().addAll(scrollPane, botonesBox);

            Scene escena = new Scene(contenido, 650, 550);
            ventanaPrevia.setScene(escena);
            ventanaPrevia.initModality(Modality.APPLICATION_MODAL);
            ventanaPrevia.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo mostrar la vista previa del reporte.");
        }
    }

    // Método auxiliar para generar contenido visual del reporte
    private VBox generarContenidoReporte(ReporteGenerado reporte) {
        VBox contenido = new VBox(15);
        contenido.setStyle(
                "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1px; -fx-padding: 20; -fx-border-radius: 5px;");

        Label tituloSeccion = new Label("📊 RESUMEN DE VENTAS");
        tituloSeccion.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        VBox tablaDatos = new VBox(5);
        tablaDatos.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 1px; -fx-padding: 10;");

        // Headers
        HBox headerTabla = new HBox();
        headerTabla.setStyle("-fx-background-color: #3498db; -fx-padding: 8;");
        headerTabla.getChildren().addAll(
                crearCeldaTabla("Fecha", true),
                crearCeldaTabla("Boletos Vendidos", true),
                crearCeldaTabla("Ingresos", true));

        // Datos
        VBox filasDatos = new VBox(2);
        filasDatos.getChildren().addAll(
                crearFilaTabla(reporte.getFechaGeneracion().toString(), "125", "$3,750.00"),
                crearFilaTabla(reporte.getFechaGeneracion().minusDays(1).toString(), "98", "$2,940.00"),
                crearFilaTabla(reporte.getFechaGeneracion().minusDays(2).toString(), "156", "$4,680.00"),
                crearFilaTabla(reporte.getFechaGeneracion().minusDays(3).toString(), "87", "$2,610.00"));

        // Total
        HBox totalRow = new HBox();
        totalRow.setStyle("-fx-background-color: #2ecc71; -fx-padding: 8;");
        totalRow.getChildren().addAll(
                crearCeldaTabla("TOTAL:", true),
                crearCeldaTabla("466", true),
                crearCeldaTabla("$13,980.00", true));

        tablaDatos.getChildren().addAll(headerTabla, filasDatos, totalRow);
        contenido.getChildren().addAll(tituloSeccion, tablaDatos);

        return contenido;
    }

    // Método para descargar el reporte como PDF usando Apache PDFBox
    private void descargarReportePDF(ReporteGenerado reporte) {
        try {
            // Crear el FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte PDF");

            // Configurar el nombre por defecto del archivo
            String nombreArchivo = reporte.getNombre().replaceAll("[^a-zA-Z0-9\\s]", "_") + "_" +
                    reporte.getFechaGeneracion().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
            fileChooser.setInitialFileName(nombreArchivo + ".pdf");

            // Configurar filtro de extensión para PDF
            FileChooser.ExtensionFilter filtroPDF = new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(filtroPDF);

            // Mostrar el diálogo de guardar
            Stage stage = (Stage) tablaReportesGenerados.getScene().getWindow();
            File archivo = fileChooser.showSaveDialog(stage);

            if (archivo != null) {
                // Generar el PDF
                generarPDFConPDFBox(reporte, archivo);

                mostrarAlerta("✅ Descarga Exitosa",
                        "El reporte PDF '" + reporte.getNombre() + "' ha sido guardado exitosamente en:\n" +
                                archivo.getAbsolutePath());

            } else {
                System.out.println("Descarga cancelada por el usuario.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo descargar el reporte PDF: " + e.getMessage());
        }
    }

    // Método para generar el archivo PDF usando Apache PDFBox
    private void generarPDFConPDFBox(ReporteGenerado reporte, File archivo) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Configurar fuente
                PDType1Font fontBold = PDType1Font.HELVETICA_BOLD;
                PDType1Font fontNormal = PDType1Font.HELVETICA;

                float margin = 50;
                float yPosition = page.getMediaBox().getHeight() - margin;
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

                // Dibujar tabla manualmente
                float tableWidth = 400;
                float tableHeight = 150;
                float rowHeight = 25;

                // Definir posiciones de columnas
                float col1X = margin;
                float col2X = margin + 150;
                float col3X = margin + 250;
                float col4X = margin + 350;

                // Dibujar líneas de la tabla
                contentStream.setLineWidth(1);

                // Líneas horizontales
                for (int i = 0; i <= 6; i++) {
                    contentStream.moveTo(col1X, yPosition - (i * rowHeight));
                    contentStream.lineTo(col1X + tableWidth, yPosition - (i * rowHeight));
                    contentStream.stroke();
                }

                // Líneas verticales
                float[] colPositions = { col1X, col2X, col3X, col4X };
                for (float colX : colPositions) {
                    contentStream.moveTo(colX, yPosition);
                    contentStream.lineTo(colX, yPosition - tableHeight);
                    contentStream.stroke();
                }

                // Headers de la tabla
                contentStream.setFont(fontBold, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(col1X + 5, yPosition - 15);
                contentStream.showText("Fecha");
                contentStream.newLineAtOffset(col2X - col1X - 5, 0);
                contentStream.showText("Boletos");
                contentStream.newLineAtOffset(col3X - col2X, 0);
                contentStream.showText("Ingresos");
                contentStream.endText();

                // Datos de la tabla
                contentStream.setFont(fontNormal, 10);
                String[][] datos = {
                        { reporte.getFechaGeneracion().toString(), "125", "$3,750.00" },
                        { reporte.getFechaGeneracion().minusDays(1).toString(), "98", "$2,940.00" },
                        { reporte.getFechaGeneracion().minusDays(2).toString(), "156", "$4,680.00" },
                        { reporte.getFechaGeneracion().minusDays(3).toString(), "87", "$2,610.00" }
                };

                for (int i = 0; i < datos.length; i++) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(col1X + 5, yPosition - 40 - (i * rowHeight));
                    contentStream.showText(datos[i][0]);
                    contentStream.newLineAtOffset(col2X - col1X - 5, 0);
                    contentStream.showText(datos[i][1]);
                    contentStream.newLineAtOffset(col3X - col2X, 0);
                    contentStream.showText(datos[i][2]);
                    contentStream.endText();
                }

                // Fila de total
                contentStream.setFont(fontBold, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(col1X + 5, yPosition - 140);
                contentStream.showText("TOTAL:");
                contentStream.newLineAtOffset(col2X - col1X - 5, 0);
                contentStream.showText("466");
                contentStream.newLineAtOffset(col3X - col2X, 0);
                contentStream.showText("$13,980.00");
                contentStream.endText();

                // Información adicional
                yPosition -= 200;
                contentStream.setFont(fontBold, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Observaciones:");
                contentStream.endText();

                yPosition -= 20;
                contentStream.setFont(fontNormal, 10);
                String[] observaciones = {
                        "• Este reporte muestra las ventas de boletos de cine en el periodo especificado.",
                        "• Los ingresos estan calculados en base al precio promedio de $30.00 por boleto.",
                        "• Reporte generado automaticamente por el sistema CineMax."
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
                contentStream.showText("© 2024 CineMax - Sistema de Gestion de Reportes");
                contentStream.endText();
            }

            // Guardar el documento
            document.save(archivo);
            System.out.println("PDF generado exitosamente con PDFBox: " + archivo.getAbsolutePath());
        }
    }

    @FXML
    void goToReportesPrincipal(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/reportes/PantallaModuloReportesPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @FXML
    void onCerrarSesion(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaLogin.fxml"));
        try {
            Parent root = loader.load();

            // Obtener el Stage actual desde el botón o cualquier nodo
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Portal del Administrador");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
