package com.cinemax.empleados.modelos.persistencia;

import com.cinemax.comun.ConexionBaseSingleton;
import com.cinemax.empleados.modelos.entidades.Usuario;
import com.cinemax.empleados.modelos.entidades.Rol;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private final ConexionBaseSingleton db;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public UsuarioDAO() {
        this.db = ConexionBaseSingleton.getInstancia();
    }

    /* ======================= CRUD ======================= */

    public void crearUsuario(Usuario u) throws Exception {
        String sql = """
        CALL crear_usuario(%d, %d, '%s', '%s', '%s', '%s', '%s', '%s', %b, '%s', '%s')
        """.formatted(
                u.getId(), u.getRol().getId(), u.getNombreUsuario(), u.getCorreo(),
                u.getClave(), u.getNombreCompleto(), u.getCedula(), u.getCelular(),
                u.isActivo(),
                u.getFechaCreacion().format(formatter),
                u.getFechaUltimaModificacion().format(formatter)
        );

        db.ejecutarActualizacion(sql);
//        db.insertarModificarEliminar(sql);
    }

    public void actualizarUsuario(Usuario u) throws Exception {
        String sql = """
                CALL actualizar_usuario(%d, %d, '%s', '%s', '%s', '%s', '%s', '%s', %b, '%s')
                """.formatted(
                u.getId(),
                u.getRol().getId(),
                u.getNombreUsuario(),
                u.getCorreo(),
                u.getClave(),
                u.getNombreCompleto(),
                u.getCedula(),
                u.getCelular(),
                u.isActivo(),
                LocalDateTime.now().format(formatter)
        );

        db.ejecutarActualizacion(sql);

//        db.insertarModificarEliminar(sql);
    }

    /* ======================= BÚSQUEDAS ======================= */

    public Usuario buscarPorId(Long id) throws Exception {
        String sql = "SELECT * FROM buscar_usuario_por_id(" + id + ")";
        // SELECT
        ResultSet rs = null;
        Statement st  = null;
        try {
            rs = db.ejecutarConsulta(sql);
            return rs.next() ? mapear(rs) : null;
        } finally {
            ConexionBaseSingleton.cerrarRecursos(rs, st);  // Libera recursos
        }

//        db.consultarBase(sql);
//        ResultSet rs = db.getResultado();
//        return rs.next() ? mapear(rs) : null;
    }

    public Usuario buscarPorNombreUsuario(String nombre) throws Exception {
        String sql = "SELECT * FROM buscar_usuario_por_nombre_usuario('" + nombre + "')";

        // SELECT
        ResultSet rs = null;
        Statement st  = null;
        try {
            rs = db.ejecutarConsulta(sql);
            return rs.next() ? mapear(rs) : null;
        } finally {
            ConexionBaseSingleton.cerrarRecursos(rs, st);  // Libera recursos
        }

//        db.consultarBase(sql);
//        ResultSet rs = db.getResultado();
//        return rs.next() ? mapear(rs) : null;
    }

    public Usuario buscarPorCorreo(String correo) throws Exception {
        String sql = "SELECT * FROM buscar_usuario_por_correo('" + correo + "')";

        // SELECT
        ResultSet rs = null;
        Statement st  = null;
        try {
            rs = db.ejecutarConsulta(sql);
            return rs.next() ? mapear(rs) : null;
        } finally {
            ConexionBaseSingleton.cerrarRecursos(rs, st);  // Libera recursos
        }

//        db.consultarBase(sql);
//        ResultSet rs = db.getResultado();
//        return rs.next() ? mapear(rs) : null;
    }

    /* ======================= LISTADOS ======================= */

    public List<Usuario> listarTodos() throws Exception {
        String sql = "SELECT * FROM listar_usuarios()";

        // SELECT
        ResultSet rs = null;
        Statement st  = null;
        try {
            rs = db.ejecutarConsulta(sql);
            return mapearLista(rs);
        } finally {
            ConexionBaseSingleton.cerrarRecursos(rs, st);  // Libera recursos
        }
//        db.consultarBase(sql);
//        return mapearLista(db.getResultado());
    }
//
//    public List<Usuario> listarActivos() throws Exception {
//        String sql = """
//            SELECT u.*, r.IDROL, r.NOMBRE AS NOMBRE_ROL, r.DESCRIPCION, r.ROLACTIVO
//            FROM USUARIO u
//            INNER JOIN ROL r ON u.IDROL = r.IDROL
//            WHERE u.ACTIVO = 1
//            ORDER BY u.NOMBREUSUARIO
//            """;
//
//        // SELECT
//        ResultSet rs = null;
//        Statement st  = null;
//        try {
//            rs = db.ejecutarConsulta(sql);
//            return mapearLista(rs);
//        } finally {
//            ConexionBaseSingleton.cerrarRecursos(rs, st);  // Libera recursos
//        }
//
////        db.consultarBase(sql);
////        return mapearLista(db.getResultado());
//    }

    /* ======================= ESTADO ======================= */

