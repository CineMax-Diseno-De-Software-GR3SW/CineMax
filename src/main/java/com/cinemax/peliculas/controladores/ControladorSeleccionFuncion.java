package com.cinemax.peliculas.controladores;

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
import com.cinemax.peliculas.modelos.entidades.Idioma;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.persistencia.FuncionDAO;
import com.cinemax.peliculas.modelos.persistencia.PeliculaDAO;
import com.cinemax.peliculas.servicios.ServicioFuncion;
import com.cinemax.peliculas.servicios.ServicioPelicula;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.servicios.SalaService;
import com.cinemax.comun.ManejadorMetodosComunes;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.util.StringConverter;

public class ControladorSeleccionFuncion implements Initializable {

    private ServicioPelicula servicioPelicula;
    private ServicioFuncion servicioFuncion;
    private Cartelera cartelera;
    private FuncionDAO funcionDAO;
    private PeliculaDAO peliculaDAO;
    private SalaService salaService;

    // Variables de estado
    private Pelicula peliculaSeleccionada;
    private LocalDate fechaSeleccionada;
    private List<Funcion> funcionesDisponibles;
    private ToggleGroup grupoFechas;

    // Control de concurrencia
    private final AtomicBoolean cargandoPeliculas = new AtomicBoolean(false);
    private final AtomicBoolean cargandoFunciones = new AtomicBoolean(false);
    private Task<?> tareaActualPeliculas;
    private Task<?> tareaActualFunciones;

    // Indicadores de carga
    private ProgressIndicator indicadorCargaPeliculas;
    private ProgressIndicator indicadorCargaFunciones;
    private StackPane contenedorPeliculas;
    private StackPane contenedorFuncionesConIndicador;

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

    @FXML private Label lblPeliculaSeleccionada;
    @FXML private Label lblFechaSeleccionada;
    @FXML private Label lblTotalPeliculas;
    @FXML private Label lblTotalFunciones;
    @FXML private Label lblEstadoSeleccion;

    public ControladorSeleccionFuncion() {
        this.servicioPelicula = new ServicioPelicula();
        this.servicioFuncion = new ServicioFuncion();
        this.cartelera = new Cartelera(new ArrayList<>());
        this.funcionDAO = new FuncionDAO();
        this.peliculaDAO = new PeliculaDAO();
        this.salaService = new SalaService();
        this.funcionesDisponibles = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarIndicadoresCarga();
        configurarFiltrosAsync();
        configurarGrupoFechas();
        configurarEventos();
        actualizarCarteleraAsync();

        // Seleccionar hoy por defecto
        fechaSeleccionada = LocalDate.now();
        btnDiaHoy.setSelected(true);
        actualizarLabelFecha();
    }

    private void configurarIndicadoresCarga() {
        // Configurar indicador para películas
        indicadorCargaPeliculas = new ProgressIndicator();
        indicadorCargaPeliculas.setVisible(false);
        indicadorCargaPeliculas.setPrefSize(50, 50);

        contenedorPeliculas = new StackPane();
        contenedorPeliculas.getChildren().addAll(grillaPeliculas, indicadorCargaPeliculas);

        // Configurar indicador para funciones
        indicadorCargaFunciones = new ProgressIndicator();
        indicadorCargaFunciones.setVisible(false);
        indicadorCargaFunciones.setPrefSize(40, 40);

        contenedorFuncionesConIndicador = new StackPane();
        contenedorFuncionesConIndicador.getChildren().addAll(contenedorFunciones, indicadorCargaFunciones);

        // Reemplazar contenedores en la interfaz si es necesario
        if (grillaPeliculas.getParent() instanceof VBox) {
            VBox parent = (VBox) grillaPeliculas.getParent();
            int index = parent.getChildren().indexOf(grillaPeliculas);
            parent.getChildren().remove(grillaPeliculas);
            parent.getChildren().add(index, contenedorPeliculas);
        }

        if (contenedorFunciones.getParent() instanceof VBox) {
            VBox parent = (VBox) contenedorFunciones.getParent();
            int index = parent.getChildren().indexOf(contenedorFunciones);
            parent.getChildren().remove(contenedorFunciones);
            parent.getChildren().add(index, contenedorFuncionesConIndicador);
        }
    }

