package com.cinemax.empleados.controladores;

import com.cinemax.empleados.servicios.ServicioUsuarios;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControladorRecuperarContrasena {

    @FXML
    private TextField txtEmail;

    @FXML
    private Label lblMensaje;
    private ServicioUsuarios servicioUsuarios;

    @FXML
    private void initialize() {
        lblMensaje.setVisible(false); // Oculta el mensaje al inicioo
        this.servicioUsuarios = new ServicioUsuarios();
    }

    @FXML
    private void onSendClick(ActionEvent event) {
        String correo = txtEmail.getText().trim();

        if (correo.isEmpty()) {
            mostrarMensaje("Por favor, ingresa tu correo electrónico.", true);
            return;
        }

        if (!isValidEmail(correo)) {
            mostrarMensaje("Formato de correo electrónico inválido.", true);
            return;
        }

        servicioUsuarios.recuperarClave(correo);
        // --- Simulación de envío de credencial ---
        // 1. Validar si el correo existe en tu base de datos.
        // 2. Generar un token seguro o una contraseña temporal.
        // 3. Enviar un correo electrónico real con el enlace de restablecimiento/nueva credencial.
        // 4. Manejar el resultado (éxito/fallo) de la operación.

        // Por ahora, solo simulamos el éxito con un mensaje al usuario
        mostrarMensaje("Si el correo electrónico está registrado, recibirás un mensaje con instrucciones para restablecer tu contraseña.", false);
        txtEmail.clear(); // Limpia el campo después de "enviar"
    }

    @FXML
    private void onBackToLoginClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaLogin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("CineMAX - Login");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.err.println("Error al cargar la pantalla de login: " + e.getMessage());
            e.printStackTrace();
            mostrarMensaje("Error interno al volver al login.", true);
        }
    }

    /**
     * Valida el formato de un correo electrónico usando una expresión regular.
     * @param email El correo a validar.
     * @return true si el formato es válido, false en caso contrario.
     */
    private boolean isValidEmail(String email) {
        //validación básica de correo electrónico
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Muestra un mensaje en la etiqueta lblMensaje y controla su visibilidad y estilo.
     * @param mensaje El texto a mostrar.
     * @param esError true si el mensaje indica un error (texto rojo), false para información/éxito (texto verde).
     */
    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.setText(mensaje);
        lblMensaje.setVisible(true);
        if (esError) {
            lblMensaje.setStyle("-fx-text-fill: red;"); // Estilo CSS para texto rojo (errores)
        } else {
            lblMensaje.setStyle("-fx-text-fill: green;"); // Estilo CSS para texto verde (éxito/información)
        }
    }
}
