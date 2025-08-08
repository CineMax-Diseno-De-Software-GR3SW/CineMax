package com.cinemax.reportes.controladores;

import com.cinemax.comun.ManejadorMetodosComunes;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.cinemax.reportes.modelos.Export;
import com.cinemax.reportes.modelos.ExportarCSVStrategy;
import com.cinemax.reportes.modelos.ExportarPDFStrategy;
import com.cinemax.reportes.modelos.ReporteGenerado;
import com.cinemax.reportes.modelos.ReporteVentaDTO;
import com.cinemax.reportes.servicios.ReportesSchedulerService;
import com.cinemax.reportes.servicios.VentasService;

public class ControladorReportesProgramados {

    @FXML
    private Button btnBack;

    @FXML
    private ChoiceBox<String> choiceFrecuencia;

    @FXML
    private DatePicker dateDesde;

    @FXML
    private DatePicker dateHasta;

    @FXML
    private ChoiceBox<String> choiceSala;

    @FXML
    private ChoiceBox<String> choiceTipoBoleto;

    @FXML
    private ChoiceBox<String> choiceHorario;

    @FXML
    private TableView<ReporteGenerado> tablaReportesGenerados;

    @FXML
    private TableColumn<ReporteGenerado, String> columnaNombre;

    @FXML
    private TableColumn<ReporteGenerado, String> columnaEstado;

    @FXML
    private TableColumn<ReporteGenerado, LocalDateTime> columnaFecha;

    @FXML
    private TableColumn<ReporteGenerado, Void> columnaAcciones;

    @FXML
    private Button btnProgramar;

    // Singleton service
    private final ReportesSchedulerService schedulerService = ReportesSchedulerService.getInstance();
    private final VentasService ventasService = new VentasService();

