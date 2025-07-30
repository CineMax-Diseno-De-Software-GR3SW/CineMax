package com.cinemax.venta_boletos.Controladores;

import com.cinemax.venta_boletos.Servicios.ServicioMostrarCartelera;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
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

public class ControladorMostrarCartelera {

    @FXML
    private FlowPane flowPanePeliculas;

    private final ServicioMostrarCartelera servicioMostrarCartelera = new ServicioMostrarCartelera();
    private VBox selectedMovieCard = null;

    @FXML
    public void initialize() {
        servicioMostrarCartelera.inicializarListaPeliculas();

        servicioMostrarCartelera.getPeliculas().addListener((ListChangeListener<Pelicula>) c -> {
            while (c.next()) {
                if (c.wasAdded() || c.wasRemoved() || c.wasUpdated()) {
                    actualizarDisplayPeliculas(servicioMostrarCartelera.getPeliculas());
                }
            }
        });

        actualizarDisplayPeliculas(servicioMostrarCartelera.getPeliculas());
    }

    private void actualizarDisplayPeliculas(List<Pelicula> peliculas) {
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

    private VBox crearTarjetaPelicula(Pelicula pelicula) {
        ImageView imagenPelicula = new ImageView();
        Label tituloLabel = new Label(pelicula.getTitulo());
        Label generoAnioLabel = new Label(pelicula.getGenerosComoString() + " (" + pelicula.getAnio() + ")");

        imagenPelicula.setFitWidth(160);
        imagenPelicula.setFitHeight(240);
        imagenPelicula.setPreserveRatio(false);
        imagenPelicula.getStyleClass().add("movie-poster");

        tituloLabel.getStyleClass().add("movie-title-card");
        tituloLabel.setWrapText(true);
        tituloLabel.setMaxWidth(160);
        generoAnioLabel.getStyleClass().add("movie-genre-year-card");
        generoAnioLabel.setWrapText(true);
        generoAnioLabel.setMaxWidth(160);

        VBox card = new VBox(imagenPelicula, tituloLabel, generoAnioLabel);
        card.getStyleClass().add("movie-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setSpacing(5);
        card.setPrefSize(180, 300);
        card.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;"); // Borde inicial transparente

        String imageUrl = pelicula.getImagenUrl();
        Image imageToSet = null;

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            try {
                imageToSet = new Image(imageUrl, true);
            } catch (Exception e) {
                System.err.println("No se pudo cargar la imagen de la película '" + pelicula.getTitulo() + "' desde: "
                        + imageUrl + ". Error: " + e.getMessage());
            }
        }

        if (imageToSet == null || (imageToSet != null && imageToSet.isError())) {
            try {
                imageToSet = new Image(getClass().getResourceAsStream("/images/no-image.png"));
                if (imageToSet != null && imageToSet.isError()) {
                    System.err.println("Error al cargar la imagen por defecto: /images/no-image.png.");
                }
            } catch (Exception e) {
                System.err.println(
                        "Fallo total al cargar cualquier imagen, incluyendo la por defecto. Error: " + e.getMessage());
                imageToSet = null;
            }
        }
        imagenPelicula.setImage(imageToSet);

        card.setOnMouseClicked(event -> {
            if (selectedMovieCard != null) {
                selectedMovieCard.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;");
            }
            card.setStyle("-fx-border-color: #007bff; -fx-border-width: 3px;"); // Borde azul, 3px ancho
            selectedMovieCard = card;

            System.out.println("Tarjeta de película seleccionada: " + pelicula.getTitulo());
            servicioMostrarCartelera.setSelectedPelicula(pelicula);
        });

        return card;
    }

    @FXML
    private void handleSeleccionar() {
        Pelicula peliculaSeleccionada = servicioMostrarCartelera.getSelectedPelicula();
        if (peliculaSeleccionada != null) {
            Stage currentStage = (Stage) flowPanePeliculas.getScene().getWindow();
            servicioMostrarCartelera.seleccionarPelicula(peliculaSeleccionada, currentStage);
        }
    }

    @FXML
    public void handleRegresar(ActionEvent event) {
        servicioMostrarCartelera.regresarPantallaPrincipal(event);
    }
}