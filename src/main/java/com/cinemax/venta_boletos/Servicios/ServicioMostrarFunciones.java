package com.cinemax.venta_boletos.Servicios;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.venta_boletos.Controladores.ControladorBoleto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class ServicioMostrarFunciones {

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

    private final Funcion[] FUNCIONES = {
            new Funcion("16:00", "Sala 1"),
            new Funcion("19:30", "Sala 3D"),
            new Funcion("22:15", "Sala VIP")
    };

    public void cargarFunciones(TableView<Funcion> tabla, TableColumn<Funcion, String> colHora,
            TableColumn<Funcion, String> colSala) {
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colSala.setCellValueFactory(new PropertyValueFactory<>("sala"));

        ObservableList<Funcion> lista = FXCollections.observableArrayList(FUNCIONES);
        tabla.setItems(lista);

        tabla.setStyle("-fx-selection-bar: #2a9df4; -fx-selection-bar-non-focused: #d0e6f5;");
    }

    public void regresarPantallaCartelera(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/cartelera-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene newScene = new Scene(root, 800, 600);
            stage.setScene(newScene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo regresar a la cartelera: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void confirmarFuncion(TableView<Funcion> tabla, String pelicula) {
        Funcion seleccion = tabla.getSelectionModel().getSelectedItem();

        if (seleccion != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/boleto-view.fxml"));
                Parent root = loader.load();

                ControladorBoleto controller = loader.getController();
                String funcionTexto = seleccion.getHora() + " - " + seleccion.getSala();
                controller.initData(pelicula, funcionTexto);

                Stage stage = (Stage) tabla.getScene().getWindow();
                Scene newScene = new Scene(root, 800, 600);
                stage.setScene(newScene);
                stage.centerOnScreen();

            } catch (IOException e) {
                ManejadorMetodosComunes
                        .mostrarVentanaError("Error al cargar la pantalla de boletos: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                ManejadorMetodosComunes.mostrarVentanaError("Error inesperado: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor seleccione una función.");
        }
    }
}
