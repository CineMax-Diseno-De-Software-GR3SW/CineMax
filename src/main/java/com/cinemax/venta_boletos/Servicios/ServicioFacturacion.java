package com.cinemax.venta_boletos.servicios;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.cinemax.comun.ServicioCorreoSingleton;
import com.cinemax.empleados.servicios.ContenidoMensaje;
import com.cinemax.venta_boletos.modelos.entidades.CalculadorIVA;
import com.cinemax.venta_boletos.modelos.entidades.CalculadorImpuesto;
import com.cinemax.venta_boletos.modelos.entidades.Cliente;
import com.cinemax.venta_boletos.modelos.entidades.Factura;
import com.cinemax.venta_boletos.modelos.entidades.Producto;


public class ServicioFacturacion {

    CalculadorImpuesto calculadorImpuesto;

    public ServicioFacturacion() {
        this.calculadorImpuesto = new CalculadorIVA();
    }

    public Factura generarFactura(List<Producto> productos, Cliente cliente) {
        Factura factura = new Factura(
                generarCodigoFactura(),
                getFechaActual(),
                cliente);
        factura.setProductos(productos);
        factura.calcularSubTotal();
        factura.calcularTotal(calculadorImpuesto);

        ServicioGeneradorArchivo generador = new ServicioGeneradorArchivoPDF();
        generador.generarFacturaPDF(factura);

        // Enviar el PDF al correo del cliente usando ServicioCorreoSingleton
        try {
            ServicioCorreoSingleton correo = ServicioCorreoSingleton.getInstancia();
            ContenidoMensaje contenido = ServicioContenidoMensajeFactura.crear(factura);
            // Construir la ruta del archivo PDF generado
            String rutaPDF = "PDFsGenerados_BoletoFactura/FacturasGeneradas/Factura_" + factura.getCodigoFactura() + ".pdf";
            File archivoPDF = new File(rutaPDF);
            correo.enviarCorreo(cliente.getCorreoElectronico(), contenido, archivoPDF);
        } catch (Exception e) {
            e.printStackTrace();
            // Manejo de error si falla el envío de correo
        }

        return factura;
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
