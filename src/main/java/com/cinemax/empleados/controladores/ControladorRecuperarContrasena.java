package com.cinemax.empleados.controladores;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.empleados.servicios.ServicioUsuarios;
import com.cinemax.empleados.servicios.ValidadorUsuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControladorRecuperarContrasena {

    public Hyperlink vinculoVolver;
    @FXML
    private TextField txtEmail;

    @FXML
    private Label lblMensaje;
    private ServicioUsuarios servicioUsuarios;

    @FXML
    private void initialize() {
        lblMensaje.setVisible(false); // Oculta el mensaje al inicio
        this.servicioUsuarios = new ServicioUsuarios();
    }

    @FXML
    private void onSendClick() {
        String correo = txtEmail.getText().trim();

        if (correo.isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, ingresa tu correo electrónico.");
            return;
        }

        if (!ValidadorUsuario.validarCorreo(correo)) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Formato de correo electrónico inválido.");
            return;
        }

        try {
            servicioUsuarios.recuperarClave(correo);
            ManejadorMetodosComunes.mostrarVentanaExito("Si el correo está registrado, recibirás un mensaje con instrucciones para restablecer tu contraseña.");

        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Surgio un error inesperado.");
        }
        //todo: Clave hasheada guardada en base de datos

        txtEmail.clear(); // Limpia el campo después de "enviar"
    }

    @FXML
    private void onBackToLoginClick(ActionEvent event) {
        ManejadorMetodosComunes.cambiarVentana((Stage) vinculoVolver.getScene().getWindow(),
                "/vistas/empleados/PantallaLogin.fxml");
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaLogin.fxml"));
//            Parent root = loader.load();
//
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//            stage.setTitle("CineMAX - Login");
//            stage.setScene(new Scene(root));
//            stage.show();
//
//        } catch (Exception e) {
//            System.err.println("Error al cargar la pantalla de login: " + e.getMessage());
//            e.printStackTrace();
//            ManejadorMetodosComunes.mostrarVentanaError("Error interno al volver al login.");
//        }
    }
}
