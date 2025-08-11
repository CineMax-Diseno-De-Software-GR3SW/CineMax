package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.utilidades.ManejadorMetodosComunes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Controlador para la visualización de detalles completos de una función cinematográfica.
 *
 * <p>Esta clase maneja la interfaz gráfica que muestra información detallada
 * de una función específica, incluyendo datos de la película, horarios,
 * información de la sala y estadísticas de ocupación.
 *
 * <p>Funcionalidades principales:
 * <ul>
 *   <li>Visualización completa de información de función y película asociada</li>
 *   <li>Análisis de estado de la función (programada, en curso, finalizada)</li>
 *   <li>Simulación de estadísticas de ocupación de sala</li>
 *   <li>Navegación a edición de función y gestión de boletos</li>
 *   <li>Actualización manual de información</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public class ControladorDetallesFuncion implements Initializable {

    /** Imagen de la película */
    @FXML private ImageView imgPelicula;
    
    /** Título de la película */
    @FXML private Label lblTituloPelicula;
    
    /** ID de la función */
    @FXML private Label lblIdFuncion;
    
    /** Nombre de la sala */
    @FXML private Label lblSala;
    
    /** Fecha de la función */
    @FXML private Label lblFecha;
    
    /** Hora de inicio */
    @FXML private Label lblHoraInicio;
    
    /** Hora de finalización */
    @FXML private Label lblHoraFin;
    
    /** Formato de proyección */
    @FXML private Label lblFormato;
    
    /** Tipo de estreno */
    @FXML private Label lblTipoEstreno;
    
    /** Estado actual de la función */
    @FXML private Label lblEstadoFuncion;
    
    /** ID de la película */
    @FXML private Label lblIdPelicula;
    
    /** Año de la película */
    @FXML private Label lblAnio;
    
    /** Duración de la película */
    @FXML private Label lblDuracion;
    
    /** Géneros de la película */
    @FXML private Label lblGeneros;
    
    /** Idioma de la película */
    @FXML private Label lblIdioma;
    
    /** Capacidad total de la sala */
    @FXML private Label lblCapacidad;
    
    /** Estado de la sala */
    @FXML private Label lblEstadoSala;
    
    /** Número de boletos vendidos */
    @FXML private Label lblBoletosVendidos;
    
    /** Información adicional */
    @FXML private Label lblInformacionExtra;
    
    /** Barra de progreso de ocupación */
    @FXML private ProgressBar progressOcupacion;
    
    /** Botón para volver */
    @FXML private Button btnVolver;
    
    /** Botón para editar función */
    @FXML private Button btnEditarFuncion;
    
    /** Botón para gestionar boletos */
    @FXML private Button btnGestionarBoletos;
    
    /** Botón para actualizar información */
    @FXML private Button btnActualizar;
    
    /** Botón para cerrar sesión */
    @FXML private Button btnLogOut;

    /** Función actualmente cargada en la vista */
    private Funcion funcionActual;

    /**
     * Inicializa el controlador después de que se ha cargado el FXML.
     * 
     * @param location La ubicación utilizada para resolver rutas relativas para el objeto raíz
     * @param resources Los recursos utilizados para localizar el objeto raíz
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuración inicial si es necesaria
    }

    /**
     * Carga y muestra los datos de una función específica.
     * 
     * <p>Este método configura toda la información visible en la interfaz
     * incluyendo datos de la función, película asociada y estadísticas de sala.
     * 
     * @param funcion La función a mostrar en detalle
     */
    public void cargarFuncion(Funcion funcion) {
        this.funcionActual = funcion;
        mostrarDetallesFuncion();
        cargarInformacionAdicional();
    }

    /**
     * Muestra los detalles completos de la función en la interfaz.
     * 
     * <p>Incluye información de la función, película asociada, horarios
     * y configuración del estado visual según el tiempo actual.
     */
    private void mostrarDetallesFuncion() {
        if (funcionActual == null) return;

        // Información de la función
        lblIdFuncion.setText(String.valueOf(funcionActual.getId()));
        lblSala.setText(funcionActual.getSala().getNombre());
        lblFecha.setText(funcionActual.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblHoraInicio.setText(funcionActual.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")));
        lblHoraFin.setText(funcionActual.getFechaHoraFin().format(DateTimeFormatter.ofPattern("HH:mm")));
        lblFormato.setText(funcionActual.getFormato().name());
        lblTipoEstreno.setText(funcionActual.getTipoEstreno().name());

        // Configurar estado de la función basado en el tiempo actual
        configurarEstadoFuncion();

        // Información de la película
        mostrarInformacionPelicula();

        // Configurar imagen de la película
        cargarImagenPelicula();

        // Información de la sala
        configurarInformacionSala();

        lblInformacionExtra.setText("Función ID: " + funcionActual.getId() + " - " + 
                                   funcionActual.getPelicula().getTitulo());
    }

    /**
     * Configura el estado visual de la función basándose en el tiempo actual.
     */
    private void configurarEstadoFuncion() {
        if (funcionActual.getFechaHoraInicio().isAfter(java.time.LocalDateTime.now())) {
            lblEstadoFuncion.setText("Programada");
            lblEstadoFuncion.setStyle("-fx-text-fill: #28a745;"); // Verde
        } else if (funcionActual.getFechaHoraFin().isAfter(java.time.LocalDateTime.now())) {
            lblEstadoFuncion.setText("En Curso");
            lblEstadoFuncion.setStyle("-fx-text-fill: #ffc107;"); // Amarillo
        } else {
            lblEstadoFuncion.setText("Finalizada");
            lblEstadoFuncion.setStyle("-fx-text-fill: #6c757d;"); // Gris
        }
    }

    /**
     * Muestra la información de la película asociada a la función.
     */
    private void mostrarInformacionPelicula() {
        lblTituloPelicula.setText(funcionActual.getPelicula().getTitulo());
        lblIdPelicula.setText(String.valueOf(funcionActual.getPelicula().getId()));
        lblAnio.setText(String.valueOf(funcionActual.getPelicula().getAnio()));
        lblDuracion.setText(funcionActual.getPelicula().getDuracionMinutos() + " minutos");
        lblGeneros.setText(funcionActual.getPelicula().getGenerosComoString());
        lblIdioma.setText(funcionActual.getPelicula().getIdioma() != null ? 
                         funcionActual.getPelicula().getIdioma().getNombre() : "No especificado");
    }

    /**
     * Carga la imagen de la película de forma segura.
     */
    private void cargarImagenPelicula() {
        if (funcionActual.getPelicula().getImagenUrl() != null && 
            !funcionActual.getPelicula().getImagenUrl().trim().isEmpty()) {
            try {
                Image imagen = new Image(funcionActual.getPelicula().getImagenUrl(), true);
                imgPelicula.setImage(imagen);
            } catch (Exception e) {
                // Mantener imagen por defecto en caso de error
            }
        }
    }

    /**
     * Configura la información básica de la sala.
     */
    private void configurarInformacionSala() {
        lblCapacidad.setText(funcionActual.getSala().getCapacidad() + " asientos");
        lblEstadoSala.setText("Disponible");
    }

    /**
     * Carga información adicional como estadísticas de boletos y ocupación.
     * 
     * <p>Actualmente simula datos de ocupación. En una implementación real,
     * esta información vendría de un servicio de boletos.
     */
    private void cargarInformacionAdicional() {
        if (funcionActual == null) return;

        try {
            // Simular carga de boletos vendidos (esto debería venir de un servicio real)
            int boletosVendidos = (int)(Math.random() * funcionActual.getSala().getCapacidad());
            lblBoletosVendidos.setText(boletosVendidos + " de " + funcionActual.getSala().getCapacidad());
            
            // Calcular y mostrar progreso de ocupación
            configurarBarraOcupacion(boletosVendidos);
            
        } catch (Exception e) {
            configurarErrorEstadisticas();
            mostrarError("Error", "No se pudo cargar información de boletos: " + e.getMessage());
        }
    }

    /**
     * Configura la barra de progreso de ocupación con colores apropiados.
     * 
     * @param boletosVendidos Número de boletos vendidos
     */
    private void configurarBarraOcupacion(int boletosVendidos) {
        double ocupacion = (double) boletosVendidos / funcionActual.getSala().getCapacidad();
        progressOcupacion.setProgress(ocupacion);
        
        // Cambiar color según nivel de ocupación
        if (ocupacion >= 0.8) {
            progressOcupacion.setStyle("-fx-accent: #dc3545;"); // Rojo - casi lleno
        } else if (ocupacion >= 0.5) {
            progressOcupacion.setStyle("-fx-accent: #ffc107;"); // Amarillo - medio lleno
        } else {
            progressOcupacion.setStyle("-fx-accent: #28a745;"); // Verde - disponible
        }
    }

    /**
     * Configura el estado visual para errores en estadísticas.
     */
    private void configurarErrorEstadisticas() {
        lblBoletosVendidos.setText("Error al consultar");
        progressOcupacion.setProgress(0);
    }

    /**
     * Maneja el evento de volver a la gestión de funciones.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onVolver(ActionEvent event) {
        volverAGestionFunciones();
    }

    /**
     * Maneja el evento de editar la función actual.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onEditarFuncion(ActionEvent event) {
        if (funcionActual != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/peliculas/PantallaFormularioFuncion.fxml"));
                Parent root = loader.load();
                
                // Configurar para edición
                ControladorFormularioFuncion controlador = loader.getController();
                controlador.configurarParaEdicion(funcionActual);
                
                Stage stage = (Stage) btnVolver.getScene().getWindow();
                stage.getScene().setRoot(root);
            } catch (Exception e) {
                mostrarError("Error de navegación", "No se pudo abrir el formulario de edición: " + e.getMessage());
            }
        }
    }

    /**
     * Maneja el evento de gestionar boletos de la función.
     * 
     * <p>Actualmente muestra un mensaje informativo ya que la funcionalidad
     * de gestión de boletos no está implementada.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onGestionarBoletos(ActionEvent event) {
        mostrarInformacion("Gestión de Boletos", 
                          "Funcionalidad de gestión de boletos no implementada aún.\n" +
                          "Función: " + funcionActual.getPelicula().getTitulo() + "\n" +
                          "Sala: " + funcionActual.getSala().getNombre());
    }

    /**
     * Maneja el evento de actualizar información de la función.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onActualizar(ActionEvent event) {
        cargarInformacionAdicional();
        mostrarInformacion("Actualizado", "Información de la función actualizada");
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
     * Navega de vuelta a la pantalla de gestión de funciones.
     */
    private void volverAGestionFunciones() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/peliculas/PantallaGestionFunciones.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("Error de navegación", "No se pudo volver a la gestión de funciones: " + e.getMessage());
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
