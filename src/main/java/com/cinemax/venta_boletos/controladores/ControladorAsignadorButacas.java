package com.cinemax.venta_boletos.controladores;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.controladores.ControladorDeConsultaSalas;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.utilidades.ManejadorMetodosComunes;
import com.cinemax.venta_boletos.servicios.ServicioTemporizador;
import com.cinemax.venta_boletos.modelos.entidades.Producto;
import com.cinemax.venta_boletos.modelos.persistencia.BoletoDAO;
import com.cinemax.venta_boletos.servicios.ServicioGeneradorBoleto;

// Imports para manejo de la interfaz JavaFX
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controlador para la pantalla de selección de butacas en el proceso de venta
 * de boletos.
 * 
 * Esta clase maneja la lógica de:
 * - Visualización del mapa de butacas de una sala específica
 * - Gestión de butacas ocupadas y disponibles
 * - Selección/deselección interactiva de butacas
 * - Navegación hacia la pantalla de facturación
 * - Integración con controladores de información lateral
 * 
 * Flujo principal:
 * 1. Recibe función seleccionada desde pantalla anterior
 * 2. Carga butacas ocupadas desde base de datos
 * 3. Renderiza mapa visual de la sala
 * 4. Permite selección interactiva de butacas disponibles
 * 5. Navega a facturación con butacas seleccionadas
 * 
 * @author GR3SW
 * @version 1.0
 */
public class ControladorAsignadorButacas implements SuscriptorSeleccionButaca {

    // ===== ELEMENTOS DE LA INTERFAZ (FXML) =====

    @FXML
    private Button buttonContinuar;

    @FXML
    private Button buttonVolver;

    @FXML
    private HBox headerBar;

    @FXML
    private VBox informacionFuncionContainer;

    @FXML
    private Label labelTipoSala;

    @FXML
    private VBox mapaButacasContainer;

    @FXML
    private Label timerLabel;


    // ===== ATRIBUTOS DE LÓGICA =====
    private Funcion funcionSeleccionada;

    /** Controlador del panel lateral que muestra información de la función */
    private ControladorInformacionDeVenta controladorInformacionDeVenta;

    /** Controlador para la gestión del mapa de butacas y su visualización */
    //private ControladorDeConsultaSalas controladorDeConsultaSalas;

    private List<Butaca> butacasSeleccionadas;

    private BoletoDAO boletoDAO;

    private List<Butaca> butacasYaSeleccionadas;

    //private String idDeSesion;

    public ControladorAsignadorButacas() {
        boletoDAO = new BoletoDAO();
        butacasSeleccionadas = new ArrayList<>();
        butacasYaSeleccionadas = new ArrayList<>();
    }

    

