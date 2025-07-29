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
import java.util.ArrayList;
// import java.util.ArrayList;
import java.util.List;

public class ServicioFuncion {

    private FuncionDAO funcionDAO;

    // Constructor
    public ServicioFuncion() {
        this.funcionDAO = new FuncionDAO();
    }

   

    public Funcion crearFuncion(Pelicula pelicula, Sala sala, LocalDateTime fechaHoraInicio,
            FormatoFuncion formato, TipoEstreno tipoEstreno)
            throws IllegalArgumentException, SQLException {

        validarDatosFuncion(pelicula, sala, fechaHoraInicio, formato, tipoEstreno);

        LocalDateTime fechaHoraFin = fechaHoraInicio.plusMinutes(pelicula.getDuracionMinutos() + 40);

        validarHorarioTrabajo(fechaHoraInicio, fechaHoraFin);

        validarTraslapeFunciones(sala, fechaHoraInicio, fechaHoraFin);

        Funcion funcion = new Funcion(0, pelicula, sala, fechaHoraInicio, fechaHoraFin, formato, tipoEstreno);
        funcionDAO.crear(funcion);
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
        if (sala.getEstado() != null && sala.getEstado() != EstadoSala.DISPONIBLE) {
            throw new IllegalArgumentException("La sala seleccionada no está disponible (actualmente está "
                    + sala.getEstado().name().toLowerCase() + ").");
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
        validarTraslapeFunciones(sala, inicio, fin, -1); // -1 indica que no hay función a excluir
    }

    private void validarTraslapeFunciones(Sala sala, LocalDateTime inicio, LocalDateTime fin, int idFuncionAExcluir) throws SQLException {
        List<Funcion> funcionesSala = funcionDAO.listarFuncionesPorSala(sala.getId());
        for (Funcion f : funcionesSala) {
            // Excluir la función que se está editando
            if (f.getId() != idFuncionAExcluir && inicio.isBefore(f.getFechaHoraFin()) && fin.isAfter(f.getFechaHoraInicio())) {
                throw new IllegalArgumentException(
                        String.format("Ya existe una función programada en la sala %s entre %s y %s.",
                                sala.getNombre(), f.getFechaHoraInicio(), f.getFechaHoraFin()));
            }
        }
    }

    public void actualizarFuncion(int id, Pelicula pelicula, Sala sala, LocalDateTime fechaHoraInicio,
            FormatoFuncion formato, TipoEstreno tipoEstreno) throws SQLException {
        Funcion funcionExistente = funcionDAO.buscarPorId(id);
        if (funcionExistente == null) {
            throw new IllegalArgumentException("No se encontró la función con ID: " + id);
        }

        validarDatosFuncion(pelicula, sala, fechaHoraInicio, formato, tipoEstreno);
        LocalDateTime fechaHoraFin = fechaHoraInicio.plusMinutes(pelicula.getDuracionMinutos() + 40);
        validarHorarioTrabajo(fechaHoraInicio, fechaHoraFin);
        validarTraslapeFunciones(sala, fechaHoraInicio, fechaHoraFin, id); // Excluir la función que se está editando

        funcionExistente.setPelicula(pelicula);
        funcionExistente.setSala(sala);
        funcionExistente.setFechaHoraInicio(fechaHoraInicio);
        funcionExistente.setFechaHoraFin(fechaHoraFin);
        funcionExistente.setFormato(formato);
        funcionExistente.setTipoEstreno(tipoEstreno);

        funcionDAO.actualizar(funcionExistente);
    }

    public List<Funcion> listarTodasLasFunciones() {
        return funcionDAO.listarTodasLasFunciones();
    }

    public Funcion buscarFuncionPorId(int id) throws SQLException {
        return funcionDAO.buscarPorId(id);
    }

    public List<Funcion> listarFuncionesPorSala(int salaId) throws SQLException {
        return funcionDAO.listarFuncionesPorSala(salaId);
    }

    public void eliminarFuncion(int id) throws SQLException {
        // Verificar que existe antes de eliminar
        Funcion funcion = funcionDAO.buscarPorId(id);
        if (funcion == null) {
            throw new IllegalArgumentException("No existe una película con ID: " + id);
        }

        funcionDAO.eliminar(id);
    }

        /**
     * Obtiene las funciones de una película específica por su nombre/título
     * @param nombrePelicula El título de la película a buscar
     * @return Lista de funciones de la película especificada
     * @throws Exception Si ocurre un error durante la búsqueda
     */
    public List<Funcion> obtenerFuncionesPorNombrePelicula(String nombrePelicula) throws Exception {
        if (nombrePelicula == null || nombrePelicula.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la película no puede estar vacío");
        }
        
        try {
            // Obtener todas las funciones
            List<Funcion> todasLasFunciones = funcionDAO.listarTodasLasFunciones();
            List<Funcion> funcionesEncontradas = new ArrayList<>();
            
            // Filtrar por nombre de película (búsqueda case-insensitive y parcial)
            String nombreBusqueda = nombrePelicula.trim().toLowerCase();
            
            for (Funcion funcion : todasLasFunciones) {
                if (funcion.getPelicula() != null && 
                    funcion.getPelicula().getTitulo() != null &&
                    funcion.getPelicula().getTitulo().toLowerCase().contains(nombreBusqueda)) {
                    funcionesEncontradas.add(funcion);
                }
            }
            
            // Ordenar por fecha y hora de inicio
            funcionesEncontradas.sort((f1, f2) -> {
                if (f1.getFechaHoraInicio() != null && f2.getFechaHoraInicio() != null) {
                    return f1.getFechaHoraInicio().compareTo(f2.getFechaHoraInicio());
                }
                return 0;
            });
            
            return funcionesEncontradas;
            
        } catch (Exception e) {
            throw new Exception("Error al obtener funciones por película: " + e.getMessage(), e);
        }
    }
}