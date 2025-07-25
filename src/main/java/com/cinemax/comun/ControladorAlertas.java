package com.cinemax.comun;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControladorAlertas {

    @FXML
    private VBox alertPane;
    @FXML
    private Label titleLabel;
    @FXML
    private Label messageLabel;
    @FXML
    private Button okButton;

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

        // Forzar aplicación de estilos de iconos después de la inicialización
        javafx.application.Platform.runLater(() -> {
            aplicarEstilosIconos();
        });
    }

    private void aplicarEstilosIconos() {
        if (titleLabel != null && titleLabel.getGraphic() != null && titleLabel.getGraphic() instanceof Label) {
            Label iconLabel = (Label) titleLabel.getGraphic();

            // Forzar que las clases CSS definidas se apliquen correctamente
            String currentText = iconLabel.getText();
            if ("✓".equals(currentText) || "✔".equals(currentText)) {
                iconLabel.getStyleClass().removeAll("error-icon", "warning-icon", "success-icon", "x-icon");
                if (!iconLabel.getStyleClass().contains("check-icon")) {
                    iconLabel.getStyleClass().add("check-icon");
                }
            } else if ("X".equals(currentText) || "✗".equals(currentText) || "✖".equals(currentText)) {
                iconLabel.getStyleClass().removeAll("success-icon", "warning-icon", "check-icon", "error-icon");
                if (!iconLabel.getStyleClass().contains("x-icon")) {
                    iconLabel.getStyleClass().add("x-icon");
                }
            } else if ("⚠".equals(currentText) || "!".equals(currentText)) {
                iconLabel.getStyleClass().removeAll("success-icon", "error-icon", "check-icon", "x-icon");
                if (!iconLabel.getStyleClass().contains("warning-icon")) {
                    iconLabel.getStyleClass().add("warning-icon");
                }
            }
        }
    }

    @FXML
    private void onOkAction() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
