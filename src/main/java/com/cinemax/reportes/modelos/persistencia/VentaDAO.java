package com.cinemax.reportes.modelos.persistencia;

import com.cinemax.comun.ConexionBaseSingleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase de acceso a datos (DAO - Data Access Object) para operaciones relacionadas con ventas.
 * Implementa el patrón DAO para encapsular el acceso a la base de datos y proporcionar
 * una interfaz limpia para consultas de ventas, estadísticas e información del sistema.
 * 
 * Esta clase maneja:
 * - Consultas de resumen de ventas generales y por período
 * - Estadísticas para gráficos y reportes
 * - Información de clientes destacados
 * - Verificación de conectividad y metadatos del sistema
 */
public class VentaDAO {

    /**
     * Obtiene un resumen completo de todas las ventas del sistema.
     * Consulta datos agregados de boletos vendidos, facturas, ingresos totales,
     * funciones realizadas y rango de fechas de actividad.
     * 
     * @return Map<String, Object> con las siguientes claves:
     *         - total_boletos_vendidos: Número total de boletos vendidos
     *         - total_facturas: Número total de transacciones de venta
     *         - ingreso_total: Suma total de ingresos generados
     *         - total_funciones: Número total de funciones de cine realizadas
     *         - fecha_inicio: Fecha de la primera función registrada
     *         - fecha_fin: Fecha de la última función registrada
     */
    // Método principal para obtener resumen de ventas
    public Map<String, Object> obtenerResumenVentas() {
        // Consulta SQL que obtiene estadísticas agregadas de ventas
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
        
        // Usar try-with-resources para manejo automático de recursos de base de datos
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Procesar el resultado de la consulta
            if (rs.next()) {
                // Extraer y mapear todos los valores de la consulta
                resumen.put("total_boletos_vendidos", rs.getInt("total_boletos_vendidos"));
                resumen.put("total_facturas", rs.getInt("total_facturas"));
                resumen.put("ingreso_total", rs.getDouble("ingreso_total"));
                resumen.put("total_funciones", rs.getInt("total_funciones"));
                // Manejar posibles valores null en las fechas
                resumen.put("fecha_inicio", 
                    rs.getString("fecha_inicio") != null ? rs.getString("fecha_inicio") : "N/A");
                resumen.put("fecha_fin", 
                    rs.getString("fecha_fin") != null ? rs.getString("fecha_fin") : "N/A");
            } else {
                // Proporcionar valores por defecto si no hay datos en la base de datos
                resumen.put("total_boletos_vendidos", 0);
                resumen.put("total_facturas", 0);
                resumen.put("ingreso_total", 0.0);
                resumen.put("total_funciones", 0);
                resumen.put("fecha_inicio", "N/A");
                resumen.put("fecha_fin", "N/A");
            }
        } catch (SQLException e) {
            // Registrar error para depuración
            e.printStackTrace();
            System.err.println("Error al obtener resumen de ventas: " + e.getMessage());
            
            // Retornar valores por defecto con indicador de error
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
     * Obtiene un resumen de ventas filtrado por un período de fechas específico.
     * Proporciona estadísticas más detalladas incluyendo información sobre
     * películas diferentes y precios promedio.
     * 
     * @param fechaInicio Fecha de inicio del período a consultar
     * @param fechaFin Fecha de fin del período a consultar
     * @return Map<String, Object> con estadísticas del período especificado:
     *         - total_boletos_vendidos: Boletos vendidos en el período
     *         - total_facturas: Facturas generadas en el período
     *         - ingreso_total: Ingresos totales del período
     *         - total_funciones: Funciones realizadas en el período
     *         - peliculas_diferentes: Número de películas distintas exhibidas
     *         - precio_promedio: Precio promedio de las transacciones
     *         - fecha_inicio/fecha_fin: Fechas del período consultado
     */
    // Método para obtener resumen de ventas por período específico
    public Map<String, Object> obtenerResumenVentasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        // Consulta SQL parametrizada para filtrar por rango de fechas
        String sql = "SELECT " +
                "COUNT(DISTINCT b.idboleto) AS total_boletos_vendidos, " +
                "COUNT(DISTINCT f.idfactura) AS total_facturas, " +
                "COALESCE(SUM(f.total), 0) AS ingreso_total, " +
                "COUNT(DISTINCT fun.id_funcion) AS total_funciones, " +
                "COUNT(DISTINCT p.id) AS peliculas_diferentes, " +
                "AVG(f.total) AS precio_promedio " +
                "FROM factura f " +
                "JOIN boleto b ON f.idfactura = b.idfactura " +
                "JOIN funcion fun ON b.idfuncion = fun.id_funcion " +
                "JOIN pelicula p ON fun.id_pelicula = p.id " +
                "WHERE fun.fecha_hora_inicio::date BETWEEN ? AND ?";

        Map<String, Object> resumen = new HashMap<>();
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Establecer los parámetros de fecha en la consulta preparada
            stmt.setObject(1, fechaInicio);
            stmt.setObject(2, fechaFin);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Mapear todos los resultados de la consulta
                    resumen.put("total_boletos_vendidos", rs.getInt("total_boletos_vendidos"));
                    resumen.put("total_facturas", rs.getInt("total_facturas"));
                    resumen.put("ingreso_total", rs.getDouble("ingreso_total"));
                    resumen.put("total_funciones", rs.getInt("total_funciones"));
                    resumen.put("peliculas_diferentes", rs.getInt("peliculas_diferentes"));
                    resumen.put("precio_promedio", rs.getDouble("precio_promedio"));
                    resumen.put("fecha_inicio", fechaInicio.toString());
                    resumen.put("fecha_fin", fechaFin.toString());
                } else {
                    // Valores por defecto si no hay datos en el período especificado
                    resumen.put("total_boletos_vendidos", 0);
                    resumen.put("total_facturas", 0);
                    resumen.put("ingreso_total", 0.0);
                    resumen.put("total_funciones", 0);
                    resumen.put("peliculas_diferentes", 0);
                    resumen.put("precio_promedio", 0.0);
                    resumen.put("fecha_inicio", fechaInicio.toString());
                    resumen.put("fecha_fin", fechaFin.toString());
                }
            }
        } catch (SQLException e) {
            // Registrar error sin interrumpir la ejecución
            e.printStackTrace();
            System.err.println("Error al obtener resumen de ventas por período: " + e.getMessage());
        }
        
        return resumen;
    }

    /**
     * Obtiene estadísticas de ventas agrupadas por fecha para generar gráficos de barras.
     * Limita los resultados a los últimos 7 registros para mantener la visualización manejable.
     * 
     * @return List<Map<String, Object>> donde cada mapa representa un día con:
     *         - fecha: Fecha del registro
     *         - total_boletos_vendidos: Boletos vendidos ese día
     *         - ingreso_total: Ingresos generados ese día
     *         - tipos_sala: Tipos de salas utilizadas (concatenados)
     *         - formatos: Formatos de película utilizados (concatenados)
     */
    public List<Map<String, Object>> obtenerEstadisticasDeBarras() {
        // Consulta SQL que agrupa por fecha y concatena tipos de sala y formatos
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
            "LIMIT 7"; // Limitar a 7 registros para gráficos manejables

        List<Map<String, Object>> estadisticas = new ArrayList<>();

        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Procesar cada fila del resultado
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
            // Registrar error y retornar lista vacía para evitar fallos en la aplicación
            e.printStackTrace();
            System.err.println("Error al obtener estadísticas de barras: " + e.getMessage());
        }

        return estadisticas;
    }

    /**
     * Obtiene estadísticas adicionales del sistema para dashboards y reportes avanzados.
     * Incluye información sobre infraestructura (salas, películas) y clasificación
     * de boletos por tipo y formato.
     * 
     * @return Map<String, Object> con estadísticas del sistema:
     *         - total_salas: Número total de salas disponibles
     *         - total_peliculas_activas: Películas en cartelera
     *         - total_clientes_unicos: Clientes diferentes que han comprado
     *         - boletos_vip: Boletos vendidos con precio superior a $15
     *         - boletos_normales: Boletos vendidos con precio $15 o menor
     *         - funciones_3d: Funciones en formato 3D
     *         - funciones_2d: Funciones en formato 2D o sin especificar
     */
    // Método para obtener estadísticas adicionales
    public Map<String, Object> obtenerEstadisticasAdicionales() {
        // Consulta compleja que obtiene estadísticas variadas del sistema
        String sql = "SELECT " +
                "COUNT(DISTINCT s.id) AS total_salas, " +
                "COUNT(DISTINCT p.id) AS total_peliculas_activas, " +
                "COUNT(DISTINCT c.idcliente) AS total_clientes_unicos, " +
                // Clasificar boletos por precio (VIP vs Normal)
                "SUM(CASE WHEN f.total > 15 THEN 1 ELSE 0 END) AS boletos_vip, " +
                "SUM(CASE WHEN f.total <= 15 THEN 1 ELSE 0 END) AS boletos_normales, " +
                // Clasificar funciones por formato
                "COUNT(CASE WHEN fun.formato = '3D' THEN 1 END) AS funciones_3d, " +
                "COUNT(CASE WHEN fun.formato = '2D' OR fun.formato IS NULL THEN 1 END) AS funciones_2d " +
                "FROM sala s " +
                "CROSS JOIN pelicula p " + // Producto cartesiano para contar totales
                "LEFT JOIN funcion fun ON p.id = fun.id_pelicula " +
                "LEFT JOIN boleto b ON fun.id_funcion = b.idfuncion " +
                "LEFT JOIN factura f ON b.idfactura = f.idfactura " +
                "LEFT JOIN cliente c ON f.idcliente = c.idcliente";

        Map<String, Object> estadisticas = new HashMap<>();
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                // Mapear todos los resultados estadísticos
                estadisticas.put("total_salas", rs.getInt("total_salas"));
                estadisticas.put("total_peliculas_activas", rs.getInt("total_peliculas_activas"));
                estadisticas.put("total_clientes_unicos", rs.getInt("total_clientes_unicos"));
                estadisticas.put("boletos_vip", rs.getInt("boletos_vip"));
                estadisticas.put("boletos_normales", rs.getInt("boletos_normales"));
                estadisticas.put("funciones_3d", rs.getInt("funciones_3d"));
                estadisticas.put("funciones_2d", rs.getInt("funciones_2d"));
            }
        } catch (SQLException e) {
            // Registrar error para análisis posterior
            e.printStackTrace();
            System.err.println("Error al obtener estadísticas adicionales: " + e.getMessage());
        }
        
        return estadisticas;
    }

    /**
     * Obtiene los clientes que más han gastado en el sistema.
     * Útil para identificar clientes VIP y generar reportes de marketing.
     * 
     * @param limite Número máximo de clientes a retornar en el ranking
     * @return Map<String, Object> con claves "cliente_X" donde X es la posición,
     *         y cada valor es un mapa con:
     *         - nombre_completo: Nombre y apellido del cliente
     *         - total_compras: Número de transacciones realizadas
     *         - total_gastado: Monto total gastado por el cliente
     */
    // Método para obtener el top de clientes
    public Map<String, Object> obtenerTopClientes(int limite) {
        // Consulta que ordena clientes por gasto total descendente
        String sql = "SELECT " +
                "c.nombre, " +
                "c.apellido, " +
                "COUNT(f.idfactura) AS total_compras, " +
                "SUM(f.total) AS total_gastado " +
                "FROM cliente c " +
                "JOIN factura f ON c.idcliente = f.idcliente " +
                "GROUP BY c.idcliente, c.nombre, c.apellido " +
                "ORDER BY total_gastado DESC " +
                "LIMIT ?"; // Parámetro para limitar resultados

        Map<String, Object> topClientes = new HashMap<>();
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Establecer el límite de resultados
            stmt.setInt(1, limite);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int posicion = 1;
                while (rs.next()) {
                    // Crear un mapa para cada cliente con su información
                    Map<String, Object> cliente = new HashMap<>();
                    cliente.put("nombre_completo", rs.getString("nombre") + " " + rs.getString("apellido"));
                    cliente.put("total_compras", rs.getInt("total_compras"));
                    cliente.put("total_gastado", rs.getDouble("total_gastado"));
                    
                    // Usar la posición como clave para mantener el ranking
                    topClientes.put("cliente_" + posicion, cliente);
                    posicion++;
                }
            }
        } catch (SQLException e) {
            // Registrar error sin interrumpir la aplicación
            e.printStackTrace();
            System.err.println("Error al obtener top clientes: " + e.getMessage());
        }
        
        return topClientes;
    }

    /**
     * Verifica si la conexión a la base de datos está activa y funcionando.
     * Método útil para diagnósticos y verificaciones de salud del sistema.
     * 
     * @return true si la conexión está activa y operativa, false en caso contrario
     */
    // Método para verificar el estado de la conexión
    public boolean verificarConexion() {
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion()) {
            // Verificar que la conexión exista y no esté cerrada
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error al verificar conexión: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene información detallada sobre el sistema de base de datos.
     * Incluye metadatos de conexión, versión de BD y driver utilizado.
     * Útil para reportes de sistema y diagnósticos técnicos.
     * 
     * @return Map<String, Object> con información del sistema:
     *         - conexion_activa: boolean indicando si hay conexión
     *         - base_datos: Nombre del sistema de base de datos
     *         - version_bd: Versión de la base de datos
     *         - driver: Nombre del driver JDBC utilizado
     *         - url_conexion: URL de conexión a la base de datos
     *         - error: Mensaje de error (solo si hay problemas)
     */
    // Método para obtener información del sistema
    public Map<String, Object> obtenerInfoSistema() {
        Map<String, Object> info = new HashMap<>();
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion()) {
            if (conn != null) {
                // Obtener metadatos de la conexión para información del sistema
                info.put("conexion_activa", true);
                info.put("base_datos", conn.getMetaData().getDatabaseProductName());
                info.put("version_bd", conn.getMetaData().getDatabaseProductVersion());
                info.put("driver", conn.getMetaData().getDriverName());
                info.put("url_conexion", conn.getMetaData().getURL());
            } else {
                // Indicar falta de conexión
                info.put("conexion_activa", false);
                info.put("error", "No se pudo establecer conexión");
            }
        } catch (SQLException e) {
            // Registrar error y proporcionar información de falla
            e.printStackTrace();
            info.put("conexion_activa", false);
            info.put("error", e.getMessage());
        }
        
        return info;
    }
}