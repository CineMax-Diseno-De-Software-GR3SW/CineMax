package com.cinemax.salas.modelos.persistencia;

import com.cinemax.comun.modelos.persistencia.GestorDB;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.modelos.TipoSala;
import com.cinemax.salas.modelos.EstadoSala;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalaDAO {

    public Sala buscarPorId(int id) {
    String sql = "SELECT id, nombre, capacidad, tipo, estado FROM sala WHERE id = ?";
    try (Connection conn = GestorDB.obtenerInstancia().conectarDB();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, id);
        try (ResultSet rs = stmt.executeQuery()) {
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
    } catch (SQLException e) {
        throw new RuntimeException("Error al buscar sala por ID: " + e.getMessage(), e);
    }
    return null;
}

   public List<Sala> listarTodas() {
    List<Sala> salas = new ArrayList<>();
    String sql = "SELECT id, nombre, capacidad, tipo, estado FROM sala";
    try (Connection conn = GestorDB.obtenerInstancia().conectarDB();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            salas.add(new Sala(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getInt("capacidad"),
                TipoSala.valueOf(rs.getString("tipo")),
                EstadoSala.valueOf(rs.getString("estado"))
            ));
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error al listar salas: " + e.getMessage(), e);
    }
    return salas;
}
}
