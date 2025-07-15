package com.cinemax.peliculas.modelos.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.cinemax.comun.modelos.persistencia.ConexionBaseSingleton;
import com.cinemax.peliculas.modelos.entidades.Idioma;
import com.cinemax.peliculas.modelos.entidades.Pelicula;

public class PeliculaDAO {
    
    private ConexionBaseSingleton conexionBaseSingleton;
    
    // Constructor
    public PeliculaDAO() {
        this.conexionBaseSingleton = ConexionBaseSingleton.getInstancia();
    }
    
    // Método para guardar una nueva película
    public void guardar(Pelicula pelicula) throws SQLException {
        if (pelicula == null) {
            throw new IllegalArgumentException("La película no puede ser null");
        }
        
        // Verificar duplicados antes de insertar
        if (existeDuplicado(pelicula.getTitulo(), pelicula.getAnio())) {
            throw new SQLException("Ya existe una película con el mismo título y año");
        }
        
        String sql = """
            INSERT INTO pelicula (titulo, sinopsis, duracion_minutos, anio, idioma, genero, imagen_url)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = conexionBaseSingleton.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, pelicula.getTitulo());
            stmt.setString(2, pelicula.getSinopsis());
            stmt.setInt(3, pelicula.getDuracionMinutos());
            stmt.setInt(4, pelicula.getAnio());
            stmt.setString(5, pelicula.getIdioma() != null ? pelicula.getIdioma().getCodigo() : null);
            stmt.setString(6, pelicula.getGenerosComoString());
            stmt.setString(7, pelicula.getImagenUrl());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado y asignarlo a la película
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pelicula.setId(rs.getInt(1));
                        System.out.println("Película guardada con ID: " + pelicula.getId());
                    }
                }
            } else {
                throw new SQLException("Error al guardar la película: no se insertó ninguna fila");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al guardar película: " + e.getMessage());
            throw e;
        }
    }
    
    // Método para actualizar una película existente
    public void actualizar(Pelicula pelicula) throws SQLException {
        if (pelicula == null || pelicula.getId() <= 0) {
            throw new IllegalArgumentException("La película debe tener un ID válido para actualizar");
        }
        
        String sql = """
            UPDATE pelicula 
            SET titulo = ?, sinopsis = ?, duracion_minutos = ?, anio = ?, idioma = ?, 
                genero = ?, imagen_url = ?
            WHERE id = ?
            """;
        
        try (Connection conn = conexionBaseSingleton.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, pelicula.getTitulo());
            stmt.setString(2, pelicula.getSinopsis());
            stmt.setInt(3, pelicula.getDuracionMinutos());
            stmt.setInt(4, pelicula.getAnio());
            stmt.setString(5, pelicula.getIdioma() != null ? pelicula.getIdioma().getCodigo() : null);
            stmt.setString(6, pelicula.getGenerosComoString());
            stmt.setString(7, pelicula.getImagenUrl());
            stmt.setInt(8, pelicula.getId());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró la película con ID: " + pelicula.getId());
            } else {
                System.out.println("Película actualizada correctamente: " + pelicula.getTitulo());
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar película: " + e.getMessage());
            throw e;
        }
    }
    
    // Método para eliminar una película por ID
    public void eliminar(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        
        String sql = "DELETE FROM pelicula WHERE id = ?";
        
        try (Connection conn = conexionBaseSingleton.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró la película con ID: " + id);
            } else {
                System.out.println("Película eliminada correctamente con ID: " + id);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar película: " + e.getMessage());
            throw e;
        }
    }
    
    // Método para buscar una película por ID
    public Pelicula buscarPorId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        
        String sql = """
            SELECT id, titulo, sinopsis, duracion_minutos, anio, idioma, 
                   genero, imagen_url
            FROM pelicula WHERE id = ?
            """;
        
        try (Connection conn = conexionBaseSingleton.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetAPelicula(rs);
                } else {
                    return null; // No se encontró la película
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar película por ID: " + e.getMessage());
            throw e;
        }
    }
    
    // Método para verificar si existe una película duplicada
    public boolean existeDuplicado(String titulo, int anio) throws SQLException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        
        String sql = "SELECT COUNT(*) FROM pelicula WHERE LOWER(titulo) = LOWER(?) AND anio = ?";
        
        try (Connection conn = conexionBaseSingleton.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, titulo.trim());
            stmt.setInt(2, anio);
            
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
    
    // Método para obtener todas las películas
    public List<Pelicula> obtenerTodas() throws SQLException {
        List<Pelicula> peliculas = new ArrayList<>();
        
        String sql = """
            SELECT id, titulo, sinopsis, duracion_minutos, anio, idioma, 
                   genero, imagen_url
            FROM pelicula 
            ORDER BY id DESC
            """;
        
        try (Connection conn = conexionBaseSingleton.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                peliculas.add(mapearResultSetAPelicula(rs));
            }
            
            System.out.println("Se encontraron " + peliculas.size() + " películas");
            return peliculas;
            
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las películas: " + e.getMessage());
            throw e;
        }
    }
    
    // Método adicional: buscar por título (búsqueda parcial)
    public List<Pelicula> buscarPorTitulo(String titulo) throws SQLException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título de búsqueda no puede estar vacío");
        }
        
        List<Pelicula> peliculas = new ArrayList<>();
        
        String sql = """
            SELECT id, titulo, sinopsis, duracion_minutos, anio, idioma, 
                   genero, imagen_url
            FROM pelicula 
            WHERE LOWER(titulo) LIKE LOWER(?)
            ORDER BY titulo
            """;
        
        try (Connection conn = conexionBaseSingleton.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + titulo.trim() + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    peliculas.add(mapearResultSetAPelicula(rs));
                }
            }
            
            System.out.println("Se encontraron " + peliculas.size() + " películas con título similar a: " + titulo);
            return peliculas;
            
        } catch (SQLException e) {
            System.err.println("Error al buscar películas por título: " + e.getMessage());
            throw e;
        }
    }
    
    // Método privado para mapear ResultSet a objeto Pelicula
    private Pelicula mapearResultSetAPelicula(ResultSet rs) throws SQLException {
        Pelicula pelicula = new Pelicula();
        
        pelicula.setId(rs.getInt("id"));
        pelicula.setTitulo(rs.getString("titulo"));
        pelicula.setSinopsis(rs.getString("sinopsis"));
        pelicula.setDuracionMinutos(rs.getInt("duracion_minutos"));
        pelicula.setAnio(rs.getInt("anio"));
        
        // Mapear idioma de String a Enum
        String idiomaCodigo = rs.getString("idioma");
        if (idiomaCodigo != null && !idiomaCodigo.trim().isEmpty()) {
            try {
                pelicula.setIdioma(Idioma.porCodigo(idiomaCodigo));
            } catch (IllegalArgumentException e) {
                System.err.println("Código de idioma no válido en BD: " + idiomaCodigo + ". Se establece null.");
                // Se mantiene null si el idioma no es válido
            }
        }
        
        
        pelicula.setGenerosPorString(rs.getString("genero"));
        pelicula.setImagenUrl(rs.getString("imagen_url"));
        
        return pelicula;
    }
}
