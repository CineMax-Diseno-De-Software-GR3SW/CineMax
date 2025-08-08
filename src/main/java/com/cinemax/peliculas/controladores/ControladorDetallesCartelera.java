package com.cinemax.peliculas.controladores;

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

    private Pelicula peliculaActual;
    private ServicioFuncion servicioFuncion;

    public ControladorDetallesCartelera() {
        this.servicioFuncion = new ServicioFuncion();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuración inicial si es necesaria
    }

    public void cargarPelicula(Pelicula pelicula) {
        this.peliculaActual = pelicula;
        mostrarDetallesPelicula();
        cargarInformacionCartelera();
    }

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
                System.out.println("No se pudo cargar la imagen: " + e.getMessage());
                // Mantener imagen por defecto
            }
        } else {
            txtImagenUrl.setText("Sin URL de imagen");
        }

        lblInformacionExtra.setText("Película ID: " + peliculaActual.getId() + " - " + peliculaActual.getTitulo());
    }

    private void cargarInformacionCartelera() {
        if (peliculaActual == null) return;

        try {
            // Obtener todas las funciones y filtrar por esta película
            List<Funcion> todasLasFunciones = servicioFuncion.listarTodasLasFunciones();
            List<Funcion> funciones = new java.util.ArrayList<>();
            
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
                Funcion proximaFuncion = null;
                for (Funcion funcion : funciones) {
                    if (funcion.getFechaHoraInicio().isAfter(java.time.LocalDateTime.now())) {
                        if (proximaFuncion == null || funcion.getFechaHoraInicio().isBefore(proximaFuncion.getFechaHoraInicio())) {
                            proximaFuncion = funcion;
                        }
                    }
                }
                
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

    @FXML
    private void onVolver(ActionEvent event) {
        volverACartelera();
    }

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

    @FXML
    private void onActualizarInfo(ActionEvent event) {
        cargarInformacionCartelera();
        mostrarInformacion("Actualizado", "Información de cartelera actualizada");
    }

    @FXML
    private void onLogOut(ActionEvent event) {
        ManejadorMetodosComunes.mostrarVentanaAdvertencia("Sesión cerrada");
        javafx.application.Platform.exit();
    }

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

    private void mostrarError(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaError(mensaje != null ? mensaje : "Error desconocido");
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaExito(mensaje != null ? mensaje : "Operación completada");
    }
}
