package com.cinemax.utilidades;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Controlador para la pantalla de carga
 *
 * Esta clase maneja la lógica de una pantalla de carga que muestra un progress bar
 * mientras se ejecuta una tarea en segundo plano. Permite dos tipos de carga:
 * 1. Carga simple que navega a otra ventana al finalizar
 * 2. Carga con datos que ejecuta un controlador específico al finalizar
 * 
 * @author GR3SW
 * @version 2.0
 */
public class ControladorCarga implements Initializable {
    private static final long TIEMPO_MINIMO_CARGA = 500;
    
    @FXML
    private ProgressBar progressBar;

    public ControladorCarga() {
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
     * Versión optimizada que carga la siguiente ventana EN PARALELO con la animación.
     * 
     * @param stage La ventana actual
     * @param rutaFXMLSiguienteVentana Ruta del archivo FXML de la siguiente ventana
     * @param saltosEnElProgreso Número de pasos en la barra de progreso
     * @param tiempoPorSalto Tiempo en milisegundos por cada paso
     */
    public void iniciarCarga(Stage stage, String rutaFXMLSiguienteVentana, int saltosEnElProgreso, int tiempoPorSalto) {
        long tiempoInicio = System.currentTimeMillis();
        
        // 1. INICIAR CARGA REAL EN PARALELO
        CompletableFuture<Parent> cargaReal = CompletableFuture.supplyAsync(() -> {
            try {
                // Simular aquí cualquier procesamiento adicional necesario antes de cargar la vista
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXMLSiguienteVentana));
                return loader.load();
                
            } catch (IOException e) {
                throw new RuntimeException("Error cargando FXML: " + rutaFXMLSiguienteVentana, e);
            }
        });

        // 2. ANIMAR BARRA DE PROGRESO EN PARALELO
        Task<Void> tareaAnimacion = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int totalPasos = saltosEnElProgreso;
                
                for (int i = 0; i < totalPasos; i++) {
                    // Verificar si la tarea fue cancelada
                    if (isCancelled()) {
                        break;
                    }
                    
                    Thread.sleep(tiempoPorSalto);
                    
                    final double progreso = (double) (i + 1) / totalPasos;
                    Platform.runLater(() -> {
                        if (!isCancelled()) {
                            progressBar.setProgress(progreso);
                        }
                    });
                }
                
