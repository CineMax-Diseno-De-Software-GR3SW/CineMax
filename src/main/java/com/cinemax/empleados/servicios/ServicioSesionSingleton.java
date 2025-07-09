package com.cinemax.empleados.servicios;

import com.cinemax.empleados.modelo.entidades.Permiso;
import com.cinemax.empleados.modelo.entidades.Usuario;
import com.cinemax.empleados.modelo.persistencia.UsuarioDAO;

import com.cinemax.empleados.modelo.entidades.Rol;

public class ServicioSesionSingleton {
    private static ServicioSesionSingleton servicioSesion;
    private Usuario usuarioActivo;
    private UsuarioDAO usuarioDAO;
    private ServicioRoles servicioRoles;

    private ServicioSesionSingleton() {
        this.usuarioDAO = new UsuarioDAO();
        this.servicioRoles = new ServicioRoles();
    }

    public static synchronized ServicioSesionSingleton getInstancia() {
        if (servicioSesion == null) {
            servicioSesion = new ServicioSesionSingleton();
        }
        return servicioSesion;
    }

    
    public boolean iniciarSesion(String nombreUsuario, String clave) throws Exception {
        if (nombreUsuario == null || clave == null || 
            nombreUsuario.trim().isEmpty() || clave.trim().isEmpty()) {
            return false;
        }
        
        // Buscar usuario por nombre de usuario
        Usuario usuario = usuarioDAO.buscarPorNombreUsuario(nombreUsuario);
        
        if (usuario != null && usuario.isActivo() && usuario.verificarClave(clave)) {
            // // Cargar rol completo con permisos
            Rol rolCompleto = servicioRoles.obtenerRolPorIdUsuario(usuario.getId());
            usuario.setRol(rolCompleto);

            this.usuarioActivo = usuario;
            
            return true;
        }
        
        return false;
    }
    
    public void cerrarSesion() {
        this.usuarioActivo = null;
    }
    
    public Usuario getUsuarioActivo() {
        return usuarioActivo;
    }
    
    public boolean estaAutenticado() {
        return usuarioActivo != null;
    }
    
//    public void setUsuarioActivo(Usuario usuario) {
//        this.usuarioActivo = usuario;
//    }
    
    /**
     * Verifica si el usuario activo tiene un permiso específico
     */
    public boolean tienePermiso(Permiso permiso) {
        if (usuarioActivo == null || usuarioActivo.getRol() == null) {
            return false;
        }
        return usuarioActivo.getRol().tienePermiso(permiso);
    }


    /**
     * Verifica si el usuario activo está activo
     */
    public boolean usuarioActivoEstaActivo() {
        return usuarioActivo != null && usuarioActivo.isActivo();
    }

    public String getNombreUsuario() {
        return usuarioActivo.getNombreCompleto();
   }

    public String getRolUsuarioActivo() {
        return usuarioActivo.getDescripcionRol();
    }


}
