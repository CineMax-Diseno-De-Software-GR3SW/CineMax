package com.cinemax.peliculas.servicios;

import java.sql.SQLException;
import java.util.List;

import com.cinemax.peliculas.modelos.entidades.Idioma;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.persistencia.PeliculaDAO;

public class ServicioPelicula {
    
    private PeliculaDAO peliculaDAO;
    
    // Constructor
    public ServicioPelicula() {
        this.peliculaDAO = new PeliculaDAO();
    }
    
    /**
     * Crea una nueva película con validaciones de negocio
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
        peliculaDAO.guardar(nuevaPelicula);
        
        return nuevaPelicula;
    }
    
    /**
     * Actualiza una película existente
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
     * Elimina una película por ID
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
     * Busca una película por ID
     */
    public Pelicula buscarPeliculaPorId(int id) throws IllegalArgumentException, SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        
        return peliculaDAO.buscarPorId(id);
    }
    
    /**
     * Obtiene todas las películas
     */
    public List<Pelicula> listarTodasLasPeliculas() throws SQLException {
        return peliculaDAO.obtenerTodas();
    }
    
    /**
     * Busca películas por título (búsqueda parcial)
     */
    public List<Pelicula> buscarPeliculasPorTitulo(String titulo) 
                                                  throws IllegalArgumentException, SQLException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título de búsqueda no puede estar vacío");
        }
        
        return peliculaDAO.buscarPorTitulo(titulo);
    }
    
    /**
     * Verifica si existe una película duplicada
     */
    public boolean existePeliculaDuplicada(String titulo, int anio) throws SQLException {
        if (titulo == null || titulo.trim().isEmpty()) {
            return false;
        }
        
        return peliculaDAO.existeDuplicado(titulo, anio);
    }
    
    /**
     * Método privado para validar los datos de una película
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
}
