package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ControladorCartelera implements Initializable {

    private ServicioPelicula servicioPelicula;
    private Cartelera cartelera;
    private FuncionDAO funcionDAO;
    private PeliculaDAO peliculaDAO;

    // Componentes de la interfaz FXML
    @FXML private TextField txtBuscarTitulo;
    @FXML private TextField txtBuscarId;
    @FXML private GridPane grillaCartelera;

    @FXML private Button btnActualizarCartelera;
    @FXML private Button btnBuscarTitulo;
    @FXML private Button btnBuscarId;
    @FXML private Button btnLimpiarBusqueda;
    @FXML private Button btnVerDetalles;

    @FXML private Label lblTotalPeliculas;
    @FXML private Label lblEstadoCartelera;

    // Datos para la tabla
    private ObservableList<Pelicula> listaPeliculasCartelera;
    private ObservableList<Pelicula> peliculasFiltradas;

    // Variable para rastrear la película seleccionada
    private Pelicula peliculaSeleccionada;

    // Indicador de carga
    private ProgressIndicator indicadorCarga;
    private StackPane contenedorPrincipal;

    public ControladorCartelera() {
        this.servicioPelicula = new ServicioPelicula();
        this.cartelera = new Cartelera(new ArrayList<>());
        this.funcionDAO = new FuncionDAO();
        this.peliculaDAO = new PeliculaDAO();
        this.listaPeliculasCartelera = FXCollections.observableArrayList();
        this.peliculasFiltradas = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar el botón Ver Detalles como deshabilitado
        btnVerDetalles.setDisable(true);

        configurarIndicadorCarga();
        cargarCarteleraAsync();
    }

    private void configurarIndicadorCarga() {
        // Crear indicador de carga con estilos del tema
        if (indicadorCarga == null) {
            indicadorCarga = new ProgressIndicator();
            indicadorCarga.setVisible(false);
            indicadorCarga.setPrefSize(50, 50);
            indicadorCarga.setStyle("-fx-progress-color: #FF8F40;"); // Color naranja del tema Ayu
        }

        // Enfoque simplificado: no modificar la estructura FXML
        // Solo usar el indicador para mostrar estado en los labels
        System.out.println("Indicador de carga configurado con tema Ayu");
    }

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

    @FXML
    private void onActualizarCartelera(ActionEvent event) {
        actualizarCarteleraAsync();
    }

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
                // Aplicar estilos del tema Ayu Dark
                contenedor.setStyle("-fx-padding: 15; " +
                                  "-fx-background-color: #0D121A; " +
                                  "-fx-background-radius: 12; " +
                                  "-fx-border-color: #1F2430; " +
                                  "-fx-border-width: 1; " +
                                  "-fx-border-radius: 12; " +
                                  "-fx-effect: dropshadow(gaussian, rgba(255, 143, 64, 0.1), 20, 0, 0, 0);");

                ImageView imagen = new ImageView();
                imagen.setFitWidth(150);
                imagen.setFitHeight(200);
                imagen.setPreserveRatio(true);
                imagen.setStyle("-fx-background-radius: 8;");

                // Cargar imagen de forma asíncrona
                cargarImagenAsync(imagen, pelicula.getUrlImagen());

                Label titulo = new Label(pelicula.getTitulo());
                titulo.setStyle("-fx-font-weight: bold; " +
                              "-fx-font-size: 12px; " +
                              "-fx-text-fill: #CBCCC6;"); // Color de texto principal de Ayu
                titulo.setWrapText(true);

                Label genero = new Label(pelicula.getGenero());
                genero.setStyle("-fx-font-size: 10px; " +
                              "-fx-text-fill: #6C7079;"); // Gris suave para detalles

                Label anio = new Label("(" + pelicula.getAnio() + ")");
                anio.setStyle("-fx-font-size: 10px; " +
                            "-fx-text-fill: #6C7079;"); // Gris suave para detalles

                contenedor.getChildren().addAll(imagen, titulo, genero, anio);

                // Agregar evento de clic para seleccionar la película
                contenedor.setOnMouseClicked(event -> {
                    // Limpiar selección anterior
                    limpiarSeleccionPrevia();

                    // Marcar como seleccionada con colores del tema Ayu
                    contenedor.setStyle("-fx-padding: 15; " +
                                      "-fx-background-color: #0D121A; " +
                                      "-fx-background-radius: 12; " +
                                      "-fx-border-color: #FF8F40; " +
                                      "-fx-border-width: 3; " +
                                      "-fx-border-radius: 12; " +
                                      "-fx-effect: dropshadow(gaussian, rgba(255, 143, 64, 0.6), 15, 0, 0, 0);");
                    peliculaSeleccionada = pelicula;

                    // Habilitar botón de ver detalles
                    btnVerDetalles.setDisable(false);

                    // Actualizar estado
                    lblEstadoCartelera.setText("Película seleccionada: " + pelicula.getTitulo());
                });

                // Efecto hover con colores del tema Ayu
                contenedor.setOnMouseEntered(event -> {
                    if (peliculaSeleccionada != pelicula) {
                        contenedor.setStyle("-fx-padding: 15; " +
                                          "-fx-background-color: #0D121A; " +
                                          "-fx-background-radius: 12; " +
                                          "-fx-border-color: #4A505F; " +
                                          "-fx-border-width: 2; " +
                                          "-fx-border-radius: 12; " +
                                          "-fx-effect: dropshadow(gaussian, rgba(255, 143, 64, 0.2), 15, 0, 0, 0);");
                    }
                });

                contenedor.setOnMouseExited(event -> {
                    if (peliculaSeleccionada != pelicula) {
                        contenedor.setStyle("-fx-padding: 15; " +
                                          "-fx-background-color: #0D121A; " +
                                          "-fx-background-radius: 12; " +
                                          "-fx-border-color: #1F2430; " +
                                          "-fx-border-width: 1; " +
                                          "-fx-border-radius: 12; " +
                                          "-fx-effect: dropshadow(gaussian, rgba(255, 143, 64, 0.1), 20, 0, 0, 0);");
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

    private void limpiarSeleccionPrevia() {
        // Restaurar estilo normal a todos los contenedores con tema Ayu
        grillaCartelera.getChildren().forEach(node -> {
            if (node instanceof VBox) {
                node.setStyle("-fx-padding: 15; " +
                            "-fx-background-color: #0D121A; " +
                            "-fx-background-radius: 12; " +
                            "-fx-border-color: #1F2430; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 12; " +
                            "-fx-effect: dropshadow(gaussian, rgba(255, 143, 64, 0.1), 20, 0, 0, 0);");
            }
        });
        peliculaSeleccionada = null;
        btnVerDetalles.setDisable(true);
    }

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

    private void mostrarTodasLasPeliculas() {
        // Limpiar selección al mostrar todas
        peliculaSeleccionada = null;
        btnVerDetalles.setDisable(true);

        peliculasFiltradas.clear();
        peliculasFiltradas.addAll(listaPeliculasCartelera);
        actualizarGrilla();
        lblEstadoCartelera.setText("Mostrando todas las películas.");
    }

    private void actualizarEstadisticas() {
        int total = peliculasFiltradas.size();
        lblTotalPeliculas.setText("Películas mostradas: " + total + " de " + listaPeliculasCartelera.size());
    }

    private void mostrarDetallesPelicula(Pelicula pelicula) {
        Alert detalles = new Alert(Alert.AlertType.INFORMATION);
        detalles.setTitle("Detalles de la Película");
        detalles.setHeaderText(pelicula.getTitulo());

        StringBuilder contenido = new StringBuilder();
        contenido.append("ID: ").append(pelicula.getId()).append("\n");
        contenido.append("Año: ").append(pelicula.getAnio()).append("\n");
        contenido.append("Género: ").append(pelicula.getGenerosComoString()).append("\n");
        contenido.append("Duración: ").append(pelicula.getDuracionMinutos()).append(" minutos\n");
        if (pelicula.getIdioma() != null) {
            contenido.append("Idioma: ").append(pelicula.getIdioma().getNombre()).append("\n");
        }
        if (pelicula.getSinopsis() != null && !pelicula.getSinopsis().isEmpty()) {
            contenido.append("\nSinopsis:\n").append(pelicula.getSinopsis());
        }

        detalles.setContentText(contenido.toString());
        detalles.showAndWait();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void onBuscarTitulo(ActionEvent event) {
        buscarPorTitulo();
    }

    @FXML
    private void onBuscarId(ActionEvent event) {
        buscarPorId();
    }

    @FXML
    private void onLimpiarBusqueda(ActionEvent event) {
        txtBuscarTitulo.clear();
        txtBuscarId.clear();
        mostrarTodasLasPeliculas();
    }

    @FXML
    private void onVerDetalles(ActionEvent event) {
        if (peliculaSeleccionada != null) {
            mostrarDetallesPelicula(peliculaSeleccionada);
        } else {
            mostrarError("Sin selección", "Por favor, seleccione una película de la cartelera para ver sus detalles.");
        }
    }
}
