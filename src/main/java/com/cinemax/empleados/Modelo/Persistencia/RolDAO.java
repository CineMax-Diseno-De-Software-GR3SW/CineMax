package com.cinemax.empleados.Modelo.Persistencia;

import com.cinemax.empleados.Comun.ConexionBaseSingleton;
import com.cinemax.empleados.Modelo.Entidades.Rol;
import com.cinemax.empleados.Modelo.Entidades.Permiso;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class RolDAO {

    private final ConexionBaseSingleton conexionBase;

    public RolDAO() {
        this.conexionBase = ConexionBaseSingleton.getInstancia();
    }

    public Rol buscarRolPorIdEmpleado(Long idEmpleado) throws Exception {
        String sql = "SELECT r.IDROL, r.NOMBRE, r.DESCRIPCION, r.ROLACTIVO " +
                     "FROM ROL r " +
                     "INNER JOIN USUARIO u ON r.IDROL = u.IDROL " +
                     "WHERE u.IDUSUARIO = " + idEmpleado;

        conexionBase.consultarBase(sql);
        ResultSet rs = conexionBase.getResultado();
        if (rs.next()) {
            Rol rol = mapearResultadoARol(rs);
            rol.setPermisos(obtenerPermisosDelRol(rol.getId()));
            return rol;
        }
        return null;
    }

    public void crearRol(Rol rol) throws Exception {
        String sql = "INSERT INTO ROL (IDROL, NOMBRE, DESCRIPCION, ROLACTIVO) " +
                "VALUES (" + rol.getId() + ", '" + rol.getNombre() + "', " +
                "'" + rol.getDescripcion() + "', " + (rol.estaActivo() ? "1" : "0") + ")";

        conexionBase.insertarModificarEliminar(sql);

        if (rol.getPermisos() != null && !rol.getPermisos().isEmpty()) {
            for (Permiso permiso : rol.getPermisos()) {
                asignarPermisoARol(rol.getId(), (long) (permiso.ordinal() + 1));
            }
        }
    }

    public void actualizarRol(Rol rol) throws Exception {
        String sql = "UPDATE ROL SET " +
                "NOMBRE = '" + rol.getNombre() + "', " +
                "DESCRIPCION = '" + rol.getDescripcion() + "', " +
                "ROLACTIVO = " + (rol.estaActivo() ? "1" : "0") + " " +
                "WHERE IDROL = " + rol.getId();

        conexionBase.insertarModificarEliminar(sql);

        eliminarPermisosDelRol(rol.getId());
        if (rol.getPermisos() != null && !rol.getPermisos().isEmpty()) {
            for (Permiso permiso : rol.getPermisos()) {
                asignarPermisoARol(rol.getId(), (long) (permiso.ordinal() + 1));
            }
        }
    }

    public Rol buscarPorId(Long id) throws Exception {
        String sql = "SELECT * FROM ROL WHERE IDROL = " + id;
        conexionBase.consultarBase(sql);

        ResultSet rs = conexionBase.getResultado();
        if (rs.next()) {
            Rol rol = mapearResultadoARol(rs);
            rol.setPermisos(obtenerPermisosDelRol(id));
            return rol;
        }
        return null;
    }

    public Rol buscarPorNombre(String nombre) throws Exception {
        String sql = "SELECT * FROM ROL WHERE NOMBRE = '" + nombre + "'";
        conexionBase.consultarBase(sql);

        ResultSet rs = conexionBase.getResultado();
        if (rs.next()) {
            Rol rol = mapearResultadoARol(rs);
            rol.setPermisos(obtenerPermisosDelRol(rol.getId()));
            return rol;
        }
        return null;
    }

    public List<Rol> listarTodos() throws Exception {
        String sql = "SELECT * FROM ROL ORDER BY NOMBRE";
        conexionBase.consultarBase(sql);

        List<Rol> roles = new ArrayList<>();
        ResultSet rs = conexionBase.getResultado();
        while (rs.next()) {
            Rol rol = mapearResultadoARol(rs);
            rol.setPermisos(obtenerPermisosDelRol(rol.getId()));
            roles.add(rol);
        }
        return roles;
    }

    public List<Rol> listarActivos() throws Exception {
        String sql = "SELECT * FROM ROL WHERE ROLACTIVO = 1 ORDER BY NOMBRE";
        conexionBase.consultarBase(sql);

        List<Rol> roles = new ArrayList<>();
        ResultSet rs = conexionBase.getResultado();
        while (rs.next()) {
            Rol rol = mapearResultadoARol(rs);
            rol.setPermisos(obtenerPermisosDelRol(rol.getId()));
            roles.add(rol);
        }
        return roles;
    }

    public void activarRol(Long id) throws Exception {
        String sql = "UPDATE ROL SET ROLACTIVO = 1 WHERE IDROL = " + id;
        conexionBase.insertarModificarEliminar(sql);
    }

    public void desactivarRol(Long id) throws Exception {
        String sql = "UPDATE ROL SET ROLACTIVO = 0 WHERE IDROL = " + id;
        conexionBase.insertarModificarEliminar(sql);
    }

    public void eliminarRol(Long id) throws Exception {
        eliminarPermisosDelRol(id);
        String sql = "DELETE FROM ROL WHERE IDROL = " + id;
        conexionBase.insertarModificarEliminar(sql);
    }

    public Long obtenerSiguienteId() throws Exception {
        String sql = "SELECT ISNULL(MAX(IDROL), 0) + 1 as SIGUIENTE_ID FROM ROL";
        conexionBase.consultarBase(sql);

        ResultSet rs = conexionBase.getResultado();
        if (rs.next()) {
            return rs.getLong("SIGUIENTE_ID");
        }
        return 1L;
    }

    public void asignarPermisoARol(Long idRol, Long idPermiso) throws Exception {
        String sql = "INSERT INTO ROL_PERMISO (IDROL, IDPERMISO) VALUES (" + idRol + ", " + idPermiso + ")";
        conexionBase.insertarModificarEliminar(sql);
    }

    public void eliminarPermisoDelRol(Long idRol, Long idPermiso) throws Exception {
        String sql = "DELETE FROM ROL_PERMISO WHERE IDROL = " + idRol + " AND IDPERMISO = " + idPermiso;
        conexionBase.insertarModificarEliminar(sql);
    }

    public void eliminarPermisosDelRol(Long idRol) throws Exception {
        String sql = "DELETE FROM ROL_PERMISO WHERE IDROL = " + idRol;
        conexionBase.insertarModificarEliminar(sql);
    }

    public Set<Permiso> obtenerPermisosDelRol(Long idRol) throws Exception {
        String sql = "SELECT p.IDPERMISO, p.NOMBREPERMISO " +
                "FROM ROL_PERMISO r2 " +
                "INNER JOIN PERMISO p ON r2.IDPERMISO = p.IDPERMISO " +
                "WHERE r2.IDROL = " + idRol;

        conexionBase.consultarBase(sql);

        Set<Permiso> permisos = new HashSet<>();
        ResultSet rs = conexionBase.getResultado();
        while (rs.next()) {
            int idPermiso = rs.getInt("IDPERMISO");
            if (idPermiso > 0 && idPermiso <= Permiso.values().length) {
                permisos.add(Permiso.values()[idPermiso - 1]);
            }
        }
        return permisos;
    }

    public boolean tienePermiso(Long idRol, Permiso permiso) throws Exception {
        String sql = "SELECT COUNT(*) as TOTAL " +
                "FROM ROL_PERMISO r2 " +
                "INNER JOIN PERMISO p ON r2.IDPERMISO = p.IDPERMISO " +
                "WHERE r2.IDROL = " + idRol + " AND p.IDPERMISO = " + (permiso.ordinal() + 1);

        conexionBase.consultarBase(sql);

        ResultSet rs = conexionBase.getResultado();
        return rs.next() && rs.getInt("TOTAL") > 0;
    }

    private Rol mapearResultadoARol(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setId(rs.getLong("IDROL"));
        rol.setNombre(rs.getString("NOMBRE"));
        rol.setDescripcion(rs.getString("DESCRIPCION"));
        rol.setActivo(rs.getBoolean("ROLACTIVO"));
        return rol;
    }

}
