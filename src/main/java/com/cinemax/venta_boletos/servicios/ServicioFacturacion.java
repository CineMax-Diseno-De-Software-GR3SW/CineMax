package com.cinemax.venta_boletos.servicios;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.comun.ServicioCorreoSingleton;
import com.cinemax.empleados.servicios.ContenidoMensaje;
import com.cinemax.venta_boletos.modelos.entidades.Boleto;
import com.cinemax.venta_boletos.modelos.entidades.CalculadorImpuesto;
import com.cinemax.venta_boletos.modelos.entidades.Cliente;
import com.cinemax.venta_boletos.modelos.entidades.Factura;
import com.cinemax.venta_boletos.modelos.entidades.Producto;
import com.cinemax.venta_boletos.modelos.persistencia.BoletoDAO;
import com.cinemax.venta_boletos.modelos.persistencia.FacturaDAO;


public class ServicioFacturacion {

    private FacturaDAO facturaDAO = new FacturaDAO();
    private BoletoDAO boletoDAO = new BoletoDAO();

    public void generarFactura(List<Producto> productos, Cliente cliente, CalculadorImpuesto calculadorImpuesto) {
        Factura factura = new Factura(
                generarCodigoFactura(),
                getFechaActual(),
                cliente);
        factura.setProductos(productos);
        factura.calcularSubTotal();
        factura.calcularTotal(calculadorImpuesto);

        try {
            // Guardar la factura en la base de datos.
            facturaDAO.crearFactura(factura);
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al crear la factura: " + e.getMessage());
            e.printStackTrace();
        }

        // Guardar los boletos asociados a la factura en la base de datos.
        for (Producto boleto : productos) {
            try {
                boletoDAO.crearBoleto((Boleto) boleto, factura);
            } catch (Exception e) {
                ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al crear el boleto: " + e.getMessage());
                e.printStackTrace();
                return; 
            }
        }

        ServicioGeneradorArchivo generador = new ServicioGeneradorArchivoPDF();
        generador.generarFactura(factura);

        // Mostrar un mensaje de éxito al usuario indicando que la factura se ha creado exitosamente.
        ManejadorMetodosComunes.mostrarVentanaExito("Factura creada exitosamente: " + factura.getCodigoFactura());

        // Enviar el PDF al correo del cliente usando ServicioCorreoSingleton
        try {
            ServicioCorreoSingleton correo = ServicioCorreoSingleton.getInstancia();
            ContenidoMensaje contenido = ServicioContenidoMensajeFactura.crearMensajeFactura(factura);
            // Construir la ruta del archivo PDF generado
            String rutaPDF = "PDFsGenerados_BoletoFactura/FacturasGeneradas/Factura_" + factura.getCodigoFactura() + ".pdf";
            File archivoPDF = new File(rutaPDF);
            correo.enviarCorreo(cliente.getCorreoElectronico(), contenido, archivoPDF);
        } catch (Exception e) {
            e.printStackTrace();
            // Manejo de error si falla el envío de correo
        }
    }

    /**
     * Genera un código único para la factura
     */
    private long generarCodigoFactura() {
        return System.currentTimeMillis(); // Solo el número
    }

    /**
     * Obtiene la fecha actual formateada
     */
    private String getFechaActual() {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
    }

}
