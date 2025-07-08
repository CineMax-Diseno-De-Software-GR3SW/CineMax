package com.cinemax.peliculas.servicios;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.entidades.FormatoFuncion;
import com.cinemax.peliculas.modelos.entidades.TipoEstreno;
import com.cinemax.peliculas.modelos.persistencia.FuncionDAO;
import com.cinemax.salas.modelos.entidades.Sala;

import java.sql.SQLException;
import java.time.LocalDateTime;
// import java.util.ArrayList;
import java.util.List;

public class ServicioFuncion {

    private FuncionDAO funcionDAO;

    // Constructor
    public ServicioFuncion() {
        this.funcionDAO = new FuncionDAO();
    }

    // private List<Funcion> funciones = new ArrayList<>();

    public Funcion programarNuevaFuncion(Pelicula pelicula, Sala sala, LocalDateTime fechaHoraInicio,
            FormatoFuncion formato, TipoEstreno tipoEstreno) throws IllegalArgumentException, SQLException {
        LocalDateTime fechaHoraFin = fechaHoraInicio.plusHours(3);

        // Validar traslapes consultando la BD
        List<Funcion> funcionesSala = funcionDAO.listarTodas(); // Mejor: crear un método listarPorSala(salaId)
        for (Funcion f : funcionesSala) {
            if (f.getSala().getId() == sala.getId()) {
                if (fechaHoraInicio.isBefore(f.getFechaHoraFin()) && fechaHoraFin.isAfter(f.getFechaHoraInicio())) {
                    return null;
                }
            }
        }

        Funcion funcion = new Funcion(0, pelicula, sala, fechaHoraInicio, fechaHoraFin, formato, tipoEstreno);
        funcionDAO.guardar(funcion);
        return funcion;
    }

    public List<Funcion> listarTodasLasFunciones() {
        return funcionDAO.listarTodas();
    }
}