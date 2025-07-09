package com.cinemax.empleados;

import com.cinemax.comun.ConexionBaseSingleton;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {


    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/Vista/empleados/PantallaLogin.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);


        stage.setTitle("CineMax - Empleados");
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
            /* Carga explícita del driver JDBC */
            Class.forName("org.postgresql.Driver");

            /* Ahora sí pedimos la conexión al Singleton */
            try {
                ConexionBaseSingleton.getInstancia().getConexion();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("¡Conexión exitosa!");
        } catch (Exception e) {
            System.err.println("No se encontró el driver PostgreSQL: " + e.getMessage());
        }
        // You can add any pre-launch logic here if needed
        launch();
    }
}
