package com.cinemax.salas.servicios;

import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.modelos.persistencia.ButacasDAO;
import com.cinemax.salas.modelos.persistencia.SalasDAO;

import java.util.List;

public class ButacaService {
    private final ButacasDAO butacasDAO = new ButacasDAO();
    private final SalasDAO salasDAO = new SalasDAO();

    public void crearButaca(Butaca butaca) throws Exception {
        if (butaca.getFila() == null || butaca.getFila().trim().isEmpty())
            throw new Exception("La fila no puede estar vacía.");
        if (butaca.getColumna() == null || butaca.getColumna().trim().isEmpty())
            throw new Exception("La columna no puede estar vacía.");
        if (butaca.getEstado() == null)
            throw new Exception("Debe seleccionar un estado para la butaca.");

        List<Butaca> butacas = butacasDAO.listarButacasPorSala(butaca.getIdSala());
        for (Butaca b : butacas) {
            if (b.getFila().equalsIgnoreCase(butaca.getFila()) &&
                    b.getColumna().equalsIgnoreCase(butaca.getColumna()))
                throw new Exception("Ya existe una butaca en esa posición para esta sala.");
        }

        Sala sala = salasDAO.obtenerSalaPorId(butaca.getIdSala());
        if (sala == null)
            throw new Exception("La sala no existe.");
        if (butacas.size() >= sala.getCapacidad())
            throw new Exception("No se pueden agregar más butacas, se alcanzó la capacidad máxima de la sala.");

        butacasDAO.crearButaca(butaca);
    }

    public Butaca obtenerButacaPorId(int id) throws Exception {
        return butacasDAO.obtenerButacaPorId(id);
    }

    public List<Butaca> listarButacasPorSala(int idSala) throws Exception {
        return butacasDAO.listarButacasPorSala(idSala);
    }

    public void actualizarButaca(Butaca butaca) throws Exception {
        if (butaca.getFila() == null || butaca.getFila().trim().isEmpty())
            throw new Exception("La fila no puede estar vacía.");
        if (butaca.getColumna() == null || butaca.getColumna().trim().isEmpty())
            throw new Exception("La columna no puede estar vacía.");
        if (butaca.getEstado() == null)
            throw new Exception("Debe seleccionar un estado para la butaca.");

        List<Butaca> butacas = butacasDAO.listarButacasPorSala(butaca.getIdSala());
        for (Butaca b : butacas) {
            if (b.getFila().equalsIgnoreCase(butaca.getFila()) &&
                    b.getColumna().equalsIgnoreCase(butaca.getColumna()) &&
                    b.getId() != butaca.getId())
                throw new Exception("Ya existe una butaca en esa posición para esta sala.");
        }

        Sala sala = salasDAO.obtenerSalaPorId(butaca.getIdSala());
        if (sala == null)
            throw new Exception("La sala no existe.");
        // No se valida la capacidad en actualizar, solo en crear

        butacasDAO.actualizarButaca(butaca);
    }

    public void eliminarButaca(int id) throws Exception {
        butacasDAO.eliminarButaca(id);
    }
}

