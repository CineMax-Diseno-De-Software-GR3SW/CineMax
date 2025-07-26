package com.cinemax.comun;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ManejadorMetodosComunes {

     public static void cambiarVentana(Stage currentStage, String rutaFXML, String titulo) {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(ManejadorMetodosComunes.class.getResource(rutaFXML));
            Parent root = loader.load();

            // Cambiar la escena del Stage actual
            currentStage.setScene(new Scene(root));
            currentStage.setTitle(titulo);

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

            // Cambiar la escena del Stage actual
            currentStage.setScene(new Scene(root));

        } catch (IOException e) {
            mostrarVentanaError("No se pudo cargar la interfaz de usuario.");
            e.printStackTrace();
        }
    }


    public static void mostrarVentanaExito(String mensaje) {
        mostrarVentanaEmergente("Éxito", mensaje, "/Vista/comun/VistaExito.fxml");
    }

    public static void mostrarVentanaError(String mensaje) {
        mostrarVentanaEmergente("Error", mensaje, "/Vista/comun/VistaError.fxml");
    }

    public static void mostrarVentanaAdvertencia(String mensaje) {
        mostrarVentanaEmergente("Advertencia", mensaje, "/Vista/comun/VistaAdvertencia.fxml");
    }

    // Método para validar campos vacíos
    public static boolean validarCampoObligatorio(String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            mostrarVentanaError("El campo '" + nombreCampo + "' es obligatorio.");
            return false;
        }
        return true;
    }

    // Método para validar números
    public static boolean validarNumero(String valor, String nombreCampo) {
        try {
            Integer.parseInt(valor.trim());
            return true;
        } catch (NumberFormatException e) {
            mostrarVentanaError("El campo '" + nombreCampo + "' debe ser un número válido.");
            return false;
        }
    }

    // Método para mostrar confirmación exitosa de operación
    public static void mostrarOperacionExitosa(String operacion, String detalle) {
        mostrarVentanaExito(operacion + " realizada exitosamente.\n" + detalle);
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
            
            // Aplicar estilos CSS: primero general, luego específico para mayor precedencia
            String generalCssPath = ManejadorMetodosComunes.class.getResource("/temas/styles.css").toExternalForm();
            String alertasCssPath = ManejadorMetodosComunes.class.getResource("/Vista/comun/estilos-alertas.css").toExternalForm();
            scene.getStylesheets().clear(); // Limpiar cualquier CSS previo
            scene.getStylesheets().add(generalCssPath);
            scene.getStylesheets().add(alertasCssPath); // Este se carga último y tiene prioridad

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