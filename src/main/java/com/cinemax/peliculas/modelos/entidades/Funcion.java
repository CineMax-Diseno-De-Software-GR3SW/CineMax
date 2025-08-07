package com.cinemax.peliculas.modelos.entidades;

import java.time.LocalDateTime;
import com.cinemax.salas.modelos.entidades.Sala;

/**
 * Clase que representa una función cinematográfica en el sistema CineMax.
 *
 * <p>Una función es una proyección específica de una película en una sala determinada,
 * con horarios definidos y características particulares como formato y tipo de estreno.
 * Esta clase encapsula toda la información necesaria para gestionar la programación
 * cinematográfica y el cálculo de precios.
 *
 * <p>La función incluye:
 * <ul>
 *   <li>Información de la película a proyectar</li>
 *   <li>Sala asignada para la proyección</li>
 *   <li>Horarios de inicio y fin</li>
 *   <li>Formato de proyección (2D/3D)</li>
 *   <li>Tipo de estreno que afecta el pricing</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public class Funcion {
    /** Identificador único de la función */
    private int id;

    /** Película que será proyectada en esta función */
    private Pelicula pelicula;

    /** Sala donde se realizará la proyección */
    private Sala sala;

    /** Fecha y hora de inicio de la función */
    private LocalDateTime fechaHoraInicio;

    /** Fecha y hora de finalización de la función */
    private LocalDateTime fechaHoraFin;

    /** Formato de proyección (2D o 3D) */
    private FormatoFuncion formato;

    /** Tipo de estreno que afecta el precio */
    private TipoEstreno tipoEstreno;

    /**
     * Constructor completo para crear una nueva función cinematográfica.
     *
     * @param id Identificador único de la función
     * @param pelicula Película a proyectar, no puede ser null
     * @param sala Sala donde se realizará la proyección, no puede ser null
     * @param fechaHoraInicio Fecha y hora de inicio, no puede ser null
     * @param fechaHoraFin Fecha y hora de finalización, no puede ser null
     * @param formato Formato de proyección, no puede ser null
     * @param tipoEstreno Tipo de estreno, no puede ser null
     */
    public Funcion(int id, Pelicula pelicula, Sala sala, LocalDateTime fechaHoraInicio,
                   LocalDateTime fechaHoraFin, FormatoFuncion formato, TipoEstreno tipoEstreno) {
        this.id = id;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.formato = formato;
        this.tipoEstreno = tipoEstreno;
    }

    // Getters

    /**
     * Obtiene el identificador único de la función.
     *
     * @return ID de la función
     */
    public int getId() { return id; }

    /**
     * Obtiene la película asociada a esta función.
     *
     * @return Objeto Pelicula que se proyectará
     */
    public Pelicula getPelicula() { return pelicula; }

    /**
     * Obtiene la sala donde se realizará la función.
     *
     * @return Objeto Sala asignado para la proyección
     */
    public Sala getSala() { return sala; }

    /**
     * Obtiene la fecha y hora de inicio de la función.
     *
     * @return LocalDateTime con el momento de inicio
     */
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }

    /**
     * Obtiene la fecha y hora de finalización de la función.
     *
     * @return LocalDateTime con el momento de finalización
     */
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }

    /**
     * Obtiene el formato de proyección de la función.
     *
     * @return FormatoFuncion (2D o 3D)
     */
    public FormatoFuncion getFormato() { return formato; }

    /**
     * Obtiene el tipo de estreno de la función.
     *
     * @return TipoEstreno que afecta el precio
     */
    public TipoEstreno getTipoEstreno() { return tipoEstreno; }

    // Setters

    /**
     * Establece el identificador de la función.
     *
     * @param id Nuevo identificador único
     */
    public void setId(int id) { this.id = id; }

    /**
     * Establece la película a proyectar.
     *
     * @param pelicula Nueva película, no puede ser null
     */
    public void setPelicula(Pelicula pelicula) { this.pelicula = pelicula; }

    /**
     * Establece la sala para la proyección.
     *
     * @param sala Nueva sala, no puede ser null
     */
    public void setSala(Sala sala) { this.sala = sala; }

    /**
     * Establece la fecha y hora de inicio.
     *
     * @param fechaHoraInicio Nueva fecha/hora de inicio, no puede ser null
     */
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    /**
     * Establece la fecha y hora de finalización.
     *
     * @param fechaHoraFin Nueva fecha/hora de fin, no puede ser null
     */
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    /**
     * Establece el formato de proyección.
     *
     * @param formato Nuevo formato, no puede ser null
     */
    public void setFormato(FormatoFuncion formato) { this.formato = formato; }

    /**
     * Establece el tipo de estreno.
     *
     * @param tipoEstreno Nuevo tipo de estreno, no puede ser null
     */
    public void setTipoEstreno(TipoEstreno tipoEstreno) { this.tipoEstreno = tipoEstreno; }

    /**
     * Obtiene el día de la semana de esta función para cálculos de precios.
     *
     * <p>Este método utiliza la fecha de inicio de la función para determinar
     * el día de la semana correspondiente, necesario para aplicar los
     * multiplicadores de precio por día.
     *
     * @return El enum DiaSemana correspondiente al día de inicio de la función
     */
    public DiaSemana getDiaSemana() {
        return DiaSemana.obtenerDiaDeFecha(this.fechaHoraInicio);
    }

    /**
     * Obtiene el multiplicador de precio base según el día de la semana.
     *
     * <p>El precio base varía según el día de la semana:
     * <ul>
     *   <li>Lunes a Viernes: 1.50</li>
     *   <li>Sábado y Domingo: 1.75</li>
     * </ul>
     *
     * @return Multiplicador de precio correspondiente al día de la semana
     */
    public java.math.BigDecimal getPrecioPorDia() {
        return getDiaSemana().getPrecio();
    }

    /**
     * Obtiene el multiplicador de precio por formato de proyección.
     *
     * <p>Los multiplicadores son:
     * <ul>
     *   <li>2D: 1.0 (sin recargo)</li>
     *   <li>3D: 1.5 (50% de recargo)</li>
     * </ul>
     *
     * @return Multiplicador del formato de proyección
     */
    public java.math.BigDecimal getMultiplicadorFormato() {
        return this.formato.getMultiplicadorPrecio();
    }

    /**
     * Obtiene el multiplicador de precio por tipo de estreno.
     *
     * <p>Los multiplicadores son:
     * <ul>
     *   <li>Estreno: 1.75 (75% de recargo)</li>
     *   <li>Preestreno: 2.0 (100% de recargo)</li>
     * </ul>
     *
     * @return Multiplicador del tipo de estreno
     */
    public java.math.BigDecimal getMultiplicadorTipoEstreno() {
        return this.tipoEstreno.getMultiplicadorPrecio();
    }

    /**
     * Calcula el precio final del boleto aplicando todos los multiplicadores.
     *
     * <p>La fórmula utilizada es:
     * <code>Precio Final = Precio por Día × Multiplicador Formato × Multiplicador Tipo Estreno</code>
     *
     * <p>Este cálculo considera:
     * <ul>
     *   <li>Día de la semana de la función</li>
     *   <li>Formato de proyección (2D/3D)</li>
     *   <li>Tipo de estreno</li>
     * </ul>
     *
     * @return El precio final calculado con todos los factores aplicados
     */
    public java.math.BigDecimal calcularPrecioFinal() {
        return getPrecioPorDia()
                .multiply(getMultiplicadorFormato())
                .multiply(getMultiplicadorTipoEstreno());
    }
}