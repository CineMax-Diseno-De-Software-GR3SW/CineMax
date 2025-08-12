package com.cinemax.empleados.controladores;

import javafx.scene.control.*;
import javafx.stage.Stage;


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
    private void onIniciarSesion() {
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
    private void onRecuperarContrasena() {
            ManejadorMetodosComunes.cambiarVentana((Stage)vinculoRecuperarContrasena.getScene().getWindow(),"/vistas/empleados/PantallaRecuperarContrasena.fxml");
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





