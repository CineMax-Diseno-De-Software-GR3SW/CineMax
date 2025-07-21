package com.cinemax.salas.modelos.persistencia;

import com.cinemax.salas.modelos.entidades.Butaca;
import src.main.java.com.cinemax.comun.ConexionBaseSingleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ButacasDAO {

    /** Inserta en la BD y asigna el ID generado al objeto. */
    public void crearButaca(Butaca butaca) throws Exception {
        String sql = """
            INSERT INTO butaca (sala_id, fila, columna, estado)
            VALUES (?, ?, ?,?::estado_butaca)
            RETURNING id
        """;
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, butaca.getIdSala());
            ps.setString(2, butaca.getFila());
            ps.setString(3, butaca.getColumna());
            ps.setString(4, butaca.getEstado());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    butaca.setId(rs.getInt("id"));
                } else {
                    throw new Exception("No se obtuvo ID al crear butaca.");
                }
            }
        }
    }

    /** Recupera una butaca por su ID. */
    public Butaca obtenerButacaPorId(int id) throws Exception {
        String sql = "SELECT id, sala_id, fila, columna, estado FROM butaca WHERE id = ?";
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Butaca b = new Butaca();
                    b.setId(rs.getInt("id"));
                    b.setIdSala(rs.getInt("sala_id"));
                    b.setFila(rs.getString("fila"));
                    b.setColumna(rs.getString("columna"));
                    b.setEstado(rs.getString("estado"));
                    return b;
                } else {
                    return null;
                }
            }
        }
    }

    /** Lista todas las butacas de la base de datos. */
    public List<Butaca> listarTodasButacas() throws Exception {
        String sql = "SELECT id, sala_id, fila, columna, estado FROM butaca ORDER BY sala_id, fila, columna";
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Butaca> lista = new ArrayList<>();
            while (rs.next()) {
                Butaca b = new Butaca();
                b.setId(rs.getInt("id"));
                b.setIdSala(rs.getInt("sala_id"));
                b.setFila(rs.getString("fila"));
                b.setColumna(rs.getString("columna"));
                b.setEstado(rs.getString("estado"));
                lista.add(b);
            }
            return lista;
        }
    }

    /** Lista las butacas de una sala concreta. */
    public List<Butaca> listarButacasPorSala(int salaId) throws Exception {
        String sql = "SELECT id, sala_id, fila, columna, estado FROM butaca WHERE sala_id = ? ORDER BY fila, columna";
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, salaId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Butaca> resultado = new ArrayList<>();
                while (rs.next()) {
                    Butaca b = new Butaca();
                    b.setId(rs.getInt("id"));
                    b.setIdSala(rs.getInt("sala_id"));
                    b.setFila(rs.getString("fila"));
                    b.setColumna(rs.getString("columna"));
                    b.setEstado(rs.getString("estado"));
                    resultado.add(b);
                }
                return resultado;
            }
        }
    }

    /** Actualiza una butaca existente. */
    public void actualizarButaca(Butaca butaca) throws Exception {
        String sql = """
            UPDATE butaca
               SET sala_id = ?, fila = ?, columna = ?, estado  = ?::estado_butaca
             WHERE id = ?
        """;
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, butaca.getIdSala());
            ps.setString(2, butaca.getFila());
            ps.setString(3, butaca.getColumna());
            ps.setString(4, butaca.getEstado());
            ps.setInt(5, butaca.getId());

            if (ps.executeUpdate() == 0) {
                throw new Exception("No existe butaca con ID " + butaca.getId());
            }
        }
    }

    /** Elimina una butaca de la BD. */
    public void eliminarButaca(int id) throws Exception {
        String sql = "DELETE FROM butaca WHERE id = ?";
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
