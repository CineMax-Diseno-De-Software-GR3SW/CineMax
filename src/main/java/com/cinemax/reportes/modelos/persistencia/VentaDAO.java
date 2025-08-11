package com.cinemax.reportes.modelos.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cinemax.utilidades.conexiones.ConexionBaseSingleton;

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
}
