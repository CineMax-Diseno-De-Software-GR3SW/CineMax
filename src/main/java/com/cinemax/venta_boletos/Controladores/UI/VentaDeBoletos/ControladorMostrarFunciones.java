package com.cinemax.venta_boletos.Controladores.UI.VentaDeBoletos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

import com.cinemax.venta_boletos.Util.ThemeManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
                        getClass().getResource("/vistas/venta_boletos/boleto-view.fxml"));
                Parent root = fxmlLoader.load(); // Cargamos el FXML

                // Obtenemos el controlador de la vista de boleto
                ControllerBoleto controllerBoleto = fxmlLoader.getController();

                // Pasamos la película y la función al controlador del boleto
                String funcionTexto = funcionSeleccionada.getHora() + " - " + funcionSeleccionada.getSala();
                controllerBoleto.initData(peliculaTituloLabel.toString(), funcionTexto); // Usamos el nombre de película
                                                                                         // guardado

                // Creamos una nueva escena con la vista cargada
                Scene scene = new Scene(root);

                // Opcional: Aplicar tema si tienes un ThemeManager
                ThemeManager.getInstance().applyTheme(scene);

                // Establecemos la nueva escena en el Stage
                stage.setScene(scene);
                stage.setTitle("CINE MAX - Seleccionar Boletos"); // Puedes cambiar el título
                stage.show(); // Mostramos la nueva escena

            } catch (IOException e) {
                mostrarError("Error al cargar la pantalla de boletos: " + e.getMessage());
                e.printStackTrace(); // Imprime el stack trace para depuración
            } catch (Exception e) {
                mostrarError("Error inesperado al cargar la pantalla de boletos: " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            mostrarError("Seleccione una función");
        }
    }

    private void mostrarError(String mensaje) {
        // Implementación similar a la del controlador anterior
    }
}