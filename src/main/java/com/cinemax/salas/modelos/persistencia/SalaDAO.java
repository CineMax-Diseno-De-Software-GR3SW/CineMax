package com.cinemax.salas.modelos.persistencia;

import com.cinemax.peliculas.modelos.persistencia.GestorDB;
import com.cinemax.salas.modelos.entidades.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SalaDAO {
    
    private GestorDB gestorDB;
    
    // Constructor
    public SalaDAO() {
        this.gestorDB = GestorDB.obtenerInstancia();
    }
    
    // Método para guardar una nueva sala
    public void guardar(Sala sala) throws SQLException {
        if (sala == null) {
            throw new IllegalArgumentException("La sala no puede ser null");
        }
        
        // Verificar duplicados antes de insertar
        if (existeDuplicado(sala.getNombre())) {
            throw new SQLException("Ya existe una sala con el mismo nombre");
        }
        
        String sql = """
            INSERT INTO sala (nombre, capacidad, tipo, estado)
            VALUES (?, ?, ?, ?)
            """;
        
        try (Connection conn = gestorDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, sala.getNombre());
            stmt.setInt(2, sala.getCapacidad());
            stmt.setString(3, sala.getTipoSala().name());
            stmt.setString(4, sala.getEstado().name());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado y asignarlo a la sala
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        sala.setId(rs.getInt(1));
                        System.out.println("Sala guardada con ID: " + sala.getId());
                    }
                }
            } else {
                throw new SQLException("Error al guardar la sala: no se insertó ninguna fila");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al guardar sala: " + e.getMessage());
            throw e;
        }
    }
    
    // Método para actualizar una sala existente
    public void actualizar(Sala sala) throws SQLException {
        if (sala == null || sala.getId() <= 0) {
            throw new IllegalArgumentException("La sala debe tener un ID válido para actualizar");
        }
        
        String sql = """
            UPDATE sala 
            SET nombre = ?, capacidad = ?, tipo = ?, estado = ?
            WHERE id = ?
            """;
        
        try (Connection conn = gestorDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sala.getNombre());
            stmt.setInt(2, sala.getCapacidad());
            stmt.setString(3, sala.getTipoSala().name());
            stmt.setString(4, sala.getEstado().name());
            stmt.setInt(5, sala.getId());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró la sala con ID: " + sala.getId());
            } else {
                System.out.println("Sala actualizada correctamente: " + sala.getNombre());
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar sala: " + e.getMessage());
            throw e;
        }
    }
    
    // Método para eliminar una sala por ID
    public void eliminar(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        
        String sql = "DELETE FROM sala WHERE id = ?";
        
        try (Connection conn = gestorDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró la sala con ID: " + id);
            } else {
                System.out.println("Sala eliminada correctamente con ID: " + id);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar sala: " + e.getMessage());
            throw e;
        }
    }

    public Sala buscarPorId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        
        String sql = "SELECT id, nombre, capacidad, tipo, estado FROM sala WHERE id = ?";
        try (Connection conn = gestorDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetASala(rs);
                } else {
                    return null; // No se encontró la sala
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar sala por ID: " + e.getMessage());
            throw e;
        }
    }
    
    // Método para verificar si existe una sala duplicada
    public boolean existeDuplicado(String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        
        String sql = "SELECT COUNT(*) FROM sala WHERE LOWER(nombre) = LOWER(?)";
        
        try (Connection conn = gestorDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre.trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar duplicados: " + e.getMessage());
            throw e;
        }
    }

    public List<Sala> obtenerTodas() throws SQLException {
        List<Sala> salas = new ArrayList<>();
        
        String sql = """
            SELECT id, nombre, capacidad, tipo, estado
            FROM sala 
            ORDER BY id DESC
            """;
        
        try (Connection conn = gestorDB.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                salas.add(mapearResultSetASala(rs));
            }
            
            System.out.println("Se encontraron " + salas.size() + " salas");
            return salas;
            
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las salas: " + e.getMessage());
            throw e;
        }
    }
    
    // Método adicional: buscar por nombre (búsqueda parcial)
    public List<Sala> buscarPorNombre(String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de búsqueda no puede estar vacío");
        }
        
        List<Sala> salas = new ArrayList<>();
        
        String sql = """
            SELECT id, nombre, capacidad, tipo, estado
            FROM sala 
            WHERE LOWER(nombre) LIKE LOWER(?)
            ORDER BY nombre
            """;
        
        try (Connection conn = gestorDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + nombre.trim() + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    salas.add(mapearResultSetASala(rs));
                }
            }
            
            System.out.println("Se encontraron " + salas.size() + " salas con nombre similar a: " + nombre);
            return salas;
            
        } catch (SQLException e) {
            System.err.println("Error al buscar salas por nombre: " + e.getMessage());
            throw e;
        }
    }
    
    // Método privado para mapear ResultSet a objeto Sala
    private Sala mapearResultSetASala(ResultSet rs) throws SQLException {
        return new Sala(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getInt("capacidad"),
            TipoSala.valueOf(rs.getString("tipo")),
            EstadoSala.valueOf(rs.getString("estado"))
        );
    }
    
    // Método original renombrado para mantener compatibilidad
    public List<Sala> listarTodas() {
        try {
            return obtenerTodas();
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar salas: " + e.getMessage(), e);
        }
    }
}
