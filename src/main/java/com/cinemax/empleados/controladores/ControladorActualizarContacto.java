package com.cinemax.empleados.controladores;

import com.cinemax.utilidades.ManejadorMetodosComunes;
import com.cinemax.empleados.modelos.entidades.Usuario;
import com.cinemax.empleados.servicios.ServicioPerfilUsuario;
import com.cinemax.empleados.servicios.ServicioSesionSingleton;
import com.cinemax.empleados.servicios.ValidadorUsuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControladorActualizarContacto {

    public Button btnCancelar;
    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtCelular;

    @FXML
    private Button btnActualizar;


    @FXML
    private Stage dialogStage;

    private ServicioPerfilUsuario servicioPerfilUsuario;
    private ServicioSesionSingleton sesionSingleton;
    public void initialize() {
        sesionSingleton = ServicioSesionSingleton.getInstancia();

        servicioPerfilUsuario = new ServicioPerfilUsuario();
        txtCelular.textProperty().addListener((observable, oldValue, newValue) -> {
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
            if (!txtCelular.getText().equals(filteredValue)) {
                txtCelular.setText(filteredValue);
            }
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setDatosActuales(String correo, String celular) {
        txtCorreo.setText(correo);
        txtCelular.setText(celular);
    }

    @FXML
    private void actualizarContacto() {
        String nuevoCorreo = txtCorreo.getText().trim();
        String nuevoCelular = txtCelular.getText().trim();

        if (nuevoCorreo.isEmpty() || nuevoCelular.isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, complete todos los campos.");
            return;
        }

        if (!ValidadorUsuario.validarCorreo(nuevoCorreo)) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, ingrese un correo válido.");
            return;
        }

        if (!ValidadorUsuario.validarCelular(nuevoCelular)) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, ingrese un número de celular válido.");
            return;
        }

        try {
            Usuario usuarioActivo = sesionSingleton.getUsuarioActivo();
            usuarioActivo.setCorreo(nuevoCorreo);
            usuarioActivo.setCelular(nuevoCelular);


            servicioPerfilUsuario.actualizarCorreo(usuarioActivo, usuarioActivo.getCorreo());
            servicioPerfilUsuario.actualizarCelular(usuarioActivo, usuarioActivo.getCelular());

            ManejadorMetodosComunes.mostrarVentanaExito("Información de contacto actualizada exitosamente.");

            ((Stage) btnActualizar.getScene().getWindow()).close();
            Stage parentStage = dialogStage;
            ManejadorMetodosComunes.cambiarVentana(parentStage,"/vistas/empleados/PantallaPortalPrincipal.fxml");
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Surgió un error inesperado al actualizar ");
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelarActualizacion() {
            ((Stage) btnCancelar.getScene().getWindow()).close();
    }

}