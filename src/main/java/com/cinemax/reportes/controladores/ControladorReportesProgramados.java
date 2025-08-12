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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

import com.cinemax.reportes.modelos.entidades.ReporteGenerado;
import com.cinemax.reportes.servicios.ServicioDeReportes;
import com.cinemax.reportes.servicios.ServicioReportesProgramados;
import com.cinemax.utilidades.ManejadorMetodosComunes;
import com.cinemax.utilidades.strategyParaDocumentos.EstrategiaExportarCSV;
import com.cinemax.utilidades.strategyParaDocumentos.EstrategiaExportarPDF;
import com.cinemax.utilidades.strategyParaDocumentos.Exportable;

// Para la dependencia de programado task

/**
 * Controlador para la gestión de reportes programados en el sistema Cinemax
 * Permite programar reportes automáticos, visualizar reportes generados y
 * exportarlos
 */
public class ControladorReportesProgramados {

    // Componentes FXML de la interfaz
    @FXML
    private Button btnBack; // Botón para regresar a la pantalla anterior

    @FXML
    private TableView<ReporteGenerado> tablaReportesGenerados; // Tabla que muestra los reportes generados

    @FXML
    private TableColumn<ReporteGenerado, String> columnaNombre; // Columna para el nombre del reporte

    @FXML
    private TableColumn<ReporteGenerado, String> columnaEstado; // Columna para el estado del reporte

    @FXML
    private TableColumn<ReporteGenerado, LocalDate> columnaFecha; // Columna para la fecha de generación

    @FXML
    private TableColumn<ReporteGenerado, Void> columnaAcciones; // Columna para botones de acción

    @FXML
    private ComboBox<String> elegirFrecuencia; // ComboBox para seleccionar la frecuencia del reporte

    // Instancia del servicio singleton para gestionar reportes programados
    final ServicioReportesProgramados schedulerService = ServicioReportesProgramados.getInstance();

    // Servicio para obtener datos de ventas
    private ServicioDeReportes ventasService = new ServicioDeReportes();
    // Datos del resumen de ventas que se usarán en los reportes
    private Map<String, Object> datos = ventasService.getResumenDeVentas();;

