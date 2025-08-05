package com.cinemax.comun;

import java.io.IOException;
import java.util.List;

import javafx.stage.Stage;

public abstract class ControladorCargaConDatos {

    private String rutaFXML;
    private Stage currentStage;
    private List<Object> datosTransferencia;

    public ControladorCargaConDatos(String rutaFXML, Stage currentStage, List<Object> datosTransferencia) {
        this.rutaFXML = rutaFXML;
        this.currentStage = currentStage;
        this.datosTransferencia = datosTransferencia;
    }

    public String getRutaFXML() {
        return rutaFXML;
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public List<Object> getDatosTransferencia() {
        return datosTransferencia;
    }

    public abstract void cargarVistaPasandoDatos() throws IOException;

}
