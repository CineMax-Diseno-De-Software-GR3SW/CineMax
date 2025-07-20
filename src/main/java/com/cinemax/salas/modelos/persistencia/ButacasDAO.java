package com.cinemax.salas.modelos.persistencia;

import com.cinemax.salas.modelos.entidades.Butaca;

import java.util.ArrayList;
import java.util.List;

public class ButacasDAO {
    // Simulación de almacenamiento en memoria
    private static final List<Butaca> butacas = new ArrayList<>();
    private static int idCounter = 1;

    public void crearButaca(Butaca butaca) {
        butaca.setId(idCounter++);
        butacas.add(butaca);
    }

    public Butaca obtenerButacaPorId(int id) {
        for (Butaca b : butacas) {
            if (b.getId() == id) return b;
        }
        return null;
    }

    public List<Butaca> listarButacasPorSala(int idSala) {
        List<Butaca> resultado = new ArrayList<>();
        for (Butaca b : butacas) {
            if (b.getIdSala() == idSala) resultado.add(b);
        }
        return resultado;
    }

    public void actualizarButaca(Butaca butaca) {
        for (int i = 0; i < butacas.size(); i++) {
            if (butacas.get(i).getId() == butaca.getId()) {
                butacas.set(i, butaca);
                return;
            }
        }
    }

    public void eliminarButaca(int id) {
        butacas.removeIf(b -> b.getId() == id);
    }
}
