package com.cinemax.reportes.modelos.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cinemax.comun.ConexionBaseSingleton;

/**
 * DAO (Data Access Object) para la gestión de consultas de ventas.
 * <p>
 * Esta clase se encarga de interactuar con la base de datos para obtener información
 * relacionada con ventas, como resúmenes, estadísticas y filtrado de datos.
 * Utiliza conexiones JDBC y retorna los resultados en estructuras de tipo Map y List.
 * </p>
 *
 * <ul>
 *   <li>Obtiene un resumen general de ventas (boletos, facturas, ingresos, funciones, fechas).</li>
 *   <li>Obtiene estadísticas para gráficas de barras (por fecha, tipo de sala y formato).</li>
 *   <li>Filtra datos de ventas en memoria según un rango de fechas.</li>
 * </ul>
 *
 * @author Grupo E
 * @version 1.0
 * @since 2025-08-11
 */
public class VentaDAO {

    /**
     * Método que obtiene un resumen general de las ventas desde la base de datos.
     *
     * @return Un mapa con el resumen de ventas (total de boletos, facturas, ingreso, funciones, fechas).
     *         Si ocurre un error, retorna valores por defecto y un mensaje de error.
     */
    public Map<String, Object> obtenerResumenVentas() {
        String sql = "SELECT " +
                "COUNT(DISTINCT b.idboleto) AS total_boletos_vendidos, " +
                "COUNT(DISTINCT f.idfactura) AS total_facturas, " +
                "COALESCE(SUM(f.total), 0) AS ingreso_total, " +
                "COUNT(DISTINCT fun.id_funcion) AS total_funciones, " +
                "TO_CHAR(MIN(fun.fecha_hora_inicio), 'DD-MM-YY') AS fecha_inicio, " +
                "TO_CHAR(MAX(fun.fecha_hora_fin), 'DD-MM-YY') AS fecha_fin " +
                "FROM factura f " +
                "JOIN boleto b ON f.idfactura = b.idfactura " +
                "JOIN funcion fun ON b.idfuncion = fun.id_funcion";

        Map<String, Object> resumen = new HashMap<>();

        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                resumen.put("total_boletos_vendidos", rs.getInt("total_boletos_vendidos"));
                resumen.put("total_facturas", rs.getInt("total_facturas"));
                resumen.put("ingreso_total", rs.getDouble("ingreso_total"));
                resumen.put("total_funciones", rs.getInt("total_funciones"));
                resumen.put("fecha_inicio",
                        rs.getString("fecha_inicio") != null ? rs.getString("fecha_inicio") : "N/A");
                resumen.put("fecha_fin",
                        rs.getString("fecha_fin") != null ? rs.getString("fecha_fin") : "N/A");
            } else {
                // Valores por defecto si no hay datos
                resumen.put("total_boletos_vendidos", 0);
                resumen.put("total_facturas", 0);
                resumen.put("ingreso_total", 0.0);
                resumen.put("total_funciones", 0);
                resumen.put("fecha_inicio", "N/A");
                resumen.put("fecha_fin", "N/A");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener resumen de ventas: " + e.getMessage());

            // Retornar valores por defecto en caso de error
            resumen.put("total_boletos_vendidos", 0);
            resumen.put("total_facturas", 0);
            resumen.put("ingreso_total", 0.0);
            resumen.put("total_funciones", 0);
            resumen.put("fecha_inicio", "Error");
            resumen.put("fecha_fin", "Error");
        }

