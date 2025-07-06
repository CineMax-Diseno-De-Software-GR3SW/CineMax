package com.cinemax.empleados.Servicios;

import com.cinemax.empleados.Modelo.Entidades.Rol;
import com.cinemax.empleados.Modelo.Entidades.Permiso;
import com.cinemax.empleados.Modelo.Persistencia.RolDAO;
import java.util.List;
import java.util.Set;

public class ServicioRoles {
    private RolDAO rolDAO;
    
    public ServicioRoles() {
        this.rolDAO = new RolDAO();
    }
    
    /**
     * Crea un nuevo rol
     */
    public void crearRol(Rol rol) throws Exception {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser null");
        }
        
        if (rol.getNombre() == null || rol.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede estar vacío");
        }
        
        // Verificar que el nombre del rol no exista
        if (rolDAO.buscarPorNombre(rol.getNombre()) != null) {
            throw new IllegalArgumentException("Ya existe un rol con ese nombre");
        }
        
        // Asignar ID si no tiene
        if (rol.getId() == null) {
            rol.setId(rolDAO.obtenerSiguienteId());
        }
        
        rolDAO.crearRol(rol);
    }
    
    /**
     * Actualiza un rol existente
     */
    public void actualizarRol(Rol rol) throws Exception {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser null");
        }
        
        // Verificar que el rol existe
        Rol rolExistente = rolDAO.buscarPorId(rol.getId());
        if (rolExistente == null) {
            throw new IllegalArgumentException("El rol no existe");
        }
        
        // Verificar que el nombre no esté en uso por otro rol
        Rol rolConNombre = rolDAO.buscarPorNombre(rol.getNombre());
        if (rolConNombre != null && !rolConNombre.getId().equals(rol.getId())) {
            throw new IllegalArgumentException("Ya existe otro rol con ese nombre");
        }
        
        rolDAO.actualizarRol(rol);
    }
    
    /**
     * Busca un rol por su ID
     */
    public Rol obtenerRolPorIdUsuario(Long idUsuario) {
        if (idUsuario == null) {
            return null;
        }
        
        Rol rol=null;
        try {
            rol = rolDAO.buscarRolPorIdEmpleado(idUsuario);
            rol.setPermisos(rolDAO.obtenerPermisosDelRol(rol.getId()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rol;
    }
    
    /**
     * Busca un rol por su nombre
     */
    public Rol buscarRolPorNombre(String nombre) throws Exception {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }
        
        return rolDAO.buscarPorNombre(nombre);
    }
    
    /**
     * Lista todos los roles
     */
    public List<Rol> listarRoles() throws Exception {
        return rolDAO.listarTodos();
    }
    
    /**
     * Lista roles activos
     */
    public List<Rol> listarRolesActivos() throws Exception {
        return rolDAO.listarActivos();
    }
    
    /**
     * Activa un rol
     */
    public void activarRol(Long id) throws Exception {
        if (id != null) {
            rolDAO.activarRol(id);
        }
    }
    
    /**
     * Desactiva un rol
     */
    public void desactivarRol(Long id) throws Exception {
        if (id != null) {
            rolDAO.desactivarRol(id);
        }
    }
    
    /**
     * Elimina un rol
     */
    public void eliminarRol(Long id) throws Exception {
        if (id != null) {
            rolDAO.eliminarRol(id);
        }
    }
    
    /**
     * Asigna un permiso a un rol
     */
    public void asignarPermisoARol(Long idRol, Permiso permiso) throws Exception {
        if (idRol == null || permiso == null) {
            throw new IllegalArgumentException("El ID del rol y el permiso no pueden ser null");
        }
        
        rolDAO.asignarPermisoARol(idRol, (long)(permiso.ordinal() + 1));
    }
    
    /**
     * Elimina un permiso de un rol
     */
    public void eliminarPermisoDelRol(Long idRol, Permiso permiso) throws Exception {
        if (idRol == null || permiso == null) {
            throw new IllegalArgumentException("El ID del rol y el permiso no pueden ser null");
        }
        
        rolDAO.eliminarPermisoDelRol(idRol, (long)(permiso.ordinal() + 1));
    }
    
    /**
     * Obtiene todos los permisos de un rol
     */
    public Set<Permiso> obtenerPermisosDelRol(Long idRol) throws Exception {
        if (idRol == null) {
            return null;
        }
        
        return rolDAO.obtenerPermisosDelRol(idRol);
    }
    
    /**
     * Verifica si un rol tiene un permiso específico
     */
    public boolean rolTienePermiso(Long idRol, Permiso permiso) throws Exception {
        if (idRol == null || permiso == null) {
            return false;
        }
        
        return rolDAO.tienePermiso(idRol, permiso);
    }
    
    /**
     * Obtiene roles por permisos específicos
     */
    public List<Rol> obtenerRolesConPermiso(Permiso permiso) throws Exception {
        List<Rol> todosLosRoles = listarRoles();
        List<Rol> rolesConPermiso = new java.util.ArrayList<>();
        
        for (Rol rol : todosLosRoles) {
            if (rolTienePermiso(rol.getId(), permiso)) {
                rolesConPermiso.add(rol);
            }
        }
        
        return rolesConPermiso;
    }


}