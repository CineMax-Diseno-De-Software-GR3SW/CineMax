package com.cinemax.venta_boletos.Controladores.UI.Shared;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControllerAlert {

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
    }

    @FXML
    private void onOkAction() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
