package com.cinemax.venta_boletos.controladores;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.venta_boletos.servicios.ServicioMostrarFunciones;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;
import javafx.application.Platform;

public class ControladorMostrarFunciones {

    @FXML
    private Label peliculaTituloLabel;
    @FXML
    private TableView<Funcion> tableViewFunciones;
    @FXML
    private TableColumn<Funcion, String> colHora;
    @FXML
    private TableColumn<Funcion, String> colSala;
    @FXML
    private TableColumn<Funcion, String> colTipoSala;
    @FXML
    private TableColumn<Funcion, String> colFormato;
    @FXML
    private TableColumn<Funcion, String> colTipoEstreno;
    @FXML
    private TableColumn<Funcion, String> colPrecio;
    @FXML
    private TableColumn<Funcion, String> colFecha;

    private final ServicioMostrarFunciones servicio = new ServicioMostrarFunciones();
    private String nombrePeliculaActual;

    @FXML
    public void initialize() {
        System.out.println("Iniciando ControladorMostrarFunciones...");
        verificarInyecciones();

        // Configurar el estilo de selección de la tabla
        tableViewFunciones.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void verificarInyecciones() {
        if (tableViewFunciones == null)
            System.err.println("ERROR: tableViewFunciones no inyectado");
        if (colHora == null)
            System.err.println("ERROR: colHora no inyectado");
        if (colSala == null)
            System.err.println("ERROR: colSala no inyectado");
        if (colTipoSala == null)
            System.err.println("ERROR: colTipoSala no inyectado");
        if (colFormato == null)
            System.err.println("ERROR: colFormato no inyectado");
        if (colTipoEstreno == null)
            System.err.println("ERROR: colTipoEstreno no inyectado");
        if (colPrecio == null)
            System.err.println("ERROR: colPrecio no inyectado");
        if (colFecha == null)
            System.err.println("ERROR: colFecha no inyectado");
    }

    public void setPelicula(String pelicula) {
        this.nombrePeliculaActual = pelicula;

        Platform.runLater(() -> {
            if (peliculaTituloLabel != null) {
                peliculaTituloLabel.setText("Película: " + pelicula);
            }

            servicio.cargarFunciones(
                    tableViewFunciones,
                    colHora,
                    colSala,
                    colFormato,
                    colTipoEstreno,
                    colPrecio,
                    colFecha,
                    colTipoSala,
                    pelicula);
        });
    }

    @FXML
    public void handleRegresar(ActionEvent event) {
        servicio.regresarPantallaCartelera(event);
    }

    @FXML
    private void handleConfirmar() {
        servicio.confirmarFuncion(tableViewFunciones, nombrePeliculaActual);
    }
}