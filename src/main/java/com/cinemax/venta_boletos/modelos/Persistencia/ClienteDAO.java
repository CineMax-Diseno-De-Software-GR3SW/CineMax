package com.cinemax.venta_boletos.modelos.persistencia;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.cinemax.comun.ConexionBaseSingleton;
import com.cinemax.venta_boletos.modelos.entidades.Cliente;

/**
 * Clase ClienteDAO que maneja las operaciones de persistencia para la entidad Cliente.
 * Utiliza procedimientos almacenados para crear, actualizar, eliminar y buscar clientes.
 */
public class ClienteDAO {
    
    /** Instancia singleton para manejar la conexión a la base de datos */
    private final ConexionBaseSingleton conexionBase;

    /** Constructor de la clase ClienteDAO que inicializa la conexión a la base de datos */
    public ClienteDAO() {
        this.conexionBase = ConexionBaseSingleton.getInstancia();
    }

    /**
     * Crea un nuevo cliente en la base de datos.
     * @param cliente El objeto Cliente que contiene la información del cliente a insertar.
     * @throws Exception Si ocurre un error al insertar el cliente.
     */
    public void crearCliente(Cliente cliente) throws Exception {
        String sql = "{ CALL crear_cliente(?, ?, ?, ?, ?) }";
        try (Connection conn = conexionBase.conectar();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, cliente.getIdCliente());
            cs.setString(2, cliente.getTipoDocumento());
            cs.setString(3, cliente.getNombre());
            cs.setString(4, cliente.getApellido());
            cs.setString(5, cliente.getCorreoElectronico());

            cs.execute();
        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
        }
    }

    /**
     * Actualiza la información de un cliente existente en la base de datos.
     * @param cliente El objeto Cliente que contiene la información actualizada del cliente.
     * @throws Exception Si ocurre un error al actualizar el cliente.
     */
    public void actualizarCliente(Cliente cliente) throws Exception {
        String sql = "{ CALL actualizar_cliente(?, ?, ?, ?) }"; // solo 4 parámetros
        try (Connection conn = conexionBase.conectar();
            CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, cliente.getIdCliente());
            cs.setString(2, cliente.getNombre());
            cs.setString(3, cliente.getApellido());
            cs.setString(4, cliente.getCorreoElectronico());

            cs.execute();
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
        }
    }

    /**
     * Elimina un cliente de la base de datos.
     * @param numeroDocumento El número de documento del cliente a eliminar.
     * @throws Exception Si ocurre un error al eliminar el cliente.
     */
    public void eliminarCliente(String numeroDocumento) throws Exception {
        String sql = "{ CALL eliminar_cliente(?) }";
        try (Connection conn = conexionBase.conectar();
            CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, numeroDocumento);
            cs.execute();
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
        }
    }

    /**
     * Busca un cliente por su número de documento.
     * @param numeroDocumento El número de documento del cliente a buscar.
     * @return El objeto Cliente encontrado o null si no se encuentra.
     * @throws Exception Si ocurre un error al buscar el cliente.
     */
    public Cliente buscarPorId(String numeroDocumento) throws Exception {
        String sql = "SELECT * FROM obtener_cliente(?)";
        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = conexionBase.conectar();
            ps = conn.prepareStatement(sql);
            ps.setString(1, numeroDocumento);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Cliente(
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("idcliente"),
                        rs.getString("correo"),
                        rs.getString("tipodocumento")
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

    /**
     * Lista todos los clientes en la base de datos.
     * @return Una lista de objetos Cliente.
     * @throws Exception Si ocurre un error al listar los clientes.
     */
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
                    rs.getString("correo"),
                    rs.getString("tipodocumento")
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
