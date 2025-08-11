package com.cinemax.reportes.servicios;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import com.cinemax.reportes.modelos.entidades.EstrategiaAnual;
import com.cinemax.reportes.modelos.entidades.EstrategiaDeFrecuencia;
import com.cinemax.reportes.modelos.entidades.EstrategiaDiaria;
import com.cinemax.reportes.modelos.entidades.EstrategiaMensual;
import com.cinemax.reportes.modelos.entidades.EstrategiaSemanal;
import com.cinemax.reportes.modelos.entidades.EstrategiaTrimestal;
import com.cinemax.reportes.modelos.entidades.ReporteGenerado;

/**
 * Servicio que gestiona la programación y ejecución automática de reportes.
 * Implementa el patrón Singleton para asegurar que solo haya una instancia
 * del servicio en toda la aplicación. Se encarga de:
 * - Programar reportes para ejecución automática
 * - Monitorear reportes pendientes
 * - Ejecutar reportes cuando llega su fecha programada
 * - Reprogramar reportes recurrentes
 */
// Aqui se aplico el patron Singleton para asegurar que solo haya una instancia de este servicio
public class ServicioReportesProgramados {
    
    // Instancia única del servicio (patrón Singleton)
    private static ServicioReportesProgramados instance;
    
    // Lista de reportes que están esperando ser ejecutados
    private final List<ReporteGenerado> reportesPendientes = new ArrayList<>();
    
    // Lista observable de reportes que ya fueron ejecutados (para la interfaz gráfica)
    private final ObservableList<ReporteGenerado> reportesEjecutados = FXCollections.observableArrayList();
    
    // Ejecutor que maneja la programación y ejecución de tareas automáticas
    private ScheduledExecutorService scheduler;

    /**
     * Constructor privado para implementar el patrón Singleton.
     * Previene la creación de múltiples instancias del servicio.
     */
    private ServicioReportesProgramados() {
    }

    /**
     * Obtiene la instancia única del servicio (patrón Singleton).
     * Si no existe una instancia, la crea; si ya existe, la retorna.
     * 
     * @return La instancia única del ServicioReportesProgramados
     */
    public static synchronized ServicioReportesProgramados getInstance() {
        if (instance == null) {
            instance = new ServicioReportesProgramados();
        }
        return instance;
    }

    /**
     * Inicializa y pone en marcha el programador de tareas (scheduler).
     * Configura una tarea que revisa cada 10 segundos si hay reportes
     * pendientes que deben ejecutarse.
     */
    public void iniciarScheduler() {
        // Solo crear un nuevo scheduler si no existe uno o está detenido
        if (scheduler == null || scheduler.isShutdown()) {
            // Crear un executor de un solo hilo para las tareas programadas
            scheduler = Executors.newSingleThreadScheduledExecutor();

            // Programar la revisión de reportes cada 10 segundos
            // Platform.runLater asegura que la ejecución se haga en el hilo de JavaFX
            scheduler.scheduleAtFixedRate(() -> {
                Platform.runLater(this::revisarReportesPendientes);
            }, 0, 10, TimeUnit.SECONDS);
        }
    }

    /**
     * Detiene el programador de tareas y cancela todas las tareas pendientes.
     * Útil para limpiar recursos cuando se cierra la aplicación.
     */
    public void detenerScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    /**
     * Obtiene la lista de reportes que están esperando ser ejecutados.
     * 
     * @return Lista de reportes pendientes
     */
    public List<ReporteGenerado> getReportesPendientes() {
        return reportesPendientes;
    }

    /**
     * Obtiene la lista observable de reportes que ya fueron ejecutados.
     * Esta lista se usa para mostrar el historial en la interfaz gráfica.
     * 
     * @return Lista observable de reportes ejecutados
     */
    public ObservableList<ReporteGenerado> getReportesEjecutados() {
        return reportesEjecutados;
    }

