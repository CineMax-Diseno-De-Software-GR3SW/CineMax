package com.cinemax.venta_boletos.Modelos.Persistencia;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.cinemax.comun.ConexionBaseSingleton;
import com.cinemax.venta_boletos.Modelos.Cliente;
import com.cinemax.venta_boletos.Modelos.Factura;

public class FacturaDAO {
    
    private final ConexionBaseSingleton conexionBase;

    public FacturaDAO() {
        this.conexionBase = ConexionBaseSingleton.getInstancia();
    }

    public void crearFactura(Factura factura) throws Exception {
        String sql = "SELECT crear_factura(?, ?, ?, ?)";
        try (Connection conn = conexionBase.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, factura.getCliente().getCedula());
            ps.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            ps.setBigDecimal(3, BigDecimal.valueOf(factura.getSubTotal()));
            ps.setBigDecimal(4, BigDecimal.valueOf(factura.getTotal()));

            ps.execute();
        }
    }

    public void actualizarFactura(Factura factura) throws Exception {
        String sql = "SELECT actualizar_factura(?, ?, ?, ?, ?)";
        try (Connection conn = conexionBase.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, Long.parseLong(factura.getCodigoFactura()));
            ps.setString(2, factura.getCliente().getCedula());
            ps.setDate(3, java.sql.Date.valueOf(factura.getFecha()));
            ps.setBigDecimal(4, BigDecimal.valueOf(factura.getSubTotal()));
            ps.setBigDecimal(5, BigDecimal.valueOf(factura.getTotal()));

            ps.execute();
        }
    }

    public void eliminarFactura(Long idFactura) throws Exception {
        String sql = "SELECT eliminar_factura(?)";
        try (Connection conn = conexionBase.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idFactura);
            ps.execute();
        }
    }

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
                            rsCliente.getString("correo")
                        );
                    }
                }

                return new Factura(
                        String.valueOf(rs.getLong("idfactura")),
                        rs.getTimestamp("fecha").toLocalDateTime().toLocalDate().toString(),
                        cliente,
                        rs.getBigDecimal("subtotal").doubleValue(),
                        rs.getBigDecimal("total").doubleValue()
                );
            }
            return null;
        }
    }

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
                            rsCliente.getString("correo")
                        );
                    }
                }

                Factura factura = new Factura(
                        String.valueOf(rs.getLong("idfactura")),
                        rs.getTimestamp("fecha").toLocalDateTime().toLocalDate().toString(),
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
