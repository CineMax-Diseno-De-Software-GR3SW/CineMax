package com.cinemax.utilidades;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControladorConfirmacion {
    @FXML
    private VBox alertPane;
    @FXML
    private Label titleLabel;
    @FXML
    private Label alertIcon;
    @FXML
    private Label messageLabel;
    @FXML
    private Button btnAceptar;
    @FXML
    private Button btnCancelar;

    private boolean confirmado = false;

    public void setTitulo(String titulo) {
        titleLabel.setText(titulo);
    }

    public void setMensaje(String mensaje) {
        messageLabel.setText(mensaje);
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    @FXML
    public void initialize() {
        btnAceptar.setOnAction(e -> {
            confirmado = true;
            cerrarVentana();
        });
        btnCancelar.setOnAction(e -> {
            confirmado = false;
            cerrarVentana();
        });
        // Permitir arrastrar la ventana igual que en ControladorAlertas
        alertPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        alertPane.setOnMouseDragged(event -> {
            Stage stage = (Stage) alertPane.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    private double xOffset = 0;
    private double yOffset = 0;

    private void cerrarVentana() {
        Stage stage = (Stage) btnAceptar.getScene().getWindow();
        stage.close();
    }
}
