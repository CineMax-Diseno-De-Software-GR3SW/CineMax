package com.cinemax.comun;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la pantalla de carga
 *
 * Esta clase maneja la lógica de una pantalla de carga que muestra un progress bar
 * mientras se ejecuta una tarea en segundo plano. Permite dos tipos de carga:
 * 1. Carga simple que navega a otra ventana al finalizar
 * 2. Carga con datos que ejecuta un controlador específico al finalizar
 * 
 * @author GR3SW
 * @version 1.0
 */
public class ControladorCarga implements Initializable {
    /**
     * Elemento de la interfaz que representa la barra de progreso de carga.
     * Se inyecta automáticamente desde el archivo FXML correspondiente.
     */
    @FXML
    private ProgressBar progressBar;

    /**
     * Constructor público sin argumentos requerido por JavaFX.
     * Este constructor es necesario para que JavaFX pueda instanciar el controlador
     * automáticamente cuando se carga el archivo FXML.
     */
    public ControladorCarga() {
        // Constructor público sin argumentos
    }
    /**
     * Método de inicialización que se ejecuta automáticamente después de cargar el FXML.
     * Configura el estado inicial de la barra de progreso en 0%.
     * 
     * @param location URL del archivo FXML (no utilizada en esta implementación)
     * @param resources ResourceBundle para internacionalización (no utilizado)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar el progress bar en 0% al cargar la vista
        progressBar.setProgress(0.0);
    } 
    
    /**
     * Inicia el proceso de carga simple que navega a otra ventana al finalizar.
     * 
     * Crea una tarea en segundo plano que simula un proceso de carga mediante
     * incrementos en la barra de progreso. Al completarse, navega automáticamente
     * a la ventana especificada.
     * 
     * @param stage La ventana actual de JavaFX que se cerrará tras la carga
     * @param rutaFXMLSiguienteVentana Ruta relativa del archivo FXML de la siguiente ventana
     * @param saltosEnElProgreso Número de pasos/incrementos en la barra de progreso
     * @param tiempoPorSalto Tiempo en milisegundos que dura cada paso de progreso
     */
    public void iniciarCarga(Stage stage, String rutaFXMLSiguienteVentana, int saltosEnElProgreso, int tiempoPorSalto) {
        // Crear una tarea en segundo plano para simular la carga sin bloquear la UI
        Task<Void> tareaCargar = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                
                int totalPasos = saltosEnElProgreso;
                
                // Simular progreso paso a paso
                for (int i = 0; i < totalPasos; i++) {
                    
                    // Simular tiempo de procesamiento en cada paso
                    Thread.sleep(tiempoPorSalto);

                    // Calcular el progreso como porcentaje (0.0 a 1.0)
                    final double progreso = (double) (i + 1) / totalPasos;
                    
                    // Actualizar la UI en el hilo principal de JavaFX
                    Platform.runLater(() -> {
                        progressBar.setProgress(progreso);
                    });
                }
                
                return null;
            }
            
            @Override
            protected void succeeded() {
                // Ejecutar cuando la tarea se complete exitosamente
                Platform.runLater(() -> {
                    // Cambiar a la siguiente ventana usando el método común
                    ManejadorMetodosComunes.cambiarVentana(stage, rutaFXMLSiguienteVentana);
                });
            }
            
            @Override
            protected void failed() {
                // Ejecutar en caso de error durante la tarea
                Platform.runLater(() -> {
                    // Mostrar ventana de error y resetear la barra de progreso
                    ManejadorMetodosComunes.mostrarVentanaError("Error durante la carga: ");
                    progressBar.setProgress(0.0);
                });
            }
        };
        
        // Crear y configurar el hilo para ejecutar la tarea
        Thread hiloCargar = new Thread(tareaCargar);
        hiloCargar.setDaemon(true); // El hilo se cierra automáticamente al cerrar la aplicación
        hiloCargar.start();
    }

    /**
     * Inicia el proceso de carga con controlador personalizado.
     * 
     * Esta versión sobrecargada del método permite ejecutar un controlador específico
     * al finalizar la carga, en lugar de simplemente navegar a otra ventana.
     * Útil cuando se necesita pasar datos o ejecutar lógica compleja tras la carga.
     * 
     * @param stage La ventana actual de JavaFX
     * @param controladorCargaConDatos Controlador personalizado que implementa ControladorCargaConDatos
     * @param saltosEnElProgreso Número de pasos/incrementos en la barra de progreso
     * @param tiempoPorSalto Tiempo en milisegundos que dura cada paso de progreso
     */
    public void iniciarCarga(Stage stage, ControladorCargaConDatos controladorCargaConDatos, int saltosEnElProgreso, int tiempoPorSalto) {
        // Crear tarea en segundo plano similar al método anterior
        Task<Void> tareaCargar = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int totalPasos = saltosEnElProgreso;

                // Simular progreso paso a paso
                for (int i = 0; i < totalPasos; i++) {
                    // Pausa simulando procesamiento
                    Thread.sleep(tiempoPorSalto);
                    
                    // Calcular progreso y actualizar UI
                    final double progreso = (double) (i + 1) / totalPasos;
                    Platform.runLater(() -> {
                        progressBar.setProgress(progreso);
                    });
                }

                return null;
            }

            @Override
            protected void succeeded() {
                // Ejecutar el controlador personalizado al completar la carga
                Platform.runLater(() -> {
                    try {
                        // Llamar al método del controlador que maneja la vista con datos
                        controladorCargaConDatos.cargarVistaPasandoDatos();

                    } catch (IOException e) {
                        // Manejar errores de E/O al cargar la vista
                        e.printStackTrace();
                        ManejadorMetodosComunes.mostrarVentanaError("Error al cargar la vista: " + e.getMessage());
                    }
                });
            }

            @Override
            protected void failed() {
                // Manejar errores durante la tarea de carga
                Platform.runLater(() -> {
                    // Mostrar error específico con detalles de la excepción
                    ManejadorMetodosComunes.mostrarVentanaError("Error durante la carga: " + getException().getMessage());
                    progressBar.setProgress(0.0);
                });
            }
        };

        // Crear y ejecutar el hilo para la tarea
        Thread hiloCargar = new Thread(tareaCargar);
        hiloCargar.setDaemon(true); // Hilo daemon para cierre automático
        hiloCargar.start();
    }
}