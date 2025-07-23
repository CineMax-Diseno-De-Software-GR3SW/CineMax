
package com.cinemax.venta_boletos.Controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import javafx.scene.Node;

public class ControladorMostrarCartelera {

    @FXML
    private ListView<String> listViewPeliculas;

    // Datos quemados temporalmente
    private final String[] PELICULAS = {
            "Avengers: Endgame",
            "The Batman",
            "Dune: Parte 2",
            "Spider-Man: No Way Home"
    };

    @FXML
    public void initialize() {
        // Configurar ListView
        listViewPeliculas.getItems().addAll(PELICULAS);
        listViewPeliculas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Estilo para selección
        listViewPeliculas.setStyle("-fx-selection-bar: #2a9df4; -fx-selection-bar-non-focused: #d0e6f5;");
    }

    @FXML
    private void handleSeleccionar() {
        String peliculaSeleccionada = listViewPeliculas.getSelectionModel().getSelectedItem();

        if (peliculaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Por favor seleccione una película");
            return;
        }

        try {
            // Cargar vista de funciones
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/vistas/venta_boletos/funciones-view.fxml"));
            Parent root = loader.load();

            // Configurar controlador de funciones
            ControladorMostrarFunciones controller = loader.getController();
            controller.setPelicula(peliculaSeleccionada);

            // Obtener el stage actual
            Stage stage = (Stage) listViewPeliculas.getScene().getWindow();

            // Mantener el mismo tamaño
            Scene newScene = new Scene(root, 800, 600);
            stage.setScene(newScene);
            stage.centerOnScreen();

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error",
                    "No se pudo cargar la pantalla de funciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRegresar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        // Estilo para la alerta
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/vistas/temas/ayu-theme.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");

        alert.showAndWait();
    }
}