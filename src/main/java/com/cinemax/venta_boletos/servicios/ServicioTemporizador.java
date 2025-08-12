package com.cinemax.venta_boletos.servicios;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

import com.cinemax.utilidades.ManejadorMetodosComunes;

/**
 * Servicio Singleton para gestionar un temporizador de inactividad durante la venta de boletos.
 * Si el temporizador de 15 minutos expira, redirige al usuario al menú principal.
 */
public class ServicioTemporizador {

    private static ServicioTemporizador instancia;
    private Timer temporizador;
    private boolean tempEnEjecucion = false;
    private long finTiempo;
    private final StringProperty tiempoRestante = new SimpleStringProperty("15:00");


    private ServicioTemporizador() {}

    /**
     * Obtiene la instancia única del servicio de temporizador.
     * @return La instancia única.
     */
    public static synchronized ServicioTemporizador getInstancia() {
        if (instancia == null) {
            instancia = new ServicioTemporizador();
        }
        return instancia;
    }

    /**
     * Propiedad observable que contiene el tiempo restante formateado (MM:SS).
     * La Ui se puede vincular a esta propiedad para mostrar la cuenta regresiva
     * @return la propiedad del tiempo restante
     */
    public StringProperty tiempoRestanteProperty(){return tiempoRestante;}

    /**
     * Inicia el temporizador de 15 minutos.
     * Si ya hay un temporizador en ejecución, no hace nada, permitiendo que continúe.
     * @param stage La ventana actual, necesaria para la redirección si el tiempo expira.
     */
    public void empezarTemporizador(Stage stage) {
        if (tempEnEjecucion) {
            System.out.println("El temporizador ya está en ejecución. Dejando que continúe.");
            return;
        }

        temporizador = new Timer(true); // true para que sea un hilo daemon
        tempEnEjecucion = true;
        long duracion = 15 * 60 * 1000; // 15 minutos
        finTiempo = System.currentTimeMillis() + duracion;
        System.out.println("Temporizador de venta iniciado (15 minutos).");

        temporizador.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long tiempoRestanteMillis = finTiempo - System.currentTimeMillis();
                if (tiempoRestanteMillis <= 0){
                    detenerTemporizador();

                    Platform.runLater(() -> {
                        System.out.println("Tiempo de venta expirado. Redirigiendo al menú principal.");
                        ManejadorMetodosComunes.mostrarVentanaAdvertencia("El tiempo para completar la compra ha expirado. Serás redirigido al menú principal.");
                        ManejadorMetodosComunes.cambiarVentana(stage, "/vistas/empleados/PantallaPortalPrincipal.fxml");
                    });
                } else {
                    long minutos = (tiempoRestanteMillis / 1000) / 60;
                    long segundos = (tiempoRestanteMillis / 1000) % 60;
                    String formatoTiempo = String.format("%02d:%02d", minutos, segundos);
                    Platform.runLater(() -> tiempoRestante.set(formatoTiempo));
                }
            }
        }, 0, 1000); // Se ejecuta cada segundo.
    }

    /**
     * Detiene el temporizador si está en ejecución.
     */
    public void detenerTemporizador() {
        if (temporizador != null) {
            temporizador.cancel();
            temporizador = null;
        }
        tempEnEjecucion = false;
        Platform.runLater(() -> tiempoRestante.set("15:00"));
        System.out.println("Temporizador de venta detenido.");
    }

    /**
     * Verifica si el temporizador está actualmente en ejecución.
     * @return true si el temporizador está activo, false en caso contrario.
     */
    public boolean tempEnEjecucion() {return tempEnEjecucion;}
}
