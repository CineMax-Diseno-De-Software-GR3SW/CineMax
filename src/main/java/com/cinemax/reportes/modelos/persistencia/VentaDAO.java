package com.cinemax.reportes.modelos.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class VentaDAO {

    private static final String URL = "jdbc:postgresql://tramway.proxy.rlwy.net:18687/railway";
    private static final String USER = "postgres";
    private static final String PASSWORD = "tTCOwIHvtDnIJQZjalEcwvKbbKhGQJvl";

    public Map<String, Object> obtenerResumenVentas() {
        String sql = "SELECT " +
                "COUNT(DISTINCT b.idboleto) AS total_boletos_vendidos, " +
                "COUNT(DISTINCT f.idfactura) AS total_facturas, " +
                "SUM(f.total) AS ingreso_total, " +
                "COUNT(DISTINCT fun.id_funcion) AS total_funciones, " +
                "TO_CHAR(MIN(fun.fecha_hora_inicio), 'YY-DD-MM') AS fecha_inicio, " +
                "TO_CHAR(MAX(fun.fecha_hora_fin), 'YY-DD-MM') AS fecha_fin " +
                "FROM factura f " +
                "JOIN boleto b ON f.idfactura = b.idfactura " +
                "JOIN funcion fun ON b.idfuncion = fun.id_funcion;";

        Map<String, Object> resumen = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                resumen.put("total_boletos_vendidos", rs.getInt("total_boletos_vendidos"));
                resumen.put("total_facturas", rs.getInt("total_facturas"));
                resumen.put("ingreso_total", rs.getDouble("ingreso_total"));
                resumen.put("total_funciones", rs.getInt("total_funciones"));
                resumen.put("fecha_inicio",
                        rs.getString("fecha_inicio") != null ? rs.getString("fecha_inicio") : "");
                resumen.put("fecha_fin",
                        rs.getString("fecha_fin") != null ? rs.getString("fecha_fin") : "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resumen;
    }

}
