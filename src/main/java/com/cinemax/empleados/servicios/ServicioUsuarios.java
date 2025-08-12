package com.cinemax.empleados.servicios;

import com.cinemax.empleados.modelos.entidades.*;
import com.cinemax.empleados.modelos.persistencia.UsuarioDAO;
import java.util.List;

public class ServicioUsuarios {
    private ValidadorUsuario validador;
    private UsuarioDAO usuarioDAO;

    public ServicioUsuarios() {
        this.validador = new ValidadorUsuario();
        this.usuarioDAO = new UsuarioDAO();
    }

    public void crearUsuario(Usuario usuario) throws Exception {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }

        if (!validador.validarCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("El correo electrónico no es válido");
        }

        if (!validador.validarClave(usuario.getClave())) {
            throw new IllegalArgumentException("La clave no cumple con los requisitos de seguridad");
        }

        // Verificar que el nombre de usuario no exista
        if (usuarioDAO.buscarPorNombreUsuario(usuario.getNombreUsuario()) != null) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        // Verificar que el correo no exista
        if (usuarioDAO.buscarPorCorreo(usuario.getCorreo()) != null) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }

        // Asignar ID si no tiene
        if (usuario.getId() == null) {
            usuario.setId(usuarioDAO.obtenerSiguienteId());
        }

        usuarioDAO.crearUsuario(usuario);
    }

    public void actualizarRolUsuario(Long idUsuario, Rol nuevoRol) throws Exception {
        if (idUsuario == null || nuevoRol == null) {
            throw new IllegalArgumentException("El id del usuario y el nuevo rol no pueden ser null");
        }

        // Buscar el usuario existente
        Usuario usuarioExistente = usuarioDAO.buscarPorId(idUsuario);
        if (usuarioExistente == null) {
            throw new IllegalArgumentException("El usuario no existe");
        }

        // Actualizar solo el rol
        usuarioExistente.setRol(nuevoRol);

        // Guardar el cambio
        usuarioDAO.actualizarRol(idUsuario,nuevoRol.getId());
    }

    public Usuario buscarUsuarioPorCorreo(String correo) throws Exception {
        if (correo == null || correo.trim().isEmpty()) {
            return null;
        }

        return usuarioDAO.buscarPorCorreo(correo);
    }

    public Usuario buscarUsuarioPorNombreUsuario(String nombreUsuario) throws Exception {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            return null;
        }

        return usuarioDAO.buscarPorNombreUsuario(nombreUsuario);
    }

    public Usuario buscarUsuarioPorId(Long id) throws Exception {
        if (id == null) {
            return null;
        }

        return usuarioDAO.buscarPorId(id);
    }

    public List<Usuario> listarUsuarios() throws Exception {
        return usuarioDAO.listarTodos();
    }

    public List<Usuario> listarUsuariosActivos() throws Exception {
        return usuarioDAO.listarActivos();
    }

    public void activarUsuario(Usuario usuario) throws Exception {
        if (usuario != null) {
            usuario.activar();
            usuarioDAO.activarUsuario(usuario.getId());
        }
    }

    public void desactivarUsuario(Usuario usuario) throws Exception {
        if (usuario != null) {
            usuario.desactivar();
            usuarioDAO.desactivarUsuario(usuario.getId());
        }
    }

    public void eliminarUsuario(Long id) throws Exception {
        if (id != null) {
            usuarioDAO.eliminarUsuario(id);
        }
    }

    public void actualizarEstado(Long idUsuario, Boolean nuevoEstado) throws Exception {
        usuarioDAO.cambiarEstado(idUsuario,nuevoEstado);
    }

}

