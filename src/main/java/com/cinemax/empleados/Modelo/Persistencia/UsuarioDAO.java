package com.cinemax.empleados.Modelo.Persistencia;

import com.cinemax.empleados.Comun.ConexionBaseSingleton;
import com.cinemax.empleados.Modelo.Entidades.Usuario;
import com.cinemax.empleados.Modelo.Entidades.Rol;

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
            INSERT INTO USUARIO (IDUSUARIO, IDROL, NOMBREUSUARIO, CORREO, CLAVE,
                                 NOMBRECOMPLETO, CEDULA, CELULAR, ACTIVO,
                                 FECHACREACION, FECHAULTIMAMODIFICACION)
            VALUES (%d, %d, '%s', '%s', '%s', '%s', '%s', '%s', %d, '%s', '%s')
            """.formatted(
                u.getId(), u.getRol().getId(), u.getNombreUsuario(), u.getCorreo(),
                u.getClave(), u.getNombreCompleto(), u.getCedula(), u.getCelular(),
                u.isActivo() ? 1 : 0,
                u.getFechaCreacion().format(formatter),
                u.getFechaUltimaModificacion().format(formatter)
        );

        db.insertarModificarEliminar(sql);
    }

    public void actualizarUsuario(Usuario u) throws Exception {
        String sql = """
            UPDATE USUARIO SET
                IDROL = %d,
                NOMBREUSUARIO = '%s',
                CORREO = '%s',
                CLAVE = '%s',
                NOMBRECOMPLETO = '%s',
                CEDULA = '%s',
                CELULAR = '%s',
                ACTIVO = %d,
                FECHAULTIMAMODIFICACION = '%s'
            WHERE IDUSUARIO = %d
            """.formatted(
                u.getRol().getId(), u.getNombreUsuario(), u.getCorreo(), u.getClave(),
                u.getNombreCompleto(), u.getCedula(), u.getCelular(),
                u.isActivo() ? 1 : 0,
                LocalDateTime.now().format(formatter),
                u.getId()
        );

        db.insertarModificarEliminar(sql);
    }

    /* ======================= BÚSQUEDAS ======================= */

    public Usuario buscarPorId(Long id) throws Exception {
        String sql = """
            SELECT u.*, r.IDROL, r.NOMBRE AS NOMBRE_ROL, r.DESCRIPCION, r.ROLACTIVO
            FROM USUARIO u
            INNER JOIN ROL r ON u.IDROL = r.IDROL
            WHERE u.IDUSUARIO = %d
            """.formatted(id);

        db.consultarBase(sql);
        ResultSet rs = db.getResultado();
        return rs.next() ? mapear(rs) : null;
    }

    public Usuario buscarPorNombreUsuario(String nombre) throws Exception {
        String sql = """
            SELECT u.*, r.IDROL, r.NOMBRE AS NOMBRE_ROL, r.DESCRIPCION, r.ROLACTIVO
            FROM USUARIO u
            INNER JOIN ROL r ON u.IDROL = r.IDROL
            WHERE u.NOMBREUSUARIO = '%s'
            """.formatted(nombre);

        db.consultarBase(sql);
        ResultSet rs = db.getResultado();
        return rs.next() ? mapear(rs) : null;
    }

    public Usuario buscarPorCorreo(String correo) throws Exception {
        String sql = """
            SELECT u.*, r.IDROL, r.NOMBRE AS NOMBRE_ROL, r.DESCRIPCION, r.ROLACTIVO
            FROM USUARIO u
            INNER JOIN ROL r ON u.IDROL = r.IDROL
            WHERE u.CORREO = '%s'
            """.formatted(correo);

        db.consultarBase(sql);
        ResultSet rs = db.getResultado();
        return rs.next() ? mapear(rs) : null;
    }

    /* ======================= LISTADOS ======================= */

    public List<Usuario> listarTodos() throws Exception {
        String sql = """
            SELECT u.*, r.IDROL, r.NOMBRE AS NOMBRE_ROL, r.DESCRIPCION, r.ROLACTIVO
            FROM USUARIO u
            INNER JOIN ROL r ON u.IDROL = r.IDROL
            ORDER BY u.NOMBREUSUARIO
            """;

        db.consultarBase(sql);
        return mapearLista(db.getResultado());
    }

    public List<Usuario> listarActivos() throws Exception {
        String sql = """
            SELECT u.*, r.IDROL, r.NOMBRE AS NOMBRE_ROL, r.DESCRIPCION, r.ROLACTIVO
            FROM USUARIO u
            INNER JOIN ROL r ON u.IDROL = r.IDROL
            WHERE u.ACTIVO = 1
            ORDER BY u.NOMBREUSUARIO
            """;

        db.consultarBase(sql);
        return mapearLista(db.getResultado());
    }

    /* ======================= ESTADO ======================= */

    public void activarUsuario(Long id) throws Exception {
        cambiarEstado(id, true);
    }

    public void desactivarUsuario(Long id) throws Exception {
        cambiarEstado(id, false);
    }

    public void eliminarUsuario(Long id) throws Exception {
        String sql = "DELETE FROM USUARIO WHERE IDUSUARIO = " + id;
        db.insertarModificarEliminar(sql);
    }

    /* ======================= UTIL ======================= */

    public Long obtenerSiguienteId() throws Exception {
        String sql = "SELECT ISNULL(MAX(IDUSUARIO),0)+1 AS SIGUIENTE_ID FROM USUARIO";
        db.consultarBase(sql);

        ResultSet rs = db.getResultado();
        return rs.next() ? rs.getLong("SIGUIENTE_ID") : 1L;
    }

    /* ===================================================== */

    public void cambiarEstado(Long id, boolean activo) throws Exception {
        String sql = """
            UPDATE USUARIO SET ACTIVO = %d,
                FECHAULTIMAMODIFICACION = '%s'
            WHERE IDUSUARIO = %d
            """.formatted(
                activo ? 1 : 0,
                LocalDateTime.now().format(formatter),
                id
        );
        db.insertarModificarEliminar(sql);
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

    public void actualizarEstado(Long idUsuario, Boolean nuevoEstado) {

    }

    public void actualizarRol(Long idUsuario, Long idRol) throws Exception {
        String sql = """
            UPDATE USUARIO SET IDROL = %d,
                FECHAULTIMAMODIFICACION = '%s'
            WHERE IDUSUARIO = %d
            """.formatted(
                idRol,
                LocalDateTime.now().format(formatter),
                idUsuario
        );
        db.insertarModificarEliminar(sql);
    }
}
