package com.cinemax.empleados.controladores;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.cinemax.empleados.modelos.entidades.Usuario;
import com.cinemax.empleados.servicios.ServicioPerfilUsuario;
import com.cinemax.empleados.servicios.ServicioSesionSingleton;

import com.cinemax.empleados.servicios.ServicioUsuarios;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControladorPerfil implements Initializable {

    @FXML
    private Label lblNombreCompleto;

    @FXML
    private Label lblCedula;

    @FXML
    private Label lblUsuario;

    @FXML
    private Label lblRol;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtTelefono;

    @FXML
    private Button btnEditarPerfil;

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
    private void onVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditarPerfil(ActionEvent event) {
        // Implementar funcionalidad para editar perfil
        System.out.println("Editar perfil clicked");
    }

    @FXML
    private void onCambiarContrasena(ActionEvent event) {
        // Implementar funcionalidad para cambiar contraseña
        //System.out.println("Cambiar contraseña clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PopUpCambiarContrasena.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            // Aplicar hoja de estilos
            scene.getStylesheets().add(getClass().getResource("/vistas/temas/ayu-theme.css").toExternalForm());

            // Crear stage
            Stage stage = new Stage();
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
    private void onEditarEmail() {
        editandoEmail = !editandoEmail;
        txtEmail.setEditable(editandoEmail);

        if (!editandoEmail) {
            String nuevoEmail = txtEmail.getText();
            // Aquí podrías guardar el email a base de datos o backend
            System.out.println("Nuevo email guardado: " + nuevoEmail);
            // Relación con el servicio actualizarCorreo
            try {
                servicioPerfilUsuario.actualizarCorreo(sesionSingleton.getUsuarioActivo(),nuevoEmail);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Correo Registrado", "Actualizacion", "Correo " + nuevoEmail + " registrado exitosamente");
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Error al registrar el correo", "Error", e.getMessage());
            }
        }
    }

    @FXML
    private void onEditarTelefono() {
        editandoTelefono = !editandoTelefono;
        txtTelefono.setEditable(editandoTelefono);

        if (!editandoTelefono) {
            String nuevoCelular = txtTelefono.getText();
            // Aquí podrías guardar el teléfono a base de datos o backend
            System.out.println("Nuevo teléfono guardado: " + nuevoCelular);
            try {
                servicioPerfilUsuario.actualizarCelular(sesionSingleton.getUsuarioActivo(),nuevoCelular);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Celular Registrado", "Actualizacion", "Celular " + nuevoCelular + " registrado exitosamente");
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Error al registrar el celular", "Error", e.getMessage());
            }
        }
    }



    private void mostrarAlerta(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}