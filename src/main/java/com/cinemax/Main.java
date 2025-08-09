package com.cinemax;

import java.io.IOException;

import com.cinemax.comun.conexiones.ConexionBaseSingleton;
import com.cinemax.comun.conexiones.ConexionFirebaseStorage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {


    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/vistas/empleados/PantallaLogin.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Image icon = new Image(Main.class.getResourceAsStream("/imagenes/logo.png"));
        stage.getIcons().add(icon);

        // https://cdn-icons-png.flaticon.com/512/44/44460.png

        stage.setTitle("CineMax");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void stop() {
        // Aquí cierras la conexión
        System.out.println("Cerrando conexion");
        ConexionBaseSingleton.getInstancia().cerrar();
        //ConexionFirebaseStorage.getInstancia().cerrar();
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
            System.out.println("¡Conexión exitosa Postgresql!");
            } catch (Exception e) {
                System.err.println("No se encontró el driver PostgreSQL: " + e.getMessage());
            }

            try {
                ConexionFirebaseStorage.getInstancia();
            } catch (Exception e) {
                throw new RuntimeException("Error al inicializar Firebase Storage: " + e.getMessage(), e);
            }
            System.out.println("¡Conexión exitosa Firebase Storage!");
        launch();
    }
}
