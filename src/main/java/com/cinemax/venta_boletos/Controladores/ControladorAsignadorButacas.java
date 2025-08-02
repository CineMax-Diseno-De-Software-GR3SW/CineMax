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
    private List<Butaca> butacasOcupadas;
    private Funcion funcionSeleccionada;
    
    private ButacaService butacaService;
    ControladorInformacionLateral controladorInformacionLateral;

    public ControladorAsignadorButacas() {
        boletoDAO = new BoletoDAO();
        butacasOcupadas = new ArrayList<>();
        butacasSeleccionadas = new ArrayList<>();
        butacaService = new ButacaService();
        butacaService = new ButacaService();
    }

    private void cargarMapaButacas(Sala sala) {
        try {
            // Cargar el FXML del mapa de butacas
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/vistas/salas/MapaButacas.fxml"));
            Parent mapaButacas = loader.load();
            
            // Agregar el mapa al contenedor
            mapaButacasContainer.getChildren().add(mapaButacas);
            
            // Obtener referencia al controlador del mapa
            controladorConsultaSalas = loader.getController();
            controladorConsultaSalas.setSala(sala); // Asignar la sala seleccionada
            controladorConsultaSalas.setControladorAsignadorButacas(this); // Pasar el controlador de asignación de butacas

        } catch (IOException e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al cargar el mapa de butacas: " + e.getMessage());
            e.printStackTrace();
        }
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
    

    @FXML
    void onBackAction(ActionEvent event) {
        // Regresar a la pantalla de funciones con la información de la película seleccionada
        
        Stage currentStage = (Stage) buttonContinuar.getScene().getWindow(); // ventana actual
        // Cambiar a la vista de funciones y pasar el título de la película seleccionada
        ControladorMostrarFunciones controladorFunciones = ManejadorMetodosComunes.cambiarVentanaConControlador(currentStage, "/vistas/venta_boletos/funciones-view.fxml", "funciones de " + funcionSeleccionada.getPelicula().getTitulo());
        controladorFunciones.setPelicula(funcionSeleccionada.getPelicula().getTitulo());
    }

    @FXML
    void onContinuarAction(ActionEvent event) {
        if(butacasSeleccionadas.isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Debe seleccionar al menos una butaca.");
            return;
        }

        try {
            // 1. Generar los boletos
            ServicioGeneradorBoleto servicioGeneradorBoleto = new ServicioGeneradorBoleto();
            List<Producto> boletosGenerados = servicioGeneradorBoleto.generarBoleto(funcionSeleccionada, butacasSeleccionadas);
            
            
            // 3. Cambiar la escena a la nueva vista con tamaño de pantalla completa
            Stage currentStage = (Stage) buttonContinuar.getScene().getWindow();
            ControladorFacturacion controladorFacturacion = ManejadorMetodosComunes.cambiarVentanaConControlador(currentStage, "/vistas/venta_boletos/datos-cliente-view.fxml", "Datos del Cliente");
            
            // 4. Inicializar el controlador de facturación con los datos necesarios
            controladorFacturacion.setControladorInformacionLateral(controladorInformacionLateral);
            controladorFacturacion.initData(boletosGenerados, 0, 0, 0);
            
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al confirmar: " + e.getMessage());
            System.err.println("Error al cargar la vista de datos del cliente: " + e.getMessage());
            e.printStackTrace();
        }

    }


    public List<String> asignarButacas(String controladorDeConsultaSalas, String funcion, List<String> butacasOcupadas, int totalBoletos) {
        /*
         * TODO: Buscar boletos en la BD usando la "función" para encontrar todos los
         * boletos asociados. De ahí, extraer en una estructura de datos, los códigos
         * alfanuméricos de las butacas asociadas a esos boletos. Esa estructura de
         * datos servirá para saber la cantidad de asientos disponibles y para mostrar
         * cuáles están ocupados en la pantalla de asignación de butacas.
         */
        //BoletoDAO boletoDAO = new BoletoDAO();
        //bo
        
        // iterar sobre el mapa de butacas de funcion buscando cuales coinciden con las ocupadas con respecto a su codigo alfanumérico
        // mostrar el mapa de butacas disponibles y ocupadas
        // seleccionar las butacas disponibles para asignar de acuerdo al total de boletos
        // devolver la lista de butacas asignadas


        List<String> butacas = new ArrayList<>();
        for (int i = 1; i <= totalBoletos; i++) butacas.add("F" + i);
        return butacas; // Retorna la lista de butacas asignadas
    }

    public void inicializarDatos(Funcion funcionSeleccionada) { 
        System.out.println("Inicializando datos en ControladorAsignadorButacas");

        labelTipoSala.setText(funcionSeleccionada.getSala().getTipo().name()); // encabezado

        cargarInformacionFuncion(funcionSeleccionada);

        try {
            butacasOcupadas = boletoDAO.listarButacasDeBoletosPorFuncion(funcionSeleccionada);
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al cargar las butacas ocupadas: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        List<Butaca> butacasDeSala;
        try {
            butacasDeSala = butacaService.listarButacasPorSala(funcionSeleccionada.getSala().getId());
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al cargar las butacas de la sala: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        for (Butaca butacaDeSala : butacasDeSala) {
            for (Butaca butacaOcupada : butacasOcupadas) {
                if (butacaDeSala.getId() == butacaOcupada.getId()) {
                    butacaDeSala.setEstado(EstadoButaca.OCUPADA.name());
                } 
                try {
                    butacaService.actualizarButaca(butacaDeSala);
                } catch (Exception e) {
                    ManejadorMetodosComunes.mostrarVentanaError("Error al actualizar el estado de la butaca: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        cargarMapaButacas(funcionSeleccionada.getSala());
        this.funcionSeleccionada = funcionSeleccionada;
        
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
