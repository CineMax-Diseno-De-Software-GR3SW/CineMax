package com.cinemax.venta_boletos.Controladores;

import java.util.ArrayList;
import java.util.List;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.controladores.ControladorDeConsultaSalas;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.EstadoButaca;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.modelos.entidades.TipoSala;
import com.cinemax.salas.servicios.ButacaService;
import com.cinemax.venta_boletos.Modelos.Persistencia.BoletoDAO;

// imports para manejar el mapa de butacas
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    private VBox boletosContainer;

    @FXML
    private Button buttonVolver;

    @FXML
    private Button continueButton;

    @FXML
    private VBox funcionInfoContainer;

    @FXML
    private HBox headerBar;

    @FXML
    private ImageView imagenPelicula;

    @FXML
    private Label labelFechaFuncion;

    @FXML
    private Label labelFormato;

    @FXML
    private Label labelGeneroPelicula;

    @FXML
    private Label labelHoraFuncion;

    @FXML
    private Label labelLugarSala;

    @FXML
    private Label labelNombrePelicula;

    @FXML
    private Label labelTipoEstreno;

    @FXML
    private Label labelTipoSala;

    @FXML
    private VBox listaBoletos;

    @FXML
    private VBox mapaButacasContainer;

    @FXML
    private ScrollPane scrollBoletos;

    @FXML
    private Label totalLabel;

    // ------Atributos------
    private ControladorDeConsultaSalas controladorConsultaSalas;
    private List<Butaca> butacasSeleccionadas;
    private BoletoDAO boletoDAO;
    private List<Butaca> butacasOcupadas;
    private int cantidad;
    private ButacaService butacaService;

    public ControladorAsignadorButacas() {
        boletoDAO = new BoletoDAO();
        butacasOcupadas = new ArrayList<>();
        butacasSeleccionadas = new ArrayList<>();
        butacaService = new ButacaService();
        cantidad = 1;
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
    

    @FXML
    void onBackAction(ActionEvent event) {
        ManejadorMetodosComunes.cambiarVentana((Stage) buttonVolver.getScene().getWindow(), "/vistas/venta_boletos/funciones-view.fxml", "Cartelera");
    }

    @FXML
    void onContinuarAction(ActionEvent event) {
        ManejadorMetodosComunes.cambiarVentana((Stage) continueButton.getScene().getWindow(), "/vistas/venta_boletos/datos-cliente-view.fxml", "Resumen");
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

        colocarInformacionFuncion(funcionSeleccionada);

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
    }

    private void colocarInformacionFuncion(Funcion funcionSeleccionada) {
        labelNombrePelicula.setText(funcionSeleccionada.getPelicula() != null ? funcionSeleccionada.getPelicula().getTitulo() : "Título no disponible");
        labelGeneroPelicula.setText(funcionSeleccionada.getPelicula() != null && funcionSeleccionada.getPelicula().getGenero() != null ? funcionSeleccionada.getPelicula().getGenero() : "Género no disponible");
        labelLugarSala.setText(funcionSeleccionada.getSala() != null ? funcionSeleccionada.getSala().getNombre() : "Sala no disponible");
        labelFechaFuncion.setText(funcionSeleccionada.getFechaHoraInicio() != null ? funcionSeleccionada.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Fecha no disponible");
        labelHoraFuncion.setText(funcionSeleccionada.getFechaHoraInicio() != null ? funcionSeleccionada.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")) : "Hora no disponible");
        labelFormato.setText(funcionSeleccionada.getFormato() != null ? funcionSeleccionada.getFormato().name().replace("_", " ") : "Formato no disponible");
        imagenPelicula.setImage(new Image(funcionSeleccionada.getPelicula() != null && funcionSeleccionada.getPelicula().getImagenUrl() != null ? funcionSeleccionada.getPelicula().getImagenUrl() : "/images/no-image.png"));
        labelTipoEstreno.setText(funcionSeleccionada.getTipoEstreno() != null ? funcionSeleccionada.getTipoEstreno().name().replace("_", " ") : "Tipo de estreno no disponible");
    }

    public void agregarButacaSeleccionada(Butaca butaca) {
        if (butaca == null || butacasSeleccionadas.contains(butaca)) {
            return; // No agregar si la butaca es nula o ya está seleccionada
        }
        butacasSeleccionadas.add(butaca);
        mostrarButacaSeleccionada(butaca);
        calcularTotalBoletos();
    }

    private void calcularTotalBoletos() { 
        totalLabel.setText(String.format("%.2f", TipoSala.NORMAL.getMultiplicador() * butacasSeleccionadas.size()));
    }

    public void quitarButacaDeseleccionada(Butaca butaca) {
        if (butaca == null || !butacasSeleccionadas.contains(butaca)) {
            return; // No quitar si la butaca es nula o no está seleccionada
        }
        butacasSeleccionadas.remove(butaca);
        removerButacaDeLista(butaca);
        calcularTotalBoletos();
    }

    // Método para mostrar la butaca seleccionada en la lista de boletos
    private void mostrarButacaSeleccionada(Butaca butaca) {
        // Crear el contenedor principal como un rectángulo
        HBox butacaItem = new HBox();
        butacaItem.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        butacaItem.setSpacing(10);
        butacaItem.setPrefHeight(40);
        butacaItem.setStyle(
            "-fx-background-color: #e8f4f8; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #b3d9e6; " +
            "-fx-border-radius: 8; " +
            "-fx-border-width: 1; " +
            "-fx-padding: 8 12 8 12;"
        );
        
        // Label de cantidad
        Label labelCantidad = new Label(String.valueOf(cantidad++));
        labelCantidad.setStyle(
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-text-fill: #2c5aa0; " +
            "-fx-min-width: 10px;"
        );
        
        // Separador visual
        Region spacer = new Region();
        spacer.setPrefWidth(5);
        spacer.setStyle("-fx-background-color: #b3d9e6; -fx-pref-height: 20px; -fx-max-width: 1px;");

        // Label con el código alfanumérico en la parte izquierda
        Label labelButaca = new Label(butaca.getFila().toUpperCase() + butaca.getColumna());
        labelButaca.setStyle(
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-text-fill: #2c5aa0; " +
            "-fx-min-width: 50px;"
        );

        // Agregar todos los elementos al contenedor
        butacaItem.getChildren().addAll(labelCantidad,spacer,labelButaca);
        
        // Asignar un ID único para poder encontrar y eliminar el elemento después
        butacaItem.setId("butaca-" + butaca.getId());
        
        // Agregar al contenedor de boletos
        listaBoletos.getChildren().add(butacaItem);
    }

    private void removerButacaDeLista(Butaca butaca) {
        // Remover el elemento con el ID correspondiente
        listaBoletos.getChildren().removeIf(node ->
            ("butaca-" + butaca.getId()).equals(node.getId()));
        cantidad--;
    }
}