//
//package com.cinemax.empleados.servicios;
//
//import com.cinemax.empleados.modelo.entidades.*;
//import com.cinemax.empleados.modelo.persistencia.UsuarioDAO;
//
//import java.util.List;
//
//public class ServicioUsuarios {
//    private ValidadorUsuario validador;
//    private UsuarioDAO usuarioDAO;
//
//    public ServicioUsuarios() {
//        this.validador = new ValidadorUsuario();
//        this.usuarioDAO = new UsuarioDAO();
//    }
//
//    // --- Método principal para guardar el usuario en la BD (con validación completa) ---
//    public void crearUsuario(Usuario usuario) throws Exception {
//        if (usuario == null) {
//            throw new IllegalArgumentException("El usuario no puede ser null");
//        }
//
//        if (!validador.validarCorreo(usuario.getCorreo())) {
//            throw new IllegalArgumentException("El correo electrónico no es válido");
//        }
//
//        if (!validador.validarClave(usuario.getClave())) {
//            throw new IllegalArgumentException("La clave no cumple con los requisitos de seguridad");
//        }
//
//        if (usuarioDAO.buscarPorNombreUsuario(usuario.getNombreUsuario()) != null) {
//            throw new IllegalArgumentException("El nombre de usuario ya existe");
//        }
//
//        if (usuarioDAO.buscarPorCorreo(usuario.getCorreo()) != null) {
//            throw new IllegalArgumentException("El correo electrónico ya está registrado");
//        }
//
//        if (usuario.getId() == null) {
//            usuario.setId(usuarioDAO.obtenerSiguienteId());
//        }
//
//        usuarioDAO.crearUsuario(usuario);
//    }
//
//    // --- Nuevo método para crear usuario con contraseña temporal ---
//    public Usuario crearUsuario(String nombreUsuario, String correo, String nombreCompleto,
//                                String cedula, String celular, Rol rol) {
//
//        // 1. Generar contraseña temporal automáticamente
//        String claveTemporal = generarContrasenaTemporal(nombreUsuario, cedula);
//
//        // 2. Crear instancia del usuario con clave generada
//        Usuario nuevo = new Usuario(nombreUsuario, correo, claveTemporal, nombreCompleto,
//                cedula, celular, rol);
//
//        // 3. Mostrar la clave temporal en consola (puedes ajustarlo para UI o envío por correo)
//        System.out.println("Contraseña temporal para " + nombreUsuario + ": " + claveTemporal);
//
//        return nuevo;
//    }
//
//    // --- Utilidad interna para generar clave temporal básica ---
//    private String generarContrasenaTemporal(String nombreUsuario, String cedula) {
//        String randomPart = Long.toHexString(Double.doubleToLongBits(Math.random()));
//        return nombreUsuario.substring(0, 3).toLowerCase() +
//                cedula.substring(Math.max(0, cedula.length() - 3)) +
//                randomPart.substring(0, 4);
//    }
//
//    // --- Métodos CRUD auxiliares ---
//    public void actualizarRolUsuario(Long idUsuario, Rol nuevoRol) throws Exception {
//        if (idUsuario == null || nuevoRol == null) {
//            throw new IllegalArgumentException("El id del usuario y el nuevo rol no pueden ser null");
//        }
//
//        Usuario usuarioExistente = usuarioDAO.buscarPorId(idUsuario);
//        if (usuarioExistente == null) {
//            throw new IllegalArgumentException("El usuario no existe");
//        }
//
//        usuarioExistente.setRol(nuevoRol);
//        usuarioDAO.actualizarRol(idUsuario, nuevoRol.getId());
//    }
//
//    public Usuario buscarUsuarioPorCorreo(String correo) throws Exception {
//        if (correo == null || correo.trim().isEmpty()) {
//            return null;
//        }
//        return usuarioDAO.buscarPorCorreo(correo);
//    }
//
//    public Usuario buscarUsuarioPorNombreUsuario(String nombreUsuario) throws Exception {
//        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
//            return null;
//        }
//        return usuarioDAO.buscarPorNombreUsuario(nombreUsuario);
//    }
//
//    public Usuario buscarUsuarioPorId(Long id) throws Exception {
//        if (id == null) {
//            return null;
//        }
//        return usuarioDAO.buscarPorId(id);
//    }
//
//    public List<Usuario> listarUsuarios() throws Exception {
//        return usuarioDAO.listarTodos();
//    }
//
//    public List<Usuario> listarUsuariosActivos() throws Exception {
//        return usuarioDAO.listarActivos();
//    }
//
//    public void activarUsuario(Usuario usuario) throws Exception {
//        if (usuario != null) {
//            usuario.activar();
//            usuarioDAO.activarUsuario(usuario.getId());
//        }
//    }
//
//    public void desactivarUsuario(Usuario usuario) throws Exception {
//        if (usuario != null) {
//            usuario.desactivar();
//            usuarioDAO.desactivarUsuario(usuario.getId());
//        }
//    }
//
//    public void eliminarUsuario(Long id) throws Exception {
//        if (id != null) {
//            usuarioDAO.eliminarUsuario(id);
//        }
//    }
//
//    public void actualizarEstado(Long idUsuario, Boolean nuevoEstado) throws Exception {
//        usuarioDAO.cambiarEstado(idUsuario, nuevoEstado);
//    }
//}
