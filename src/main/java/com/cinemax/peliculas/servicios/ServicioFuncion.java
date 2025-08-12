package com.cinemax.peliculas.servicios;

/**
 * Servicio de negocio para la gestión de funciones cinematográficas.
 *
 * <p>Esta clase implementa la lógica de negocio relacionada con las funciones
 * de cine, incluyendo validaciones de horarios, traslapes, restricciones
 * operativas y reglas de programación. Actúa como intermediario entre
 * la capa de presentación y la capa de persistencia.
 *
 * <p>Funcionalidades principales:
 * <ul>
 *   <li>Creación y actualización de funciones con validaciones completas</li>
 *   <li>Validación de horarios de trabajo del cine</li>
 *   <li>Prevención de traslapes de funciones en salas</li>
 *   <li>Gestión del CRUD completo de funciones</li>
 *   <li>Búsquedas especializadas por película y sala</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.entidades.FormatoFuncion;
import com.cinemax.peliculas.modelos.entidades.TipoEstreno;
import com.cinemax.peliculas.modelos.persistencia.FuncionDAO;
import com.cinemax.salas.modelos.entidades.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServicioFuncion {

    /** DAO para acceso a datos de funciones */
    private FuncionDAO funcionDAO;

    /**
     * Constructor que inicializa el servicio con el DAO correspondiente.
     */
    public ServicioFuncion() {
        this.funcionDAO = new FuncionDAO();
    }

    /**
     * Crea una nueva función cinematográfica con validaciones completas.
     *
     * <p>Este método realiza las siguientes validaciones:
     * <ul>
     *   <li>Validación de datos obligatorios</li>
     *   <li>Verificación de disponibilidad de sala</li>
     *   <li>Validación de horarios de trabajo</li>
     *   <li>Prevención de traslapes de funciones</li>
     * </ul>
     *
     * @param pelicula Película a proyectar, no puede ser null
     * @param sala Sala donde se realizará la proyección, no puede ser null
     * @param fechaHoraInicio Fecha y hora de inicio de la función
     * @param formato Formato de proyección (2D/3D)
     * @param tipoEstreno Tipo de estreno de la función
     * @return Función creada con ID asignado
     * @throws IllegalArgumentException Si algún parámetro es inválido o no cumple reglas de negocio
     * @throws SQLException Si ocurre un error durante la persistencia
     */
    public Funcion crearFuncion(Pelicula pelicula, Sala sala, LocalDateTime fechaHoraInicio,
            FormatoFuncion formato, TipoEstreno tipoEstreno)
            throws IllegalArgumentException, SQLException {

        validarDatosFuncion(pelicula, sala, fechaHoraInicio, formato, tipoEstreno);

        LocalDateTime fechaHoraFin = fechaHoraInicio.plusMinutes(pelicula.getDuracionMinutos() + 40);

        validarHorarioTrabajo(fechaHoraInicio, fechaHoraFin);

        validarTraslapeFunciones(sala, fechaHoraInicio, fechaHoraFin);

        Funcion funcion = new Funcion(0, pelicula, sala, fechaHoraInicio, fechaHoraFin, formato, tipoEstreno);
        funcionDAO.crear(funcion);
        return funcion;
    }

    /**
     * Valida los datos básicos requeridos para crear una función.
     *
     * @param pelicula Película a validar
     * @param sala Sala a validar
     * @param fechaHoraInicio Fecha/hora de inicio a validar
     * @param formato Formato de proyección a validar
     * @param tipoEstreno Tipo de estreno a validar
     * @throws IllegalArgumentException Si algún dato es inválido
     */
    private void validarDatosFuncion(Pelicula pelicula, Sala sala, LocalDateTime fechaHoraInicio,
            FormatoFuncion formato, TipoEstreno tipoEstreno) {
        if (pelicula == null) {
            throw new IllegalArgumentException("Debe seleccionar una película válida.");
        }
        if (sala == null) {
            throw new IllegalArgumentException("Debe seleccionar una sala válida.");
        }
        if (sala.getEstado() != null && sala.getEstado() != EstadoSala.DISPONIBLE) {
            throw new IllegalArgumentException("La sala seleccionada no está disponible (actualmente está "
                    + sala.getEstado().name().toLowerCase() + ").");
        }
        if (fechaHoraInicio == null) {
            throw new IllegalArgumentException("Debe ingresar una fecha y hora de inicio válida.");
        }
        if (formato == null) {
            throw new IllegalArgumentException("Debe seleccionar un formato válido.");
        }
        if (tipoEstreno == null) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de estreno válido.");
        }
    }

    /**
     * Valida que la función se encuentre dentro del horario de trabajo del cine.
     *
     * <p>El cine opera de 9:00 AM a 10:00 PM. Las funciones deben iniciar
     * después de las 9:00 AM y terminar antes de las 10:00 PM.
     *
     * @param inicio Fecha/hora de inicio de la función
     * @param fin Fecha/hora de finalización de la función
     * @throws IllegalArgumentException Si la función está fuera del horario laboral
     */
    private void validarHorarioTrabajo(LocalDateTime inicio, LocalDateTime fin) {
        LocalTime horaApertura = LocalTime.of(9, 0);
        LocalTime horaCierre = LocalTime.of(22, 0);

        if (inicio.toLocalTime().isBefore(horaApertura)) {
            throw new IllegalArgumentException("La función no puede iniciar antes de las 9:00 a.m.");
        }
        if (fin.toLocalTime().isAfter(horaCierre)) {
            throw new IllegalArgumentException("La función no puede terminar después de las 10:00 p.m.");
        }
    }

    /**
     * Valida que no existan traslapes de funciones en una sala específica.
     *
     * @param sala Sala a verificar
     * @param inicio Fecha/hora de inicio de la nueva función
     * @param fin Fecha/hora de finalización de la nueva función
     * @throws SQLException Si ocurre un error al consultar funciones existentes
     * @throws IllegalArgumentException Si existe traslape con otra función
     */
    private void validarTraslapeFunciones(Sala sala, LocalDateTime inicio, LocalDateTime fin) throws SQLException {
        validarTraslapeFunciones(sala, inicio, fin, -1); // -1 indica que no hay función a excluir
    }

    /**
     * Valida traslapes de funciones excluyendo una función específica (útil para actualizaciones).
     *
     * @param sala Sala a verificar
     * @param inicio Fecha/hora de inicio
     * @param fin Fecha/hora de finalización
     * @param idFuncionAExcluir ID de la función a excluir de la validación
     * @throws SQLException Si ocurre un error al consultar funciones existentes
     * @throws IllegalArgumentException Si existe traslape con otra función
     */
    private void validarTraslapeFunciones(Sala sala, LocalDateTime inicio, LocalDateTime fin, int idFuncionAExcluir) throws SQLException {
        List<Funcion> funcionesSala = funcionDAO.listarFuncionesPorSala(sala.getId());
        for (Funcion f : funcionesSala) {
            // Excluir la función que se está editando
            if (f.getId() != idFuncionAExcluir && inicio.isBefore(f.getFechaHoraFin()) && fin.isAfter(f.getFechaHoraInicio())) {
                throw new IllegalArgumentException(
                        String.format("Ya existe una función programada en la sala %s entre %s y %s.",
                                sala.getNombre(), f.getFechaHoraInicio(), f.getFechaHoraFin()));
            }
        }
    }

    /**
     * Actualiza una función existente with validaciones completas.
     *
     * <p>Realiza las mismas validaciones que la creación, pero excluye
     * la función actual del análisis de traslapes.
     *
     * @param id Identificador de la función a actualizar
     * @param pelicula Nueva película a proyectar
     * @param sala Nueva sala de proyección
     * @param fechaHoraInicio Nueva fecha/hora de inicio
     * @param formato Nuevo formato de proyección
     * @param tipoEstreno Nuevo tipo de estreno
     * @throws SQLException Si ocurre un error durante la actualización
     * @throws IllegalArgumentException Si los datos son inválidos o la función no existe
     */
    public void actualizarFuncion(int id, Pelicula pelicula, Sala sala, LocalDateTime fechaHoraInicio,
            FormatoFuncion formato, TipoEstreno tipoEstreno) throws SQLException {
        Funcion funcionExistente = funcionDAO.buscarPorId(id);
        if (funcionExistente == null) {
            throw new IllegalArgumentException("No se encontró la función con ID: " + id);
        }

        validarDatosFuncion(pelicula, sala, fechaHoraInicio, formato, tipoEstreno);
        LocalDateTime fechaHoraFin = fechaHoraInicio.plusMinutes(pelicula.getDuracionMinutos() + 40);
        validarHorarioTrabajo(fechaHoraInicio, fechaHoraFin);
        validarTraslapeFunciones(sala, fechaHoraInicio, fechaHoraFin, id); // Excluir la función que se está editando

        funcionExistente.setPelicula(pelicula);
        funcionExistente.setSala(sala);
        funcionExistente.setFechaHoraInicio(fechaHoraInicio);
        funcionExistente.setFechaHoraFin(fechaHoraFin);
        funcionExistente.setFormato(formato);
        funcionExistente.setTipoEstreno(tipoEstreno);

        funcionDAO.actualizar(funcionExistente);
    }

    /**
     * Obtiene todas las funciones disponibles en el sistema.
     *
     * @return Lista de todas las funciones
     */
    public List<Funcion> listarTodasLasFunciones() {
        return funcionDAO.listarTodasLasFunciones();
    }

    /**
     * Busca una función específica por su identificador.
     *
     * @param id Identificador único de la función
     * @return Función encontrada o null si no existe
     * @throws SQLException Si ocurre un error durante la búsqueda
     */
    public Funcion buscarFuncionPorId(int id) throws SQLException {
        return funcionDAO.buscarPorId(id);
    }

    /**
     * Obtiene todas las funciones programadas para una sala específica.
     *
     * @param salaId Identificador de la sala
     * @return Lista de funciones de la sala
     * @throws SQLException Si ocurre un error durante la consulta
     */
    public List<Funcion> listarFuncionesPorSala(int salaId) throws SQLException {
        return funcionDAO.listarFuncionesPorSala(salaId);
    }

    /**
     * Elimina una función del sistema.
     *
     * @param id Identificador de la función a eliminar
     * @throws SQLException Si ocurre un error durante la eliminación
     * @throws IllegalArgumentException Si la función no existe
     */
    public void eliminarFuncion(int id) throws SQLException {
        // Verificar que existe antes de eliminar
        Funcion funcion = funcionDAO.buscarPorId(id);
        if (funcion == null) {
            throw new IllegalArgumentException("No existe una función con ID: " + id);
        }

        funcionDAO.eliminar(id);
    }

    /**
     * Obtiene las funciones de una película específica por su título.
     *
     * <p>Realiza una búsqueda case-insensitive y ordena los resultados
     * por fecha y hora de inicio.
     *
     * @param nombrePelicula El título de la película a buscar
     * @return Lista de funciones de la película especificada, ordenadas por fecha
     * @throws Exception Si ocurre un error durante la búsqueda
     * @throws IllegalArgumentException Si el nombre de la película es vacío o null
     */
    public List<Funcion> obtenerFuncionesPorNombrePelicula(String nombrePelicula) throws Exception {
        if (nombrePelicula == null || nombrePelicula.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la película no puede estar vacío");
        }
        
        try {
            // Obtener todas las funciones
            List<Funcion> todasLasFunciones = funcionDAO.listarTodasLasFunciones();
            List<Funcion> funcionesEncontradas = new ArrayList<>();
            
            // Filtrar por nombre de película (búsqueda case-insensitive y parcial)
            String nombreBusqueda = nombrePelicula.trim().toLowerCase();
            
            for (Funcion funcion : todasLasFunciones) {
                if (funcion.getPelicula() != null && 
                    funcion.getPelicula().getTitulo() != null &&
                    funcion.getPelicula().getTitulo().toLowerCase().contains(nombreBusqueda)) {
                    funcionesEncontradas.add(funcion);
                }
            }
            
            // Ordenar por fecha y hora de inicio
            funcionesEncontradas.sort((f1, f2) -> {
                if (f1.getFechaHoraInicio() != null && f2.getFechaHoraInicio() != null) {
                    return f1.getFechaHoraInicio().compareTo(f2.getFechaHoraInicio());
                }
                return 0;
            });
            
            return funcionesEncontradas;
            
        } catch (Exception e) {
            throw new Exception("Error al obtener funciones por película: " + e.getMessage(), e);
        }
    }
}