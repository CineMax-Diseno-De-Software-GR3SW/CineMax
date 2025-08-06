package com.cinemax.venta_boletos.controladores;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.venta_boletos.servicios.ServicioMostrarFunciones;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;
import javafx.application.Platform;

/**
 * Controlador para la pantalla de visualización de funciones de cine.
 * 
 * Responsabilidades:
 * - Muestra las funciones disponibles para una película seleccionada
 * - Gestiona la selección de funciones por parte del usuario
 * - Proporciona navegación hacia:
 * - Pantalla de cartelera (regreso)
 * - Pantalla de selección de butacas (confirmación)
 * 
 * Flujo principal:
 * 1. Recibe el nombre de la película seleccionada
 * 2. Carga y muestra las funciones disponibles
 * 3. Permite seleccionar una función
 * 4. Navega a la siguiente pantalla con la función seleccionada
 * 
 * @author [Tu nombre o equipo]
 * @version 1.0
 */
public class ControladorMostrarFunciones {

    // ===== ELEMENTOS DE INTERFAZ (FXML) =====

    /** Etiqueta que muestra el título de la película seleccionada */
    @FXML
    private Label peliculaTituloLabel;

    /** Tabla que muestra la lista de funciones disponibles */
    @FXML
    private TableView<Funcion> tableViewFunciones;

    /** Columnas de la tabla de funciones */
    @FXML
    private TableColumn<Funcion, String> colHora;
    @FXML
    private TableColumn<Funcion, String> colSala;
    @FXML
    private TableColumn<Funcion, String> colTipoSala;
    @FXML
    private TableColumn<Funcion, String> colFormato;
    @FXML
    private TableColumn<Funcion, String> colTipoEstreno;
    @FXML
    private TableColumn<Funcion, String> colPrecio;
    @FXML
    private TableColumn<Funcion, String> colFecha;

    // ===== ATRIBUTOS DE LÓGICA =====

    /** Servicio para gestión de datos de funciones */
    private final ServicioMostrarFunciones servicio = new ServicioMostrarFunciones();

    /** Nombre de la película actualmente seleccionada */
    private String nombrePeliculaActual;

    // ===== MÉTODOS DE INICIALIZACIÓN =====

    /**
     * Inicializa el controlador después de cargar el FXML.
     * 
     * Realiza:
     * 1. Verificación de inyección de dependencias FXML
     * 2. Configuración inicial de la tabla
     */
    @FXML
    public void initialize() {
        System.out.println("Iniciando ControladorMostrarFunciones...");
        verificarInyecciones();
        configurarTabla();
    }

    /**
     * Verifica que todos los elementos FXML se hayan inyectado correctamente.
     * 
     * Imprime mensajes de error para cualquier elemento no inyectado.
     */
    private void verificarInyecciones() {
        if (tableViewFunciones == null)
            System.err.println("ERROR: tableViewFunciones no inyectado");
        if (colHora == null)
            System.err.println("ERROR: colHora no inyectado");
        if (colSala == null)
            System.err.println("ERROR: colSala no inyectado");
        if (colTipoSala == null)
            System.err.println("ERROR: colTipoSala no inyectado");
        if (colFormato == null)
            System.err.println("ERROR: colFormato no inyectado");
        if (colTipoEstreno == null)
            System.err.println("ERROR: colTipoEstreno no inyectado");
        if (colPrecio == null)
            System.err.println("ERROR: colPrecio no inyectado");
        if (colFecha == null)
            System.err.println("ERROR: colFecha no inyectado");
    }

    /**
     * Configura propiedades iniciales de la tabla.
     */
    private void configurarTabla() {
        tableViewFunciones.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    // ===== MÉTODOS PRINCIPALES =====

    /**
     * Establece la película para la cual se mostrarán las funciones.
     * 
     * @param pelicula Nombre de la película seleccionada
     */
    public void setPelicula(String pelicula) {
        this.nombrePeliculaActual = pelicula;

        Platform.runLater(() -> {
            actualizarTitulo(pelicula);
            cargarFunciones();
        });
    }

    /**
     * Actualiza el título de la película en la interfaz.
     * 
     * @param pelicula Nombre de la película a mostrar
     */
    private void actualizarTitulo(String pelicula) {
        if (peliculaTituloLabel != null) {
            peliculaTituloLabel.setText("Funciones de: " + pelicula);
        }
    }

    /**
     * Carga las funciones disponibles para la película actual.
     */
    private void cargarFunciones() {
        servicio.cargarFunciones(
                tableViewFunciones,
                colHora,
                colSala,
                colFormato,
                colTipoEstreno,
                colPrecio,
                colFecha,
                colTipoSala,
                nombrePeliculaActual);
    }

    // ===== MANEJADORES DE EVENTOS =====

    /**
     * Maneja el evento de regresar a la pantalla de cartelera.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    public void handleRegresar(ActionEvent event) {
        servicio.regresarPantallaCartelera(event);
    }

    /**
     * Maneja la confirmación de la función seleccionada.
     * 
     * Navega a la pantalla de selección de butacas con la función elegida.
     */
    @FXML
    private void handleConfirmar() {
        servicio.confirmarFuncion(tableViewFunciones, nombrePeliculaActual);
    }
}