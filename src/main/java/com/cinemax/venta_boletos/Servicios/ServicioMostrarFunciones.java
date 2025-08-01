package com.cinemax.venta_boletos.Servicios;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.venta_boletos.Controladores.ControladorAsignadorButacas;
import com.cinemax.venta_boletos.Controladores.ControladorBoleto;
import com.cinemax.venta_boletos.Modelos.Persistencia.BoletoDAO;
import com.cinemax.peliculas.controladores.ControladorFunciones;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.modelos.entidades.TipoSala;

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
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServicioMostrarFunciones {

    private final ControladorFunciones controladorFunciones = new ControladorFunciones();
    private final BoletoDAO daoBoleto = new BoletoDAO();

    public void cargarFunciones(TableView<Funcion> tabla,
            TableColumn<Funcion, String> colHora,
            TableColumn<Funcion, String> colSala,
            TableColumn<Funcion, String> colFormato,
            TableColumn<Funcion, String> colTipoEstreno,
            TableColumn<Funcion, String> colPrecio,
            TableColumn<Funcion, String> colFecha,
            String nombrePelicula) {

        // Configurar columnas primero
        configurarColumnas(colHora, colSala, colFormato, colTipoEstreno, colPrecio, colFecha);

        try {
            List<Funcion> funcionesObtenidas = controladorFunciones.obtenerFuncionesPorNombrePelicula(nombrePelicula);

            // Debug detallado
            System.out.println("\n=== DEBUG: Funciones obtenidas ===");
            funcionesObtenidas.forEach(f -> {
                System.out.println(
                        "ID: " + f.getId() +
                                " | Película: " + (f.getPelicula() != null ? f.getPelicula().getTitulo() : "null") +
                                " | Sala: " + (f.getSala() != null ? f.getSala().getNombre() : "null") +
                                " | Fecha: " + f.getFechaHoraInicio());
            });

            ObservableList<Funcion> listaFunciones = FXCollections.observableArrayList(funcionesObtenidas);

            Platform.runLater(() -> {
                tabla.setItems(listaFunciones);
                System.out.println("Funciones cargadas en tabla: " + listaFunciones.size());

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

    private void configurarColumnas(TableColumn<Funcion, String> colHora,
            TableColumn<Funcion, String> colSala,
            TableColumn<Funcion, String> colFormato,
            TableColumn<Funcion, String> colTipoEstreno,
            TableColumn<Funcion, String> colPrecio,
            TableColumn<Funcion, String> colFecha) {

        colHora.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFechaHoraInicio() != null
                        ? cellData.getValue().getFechaHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm"))
                        : ""));

        colSala.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSala() != null ? cellData.getValue().getSala().getNombre()
                        : "Sala no disponible"));

        colFormato.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFormato() != null ? cellData.getValue().getFormato().name().replace("_", " ")
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
                        ? cellData.getValue().getFechaHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : ""));
    }

    public void regresarPantallaCartelera(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/cartelera-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.centerOnScreen();
        } catch (IOException e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al regresar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void confirmarFuncion(TableView<Funcion> tabla, String pelicula) {
        Funcion funcionSeleccionada = tabla.getSelectionModel().getSelectedItem();

        if (funcionSeleccionada == null) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Seleccione una función primero");
            return;
        }

        try {
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/boleto-view.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/VistaSeleccionButacas.fxml"));
            Parent root = loader.load();

            ControladorBoleto controller = loader.getController();
            //ControladorAsignadorButacas controladorAsignadorButacas = loader.getController();
            //String funcionTexto = String.format("%s-%s-%s-%s",
            //        seleccion.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")),
            //        seleccion.getSala() != null ? seleccion.getSala().getNombre() : "Sala no disponible",
            //        seleccion.getFormato() != null ? seleccion.getFormato().name().replace("_", " ") : "",
            //
            
            controller.inicializarInformacion(funcionSeleccionada);
            
            //Funcion funcionEnSalaVIP = daoBoleto.listarFuncionPorTipoDeSala(seleccion, TipoSala.VIP);
            //Funcion funcionEnSalaNormal = daoBoleto.listarFuncionPorTipoDeSala(seleccion, TipoSala.NORMAL);


            //controller.initData(pelicula, funcionTexto);
            //System.out.println("Confirmando función: " + funcionTexto);
            //controller.initData(pelicula, funcionTexto, funcionSeleccionada, null, null);

            Stage stage = (Stage) tabla.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.centerOnScreen();

        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al confirmar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
