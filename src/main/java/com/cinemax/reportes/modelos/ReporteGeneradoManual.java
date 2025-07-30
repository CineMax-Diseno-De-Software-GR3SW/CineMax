package com.cinemax.reportes.modelos;

import java.time.LocalDateTime;
import java.util.List;

public class ReporteGeneradoManual {
    private String nombreReporte;
    private LocalDateTime fechaGeneracion;
    private String filtrosAplicados;
    private List<ReporteVentaDTO> datos;
    private LocalDateTime desde;
    private LocalDateTime hasta;
    private String horario;

    public ReporteGeneradoManual(String nombreReporte, LocalDateTime fechaGeneracion, 
                                String filtrosAplicados, List<ReporteVentaDTO> datos,
                                LocalDateTime desde, LocalDateTime hasta, String horario) {
        this.nombreReporte = nombreReporte;
        this.fechaGeneracion = fechaGeneracion;
        this.filtrosAplicados = filtrosAplicados;
        this.datos = datos;
        this.desde = desde;
        this.hasta = hasta;
        this.horario = horario;
    }

    // Getters
    public String getNombreReporte() { return nombreReporte; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public String getFiltrosAplicados() { return filtrosAplicados; }
    public List<ReporteVentaDTO> getDatos() { return datos; }
    public LocalDateTime getDesde() { return desde; }
    public LocalDateTime getHasta() { return hasta; }
    public String getHorario() { return horario; }

    // Setters
    public void setNombreReporte(String nombreReporte) { this.nombreReporte = nombreReporte; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public void setFiltrosAplicados(String filtrosAplicados) { this.filtrosAplicados = filtrosAplicados; }
    public void setDatos(List<ReporteVentaDTO> datos) { this.datos = datos; }
    public void setDesde(LocalDateTime desde) { this.desde = desde; }
    public void setHasta(LocalDateTime hasta) { this.hasta = hasta; }
    public void setHorario(String horario) { this.horario = horario; }
} 