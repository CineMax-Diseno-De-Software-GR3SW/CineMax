
package com.cinemax.venta_boletos.Controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import java.io.IOException;

import com.cinemax.comun.MetodosComunes;

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
            MetodosComunes.mostrarVentanaEmergente("Campos Incompletos >", "Por favor seleccione una películal");

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
            MetodosComunes.mostrarVentanaEmergente("Error",
                    "No se pudo cargar la pantalla de funciones: ");
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

}