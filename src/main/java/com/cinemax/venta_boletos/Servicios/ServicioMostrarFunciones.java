package com.cinemax.venta_boletos.Servicios;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.venta_boletos.Controladores.ControladorBoleto;
import com.cinemax.peliculas.controladores.ControladorFunciones;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.TipoEstreno;
import com.cinemax.salas.modelos.entidades.Sala;

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
import java.util.Arrays;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServicioMostrarFunciones {

    private final ControladorFunciones controladorFunciones = new ControladorFunciones();

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

    // DATOS QUEMADOS PARA PRUEBAS
    List<Funcion> funcionesQuemadas = Arrays.asList(
        new Funcion(1, LocalDateTime.now().plusHours(2), // ID y fecha+hora
        new Funcion(2, LocalDateTime.now().plusHours(4)),
        new Funcion(3, LocalDateTime.now().plusHours(6))
    );

    // Configurar datos quemados
    funcionesQuemadas.get(0).setFormato(FormatoPelicula._3D);
    funcionesQuemadas.get(0).setTipoEstreno(TipoEstreno.ESTRENO);
    funcionesQuemadas.get(0).setPrecioBase(new BigDecimal("12.50"));
    
    funcionesQuemadas.get(1).setFormato(FormatoPelicula._2D);
    funcionesQuemadas.get(1).setTipoEstreno(TipoEstreno.NORMAL);
    funcionesQuemadas.get(1).setPrecioBase(new BigDecimal("9.00"));
    
    funcionesQuemadas.get(2).setFormato(FormatoPelicula._4DX);
    funcionesQuemadas.get(2).setTipoEstreno(TipoEstreno.PREESTRENO);
    funcionesQuemadas.get(2).setPrecioBase(new BigDecimal("15.00"));

    // Simular sala
    Sala salaPrueba = new Sala();
    salaPrueba.setNombre("Sala Premium");
    funcionesQuemadas.forEach(f -> f.setSala(salaPrueba));

    System.out.println("=== USANDO DATOS QUEMADOS PARA PRUEBAS ===");
    System.out.println("Total funciones quemadas: " + funcionesQuemadas.size());

    ObservableList<Funcion> listaFunciones = FXCollections.observableArrayList(funcionesQuemadas);

    Platform.runLater(() -> {
        tabla.setItems(listaFunciones);
        System.out.println("Funciones quemadas cargadas en tabla: " + listaFunciones.size());
        
        if (listaFunciones.isEmpty()) {
            tabla.setPlaceholder(new Label("No se cargaron funciones (pero deberían estar las quemadas)"));
        } else {
            System.out.println("Contenido de la tabla:");
            tabla.getItems().forEach(f -> System.out.println(
                "Hora: " + f.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")) + 
                " | Sala: " + f.getSala().getNombre()));
        }
    });
}

    private void configurarColumnas(TableColumn<Funcion, String> colHora,
            TableColumn<Funcion, String> colSala,
            TableColumn<Funcion, String> colFormato,
            TableColumn<Funcion, String> colTipoEstreno,
            TableColumn<Funcion, String> colPrecio,
            TableColumn<Funcion, String> colFecha) {
        // Configuración de columnas (igual que antes)
        colHora.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFechaHoraInicio() != null
                        ? cellData.getValue().getFechaHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm"))
                        : ""));

        colSala.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSala() != null ? cellData.getValue().getSala().getNombre()
                        : "Sala no disponible"));

        // ... (resto de configuraciones de columnas igual que antes)
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
        Funcion seleccion = tabla.getSelectionModel().getSelectedItem();

        if (seleccion == null) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Seleccione una función primero");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/boleto-view.fxml"));
            Parent root = loader.load();

            ControladorBoleto controller = loader.getController();
            String funcionTexto = String.format("%s - %s (%s, %s)",
                    seleccion.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")),
                    seleccion.getSala() != null ? seleccion.getSala().getNombre() : "Sala no disponible",
                    seleccion.getFormato() != null ? seleccion.getFormato().name().replace("_", " ") : "",
                    seleccion.getTipoEstreno() != null ? seleccion.getTipoEstreno().name().replace("_", " ") : "");

            controller.initData(pelicula, funcionTexto);

            Stage stage = (Stage) tabla.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.centerOnScreen();

        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al confirmar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
