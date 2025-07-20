package com.cinemax.salas.servicios;

import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.modelos.persistencia.SalasDAO;

import java.util.List;

public class SalaService {
    private final SalasDAO salasDAO = new SalasDAO();

    public void crearSala(Sala sala) throws Exception {
        if (sala.getNombre() == null || sala.getNombre().trim().isEmpty())
            throw new Exception("El nombre de la sala no puede estar vacío.");
        if (sala.getCapacidad() <= 0 || sala.getCapacidad() > 500)
            throw new Exception("La capacidad debe ser entre 1 y 500.");
        if (sala.getTipo() == null)
            throw new Exception("Debe seleccionar un tipo de sala.");
        if (sala.getEstado() == null)
            throw new Exception("Debe seleccionar un estado para la sala.");
        for (Sala s : salasDAO.listarSalas()) {
            if (s.getNombre().equalsIgnoreCase(sala.getNombre()))
                throw new Exception("Ya existe una sala con ese nombre.");
        }
        salasDAO.crearSala(sala);
    }

    public Sala obtenerSalaPorId(int id) throws Exception {
        return salasDAO.obtenerSalaPorId(id);
    }

    public List<Sala> listarSalas() throws Exception {
        return salasDAO.listarSalas();
    }

    public void actualizarSala(Sala sala) throws Exception {
        if (sala.getNombre() == null || sala.getNombre().trim().isEmpty())
            throw new Exception("El nombre de la sala no puede estar vacío.");
        if (sala.getCapacidad() <= 0 || sala.getCapacidad() > 500)
            throw new Exception("La capacidad debe ser entre 1 y 500.");
        if (sala.getTipo() == null)
            throw new Exception("Debe seleccionar un tipo de sala.");
        if (sala.getEstado() == null)
            throw new Exception("Debe seleccionar un estado para la sala.");
        for (Sala s : salasDAO.listarSalas()) {
            if (s.getNombre().equalsIgnoreCase(sala.getNombre()) && s.getId() != sala.getId())
                throw new Exception("Ya existe una sala con ese nombre.");
        }
        salasDAO.actualizarSala(sala);
    }

    public void eliminarSala(int id) throws Exception {
        salasDAO.eliminarSala(id);
    }
}
