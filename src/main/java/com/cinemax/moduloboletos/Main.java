package com.cinemax.moduloboletos;

import java.io.IOException;
import java.util.List;

import com.cinemax.moduloboletos.Controladores.ControladorCineMax;
import com.cinemax.moduloboletos.Controladores.UI.VentaDeBoletos.ControllerBoleto;
import com.cinemax.moduloboletos.Modelos.CineMax;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Boleto;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.CalculadorIVA;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.CalculadorImpuesto;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Cliente;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Factura;
import com.cinemax.moduloboletos.Modelos.VentaDeBoletos.Producto;
import com.cinemax.moduloboletos.Servicios.VentaDeBoletos.GeneradorArchivoPDF;
import com.cinemax.moduloboletos.Servicios.VentaDeBoletos.ServicioFacturacion;
import com.cinemax.moduloboletos.Servicios.VentaDeBoletos.ServicioGeneradorArchivo;
import com.cinemax.moduloboletos.Servicios.VentaDeBoletos.ServicioGeneradorBoleto;
import com.cinemax.moduloboletos.Util.ThemeManager;
import com.cinemax.moduloboletos.Vistas.VistaCineMax;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    // --- Campos estáticos para guardar los datos iniciales ---
    private static String initialPelicula;
    private static String initialFuncion;

    // --- PARTE DE JAVAFX ---
    @Override
    public void start(Stage stage) throws IOException {
        stage.initStyle(StageStyle.UNDECORATED);

        // CORRECCIÓN: La ruta debe ser absoluta desde la raíz de los recursos.
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/com/cinemax/moduloboletos/Vistas/VentaDeBoletos/boleto-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Pasar los datos guardados al primer controlador de la GUI
        ControllerBoleto controller = fxmlLoader.getController();
        controller.initData(initialPelicula, initialFuncion);

        ThemeManager.getInstance().applyTheme(scene);

        stage.setTitle("CINE MAX - Venta de Boletos");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    /**
     * Este es el nuevo método que llamará el ControladorCineMax.
     * Guarda los datos y luego lanza la aplicación.
     */
    public static void launchWithData(String pelicula, String funcion) {
        initialPelicula = pelicula;
        initialFuncion = funcion;
        // Llama al método launch() de Application, que eventualmente llamará a start()
        launch();
    }

    // --- PARTE DE LA CONSOLA ---
    // Este es el punto de entrada principal de TODA la aplicación.
    public static void main(String[] args) {
        pruebaGeneracionPDFs(); // Llama a la prueba de generación de PDFs

        // Inicia la lógica de la aplicación de consola.
        VistaCineMax vistaCineMax = new VistaCineMax();
        CineMax cineMax = new CineMax();
        ControladorCineMax controladorCineMax = new ControladorCineMax(cineMax, vistaCineMax);

        controladorCineMax.mostrarPaginaPrincipal();
    }

    private static void pruebaGeneracionPDFs() {
        try {
            // Datos quemados para prueba
            String funcion = "Avengers: Endgame - Sala 3D - 20:00";
            List<String> butacas = List.of("A1", "A2");
            Cliente cliente = new Cliente("Juan", "Pérez", "12345678", "juan@example.com");

            // Generar boletos
            ServicioGeneradorBoleto generadorBoleto = new ServicioGeneradorBoleto();
            List<Producto> boletos = generadorBoleto.generarBoleto(funcion, butacas);

            // Generar factura
            ServicioFacturacion servicioFacturacion = new ServicioFacturacion();
            Factura factura = servicioFacturacion.generarFactura(boletos, cliente);

            // Generar PDFs
            ServicioGeneradorArchivo generadorPDF = new GeneradorArchivoPDF();
            generadorPDF.generarFacturaPDF(factura);
            generadorPDF.generarBoletosPDF((List<Boleto>) (List<?>) boletos);

            System.out.println("\n[Prueba PDF] Archivos generados:");
            System.out.println("- Factura_" + factura.getCodigoFactura() + ".pdf");
            System.out.println("- Boleto_A1.pdf");
            System.out.println("- Boleto_A2.pdf");

        } catch (Exception e) {
            System.err.println("Error en prueba de PDFs: " + e.getMessage());
        }
    }
}
