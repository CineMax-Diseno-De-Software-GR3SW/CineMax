package com.cinemax.venta_boletos.Controladores;

import java.io.IOException;
import java.util.List;

import com.cinemax.comun.ControladorCargaConDatos;
import com.cinemax.comun.ResultadoCarga;
import com.cinemax.peliculas.modelos.entidades.Funcion;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
     * NUEVO MÉTODO OPTIMIZADO - Implementación asíncrona real.
     * 
     * Este método se ejecuta en un hilo de background y hace toda la carga pesada:
     * 1. Carga el archivo FXML
     * 2. Obtiene el controlador
     * 3. Inicializa los datos (incluyendo cualquier procesamiento complejo)
     * 4. Retorna todo encapsulado en un ResultadoCarga
     * 
     * ¡IMPORTANTE! Este método NO debe tocar la UI directamente.
     */
    @Override
    public ResultadoCarga cargarVistaPasandoDatos() throws IOException {
        try {
            // 1. Cargar FXML en background (la parte más pesada)
            FXMLLoader loader = new FXMLLoader(getClass().getResource(getRutaFXML()));
            Parent root = loader.load();
            
            // 2. Obtener controlador
            ControladorAsignadorButacas controller = loader.getController();
            
            // 3. Inicializar datos en background
            Funcion funcion = (Funcion) getDatosTransferencia().get(0);
            
            // Aquí ocurre el procesamiento pesado y se hace EN PARALELO con la animación de la barra
            controller.inicializarDatos(funcion);

            // Si hubiera más procesamiento pesado, se haría aquí: carga de datos de base de datos, procesar listas grandes, inicializar componentes complejos, etc.

            // 4. Retornar resultado encapsulado
            return new ResultadoCarga(root, controller, "CineMAX");
            
        } catch (Exception e) {
            // Error durante la carga - será manejado por el sistema de carga
            return new ResultadoCarga("Error cargando vista de butacas: " + e.getMessage());
        }
    }
}
