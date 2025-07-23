package com.cinemax.salas.servicios;

import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.modelos.persistencia.ButacasDAO;
import com.cinemax.salas.modelos.persistencia.SalasDAO;

import java.util.List;

public class SalaService {
    private final SalasDAO salasDAO = new SalasDAO();
    private ButacaService butacaService = new ButacaService();
    private ButacasDAO butacasDAO = new ButacasDAO();
    public void crearSala(Sala sala) throws Exception {
        // --- tus validaciones idénticas ---
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

        // 1) Persiste la sala
        salasDAO.crearSala(sala);

        // 2) Genera automáticamente las butacas 6×N
        butacaService.generarButacasAutomatica(sala.getId());
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

        Sala original = salasDAO.obtenerSalaPorId(sala.getId());
        int capacidadAnterior = original.getCapacidad();
        int capacidadNueva = sala.getCapacidad();

        List<Butaca> butacas = butacasDAO.listarButacasPorSala(sala.getId());

        if (capacidadNueva < capacidadAnterior) {
            int aEliminar = butacas.size() - capacidadNueva;
            if (aEliminar > 0) {
                butacas.sort((b1, b2) -> {
                    int cmpFila = b2.getFila().compareTo(b1.getFila());
                    if (cmpFila != 0) return cmpFila;
                    return Integer.compare(Integer.parseInt(b2.getColumna()), Integer.parseInt(b1.getColumna()));
                });
                for (int i = 0; i < aEliminar; i++) {
                    butacasDAO.eliminarButaca(butacas.get(i).getId());
                }
            }
        } else if (capacidadNueva > capacidadAnterior) {
            int aAgregar = capacidadNueva - butacas.size();
            // Encontrar la última fila y columna
            String ultimaFila = "A";
            int ultimaCol = 0;
            for (Butaca b : butacas) {
                if (b.getFila().compareTo(ultimaFila) > 0 ||
                        (b.getFila().equals(ultimaFila) && Integer.parseInt(b.getColumna()) > ultimaCol)) {
                    ultimaFila = b.getFila();
                    ultimaCol = Integer.parseInt(b.getColumna());
                }
            }
            int columnas;
            switch (capacidadNueva) {
                case 36 -> columnas = 6;
                case 42 -> columnas = 7;
                case 48 -> columnas = 8;
                default -> throw new Exception("Capacidad no soportada: " + capacidadNueva);
            }
            char fila = ultimaFila.charAt(0);
            int col = ultimaCol;
            for (int i = 0; i < aAgregar; i++) {
                col++;
                if (col > columnas) {
                    col = 1;
                    fila++;
                }
                Butaca nueva = new Butaca();
                nueva.setIdSala(sala.getId());
                nueva.setFila(String.valueOf(fila));
                nueva.setColumna(String.valueOf(col));
                nueva.setEstado("DISPONIBLE");
                butacasDAO.crearButaca(nueva);
            }
        }

        salasDAO.actualizarSala(sala);
    }

    public void eliminarSala(int id) throws Exception {
        // 1. Elimina todas las butacas de la sala
        List<Butaca> butacas = butacasDAO.listarButacasPorSala(id);
        for (Butaca b : butacas) {
            butacasDAO.eliminarButaca(b.getId());
        }
        // 2. Elimina la sala
        salasDAO.eliminarSala(id);
    }
}
