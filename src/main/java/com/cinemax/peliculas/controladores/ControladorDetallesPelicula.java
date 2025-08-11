package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.util.ResourceBundle;

import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.servicios.ServicioPelicula;
import com.cinemax.utilidades.ManejadorMetodosComunes;

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

/**
 * Controlador para la visualización de detalles completos de una película.
 *
 * <p>Esta clase maneja la interfaz gráfica que muestra información detallada
 * de una película específica, incluyendo todos sus datos básicos, imagen,
 * sinopsis y opciones de navegación para edición.
 *
 * <p>Funcionalidades principales:
 * <ul>
 *   <li>Visualización completa de información de película</li>
 *   <li>Carga segura de imágenes con fallback</li>
 *   <li>Navegación a formulario de edición</li>
 *   <li>Interfaz limpia y enfocada en los detalles</li>
 *   <li>Manejo de datos faltantes o incompletos</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public class ControladorDetallesPelicula implements Initializable {

    /** Imagen de la película */
    @FXML private ImageView imgPelicula;
    
    /** Título de la película */
    @FXML private Label lblTitulo;
    
    /** ID único de la película */
    @FXML private Label lblId;
    
    /** Año de estreno */
    @FXML private Label lblAnio;
    
    /** Duración en minutos */
    @FXML private Label lblDuracion;
    
    /** Idioma de la película */
    @FXML private Label lblIdioma;
    
    /** Géneros de la película */
    @FXML private Label lblGeneros;
    
    /** Sinopsis de la película */
    @FXML private Label lblSinopsis;
    
    /** URL de la imagen */
    @FXML private Label lblUrlImagen;
    
    /** Botón para volver */
    @FXML private Button btnVolver;
    
    /** Botón para editar película */
    @FXML private Button btnEditar;
    
    /** Botón para cerrar sesión */
    @FXML private Button btnLogOut;

    /** Servicio para operaciones con películas */
    private ServicioPelicula servicioPelicula;
    
    /** Película actualmente cargada en la vista */
    private Pelicula pelicula;

    /**
     * Constructor que inicializa el servicio de películas.
     */
    public ControladorDetallesPelicula() {
        this.servicioPelicula = new ServicioPelicula();
    }

    /**
     * Inicializa el controlador después de que se ha cargado el FXML.
     * 
     * @param location La ubicación utilizada para resolver rutas relativas para el objeto raíz
     * @param resources Los recursos utilizados para localizar el objeto raíz
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // La configuración se hará cuando se cargue la película específica
    }

    /**
     * Carga y muestra los datos de una película específica.
     * 
     * <p>Este método configura toda la información visible en la interfaz
     * incluyendo datos básicos, imagen y sinopsis de la película.
     * 
     * @param pelicula La película a mostrar en detalle
     */
    public void cargarPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
        mostrarDetallesPelicula();
    }

    /**
     * Muestra los detalles completos de la película en la interfaz.
     * 
     * <p>Incluye información básica, sinopsis, URL de imagen y manejo
     * de datos faltantes con valores por defecto apropiados.
     */
    private void mostrarDetallesPelicula() {
        if (pelicula == null) return;

        // Información básica
        lblTitulo.setText(pelicula.getTitulo());
        lblId.setText(String.valueOf(pelicula.getId()));
        lblAnio.setText(String.valueOf(pelicula.getAnio()));
        lblDuracion.setText(pelicula.getDuracionMinutos() + " min");
        lblIdioma.setText(pelicula.getIdioma() != null ? pelicula.getIdioma().getNombre() : "No especificado");
        lblGeneros.setText(pelicula.getGenerosComoString());
        
        // Configurar sinopsis con manejo de datos faltantes
        configurarSinopsis();

        // Configurar URL de imagen y cargar imagen
        configurarImagen();
    }

    /**
     * Configura la sinopsis con manejo apropiado de datos faltantes.
     */
    private void configurarSinopsis() {
        String sinopsis = pelicula.getSinopsis();
        if (sinopsis != null && !sinopsis.trim().isEmpty()) {
            lblSinopsis.setText(sinopsis);
        } else {
            lblSinopsis.setText("No hay sinopsis disponible para esta película.");
            lblSinopsis.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");
        }
    }

    /**
     * Configura la imagen de la película con manejo de errores.
     */
    private void configurarImagen() {
        String urlImagen = pelicula.getImagenUrl();
        if (urlImagen != null && !urlImagen.trim().isEmpty()) {
            lblUrlImagen.setText(urlImagen);
            cargarImagen(urlImagen);
        } else {
            lblUrlImagen.setText("No hay imagen disponible");
            cargarImagenPorDefecto();
        }
    }

    /**
     * Carga una imagen específica de forma segura.
     * 
     * @param urlImagen URL de la imagen a cargar
     */
    private void cargarImagen(String urlImagen) {
        try {
            Image imagen = new Image(urlImagen, true); // true para carga asíncrona
            imgPelicula.setImage(imagen);
        } catch (Exception e) {
            cargarImagenPorDefecto();
        }
    }

    /**
     * Carga la imagen por defecto cuando no hay imagen disponible o hay error.
     */
    private void cargarImagenPorDefecto() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/no-image.png"));
            imgPelicula.setImage(defaultImage);
        } catch (Exception e) {
            // Si no hay imagen por defecto, dejar vacío
            imgPelicula.setImage(null);
        }
    }

    /**
     * Maneja el evento de volver a la gestión de películas.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onVolver(ActionEvent event) {
        volverAPantallaPrincipal();
    }

    /**
     * Maneja el evento de editar la película actual.
     * 
     * @param event Evento de acción del botón
     */
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

    /**
     * Maneja el evento de cerrar sesión.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onLogOut(ActionEvent event) {
        ManejadorMetodosComunes.mostrarVentanaAdvertencia("Sesión cerrada");
        javafx.application.Platform.exit();
    }

    /**
     * Navega de vuelta a la pantalla principal de gestión de películas.
     */
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

    /**
     * Muestra un mensaje de error al usuario.
     * 
     * @param titulo Título del mensaje
     * @param mensaje Contenido del mensaje de error
     */
    private void mostrarError(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaError(mensaje != null ? mensaje : "Error desconocido");
    }

    /**
     * Muestra un mensaje informativo al usuario.
     * 
     * @param titulo Título del mensaje
     * @param mensaje Contenido del mensaje informativo
     */
    private void mostrarInformacion(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaExito(mensaje != null ? mensaje : "Operación completada");
    }
}
