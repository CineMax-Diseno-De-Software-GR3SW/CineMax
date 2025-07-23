package com.cinemax.salas.controladores;

import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.servicios.ButacaService;
import com.cinemax.salas.servicios.SalaService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ControladorDeConsultaSalas implements Initializable {

    @FXML
    private GridPane gridButacas;
    @FXML
    private ComboBox<Sala> comboSalas;
    @FXML
    private VBox vbox;

    private final ButacaService butacaService = new ButacaService();
    private final SalaService salaService = new SalaService();

    private Button btnContinuar = null;
    private Butaca butacaSeleccionada = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Sala> salas = salaService.listarSalas();
            comboSalas.setItems(FXCollections.observableArrayList(salas));
            comboSalas.setOnAction(e -> mostrarButacasDeSala());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarButacasDeSala() {
        gridButacas.getChildren().clear();
        removeBtnContinuar();
        Sala salaSeleccionada = comboSalas.getValue();
        if (salaSeleccionada == null) return;
        try {
            List<Butaca> butacas = butacaService.listarButacasPorSala(salaSeleccionada.getId());
            for (Butaca butaca : butacas) {
                Button btn = new Button(butaca.getFila().toUpperCase() + butaca.getColumna());
                btn.setMinSize(40, 40);
                switch (butaca.getEstado()) {
                    case "DISPONIBLE" -> {
                        btn.setStyle("-fx-background-color: green;");
                        btn.setOnAction(e -> {
                            this.butacaSeleccionada = butaca;
                            showBtnContinuar();
                        });
                    }
                    case "OCUPADA" -> btn.setStyle("-fx-background-color: red;");
                    case "INHABILITADA" -> btn.setStyle("-fx-background-color: gray;");
                }
                int fila = butaca.getFila().toUpperCase().charAt(0) - 'A';
                int columna = Integer.parseInt(butaca.getColumna()) - 1;
                gridButacas.add(btn, columna, fila);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBtnContinuar() {
        removeBtnContinuar();
        btnContinuar = new Button("Continuar");
        btnContinuar.setOnAction(e -> actualizarButacaSeleccionada());
        vbox.getChildren().add(btnContinuar);
    }

    private void removeBtnContinuar() {
        if (btnContinuar != null) {
            vbox.getChildren().remove(btnContinuar);
            btnContinuar = null;
        }
    }

    private void actualizarButacaSeleccionada() {
        if (butacaSeleccionada == null) return;
        butacaSeleccionada.setEstado("OCUPADA");
        try {
            butacaService.actualizarButaca(butacaSeleccionada);
            removeBtnContinuar();
            mostrarDialogoExito();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarDialogoExito() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("¡Éxito!");

        VBox dialogVBox = new VBox(15);
        dialogVBox.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Label label = new Label("¡Éxito! Butaca Actualizada exitosamente");
        Button btnEntendido = new Button("Entendido");
        btnEntendido.setOnAction(e -> {
            dialog.close();
            mostrarButacasDeSala();
        });

        dialogVBox.getChildren().addAll(label, btnEntendido);
        Scene dialogScene = new Scene(dialogVBox, 320, 120);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }
}