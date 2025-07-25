package com.cinemax.peliculas.modelos.entidades;

import java.time.LocalDateTime;
import com.cinemax.salas.modelos.entidades.Sala;

public class Funcion {
    private int id;
    private Pelicula pelicula;
    private Sala sala;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private FormatoFuncion formato;
    private TipoEstreno tipoEstreno;

    public Funcion(int id, Pelicula pelicula, Sala sala, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, FormatoFuncion formato, TipoEstreno tipoEstreno) {
        this.id = id;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.formato = formato;
        this.tipoEstreno = tipoEstreno;
    }

    public int getId() { return id; }
    public Pelicula getPelicula() { return pelicula; }
    public Sala getSala() { return sala; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public FormatoFuncion getFormato() { return formato; }
    public TipoEstreno getTipoEstreno() { return tipoEstreno; }

    public void setId(int id) { this.id = id; }
    public void setPelicula(Pelicula pelicula) { this.pelicula = pelicula; }
    public void setSala(Sala sala) { this.sala = sala; }
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }
    public void setFormato(FormatoFuncion formato) { this.formato = formato; }
    public void setTipoEstreno(TipoEstreno tipoEstreno) { this.tipoEstreno = tipoEstreno; }

    /**
     * Obtiene el día de la semana de esta función para cálculos de precios futuros
     * @return El enum DiaSemana correspondiente al día de inicio de la función
     */
    public DiaSemana getDiaSemana() {
        return DiaSemana.obtenerDiaDeFecha(this.fechaHoraInicio);
    }

    /**
     * Obtiene el precio base del día para esta función (para implementación futura de boletos)
     * @return El precio correspondiente al día de la semana de la función
     */
    public java.math.BigDecimal getPrecioPorDia() {
        return getDiaSemana().getPrecio();
    }

    /**
     * Obtiene el multiplicador de precio por formato (para implementación futura de boletos)
     * @return El multiplicador del formato (2D=1.0, 3D=1.5)
     */
    public java.math.BigDecimal getMultiplicadorFormato() {
        return this.formato.getMultiplicadorPrecio();
    }

    /**
     * Obtiene el multiplicador de precio por tipo de estreno (para implementación futura de boletos)
     * @return El multiplicador del tipo de estreno (Estreno=1.2, Preestreno=1.3)
     */
    public java.math.BigDecimal getMultiplicadorTipoEstreno() {
        return this.tipoEstreno.getMultiplicadorPrecio();
    }

    /**
     * Calcula el precio final aplicando todos los multiplicadores (para implementación futura de boletos)
     * Fórmula: PrecioBase * MultiplicadorFormato * MultiplicadorTipoEstreno
     * @return El precio final calculado con todos los factores
     */
    public java.math.BigDecimal calcularPrecioFinal() {
        return getPrecioPorDia()
                .multiply(getMultiplicadorFormato())
                .multiply(getMultiplicadorTipoEstreno());
    }
}