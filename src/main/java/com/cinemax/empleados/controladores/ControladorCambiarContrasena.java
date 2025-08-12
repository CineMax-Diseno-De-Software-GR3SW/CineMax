package com.cinemax.empleados.controladores;


import com.cinemax.empleados.servicios.ServicioPerfilUsuario;
import com.cinemax.empleados.servicios.ServicioSesionSingleton;
import com.cinemax.utilidades.ManejadorMetodosComunes;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import java.sql.SQLException;



public class ControladorCambiarContrasena {

    @FXML
    private PasswordField txtContrasenaActual;

    @FXML
    private PasswordField txtNuevaContrasena;

    @FXML
    private PasswordField txtConfirmarContrasena;

    private ServicioSesionSingleton sesionSingleton;

    private ServicioPerfilUsuario servicioPerfilUsuario;

    @FXML
    private void onCancelar() {
        ((Stage) txtContrasenaActual.getScene().getWindow()).close();
    }

    @FXML
    private void onGuardar() {
        sesionSingleton = ServicioSesionSingleton.getInstancia();
        servicioPerfilUsuario = new ServicioPerfilUsuario();

        String actual = txtContrasenaActual.getText();
        String nueva = txtNuevaContrasena.getText();
        String confirmar = txtConfirmarContrasena.getText();

        if (!nueva.equals(confirmar)) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia ("Las contraseñas no coinciden");

//            mostrarAlerta(Alert.AlertType.WARNING, "Error", "Las contraseñas no coinciden", "Verifica que la nueva contraseña y su confirmación sean iguales.");
            return;
        }

        try {
            servicioPerfilUsuario.actualizarClave(sesionSingleton.getUsuarioActivo(), actual, nueva);
            ManejadorMetodosComunes.mostrarVentanaExito("Contraseña actualizada exitosamente");

//            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Contraseña actualizada", "Tu contraseña se ha actualizado correctamente.");
            ((Stage) txtContrasenaActual.getScene().getWindow()).close();
        } catch (SQLException e) {
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al actualizar la contraseña");

//            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar la contraseña", e.getMessage());
        }
    }

//    private void mostrarAlerta(Alert.AlertType type, String title, String header, String content) {
//        Alert alert = new Alert(type);
//        alert.setTitle(title);
//        alert.setHeaderText(header);
//        alert.setContentText(content);
//        alert.showAndWait();
//    }
}
