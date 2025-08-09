package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.peliculas.modelos.entidades.Cartelera;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.persistencia.FuncionDAO;
import com.cinemax.peliculas.modelos.persistencia.PeliculaDAO;
import com.cinemax.peliculas.servicios.ServicioPelicula;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controlador para la gestión y visualización de la cartelera cinematográfica.
 *
 * <p>Esta clase maneja la interfaz gráfica de la cartelera del cine, permitiendo
 * visualizar las películas disponibles con funciones futuras programadas.
 * Incluye funcionalidades de búsqueda, filtrado y navegación a detalles.
 *
 * <p>Funcionalidades principales:
 * <ul>
 *   <li>Carga asíncrona de películas con funciones futuras</li>
 *   <li>Visualización en grilla con imágenes y información básica</li>
 *   <li>Búsqueda por título e ID de película</li>
 *   <li>Selección de películas para ver detalles</li>
 *   <li>Indicadores de progreso durante cargas de datos</li>
 *   <li>Actualización manual de la cartelera</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public class ControladorCartelera implements Initializable {

    /** Servicio de negocio para operaciones con películas */
    private ServicioPelicula servicioPelicula;

    /** Modelo de cartelera que contiene las películas disponibles */
    private Cartelera cartelera;

    /** DAO para acceso a datos de funciones */
    private FuncionDAO funcionDAO;

    /** DAO para acceso a datos de películas */
    private PeliculaDAO peliculaDAO;

    // Componentes de la interfaz FXML
    /** Campo de texto para búsqueda por título */
    @FXML private TextField txtBuscarTitulo;

    /** Campo de texto para búsqueda por ID */
    @FXML private TextField txtBuscarId;

    /** Contenedor en grilla para mostrar las películas */
    @FXML private GridPane grillaCartelera;

    /** Botón para actualizar la cartelera */
    @FXML private Button btnActualizarCartelera;

    /** Botón para volver al menú principal */
    @FXML private Button btnVolver;

    /** Botón para buscar por título */
    @FXML private Button btnBuscarTitulo;

    /** Botón para buscar por ID */
    @FXML private Button btnBuscarId;

    /** Botón para limpiar búsquedas */
    @FXML private Button btnLimpiarBusqueda;

    /** Botón para ver detalles de película seleccionada */
    @FXML private Button btnVerDetalles;

    /** Label que muestra el total de películas */
    @FXML private Label lblTotalPeliculas;

    /** Label que muestra el estado actual de la cartelera */
    @FXML private Label lblEstadoCartelera;

    // Datos para la gestión de películas
    /** Lista observable de todas las películas en cartelera */
    private ObservableList<Pelicula> listaPeliculasCartelera;

    /** Lista observable de películas filtradas por búsqueda */
    private ObservableList<Pelicula> peliculasFiltradas;

    /** Película actualmente seleccionada por el usuario */
    private Pelicula peliculaSeleccionada;

    /** Indicador visual de progreso durante cargas */
    private ProgressIndicator indicadorCarga;

    /** Contenedor principal para el indicador de carga */
    private StackPane contenedorPrincipal;

    /**
     * Constructor que inicializa los servicios y estructuras de datos.
     */
    public ControladorCartelera() {
        this.servicioPelicula = new ServicioPelicula();
        this.cartelera = new Cartelera(new ArrayList<>());
        this.funcionDAO = new FuncionDAO();
        this.peliculaDAO = new PeliculaDAO();
        this.listaPeliculasCartelera = FXCollections.observableArrayList();
        this.peliculasFiltradas = FXCollections.observableArrayList();
    }

    /**
     * Inicializa el controlador después de que se ha cargado el FXML.
     *
     * @param location La ubicación utilizada para resolver rutas relativas para el objeto raíz
     * @param resources Los recursos utilizados para localizar el objeto raíz
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar el botón Ver Detalles como deshabilitado
        btnVerDetalles.setDisable(true);

        configurarIndicadorCarga();
        cargarCarteleraAsync();
    }

    /**
     * Configura el indicador de progreso visual para operaciones asíncronas.
     */
    private void configurarIndicadorCarga() {
        // Crear indicador de carga con estilos del tema
        if (indicadorCarga == null) {
            indicadorCarga = new ProgressIndicator();
            indicadorCarga.setVisible(false);
            indicadorCarga.setPrefSize(50, 50);
            // Usar clase CSS en lugar de estilo inline
            indicadorCarga.getStyleClass().add("progress-indicator");
        }

        // Enfoque simplificado: no modificar la estructura FXML
        // Solo usar el indicador para mostrar estado en los labels
    }

    /**
     * Controla la visibilidad del indicador de carga y el estado de los controles.
     *
     * @param mostrar true para mostrar el indicador y deshabilitar controles, false para ocultarlo
     */
    private void mostrarIndicadorCarga(boolean mostrar) {
        Platform.runLater(() -> {
            indicadorCarga.setVisible(mostrar);
            grillaCartelera.setDisable(mostrar);
            btnActualizarCartelera.setDisable(mostrar);

            // Solo establecer texto si no está vinculado
            if (!lblEstadoCartelera.textProperty().isBound() && mostrar) {
                lblEstadoCartelera.setText("Cargando datos...");
            }
        });
    }

    /**
     * Carga la cartelera de forma asíncrona obteniendo películas con funciones futuras.
     *
     * <p>Este método ejecuta la carga en un hilo separado para no bloquear la UI,
     * mostrando indicadores de progreso y manejando errores apropiadamente.
     */
    private void cargarCarteleraAsync() {
        Task<List<Pelicula>> task = new Task<List<Pelicula>>() {
            @Override
            protected List<Pelicula> call() throws Exception {
                updateMessage("Obteniendo películas de la cartelera...");

                // Simular un pequeño delay para mostrar el indicador
                Thread.sleep(500);

                List<Integer> idsPeliculas = funcionDAO.listarIdsPeliculasDeFuncionesFuturas();
                List<Pelicula> peliculas = new ArrayList<>();

                updateMessage("Cargando detalles de películas...");

                for (int i = 0; i < idsPeliculas.size(); i++) {
                    Integer id = idsPeliculas.get(i);
                    Pelicula p = peliculaDAO.buscarPorId(id);
                    if (p != null && !peliculas.contains(p)) {
                        peliculas.add(p);
                    }

                    // Actualizar progreso
                    updateProgress(i + 1, idsPeliculas.size());
                }

                return peliculas;
            }
        };

        task.setOnRunning(e -> mostrarIndicadorCarga(true));

        task.setOnSucceeded(e -> {
            List<Pelicula> peliculas = task.getValue();
            Platform.runLater(() -> {
                try {
                    cartelera.setPeliculas(peliculas);
                    listaPeliculasCartelera.clear();
                    listaPeliculasCartelera.addAll(peliculas);

                    // Desvincular ANTES de llamar a mostrarTodasLasPeliculas
                    lblEstadoCartelera.textProperty().unbind();

                    mostrarTodasLasPeliculas();
                    mostrarIndicadorCarga(false);

                    // NO establecer mensaje aquí, ya que mostrarTodasLasPeliculas() ya lo hace
                } catch (Exception ex) {
                    lblEstadoCartelera.textProperty().unbind();
                    lblEstadoCartelera.setText("Error al procesar datos: " + ex.getMessage());
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                try {
                    mostrarIndicadorCarga(false);
                    lblEstadoCartelera.textProperty().unbind();
                    lblEstadoCartelera.setText("Error al cargar la cartelera");
                    mostrarError("Error al cargar la cartelera", task.getException().getMessage());
                } catch (Exception ex) {
                    // Capturar cualquier error adicional
                    lblEstadoCartelera.setText("Error de carga");
                }
            });
        });

        // NO vincular el mensaje del task para evitar conflictos
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Maneja el evento de actualización manual de la cartelera.
     *
     * @param event Evento de acción del botón
     */
    @FXML
    private void onActualizarCartelera(ActionEvent event) {
        actualizarCarteleraAsync();
    }

    /**
     * Actualiza la cartelera de forma asíncrona y muestra mensaje de confirmación.
     */
    private void actualizarCarteleraAsync() {
        Task<List<Pelicula>> task = new Task<List<Pelicula>>() {
            @Override
            protected List<Pelicula> call() throws Exception {
                updateMessage("Actualizando cartelera...");

                List<Integer> idsPeliculas = funcionDAO.listarIdsPeliculasDeFuncionesFuturas();
                List<Pelicula> nuevasPeliculas = new ArrayList<>();

                updateMessage("Procesando películas...");

                for (int i = 0; i < idsPeliculas.size(); i++) {
                    Integer id = idsPeliculas.get(i);
                    Pelicula p = peliculaDAO.buscarPorId(id);
                    if (p != null && !nuevasPeliculas.contains(p)) {
                        nuevasPeliculas.add(p);
                    }
                    updateProgress(i + 1, idsPeliculas.size());
                }

                return nuevasPeliculas;
            }
        };

        task.setOnRunning(e -> mostrarIndicadorCarga(true));

        task.setOnSucceeded(e -> {
            List<Pelicula> nuevasPeliculas = task.getValue();
            Platform.runLater(() -> {
                try {
                    cartelera.setPeliculas(nuevasPeliculas);
                    listaPeliculasCartelera.clear();
                    listaPeliculasCartelera.addAll(nuevasPeliculas);

                    // Limpiar selección previa al actualizar
                    peliculaSeleccionada = null;
                    btnVerDetalles.setDisable(true);

                    mostrarTodasLasPeliculas();
                    mostrarIndicadorCarga(false);

                    // Desvincular y establecer mensaje final
                    lblEstadoCartelera.textProperty().unbind();
                    lblEstadoCartelera.setText("Cartelera actualizada - " + nuevasPeliculas.size() + " películas");

                    // Mostrar mensaje de confirmación como se solicitó
                    mostrarInformacion("Cartelera Actualizada",
                        "Cartelera actualizada con éxito. Se han cargado " + nuevasPeliculas.size() + " películas.");
                } catch (Exception ex) {
                    lblEstadoCartelera.textProperty().unbind();
                    lblEstadoCartelera.setText("Error al actualizar");
                    mostrarError("Error", "Error al procesar la actualización: " + ex.getMessage());
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                try {
                    mostrarIndicadorCarga(false);
                    lblEstadoCartelera.textProperty().unbind();
                    lblEstadoCartelera.setText("Error al actualizar la cartelera");

                    String mensajeError = task.getException().getMessage();
                    mostrarError("Error al actualizar la cartelera",
                        "No se pudo actualizar la cartelera.\n\nDetalles del error:\n" +
                        (mensajeError != null ? mensajeError : "Error desconocido"));
                } catch (Exception ex) {
                    lblEstadoCartelera.setText("Error de actualización");
                    mostrarError("Error", "Error crítico durante la actualización");
                }
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Actualiza la grilla visual con las películas proporcionadas.
     *
     * @param peliculas Lista de películas a mostrar en la grilla
     */
    private void actualizarGrilla(List<Pelicula> peliculas) {
        grillaCartelera.getChildren().clear();

        int row = 0;
        int col = 0;
        for (Pelicula pelicula : peliculas) {
            ImageView imagenPelicula = new ImageView(new Image(pelicula.getUrlImagen()));
            imagenPelicula.setFitWidth(150); // Ajustar tamaño de las imágenes
            imagenPelicula.setFitHeight(200);

            Label titulo = new Label(pelicula.getTitulo());
            Label genero = new Label(pelicula.getGenero());
            Label anio = new Label(String.valueOf(pelicula.getAnio()));

            VBox item = new VBox(10, imagenPelicula, titulo, genero, anio);
            item.setAlignment(Pos.CENTER);
            item.setPrefWidth(200); // Ajustar tamaño de las grillas individuales

            grillaCartelera.add(item, col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private void actualizarGrilla() {
        Platform.runLater(() -> {
            grillaCartelera.getChildren().clear();
            int columnas = 3;
            int fila = 0;
            int columna = 0;

            for (Pelicula pelicula : peliculasFiltradas) {
                VBox contenedor = new VBox();
                contenedor.setAlignment(Pos.CENTER);
                contenedor.setSpacing(5);
                contenedor.setPrefWidth(180);
                // Usar clases CSS del tema styles.css
                contenedor.getStyleClass().add("ticket-card");

                ImageView imagen = new ImageView();
                imagen.setFitWidth(150);
                imagen.setFitHeight(200);
                imagen.setPreserveRatio(true);

                // Cargar imagen de forma asíncrona
                cargarImagenAsync(imagen, pelicula.getUrlImagen());

                Label titulo = new Label(pelicula.getTitulo());
                // Usar clase CSS para el título
                titulo.getStyleClass().add("ticket-price");
                titulo.setWrapText(true);

                Label genero = new Label(pelicula.getGenero());
                // Usar clase CSS para detalles
                genero.getStyleClass().add("summary-details");

                Label anio = new Label("(" + pelicula.getAnio() + ")");
                // Usar clase CSS para detalles
                anio.getStyleClass().add("summary-details");

                contenedor.getChildren().addAll(imagen, titulo, genero, anio);

                // Agregar evento de clic para seleccionar la película
                contenedor.setOnMouseClicked(event -> {
                    // Limpiar selección anterior
                    limpiarSeleccionPrevia();

                    // Marcar como seleccionada - usar borde de color naranja del tema
                    contenedor.setStyle("-fx-border-color: #F25F00; -fx-border-width: 3px; -fx-border-radius: 12px;");
                    peliculaSeleccionada = pelicula;

                    // Habilitar botón de ver detalles
                    btnVerDetalles.setDisable(false);

                    // Actualizar estado
                    lblEstadoCartelera.setText("Película seleccionada: " + pelicula.getTitulo());
                });

                // Efecto hover - usar borde sutil
                contenedor.setOnMouseEntered(event -> {
                    if (peliculaSeleccionada != pelicula) {
                        contenedor.setStyle("-fx-border-color: #4A4A4A; -fx-border-width: 2px; -fx-border-radius: 12px;");
                    }
                });

                contenedor.setOnMouseExited(event -> {
                    if (peliculaSeleccionada != pelicula) {
                        contenedor.setStyle(""); // Remover estilo hover
                    }
                });

                grillaCartelera.add(contenedor, columna, fila);

                columna++;
                if (columna == columnas) {
                    columna = 0;
                    fila++;
                }
            }

            actualizarEstadisticas();
        });
    }

    /**
     * Limpia la selección visual previa de películas en la grilla.
     */
    private void limpiarSeleccionPrevia() {
        // Restaurar estilo normal a todos los contenedores removiendo bordes
        grillaCartelera.getChildren().forEach(node -> {
            if (node instanceof VBox) {
                node.setStyle(""); // Limpiar todos los estilos inline
            }
        });
        peliculaSeleccionada = null;
        btnVerDetalles.setDisable(true);
    }

    /**
     * Carga una imagen de forma asíncrona en un ImageView.
     *
     * @param imageView El ImageView donde cargar la imagen
     * @param urlImagen URL de la imagen a cargar
     */
    private void cargarImagenAsync(ImageView imageView, String urlImagen) {
        Task<Image> task = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                return new Image(urlImagen, true); // true para carga asíncrona
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

    /**
     * Realiza búsqueda de películas por título.
     */
    @FXML
    private void buscarPorTitulo() {
        String titulo = txtBuscarTitulo.getText().trim().toLowerCase();
        if (titulo.isEmpty()) {
            mostrarTodasLasPeliculas();
            return;
        }

        // Limpiar selección al hacer nueva búsqueda
        peliculaSeleccionada = null;
        btnVerDetalles.setDisable(true);

        peliculasFiltradas.clear();
        for (Pelicula pelicula : listaPeliculasCartelera) {
            if (pelicula.getTitulo().toLowerCase().contains(titulo)) {
                peliculasFiltradas.add(pelicula);
            }
        }

        actualizarGrilla();
        lblEstadoCartelera.setText("Películas encontradas: " + peliculasFiltradas.size());
    }

    /**
     * Realiza búsqueda de películas por ID.
     */
    @FXML
    private void buscarPorId() {
        String idTexto = txtBuscarId.getText().trim();
        if (idTexto.isEmpty()) {
            mostrarTodasLasPeliculas();
            return;
        }

        // Limpiar selección al hacer nueva búsqueda
        peliculaSeleccionada = null;
        btnVerDetalles.setDisable(true);

        try {
            int id = Integer.parseInt(idTexto);
            peliculasFiltradas.clear();
            for (Pelicula pelicula : listaPeliculasCartelera) {
                if (pelicula.getId() == id) {
                    peliculasFiltradas.add(pelicula);
                    break;
                }
            }

            actualizarGrilla();
            if (peliculasFiltradas.isEmpty()) {
                lblEstadoCartelera.setText("No se encontró película con ID: " + id);
            } else {
                lblEstadoCartelera.setText("Película encontrada con ID: " + id);
            }
        } catch (NumberFormatException e) {
            lblEstadoCartelera.setText("Por favor ingrese un ID válido.");
        }
    }

    /**
     * Muestra todas las películas disponibles en la cartelera.
     */
    private void mostrarTodasLasPeliculas() {
        // Limpiar selección al mostrar todas
        peliculaSeleccionada = null;
        btnVerDetalles.setDisable(true);

        peliculasFiltradas.clear();
        peliculasFiltradas.addAll(listaPeliculasCartelera);
        actualizarGrilla();
        lblEstadoCartelera.setText("Mostrando todas las películas.");
    }

    /**
     * Actualiza las estadísticas mostradas en la interfaz.
     */
    private void actualizarEstadisticas() {
        int total = peliculasFiltradas.size();
        lblTotalPeliculas.setText("Películas mostradas: " + total + " de " + listaPeliculasCartelera.size());
    }

    /**
     * Navega a la pantalla de detalles de la película seleccionada.
     *
     * @param pelicula Película de la cual mostrar detalles
     */
    private void navegarADetallesCartelera(Pelicula pelicula) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/peliculas/PantallaDetallesCartelera.fxml"));
            Parent root = loader.load();
            
            // Configurar el controlador con la película seleccionada
            ControladorDetallesCartelera controlador = loader.getController();
            controlador.cargarPelicula(pelicula);
            
            Stage stage = (Stage) btnActualizarCartelera.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("Error de navegación", "No se pudo abrir los detalles: " + e.getMessage());
        }
    }

    /**
     * Muestra un mensaje de error al usuario.
     *
     * @param titulo Título del mensaje
     * @param mensaje Contenido del mensaje de error
     */
    private void mostrarError(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaError(mensaje != null ? mensaje : "Error desconocido");
    }

    /**
     * Muestra un mensaje informativo al usuario.
     *
     * @param titulo Título del mensaje
     * @param mensaje Contenido del mensaje informativo
     */
    private void mostrarInformacion(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaExito(mensaje != null ? mensaje : "Operación completada");
    }

    /**
     * Maneja el evento de búsqueda por título.
     *
     * @param event Evento de acción del botón
     */
    @FXML
    private void onBuscarTitulo(ActionEvent event) {
        buscarPorTitulo();
    }

    /**
     * Maneja el evento de búsqueda por ID.
     *
     * @param event Evento de acción del botón
     */
    @FXML
    private void onBuscarId(ActionEvent event) {
        buscarPorId();
    }

    /**
     * Maneja el evento de limpiar búsqueda.
     *
     * @param event Evento de acción del botón
     */
    @FXML
    private void onLimpiarBusqueda(ActionEvent event) {
        txtBuscarTitulo.clear();
        txtBuscarId.clear();
        mostrarTodasLasPeliculas();
    }

    /**
     * Maneja el evento de ver detalles de la película seleccionada.
     *
     * @param event Evento de acción del botón
     */
    @FXML
    private void onVerDetalles(ActionEvent event) {
        if (peliculaSeleccionada != null) {
            navegarADetallesCartelera(peliculaSeleccionada);
        } else {
            mostrarError("Sin selección", "Por favor, seleccione una película de la cartelera para ver sus detalles.");
        }
    }

        /**
     * Método público para obtener la cartelera (lista de películas)
     * Delega la lógica al servicio correspondiente
     *
     * @return Lista de películas disponibles en cartelera
     */
    public List<Pelicula> obtenerCartelera() {
        try {
            return servicioPelicula.listarTodasLasPeliculas();
        } catch (Exception e) {
            // Log del error pero no mostrar UI desde aquí
            System.err.println("Error al obtener cartelera: " + e.getMessage());
            return new ArrayList<>(); // Retornar lista vacía en caso de error
        }
    }

    /**
     * Maneja el evento de volver al portal principal.
     *
     * @param event Evento de acción del botón
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
