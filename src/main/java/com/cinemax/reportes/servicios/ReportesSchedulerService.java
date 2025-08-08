package com.cinemax.reportes.servicios;

import com.cinemax.reportes.modelos.AnualStrategy;
import com.cinemax.reportes.modelos.DiarioStrategy;
import com.cinemax.reportes.modelos.FrecuenciaStrategy;
import com.cinemax.reportes.modelos.MensualStrategy;
import com.cinemax.reportes.modelos.ReporteVentaDTO;
import com.cinemax.reportes.modelos.SemanalStrategy;
import com.cinemax.reportes.modelos.TrimestralStrategy;
import com.cinemax.reportes.modelos.Export;
import com.cinemax.reportes.modelos.ExportarPDFStrategy;
import com.cinemax.reportes.modelos.ExportarCSVStrategy;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

// Aplicación del patrón Singleton para asegurar que solo haya una instancia de este servicio
public class ReportesSchedulerService {
    private static ReportesSchedulerService instance;
    private final List<ReporteGenerado> reportesPendientes = new ArrayList<>();
    private final ObservableList<ReporteGenerado> reportesEjecutados = FXCollections.observableArrayList();
    private ScheduledExecutorService scheduler;
    private VentasService ventasService; // Agregado para usar datos reales

    private ReportesSchedulerService() {
        this.ventasService = new VentasService();
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

            // Revisar cada 30 segundos (ajustable según necesidades)
            scheduler.scheduleAtFixedRate(() -> {
                Platform.runLater(this::revisarReportesPendientes);
            }, 0, 30, TimeUnit.SECONDS);
            
            System.out.println("Scheduler iniciado - revisando reportes cada 30 segundos");
        }
    }

    public void detenerScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            System.out.println("Scheduler detenido");
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
            // Verificar si es momento de ejecutar el reporte
            if (!reporte.getFechaGeneracion().isAfter(ahora)) {
                try {
                    // Ejecutar el reporte con datos reales
                    ejecutarReporteConDatosReales(reporte);
                    
                    // Crear copia del reporte como "ejecutado"
                    ReporteGenerado reporteEjecutado = new ReporteGenerado(
                            reporte.getNombre() + "_EJECUTADO",
                            "Ejecutado",
                            LocalDateTime.now(), // Marcar como ejecutado ahora
                            reporte.getTipo(),
                            reporte.getRutaArchivo());
                    
                    reporteEjecutado.setFrecuencia(reporte.getFrecuencia());
                    reporteEjecutado.setConfiguracion(reporte.getConfiguracion());
                    
                    // Agregar a la lista de ejecutados
                    Platform.runLater(() -> reportesEjecutados.add(0, reporteEjecutado));
                    
                    // Reprogramar la próxima ejecución
                    LocalDateTime proximaEjecucion = calcularSiguienteEjecucion(
                            reporte.getFechaGeneracion(), reporte.getFrecuencia());
                    reporte.setFechaGeneracion(proximaEjecucion);
                    
                    System.out.println("Reporte ejecutado: " + reporte.getNombre() + 
                                     " - Próxima ejecución: " + proximaEjecucion);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error al ejecutar reporte " + reporte.getNombre() + ": " + e.getMessage());
                    
                    // Crear reporte de error
                    ReporteGenerado reporteError = new ReporteGenerado(
                            reporte.getNombre() + "_ERROR",
                            "Error",
                            LocalDateTime.now(),
                            "ERROR",
                            "");
                    
                    Platform.runLater(() -> reportesEjecutados.add(0, reporteError));
                }
            }
        }
    }

    private void ejecutarReporteConDatosReales(ReporteGenerado reporte) throws Exception {
        try {
            // Obtener configuración del reporte
            Map<String, Object> configuracion = reporte.getConfiguracion();
            if (configuracion == null) {
                configuracion = new HashMap<>();
            }
            
            // Determinar período para obtener datos
            LocalDate desde = LocalDate.now().minusDays(30); // Por defecto último mes
            LocalDate hasta = LocalDate.now();
            
            if (configuracion.containsKey("fecha_desde") && configuracion.containsKey("fecha_hasta")) {
                desde = LocalDate.parse((String) configuracion.get("fecha_desde"));
                hasta = LocalDate.parse((String) configuracion.get("fecha_hasta"));
            }
            
            // Obtener filtros
            String sala = (String) configuracion.getOrDefault("sala", "Todas");
            String tipoBoleto = (String) configuracion.getOrDefault("tipo_boleto", "Todos");
            String horario = (String) configuracion.getOrDefault("horario", "Todos");
            
            // Obtener datos reales de la base de datos
            List<ReporteVentaDTO> datosReales = ventasService.getVentasFiltradas(desde, hasta, sala, tipoBoleto, horario);
            
            if (datosReales.isEmpty()) {
                System.out.println("No se encontraron datos para el reporte " + reporte.getNombre());
                // Aún así generar el reporte indicando que no hay datos
            }
            
            // Crear directorio de reportes programados si no existe
            File directorioReportes = new File("reportes_programados");
            if (!directorioReportes.exists()) {
                directorioReportes.mkdirs();
            }
            
            // Generar nombre de archivo único
            String nombreArchivo = reporte.getNombre() + "_" + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            // Exportar como PDF por defecto (se puede hacer configurable)
            File archivoDestino = new File(directorioReportes, nombreArchivo + ".pdf");
            
            // Usar estrategia de exportación
            Export exportStrategy = new ExportarPDFStrategy();
            
            // Preparar datos para exportación
            Map<String, Object> datosParaExportar = new HashMap<>();
            datosParaExportar.put("datos_ventas", datosReales);
            datosParaExportar.put("total_boletos", datosReales.stream().mapToInt(v -> v.boletosVendidos).sum());
            datosParaExportar.put("total_ingresos", datosReales.stream().mapToDouble(v -> v.ingresos).sum());
            datosParaExportar.put("periodo", desde + " - " + hasta);
            datosParaExportar.put("filtros", configuracion);
            datosParaExportar.put("fecha_generacion", LocalDateTime.now().toString());
            datosParaExportar.put("frecuencia", reporte.getFrecuencia());
            
            // Exportar el reporte
            exportStrategy.exportar(reporte, archivoDestino, datosParaExportar);
            
            // Actualizar la ruta del archivo en el reporte
            reporte.setRutaArchivo(archivoDestino.getAbsolutePath());
            
            System.out.println("Reporte generado exitosamente: " + archivoDestino.getAbsolutePath());
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error al ejecutar reporte con datos reales: " + e.getMessage());
        }
    }

    // Método para programar un reporte con configuración específica
    public void programarReporte(String nombre, String frecuencia, LocalDate fechaDesde, LocalDate fechaHasta, 
                                String sala, String tipoBoleto, String horario) {
        try {
            // Calcular primera ejecución
            LocalDateTime primeraEjecucion = calcularSiguienteEjecucion(LocalDateTime.now(), frecuencia);
            
            // Crear reporte programado
            ReporteGenerado reporteProgramado = new ReporteGenerado(
                nombre + "_" + frecuencia,
                "Programado",
                primeraEjecucion,
                "PDF",
                ""
            );
            
            reporteProgramado.setFrecuencia(frecuencia);
            
            // Configurar filtros
            Map<String, Object> configuracion = new HashMap<>();
            configuracion.put("fecha_desde", fechaDesde.toString());
            configuracion.put("fecha_hasta", fechaHasta.toString());
            configuracion.put("sala", sala);
            configuracion.put("tipo_boleto", tipoBoleto);
            configuracion.put("horario", horario);
            configuracion.put("fecha_programacion", LocalDateTime.now().toString());
            
            reporteProgramado.setConfiguracion(configuracion);
            
            // Agregar a la lista de pendientes
            reportesPendientes.add(reporteProgramado);
            
            System.out.println("Reporte programado exitosamente: " + nombre + 
                             " - Primera ejecución: " + primeraEjecucion);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al programar reporte: " + e.getMessage());
        }
    }

    // Método para obtener estadísticas del scheduler
    public Map<String, Object> getEstadisticasScheduler() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        estadisticas.put("reportes_pendientes", reportesPendientes.size());
        estadisticas.put("reportes_ejecutados", reportesEjecutados.size());
        estadisticas.put("scheduler_activo", scheduler != null && !scheduler.isShutdown());
        
        // Contar reportes por frecuencia
        Map<String, Long> reportesPorFrecuencia = new HashMap<>();
        for (ReporteGenerado reporte : reportesPendientes) {
            String frecuencia = reporte.getFrecuencia();
            reportesPorFrecuencia.put(frecuencia, reportesPorFrecuencia.getOrDefault(frecuencia, 0L) + 1);
        }
        estadisticas.put("distribucion_frecuencias", reportesPorFrecuencia);
        
        // Próxima ejecución
        if (!reportesPendientes.isEmpty()) {
            LocalDateTime proximaEjecucion = reportesPendientes.stream()
                .map(ReporteGenerado::getFechaGeneracion)
                .min(LocalDateTime::compareTo)
                .orElse(null);
            estadisticas.put("proxima_ejecucion", proximaEjecucion != null ? proximaEjecucion.toString() : "N/A");
        } else {
            estadisticas.put("proxima_ejecucion", "No hay reportes programados");
        }
        
        return estadisticas;
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
                System.out.println("Frecuencia no reconocida: " + frecuencia + ". Usando DiarioStrategy por defecto.");
                return new DiarioStrategy();
        }
    }

    private LocalDateTime calcularSiguienteEjecucion(LocalDateTime fechaGeneracion, String frecuencia) {
        FrecuenciaStrategy strategy = getStrategy(frecuencia);
        return strategy.calcularSiguiente(fechaGeneracion);
    }

    public String calcularProximaEjecucion(String fechaGeneracionStr, String frecuencia) {
        try {
            LocalDateTime fechaGeneracion = LocalDateTime.parse(fechaGeneracionStr);
            LocalDateTime proximaEjecucion = calcularSiguienteEjecucion(fechaGeneracion, frecuencia);
            return proximaEjecucion.toString();
        } catch (Exception e) {
            e.printStackTrace();
            // Calcular desde ahora si hay error en el parsing
            LocalDateTime proximaEjecucion = calcularSiguienteEjecucion(LocalDateTime.now(), frecuencia);
            return proximaEjecucion.toString();
        }
    }

    // Método para limpiar reportes ejecutados antiguos
    public void limpiarReportesAntiguos(int diasAntiguedad) {
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(diasAntiguedad);
        
        reportesEjecutados.removeIf(reporte -> 
            reporte.getFechaGeneracion().isBefore(fechaLimite));
        
        System.out.println("Limpieza de reportes antiguos completada. Eliminados reportes anteriores a: " + fechaLimite);
    }

    // Método para pausar/reanudar un reporte específico
    public boolean pausarReporte(String nombreReporte) {
        for (ReporteGenerado reporte : reportesPendientes) {
            if (reporte.getNombre().equals(nombreReporte)) {
                // Mover la próxima ejecución muy lejos en el futuro (efectivamente pausado)
                reporte.setFechaGeneracion(LocalDateTime.now().plusYears(100));
                System.out.println("Reporte pausado: " + nombreReporte);
                return true;
            }
        }
        return false;
    }

    public boolean reanudarReporte(String nombreReporte, String frecuencia) {
        for (ReporteGenerado reporte : reportesPendientes) {
            if (reporte.getNombre().equals(nombreReporte)) {
                // Calcular próxima ejecución normal
                reporte.setFechaGeneracion(calcularSiguienteEjecucion(LocalDateTime.now(), frecuencia));
                System.out.println("Reporte reanudado: " + nombreReporte);
                return true;
            }
        }
        return false;
    }

    // Método para obtener información detallada de un reporte
    public Map<String, Object> getInfoReporte(String nombreReporte) {
        for (ReporteGenerado reporte : reportesPendientes) {
            if (reporte.getNombre().equals(nombreReporte)) {
                Map<String, Object> info = new HashMap<>();
                info.put("nombre", reporte.getNombre());
                info.put("frecuencia", reporte.getFrecuencia());
                info.put("proxima_ejecucion", reporte.getFechaGeneracion().toString());
                info.put("configuracion", reporte.getConfiguracion());
                info.put("ruta_archivo", reporte.getRutaArchivo());
                info.put("tipo", reporte.getTipo());
                return info;
            }
        }
        return new HashMap<>(); // Retornar mapa vacío si no se encuentra
    }
}