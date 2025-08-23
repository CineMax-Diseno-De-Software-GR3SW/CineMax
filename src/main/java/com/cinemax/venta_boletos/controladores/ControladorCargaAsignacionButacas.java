package com.cinemax.venta_boletos.controladores;

import java.io.IOException;
import java.util.List;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.utilidades.ControladorCargaConDatos;
import com.cinemax.utilidades.ResultadoCarga;

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
 * @author GR3SW
 * @version 1.0
 */
public class ControladorCargaAsignacionButacas extends ControladorCargaConDatos {

    public ControladorCargaAsignacionButacas(String rutaFXML, Stage currentStage, List<Object> datosTransferencia) {
        super(rutaFXML, currentStage, datosTransferencia);
    }

    /**
     * Este método se ejecuta en un hilo de background y hace toda la carga pesada:
     * 1. Carga el archivo FXML
     * 2. Obtiene el controlador
     * 3. Inicializa los datos (incluyendo cualquier procesamiento complejo)
     * 4. Retorna todo encapsulado en un ResultadoCarga
     * 
     * Este método NO debe tocar la UI directamente.
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
            System.out.println("DEBUG - ControladorCargaAsignacionButacas - Size datos: " + getDatosTransferencia().size());
            
            //if (getDatosTransferencia().size() < 2) {
            //    throw new IllegalArgumentException("Se esperan al menos 2 elementos en datosTransferencia, pero se recibieron: " + getDatosTransferencia().size());
            //}
            
            Funcion funcion = (Funcion) getDatosTransferencia().get(0);

            if (getDatosTransferencia().size() > 1) {
                List<Butaca> butacasSeleccionadas = (List<Butaca>) getDatosTransferencia().get(1);
                controller.setButacasYaSeleccionadas(butacasSeleccionadas);
            }

            //// Aquí ocurre el procesamiento pesado y se hace EN PARALELO con la animación de la barra
            controller.inicializarDatos(funcion);

            // Si hubiera más procesamiento pesado, se haría aquí: carga de datos de base de datos, procesar listas grandes, inicializar componentes complejos, etc.

            // 4. Retornar resultado encapsulado
            return new ResultadoCarga(root, controller, "CineMax");
            
        } catch (Exception e) {
            // Error durante la carga - será manejado por el sistema de carga
            System.err.println("Error al cargar la vista de asignación de butacas: " + e.getMessage());
            e.printStackTrace();
            return new ResultadoCarga("Error cargando vista de butacas: " + e.getMessage());
        }
    }
}
