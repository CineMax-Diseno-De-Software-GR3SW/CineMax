package com.cinemax.peliculas.controladores;

/**
 * Controlador para la visualización de detalles de películas en cartelera.
 *
 * <p>Esta clase maneja la interfaz de usuario para mostrar información detallada
 * de una película específica, incluyendo sus datos básicos, estado en cartelera
 * y funciones programadas. Proporciona navegación hacia otras funcionalidades
 * relacionadas con la gestión de películas y funciones.
 *
 * <p>Funcionalidades principales:
 * <ul>
 *   <li>Visualización completa de información de películas</li>
 *   <li>Carga y muestra de imagen de la película</li>
 *   <li>Análisis del estado de la película en cartelera</li>
 *   <li>Información sobre funciones programadas y próximas</li>
 *   <li>Navegación hacia edición de películas y gestión de funciones</li>
 *   <li>Actualización en tiempo real de información de cartelera</li>
 * </ul>
 *
 * <p>La clase utiliza servicios de funciones para obtener información actualizada
 * sobre la programación de la película y determinar su estado actual en cartelera.
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.servicios.ServicioFuncion;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ControladorDetallesCartelera implements Initializable {

    // Componentes de la interfaz FXML
    @FXML private ImageView imgPelicula;
    @FXML private Label lblTitulo;
    @FXML private Label lblId;
    @FXML private Label lblAnio;
    @FXML private Label lblDuracion;
    @FXML private Label lblIdioma;
    @FXML private Label lblGeneros;
    @FXML private Label lblSinopsis;
    @FXML private Label lblEstadoCartelera;
    @FXML private Label lblFuncionesDisponibles;
    @FXML private Label lblProximaFuncion;
    @FXML private Label lblInformacionExtra;
    @FXML private TextField txtImagenUrl;
    
    @FXML private Button btnVolver;
    @FXML private Button btnGestionarFunciones;
    @FXML private Button btnEditarPelicula;
    @FXML private Button btnActualizarInfo;
    @FXML private Button btnLogOut;

    /** Película actualmente cargada en los detalles */
    private Pelicula peliculaActual;
    
    /** Servicio para operaciones con funciones cinematográficas */
    private ServicioFuncion servicioFuncion;

    /**
     * Constructor que inicializa los servicios necesarios.
     */
    public ControladorDetallesCartelera() {
        this.servicioFuncion = new ServicioFuncion();
    }

    /**
     * Inicializa el controlador después de cargar el archivo FXML.
     *
     * @param location Ubicación del archivo FXML
     * @param resources Recursos de localización
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuración inicial si es necesaria
    }

    /**
     * Carga los datos de una película en la interfaz de detalles.
     *
     * <p>Este método establece la película actual y actualiza todos los componentes
     * de la interfaz con la información correspondiente, incluyendo la carga de
     * información de cartelera y funciones.
     *
     * @param pelicula La película cuyos detalles se van a mostrar
     */
    public void cargarPelicula(Pelicula pelicula) {
        this.peliculaActual = pelicula;
        mostrarDetallesPelicula();
        cargarInformacionCartelera();
    }

    /**
     * Muestra los detalles básicos de la película en la interfaz.
     *
     * <p>Actualiza todos los campos de información de la película incluyendo
     * título, año, duración, idioma, géneros, sinopsis y carga la imagen.
     * Maneja casos donde algunos datos pueden no estar disponibles.
     */
    private void mostrarDetallesPelicula() {
        if (peliculaActual == null) return;

        lblTitulo.setText(peliculaActual.getTitulo());
        lblId.setText(String.valueOf(peliculaActual.getId()));
        lblAnio.setText(String.valueOf(peliculaActual.getAnio()));
        lblDuracion.setText(peliculaActual.getDuracionMinutos() + " minutos");
        lblIdioma.setText(peliculaActual.getIdioma() != null ? peliculaActual.getIdioma().getNombre() : "No especificado");
        lblGeneros.setText(peliculaActual.getGenerosComoString());
        lblSinopsis.setText(peliculaActual.getSinopsis() != null ? peliculaActual.getSinopsis() : "Sin sinopsis disponible");
        
        // Configurar imagen
        if (peliculaActual.getImagenUrl() != null && !peliculaActual.getImagenUrl().trim().isEmpty()) {
            txtImagenUrl.setText(peliculaActual.getImagenUrl());
            try {
                Image imagen = new Image(peliculaActual.getImagenUrl(), true);
                imgPelicula.setImage(imagen);
            } catch (Exception e) {
                // Mantener imagen por defecto en caso de error
            }
        } else {
            txtImagenUrl.setText("Sin URL de imagen");
        }

        lblInformacionExtra.setText("Película ID: " + peliculaActual.getId() + " - " + peliculaActual.getTitulo());
    }

    /**
     * Carga y analiza la información de cartelera para la película actual.
     *
     * <p>Obtiene todas las funciones programadas para la película, determina su estado
     * en cartelera (sin funciones, en cartelera, funciones pasadas) y encuentra la
     * próxima función disponible. Actualiza los indicadores visuales según el estado.
     */
    private void cargarInformacionCartelera() {
        if (peliculaActual == null) return;

        try {
            List<Funcion> todasLasFunciones = servicioFuncion.listarTodasLasFunciones();
            List<Funcion> funciones = new java.util.ArrayList<>();
            
            // Filtrar funciones de esta película
            for (Funcion funcion : todasLasFunciones) {
                if (funcion.getPelicula().getId() == peliculaActual.getId()) {
                    funciones.add(funcion);
                }
            }
            
            if (funciones.isEmpty()) {
                lblFuncionesDisponibles.setText("Sin funciones programadas");
                lblProximaFuncion.setText("No hay funciones disponibles");
                lblEstadoCartelera.setText("Sin Funciones");
                lblEstadoCartelera.setStyle("-fx-text-fill: #dc3545;"); // Rojo
            } else {
                lblFuncionesDisponibles.setText(funciones.size() + " función(es) programada(s)");
                
                // Buscar la próxima función
                Funcion proximaFuncion = encontrarProximaFuncion(funciones);
                
                if (proximaFuncion != null) {
                    String fechaHora = proximaFuncion.getFechaHoraInicio().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    lblProximaFuncion.setText(fechaHora + " - Sala " + proximaFuncion.getSala().getNombre());
                    lblEstadoCartelera.setText("En Cartelera");
                    lblEstadoCartelera.setStyle("-fx-text-fill: #28a745;"); // Verde
                } else {
                    lblProximaFuncion.setText("Sin funciones futuras");
                    lblEstadoCartelera.setText("Funciones Pasadas");
                    lblEstadoCartelera.setStyle("-fx-text-fill: #ffc107;"); // Amarillo
                }
            }
        } catch (Exception e) {
            lblFuncionesDisponibles.setText("Error al consultar");
            lblProximaFuncion.setText("Error al consultar funciones");
            mostrarError("Error", "No se pudo cargar información de cartelera: " + e.getMessage());
        }
    }

    /**
     * Encuentra la próxima función disponible de una lista de funciones.
     *
     * @param funciones Lista de funciones a analizar
     * @return La función más próxima en el tiempo, o null si no hay funciones futuras
     */
    private Funcion encontrarProximaFuncion(List<Funcion> funciones) {
        Funcion proximaFuncion = null;
        for (Funcion funcion : funciones) {
            if (funcion.getFechaHoraInicio().isAfter(java.time.LocalDateTime.now())) {
                if (proximaFuncion == null || funcion.getFechaHoraInicio().isBefore(proximaFuncion.getFechaHoraInicio())) {
                    proximaFuncion = funcion;
                }
            }
        }
        return proximaFuncion;
    }

    /**
     * Maneja el evento de volver a la pantalla anterior.
     *
     * @param event El evento de acción
     */
    @FXML
    private void onVolver(ActionEvent event) {
        volverACartelera();
    }

    /**
     * Maneja el evento de navegar a gestión de funciones.
     *
     * @param event El evento de acción
     */
    @FXML
    private void onGestionarFunciones(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/peliculas/PantallaGestionFunciones.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("Error de navegación", "No se pudo abrir gestión de funciones: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de editar la película actual.
     *
     * @param event El evento de acción
     */
    @FXML
    private void onEditarPelicula(ActionEvent event) {
        if (peliculaActual != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/peliculas/PantallaFormularioPelicula.fxml"));
                Parent root = loader.load();
                
                // Configurar para edición
                ControladorFormularioPelicula controlador = loader.getController();
                controlador.configurarParaEdicion(peliculaActual);
                
                Stage stage = (Stage) btnVolver.getScene().getWindow();
                stage.getScene().setRoot(root);
            } catch (Exception e) {
                mostrarError("Error de navegación", "No se pudo abrir el formulario de edición: " + e.getMessage());
            }
        }
    }

    /**
     * Maneja el evento de actualizar información de cartelera.
     *
     * @param event El evento de acción
     */
    @FXML
    private void onActualizarInfo(ActionEvent event) {
        cargarInformacionCartelera();
        mostrarInformacion("Actualizado", "Información de cartelera actualizada");
    }

    /**
     * Maneja el evento de cerrar sesión.
     *
     * @param event El evento de acción
     */
    @FXML
    private void onLogOut(ActionEvent event) {
        ManejadorMetodosComunes.mostrarVentanaAdvertencia("Sesión cerrada");
        javafx.application.Platform.exit();
    }

    /**
     * Navega de vuelta a la pantalla de gestión de cartelera.
     */
    private void volverACartelera() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/peliculas/PantallaGestionCartelera.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("Error de navegación", "No se pudo volver a la cartelera: " + e.getMessage());
        }
    }

    /**
     * Muestra un mensaje de error utilizando el manejador común.
     *
     * @param titulo El título del mensaje de error (no utilizado)
     * @param mensaje El mensaje de error a mostrar
     */
    private void mostrarError(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaError(mensaje != null ? mensaje : "Error desconocido");
    }

    /**
     * Muestra un mensaje informativo utilizando el manejador común.
     *
     * @param titulo El título del mensaje informativo (no utilizado)
     * @param mensaje El mensaje informativo a mostrar
     */
    private void mostrarInformacion(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaExito(mensaje != null ? mensaje : "Operación completada");
    }
}
