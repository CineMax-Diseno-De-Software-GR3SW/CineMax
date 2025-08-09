package com.cinemax.reportes.servicios;

import com.cinemax.reportes.modelos.AnualStrategy;
import com.cinemax.reportes.modelos.DiarioStrategy;
import com.cinemax.reportes.modelos.FrecuenciaStrategy;
import com.cinemax.reportes.modelos.MensualStrategy;
import com.cinemax.reportes.modelos.ReporteGenerado;
import com.cinemax.reportes.modelos.SemanalStrategy;
import com.cinemax.reportes.modelos.TrimestralStrategy;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

// Aqui se aplico el patron Singleton para asegurar que solo haya una instancia de este servicio
public class ReportesSchedulerService {
    private static ReportesSchedulerService instance;
    private final List<ReporteGenerado> reportesPendientes = new ArrayList<>();
    private final ObservableList<ReporteGenerado> reportesEjecutados = FXCollections.observableArrayList();
    private ScheduledExecutorService scheduler;

    private ReportesSchedulerService() {
    }

    public static synchronized ReportesSchedulerService getInstance() {
        if (instance == null) {
            instance = new ReportesSchedulerService();
        }
        return instance;
    }

    public void iniciarScheduler() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();

            scheduler.scheduleAtFixedRate(() -> {
                Platform.runLater(this::revisarReportesPendientes);
            }, 0, 10, TimeUnit.SECONDS);
        }
    }

    public void detenerScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    public List<ReporteGenerado> getReportesPendientes() {
        return reportesPendientes;
    }

    public ObservableList<ReporteGenerado> getReportesEjecutados() {
        return reportesEjecutados;
    }

    private void revisarReportesPendientes() {
        LocalDateTime ahora = LocalDateTime.now();
        List<ReporteGenerado> ejecutados = new ArrayList<>();
        for (ReporteGenerado reporte : reportesPendientes) {
            if (!reporte.getFechaGeneracion().isAfter(ahora)) {
                reportesEjecutados.add(0, new ReporteGenerado(
                        reporte.getNombre(),
                        "Ejecutado",
                        reporte.getFechaGeneracion(),
                        reporte.getTipo(),
                        reporte.getRutaArchivo()));
                        
                // Reprogramar la próxima ejecución
                reporte.setFechaGeneracion(
                        calcularSiguienteEjecucion(reporte.getFechaGeneracion(), reporte.getFrecuencia()));
                ejecutados.add(reporte);
            }
        }
        // reportesPendientes.removeAll(ejecutados); // Si no quieres que sean
        // recurrentes
    }

    private FrecuenciaStrategy getStrategy(String frecuencia) {
        switch (frecuencia) {
            case "Diario":
                return new DiarioStrategy();
            case "Semanal":
                return new SemanalStrategy();
            case "Mensual":
                return new MensualStrategy();
            case "Trimestral":
                return new TrimestralStrategy();
            case "Anual":
                return new AnualStrategy();
            default:
                return new DiarioStrategy();
        }
    }

    private LocalDateTime calcularSiguienteEjecucion(LocalDateTime fechaGeneracion, String frecuencia) {
        FrecuenciaStrategy strategy = getStrategy(frecuencia);
        return strategy.calcularSiguiente(fechaGeneracion);
    }

    public String calcularProximaEjecucion(String fechaGeneracionStr, String frecuencia) {
        LocalDateTime fechaGeneracion = LocalDateTime.parse(fechaGeneracionStr);
        LocalDateTime proximaEjecucion = calcularSiguienteEjecucion(fechaGeneracion, frecuencia);
        return proximaEjecucion.toString();
    }
}