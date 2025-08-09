package com.cinemax.empleados.controladores;

import java.io.IOException;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.empleados.modelos.entidades.Permiso;
import com.cinemax.empleados.modelos.entidades.Usuario;
import com.cinemax.empleados.servicios.ServicioSesionSingleton;

import com.cinemax.venta_boletos.servicios.ServicioTemporizador;
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

    // Botones de navegación principal
    @FXML
    private Button btnGestionPeliculas;
    @FXML
    private Button btnGestionCartelera;
    @FXML
    private Button btnGestionFunciones;
    @FXML
    private Button btnSeleccionFuncion;

    // Otros botones del sistema
    @FXML
    public Button btnGestionSalas;
    @FXML
    public Button btnGestionButacas;
    @FXML
    private Button btnGestionUsuarios;
    @FXML
    private Button btnVerReportes;
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
        Usuario u = gestorSesion.getUsuarioActivo();
        lblNombreUsuario.setText(u.getNombreCompleto());
        lblRolUsuario.setText(u.getDescripcionRol());

        habilitarOpcionSiTienePermiso(btnGestionUsuarios, Permiso.GESTIONAR_USUARIO);
        habilitarOpcionSiTienePermiso(btnVerReportes, Permiso.GESTIONAR_REPORTES);
        habilitarOpcionSiTienePermiso(btnGestionSalas, Permiso.GESTIONAR_SALA);
        habilitarOpcionSiTienePermiso(btnVentaBoleto, Permiso.VENDER_BOLETO);
        habilitarOpcionSiTienePermiso(btnGestionButacas, Permiso.GESTIONAR_SALA);

        if (ServicioTemporizador.getInstance().tempEnEjecucion()){
            ServicioTemporizador.getInstance().detenerTemporizador();
        }
    }

    /* Simplifica: si no tiene alguno de los permisos, oculta (sin dejar hueco) */
    private void habilitarOpcionSiTienePermiso(Node nodo, Permiso permiso) {
        boolean visible = gestorSesion.tienePermiso(permiso);
        nodo.setVisible(visible);
        nodo.setManaged(visible); // evita huecos
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
        navegarA("/vistas/reportes/PantallaModuloReportesPrincipal.fxml", "Módulo de Reportes", event);
    }

    @FXML
    private void onConfiguracionFunciones(ActionEvent event) {
        System.out.println("Navegar a Configuración");
        // TODO: Implementar navegación a la pantalla de configuración
    }

    @FXML
    public void onGestionSalas(ActionEvent event) {
       // navegarA("/vistas/salas/VistaGSalas.fxml", "Configuración de Salas", event);
        ManejadorMetodosComunes.cambiarVentana((Stage) btnGestionSalas.getScene().getWindow(),
                "/vistas/salas/VistaGSalas.fxml");
    }

    @FXML
    public void onGestionButacas(ActionEvent event) {
        //navegarA("/vistas/salas/VistaGButacas.fxml", "Configuración de Butacas", event);
        ManejadorMetodosComunes.cambiarVentana((Stage) btnGestionButacas.getScene().getWindow(),
                "/vistas/salas/VistaGButacas.fxml");
    }
    @FXML
    private void onVenderBoleto(ActionEvent event) {
        // navegarA("/vistas/venta_boletos/VistaMostrarCartelera.fxml", "Venta de
        // Boletos", event);
        ManejadorMetodosComunes.cambiarVentana((Stage) btnVentaBoleto.getScene().getWindow(),
                "/vistas/venta_boletos/VistaMostrarCartelera.fxml");
    }

    @FXML
    private void onCerrarSesion(ActionEvent event) {
        navegarA("/vistas/empleados/PantallaLogin.fxml", "Login", event);
    }

    @FXML
    private void onMiPerfil(ActionEvent event) {
        navegarA("/vistas/empleados/PantallaPerfil.fxml", "Mi Perfil", event);
    }

    @FXML
    private void onGestionPeliculas(ActionEvent event) {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnGestionButacas.getScene().getWindow(),
                "/vistas/peliculas/PantallaGestionPeliculas.fxml");
    }

    @FXML
    private void onGestionCartelera(ActionEvent event) {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnGestionButacas.getScene().getWindow(),
                "/vistas/peliculas/PantallaGestionCartelera.fxml");
    }

    @FXML
    private void onGestionFunciones(ActionEvent event) {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnGestionButacas.getScene().getWindow(),
                "/vistas/peliculas/PantallaGestionFunciones.fxml");
    }

    @FXML
    private void onSeleccionFuncion(ActionEvent event) {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnGestionButacas.getScene().getWindow(),
                "/vistas/peliculas/PantallaSeleccionFuncion.fxml");
    }

    // Metodo genérico para navegación
    private void navegarA(String rutaFXML, String titulo, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
