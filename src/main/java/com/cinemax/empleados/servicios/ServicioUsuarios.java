package com.cinemax.empleados.servicios;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.comun.ServicioCorreoSingleton;
import com.cinemax.empleados.modelos.entidades.*;
import com.cinemax.empleados.modelos.persistencia.UsuarioDAO;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioUsuarios {
    private ValidadorUsuario validador;
    private UsuarioDAO usuarioDAO;

    public ServicioUsuarios() {
        this.validador = new ValidadorUsuario();
        this.usuarioDAO = new UsuarioDAO();
    }

    //TODO: Pasar atributos para crear el usuario

    public void crearUsuario(String nombreCompleto, String cedula, String correo, String celular, boolean estadoActivo, String nombreUsuario, Rol cargoSeleccionado) throws Exception {

        validarDatos(correo, nombreUsuario);
        String clave = generarClaveAleatoria();
        String claveHasheada = UtilidadClave.hashClave(clave);

        Usuario usuario = new Usuario(nombreUsuario,correo,claveHasheada,nombreCompleto,
                cedula,celular,cargoSeleccionado, estadoActivo, true);
        // Asignar ID si no tiene
        if (usuario.getId() == null) {
            usuario.setId(usuarioDAO.obtenerSiguienteId());
        }
        //TODO: generacion de contrasena (aleatoria con letras numeros y caracteres especiales)

        //TODO: Creacion del usuario

        usuarioDAO.crearUsuario(usuario);

        // Servicio de correo
        ServicioCorreoSingleton.getInstancia().enviarCorreo(usuario.getCorreo(), ContenidoMensaje.crearMensajeCreacionUsuario(usuario.getNombreCompleto(), usuario.getNombreUsuario(), clave));

    }

    private String generarClaveAleatoria() {
        final String MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";
        final String NUMEROS = "0123456789";
        final String ESPECIALES = "!@#$%&*";
        final String TODOS = MAYUSCULAS + MINUSCULAS + NUMEROS + ESPECIALES;
        final int LONGITUD = 12;

        SecureRandom random = new SecureRandom();
        StringBuilder clave = new StringBuilder();

        // Garantiza al menos un carácter de cada tipo
        clave.append(MAYUSCULAS.charAt(random.nextInt(MAYUSCULAS.length())));
        clave.append(MINUSCULAS.charAt(random.nextInt(MINUSCULAS.length())));
        clave.append(NUMEROS.charAt(random.nextInt(NUMEROS.length())));
        clave.append(ESPECIALES.charAt(random.nextInt(ESPECIALES.length())));

        // Completa el resto aleatoriamente
        for (int i = 4; i < LONGITUD; i++) {
            clave.append(TODOS.charAt(random.nextInt(TODOS.length())));
        }

        // Mezcla los caracteres para que no sigan un patrón predecible
        List<Character> caracteres = clave.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(caracteres, random);

        StringBuilder claveFinal = new StringBuilder();
        caracteres.forEach(claveFinal::append);

        return claveFinal.toString();
    }


    private void validarDatos(String correo, String nombreUsuario) throws Exception {
        if (!validador.validarCorreo(correo)) {
            throw new IllegalArgumentException("El correo electrónico no es válido");
        }


        // Verificar que el nombre de usuario no exista
        if (usuarioDAO.buscarPorNombreUsuario(nombreUsuario) != null) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        // Verificar que el correo no exista
        if (usuarioDAO.buscarPorCorreo(correo) != null) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }
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

//    public List<Usuario> listarUsuariosActivos() throws Exception {
//        return usuarioDAO.listarActivos();
//    }

//    public void activarUsuario(Usuario usuario) throws Exception {
//        if (usuario != null) {
//            usuario.activar();
//            usuarioDAO.cambiarEstado(usuario.getId(), true);
////            usuarioDAO.activarUsuario(usuario.getId());
//        }
//    }
//
//    public void desactivarUsuario(Usuario usuario) throws Exception {
//        if (usuario != null) {
//            usuario.desactivar();
//            usuarioDAO.cambiarEstado(usuario.getId(), false);
////            usuarioDAO.desactivarUsuario(usuario.getId());
//        }
//    }

    public void eliminarUsuario(Long id) throws Exception {
        if (id != null) {
            usuarioDAO.eliminarUsuario(id);
        }
    }

    public void actualizarEstado(Long idUsuario, Boolean nuevoEstado) throws Exception {
        usuarioDAO.cambiarEstado(idUsuario,nuevoEstado);
    }

    public void recuperarClave(String correo) throws Exception{

            Usuario usuario = usuarioDAO.buscarPorCorreo(correo);

            String nuevaClave = generarClaveAleatoria();
            String claveHasheada = UtilidadClave.hashClave(nuevaClave);
            usuario.setClave(claveHasheada);
            usuarioDAO.actualizarClave(usuario.getId(), usuario.getClave());
            usuario.setRequiereCambioClave(true);
            usuarioDAO.setRequiereCambioClave(usuario.getId(), usuario.isRequiereCambioClave());
            ServicioCorreoSingleton.getInstancia().enviarCorreo(usuario.getCorreo(), ContenidoMensaje.crearMensajeRecuperacionContrasena(usuario.getNombreCompleto(), usuario.getNombreUsuario(), nuevaClave));
            ManejadorMetodosComunes.mostrarVentanaExito("Accion realizada exitosamente");

    }

    public void actualizarClaveTemporal(Usuario usuarioActivo, String nuevaClave) throws SQLException {
        String claveHasheada = UtilidadClave.hashClave(nuevaClave);
        usuarioActivo.actualizarClave(claveHasheada);
        usuarioDAO.actualizarClaveTemporal(usuarioActivo.getId(), claveHasheada);
    }

}
