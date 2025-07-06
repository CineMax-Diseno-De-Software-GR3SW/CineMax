package com.cinemax.empleados;

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



    public static void main(String[] args) {
        // You can add any pre-launch logic here if needed
        launch();
    }
}
