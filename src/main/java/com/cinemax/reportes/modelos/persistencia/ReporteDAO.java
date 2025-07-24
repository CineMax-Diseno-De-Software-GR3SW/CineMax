package com.cinemax.reportes.modelos.persistencia;

import com.cinemax.comun.ConexionBaseSingleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.cinemax.reportes.modelos.ReporteVentaDTO;

public class ReporteDAO {
    // Método para obtener ventas filtradas (ajusta la consulta y columnas según tu BD)
    public List<ReporteVentaDTO> obtenerVentas(LocalDate desde, LocalDate hasta, String sala, String tipoBoleto, String horario) {
        List<ReporteVentaDTO> ventas = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT fecha, boletos_vendidos, ingresos FROM ventas WHERE fecha BETWEEN ? AND ?");
        List<Object> params = new ArrayList<>();
        params.add(desde);
        params.add(hasta);
        if (sala != null && !sala.equalsIgnoreCase("Todas")) {
            sql.append(" AND sala = ?");
            params.add(sala);
        }
        if (tipoBoleto != null && !tipoBoleto.equalsIgnoreCase("Todos")) {
            sql.append(" AND tipo_boleto = ?");
            params.add(tipoBoleto);
        }
        if (horario != null && !horario.equalsIgnoreCase("Todos")) {
            sql.append(" AND horario = ?");
            params.add(horario);
        }
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String fecha = rs.getString("fecha");
                    int boletosVendidos = rs.getInt("boletos_vendidos");
                    double ingresos = rs.getDouble("ingresos");
                    ventas.add(new ReporteVentaDTO(fecha, boletosVendidos, ingresos));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventas;
    }
} 