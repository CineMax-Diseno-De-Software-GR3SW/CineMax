package com.cinemax.empleados.controladores;

    import java.io.IOException;
    import java.net.URL;

    import com.cinemax.empleados.modelos.entidades.Permiso;
    import com.cinemax.empleados.modelos.entidades.Usuario;
    import com.cinemax.empleados.servicios.ServicioSesionSingleton;

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
        public Button btnConfiguracionSalas;
        @FXML
        public Button btnConfiguracionButacas;
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
            Usuario u = gestorSesion.getUsuarioActivo();
            lblNombreUsuario.setText(u.getNombreCompleto());
            lblRolUsuario.setText(u.getDescripcionRol());

            habilitarOpcionSiTienePermiso(btnGestionUsuarios, Permiso.GESTIONAR_USUARIO);
            habilitarOpcionSiTienePermiso(btnVerReportes, Permiso.GESTIONAR_REPORTES);
            habilitarOpcionSiTienePermiso(btnConfiguracionFunciones, Permiso.GESTIONAR_FUNCION);
            habilitarOpcionSiTienePermiso(btnConfiguracionSalas, Permiso.GESTIONAR_SALA);
            habilitarOpcionSiTienePermiso(btnVentaBoleto, Permiso.VENDER_BOLETO);
            habilitarOpcionSiTienePermiso(btnConfiguracionButacas, Permiso.GESTIONAR_SALA);
        }

        /* Simplifica: si no tiene alguno de los permisos, oculta (sin dejar hueco) */
        private void habilitarOpcionSiTienePermiso(Node nodo, Permiso permiso) {
            boolean visible = gestorSesion.tienePermiso(permiso);
            nodo.setVisible(visible);
            nodo.setManaged(visible); // evita huecos
        }

        // Métodos de navegación para módulos
        @FXML
        private void onGestionUsuarios(ActionEvent event) {
            navegarA("/vistas/empleados/PantallaGestionUsuarios.fxml", "Gestión de Usuarios", event);
        }

        @FXML
        private void onVerReportes(ActionEvent event) {
            System.out.println("Navegar a Ver Reportes");
            // TODO: Implementar navegación a la pantalla de reportes
        }

        @FXML
        private void onConfiguracionFunciones(ActionEvent event) {
            System.out.println("Navegar a Configuración de Funciones");
            // TODO: Implementar navegación a la pantalla de configuración
        }

        @FXML
        public void onConfiguracionSalas(ActionEvent event) {
            navegarA("/vistas/salas/VistaGSalas.fxml", "Configuración de Salas", event);
        }

        @FXML
        public void onConfiguracionButacas(ActionEvent event) {
            navegarA("/vistas/salas/VistaGButacas.fxml", "Configuración de Butacas", event);
        }

        @FXML
        private void onVenderBoleto(ActionEvent event) {
            System.out.println("Navegar a Vender Boleto");
            // TODO: Implementar navegación a la pantalla de venta de boletos
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
            navegarA("/vistas/peliculas/PantallaGestionPeliculas.fxml", "Gestión de Películas", event);
        }

        @FXML
        private void onGestionCartelera(ActionEvent event) {
            navegarA("/vistas/peliculas/PantallaGestionCartelera.fxml", "Gestión de Cartelera", event);
        }

        @FXML
        private void onGestionFunciones(ActionEvent event) {
            navegarA("/vistas/peliculas/PantallaGestionFunciones.fxml", "Gestión de Funciones", event);
        }

        @FXML
        private void onSeleccionFuncion(ActionEvent event) {
            navegarA("/vistas/peliculas/PantallaSeleccionFuncion.fxml", "Selección de Función", event);
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