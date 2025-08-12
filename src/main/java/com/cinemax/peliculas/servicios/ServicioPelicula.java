package com.cinemax.peliculas.servicios;

import java.sql.SQLException;
import java.util.List;

import com.cinemax.peliculas.modelos.entidades.Idioma;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.persistencia.PeliculaDAO;

/**
 * Servicio de negocio para la gestión de películas.
 *
 * <p>Esta clase implementa la lógica de negocio relacionada con las películas
 * del sistema CineMax, incluyendo validaciones de datos, reglas de negocio
 * y operaciones CRUD. Actúa como intermediario entre la capa de presentación
 * y la capa de persistencia, asegurando la integridad y consistencia de los datos.
 *
 * <p>Funcionalidades principales:
 * <ul>
 *   <li>Creación y actualización de películas con validaciones exhaustivas</li>
 *   <li>Validación de duplicados por título y año</li>
 *   <li>Gestión completa del CRUD de películas</li>
 *   <li>Búsquedas flexibles por diferentes criterios</li>
 *   <li>Aplicación de reglas de negocio específicas del dominio</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public class ServicioPelicula {
    
    /** DAO para acceso a datos de películas */
    private PeliculaDAO peliculaDAO;
    
    /**
     * Constructor que inicializa el servicio con el DAO correspondiente.
     */
    public ServicioPelicula() {
        this.peliculaDAO = new PeliculaDAO();
    }
    
    /**
     * Crea una nueva película con validaciones completas de negocio.
     *
     * <p>Este método realiza validaciones exhaustivas de todos los datos
     * de entrada y aplica las reglas de negocio específicas del sistema
     * antes de persistir la película.
     *
     * @param titulo Título de la película, no puede ser null ni vacío
     * @param sinopsis Descripción de la película, no puede ser null ni vacía
     * @param duracionMinutos Duración en minutos, debe ser mayor a 0
     * @param anio Año de lanzamiento, debe estar en rango válido
     * @param idioma Idioma principal de la película
     * @param genero Géneros de la película separados por comas
     * @param imagenUrl URL de la imagen promocional
     * @return Película creada con ID asignado
     * @throws IllegalArgumentException Si algún parámetro es inválido
     * @throws SQLException Si ocurre un error durante la persistencia
     */
    public Pelicula crearPelicula(String titulo, String sinopsis, int duracionMinutos, 
                                 int anio, Idioma idioma, String genero, String imagenUrl) 
                                 throws IllegalArgumentException, SQLException {
        
        // Validaciones de negocio
        validarDatosPelicula(titulo, sinopsis, duracionMinutos, anio, genero);
        
        // Crear objeto película
        Pelicula nuevaPelicula = new Pelicula(titulo.trim(), sinopsis.trim(), 
                                            duracionMinutos, anio, idioma, 
                                            new java.util.ArrayList<>(), imagenUrl); // Lista vacía por defecto
        
        // Establecer géneros desde string
        nuevaPelicula.setGenerosPorString(genero.trim());
        
        // Guardar en base de datos
        peliculaDAO.crear(nuevaPelicula);
        
        return nuevaPelicula;
    }
    
    /**
     * Actualiza una película existente con validaciones completas.
     *
     * <p>Verifica la existencia de la película antes de aplicar las
     * validaciones y actualizar los datos.
     *
     * @param id Identificador único de la película a actualizar
     * @param titulo Nuevo título de la película
     * @param sinopsis Nueva descripción de la película
     * @param duracionMinutos Nueva duración en minutos
     * @param anio Nuevo año de lanzamiento
     * @param idioma Nuevo idioma principal
     * @param genero Nuevos géneros separados por comas
     * @param imagenUrl Nueva URL de imagen promocional
     * @throws IllegalArgumentException Si los datos son inválidos o la película no existe
     * @throws SQLException Si ocurre un error durante la actualización
     */
    public void actualizarPelicula(int id, String titulo, String sinopsis, 
                                  int duracionMinutos, int anio, Idioma idioma, 
                                  String genero, String imagenUrl) 
                                  throws IllegalArgumentException, SQLException {
        
        // Validar que la película existe
        Pelicula peliculaExistente = peliculaDAO.buscarPorId(id);
        if (peliculaExistente == null) {
            throw new IllegalArgumentException("No existe una película con ID: " + id);
        }
        
        // Validaciones de negocio
        validarDatosPelicula(titulo, sinopsis, duracionMinutos, anio, genero);
        
        // Actualizar datos
        peliculaExistente.setTitulo(titulo.trim());
        peliculaExistente.setSinopsis(sinopsis.trim());
        peliculaExistente.setDuracionMinutos(duracionMinutos);
        peliculaExistente.setAnio(anio);
        peliculaExistente.setIdioma(idioma);
        peliculaExistente.setGenerosPorString(genero.trim());
        peliculaExistente.setImagenUrl(imagenUrl);
        
        // Guardar cambios
        peliculaDAO.actualizar(peliculaExistente);
    }
    
    /**
     * Elimina una película del sistema con validaciones previas.
     *
     * @param id Identificador único de la película a eliminar
     * @throws IllegalArgumentException Si el ID es inválido o la película no existe
     * @throws SQLException Si ocurre un error durante la eliminación
     */
    public void eliminarPelicula(int id) throws IllegalArgumentException, SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        
        // Verificar que existe antes de eliminar
        Pelicula pelicula = peliculaDAO.buscarPorId(id);
        if (pelicula == null) {
            throw new IllegalArgumentException("No existe una película con ID: " + id);
        }
        
        peliculaDAO.eliminar(id);
    }
    
    /**
     * Busca una película específica por su identificador único.
     *
     * @param id Identificador único de la película
     * @return Película encontrada o null si no existe
     * @throws IllegalArgumentException Si el ID es inválido
     * @throws SQLException Si ocurre un error durante la búsqueda
     */
    public Pelicula buscarPeliculaPorId(int id) throws IllegalArgumentException, SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        
        return peliculaDAO.buscarPorId(id);
    }
    
    /**
     * Obtiene todas las películas disponibles en el sistema.
     *
     * @return Lista de todas las películas
     * @throws SQLException Si ocurre un error durante la consulta
     */
    public List<Pelicula> obtenerPeliculas() throws SQLException {
        return peliculaDAO.listarTodas();
    }

    /**
     * Busca películas por título utilizando búsqueda parcial.
     *
     * <p>Realiza una búsqueda case-insensitive que permite encontrar
     * películas cuyo título contenga la cadena proporcionada.
     *
     * @param titulo Título o parte del título a buscar
     * @return Lista de películas que coinciden con el criterio
     * @throws IllegalArgumentException Si el título de búsqueda es vacío
     * @throws SQLException Si ocurre un error durante la búsqueda
     */
    public List<Pelicula> buscarPeliculasPorTitulo(String titulo) 
                                                  throws IllegalArgumentException, SQLException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título de búsqueda no puede estar vacío");
        }
        
        return peliculaDAO.buscarPorTitulo(titulo);
    }
    
    /**
     * Verifica si existe una película duplicada con el mismo título y año.
     *
     * <p>Útil para validaciones antes de crear nuevas películas.
     *
     * @param titulo Título de la película a verificar
     * @param anio Año de lanzamiento de la película
     * @return true si existe un duplicado, false en caso contrario
     * @throws SQLException Si ocurre un error durante la verificación
     */
    public boolean existePeliculaDuplicada(String titulo, int anio) throws SQLException {
        if (titulo == null || titulo.trim().isEmpty()) {
            return false;
        }
        
        return peliculaDAO.existeDuplicado(titulo, anio);
    }
    
    /**
     * Valida exhaustivamente los datos de una película según las reglas de negocio.
     *
     * <p>Este método centraliza todas las validaciones de datos para
     * garantizar la consistencia en creación y actualización.
     *
     * @param titulo Título a validar
     * @param sinopsis Sinopsis a validar
     * @param duracionMinutos Duración a validar
     * @param anio Año a validar
     * @param genero Género a validar
     * @throws IllegalArgumentException Si algún dato no cumple las reglas de negocio
     */
    private void validarDatosPelicula(String titulo, String sinopsis, int duracionMinutos, 
                                     int anio, String genero) throws IllegalArgumentException {
        
        // Validar título
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        
        if (titulo.trim().length() > 255) {
            throw new IllegalArgumentException("El título no puede tener más de 255 caracteres");
        }
        
        // Validar sinopsis
        if (sinopsis == null || sinopsis.trim().isEmpty()) {
            throw new IllegalArgumentException("La sinopsis no puede estar vacía");
        }
        
        if (sinopsis.trim().length() > 1000) {
            throw new IllegalArgumentException("La sinopsis no puede tener más de 1000 caracteres");
        }
        
        // Validar duración
        if (duracionMinutos <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor a 0 minutos");
        }
        
        if (duracionMinutos > 600) { // Más de 10 horas parece excesivo
            throw new IllegalArgumentException("La duración no puede ser mayor a 600 minutos");
        }
        
        // Validar año
        int anioActual = java.time.Year.now().getValue();
        if (anio < 1888) { // Primer película de la historia
            throw new IllegalArgumentException("El año no puede ser anterior a 1888");
        }
        
        if (anio > anioActual + 5) { // Permitir algunos años futuros
            throw new IllegalArgumentException("El año no puede ser más de 5 años en el futuro");
        }
        
        // Validar género
        if (genero == null || genero.trim().isEmpty()) {
            throw new IllegalArgumentException("El género no puede estar vacío");
        }
        
        if (genero.trim().length() > 50) {
            throw new IllegalArgumentException("El género no puede tener más de 50 caracteres");
        }
    }

    /**
     * Obtiene todas las películas disponibles en cartelera.
     *
     * <p>Método alias para mantener compatibilidad con diferentes interfaces.
     *
     * @return Lista de todas las películas
     * @throws Exception Si ocurre un error durante la consulta
     */
    public List<Pelicula> listarTodasLasPeliculas() throws Exception {
        try {
            return peliculaDAO.listarTodas();
        } catch (SQLException e) {
            throw new Exception("Error al obtener la lista de películas: " + e.getMessage(), e);
        }
    }
}
