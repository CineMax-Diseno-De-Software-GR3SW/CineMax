package com.cinemax.empleados.servicios;

import com.cinemax.empleados.modelos.entidades.Usuario;
import com.cinemax.empleados.modelos.persistencia.UsuarioDAO;
import com.cinemax.utilidades.ManejadorMetodosComunes;

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
        if (!usuarioActivo.verificarClave(claveActual)) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("La contraseña actual es incorrecta.");
            throw new IllegalArgumentException("La contraseña actual es incorrecta.");
        }

        if (!ValidadorUsuario.validarClave(nuevaClave)) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("La nueva clave no cumple con los requisitos de seguridad.");
            throw new IllegalArgumentException("Nueva clave inválida");
        }
        String claveHasheada = ServicioClave.hashClave(nuevaClave);
        usuarioActivo.setClave(claveHasheada);
        usuarioDAO.actualizarClave(usuarioActivo.getId(), usuarioActivo.getClave());
    }

}