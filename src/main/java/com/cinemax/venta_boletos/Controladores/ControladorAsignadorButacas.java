package com.cinemax.venta_boletos.Controladores;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.controladores.ControladorDeConsultaSalas;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.EstadoButaca;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.modelos.entidades.TipoSala;
import com.cinemax.salas.servicios.ButacaService;
import com.cinemax.venta_boletos.Modelos.Boleto;
import com.cinemax.venta_boletos.Modelos.Producto;
import com.cinemax.venta_boletos.Modelos.Persistencia.BoletoDAO;
import com.cinemax.venta_boletos.Servicios.ServicioGeneradorBoleto;

// imports para manejar el mapa de butacas
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControladorAsignadorButacas {

    // ------FXML------
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

    // ------Atributos------
    private ControladorDeConsultaSalas controladorConsultaSalas;
    private List<Butaca> butacasSeleccionadas;
    private BoletoDAO boletoDAO;
    private Funcion funcionSeleccionada;
    private ControladorInformacionLateral controladorInformacionLateral;

    public ControladorAsignadorButacas() {
        boletoDAO = new BoletoDAO();
        butacasSeleccionadas = new ArrayList<>();
    }

    // 1. Inicializar datos de la función seleccionada
    public void inicializarDatos(Funcion funcionSeleccionada) {

        // 1. Colocar encabezado de tipo de sala
        labelTipoSala.setText(funcionSeleccionada.getSala().getTipo().name()); // encabezado

        // 2. Cargar información de la función
        cargarInformacionFuncion(funcionSeleccionada);

        // 3. Cargar butacas ocupadas
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

        // 4. Crear un Set de códigos alfanuméricos de butacas ocupadas para búsqueda
        // O(1)
        Set<Integer> codigosButacasOcupadas = new HashSet<>();
        for (Butaca butacaOcupada : butacasOcupadas) {
            codigosButacasOcupadas.add(butacaOcupada.getId());
        }

        cargarMapaButacas(codigosButacasOcupadas, funcionSeleccionada.getSala());

        // 7. Asignar la función seleccionada al controlador
        this.funcionSeleccionada = funcionSeleccionada;

    }

    private void cargarInformacionFuncion(Funcion funcion) {
        try {
            // Cargar el FXML del mapa de butacas
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/vistas/venta_boletos/VistaInformacionLateral.fxml"));
            Parent vistaInformacionLateral = loader.load();

            // Agregar el mapa al contenedor
            informacionFuncionContainer.getChildren().add(vistaInformacionLateral);

            // Obtener referencia al controlador del mapa
            controladorInformacionLateral = loader.getController();
            controladorInformacionLateral.setRoot(vistaInformacionLateral); // Establecer la vista en el controlador
            controladorInformacionLateral.colocarInformacionFuncion(funcion); // Asignar la función seleccionada
            controladorInformacionLateral.mostrarSoloPrecio();

        } catch (IOException e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al cargar el mapa de butacas: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void cargarMapaButacas(Set<Integer> codigosButacasOcupadas, Sala salaSeleccionada) {
        try {
            // 1. Cargar el FXML del mapa de butacas
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/vistas/salas/MapaButacas.fxml"));
            Parent mapaButacas = loader.load();

            // 2. Agregar el mapa al contenedor
            mapaButacasContainer.getChildren().add(mapaButacas);

            // 3. Obtener referencia al controlador del mapa
            controladorConsultaSalas = loader.getController();

            // 4. Mostrar las butacas
            controladorConsultaSalas.setControladorAsignadorButacas(this);
            controladorConsultaSalas.mostrarButacasDeSala(codigosButacasOcupadas, salaSeleccionada);

        } catch (IOException e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al cargar el mapa de butacas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onBackAction(ActionEvent event) {
        // Regresar a la pantalla de funciones con la información de la película
        // seleccionada

        Stage currentStage = (Stage) buttonContinuar.getScene().getWindow(); // ventana actual
        // Cambiar a la vista de funciones y pasar el título de la película seleccionada
        ControladorMostrarFunciones controladorFunciones = ManejadorMetodosComunes.cambiarVentanaConControlador(
                currentStage, "/vistas/venta_boletos/VistaMostrarFunciones.fxml",
                "funciones de " + funcionSeleccionada.getPelicula().getTitulo());
        controladorFunciones.setPelicula(funcionSeleccionada.getPelicula().getTitulo());
    }

    @FXML
    void onContinuarAction(ActionEvent event) {
        if (butacasSeleccionadas.isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Debe seleccionar al menos una butaca.");
            return;
        }

        try {
            // 1. Generar los boletos
            ServicioGeneradorBoleto servicioGeneradorBoleto = new ServicioGeneradorBoleto();
            List<Producto> boletosGenerados = servicioGeneradorBoleto.generarBoleto(funcionSeleccionada,
                    butacasSeleccionadas);

            // 2. ventana actual
            Stage currentStage = (Stage) buttonContinuar.getScene().getWindow();

            // 3. Cargar la vista SIN mostrarla todavía
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/vistas/venta_boletos/datos-cliente-view.fxml"));
            Parent root = loader.load();

            // 4. Obtener el controlador
            ControladorFacturacion controladorFacturacion = loader.getController();

            // 5. Inicializar el controlador de facturación con los datos necesarios
            controladorFacturacion.setControladorInformacionLateral(controladorInformacionLateral);

            // 6. Inicializar los datos ANTES de mostrar
            controladorFacturacion.initData(boletosGenerados);

            // AHORA sí cambiar la escena con todo ya cargado
            Scene newScene = new Scene(root);
            currentStage.setScene(newScene);
            currentStage.setTitle("Seleccionar Butacas");

            System.out.println(butacasSeleccionadas.stream()
                    .map(b -> b.getFila() + b.getColumna())
                    .collect(Collectors.joining(", ")));

        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al confirmar: " + e.getMessage());
            System.err.println("Error al cargar la vista de datos del cliente: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void agregarButacaSeleccionada(Butaca butaca) {
        if (butaca == null || butacasSeleccionadas.contains(butaca)) {
            return; // No agregar si la butaca es nula o ya está seleccionada
        }
        butacasSeleccionadas.add(butaca);
        controladorInformacionLateral.mostrarButacaSeleccionada(butaca);
        controladorInformacionLateral.calcularSubtotal(butacasSeleccionadas, funcionSeleccionada);
    }

    public void quitarButacaDeseleccionada(Butaca butaca) {
        if (butaca == null || !butacasSeleccionadas.contains(butaca)) {
            return; // No quitar si la butaca es nula o no está seleccionada
        }
        butacasSeleccionadas.remove(butaca);
        controladorInformacionLateral.removerButacaDeLista(butaca);
        controladorInformacionLateral.calcularSubtotal(butacasSeleccionadas, funcionSeleccionada);
    }

}
