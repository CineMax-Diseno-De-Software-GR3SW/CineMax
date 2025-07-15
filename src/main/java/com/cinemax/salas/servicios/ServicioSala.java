package com.cinemax.salas.servicios;

import com.cinemax.salas.modelos.entidades.*;
import com.cinemax.salas.modelos.persistencia.SalaDAO;

import java.sql.SQLException;
import java.util.List;

public class ServicioSala {

    private SalaDAO salaDAO;

    public ServicioSala() {
        this.salaDAO = new SalaDAO();
    }

    // Método para listar todas las salas
    public List<Sala> listarTodasLasSalas() {
        try {
            return salaDAO.obtenerTodas();
        } catch (SQLException e) {
            System.err.println("Error al listar salas: " + e.getMessage());
            throw new RuntimeException("Error al obtener las salas", e);
        }
    }

    // Método para buscar sala por ID
    public Sala buscarSalaPorId(int id) {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("El ID debe ser mayor a 0");
            }
            return salaDAO.buscarPorId(id);
        } catch (SQLException e) {
            System.err.println("Error al buscar sala por ID: " + e.getMessage());
            throw new RuntimeException("Error al buscar la sala", e);
        }
    }

    // Método para guardar una nueva sala
    public void guardarSala(Sala sala) {
        try {
            if (sala == null) {
                throw new IllegalArgumentException("La sala no puede ser null");
            }
            salaDAO.guardar(sala);
            System.out.println("Sala guardada exitosamente: " + sala.getNombre());
        } catch (SQLException e) {
            System.err.println("Error al guardar sala: " + e.getMessage());
            throw new RuntimeException("Error al guardar la sala", e);
        }
    }

    // Método para actualizar una sala
    public void actualizarSala(Sala sala) {
        try {
            if (sala == null || sala.getId() <= 0) {
                throw new IllegalArgumentException("La sala debe tener un ID válido para actualizar");
            }
            salaDAO.actualizar(sala);
            System.out.println("Sala actualizada exitosamente: " + sala.getNombre());
        } catch (SQLException e) {
            System.err.println("Error al actualizar sala: " + e.getMessage());
            throw new RuntimeException("Error al actualizar la sala", e);
        }
    }

    // Método para eliminar una sala
    public void eliminarSala(int id) {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("El ID debe ser mayor a 0");
            }
            salaDAO.eliminar(id);
            System.out.println("Sala eliminada exitosamente con ID: " + id);
        } catch (SQLException e) {
            System.err.println("Error al eliminar sala: " + e.getMessage());
            throw new RuntimeException("Error al eliminar la sala", e);
        }
    }

    // Método para buscar salas por nombre
    public List<Sala> buscarSalasPorNombre(String nombre) {
        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de búsqueda no puede estar vacío");
            }
            return salaDAO.buscarPorNombre(nombre);
        } catch (SQLException e) {
            System.err.println("Error al buscar salas por nombre: " + e.getMessage());
            throw new RuntimeException("Error al buscar salas", e);
        }
    }

    // Método para verificar si existe una sala con el mismo nombre
    public boolean existeSalaConNombre(String nombre) {
        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                return false;
            }
            return salaDAO.existeDuplicado(nombre);
        } catch (SQLException e) {
            System.err.println("Error al verificar duplicados: " + e.getMessage());
            return false;
        }
    }

    // Método para validar datos de sala antes de guardar
    public void validarSala(Sala sala) {
        if (sala == null) {
            throw new IllegalArgumentException("La sala no puede ser null");
        }
        
        if (sala.getNombre() == null || sala.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la sala es obligatorio");
        }
        
        if (sala.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a 0");
        }
        
        if (sala.getTipoSala() == null) {
            throw new IllegalArgumentException("El tipo de sala es obligatorio");
        }
        
        if (sala.getEstado() == null) {
            throw new IllegalArgumentException("El estado de la sala es obligatorio");
        }
    }

    // Método para obtener salas por tipo
    public List<Sala> obtenerSalasPorTipo(TipoSala tipo) {
        try {
            List<Sala> todasLasSalas = salaDAO.obtenerTodas();
            return todasLasSalas.stream()
                    .filter(sala -> sala.getTipoSala() == tipo)
                    .toList();
        } catch (SQLException e) {
            System.err.println("Error al obtener salas por tipo: " + e.getMessage());
            throw new RuntimeException("Error al filtrar salas por tipo", e);
        }
    }

    // Método para obtener salas por estado
    public List<Sala> obtenerSalasPorEstado(EstadoSala estado) {
        try {
            List<Sala> todasLasSalas = salaDAO.obtenerTodas();
            return todasLasSalas.stream()
                    .filter(sala -> sala.getEstado() == estado)
                    .toList();
        } catch (SQLException e) {
            System.err.println("Error al obtener salas por estado: " + e.getMessage());
            throw new RuntimeException("Error al filtrar salas por estado", e);
        }
    }

    // Método para cambiar estado de una sala
    public void cambiarEstadoSala(int id, EstadoSala nuevoEstado) {
        try {
            Sala sala = buscarSalaPorId(id);
            if (sala == null) {
                throw new IllegalArgumentException("No se encontró la sala con ID: " + id);
            }
            
            sala.setEstado(nuevoEstado);
            actualizarSala(sala);
            System.out.println("Estado de sala cambiado a: " + nuevoEstado);
        } catch (Exception e) {
            System.err.println("Error al cambiar estado de sala: " + e.getMessage());
            throw new RuntimeException("Error al cambiar estado de la sala", e);
        }
    }

    // Método para obtener el total de salas
    public int obtenerTotalSalas() {
        try {
            return salaDAO.obtenerTodas().size();
        } catch (SQLException e) {
            System.err.println("Error al obtener total de salas: " + e.getMessage());
            return 0;
        }
    }
}