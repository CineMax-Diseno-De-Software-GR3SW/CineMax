package com.cinemax.empleados.controladores;


import com.cinemax.empleados.modelos.entidades.Rol;
import com.cinemax.empleados.modelos.entidades.Usuario;
import com.cinemax.empleados.servicios.ServicioRoles;
import com.cinemax.empleados.servicios.ServicioUsuarios;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControladorRegistrarUsuario implements Initializable {

    @FXML private TextField campoNombres;
    @FXML private TextField campoApellidos;
    @FXML private TextField campoCedula;
    @FXML private TextField campoCorreo;
    @FXML private TextField campoCelular;
    @FXML private TextField campoNombreUsuario;
    @FXML private PasswordField campoContrasena;
    @FXML private PasswordField campoConfirmar;
    @FXML private ComboBox<Rol> comboBoxRol;
    @FXML private RadioButton radioActivo;

    private ServicioUsuarios servicioUsuarios;
    private ServicioRoles servicioRoles;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        servicioUsuarios = new ServicioUsuarios();
        servicioRoles = new ServicioRoles();

        // Configurar el ComboBox de Roles
        try {
            ObservableList<Rol> roles = FXCollections.observableArrayList(servicioRoles.listarRoles());
            comboBoxRol.setItems(roles);

            // Agregar un StringConverter para mostrar solo el nombre del rol
            comboBoxRol.setConverter(new StringConverter<Rol>() {
                @Override
                public String toString(Rol rol) {
                    return (rol == null) ? "" : rol.getNombre();
                }

                @Override
                public Rol fromString(String nombre) {
                    return roles.stream()
                            .filter(r -> r.getNombre().equals(nombre))
                            .findFirst()
                            .orElse(null);
                }
            });

            if (!roles.isEmpty()) {
                comboBoxRol.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Error al Cargar", "Error al cargar Roles", "No se pudieron cargar los roles de usuario.");
        }
    }

    @FXML
    private void handleRegistrarUsuario(ActionEvent event) {
        String nombres = campoNombres.getText().trim();
        String apellidos = campoApellidos.getText().trim();
        String cedula = campoCedula.getText().trim();
        String correo = campoCorreo.getText().trim();
        String celular = campoCelular.getText().trim();
        Rol cargoSeleccionado = comboBoxRol.getSelectionModel().getSelectedItem();
        boolean estadoActivo = radioActivo.isSelected();
        String nombreUsuario = campoNombreUsuario.getText().trim();
        String contrasena = campoContrasena.getText();
        String confirmarContrasena = campoConfirmar.getText();

        // Validaciones básicas
        if (nombres.isEmpty() || apellidos.isEmpty() || cedula.isEmpty() || correo.isEmpty() ||
                celular.isEmpty() || nombreUsuario.isEmpty() ||
                contrasena.isEmpty() || confirmarContrasena.isEmpty() || cargoSeleccionado == null) {
            mostrarAlerta(AlertType.ERROR, "Campos Vacíos", "Error de Datos", "Por favor, complete todos los campos obligatorios.");
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            mostrarAlerta(AlertType.ERROR, "Contraseñas no Coinciden", "Error de Contraseña", "Las contraseñas ingresadas no coinciden. Por favor, verifique.");
            return;
        }

        //TODO: NO, Hacerlo desde el servicio
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombreCompleto(nombres + " " + apellidos);
        nuevoUsuario.setCedula(cedula);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setCelular(celular);
        nuevoUsuario.setActivo(estadoActivo);
        nuevoUsuario.setNombreUsuario(nombreUsuario);
        nuevoUsuario.setClave(contrasena);
        nuevoUsuario.setRol(cargoSeleccionado);

        try {
            servicioUsuarios.crearUsuario(nuevoUsuario);

            mostrarAlerta(AlertType.INFORMATION, "Registro Exitoso", "Empleado Registrado", "El empleado " + nuevoUsuario.getNombreCompleto() + " ha sido registrado correctamente.");
            limpiarCampos();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IllegalArgumentException e) {
            mostrarAlerta(AlertType.WARNING, "Error de Validación", "Datos Incorrectos", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Error de Registro", "Fallo al Registrar Empleado", "Ocurrió un error al intentar registrar el empleado: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
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

    private void limpiarCampos() {
        campoNombres.clear();
        campoApellidos.clear();
        campoCedula.clear();
        campoCorreo.clear();
        campoCelular.clear();
        campoNombreUsuario.clear();
        campoContrasena.clear();
        campoConfirmar.clear();
        comboBoxRol.getSelectionModel().clearSelection();
        radioActivo.setSelected(true);
    }

    private void mostrarAlerta(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
