package com.cinemax.reportes.controladores;
import com.cinemax.comun.ManejadorMetodosComunes;

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
import java.util.Map;
import java.util.Optional;

import com.cinemax.reportes.modelos.Export;
import com.cinemax.reportes.modelos.ExportarCSVStrategy;
import com.cinemax.reportes.modelos.ExportarPDFStrategy;
import com.cinemax.reportes.modelos.ReporteGenerado;
import com.cinemax.reportes.modelos.persistencia.VentaDAO;
import com.cinemax.reportes.servicios.ReportesSchedulerService;
import com.cinemax.reportes.servicios.VentasService;

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

    private VentasService ventasService = new VentasService();
    private Map<String, Object> datos = ventasService.getResumenDeVentas();;

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
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaFecha.setCellValueFactory(new PropertyValueFactory<>("fechaGeneracion"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Columna de acciones con botones
        columnaAcciones.setCellFactory(columna -> new TableCell<>() {
            private final Button btnVerPDF = new Button("Ver");
            private final Button btnDescargarPDF = new Button("Descargar PDF");
            private final Button btnDescargarCSV = new Button("Descargar CSV");
            private final HBox pane = new HBox(5, btnVerPDF, btnDescargarPDF, btnDescargarCSV);

            {
                // Configurar estilos de botones
                btnVerPDF.getStyleClass().add("btn-small");
                btnDescargarPDF.getStyleClass().add("btn-small");
                btnDescargarCSV.getStyleClass().add("btn-small");

                // Configurar acciones
                btnVerPDF.setOnAction(event -> {
                    ReporteGenerado reporte = getTableView().getItems().get(getIndex());
                    mostrarVistaPrevia(reporte);
                });

                btnDescargarPDF.setOnAction(event -> {
                    ReporteGenerado reporte = getTableView().getItems().get(getIndex());
                    descargarReporte(reporte, "pdf");
                });

                btnDescargarCSV.setOnAction(event -> {
                    ReporteGenerado reporte = getTableView().getItems().get(getIndex());
                    descargarReporte(reporte, "csv");
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

    @FXML
    void confirmarReporteProgramado(ActionEvent event) {
        // Validar que se haya seleccionado una frecuencia
        if (choiceFrecuencia.getValue() == null || choiceFrecuencia.getValue().equals("Seleccione la Ejecucion")) {
            ManejadorMetodosComunes.mostrarVentanaError("Debe seleccionar una frecuencia de ejecución.");
            return;
        }

        // Verificar si ya existe un reporte con la misma frecuencia
        String frecuenciaSeleccionada = choiceFrecuencia.getValue();
        if (existeReporteConFrecuencia(frecuenciaSeleccionada)) {
            ManejadorMetodosComunes.mostrarVentanaError("Ya existe un reporte programado con frecuencia " + frecuenciaSeleccionada + ".\n" +
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
            Stage ventanaPrevia = new Stage();
            ventanaPrevia.setTitle("Previsualización del Reporte PDF");
            ventanaPrevia.setResizable(true);

            // Fondo negro del sistema
            VBox contenidoPrincipal = new VBox(10);
            contenidoPrincipal.setPadding(new Insets(15));
            contenidoPrincipal.getStyleClass().add("root");

            // Header con información del reporte
            VBox headerBox = new VBox(5);
            headerBox.getStyleClass().add("content-pane");

            Label tituloReporte = new Label("EJEMPLO DE PROGRAMACION - CINEMAX");
            tituloReporte.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

            Label lblFechaGeneracion = new Label("Se ha agendado fecha de creacion");
            lblFechaGeneracion.setStyle("-fx-font-size: 12px; -fx-text-fill: #b2bec3;");

            Label lblFrecuencia = new Label("Frecuencia: " + choiceFrecuencia.getValue());
            lblFrecuencia.setStyle("-fx-font-size: 12px; -fx-text-fill: #b2bec3;");

            headerBox.getChildren().addAll(tituloReporte, lblFechaGeneracion, lblFrecuencia);

            // Contenido del reporte (simulado)
            VBox contenidoReporte = new VBox(10);
            contenidoReporte.getStyleClass().add("content-pane");

            Label tituloSeccion = new Label("📊 RESUMEN DE VENTAS");
            tituloSeccion.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

            VBox tablaDatos = new VBox(5);
            tablaDatos.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 1px; -fx-padding: 10;");

            HBox headerTabla = new HBox();
            headerTabla.setStyle("-fx-background-color: #3498db; -fx-padding: 8;");
            Label colFecha = crearCeldaTabla("Fecha", true);
            Label colBoletos = crearCeldaTabla("Boletos Vendidos", true);
            Label colIngresos = crearCeldaTabla("Ingresos", true);
            headerTabla.getChildren().addAll(colFecha, colBoletos, colIngresos);

            VBox filasDatos = new VBox(2);

            String frecuenciaSeleccionada = choiceFrecuencia.getValue();
            String fechaEjecucion = schedulerService.calcularProximaEjecucion(LocalDateTime.now().toString(),
                    frecuenciaSeleccionada);

            filasDatos.getChildren().addAll(
                    crearFilaTabla(fechaEjecucion, "25", "$1,250.00"));

            HBox totalRow = new HBox();
            totalRow.setStyle("-fx-background-color: #2ecc71; -fx-padding: 8;");
            Label totalLabel = crearCeldaTabla("TOTAL:", true);
            Label totalBoletos = crearCeldaTabla("125", true);
            Label totalIngresos = crearCeldaTabla("$1,250.00", true);
            totalRow.getChildren().addAll(totalLabel, totalBoletos, totalIngresos);

            tablaDatos.getChildren().addAll(headerTabla, filasDatos, totalRow);

            VBox infoAdicional = new VBox(5);
            infoAdicional.getStyleClass().add("content-pane");

            // Agregar la tabla de películas a la previsualización reutilizando las
            // variables ya declaradas arriba
            contenidoReporte.getChildren().addAll(tituloSeccion, tablaDatos);

            // Tabla de películas
            Label tituloPeliculas = new Label("🎬 RESUMEN POR PELÍCULA");
            tituloPeliculas.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

            VBox tablaPeliculas = new VBox(2);
            tablaPeliculas.setStyle(
                    "-fx-border-color: #ecf0f1; -fx-border-width: 1px; -fx-padding: 10; -fx-background-radius: 5px;");

            contenidoReporte.getChildren().addAll(tituloPeliculas, tablaPeliculas);

            // Encabezados
            HBox headerPeliculas = new HBox();
            headerPeliculas.setStyle("-fx-background-color: #8e44ad; -fx-padding: 8;");
            headerPeliculas.getChildren().addAll(
                    crearCeldaTabla("Título", true),
                    crearCeldaTabla("Funciones", true),
                    crearCeldaTabla("Boletos Vendidos", true),
                    crearCeldaTabla("Ingresos", true));

            // Datos ficticios
            String[][] peliculas = {
                    { "Barbie", "3", "320", "$9,600.00" },
                    { "Oppenheimer", "2", "210", "$6,300.00" },
                    { "Intensamente 2", "2", "180", "$5,400.00" },
                    { "Garfield", "1", "80", "$2,400.00" }
            };

            VBox filasPeliculas = new VBox(2);
            for (String[] fila : peliculas) {
                HBox filaPelicula = new HBox();
                for (String celda : fila) {
                    Label lbl = crearCeldaTabla(celda, false);
                    lbl.setStyle("-fx-text-fill: #ecf0f1; -fx-padding: 5; -fx-alignment: center;");
                    filaPelicula.getChildren().add(lbl);
                }
                filasPeliculas.getChildren().add(filaPelicula);
            }

            tablaPeliculas.getChildren().addAll(headerPeliculas, filasPeliculas);

            Label notaInfo = new Label(
                    "📝 Nota: Este es un ejemplo de cómo se verá el reporte cuando se genere automáticamente.");
            notaInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #b2bec3; -fx-font-style: italic;");
            notaInfo.setWrapText(true);

            Label proximaGeneracion = new Label("⏰ Próxima generación programada: " + fechaEjecucion);
            proximaGeneracion.setStyle("-fx-font-size: 11px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");

            infoAdicional.getChildren().addAll(notaInfo, proximaGeneracion);

            HBox botonesBox = new HBox(15);
            botonesBox.setAlignment(Pos.CENTER);
            botonesBox.setPadding(new Insets(15, 0, 5, 0));

            Button btnConfirmar = new Button("✅ Programar Reporte");
            btnConfirmar.getStyleClass().add("btn-small");
            btnConfirmar.setOnAction(e -> {
                agregarReporteATabla(fechaEjecucion);
                String mensaje = "✅ Reporte Programado\n"
                        + "El reporte ha sido programado exitosamente.\n"
                        + "Se ejecutará: " + frecuenciaSeleccionada;

                ManejadorMetodosComunes.mostrarVentanaAdvertencia(mensaje);

                ventanaPrevia.close();
            });

            Button btnCancelar = new Button("❌ Cancelar");
            btnCancelar.getStyleClass().add("btn-small");
            btnCancelar.setOnAction(e -> ventanaPrevia.close());

            botonesBox.getChildren().addAll(btnConfirmar, btnCancelar);

            ScrollPane scrollPane = new ScrollPane();
            VBox contenidoCompleto = new VBox(10);
            contenidoCompleto.getChildren().addAll(headerBox, contenidoReporte, infoAdicional);
            scrollPane.setContent(contenidoCompleto);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent;");

            contenidoPrincipal.getChildren().addAll(scrollPane, botonesBox);

            Scene escena = new Scene(contenidoPrincipal, 600, 500);
            escena.getStylesheets().add(getClass().getResource("/vistas/temas/styles.css").toExternalForm());
            ventanaPrevia.setScene(escena);
            ventanaPrevia.initModality(Modality.APPLICATION_MODAL);
            ventanaPrevia.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo mostrar la previsualización del reporte.");
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
                "Reporte de Ventas - " + frecuencia,
                "Programado",
                fecha,
                "/reportes/ventas_" + frecuencia.toLowerCase() + "_" + fechaEjecucion.replace("-", "_") + ".pdf",
                frecuencia);

        schedulerService.getReportesPendientes().add(nuevoReporte);

        choiceFrecuencia.setValue("Seleccione la Ejecucion");
        choiceFrecuencia.setDisable(true); // <-- Deshabilita el ChoiceBox
    }

    /**
     * Elimina un reporte programado de la tabla
     */
    public void eliminarReporteProgramado(ReporteGenerado reporte) {
        if (reporte == null) {
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo identificar el reporte a eliminar.");
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
            ManejadorMetodosComunes.mostrarVentanaExito("El reporte ha sido eliminado exitosamente.");
        }
    }

    // Método para mostrar vista previa del reporte (BOTON)
    private void mostrarVistaPrevia(ReporteGenerado reporte) {
        try {
            Stage ventanaPrevia = new Stage();
            ventanaPrevia.setTitle("Vista Previa PDF - " + reporte.getNombre());
            ventanaPrevia.setResizable(true);

            VBox contenido = new VBox(15);
            contenido.setPadding(new Insets(20));
            contenido.getStyleClass().add("root"); // Fondo oscuro del sistema

            // Header del reporte
            VBox headerBox = new VBox(10);
            headerBox.getStyleClass().add("content-pane");

            Label titulo = new Label("EJEMPLO DE PROGRAMACION - CINEMAX");
            titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

            Label fechaGen = new Label("Fecha de Generación: "
                    + reporte.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            fechaGen.setStyle("-fx-font-size: 12px; -fx-text-fill: #b2bec3;");

            Label frecuencia = new Label("Frecuencia: " + reporte.getFrecuencia());
            frecuencia.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");

            headerBox.getChildren().addAll(titulo, fechaGen, frecuencia);

            // Contenido del reporte
            VBox contenidoReporte = generarContenidoReporte(reporte);

            // Nota sobre PDF
            Label notaPDF = new Label(
                    "📄 Nota: Al descargar se generará un archivo PDF con este contenido y formato profesional.");
            notaPDF.setStyle(
                    "-fx-font-size: 11px; -fx-text-fill: #e67e22; -fx-font-style: italic; -fx-background-color: #232323; -fx-padding: 10; -fx-border-radius: 5px;");
            notaPDF.setWrapText(true);

            // Botones
            HBox botonesBox = new HBox(10);
            botonesBox.setAlignment(Pos.CENTER);

            Button btnDescargarPDF = new Button("📄 Descargar como PDF");
            btnDescargarPDF.setStyle(
                    "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
            btnDescargarPDF.setOnAction(e -> {
                ventanaPrevia.close();
                descargarReporte(reporte, "pdf");
            });

            Button btnDescargarCSV = new Button("🗎 Descargar como CSV");
            btnDescargarCSV.setStyle(
                    "-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
            btnDescargarCSV.setOnAction(e -> {
                ventanaPrevia.close();
                descargarReporte(reporte, "csv");
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
            escena.getStylesheets().add(getClass().getResource("/vistas/temas/styles.css").toExternalForm());
            ventanaPrevia.setScene(escena);
            ventanaPrevia.initModality(Modality.APPLICATION_MODAL);
            ventanaPrevia.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo mostrar la vista previa del reporte.");
        }
    }

    // Método auxiliar para generar contenido visual del reporte
    private VBox generarContenidoReporte(ReporteGenerado reporte) {
        VBox contenido = new VBox(15);
        contenido.getStyleClass().add("content-pane"); // Fondo secundario oscuro

        Label tituloSeccion = new Label("📊 EJEMPLO DE RESUMEN DE VENTAS RECOPILADO");
        tituloSeccion.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

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
                crearFilaTabla(reporte.getFechaGeneracion().toString(), "$125", "$270.00"));

        // Total
        HBox totalRow = new HBox();
        totalRow.setStyle("-fx-background-color: #2ecc71; -fx-padding: 8;");
        totalRow.getChildren().addAll(
                crearCeldaTabla("TOTAL:", true),
                crearCeldaTabla("466", true),
                crearCeldaTabla("$13,980.00", true));

        // Tabla de películas
        Label tituloPeliculas = new Label("🎬 RESUMEN POR PELÍCULA");
        tituloPeliculas.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

        VBox tablaPeliculas = new VBox(2);
        tablaPeliculas.setStyle(
            "-fx-border-color: #ecf0f1; -fx-border-width: 1px; -fx-padding: 10; -fx-background-radius: 5px;");

        // Encabezados
        HBox headerPeliculas = new HBox();
        headerPeliculas.setStyle("-fx-background-color: #8e44ad; -fx-padding: 8;");
        headerPeliculas.getChildren().addAll(
            crearCeldaTabla("Título", true),
            crearCeldaTabla("Funciones", true),
            crearCeldaTabla("Boletos Vendidos", true),
            crearCeldaTabla("Ingresos", true));

        // Datos ficticios
        String[][] peliculas = {
            { "Barbie", "3", "320", "$9,600.00" },
            { "Oppenheimer", "2", "210", "$6,300.00" },
            { "Intensamente 2", "2", "180", "$5,400.00" },
            { "Garfield", "1", "80", "$2,400.00" }
        };

        VBox filasPeliculas = new VBox(2);
        for (String[] fila : peliculas) {
            HBox filaPelicula = new HBox();
            for (String celda : fila) {
            Label lbl = crearCeldaTabla(celda, false);
            lbl.setStyle("-fx-text-fill: #ecf0f1; -fx-padding: 5; -fx-alignment: center;");
            filaPelicula.getChildren().add(lbl);
            }
            filasPeliculas.getChildren().add(filaPelicula);
        }

        
        
        
        tablaDatos.getChildren().addAll(headerTabla, filasDatos, totalRow);
        contenido.getChildren().addAll(tituloSeccion, tablaDatos);
        
        
        contenido.getChildren().addAll(tituloPeliculas, tablaPeliculas);
        tablaPeliculas.getChildren().addAll(headerPeliculas, filasPeliculas);
        return contenido;
    }

    private void descargarReporte(ReporteGenerado reporte, String formato) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte " + formato.toUpperCase());

            String extension = formato.equalsIgnoreCase("pdf") ? ".pdf" : ".csv";
            String nombreArchivo = reporte.getNombre().replaceAll("[^a-zA-Z0-9\\s]", "_") + "_" +
                    reporte.getFechaGeneracion().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
            fileChooser.setInitialFileName(nombreArchivo + extension);

            if (formato.equalsIgnoreCase("pdf")) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
            } else if (formato.equalsIgnoreCase("csv")) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));
            }

            Stage stage = (Stage) tablaReportesGenerados.getScene().getWindow();
            File archivo = fileChooser.showSaveDialog(stage);

            if (archivo != null) {
                Export exportStrategy;
                if (formato.equalsIgnoreCase("pdf")) {
                    exportStrategy = new ExportarPDFStrategy();
                } else if (formato.equalsIgnoreCase("csv")) {
                    exportStrategy = new ExportarCSVStrategy();
                } else {
                    ManejadorMetodosComunes.mostrarVentanaError("Formato de exportación no soportado.");
                    return;
                }
                // TODO: Aqui exportar los datos del reporte
                exportStrategy.exportar(reporte, archivo, datos);

                String mensaje = "✅ Descarga Exitosa\n"
                        + "El reporte '" + reporte.getNombre() + "' se ha sido registrado exitosamente\n";

                ManejadorMetodosComunes.mostrarVentanaExito(mensaje);
            } else {
                System.out.println("Descarga cancelada por el usuario.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo descargar el reporte: " + e.getMessage());
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
