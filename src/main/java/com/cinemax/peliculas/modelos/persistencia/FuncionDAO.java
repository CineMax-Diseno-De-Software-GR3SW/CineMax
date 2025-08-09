package com.cinemax.peliculas.modelos.persistencia;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.comun.conexiones.ConexionBaseSingleton;
import com.cinemax.peliculas.modelos.entidades.FormatoFuncion;
import com.cinemax.peliculas.modelos.entidades.TipoEstreno;
import com.cinemax.salas.modelos.entidades.Sala;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) para la gestión de funciones cinematográficas.
 *
 * <p>Esta clase proporciona métodos para realizar operaciones CRUD (Crear, Leer,
 * Actualizar, Eliminar) sobre las funciones en la base de datos. Utiliza
 * procedimientos almacenados optimizados para mejorar el rendimiento y mantener
 * la consistencia de los datos.
 *
 * <p>Características principales:
 * <ul>
 *   <li>Operaciones CRUD completas para funciones</li>
 *   <li>Consultas optimizadas con JOINs para evitar el problema N+1</li>
 *   <li>Manejo de excepciones SQL</li>
 *   <li>Mapeo automático de ResultSet a objetos de dominio</li>
 *   <li>Uso de procedimientos almacenados para mejor rendimiento</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public class FuncionDAO {

    /** Gestor de conexión a la base de datos */
    private ConexionBaseSingleton gestorDB;

    /**
     * Constructor que inicializa el DAO con la conexión a la base de datos.
     */
    public FuncionDAO() {
        this.gestorDB = ConexionBaseSingleton.getInstancia();
    }

    /**
     * Crea una nueva función en la base de datos.
     *
     * <p>Este método utiliza un procedimiento almacenado para insertar la función
     * y obtener el ID generado automáticamente, el cual se asigna al objeto función.
     *
     * @param funcion Objeto Funcion a crear, no puede ser null
     * @throws SQLException Si ocurre un error durante la operación de base de datos
     * @throws IllegalArgumentException Si la función es null o tiene datos inválidos
     */
    public void crear(Funcion funcion) throws SQLException {
        String sql = "SELECT guardar_funcion(?, ?, ?, ?, ?, ?)";

        try (Connection conn = gestorDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, funcion.getPelicula().getId());
            stmt.setInt(2, funcion.getSala().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(funcion.getFechaHoraInicio()));
            stmt.setTimestamp(4, Timestamp.valueOf(funcion.getFechaHoraFin()));
            stmt.setString(5, funcion.getFormato().toString());
            stmt.setString(6, funcion.getTipoEstreno().name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    funcion.setId(rs.getInt(1));
                    System.out.println("Función guardada con ID: " + funcion.getId());
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al guardar función (SP): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene todas las funciones disponibles en el sistema.
     *
     * <p>Utiliza un procedimiento almacenado optimizado con JOINs para cargar
     * en una sola consulta todas las funciones con sus respectivas películas
     * y salas, evitando el problema N+1 de consultas.
     *
     * @return Lista de todas las funciones disponibles
     * @throws RuntimeException Si ocurre un error durante la consulta
     */
    public List<Funcion> listarTodasLasFunciones() {
        List<Funcion> funciones = new ArrayList<>();
        String sql = "SELECT * FROM obtener_todas_funciones_optimizado()";

        try (Connection conn = gestorDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                funciones.add(mapearResultSetAFuncion(rs));
            }

            System.out.println("Funciones cargadas exitosamente con SP optimizado: " + funciones.size());
            return funciones;

        } catch (SQLException e) {
            System.err.println("Error al listar todas las funciones (SP optimizado): " + e.getMessage());
            throw new RuntimeException("Error al listar funciones: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todas las funciones programadas para una sala específica.
     *
     * <p>Utiliza un procedimiento almacenado optimizado para obtener las funciones
     * de una sala particular, útil para validaciones de traslape de horarios.
     *
     * @param salaId Identificador de la sala
     * @return Lista de funciones de la sala especificada
     * @throws RuntimeException Si ocurre un error durante la consulta
     */
    public List<Funcion> listarFuncionesPorSala(int salaId) {
        List<Funcion> funciones = new ArrayList<>();
        String sql = "SELECT * FROM listar_funciones_por_sala_optimizado(?)";

        try (Connection conn = gestorDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, salaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    funciones.add(mapearResultSetAFuncion(rs));
                }
            }
            
            System.out.println("Funciones por sala cargadas exitosamente con SP optimizado: " + funciones.size());
            return funciones;
            
        } catch (SQLException e) {
            System.err.println("Error al listar funciones por sala (SP optimizado): " + e.getMessage());
            throw new RuntimeException("Error al listar funciones por sala: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza una función existente en la base de datos.
     *
     * @param funcion Objeto Funcion con los datos actualizados
     * @throws SQLException Si ocurre un error durante la actualización
     */
    public void actualizar(Funcion funcion) throws SQLException {
        String sql = "CALL actualizar_funcion(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = gestorDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, funcion.getId());
            stmt.setInt(2, funcion.getPelicula().getId());
            stmt.setInt(3, funcion.getSala().getId());
            stmt.setTimestamp(4, Timestamp.valueOf(funcion.getFechaHoraInicio()));
            stmt.setTimestamp(5, Timestamp.valueOf(funcion.getFechaHoraFin()));
            stmt.setString(6, funcion.getFormato().toString());
            stmt.setString(7, funcion.getTipoEstreno().name());

            stmt.execute();

            System.out.println("Función actualizada con ID: " + funcion.getId());

        } catch (SQLException e) {
            System.err.println("Error al actualizar función (SP): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Busca una función específica por su identificador.
     *
     * @param id Identificador único de la función
     * @return Objeto Funcion si se encuentra, null si no existe
     * @throws SQLException Si ocurre un error durante la búsqueda
     */
    public Funcion buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM buscar_funcion_por_id_optimizado(?)";
        
        try (Connection conn = gestorDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetAFuncion(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("Error al buscar función por ID (SP optimizado): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Elimina una función de la base de datos.
     *
     * @param id Identificador único de la función a eliminar
     * @throws SQLException Si ocurre un error durante la eliminación
     */
    public void eliminar(int id) throws SQLException {
        String sql = "CALL eliminar_funcion(?)";

        try (Connection conn = gestorDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.execute();

            System.out.println("Función eliminada correctamente con ID: " + id);

        } catch (SQLException e) {
            System.err.println("Error al eliminar función (SP): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene los identificadores de las películas que tienen funciones futuras.
     *
     * <p>Este método es útil para determinar qué películas están actualmente
     * en cartelera o programadas para proyección futura.
     *
     * @return Lista de IDs de películas con funciones futuras
     * @throws RuntimeException Si ocurre un error durante la consulta
     */
    public List<Integer> listarIdsPeliculasDeFuncionesFuturas() {
        List<Integer> idsPeliculas = new ArrayList<>();
        String sql = "SELECT * FROM listar_ids_peliculas_funciones_futuras()";
        
        try (Connection conn = gestorDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                idsPeliculas.add(rs.getInt("id_pelicula"));
            }
            
            return idsPeliculas;
            
        } catch (SQLException e) {
            System.err.println("Error al listar IDs de películas de funciones futuras (SP): " + e.getMessage());
            throw new RuntimeException("Error al listar IDs de películas de funciones futuras: " + e.getMessage(), e);
        }
    }

    /**
     * Mapea un ResultSet a un objeto Funcion.
     *
     * <p>Este método privado elimina la duplicación de código y centraliza
     * la lógica de conversión de datos de base de datos a objetos de dominio.
     *
     * @param rs ResultSet con los datos de la función
     * @return Objeto Funcion mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos del ResultSet
     */
    private Funcion mapearResultSetAFuncion(ResultSet rs) throws SQLException {
        // Crear objeto Pelicula usando el patrón del PeliculaDAO
        Pelicula pelicula = new Pelicula();
        pelicula.setId(rs.getInt("pelicula_id"));
        pelicula.setTitulo(rs.getString("titulo"));
        pelicula.setSinopsis(rs.getString("sinopsis"));
        pelicula.setDuracionMinutos(rs.getInt("duracion_minutos"));
        pelicula.setAnio(rs.getInt("anio"));
        
        // Mapear idioma de String a Enum
        String idiomaCodigo = rs.getString("idioma");
        if (idiomaCodigo != null && !idiomaCodigo.trim().isEmpty()) {
            try {
                pelicula.setIdioma(com.cinemax.peliculas.modelos.entidades.Idioma.porCodigo(idiomaCodigo));
            } catch (IllegalArgumentException e) {
                System.err.println("Código de idioma no válido en BD: " + idiomaCodigo + ". Se establece null.");
            }
        }
        
        pelicula.setGenerosPorString(rs.getString("genero"));
        pelicula.setImagenUrl(rs.getString("imagen_url"));

        // Crear objeto Sala directamente del ResultSet
        Sala sala = new Sala(
            rs.getInt("sala_id"),
            rs.getString("sala_nombre"),
            rs.getInt("capacidad"),
            com.cinemax.salas.modelos.entidades.TipoSala.valueOf(rs.getString("sala_tipo")),
            com.cinemax.salas.modelos.entidades.EstadoSala.valueOf(rs.getString("sala_estado"))
        );

        // Crear y retornar la función con los objetos ya construidos
        return new Funcion(
                rs.getInt("id_funcion"),
                pelicula,
                sala,
                rs.getTimestamp("fecha_hora_inicio").toLocalDateTime(),
                rs.getTimestamp("fecha_hora_fin").toLocalDateTime(),
                FormatoFuncion.fromString(rs.getString("formato")),
                TipoEstreno.valueOf(rs.getString("tipo_estreno")));
    }

}