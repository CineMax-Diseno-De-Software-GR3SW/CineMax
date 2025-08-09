package com.cinemax.empleados.controladores;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.empleados.modelos.entidades.Rol;
import com.cinemax.empleados.modelos.entidades.Usuario;
import com.cinemax.empleados.servicios.ServicioRoles;
import com.cinemax.empleados.servicios.ServicioUsuarios;

import com.cinemax.empleados.servicios.ValidadorUsuario;
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

    @FXML
    private TextField campoNombres;
    @FXML
    private TextField campoApellidos;
    @FXML
    private TextField campoCedula;
    @FXML
    private TextField campoCorreo;
    @FXML
    private TextField campoCelular;
    @FXML
    private TextField campoNombreUsuario;
    @FXML
    private ComboBox<Rol> comboBoxRol;
    @FXML
    private RadioButton radioActivo;

    private ServicioUsuarios servicioUsuarios;
    private ServicioRoles servicioRoles;
    private ValidadorUsuario validadorUsuario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        servicioUsuarios = new ServicioUsuarios();
        servicioRoles = new ServicioRoles();
        validadorUsuario = new ValidadorUsuario();

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
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al cargar Roles");
            //mostrarAlerta(AlertType.ERROR, "Error al Cargar", "Error al cargar Roles", "No se pudieron cargar los roles de usuario.");
        }

        campoCelular.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            // Eliminar caracteres no numéricos
            String filteredValue = newValue.replaceAll("[^\\d]", "");

            // Limitar a 10 caracteres
            if (filteredValue.length() > 10) {
                filteredValue = filteredValue.substring(0, 10);
            }

            // Actualizar el campo de texto solo si es diferente para evitar un bucle infinito
            if (!campoCelular.getText().equals(filteredValue)) {
                campoCelular.setText(filteredValue);
            }
        });

        campoCedula.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            String filteredValue = newValue.replaceAll("[^\\d]", ""); // Eliminar caracteres no numéricos
            if (filteredValue.length() > 10) {
                filteredValue = filteredValue.substring(0, 10); // Limitar a 10 dígitos
            }
            if (!campoCedula.getText().equals(filteredValue)) {
                campoCedula.setText(filteredValue);
            }
        });
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


        // Validaciones básicas
        if (nombres.isEmpty() || apellidos.isEmpty() || cedula.isEmpty() || correo.isEmpty() ||
                celular.isEmpty() || nombreUsuario.isEmpty() ||
                cargoSeleccionado == null) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Campos incompletos");
          //mostrarAlerta(AlertType.ERROR, "¡ERROR!", "Campos Incompletos", "Por favor, complete todos los campos obligatorios.");
            return;
        }

        //Validación adicional para el celular
        if (!celular.matches("\\d{10}")) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Formato de Celular Inválido \n Debe contener exactamente 10 dígitos");
            //mostrarAlerta(AlertType.ERROR, "¡ERROR!", "Formato de Celular Inválido", "Debe contener exactamente 10 dígitos");
            return;
        }

        if (!cedula.matches("\\d{10}")) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Formato de Cédula Inválido \n Debe contener exactamente 10 dígitos");

          //mostrarAlerta(AlertType.ERROR, "¡ERROR!", "Formato de Cédula Inválido", "La cédula debe contener exactamente 10 dígitos numéricos.");
            return;
        }
        if (!validadorUsuario.validarCorreo(correo)) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Formato de correo inválido");
            //mostrarAlerta(AlertType.ERROR, "¡ERROR!", "Formato de Cédula Inválido", "La cédula debe contener exactamente 10 dígitos numéricos.");
            return;
        }

//        if (!contrasena.equals(confirmarContrasena)) {
//            mostrarAlerta(AlertType.ERROR, "Contraseñas no Coinciden", "Error de Contraseña", "Las contraseñas ingresadas no coinciden. Por favor, verifique.");
//            return;
//        }

        //TODO: NO, Hacerlo desde el servicio
        String nombreCompleto = nombres + " " + apellidos;
        try {
            servicioUsuarios.crearUsuario(nombreCompleto, cedula, correo, celular, estadoActivo, nombreUsuario, cargoSeleccionado);
            ManejadorMetodosComunes.mostrarVentanaExito("Empleado creado exitosamente");

//            mostrarAlerta(AlertType.INFORMATION, "¡ÉXITO!", "Empleado registrado exitosamente", "El empleado " + nombreCompleto + " ha sido registrado correctamente.");
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
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al validar los datos");

//            mostrarAlerta(AlertType.WARNING, "¡ERROR!", "Sucedió algo inesperado al validar los datos. Datos Incorrectos", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al registrar Empleado");

//            mostrarAlerta(AlertType.ERROR, "¡ERROR!", "Error al Registrar Empleado", "Sucedió algo inesperado al intentar registrar el empleado: " + e.getMessage());
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
//        campoContrasena.clear();
//        campoConfirmar.clear();
        comboBoxRol.getSelectionModel().clearSelection();
        radioActivo.setSelected(true);
    }

//    private void mostrarAlerta(AlertType type, String title, String header, String content) {
//        Alert alert = new Alert(type);
//        alert.setTitle(title);
//        alert.setHeaderText(header);
//        alert.setContentText(content);
//        alert.showAndWait();
//    }
}
