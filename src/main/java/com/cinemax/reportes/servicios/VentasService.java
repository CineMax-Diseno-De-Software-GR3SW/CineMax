package com.cinemax.reportes.servicios;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import com.cinemax.reportes.modelos.persistencia.VentaDAO;
import com.cinemax.reportes.modelos.persistencia.ReporteDAO;
import com.cinemax.reportes.modelos.ReporteVentaDTO;

public class VentasService {
    private VentaDAO ventasDAO;
    private ReporteDAO reporteDAO;

    public VentasService() {
        ventasDAO = new VentaDAO();
        reporteDAO = new ReporteDAO();
    }

    // Método principal para obtener resumen de ventas
    public Map<String, Object> getResumenDeVentas() {
        try {
            return ventasDAO.obtenerResumenVentas();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en getResumenDeVentas: " + e.getMessage());
            // Retornar datos por defecto en caso de error
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("total_boletos_vendidos", 0);
            errorResponse.put("total_facturas", 0);
            errorResponse.put("ingreso_total", 0.0);
            errorResponse.put("total_funciones", 0);
            errorResponse.put("fecha_inicio", "Error");
            errorResponse.put("fecha_fin", "Error");
            errorResponse.put("error", "No se pudieron obtener datos de la base de datos");
            return errorResponse;
        }
    }


