package com.cinemax.venta_boletos.Servicios;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.venta_boletos.Controladores.ControladorMostrarFunciones;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.servicios.ServicioPelicula;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ServicioMostrarCartelera {

    private final ServicioPelicula servicioPelicula;

    public ServicioMostrarCartelera() {
        this.servicioPelicula = new ServicioPelicula();
    }

    public void inicializarListaPeliculas(ListView<String> listViewPeliculas) {
        try {
            List<Pelicula> peliculas = servicioPelicula.obtenerPeliculas();

            listViewPeliculas.getItems().clear();
            if (peliculas != null && !peliculas.isEmpty()) {
                for (Pelicula p : peliculas) {
                    listViewPeliculas.getItems().add(p.getTitulo());
                }
            } else {
                ManejadorMetodosComunes.mostrarVentanaAdvertencia("No hay películas disponibles en la cartelera.");
            }

            listViewPeliculas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            listViewPeliculas.setStyle("-fx-selection-bar: #2a9df4; -fx-selection-bar-non-focused: #d0e6f5;");
        } catch (SQLException e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al cargar la lista de películas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void seleccionarPelicula(ListView<String> listViewPeliculas) {
        String peliculaSeleccionada = listViewPeliculas.getSelectionModel().getSelectedItem();

        if (peliculaSeleccionada == null) {
            ManejadorMetodosComunes
                    .mostrarVentanaAdvertencia("Campos incompletos. Por favor, seleccione una película.");
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
            ManejadorMetodosComunes
                    .mostrarVentanaError("No se pudo cargar la pantalla de funciones: " + e.getMessage());
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
            ManejadorMetodosComunes
                    .mostrarVentanaError("No se pudo regresar a la pantalla principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