    /**
     * Revisa los reportes pendientes y ejecuta aquellos cuya fecha
     * de ejecución ya llegó. Después de ejecutar un reporte:
     * 1. Lo marca como ejecutado
     * 2. Lo agrega al historial de reportes ejecutados
     * 3. Calcula y programa la siguiente ejecución si es recurrente
     */
    private void revisarReportesPendientes() {
        LocalDateTime ahora = LocalDateTime.now();
        List<ReporteGenerado> ejecutados = new ArrayList<>();
        
        // Revisar cada reporte pendiente
        for (ReporteGenerado reporte : reportesPendientes) {
            // Si la fecha del reporte no es posterior a ahora, debe ejecutarse
            if (!reporte.getFechaGeneracion().isAfter(ahora)) {
                // Agregar el reporte ejecutado al historial (al inicio de la lista)
                reportesEjecutados.add(0, new ReporteGenerado(
                        reporte.getNombre(),
                        "Ejecutado",
                        reporte.getFechaGeneracion(),
                        reporte.getTipo(),
                        reporte.getRutaArchivo()));
                        
                // Reprogramar la próxima ejecución usando la estrategia de frecuencia
                reporte.setFechaGeneracion(
                        calcularSiguienteEjecucion(reporte.getFechaGeneracion(), reporte.getFrecuencia()));
                        
                // Agregar a la lista de reportes procesados
                ejecutados.add(reporte);
            }
        }
        // Línea comentada: si se descomenta, los reportes no serían recurrentes
        // reportesPendientes.removeAll(ejecutados); // Si no quieres que sean recurrentes
    }

    /**
     * Obtiene la estrategia de frecuencia apropiada según el tipo especificado.
     * Implementa el patrón Strategy para manejar diferentes tipos de frecuencias.
     * 
     * @param frecuencia Tipo de frecuencia deseada (Diario, Semanal, etc.)
     * @return Instancia de la estrategia correspondiente
     */
    private EstrategiaDeFrecuencia getStrategy(String frecuencia) {
        switch (frecuencia) {
            case "Diario":
                return new EstrategiaDiaria();
            case "Semanal":
                return new EstrategiaSemanal();
            case "Mensual":
                return new EstrategiaMensual();
            case "Trimestral":
                return new EstrategiaTrimestal();
            case "Anual":
                return new EstrategiaAnual();
            default:
                // Si no se especifica frecuencia o no es reconocida, usar diaria por defecto
                return new EstrategiaDiaria();
        }
    }

    /**
     * Calcula la siguiente fecha de ejecución para un reporte
     * basándose en su fecha actual y frecuencia.
     * 
     * @param fechaGeneracion Fecha actual del reporte
     * @param frecuencia Tipo de frecuencia del reporte
     * @return Nueva fecha de ejecución calculada
     */
    private LocalDateTime calcularSiguienteEjecucion(LocalDateTime fechaGeneracion, String frecuencia) {
        // Obtener la estrategia apropiada y calcular la siguiente fecha
        EstrategiaDeFrecuencia strategy = getStrategy(frecuencia);
        return strategy.calcularSiguiente(fechaGeneracion);
    }

    /**
     * Método público para calcular la próxima ejecución de un reporte.
     * Útil para mostrar al usuario cuándo se ejecutará el próximo reporte.
     * 
     * @param fechaGeneracionStr Fecha actual en formato String
     * @param frecuencia Tipo de frecuencia del reporte
     * @return String con la fecha de la próxima ejecución
     */
    public String calcularProximaEjecucion(String fechaGeneracionStr, String frecuencia) {
        // Convertir el string a LocalDateTime
        LocalDateTime fechaGeneracion = LocalDateTime.parse(fechaGeneracionStr);
        // Calcular la próxima ejecución
        LocalDateTime proximaEjecucion = calcularSiguienteEjecucion(fechaGeneracion, frecuencia);
        // Retornar como string
        return proximaEjecucion.toString();
    }
}