package com.cinemax.empleados.controladores;

import com.cinemax.empleados.modelos.entidades.Permiso;
import com.cinemax.empleados.modelos.entidades.Usuario;
import com.cinemax.empleados.servicios.ServicioSesionSingleton;
import com.cinemax.utilidades.ManejadorMetodosComunes;
import com.cinemax.venta_boletos.servicios.ServicioTemporizador;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ControladorPortalPrincipal {

    public Button btnMiPerfil;
    public Button btnCerrarSesion;
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
        ManejadorMetodosComunes.cambiarVentana((Stage) btnGestionUsuarios.getScene().getWindow(),
                "/vistas/empleados/PantallaGestionUsuarios.fxml");
    }

    @FXML
    private void onVerReportes(ActionEvent event) {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnVerReportes.getScene().getWindow(),
                "/vistas/reportes/VistaReportesPrincipal.fxml");
    }

    @FXML
    public void onGestionSalas() {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnGestionSalas.getScene().getWindow(),
                "/vistas/salas/VistaGestionSalas.fxml");
    }

    @FXML
    public void onGestionButacas() {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnGestionButacas.getScene().getWindow(),
                "/vistas/salas/VistaGestionButacas.fxml");
    }
    @FXML
    private void onVenderBoleto() {

        ManejadorMetodosComunes.mostrarPantallaDeCargaOptimizada(
            (Stage) btnVentaBoleto.getScene().getWindow(), 
            "/vistas/venta_boletos/VistaMostrarCartelera.fxml", 
            33, 
            225);
        //ManejadorMetodosComunes.cambiarVentana((Stage) btnVentaBoleto.getScene().getWindow(),
        //        "/vistas/venta_boletos/VistaMostrarCartelera.fxml");
    }

    @FXML
    private void onCerrarSesion() {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnCerrarSesion.getScene().getWindow(),
                "/vistas/empleados/PantallaLogin.fxml");
    }

    @FXML
    private void onMiPerfil() {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnMiPerfil.getScene().getWindow(),
                "/vistas/empleados/PantallaPerfil.fxml");
    }

    @FXML
    private void onGestionPeliculas() {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnGestionButacas.getScene().getWindow(),
                "/vistas/peliculas/PantallaGestionPeliculas.fxml");
    }

    @FXML
    private void onGestionCartelera() {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnGestionButacas.getScene().getWindow(),
                "/vistas/peliculas/PantallaGestionCartelera.fxml");
    }

    @FXML
    private void onGestionFunciones() {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnGestionButacas.getScene().getWindow(),
                "/vistas/peliculas/PantallaGestionFunciones.fxml");
    }

    @FXML
    private void onSeleccionFuncion() {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnGestionButacas.getScene().getWindow(),
                "/vistas/peliculas/PantallaSeleccionFuncion.fxml");
    }

//    // Metodo genérico para navegación
//    private void navegarA(String rutaFXML, String titulo, ActionEvent event) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
//            Parent root = loader.load();
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//            stage.setTitle(titulo);
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