    private void mostrarIndicadorCargaPeliculas(boolean mostrar) {
        Platform.runLater(() -> {
            indicadorCargaPeliculas.setVisible(mostrar);
            grillaPeliculas.setDisable(mostrar);
            btnActualizarCartelera.setDisable(mostrar);

            // Bloquear búsqueda durante carga de películas
            txtBuscarPelicula.setDisable(mostrar);
            btnLimpiarBusqueda.setDisable(mostrar);

            // Solo establecer texto si no está vinculado
            if (!lblEstadoSeleccion.textProperty().isBound() && mostrar) {
                lblEstadoSeleccion.setText("Cargando películas...");
            }
        });
    }

    private void mostrarIndicadorCargaFunciones(boolean mostrar) {
        Platform.runLater(() -> {
            indicadorCargaFunciones.setVisible(mostrar);
            contenedorFunciones.setDisable(mostrar);

            // Bloquear todos los filtros durante carga de funciones
            cmbFiltroFormato.setDisable(mostrar);
            cmbFiltroSala.setDisable(mostrar);
            btnDiaHoy.setDisable(mostrar);
            btnDiaManana.setDisable(mostrar);
            btnDiaPasado.setDisable(mostrar);
            btnLimpiarBusqueda.setDisable(mostrar);

            // Solo establecer texto si no está vinculado
            if (!lblEstadoSeleccion.textProperty().isBound() && mostrar) {
                lblEstadoSeleccion.setText("Cargando funciones...");
            }
        });
    }

