package com.cinemax.venta_boletos.controladores;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.controladores.ControladorDeConsultaSalas;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.Sala;
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
public class ControladorAsignadorButacas {

    // ===== ELEMENTOS DE LA INTERFAZ (FXML) =====

    /** Botón para continuar al proceso de facturación */
    @FXML
    private Button buttonContinuar;

    /** Botón para regresar a la pantalla de selección de funciones */
    @FXML
    private Button buttonVolver;

    /** Contenedor de la barra superior de la interfaz */
    @FXML
    private HBox headerBar;

    /** Contenedor donde se carga la información lateral de la función */
    @FXML
    private VBox informacionFuncionContainer;

    /** Etiqueta que muestra el tipo de sala (VIP, Normal, etc.) */
    @FXML
    private Label labelTipoSala;

    /** Contenedor donde se renderiza el mapa visual de butacas */
    @FXML
    private VBox mapaButacasContainer;

    @FXML
    private Label timerLabel; // <-- AÑADIR ESTA LÍNEA


    // ===== ATRIBUTOS DE LÓGICA =====
    /** Función cinematográfica seleccionada (película + horario + sala) */
    private Funcion funcionSeleccionada;

    /** Controlador del panel lateral que muestra información de la función */
    private ControladorInformacionDeVenta controladorInformacionLateral;

    /** Controlador para la gestión del mapa de butacas y su visualización */
    private ControladorDeConsultaSalas controladorConsultaSalas;

    /** Lista de butacas que el usuario ha seleccionado para comprar */
    private List<Butaca> butacasSeleccionadas;

    /** DAO para acceso a datos de boletos y butacas ocupadas */
    private BoletoDAO boletoDAO;

