package com.cinemax.salas.servicios;

import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.modelos.persistencia.ButacasDAO;
import com.cinemax.salas.modelos.persistencia.SalasDAO;

import java.util.List;

public class ButacaService {
    private final ButacasDAO butacasDAO = new ButacasDAO();
    private final SalasDAO salasDAO = new SalasDAO();



    public List<Butaca> listarButacasPorSala(int idSala) throws Exception {
        return butacasDAO.listarButacasPorSala(idSala);
    }
    public void generarButacasAutomatica(int salaId) throws Exception {
        Sala sala = salasDAO.obtenerSalaPorId(salaId);
        if (sala == null) throw new Exception("Sala no existe: " + salaId);
        final int FILAS = 6;
        int columnas;
        switch (sala.getCapacidad()) {
            case 36 -> columnas = 6;
            case 42 -> columnas = 7;
            case 48 -> columnas = 8;
            default -> throw new Exception("Capacidad no soportada: " + sala.getCapacidad());
        }

        // ¡Usamos el método del DAO!
        butacasDAO.generarButacas(salaId, FILAS, columnas);
    }


    public void crearButaca(Butaca butaca) throws Exception {
        validarDatosBasicos(butaca);

        Sala sala = salasDAO.obtenerSalaPorId(butaca.getIdSala());
        if (sala == null)
            throw new Exception("La sala con ID " + butaca.getIdSala() + " no existe.");

        List<Butaca> existentes = butacasDAO.listarButacasPorSala(butaca.getIdSala());
        if (existentes.size() >= sala.getCapacidad())
            throw new Exception("No se pueden agregar más butacas, se alcanzó la capacidad máxima de la sala.");

        // duplicado
        for (Butaca b : existentes) {
            if (b.getFila().equalsIgnoreCase(butaca.getFila()) &&
                    b.getColumna().equalsIgnoreCase(butaca.getColumna())) {
                throw new Exception("Ya existe una butaca en fila " +
                        butaca.getFila() + ", columna " +
                        butaca.getColumna() + " de la sala " +
                        butaca.getIdSala() + ".");
            }
        }

        butacasDAO.crearButaca(butaca);
    }

    public void actualizarButaca(Butaca butaca) throws Exception {
        validarDatosBasicos(butaca);

        // 1) Sala destino
        Sala sala = salasDAO.obtenerSalaPorId(butaca.getIdSala());
        if (sala == null)
            throw new Exception("La sala con ID " + butaca.getIdSala() + " no existe.");

        // 2) Detección de duplicados (excluyendo esta misma butaca)
        List<Butaca> existentes = butacasDAO.listarButacasPorSala(butaca.getIdSala());
        for (Butaca b : existentes) {
            if (b.getId() != butaca.getId() &&
                    b.getFila().equalsIgnoreCase(butaca.getFila()) &&
                    b.getColumna().equalsIgnoreCase(butaca.getColumna())) {
                throw new Exception("Ya existe otra butaca en fila " +
                        butaca.getFila() + ", columna " +
                        butaca.getColumna() + " de la sala " +
                        butaca.getIdSala() + ".");
            }
        }

        // 3) Validación de capacidad si se está moviendo a una sala distinta
        Butaca original = butacasDAO.obtenerButacaPorId(butaca.getId());
        if (original.getIdSala() != butaca.getIdSala()) {
            // ya no está en la sala original, contamos en la sala destino
            if (existentes.size() >= sala.getCapacidad()) {
                throw new Exception("No se puede mover la butaca: capacidad máxima alcanzada en la sala destino.");
            }
        }

        butacasDAO.actualizarButaca(butaca);
    }

    // método común de validaciones básicas
    private void validarDatosBasicos(Butaca butaca) throws Exception {
        if (butaca.getFila() == null || butaca.getFila().trim().isEmpty())
            throw new Exception("La fila no puede estar vacía.");
        if (butaca.getColumna() == null || butaca.getColumna().trim().isEmpty())
            throw new Exception("La columna no puede estar vacía.");
        if (butaca.getEstado() == null)
            throw new Exception("Debe seleccionar un estado para la butaca.");
    }
    // ButacaService.java
    public List<Butaca> listarTodasButacas() throws Exception {
        return butacasDAO.listarTodasButacas();
    }

    public void eliminarButaca(int id) throws Exception {
        butacasDAO.eliminarButaca(id);
    }
}