        return resumen;
    }

    /**
     * Obtiene estadísticas de ventas agrupadas por fecha para gráficas de barras.
     *
     * @return Una lista de mapas con los datos estadísticos de ventas por fecha.
     *         Si ocurre un error, retorna una lista vacía.
     */
    public List<Map<String, Object>> obtenerEstadisticasDeBarras() {
        String sql = "SELECT " +
                "f.fecha::DATE AS fecha, " +
                "COUNT(b.idboleto) AS total_boletos_vendidos, " +
                "COALESCE(SUM(fac.total), 0) AS ingreso_total, " +
                "STRING_AGG(DISTINCT s.tipo::text, ', ') AS tipos_sala, " +
                "STRING_AGG(DISTINCT fun.formato::text, ', ') AS formatos " +
                "FROM boleto b " +
                "JOIN factura fac ON b.idfactura = fac.idfactura " +
                "JOIN funcion fun ON b.idfuncion = fun.id_funcion " +
                "JOIN sala s ON fun.id_sala = s.id " +
                "JOIN factura f ON b.idfactura = f.idfactura " +
                "GROUP BY f.fecha::DATE " +
                "ORDER BY f.fecha::DATE " +
                "LIMIT 7";

        List<Map<String, Object>> estadisticas = new ArrayList<>();

        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("fecha", rs.getDate("fecha"));
                fila.put("total_boletos_vendidos", rs.getInt("total_boletos_vendidos"));
                fila.put("ingreso_total", rs.getDouble("ingreso_total"));
                fila.put("tipos_sala", rs.getString("tipos_sala"));
                fila.put("formatos", rs.getString("formatos"));
                estadisticas.add(fila);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener estadísticas de barras: " + e.getMessage());
        }

        return estadisticas;

    }

    /**
     * Filtra en memoria los datos de ventas según un rango de fechas.
     *
     * @param datos      Lista de mapas con los datos de ventas a filtrar.
     * @param fechaDesde Fecha de inicio del filtro (formato "yyyy-MM-dd").
     * @param fechaHasta Fecha de fin del filtro (formato "yyyy-MM-dd").
     * @return Una lista de mapas con los datos filtrados por el rango de fechas.
     */
    public List<Map<String, Object>> obtenerFiltradoDeDatos(List<Map<String, Object>> datos, String fechaDesde,
            String fechaHasta) {
        List<Map<String, Object>> datosFiltrados = new ArrayList<>();
        
        System.out.println("Filtrando desde " + fechaDesde + " hasta " + fechaHasta);
        
        LocalDate fechaInicioFiltro = null;
        LocalDate fechaFinFiltro = null;
        
        // Convertir las fechas de filtro de String a LocalDate
        try {
            // Convertir fechas de formato "yyyy-MM-dd" a LocalDate
            if (fechaDesde != null && !fechaDesde.isEmpty()) {
                String[] partes = fechaDesde.split("-");
                if (partes.length == 3) {
                    int year = Integer.parseInt(partes[0]); // Tomar el año completo sin sumar 2000
                    int month = Integer.parseInt(partes[1]);
                    int day = Integer.parseInt(partes[2]);
                    fechaInicioFiltro = LocalDate.of(year, month, day);
                }
            }
            
            if (fechaHasta != null && !fechaHasta.isEmpty()) {
                String[] partes = fechaHasta.split("-");
                if (partes.length == 3) {
                    int year = Integer.parseInt(partes[0]); // Tomar el año completo sin sumar 2000
                    int month = Integer.parseInt(partes[1]);
                    int day = Integer.parseInt(partes[2]);
                    fechaFinFiltro = LocalDate.of(year, month, day);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear fechas de filtro: " + e.getMessage());
        }
        
        System.out.println("Fechas de filtro convertidas: desde " + fechaInicioFiltro + " hasta " + fechaFinFiltro);
        
        for (Map<String, Object> fila : datos) {
            Object fechaObj = fila.get("fecha");
            LocalDate fecha = null;

            if (fechaObj instanceof java.sql.Date) {
            java.sql.Date sqlDate = (java.sql.Date) fechaObj;
            fecha = sqlDate.toLocalDate();
            } else if (fechaObj instanceof LocalDate) {
            fecha = (LocalDate) fechaObj;
            } else if (fechaObj != null) {
            try {
                String fechaStr = fechaObj.toString();
                fecha = LocalDate.parse(fechaStr);
            } catch (Exception e) {
                System.err.println("No se pudo convertir a LocalDate: " + fechaObj);
                continue;
            }
            }

            if (fecha != null && fechaInicioFiltro != null && fechaFinFiltro != null) {
            boolean cumpleFechaInicial = !fecha.isBefore(fechaInicioFiltro);
            boolean cumpleFechaFinal = !fecha.isAfter(fechaFinFiltro);
            
            System.out.println("Verificando " + fecha + 
                       " >= " + fechaInicioFiltro + "? " + cumpleFechaInicial +
                       " | <= " + fechaFinFiltro + "? " + cumpleFechaFinal);
                       
            if (cumpleFechaInicial && cumpleFechaFinal) {
                datosFiltrados.add(fila);
                System.out.println("✅ INCLUIDO en filtro: " + fecha);
            } else {
                System.out.println("❌ EXCLUIDO del filtro: " + fecha);
            }
            }
        }
        
        System.out.println("Total de registros filtrados: " + datosFiltrados.size());
        return datosFiltrados;
    }
}