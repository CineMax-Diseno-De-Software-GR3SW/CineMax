package com.cinemax.peliculas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

import com.cinemax.comun.modelos.persistencia.ConexionBaseSingleton;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Mostrar diálogo de selección de vista
        Alert seleccion = new Alert(Alert.AlertType.CONFIRMATION);
        seleccion.setTitle("CineMax - Selección de Vista");
        seleccion.setHeaderText("Sel eccione qué vista desea abrir:");
        seleccion.setContentText("Elija una de las siguientes opciones:");

        ButtonType btnPeliculas = new ButtonType("Gestión de Películas");
        ButtonType btnCartelera = new ButtonType("Gestión de Cartelera");
        ButtonType btnFunciones = new ButtonType("Gestión de Funciones");
        ButtonType btnSeleccionFuncion = new ButtonType("Selección de Función");
        ButtonType btnCancelar = new ButtonType("Cancelar");

        seleccion.getButtonTypes().setAll(btnPeliculas, btnCartelera, btnFunciones, btnSeleccionFuncion, btnCancelar);

        Optional<ButtonType> resultado = seleccion.showAndWait();

        if (resultado.isPresent()) {
            FXMLLoader fxmlLoader;
            String titulo;

            if (resultado.get() == btnPeliculas) {
                fxmlLoader = new FXMLLoader(Main.class.getResource("/Vista/Peliculas/PantallaGestionPeliculas.fxml"));
                titulo = "CineMax - Gestión de Películas";
            } else if (resultado.get() == btnCartelera) {
                fxmlLoader = new FXMLLoader(Main.class.getResource("/Vista/Peliculas/PantallaGestionCartelera.fxml"));
                titulo = "CineMax - Gestión de Cartelera";
            } else if (resultado.get() == btnFunciones) {
                fxmlLoader = new FXMLLoader(Main.class.getResource("/Vista/Peliculas/PantallaGestionFunciones.fxml"));
                titulo = "CineMax - Gestión de Funciones";
            } else if (resultado.get() == btnSeleccionFuncion) {
                fxmlLoader = new FXMLLoader(Main.class.getResource("/Vista/Peliculas/PantallaSeleccionFuncion.fxml"));
                titulo = "CineMax - Selección de Función";
            } else {
                // Usuario canceló
                return;
            }

            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 1200, 700);

            // Aplicar estilos base
            scene.getStylesheets().add(getClass().getResource("/temas/styles.css").toExternalForm());

            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.show();
        }
    }

    @Override
    public void stop() {
        // Aquí cierras la conexión
        System.out.println("Cerrando conexion");
        ConexionBaseSingleton.getInstancia().cerrar();
    }

    public static void main(String[] args) {
        try {
            // Inicializar la conexión a la base de datos
            ConexionBaseSingleton.getInstancia().getConexion();
            System.out.println("¡Conexión exitosa!");
        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
        
        // Lanzar la aplicación JavaFX
        launch();
    }
}
