package com.cinemax.comun;

import com.cinemax.venta_boletos.Controladores.ControllerAlert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MetodosComunes {

    public static void mostrarVentanaEmergente(String titulo, String mensaje) {
        try {
            FXMLLoader loader = new FXMLLoader(MetodosComunes.class.getResource("/vistas/shared/alert-view.fxml"));
            Parent root = loader.load();

            ControllerAlert controller = loader.getController();
            controller.setData(titulo, mensaje);

            Stage alertStage = new Stage();
            alertStage.initStyle(StageStyle.TRANSPARENT);
            alertStage.initModality(Modality.APPLICATION_MODAL);

            // Establecer tamaño fijo (puedes ajustarlo a lo que necesites)
            double ancho = 400;
            double alto = 200;

            Scene scene = new Scene(root, ancho, alto);
            scene.setFill(null);

            ApuntadorTema.getInstance().applyTheme(scene);

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
