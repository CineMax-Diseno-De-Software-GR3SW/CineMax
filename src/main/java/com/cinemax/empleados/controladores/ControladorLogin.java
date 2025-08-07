package com.cinemax.empleados.controladores;

import com.cinemax.comun.ManejadorMetodosComunes;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.cinemax.empleados.servicios.ServicioSesionSingleton;

public class ControladorLogin {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    @FXML
    private Button btnIngresar;

    private ServicioSesionSingleton servicioSesionSingleton;


    @FXML
    private void initialize() {
        servicioSesionSingleton = ServicioSesionSingleton.getInstancia();
        lblError.setVisible(false);
    }

    @FXML
    private void onLoginClick() {
        if(iniciarSesion()){
            lblError.setVisible(false);
            String rutaFXML = "/vistas/empleados/PantallaPortalPrincipal.fxml";
            if(ServicioSesionSingleton.getInstancia().getUsuarioActivo().isRequiereCambioClave())
                rutaFXML = "/vistas/empleados/PantallaCambioClaveObligatorio.fxml";

            ManejadorMetodosComunes.cambiarVentana((Stage)btnIngresar.getScene().getWindow(),rutaFXML);
        } else {
            lblError.setText("Usuario o contraseña incorrectos.");
            lblError.setVisible(true);
        }

    }

    @FXML
    // metodo para manejar el clic en "Olvidaste tu contraseña"
    private void onForgotPasswordClick(ActionEvent event) {
        try {
            // Carga la nueva pantalla de recuperación de contraseña
            // Asegúrate de que esta ruta sea correcta:
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaRecuperarContrasena.fxml"));
            Parent root = loader.load();

            // Obtiene el Stage actual y cambia la escena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Recuperar Contraseña");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            lblError.setText("Error al cargar la pantalla de recuperación de contraseña.");
            lblError.setVisible(true);
            e.printStackTrace();
        }
    }

    private boolean iniciarSesion() {
        String nomUsuario = txtUsuario.getText();
        String password = txtPassword.getText();

        if (nomUsuario.isEmpty() || password.isEmpty()) {
            lblError.setText("Por favor, ingresa usuario y contraseña.");
            lblError.setVisible(true);
            return false;
        }

        try {
            return servicioSesionSingleton.iniciarSesion(nomUsuario, password);
        } catch (Exception e) {
            return false;
        }
    }
}





