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
import com.cinemax.venta_boletos.Modelos.Cliente;

public class ClienteDAO {
    
    private final ConexionBaseSingleton conexionBase;

    public ClienteDAO() {
        this.conexionBase = ConexionBaseSingleton.getInstancia();
    }

    public void crearCliente(Cliente cliente) throws Exception {
        String sql = "{ CALL insertar_cliente(?, ?, ?, ?) }";
        try (Connection conn = conexionBase.conectar();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, cliente.getCedula());          
            cs.setString(2, cliente.getNombre());          
            cs.setString(3, cliente.getApellido());        
            cs.setString(4, cliente.getCorreoElectronico());

            cs.execute();
        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
        }
    }

    public void actualizarCliente(Cliente cliente) throws Exception {
        String sql = "{ CALL actualizar_cliente(?, ?, ?, ?) }";
        try (Connection conn = conexionBase.conectar();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, cliente.getCedula());
            cs.setString(2, cliente.getNombre());
            cs.setString(3, cliente.getApellido());
            cs.setString(4, cliente.getCorreoElectronico());

            cs.execute();
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
        }
    }

    public void eliminarCliente(String cedulaCliente) throws Exception {
        String sql = "{ CALL eliminar_cliente(?) }";
        try (Connection conn = conexionBase.conectar();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, cedulaCliente);
            cs.execute();
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
        }
    }

    public Cliente buscarPorId(String cedulaCliente) throws Exception {
        String sql = "SELECT * FROM obtener_cliente(?)";
        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = conexionBase.conectar();
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaCliente);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Cliente(
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("idcliente"),
                        rs.getString("correo")
                );
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por ID: " + e.getMessage());
            return null;
        } finally {
            ConexionBaseSingleton.cerrarRecursos(rs, ps);
            if (conn != null) conn.close();
        }
    }

    public List<Cliente> listarTodos() throws Exception {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM listar_clientes()";
        ResultSet rs = null;
        Statement st = null;
        Connection conn = null;
        try {
            conn = conexionBase.conectar();
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("idcliente"),
                    rs.getString("correo")
                );
                lista.add(cliente);
            }
            return lista;
        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
            return lista;
        } finally {
            ConexionBaseSingleton.cerrarRecursos(rs, st);
            if (conn != null) conn.close();
        }
    }
}
