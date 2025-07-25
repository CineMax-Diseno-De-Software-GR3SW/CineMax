package com.cinemax.salas.modelos.persistencia;

import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.comun.modelos.persistencia.ConexionBaseSingleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ButacasDAO {

    /** Inserta en la BD usando la función crear_butaca y asigna el ID devuelto. */
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
    public void generarButacas(int salaId, int filas, int columnas) throws Exception {
        // Usamos la misma función crear_butaca, pero invocada como SELECT
        String sql = "SELECT crear_butaca(?, ?, ?, ?::estado_butaca)";
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Preparamos todo el batch
            for (int i = 0; i < filas; i++) {
                char letra = (char)('A' + i);
                for (int j = 1; j <= columnas; j++) {
                    ps.setInt   (1, salaId);
                    ps.setString(2, String.valueOf(letra));
                    ps.setString(3, String.valueOf(j));
                    ps.setString(4, "DISPONIBLE");
                    ps.addBatch();
                }
            }

            // Una sola ida al servidor para ejecutar todas las inserciones
            ps.executeBatch();
        }
    }

    /** Recupera una butaca por su ID llamando a obtener_butaca_por_id. */
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

    /** Lista todas las butacas usando listar_todas_butacas. */
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

    /** Lista las butacas de una sala concreta. */
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

    /** Actualiza una butaca existente llamando a actualizar_butaca. */
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

    /** Elimina una butaca de la BD llamando a eliminar_butaca. */
    public void eliminarButaca(int id) throws Exception {
        String call = "{ call eliminar_butaca(?) }";
        try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.setInt(1, id);
            cs.execute();
        }
    }
}
