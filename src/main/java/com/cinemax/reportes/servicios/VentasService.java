package com.cinemax.reportes.servicios;
import java.util.Map;
import com.cinemax.reportes.modelos.persistencia.VentaDAO;



public class VentasService {
    private VentaDAO ventasDAO;

    public VentasService() {
        ventasDAO = new VentaDAO();
    }

    public Map<String, Object> getResumenDeVentas() {
        return ventasDAO.obtenerResumenVentas();
    }

    public Map<String, Object> getVentasPorFecha(String fechaInicio, String fechaFin) {
        return null;
        //return ventasDAO.obtenerVentasPorFecha(fechaInicio, fechaFin);
    }

}
