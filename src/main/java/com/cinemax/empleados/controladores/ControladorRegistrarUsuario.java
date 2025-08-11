package com.cinemax.empleados.controladores;

import com.cinemax.empleados.modelos.entidades.Rol;
import com.cinemax.empleados.modelos.entidades.Usuario;
import com.cinemax.empleados.servicios.ServicioRoles;
import com.cinemax.empleados.servicios.ServicioUsuarios;

import com.cinemax.empleados.servicios.ValidadorUsuario;
import com.cinemax.utilidades.ManejadorMetodosComunes;
import com.cinemax.utilidades.EstrategiaValidacionDocumentos.ContextoValidacion;
import com.cinemax.utilidades.EstrategiaValidacionDocumentos.EstrategiaCedulaValidacion;

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

    public Button btnBack;
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
        }

        // Listener para restringir el campoNombres a solo letras y espacios
        campoNombres.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            String filteredValue = newValue.replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚñÑ ]", "");
            if (!campoNombres.getText().equals(filteredValue)) {
                campoNombres.setText(filteredValue);
            }
        });

        // Listener para restringir el campoApellidos a solo letras y espacios
        campoApellidos.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            String filteredValue = newValue.replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚñÑ ]", "");
            if (!campoApellidos.getText().equals(filteredValue)) {
                campoApellidos.setText(filteredValue);
            }
        });

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
            return;
        }

        //Validación adicional para el celular
        if (!celular.matches("\\d{10}")) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Formato de Celular Inválido \n Debe contener exactamente 10 dígitos");
            return;
        }

        if (!cedula.matches("\\d{10}")) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Formato de Cédula Inválido \n Debe contener exactamente 10 dígitos");
            return;
        }
        if (!validadorUsuario.validarCorreo(correo)) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Formato de correo inválido");
            return;
        }


        String nombreCompleto = nombres + " " + apellidos;
        //Validación de la cédula
        ContextoValidacion contextoValidacion = new ContextoValidacion();
        contextoValidacion.setEstrategia(new EstrategiaCedulaValidacion());
        if(!contextoValidacion.ejecutarEstrategia(campoCedula.getText())) {
            ManejadorMetodosComunes.mostrarVentanaError("Documento inválido: " + campoCedula.getText());
            return;
        }
        try {
            servicioUsuarios.crearUsuario(nombreCompleto, cedula, correo, celular, estadoActivo, nombreUsuario, cargoSeleccionado);
            ManejadorMetodosComunes.mostrarVentanaExito("Empleado creado exitosamente");
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
            ManejadorMetodosComunes.mostrarVentanaAdvertencia(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al registrar Empleado");
        }
    }

    @FXML
    private void handleCancelar() {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnBack.getScene().getWindow(),
                "/vistas/empleados/PantallaGestionUsuarios.fxml");
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml"));
//            Parent root = loader.load();
//            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void limpiarCampos() {
        campoNombres.clear();
        campoApellidos.clear();
        campoCedula.clear();
        campoCorreo.clear();
        campoCelular.clear();
        campoNombreUsuario.clear();
        comboBoxRol.getSelectionModel().clearSelection();
        radioActivo.setSelected(true);
    }

}
