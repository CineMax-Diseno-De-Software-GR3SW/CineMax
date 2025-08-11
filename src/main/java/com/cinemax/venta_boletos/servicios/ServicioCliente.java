package com.cinemax.venta_boletos.servicios;

import com.cinemax.utilidades.ManejadorMetodosComunes;
import com.cinemax.venta_boletos.modelos.entidades.Cliente;
import com.cinemax.venta_boletos.modelos.persistencia.ClienteDAO;

/**
 * Esta clase implementa la lógica de negocio para la gestión de clientes.
 * 
 * @author GR3SW
 * @version 1.0
 */
public class ServicioCliente {

    // Atributo para acceder a la capa de persistencia de clientes.
    private ClienteDAO clienteDAO;

    public ServicioCliente() {
        this.clienteDAO = new ClienteDAO();
    }

    /**
     * Método para crear un nuevo cliente.
     * 
     * @param cliente El cliente a crear.
     */
    public void crearCliente(Cliente cliente) {
        try {
            clienteDAO.crearCliente(cliente);
        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al crear al cliente.");
        }
    }

    /**
     * Método para actualizar un cliente existente.
     * 
     * @param cliente El cliente a actualizar.
     */
    public void actualizarCliente(Cliente cliente) {
        try {
            clienteDAO.actualizarCliente(cliente);
        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al actualizar al cliente.");
        }
    }

    /**
     * Método para buscar un cliente por su número de identificación.
     * 
     * @param numeroIdentificacion El número de identificación del cliente a buscar.
     * @return El cliente encontrado o null si no se encuentra.
     */
    public Cliente buscarCliente (String numeroIdentificacion) {
        try {
            return clienteDAO.buscarPorId(numeroIdentificacion);
        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al buscar al cliente.");
        }
        return null;
    }

    /**
     * Método para verificar si un cliente existe.
     * 
     * @param numeroIdentificacion El número de identificación del cliente a verificar.
     * @return true si el cliente existe, false en caso contrario.
     */
    public boolean existeCliente(String numeroIdentificacion) {
        try {
            Cliente cliente = clienteDAO.buscarPorId(numeroIdentificacion);
            return cliente != null;
        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al verificar la existencia del cliente.");
        }
        return false;
    }
}