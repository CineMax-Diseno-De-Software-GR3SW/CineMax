package com.cinemax.peliculas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.cinemax.comun.modelos.persistencia.ConexionBaseSingleton;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/Vista/Peliculas/PantallaGestionPeliculas.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 650);

        stage.setTitle("CineMax - Gestión de Películas");
        stage.setScene(scene);
        stage.show();
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
