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
    
    /**
     * Métodos específicos para casos comunes
     */
    public static void mostrarRegistroExitoso(String entidad) {
        mostrarVentanaExito("¡" + entidad + " registrado exitosamente!");
    }
    
    public static void mostrarActualizacionExitosa(String entidad) {
        mostrarVentanaExito("¡" + entidad + " actualizado exitosamente!");
    }
    
    public static void mostrarEliminacionExitosa(String entidad) {
        mostrarVentanaExito("¡" + entidad + " eliminado exitosamente!");
    }
    
    public static void mostrarErrorCamposVacios() {
        mostrarVentanaError("Por favor, complete todos los campos obligatorios.");
    }
    
    public static void mostrarErrorBaseDatos() {
        mostrarVentanaError("Error al conectar con la base de datos. Intente nuevamente.");
    }
    
    public static void mostrarAdvertenciaEliminacion(String entidad) {
        mostrarVentanaAdvertencia("¿Está seguro de que desea eliminar " + entidad + "? Esta acción no se puede deshacer.");
    }
    
    public static void mostrarInformacion(String mensaje) {
        mostrarVentanaEmergente("Información", mensaje, "/vistas/comun/VistaExito.fxml");
    }

    private static void mostrarVentanaEmergente(String titulo, String mensaje, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(ManejadorMetodosComunes.class.getResource(fxmlPath));
            Parent root = loader.load();

            ControladorAlertas controller = loader.getController();
            controller.setData(titulo, mensaje);

            Stage alertStage = new Stage();
            alertStage.initStyle(StageStyle.UNDECORATED); // Sin decoraciones pero no transparente
            alertStage.initModality(Modality.APPLICATION_MODAL);

            // Establecer tamaño fijo
            double ancho = 420;
            double alto = 220;

            Scene scene = new Scene(root, ancho, alto);
            
            // Aplicar estilos CSS: primero general, luego específico para mayor precedencia
            String generalCssPath = ManejadorMetodosComunes.class.getResource("/temas/styles.css").toExternalForm();
            String alertasCssPath = ManejadorMetodosComunes.class.getResource("/Vista/comun/estilos-alertas.css").toExternalForm();
            scene.getStylesheets().clear(); // Limpiar cualquier CSS previo
            scene.getStylesheets().add(generalCssPath);
            scene.getStylesheets().add(alertasCssPath); // Este se carga último y tiene prioridad

            alertStage.setScene(scene);
            alertStage.setWidth(ancho);
            alertStage.setHeight(alto);
            alertStage.setResizable(false);
            alertStage.centerOnScreen(); // Centrar en pantalla
            alertStage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error al cargar la ventana emergente: " + e.getMessage());
            e.printStackTrace();
            // Fallback: mostrar un diálogo simple si falla la carga del FXML
            mostrarDialogoSimple(titulo, mensaje);
        }
    }
    
    /**
     * Método de respaldo para mostrar un diálogo simple si falla la carga del FXML
     */
    private static void mostrarDialogoSimple(String titulo, String mensaje) {
        try {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error crítico al mostrar mensaje: " + titulo + " - " + mensaje);
        }
    }
}