    @FXML
    private void initialize() {
        // Inicializar la tabla de reportes generados
        if (tablaReportesGenerados != null) {
            inicializarTablaReportes();
        }

        // Opciones para la frecuencia del reporte
        ObservableList<String> opcionesFrecuencia = FXCollections.observableArrayList(
                "Diario", "Semanal", "Mensual", "Trimestral", "Anual");
        choiceFrecuencia.setItems(opcionesFrecuencia);
        choiceFrecuencia.setValue("Seleccione la Frecuencia");

        // Configurar filtros adicionales
        inicializarFiltros();

        // Configurar fechas por defecto
        dateDesde.setValue(LocalDate.now().minusDays(7));
        dateHasta.setValue(LocalDate.now());

        // Listener para detectar cambios en la frecuencia
        choiceFrecuencia.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Frecuencia seleccionada: " + newValue);
            if (newValue != null && !newValue.equals("Seleccione la Frecuencia")) {
                btnProgramar.setDisable(false);
            }
        });

        // Iniciar el programador de tareas
        schedulerService.iniciarScheduler();

        // Detener el scheduler al cerrar la ventana
        Platform.runLater(() -> {
            Stage stage = (Stage) btnBack.getScene().getWindow();
            if (stage != null) {
                stage.setOnCloseRequest(event -> schedulerService.detenerScheduler());
            }
        });

        // Obtener datos reales del resumen para mostrar en consola
        Map<String, Object> datosReales = ventasService.getResumenDeVentas();
        System.out.println("=== DATOS REALES DISPONIBLES PARA REPORTES PROGRAMADOS ===");
        System.out.println("Total boletos: " + datosReales.get("total_boletos_vendidos"));
        System.out.println("Total facturas: " + datosReales.get("total_facturas"));
        System.out.println("Ingreso total: " + datosReales.get("ingreso_total"));
    }

    private void inicializarFiltros() {
        // Opciones de horario
        choiceHorario.getItems().addAll("Todos", "Matutino", "Nocturno");
        choiceHorario.setValue("Todos");

        // Opciones de tipo de boleto
        choiceTipoBoleto.getItems().addAll("Todos", "VIP", "Normal");
        choiceTipoBoleto.setValue("Todos");

        // Cargar salas desde la base de datos
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
        }
    }

    private void inicializarTablaReportes() {
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        columnaFecha.setCellValueFactory(new PropertyValueFactory<>("fechaGeneracion"));

        // Configurar columna de acciones
        columnaAcciones.setCellFactory(col -> new TableCell<ReporteGenerado, Void>() {
            private final Button btnDescargarPDF = new Button("📄 PDF");
            private final Button btnDescargarCSV = new Button("📊 CSV");
            private final Button btnEliminar = new Button("🗑");
            private final HBox buttons = new HBox(5);

            {
                // Configurar estilos de botones
                btnDescargarPDF.getStyleClass().add("btn-accion-descargar");
                btnDescargarCSV.getStyleClass().add("btn-accion-descargar");
                btnEliminar.getStyleClass().add("btn-accion-eliminar");
                buttons.setAlignment(Pos.CENTER);
                buttons.getChildren().addAll(btnDescargarPDF, btnDescargarCSV, btnEliminar);

                btnDescargarPDF.setOnAction(e -> {
                    ReporteGenerado reporte = getTableView().getItems().get(getIndex());
                    descargarReporte(reporte, "PDF");
                });

                btnDescargarCSV.setOnAction(e -> {
                    ReporteGenerado reporte = getTableView().getItems().get(getIndex());
                    descargarReporte(reporte, "CSV");
                });

                btnEliminar.setOnAction(e -> {
                    ReporteGenerado reporte = getTableView().getItems().get(getIndex());
                    eliminarReporte(reporte);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });

        tablaReportesGenerados.setItems(schedulerService.getReportesEjecutados());
    }

    @FXML
    private void onProgramarReporte(ActionEvent event) {
        String frecuencia = choiceFrecuencia.getValue();
        LocalDate desde = dateDesde.getValue();
        LocalDate hasta = dateHasta.getValue();

        if (frecuencia == null || frecuencia.equals("Seleccione la Frecuencia")) {
            ManejadorMetodosComunes.mostrarVentanaError("Por favor seleccione una frecuencia");
            return;
        }

        if (desde == null || hasta == null) {
            ManejadorMetodosComunes.mostrarVentanaError("Por favor seleccione las fechas de inicio y fin");
            return;
        }

        // Validar que las fechas sean lógicas
        if (desde.isAfter(hasta)) {
            ManejadorMetodosComunes.mostrarVentanaError("La fecha de inicio no puede ser posterior a la fecha de fin");
            return;
        }
        
        // Verificar si ya existe un reporte con la misma frecuencia
        if (existeReporteConFrecuencia(frecuencia)) {
            ManejadorMetodosComunes.mostrarVentanaError("Ya existe un reporte programado con frecuencia " + frecuencia + ".\n" +
                    "Solo puede haber una ejecución por cada tipo de frecuencia.");
            return;
        }


        // Mostrar ventana de previsualización con datos reales
        mostrarVentanaPrevia();
    }

    /**
     * Verifica si ya existe un reporte programado con la misma frecuencia.
     */
    private boolean existeReporteConFrecuencia(String frecuencia) {
        // Revisar en la tabla de ejecutados
        ObservableList<ReporteGenerado> reportesEjecutados = tablaReportesGenerados.getItems();
        for (ReporteGenerado reporte : reportesEjecutados) {
            if (frecuencia.equals(reporte.getFrecuencia())) {
                return true;
            }
        }
        // Revisar en la lista de pendientes del scheduler
        for (ReporteGenerado reporte : schedulerService.getReportesPendientes()) {
            if (frecuencia.equals(reporte.getFrecuencia())) {
                return true;
            }
        }
        return false;
    }

    private void mostrarVentanaPrevia() {
        try {
            Stage ventanaPrevia = new Stage();
            ventanaPrevia.setTitle("Previsualización del Reporte Programado");
            ventanaPrevia.setResizable(true);

            VBox contenidoPrincipal = new VBox(10);
            contenidoPrincipal.setPadding(new Insets(15));
            contenidoPrincipal.setStyle("-fx-background-color: #2B2B2B;");

            // Header con información del reporte
            VBox headerBox = new VBox(5);
            headerBox.setStyle("-fx-background-color: #34495e; -fx-padding: 15; -fx-border-radius: 5px;");

            Label tituloReporte = new Label("REPORTE PROGRAMADO - CINEMAX (DATOS REALES)");
            tituloReporte.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

            String frecuencia = choiceFrecuencia.getValue();
            LocalDate desde = dateDesde.getValue();
            LocalDate hasta = dateHasta.getValue();
            String sala = choiceSala.getValue();
            String tipoBoleto = choiceTipoBoleto.getValue();
            String horario = choiceHorario.getValue();

            Label lblConfig = new Label("Configuración:");
            lblConfig.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3498db;");

            Label lblFrecuencia = new Label("• Frecuencia: " + frecuencia);
            lblFrecuencia.setStyle("-fx-font-size: 12px; -fx-text-fill: #ecf0f1;");

            Label lblPeriodo = new Label("• Período: " + desde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                                         " - " + hasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            lblPeriodo.setStyle("-fx-font-size: 12px; -fx-text-fill: #ecf0f1;");

            Label lblFiltros = new Label("• Filtros: Sala=" + sala + ", Tipo=" + tipoBoleto + ", Horario=" + horario);
            lblFiltros.setStyle("-fx-font-size: 12px; -fx-text-fill: #ecf0f1;");

            headerBox.getChildren().addAll(tituloReporte, lblConfig, lblFrecuencia, lblPeriodo, lblFiltros);

            // Contenido del reporte con datos reales
            VBox contenidoReporte = generarContenidoReporteConDatosReales();

            VBox infoAdicional = new VBox(5);
            infoAdicional.setStyle("-fx-background-color: #2c3e50; -fx-padding: 10; -fx-border-radius: 5px;");

            Label notaInfo = new Label(
                    "📝 Nota: Este reporte será generado automáticamente con datos reales de la base de datos PostgreSQL según la frecuencia seleccionada.");
            notaInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #e67e22; -fx-font-style: italic;");
            notaInfo.setWrapText(true);

            String proximaEjecucion = schedulerService.calcularProximaEjecucion(
                    LocalDateTime.now().toString(), frecuencia);
            Label proximaGeneracion = new Label("⏰ Próxima generación programada: " +
                    LocalDateTime.parse(proximaEjecucion).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            proximaGeneracion.setStyle("-fx-font-size: 11px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");

            infoAdicional.getChildren().addAll(notaInfo, proximaGeneracion);

            HBox botonesBox = new HBox(15);
            botonesBox.setAlignment(Pos.CENTER);
            botonesBox.setPadding(new Insets(15, 0, 5, 0));

            Button btnConfirmar = new Button("✅ Programar Reporte");
            btnConfirmar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
            btnConfirmar.setOnAction(e -> {
                confirmarProgramacion(proximaEjecucion);
                ventanaPrevia.close();
            });

            Button btnCancelar = new Button("❌ Cancelar");
            btnCancelar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
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
            ventanaPrevia.setScene(escena);
            ventanaPrevia.initModality(Modality.APPLICATION_MODAL);
            ventanaPrevia.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo mostrar la previsualización del reporte.");
        }
    }

    private VBox generarContenidoReporteConDatosReales() {
        VBox contenido = new VBox(10);
        contenido.setStyle("-fx-background-color: #34495e; -fx-padding: 15; -fx-border-radius: 5px;");

        Label tituloSeccion = new Label("📊 RESUMEN DE VENTAS (DATOS REALES)");
        tituloSeccion.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

        try {
            // Obtener datos reales con los filtros seleccionados
            LocalDate desde = dateDesde.getValue();
            LocalDate hasta = dateHasta.getValue();
            String sala = choiceSala.getValue();
            String tipoBoleto = choiceTipoBoleto.getValue();
            String horario = choiceHorario.getValue();

            List<ReporteVentaDTO> datosReales = ventasService.getVentasFiltradas(desde, hasta, sala, tipoBoleto, horario);
            
            VBox tablaDatos = new VBox(5);
            tablaDatos.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 1px; -fx-padding: 10;");

            // Headers
            HBox headerTabla = new HBox();
            headerTabla.setStyle("-fx-background-color: #3498db; -fx-padding: 8;");
            headerTabla.getChildren().addAll(
                    crearCeldaTabla("Métrica", true),
                    crearCeldaTabla("Valor Real", true));

            // Calcular estadísticas de datos reales
            long totalBoletos = datosReales.stream().mapToLong(v -> v.boletosVendidos).sum();
            double totalIngresos = datosReales.stream().mapToDouble(v -> v.ingresos).sum();
            
            // Calcular boletos por tipo
            long boletosVIP = datosReales.stream()
                .filter(v -> "VIP".equals(v.tipoBoleto))
                .mapToLong(v -> v.boletosVendidos)
                .sum();
            long boletosNormal = datosReales.stream()
                .filter(v -> "Normal".equals(v.tipoBoleto))
                .mapToLong(v -> v.boletosVendidos)
                .sum();

            VBox filasDatos = new VBox(2);
            filasDatos.getChildren().addAll(
                    crearFilaTablaMetrica("Boletos Vendidos", String.valueOf(totalBoletos)),
                    crearFilaTablaMetrica("Ingresos Totales", "$" + String.format("%.2f", totalIngresos)),
                    crearFilaTablaMetrica("Boletos VIP", String.valueOf(boletosVIP)),
                    crearFilaTablaMetrica("Boletos Normal", String.valueOf(boletosNormal)),
                    crearFilaTablaMetrica("Promedio por Boleto", totalBoletos > 0 ? "$" + String.format("%.2f", totalIngresos / totalBoletos) : "$0.00")
            );

            tablaDatos.getChildren().addAll(headerTabla, filasDatos);
            contenido.getChildren().addAll(tituloSeccion, tablaDatos);

        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error al obtener datos reales: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");
            contenido.getChildren().addAll(tituloSeccion, errorLabel);
        }

        // Información adicional sobre la frecuencia
        Label infoFrecuencia = new Label("📅 Frecuencia de generación: " + choiceFrecuencia.getValue());
        infoFrecuencia.setStyle("-fx-font-size: 12px; -fx-text-fill: #e67e22; -fx-font-weight: bold;");

        Label infoBaseDatos = new Label("💾 Fuente: Base de datos PostgreSQL (datos actualizados en tiempo real)");
        infoBaseDatos.setStyle("-fx-font-size: 11px; -fx-text-fill: #27ae60; -fx-font-style: italic;");

        contenido.getChildren().addAll(infoFrecuencia, infoBaseDatos);
        return contenido;
    }

    private void confirmarProgramacion(String proximaEjecucion) {
        try {
            String frecuencia = choiceFrecuencia.getValue();
            LocalDate desde = dateDesde.getValue();
            LocalDate hasta = dateHasta.getValue();
            
            // Crear el reporte programado con configuración real
            ReporteGenerado reporteProgramado = new ReporteGenerado(
                "Reporte_Programado_" + frecuencia + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")),
                "Programado",
                LocalDateTime.parse(proximaEjecucion),
                "PDF",
                "/reportes/programados/"
            );
            
            // Establecer configuración adicional
            reporteProgramado.setFrecuencia(frecuencia);
            reporteProgramado.setConfiguracion(Map.of(
                "fecha_desde", desde.toString(),
                "fecha_hasta", hasta.toString(),
                "sala", choiceSala.getValue(),
                "tipo_boleto", choiceTipoBoleto.getValue(),
                "horario", choiceHorario.getValue()
            ));

            // Agregar a la lista de reportes programados
            schedulerService.getReportesPendientes().add(reporteProgramado);

            // Agregar también a la tabla para visualización inmediata
            schedulerService.getReportesEjecutados().add(0, reporteProgramado);

            String mensaje = "✅ Reporte Programado con Datos Reales\n" +
                    "El reporte ha sido programado exitosamente.\n" +
                    "Se ejecutará: " + frecuencia + "\n" +
                    "Próxima ejecución: " + LocalDateTime.parse(proximaEjecucion).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" +
                    "Utilizará datos actualizados de la base de datos PostgreSQL";

            ManejadorMetodosComunes.mostrarVentanaExito(mensaje);

        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("Error al programar el reporte: " + e.getMessage());
        }
    }

    private Label crearCeldaTabla(String texto, boolean esHeader) {
        Label celda = new Label(texto);
        celda.setPrefWidth(200); // Ajustado para un diseño más limpio
        celda.setMaxWidth(200);
        celda.setStyle(esHeader ? "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5; -fx-alignment: center;"
                : "-fx-text-fill: #ecf0f1; -fx-padding: 5; -fx-alignment: center-left;");
        return celda;
    }

    private HBox crearFilaTablaMetrica(String metrica, String valor) {
        HBox fila = new HBox();
        fila.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #ecf0f1; -fx-border-width: 0 0 1 0;");
        Label lblMetrica = crearCeldaTabla(metrica, false);
        lblMetrica.setStyle("-fx-font-weight: bold; -fx-text-fill: #ecf0f1; -fx-padding: 5; -fx-alignment: center-left;");
        Label lblValor = crearCeldaTabla(valor, false);
        lblValor.setStyle("-fx-text-fill: #ecf0f1; -fx-padding: 5; -fx-alignment: center;");
        fila.getChildren().addAll(lblMetrica, lblValor);
        return fila;
    }

    private void descargarReporte(ReporteGenerado reporte, String tipo) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte " + tipo);
        fileChooser.setInitialFileName(reporte.getNombre() + "." + tipo.toLowerCase());

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(tipo + " files (*." + tipo.toLowerCase() + ")", "*." + tipo.toLowerCase());
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            Export exportador;
            if (tipo.equalsIgnoreCase("PDF")) {
                exportador = new Export(new ExportarPDFStrategy());
            } else {
                exportador = new Export(new ExportarCSVStrategy());
            }
            try {
                // Generar los datos para el reporte usando los filtros guardados
                LocalDate desde = LocalDate.parse(reporte.getConfiguracion().get("fecha_desde"));
                LocalDate hasta = LocalDate.parse(reporte.getConfiguracion().get("fecha_hasta"));
                String sala = reporte.getConfiguracion().get("sala");
                String tipoBoleto = reporte.getConfiguracion().get("tipo_boleto");
                String horario = reporte.getConfiguracion().get("horario");
                
                List<ReporteVentaDTO> datosReporte = ventasService.getVentasFiltradas(desde, hasta, sala, tipoBoleto, horario);
                exportador.exportarDatos(datosReporte, file.getAbsolutePath());
                ManejadorMetodosComunes.mostrarVentanaExito("Reporte " + tipo + " guardado exitosamente en: " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                ManejadorMetodosComunes.mostrarVentanaError("Error al guardar el reporte: " + e.getMessage());
            }
        }
    }

    private void eliminarReporte(ReporteGenerado reporte) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("Eliminar Reporte");
        alert.setContentText("¿Está seguro de que desea eliminar el reporte '" + reporte.getNombre() + "'? Esta acción es irreversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            schedulerService.getReportesEjecutados().remove(reporte);
            schedulerService.getReportesPendientes().remove(reporte);
            ManejadorMetodosComunes.mostrarVentanaExito("El reporte ha sido eliminado exitosamente.");
        }
    }

    @FXML
    private void back(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/vistas/MainReportes.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}