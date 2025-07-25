package com.cinemax.salas.modelos.persistencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.cinemax.comun.modelos.persistencia.ConexionBaseSingleton;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.modelos.entidades.EstadoSala;
import com.cinemax.salas.modelos.entidades.TipoSala;


public class SalasDAO {

    public void crearSala(Sala sala) throws SQLException {
        String sql = "INSERT INTO Sala (nombre, capacidad, tipo, estado) VALUES (?, ?, ?::tipo_sala, ?::estado_sala) RETURNING id";
        Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sala.getNombre());
            stmt.setInt(2, sala.getCapacidad());
            stmt.setString(3, sala.getTipo().name());
            stmt.setString(4, sala.getEstado().name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                sala.setId(rs.getInt(1));
            }
        }
    }

    public Sala obtenerSalaPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Sala WHERE id = ?";
        Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Sala(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("capacidad"),
                        TipoSala.valueOf(rs.getString("tipo")),
                        EstadoSala.valueOf(rs.getString("estado"))
                );
            }
        }
        return null;
    }

    public List<Sala> listarSalas() throws SQLException {
        List<Sala> salas = new ArrayList<>();
        String sql = "SELECT * FROM Sala";
        Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                salas.add(new Sala(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("capacidad"),
                        TipoSala.valueOf(rs.getString("tipo")),
                        EstadoSala.valueOf(rs.getString("estado"))
                ));
            }
        }
        return salas;
    }

    public void actualizarSala(Sala sala) throws SQLException {
        String sql = "UPDATE Sala SET nombre=?, capacidad=?, tipo=?::tipo_sala, estado=?::estado_sala WHERE id=?";
        Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sala.getNombre());
            stmt.setInt(2, sala.getCapacidad());
            stmt.setString(3, sala.getTipo().name());
            stmt.setString(4, sala.getEstado().name());
            stmt.setInt(5, sala.getId());
            stmt.executeUpdate();
        }
    }

    public void eliminarSala(int id) throws SQLException {
        String sql = "DELETE FROM Sala WHERE id=?";
        Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}