    private void configurarFiltrosAsync() {
        // Configurar filtro de formato
        Platform.runLater(() -> {
            cmbFiltroFormato.getItems().clear();
            cmbFiltroFormato.getItems().add(null); // Opción "Todos"
            for (FormatoFuncion formato : FormatoFuncion.values()) {
                cmbFiltroFormato.getItems().add(formato);
            }
            cmbFiltroFormato.setConverter(new StringConverter<FormatoFuncion>() {
                @Override
                public String toString(FormatoFuncion formato) {
                    return formato != null ? formato.toString() : "Todos";
                }

                @Override
                public FormatoFuncion fromString(String string) {
                    return null;
                }
            });
        });

        // Configurar filtro de sala
        Task<List<Sala>> task = new Task<List<Sala>>() {
            @Override
            protected List<Sala> call() throws Exception {
                updateMessage("Cargando salas para filtros...");
                return salaService.listarSalas();
            }
        };

        task.setOnSucceeded(e -> {
            List<Sala> salas = task.getValue();
            Platform.runLater(() -> {
                cmbFiltroSala.getItems().clear();
                cmbFiltroSala.getItems().add(null); // Opción "Todas"
                cmbFiltroSala.getItems().addAll(salas);
                cmbFiltroSala.setConverter(new StringConverter<Sala>() {
                    @Override
                    public String toString(Sala sala) {
                        return sala != null ? sala.getNombre() + " (" + sala.getTipo() + ")" : "Todas";
                    }

                    @Override
                    public Sala fromString(String string) {
                        return null;
                    }
                });
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                ManejadorMetodosComunes.mostrarVentanaError("No se pudieron cargar las salas: " +
                    task.getException().getMessage());
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void actualizarCarteleraAsync() {
        // Verificar si ya hay una tarea de carga de películas en ejecución
        if (cargandoPeliculas.compareAndSet(false, true)) {
            // Cancelar tarea anterior si existe
            if (tareaActualPeliculas != null && !tareaActualPeliculas.isDone()) {
                tareaActualPeliculas.cancel(true);
            }

            Task<List<Pelicula>> task = new Task<List<Pelicula>>() {
                @Override
                protected List<Pelicula> call() throws Exception {
                    updateMessage("Obteniendo películas de la cartelera...");

                    try {
                        List<Integer> idsPeliculas = funcionDAO.listarIdsPeliculasDeFuncionesFuturas();
                        List<Pelicula> nuevasPeliculas = new ArrayList<>();

                        updateMessage("Cargando detalles de películas...");

                        for (int i = 0; i < idsPeliculas.size(); i++) {
                            // Verificar si la tarea fue cancelada
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
                                // Log del error pero continúa con la siguiente película
                                System.err.println("Error cargando película ID " + id + ": " + e.getMessage());
                            }

                            // Actualizar progreso
                            updateProgress(i + 1, idsPeliculas.size());
                        }

                        return nuevasPeliculas;
                    } catch (Exception e) {
                        throw new RuntimeException("Error al conectar con la base de datos: " + e.getMessage(), e);
                    }
                }
            };

            task.setOnRunning(e -> mostrarIndicadorCargaPeliculas(true));

            task.setOnSucceeded(e -> {
                List<Pelicula> nuevasPeliculas = task.getValue();
                Platform.runLater(() -> {
                    try {
                        cartelera.setPeliculas(nuevasPeliculas);
                        mostrarPeliculasEnGrillaAsync();
                        mostrarIndicadorCargaPeliculas(false);
                        cargandoPeliculas.set(false);

                        // Desvincular y establecer mensaje final
                        lblEstadoSeleccion.textProperty().unbind();
                        lblEstadoSeleccion.setText("Cartelera actualizada correctamente - " + nuevasPeliculas.size() + " películas");
                    } catch (Exception ex) {
                        lblEstadoSeleccion.textProperty().unbind();
                        lblEstadoSeleccion.setText("Error al procesar cartelera");
                        cargandoPeliculas.set(false);
                    }
                });
            });

            task.setOnFailed(e -> {
                Platform.runLater(() -> {
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
                });
            });

            task.setOnCancelled(e -> {
                Platform.runLater(() -> {
                    mostrarIndicadorCargaPeliculas(false);
                    cargandoPeliculas.set(false);
                    lblEstadoSeleccion.setText("Carga cancelada");
                });
            });

            // Vincular el mensaje del task al label de estado solo si no hay binding previo
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
            // Ya hay una carga en progreso
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Ya hay una actualización de cartelera en progreso. Espere a que termine.");
        }
    }

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

        // Cargar imagen de forma asíncrona
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
        btnSeleccionar.setOnAction(e -> seleccionarPelicula(pelicula));
        btnSeleccionar.setPrefWidth(140);
        btnSeleccionar.getStyleClass().add("primary-button");

        tarjeta.getChildren().addAll(imagenPelicula, lblTitulo, lblInfo, btnSeleccionar);
        return tarjeta;
    }

    private void cargarImagenAsync(ImageView imageView, String urlImagen) {
        Task<Image> task = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                if (urlImagen != null && !urlImagen.isEmpty()) {
                    return new Image(urlImagen, true); // true para carga asíncrona
                } else {
                    return new Image(getClass().getResourceAsStream("/images/no-image.png"));
                }
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> imageView.setImage(task.getValue()));
        });

        task.setOnFailed(e -> {
            // Imagen por defecto en caso de error
            Platform.runLater(() -> {
                try {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/no-image.png"));
                    imageView.setImage(defaultImage);
                } catch (Exception ex) {
                    // Si no hay imagen por defecto, dejar vacío
                }
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void cargarFuncionesPeliculaSeleccionadaAsync() {
        if (peliculaSeleccionada == null || fechaSeleccionada == null) {
            contenedorFunciones.getChildren().clear();
            lblTotalFunciones.setText("Total de funciones: 0");
            lblEstadoSeleccion.setText("Seleccione una película y una fecha");
            return;
        }

        // Verificar si ya hay una tarea de carga de funciones en ejecución
        if (cargandoFunciones.compareAndSet(false, true)) {
            // Cancelar tarea anterior si existe
            if (tareaActualFunciones != null && !tareaActualFunciones.isDone()) {
                tareaActualFunciones.cancel(true);
            }

            Task<List<Funcion>> task = new Task<List<Funcion>>() {
                @Override
                protected List<Funcion> call() throws Exception {
                    updateMessage("Cargando funciones disponibles...");

                    try {
                        List<Funcion> todasLasFunciones = funcionDAO.listarTodasLasFunciones();
                        List<Funcion> funcionesFiltradas = new ArrayList<>();

                        for (Funcion funcion : todasLasFunciones) {
                            if (funcion.getPelicula().getId() == peliculaSeleccionada.getId() &&
                                funcion.getFechaHoraInicio().toLocalDate().equals(fechaSeleccionada)) {

                                // Aplicar filtros
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
            // Ya hay una carga en progreso - solo actualizar estado, sin mensaje intrusivo
            lblEstadoSeleccion.setText("Carga de funciones en progreso...");
        }
    }

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

        // Aquí podrías agregar lógica adicional como navegar a la compra de boletos
        lblEstadoSeleccion.setText("Función seleccionada: " + funcion.getFechaHoraInicio().format(formatter));
    }

    private void actualizarCartelera() {
        try {
            List<Integer> idsPeliculas = funcionDAO.listarIdsPeliculasDeFuncionesFuturas();
            List<Pelicula> nuevasPeliculas = new ArrayList<>();
            for (Integer id : idsPeliculas) {
                Pelicula p = peliculaDAO.buscarPorId(id);
                if (p != null && !nuevasPeliculas.contains(p)) {
                    nuevasPeliculas.add(p);
                }
            }
            cartelera.setPeliculas(nuevasPeliculas);
            mostrarPeliculasEnGrilla();
            lblEstadoSeleccion.setText("Cartelera actualizada correctamente");
        } catch (Exception e) {
            mostrarError("Error", "Error al actualizar la cartelera: " + e.getMessage());
        }
    }

    private void mostrarPeliculasEnGrilla() {
        grillaPeliculas.getChildren().clear();

        List<Pelicula> peliculasFiltradas = obtenerPeliculasFiltradas();
        int columnas = 2;
        int fila = 0;
        int columna = 0;

        for (Pelicula pelicula : peliculasFiltradas) {
            VBox tarjetaPelicula = crearTarjetaPelicula(pelicula);
            grillaPeliculas.add(tarjetaPelicula, columna, fila);

            columna++;
            if (columna >= columnas) {
                columna = 0;
                fila++;
            }
        }

        lblTotalPeliculas.setText("Películas mostradas: " + peliculasFiltradas.size());
    }

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

    private void filtrarPeliculas() {
        mostrarPeliculasEnGrillaAsync();
    }

    private VBox crearTarjetaPelicula(Pelicula pelicula) {
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

        try {
            if (pelicula.getImagenUrl() != null && !pelicula.getImagenUrl().isEmpty()) {
                Image imagen = new Image(pelicula.getImagenUrl(), true);
                imagenPelicula.setImage(imagen);
            } else {
                // Imagen por defecto
                Image imagenDefault = new Image(getClass().getResourceAsStream("/images/no-image.png"));
                imagenPelicula.setImage(imagenDefault);
            }
        } catch (Exception e) {
            // Si hay error cargando la imagen, usar una imagen por defecto o dejar vacío
            imagenPelicula.setImage(null);
        }

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
        btnSeleccionar.setOnAction(e -> seleccionarPelicula(pelicula));
        btnSeleccionar.setPrefWidth(140);
        btnSeleccionar.getStyleClass().add("primary-button");

        tarjeta.getChildren().addAll(imagenPelicula, lblTitulo, lblInfo, btnSeleccionar);
        return tarjeta;
    }

    private void seleccionarPelicula(Pelicula pelicula) {
        this.peliculaSeleccionada = pelicula;
        lblPeliculaSeleccionada.setText("Película: " + pelicula.getTitulo());
        cargarFuncionesPeliculaSeleccionadaAsync();
    }

    private void cargarFuncionesPeliculaSeleccionada() {
        if (peliculaSeleccionada == null || fechaSeleccionada == null) {
            contenedorFunciones.getChildren().clear();
            lblTotalFunciones.setText("Total de funciones: 0");
            lblEstadoSeleccion.setText("Seleccione una película y una fecha");
            return;
        }

        try {
            // Obtener todas las funciones de la película para la fecha seleccionada
            List<Funcion> todasLasFunciones = funcionDAO.listarTodasLasFunciones();
            funcionesDisponibles.clear();

            for (Funcion funcion : todasLasFunciones) {
                if (funcion.getPelicula().getId() == peliculaSeleccionada.getId() &&
                    funcion.getFechaHoraInicio().toLocalDate().equals(fechaSeleccionada)) {

                    // Aplicar filtros solo para formato y sala
                    boolean pasaFiltros = true;


                    if (cmbFiltroFormato.getValue() != null) {
                        pasaFiltros = pasaFiltros && funcion.getFormato() == cmbFiltroFormato.getValue();
                    }

                    if (cmbFiltroSala.getValue() != null) {
                        pasaFiltros = pasaFiltros && funcion.getSala().getId() == cmbFiltroSala.getValue().getId();
                    }

                    if (pasaFiltros) {
                        funcionesDisponibles.add(funcion);
                    }
                }
            }

            mostrarFunciones();

        } catch (Exception e) {
            mostrarError("Error", "Error al cargar las funciones: " + e.getMessage());
        }
    }

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

    private HBox crearTarjetaFuncion(Funcion funcion) {
        HBox tarjeta = new HBox(15);
        tarjeta.setAlignment(Pos.CENTER_LEFT);
        tarjeta.setPadding(new Insets(10));
        tarjeta.getStyleClass().add("ticket-card");

        // Información de la función
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

        // Botón para seleccionar función
        Button btnSeleccionarFuncion = new Button("Seleccionar Función");
        btnSeleccionarFuncion.setOnAction(e -> seleccionarFuncion(funcion));
        btnSeleccionarFuncion.getStyleClass().add("stepper-button");

        HBox.setHgrow(infoFuncion, javafx.scene.layout.Priority.ALWAYS);
        tarjeta.getChildren().addAll(infoFuncion, btnSeleccionarFuncion);

        return tarjeta;
    }


    private void actualizarLabelFecha() {
        if (fechaSeleccionada != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM");
            lblFechaSeleccionada.setText("Funciones para " + fechaSeleccionada.format(formatter));
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaError(mensaje != null ? mensaje : "Error desconocido");
    }

    private void configurarGrupoFechas() {
        grupoFechas = new ToggleGroup();
        btnDiaHoy.setToggleGroup(grupoFechas);
        btnDiaManana.setToggleGroup(grupoFechas);
        btnDiaPasado.setToggleGroup(grupoFechas);

        // Configurar textos con fechas (sin año)
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        btnDiaHoy.setText("Hoy " + hoy.format(formatter));
        btnDiaManana.setText(hoy.plusDays(1).format(formatter)); // Solo día y fecha
        btnDiaPasado.setText(hoy.plusDays(2).format(formatter)); // Solo día y fecha
    }

    private void configurarEventos() {
        // Búsqueda en tiempo real
        txtBuscarPelicula.textProperty().addListener((obs, oldText, newText) -> filtrarPeliculas());

        // Filtros - solo agregar listeners si no están ya configurados
        if (cmbFiltroFormato.getOnAction() == null) {
            cmbFiltroFormato.setOnAction(e -> cargarFuncionesPeliculaSeleccionadaAsync());
        }
        if (cmbFiltroSala.getOnAction() == null) {
            cmbFiltroSala.setOnAction(e -> cargarFuncionesPeliculaSeleccionadaAsync());
        }
    }

    @FXML
    private void onActualizarCartelera(ActionEvent event) {
        actualizarCarteleraAsync();
    }

    @FXML
    private void onLimpiarBusqueda(ActionEvent event) {
        txtBuscarPelicula.clear();
        cmbFiltroFormato.setValue(null);
        cmbFiltroSala.setValue(null);

        // Recargar funciones si hay una película seleccionada
        if (peliculaSeleccionada != null) {
            cargarFuncionesPeliculaSeleccionadaAsync();
        }
    }

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

        // Solo cargar funciones si hay una película seleccionada
        if (peliculaSeleccionada != null) {
            cargarFuncionesPeliculaSeleccionadaAsync();
        }
    }
}
