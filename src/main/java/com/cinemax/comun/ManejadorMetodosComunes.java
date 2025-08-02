package com.cinemax.comun;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ManejadorMetodosComunes {

     public static void cambiarVentana(Stage currentStage, String rutaFXML, String titulo) {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(ManejadorMetodosComunes.class.getResource(rutaFXML));
            Parent root = loader.load();

            // Obtener dimensiones de pantalla
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            // Configurar dimensiones antes de la escena
            currentStage.setX(screenBounds.getMinX());
            currentStage.setY(screenBounds.getMinY());
            currentStage.setWidth(screenBounds.getWidth());
            currentStage.setHeight(screenBounds.getHeight());
            

            currentStage.setTitle(titulo);

            // Cambiar la escena
            currentStage.setScene(new Scene(root));
            

            // Maximizar la ventana
            currentStage.setMaximized(true);

        } catch (IOException e) {
            mostrarVentanaError("No se pudo cargar la interfaz de usuario.");
            e.printStackTrace();
        }
    }

    public static void cambiarVentana(Stage currentStage, String rutaFXML) {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(ManejadorMetodosComunes.class.getResource(rutaFXML));
            Parent root = loader.load();

            // Obtener dimensiones de pantalla
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Configurar dimensiones antes de la escena
            currentStage.setX(screenBounds.getMinX());
            currentStage.setY(screenBounds.getMinY());
            currentStage.setWidth(screenBounds.getWidth());
            currentStage.setHeight(screenBounds.getHeight());
            
            // Cambiar la escena
            currentStage.setScene(new Scene(root));
            

            // Maximizar la ventana
            currentStage.setMaximized(true);

        } catch (IOException e) {
            mostrarVentanaError("No se pudo cargar la interfaz de usuario.");
            e.printStackTrace();
        }
    }

    // Método sobrecargado que permite acceso al controlador antes de mostrar la ventana
    public static <T> T cambiarVentanaConControlador(Stage currentStage, String rutaFXML, String titulo) {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(ManejadorMetodosComunes.class.getResource(rutaFXML));
            Parent root = loader.load();

            // Obtener el controlador para permitir configuración
            T controller = loader.getController();

            // Obtener dimensiones de pantalla
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Configurar dimensiones antes de la escena
            currentStage.setX(screenBounds.getMinX());
            currentStage.setY(screenBounds.getMinY());
            currentStage.setWidth(screenBounds.getWidth());
            currentStage.setHeight(screenBounds.getHeight());
            
            // Cambiar la escena
            currentStage.setScene(new Scene(root));
            currentStage.setTitle(titulo);
            currentStage.setMaximized(true); // Maximizar la ventana

            return controller; // Retorna el controlador para configuración adicional

        } catch (IOException e) {
            mostrarVentanaError("No se pudo cargar la interfaz de usuario.");
            e.printStackTrace();
            return null;
        }
    }


    public static void mostrarVentanaExito(String mensaje) {
        mostrarVentanaEmergente("Éxito", mensaje, "/vistas/comun/VistaExito.fxml");
    }

    public static void mostrarVentanaError(String mensaje) {
        mostrarVentanaEmergente("Error", mensaje, "/vistas/comun/VistaError.fxml");
    }

    public static void mostrarVentanaAdvertencia(String mensaje) {
        mostrarVentanaEmergente("Advertencia", mensaje, "/vistas/comun/VistaAdvertencia.fxml");
    }

    private static void mostrarVentanaEmergente(String titulo, String mensaje, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(ManejadorMetodosComunes.class.getResource(fxmlPath));
            Parent root = loader.load();

            ControladorAlertas controller = loader.getController();
            controller.setData(titulo, mensaje);

            Stage alertStage = new Stage();
            alertStage.initStyle(StageStyle.TRANSPARENT);
            alertStage.initModality(Modality.APPLICATION_MODAL);

            // Establecer tamaño fijo (puedes ajustarlo a lo que necesites)
            double ancho = 400;
            double alto = 200;

            Scene scene = new Scene(root, ancho, alto);
            scene.setFill(null);

            //ApuntadorTema.getInstance().applyTheme(scene);

            alertStage.setScene(scene);
            alertStage.setWidth(ancho);
            alertStage.setHeight(alto);
            alertStage.setResizable(false); // Evita que el usuario lo cambie
            alertStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
