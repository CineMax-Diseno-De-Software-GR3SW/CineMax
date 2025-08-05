package com.cinemax.venta_boletos.Servicios;

import com.cinemax.comun.ControladorCarga;
import com.cinemax.comun.ControladorCargaConDatos;
import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.peliculas.controladores.ControladorFunciones;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.venta_boletos.Controladores.ControladorCargaAsignacionButacas;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

public class ServicioMostrarFunciones {

    private final ControladorFunciones controladorFunciones = new ControladorFunciones();
    //private final BoletoDAO daoBoleto = new BoletoDAO();

    public void cargarFunciones(TableView<Funcion> tabla,
            TableColumn<Funcion, String> colHora,
            TableColumn<Funcion, String> colSala,
            TableColumn<Funcion, String> colFormato,
            TableColumn<Funcion, String> colTipoEstreno,
            TableColumn<Funcion, String> colPrecio,
            TableColumn<Funcion, String> colFecha,
            String nombrePelicula) {

        // Configurar columnas primero
        configurarColumnas(colHora, colSala, colFormato, colTipoEstreno, colPrecio, colFecha);

        try {
            List<Funcion> funcionesObtenidas = controladorFunciones.obtenerFuncionesPorNombrePelicula(nombrePelicula);

            // Debug detallado
            System.out.println("\n=== DEBUG: Funciones obtenidas ===");
            funcionesObtenidas.forEach(f -> {
                System.out.println(
                        "ID: " + f.getId() +
                                " | Película: " + (f.getPelicula() != null ? f.getPelicula().getTitulo() : "null") +
                                " | Sala: " + (f.getSala() != null ? f.getSala().getNombre() : "null") +
                                " | Fecha: " + f.getFechaHoraInicio());
            });

            ObservableList<Funcion> listaFunciones = FXCollections.observableArrayList(funcionesObtenidas);

            Platform.runLater(() -> {
                tabla.setItems(listaFunciones);
                System.out.println("Funciones cargadas en tabla: " + listaFunciones.size());

                if (listaFunciones.isEmpty()) {
                    tabla.setPlaceholder(new Label("No hay funciones disponibles para " + nombrePelicula));
                }
            });

        } catch (Exception e) {
            Platform.runLater(() -> {
                tabla.setPlaceholder(new Label("Error al cargar funciones"));
                ManejadorMetodosComunes.mostrarVentanaError("Error al cargar funciones: " + e.getMessage());
            });
            e.printStackTrace();
        }
    }

    private void configurarColumnas(TableColumn<Funcion, String> colHora,
            TableColumn<Funcion, String> colSala,
            TableColumn<Funcion, String> colFormato,
            TableColumn<Funcion, String> colTipoEstreno,
            TableColumn<Funcion, String> colPrecio,
            TableColumn<Funcion, String> colFecha) {

        colHora.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFechaHoraInicio() != null
                        ? cellData.getValue().getFechaHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm"))
                        : ""));

        colSala.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSala() != null ? cellData.getValue().getSala().getNombre()
                        : "Sala no disponible"));

        colFormato.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFormato() != null ? cellData.getValue().getFormato().name().replace("_", " ")
                        : ""));

        colTipoEstreno.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTipoEstreno() != null
                        ? cellData.getValue().getTipoEstreno().name().replace("_", " ")
                        : ""));

        colPrecio.setCellValueFactory(cellData -> {
            BigDecimal precio = cellData.getValue().calcularPrecioFinal();
            return new SimpleStringProperty(precio != null ? String.format("$%.2f", precio) : "$0.00");
        });

        colFecha.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFechaHoraInicio() != null
                        ? cellData.getValue().getFechaHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : ""));
    }

    public void regresarPantallaCartelera(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/cartelera-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.centerOnScreen();
        } catch (IOException e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al regresar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void confirmarFuncion(TableView<Funcion> tabla, String pelicula) {
        Funcion funcionSeleccionada = tabla.getSelectionModel().getSelectedItem();

        if (funcionSeleccionada == null) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Seleccione una función primero");
            return;
        }

        try {

            // 1. ventana actual
            Stage currentStage = (Stage) tabla.getScene().getWindow();

            // 2. Objetos para pasar los datos a la siguiente pantalla usando la pantalla de carga
            ControladorCargaConDatos controladorCargaConDatos = new ControladorCargaAsignacionButacas(
                "/vistas/venta_boletos/VistaSeleccionButacas.fxml",
                currentStage,
                new ArrayList<>(List.of(funcionSeleccionada))
            );            

            // 3. Llamar al manejador de métodos comunes para mostrar la pantalla de carga
            ManejadorMetodosComunes.mostrarVistaDeCargaPasandoDatos(currentStage, controladorCargaConDatos, 8, 150);

        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al confirmar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
