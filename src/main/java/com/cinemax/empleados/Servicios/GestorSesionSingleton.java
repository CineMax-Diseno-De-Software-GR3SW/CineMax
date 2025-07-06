package com.cinemax.empleados.Servicios;

import com.cinemax.empleados.Modelo.Entidades.Permiso;
import com.cinemax.empleados.Modelo.Entidades.Usuario;
import com.cinemax.empleados.Modelo.Persistencia.UsuarioDAO;

import com.cinemax.empleados.Modelo.Entidades.Rol;

public class GestorSesionSingleton {
    private static GestorSesionSingleton gestorSesion;
    private Usuario usuarioActivo;
    private UsuarioDAO usuarioDAO;
    private ServicioRoles servicioRoles;

    private GestorSesionSingleton() {
        this.usuarioDAO = new UsuarioDAO();
        this.servicioRoles = new ServicioRoles();
    }

    public static synchronized GestorSesionSingleton getInstancia() {
        if (gestorSesion == null) {
            gestorSesion = new GestorSesionSingleton();
        }
        return gestorSesion;
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
    
    public void logout() {
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