//    public void activarUsuario(Long id) throws Exception {
//        cambiarEstado(id, true);
//    }
//
//    public void desactivarUsuario(Long id) throws Exception {
//        cambiarEstado(id, false);
//    }

    public void eliminarUsuario(Long id) throws Exception {
        String sql = "CALL eliminar_usuario(" + id + ")";
        db.ejecutarActualizacion(sql);

//        db.insertarModificarEliminar(sql);
    }

    /* ======================= UTIL ======================= */

    public Long obtenerSiguienteId() throws Exception {
        String sql = "SELECT obtener_siguiente_id_usuario() AS SIGUIENTE_ID";

        // SELECT
        ResultSet rs = null;
        Statement st  = null;
        try {
            rs = db.ejecutarConsulta(sql);
            return rs.next() ? rs.getLong("SIGUIENTE_ID") : 1L;
        } finally {
            ConexionBaseSingleton.cerrarRecursos(rs, st);  // Libera recursos
        }

//        db.consultarBase(sql);
//        ResultSet rs = db.getResultado();
//        return rs.next() ? rs.getLong("SIGUIENTE_ID") : 1L;
    }

    /* ===================================================== */

    public void cambiarEstado(Long id, boolean activo) throws Exception {
        String sql = """
        CALL cambiar_estado_usuario(%d, %b, '%s')
        """.formatted(
                id,
                activo,
                LocalDateTime.now().format(formatter)
        );
        db.ejecutarActualizacion(sql);

//        db.insertarModificarEliminar(sql);
    }

    private List<Usuario> mapearLista(ResultSet rs) throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        while (rs.next()) lista.add(mapear(rs));
        return lista;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("IDUSUARIO"));
        u.setNombreUsuario(rs.getString("NOMBREUSUARIO"));
        u.setCorreo(rs.getString("CORREO"));
        u.setClave(rs.getString("CLAVE"));
        u.setNombreCompleto(rs.getString("NOMBRECOMPLETO"));
        u.setCedula(rs.getString("CEDULA"));
        u.setCelular(rs.getString("CELULAR"));
        u.setActivo(rs.getBoolean("ACTIVO"));

        Timestamp fc = rs.getTimestamp("FECHACREACION");
        if (fc != null) u.setFechaCreacion(fc.toLocalDateTime());

        Timestamp fm = rs.getTimestamp("FECHAULTIMAMODIFICACION");
        if (fm != null) u.setFechaUltimaModificacion(fm.toLocalDateTime());

        Rol rol = new Rol();
        rol.setId(rs.getLong("IDROL"));
        rol.setNombre(rs.getString("NOMBRE_ROL"));
        rol.setDescripcion(rs.getString("DESCRIPCION"));
        rol.setActivo(rs.getBoolean("ROLACTIVO"));
        u.setRol(rol);

        return u;
    }

//    public void actualizarEstado(Long idUsuario, Boolean nuevoEstado) {
//
//    }

    public void actualizarRol(Long idUsuario, Long idRol) throws Exception {
        String sql = """
        CALL actualizar_rol_usuario(%d, %d, '%s')
        """.formatted(
                idUsuario,
                idRol,
                LocalDateTime.now().format(formatter)
        );
        db.ejecutarActualizacion(sql);

//        db.insertarModificarEliminar(sql);
    }

    public void actualizarCorreo(Long idUsuario, String nuevoEmail) throws SQLException {
        String sql = """
                CALL actualizar_correo_usuario(%d,'%s')
                """.formatted(
                idUsuario,
                nuevoEmail
        );
        db.ejecutarActualizacion(sql);

//        db.insertarModificarEliminar(sql);
    }

    public void actualizarCelular(Long idUsuario, String nuevoCelular) throws SQLException {
        String sql = """
                CALL actualizar_celular_usuario(%d,'%s')
                """.formatted(
                idUsuario,
                nuevoCelular
        );
        db.ejecutarActualizacion(sql);

//        db.insertarModificarEliminar(sql);
    }

    public void actualizarClave(Long id, String nuevaClave) {
        String sql = """
                CALL actualizar_clave_usuario(%d,'%s')
                """.formatted(
                id,
                nuevaClave
        );
        try {
            db.ejecutarActualizacion(sql);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar la clave del usuario: " + e.getMessage());
        }
    }
}
