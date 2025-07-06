package com.cinemax.empleados.Controlador;

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
import com.cinemax.empleados.Servicios.GestorSesionSingleton;

public class ControladorLogin {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    @FXML
    private Button btnIngresar;

    private GestorSesionSingleton gestorSesionSingleton;


    @FXML
    private void initialize() {
        gestorSesionSingleton = GestorSesionSingleton.getInstancia();
        lblError.setVisible(false);
    }

    @FXML
    private void onLoginClick(ActionEvent event) {
        if(iniciarSesion()){
            // URL url = getClass().getResource("/Vista/PantallaAdministrador.fxml");
            // System.out.println(url); // Si imprime null, el archivo no se encuentra
        // FXMLLoader Loader = new FXMLLoader(getClass().getResource("/Vista/PantallaLogin.fxml"));
        

            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cinemax/moduloboletos/vistas/VentaDeBoletos/datos-cliente-view.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vista/empleados/PantallaPortalPrincipal.fxml"));
            try {
                Parent root = loader.load();

                ControladorPortalPrincipal controlador = loader.getController();
                controlador.initialize();

                // Obtener el Stage actual desde el botón o cualquier nodo
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Portal del Administrador");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
e.printStackTrace();            }

        } else {
            lblError.setText("Usuario o contraseña incorrectos.");
            lblError.setVisible(true);
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
            return gestorSesionSingleton.iniciarSesion(nomUsuario, password);
        } catch (Exception e) {
            return false;
        }
    }
}





