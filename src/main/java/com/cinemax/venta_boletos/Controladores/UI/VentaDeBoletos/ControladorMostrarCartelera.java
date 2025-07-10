
package com.cinemax.venta_boletos.Controladores.UI.VentaDeBoletos;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ControladorMostrarCartelera {

    @FXML
    private ListView<String> listViewPeliculas;

    // Datos quemados para cartelera
    private final String[] PELICULAS = {
            "Avengers: Endgame",
            "The Batman",
            "Dune: Parte 2",
            "Spider-Man: No Way Home"
    };

    @FXML
    public void initialize() {
        // Cargar películas al iniciar
        listViewPeliculas.getItems().addAll(PELICULAS);
    }

    @FXML
    private void handleSeleccionar() {
        String peliculaSeleccionada = listViewPeliculas.getSelectionModel().getSelectedItem();

        if (peliculaSeleccionada != null) {
            try {
                // Cargar vista de funciones
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/vistas/venta_boletos/cartelera-view.fxml"));
                Parent root = loader.load();

                // Pasar la película seleccionada al controlador de funciones
                ControladorMostrarFunciones controller = loader.getController();
                controller.setPelicula(peliculaSeleccionada);

                // Cambiar de escena
                Stage stage = (Stage) listViewPeliculas.getScene().getWindow();
                stage.setScene(new Scene(root));

            } catch (Exception e) {
                mostrarError("Error al cargar funciones: " + e.getMessage());
            }
        } else {
            mostrarError("Seleccione una película");
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}