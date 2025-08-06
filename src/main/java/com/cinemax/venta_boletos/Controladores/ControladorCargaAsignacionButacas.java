package com.cinemax.venta_boletos.controladores;

import java.io.IOException;
import java.util.List;

import com.cinemax.comun.ControladorCargaConDatos;
import com.cinemax.peliculas.modelos.entidades.Funcion;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controlador especializado para cargar la vista de asignación de butacas con datos.
 * 
 * 
 * Funcionalidad específica:
 * - Carga la vista VistaAsignadorButacas.fxml
 * - Transfiere la función seleccionada al controlador de destino
 * - Inicializa el mapa de butacas con los datos correctos
 * - Maneja la transición de ventana de forma fluida
 * 
 * Uso típico: Se utiliza cuando se navega desde la selección de funciones
 * hacia la selección de butacas con una pantalla de carga intermedia.
 * 
 * @author GR3SW
 * @version 1.0
 */
public class ControladorCargaAsignacionButacas extends ControladorCargaConDatos {

    /**
     * Constructor que inicializa el controlador con los datos necesarios para la carga.
     * 
     * Delega la inicialización básica a la clase padre ControladorCargaConDatos,
     * estableciendo la ruta FXML, ventana actual y datos a transferir.
     * 
     * @param rutaFXML Ruta del archivo FXML de la vista de asignación de butacas
     * @param currentStage Ventana actual que será reemplazada
     * @param datosTransferencia Lista que debe contener la Función seleccionada en posición 0
     */
    public ControladorCargaAsignacionButacas(String rutaFXML, Stage currentStage, List<Object> datosTransferencia) {
        super(rutaFXML, currentStage, datosTransferencia);
    }

    /**
     * Implementación específica del método abstracto de la clase padre.
     * 
     * Ejecuta la secuencia completa para cargar la vista de asignación de butacas:
     * 1. Carga el archivo FXML de la vista de butacas
     * 2. Obtiene el controlador asociado (ControladorAsignadorButacas)
     * 3. Transfiere los datos de la función al controlador de destino
     * 4. Inicializa el mapa de butacas y la interfaz
     * 5. Cambia la escena de la ventana actual
     * 
     * Este método es llamado automáticamente por ControladorCarga cuando
     * la barra de progreso completa su animación.
     * 
     * @throws IOException Si ocurre un error al cargar el archivo FXML
     */
    @Override
    public void cargarVistaPasandoDatos() throws IOException {
        // 1. Cargar la vista FXML del asignador de butacas
        FXMLLoader loader = new FXMLLoader(getClass().getResource(getRutaFXML()));
        Parent root = loader.load();
        
        // 2. Obtener referencia al controlador de la vista cargada
        ControladorAsignadorButacas controller = loader.getController();
        
        // 3. Transferir datos: extraer función de la lista (posición 0) y inicializar
        // Se asume que datosTransferencia[0] contiene la Función seleccionada
        controller.inicializarDatos((Funcion) getDatosTransferencia().get(0));
        
        // 4. Configurar y mostrar la nueva escena en la ventana actual
        Stage stage = getCurrentStage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("CineMAX");
    }
}
