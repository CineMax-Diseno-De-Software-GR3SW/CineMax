package com.cinemax.empleados.controladores;

import com.cinemax.Main;
import com.cinemax.empleados.servicios.ServicioPerfilUsuario;
import com.cinemax.empleados.servicios.ServicioUsuarios;
import com.cinemax.utilidades.ControladorAlertas;
import com.cinemax.utilidades.ManejadorMetodosComunes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import com.cinemax.empleados.modelos.entidades.Usuario;
import com.cinemax.empleados.servicios.ServicioSesionSingleton;
import javafx.stage.Stage;

public class ControladorCambioClaveObligatorio {

    public Button cambiarClave;
    @FXML
    private PasswordField txtNuevaClave;

    @FXML
    private PasswordField txtConfirmarClave;

    @FXML
    private Label lblMensaje;

    private final ServicioUsuarios servicioUsuarios = new ServicioUsuarios();

    private final ServicioPerfilUsuario servicioPerfilUsuario = new ServicioPerfilUsuario();

    @FXML
    private void onCambiarClave() {
        String nuevaClave = txtNuevaClave.getText();
        String confirmarClave = txtConfirmarClave.getText();

        lblMensaje.setVisible(false);
        lblMensaje.getStyleClass().clear();

        if (nuevaClave.isBlank() || confirmarClave.isBlank()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, completa todos los campos.");
            return;
        }

        if (!nuevaClave.equals(confirmarClave)) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Las contraseñas no coinciden.");
            return;
        }

        if (nuevaClave.length() < 8) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("La contraseña debe tener al menos 8 caracteres.");
            return;
        }

        try {
            Usuario usuarioActual = ServicioSesionSingleton.getInstancia().getUsuarioActivo();
            servicioUsuarios.actualizarClaveTemporal(usuarioActual, nuevaClave);

            ManejadorMetodosComunes.mostrarVentanaExito("Contraseña actualizada con éxito.");
            //todo:avanzar a la pantalla de inicio
            ManejadorMetodosComunes.cambiarVentana((Stage) cambiarClave.getScene().getWindow(), "/vistas/empleados/PantallaPortalPrincipal.fxml");
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al cambiar la contraseña: " + e.getMessage());
        }
    }
}