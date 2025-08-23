package com.cinemax.venta_boletos.servicios;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.cinemax.empleados.servicios.ContenidoMensaje;
import com.cinemax.utilidades.ManejadorMetodosComunes;
import com.cinemax.utilidades.ServicioCorreoSingleton;
import com.cinemax.venta_boletos.modelos.entidades.Boleto;
import com.cinemax.venta_boletos.modelos.entidades.CalculadorImpuesto;
import com.cinemax.venta_boletos.modelos.entidades.Cliente;
import com.cinemax.venta_boletos.modelos.entidades.Factura;
import com.cinemax.venta_boletos.modelos.entidades.Producto;
import com.cinemax.venta_boletos.modelos.persistencia.BoletoDAO;
import com.cinemax.venta_boletos.modelos.persistencia.FacturaDAO;


public class ServicioFacturacion {

    private FacturaDAO facturaDAO;
    private BoletoDAO boletoDAO;

    public ServicioFacturacion() {
        this.facturaDAO = new FacturaDAO();
        this.boletoDAO = new BoletoDAO();
    }

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
            ManejadorMetodosComunes.mostrarVentanaError("1. Sucedió algo inesperado al crear la factura: " + e.getMessage());
            System.out.println(e.getMessage());
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

        ServicioContenidoFactura generador = new ServicioContenidoFactura();
        generador.generarFactura(factura);
        generador.generarBoletos(productos);

        // Buscar los archivos PDF de los boletos generados para esta factura
        // Se asume que los nombres de los boletos contienen el código de la factura o se generan todos los boletos en la carpeta correspondiente
        List<File> archivosBoletos = new java.util.ArrayList<>();
        String carpetaBoletos = "PDFsGenerados_BoletoFactura/BoletosGenerados/";
        for (Producto producto : productos) {
            if (producto instanceof Boleto) {
                Boleto boleto = (Boleto) producto;
                String nombreArchivo = generador.generarNombreArchivoBoleto(boleto);
                File archivoBoleto = new File(carpetaBoletos + nombreArchivo);
                if (archivoBoleto.exists()) {
                    archivosBoletos.add(archivoBoleto);
                }
            }
        }

        // Unir la factura y los boletos en un solo PDF
        String rutaFactura = "PDFsGenerados_BoletoFactura/FacturasGeneradas/Factura_" + factura.getCodigoFactura() + ".pdf";
        File archivoFactura = new File(rutaFactura);
        String nombreCombinado = "FacturaYBoletos_" + factura.getCodigoFactura() + ".pdf";
        String rutaCombinada = "PDFsGenerados_BoletoFactura/FacturasGeneradas/" + nombreCombinado;
        File archivoCombinado = new File(rutaCombinada);
        try {
            generador.unirPDFsFacturaYBoletos(archivoFactura, archivosBoletos, archivoCombinado);
        } catch (Exception e) {
            e.printStackTrace();
            // Si falla la unión, se puede seguir enviando solo la factura
        }

        // Mostrar un mensaje de éxito al usuario indicando que la factura se ha creado exitosamente.
        ManejadorMetodosComunes.mostrarVentanaExito("Factura creada exitosamente: " + factura.getCodigoFactura());

        // Enviar el PDF combinado al correo del cliente usando ServicioCorreoSingleton
        try {
            ServicioCorreoSingleton correo = ServicioCorreoSingleton.getInstancia();
            ContenidoMensaje contenido = ServicioContenidoMensajeFactura.crearMensajeFactura(factura);
            correo.enviarCorreo(cliente.getCorreoElectronico(), contenido, archivoCombinado);
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
