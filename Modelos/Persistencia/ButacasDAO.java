package Modelos.Persistencia;

import Modelos.Entidades.Butaca;
import Modelos.Entidades.EstadoButaca;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ButacasDAO {

    public void crearButaca(Butaca butaca) throws SQLException {
        Connection conn = GestorDB.obtenerInstancia().obtenerConexion();

        // 1. Obtener la capacidad máxima de la sala
        String sqlCapacidad = "SELECT capacidad FROM Sala WHERE id = ?";
        int capacidad = 0;
        try (PreparedStatement stmt = conn.prepareStatement(sqlCapacidad)) {
            stmt.setInt(1, butaca.getSalaId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                capacidad = rs.getInt("capacidad");
            } else {
                throw new SQLException("Sala no encontrada");
            }
        }

        // 2. Contar las butacas existentes en la sala
        String sqlCount = "SELECT COUNT(*) AS total FROM Butaca WHERE sala_id = ?";
        int totalButacas = 0;
        try (PreparedStatement stmt = conn.prepareStatement(sqlCount)) {
            stmt.setInt(1, butaca.getSalaId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalButacas = rs.getInt("total");
            }
        }

        // 3. Validar la capacidad
        if (totalButacas >= capacidad) {
            throw new SQLException("No se pueden agregar más butacas: capacidad máxima alcanzada para la sala.");
        }

        // 4. Insertar la butaca si no se supera la capacidad
        String sql = "INSERT INTO Butaca (sala_id, fila, columna, estado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, butaca.getSalaId());
            stmt.setInt(2, butaca.getFila());
            stmt.setInt(3, butaca.getColumna());
            stmt.setString(4, butaca.getEstado().name());
            stmt.executeUpdate();
        }
    }

    public Butaca obtenerButacaPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Butaca WHERE id = ?";
        Connection conn = GestorDB.obtenerInstancia().obtenerConexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Butaca(
                        rs.getInt("id"),
                        rs.getInt("sala_id"),
                        rs.getInt("fila"),
                        rs.getInt("columna"),
                        EstadoButaca.valueOf(rs.getString("estado"))
                );
            }
        }
        return null;
    }

    public List<Butaca> listarButacasPorSala(int salaId) throws SQLException {
        List<Butaca> butacas = new ArrayList<>();
        String sql = "SELECT * FROM Butaca WHERE sala_id = ?";
        Connection conn = GestorDB.obtenerInstancia().obtenerConexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                butacas.add(new Butaca(
                        rs.getInt("id"),
                        rs.getInt("sala_id"),
                        rs.getInt("fila"),
                        rs.getInt("columna"),
                        EstadoButaca.valueOf(rs.getString("estado"))
                ));
            }
        }
        return butacas;
    }

    public void actualizarButaca(Butaca butaca) throws SQLException {
        String sql = "UPDATE Butaca SET sala_id=?, fila=?, columna=?, estado=? WHERE id=?";
        Connection conn = GestorDB.obtenerInstancia().obtenerConexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, butaca.getSalaId());
            stmt.setInt(2, butaca.getFila());
            stmt.setInt(3, butaca.getColumna());
            stmt.setString(4, butaca.getEstado().name());
            stmt.setInt(5, butaca.getId());
            stmt.executeUpdate();
        }
    }

    public void eliminarButaca(int id) throws SQLException {
        String sql = "DELETE FROM Butaca WHERE id=?";
        Connection conn = GestorDB.obtenerInstancia().obtenerConexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}