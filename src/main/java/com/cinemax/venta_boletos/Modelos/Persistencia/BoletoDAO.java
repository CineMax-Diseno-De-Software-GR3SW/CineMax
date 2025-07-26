package com.cinemax.venta_boletos.Modelos.Persistencia;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.cinemax.comun.ConexionBaseSingleton;
import com.cinemax.venta_boletos.Modelos.Boleto;

public class BoletoDAO {
    
private final ConexionBaseSingleton conexionBase;

    public BoletoDAO() {
        this.conexionBase = ConexionBaseSingleton.getInstancia();
    }

    public void crearBoleto(Boleto boleto) throws Exception {
        String sql = "{ CALL insertar_boleto(?, ?, ?) }";
        try (Connection conn = conexionBase.conectar();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, boleto.getIdFuncion());
            cs.setLong(2, boleto.getIdFactura());
            cs.setInt(3, boleto.getIdButaca());

            cs.execute();
        } catch (SQLException e) {
            System.err.println("Error al insertar boleto: " + e.getMessage());
        }
    }

    public void actualizarBoleto(Boleto boleto) throws Exception {
        String sql = "{ CALL actualizar_boleto(?, ?, ?, ?) }";
        try (Connection conn = conexionBase.conectar();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setLong(1, boleto.getIdBoleto());
            cs.setInt(2, boleto.getIdFuncion());
            cs.setLong(3, boleto.getIdFactura());
            cs.setInt(4, boleto.getIdButaca());

            cs.execute();
        } catch (SQLException e) {
            System.err.println("Error al actualizar boleto: " + e.getMessage());
        }
    }

    public void eliminarBoleto(long idBoleto) throws Exception {
        String sql = "{ CALL eliminar_boleto(?) }";
        try (Connection conn = conexionBase.conectar();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setLong(1, idBoleto);
            cs.execute();
        } catch (SQLException e) {
            System.err.println("Error al eliminar boleto: " + e.getMessage());
        }
    }

    public Boleto buscarPorId(long idBoleto) throws Exception {
        String sql = "SELECT * FROM obtener_boleto(?)";
        try (Connection conn = conexionBase.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idBoleto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Boleto(
                    rs.getLong("idboleto"),
                    rs.getInt("idfuncion"),
                    rs.getLong("idfactura"),
                    rs.getInt("idbutaca")
                );
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error al buscar boleto por ID: " + e.getMessage());
            return null;
        }
    }

    public List<Boleto> listarPorFactura(long idFactura) throws Exception {
        List<Boleto> lista = new ArrayList<>();
        String sql = "SELECT * FROM obtener_boletos_por_factura(?)";
        try (Connection conn = conexionBase.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idFactura);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Boleto(
                    rs.getLong("idboleto"),
                    rs.getInt("idfuncion"),
                    rs.getLong("idfactura"),
                    rs.getInt("idbutaca")
                ));
            }
            return lista;
        } catch (SQLException e) {
            System.err.println("Error al obtener boletos por factura: " + e.getMessage());
            return lista;
        }
    }

    public List<Boleto> listarTodos() throws Exception {
        List<Boleto> boletos = new ArrayList<>();
        String sql = "SELECT * FROM listar_boletos()";
        try (Connection conn = conexionBase.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                boletos.add(new Boleto(
                    rs.getLong("idboleto"),
                    rs.getInt("idfuncion"),
                    rs.getLong("idfactura"),
                    rs.getInt("idbutaca")
                ));
            }
            return boletos;
        } catch (SQLException e) {
            System.err.println("Error al listar boletos: " + e.getMessage());
            return boletos;
        }
    }
}
