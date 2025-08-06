package com.cinemax.reportes.controladores;
import com.cinemax.comun.ManejadorMetodosComunes;

import java.io.IOException;
import java.util.Arrays;

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
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    final ReportesSchedulerService schedulerService = ReportesSchedulerService.getInstance();
    private VentasService ventasService = new VentasService();

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

        // Detiene el scheduler al cerrar la ventana
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

            {
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
                    HBox buttons = new HBox(5);
                    buttons.getChildren().addAll(btnDescargarPDF, btnDescargarCSV, btnEliminar);
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

        // Mostrar ventana de previsualización con datos reales
        mostrarVentanaPrevia();
    }

    private void mostrarVentanaPrevia() {
        try {
            Stage ventanaPrevia = new Stage();
            ventanaPrevia.setTitle("Previsualización del Reporte Programado - DATOS REALES");
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
            int totalBoletos = datosReales.stream().mapToInt(v -> v.boletosVendidos).sum();
            double totalIngresos = datosReales.stream().mapToDouble(v -> v.ingresos).sum();
            
            // Calcular boletos por tipo
            int boletosVIP = datosReales.stream()
                .filter(v -> "VIP".equals(v.tipoBoleto))
                .mapToInt(v -> v.boletosVendidos)
                .sum();
            int boletosNormal = datosReales.stream()
                .filter(v -> "Normal".equals(v.tipoBoleto))
                .mapToInt(v -> v.boletosVendidos)
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
                // Obtener datos reales para la exportación
                LocalDate desde = LocalDate.now().minusDays(30); // Período por defecto
                LocalDate hasta = LocalDate.now();
                
                // Si el reporte tiene configuración específica, usarla
                if (reporte.getConfiguracion() != null && !reporte.getConfiguracion().isEmpty()) {
                    Map<String, Object> config = reporte.getConfiguracion();
                    desde = LocalDate.parse((String) config.getOrDefault("fecha_desde", desde.toString()));
                    hasta = LocalDate.parse((String) config.getOrDefault("fecha_hasta", hasta.toString()));
                }

                List<ReporteVentaDTO> datosReales = ventasService.getVentasFiltradas(desde, hasta, null, null, null);

                Export exportStrategy;
                if (formato.equalsIgnoreCase("pdf")) {
                    exportStrategy = new ExportarPDFStrategy();
                } else if (formato.equalsIgnoreCase("csv")) {
                    exportStrategy = new ExportarCSVStrategy();
                } else {
                    ManejadorMetodosComunes.mostrarVentanaError("Formato de exportación no soportado.");
                    return;
                }

                // Crear un mapa con los datos reales para exportar
                Map<String, Object> datosParaExportar = Map.of(
                    "datos_ventas", datosReales,
                    "total_boletos", datosReales.stream().mapToInt(v -> v.boletosVendidos).sum(),
                    "total_ingresos", datosReales.stream().mapToDouble(v -> v.ingresos).sum(),
                    "periodo", desde + " - " + hasta
                );

                // Exportar con datos reales
                exportStrategy.exportar(reporte, archivo, datosParaExportar);

                String mensaje = "✅ Descarga Exitosa con Datos Reales\n" +
                        "El reporte '" + reporte.getNombre() + "' se ha generado exitosamente\n" +
                        "Contiene datos actualizados de la base de datos PostgreSQL";

                ManejadorMetodosComunes.mostrarVentanaExito(mensaje);
            } else {
                System.out.println("Descarga cancelada por el usuario.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo descargar el reporte: " + e.getMessage());
        }
    }

    private void eliminarReporte(ReporteGenerado reporte) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar reporte?");
        confirmacion.setContentText("¿Está seguro de que desea eliminar el reporte '" + reporte.getNombre() + "'?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            schedulerService.getReportesEjecutados().remove(reporte);
            schedulerService.getReportesPendientes().remove(reporte);
            ManejadorMetodosComunes.mostrarVentanaExito("Reporte eliminado exitosamente");
        }
    }

    // Métodos auxiliares para crear elementos de la tabla
    private HBox crearFilaTablaMetrica(String metrica, String valor) {
        HBox fila = new HBox();
        fila.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");

        Label celdaMetrica = crearCeldaTabla(metrica, false);
        celdaMetrica.setStyle("-fx-text-fill: #2c3e50; -fx-padding: 5; -fx-alignment: center-left;");
        
        Label celdaValor = crearCeldaTabla(valor, false);
        celdaValor.setStyle("-fx-text-fill: #27ae60; -fx-padding: 5; -fx-alignment: center; -fx-font-weight: bold;");

        fila.getChildren().addAll(celdaMetrica, celdaValor);
        return fila;
    }

    private Label crearCeldaTabla(String texto, boolean esHeader) {
        Label celda = new Label(texto);
        celda.setPrefWidth(200);
        celda.setMaxWidth(200);
        if (esHeader) {
            celda.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5; -fx-alignment: center;");
        } else {
            celda.setStyle("-fx-text-fill: #2c3e50; -fx-padding: 5; -fx-alignment: center;");
        }
        return celda;
    }

    // Métodos de navegación
    @FXML
    public void goToReportesPrincipal(ActionEvent event) {
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
    public void onBackAction(ActionEvent event) {
        try {
            // Detener el scheduler antes de salir
            schedulerService.detenerScheduler();
            
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
    public void onCerrarSesion(ActionEvent event) {
        // Detener el scheduler antes de cerrar sesión
        schedulerService.detenerScheduler();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaLogin.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Portal del Administrador");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    @FXML
    public void confirmarReporteProgramado(ActionEvent event) {
        // Validar que se haya seleccionado una frecuencia
        if (choiceFrecuencia.getValue() == null || choiceFrecuencia.getValue().equals("Seleccione la Ejecucion")) {
            ManejadorMetodosComunes.mostrarVentanaError("Debe seleccionar una frecuencia de ejecución.");
            return;
        }
    }
}