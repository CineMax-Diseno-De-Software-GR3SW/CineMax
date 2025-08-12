package com.cinemax.salas.servicios;

import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.modelos.persistencia.ButacasDAO;
import com.cinemax.salas.modelos.persistencia.SalasDAO;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de negocio para la gestión de salas de cine.
 *
 * Propósito:
 * - Aplicar las reglas de negocio relacionadas con salas y sus butacas.
 * - Coordinar la interacción entre DAOs y la capa de presentación/controladores.
 * - Incluir validaciones antes de persistir cambios en la base de datos.
 *
 * Funcionalidades principales:
 * - Crear una sala con sus butacas generadas automáticamente.
 * - Listar, obtener, actualizar y eliminar salas.
 * - Ajustar las butacas de la sala si cambia la capacidad.
 */
public class ServicioSala {

    /** DAO para operaciones CRUD de salas */
    private final SalasDAO salasDAO = new SalasDAO();

    /** Servicio para la gestión de butacas (generación automática) */
    private ServicioButaca servicioButaca = new ServicioButaca();

    /** DAO para operaciones CRUD de butacas */
    private ButacasDAO butacasDAO = new ButacasDAO();

    /**
     * Crea una sala nueva, valida datos y genera sus butacas.
     *
     * @param sala Objeto {@link Sala} a registrar.
     * @throws Exception si los datos no son válidos o ya existe una sala con el mismo nombre.
     */
    public void crearSala(Sala sala) throws Exception {
        // Validaciones de negocio
        if (sala.getNombre() == null || sala.getNombre().trim().isEmpty())
            throw new Exception("El nombre de la sala no puede estar vacío.");
        if (sala.getCapacidad() <= 0 || sala.getCapacidad() > 500)
            throw new Exception("La capacidad debe ser entre 1 y 500.");
        if (sala.getTipo() == null)
            throw new Exception("Debe seleccionar un tipo de sala.");
        if (sala.getEstado() == null)
            throw new Exception("Debe seleccionar un estado para la sala.");
        for (Sala s : salasDAO.listarSalas()) {
            if (s.getNombre().equalsIgnoreCase(sala.getNombre()))
                throw new Exception("Ya existe una sala con ese nombre.");
        }

        // 1) Guardar sala
        salasDAO.crearSala(sala);

        // 2) Generar butacas automáticamente
        servicioButaca.generarButacasAutomatica(sala.getId());
    }

    /**
     * Recupera una sala por su ID.
     */
    public Sala obtenerSalaPorId(int id) throws Exception {
        return salasDAO.obtenerSalaPorId(id);
    }

    /**
     * Busca salas por nombre (parcial).
     *
     * @param nombre Nombre o parte del nombre de la sala a buscar.
     * @return Lista de salas que coinciden con el criterio.
     * @throws Exception si el nombre es nulo o vacío.
     */
    public List<Sala> buscarSalasPorNombre(String nombre) throws Exception {
        if (nombre == null || nombre.trim().isEmpty())
            throw new Exception("El nombre de la sala no puede estar vacío.");
        List<Sala> todas = salasDAO.listarSalas();
        List<Sala> resultado = new ArrayList<>();
        for (Sala s : todas) {
            if (s.getNombre().toLowerCase().contains(nombre.trim().toLowerCase())) {
                resultado.add(s);
            }
        }
        return resultado;
    }
    /**
     * Lista todas las salas registradas.
     */
    public List<Sala> listarSalas() throws Exception {
        return salasDAO.listarSalas();
    }

    /**
     * Actualiza los datos de una sala y ajusta el número de butacas si cambia la capacidad.
     *
     * @param sala Sala con nuevos datos.
     * @throws Exception si hay errores de validación o capacidad no soportada.
     */
    public void actualizarSala(Sala sala) throws Exception {
        // Validaciones básicas
        if (sala.getNombre() == null || sala.getNombre().trim().isEmpty())
            throw new Exception("El nombre de la sala no puede estar vacío.");
        if (sala.getCapacidad() <= 0 || sala.getCapacidad() > 500)
            throw new Exception("La capacidad debe ser entre 1 y 500.");
        if (sala.getTipo() == null)
            throw new Exception("Debe seleccionar un tipo de sala.");
        if (sala.getEstado() == null)
            throw new Exception("Debe seleccionar un estado para la sala.");
        for (Sala s : salasDAO.listarSalas()) {
            if (s.getNombre().equalsIgnoreCase(sala.getNombre()) && s.getId() != sala.getId())
                throw new Exception("Ya existe una sala con ese nombre.");
        }

        // Comparar capacidad anterior y nueva
        Sala original = salasDAO.obtenerSalaPorId(sala.getId());
        int capacidadAnterior = original.getCapacidad();
        int capacidadNueva = sala.getCapacidad();

        // Obtener butacas actuales
        List<Butaca> butacas = butacasDAO.listarButacasPorSala(sala.getId());

        // Reducción de capacidad → eliminar butacas sobrantes
        if (capacidadNueva < capacidadAnterior) {
            int aEliminar = butacas.size() - capacidadNueva;
            if (aEliminar > 0) {
                // Orden descendente: últimas filas y columnas primero
                butacas.sort((b1, b2) -> {
                    int cmpFila = b2.getFila().compareTo(b1.getFila());
                    if (cmpFila != 0) return cmpFila;
                    return Integer.compare(Integer.parseInt(b2.getColumna()), Integer.parseInt(b1.getColumna()));
                });
                for (int i = 0; i < aEliminar; i++) {
                    butacasDAO.eliminarButaca(butacas.get(i).getId());
                }
            }
        }
        // Ampliación de capacidad → agregar butacas
        else if (capacidadNueva > capacidadAnterior) {
            int aAgregar = capacidadNueva - butacas.size();

            // Encontrar última fila y columna usadas
            String ultimaFila = "A";
            int ultimaCol = 0;
            for (Butaca b : butacas) {
                if (b.getFila().compareTo(ultimaFila) > 0 ||
                        (b.getFila().equals(ultimaFila) && Integer.parseInt(b.getColumna()) > ultimaCol)) {
                    ultimaFila = b.getFila();
                    ultimaCol = Integer.parseInt(b.getColumna());
                }
            }

            // Determinar número de columnas por capacidad
            int columnas;
            switch (capacidadNueva) {
                case 36 -> columnas = 6;
                case 42 -> columnas = 7;
                case 48 -> columnas = 8;
                default -> throw new Exception("Capacidad no soportada: " + capacidadNueva);
            }

            // Crear nuevas butacas
            char fila = ultimaFila.charAt(0);
            int col = ultimaCol;
            for (int i = 0; i < aAgregar; i++) {
                col++;
                if (col > columnas) {
                    col = 1;
                    fila++;
                }
                Butaca nueva = new Butaca();
                nueva.setIdSala(sala.getId());
                nueva.setFila(String.valueOf(fila));
                nueva.setColumna(String.valueOf(col));
                nueva.setEstado("DISPONIBLE");
                butacasDAO.crearButaca(nueva);
            }
        }

        // Actualizar datos de la sala
        salasDAO.actualizarSala(sala);
    }

    /**
     * Elimina una sala y todas sus butacas asociadas.
     *
     * @param id ID de la sala a eliminar.
     * @throws Exception si ocurre un error SQL.
     */
    public void eliminarSala(int id) throws Exception {
        // 1) Eliminar butacas
        List<Butaca> butacas = butacasDAO.listarButacasPorSala(id);
        for (Butaca b : butacas) {
            butacasDAO.eliminarButaca(b.getId());
        }
        // 2) Eliminar sala
        salasDAO.eliminarSala(id);
    }
}