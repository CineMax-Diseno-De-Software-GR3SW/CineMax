package com.cinemax.empleados.servicios;

import com.cinemax.empleados.modelos.entidades.Usuario;
import com.cinemax.empleados.modelos.persistencia.UsuarioDAO;

import java.sql.SQLException;

public class ServicioPerfilUsuario {
    private UsuarioDAO usuarioDAO;
    
    public ServicioPerfilUsuario() {
        this.usuarioDAO = new UsuarioDAO();
    }
    
    public void actualizarPerfil(Usuario usuario, String nuevoCorreo, String nuevoCelular) throws Exception {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }
        
        if (nuevoCorreo != null && !ValidadorUsuario.validarCorreo(nuevoCorreo)) {
            throw new IllegalArgumentException("El nuevo correo electrónico no es válido");
        }
        
        // Verificar que el nuevo correo no esté en uso por otro usuario
        if (nuevoCorreo != null && !nuevoCorreo.equals(usuario.getCorreo())) {
            Usuario usuarioConCorreo = usuarioDAO.buscarPorCorreo(nuevoCorreo);
            if (usuarioConCorreo != null && !usuarioConCorreo.getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("El correo electrónico ya está en uso por otro usuario");
            }
        }
        
        usuario.actualizarContacto(nuevoCorreo, nuevoCelular);
        usuarioDAO.actualizarUsuario(usuario);
    }
    
    public boolean cambiarClave(Usuario usuario, String claveActual, String nuevaClave) throws Exception {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }
        
        if (!usuario.verificarClave(claveActual)) {
            return false; // Clave actual incorrecta
        }
        
        if (!ValidadorUsuario.validarClave(nuevaClave)) {
            throw new IllegalArgumentException("La nueva clave no cumple con los requisitos de seguridad");
        }
        
        usuario.setClave(nuevaClave);
        usuarioDAO.actualizarUsuario(usuario);
        return true;
    }
    
    /**
     * Obtiene el perfil completo de un usuario por su ID
     */
    public Usuario obtenerPerfil(Long idUsuario) throws Exception {
        if (idUsuario == null) {
            return null;
        }
        
        return usuarioDAO.buscarPorId(idUsuario);
    }
    
    /**
     * Verifica si un usuario puede actualizar su perfil
     */
    public boolean puedeActualizarPerfil(Usuario usuarioActual, Long idUsuarioPerfil) {
        // Un usuario solo puede actualizar su propio perfil
        return usuarioActual != null && usuarioActual.getId().equals(idUsuarioPerfil);
    }

    public void actualizarCorreo(Usuario usuarioActivo, String nuevoEmail) throws SQLException {
        usuarioActivo.actualizarCorreo(nuevoEmail);
        usuarioDAO.actualizarCorreo(usuarioActivo.getId(),nuevoEmail);
    }

    public void actualizarCelular(Usuario usuarioActivo, String nuevoCelular) throws SQLException {
        usuarioActivo.actualizarCelular(nuevoCelular);
        usuarioDAO.actualizarCelular(usuarioActivo.getId(), nuevoCelular);
    }

    public void actualizarClave(Usuario usuarioActivo, String claveActual,String nuevaClave) throws SQLException {
        if (!usuarioActivo.getClave().equals(claveActual)) {
            throw new SQLException("La contraseña actual es incorrecta.");
        }

        usuarioActivo.actualizarClave(nuevaClave);
        usuarioDAO.actualizarClave(usuarioActivo.getId(), nuevaClave);
    }

}