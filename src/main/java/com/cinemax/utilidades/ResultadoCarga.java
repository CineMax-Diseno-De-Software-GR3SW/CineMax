package com.cinemax.comun;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase que encapsula el resultado de una operación de carga asíncrona.
 * 
 * Esta clase contiene toda la información necesaria para cambiar de ventana
 * una vez que la carga paralela ha terminado. Permite separar la lógica
 * de carga (que ocurre en background) de la aplicación de cambios UI
 * (que debe ocurrir en el hilo de JavaFX).
 * 
 * @author GR3SW
 * @version 1.0
 */
public class ResultadoCarga {
    
    private final Parent nuevaVista;
    private final String titulo;
    private final Object controlador;
    private final boolean exitoso;
    private final String mensajeError;

    /**
     * Constructor para carga exitosa.
     * 
     * @param nuevaVista La vista cargada lista para mostrar
     * @param controlador El controlador de la nueva vista (puede ser null)
     * @param titulo Título para la ventana
     */
    public ResultadoCarga(Parent nuevaVista, Object controlador, String titulo) {
        this.nuevaVista = nuevaVista;
        this.controlador = controlador;
        this.titulo = titulo != null ? titulo : "CineMAX";
        this.exitoso = true;
        this.mensajeError = null;
    }

    /**
     * Constructor para carga con error.
     * 
     * @param mensajeError Descripción del error ocurrido
     */
    public ResultadoCarga(String mensajeError) {
        this.nuevaVista = null;
        this.controlador = null;
        this.titulo = null;
        this.exitoso = false;
        this.mensajeError = mensajeError;
    }

    /**
     * Aplica el cambio de ventana usando el resultado de la carga.
     * 
     * Este método debe ser llamado desde el hilo de JavaFX.
     * 
     * @param stage La ventana donde aplicar los cambios
     */
    public void aplicarCambioDeVentana(Stage stage) {
        if (!exitoso) {
            ManejadorMetodosComunes.mostrarVentanaError(mensajeError);
            return;
        }

        try {
            // Aplicar la nueva vista ya cargada
            Scene scene = new Scene(nuevaVista);
            stage.setScene(scene);
            stage.setTitle(titulo);
            stage.setMaximized(true);
            
            // Si hay procesamiento adicional del controlador, hacerlo aquí
            if (controlador != null) {
                // Aquí puedes agregar lógica adicional si el controlador necesita
                // hacer algo específico después de que la ventana sea visible
            }
            
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error aplicando cambio de ventana: " + e.getMessage());
        }
    }

    // Getters
    public Parent getNuevaVista() {
        return nuevaVista;
    }

    public Object getControlador() {
        return controlador;
    }

    public String getTitulo() {
        return titulo;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public String getMensajeError() {
        return mensajeError;
    }
}