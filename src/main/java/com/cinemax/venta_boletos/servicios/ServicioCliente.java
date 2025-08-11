package com.cinemax.venta_boletos.servicios;

import com.cinemax.utilidades.ManejadorMetodosComunes;
import com.cinemax.venta_boletos.modelos.entidades.Cliente;
import com.cinemax.venta_boletos.modelos.persistencia.ClienteDAO;

public class ServicioCliente {

    private ClienteDAO clienteDAO;

    public ServicioCliente() {
        this.clienteDAO = new ClienteDAO();
    }

    public Cliente buscarCliente (String numeroIdentificacion) {
        try {
            return clienteDAO.buscarPorId(numeroIdentificacion);
        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al buscar al cliente.");
        }
        return null;
    }

    public void crearCliente(Cliente cliente) {
        try {
            clienteDAO.crearCliente(cliente);
        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al crear al cliente.");
        }
    }

    public void actualizarCliente(Cliente cliente) {
        try {
            clienteDAO.actualizarCliente(cliente);
        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al actualizar al cliente.");
        }
    }

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