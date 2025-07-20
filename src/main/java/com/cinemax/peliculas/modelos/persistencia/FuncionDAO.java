package com.cinemax.peliculas.modelos.persistencia;

import com.cinemax.comun.modelos.persistencia.ConexionBaseSingleton;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.entidades.FormatoFuncion;
import com.cinemax.peliculas.modelos.entidades.TipoEstreno;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.modelos.persistencia.SalaDAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FuncionDAO {

    private ConexionBaseSingleton gestorDB;

    public FuncionDAO() {
        this.gestorDB = ConexionBaseSingleton.getInstancia();
    }

    public void guardar(Funcion funcion) throws SQLException {
        String sql = "INSERT INTO funcion (id_pelicula, id_sala, fecha_hora_inicio, fecha_hora_fin, formato, tipo_estreno) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = gestorDB.getConexion();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, funcion.getPelicula().getId());
            stmt.setInt(2, funcion.getSala().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(funcion.getFechaHoraInicio()));
            stmt.setTimestamp(4, Timestamp.valueOf(funcion.getFechaHoraFin()));
            stmt.setString(5, funcion.getFormato().toString());
            stmt.setString(6, funcion.getTipoEstreno().name());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    funcion.setId(generatedKeys.getInt(1));
                    System.out.println("Función guardada con ID: " + funcion.getId());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar función: " + e.getMessage(), e);
        }
    }

    public List<Funcion> listarTodas() {
        List<Funcion> funciones = new ArrayList<>();
        String sql = "SELECT * FROM funcion";
        PeliculaDAO peliculaDAO = new PeliculaDAO();
        SalaDAO salaDAO = new SalaDAO();

        try (Connection conn = gestorDB.getConexion();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int peliculaId = rs.getInt("id_pelicula");
                int salaId = rs.getInt("id_sala");
                Pelicula pelicula = peliculaDAO.buscarPorId(peliculaId);
                Sala sala = salaDAO.buscarPorId(salaId);

                Funcion funcion = new Funcion(
                        rs.getInt("id_funcion"),
                        pelicula,
                        sala,
                        rs.getTimestamp("fecha_hora_inicio").toLocalDateTime(),
                        rs.getTimestamp("fecha_hora_fin").toLocalDateTime(),
                        FormatoFuncion.fromString(rs.getString("formato")),
                        TipoEstreno.valueOf(rs.getString("tipo_estreno")));
                funciones.add(funcion);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar funciones: " + e.getMessage(), e);
        }
        return funciones;
    }

    public List<Funcion> listarPorSala(int salaId) {
        List<Funcion> funciones = new ArrayList<>();
        String sql = "SELECT * FROM funcion WHERE id_sala = ?";
        PeliculaDAO peliculaDAO = new PeliculaDAO();
        SalaDAO salaDAO = new SalaDAO();

        try (Connection conn = gestorDB.getConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int peliculaId = rs.getInt("id_pelicula");
                    Pelicula pelicula = peliculaDAO.buscarPorId(peliculaId);
                    Sala sala = salaDAO.buscarPorId(salaId);

                    Funcion funcion = new Funcion(
                            rs.getInt("id_funcion"),
                            pelicula,
                            sala,
                            rs.getTimestamp("fecha_hora_inicio").toLocalDateTime(),
                            rs.getTimestamp("fecha_hora_fin").toLocalDateTime(),
                            FormatoFuncion.fromString(rs.getString("formato")),
                            TipoEstreno.valueOf(rs.getString("tipo_estreno")));
                    funciones.add(funcion);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar funciones por sala: " + e.getMessage(), e);
        }
        return funciones;
    }

    public void editar(Funcion funcion) throws SQLException {
        String sql = "UPDATE funcion SET id_pelicula = ?, id_sala = ?, fecha_hora_inicio = ?, fecha_hora_fin = ?, formato = ?, tipo_estreno = ? WHERE id_funcion = ?";
        try (Connection conn = gestorDB.getConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, funcion.getPelicula().getId());
            stmt.setInt(2, funcion.getSala().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(funcion.getFechaHoraInicio()));
            stmt.setTimestamp(4, Timestamp.valueOf(funcion.getFechaHoraFin()));
            stmt.setString(5, funcion.getFormato().toString());
            stmt.setString(6, funcion.getTipoEstreno().name());
            stmt.setInt(7, funcion.getId());

            int filas = stmt.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se encontró la función a editar.");
            }
        }
    }

    public Funcion buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM funcion WHERE id_funcion = ?";
        try (Connection conn = gestorDB.getConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Aquí debes reconstruir el objeto Funcion como en listarTodas()
                    // Suponiendo que tienes PeliculaDAO y SalaDAO disponibles:
                    PeliculaDAO peliculaDAO = new PeliculaDAO();
                    SalaDAO salaDAO = new SalaDAO();
                    Pelicula pelicula = peliculaDAO.buscarPorId(rs.getInt("id_pelicula"));
                    Sala sala = salaDAO.buscarPorId(rs.getInt("id_sala"));
                    return new Funcion(
                            rs.getInt("id_funcion"),
                            pelicula,
                            sala,
                            rs.getTimestamp("fecha_hora_inicio").toLocalDateTime(),
                            rs.getTimestamp("fecha_hora_fin").toLocalDateTime(),
                            FormatoFuncion.fromString(rs.getString("formato")),
                            TipoEstreno.valueOf(rs.getString("tipo_estreno")));
                }
            }
        }
        return null;
    }

    public void eliminar(int id) throws SQLException {

        String sql = "DELETE FROM funcion WHERE id_funcion = ?";

        try (Connection conn = gestorDB.getConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró la función con ID: " + id);
            } else {
                System.out.println("Película eliminada correctamente con ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar película: " + e.getMessage());
            throw e;
        }
    }

    public List<Integer> listarIdsPeliculasDeFuncionesFuturas() {
        List<Integer> idsPeliculas = new ArrayList<>();
        String sql = "SELECT DISTINCT id_pelicula FROM funcion WHERE fecha_hora_inicio > ?";
        try (Connection conn = gestorDB.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    idsPeliculas.add(rs.getInt("id_pelicula"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar IDs de películas de funciones futuras: " + e.getMessage(), e);
        }
        return idsPeliculas;
    }

}