package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.peliculas.modelos.entidades.Funcion;

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

public class ControladorDetallesFuncion implements Initializable {

    @FXML private ImageView imgPelicula;
    @FXML private Label lblTituloPelicula;
    @FXML private Label lblIdFuncion;
    @FXML private Label lblSala;
    @FXML private Label lblFecha;
    @FXML private Label lblHoraInicio;
    @FXML private Label lblHoraFin;
    @FXML private Label lblFormato;
    @FXML private Label lblTipoEstreno;
    @FXML private Label lblEstadoFuncion;
    
    @FXML private Label lblIdPelicula;
    @FXML private Label lblAnio;
    @FXML private Label lblDuracion;
    @FXML private Label lblGeneros;
    @FXML private Label lblIdioma;
    
    @FXML private Label lblCapacidad;
    @FXML private Label lblEstadoSala;
    @FXML private Label lblBoletosVendidos;
    @FXML private Label lblInformacionExtra;
    @FXML private ProgressBar progressOcupacion;
    
    @FXML private Button btnVolver;
    @FXML private Button btnEditarFuncion;
    @FXML private Button btnGestionarBoletos;
    @FXML private Button btnActualizar;
    @FXML private Button btnLogOut;

    private Funcion funcionActual;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuración inicial si es necesaria
    }

    public void cargarFuncion(Funcion funcion) {
        this.funcionActual = funcion;
        mostrarDetallesFuncion();
        cargarInformacionAdicional();
    }

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

        // Estado de la función
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

        // Información de la película
        lblTituloPelicula.setText(funcionActual.getPelicula().getTitulo());
        lblIdPelicula.setText(String.valueOf(funcionActual.getPelicula().getId()));
        lblAnio.setText(String.valueOf(funcionActual.getPelicula().getAnio()));
        lblDuracion.setText(funcionActual.getPelicula().getDuracionMinutos() + " minutos");
        lblGeneros.setText(funcionActual.getPelicula().getGenerosComoString());
        lblIdioma.setText(funcionActual.getPelicula().getIdioma() != null ? 
                         funcionActual.getPelicula().getIdioma().getNombre() : "No especificado");

        // Configurar imagen de la película
        if (funcionActual.getPelicula().getImagenUrl() != null && 
            !funcionActual.getPelicula().getImagenUrl().trim().isEmpty()) {
            try {
                Image imagen = new Image(funcionActual.getPelicula().getImagenUrl(), true);
                imgPelicula.setImage(imagen);
            } catch (Exception e) {
                System.out.println("No se pudo cargar la imagen: " + e.getMessage());
                // Mantener imagen por defecto
            }
        }

        // Información de la sala
        lblCapacidad.setText(funcionActual.getSala().getCapacidad() + " asientos");
        lblEstadoSala.setText("Disponible"); // Por ahora estado fijo

        lblInformacionExtra.setText("Función ID: " + funcionActual.getId() + " - " + 
                                   funcionActual.getPelicula().getTitulo());
    }

    private void cargarInformacionAdicional() {
        if (funcionActual == null) return;

        try {
            // Simular carga de boletos vendidos (esto debería venir de un servicio real)
            // Por ahora mostramos información simulada
            int boletosVendidos = (int)(Math.random() * funcionActual.getSala().getCapacidad());
            lblBoletosVendidos.setText(boletosVendidos + " de " + funcionActual.getSala().getCapacidad());
            
            // Calcular progreso de ocupación
            double ocupacion = (double) boletosVendidos / funcionActual.getSala().getCapacidad();
            progressOcupacion.setProgress(ocupacion);
            
            // Cambiar color según ocupación
            if (ocupacion >= 0.8) {
                progressOcupacion.setStyle("-fx-accent: #dc3545;"); // Rojo - casi lleno
            } else if (ocupacion >= 0.5) {
                progressOcupacion.setStyle("-fx-accent: #ffc107;"); // Amarillo - medio lleno
            } else {
                progressOcupacion.setStyle("-fx-accent: #28a745;"); // Verde - disponible
            }
            
        } catch (Exception e) {
            lblBoletosVendidos.setText("Error al consultar");
            progressOcupacion.setProgress(0);
            mostrarError("Error", "No se pudo cargar información de boletos: " + e.getMessage());
        }
    }

    @FXML
    private void onVolver(ActionEvent event) {
        volverAGestionFunciones();
    }

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

    @FXML
    private void onGestionarBoletos(ActionEvent event) {
        // Por ahora solo mostramos un mensaje informativo
        mostrarInformacion("Gestión de Boletos", 
                          "Funcionalidad de gestión de boletos no implementada aún.\n" +
                          "Función: " + funcionActual.getPelicula().getTitulo() + "\n" +
                          "Sala: " + funcionActual.getSala().getNombre());
    }

    @FXML
    private void onActualizar(ActionEvent event) {
        cargarInformacionAdicional();
        mostrarInformacion("Actualizado", "Información de la función actualizada");
    }

    @FXML
    private void onLogOut(ActionEvent event) {
        ManejadorMetodosComunes.mostrarVentanaAdvertencia("Sesión cerrada");
        javafx.application.Platform.exit();
    }

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

    private void mostrarError(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaError(mensaje != null ? mensaje : "Error desconocido");
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaExito(mensaje != null ? mensaje : "Operación completada");
    }
}
