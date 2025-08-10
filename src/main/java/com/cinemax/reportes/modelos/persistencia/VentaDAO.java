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

public class VentaDAO {

    // Método principal para obtener resumen de ventas
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

    // Método para obtener resumen de ventas por período específico
    public Map<String, Object> obtenerResumenVentasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
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
            
            stmt.setObject(1, fechaInicio);
            stmt.setObject(2, fechaFin);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    resumen.put("total_boletos_vendidos", rs.getInt("total_boletos_vendidos"));
                    resumen.put("total_facturas", rs.getInt("total_facturas"));
                    resumen.put("ingreso_total", rs.getDouble("ingreso_total"));
                    resumen.put("total_funciones", rs.getInt("total_funciones"));
                    resumen.put("peliculas_diferentes", rs.getInt("peliculas_diferentes"));
                    resumen.put("precio_promedio", rs.getDouble("precio_promedio"));
                    resumen.put("fecha_inicio", fechaInicio.toString());
                    resumen.put("fecha_fin", fechaFin.toString());
                } else {
                    // Valores por defecto si no hay datos en el período
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
            e.printStackTrace();
            System.err.println("Error al obtener resumen de ventas por período: " + e.getMessage());
        }
        
        return resumen;
    }


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

    // Método para obtener estadísticas adicionales
    public Map<String, Object> obtenerEstadisticasAdicionales() {
        String sql = "SELECT " +
                "COUNT(DISTINCT s.id) AS total_salas, " +
                "COUNT(DISTINCT p.id) AS total_peliculas_activas, " +
                "COUNT(DISTINCT c.idcliente) AS total_clientes_unicos, " +
                "SUM(CASE WHEN f.total > 15 THEN 1 ELSE 0 END) AS boletos_vip, " +
                "SUM(CASE WHEN f.total <= 15 THEN 1 ELSE 0 END) AS boletos_normales, " +
                "COUNT(CASE WHEN fun.formato = '3D' THEN 1 END) AS funciones_3d, " +
                "COUNT(CASE WHEN fun.formato = '2D' OR fun.formato IS NULL THEN 1 END) AS funciones_2d " +
                "FROM sala s " +
                "CROSS JOIN pelicula p " +
                "LEFT JOIN funcion fun ON p.id = fun.id_pelicula " +
                "LEFT JOIN boleto b ON fun.id_funcion = b.idfuncion " +
                "LEFT JOIN factura f ON b.idfactura = f.idfactura " +
                "LEFT JOIN cliente c ON f.idcliente = c.idcliente";

        Map<String, Object> estadisticas = new HashMap<>();
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                estadisticas.put("total_salas", rs.getInt("total_salas"));
                estadisticas.put("total_peliculas_activas", rs.getInt("total_peliculas_activas"));
                estadisticas.put("total_clientes_unicos", rs.getInt("total_clientes_unicos"));
                estadisticas.put("boletos_vip", rs.getInt("boletos_vip"));
                estadisticas.put("boletos_normales", rs.getInt("boletos_normales"));
                estadisticas.put("funciones_3d", rs.getInt("funciones_3d"));
                estadisticas.put("funciones_2d", rs.getInt("funciones_2d"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener estadísticas adicionales: " + e.getMessage());
        }
        
        return estadisticas;
    }

    // Método para obtener el top de clientes
    public Map<String, Object> obtenerTopClientes(int limite) {
        String sql = "SELECT " +
                "c.nombre, " +
                "c.apellido, " +
                "COUNT(f.idfactura) AS total_compras, " +
                "SUM(f.total) AS total_gastado " +
                "FROM cliente c " +
                "JOIN factura f ON c.idcliente = f.idcliente " +
                "GROUP BY c.idcliente, c.nombre, c.apellido " +
                "ORDER BY total_gastado DESC " +
                "LIMIT ?";

        Map<String, Object> topClientes = new HashMap<>();
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limite);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int posicion = 1;
                while (rs.next()) {
                    Map<String, Object> cliente = new HashMap<>();
                    cliente.put("nombre_completo", rs.getString("nombre") + " " + rs.getString("apellido"));
                    cliente.put("total_compras", rs.getInt("total_compras"));
                    cliente.put("total_gastado", rs.getDouble("total_gastado"));
                    
                    topClientes.put("cliente_" + posicion, cliente);
                    posicion++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener top clientes: " + e.getMessage());
        }
        
        return topClientes;
    }

    // Método para verificar el estado de la conexión
    public boolean verificarConexion() {
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error al verificar conexión: " + e.getMessage());
            return false;
        }
    }

    // Método para obtener información del sistema
    public Map<String, Object> obtenerInfoSistema() {
        Map<String, Object> info = new HashMap<>();
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion()) {
            if (conn != null) {
                info.put("conexion_activa", true);
                info.put("base_datos", conn.getMetaData().getDatabaseProductName());
                info.put("version_bd", conn.getMetaData().getDatabaseProductVersion());
                info.put("driver", conn.getMetaData().getDriverName());
                info.put("url_conexion", conn.getMetaData().getURL());
            } else {
                info.put("conexion_activa", false);
                info.put("error", "No se pudo establecer conexión");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            info.put("conexion_activa", false);
            info.put("error", e.getMessage());
        }
        
        return info;
    }
}
