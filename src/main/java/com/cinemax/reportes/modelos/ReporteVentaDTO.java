package com.cinemax.reportes.modelos;

public class ReporteVentaDTO {
    public String fecha;
    public int boletosVendidos;
    public double ingresos;
    public String tipoBoleto; // "VIP" o "Normal"
    public String formato; // "2D" o "3D"

    public ReporteVentaDTO(String fecha, int boletosVendidos, double ingresos) {
        this.fecha = fecha;
        this.boletosVendidos = boletosVendidos;
        this.ingresos = ingresos;
        this.tipoBoleto = "Normal"; // Valor por defecto
        this.formato = "2D"; // Valor por defecto
    }

    public ReporteVentaDTO(String fecha, int boletosVendidos, double ingresos, String tipoBoleto, String formato) {
        this.fecha = fecha;
        this.boletosVendidos = boletosVendidos;
        this.ingresos = ingresos;
        this.tipoBoleto = tipoBoleto;
        this.formato = formato;
    }
} 