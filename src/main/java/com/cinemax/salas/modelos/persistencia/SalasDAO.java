package com.cinemax.salas.modelos.persistencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.modelos.entidades.EstadoSala;
import com.cinemax.salas.modelos.entidades.TipoSala;
import com.cinemax.utilidades.conexiones.ConexionBaseSingleton;

/**
 * DAO (Data Access Object) para gestionar operaciones de persistencia
 * relacionadas con la entidad {@link Sala}.
 *
 * Propósito:
 * - Encapsular el acceso a la base de datos para CRUD de salas.
 * - Mapear datos de la tabla Sala a objetos Java.
 *
 * Métodos:
 * - crearSala(): inserta una nueva sala en la base de datos.
 * - obtenerSalaPorId(): busca una sala por su ID.
 * - listarSalas(): obtiene todas las salas registradas.
 * - actualizarSala(): modifica los datos de una sala existente.
 * - eliminarSala(): borra una sala por su ID.
 */
public class SalasDAO {

    /**
     * Inserta una nueva sala en la base de datos y asigna el ID generado.
     *
     * @param sala Objeto {@link Sala} con los datos a registrar.
     * @throws SQLException si ocurre un error de conexión o ejecución SQL.
     */
    public void crearSala(Sala sala) throws SQLException {
        String sql = "INSERT INTO Sala (nombre, capacidad, tipo, estado) " +
                "VALUES (?, ?, ?::tipo_sala, ?::estado_sala) RETURNING id";
        Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sala.getNombre());
            stmt.setInt(2, sala.getCapacidad());
            stmt.setString(3, sala.getTipo().name());
            stmt.setString(4, sala.getEstado().name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                sala.setId(rs.getInt(1)); // Asigna el ID generado
            }
        }
    }
    /**
     * Busca una sala por su nombre, ignorando mayúsculas y minúsculas.
     *
     * @param nombre Nombre de la sala a buscar.
     * @return Objeto {@link Sala} si se encuentra, null si no existe.
     * @throws Exception si ocurre un error en la consulta SQL.
     */
    public Sala buscarSalaPorNombre(String nombre) throws Exception {
        String sql = "SELECT * FROM Sala WHERE LOWER(nombre) = LOWER(?)";
        Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
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
    // src/main/java/com/cinemax/salas/modelos/persistencia/SalasDAO.java
    public List<Sala> buscarSalasPorNombreParcial(String nombre) throws Exception {
        List<Sala> resultado = new ArrayList<>();
        String sql = "SELECT * FROM Sala WHERE nombre ILIKE ?";
        Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nombre + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultado.add(new Sala(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("capacidad"),
                        TipoSala.valueOf(rs.getString("tipo")),
                        EstadoSala.valueOf(rs.getString("estado"))
                ));
            }
        }
        return resultado;
    }
    /**
     * Recupera una sala por su ID.
     *
     * @param id identificador único de la sala.
     * @return Objeto {@link Sala} si existe, null si no se encuentra.
     * @throws SQLException si ocurre un error SQL.
     */
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

    /**
     * Lista todas las salas registradas en la base de datos.
     *
     * @return Lista de objetos {@link Sala}.
     * @throws SQLException si ocurre un error SQL.
     */
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

    /**
     * Actualiza los datos de una sala existente.
     *
     * @param sala Objeto {@link Sala} con los nuevos datos.
     * @throws SQLException si ocurre un error SQL.
     */
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

    /**
     * Elimina una sala de la base de datos.
     *
     * @param id identificador único de la sala a eliminar.
     * @throws SQLException si ocurre un error SQL.
     */
    public void eliminarSala(int id) throws SQLException {
        String sql = "DELETE FROM Sala WHERE id=?";
        Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Verifica si una sala tiene funciones programadas.
     *
     * @param idSala ID de la sala a verificar.
     * @return true si la sala tiene funciones, false en caso contrario.
     * @throws Exception si ocurre un error en la consulta SQL.
     */
    public boolean salaTieneFunciones(int idSala) throws Exception {
        String sql = "SELECT COUNT(*) FROM Funcion WHERE id_sala = ?";
        Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idSala);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}