                return null;
            }
            
            @Override
            protected void succeeded() {
                // La animación terminó, ahora sincronizar con la carga real
                sincronizarYMostrarVentana(stage, cargaReal, tiempoInicio);
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    ManejadorMetodosComunes.mostrarVentanaError("Error en la animación de carga: " + getException().getMessage());
                    progressBar.setProgress(0.0);
                });
                // Cancelar la carga real si la animación falla
                cargaReal.cancel(true);
            }
        };
        
        // Manejar errores en la carga real
        cargaReal.exceptionally(throwable -> {
            Platform.runLater(() -> {
                ManejadorMetodosComunes.mostrarVentanaError("Error cargando la siguiente ventana: " + throwable.getMessage());
                progressBar.setProgress(0.0);
            });
            // Cancelar la animación si la carga real falla
            tareaAnimacion.cancel();
            return null;
        });
        
        // Ejecutar la animación
        Thread hiloAnimacion = new Thread(tareaAnimacion);
        hiloAnimacion.setDaemon(true);
        hiloAnimacion.start();
    }

    /**
     * Inicia el proceso de carga con controlador personalizado.
     * 
     * @param stage La ventana actual de JavaFX
     * @param controladorCargaConDatos Controlador personalizado que implementa ControladorCargaConDatos
     * @param saltosEnElProgreso Número de pasos/incrementos en la barra de progreso
     * @param tiempoPorSalto Tiempo en milisegundos que dura cada paso de progreso
     */
    public void iniciarCarga(Stage stage, ControladorCargaConDatos controladorCargaConDatos, int saltosEnElProgreso, int tiempoPorSalto) {
        long tiempoInicio = System.currentTimeMillis();
        
        // 1. INICIAR CARGA REAL EN PARALELO
        CompletableFuture<ResultadoCarga> cargaReal = CompletableFuture.supplyAsync(() -> {
            try {
                // Aquí se puede agregar procesamiento adicional si es necesario
                return controladorCargaConDatos.cargarVistaPasandoDatos();
                
            } catch (Exception e) {
                throw new RuntimeException("Error en carga con datos", e);
            }
        });

        // 2. ANIMAR BARRA DE PROGRESO EN PARALELO  
        Task<Void> tareaAnimacion = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int totalPasos = saltosEnElProgreso;
                
                for (int i = 0; i < totalPasos; i++) {
                    if (isCancelled()) {
                        break;
                    }
                    
                    Thread.sleep(tiempoPorSalto);
                    
                    final double progreso = (double) (i + 1) / totalPasos;
                    Platform.runLater(() -> {
                        if (!isCancelled()) {
                            progressBar.setProgress(progreso);
                        }
                    });
                }
                
                return null;
            }
            
            @Override
            protected void succeeded() {
                // Sincronizar con la carga real
                sincronizarYEjecutarControlador(stage, cargaReal, tiempoInicio);
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    ManejadorMetodosComunes.mostrarVentanaError("Error en la animación: " + getException().getMessage());
                    progressBar.setProgress(0.0);
                });
                cargaReal.cancel(true);
            }
        };
        
        // Manejar errores en la carga real
        cargaReal.exceptionally(throwable -> {
            Platform.runLater(() -> {
                ManejadorMetodosComunes.mostrarVentanaError("Error cargando vista con datos: " + throwable.getMessage());
                progressBar.setProgress(0.0);
            });
            tareaAnimacion.cancel();
            return null;
        });
        
        // Ejecutar la animación
        Thread hiloAnimacion = new Thread(tareaAnimacion);
        hiloAnimacion.setDaemon(true);
        hiloAnimacion.start();
    }

    /**
     * Sincroniza la finalización de la animación con la carga real de la ventana.
     * Garantiza un tiempo mínimo de visualización de la pantalla de carga.
     */
    private void sincronizarYMostrarVentana(Stage stage, CompletableFuture<Parent> cargaReal, long tiempoInicio) {
        // Ejecutar en un hilo separado para no bloquear la UI
        CompletableFuture.runAsync(() -> {
            try {
                // Esperar a que termine la carga real
                Parent nuevaVista = cargaReal.get();
                
                // Garantizar tiempo mínimo de visualización
                long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;
                if (tiempoTranscurrido < TIEMPO_MINIMO_CARGA) {
                    Thread.sleep(TIEMPO_MINIMO_CARGA - tiempoTranscurrido);
                }
                
                // Cambiar a la nueva ventana en el hilo de JavaFX
                Platform.runLater(() -> {
                    ManejadorMetodosComunes.cambiarVentanaConVistaYaCargada(stage, nuevaVista);
                });
                
            } catch (InterruptedException | ExecutionException e) {
                Platform.runLater(() -> {
                    ManejadorMetodosComunes.mostrarVentanaError("Error sincronizando carga: " + e.getMessage());
                    progressBar.setProgress(0.0);
                });
            }
        });
    }

    /**
     * Sincroniza la animación con el controlador personalizado.
     */
    private void sincronizarYEjecutarControlador(Stage stage, CompletableFuture<ResultadoCarga> cargaReal, long tiempoInicio) {
        CompletableFuture.runAsync(() -> {
            try {
                // Esperar el resultado de la carga
                ResultadoCarga resultado = cargaReal.get();
                
                // Garantizar tiempo mínimo
                long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;
                if (tiempoTranscurrido < TIEMPO_MINIMO_CARGA) {
                    Thread.sleep(TIEMPO_MINIMO_CARGA - tiempoTranscurrido);
                }
                
                // Aplicar el resultado en el hilo de JavaFX
                Platform.runLater(() -> {
                    resultado.aplicarCambioDeVentana(stage);
                });
                
            } catch (InterruptedException | ExecutionException e) {
                Platform.runLater(() -> {
                    ManejadorMetodosComunes.mostrarVentanaError("Error ejecutando controlador: " + e.getMessage());
                    progressBar.setProgress(0.0);
                });
            }
        });
    }
}