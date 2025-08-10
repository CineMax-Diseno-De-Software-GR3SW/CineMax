package com.cinemax.reportes.modelos.entidades;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import com.cinemax.reportes.modelos.persistencia.VentaDAO;

/**
 * Servicio que maneja la lógica de negocio relacionada con las ventas del cine.
 * Actúa como una capa intermedia entre la interfaz de usuario y el acceso a datos (DAO).
 * Proporciona métodos para obtener resúmenes y estadísticas de ventas,
 * manejando errores y proporcionando datos por defecto cuando es necesario.
 */
public class VentasService {
    
    // Objeto de acceso a datos para consultas relacionadas con ventas
    private VentaDAO ventasDAO;

    /**
     * Constructor que inicializa el servicio con una instancia del DAO de ventas.
     * Establece la conexión con la capa de persistencia de datos.
     */
    public VentasService() {
        ventasDAO = new VentaDAO();
    }

    /**
     * Obtiene un resumen completo de las ventas del sistema.
     * Este método encapsula la llamada al DAO y maneja posibles errores,
     * proporcionando datos por defecto en caso de falla en la base de datos.
     * 
     * @return Map<String, Object> conteniendo:
     *         - total_boletos_vendidos: Número total de boletos vendidos
     *         - total_facturas: Número total de transacciones
     *         - ingreso_total: Suma total de ingresos generados
     *         - total_funciones: Número total de funciones realizadas
     *         - fecha_inicio: Fecha del primer registro
     *         - fecha_fin: Fecha del último registro
     *         - error: Mensaje de error (solo si ocurrió un problema)
     */
    // Método principal para obtener resumen de ventas
    public Map<String, Object> getResumenDeVentas() {
        try {
            // Intentar obtener los datos reales de la base de datos
            return ventasDAO.obtenerResumenVentas();
        } catch (Exception e) {
            // Registrar el error en la consola para depuración
            e.printStackTrace();
            System.err.println("Error en getResumenDeVentas: " + e.getMessage());
            
            // Retornar datos por defecto en caso de error para evitar que la aplicación falle
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

    /**
     * Obtiene estadísticas de ventas estructuradas para gráficos de barras.
     * Este método está diseñado para proporcionar datos que pueden ser
     * fácilmente visualizados en gráficos o reportes estadísticos.
     * 
     * @return List<Map<String, Object>> lista de mapas donde cada mapa representa
     *         un conjunto de datos estadísticos (fechas, cantidades, categorías, etc.)
     *         Retorna lista vacía en caso de error para evitar excepciones
     */
    public List<Map<String, Object>> getEstadisticasDeBarras() {
        try{
            // Intentar obtener las estadísticas desde la base de datos
            return ventasDAO.obtenerEstadisticasDeBarras();
        }
        catch (Exception e) {
            // Registrar el error en la consola para depuración
            e.printStackTrace();
            System.err.println("Error en getEstadisticasDeBarras: " + e.getMessage());
            
            // Retornar lista vacía en caso de error para mantener la estabilidad
            return new ArrayList<>();
        }
    }
}