    /**
     * Inicializa todos los datos necesarios para mostrar el mapa de butacas.
     * 
     * Proceso de inicialización:
     * 1. Configura el encabezado con el tipo de sala
     * 2. Carga la información lateral de la función
     * 3. Consulta butacas ocupadas desde la base de datos
     * 4. Crea un Set optimizado para búsqueda de butacas ocupadas (O(1))
     * 5. Renderiza el mapa visual de butacas
     * 6. Almacena la referencia a la función seleccionada
     * 
     * @param funcionSeleccionada La función para la cual se seleccionarán butacas
     */
    public void inicializarDatos(Funcion funcionSeleccionada) {
        // 0. id de sesion
        //crearIdDeSesion();

        // 1. Configurar encabezado con tipo de sala (VIP, Normal, etc.)
        labelTipoSala.setText(funcionSeleccionada.getSala().getTipo().name());

        // 2. Cargar panel lateral con información de la función
        cargarInformacionFuncion(funcionSeleccionada);

        // 3. Obtener butacas ya ocupadas por otros boletos vendidos
        List<Butaca> butacasOcupadas;
        try {
            butacasOcupadas = boletoDAO.listarButacasDeBoletosPorFuncion(funcionSeleccionada);
            System.out.println("=== DEBUG BUTACAS OCUPADAS ===");
            System.out.println("Butacas ocupadas encontradas: " + butacasOcupadas.size());
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al cargar las butacas OCUPADAS: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 4. Crear Set de IDs para búsqueda eficiente O(1) de butacas ocupadas
        Set<Integer> codigosButacasOcupadas = new HashSet<>();
        for (Butaca butacaOcupada : butacasOcupadas) {
            codigosButacasOcupadas.add(butacaOcupada.getId());
        }

        // 4.5 Obtener butacas reservadas
        List<Butaca> butacasReservadas;
        try {
            butacasReservadas = boletoDAO.listarButacasReservadasPorFuncion(funcionSeleccionada, ServicioTemporizador.getInstancia().getIdDeSesion());
            System.out.println("=== DEBUG BUTACAS RESERVADAS ===");
            System.out.println("Butacas reservadas encontradas: " + butacasReservadas.size());
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al cargar las butacas RESERVADAS: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 4.75 Crear Set de IDs para búsqueda eficiente O(1) de butacas reservadas
        Set<Integer> codigosButacasReservadas = new HashSet<>();
        for (Butaca butacaReservada : butacasReservadas) {
            codigosButacasReservadas.add(butacaReservada.getId());
        }

        // 6. Guardar referencia para uso posterior en otros métodos
        this.funcionSeleccionada = funcionSeleccionada;

        // 5. Renderizar el mapa visual de butacas con estado ocupado/disponible
        cargarMapaButacas(codigosButacasOcupadas, funcionSeleccionada.getSala(), codigosButacasReservadas);

        
        // Vincular el label del temporizador para que se actualice automáticamente
        if (timerLabel != null) {
            timerLabel.textProperty().bind(ServicioTemporizador.getInstancia().tiempoRestanteProperty());
        }

        // Configurar el cierre de ventana cuando esté disponible
        configurarCierreDeVentanaConListener();
    }

    /**
     * Carga el panel lateral con información detallada de la función.
     * 
     * @param funcion La función de la cual mostrar información
     */
    private void cargarInformacionFuncion(Funcion funcion) {
        try {
            // Cargar vista FXML del panel de información lateral
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/vistas/venta_boletos/VistaInformacionDeVenta.fxml"));
            Parent vistaInformacionLateral = loader.load();

            // Inyectar la vista en el contenedor de la interfaz principal
            informacionFuncionContainer.getChildren().add(vistaInformacionLateral);

            // Configurar el controlador del panel lateral
            controladorInformacionDeVenta = loader.getController();
            controladorInformacionDeVenta.setRoot(vistaInformacionLateral);
            controladorInformacionDeVenta.cargarInformacionDeFuncionSeleccionada(funcion);            

        } catch (IOException e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al cargar el mapa de butacas: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Integra el componente MapaButacas.fxml que maneja la visualización
     * gráfica de todas las butacas, distinguiendo entre ocupadas y disponibles.
     * Establece la comunicación bidireccional con este controlador.
     * 
     * @param codigosButacasOcupadas Set con IDs de butacas ya vendidas
     * @param salaSeleccionada       Sala cuyo mapa se debe renderizar
     */
    private void cargarMapaButacas(Set<Integer> codigosButacasOcupadas, Sala salaSeleccionada, Set<Integer> codigosButacasReservadas) {
        try {
            // 1. Cargar vista FXML del mapa de butacas
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/vistas/salas/MapaButacas.fxml"));
            Parent mapaButacas = loader.load();

            // 2. Inyectar el mapa en el contenedor principal
            mapaButacasContainer.getChildren().add(mapaButacas);

            // 3. Configurar comunicación bidireccional entre controladores
            ControladorDeConsultaSalas controladorDeConsultaSalas = loader.getController();
            //controladorDeConsultaSalas.setControladorAsignadorButacas(this);
            controladorDeConsultaSalas.suscribir(this);

            controladorDeConsultaSalas.setButacasYaSeleccionadas(butacasYaSeleccionadas);

            // 4. Renderizar butacas con estado visual (ocupada/disponible)
            controladorDeConsultaSalas.mostrarButacasDeSala(codigosButacasOcupadas, salaSeleccionada, codigosButacasReservadas, funcionSeleccionada, ServicioTemporizador.getInstancia().getIdDeSesion());
            //controladorDeConsultaSalas.mostrarButacasDeSala(codigosButacasOcupadas, salaSeleccionada, funcionSeleccionada, idDeSesion);


        } catch (IOException e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al cargar el mapa de butacas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Regresa a la pantalla anterior (selección de funciones) manteniendo
     * el contexto de la película seleccionada para facilitar la navegación.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    void onBackAction(ActionEvent event) {
        // Detener el temporizador al retroceder
        ServicioTemporizador.getInstancia().detenerTemporizador();
        // Obtener referencia a la ventana actual
        Stage currentStage = (Stage) buttonContinuar.getScene().getWindow();

        realizarAccionesAntesDeCerrarVentana();

        // Cambiar a la vista de funciones preservando el contexto de la película
        ControladorVisualizadorFunciones controladorFunciones = ManejadorMetodosComunes
                .cambiarVentanaConControlador(currentStage, "/vistas/venta_boletos/VistaMostrarFunciones.fxml",
                        "CineMax");
        controladorFunciones.asignarPeliculaSeleccionada(funcionSeleccionada.getPelicula());
    }

    /**
     * Valida que se hayan seleccionado butacas, genera los boletos correspondientes
     * y navega a la pantalla de facturación. Implementa un patrón de carga previa
     * de datos antes de mostrar la nueva vista para evitar problemas de
     * renderizado.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    void onContinuarAction(ActionEvent event) {
        // Validar que el usuario haya seleccionado al menos una butaca
        if (butacasSeleccionadas.isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Debe seleccionar al menos una butaca.");
            return;
        }

        try {
            // 1. Generar boletos basados en función y butacas seleccionadas
            ServicioGeneradorBoleto servicioGeneradorBoleto = new ServicioGeneradorBoleto();
            List<Producto> boletosGenerados = servicioGeneradorBoleto.generarBoletos(funcionSeleccionada,
                    butacasSeleccionadas);

            // 2. Obtener referencia a la ventana actual
            Stage currentStage = (Stage) buttonContinuar.getScene().getWindow();

            // 3. Cargar vista de facturación SIN mostrarla aún (patrón de pre-carga)
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/vistas/venta_boletos/VistaFacturacion.fxml"));
            Parent root = loader.load();

            // 4. Configurar el controlador de facturación con datos necesarios
            ControladorFacturacion controladorFacturacion = loader.getController();
            controladorFacturacion.setControladorInformacionDeVenta(controladorInformacionDeVenta);

            // 5. Inicializar datos ANTES de mostrar la vista (evita problemas de
            // renderizado)
            controladorFacturacion.cargarBoletosSeleccionados(boletosGenerados);

            // 6. Ahora sí cambiar la escena con todos los datos ya cargados
            Scene newScene = new Scene(root);
            currentStage.setScene(newScene);
            currentStage.setTitle("CineMax"); 

        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al confirmar: " + e.getMessage());
            System.err.println("Error al cargar la vista de datos del cliente: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Método llamado por el controlador del mapa de butacas cuando el usuario
     * hace clic en una butaca disponible. Actualiza tanto la lista interna
     * como la visualización en el panel lateral.
     * 
     * @param butaca La butaca que se quiere agregar a la selección
     */
    //public void agregarButacaSeleccionada(Butaca butaca) {
    //    // Validar que la butaca sea válida y no esté ya seleccionada
    //    if (butaca == null || butacasSeleccionadas.contains(butaca)) {
    //        return;
    //    }
//
    //    // Agregar a la lista interna de seleccionadas
    //    butacasSeleccionadas.add(butaca);
//
    //    // Actualizar visualización en panel lateral
    //    ControladorInformacionDeVenta.cargarButacaSeleccionada(butaca);
    //    ControladorInformacionDeVenta.calcularPosibleSubtotal(butacasSeleccionadas, funcionSeleccionada);
    //}

    /**
     * Método llamado cuando el usuario hace clic nuevamente en una butaca
     * ya seleccionada para deseleccionarla. Actualiza tanto la lista como
     * la visualización del panel lateral.
     * 
     * @param butaca La butaca que se quiere remover de la selección
     */
    //public void quitarButacaDeseleccionada(Butaca butaca) {
    //    // Validar que la butaca sea válida y esté en la lista de seleccionadas
    //    if (butaca == null || !butacasSeleccionadas.contains(butaca)) {
    //        return;
    //    }
//
    //    // Remover de la lista interna
    //    butacasSeleccionadas.remove(butaca);
//
    //    // Actualizar visualización en panel lateral
    //    ControladorInformacionDeVenta.removerButacaSeleccionada(butaca);
    //    ControladorInformacionDeVenta.calcularPosibleSubtotal(butacasSeleccionadas, funcionSeleccionada);
    //}

    @Override
    public void agregarButacaSeleccionada(Butaca butaca) {
         // Validar que la butaca sea válida y no esté ya seleccionada
        if (butaca == null || butacasSeleccionadas.contains(butaca)) {
            return;
        }

        // Agregar a la lista interna de seleccionadas
        butacasSeleccionadas.add(butaca);

        // Actualizar visualización en panel lateral
        controladorInformacionDeVenta.cargarButacaSeleccionada(butaca);
        controladorInformacionDeVenta.calcularSubTotalImpuestoYTotal(butacasSeleccionadas, funcionSeleccionada);
    }

    @Override
    public void eliminarButacaSeleccionada(Butaca butaca) {
        // Validar que la butaca sea válida y esté en la lista de seleccionadas
        if (butaca == null || !butacasSeleccionadas.contains(butaca)) {
            return;
        }

        // Remover de la lista interna
        butacasSeleccionadas.remove(butaca);

        // Actualizar visualización en panel lateral
        controladorInformacionDeVenta.removerButacaSeleccionada(butaca);
        controladorInformacionDeVenta.calcularSubTotalImpuestoYTotal(butacasSeleccionadas, funcionSeleccionada);
    }

    /**
     * Configura el manejo del evento de cierre de ventana usando un listener
     * que se activa cuando la escena está disponible
     */
    private void configurarCierreDeVentanaConListener() {
        // Agregar un listener a la propiedad scene del botón
        buttonContinuar.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                System.out.println("Escena detectada, configurando cierre de ventana...");
                // Agregar un listener adicional para cuando la ventana esté disponible
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        System.out.println("Ventana detectada, configurando evento de cierre...");
                        configurarCierreDeVentana((Stage) newWindow);
                    }
                });
                
                // Si la ventana ya está disponible, configurar inmediatamente
                if (newScene.getWindow() != null) {
                    System.out.println("Ventana ya disponible, configurando evento de cierre...");
                    configurarCierreDeVentana((Stage) newScene.getWindow());
                }
            }
        });
        
        // Si la escena ya está disponible, configurar inmediatamente
        if (buttonContinuar.getScene() != null) {
            System.out.println("Escena ya disponible, configurando cierre...");
            if (buttonContinuar.getScene().getWindow() != null) {
                configurarCierreDeVentana((Stage) buttonContinuar.getScene().getWindow());
            }
        }
    }

