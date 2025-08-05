package com.cinemax.venta_boletos.Controladores;

import java.io.IOException;
import java.util.List;

import com.cinemax.comun.ControladorCargaConDatos;
import com.cinemax.peliculas.modelos.entidades.Funcion;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ControladorCargaAsignacionButacas extends ControladorCargaConDatos {

    public ControladorCargaAsignacionButacas(String rutaFXML, Stage currentStage, List<Object> datosTransferencia) {
        super(rutaFXML, currentStage, datosTransferencia);
    }

    @Override
    public void cargarVistaPasandoDatos() throws IOException {
        // Cargar la vista de butacas
        FXMLLoader loader = new FXMLLoader(getClass().getResource(getRutaFXML()));
        Parent root = loader.load();
        // Obtener el controlador e inicializar datos
        ControladorAsignadorButacas controller = loader.getController();
        controller.inicializarDatos((Funcion) getDatosTransferencia().get(0));
        // Cambiar a la nueva escena
        Stage stage = (Stage) getCurrentStage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("CineMAX");
    }
}
