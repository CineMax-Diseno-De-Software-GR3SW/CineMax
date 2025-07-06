package com.cinemax.moduloboletos.Controladores.VentaDeBoletos;

import java.util.List;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.*;
import com.cinemax.moduloboletos.Servicios.VentaDeBoletos.*;

public class ControladorVentaDeBoletos {

    private ControladorAsignadorButacas controladorAsignadorButacas;
    private ControladorAsignadorFuncion controladorAsignadorFuncion;
    private ControladorBoleto controladorBoleto;
    private ControladorFacturacion controladorFacturacion;
    private ServicioGeneradorArchivo generadorPDF; // Nuevo: Servicio de PDF

    public void gestionarVentaDeBoletos(String controladorCartelera, String controladorDeConsultaSalas) {
        System.out.println("Módulo de Venta de Boletos iniciado.\n");

        // Inicializar controladores
        controladorAsignadorButacas = new ControladorAsignadorButacas();
        controladorAsignadorFuncion = new ControladorAsignadorFuncion();
        controladorBoleto = new ControladorBoleto();
        controladorFacturacion = new ControladorFacturacion();
        generadorPDF = new GeneradorArchivoPDF(); // Inicializar generador de PDF

        // Datos quemados (simulados)
        String funcion = controladorAsignadorFuncion.asignarFuncion(controladorCartelera); // Ej: "Avengers - 20:00"
        List<String> butacas = controladorAsignadorButacas.asignarButacas(controladorDeConsultaSalas, funcion); // Ej:
                                                                                                                // ["A1",
                                                                                                                // "A2"]
        List<Producto> boletos = controladorBoleto.generarBoleto(funcion, butacas);
        Cliente cliente = new Cliente("Juan", "Pérez", "12345678", "juan.perez@example.com"); // Cliente quemado

        // Generar factura
        Factura factura = controladorFacturacion.generarFactura(boletos, cliente);

        // Generar PDFs (nueva funcionalidad)
        generadorPDF.generarFacturaPDF(factura);
        generadorPDF.generarBoletosPDF((List<Boleto>) (List<?>) boletos); // Cast seguro por datos quemados

        // Salida por consola (original)
        System.out.println("Factura generada:");
        System.out.println(factura);
        System.out.println("\nPDFs generados en la raíz del proyecto:");
        System.out.println("- Factura_" + factura.getCodigoFactura() + ".pdf");
        butacas.forEach(butaca -> System.out.println("- Boleto_" + butaca + ".pdf"));
    }
}
