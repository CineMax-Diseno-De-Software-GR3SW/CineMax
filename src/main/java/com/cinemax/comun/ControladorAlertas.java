package com.cinemax.comun;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControladorAlertas {

    @FXML private VBox alertPane;
    @FXML private Label titleLabel;
    @FXML private Label messageLabel;
    @FXML private Button okButton;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        // Hacer que la ventana de alerta sea arrastrable
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

    public void setData(String title, String message) {
        titleLabel.setText(title);
        messageLabel.setText(message);
        
        // Permitir cerrar con Enter o Escape
        alertPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.ESCAPE) {
                onOkAction();
            }
        });
        
        // Enfocar el botón por defecto
        if (okButton != null) {
            okButton.requestFocus();
        }
    }

    @FXML
    private void onOkAction() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