    /**
     * Constructor que inicializa los componentes básicos del controlador.
     * 
     * Configura el DAO para acceso a datos y la lista para butacas seleccionadas.
     * Se ejecuta automáticamente al crear una instancia del controlador.
     */
    public ControladorAsignadorButacas() {
        boletoDAO = new BoletoDAO();
        butacasSeleccionadas = new ArrayList<>();
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
            ManejadorMetodosComunes.mostrarVentanaError("Error al cargar las butacas ocupadas: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 4. Crear Set de IDs para búsqueda eficiente O(1) de butacas ocupadas
        Set<Integer> codigosButacasOcupadas = new HashSet<>();
        for (Butaca butacaOcupada : butacasOcupadas) {
            codigosButacasOcupadas.add(butacaOcupada.getId());
        }

        // 5. Renderizar el mapa visual de butacas con estado ocupado/disponible
        cargarMapaButacas(codigosButacasOcupadas, funcionSeleccionada.getSala());

        // 6. Guardar referencia para uso posterior en otros métodos
        this.funcionSeleccionada = funcionSeleccionada;

        // Vincular el label del temporizador para que se actualice automáticamente
        if (timerLabel != null) {
            timerLabel.textProperty().bind(ServicioTemporizador.getInstance().tiempoRestanteProperty());
        }


    }

    /**
     * Carga el panel lateral con información detallada de la función.
     * 
     * Renderiza dinámicamente la vista VistaInformacionLateral.fxml que muestra
     * detalles como película, horario, precio, etc. Configura el controlador
     * asociado para mostrar solo el precio inicialmente.
     * 
     * @param funcion La función de la cual mostrar información
     */
    private void cargarInformacionFuncion(Funcion funcion) {
        try {
            // Cargar vista FXML del panel de información lateral
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/vistas/venta_boletos/VistaInformacionLateral.fxml"));
            Parent vistaInformacionLateral = loader.load();

            // Inyectar la vista en el contenedor de la interfaz principal
            informacionFuncionContainer.getChildren().add(vistaInformacionLateral);

            // Configurar el controlador del panel lateral
            controladorInformacionLateral = loader.getController();
            controladorInformacionLateral.setRoot(vistaInformacionLateral);
            controladorInformacionLateral.mostrarInformacionDeFuncionSeleccionada(funcion);
            controladorInformacionLateral.mostrarSoloSubtotal(); // Vista inicial simplificada

        } catch (IOException e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al cargar el mapa de butacas: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Carga y renderiza el mapa visual interactivo de butacas de la sala.
     * 
     * Integra el componente MapaButacas.fxml que maneja la visualización
     * gráfica de todas las butacas, distinguiendo entre ocupadas y disponibles.
     * Establece la comunicación bidireccional con este controlador.
     * 
     * @param codigosButacasOcupadas Set con IDs de butacas ya vendidas
     * @param salaSeleccionada       Sala cuyo mapa se debe renderizar
     */
    private void cargarMapaButacas(Set<Integer> codigosButacasOcupadas, Sala salaSeleccionada) {
        try {
            // 1. Cargar vista FXML del mapa de butacas
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/vistas/salas/MapaButacas.fxml"));
            Parent mapaButacas = loader.load();

            // 2. Inyectar el mapa en el contenedor principal
            mapaButacasContainer.getChildren().add(mapaButacas);

            // 3. Configurar comunicación bidireccional entre controladores
            controladorConsultaSalas = loader.getController();
            controladorConsultaSalas.setControladorAsignadorButacas(this);

            // 4. Renderizar butacas con estado visual (ocupada/disponible)
            controladorConsultaSalas.mostrarButacasDeSala(codigosButacasOcupadas, salaSeleccionada);

        } catch (IOException e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al cargar el mapa de butacas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Maneja el evento de clic en el botón "Volver".
     * 
     * Regresa a la pantalla anterior (selección de funciones) manteniendo
     * el contexto de la película seleccionada para facilitar la navegación.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    void onBackAction(ActionEvent event) {
        // Detener el temporizador al retroceder
        ServicioTemporizador.getInstance().detenerTemporizador();
        // Obtener referencia a la ventana actual
        Stage currentStage = (Stage) buttonContinuar.getScene().getWindow();

        // Cambiar a la vista de funciones preservando el contexto de la película
        ControladorMostrarFunciones controladorFunciones = ManejadorMetodosComunes
                .cambiarVentanaConControlador(currentStage, "/vistas/venta_boletos/VistaMostrarFunciones.fxml",
                        "CineMAX");
        controladorFunciones.setPelicula(funcionSeleccionada.getPelicula());
    }

    /**
     * Maneja el evento de clic en el botón "Continuar".
     * 
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
            List<Producto> boletosGenerados = servicioGeneradorBoleto.generarBoleto(funcionSeleccionada,
                    butacasSeleccionadas);

            // 2. Obtener referencia a la ventana actual
            Stage currentStage = (Stage) buttonContinuar.getScene().getWindow();

            // 3. Cargar vista de facturación SIN mostrarla aún (patrón de pre-carga)
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/vistas/venta_boletos/VistaFacturacion.fxml"));
            Parent root = loader.load();

            // 4. Configurar el controlador de facturación con datos necesarios
            ControladorFacturacion controladorFacturacion = loader.getController();
            controladorFacturacion.setControladorInformacionLateral(controladorInformacionLateral);

            // 5. Inicializar datos ANTES de mostrar la vista (evita problemas de
            // renderizado)
            controladorFacturacion.cargarBoletosSeleccionados(boletosGenerados);

            // 6. Ahora sí cambiar la escena con todos los datos ya cargados
            Scene newScene = new Scene(root);
            currentStage.setScene(newScene);
            currentStage.setTitle("CineMAX");

        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al confirmar: " + e.getMessage());
            System.err.println("Error al cargar la vista de datos del cliente: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Añade una butaca a la lista de seleccionadas.
     * 
     * Método llamado por el controlador del mapa de butacas cuando el usuario
     * hace clic en una butaca disponible. Actualiza tanto la lista interna
     * como la visualización en el panel lateral.
     * 
     * @param butaca La butaca que se quiere agregar a la selección
     */
    public void agregarButacaSeleccionada(Butaca butaca) {
        // Validar que la butaca sea válida y no esté ya seleccionada
        if (butaca == null || butacasSeleccionadas.contains(butaca)) {
            return;
        }

        // Agregar a la lista interna de seleccionadas
        butacasSeleccionadas.add(butaca);

        // Actualizar visualización en panel lateral
        controladorInformacionLateral.mostrarButacaSeleccionada(butaca);
        controladorInformacionLateral.mostrarPosibleSubtotal(butacasSeleccionadas, funcionSeleccionada);
    }

    /**
     * Remueve una butaca de la lista de seleccionadas.
     * 
     * Método llamado cuando el usuario hace clic nuevamente en una butaca
     * ya seleccionada para deseleccionarla. Actualiza tanto la lista como
     * la visualización del panel lateral.
     * 
     * @param butaca La butaca que se quiere remover de la selección
     */
    public void quitarButacaDeseleccionada(Butaca butaca) {
        // Validar que la butaca sea válida y esté en la lista de seleccionadas
        if (butaca == null || !butacasSeleccionadas.contains(butaca)) {
            return;
        }

        // Remover de la lista interna
        butacasSeleccionadas.remove(butaca);

        // Actualizar visualización en panel lateral
        controladorInformacionLateral.removerButacaDeLista(butaca);
        controladorInformacionLateral.mostrarPosibleSubtotal(butacasSeleccionadas, funcionSeleccionada);
    }

}
