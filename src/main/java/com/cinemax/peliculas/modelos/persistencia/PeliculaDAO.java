package com.cinemax.peliculas.modelos.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cinemax.comun.ConexionBaseSingleton;
import com.cinemax.peliculas.modelos.entidades.Idioma;
import com.cinemax.peliculas.modelos.entidades.Pelicula;

/**
 * Clase de acceso a datos (DAO) para la gestión de películas.
 *
 * <p>Esta clase proporciona métodos para realizar operaciones CRUD (Crear, Leer,
 * Actualizar, Eliminar) sobre las películas en la base de datos. Utiliza
 * procedimientos almacenados para garantizar la consistencia y optimizar
 * el rendimiento de las operaciones.
 *
 * <p>Características principales:
 * <ul>
 *   <li>Operaciones CRUD completas para películas</li>
 *   <li>Validación de duplicados por título y año</li>
 *   <li>Búsquedas flexibles por título</li>
 *   <li>Manejo robusto de excepciones SQL</li>
 *   <li>Mapeo automático de ResultSet a objetos de dominio</li>
 *   <li>Uso de procedimientos almacenados para mejor rendimiento</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public class PeliculaDAO {
    
    /** Gestor singleton de conexión a la base de datos */
    private ConexionBaseSingleton conexionBaseSingleton;
    
    /**
     * Constructor que inicializa el DAO con la conexión a la base de datos.
     */
    public PeliculaDAO() {
        this.conexionBaseSingleton = ConexionBaseSingleton.getInstancia();
    }
    
    /**
     * Crea una nueva película en la base de datos.
     *
     * <p>Este método utiliza un procedimiento almacenado para insertar la película
     * y obtener el ID generado automáticamente, el cual se asigna al objeto película.
     *
     * @param pelicula Objeto Pelicula a crear, no puede ser null
     * @throws SQLException Si ocurre un error durante la operación de base de datos
     * @throws IllegalArgumentException Si la película es null o tiene datos inválidos
     */
    public void crear(Pelicula pelicula) throws SQLException {
        String sql = "SELECT guardar_pelicula(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexionBaseSingleton.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pelicula.getTitulo());
            stmt.setString(2, pelicula.getSinopsis());
            stmt.setInt(3, pelicula.getDuracionMinutos());
            stmt.setInt(4, pelicula.getAnio());
            stmt.setString(5, pelicula.getIdioma() != null ? pelicula.getIdioma().getCodigo() : null);
            stmt.setString(6, pelicula.getGenerosComoString());
            stmt.setString(7, pelicula.getImagenUrl());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pelicula.setId(rs.getInt(1));
                    System.out.println("Película guardada con ID: " + pelicula.getId());
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al guardar película (SP): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Actualiza una película existente en la base de datos.
     *
     * <p>Utiliza un procedimiento almacenado para garantizar la atomicidad
     * de la operación y mantener la integridad referencial.
     *
     * @param pelicula Objeto Pelicula con los datos actualizados
     * @throws SQLException Si ocurre un error durante la actualización
     */
    public void actualizar(Pelicula pelicula) throws SQLException {
        String sql = "CALL actualizar_pelicula(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexionBaseSingleton.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pelicula.getId());
            stmt.setString(2, pelicula.getTitulo());
            stmt.setString(3, pelicula.getSinopsis());
            stmt.setInt(4, pelicula.getDuracionMinutos());
            stmt.setInt(5, pelicula.getAnio());
            stmt.setString(6, pelicula.getIdioma() != null ? pelicula.getIdioma().getCodigo() : null);
            stmt.setString(7, pelicula.getGenerosComoString());
            stmt.setString(8, pelicula.getImagenUrl());

            stmt.execute();

            System.out.println("Película actualizada con ID: " + pelicula.getId());

        } catch (SQLException e) {
            System.err.println("Error al actualizar película (SP): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Elimina una película de la base de datos por su identificador.
     *
     * <p>Esta operación elimina permanentemente la película del sistema.
     * Se debe validar previamente que no existan dependencias activas.
     *
     * @param id Identificador único de la película a eliminar
     * @throws SQLException Si ocurre un error durante la eliminación
     */
    public void eliminar(int id) throws SQLException {
        String sql = "CALL eliminar_pelicula(?)";

        try (Connection conn = conexionBaseSingleton.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.execute();

            System.out.println("Película eliminada con ID: " + id);

        } catch (SQLException e) {
            System.err.println("Error al eliminar película (SP): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Busca una película específica por su identificador único.
     *
     * @param id Identificador único de la película
     * @return Objeto Pelicula si se encuentra, null si no existe
     * @throws SQLException Si ocurre un error durante la búsqueda
     */
    public Pelicula buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM buscar_pelicula_por_id(?)";

        try (Connection conn = conexionBaseSingleton.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetAPelicula(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            System.err.println("Error al buscar película por ID (SP): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Verifica si existe una película duplicada con el mismo título y año.
     *
     * <p>Este método es útil para validar duplicados antes de crear
     * nuevas películas en el sistema.
     *
     * @param titulo Título de la película a verificar
     * @param anio Año de lanzamiento de la película
     * @return true si existe una película duplicada, false en caso contrario
     * @throws SQLException Si ocurre un error durante la verificación
     * @throws IllegalArgumentException Si el título es null o vacío
     */
    public boolean existeDuplicado(String titulo, int anio) throws SQLException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        
        String sql = "SELECT COUNT(*) FROM pelicula WHERE LOWER(titulo) = LOWER(?) AND anio = ?";
        
        try (Connection conn = conexionBaseSingleton.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, titulo.trim());
            stmt.setInt(2, anio);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar duplicados: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Obtiene todas las películas disponibles en el sistema.
     *
     * <p>Utiliza un procedimiento almacenado para obtener eficientemente
     * toda la lista de películas con sus datos completos.
     *
     * @return Lista de todas las películas disponibles
     * @throws SQLException Si ocurre un error durante la consulta
     */
    public List<Pelicula> listarTodas() throws SQLException {
        List<Pelicula> peliculas = new ArrayList<>();
        String sql = "SELECT * FROM obtener_todas_peliculas()";

        try (Connection conn = conexionBaseSingleton.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                peliculas.add(mapearResultSetAPelicula(rs));
            }

            return peliculas;

        } catch (SQLException e) {
            System.err.println("Error al obtener todas las películas (SP): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Busca películas por título utilizando búsqueda parcial.
     *
     * <p>Realiza una búsqueda case-insensitive que permite encontrar
     * películas cuyo título contenga la cadena proporcionada.
     *
     * @param titulo Título o parte del título a buscar
     * @return Lista de películas que coinciden con el criterio de búsqueda
     * @throws SQLException Si ocurre un error durante la búsqueda
     */
    public List<Pelicula> buscarPorTitulo(String titulo) throws SQLException {
        List<Pelicula> peliculas = new ArrayList<>();
        String sql = "SELECT * FROM buscar_peliculas_por_titulo(?)";

        try (Connection conn = conexionBaseSingleton.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, titulo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    peliculas.add(mapearResultSetAPelicula(rs));
                }
            }

            return peliculas;

        } catch (SQLException e) {
            System.err.println("Error al buscar películas por título (SP): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Mapea un ResultSet a un objeto Pelicula.
     *
     * <p>Este método privado centraliza la lógica de conversión de datos
     * de base de datos a objetos de dominio, evitando duplicación de código
     * y facilitando el mantenimiento.
     *
     * @param rs ResultSet con los datos de la película
     * @return Objeto Pelicula mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos del ResultSet
     */
    private Pelicula mapearResultSetAPelicula(ResultSet rs) throws SQLException {
        Pelicula pelicula = new Pelicula();
        
        pelicula.setId(rs.getInt("id"));
        pelicula.setTitulo(rs.getString("titulo"));
        pelicula.setSinopsis(rs.getString("sinopsis"));
        pelicula.setDuracionMinutos(rs.getInt("duracion_minutos"));
        pelicula.setAnio(rs.getInt("anio"));
        
        // Mapear idioma de String a Enum con manejo de errores
        String idiomaCodigo = rs.getString("idioma");
        if (idiomaCodigo != null && !idiomaCodigo.trim().isEmpty()) {
            try {
                pelicula.setIdioma(Idioma.porCodigo(idiomaCodigo));
            } catch (IllegalArgumentException e) {
                System.err.println("Código de idioma no válido en BD: " + idiomaCodigo + ". Se establece null.");
            }
        }

        pelicula.setGenerosPorString(rs.getString("genero"));
        pelicula.setImagenUrl(rs.getString("imagen_url"));
        
        return pelicula;
    }
}
