package com.cinemax.reportes.controladores;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.reportes.modelos.entidades.EstrategiaExportarCSV;
import com.cinemax.reportes.modelos.entidades.EstrategiaExportarPDF;
import com.cinemax.reportes.modelos.entidades.Exportable;
import com.cinemax.reportes.modelos.entidades.ReporteGenerado;
import com.cinemax.reportes.servicios.ServicioDeReportes;

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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controlador principal para la gestión de reportes de ventas en el sistema
 * Cinemax.
 * 
 * <p>
 * Esta clase se encarga de manejar la visualización de gráficos, filtrado de
 * datos,
 * generación y exportación de reportes de ventas. Permite visualizar reportes
 * existentes,
 * crear nuevos reportes en diferentes formatos (PDF, CSV), y mostrar análisis
 * visual
 * mediante gráficas de barras y pastel.
 * </p>
 * 
 * <ul>
 * <li>Permite filtrar datos por rango de fechas y horario.</li>
 * <li>Genera y muestra reportes de ventas con tablas y gráficas.</li>
 * <li>Exporta reportes en formato PDF o CSV usando el patrón Strategy.</li>
 * <li>Muestra reportes generados previamente en una tabla.</li>
 * </ul>
 * 
 * <p>
 * Componentes principales:
 * <ul>
 * <li>Botones de navegación y acción (filtrar, confirmar, volver,
 * exportar).</li>
 * <li>Pickers de fecha y combo para horario.</li>
 * <li>Gráficas de barras y pastel para análisis visual.</li>
 * <li>Tabla de reportes generados.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Métodos clave:
 * <ul>
 * <li>{@link #initialize()} - Inicializa la vista y componentes.</li>
 * <li>{@link #onFiltrar(ActionEvent)} - Aplica filtros y actualiza las
 * gráficas.</li>
 * <li>{@link #onConfirmarReporte(ActionEvent)} - Muestra previsualización y
 * permite exportar.</li>
 * <li>{@link #actualizarGraficaBarras(List)} - Actualiza la gráfica de
 * barras.</li>
 * <li>{@link #actualizarGraficaPastel(List)} - Actualiza la gráfica de
 * pastel.</li>
 * <li>{@link #exportarReporte(Exportable, String)} - Exporta el reporte en el
 * formato seleccionado.</li>
 * <li>{@link #mostrarPrevisualizacionReporte(List, boolean)} - Muestra la
 * previsualización del reporte.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Uso:
 * <ul>
 * <li>El usuario selecciona un rango de fechas y horario, y presiona
 * "Filtrar".</li>
 * <li>Se muestran las gráficas y datos correspondientes.</li>
 * <li>Puede generar y exportar reportes en PDF o CSV.</li>
 * <li>Puede visualizar reportes generados previamente.</li>
 * </ul>
 * </p>
 * 
 * @author Grupo E
 * @version 1.0
 * @since 2025-08-11
 */
public class ControladorReportesPrincipal {

    // Componentes FXML de navegación y control
    @FXML
    private Button btnBack; // Botón para regresar a la pantalla anterior
    @FXML
    private Button btnFiltrar; // Botón para aplicar filtros a los datos
    @FXML
    private Button btnConfirmar; // Botón para confirmar la generación del reporte

    // Componentes de filtrado de fechas y horarios
    @FXML
    private DatePicker dateDesde; // Selector de fecha de inicio del período
    @FXML
    private DatePicker dateHasta; // Selector de fecha de fin del período

    @FXML
    private ComboBox<String> choiceHorario; // Selector de horario (Matutino, Nocturno, Todos)

    // Componentes gráficos para visualización de datos
    @FXML
    private BarChart<String, Number> barChart; // Gráfico de barras para mostrar ventas por tipo de boleto
    @FXML
    private PieChart pieChart; // Gráfico circular para mostrar distribución 2D vs 3D

    // Componentes de tabla para mostrar reportes generados
    @FXML
    private TableView<ReporteGenerado> tablaReportes; // Tabla principal de reportes
    @FXML
    private TableColumn<ReporteGenerado, String> colNombre; // Columna del nombre del reporte
    @FXML
    private TableColumn<ReporteGenerado, String> colTipo; // Columna del tipo de archivo (PDF/CSV)
    @FXML
    private TableColumn<ReporteGenerado, LocalDateTime> colFecha; // Columna de fecha de generación
    @FXML
    private TableColumn<ReporteGenerado, String> colDescripcion; // Columna de descripción del reporte
    @FXML
    private TableColumn<ReporteGenerado, Integer> colAcciones; // Columna con botones de acción

    // Lista observable para gestionar los reportes en la tabla
    private ObservableList<ReporteGenerado> reportesGenerados = FXCollections.observableArrayList();

    // Servicios y datos del negocio
    private ServicioDeReportes servicioReportes = new ServicioDeReportes(); // Servicio para obtener datos de ventas
    private Map<String, Object> datos = servicioReportes.getResumenDeVentas(); // Datos resumen de ventas

    // Datos estadísticos para poblar las gráficas
    private List<Map<String, Object>> estadisticas = servicioReportes.getEstadisticasDeBarras();

    // Datos simulados para reportes generados
    private final List<ReporteGenerado> reportesSimulados = Arrays.asList();


    /**
     * Inicializa la vista y los componentes del controlador.
     * Carga los datos iniciales, configura los listeners y prepara las gráficas.
     */
    @FXML
    public void initialize() {
        choiceHorario.getItems().addAll("Todos", "Matutino", "Nocturno");

        // Configurar tabla de reportes
        configurarTablaReportes();

        // Cargar reportes simulados
        cargarReportesSimulados();

        // Inicializar gráficas vacías
        inicializarGraficasVacias();

    }

    /**
     * Configura las columnas y celdas de la tabla de reportes generados.
     * Establece los formatos de fecha y los botones de acción.
     */
    private void configurarTablaReportes() {
        // Configurar las columnas
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaGeneracion"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Configuracion relleno columnas
        tablaReportes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
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
                btnAbrir.getStyleClass().add("primary-button");
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

    /**
     * Carga los reportes simulados en la tabla de reportes generados.
     */
    private void cargarReportesSimulados() {
        reportesGenerados.clear();
        reportesGenerados.addAll(reportesSimulados);
    }

    /**
     * Inicializa las gráficas de barras y pastel en estado vacío.
     * Se utiliza cuando no hay datos o al iniciar la vista.
     */
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

    /**
     * Abre la previsualización de un reporte generado.
     * 
     * @param reporte El reporte generado a visualizar.
     */
    private void abrirReporte(ReporteGenerado reporte) {
        try {
            // Mostrar previsualización del reporte sin opciones de descarga
            mostrarPrevisualizacionReporte(estadisticas, false);
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al abrir el reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navega a la vista de reportes programados.
     * 
     * @param event Evento de acción generado por el botón correspondiente.
     */
    @FXML
    private void goToReporteProgramado(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/reportes/VistaReportesProgramados.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Aplica los filtros seleccionados por el usuario (fechas y horario) y
     * actualiza las gráficas.
     * 
     * @param event Evento de acción generado por el botón de filtrar.
     */
    @FXML
    private void onFiltrar(ActionEvent event) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM");
        LocalDate desde = dateDesde.getValue();
        LocalDate hasta = dateHasta.getValue();

        String desdeStr = desde != null ? desde.format(formatter) : null;
        String hastaStr = hasta != null ? hasta.format(formatter) : null;

        String horario = choiceHorario.getValue();

        if (desde == null || hasta == null) {
            ManejadorMetodosComunes.mostrarVentanaError("Por favor seleccione las fechas de inicio y fin");
            return;
        }

        System.out.println("Filtrando desde " + desdeStr + " hasta " + hastaStr);

        // CAMBIO AQUÍ: Primero obtener todos los datos sin filtrar
        List<Map<String, Object>> datosOriginales = servicioReportes.getEstadisticasDeBarras();

        // Actualizar gráficas con datos filtrados
        estadisticas = servicioReportes.obtenerDatosFiltrados(datosOriginales, desdeStr, hastaStr);

        // Si no hay datos con ese filtro, mostrar mensaje
        if (estadisticas.isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("No hay datos para el período seleccionado");
            inicializarGraficasVacias();
            return;
        }

        actualizarGraficaBarras(estadisticas);
        actualizarGraficaPastel(estadisticas);

        String mensaje = "Filtros aplicados:\n" +
                "• Desde: " + desde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                "• Hasta: " + hasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                "• Horario: " + horario;

        // Mostrar mensaje de confirmación
        ManejadorMetodosComunes.mostrarVentanaAdvertencia(mensaje);
    }

    /**
     * Muestra la previsualización del reporte de ventas según los filtros
     * aplicados.
     * Permite al usuario confirmar la generación y exportación del reporte.
     * 
     * @param event Evento de acción generado por el botón de confirmar.
     */
    @FXML
    private void onConfirmarReporte(ActionEvent event) {
        // Obtener valores de los filtros
        LocalDate desde = dateDesde.getValue();
        LocalDate hasta = dateHasta.getValue();
        String horario = choiceHorario.getValue();

        // Validar que se hayan seleccionado las fechas obligatorias
        if (desde == null || hasta == null) {
            ManejadorMetodosComunes.mostrarVentanaError("Por favor seleccione las fechas de inicio y fin");
            return;
        }

        // Verificar que haya datos para mostrar en el reporte
        if (estadisticas.isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaError("No hay datos para mostrar con los filtros seleccionados");
            return;
        }

        // Mostrar previsualización del reporte con opciones de descarga habilitadas
        mostrarPrevisualizacionReporte(estadisticas, true);
    }

    private void actualizarGraficaBarras(List<Map<String, Object>> estadisticas) {
        if (barChart != null) {
            // Limpiar datos anteriores
            barChart.getData().clear();
            barChart.layout();

            if (estadisticas == null || estadisticas.isEmpty()) {
                barChart.setTitle("No hay datos para mostrar con los filtros seleccionados");
                return;
            }

            // Crear eje X y Y nuevos para evitar problemas de caché
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            barChart.setAnimated(false); // Desactivar animaciones para evitar problemas

            // Establecer etiquetas de ejes
            xAxis.setLabel("Fecha");
            yAxis.setLabel("Cantidad de Boletos");

            // Series para los datos
            XYChart.Series<String, Number> serieVIP = new XYChart.Series<>();
            serieVIP.setName("VIP");
            XYChart.Series<String, Number> serieNormal = new XYChart.Series<>();
            serieNormal.setName("Normal");

            // Ordenar los datos por fecha
            Map<LocalDate, Map<String, Integer>> datosPorFecha = new TreeMap<>();
            List<String> categorias = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Procesar y agrupar datos
            for (Map<String, Object> fila : estadisticas) {
                LocalDate fecha = null;
                Object fechaObj = fila.get("fecha");

                // Convertir fecha a LocalDate
                if (fechaObj instanceof java.sql.Date) {
                    fecha = ((java.sql.Date) fechaObj).toLocalDate();
                } else if (fechaObj instanceof LocalDate) {
                    fecha = (LocalDate) fechaObj;
                } else if (fechaObj != null) {
                    try {
                        fecha = LocalDate.parse(fechaObj.toString());
                    } catch (Exception e) {
                        continue;
                    }
                }

                if (fecha == null)
                    continue;

                // Inicializar mapa para esta fecha
                datosPorFecha.putIfAbsent(fecha, new HashMap<>());
                String tipoSala = (String) fila.get("tipos_sala");
                int boletos = (int) fila.get("total_boletos_vendidos");

                // Dividir boletos entre VIP y Normal si hay ambos tipos
                if (tipoSala != null && tipoSala.contains("VIP")) {
                    if (tipoSala.contains("NORMAL")) {
                        int boletosVIP = boletos / 2;
                        int boletosNormal = boletos - boletosVIP;
                        datosPorFecha.get(fecha).merge("VIP", boletosVIP, Integer::sum);
                        datosPorFecha.get(fecha).merge("Normal", boletosNormal, Integer::sum);
                    } else {
                        datosPorFecha.get(fecha).merge("VIP", boletos, Integer::sum);
                    }
                } else {
                    datosPorFecha.get(fecha).merge("Normal", boletos, Integer::sum);
                }

                // Añadir la fecha como categoría si no existe
                String fechaStr = fecha.format(formatter);
                if (!categorias.contains(fechaStr)) {
                    categorias.add(fechaStr);
                }
            }

            System.out.println("Categorías (fechas) encontradas: " + categorias);

            // Crear nuevo gráfico con los datos procesados
            BarChart<String, Number> nuevoBarChart = new BarChart<>(xAxis, yAxis);
            nuevoBarChart.setAnimated(false);

            // Configurar categorías explícitamente
            xAxis.setCategories(FXCollections.observableArrayList(categorias));

            // Añadir datos a las series
            for (LocalDate fecha : datosPorFecha.keySet()) {
                String fechaStr = fecha.format(formatter);
                Map<String, Integer> datos = datosPorFecha.get(fecha);

                serieVIP.getData().add(new XYChart.Data<>(fechaStr, datos.getOrDefault("VIP", 0)));
                serieNormal.getData().add(new XYChart.Data<>(fechaStr, datos.getOrDefault("Normal", 0)));
            }

            // Añadir series al gráfico
            barChart.getData().addAll(serieVIP, serieNormal);

            // Forzar actualización
            barChart.layout();

            // Actualizar título
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

    /**
     * Actualiza la gráfica de pastel con distribución de formatos 2D vs 3D
     * Procesa los datos estadísticos para mostrar la proporción de cada formato
     * 
     * @param estadisticas Lista de mapas con datos estadísticos de ventas
     */
    private void actualizarGraficaPastel(List<Map<String, Object>> estadisticas) {
        if (pieChart != null) {
            pieChart.getData().clear(); // Limpiar datos existentes

            // Verificar si hay datos para mostrar
            if (estadisticas == null || estadisticas.isEmpty()) {
                pieChart.setTitle("No hay datos para mostrar con los filtros seleccionados");
                return;
            }

            // Contadores para cada formato
            int total2D = 0;
            int total3D = 0;

            // Procesar cada registro para agrupar por formato
            for (Map<String, Object> fila : estadisticas) {
                String formatos = (String) fila.get("formatos");
                int totalBoletos = (int) fila.get("total_boletos_vendidos");

                // Distribuir boletos según los formatos disponibles
                if (formatos != null && !formatos.isEmpty()) {
                    if (formatos.contains("2D") && formatos.contains("3D")) {
                        // Si hay ambos formatos, distribución equitativa
                        total2D += totalBoletos / 2;
                        total3D += totalBoletos / 2;
                    } else if (formatos.contains("2D")) {
                        total2D += totalBoletos; // Todo a 2D
                    } else if (formatos.contains("3D")) {
                        total3D += totalBoletos; // Todo a 3D
                    } else {
                        // Formato no especificado, asumir 2D por defecto
                        total2D += totalBoletos;
                    }
                } else {
                    // Sin información de formato, asumir 2D
                    total2D += totalBoletos;
                }
            }

            // Agregar segmentos al gráfico de pastel solo si hay datos
            if (total2D > 0) {
                pieChart.getData().add(new PieChart.Data("2D (" + total2D + ")", total2D));
            }
            if (total3D > 0) {
                pieChart.getData().add(new PieChart.Data("3D (" + total3D + ")", total3D));
            }

            // Configurar propiedades visuales del gráfico
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

            // Aplicar estilos personalizados a las etiquetas mediante listener
            pieChart.getData().addListener((javafx.collections.ListChangeListener<PieChart.Data>) c -> {
                pieChart.lookupAll(".chart-pie-label")
                        .forEach(node -> node.setStyle("-fx-text-fill: #ecf0f1; -fx-font-weight: bold;"));
            });

            // Aplicar estilos inmediatamente
            pieChart.applyCss();
            pieChart.lookupAll(".chart-pie-label")
                    .forEach(node -> node.setStyle("-fx-text-fill: #ecf0f1; -fx-font-weight: bold;"));
        }
    }

    /**
     * Maneja la exportación de reportes utilizando el patrón Strategy
     * Permite guardar el reporte en diferentes formatos (PDF o CSV)
     * 
     * @param strategy La estrategia de exportación a utilizar (PDF o CSV)
     * @param tipo     El tipo de archivo a generar ("pdf" o "csv")
     */
    private void exportarReporte(Exportable strategy, String tipo) {
        try {
            // Obtener valores de filtros para incluir en el reporte
            LocalDate desde = dateDesde.getValue();
            LocalDate hasta = dateHasta.getValue();
            String horario = choiceHorario.getValue();

            // Validar que se hayan seleccionado las fechas antes de exportar
            if (desde == null || hasta == null) {
                ManejadorMetodosComunes.mostrarVentanaError("Por favor seleccione las fechas antes de exportar");
                return;
            }

            // Usar los datos estadísticos actuales para la exportación
            List<Map<String, Object>> datos = estadisticas;

            // Configurar el selector de archivos para guardar
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte " + tipo.toUpperCase());
            fileChooser.setInitialFileName("reporte_ventas." + tipo);

            // Agregar filtros de extensión según el tipo de archivo
            if (tipo.equals("pdf")) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
            } else if (tipo.equals("csv")) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));
            }

            // Obtener la ventana padre para el diálogo
            Stage stage = (Stage) btnBack.getScene().getWindow();
            File archivo = fileChooser.showSaveDialog(stage);

            if (archivo != null) {
                // Crear información adicional para incluir en el reporte
                Map<String, Object> infoExtra = new HashMap<>();
                infoExtra.put("subtitulo", "Reporte generado el "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

                // Ejecutar la estrategia de exportación seleccionada
                strategy.exportarFormatoPrincipal(datos, archivo, "REPORTE DE VENTAS - CINEMAX", infoExtra);

                // Crear nuevo registro de reporte generado para agregar a la tabla
                ReporteGenerado nuevoReporte = new ReporteGenerado(
                        reportesGenerados.size() + 1, // ID incremental
                        "Reporte_Ventas_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")),
                        tipo.toUpperCase(), // Tipo en mayúsculas (PDF/CSV)
                        LocalDateTime.now(), // Fecha de generación actual
                        archivo.getAbsolutePath(), // Ruta completa del archivo
                        "Reporte de ventas del " + desde + " al " + hasta); // Descripción descriptiva

                // Agregar el nuevo reporte al inicio de la lista (más reciente primero)
                reportesGenerados.add(0, nuevoReporte);

            }
        } catch (Exception e) {
            // Manejar errores durante la exportación
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo exportar el reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crea y muestra una ventana modal con la previsualización completa del reporte
     * Incluye tablas de datos, gráficas y estadísticas según los filtros aplicados
     * 
     * @param datos            Lista de datos estadísticos a mostrar en el reporte
     * @param permitirDescarga Si true, muestra botones de descarga; si false, solo
     *                         visualización
     */
    private void mostrarPrevisualizacionReporte(List<Map<String, Object>> datos, boolean permitirDescarga) {
        try {
            // Crear ventana modal para la previsualización
            Stage ventanaPrevia = new Stage();
            ventanaPrevia.setTitle("Previsualización del Reporte - CineMax");
            ventanaPrevia.setResizable(true);

            // Agregar ícono usando la lógica sugerida
            Image icon = new Image(getClass().getResourceAsStream("/imagenes/logo.png"));
            ventanaPrevia.getIcons().add(icon);

            // Contenedor principal con estilo oscuro
            VBox contenido = new VBox(15);
            contenido.setPadding(new Insets(20));
            contenido.setStyle("-fx-background-color: #2B2B2B;");

            // Sección del encabezado del reporte
            VBox headerBox = new VBox(10);
            headerBox.setStyle(
                    "-fx-background-color: #2B2B2B; -fx-border-color: #2B2B2B; -fx-border-width: 1px; -fx-padding: 20; -fx-border-radius: 5px;");

            // Título principal centrado
            Label titulo = new Label("REPORTE DE VENTAS - CINEMAX");
            titulo.setStyle(
                    "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1; -fx-alignment: center; -fx-pref-width: 760;");
            titulo.setMaxWidth(Double.MAX_VALUE);
            titulo.setAlignment(Pos.CENTER);

            // Obtener información de filtros para mostrar en el encabezado
            LocalDate desde = dateDesde.getValue();
            LocalDate hasta = dateHasta.getValue();
            String horario = choiceHorario.getValue();

            // Etiquetas informativas del reporte
            Label fechaGen = new Label("Período: " + desde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    " - " + hasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            fechaGen.setStyle("-fx-font-size: 12px; -fx-text-fill: #ecf0f1;");

            Label horarioLabel = new Label("Horario: " + horario);
            horarioLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ecf0f1;");

            Label estado = new Label("Estado: Generado el "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            estado.setStyle("-fx-font-size: 12px; -fx-text-fill: #ffffffff; -fx-font-weight: bold;");

            headerBox.getChildren().addAll(titulo, fechaGen, horarioLabel, estado);

            // Generar el contenido completo del reporte con gráficas y tablas
            VBox contenidoReporte = generarContenidoReporteCompleto(datos);

            // Barra con nota informativa sobre el contenido del reporte
            HBox barraNota = new HBox();
            barraNota.setStyle(
                    "-fx-background-color: #2B2B2B; -fx-padding: 12 0 12 0; -fx-border-radius: 0; -fx-border-width: 0;");
            barraNota.setAlignment(Pos.CENTER_LEFT);
            barraNota.setMaxWidth(Double.MAX_VALUE);

            Label notaReporte = new Label(
                    "Este reporte incluye datos de ventas, gráficas de distribución y análisis detallado del período seleccionado.");
            notaReporte.setStyle(
                    "-fx-font-size: 12px; -fx-text-fill: #ffffffff; -fx-font-style: italic; -fx-padding: 0 20 0 20;");
            notaReporte.setMaxWidth(Double.MAX_VALUE);

            barraNota.getChildren().add(notaReporte);

            // Sección de botones de acción
            HBox botonesBox = new HBox(10);
            botonesBox.setAlignment(Pos.CENTER);

            // Mostrar botones de descarga solo si está habilitado
            if (permitirDescarga) {
                // Botón para descargar como PDF
                Button btnDescargarPDF = new Button("Descargar como PDF");
                btnDescargarPDF.setStyle(
                        "-fx-background-color: #02487b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
                btnDescargarPDF.setOnAction(e -> {
                    ventanaPrevia.close();
                    exportarReporte(new EstrategiaExportarPDF(), "pdf");
                });

                // Botón para descargar como CSV
                Button btnDescargarCSV = new Button("Descargar como CSV");
                btnDescargarCSV.setStyle(
                        "-fx-background-color: #02487b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
                btnDescargarCSV.setOnAction(e -> {
                    ventanaPrevia.close();
                    exportarReporte(new EstrategiaExportarCSV(), "csv");

                });

                botonesBox.getChildren().addAll(btnDescargarPDF, btnDescargarCSV);
            }

            // Botón para cerrar la ventana
            Button btnCerrar = new Button("Cerrar");
            btnCerrar.setStyle(
                    "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
            btnCerrar.setOnAction(e -> ventanaPrevia.close());

            botonesBox.getChildren().add(btnCerrar);

            // Configurar scroll pane para contenido desplazable
            ScrollPane scrollPane = new ScrollPane();
            VBox contenidoCompleto = new VBox(15);
            contenidoCompleto.getChildren().addAll(headerBox, contenidoReporte, barraNota);
            scrollPane.setContent(contenidoCompleto);
            scrollPane.setFitToWidth(true);

            contenido.getChildren().addAll(scrollPane, botonesBox);

            // Configurar y mostrar la ventana modal
            Scene escena = new Scene(contenido, 800, 700);
            ventanaPrevia.setScene(escena);
            ventanaPrevia.initModality(Modality.APPLICATION_MODAL);
            ventanaPrevia.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo mostrar la previsualización del reporte.");
        }
    }

    /**
     * Genera el contenido completo del reporte incluyendo tablas de datos, gráficas
     * y estadísticas
     * Crea una representación visual completa de todos los datos del período
     * seleccionado
     * 
     * @param datos Lista de mapas con los datos estadísticos a procesar
     * @return VBox conteniendo todo el contenido visual del reporte
     */
    private VBox generarContenidoReporteCompleto(List<Map<String, Object>> datos) {
        VBox contenido = new VBox(20);
        contenido.setStyle(
                "-fx-background-color: #2B2B2B; -fx-border-color: #2B2B2B; -fx-border-width: 1px; -fx-padding: 20; -fx-border-radius: 5px;");

        // Sección de resumen de datos tabulares
        Label tituloSeccion = new Label("RESUMEN DE VENTAS");
        tituloSeccion.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

        // Contenedor para la tabla de datos de ventas
        VBox tablaDatos = new VBox(5);
        tablaDatos.setStyle(
                "-fx-background-color: #2B2B2B; -fx-border-color: #ecf0f1; -fx-border-width: 1px; -fx-padding: 10; -fx-text-fill: #ecf0f1;");

        // Crear encabezados de la tabla de ventas
        HBox headerTabla = new HBox();
        headerTabla.setStyle("-fx-background-color: #02487b; -fx-padding: 8;");
        headerTabla.getChildren().addAll(
                crearCeldaTabla("Fecha", true),
                crearCeldaTabla("Tipo Boleto", true),
                crearCeldaTabla("Formato", true),
                crearCeldaTabla("Boletos", true),
                crearCeldaTabla("Ingresos", true));

        // Contenedor para las filas de datos de la tabla
        VBox filasDatos = new VBox(2);
        int totalBoletos = 0;
        double totalIngresos = 0;

        // Procesar cada registro de datos para crear filas de la tabla
        for (Map<String, Object> fila : datos) {
            // Extraer valores específicos del mapa de datos
            String fecha = fila.get("fecha").toString();
            int boletosVendidos = (int) fila.get("total_boletos_vendidos");
            double ingreso = (double) fila.get("ingreso_total");
            String tipoSala = (String) fila.get("tipos_sala");
            String formato = (String) fila.get("formatos");

            // Crear fila visual para la tabla
            HBox filaTabla = new HBox();
            filaTabla.setStyle("-fx-background-color: #2B2B2B; -fx-border-color: #2B2B2B; -fx-border-width: 0 0 1 0;");

            // Agregar celdas con datos a la fila (con valores por defecto si son nulos)
            filaTabla.getChildren().addAll(
                    crearCeldaTabla(fecha, false),
                    crearCeldaTabla(tipoSala != null ? tipoSala : "Normal", false),
                    crearCeldaTabla(formato != null ? formato : "2D", false),
                    crearCeldaTabla(String.valueOf(boletosVendidos), false),
                    crearCeldaTabla(String.format("$%.2f", ingreso), false));

            filasDatos.getChildren().add(filaTabla);
            // Acumular totales para la fila de resumen
            totalBoletos += boletosVendidos;
            totalIngresos += ingreso;
        }

        // Crear fila de totales al final de la tabla
        HBox totalRow = new HBox();
        totalRow.setStyle("-fx-background-color: #fdfdfdff; -fx-padding: 8;");
        totalRow.getChildren().addAll(
                crearCeldaTabla("TOTAL:", true),
                crearCeldaTabla("", true), // Columna vacía
                crearCeldaTabla("", true), // Columna vacía
                crearCeldaTabla(String.valueOf(totalBoletos), true),
                crearCeldaTabla(String.format("$%.2f", totalIngresos), true));

        // Ensamblar la tabla completa
        tablaDatos.getChildren().addAll(headerTabla, filasDatos, totalRow);

        // Sección de gráficas de análisis visual
        Label tituloGraficas = new Label("GRÁFICAS DE ANÁLISIS");
        tituloGraficas.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

        VBox graficasBox = new VBox(15);
        graficasBox.setStyle("-fx-border-color: #2B2B2B; -fx-border-width: 1px; -fx-padding: 15;");

        // Contenedor para gráfica de barras
        VBox graficaBarras = new VBox(10);
        graficaBarras.setStyle("-fx-background-color: #2B2B2B; -fx-padding: 15; -fx-border-radius: 5px;");
        Label lblGraficaBarras = new Label("Gráfica de Barras: Ventas por Tipo de Boleto (VIP vs Normal)");
        lblGraficaBarras.setStyle("-fx-font-weight: bold; -fx-text-fill: #ecf0f1; -fx-font-size: 14px;");

        // Crear la gráfica de barras con los datos procesados
        BarChart<String, Number> barChartPreview = crearGraficaBarrasPreview(datos);
        barChartPreview.setPrefHeight(300);
        barChartPreview.setPrefWidth(600);

        graficaBarras.getChildren().addAll(lblGraficaBarras, barChartPreview);

        // Contenedor para gráfica de pastel
        VBox graficaPastel = new VBox(10);
        graficaPastel.setStyle("-fx-background-color: #2B2B2B; -fx-padding: 15; -fx-border-radius: 5px;");
        Label lblGraficaPastel = new Label("Gráfica de Pastel: Distribución de Boletos por Formato (2D vs 3D)");
        lblGraficaPastel.setStyle("-fx-font-weight: bold; -fx-text-fill: #ecf0f1; -fx-font-size: 14px;");

        // Crear la gráfica de pastel con los datos procesados
        PieChart pieChartPreview = crearGraficaPastelPreview(datos);
        pieChartPreview.setPrefHeight(300);
        pieChartPreview.setPrefWidth(400);

        graficaPastel.getChildren().addAll(lblGraficaPastel, pieChartPreview);

        // Agregar ambas gráficas al contenedor
        graficasBox.getChildren().addAll(graficaBarras, graficaPastel);

        // Sección de estadísticas calculadas adicionales
        Label tituloEstadisticas = new Label("ESTADÍSTICAS ADICIONALES");
        tituloEstadisticas.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

        VBox estadisticasBox = new VBox(10);
        estadisticasBox.setStyle("-fx-border-color: #2B2B2B; -fx-border-width: 1px; -fx-padding: 15;");

        // Calcular estadísticas agrupadas por categorías
        Map<String, Integer> boletosPorTipo = new HashMap<>();
        Map<String, Integer> boletosPorFormato = new HashMap<>();

        // Procesar datos para obtener totales por categoría
        for (Map<String, Object> fila : datos) {
            String tipoSala = (String) fila.get("tipos_sala");
            String formato = (String) fila.get("formatos");
            int boletosVendidos = (int) fila.get("total_boletos_vendidos");

            // Agrupar por tipo de sala (VIP vs Normal)
            if (tipoSala != null) {
                boletosPorTipo.merge(tipoSala, boletosVendidos, Integer::sum);
            } else {
                boletosPorTipo.merge("Normal", boletosVendidos, Integer::sum);
            }

            // Agrupar por formato (2D vs 3D)
            if (formato != null) {
                boletosPorFormato.merge(formato, boletosVendidos, Integer::sum);
            } else {
                boletosPorFormato.merge("2D", boletosVendidos, Integer::sum);
            }
        }

        // Crear elementos visuales para cada estadística calculada
        estadisticasBox.getChildren().addAll(
                crearEstadistica("Total de Boletos Vendidos", String.valueOf(totalBoletos)),
                crearEstadistica("Total de Ingresos", String.format("$%.2f", totalIngresos)),
                crearEstadistica("Promedio por Boleto",
                        String.format("$%.2f", totalBoletos > 0 ? totalIngresos / totalBoletos : 0)),
                crearEstadistica("Boletos VIP", String.valueOf(boletosPorTipo.getOrDefault("VIP", 0))),
                crearEstadistica("Boletos Normal", String.valueOf(boletosPorTipo.getOrDefault("Normal", 0))),
                crearEstadistica("Boletos 2D", String.valueOf(boletosPorFormato.getOrDefault("2D", 0))),
                crearEstadistica("Boletos 3D", String.valueOf(boletosPorFormato.getOrDefault("3D", 0))));

        // Ensamblar todo el contenido del reporte
        contenido.getChildren().addAll(tituloSeccion, tablaDatos, tituloGraficas, graficasBox, tituloEstadisticas,
                estadisticasBox);

        return contenido;
    }

    /**
     * Crea un elemento visual para mostrar una estadística individual
     * Muestra el título de la estadística y su valor correspondiente
     * 
     * @param titulo El nombre descriptivo de la estadística
     * @param valor  El valor numérico o textual de la estadística
     * @return HBox conteniendo la estadística formateada
     */
    private HBox crearEstadistica(String titulo, String valor) {
        HBox estadistica = new HBox(10);
        estadistica.setStyle("-fx-background-color: #2B2B2B; -fx-padding: 8; -fx-border-radius: 3px;");

        // Etiqueta para el título de la estadística
        Label lblTitulo = new Label(titulo + ":");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-text-fill: #ecf0f1; -fx-min-width: 150;");

        // Etiqueta para el valor de la estadística con color distintivo
        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-text-fill: #02487b; -fx-font-weight: bold;");

        estadistica.getChildren().addAll(lblTitulo, lblValor);
        return estadistica;
    }

    /**
     * Crea una gráfica de barras personalizada para la previsualización del reporte
     * Agrupa los datos por fecha y tipo de boleto (VIP vs Normal)
     * 
     * @param datos Lista de datos estadísticos a graficar
     * @return BarChart configurado con los datos procesados
     */
    private BarChart<String, Number> crearGraficaBarrasPreview(List<Map<String, Object>> datos) {
        // Crear ejes para la gráfica
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChartPreview = new BarChart<>(xAxis, yAxis);

        barChartPreview.setTitle("Ventas por Tipo de Boleto (VIP vs Normal)");
        barChartPreview.setStyle("-fx-background-color: #2B2B2B; -fx-border-color: #2B2B2B; -fx-border-width: 1px;");
        xAxis.setLabel("Fecha");
        yAxis.setLabel("Cantidad de Boletos Vendidos");
        xAxis.setStyle("-fx-tick-label-fill: #ecf0f1; -fx-font-weight: bold;");
        yAxis.setStyle("-fx-tick-label-fill: #ecf0f1; -fx-font-weight: bold;");

        XYChart.Series<String, Number> serieVIP = new XYChart.Series<>();
        serieVIP.setName("VIP");
        XYChart.Series<String, Number> serieNormal = new XYChart.Series<>();
        serieNormal.setName("Normal");

        // Agrupar datos por fecha y tipo, igual que en actualizarGraficaBarras
        Map<String, Map<String, Integer>> datosAgrupados = new HashMap<>();
        for (Map<String, Object> fila : datos) {
            String fecha = fila.get("fecha").toString();
            String tipoSala = (String) fila.get("tipos_sala");
            int boletosVendidos = (int) fila.get("total_boletos_vendidos");
            datosAgrupados.putIfAbsent(fecha, new HashMap<>());

            if (tipoSala != null && tipoSala.contains("VIP")) {
                if (tipoSala.contains("NORMAL")) {
                    int boletosVIP = boletosVendidos / 2;
                    int boletosNormal = boletosVendidos - boletosVIP;
                    datosAgrupados.get(fecha).merge("VIP", boletosVIP, Integer::sum);
                    datosAgrupados.get(fecha).merge("Normal", boletosNormal, Integer::sum);
                } else {
                    datosAgrupados.get(fecha).merge("VIP", boletosVendidos, Integer::sum);
                }
            } else {
                datosAgrupados.get(fecha).merge("Normal", boletosVendidos, Integer::sum);
            }
        }

        // Ordenar fechas
        List<String> fechasOrdenadas = new ArrayList<>(datosAgrupados.keySet());
        fechasOrdenadas.sort(Comparator.naturalOrder());

        for (String fecha : fechasOrdenadas) {
            Map<String, Integer> tiposEnFecha = datosAgrupados.get(fecha);
            serieVIP.getData().add(new XYChart.Data<>(fecha, tiposEnFecha.getOrDefault("VIP", 0)));
            serieNormal.getData().add(new XYChart.Data<>(fecha, tiposEnFecha.getOrDefault("Normal", 0)));
        }

        barChartPreview.getData().addAll(serieVIP, serieNormal);

        // Aplicar estilos personalizados a todos los elementos de la gráfica
        barChartPreview.applyCss();
        if (barChartPreview.lookup(".chart-title") != null)
            barChartPreview.lookup(".chart-title")
                    .setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 16px; -fx-font-weight: bold;");
        barChartPreview.lookupAll(".axis-label")
                .forEach(node -> node.setStyle("-fx-text-fill: #ecf0f1; -fx-font-weight: bold;"));
        barChartPreview.lookupAll(".chart-legend")
                .forEach(node -> node.setStyle("-fx-text-fill: #ecf0f1;"));

        return barChartPreview;
    }

    /**
     * Crea una gráfica de pastel personalizada para la previsualización del reporte
     * Muestra la distribución de boletos por formato (2D vs 3D)
     * 
     * @param datos Lista de datos estadísticos a procesar
     * @return PieChart configurado con la distribución de formatos
     */
    private PieChart crearGraficaPastelPreview(List<Map<String, Object>> datos) {
        PieChart pieChartPreview = new PieChart();
        pieChartPreview.setTitle("Distribución de Boletos por Formato (2D vs 3D)");
        pieChartPreview.setStyle("-fx-background-color: #2B2B2B; -fx-border-color: #ecf0f1; -fx-border-width: 1px;");

        // Contadores para cada tipo de formato
        int total2D = 0;
        int total3D = 0;

        // Procesar cada registro para agrupar por formato
        for (Map<String, Object> fila : datos) {
            String formatos = (String) fila.get("formatos");
            int totalBoletos = (int) fila.get("total_boletos_vendidos");

            // Distribuir boletos según los formatos disponibles en el registro
            if (formatos != null && !formatos.isEmpty()) {
                if (formatos.contains("2D") && formatos.contains("3D")) {
                    // Si contiene ambos formatos, distribuir equitativamente
                    total2D += totalBoletos / 2;
                    total3D += totalBoletos / 2;
                } else if (formatos.contains("2D")) {
                    total2D += totalBoletos; // Todos los boletos son 2D
                } else if (formatos.contains("3D")) {
                    total3D += totalBoletos; // Todos los boletos son 3D
                } else {
                    // Formato no reconocido, asumir 2D por defecto
                    total2D += totalBoletos;
                }
            } else {
                // Sin información de formato, asumir 2D
                total2D += totalBoletos;
            }
        }

        // Agregar segmentos al gráfico solo si hay datos para mostrar
        if (total2D > 0) {
            pieChartPreview.getData().add(new PieChart.Data("2D (" + total2D + ")", total2D));
        }
        if (total3D > 0) {
            pieChartPreview.getData().add(new PieChart.Data("3D (" + total3D + ")", total3D));
        }

        // Configurar propiedades de visualización del gráfico
        pieChartPreview.setLabelLineLength(10);
        pieChartPreview.setLabelsVisible(true);

        // Listener para aplicar estilos personalizados a las etiquetas dinámicamente
        pieChartPreview.getData().addListener((javafx.collections.ListChangeListener<PieChart.Data>) c -> {
            pieChartPreview.applyCss();
            pieChartPreview.lookupAll(".chart-pie-label")
                    .forEach(node -> node.setStyle("-fx-text-fill: #ecf0f1; -fx-font-weight: bold;"));
        });

        // Aplicar estilos inmediatamente al crear la gráfica
        pieChartPreview.applyCss();
        if (pieChartPreview.lookup(".chart-title") != null)
            pieChartPreview.lookup(".chart-title")
                    .setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 16px; -fx-font-weight: bold;");
        pieChartPreview.lookupAll(".chart-legend")
                .forEach(node -> node.setStyle("-fx-text-fill: #ecf0f1;"));
        pieChartPreview.lookupAll(".chart-pie-label")
                .forEach(node -> node.setStyle("-fx-text-fill: #ecf0f1; -fx-font-weight: bold;"));

        return pieChartPreview;
    }

    /**
     * Método auxiliar para crear celdas de tabla con formato consistente
     * Utilizado tanto en tablas de datos como en la previsualización
     * 
     * @param texto    El contenido textual de la celda
     * @param esHeader Indica si es una celda de encabezado (true) o de datos
     *                 (false)
     * @return Label configurado como celda de tabla con estilos apropiados
     */
    private Label crearCeldaTabla(String texto, boolean esHeader) {
        Label celda = new Label(texto);
        celda.setPrefWidth(120);
        celda.setMaxWidth(120);
        // Aplicar estilos diferentes según el tipo de celda
        celda.setStyle(esHeader ? "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5; -fx-alignment: center;"
                : "-fx-text-fill: #ecf0f1; -fx-padding: 5; -fx-alignment: center;");
        return celda;
    }

    @FXML
    void volverEscena(ActionEvent event) {
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

}