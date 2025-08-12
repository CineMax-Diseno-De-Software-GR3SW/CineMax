package com.cinemax.utilidades;

import java.io.IOException;
import java.util.List;

import javafx.stage.Stage;

/**
 * Clase abstracta que define el contrato para controladores que cargan vistas 
 * con transferencia de datos.
 * 
 * Esta clase proporciona la estructura base para controladores que necesitan:
 * 1. Cargar una vista específica desde un archivo FXML
 * 2. Mantener referencia a la ventana actual
 * 3. Transferir datos entre ventanas durante la navegación
 * 
 * Se utiliza en conjunto con ControladorCarga para crear pantallas de carga
 * que al finalizar ejecutan la lógica personalizada definida en las clases hijas.
 * 
 * @author GR3SW
 * @version 1.0
 */
public abstract class ControladorCargaConDatos {

    private String rutaFXML;
    
    /**
     * Referencia a la ventana (Stage) actual de JavaFX.
     * Se utiliza para cerrar la ventana actual y/o para posicionar la nueva ventana.
     */
    private Stage currentStage;
    
    /**
     * Lista genérica de objetos que contiene los datos a transferir entre ventanas.
     * El uso de List<Object> permite flexibilidad para pasar diferentes tipos de datos,
     * pero requiere casting apropiado en las clases implementadoras.
     */
    private List<Object> datosTransferencia;

    public ControladorCargaConDatos(String rutaFXML, Stage currentStage, List<Object> datosTransferencia) {
        // Asignar la ruta del archivo FXML de destino
        this.rutaFXML = rutaFXML;
        // Guardar referencia a la ventana actual
        this.currentStage = currentStage;
        // Almacenar los datos que se transferirán a la nueva vista
        this.datosTransferencia = datosTransferencia;
    }

    public String getRutaFXML() {
        return rutaFXML;
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    /**
     * Obtiene la lista de datos configurados para la transferencia.
     * 
     * Las clases hijas pueden usar este método para acceder a los datos
     * que necesitan pasar al controlador de la nueva vista. Es responsabilidad
     * de la clase implementadora realizar el casting apropiado de los objetos.
     * 
     * @return List<Object> con los datos a transferir entre ventanas
     */
    public List<Object> getDatosTransferencia() {
        return datosTransferencia;
    }

    /**
     * Versión asíncrona que se ejecuta en paralelo.
     * 
     * Este método debe realizar toda la carga pesada (FXML, datos, inicializaciones)
     * y retornar un ResultadoCarga que contiene todo lo necesario para cambiar la ventana.
     * 
     * Este método se ejecuta en un hilo de background, NO en el hilo de JavaFX.
     * No debe hacer cambios directos a la UI.
     * 
     * @return ResultadoCarga con toda la información necesaria para cambiar la ventana
     * @throws IOException Si ocurre un error durante la carga
     */
    public abstract ResultadoCarga cargarVistaPasandoDatos() throws IOException;

}
