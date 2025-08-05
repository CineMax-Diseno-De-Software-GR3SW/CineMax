package com.cinemax.venta_boletos.modelos.persistencia;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cinemax.comun.ConexionBaseSingleton;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.venta_boletos.modelos.entidades.Boleto;
import com.cinemax.venta_boletos.modelos.entidades.Factura;

public class BoletoDAO {
    
    private final ConexionBaseSingleton conexionBase;

    public BoletoDAO() {
        this.conexionBase = ConexionBaseSingleton.getInstancia();
    }

    public List<Butaca> listarButacasDeBoletosPorFuncion(Funcion funcion) throws Exception {
    List<Butaca> butacas = new ArrayList<>();

    // Opción 1: Más simple - usando directamente el ID de la función
    String sql = """
        SELECT 
            bu.id AS id_butaca,
            bu.sala_id,
            bu.fila,
            bu.columna,
            bu.estado
        FROM 
            Boleto bo
        JOIN 
            butaca bu ON bo.idbutaca = bu.id
        WHERE 
            bo.idfuncion = ?
        """;

    PreparedStatement ps = null;
    ResultSet rs = null;
    Connection conn = null;
    
    try {
        conn = conexionBase.conectar();
        ps = conn.prepareStatement(sql);
        ps.setInt(1, funcion.getId()); // Asumiendo que tienes getId() en Funcion
        
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

    //public Funcion listarFuncionPorTipoDeSala(Funcion funcion, TipoSala tipoSala) throws Exception {
    //    List<Butaca> butacas = new ArrayList<>();
//
    //    // Lógica para obtener las butacas de los boletos asociados a la función
    //    String sql = """
    //        SELECT 
    //            id_funcion,
    //            id_pelicula,
    //            id_sala,
    //            fecha_hora_inicio,
    //            fecha_hora_fin,
    //            formato,
    //            tipo_estreno,
    //        FROM 
    //            funcion
    //        WHERE 
    //            id_funcion = :id_funcion AND
    //            id_pelicula = :id_pelicula AND
    //            id_sala = :id_sala AND
    //            fecha_hora_inicio = :fecha_hora_inicio AND
    //            fecha_hora_fin = :fecha_hora_fin AND
    //            formato = :formato AND
    //            tipo_estreno = :tipo_estreno
    //    """.replace(":id_funcion", String.valueOf(funcion.getId()))
    //            .replace(":id_pelicula", String.valueOf(funcion.getPelicula().getId()))
    //            .replace(":id_sala", String.valueOf(funcion.getSala().getId()))
    //            .replace(":fecha_hora_inicio", funcion.getFechaHoraInicio().toString())
    //            .replace(":fecha_hora_fin", funcion.getFechaHoraFin().toString())
    //            .replace(":formato", funcion.getFormato().name())
    //            .replace(":tipo_estreno", funcion.getTipoEstreno().name());
//
//
    //    ResultSet rs = null;
    //    Statement st = null;
    //    Connection conn = null;
    //    try {
    //        conn = conexionBase.conectar();
    //        st = conn.createStatement();
    //        rs = st.executeQuery(sql);
    //        while (rs.next()) {
    //            Butaca butaca = new Butaca();
    //            butaca.setId(rs.getInt("id"));
    //            butaca.setIdSala(rs.getInt("sala_id"));
    //            butaca.setFila(rs.getString("fila"));
    //            butaca.setColumna(rs.getString("columna"));
    //            butaca.setEstado(rs.getString("estado"));
    //            butacas.add(butaca);
    //        }
    //        return butacas;
//
    //    } catch (SQLException e) {
    //        System.err.println("Error al listar clientes: " + e.getMessage());
    //        return butacas;
    //    } finally {
    //        ConexionBaseSingleton.cerrarRecursos(rs, st);
    //        if (conn != null) conn.close();
    //    }
//
    //}

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
