package com.cinemax.reportes.modelos.entidades;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

// Aqui se aplico el patron Singleton para asegurar que solo haya una instancia de este servicio
public class ServicioReportesProgramados {
    private static ServicioReportesProgramados instance;
    private final List<ReporteGenerado> reportesPendientes = new ArrayList<>();
    private final ObservableList<ReporteGenerado> reportesEjecutados = FXCollections.observableArrayList();
    private ScheduledExecutorService scheduler;

    private ServicioReportesProgramados() {
    }

    public static synchronized ServicioReportesProgramados getInstance() {
        if (instance == null) {
            instance = new ServicioReportesProgramados();
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
                return new EstrategiaDiaria();
        }
    }

    private LocalDateTime calcularSiguienteEjecucion(LocalDateTime fechaGeneracion, String frecuencia) {
        EstrategiaDeFrecuencia strategy = getStrategy(frecuencia);
        return strategy.calcularSiguiente(fechaGeneracion);
    }

    public String calcularProximaEjecucion(String fechaGeneracionStr, String frecuencia) {
        LocalDateTime fechaGeneracion = LocalDateTime.parse(fechaGeneracionStr);
        LocalDateTime proximaEjecucion = calcularSiguienteEjecucion(fechaGeneracion, frecuencia);
        return proximaEjecucion.toString();
    }
}