package com.cinemax.empleados.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;


import javafx.fxml.FXML;
import com.cinemax.empleados.servicios.ServicioSesionSingleton;
import com.cinemax.utilidades.ManejadorMetodosComunes;

public class ControladorLogin {

    public Hyperlink vinculoRecuperarContrasena;
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
    private void onForgotPasswordClick() {
//        try {
            ManejadorMetodosComunes.cambiarVentana((Stage)vinculoRecuperarContrasena.getScene().getWindow(),"/vistas/empleados/PantallaRecuperarContrasena.fxml");
//
//            // Carga la nueva pantalla de recuperación de contraseña
//            // Asegúrate de que esta ruta sea correcta:
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaRecuperarContrasena.fxml"));
//            Parent root = loader.load();
//
//            // Obtiene el Stage actual y cambia la escena
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//            stage.setTitle("Recuperar Contraseña");
//            stage.setScene(new Scene(root));
//            stage.show();
//
//        } catch (Exception e) {
//            lblError.setText("Error al cargar la pantalla de recuperación de contraseña.");
//            lblError.setVisible(true);
//            e.printStackTrace();
//        }
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