    public List<Map<String, Object>> getEstadisticasDeBarras() {
        try{
            return ventasDAO.obtenerEstadisticasDeBarras();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en getEstadisticasDeBarras: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Método para obtener ventas por período específico
    public Map<String, Object> getVentasPorFecha(String fechaInicio, String fechaFin) {
        try {
            LocalDate desde = LocalDate.parse(fechaInicio);
            LocalDate hasta = LocalDate.parse(fechaFin);
            
            // Obtener resumen del período
            Map<String, Object> resumenPeriodo = ventasDAO.obtenerResumenVentasPorPeriodo(desde, hasta);
            
            // Obtener ventas filtradas detalladas
            List<ReporteVentaDTO> ventas = reporteDAO.obtenerVentas(desde, hasta, null, null, null);
            
            // Calcular totales adicionales
            int totalBoletos = ventas.stream().mapToInt(v -> v.boletosVendidos).sum();
            double totalIngresos = ventas.stream().mapToDouble(v -> v.ingresos).sum();
            
            Map<String, Object> resultado = new HashMap<>();
            resultado.putAll(resumenPeriodo);
            resultado.put("ventas_detalladas", ventas);
            resultado.put("total_boletos_calculado", totalBoletos);
            resultado.put("total_ingresos_calculado", totalIngresos);
            resultado.put("fecha_consulta", fechaInicio + " - " + fechaFin);
            
            return resultado;
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en getVentasPorFecha: " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    // Método para obtener ventas con filtros específicos
    public List<ReporteVentaDTO> getVentasFiltradas(LocalDate desde, LocalDate hasta, String sala, String tipoBoleto, String horario) {
        try {
            List<ReporteVentaDTO> ventas = reporteDAO.obtenerVentas(desde, hasta, sala, tipoBoleto, horario);
            System.out.println("VentasService: Obtenidas " + ventas.size() + " ventas filtradas de la BD");
            return ventas;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en getVentasFiltradas: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Método para obtener ventas por película
    public List<ReporteVentaDTO> getVentasPorPelicula(LocalDate desde, LocalDate hasta) {
        try {
            return reporteDAO.obtenerVentasPorPelicula(desde, hasta);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en getVentasPorPelicula: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Método para obtener ventas por sala
    public List<ReporteVentaDTO> getVentasPorSala(LocalDate desde, LocalDate hasta) {
        try {
            return reporteDAO.obtenerVentasPorSala(desde, hasta);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en getVentasPorSala: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Método para obtener estadísticas de ocupación
    public List<ReporteVentaDTO> getEstadisticasOcupacion(LocalDate desde, LocalDate hasta) {
        try {
            return reporteDAO.obtenerEstadisticasOcupacion(desde, hasta);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en getEstadisticasOcupacion: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Método para obtener top películas
    public List<ReporteVentaDTO> getTopPeliculas(LocalDate desde, LocalDate hasta, int limite) {
        try {
            return reporteDAO.obtenerTopPeliculas(desde, hasta, limite);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en getTopPeliculas: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Método para obtener ventas por horario
    public List<ReporteVentaDTO> getVentasPorHorario(LocalDate desde, LocalDate hasta) {
        try {
            return reporteDAO.obtenerVentasPorHorario(desde, hasta);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en getVentasPorHorario: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Método para obtener salas disponibles
    public List<String> getSalasDisponibles() {
        try {
            return reporteDAO.obtenerSalas();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en getSalasDisponibles: " + e.getMessage());
            // Retornar lista por defecto en caso de error
            List<String> salasDefault = new ArrayList<>();
            salasDefault.add("Todas");
            salasDefault.add("Sala 1");
            salasDefault.add("Sala 2");
            salasDefault.add("Sala 3");
            return salasDefault;
        }
    }
    
    // Método para obtener géneros disponibles
    public List<String> getGenerosDisponibles() {
        try {
            return reporteDAO.obtenerGeneros();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en getGenerosDisponibles: " + e.getMessage());
            // Retornar lista por defecto en caso de error
            List<String> generosDefault = new ArrayList<>();
            generosDefault.add("Todos");
            generosDefault.add("Acción");
            generosDefault.add("Comedia");
            generosDefault.add("Drama");
            generosDefault.add("Terror");
            return generosDefault;
        }
    }
    
    // Método para obtener resumen completo con análisis detallado
    public Map<String, Object> getResumenCompleto(LocalDate desde, LocalDate hasta) {
        Map<String, Object> resumen = new HashMap<>();
        
        try {
            // Obtener ventas generales
            List<ReporteVentaDTO> ventas = reporteDAO.obtenerVentas(desde, hasta, null, null, null);
            
            // Obtener ventas por película
            List<ReporteVentaDTO> ventasPeliculas = reporteDAO.obtenerVentasPorPelicula(desde, hasta);
            
            // Obtener ventas por sala
            List<ReporteVentaDTO> ventasSalas = reporteDAO.obtenerVentasPorSala(desde, hasta);
            
            // Obtener estadísticas de ocupación
            List<ReporteVentaDTO> estadisticasOcupacion = reporteDAO.obtenerEstadisticasOcupacion(desde, hasta);
            
            // Obtener ventas por horario
            List<ReporteVentaDTO> ventasHorario = reporteDAO.obtenerVentasPorHorario(desde, hasta);
            
            // Obtener resumen ejecutivo
            ReporteVentaDTO resumenEjecutivo = reporteDAO.obtenerResumenEjecutivo(desde, hasta);
            
            // Calcular totales generales
            int totalBoletos = ventas.stream().mapToInt(v -> v.boletosVendidos).sum();
            double totalIngresos = ventas.stream().mapToDouble(v -> v.ingresos).sum();
            
            // Calcular estadísticas por tipo de boleto
            int boletosVIP = ventas.stream()
                .filter(v -> "VIP".equals(v.tipoBoleto))
                .mapToInt(v -> v.boletosVendidos)
                .sum();
            
            int boletosNormal = ventas.stream()
                .filter(v -> "Normal".equals(v.tipoBoleto))
                .mapToInt(v -> v.boletosVendidos)
                .sum();
            
            // Calcular estadísticas por formato
            int boletos2D = ventas.stream()
                .filter(v -> "2D".equals(v.formato) || v.formato == null || v.formato.isEmpty())
                .mapToInt(v -> v.boletosVendidos)
                .sum();
            
            int boletos3D = ventas.stream()
                .filter(v -> "3D".equals(v.formato))
                .mapToInt(v -> v.boletosVendidos)
                .sum();
            
            // Obtener estadísticas adicionales del sistema
            Map<String, Object> estadisticasAdicionales = ventasDAO.obtenerEstadisticasAdicionales();
            
            // Preparar respuesta completa
            resumen.put("ventas_generales", ventas);
            resumen.put("ventas_peliculas", ventasPeliculas);
            resumen.put("ventas_salas", ventasSalas);
            resumen.put("estadisticas_ocupacion", estadisticasOcupacion);
            resumen.put("ventas_horario", ventasHorario);
            resumen.put("resumen_ejecutivo", resumenEjecutivo);
            resumen.put("total_boletos", totalBoletos);
            resumen.put("total_ingresos", totalIngresos);
            resumen.put("boletos_vip", boletosVIP);
            resumen.put("boletos_normal", boletosNormal);
            resumen.put("boletos_2d", boletos2D);
            resumen.put("boletos_3d", boletos3D);
            resumen.put("promedio_por_boleto", totalBoletos > 0 ? totalIngresos / totalBoletos : 0);
            resumen.put("fecha_inicio", desde.toString());
            resumen.put("fecha_fin", hasta.toString());
            resumen.put("estadisticas_sistema", estadisticasAdicionales);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener resumen completo: " + e.getMessage());
            resumen.put("error", "No se pudo obtener el resumen completo: " + e.getMessage());
        }
        
        return resumen;
    }
    
    // Método para obtener dashboard de métricas principales
    public Map<String, Object> getDashboardMetricas() {
        Map<String, Object> dashboard = new HashMap<>();
        
        try {
            // Obtener resumen general
            Map<String, Object> resumenGeneral = ventasDAO.obtenerResumenVentas();
            
            // Obtener estadísticas adicionales
            Map<String, Object> estadisticasAdicionales = ventasDAO.obtenerEstadisticasAdicionales();
            
            // Obtener top clientes
            Map<String, Object> topClientes = ventasDAO.obtenerTopClientes(5);
            
            // Obtener ventas de los últimos 7 días
            LocalDate hoy = LocalDate.now();
            LocalDate semanaAtras = hoy.minusDays(7);
            List<ReporteVentaDTO> ventasRecientes = reporteDAO.obtenerVentas(semanaAtras, hoy, null, null, null);
            
            // Obtener top 5 películas del mes
            LocalDate inicioMes = hoy.withDayOfMonth(1);
            List<ReporteVentaDTO> topPeliculasMes = reporteDAO.obtenerTopPeliculas(inicioMes, hoy, 5);
            
            // Calcular tendencias
            int ventasUltimos7Dias = ventasRecientes.stream().mapToInt(v -> v.boletosVendidos).sum();
            double ingresosUltimos7Dias = ventasRecientes.stream().mapToDouble(v -> v.ingresos).sum();
            
            // Ensamblar dashboard
            dashboard.put("resumen_general", resumenGeneral);
            dashboard.put("estadisticas_adicionales", estadisticasAdicionales);
            dashboard.put("top_clientes", topClientes);
            dashboard.put("ventas_recientes", ventasRecientes);
            dashboard.put("top_peliculas_mes", topPeliculasMes);
            dashboard.put("ventas_ultimos_7_dias", ventasUltimos7Dias);
            dashboard.put("ingresos_ultimos_7_dias", ingresosUltimos7Dias);
            dashboard.put("fecha_actualizacion", java.time.LocalDateTime.now().toString());
            dashboard.put("periodo_recientes", semanaAtras + " - " + hoy);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener dashboard de métricas: " + e.getMessage());
            dashboard.put("error", "No se pudo cargar el dashboard: " + e.getMessage());
        }
        
        return dashboard;
    }
    
    // Método para validar la conexión y obtener información del sistema
    public Map<String, Object> getInfoSistema() {
        try {
            Map<String, Object> info = ventasDAO.obtenerInfoSistema();
            info.put("ultima_actualizacion", reporteDAO.obtenerUltimaActualizacion());
            info.put("conexion_valida", reporteDAO.validarConexion());
            return info;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("conexion_activa", false);
            errorInfo.put("error", "Error al obtener información del sistema: " + e.getMessage());
            return errorInfo;
        }
    }
    
    // Método para obtener análisis de rendimiento por período
    public Map<String, Object> getAnalisisRendimiento(LocalDate desde, LocalDate hasta) {
        Map<String, Object> analisis = new HashMap<>();
        
        try {
            // Obtener datos del período actual
            Map<String, Object> periodoActual = ventasDAO.obtenerResumenVentasPorPeriodo(desde, hasta);
            
            // Calcular período anterior para comparación
            long diasPeriodo = hasta.toEpochDay() - desde.toEpochDay();
            LocalDate inicioAnterior = desde.minusDays(diasPeriodo + 1);
            LocalDate finAnterior = desde.minusDays(1);
            
            Map<String, Object> periodoAnterior = ventasDAO.obtenerResumenVentasPorPeriodo(inicioAnterior, finAnterior);
            
            // Calcular variaciones
            int boletosActual = (Integer) periodoActual.getOrDefault("total_boletos_vendidos", 0);
            int boletosAnterior = (Integer) periodoAnterior.getOrDefault("total_boletos_vendidos", 0);
            
            double ingresosActual = (Double) periodoActual.getOrDefault("ingreso_total", 0.0);
            double ingresosAnterior = (Double) periodoAnterior.getOrDefault("ingreso_total", 0.0);
            
            double variacionBoletos = boletosAnterior > 0 ? 
                ((double)(boletosActual - boletosAnterior) / boletosAnterior) * 100 : 0;
            
            double variacionIngresos = ingresosAnterior > 0 ? 
                ((ingresosActual - ingresosAnterior) / ingresosAnterior) * 100 : 0;
            
            // Obtener análisis detallado
            List<ReporteVentaDTO> topPeliculas = reporteDAO.obtenerTopPeliculas(desde, hasta, 10);
            List<ReporteVentaDTO> ventasPorSala = reporteDAO.obtenerVentasPorSala(desde, hasta);
            List<ReporteVentaDTO> ventasPorHorario = reporteDAO.obtenerVentasPorHorario(desde, hasta);
            
            // Ensamblar análisis
            analisis.put("periodo_actual", periodoActual);
            analisis.put("periodo_anterior", periodoAnterior);
            analisis.put("variacion_boletos_porcentaje", variacionBoletos);
            analisis.put("variacion_ingresos_porcentaje", variacionIngresos);
            analisis.put("top_peliculas", topPeliculas);
            analisis.put("rendimiento_salas", ventasPorSala);
            analisis.put("distribucion_horarios", ventasPorHorario);
            analisis.put("fecha_analisis", java.time.LocalDateTime.now().toString());
            analisis.put("periodo_comparacion", inicioAnterior + " - " + finAnterior);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en análisis de rendimiento: " + e.getMessage());
            analisis.put("error", "No se pudo generar el análisis: " + e.getMessage());
        }
        
        return analisis;
    }
    
    // Método para exportar datos para reportes programados
    public Map<String, Object> getDatosParaReporteProgramado(LocalDate desde, LocalDate hasta, Map<String, String> filtros) {
        Map<String, Object> datos = new HashMap<>();
        
        try {
            String sala = filtros.getOrDefault("sala", "Todas");
            String tipoBoleto = filtros.getOrDefault("tipo_boleto", "Todos");
            String horario = filtros.getOrDefault("horario", "Todos");
            
            // Obtener datos principales
            List<ReporteVentaDTO> ventasPrincipales = reporteDAO.obtenerVentas(desde, hasta, sala, tipoBoleto, horario);
            
            // Obtener resumen del período
            Map<String, Object> resumenPeriodo = ventasDAO.obtenerResumenVentasPorPeriodo(desde, hasta);
            
            // Calcular métricas específicas
            int totalBoletos = ventasPrincipales.stream().mapToInt(v -> v.boletosVendidos).sum();
            double totalIngresos = ventasPrincipales.stream().mapToDouble(v -> v.ingresos).sum();
            
            Map<String, Integer> boletosPorTipo = new HashMap<>();
            Map<String, Integer> boletosPorFormato = new HashMap<>();
            
            for (ReporteVentaDTO venta : ventasPrincipales) {
                boletosPorTipo.merge(venta.tipoBoleto, venta.boletosVendidos, Integer::sum);
                boletosPorFormato.merge(venta.formato, venta.boletosVendidos, Integer::sum);
            }
            
            // Ensamblar datos para el reporte
            datos.put("ventas_detalladas", ventasPrincipales);
            datos.put("resumen_periodo", resumenPeriodo);
            datos.put("total_boletos", totalBoletos);
            datos.put("total_ingresos", totalIngresos);
            datos.put("distribucion_tipos", boletosPorTipo);
            datos.put("distribucion_formatos", boletosPorFormato);
            datos.put("filtros_aplicados", filtros);
            datos.put("periodo", desde + " - " + hasta);
            datos.put("fecha_generacion", java.time.LocalDateTime.now().toString());
            datos.put("promedio_por_boleto", totalBoletos > 0 ? totalIngresos / totalBoletos : 0);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al obtener datos para reporte programado: " + e.getMessage());
            datos.put("error", "Error al generar datos del reporte: " + e.getMessage());
        }
        
        return datos;
    }
    
    // Método de utilidad para limpiar y reinicializar conexiones
    public boolean reinicializarConexiones() {
        try {
            // Verificar conexión actual
            boolean conexionAntes = ventasDAO.verificarConexion();
            
            // Crear nuevas instancias de DAO
            ventasDAO = new VentaDAO();
            reporteDAO = new ReporteDAO();
            
            // Verificar nueva conexión
            boolean conexionDespues = ventasDAO.verificarConexion();
            
            System.out.println("Reinicialización de conexiones - Antes: " + conexionAntes + ", Después: " + conexionDespues);
            
            return conexionDespues;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al reinicializar conexiones: " + e.getMessage());
            return false;
        }
    }
}