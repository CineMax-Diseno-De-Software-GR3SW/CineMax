package com.cinemax.empleados.controladores;

import java.io.IOException;
import java.net.URL;

import com.cinemax.empleados.modelos.entidades.Permiso;
import com.cinemax.empleados.modelos.entidades.Usuario;
import com.cinemax.empleados.servicios.ServicioSesionSingleton;
import com.cinemax.venta_boletos.Util.ThemeManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ControladorPortalPrincipal {

    @FXML
    public Button btnConfiguracionSalas;
    @FXML
    private Button btnGestionUsuarios;
    @FXML
    private Button btnVerReportes;
    @FXML
    private Button btnConfiguracionFunciones;
    @FXML
    private Button btnVentaBoleto;
    @FXML
    private Label lblNombreUsuario;
    @FXML
    private Label lblRolUsuario;
    @FXML
    private HBox headerBar;

        private ServicioSesionSingleton gestorSesion;

        /**
         * Método para inicializar el controlador con el usuario activo.
         * Debe llamarse después de cargar la vista.
         */
        @FXML
        public void initialize() {
            gestorSesion = ServicioSesionSingleton.getInstancia();
//            cargarDatos();
         Usuario u = gestorSesion.getUsuarioActivo();
         lblNombreUsuario.setText(u.getNombreCompleto());
         lblRolUsuario.setText(u.getDescripcionRol());

//            System.out.println(gestorSesion.getUsuarioActivo().toString());
//                        System.out.println(gestorSesion.getUsuarioActivo().getRol().toString());
//            for (Permiso i : gestorSesion.getUsuarioActivo().getRol().getPermisos()) {
//                System.out.println(i);
//
//            }
//            // Controlar visibilidad de botones según permisos
//            btnGestionUsuarios.setVisible(gestorSesion.tienePermiso(Permiso.GESTIONAR_USUARIO));
//            btnVerReportes.setVisible(gestorSesion.tienePermiso(Permiso.GESTIONAR_REPORTES));
//            btnConfiguracion.setVisible(gestorSesion.tienePermiso(Permiso.GESTIONAR_SALA) || gestorSesion.tienePermiso(Permiso.GESTIONAR_FUNCION));
//            btnVentaBoleto.setVisible(gestorSesion.tienePermiso(Permiso.VENDER_BOLETO));
//        }

    habilitarOpcionSiTienePermiso(btnGestionUsuarios,   Permiso.GESTIONAR_USUARIO);
    habilitarOpcionSiTienePermiso(btnVerReportes,   Permiso.GESTIONAR_REPORTES);
    habilitarOpcionSiTienePermiso(btnConfiguracionFunciones,     Permiso.GESTIONAR_FUNCION);
    habilitarOpcionSiTienePermiso(btnConfiguracionSalas,     Permiso.GESTIONAR_SALA);
    habilitarOpcionSiTienePermiso(btnVentaBoleto,     Permiso.VENDER_BOLETO);

        }

    // // --- Control dinámico de permisos ---


//         // Selecciona la vista por defecto
//         btnCartelera.setSelected(true);
//     }

    /* Simplifica: si no tiene alguno de los permisos, oculta (sin dejar hueco) */
    private void habilitarOpcionSiTienePermiso(Node nodo, Permiso permiso) {
        boolean visible = gestorSesion.tienePermiso(permiso);
        nodo.setVisible(visible);
        nodo.setManaged(visible);           // evita huecos
    }

        @FXML
        private void onGestionUsuarios(ActionEvent event) {
            System.out.println("Navegar a Gestión de Usuarios");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaGestionUsuarios.fxml"));
            try {
                Parent root = loader.load();

                // Obtener el Stage actual desde el botón o cualquier nodo
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Portal del Administrador");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        @FXML
        private void onVerReportes(ActionEvent event) {
            System.out.println("Navegar a Ver Reportes");
            // TODO: Implementar navegación a la pantalla de reportes
        }

        @FXML
        private void onConfiguracionFunciones(ActionEvent event) {
            System.out.println("Navegar a Configuración");
            // TODO: Implementar navegación a la pantalla de configuración
        }

        public void onConfiguracionSalas(ActionEvent event) {
        }

        @FXML
        private void onVenderBoleto(ActionEvent event) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/cartelera-view.fxml"));
            try {
                Parent root = loader.load();

                // Obtener el Stage actual desde el botón o cualquier nodo
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("CineMax - Cartelera");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("Navegar a Vender Boleto");
            
        }

        @FXML
        private void onCerrarSesion(ActionEvent event) {
            System.out.println("Cerrar sesión y volver al login");
            // TODO: Implementar cerrar sesión y volver a la pantalla de login
            URL url = getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml");
            System.out.println(url); // Si imprime null, el archivo no se encuentra

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaLogin.fxml"));
            try {
                Parent root = loader.load();

                // Obtener el Stage actual desde el botón o cualquier nodo
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Portal del Administrador");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
                // Ejemplo de cerrar ventana actual (si fuera necesario)
            // Stage stage = (Stage) txtBienvenida.getScene().getWindow();
            // stage.close();
        }

    @FXML
    private void onMiPerfil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaPerfil.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


