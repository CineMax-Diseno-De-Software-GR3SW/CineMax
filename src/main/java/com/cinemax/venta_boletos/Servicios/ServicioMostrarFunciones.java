package com.cinemax.venta_boletos.servicios;

import com.cinemax.comun.ControladorCargaConDatos;
import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.peliculas.controladores.ControladorFunciones;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.venta_boletos.controladores.ControladorCargaAsignacionButacas;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

/**
 * Servicio para la gestión y visualización de funciones cinematográficas.
 * 
 * Responsabilidades principales:
 * 1. Carga y muestra las funciones disponibles para una película específica
 * 2. Configura la presentación de datos en una tabla JavaFX
 * 3. Maneja la navegación entre pantallas de cartelera y selección de butacas
 * 4. Valida y procesa la selección de funciones por parte del usuario
 * 
 * Flujo principal de operación:
 * 1. Recibe una película como parámetro de entrada
 * 2. Consulta las funciones disponibles para esa película
 * 3. Configura y popula una tabla con la información de las funciones
 * 4. Proporciona mecanismos para:
 * - Regresar a la pantalla de cartelera
 * - Seleccionar una función para continuar con la compra
 * 
 * Integración con otros componentes:
 * - Utiliza ControladorFunciones para obtener datos de funciones
 * - Interactúa con ControladorCargaAsignacionButacas para transición a
 * selección de butacas
 * - Emplea ManejadorMetodosComunes para manejo estándar de errores y navegación
 * 
 * @author [Nombre del autor o equipo]
 * @version 1.0
 * @since [Fecha de creación o versión]
 */
public class ServicioMostrarFunciones {

    // ==================== CONSTANTES ====================
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ==================== ATRIBUTOS ====================
    private final ControladorFunciones controladorFunciones = new ControladorFunciones();

    // ==================== MÉTODOS PÚBLICOS ====================

    /**
     * Carga las funciones disponibles para una película específica en una tabla
     * JavaFX.
     * 
     * @param tabla          Tabla donde se mostrarán las funciones
     * @param colHora        Columna para mostrar la hora de la función
     * @param colSala        Columna para mostrar la sala
     * @param colFormato     Columna para mostrar el formato (2D, 3D, etc.)
     * @param colTipoEstreno Columna para mostrar el tipo de estreno
     * @param colPrecio      Columna para mostrar el precio
     * @param colFecha       Columna para mostrar la fecha
     * @param colTipoSala    Columna para mostrar el tipo de sala
     * @param nombrePelicula Nombre de la película para filtrar funciones
     * 
     *                       Proceso:
     *                       1. Configura las columnas de la tabla
     *                       2. Obtiene las funciones desde el controlador
     *                       3. Actualiza la tabla en el hilo de JavaFX
     *                       4. Maneja casos de error o datos vacíos
     */
    public void cargarFunciones(TableView<Funcion> tabla,
            TableColumn<Funcion, String> colHora,
            TableColumn<Funcion, String> colSala,
            TableColumn<Funcion, String> colFormato,
            TableColumn<Funcion, String> colTipoEstreno,
            TableColumn<Funcion, String> colPrecio,
            TableColumn<Funcion, String> colFecha,
            TableColumn<Funcion, String> colTipoSala,
            String nombrePelicula) {

        configurarColumnas(colHora, colSala, colFormato, colTipoEstreno, colPrecio, colFecha, colTipoSala);

        try {
            List<Funcion> funcionesObtenidas = controladorFunciones.obtenerFuncionesPorNombrePelicula(nombrePelicula);
            ObservableList<Funcion> listaFunciones = FXCollections.observableArrayList(funcionesObtenidas);

            Platform.runLater(() -> {
                tabla.setItems(listaFunciones);
                if (listaFunciones.isEmpty()) {
                    tabla.setPlaceholder(new Label("No hay funciones disponibles para " + nombrePelicula));
                }
            });

        } catch (Exception e) {
            Platform.runLater(() -> {
                tabla.setPlaceholder(new Label("Error al cargar funciones"));
                ManejadorMetodosComunes.mostrarVentanaError("Error al cargar funciones: " + e.getMessage());
            });
            e.printStackTrace();
        }
    }

