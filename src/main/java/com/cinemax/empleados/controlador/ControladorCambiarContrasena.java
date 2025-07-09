package com.cinemax.empleados.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class ControladorCambiarContrasena {

    @FXML
    private PasswordField txtContrasenaActual;

    @FXML
    private PasswordField txtNuevaContrasena;

    @FXML
    private PasswordField txtConfirmarContrasena;

    @FXML
    private void onCancelar() {
        ((Stage) txtContrasenaActual.getScene().getWindow()).close();
    }

    @FXML
    private void onGuardar() {
        String actual = txtContrasenaActual.getText();
        String nueva = txtNuevaContrasena.getText();
        String confirmar = txtConfirmarContrasena.getText();

        if (!nueva.equals(confirmar)) {
            System.out.println("Las contraseñas no coinciden");
            return;
        }

        // Aquí iría la lógica real para validar y guardar la contraseña
        System.out.println("Contraseña actual: " + actual);
        System.out.println("Nueva contraseña guardada: " + nueva);

        ((Stage) txtContrasenaActual.getScene().getWindow()).close();
    }
}
