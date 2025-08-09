package com.cinemax.peliculas.controladores;

/**
 * Controlador para la selección de funciones cinematográficas.
 *
 * <p>Esta clase maneja la interfaz de usuario para la selección de películas y funciones
 * en el sistema CineMax. Proporciona funcionalidades para buscar películas, filtrar funciones
 * por diferentes criterios y permitir la selección de funciones específicas.
 *
 * <p>Funcionalidades principales:
 * <ul>
 *   <li>Carga asíncrona de cartelera de películas</li>
 *   <li>Búsqueda y filtrado de películas en tiempo real</li>
 *   <li>Selección de fechas para visualizar funciones</li>
 *   <li>Filtrado de funciones por formato, sala y fecha</li>
 *   <li>Navegación a selección de asientos y compra</li>
 *   <li>Gestión de estado de carga con indicadores visuales</li>
 * </ul>
 *
 * <p>La interfaz implementa carga asíncrona para mejorar la experiencia del usuario,
 * con indicadores de progreso y prevención de operaciones concurrentes conflictivas.
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cinemax.peliculas.modelos.entidades.Cartelera;
import com.cinemax.peliculas.modelos.entidades.FormatoFuncion;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.persistencia.FuncionDAO;
import com.cinemax.peliculas.modelos.persistencia.PeliculaDAO;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.servicios.SalaService;
import com.cinemax.comun.ManejadorMetodosComunes;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class ControladorSeleccionFuncion implements Initializable {

    // Servicios de negocio
    /** Servicio para operaciones con salas */
    private final SalaService salaService;
    
    // DAOs para acceso directo a datos
    /** DAO para acceso directo a funciones */
    private final FuncionDAO funcionDAO;
    
    /** DAO para acceso directo a películas */
    private final PeliculaDAO peliculaDAO;

    // Modelo de datos
    /** Cartelera actual con películas disponibles */
    private final Cartelera cartelera;
    
    /** Película actualmente seleccionada por el usuario */
    private Pelicula peliculaSeleccionada;
    
    /** Fecha seleccionada para mostrar funciones */
    private LocalDate fechaSeleccionada;
    
    /** Lista de funciones disponibles para la película y fecha seleccionadas */
    private final List<Funcion> funcionesDisponibles;

    // Control de concurrencia
    /** Bandera atómica para prevenir carga concurrente de películas */
    private final AtomicBoolean cargandoPeliculas = new AtomicBoolean(false);
    
    /** Bandera atómica para prevenir carga concurrente de funciones */
    private final AtomicBoolean cargandoFunciones = new AtomicBoolean(false);
    
    /** Tarea actual de carga de películas */
    private Task<?> tareaActualPeliculas;
    
    /** Tarea actual de carga de funciones */
    private Task<?> tareaActualFunciones;

    // Indicadores de carga visual
    /** Indicador de progreso para carga de películas */
    private ProgressIndicator indicadorCargaPeliculas;
    
    /** Indicador de progreso para carga de funciones */
    private ProgressIndicator indicadorCargaFunciones;

    // Componentes de la interfaz FXML
    @FXML private TextField txtBuscarPelicula;
    @FXML private GridPane grillaPeliculas;
    @FXML private ComboBox<FormatoFuncion> cmbFiltroFormato;
    @FXML private ComboBox<Sala> cmbFiltroSala;
    @FXML private ToggleButton btnDiaHoy;
    @FXML private ToggleButton btnDiaManana;
    @FXML private ToggleButton btnDiaPasado;
    @FXML private VBox contenedorFunciones;
    @FXML private Button btnActualizarCartelera;
    @FXML private Button btnLimpiarBusqueda;
    @FXML private Button btnVolver;
    @FXML private Label lblPeliculaSeleccionada;
    @FXML private Label lblFechaSeleccionada;
    @FXML private Label lblTotalPeliculas;
    @FXML private Label lblTotalFunciones;
    @FXML private Label lblEstadoSeleccion;

    /**
     * Constructor que inicializa los servicios y estructuras de datos.
     */
    public ControladorSeleccionFuncion() {
        this.cartelera = new Cartelera(new ArrayList<>());
        this.funcionDAO = new FuncionDAO();
        this.peliculaDAO = new PeliculaDAO();
        this.salaService = new SalaService();
        this.funcionesDisponibles = new ArrayList<>();
    }

    /**
     * Inicializa el controlador después de cargar el archivo FXML.
     *
     * @param location Ubicación del archivo FXML
     * @param resources Recursos de localización
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarIndicadoresCarga();
        configurarFiltros();
        configurarGrupoFechas();
        configurarEventos();
        actualizarCarteleraAsync();

        // Seleccionar hoy por defecto
        fechaSeleccionada = LocalDate.now();
        btnDiaHoy.setSelected(true);
        actualizarLabelFecha();
    }

    /**
     * Configura los indicadores de carga visual para películas y funciones.
     * 
     * <p>Crea indicadores de progreso y los integra en contenedores de tipo StackPane
     * para superponer los indicadores sobre el contenido durante las operaciones asíncronas.
     */
    private void configurarIndicadoresCarga() {
        // Configurar indicador para películas
        indicadorCargaPeliculas = new ProgressIndicator();
        indicadorCargaPeliculas.setVisible(false);
        indicadorCargaPeliculas.setPrefSize(50, 50);

        StackPane contenedorPeliculas = new StackPane();
        contenedorPeliculas.getChildren().addAll(grillaPeliculas, indicadorCargaPeliculas);

        // Configurar indicador para funciones
        indicadorCargaFunciones = new ProgressIndicator();
        indicadorCargaFunciones.setVisible(false);
        indicadorCargaFunciones.setPrefSize(40, 40);

        StackPane contenedorFuncionesConIndicador = new StackPane();
        contenedorFuncionesConIndicador.getChildren().addAll(contenedorFunciones, indicadorCargaFunciones);

        // Reemplazar contenedores en la interfaz si es necesario
        if (grillaPeliculas.getParent() instanceof VBox parent) {
            int index = parent.getChildren().indexOf(grillaPeliculas);
            parent.getChildren().remove(grillaPeliculas);
            parent.getChildren().add(index, contenedorPeliculas);
        }

        if (contenedorFunciones.getParent() instanceof VBox parent) {
            int index = parent.getChildren().indexOf(contenedorFunciones);
            parent.getChildren().remove(contenedorFunciones);
            parent.getChildren().add(index, contenedorFuncionesConIndicador);
        }
    }

    /**
     * Muestra u oculta el indicador de carga para películas.
     *
     * @param mostrar true para mostrar el indicador, false para ocultarlo
     */
    private void mostrarIndicadorCargaPeliculas(boolean mostrar) {
        Platform.runLater(() -> {
            indicadorCargaPeliculas.setVisible(mostrar);
            grillaPeliculas.setDisable(mostrar);
            btnActualizarCartelera.setDisable(mostrar);
            txtBuscarPelicula.setDisable(mostrar);
            btnLimpiarBusqueda.setDisable(mostrar);

            if (!lblEstadoSeleccion.textProperty().isBound() && mostrar) {
                lblEstadoSeleccion.setText("Cargando películas...");
            }
        });
    }

    /**
     * Muestra u oculta el indicador de carga para funciones.
     *
     * @param mostrar true para mostrar el indicador, false para ocultarlo
     */
    private void mostrarIndicadorCargaFunciones(boolean mostrar) {
        Platform.runLater(() -> {
            indicadorCargaFunciones.setVisible(mostrar);
            contenedorFunciones.setDisable(mostrar);
            cmbFiltroFormato.setDisable(mostrar);
            cmbFiltroSala.setDisable(mostrar);
            btnDiaHoy.setDisable(mostrar);
            btnDiaManana.setDisable(mostrar);
            btnDiaPasado.setDisable(mostrar);
            btnLimpiarBusqueda.setDisable(mostrar);

            if (!lblEstadoSeleccion.textProperty().isBound() && mostrar) {
                lblEstadoSeleccion.setText("Cargando funciones...");
            }
        });
    }

    /**
     * Configura los ComboBox de filtros con sus opciones disponibles.
     * 
     * <p>Inicializa el filtro de formato con todos los valores del enum FormatoFuncion
     * y el filtro de salas con las salas disponibles del sistema.
     */
    private void configurarFiltros() {
        // Configurar filtro de formato
        cmbFiltroFormato.getItems().clear();
        cmbFiltroFormato.getItems().add(null); // Opción "Todos"
        for (FormatoFuncion formato : FormatoFuncion.values()) {
            cmbFiltroFormato.getItems().add(formato);
        }
        cmbFiltroFormato.setConverter(new StringConverter<>() {
            @Override
            public String toString(FormatoFuncion formato) {
                return formato != null ? formato.toString() : "Todos";
            }

            @Override
            public FormatoFuncion fromString(String string) {
                return null;
            }
        });

        // Configurar filtro de sala
        try {
            List<Sala> salas = salaService.listarSalas();
            cmbFiltroSala.getItems().clear();
            cmbFiltroSala.getItems().add(null); // Opción "Todas"
            cmbFiltroSala.getItems().addAll(salas);
            cmbFiltroSala.setConverter(new StringConverter<>() {
                @Override
                public String toString(Sala sala) {
                    return sala != null ? sala.getNombre() + " (" + sala.getTipo() + ")" : "Todas";
                }

                @Override
                public Sala fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("No se pudieron cargar las salas: " + e.getMessage());
        }
    }

    /**
     * Actualiza la cartelera de películas de forma asíncrona.
     * 
     * <p>Carga las películas que tienen funciones futuras programadas y las muestra
     * en la grilla. Utiliza control de concurrencia para prevenir múltiples cargas
     * simultáneas y proporciona retroalimentación visual del progreso.
     */
    private void actualizarCarteleraAsync() {
        if (cargandoPeliculas.compareAndSet(false, true)) {
            if (tareaActualPeliculas != null && !tareaActualPeliculas.isDone()) {
                tareaActualPeliculas.cancel(true);
            }

            Task<List<Pelicula>> task = new Task<>() {
                @Override
                protected List<Pelicula> call() {
                    updateMessage("Obteniendo películas de la cartelera...");

                    try {
                        List<Integer> idsPeliculas = funcionDAO.listarIdsPeliculasDeFuncionesFuturas();
                        List<Pelicula> nuevasPeliculas = new ArrayList<>();

                        updateMessage("Cargando detalles de películas...");

                        for (int i = 0; i < idsPeliculas.size(); i++) {
                            if (isCancelled()) {
                                break;
                            }

                            Integer id = idsPeliculas.get(i);
                            try {
                                Pelicula p = peliculaDAO.buscarPorId(id);
                                if (p != null && !nuevasPeliculas.contains(p)) {
                                    nuevasPeliculas.add(p);
                                }
                            } catch (Exception e) {
                                // Continúa con la siguiente película en caso de error
                            }

                            updateProgress(i + 1, idsPeliculas.size());
                        }

                        return nuevasPeliculas;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al conectar con la base de datos: " + e.getMessage(), e);
                    }
                }
            };

            task.setOnRunning(event -> mostrarIndicadorCargaPeliculas(true));

            task.setOnSucceeded(event -> {
                List<Pelicula> nuevasPeliculas = task.getValue();
                Platform.runLater(() -> {
                    try {
                        cartelera.setPeliculas(nuevasPeliculas);
                        mostrarPeliculasEnGrillaAsync();
                        mostrarIndicadorCargaPeliculas(false);
                        cargandoPeliculas.set(false);

                        lblEstadoSeleccion.textProperty().unbind();
                        lblEstadoSeleccion.setText("Cartelera actualizada correctamente - " + nuevasPeliculas.size() + " películas");
                    } catch (Exception ex) {
                        lblEstadoSeleccion.textProperty().unbind();
                        lblEstadoSeleccion.setText("Error al procesar cartelera");
                        cargandoPeliculas.set(false);
                    }
                });
            });

            task.setOnFailed(event -> Platform.runLater(() -> {
                try {
                    mostrarIndicadorCargaPeliculas(false);
                    cargandoPeliculas.set(false);
                    lblEstadoSeleccion.textProperty().unbind();
                    lblEstadoSeleccion.setText("Error al cargar la cartelera");

                    String mensajeError = task.getException() != null ?
                        task.getException().getMessage() : "Error desconocido al cargar la cartelera";
                    ManejadorMetodosComunes.mostrarVentanaError("Error al actualizar la cartelera: " + mensajeError);
                } catch (Exception ex) {
                    lblEstadoSeleccion.setText("Error de carga");
                    cargandoPeliculas.set(false);
                }
            }));

            task.setOnCancelled(event -> Platform.runLater(() -> {
                mostrarIndicadorCargaPeliculas(false);
                cargandoPeliculas.set(false);
                lblEstadoSeleccion.setText("Carga cancelada");
            }));

            try {
                lblEstadoSeleccion.textProperty().bind(task.messageProperty());
            } catch (Exception e) {
                lblEstadoSeleccion.setText("Iniciando carga...");
            }

            tareaActualPeliculas = task;
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        } else {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Ya hay una actualización de cartelera en progreso. Espere a que termine.");
        }
    }

    /**
     * Muestra las películas filtradas en la grilla de forma asíncrona.
     * 
     * <p>Aplica los filtros de búsqueda y organiza las películas en una grilla
     * de dos columnas con tarjetas visuales para cada película.
     */
    private void mostrarPeliculasEnGrillaAsync() {
        Platform.runLater(() -> {
            grillaPeliculas.getChildren().clear();

            List<Pelicula> peliculasFiltradas = obtenerPeliculasFiltradas();
            int columnas = 2;
            int fila = 0;
            int columna = 0;

            for (Pelicula pelicula : peliculasFiltradas) {
                VBox tarjetaPelicula = crearTarjetaPeliculaAsync(pelicula);
                grillaPeliculas.add(tarjetaPelicula, columna, fila);

                columna++;
                if (columna >= columnas) {
                    columna = 0;
                    fila++;
                }
            }

            lblTotalPeliculas.setText("Películas mostradas: " + peliculasFiltradas.size());
        });
    }

    /**
     * Crea una tarjeta visual para una película con carga asíncrona de imagen.
     *
     * @param pelicula La película para la cual crear la tarjeta
     * @return VBox conteniendo la tarjeta visual de la película
     */
    private VBox crearTarjetaPeliculaAsync(Pelicula pelicula) {
        VBox tarjeta = new VBox(8);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(10));
        tarjeta.getStyleClass().add("ticket-card");
        tarjeta.setPrefWidth(180);
        tarjeta.setMaxWidth(180);

        // Imagen de la película
        ImageView imagenPelicula = new ImageView();
        imagenPelicula.setFitWidth(140);
        imagenPelicula.setFitHeight(200);
        imagenPelicula.setPreserveRatio(false);

        cargarImagenAsync(imagenPelicula, pelicula.getImagenUrl());

        // Título de la película
        Label lblTitulo = new Label(pelicula.getTitulo());
        lblTitulo.getStyleClass().add("ticket-price");
        lblTitulo.setWrapText(true);
        lblTitulo.setMaxWidth(160);
        lblTitulo.setAlignment(Pos.CENTER);

        // Información adicional
        Label lblInfo = new Label(String.format("%d min • %s",
            pelicula.getDuracionMinutos(),
            pelicula.getIdioma() != null ? pelicula.getIdioma().getNombre() : "N/A"));
        lblInfo.getStyleClass().add("summary-details");

        // Botón para seleccionar
        Button btnSeleccionar = new Button("Seleccionar");
        btnSeleccionar.setOnAction(event -> seleccionarPelicula(pelicula));
        btnSeleccionar.setPrefWidth(140);
        btnSeleccionar.getStyleClass().add("primary-button");

        tarjeta.getChildren().addAll(imagenPelicula, lblTitulo, lblInfo, btnSeleccionar);
        return tarjeta;
    }

    /**
     * Carga una imagen de forma asíncrona en un ImageView.
     *
     * @param imageView El ImageView donde cargar la imagen
     * @param urlImagen La URL de la imagen a cargar
     */
    private void cargarImagenAsync(ImageView imageView, String urlImagen) {
        Task<Image> task = new Task<>() {
            @Override
            protected Image call() throws Exception {
                if (urlImagen != null && !urlImagen.isEmpty()) {
                    return new Image(urlImagen, true);
                } else {
                    return new Image(getClass().getResourceAsStream("/images/no-image.png"));
                }
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> imageView.setImage(task.getValue()));
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                try {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/no-image.png"));
                    imageView.setImage(defaultImage);
                } catch (Exception ex) {
                    // Imagen por defecto no disponible, dejar vacío
                }
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Carga las funciones disponibles para la película seleccionada de forma asíncrona.
     * 
     * <p>Obtiene todas las funciones que coinciden con la película seleccionada,
     * la fecha seleccionada y los filtros aplicados, luego las muestra en la interfaz.
     */
    private void cargarFuncionesPeliculaSeleccionadaAsync() {
        if (peliculaSeleccionada == null || fechaSeleccionada == null) {
            contenedorFunciones.getChildren().clear();
            lblTotalFunciones.setText("Total de funciones: 0");
            lblEstadoSeleccion.setText("Seleccione una película y una fecha");
            return;
        }

        if (cargandoFunciones.compareAndSet(false, true)) {
            if (tareaActualFunciones != null && !tareaActualFunciones.isDone()) {
                tareaActualFunciones.cancel(true);
            }

            Task<List<Funcion>> task = new Task<>() {
                @Override
                protected List<Funcion> call() throws Exception {
                    updateMessage("Cargando funciones disponibles...");

                    try {
                        List<Funcion> todasLasFunciones = funcionDAO.listarTodasLasFunciones();
                        List<Funcion> funcionesFiltradas = new ArrayList<>();

                        for (Funcion funcion : todasLasFunciones) {
                            if (funcion.getPelicula().getId() == peliculaSeleccionada.getId() &&
                                funcion.getFechaHoraInicio().toLocalDate().equals(fechaSeleccionada)) {

                                boolean pasaFiltros = true;

                                if (cmbFiltroFormato.getValue() != null) {
                                    pasaFiltros = pasaFiltros && funcion.getFormato() == cmbFiltroFormato.getValue();
                                }

                                if (cmbFiltroSala.getValue() != null) {
                                    pasaFiltros = pasaFiltros && funcion.getSala().getId() == cmbFiltroSala.getValue().getId();
                                }

                                if (pasaFiltros) {
                                    funcionesFiltradas.add(funcion);
                                }
                            }
                        }

                        return funcionesFiltradas;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al cargar funciones desde la base de datos: " + e.getMessage(), e);
                    }
                }
            };

            task.setOnRunning(e -> mostrarIndicadorCargaFunciones(true));

            task.setOnSucceeded(e -> {
                List<Funcion> funcionesEncontradas = task.getValue();
                Platform.runLater(() -> {
                    try {
                        funcionesDisponibles.clear();
                        funcionesDisponibles.addAll(funcionesEncontradas);
                        mostrarFunciones();
                        mostrarIndicadorCargaFunciones(false);
                        cargandoFunciones.set(false);
                        lblEstadoSeleccion.setText("Funciones cargadas para " + peliculaSeleccionada.getTitulo());
                    } catch (Exception ex) {
                        lblEstadoSeleccion.setText("Error al procesar funciones");
                        cargandoFunciones.set(false);
                    }
                });
            });

            task.setOnFailed(e -> {
                Platform.runLater(() -> {
                    try {
                        mostrarIndicadorCargaFunciones(false);
                        cargandoFunciones.set(false);
                        lblEstadoSeleccion.setText("Error al cargar las funciones");

                        String mensajeError = task.getException() != null ?
                            task.getException().getMessage() : "Error desconocido al cargar funciones";
                        ManejadorMetodosComunes.mostrarVentanaError("Error al cargar las funciones: " + mensajeError);
                    } catch (Exception ex) {
                        lblEstadoSeleccion.setText("Error de carga");
                        cargandoFunciones.set(false);
                    }
                });
            });

            task.setOnCancelled(e -> {
                Platform.runLater(() -> {
                    mostrarIndicadorCargaFunciones(false);
                    cargandoFunciones.set(false);
                    lblEstadoSeleccion.setText("Carga de funciones cancelada");
                });
            });

            tareaActualFunciones = task;
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        } else {
            lblEstadoSeleccion.setText("Carga de funciones en progreso...");
        }
    }

    /**
     * Procesa la selección de una función específica.
     *
     * @param funcion La función seleccionada por el usuario
     */
    private void seleccionarFuncion(Funcion funcion) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String contenido = String.format(
            "Película: %s\n" +
            "Sala: %s\n" +
            "Fecha y Hora: %s\n" +
            "Formato: %s\n" +
            "Idioma: %s",
            funcion.getPelicula().getTitulo(),
            funcion.getSala().getNombre(),
            funcion.getFechaHoraInicio().format(formatter),
            funcion.getFormato().toString(),
            funcion.getPelicula().getIdioma().getNombre()
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Función Seleccionada");
        alert.setHeaderText("Has seleccionado la siguiente función:");
        alert.setContentText(contenido);
        alert.showAndWait();

        lblEstadoSeleccion.setText("Función seleccionada: " + funcion.getFechaHoraInicio().format(formatter));
    }

    /**
     * Obtiene la lista de películas filtrada según el texto de búsqueda.
     *
     * @return Lista de películas que coinciden con los criterios de búsqueda
     */
    private List<Pelicula> obtenerPeliculasFiltradas() {
        List<Pelicula> peliculasFiltradas = new ArrayList<>();
        String textoBusqueda = txtBuscarPelicula.getText().toLowerCase().trim();

        for (Pelicula pelicula : cartelera.getPeliculas()) {
            boolean coincideTexto = textoBusqueda.isEmpty() ||
                pelicula.getTitulo().toLowerCase().contains(textoBusqueda);

            if (coincideTexto) {
                peliculasFiltradas.add(pelicula);
            }
        }

        return peliculasFiltradas;
    }

    /**
     * Aplica filtros y actualiza la visualización de películas.
     */
    private void filtrarPeliculas() {
        mostrarPeliculasEnGrillaAsync();
    }

    /**
     * Establece la película seleccionada y carga sus funciones.
     *
     * @param pelicula La película seleccionada por el usuario
     */
    private void seleccionarPelicula(Pelicula pelicula) {
        this.peliculaSeleccionada = pelicula;
        lblPeliculaSeleccionada.setText("Película: " + pelicula.getTitulo());
        cargarFuncionesPeliculaSeleccionadaAsync();
    }

    /**
     * Muestra las funciones disponibles en la interfaz de usuario.
     * 
     * <p>Crea tarjetas visuales para cada función disponible y las organiza
     * en el contenedor de funciones. Si no hay funciones, muestra un mensaje apropiado.
     */
    private void mostrarFunciones() {
        contenedorFunciones.getChildren().clear();

        if (funcionesDisponibles.isEmpty()) {
            Label lblSinFunciones = new Label("No hay funciones disponibles para los filtros seleccionados");
            lblSinFunciones.getStyleClass().add("summary-details");
            contenedorFunciones.getChildren().add(lblSinFunciones);
            lblTotalFunciones.setText("Total de funciones: 0");
            return;
        }

        for (Funcion funcion : funcionesDisponibles) {
            HBox tarjetaFuncion = crearTarjetaFuncion(funcion);
            contenedorFunciones.getChildren().add(tarjetaFuncion);
        }

        lblTotalFunciones.setText("Total de funciones: " + funcionesDisponibles.size());
        lblEstadoSeleccion.setText("Funciones cargadas para " + peliculaSeleccionada.getTitulo());
    }

    /**
     * Crea una tarjeta visual para mostrar información de una función.
     *
     * @param funcion La función para la cual crear la tarjeta
     * @return HBox conteniendo la tarjeta visual de la función
     */
    private HBox crearTarjetaFuncion(Funcion funcion) {
        HBox tarjeta = new HBox(15);
        tarjeta.setAlignment(Pos.CENTER_LEFT);
        tarjeta.setPadding(new Insets(10));
        tarjeta.getStyleClass().add("ticket-card");

        VBox infoFuncion = new VBox(5);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        Label lblHora = new Label(funcion.getFechaHoraInicio().format(timeFormatter));
        lblHora.getStyleClass().add("ticket-price");

        Label lblSala = new Label("Sala: " + funcion.getSala().getNombre());
        lblSala.getStyleClass().add("summary-details");

        Label lblFormato = new Label("Formato: " + funcion.getFormato().toString());
        lblFormato.getStyleClass().add("summary-details");

        Label lblIdioma = new Label("Idioma: " + funcion.getPelicula().getIdioma().getNombre());
        lblIdioma.getStyleClass().add("summary-details");

        infoFuncion.getChildren().addAll(lblHora, lblSala, lblFormato, lblIdioma);

        Button btnSeleccionarFuncion = new Button("Seleccionar Función");
        btnSeleccionarFuncion.setOnAction(e -> seleccionarFuncion(funcion));
        btnSeleccionarFuncion.getStyleClass().add("stepper-button");

        HBox.setHgrow(infoFuncion, javafx.scene.layout.Priority.ALWAYS);
        tarjeta.getChildren().addAll(infoFuncion, btnSeleccionarFuncion);

        return tarjeta;
    }

    /**
     * Actualiza el label de fecha con la fecha seleccionada formateada.
     */
    private void actualizarLabelFecha() {
        if (fechaSeleccionada != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM");
            lblFechaSeleccionada.setText("Funciones para " + fechaSeleccionada.format(formatter));
        }
    }

    /**
     * Muestra un mensaje de error utilizando el manejador común.
     *
     * @param titulo El título del mensaje de error (no utilizado)
     * @param mensaje El mensaje de error a mostrar
     */
    private void mostrarError(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaError(mensaje != null ? mensaje : "Error desconocido");
    }

    /**
     * Configura el grupo de botones para selección de fechas.
     * 
     * <p>Inicializa los botones de fecha (hoy, mañana, pasado mañana) con sus
     * respectivas fechas formateadas y los agrupa en un ToggleGroup.
     */
    private void configurarGrupoFechas() {
        ToggleGroup grupoFechas = new ToggleGroup();
        btnDiaHoy.setToggleGroup(grupoFechas);
        btnDiaManana.setToggleGroup(grupoFechas);
        btnDiaPasado.setToggleGroup(grupoFechas);

        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        btnDiaHoy.setText("Hoy " + hoy.format(formatter));
        btnDiaManana.setText(hoy.plusDays(1).format(formatter));
        btnDiaPasado.setText(hoy.plusDays(2).format(formatter));
    }

    /**
     * Configura los eventos y listeners de la interfaz de usuario.
     * 
     * <p>Establece listeners para búsqueda en tiempo real y filtros,
     * evitando duplicar listeners en múltiples inicializaciones.
     */
    private void configurarEventos() {
        txtBuscarPelicula.textProperty().addListener((obs, oldText, newText) -> filtrarPeliculas());

        if (cmbFiltroFormato.getOnAction() == null) {
            cmbFiltroFormato.setOnAction(event -> cargarFuncionesPeliculaSeleccionadaAsync());
        }
        if (cmbFiltroSala.getOnAction() == null) {
            cmbFiltroSala.setOnAction(event -> cargarFuncionesPeliculaSeleccionadaAsync());
        }
    }

    /**
     * Maneja el evento de actualización de cartelera.
     *
     * @param event El evento de acción
     */
    @FXML
    private void onActualizarCartelera(ActionEvent event) {
        actualizarCarteleraAsync();
    }

    /**
     * Maneja el evento de limpiar búsqueda y filtros.
     *
     * @param event El evento de acción
     */
    @FXML
    private void onLimpiarBusqueda(ActionEvent event) {
        txtBuscarPelicula.clear();
        cmbFiltroFormato.setValue(null);
        cmbFiltroSala.setValue(null);

        if (peliculaSeleccionada != null) {
            cargarFuncionesPeliculaSeleccionadaAsync();
        }
    }

    /**
     * Maneja el evento de selección de día.
     *
     * @param event El evento de acción del botón de fecha
     */
    @FXML
    private void onSeleccionarDia(ActionEvent event) {
        ToggleButton botonSeleccionado = (ToggleButton) event.getSource();

        if (botonSeleccionado == btnDiaHoy) {
            fechaSeleccionada = LocalDate.now();
        } else if (botonSeleccionado == btnDiaManana) {
            fechaSeleccionada = LocalDate.now().plusDays(1);
        } else if (botonSeleccionado == btnDiaPasado) {
            fechaSeleccionada = LocalDate.now().plusDays(2);
        }

        actualizarLabelFecha();

        if (peliculaSeleccionada != null) {
            cargarFuncionesPeliculaSeleccionadaAsync();
        }
    }

    /**
     * Maneja el evento de volver al portal principal.
     *
     * @param event El evento de acción
     */
    @FXML
    private void onVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo volver al portal: " + e.getMessage());
        }
    }
}
