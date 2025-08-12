package com.cinemax.salas.modelos.persistencia;

import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.utilidades.conexiones.ConexionBaseSingleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para gestionar operaciones de persistencia
 * relacionadas con la entidad {@link Butaca}.
 *
 * Propósito:
 * - Encapsular el acceso a la base de datos para CRUD de butacas.
 * - Ejecutar funciones y procedimientos almacenados en la BD.
 *
 * Métodos:
 * - crearButaca(): inserta una nueva butaca usando función PL/pgSQL.
 * - generarButacas(): crea automáticamente un conjunto de butacas para una sala.
 * - obtenerButacaPorId(): recupera una butaca específica.
 * - listarTodasButacas(): obtiene todas las butacas registradas.
 * - listarButacasPorSala(): obtiene las butacas de una sala en particular.
 * - actualizarButaca(): modifica datos de una butaca existente.
 * - eliminarButaca(): elimina una butaca por su ID.
 */
public class ButacasDAO {

    /**
     * Inserta una nueva butaca en la BD usando la función crear_butaca
     * y asigna el ID generado al objeto.
     *
     * @param butaca objeto {@link Butaca} con los datos a insertar.
     * @throws Exception si ocurre un error de conexión o ejecución SQL.
     */
    public void crearButaca(Butaca butaca) throws Exception {
        String call = "{ ? = call crear_butaca( ?, ?, ?, ?::estado_butaca ) }";
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, butaca.getIdSala());
            cs.setString(3, butaca.getFila());
            cs.setString(4, butaca.getColumna());
            cs.setString(5, butaca.getEstado());

            cs.execute();
            butaca.setId(cs.getInt(1));
        }
    }

    /**
     * Genera de forma masiva todas las butacas de una sala
     * (según número de filas y columnas) con estado DISPONIBLE.
     *
     * @param salaId ID de la sala.
     * @param filas número total de filas.
     * @param columnas número total de columnas.
     * @throws Exception si ocurre un error de conexión o ejecución SQL.
     */
    public void generarButacas(int salaId, int filas, int columnas) throws Exception {
        String sql = "SELECT crear_butaca(?, ?, ?, ?::estado_butaca)";
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Genera y agrega todas las butacas al batch
            for (int i = 0; i < filas; i++) {
                char letra = (char) ('A' + i);
                for (int j = 1; j <= columnas; j++) {
                    ps.setInt(1, salaId);
                    ps.setString(2, String.valueOf(letra));
                    ps.setString(3, String.valueOf(j));
                    ps.setString(4, "DISPONIBLE");
                    ps.addBatch();
                }
            }
            // Ejecuta todas las inserciones en una sola llamada
            ps.executeBatch();
        }
    }

    /**
     * Recupera una butaca por su ID llamando a la función obtener_butaca_por_id.
     *
     * @param id identificador único de la butaca.
     * @return objeto {@link Butaca} si existe, null si no se encontró.
     * @throws Exception si ocurre un error SQL.
     */
    public Butaca obtenerButacaPorId(int id) throws Exception {
        String sql = "SELECT * FROM obtener_butaca_por_id(?)";
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
                }
                return null;
            }
        }
    }

    /**
     * Lista todas las butacas registradas en la base de datos.
     *
     * @return lista de objetos {@link Butaca}.
     * @throws Exception si ocurre un error SQL.
     */
    public List<Butaca> listarTodasButacas() throws Exception {
        String sql = "SELECT * FROM listar_todas_butacas()";
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

    /**
     * Lista las butacas asociadas a una sala específica.
     *
     * @param salaId ID de la sala.
     * @return lista de butacas de la sala indicada.
     * @throws Exception si ocurre un error SQL.
     */
    public List<Butaca> listarButacasPorSala(int salaId) throws Exception {
        String sql = "SELECT * FROM listar_butacas_por_sala(?)";
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

    /**
     * Actualiza una butaca existente llamando al procedimiento actualizar_butaca.
     *
     * @param butaca objeto {@link Butaca} con los nuevos valores.
     * @throws Exception si ocurre un error SQL.
     */
    public void actualizarButaca(Butaca butaca) throws Exception {
        String call = "{ call actualizar_butaca( ?, ?, ?, ?, ?::estado_butaca ) }";
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.setInt(1, butaca.getId());
            cs.setInt(2, butaca.getIdSala());
            cs.setString(3, butaca.getFila());
            cs.setString(4, butaca.getColumna());
            cs.setString(5, butaca.getEstado());

            cs.execute();
        }
    }

    /**
     * Elimina una butaca de la base de datos llamando al procedimiento eliminar_butaca.
     *
     * @param id identificador único de la butaca a eliminar.
     * @throws Exception si ocurre un error SQL.
     */
    public void eliminarButaca(int id) throws Exception {
        String call = "{ call eliminar_butaca(?) }";
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.setInt(1, id);
            cs.execute();
        }
    }
}

