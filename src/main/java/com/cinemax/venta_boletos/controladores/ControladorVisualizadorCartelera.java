package com.cinemax.venta_boletos.controladores;

import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.venta_boletos.servicios.ServicioVisualizadorCartelera;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.collections.ListChangeListener;
import javafx.stage.Stage;

import java.util.List;

/**
 * Controlador para la pantalla de visualización de la cartelera de películas.
 * 
 * Esta clase maneja:
 * - Carga y visualización de películas disponibles.
 * - Selección interactiva de películas mediante tarjetas visuales.
 * - Navegación hacia la pantalla de funciones de la película seleccionada.
 * - Gestión de imágenes de películas (incluyendo fallback para imágenes no
 * disponibles).
 * 
 * Flujo principal:
 * 1. Inicializa la lista de películas desde el servicio.
 * 2. Renderiza tarjetas visuales para cada película.
 * 3. Permite selección/deselección interactiva.
 * 4. Navega a funciones al confirmar selección.
 * 
 * @author GR3SW
 * @version 1.0
 */
public class ControladorVisualizadorCartelera {

    // ===== ELEMENTOS DE LA INTERFAZ (FXML) =====

    /** Contenedor FlowPane para organizar las tarjetas de películas */
    @FXML
    private FlowPane flowPanePeliculas;

    // ===== ATRIBUTOS DE LÓGICA =====

    /** Servicio para gestión de datos de la cartelera */
    private final ServicioVisualizadorCartelera servicioMostrarCartelera = new ServicioVisualizadorCartelera();

    /** Referencia a la tarjeta de película actualmente seleccionada */
    private VBox selectedMovieCard = null;

    // ===== MÉTODOS PRINCIPALES =====

    /**
     * Inicializa el controlador al cargar la vista FXML.
     * 
     * Configura:
     * 1. Carga inicial de películas
     * 2. Listener para cambios en la lista de películas
     * 3. Renderizado inicial de las tarjetas
     */
    @FXML
    public void initialize() {
        servicioMostrarCartelera.cargarPeliculasDeCartelera();

        servicioMostrarCartelera.getListaPeliculas().addListener((ListChangeListener<Pelicula>) c -> {
            while (c.next()) {
                if (c.wasAdded() || c.wasRemoved() || c.wasUpdated()) {
                    cargarCartelera(servicioMostrarCartelera.getListaPeliculas());
                }
            }
        });

        cargarCartelera(servicioMostrarCartelera.getListaPeliculas());
    }

    /**
     * Carga el display de películas en el FlowPane.
     * 
     * @param peliculas Lista de películas a mostrar
     */
    private void cargarCartelera(List<Pelicula> peliculas) {
        flowPanePeliculas.getChildren().clear();
        if (selectedMovieCard != null) {
            selectedMovieCard.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;");
            selectedMovieCard = null;
        }
        if (peliculas != null) {
            for (Pelicula pelicula : peliculas) {
                flowPanePeliculas.getChildren().add(crearTarjetaPelicula(pelicula));
            }
        }
    }

    /**
     * Crea una tarjeta visual interactiva para una película.
     * 
     * @param pelicula La película para la cual crear la tarjeta
     * @return VBox configurada como tarjeta de película
     */
    private VBox crearTarjetaPelicula(Pelicula pelicula) {
        // Configuración de elementos visuales
        ImageView imagenPelicula = new ImageView();
        Label tituloLabel = new Label(pelicula.getTitulo());
        Label generoAnioLabel = new Label(pelicula.getGenerosComoString() + " (" + pelicula.getAnio() + ")");

        // Estilo de la imagen
        imagenPelicula.setFitWidth(160);
        imagenPelicula.setFitHeight(240);
        imagenPelicula.setPreserveRatio(false);
        imagenPelicula.getStyleClass().add("movie-poster");

        // Estilo de las etiquetas
        // Se usa la clase específica para la cartelera para no afectar otras vistas
        tituloLabel.getStyleClass().add("movie-title-card");
        tituloLabel.setMaxWidth(160); // Ancho máximo para que el texto sepa cuándo ajustarse

        generoAnioLabel.getStyleClass().add("movie-genre-year-card");
        generoAnioLabel.setMaxWidth(160);

        // Creación del contenedor principal
        VBox card = new VBox(imagenPelicula, tituloLabel, generoAnioLabel);
        card.getStyleClass().add("movie-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setSpacing(5);

        // Se establece un ancho preferido y una altura MÍNIMA.
        // Esto permite que la tarjeta crezca verticalmente si el título es largo.
        card.setPrefWidth(180);
        card.setMinHeight(300);

        card.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;");

        // Carga de imagen con manejo de errores
        cargarImagenPelicula(pelicula, imagenPelicula);

        // Configuración de evento de selección
        configurarEventoSeleccion(pelicula, card);

        return card;
    }

    /**
     * Carga la imagen de la película con manejo de errores.
     * 
     * @param pelicula       Película de la cual cargar la imagen
     * @param imagenPelicula ImageView donde mostrar la imagen
     */
    private void cargarImagenPelicula(Pelicula pelicula, ImageView imagenPelicula) {
        String imageUrl = pelicula.getImagenUrl();
        Image imageToSet = null;

        // Intento 1: Cargar desde URL proporcionada
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            try {
                imageToSet = new Image(imageUrl, true);
            } catch (Exception e) {
                System.err.println("Error al cargar imagen desde URL: " + imageUrl);
            }
        }

        // Intento 2: Cargar imagen por defecto si falla la URL
        if (imageToSet == null || imageToSet.isError()) {
            try {
                imageToSet = new Image("https://i.imgur.com/6LWDqET.png", true);
                if (imageToSet.isError()) {
                    System.err.println("Error al cargar la imagen por defecto: https://i.imgur.com/6LWDqET.png");
                }
            } catch (Exception e) {
                System.err.println("Error al cargar imagen por defecto: " + e.getMessage());
            }
        }

        imagenPelicula.setImage(imageToSet);
    }

    /**
     * Configura el evento de selección para la tarjeta de película.
     * 
     * @param pelicula Película asociada a la tarjeta
     * @param card     Contenedor VBox de la tarjeta
     */
    private void configurarEventoSeleccion(Pelicula pelicula, VBox card) {
        card.setOnMouseClicked(event -> {
            if (selectedMovieCard != null) {
                selectedMovieCard.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;");
            }
            card.setStyle("-fx-border-color: #007bff; -fx-border-width: 3px;");
            selectedMovieCard = card;

            System.out.println("Tarjeta de película seleccionada: " + pelicula.getTitulo());
            servicioMostrarCartelera.setPeliculaSeleccionada(pelicula);
        });
    }

    // ===== MANEJADORES DE EVENTOS =====

    /**
     * Maneja el evento de confirmación de selección.
     * 
     * Navega a la pantalla de funciones para la película seleccionada.
     */
    @FXML
    private void onSeleccionar() {
        Stage currentStage = (Stage) flowPanePeliculas.getScene().getWindow();
        Pelicula peliculaSeleccionada = servicioMostrarCartelera.getPeliculaSeleccionada();
        servicioMostrarCartelera.seleccionarPelicula(peliculaSeleccionada, currentStage);
    }

    /**
     * Maneja el evento de regreso a la pantalla principal.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    public void onRegresar(ActionEvent event) {
        servicioMostrarCartelera.regresarPantallaPrincipal(event);
    }

}