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
    
    // Método para obtener ventas filtradas con datos reales de la BD
    public List<ReporteVentaDTO> obtenerVentas(LocalDate desde, LocalDate hasta, String sala, String tipoBoleto, String horario) {
        List<ReporteVentaDTO> ventas = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "    TO_CHAR(fun.fecha_hora_inicio, 'YYYY-MM-DD') as fecha, " +
            "    COUNT(b.idboleto) as boletos_vendidos, " +
            "    SUM(f.total) as ingresos, " +
            "    CASE " +
            "        WHEN f.total > 15 THEN 'VIP' " +
            "        ELSE 'Normal' " +
            "    END as tipo_boleto, " +
            "    COALESCE(fun.formato, '2D') as formato " +
            "FROM factura f " +
            "JOIN boleto b ON f.idfactura = b.idfactura " +
            "JOIN funcion fun ON b.idfuncion = fun.id_funcion " +
            "JOIN sala s ON fun.id_sala = s.id " +
            "WHERE fun.fecha_hora_inicio::date BETWEEN ? AND ? "
        );
        
        List<Object> params = new ArrayList<>();
        params.add(desde);
        params.add(hasta);
        
        // Filtro por sala
        if (sala != null && !sala.equalsIgnoreCase("Todas")) {
            sql.append(" AND s.nombre = ? ");
            params.add(sala);
        }
        
        // Filtro por tipo de boleto (basado en el precio)
        if (tipoBoleto != null && !tipoBoleto.equalsIgnoreCase("Todos")) {
            if (tipoBoleto.equalsIgnoreCase("VIP")) {
                sql.append(" AND f.total > 15 ");
            } else if (tipoBoleto.equalsIgnoreCase("Normal")) {
                sql.append(" AND f.total <= 15 ");
            }
        }
        
        // Filtro por horario
        if (horario != null && !horario.equalsIgnoreCase("Todos")) {
            if (horario.equalsIgnoreCase("Matutino")) {
                sql.append(" AND EXTRACT(HOUR FROM fun.fecha_hora_inicio) < 18 ");
            } else if (horario.equalsIgnoreCase("Nocturno")) {
                sql.append(" AND EXTRACT(HOUR FROM fun.fecha_hora_inicio) >= 18 ");
            }
        }
        
        sql.append(" GROUP BY TO_CHAR(fun.fecha_hora_inicio, 'YYYY-MM-DD'), " +
                "CASE WHEN f.total > 15 THEN 'VIP' ELSE 'Normal' END, fun.formato " +
                "ORDER BY fecha DESC");
        
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
                    String tipoBoletoDB = rs.getString("tipo_boleto");
                    String formato = rs.getString("formato");
                    
                    ventas.add(new ReporteVentaDTO(fecha, boletosVendidos, ingresos, tipoBoletoDB, formato));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener ventas: " + e.getMessage());
        }
        
        return ventas;
    }
    
    // Método para obtener ventas por película
    public List<ReporteVentaDTO> obtenerVentasPorPelicula(LocalDate desde, LocalDate hasta) {
        List<ReporteVentaDTO> ventasPeliculas = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    p.titulo as pelicula, " +
                     "    COUNT(DISTINCT fun.id_funcion) as funciones, " +
                     "    COUNT(b.idboleto) as boletos_vendidos, " +
                     "    SUM(f.total) as ingresos, " +
                     "    AVG(f.total) as precio_promedio " +
                     "FROM factura f " +
                     "JOIN boleto b ON f.idfactura = b.idfactura " +
                     "JOIN funcion fun ON b.idfuncion = fun.id_funcion " +
                     "JOIN pelicula p ON fun.id_pelicula = p.id " +
                     "WHERE fun.fecha_hora_inicio::date BETWEEN ? AND ? " +
                     "GROUP BY p.titulo, p.id " +
                     "ORDER BY ingresos DESC";
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String pelicula = rs.getString("pelicula");
                    int funciones = rs.getInt("funciones");
                    int boletosVendidos = rs.getInt("boletos_vendidos");
                    double ingresos = rs.getDouble("ingresos");
                    double precioPromedio = rs.getDouble("precio_promedio");
                    
                    // Usamos el constructor con película como fecha para reutilizar el DTO
                    ReporteVentaDTO ventaPelicula = new ReporteVentaDTO(
                        pelicula, 
                        boletosVendidos, 
                        ingresos, 
                        String.valueOf(funciones), 
                        String.format("$%.2f", precioPromedio)
                    );
                    ventasPeliculas.add(ventaPelicula);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener ventas por película: " + e.getMessage());
        }
        
        return ventasPeliculas;
    }
    
    // Método para obtener ventas por sala
    public List<ReporteVentaDTO> obtenerVentasPorSala(LocalDate desde, LocalDate hasta) {
        List<ReporteVentaDTO> ventasSalas = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    s.nombre as sala, " +
                     "    s.capacidad, " +
                     "    COUNT(b.idboleto) as boletos_vendidos, " +
                     "    SUM(f.total) as ingresos, " +
                     "    ROUND((COUNT(b.idboleto)::decimal / s.capacidad) * 100, 2) as ocupacion_promedio " +
                     "FROM sala s " +
                     "LEFT JOIN funcion fun ON s.id = fun.id_sala " +
                     "LEFT JOIN boleto b ON fun.id_funcion = b.idfuncion " +
                     "LEFT JOIN factura f ON b.idfactura = f.idfactura " +
                     "WHERE fun.fecha_hora_inicio::date BETWEEN ? AND ? " +
                     "GROUP BY s.id, s.nombre, s.capacidad " +
                     "ORDER BY ingresos DESC";
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String sala = rs.getString("sala");
                    int capacidad = rs.getInt("capacidad");
                    int boletosVendidos = rs.getInt("boletos_vendidos");
                    double ingresos = rs.getDouble("ingresos");
                    double ocupacion = rs.getDouble("ocupacion_promedio");
                    
                    ReporteVentaDTO ventaSala = new ReporteVentaDTO(
                        sala, 
                        boletosVendidos, 
                        ingresos, 
                        String.valueOf(capacidad), 
                        String.format("%.2f%%", ocupacion)
                    );
                    ventasSalas.add(ventaSala);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener ventas por sala: " + e.getMessage());
        }
        
        return ventasSalas;
    }
    
    // Método para obtener estadísticas de ocupación
    public List<ReporteVentaDTO> obtenerEstadisticasOcupacion(LocalDate desde, LocalDate hasta) {
        List<ReporteVentaDTO> estadisticas = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    TO_CHAR(fun.fecha_hora_inicio, 'YYYY-MM-DD') as fecha, " +
                     "    COUNT(DISTINCT fun.id_funcion) as total_funciones, " +
                     "    SUM(s.capacidad) as capacidad_total, " +
                     "    COUNT(b.idboleto) as boletos_vendidos, " +
                     "    ROUND((COUNT(b.idboleto)::decimal / SUM(s.capacidad)) * 100, 2) as ocupacion_dia " +
                     "FROM funcion fun " +
                     "JOIN sala s ON fun.id_sala = s.id " +
                     "LEFT JOIN boleto b ON fun.id_funcion = b.idfuncion " +
                     "WHERE fun.fecha_hora_inicio::date BETWEEN ? AND ? " +
                     "GROUP BY TO_CHAR(fun.fecha_hora_inicio, 'YYYY-MM-DD') " +
                     "ORDER BY fecha DESC";
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String fecha = rs.getString("fecha");
                    int funciones = rs.getInt("total_funciones");
                    int capacidadTotal = rs.getInt("capacidad_total");
                    int boletosVendidos = rs.getInt("boletos_vendidos");
                    double ocupacion = rs.getDouble("ocupacion_dia");
                    
                    ReporteVentaDTO estadistica = new ReporteVentaDTO(
                        fecha, 
                        boletosVendidos, 
                        ocupacion, // Usamos ingresos para guardar el porcentaje de ocupación
                        String.valueOf(funciones), 
                        String.valueOf(capacidadTotal)
                    );
                    estadisticas.add(estadistica);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener estadísticas de ocupación: " + e.getMessage());
        }
        
        return estadisticas;
    }
    
    // Método para obtener top películas más vendidas
    public List<ReporteVentaDTO> obtenerTopPeliculas(LocalDate desde, LocalDate hasta, int limite) {
        List<ReporteVentaDTO> topPeliculas = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    p.titulo, " +
                     "    p.genero, " +
                     "    COUNT(b.idboleto) as boletos_vendidos, " +
                     "    SUM(f.total) as ingresos_totales, " +
                     "    COUNT(DISTINCT fun.id_funcion) as funciones_realizadas " +
                     "FROM pelicula p " +
                     "JOIN funcion fun ON p.id = fun.id_pelicula " +
                     "JOIN boleto b ON fun.id_funcion = b.idfuncion " +
                     "JOIN factura f ON b.idfactura = f.idfactura " +
                     "WHERE fun.fecha_hora_inicio::date BETWEEN ? AND ? " +
                     "GROUP BY p.id, p.titulo, p.genero " +
                     "ORDER BY boletos_vendidos DESC " +
                     "LIMIT ?";
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            ps.setInt(3, limite);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String titulo = rs.getString("titulo");
                    String genero = rs.getString("genero");
                    int boletosVendidos = rs.getInt("boletos_vendidos");
                    double ingresos = rs.getDouble("ingresos_totales");
                    int funciones = rs.getInt("funciones_realizadas");
                    
                    ReporteVentaDTO topPelicula = new ReporteVentaDTO(
                        titulo, 
                        boletosVendidos, 
                        ingresos, 
                        genero, 
                        String.valueOf(funciones)
                    );
                    topPeliculas.add(topPelicula);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener top películas: " + e.getMessage());
        }
        
        return topPeliculas;
    }
    
    // Método para obtener ventas por horario
    public List<ReporteVentaDTO> obtenerVentasPorHorario(LocalDate desde, LocalDate hasta) {
        List<ReporteVentaDTO> ventasHorario = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    CASE " +
                     "        WHEN EXTRACT(HOUR FROM fun.fecha_hora_inicio) < 12 THEN 'Matutino (6AM-12PM)' " +
                     "        WHEN EXTRACT(HOUR FROM fun.fecha_hora_inicio) < 18 THEN 'Vespertino (12PM-6PM)' " +
                     "        ELSE 'Nocturno (6PM-12AM)' " +
                     "    END as horario, " +
                     "    COUNT(b.idboleto) as boletos_vendidos, " +
                     "    SUM(f.total) as ingresos, " +
                     "    COUNT(DISTINCT fun.id_funcion) as funciones, " +
                     "    AVG(f.total) as precio_promedio " +
                     "FROM factura f " +
                     "JOIN boleto b ON f.idfactura = b.idfactura " +
                     "JOIN funcion fun ON b.idfuncion = fun.id_funcion " +
                     "WHERE fun.fecha_hora_inicio::date BETWEEN ? AND ? " +
                     "GROUP BY " +
                     "    CASE " +
                     "        WHEN EXTRACT(HOUR FROM fun.fecha_hora_inicio) < 12 THEN 'Matutino (6AM-12PM)' " +
                     "        WHEN EXTRACT(HOUR FROM fun.fecha_hora_inicio) < 18 THEN 'Vespertino (12PM-6PM)' " +
                     "        ELSE 'Nocturno (6PM-12AM)' " +
                     "    END " +
                     "ORDER BY ingresos DESC";
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String horario = rs.getString("horario");
                    int boletosVendidos = rs.getInt("boletos_vendidos");
                    double ingresos = rs.getDouble("ingresos");
                    int funciones = rs.getInt("funciones");
                    double precioPromedio = rs.getDouble("precio_promedio");
                    
                    ReporteVentaDTO ventaHorario = new ReporteVentaDTO(
                        horario, 
                        boletosVendidos, 
                        ingresos, 
                        String.valueOf(funciones), 
                        String.format("$%.2f", precioPromedio)
                    );
                    ventasHorario.add(ventaHorario);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener ventas por horario: " + e.getMessage());
        }
        
        return ventasHorario;
    }
    
    // Método para obtener resumen ejecutivo
    public ReporteVentaDTO obtenerResumenEjecutivo(LocalDate desde, LocalDate hasta) {
        String sql = "SELECT " +
                     "    COUNT(DISTINCT b.idboleto) as total_boletos, " +
                     "    SUM(f.total) as ingresos_totales, " +
                     "    COUNT(DISTINCT f.idfactura) as total_transacciones, " +
                     "    COUNT(DISTINCT fun.id_funcion) as total_funciones, " +
                     "    AVG(f.total) as ticket_promedio " +
                     "FROM factura f " +
                     "JOIN boleto b ON f.idfactura = b.idfactura " +
                     "JOIN funcion fun ON b.idfuncion = fun.id_funcion " +
                     "WHERE fun.fecha_hora_inicio::date BETWEEN ? AND ?";
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int totalBoletos = rs.getInt("total_boletos");
                    double ingresosTotales = rs.getDouble("ingresos_totales");
                    int totalTransacciones = rs.getInt("total_transacciones");
                    int totalFunciones = rs.getInt("total_funciones");
                    double ticketPromedio = rs.getDouble("ticket_promedio");
                    
                    return new ReporteVentaDTO(
                        "RESUMEN_EJECUTIVO", 
                        totalBoletos, 
                        ingresosTotales, 
                        String.valueOf(totalTransacciones), 
                        String.format("%.2f", ticketPromedio)
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener resumen ejecutivo: " + e.getMessage());
        }
        
        return new ReporteVentaDTO("ERROR", 0, 0.0, "0", "0.00");
    }
    
    // Método para obtener salas disponibles
    public List<String> obtenerSalas() {
        List<String> salas = new ArrayList<>();
        salas.add("Todas"); // Opción por defecto
        
        String sql = "SELECT DISTINCT nombre FROM sala ORDER BY nombre";
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                salas.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener salas: " + e.getMessage());
        }
        
        return salas;
    }
    
    // Método para obtener géneros disponibles
    public List<String> obtenerGeneros() {
        List<String> generos = new ArrayList<>();
        generos.add("Todos"); // Opción por defecto
        
        String sql = "SELECT DISTINCT genero FROM pelicula WHERE genero IS NOT NULL ORDER BY genero";
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String genero = rs.getString("genero");
                if (genero != null && !genero.trim().isEmpty()) {
                    generos.add(genero);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener géneros: " + e.getMessage());
        }
        
        return generos;
    }
    
    // Método para validar conexión a la base de datos
    public boolean validarConexion() {
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error al validar conexión: " + e.getMessage());
            return false;
        }
    }
    
    // Método para obtener información de la última actualización
    public String obtenerUltimaActualizacion() {
        String sql = "SELECT MAX(fun.fecha_hora_inicio) as ultima_funcion " +
                     "FROM funcion fun " +
                     "JOIN boleto b ON fun.id_funcion = b.idfuncion";
        
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getString("ultima_funcion");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener última actualización: " + e.getMessage());
        }
        
        return "No disponible";
    }
}