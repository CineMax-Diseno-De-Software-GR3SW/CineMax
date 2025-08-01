package com.cinemax.venta_boletos.Modelos.Persistencia;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.cinemax.comun.ConexionBaseSingleton;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.TipoSala;
import com.cinemax.venta_boletos.Modelos.Cliente;

public class BoletoDAO {
    
    private final ConexionBaseSingleton conexionBase;

    public BoletoDAO() {
        this.conexionBase = ConexionBaseSingleton.getInstancia();
    }

    public List<Butaca> listarButacasDeBoletosPorFuncion(Funcion funcion) throws Exception {
        List<Butaca> butacas = new ArrayList<>();

        // Lógica para obtener las butacas de los boletos asociados a la función
        String sql = """
            SELECT 
                bu.id AS id_butaca,
                bu.fila,
                bu.columna,
                bu.estado
            FROM 
                Boleto bo
            JOIN 
                funcion f ON bo.idfuncion = f.id_funcion
            JOIN 
                butaca bu ON bo.idbutaca = bu.id
            WHERE 
                f.id_pelicula = :id_pelicula AND
                f.id_sala = :id_sala AND
                f.fecha_hora_inicio::date = :fecha AND
                f.fecha_hora_inicio::time = :hora;
        """;


        ResultSet rs = null;
        Statement st = null;
        Connection conn = null;
        try {
            conn = conexionBase.conectar();
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                Butaca butaca = new Butaca();
                butaca.setId(rs.getInt("id"));
                butaca.setIdSala(rs.getInt("sala_id"));
                butaca.setFila(rs.getString("fila"));
                butaca.setColumna(rs.getString("columna"));
                butaca.setEstado(rs.getString("estado"));
                butacas.add(butaca);
            }
            return butacas;

        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
            return butacas;
        } finally {
            ConexionBaseSingleton.cerrarRecursos(rs, st);
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
 }
