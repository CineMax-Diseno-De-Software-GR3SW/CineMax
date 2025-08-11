package com.cinemax.salas.servicios;

import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.modelos.persistencia.ButacasDAO;
import com.cinemax.salas.modelos.persistencia.SalasDAO;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de negocio para la gestión de butacas.
 *
 * Propósito:
 * - Encapsular la lógica de negocio y validaciones antes de interactuar con la base de datos.
 * - Coordinar operaciones entre las butacas y la sala a la que pertenecen.
 *
 * Funcionalidades principales:
 * - Crear, actualizar, listar y eliminar butacas.
 * - Generar automáticamente butacas según la capacidad de la sala.
 * - Validar disponibilidad y evitar duplicados.
 */
public class ServicioButaca {

    /** DAO para operaciones CRUD de butacas */
    private final ButacasDAO butacasDAO = new ButacasDAO();

    /** DAO para operaciones CRUD de salas (usado para validar capacidad y existencia) */
    private final SalasDAO salasDAO = new SalasDAO();

    /**
     * Lista todas las butacas de una sala específica.
     *
     * @param idSala ID de la sala.
     * @return Lista de butacas.
     * @throws Exception si ocurre un error en la consulta.
     */
    public List<Butaca> listarButacasPorSala(int idSala) throws Exception {
        return butacasDAO.listarButacasPorSala(idSala);
    }

    /**
     * Genera automáticamente todas las butacas para una sala
     * según su capacidad y una disposición de 6 filas.
     *
     * @param salaId ID de la sala.
     * @throws Exception si la sala no existe o la capacidad no está soportada.
     */
    public void generarButacasAutomatica(int salaId) throws Exception {
        Sala sala = salasDAO.obtenerSalaPorId(salaId);
        if (sala == null)
            throw new Exception("Sala no existe: " + salaId);

        final int FILAS = 6;
        int columnas;
        switch (sala.getCapacidad()) {
            case 36 -> columnas = 6;
            case 42 -> columnas = 7;
            case 48 -> columnas = 8;
            default -> throw new Exception("Capacidad no soportada: " + sala.getCapacidad());
        }

        // Delegar generación al DAO
        butacasDAO.generarButacas(salaId, FILAS, columnas);
    }

    /**
     * Crea una nueva butaca en una sala, validando duplicados y capacidad.
     *
     * @param butaca Butaca a crear.
     * @throws Exception si la sala no existe, se supera la capacidad o la posición está ocupada.
     */
    public void crearButaca(Butaca butaca) throws Exception {
        validarDatosBasicos(butaca);

        Sala sala = salasDAO.obtenerSalaPorId(butaca.getIdSala());
        if (sala == null)
            throw new Exception("La sala con ID " + butaca.getIdSala() + " no existe.");

        List<Butaca> existentes = butacasDAO.listarButacasPorSala(butaca.getIdSala());
        if (existentes.size() >= sala.getCapacidad())
            throw new Exception("No se pueden agregar más butacas, capacidad máxima alcanzada.");

        // Evitar duplicados por fila y columna
        for (Butaca b : existentes) {
            if (b.getFila().equalsIgnoreCase(butaca.getFila()) &&
                    b.getColumna().equalsIgnoreCase(butaca.getColumna())) {
                throw new Exception("Ya existe una butaca en fila " +
                        butaca.getFila() + ", columna " +
                        butaca.getColumna() + " de la sala " +
                        butaca.getIdSala() + ".");
            }
        }

        butacasDAO.crearButaca(butaca);
    }

    /**
     * Actualiza los datos de una butaca, validando duplicados y capacidad.
     *
     * @param butaca Butaca con datos actualizados.
     * @throws Exception si la sala no existe, hay duplicados o capacidad superada.
     */
    public void actualizarButaca(Butaca butaca) throws Exception {
        validarDatosBasicos(butaca);

        // Validar existencia de la sala destino
        Sala sala = salasDAO.obtenerSalaPorId(butaca.getIdSala());
        if (sala == null)
            throw new Exception("La sala con ID " + butaca.getIdSala() + " no existe.");

        // Evitar duplicados (excluyendo esta misma butaca)
        List<Butaca> existentes = butacasDAO.listarButacasPorSala(butaca.getIdSala());
        for (Butaca b : existentes) {
            if (b.getId() != butaca.getId() &&
                    b.getFila().equalsIgnoreCase(butaca.getFila()) &&
                    b.getColumna().equalsIgnoreCase(butaca.getColumna())) {
                throw new Exception("Ya existe otra butaca en fila " +
                        butaca.getFila() + ", columna " +
                        butaca.getColumna() + " de la sala " +
                        butaca.getIdSala() + ".");
            }
        }

        // Verificar capacidad si cambia de sala
        Butaca original = butacasDAO.obtenerButacaPorId(butaca.getId());
        if (original.getIdSala() != butaca.getIdSala()) {
            if (existentes.size() >= sala.getCapacidad()) {
                throw new Exception("No se puede mover la butaca: capacidad máxima alcanzada en la sala destino.");
            }
        }

        butacasDAO.actualizarButaca(butaca);
    }

    /**
     * Valida los campos básicos de una butaca antes de cualquier operación.
     */
    private void validarDatosBasicos(Butaca butaca) throws Exception {
        if (butaca.getFila() == null || butaca.getFila().trim().isEmpty())
            throw new Exception("La fila no puede estar vacía.");
        if (butaca.getColumna() == null || butaca.getColumna().trim().isEmpty())
            throw new Exception("La columna no puede estar vacía.");
        if (butaca.getEstado() == null)
            throw new Exception("Debe seleccionar un estado para la butaca.");
    }

    /**
     * Lista todas las butacas de todas las salas.
     *
     * @return Lista de todas las butacas.
     * @throws Exception si ocurre un error en la consulta.
     */
    public List<Butaca> listarTodasButacas() throws Exception {
        return butacasDAO.listarTodasButacas();
    }

    /**
     * Elimina una butaca por su ID.
     *
     * @param id ID de la butaca a eliminar.
     * @throws Exception si ocurre un error en la eliminación.
     */
    public void eliminarButaca(int id) throws Exception {
        butacasDAO.eliminarButaca(id);
    }
    /**
     * Lista todas las butacas de una sala por su nombre.
     *
     * @param nombreSala Nombre de la sala.
     * @return Lista de butacas de la sala especificada.
     * @throws Exception si la sala no existe o hay un error en la consulta.
     */
    // ServicioButaca.java
    public List<Butaca> buscarButacasPorNombreSalaParcial(String nombreParcial) throws Exception {
        List<Sala> salas = salasDAO.buscarSalasPorNombreParcial(nombreParcial);
        List<Butaca> resultado = new ArrayList<>();
        for (Sala sala : salas) {
            resultado.addAll(butacasDAO.listarButacasPorSala(sala.getId()));
        }
        return resultado;
    }
}