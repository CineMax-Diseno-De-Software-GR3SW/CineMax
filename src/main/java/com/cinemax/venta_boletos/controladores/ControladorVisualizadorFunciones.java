package com.cinemax.venta_boletos.controladores;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.venta_boletos.servicios.ServicioVisualizadorFunciones;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.application.Platform;

/**
 * Controlador para la pantalla de visualización de funciones de cine.
 * 
 * Responsabilidades:
 * - Muestra las funciones disponibles para una película seleccionada
 * - Permite filtrar funciones por fecha, formato y tipo de sala
 * - Gestiona la selección de funciones por parte del usuario
 * - Navega a la pantalla de selección de butacas al confirmar
 * 
 * Patrones utilizados:
 * - MVC (Model-View-Controller)
 * - Observer (para actualizaciones de UI)
 * 
 * @author CineMax Development Team
 * @version 1.3
 */
public class ControladorVisualizadorFunciones {

    // ========== COMPONENTES UI ==========

    @FXML
    private Label etiquetaTituloPelicula;
    @FXML
    private TableView<Funcion> tablaFunciones;
    @FXML
    private TableColumn<Funcion, String> columnaHora;
    @FXML
    private TableColumn<Funcion, String> columnaSala;
    @FXML
    private TableColumn<Funcion, String> columnaTipoSala;
    @FXML
    private TableColumn<Funcion, String> columnaFormato;
    @FXML
    private TableColumn<Funcion, String> columnaTipoEstreno;
    @FXML
    private TableColumn<Funcion, String> columnaPrecio;
    @FXML
    private TableColumn<Funcion, String> columnaFecha;
    @FXML
    private DatePicker selectorFecha;
    @FXML
    private ComboBox<String> cmbFiltroFormato;
    @FXML
    private ComboBox<String> cmbFiltroTipoSala;
    @FXML
    private ImageView imagenPelicula;
    @FXML
    private Label etiquetaGenero;
    @FXML
    private Label etiquetaDuracion;

    // ========== DEPENDENCIAS ==========
    private final ServicioVisualizadorFunciones servicioVisualizadorFunciones = new ServicioVisualizadorFunciones();
    private Pelicula peliculaSeleccionada;

    // ========== INICIALIZACIÓN ==========

    /**
     * Inicializa el controlador después de cargar el FXML
     */
    @FXML
    public void initialize() {
        configurarCamposTabla();
        configurarFiltrosTabla();
    }

    /**
     * Configura propiedades iniciales de la tabla
     */
    private void configurarCamposTabla() {
        tablaFunciones.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    /**
     * Configura los componentes de filtrado
     */
    private void configurarFiltrosTabla() {
        if (selectorFecha != null) {
            selectorFecha.setOnAction(event -> aplicarFiltros());
        }

        // Configurar opciones de formato
        cmbFiltroFormato.getItems().addAll("Todos", "2D", "3D", "4DX", "IMAX");
        cmbFiltroFormato.getSelectionModel().selectFirst();
        cmbFiltroFormato.setOnAction(event -> aplicarFiltros());

        // Configurar opciones de tipo de sala
        cmbFiltroTipoSala.getItems().addAll("Todos", "NORMAL", "VIP", "PREMIUM", "D-Box");
        cmbFiltroTipoSala.getSelectionModel().selectFirst();
        cmbFiltroTipoSala.setOnAction(event -> aplicarFiltros());
    }

    // ========== MÉTODOS PRINCIPALES ==========

    /**
     * Establece la película actual y actualiza la UI
     * 
     * @param pelicula Película seleccionada
     */
    public void asignarPeliculaSeleccionada(Pelicula pelicula) {
        this.peliculaSeleccionada = pelicula;
        Platform.runLater(() -> {
            cargarInformacionPelicula(pelicula);
            cargarFunciones();
        });
    }

    /**
     * Actualiza los componentes UI con información de la película
     * 
     * @param pelicula Película a mostrar
     */
    private void cargarInformacionPelicula(Pelicula pelicula) {
        etiquetaTituloPelicula.setText("Funciones de: " + pelicula.getTitulo());
        etiquetaGenero.setText(pelicula.getGenerosComoString());
        etiquetaDuracion.setText(pelicula.getDuracionMinutos() + " min");
        cargarImagenPelicula(pelicula.getImagenUrl());
    }

    /**
     * Carga la imagen de la película con manejo de errores
     * 
     * @param urlImagen URL de la imagen a cargar
     */
    private void cargarImagenPelicula(String urlImagen) {
        try {
            Image imagen = new Image(urlImagen, true);
            imagen.errorProperty().addListener((observable, valorAnterior, esError) -> {
                if (esError) {
                    imagenPelicula.setImage(new Image("https://i.imgur.com/6LWDqET.png"));
                }
            });
            imagenPelicula.setImage(imagen);
        } catch (Exception excepcion) {
            imagenPelicula.setImage(new Image("https://i.imgur.com/6LWDqET.png"));
        }
    }

    /**
     * Carga las funciones aplicando los filtros actuales
     */
    private void cargarFunciones() {
        LocalDate fecha = selectorFecha.getValue();
        String formato = cmbFiltroFormato.getValue();
        String tipoSala = cmbFiltroTipoSala.getValue();

        if (peliculaSeleccionada != null) {
            servicioVisualizadorFunciones.cargarFunciones(
                    tablaFunciones,
                    columnaHora,
                    columnaSala,
                    columnaFormato,
                    columnaTipoEstreno,
                    columnaPrecio,
                    columnaFecha,
                    columnaTipoSala,
                    peliculaSeleccionada.getTitulo(),
                    fecha,
                    formato,
                    tipoSala);
        }
    }

    // ========== MANEJADORES DE EVENTOS ==========

    /**
     * Maneja el evento de regresar a la cartelera
     * 
     * @param evento Evento de acción
     */
    @FXML
    public void onRegresar(ActionEvent evento) {
        servicioVisualizadorFunciones.regresarPantallaCartelera(evento);
    }

    /**
     * Maneja la confirmación de función seleccionada
     */
    @FXML
    private void onConfirmacion() {
        servicioVisualizadorFunciones.seleccionarFuncion(tablaFunciones);
    }

    /**
     * Aplica los filtros actuales y recarga las funciones
     */
    private void aplicarFiltros() {
        cargarFunciones();
    }

}
