package com.cinemax.comun;

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

    /**
     * Ruta del archivo FXML que contiene la definición de la vista a cargar.
     * Esta ruta es relativa al directorio de recursos de la aplicación.
     */
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

    /**
     * Constructor que inicializa todos los componentes necesarios para la carga con datos.
     * 
     * Este constructor debe ser llamado por todas las clases hijas para establecer
     * los parámetros básicos necesarios para la navegación y transferencia de datos.
     * 
     * @param rutaFXML Ruta relativa al archivo FXML de la vista de destino
     * @param currentStage Ventana actual que será reemplazada o cerrada
     * @param datosTransferencia Lista de objetos con los datos a pasar a la nueva vista
     */
    public ControladorCargaConDatos(String rutaFXML, Stage currentStage, List<Object> datosTransferencia) {
        // Asignar la ruta del archivo FXML de destino
        this.rutaFXML = rutaFXML;
        // Guardar referencia a la ventana actual
        this.currentStage = currentStage;
        // Almacenar los datos que se transferirán a la nueva vista
        this.datosTransferencia = datosTransferencia;
    }

    /**
     * Obtiene la ruta del archivo FXML configurado para esta instancia.
     * 
     * @return String con la ruta relativa del archivo FXML de la vista de destino
     */
    public String getRutaFXML() {
        return rutaFXML;
    }

    /**
     * Obtiene la referencia a la ventana actual.
     * 
     * Útil para las clases hijas que necesiten manipular la ventana actual,
     * como cerrarla, obtener su posición, o transferir propiedades a la nueva ventana.
     * 
     * @return Stage que representa la ventana actual de JavaFX
     */
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
     * Método abstracto que debe ser implementado por las clases hijas.
     * 
     * Este método define la lógica específica para cargar la vista de destino
     * y transferir los datos correspondientes. Cada implementación concreta debe:
     * 
     * 1. Cargar el archivo FXML especificado en rutaFXML
     * 2. Obtener el controlador de la nueva vista
     * 3. Transferir los datos usando datosTransferencia
     * 4. Configurar y mostrar la nueva ventana
     * 5. Cerrar o gestionar la ventana actual si es necesario
     * 
     * Este método es llamado por ControladorCarga cuando la barra de progreso
     * completa su carga, permitiendo una transición fluida entre vistas.
     * 
     * @throws IOException Si ocurre un error al cargar el archivo FXML o 
     *                     al acceder a los recursos necesarios
     */
    public abstract void cargarVistaPasandoDatos() throws IOException;

}
