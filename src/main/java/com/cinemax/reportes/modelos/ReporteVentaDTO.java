package com.cinemax.reportes.modelos;

public class ReporteVentaDTO {
    public String fecha;
    public int boletosVendidos;
    public double ingresos;

    public ReporteVentaDTO(String fecha, int boletosVendidos, double ingresos) {
        this.fecha = fecha;
        this.boletosVendidos = boletosVendidos;
        this.ingresos = ingresos;
    }
} 