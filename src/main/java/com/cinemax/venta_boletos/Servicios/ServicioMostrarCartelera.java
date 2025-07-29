package com.cinemax.venta_boletos.Servicios;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.venta_boletos.Controladores.ControladorMostrarFunciones;
import com.cinemax.peliculas.controladores.ControladorCartelera;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;

public class ServicioMostrarCartelera {

    private final ControladorCartelera controladorCartelera = new ControladorCartelera();
    private ObservableList<Pelicula> peliculas = FXCollections.observableArrayList();
    private Pelicula selectedPelicula;

    public ObservableList<Pelicula> getPeliculas() {
        return peliculas;
    }

    public Pelicula getSelectedPelicula() {
        return selectedPelicula;
    }

    public void setSelectedPelicula(Pelicula selectedPelicula) {
        this.selectedPelicula = selectedPelicula;
    }

    public void inicializarListaPeliculas() {
        try {
            List<Pelicula> peliculasCargadas = controladorCartelera.obtenerCartelera();
            peliculas.setAll(peliculasCargadas);
        } catch (Exception e) {
            System.err.println("Error al obtener cartelera: " + e.getMessage());
            ManejadorMetodosComunes.mostrarVentanaError("Error al cargar películas: " + e.getMessage());
            peliculas.clear();
        }
    }

    // Método corregido para cargar el FXML una sola vez y pasar la película antes
    // de mostrar
    public void seleccionarPelicula(Pelicula peliculaSeleccionada, Stage currentStage) {
        if (peliculaSeleccionada == null) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, selecciona una película.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/funciones-view.fxml"));
            Parent root = loader.load();

            ControladorMostrarFunciones controller = loader.getController();
            controller.setPelicula(peliculaSeleccionada.getTitulo());

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Funciones de " + peliculaSeleccionada.getTitulo());
            currentStage.centerOnScreen();
            currentStage.show();

        } catch (IOException e) {
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo cargar la pantalla de funciones");
            e.printStackTrace();
        }
    }

    public void regresarPantallaPrincipal(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            ManejadorMetodosComunes.cambiarVentana(currentStage, "/vistas/empleados/PantallaPortalPrincipal.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("Error al regresar a la pantalla principal.");
        }
    }
}
