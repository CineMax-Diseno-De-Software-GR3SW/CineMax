package com.cinemax.venta_boletos.servicios;

import com.cinemax.comun.ControladorCargaConDatos;
import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.peliculas.controladores.ControladorFunciones;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
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
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Servicio para la gestión y visualización de funciones cinematográficas.
 * <p>
 * Responsabilidades principales:
 * 1. Carga y muestra las funciones disponibles para una película específica.
 * 2. Configura la presentación de datos en una tabla JavaFX.
 * 3. Maneja la navegación entre pantallas de cartelera y selección de butacas.
 * 4. Valida y procesa la selección de funciones por parte del usuario.
 *
 * @author GR3SW
 * @version 1.2
 */
public class ServicioVisualizadorFunciones {

    // ==================== CONSTANTES ====================
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ==================== ATRIBUTOS ====================
    private final ControladorFunciones controladorFunciones = new ControladorFunciones();

    // ==================== MÉTODOS PÚBLICOS ====================

    /**
     * Carga las funciones disponibles para una película específica en una tabla
     * JavaFX, con la opción de aplicar filtros por fecha, formato y tipo de sala.
     *
     * @param tabla              Tabla donde se mostrarán las funciones.
     * @param columnaHora        Columna para mostrar la hora de la función.
     * @param columnaSala        Columna para mostrar la sala.
     * @param columnaFormato     Columna para mostrar el formato (2D, 3D, etc.).
     * @param columnaTipoEstreno Columna para mostrar el tipo de estreno.
     * @param columnaPrecio      Columna para mostrar el precio.
     * @param columnaFecha       Columna para mostrar la fecha.
     * @param columnaTipoSala    Columna para mostrar el tipo de sala.
     * @param nombrePelicula     Nombre de la película para filtrar funciones.
     * @param fechaFiltro        Fecha por la cual filtrar las funciones. Puede ser
     *                           nulo.
     * @param formatoFiltro      Formato por el cual filtrar las funciones ("Todos",
     *                           "2D", etc.).
     * @param tipoSalaFiltro     Tipo de sala por el cual filtrar ("Todos", "VIP",
     *                           etc.).
     */
    public void cargarFunciones(TableView<Funcion> tabla,
            TableColumn<Funcion, String> columnaHora,
            TableColumn<Funcion, String> columnaSala,
            TableColumn<Funcion, String> columnaFormato,
            TableColumn<Funcion, String> columnaTipoEstreno,
            TableColumn<Funcion, String> columnaPrecio,
            TableColumn<Funcion, String> columnaFecha,
            TableColumn<Funcion, String> columnaTipoSala,
            String nombrePelicula,
            LocalDate fechaFiltro,
            String formatoFiltro,
            String tipoSalaFiltro) {

        configurarColumnas(columnaHora, columnaSala, columnaFormato, columnaTipoEstreno, columnaPrecio, columnaFecha,
                columnaTipoSala);

        try {
            // Ya vienen filtradas las funciones a partir de ahora
            List<Funcion> funcionesObtenidas = controladorFunciones.obtenerFuncionesPorNombrePelicula(nombrePelicula);

            // Aquí solo aplicamos filtros adicionales (fecha exacta, formato, tipoSala)
            if (fechaFiltro != null) {
                funcionesObtenidas = funcionesObtenidas.stream()
                        .filter(funcion -> funcion.getFechaHoraInicio().toLocalDate().isEqual(fechaFiltro))
                        .collect(Collectors.toList());
            }

            if (formatoFiltro != null && !"Todos".equals(formatoFiltro)) {
                funcionesObtenidas = funcionesObtenidas.stream()
                        .filter(funcion -> funcion.getFormato() != null &&
                                formatoFiltro.equals(funcion.getFormato().toString()))
                        .collect(Collectors.toList());
            }

            if (tipoSalaFiltro != null && !"Todos".equals(tipoSalaFiltro)) {
                funcionesObtenidas = funcionesObtenidas.stream()
                        .filter(funcion -> funcion.getSala() != null &&
                                funcion.getSala().getTipo() != null &&
                                tipoSalaFiltro.equals(funcion.getSala().getTipo().toString()))
                        .collect(Collectors.toList());
            }

            ObservableList<Funcion> listaFunciones = FXCollections.observableArrayList(funcionesObtenidas);

            Platform.runLater(() -> {
                tabla.setItems(listaFunciones);
                if (listaFunciones.isEmpty()) {
                    tabla.setPlaceholder(new Label("No hay funciones disponibles con los filtros seleccionados."));
                }
            });

        } catch (Exception e) {
            Platform.runLater(() -> {
                tabla.setPlaceholder(new Label("Error al cargar funciones."));
                ManejadorMetodosComunes.mostrarVentanaError("Error al cargar funciones: " + e.getMessage());
            });
            e.printStackTrace();
        }
    }

