package com.cinemax.venta_boletos.servicios;

import com.cinemax.peliculas.controladores.ControladorCartelera;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.utilidades.ManejadorMetodosComunes;
import com.cinemax.venta_boletos.controladores.ControladorVisualizadorFunciones;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Servicio para manejar la lógica de la cartelera de películas.
 * 
 * Responsabilidades:
 * - Gestionar la lista observable de películas disponibles
 * - Manejar la selección de películas por parte del usuario
 * - Coordinar la navegación entre pantallas
 * - Proporcionar datos para la vista de cartelera
 * 
 * @author CineMax Development Team
 * @version 1.2
 */
public class ServicioVisualizadorCartelera {

    // Controlador para obtener datos de películas
    private final ControladorCartelera controladorCartelera = new ControladorCartelera();

    // Lista observable de películas para enlace con la vista
    private ObservableList<Pelicula> listaPeliculas = FXCollections.observableArrayList();
    // Película actualmente seleccionada
    private Pelicula peliculaSeleccionada;
    private static final ServicioVisualizadorCartelera instancia = new ServicioVisualizadorCartelera();
    private final ScheduledExecutorService planificador = Executors.newScheduledThreadPool(1);

    public static ServicioVisualizadorCartelera obtenerInstancia() {
        return instancia;
    }

    private ServicioVisualizadorCartelera() {
        // Programar la actualización automática cada 10 minutos/
        planificador.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                cargarPeliculasDeCartelera(true); // Forzar recarga
                System.out.println("Cartelera actualizada automáticamente a las " + new java.util.Date());
            });
        }, 0, 10, TimeUnit.MINUTES); // Inicia inmediatamente y luego cada 10 minutos
    }
    /**
     * Detiene el planificador de actualizaciones automáticas
     */
    public void detenerPlanificador() {
        if (planificador != null && !planificador.isShutdown()) {
            planificador.shutdownNow();
            System.out.println("Planificador de cartelera detenido");
        }
    }

    // ========== MÉTODOS PÚBLICOS ==========

    /**
     * Obtiene la lista observable de películas
     * 
     * @return Lista observable de objetos Pelicula
     */
    public ObservableList<Pelicula> getListaPeliculas() {
        return listaPeliculas;
    }

    /**
     * Obtiene la película seleccionada
     * 
     * @return Pelicula seleccionada o null si no hay selección
     */
    public Pelicula getPeliculaSeleccionada() {
        return peliculaSeleccionada;
    }

    /**
     * Establece la película seleccionada
     * 
     * @param peliculaSeleccionada Película a marcar como seleccionada
     */
    public void setPeliculaSeleccionada(Pelicula peliculaSeleccionada) {
        this.peliculaSeleccionada = peliculaSeleccionada;
    }

    /**
     * Carga inicialmente la lista de películas disponibles
     */
    public void cargarPeliculasDeCartelera() {
        cargarPeliculasDeCartelera(false);
    }

    private void cargarPeliculasDeCartelera(boolean forzarRecarga) {
        try {
            // Si no se fuerza la recarga y la lista ya tiene películas, no hacemos nada
            if (!forzarRecarga && listaPeliculas != null && !listaPeliculas.isEmpty()) {
                return;
            }

            List<Pelicula> peliculasCargadas = controladorCartelera.obtenerCartelera();

            if (peliculasCargadas != null) {
                listaPeliculas.setAll(peliculasCargadas);
            } else {
                listaPeliculas.clear();
            }

        } catch (Exception e) {
            System.err.println("Error al obtener cartelera: " + e.getMessage());
            ManejadorMetodosComunes.mostrarVentanaError("Error al cargar películas: " + e.getMessage());
            listaPeliculas.clear();
        }
    }

    /**
     * Maneja la selección de una película y navega a la pantalla de funciones
     * 
     * @param peliculaSeleccionada Película seleccionada por el usuario
     * @param currentStage         Escenario actual de la aplicación
     */
    public void seleccionarPelicula(Pelicula peliculaSeleccionada, Stage currentStage) {
        validarSeleccionPelicula(peliculaSeleccionada); // aquí se hace la validación

        try {
            ControladorVisualizadorFunciones controller = ManejadorMetodosComunes.cambiarVentanaConControlador(
                    currentStage,
                    "/vistas/venta_boletos/VistaMostrarFunciones.fxml",
                    "CineMax");

            if (controller != null) {
                controller.asignarPeliculaSeleccionada(peliculaSeleccionada);
            }
        } catch (Exception e) {
            manejarErrorNavegacion(e);
        }
    }

    /**
     * Maneja el evento de regreso a la pantalla principal
     * 
     * @param event Evento de acción que disparó el regreso
     */
    public void regresarPantallaPrincipal(ActionEvent event) {
        try {
            Stage currentStage = obtenerStageDesdeEvento(event);
            ManejadorMetodosComunes.cambiarVentana(currentStage, "/vistas/empleados/PantallaPortalPrincipal.fxml");
        } catch (Exception e) {
            manejarErrorRegreso(e);
        }
    }

    // ========== MÉTODOS PRIVADOS ==========

    /**
     * Valida que se haya seleccionado una película válida
     * 
     * @param pelicula Película a validar
     */
    private void validarSeleccionPelicula(Pelicula pelicula) {
        if (pelicula == null) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, selecciona una película.");
            throw new IllegalArgumentException("No se seleccionó ninguna película");
        }
    }

    /**
     * Maneja errores durante la navegación
     * 
     * @param e Excepción ocurrida
     */
    private void manejarErrorNavegacion(Exception e) {
        ManejadorMetodosComunes.mostrarVentanaError("No se pudo cargar la pantalla de funciones");
        System.err.println("Error de navegación: " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * Maneja errores durante el regreso a pantalla principal
     * 
     * @param e Excepción ocurrida
     */
    private void manejarErrorRegreso(Exception e) {
        ManejadorMetodosComunes.mostrarVentanaError("Error al regresar a la pantalla principal.");
        System.err.println("Error al regresar: " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * Obtiene el Stage desde un evento de acción
     * 
     * @param event Evento de acción
     * @return Stage asociado al evento
     */
    private Stage obtenerStageDesdeEvento(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }
}