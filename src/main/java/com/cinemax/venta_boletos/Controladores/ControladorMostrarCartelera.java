package com.cinemax.venta_boletos.Controladores;

import com.cinemax.venta_boletos.Servicios.ServicioMostrarCartelera;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class ControladorMostrarCartelera {

    @FXML
    private ListView<String> listViewPeliculas;

    private ServicioMostrarCartelera servicioMostrarCartelera;

    @FXML
    public void initialize() {
        servicioMostrarCartelera = new ServicioMostrarCartelera();
        servicioMostrarCartelera.inicializarListaPeliculas(listViewPeliculas);
    }

    @FXML
    private void handleSeleccionar() {
        servicioMostrarCartelera.seleccionarPelicula(listViewPeliculas);
    }

    @FXML
    public void handleRegresar(ActionEvent event) {
        servicioMostrarCartelera.regresarPantallaPrincipal(event);
    }
}