    /**
     * Navega de regreso a la pantalla de cartelera principal.
     *
     * @param event Evento de acción que disparó la navegación.
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
     * @param tabla Tabla que contiene las funciones disponibles.
     */
    public void seleccionarFuncion(TableView<Funcion> tabla) {
        Funcion funcionSeleccionada = tabla.getSelectionModel().getSelectedItem();
        validarSeleccionPelicula(funcionSeleccionada);

        try {
            Stage currentStage = (Stage) tabla.getScene().getWindow();
            ServicioTemporizador.getInstance().empezarTemporizador(currentStage);

            ControladorCargaConDatos controladorCargaConDatos = new ControladorCargaAsignacionButacas(
                    "/vistas/venta_boletos/VistaSeleccionButacas.fxml",
                    currentStage,
                    new ArrayList<>(List.of(funcionSeleccionada)));

            ManejadorMetodosComunes.mostrarVistaDeCargaPasandoDatosOptimizada(currentStage, controladorCargaConDatos, 8,
                    150);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== MÉTODOS PRIVADOS ====================

    /**
     * Configura los renderizadores de valor para cada columna de la tabla.
     *
     * @param columnaHora        Columna de hora.
     * @param columnaSala        Columna de sala.
     * @param columnaFormato     Columna de formato.
     * @param columnaTipoEstreno Columna de tipo de estreno.
     * @param columnaPrecio      Columna de precio.
     * @param columnaFecha       Columna de fecha.
     * @param columnaTipoSala    Columna de tipo de sala.
     */
    private void configurarColumnas(TableColumn<Funcion, String> columnaHora,
            TableColumn<Funcion, String> columnaSala,
            TableColumn<Funcion, String> columnaFormato,
            TableColumn<Funcion, String> columnaTipoEstreno,
            TableColumn<Funcion, String> columnaPrecio,
            TableColumn<Funcion, String> columnaFecha,
            TableColumn<Funcion, String> columnaTipoSala) {

        columnaHora.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFechaHoraInicio() != null
                        ? cellData.getValue().getFechaHoraInicio().format(FORMATO_HORA)
                        : ""));

        columnaSala.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSala() != null ? cellData.getValue().getSala().getNombre()
                        : "Sala no disponible"));

        columnaTipoSala.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSala() != null && cellData.getValue().getSala().getTipo() != null
                        ? cellData.getValue().getSala().getTipo().toString()
                        : ""));

        columnaFormato.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFormato() != null ? cellData.getValue().getFormato().toString()
                        : ""));

        columnaTipoEstreno.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTipoEstreno() != null
                        ? cellData.getValue().getTipoEstreno().name().replace("_", " ")
                        : ""));

        columnaPrecio.setCellValueFactory(cellData -> {
            BigDecimal precio = cellData.getValue().calcularPrecioFinal();
            return new SimpleStringProperty(precio != null ? String.format("$%.2f", precio) : "$0.00");
        });

        columnaFecha.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFechaHoraInicio() != null
                        ? cellData.getValue().getFechaHoraInicio().format(FORMATO_FECHA)
                        : ""));
    }

    /**
     * Valida que se haya seleccionado una funcion válida
     * 
     * @param pelicula Película a validar
     */
    private void validarSeleccionPelicula(Funcion funcionSeleccionada) {
        if (funcionSeleccionada == null) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, selecciona una función.");
            return;
        }
    }

}