    /**
     * Navega de regreso a la pantalla de cartelera principal.
     * 
     * @param event Evento de acción que disparó la navegación
     * 
     *              Proceso:
     *              1. Carga la vista de cartelera desde archivo FXML
     *              2. Configura la nueva escena
     *              3. Maneja posibles errores de carga
     */
    public void regresarPantallaCartelera(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/venta_boletos/VistaMostrarCartelera.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.centerOnScreen();
        } catch (IOException e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al regresar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Confirma la función seleccionada para avanzar al proceso de selección de
     * butacas.
     * 
     * @param tabla    Tabla que contiene las funciones disponibles
     * @param pelicula Nombre de la película seleccionada (para contexto)
     * 
     *                 Proceso:
     *                 1. Valida que se haya seleccionado una función
     *                 2. Prepara los datos para la siguiente pantalla
     *                 3. Inicia la transición con pantalla de carga
     *                 4. Maneja posibles errores en el proceso
     */
    public void confirmarFuncion(TableView<Funcion> tabla, String pelicula) {
        Funcion funcionSeleccionada = tabla.getSelectionModel().getSelectedItem();

        if (funcionSeleccionada == null) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Seleccione una función primero");
            return;
        }

        try {
            Stage currentStage = (Stage) tabla.getScene().getWindow();
            ControladorCargaConDatos controladorCargaConDatos = new ControladorCargaAsignacionButacas(
                    "/vistas/venta_boletos/VistaSeleccionButacas.fxml",
                    currentStage,
                    new ArrayList<>(List.of(funcionSeleccionada)));

            ManejadorMetodosComunes.mostrarVistaDeCargaPasandoDatos(currentStage, controladorCargaConDatos, 8, 150);

        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al confirmar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== MÉTODOS PRIVADOS ====================

    /**
     * Configura los renderizadores de valor para cada columna de la tabla.
     * 
     * @param colHora        Columna de hora
     * @param colSala        Columna de sala
     * @param colFormato     Columna de formato
     * @param colTipoEstreno Columna de tipo de estreno
     * @param colPrecio      Columna de precio
     * @param colFecha       Columna de fecha
     * @param colTipoSala    Columna de tipo de sala
     * 
     *                       Detalles de configuración:
     *                       - Formatea fechas y horas según patrones predefinidos
     *                       - Maneja valores nulos en todos los campos
     *                       - Aplica formato monetario al precio
     *                       - Adapta nombres de enumeraciones para mejor
     *                       visualización
     */
    private void configurarColumnas(TableColumn<Funcion, String> colHora,
            TableColumn<Funcion, String> colSala,
            TableColumn<Funcion, String> colFormato,
            TableColumn<Funcion, String> colTipoEstreno,
            TableColumn<Funcion, String> colPrecio,
            TableColumn<Funcion, String> colFecha,
            TableColumn<Funcion, String> colTipoSala) {

        colHora.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFechaHoraInicio() != null
                        ? cellData.getValue().getFechaHoraInicio().format(FORMATO_HORA)
                        : ""));

        colSala.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSala() != null ? cellData.getValue().getSala().getNombre()
                        : "Sala no disponible"));

        colTipoSala.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSala() != null && cellData.getValue().getSala().getTipo() != null
                        ? cellData.getValue().getSala().getTipo().toString()
                        : ""));

        colFormato.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFormato() != null ? cellData.getValue().getFormato().toString()
                        : ""));

        colTipoEstreno.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTipoEstreno() != null
                        ? cellData.getValue().getTipoEstreno().name().replace("_", " ")
                        : ""));

        colPrecio.setCellValueFactory(cellData -> {
            BigDecimal precio = cellData.getValue().calcularPrecioFinal();
            return new SimpleStringProperty(precio != null ? String.format("$%.2f", precio) : "$0.00");
        });

        colFecha.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFechaHoraInicio() != null
                        ? cellData.getValue().getFechaHoraInicio().format(FORMATO_FECHA)
                        : ""));
    }
}