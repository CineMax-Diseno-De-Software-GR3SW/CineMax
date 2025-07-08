package com.cinemax.peliculas.servicios;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.entidades.FormatoFuncion;
import com.cinemax.peliculas.modelos.entidades.TipoEstreno;
import com.cinemax.peliculas.modelos.persistencia.FuncionDAO;
import com.cinemax.salas.modelos.entidades.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
// import java.util.ArrayList;
import java.util.List;

public class ServicioFuncion {

    private FuncionDAO funcionDAO;

    // Constructor
    public ServicioFuncion() {
        this.funcionDAO = new FuncionDAO();
    }

    // private List<Funcion> funciones = new ArrayList<>();

    // public Funcion programarNuevaFuncion(Pelicula pelicula, Sala sala,
    // LocalDateTime fechaHoraInicio,
    // FormatoFuncion formato, TipoEstreno tipoEstreno) throws
    // IllegalArgumentException, SQLException {

    // LocalDateTime fechaHoraFin = fechaHoraInicio.plusHours(3);

    // // Validar traslapes consultando la BD
    // List<Funcion> funcionesSala = funcionDAO.listarTodas(); // Mejor: crear un
    // método listarPorSala(salaId)
    // for (Funcion f : funcionesSala) {
    // if (f.getSala().getId() == sala.getId()) {
    // if (fechaHoraInicio.isBefore(f.getFechaHoraFin()) &&
    // fechaHoraFin.isAfter(f.getFechaHoraInicio())) {
    // return null;
    // }
    // }
    // }

    // Funcion funcion = new Funcion(0, pelicula, sala, fechaHoraInicio,
    // fechaHoraFin, formato, tipoEstreno);
    // funcionDAO.guardar(funcion);
    // return funcion;
    // }

    public Funcion programarNuevaFuncion(Pelicula pelicula, Sala sala, LocalDateTime fechaHoraInicio,
            FormatoFuncion formato, TipoEstreno tipoEstreno)
            throws IllegalArgumentException, SQLException {

        validarDatosFuncion(pelicula, sala, fechaHoraInicio, formato, tipoEstreno);

        LocalDateTime fechaHoraFin = fechaHoraInicio.plusHours(3);

        validarHorarioTrabajo(fechaHoraInicio, fechaHoraFin);

        validarTraslapeFunciones(sala, fechaHoraInicio, fechaHoraFin);

        Funcion funcion = new Funcion(0, pelicula, sala, fechaHoraInicio, fechaHoraFin, formato, tipoEstreno);
        funcionDAO.guardar(funcion);
        return funcion;
    }

    private void validarDatosFuncion(Pelicula pelicula, Sala sala, LocalDateTime fechaHoraInicio,
            FormatoFuncion formato, TipoEstreno tipoEstreno) {
        if (pelicula == null) {
            throw new IllegalArgumentException("Debe seleccionar una película válida.");
        }
        if (sala == null) {
            throw new IllegalArgumentException("Debe seleccionar una sala válida.");
        }
         if (sala.getEstadoSala() != null && sala.getEstadoSala() != EstadoSala.DISPONIBLE) {
        throw new IllegalArgumentException("La sala seleccionada no está disponible (actualmente está " + sala.getEstadoSala().name().toLowerCase() + ").");
    }
        if (fechaHoraInicio == null) {
            throw new IllegalArgumentException("Debe ingresar una fecha y hora de inicio válida.");
        }
        if (formato == null) {
            throw new IllegalArgumentException("Debe seleccionar un formato válido.");
        }
        if (tipoEstreno == null) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de estreno válido.");
        }
    }

    private void validarHorarioTrabajo(LocalDateTime inicio, LocalDateTime fin) {
        LocalTime horaApertura = LocalTime.of(9, 0);
        LocalTime horaCierre = LocalTime.of(22, 0);

        if (inicio.toLocalTime().isBefore(horaApertura)) {
            throw new IllegalArgumentException("La función no puede iniciar antes de las 9:00 a.m.");
        }
        if (fin.toLocalTime().isAfter(horaCierre)) {
            throw new IllegalArgumentException("La función no puede terminar después de las 10:00 p.m.");
        }
    }

    private void validarTraslapeFunciones(Sala sala, LocalDateTime inicio, LocalDateTime fin) throws SQLException {
        List<Funcion> funcionesSala = funcionDAO.listarPorSala(sala.getId());
        for (Funcion f : funcionesSala) {
            if (inicio.isBefore(f.getFechaHoraFin()) && fin.isAfter(f.getFechaHoraInicio())) {
                throw new IllegalArgumentException(
                        String.format("Ya existe una función programada en la sala %s entre %s y %s.",
                                sala.getNombre(), f.getFechaHoraInicio(), f.getFechaHoraFin()));
            }
        }
    }

    public List<Funcion> listarTodasLasFunciones() {
        return funcionDAO.listarTodas();
    }
}