    /**
     * Método de inicialización que se ejecuta automáticamente al cargar el FXML
     * Configura la tabla, el ComboBox y el servicio de programación
     */
    @FXML
    private void initialize() {
        // Configurar la política de redimensionamiento de columnas de la tabla
        tablaReportesGenerados.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Inicializar la tabla de reportes si existe
        if (tablaReportesGenerados != null) {
            inicializarTablaReportes();
        }

        // Crear lista de opciones para la frecuencia de ejecución de reportes
        ObservableList<String> opcionesFrecuencia = FXCollections.observableArrayList(
                "Diario",
                "Semanal",
                "Mensual",
                "Trimestral",
                "Anual");

        // Asignar las opciones al ComboBox
        elegirFrecuencia.setItems(opcionesFrecuencia);

        // Agregar listener para detectar cambios en la selección de frecuencia
        elegirFrecuencia.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Frecuencia seleccionada: " + newValue);
        });

        // Iniciar el servicio programador de tareas
        schedulerService.iniciarScheduler();

        // Configurar el cierre del scheduler cuando se cierre la ventana
        Platform.runLater(() -> {
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setOnCloseRequest(event -> schedulerService.detenerScheduler());
        });

    }

    /**
     * Configura las columnas de la tabla y sus comportamientos
     * Establece los valores de las celdas y crea la columna de acciones con botones
     */
    private void inicializarTablaReportes() {
        // Configurar las columnas usando PropertyValueFactory para mapear con los
        // atributos del modelo
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaFecha.setCellValueFactory(new PropertyValueFactory<>("fechaGeneracion"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Crear la columna de acciones con botones personalizados
        columnaAcciones.setCellFactory(columna -> new TableCell<>() {
            // Botones para las diferentes acciones disponibles
            private final Button btnVerPDF = new Button("Ver");
            private final Button btnDescargarPDF = new Button("Descargar PDF");
            private final Button btnDescargarCSV = new Button("Descargar CSV");
            private final HBox pane = new HBox(5, btnVerPDF, btnDescargarPDF, btnDescargarCSV);

            {
                // Aplicar estilos CSS a los botones
                btnVerPDF.getStyleClass().add("btn-small");
                btnDescargarPDF.getStyleClass().add("btn-small");
                btnDescargarCSV.getStyleClass().add("btn-small");

                // Configurar las acciones de cada botón
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

                // Centrar los botones en la celda
                pane.setAlignment(Pos.CENTER);
            }

            /**
             * Método que actualiza el contenido de la celda
             * Muestra u oculta los botones según si la fila tiene datos
             */
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        // Listener para habilitar/deshabilitar el ComboBox según si hay reportes
        // ejecutados
        schedulerService.getReportesEjecutados()
                .addListener((javafx.collections.ListChangeListener<ReporteGenerado>) change -> {
                    if (!schedulerService.getReportesPendientes().isEmpty()) {
                        elegirFrecuencia.setDisable(true); // Deshabilitar si hay reportes pendientes
                    } else {
                        elegirFrecuencia.setDisable(false); // Habilitar si no hay reportes pendientes
                    }
                });

        // Verificación inicial del estado del ComboBox al cargar la vista
        if (!schedulerService.getReportesPendientes().isEmpty()) {
            elegirFrecuencia.setDisable(true);
        } else {
            elegirFrecuencia.setDisable(false);
        }

        // Vincular la tabla con la lista de reportes ejecutados del servicio
        tablaReportesGenerados.setItems(schedulerService.getReportesEjecutados());

    }

    /**
     * Maneja el evento de confirmación para crear un nuevo reporte programado
     * Valida la selección y muestra la ventana de previsualización
     */
    @FXML
    void confirmarReporteProgramado(ActionEvent event) {
        // Validar que se haya seleccionado una frecuencia válida
        if (elegirFrecuencia.getValue() == null || elegirFrecuencia.getValue().equals("Seleccione la Ejecucion")) {
            ManejadorMetodosComunes.mostrarVentanaError("Debe seleccionar una frecuencia de ejecución.");
            return;
        }

        // Verificar si ya existe un reporte con la misma frecuencia
        String frecuenciaSeleccionada = elegirFrecuencia.getValue();
        if (existeReporteConFrecuencia(frecuenciaSeleccionada)) {
            ManejadorMetodosComunes.mostrarVentanaError(
                    "Ya existe un reporte programado con frecuencia " + frecuenciaSeleccionada + ".\n" +
                            "Solo puede haber una ejecución por cada tipo de frecuencia.");
            return;
        }

        // Mostrar la ventana de previsualización del reporte
        mostrarVentanaPrevia();
    }

    /**
     * Verifica si ya existe un reporte programado con la misma frecuencia
     * Revisa tanto en reportes ejecutados como en pendientes
     * 
     * @param frecuencia La frecuencia a verificar
     * @return true si ya existe un reporte con esa frecuencia, false en caso
     *         contrario
     */
    private boolean existeReporteConFrecuencia(String frecuencia) {
        // Revisar en la tabla de reportes ejecutados
        ObservableList<ReporteGenerado> reportes = tablaReportesGenerados.getItems();
        for (ReporteGenerado reporte : reportes) {
            if (reporte.getFrecuencia().equals(frecuencia)) {
                return true;
            }
        }
        // Revisar en la lista de reportes pendientes
        for (ReporteGenerado reporte : schedulerService.getReportesPendientes()) {
            if (reporte.getFrecuencia().equals(frecuencia)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crea y muestra una ventana modal con la previsualización del reporte
     * Permite al usuario ver cómo se verá el reporte antes de programarlo
     */
    private void mostrarVentanaPrevia() {
        try {
            // Crear nueva ventana modal
            Stage ventanaPrevia = new Stage();
            ventanaPrevia.setTitle("Previsualización del Reporte PDF");
            ventanaPrevia.setResizable(true);

            // Contenedor principal con estilo oscuro
            VBox contenidoPrincipal = new VBox(10);
            contenidoPrincipal.setPadding(new Insets(15));
            contenidoPrincipal.getStyleClass().add("root");

            // Agregar ícono usando la lógica sugerida
            Image icon = new Image(getClass().getResourceAsStream("/imagenes/logo.png"));
            ventanaPrevia.getIcons().add(icon);
            
            // Sección del encabezado con información del reporte
            VBox headerBox = new VBox(5);
            headerBox.getStyleClass().add("content-pane");

            // Título del reporte
            Label tituloReporte = new Label("EJEMPLO DE PROGRAMACION - CINEMAX");
            tituloReporte.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

            // Etiqueta de fecha programada
            Label lblFechaGeneracion = new Label("Se ha agendado fecha de creacion");
            lblFechaGeneracion.setStyle("-fx-font-size: 12px; -fx-text-fill: #b2bec3;");

            // Mostrar la frecuencia seleccionada
            Label lblFrecuencia = new Label("Frecuencia: " + elegirFrecuencia.getValue());
            lblFrecuencia.setStyle("-fx-font-size: 12px; -fx-text-fill: #b2bec3;");

            headerBox.getChildren().addAll(tituloReporte, lblFechaGeneracion, lblFrecuencia);

            // Contenido principal del reporte con datos simulados
            VBox contenidoReporte = new VBox(10);
            contenidoReporte.getStyleClass().add("content-pane");

            // Título de la sección de ventas
            Label tituloSeccion = new Label("RESUMEN DE VENTAS");
            tituloSeccion.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

            // Crear tabla de datos de ventas
            VBox tablaDatos = new VBox(5);
            tablaDatos.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 1px; -fx-padding: 10;");

            // Encabezado de la tabla de ventas
            HBox headerTabla = new HBox();
            headerTabla.setStyle("-fx-background-color: #02487b; -fx-padding: 8;");
            Label colFecha = crearCeldaTabla("Fecha", true);
            Label colBoletos = crearCeldaTabla("Boletos Vendidos", true);
            Label colIngresos = crearCeldaTabla("Ingresos", true);
            headerTabla.getChildren().addAll(colFecha, colBoletos, colIngresos);

            VBox filasDatos = new VBox(2);

            // Calcular la próxima fecha de ejecución basada en la frecuencia seleccionada
            String frecuenciaSeleccionada = elegirFrecuencia.getValue();
            String fechaEjecucion = schedulerService.calcularProximaEjecucion(LocalDateTime.now().toString(),
                    frecuenciaSeleccionada);

            // Agregar fila de datos de ejemplo
            filasDatos.getChildren().addAll(
                    crearFilaTabla(fechaEjecucion, "25", "$1,250.00"));

            // Fila de totales
            HBox totalRow = new HBox();
            totalRow.setStyle("-fx-background-color: #02487b; -fx-padding: 8;");
            Label totalLabel = crearCeldaTabla("TOTAL:", true);
            Label totalBoletos = crearCeldaTabla("125", true);
            Label totalIngresos = crearCeldaTabla("$1,250.00", true);
            totalRow.getChildren().addAll(totalLabel, totalBoletos, totalIngresos);

            tablaDatos.getChildren().addAll(headerTabla, filasDatos, totalRow);

            // Sección de información adicional
            VBox infoAdicional = new VBox(5);
            infoAdicional.getStyleClass().add("content-pane");

            // Agregar la tabla de ventas al contenido del reporte
            contenidoReporte.getChildren().addAll(tituloSeccion, tablaDatos);

            // Sección de películas
            Label tituloPeliculas = new Label("RESUMEN POR PELÍCULA");
            tituloPeliculas.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

            // Crear tabla de películas
            VBox tablaPeliculas = new VBox(2);
            tablaPeliculas.setStyle(
                    "-fx-border-color: #ecf0f1; -fx-border-width: 1px; -fx-padding: 10; -fx-background-radius: 5px;");

            contenidoReporte.getChildren().addAll(tituloPeliculas, tablaPeliculas);

            // Encabezados de la tabla de películas
            HBox headerPeliculas = new HBox();
            headerPeliculas.setStyle("-fx-background-color: #02487b; -fx-padding: 8;");
            headerPeliculas.getChildren().addAll(
                    crearCeldaTabla("Título", true),
                    crearCeldaTabla("Funciones", true),
                    crearCeldaTabla("Boletos Vendidos", true),
                    crearCeldaTabla("Ingresos", true));

            // Datos ficticios de películas para la demostración
            String[][] peliculas = {
                    { "Barbie", "3", "320", "$9,600.00" },
                    { "Oppenheimer", "2", "210", "$6,300.00" },
                    { "Intensamente 2", "2", "180", "$5,400.00" },
                    { "Garfield", "1", "80", "$2,400.00" }
            };

            // Crear filas de datos de películas
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

            // Nota informativa sobre la previsualización
            Label notaInfo = new Label(
                    "Nota: Este es un ejemplo de cómo se verá el reporte cuando se genere automáticamente.");
            notaInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #b2bec3; -fx-font-style: italic;");
            notaInfo.setWrapText(true);

            // Mostrar cuándo será la próxima generación
            Label proximaGeneracion = new Label("Próxima generación programada: " + fechaEjecucion);
            proximaGeneracion.setStyle("-fx-font-size: 11px; -fx-text-fill: #ffffffff; -fx-font-weight: bold;");

            infoAdicional.getChildren().addAll(notaInfo, proximaGeneracion);

            // Sección de botones de acción
            HBox botonesBox = new HBox(15);
            botonesBox.setAlignment(Pos.CENTER);
            botonesBox.setPadding(new Insets(15, 0, 5, 0));

            // Botón para confirmar la programación del reporte
            Button btnConfirmar = new Button("Programar Reporte");
            btnConfirmar.getStyleClass().add("btn-small");
            btnConfirmar.setOnAction(e -> {
                agregarReporteATabla(fechaEjecucion); // Agregar el reporte a la tabla
                String mensaje = "Reporte Programado exitosamente\n"
                        + "Se ejecutará: " + frecuenciaSeleccionada;

                ManejadorMetodosComunes.mostrarVentanaExito(mensaje);

                ventanaPrevia.close(); // Cerrar la ventana de previsualización
            });

            // Botón para cancelar la operación
            Button btnCancelar = new Button("❌ Cancelar");
            btnCancelar.getStyleClass().add("btn-small");
            btnCancelar.setOnAction(e -> ventanaPrevia.close());

            botonesBox.getChildren().addAll(btnConfirmar, btnCancelar);

            // Crear scroll pane para contenido desplazable
            ScrollPane scrollPane = new ScrollPane();
            VBox contenidoCompleto = new VBox(10);
            contenidoCompleto.getChildren().addAll(headerBox, contenidoReporte, infoAdicional);
            scrollPane.setContent(contenidoCompleto);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent;");

            contenidoPrincipal.getChildren().addAll(scrollPane, botonesBox);

            // Configurar y mostrar la ventana
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

    /**
     * Método auxiliar para crear celdas de tabla con formato consistente
     * 
     * @param texto    El texto a mostrar en la celda
     * @param esHeader Indica si es una celda de encabezado (true) o de datos
     *                 (false)
     * @return Label configurado como celda de tabla
     */
    private Label crearCeldaTabla(String texto, boolean esHeader) {
        Label celda = new Label(texto);
        celda.setPrefWidth(180);
        celda.setMaxWidth(180);
        // Aplicar estilos diferentes según si es encabezado o datos
        celda.setStyle(esHeader ? "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5; -fx-alignment: center;"
                : "-fx-text-fill: #2c3e50; -fx-padding: 5; -fx-alignment: center;");
        return celda;
    }

    /**
     * Crea una fila de tabla con los datos proporcionados
     * 
     * @param fecha    Fecha para la primera columna
     * @param boletos  Número de boletos para la segunda columna
     * @param ingresos Ingresos para la tercera columna
     * @return HBox que representa una fila de tabla
     */
    private HBox crearFilaTabla(String fecha, String boletos, String ingresos) {
        HBox fila = new HBox();
        fila.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");

        // Crear las celdas de la fila
        Label celdaFecha = crearCeldaTabla(fecha, false);
        Label celdaBoletos = crearCeldaTabla(boletos, false);
        Label celdaIngresos = crearCeldaTabla(ingresos, false);

        fila.getChildren().addAll(celdaFecha, celdaBoletos, celdaIngresos);
        return fila;
    }

    /**
     * Agrega un nuevo reporte programado a la tabla y al servicio de programación
     * 
     * @param fechaEjecucion La fecha calculada para la ejecución del reporte
     */
    private void agregarReporteATabla(String fechaEjecucion) {
        String frecuencia = elegirFrecuencia.getValue();
        LocalDateTime fecha = LocalDateTime.parse(fechaEjecucion);

        // Crear nuevo objeto ReporteGenerado
        ReporteGenerado nuevoReporte = new ReporteGenerado(
                "Reporte de Ventas - " + frecuencia, // Nombre del reporte
                "Programado", // Estado inicial
                fecha, // Fecha de ejecución
                "/reportes/ventas_" + frecuencia.toLowerCase() + "_" + fechaEjecucion.replace("-", "_") + ".pdf", // Ruta
                                                                                                                  // del
                                                                                                                  // archivo
                frecuencia); // Frecuencia de ejecución

        // Agregar el reporte a la lista de pendientes del servicio
        schedulerService.getReportesPendientes().add(nuevoReporte);

        // Resetear el ComboBox y deshabilitarlo
        elegirFrecuencia.setValue("Seleccione la Ejecucion");
        elegirFrecuencia.setDisable(true);
    }

    /**
     * Elimina un reporte programado después de confirmación del usuario
     * 
     * @param reporte El reporte a eliminar
     */
    public void eliminarReporteProgramado(ReporteGenerado reporte) {
        if (reporte == null) {
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo identificar el reporte a eliminar.");
            return;
        }

        // Mostrar diálogo de confirmación antes de eliminar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro que desea eliminar el reporte '" +
                reporte.getNombre() + "'?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Eliminar el reporte de la tabla si el usuario confirma
            tablaReportesGenerados.getItems().remove(reporte);
            ManejadorMetodosComunes.mostrarVentanaExito("El reporte ha sido eliminado exitosamente.");
        }
    }

    /**
     * Muestra una ventana modal con la vista previa completa del reporte
     * seleccionado
     * Permite visualizar el contenido antes de descargarlo
     * 
     * @param reporte El reporte a visualizar
     */
    private void mostrarVistaPrevia(ReporteGenerado reporte) {
        try {
            // Crear ventana modal para la vista previa
            Stage ventanaPrevia = new Stage();
            ventanaPrevia.setTitle("Vista Previa PDF - " + reporte.getNombre());
            ventanaPrevia.setResizable(true);

            VBox contenido = new VBox(15);
            contenido.setPadding(new Insets(20));
            contenido.getStyleClass().add("root"); // Aplicar fondo oscuro del sistema

            // Header con información del reporte
            VBox headerBox = new VBox(10);
            headerBox.getStyleClass().add("content-pane");

            // Título principal del reporte
            Label titulo = new Label("EJEMPLO DE PROGRAMACION - CINEMAX");
            titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

            // Fecha de generación del reporte
            Label fechaGen = new Label("Fecha de Generación: "
                    + reporte.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            fechaGen.setStyle("-fx-font-size: 12px; -fx-text-fill: #b2bec3;");

            // Frecuencia del reporte
            Label frecuencia = new Label("Frecuencia: " + reporte.getFrecuencia());
            frecuencia.setStyle("-fx-font-size: 12px; -fx-text-fill: #ffffffff; -fx-font-weight: bold;");

            headerBox.getChildren().addAll(titulo, fechaGen, frecuencia);

            // Generar el contenido visual del reporte
            VBox contenidoReporte = generarContenidoReporte(reporte);

            // Nota informativa sobre la descarga
            Label notaPDF = new Label(
                    "Nota: Al descargar se generará un archivo PDF con este contenido y formato profesional.");
            notaPDF.setStyle(
                    "-fx-font-size: 11px; -fx-text-fill: #ffffffff; -fx-font-style: italic; -fx-background-color: #232323; -fx-padding: 10; -fx-border-radius: 5px;");
            notaPDF.setWrapText(true);

            // Sección de botones de acción
            HBox botonesBox = new HBox(10);
            botonesBox.setAlignment(Pos.CENTER);

            // Botón para descargar como PDF
            Button btnDescargarPDF = new Button("📄 Descargar como PDF");
            btnDescargarPDF.setStyle(
                    "-fx-background-color: #02487b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
            btnDescargarPDF.setOnAction(e -> {
                ventanaPrevia.close();
                descargarReporte(reporte, "pdf");
            });

            // Botón para descargar como CSV
            Button btnDescargarCSV = new Button("Descargar como CSV");
            btnDescargarCSV.setStyle(
                    "-fx-background-color: #02487b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
            btnDescargarCSV.setOnAction(e -> {
                ventanaPrevia.close();
                descargarReporte(reporte, "csv");
            });

            // Botón para eliminar el reporte
            Button btnEliminar = new Button("Eliminar Reporte");
            btnEliminar.setStyle(
                    "-fx-background-color: #02487b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
            btnEliminar.setOnAction(e -> {
                ventanaPrevia.close();
                eliminarReporteProgramado(reporte);
            });

            // Botón para cerrar la ventana
            Button btnCerrar = new Button("Cerrar");
            btnCerrar.setStyle(
                    "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
            btnCerrar.setOnAction(e -> ventanaPrevia.close());

            botonesBox.getChildren().addAll(btnDescargarPDF, btnEliminar, btnCerrar);

            // Crear scroll pane para contenido desplazable
            ScrollPane scrollPane = new ScrollPane();
            VBox contenidoCompleto = new VBox(15);
            contenidoCompleto.getChildren().addAll(headerBox, contenidoReporte, notaPDF);
            scrollPane.setContent(contenidoCompleto);
            scrollPane.setFitToWidth(true);

            contenido.getChildren().addAll(scrollPane, botonesBox);

            // Configurar y mostrar la ventana
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

    /**
     * Método auxiliar para generar el contenido visual completo del reporte
     * Crea las tablas de datos de ventas y películas con información de ejemplo
     * 
     * @param reporte El reporte del cual generar el contenido visual
     * @return VBox conteniendo todo el contenido visual del reporte
     */
    private VBox generarContenidoReporte(ReporteGenerado reporte) {
        VBox contenido = new VBox(15);
        contenido.getStyleClass().add("content-pane"); // Aplicar fondo secundario oscuro

        // Título de la sección de resumen de ventas
        Label tituloSeccion = new Label("EJEMPLO DE RESUMEN DE VENTAS RECOPILADO");
        tituloSeccion.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

        // Crear contenedor para la tabla de datos de ventas
        VBox tablaDatos = new VBox(5);
        tablaDatos.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 1px; -fx-padding: 10;");

        // Crear encabezados de la tabla de ventas
        HBox headerTabla = new HBox();
        headerTabla.setStyle("-fx-background-color: #02487b; -fx-padding: 8;");
        headerTabla.getChildren().addAll(
                crearCeldaTabla("Fecha", true),
                crearCeldaTabla("Boletos Vendidos", true),
                crearCeldaTabla("Ingresos", true));

        // Crear filas de datos usando la fecha del reporte
        VBox filasDatos = new VBox(2);
        filasDatos.getChildren().addAll(
                crearFilaTabla(reporte.getFechaGeneracion().toString(), "$125", "$270.00"));

        // Crear fila de totales
        HBox totalRow = new HBox();
        totalRow.setStyle("-fx-background-color: #02487b; -fx-padding: 8;");
        totalRow.getChildren().addAll(
                crearCeldaTabla("TOTAL:", true),
                crearCeldaTabla("466", true),
                crearCeldaTabla("$13,980.00", true));

        // Título de la sección de películas
        Label tituloPeliculas = new Label("RESUMEN POR PELÍCULA");
        tituloPeliculas.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

        // Crear contenedor para la tabla de películas
        VBox tablaPeliculas = new VBox(2);
        tablaPeliculas.setStyle(
                "-fx-border-color: #ecf0f1; -fx-border-width: 1px; -fx-padding: 10; -fx-background-radius: 5px;");

        // Crear encabezados de la tabla de películas
        HBox headerPeliculas = new HBox();
        headerPeliculas.setStyle("-fx-background-color: #02487b; -fx-padding: 8;");
        headerPeliculas.getChildren().addAll(
                crearCeldaTabla("Título", true),
                crearCeldaTabla("Funciones", true),
                crearCeldaTabla("Boletos Vendidos", true),
                crearCeldaTabla("Ingresos", true));

        // Datos ficticios de películas para mostrar en el reporte
        String[][] peliculas = {
                { "Barbie", "3", "320", "$9,600.00" },
                { "Oppenheimer", "2", "210", "$6,300.00" },
                { "Intensamente 2", "2", "180", "$5,400.00" },
                { "Garfield", "1", "80", "$2,400.00" }
        };

        // Crear filas de datos para cada película
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

        // Ensamblar todos los componentes de las tablas
        tablaDatos.getChildren().addAll(headerTabla, filasDatos, totalRow);
        contenido.getChildren().addAll(tituloSeccion, tablaDatos);

        contenido.getChildren().addAll(tituloPeliculas, tablaPeliculas);
        tablaPeliculas.getChildren().addAll(headerPeliculas, filasPeliculas);
        return contenido;
    }

    /**
     * Maneja la descarga de reportes en diferentes formatos (PDF o CSV)
     * Abre un diálogo para seleccionar la ubicación de guardado y utiliza las
     * estrategias de exportación
     * 
     * @param reporte El reporte a descargar
     * @param formato El formato de descarga ("pdf" o "csv")
     */
    private void descargarReporte(ReporteGenerado reporte, String formato) {
        try {
            // Configurar el selector de archivos
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte " + formato.toUpperCase());

            // Determinar la extensión del archivo según el formato
            String extension = formato.equalsIgnoreCase("pdf") ? ".pdf" : ".csv";

            // Crear nombre de archivo limpio eliminando caracteres especiales
            String nombreArchivo = reporte.getNombre().replaceAll("[^a-zA-Z0-9\\s]", "_") + "_" +
                    reporte.getFechaGeneracion().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
            fileChooser.setInitialFileName(nombreArchivo + extension);

            // Configurar filtros de extensión según el formato
            if (formato.equalsIgnoreCase("pdf")) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
            } else if (formato.equalsIgnoreCase("csv")) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));
            }

            // Obtener la ventana padre para el diálogo
            Stage stage = (Stage) tablaReportesGenerados.getScene().getWindow();
            File archivo = fileChooser.showSaveDialog(stage);

            if (archivo != null) {
                // Seleccionar la estrategia de exportación apropiada usando el patrón Strategy
                Exportable exportStrategy;
                if (formato.equalsIgnoreCase("pdf")) {
                    exportStrategy = new EstrategiaExportarPDF();
                } else if (formato.equalsIgnoreCase("csv")) {
                    exportStrategy = new EstrategiaExportarCSV();
                } else {
                    ManejadorMetodosComunes.mostrarVentanaError("Formato de exportación no soportado.");
                    return;
                }
                // TODO: Aquí se exportan los datos del reporte usando la estrategia
                // seleccionada
                exportStrategy.exportar(reporte, archivo, datos);

                // Mostrar mensaje de éxito al usuario
                String mensaje = "✅ Descarga Exitosa\n"
                        + "El reporte '" + reporte.getNombre() + "' se ha sido registrado exitosamente\n";

                ManejadorMetodosComunes.mostrarVentanaExito(mensaje);
            } else {
                // El usuario canceló la descarga
                System.out.println("Descarga cancelada por el usuario.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo descargar el reporte: " + e.getMessage());
        }
    }

    /**
     * Navega de vuelta a la vista principal de reportes
     * Carga el archivo FXML correspondiente y cambia la escena
     */
    @FXML
    void goToReportesPrincipal(ActionEvent event) {
        try {
            // Cargar la vista principal de reportes
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/reportes/VistaReportesPrincipal.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual y cambiar la escena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navega de vuelta al portal principal del empleado
     * Carga el archivo FXML del portal principal y cambia la escena
     */
    @FXML
    void volverEscena(ActionEvent event) {
        try {
            // Cargar la vista del portal principal
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual y cambiar la escena
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}