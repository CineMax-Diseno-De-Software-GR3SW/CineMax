package com.cinemax.peliculas.modelos.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String sql = "SELECT guardar_pelicula(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexionBaseSingleton.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pelicula.getTitulo());
            stmt.setString(2, pelicula.getSinopsis());
            stmt.setInt(3, pelicula.getDuracionMinutos());
            stmt.setInt(4, pelicula.getAnio());
            stmt.setString(5, pelicula.getIdioma() != null ? pelicula.getIdioma().getCodigo() : null);
            stmt.setString(6, pelicula.getGenerosComoString());
            stmt.setString(7, pelicula.getImagenUrl());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pelicula.setId(rs.getInt(1));
                    System.out.println("Película guardada con ID: " + pelicula.getId());
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al guardar película (SP): " + e.getMessage());
            throw e;
        }
    }

    
    // Método para actualizar una película existente
    public void actualizar(Pelicula pelicula) throws SQLException {
        String sql = "CALL actualizar_pelicula(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexionBaseSingleton.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pelicula.getId());
            stmt.setString(2, pelicula.getTitulo());
            stmt.setString(3, pelicula.getSinopsis());
            stmt.setInt(4, pelicula.getDuracionMinutos());
            stmt.setInt(5, pelicula.getAnio());
            stmt.setString(6, pelicula.getIdioma() != null ? pelicula.getIdioma().getCodigo() : null);
            stmt.setString(7, pelicula.getGenerosComoString());
            stmt.setString(8, pelicula.getImagenUrl());

            stmt.execute();

            System.out.println("Película actualizada con ID: " + pelicula.getId());

        } catch (SQLException e) {
            System.err.println("Error al actualizar película (SP): " + e.getMessage());
            throw e;
        }
    }

    
    // Método para eliminar una película por ID
    public void eliminar(int id) throws SQLException {
        String sql = "CALL eliminar_pelicula(?)";

        try (Connection conn = conexionBaseSingleton.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.execute();

            System.out.println("Película eliminada con ID: " + id);

        } catch (SQLException e) {
            System.err.println("Error al eliminar película (SP): " + e.getMessage());
            throw e;
        }
    }

    // Método para buscar una película por ID
    public Pelicula buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM buscar_pelicula_por_id(?)";

        try (Connection conn = conexionBaseSingleton.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetAPelicula(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            System.err.println("Error al buscar película por ID (SP): " + e.getMessage());
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
        String sql = "SELECT * FROM obtener_todas_peliculas()";

        try (Connection conn = conexionBaseSingleton.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                peliculas.add(mapearResultSetAPelicula(rs));
            }

            return peliculas;

        } catch (SQLException e) {
            System.err.println("Error al obtener todas las películas (SP): " + e.getMessage());
            throw e;
        }
    }

    
    // Método adicional: buscar por título (búsqueda parcial)
    public List<Pelicula> buscarPorTitulo(String titulo) throws SQLException {
        List<Pelicula> peliculas = new ArrayList<>();
        String sql = "SELECT * FROM buscar_peliculas_por_titulo(?)";

        try (Connection conn = conexionBaseSingleton.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, titulo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    peliculas.add(mapearResultSetAPelicula(rs));
                }
            }

            return peliculas;

        } catch (SQLException e) {
            System.err.println("Error al buscar películas por título (SP): " + e.getMessage());
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
