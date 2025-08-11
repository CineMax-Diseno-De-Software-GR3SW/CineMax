package com.cinemax.reportes.modelos.entidades;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import com.cinemax.reportes.modelos.persistencia.VentaDAO;
import java.time.LocalDate;

/**
 * Servicio para la gestión y obtención de reportes de ventas.
 * <p>
 * Esta clase actúa como intermediario entre el controlador y la capa de persistencia (VentaDAO),
 * proporcionando métodos para obtener resúmenes, estadísticas y datos filtrados de ventas.
 * Maneja excepciones y retorna valores por defecto en caso de error.
 * </p>
 *
 * <ul>
 *   <li>Obtiene el resumen general de ventas.</li>
 *   <li>Obtiene estadísticas para gráficas de barras.</li>
 *   <li>Filtra datos de ventas por rango de fechas.</li>
 * </ul>
 *
 * @author Grupo E 
 * @version 1.0
 * @since 2025-08-11
 */
public class VentasService {
    private VentaDAO ventasDAO;

    /**
     * Constructor. Inicializa el DAO de ventas.
     */
    public VentasService() {
        ventasDAO = new VentaDAO();
    }

    /**
     * Obtiene un resumen general de las ventas.
     * 
     * @return Un mapa con el resumen de ventas (total de boletos, facturas, ingreso, etc.).
     *         Si ocurre un error, retorna un mapa con valores por defecto y un mensaje de error.
     */
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

    /**
     * Obtiene las estadísticas de ventas para gráficas de barras.
     * 
     * @return Una lista de mapas con los datos estadísticos de ventas.
     *         Si ocurre un error, retorna una lista vacía.
     */
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

    /**
     * Filtra los datos de ventas según el rango de fechas proporcionado.
     * 
     * @param datos Lista de mapas con los datos de ventas a filtrar.
     * @param desde Fecha de inicio del filtro (formato String).
     * @param hasta Fecha de fin del filtro (formato String).
     * @return Una lista de mapas con los datos filtrados.
     *         Si ocurre un error, retorna una lista vacía.
     */
    public List<Map<String, Object>> obtenerDatosFiltrados(List<Map<String, Object>> datos, String desde,
            String hasta) {
        try {
            return ventasDAO.obtenerFiltradoDeDatos(datos, desde, hasta);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en obtenerDatosFiltrados: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}