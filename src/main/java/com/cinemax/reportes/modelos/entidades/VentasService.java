package com.cinemax.reportes.modelos.entidades;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import com.cinemax.reportes.modelos.persistencia.VentaDAO;

public class VentasService {
    private VentaDAO ventasDAO;

    public VentasService() {
        ventasDAO = new VentaDAO();
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
}