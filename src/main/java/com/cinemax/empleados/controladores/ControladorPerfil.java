package com.cinemax.empleados.controladores;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.cinemax.Main;
import com.cinemax.utilidades.ManejadorMetodosComunes;
import com.cinemax.empleados.modelos.entidades.Usuario;
import com.cinemax.empleados.servicios.ServicioPerfilUsuario;
import com.cinemax.empleados.servicios.ServicioSesionSingleton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ControladorPerfil implements Initializable {

    public Button btnBack;
    public Button btnActualizarContacto;
    @FXML
    private Label lblNombreCompleto;

    @FXML
    private Label lblCedula;

    @FXML
    private Label lblUsuario;

    @FXML
    private Label lblRol;

    @FXML
    private Label txtEmail;

    @FXML
    private Label txtTelefono;

    @FXML
    private Button btnCambiarContrasena;

    private ServicioSesionSingleton sesionSingleton;

    private boolean editandoEmail = false;

    private boolean editandoTelefono = false;
    private ServicioPerfilUsuario servicioPerfilUsuario;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sesionSingleton = ServicioSesionSingleton.getInstancia();
        servicioPerfilUsuario = new ServicioPerfilUsuario();

        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        Usuario usuarioActual = sesionSingleton.getUsuarioActivo();
        if (usuarioActual != null) {
            lblNombreCompleto.setText(usuarioActual.getNombreCompleto());
            lblCedula.setText(usuarioActual.getCedula());
            lblUsuario.setText(usuarioActual.getNombreUsuario());
            lblRol.setText(usuarioActual.getRol().getNombre());
            // lblEmail.setText(usuarioActual.getEmail());
            // lblTelefono.setText(usuarioActual.getTelefono());
            txtEmail.setText(usuarioActual.getCorreo());
            txtTelefono.setText(usuarioActual.getCelular());
        }
    }

    @FXML
    private void onRegresar() {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnBack.getScene().getWindow(),
                "/vistas/empleados/PantallaPortalPrincipal.fxml");
    }


    @FXML
    private void cambiarContrasena(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PopUpCambiarContrasena.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Crear stage
            Stage stage = new Stage();
            Image icon = new Image(Main.class.getResourceAsStream("/imagenes/logo.png"));
            stage.getIcons().add(icon);
            stage.setTitle("Cambiar Contraseña");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initOwner(((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow()));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void actualizarContacto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PopUpActualizarContacto.fxml"));
            Parent root = loader.load();

            ControladorActualizarContacto controller = loader.getController();
            controller.setDatosActuales(txtEmail.getText(), txtTelefono.getText());
            controller.setDialogStage((Stage)this.btnActualizarContacto.getScene().getWindow());
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            Image icon = new Image(Main.class.getResourceAsStream("/imagenes/logo.png"));
            stage.getIcons().add(icon);
            stage.setTitle("Actualizar Contacto");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initOwner(((Stage) btnActualizarContacto.getScene().getWindow()));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}