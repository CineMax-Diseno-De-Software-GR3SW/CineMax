package com.cinemax.venta_boletos.Controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;

import java.io.IOException;
import javafx.scene.Node;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

public class ControladorMostrarFunciones {

    @FXML
    private Label peliculaTituloLabel;

    @FXML
    private TableView<Funcion> tableViewFunciones;

    @FXML
    private TableColumn<Funcion, String> columnaHora;

    @FXML
    private TableColumn<Funcion, String> columnaSala;

    // Clase interna para representar funciones
    public static class Funcion {
        private final String hora;
        private final String sala;

        public Funcion(String hora, String sala) {
            this.hora = hora;
            this.sala = sala;
        }

        public String getHora() {
            return hora;
        }

        public String getSala() {
            return sala;
        }
    }

    // Datos quemados para funciones
    private final Funcion[] FUNCIONES = {
            new Funcion("16:00", "Sala 1"),
            new Funcion("19:30", "Sala 3D"),
            new Funcion("22:15", "Sala VIP")
    };

    public void setPelicula(String pelicula) {
        peliculaTituloLabel.setText(pelicula);
        cargarFunciones();
    }

    private void cargarFunciones() {
        columnaHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        columnaSala.setCellValueFactory(new PropertyValueFactory<>("sala"));

        ObservableList<Funcion> funciones = FXCollections.observableArrayList(FUNCIONES);
        tableViewFunciones.setItems(funciones);

        // Estilo para selección
        tableViewFunciones.setStyle("-fx-selection-bar: #2a9df4; -fx-selection-bar-non-focused: #d0e6f5;");
    }

    @FXML
    public void handleRegresar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/venta_boletos/cartelera-view.fxml")); // Ruta corregida
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene newScene = new Scene(root, 800, 600);
            stage.setScene(newScene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleConfirmar() {
        Funcion funcionSeleccionada = tableViewFunciones.getSelectionModel().getSelectedItem();

        if (funcionSeleccionada != null) {
            try {
                // Obtenemos el Stage actual
                Stage stage = (Stage) tableViewFunciones.getScene().getWindow();

                // Cargamos la vista de boleto
                FXMLLoader fxmlLoader = new FXMLLoader(
                        getClass().getResource("/vistas/venta_boletos/boleto-view.fxml")); // Ruta corregida
                Parent root = fxmlLoader.load();

                // Obtenemos el controlador de la vista de boleto
                ControladorBoleto controllerBoleto = fxmlLoader.getController();

                // Pasamos la película y la función al controlador del boleto
                String funcionTexto = funcionSeleccionada.getHora() + " - " + funcionSeleccionada.getSala();
                controllerBoleto.initData(peliculaTituloLabel.getText(), funcionTexto);

                Scene newScene = new Scene(root, 800, 600);
                stage.setScene(newScene);
                stage.centerOnScreen();
            } catch (IOException e) {
                mostrarError("Error al cargar la pantalla de boletos: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                mostrarError("Error inesperado al cargar la pantalla de boletos: " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            mostrarError("Por favor seleccione una función");
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        // Estilo para la alerta
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/vistas/temas/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");

        alert.showAndWait();
    }
}