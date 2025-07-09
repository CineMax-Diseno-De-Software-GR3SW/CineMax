package com.cinemax.salas.servicios;


import com.cinemax.salas.modelos.entidades.*;
import com.cinemax.salas.modelos.persistencia.SalaDAO;

import java.util.ArrayList;
import java.util.List;

public class ServicioSala {

    private SalaDAO salaDAO = new SalaDAO();

    public List<Sala> listarTodasLasSalas() {
        return salaDAO.listarTodas();
    }

    public Sala buscarSalaPorId(int id) {
        return salaDAO.buscarPorId(id);
    }

    private List<Sala> salas = new ArrayList<>();

    public ServicioSala() {
        // Ejemplo de salas iniciales
        salas.add(new Sala(1, "Sala 1", 15, TipoSala.NORMAL, EstadoSala.DISPONIBLE));
        salas.add(new Sala(2, "Sala 2", 12, TipoSala.VIP, EstadoSala.DISPONIBLE));
    }

    // public List<Sala> listarTodasLasSalas() {
    //     return new ArrayList<>(salas);
    // }

    // public Sala buscarSalaPorId(int id) {
    //     for (Sala sala : salas) {
    //         if (sala.getId() == id) {
    //             return sala;
    //         }
    //     }
    //     return null;
    // }

    // public void agregarSala(Sala sala) {
    //     salas.add(sala);
    // }

    // public boolean eliminarSala(int id) {
    //     return salas.removeIf(sala -> sala.getId() == id);
    // }

    // public boolean actualizarSala(Sala salaActualizada) {
    //     for (int i = 0; i < salas.size(); i++) {
    //         if (salas.get(i).getId() == salaActualizada.getId()) {
    //             salas.set(i, salaActualizada);
    //             return true;
    //         }
    //     }
    //     return false;
    // }
}