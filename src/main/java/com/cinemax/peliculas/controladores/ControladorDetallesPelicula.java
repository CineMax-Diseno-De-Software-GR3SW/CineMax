package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.util.ResourceBundle;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.servicios.ServicioPelicula;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ControladorDetallesPelicula implements Initializable {

    @FXML private ImageView imgPelicula;
    @FXML private Label lblTitulo;
    @FXML private Label lblId;
    @FXML private Label lblAnio;
    @FXML private Label lblDuracion;
    @FXML private Label lblIdioma;
    @FXML private Label lblGeneros;
    @FXML private Label lblSinopsis;
    @FXML private Label lblUrlImagen;
    @FXML private Label lblFechaCreacion;
    @FXML private Label lblEstado;
    @FXML private Label lblTotalFunciones;
    @FXML private Label lblProximaFuncion;
    @FXML private Label lblUltimaModificacion;

    @FXML private Button btnVolver;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVerFunciones;
    @FXML private Button btnCompartir;
    @FXML private Button btnLogOut;

    private ServicioPelicula servicioPelicula;
    private Pelicula pelicula;

    public ControladorDetallesPelicula() {
        this.servicioPelicula = new ServicioPelicula();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // La configuración se hará cuando se cargue la película específica
    }

    public void cargarPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
        mostrarDetallesPelicula();
    }

    private void mostrarDetallesPelicula() {
        if (pelicula == null) return;

        // Información básica
        lblTitulo.setText(pelicula.getTitulo());
        lblId.setText(String.valueOf(pelicula.getId()));
        lblAnio.setText(String.valueOf(pelicula.getAnio()));
        lblDuracion.setText(pelicula.getDuracionMinutos() + " min");
        lblIdioma.setText(pelicula.getIdioma() != null ? pelicula.getIdioma().getNombre() : "No especificado");
        lblGeneros.setText(pelicula.getGenerosComoString());
        
        // Sinopsis
        String sinopsis = pelicula.getSinopsis();
        if (sinopsis != null && !sinopsis.trim().isEmpty()) {
            lblSinopsis.setText(sinopsis);
        } else {
            lblSinopsis.setText("No hay sinopsis disponible para esta película.");
            lblSinopsis.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");
        }

        // URL de imagen
        String urlImagen = pelicula.getImagenUrl();
        if (urlImagen != null && !urlImagen.trim().isEmpty()) {
            lblUrlImagen.setText(urlImagen);
            cargarImagen(urlImagen);
        } else {
            lblUrlImagen.setText("No hay imagen disponible");
            cargarImagenPorDefecto();
        }

        // Información técnica (valores estáticos por ahora)
        lblFechaCreacion.setText("Información no disponible");
        lblEstado.setText("Activa");
        lblUltimaModificacion.setText("Información no disponible");

        // Cargar información de funciones de forma asíncrona
        cargarInformacionFunciones();
    }

    private void cargarImagen(String urlImagen) {
        try {
            Image imagen = new Image(urlImagen, true); // true para carga asíncrona
            imgPelicula.setImage(imagen);
        } catch (Exception e) {
            cargarImagenPorDefecto();
        }
    }

    private void cargarImagenPorDefecto() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/no-image.png"));
            imgPelicula.setImage(defaultImage);
        } catch (Exception e) {
            // Si no hay imagen por defecto, dejar vacío
            imgPelicula.setImage(null);
        }
    }

    private void cargarInformacionFunciones() {
        // Por ahora valores estáticos, se puede implementar con DAO de funciones
        lblTotalFunciones.setText("Por implementar");
        lblProximaFuncion.setText("Por implementar");
    }

    @FXML
    private void onVolver(ActionEvent event) {
        volverAPantallaPrincipal();
    }

    @FXML
    private void onEditar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/peliculas/PantallaFormularioPelicula.fxml"));
            Parent root = loader.load();
            
            // Configurar el controlador para modo edición
            ControladorFormularioPelicula controlador = loader.getController();
            controlador.configurarParaEdicion(pelicula);
            
            Stage stage = (Stage) btnEditar.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("Error de navegación", "No se pudo abrir el formulario de edición: " + e.getMessage());
        }
    }

    @FXML
    private void onEliminar(ActionEvent event) {
        try {
            // Mostrar advertencia de confirmación
            String mensaje = "¿Está seguro de eliminar esta película?\n\n" +
                           "Título: " + pelicula.getTitulo() + 
                           "\n\nATENCIÓN: Esta acción no se puede deshacer.";
            ManejadorMetodosComunes.mostrarVentanaAdvertencia(mensaje);
            
            // Proceder con la eliminación
            servicioPelicula.eliminarPelicula(pelicula.getId());
            mostrarInformacion("Éxito", "Película eliminada correctamente");
            
            // Volver a la pantalla principal
            volverAPantallaPrincipal();
            
        } catch (Exception e) {
            String mensajeError = e.getMessage();
            if (mensajeError.contains("foreign key constraint") || mensajeError.contains("violates")) {
                mostrarErrorRestriccion();
            } else {
                mostrarError("Error", "No se pudo eliminar la película: " + mensajeError);
            }
        }
    }

    @FXML
    private void onVerFunciones(ActionEvent event) {
        // TODO: Implementar navegación a funciones
        mostrarInformacion("Funciones", "Navegación a funciones por implementar");
    }

    @FXML
    private void onCompartir(ActionEvent event) {
        // TODO: Implementar exportar información
        mostrarInformacion("Exportar", "Funcionalidad de exportar por implementar");
    }

    @FXML
    private void onLogOut(ActionEvent event) {
        ManejadorMetodosComunes.mostrarVentanaAdvertencia("Sesión cerrada");
        javafx.application.Platform.exit();
    }

    private void volverAPantallaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/peliculas/PantallaGestionPeliculas.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("Error de navegación", "No se pudo volver a la pantalla principal: " + e.getMessage());
        }
    }

    private void mostrarErrorRestriccion() {
        String mensaje = "No se puede eliminar la película '" + pelicula.getTitulo() + 
                        "' porque está asociada con:\n\n" +
                        "• Funciones programadas\n" +
                        "• Cartelera\n" +
                        "• Boletos vendidos\n" +
                        "• Reservas existentes\n\n" +
                        "ACCIÓN REQUERIDA:\n" +
                        "Para eliminar esta película, primero debe eliminar todas las funciones\n" +
                        "y entradas de cartelera asociadas en el gestor correspondiente.";
        
        ManejadorMetodosComunes.mostrarVentanaError(mensaje);
    }

    private void mostrarError(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaError(mensaje != null ? mensaje : "Error desconocido");
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaExito(mensaje != null ? mensaje : "Operación completada");
    }
}