    /**
     * Configura el manejo del evento de cierre de ventana
     */
    private void configurarCierreDeVentana(Stage stage) {
        try {
            System.out.println("Configurando evento setOnCloseRequest...");
            // Interceptar el evento de cierre (cuando presionan la "X")
            stage.setOnCloseRequest(e -> {
                System.out.println("Usuario cerró la ventana - Liberando reservas...");
                realizarAccionesAntesDeCerrarVentana();
            });
            System.out.println("Evento de cierre configurado exitosamente!");
            
        } catch (Exception e) {
            System.err.println("Error al configurar el cierre de ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Libera las reservas cuando se cierra la ventana
     */
    private void realizarAccionesAntesDeCerrarVentana() {
        try {
            // Detener temporizador
            ServicioTemporizador.getInstancia().detenerTemporizador();
            
            // Liberar reservas de este session
            if (ServicioTemporizador.getInstancia().getIdDeSesion() != null) {
                boletoDAO.liberarTodasButacasReservadasTemporalmentePorSession(ServicioTemporizador.getInstancia().getIdDeSesion());
                System.out.println("Reservas liberadas para session: " + ServicioTemporizador.getInstancia().getIdDeSesion());
            }
            
        } catch (Exception e) {
            System.err.println("Error al liberar reservas al cerrar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setButacasYaSeleccionadas(List<Butaca> butacasYaSeleccionadas) {
        this.butacasYaSeleccionadas = butacasYaSeleccionadas;
    }

}
