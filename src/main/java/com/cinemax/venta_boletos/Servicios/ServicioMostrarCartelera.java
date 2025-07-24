package com.cinemax.venta_boletos.Servicios;

import com.cinemax.comun.MetodosComunes;
import com.cinemax.venta_boletos.Controladores.ControladorMostrarFunciones;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class ServicioMostrarCartelera {

    private final String[] PELICULAS = {
            "Avengers: Endgame",
            "The Batman",
            "Dune: Parte 2",
            "Spider-Man: No Way Home"
    };

    public void inicializarListaPeliculas(ListView<String> listViewPeliculas) {
        listViewPeliculas.getItems().addAll(PELICULAS);
        listViewPeliculas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listViewPeliculas.setStyle("-fx-selection-bar: #2a9df4; -fx-selection-bar-non-focused: #d0e6f5;");
    }

    public void seleccionarPelicula(ListView<String> listViewPeliculas) {
        String peliculaSeleccionada = listViewPeliculas.getSelectionModel().getSelectedItem();

        if (peliculaSeleccionada == null) {
            MetodosComunes.mostrarVentanaEmergente("Campos Incompletos >", "Por favor seleccione una película");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/funciones-view.fxml"));
            Parent root = loader.load();

            ControladorMostrarFunciones controller = loader.getController();
            controller.setPelicula(peliculaSeleccionada);

            Stage stage = (Stage) listViewPeliculas.getScene().getWindow();
            Scene newScene = new Scene(root, 800, 600);
            stage.setScene(newScene);
            stage.centerOnScreen();

        } catch (IOException e) {
            MetodosComunes.mostrarVentanaEmergente("Error", "No se pudo cargar la pantalla de funciones");
            e.printStackTrace();
        }
    }

    public void regresarPantallaPrincipal(ActionEvent event) {
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
