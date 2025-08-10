package com.cinemax.venta_boletos.Modelos.Persistencia;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cinemax.comun.conexiones.ConexionBaseSingleton;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.venta_boletos.Modelos.entidades.Boleto;
import com.cinemax.venta_boletos.Modelos.entidades.Factura;

/** 
 * Clase BoletoDAO que maneja las operaciones de acceso a datos relacionadas con los boletos.
 * Proporciona métodos para listar butacas ocupadas por función e insertar nuevos boletos.
 */
public class BoletoDAO {
    
    /** Instancia singleton para manejar la conexión a la base de datos */
    private final ConexionBaseSingleton conexionBase;

    /** Constructor de la clase BoletoDAO que inicializa la conexión a la base de datos */
    public BoletoDAO() {
        this.conexionBase = ConexionBaseSingleton.getInstancia();
    }

    /**
     * Lista las butacas ocupadas para una función específica.
     * @param funcion La función para la cual se desean listar las butacas ocupadas.
     * @return Una lista de objetos Butaca que representan las butacas ocupadas en la función.
     * @throws Exception Si ocurre un error al consultar las butacas ocupadas.
     */
    public List<Butaca> listarButacasDeBoletosPorFuncion(Funcion funcion) throws Exception {
        List<Butaca> butacas = new ArrayList<>();
        String sql = "SELECT * FROM listar_butacas_por_funcion(?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = conexionBase.conectar(); 
            ps = conn.prepareStatement(sql);
            ps.setInt(1, funcion.getId());

            rs = ps.executeQuery();

            while (rs.next()) {
                Butaca butaca = new Butaca();
                butaca.setId(rs.getInt("id_butaca"));
                butaca.setIdSala(rs.getInt("sala_id"));
                butaca.setFila(rs.getString("fila"));
                butaca.setColumna(rs.getString("columna"));
                butaca.setEstado(rs.getString("estado"));
                butacas.add(butaca);
            }
            return butacas;
        } catch (SQLException e) {
            System.err.println("Error al listar butacas ocupadas: " + e.getMessage());
            throw new Exception("Error al consultar butacas ocupadas", e);
        } finally {
            ConexionBaseSingleton.cerrarRecursos(rs, ps);
            if (conn != null) conn.close();
        }
    }

    /**
     * Inserta un nuevo boleto en la base de datos.
     * @param boleto El objeto Boleto que contiene la información del boleto a insertar.
     * @param factura La factura asociada al boleto.
     * @throws Exception Si ocurre un error al insertar el boleto.
     */
    public void crearBoleto(Boleto boleto, Factura factura) throws Exception {
        String sql = "{ CALL insertar_boleto(?, ?, ?) }";
        try (Connection conn = conexionBase.conectar();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, boleto.getFuncion().getId());
            cs.setLong(2, factura.getCodigoFactura());
            cs.setInt(3, boleto.getButaca().getId());

            cs.execute();
        } catch (SQLException e) {
            System.err.println("Error al insertar boleto: " + e.getMessage());
        }
    }
 }
