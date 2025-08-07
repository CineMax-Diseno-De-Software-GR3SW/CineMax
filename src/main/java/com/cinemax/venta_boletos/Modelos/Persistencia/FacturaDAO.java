package com.cinemax.venta_boletos.Modelos.Persistencia;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.cinemax.comun.ConexionBaseSingleton;
import com.cinemax.venta_boletos.Modelos.entidades.Cliente;
import com.cinemax.venta_boletos.Modelos.entidades.Factura;

/** 
 * Clase FacturaDAO que maneja las operaciones de persistencia para la entidad Factura.
 * Utiliza procedimientos almacenados para crear, actualizar, eliminar y buscar facturas.
 */
public class FacturaDAO {

    /** Instancia singleton para manejar la conexión a la base de datos */
    private final ConexionBaseSingleton conexionBase;

    /** Constructor de la clase FacturaDAO que inicializa la conexión a la base de datos */
    public FacturaDAO() {
        this.conexionBase = ConexionBaseSingleton.getInstancia();
    }

    /**
     * Crea una nueva factura en la base de datos.
     * @param factura El objeto Factura que contiene la información de la factura a insertar.
     * @throws Exception Si ocurre un error al insertar la factura.
     */
    public void crearFactura(Factura factura) throws Exception {
        String sql = "SELECT crear_factura(?, ?, ?, ?, ?)";
        try (Connection conn = conexionBase.conectar();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, factura.getCodigoFactura());
            ps.setString(2, factura.getCliente().getIdCliente());

            DateTimeFormatter entrada = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime fechaFormateada = LocalDateTime.parse(factura.getFecha(), entrada);
            ps.setTimestamp(3, Timestamp.valueOf(fechaFormateada));

            ps.setBigDecimal(4, BigDecimal.valueOf(factura.getSubTotal()));
            ps.setBigDecimal(5, BigDecimal.valueOf(factura.getTotal()));

            ps.execute();
        }
    }

    /**
     * Actualiza la información de una factura existente en la base de datos.
     * @param factura El objeto Factura que contiene la información actualizada de la factura.
     * @throws Exception Si ocurre un error al actualizar la factura.
     */
    public void actualizarFactura(Factura factura) throws Exception {
        String sql = "SELECT actualizar_factura(?, ?, ?, ?, ?)";
        try (Connection conn = conexionBase.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, factura.getCodigoFactura());
            ps.setString(2, factura.getCliente().getIdCliente());
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(factura.getFecha()));
            ps.setBigDecimal(4, BigDecimal.valueOf(factura.getSubTotal()));
            ps.setBigDecimal(5, BigDecimal.valueOf(factura.getTotal()));

            ps.execute();
        }
    }

    /**
     * Elimina una factura de la base de datos.
     * @param idFactura El ID de la factura a eliminar.
     * @throws Exception Si ocurre un error al eliminar la factura.
     */
    public void eliminarFactura(Long idFactura) throws Exception {
        String sql = "SELECT eliminar_factura(?)";
        try (Connection conn = conexionBase.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idFactura);
            ps.execute();
        }
    }

    /**
     * Busca una factura por su ID.
     * @param idFactura El ID de la factura a buscar.
     * @return El objeto Factura encontrado o null si no se encuentra.
     * @throws Exception Si ocurre un error al buscar la factura.
     */
    public Factura buscarPorId(Long idFactura) throws Exception {
        String sql = "SELECT * FROM obtener_factura(?)";
        try (Connection conn = conexionBase.conectar();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idFactura);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String idCliente = rs.getString("idcliente");

                // Nueva consulta para obtener todos los datos del cliente
                Cliente cliente = null;
                String clienteSql = "SELECT * FROM obtener_cliente(?)";
                try (PreparedStatement psCliente = conn.prepareStatement(clienteSql)) {
                    psCliente.setString(1, idCliente);
                    ResultSet rsCliente = psCliente.executeQuery();

                    if (rsCliente.next()) {
                        cliente = new Cliente(
                            rsCliente.getString("nombre"),
                            rsCliente.getString("apellido"),
                            rsCliente.getString("idcliente"),
                            rsCliente.getString("correo"),
                            rsCliente.getString("tipodocumento") 
                        );
                    }
                }

                return new Factura(
                    rs.getLong("idfactura"),
                    rs.getTimestamp("fecha").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    cliente,
                    rs.getBigDecimal("subtotal").doubleValue(),
                    rs.getBigDecimal("total").doubleValue()
                );
            }
            return null;
        }
    }

    /**
     * Lista todas las facturas en la base de datos.
     * @return Una lista de objetos Factura.
     * @throws Exception Si ocurre un error al listar las facturas.
     */
    public List<Factura> listarTodas() throws Exception {
        List<Factura> lista = new ArrayList<>();
        String sql = "SELECT * FROM listar_facturas()";

        try (Connection conn = conexionBase.conectar();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String idCliente = rs.getString("idcliente");

                // Obtener cliente completo
                Cliente cliente = null;
                String sqlCliente = "SELECT * FROM obtener_cliente(?)";
                try (PreparedStatement psCliente = conn.prepareStatement(sqlCliente)) {
                    psCliente.setString(1, idCliente);
                    ResultSet rsCliente = psCliente.executeQuery();
                    if (rsCliente.next()) {
                        cliente = new Cliente(
                            rsCliente.getString("nombre"),
                            rsCliente.getString("apellido"),
                            rsCliente.getString("idcliente"),
                            rsCliente.getString("correo"),
                            rsCliente.getString("tipodocumento") 
                        );
                    }
                }

                Factura factura = new Factura(
                        rs.getLong("idfactura"),
                        rs.getTimestamp("fecha").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        cliente,
                        rs.getBigDecimal("subtotal").doubleValue(),
                        rs.getBigDecimal("total").doubleValue()
                );
                lista.add(factura);
            }
        }
        return lista;
    }

}
