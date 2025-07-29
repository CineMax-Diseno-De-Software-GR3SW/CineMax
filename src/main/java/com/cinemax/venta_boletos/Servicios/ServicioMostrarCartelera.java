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
import javafx.stage.Stage; // Importa Stage
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

    // MODIFICADO: Ahora el servicio recibe el Stage directamente
    public void seleccionarPelicula(Pelicula peliculaSeleccionada, Stage currentStage) {
        if (peliculaSeleccionada == null) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, selecciona una película.");
            return;
        }

        try {
            // Aquí puedes usar tu método de cambio de ventana existente
            ManejadorMetodosComunes.cambiarVentana(currentStage, "/vistas/venta_boletos/funciones-view.fxml",
                    "Funciones de " + peliculaSeleccionada.getTitulo());

            // Si necesitas pasar el objeto Pelicula al controlador de funciones-view.fxml:
            // Tienes que cargar el loader de nuevo para obtener el controlador.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/funciones-view.fxml"));
            loader.load(); // Esto carga la jerarquía de nodos, pero no la muestra.
            ControladorMostrarFunciones controller = loader.getController();
            controller.setPelicula(peliculaSeleccionada.getTitulo()); // Asume que necesitas el título.
            // Si el controlador necesita el objeto Pelicula completo:
            // controller.setPeliculaObjeto(peliculaSeleccionada); // Necesitarías un setter
            // así en tu controlador

            // NOTA: El método cambiarVentana de ManejadorMetodosComunes ya establece la
            // escena y muestra el Stage.
            // Por lo tanto, no necesitas las líneas de Scene y stage.show() aquí.
            // Lo importante es que el Stage que le pasas a cambiarVentana sea el mismo que
            // tienes activo.

        } catch (IOException e) {
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo cargar la pantalla de funciones");
            e.printStackTrace();
        }
    }

    public void regresarPantallaPrincipal(ActionEvent event) {
        try {
            // Aquí ya usas ManejadorMetodosComunes.cambiarVentana
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            ManejadorMetodosComunes.cambiarVentana(currentStage, "/vistas/empleados/PantallaPortalPrincipal.fxml");
        } catch (Exception e) { // Cambiado a Exception para capturar cualquier error al obtener el Stage
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("Error al regresar a la pantalla principal.");
        }
    }
}