package com.cinemax.venta_boletos.Controladores;

import com.cinemax.venta_boletos.Servicios.ServicioMostrarFunciones;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;

public class ControladorMostrarFunciones {

    @FXML
    private Label peliculaTituloLabel;

    @FXML
    private TableView<ServicioMostrarFunciones.Funcion> tableViewFunciones;

    @FXML
    private TableColumn<ServicioMostrarFunciones.Funcion, String> columnaHora;

    @FXML
    private TableColumn<ServicioMostrarFunciones.Funcion, String> columnaSala;

    private final ServicioMostrarFunciones servicio = new ServicioMostrarFunciones();

    public void setPelicula(String pelicula) {
        peliculaTituloLabel.setText(pelicula);
        servicio.cargarFunciones(tableViewFunciones, columnaHora, columnaSala);
    }

    @FXML
    public void handleRegresar(ActionEvent event) {
        servicio.regresarPantallaCartelera(event);
    }

    @FXML
    private void handleConfirmar() {
        servicio.confirmarFuncion(tableViewFunciones, peliculaTituloLabel.getText());
    }
}
