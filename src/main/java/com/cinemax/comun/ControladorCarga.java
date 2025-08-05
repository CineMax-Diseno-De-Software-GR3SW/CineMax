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


public class ControladorCarga implements Initializable {
    
    @FXML
    private ProgressBar progressBar;

    public ControladorCarga() {
    // Constructor público sin argumentos
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar el progress bar en 0
        progressBar.setProgress(0.0);
    } 
    
    /**
     * Configura la ventana siguiente y inicia el proceso de carga
     * @param stage La ventana actual
     * @param rutaFXMLSiguienteVentana Ruta del archivo FXML de la siguiente ventana
     */
    public void iniciarCarga(Stage stage, String rutaFXMLSiguienteVentana, int saltosEnElProgreso, int tiempoPorSalto) {
        // Crear una tarea en segundo plano para simular la carga
        Task<Void> tareaCargar = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                
                int totalPasos = saltosEnElProgreso;
                
                for (int i = 0; i < totalPasos; i++) {
                    
                    // Simular tiempo de carga (ajusta según tus necesidades)
                    Thread.sleep(tiempoPorSalto); // 700ms por paso

                    // Actualizar progreso
                    final double progreso = (double) (i + 1) / totalPasos;
                    Platform.runLater(() -> {
                        progressBar.setProgress(progreso);
                    });
                }
                
                return null;
            }
            
            @Override
            protected void succeeded() {
                // Cuando la carga termine, cambiar a la siguiente ventana
                Platform.runLater(() -> {
                    ManejadorMetodosComunes.cambiarVentana(stage, rutaFXMLSiguienteVentana);
                });
            }
            
            @Override
            protected void failed() {
                // En caso de error en la tarea
                Platform.runLater(() -> {
                    ManejadorMetodosComunes.mostrarVentanaError("Error durante la carga: ");
                    progressBar.setProgress(0.0);
                });
            }
        };
        
        // Ejecutar la tarea en un hilo separado
        Thread hiloCargar = new Thread(tareaCargar);
        hiloCargar.setDaemon(true); // Para que se cierre cuando termine la aplicación
        hiloCargar.start();
    }

    public void iniciarCarga(Stage stage, ControladorCargaConDatos controladorCargaConDatos, int saltosEnElProgreso, int tiempoPorSalto) {
        Task<Void> tareaCargar = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int totalPasos = saltosEnElProgreso;

                for (int i = 0; i < totalPasos; i++) {
                    Thread.sleep(tiempoPorSalto);
                    final double progreso = (double) (i + 1) / totalPasos;
                    Platform.runLater(() -> {
                        progressBar.setProgress(progreso);
                    });
                }

                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    try {
                        controladorCargaConDatos.cargarVistaPasandoDatos();

                    } catch (IOException e) {
                        e.printStackTrace();
                        ManejadorMetodosComunes.mostrarVentanaError("Error al cargar la vista: " + e.getMessage());
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    ManejadorMetodosComunes.mostrarVentanaError("Error durante la carga: " + getException().getMessage());
                    progressBar.setProgress(0.0);
                });
            }
        };

        Thread hiloCargar = new Thread(tareaCargar);
        hiloCargar.setDaemon(true);
        hiloCargar.start();
    }
}