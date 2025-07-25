package com.cinemax.venta_boletos.Controladores;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.venta_boletos.Modelos.Producto;
import com.cinemax.venta_boletos.Servicios.ServicioGeneradorBoleto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ControladorBoleto {

    // --- Lógica de Negocio ---
    private final ServicioGeneradorBoleto servicioBoleto = new ServicioGeneradorBoleto();

    // --- Estado de la Vista ---
    private final int MAX_BOLETOS = 10; // TODO: Debería depender de la cantidad de butacas disponibles en la sala
    private int boletosSalaNormal = 0;
    private double xOffset = 0;
    private double yOffset = 0;
    private String pelicula;
    private String funcion;

    private List<String> butacasOcupadas = new ArrayList<>(); // Lista de butacas ocupadas
    private double subtotal = 0.0;
    // TODO: Son datos que deben ser entregados por el modulo sala
    private double precioSalaNormal = 3.00;

    // --- Componentes FXML ---
    @FXML
    private HBox headerBar;
    @FXML
    private Label normalCountLabel;
    @FXML
    private Label peliculaLabel;
    @FXML
    private Label salaLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Button continueButton;
    @FXML
    private Text tusBoletosTitle;
    @FXML
    private VBox ticketSummaryContainer;
    @FXML
    private Label peliculaTitleLabel;
    @FXML
    private Label funcionDetailsLabel;


    /**
     * Método llamado por el controlador anterior para
     * pasarle la información de la película y la función seleccionadas.
     * @param pelicula El nombre de la película.
     * @param funcion La información de la sala y el horario.
     */
    public void initData(String pelicula, String funcion) {
        this.pelicula = pelicula;
        this.funcion = funcion;

        this.peliculaTitleLabel.setText(pelicula);
        this.funcionDetailsLabel.setText(funcion);
        this.peliculaLabel.setText(pelicula);
        this.salaLabel.setText(funcion);
    }

    @FXML
    public void initialize() {
        actualizarVista();
        headerBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        headerBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) headerBar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    @FXML
    private void onNormalPlus() {
        if (boletosSalaNormal < MAX_BOLETOS)
            boletosSalaNormal++;
        actualizarVista();
    }

    @FXML
    private void onNormalMinus() {
        if (boletosSalaNormal > 0)
            boletosSalaNormal--;
        actualizarVista();
    }

    private void actualizarVista() {
        if (normalCountLabel != null) { // Comprobación para evitar errores
            normalCountLabel.setText(String.valueOf(boletosSalaNormal));
            double total = boletosSalaNormal * precioSalaNormal;
            DecimalFormat df = new DecimalFormat("$ #,##0.00");
            totalLabel.setText(df.format(total));

            actualizarResumenDinamico();
        }
    }

    /**
     * Muestra u oculta la sección "Tus Boletos" y actualiza su contenido
     * basado en si se han seleccionado boletos o no.
     */
    private void actualizarResumenDinamico() {
        ticketSummaryContainer.getChildren().clear();
        boolean hayBoletos = boletosSalaNormal > 0;

        // Controlar visibilidad de toda la sección de resumen
        tusBoletosTitle.setVisible(hayBoletos);
        tusBoletosTitle.setManaged(hayBoletos);
        totalLabel.setVisible(hayBoletos);
        totalLabel.setManaged(hayBoletos);
        ticketSummaryContainer.setVisible(hayBoletos);
        ticketSummaryContainer.setManaged(hayBoletos);

        // Si hay boletos, crear y añadir la fila de resumen
        if (hayBoletos) {
            ticketSummaryContainer.getChildren().add(crearFilaResumen(this.funcion, boletosSalaNormal, precioSalaNormal));
        }

    /*
         * TODO: Buscar boletos en la BD usando la "función" para encontrar todos los
         * boletos asociados. De ahí, extraer en una estructura de datos, los códigos
         * alfanuméricos de las butacas asociadas a esos boletos. Esa estructura de
         * datos servirá para saber la cantidad de asientos disponibles y para mostrar
         * cuáles están ocupados en la pantalla de asignación de butacas.
         */
        // butacasOcupadas = daoBoleto.buscarButacasOcupadasEnBoletosPorFuncion(funcion)
        // disponibilidadDeButacas = funcion.getSala().getButacas().len -
        // butacasOcupadas.size();
    }

    private HBox crearFilaResumen(String nombreBoleto, int cantidad, double precioUnitario) {
        HBox fila = new HBox();
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setSpacing(10.0);
        Label lblCantidad = new Label(String.valueOf(cantidad));
        lblCantidad.getStyleClass().add("summary-ticket-count");
        Label lblNombre = new Label(nombreBoleto);
        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);
        DecimalFormat df = new DecimalFormat("$ #,##0.00");
        Label lblPrecio = new Label(df.format(cantidad * precioUnitario));
        lblPrecio.getStyleClass().add("summary-price");
        fila.getChildren().addAll(lblCantidad, lblNombre, espaciador, lblPrecio);
        // System.out.println(fila);
        return fila;
    }

    @FXML
    protected void onContinuarAction() {
        if (boletosSalaNormal == 0) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Selecciona al menos un boleto para continuar.");
            return;
        }
        try {
            // 1. Simular asignación de butacas
            // List<String> butacas = new ArrayList<>();
            int totalBoletos = boletosSalaNormal; //TODO Revisar si es correcto
            String controladoDeConsultasSalas = "Controlador de Consultas de Salas"; // Simulación
            ControladorAsignadorButacas controladorAsignadorButacas = new ControladorAsignadorButacas();
            List<String> butacasAsignadas = controladorAsignadorButacas.asignarButacas(controladoDeConsultasSalas,
                    funcion, butacasOcupadas, totalBoletos);

            // 2. Usar servicio para generar los boletos reales
            List<Producto> boletosGenerados = servicioBoleto.generarBoleto(this.funcion, butacasAsignadas);

            // 3. Cargar la siguiente pantalla y pasarle los boletos
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/resumen-view.fxml"));
            Parent root = loader.load();
            ControladorResumen controllerResumen = loader.getController();

            controllerResumen.initData(this.pelicula, this.funcion, boletosGenerados, subtotal);
            controllerResumen.setPreviousScene(continueButton.getScene());

            Stage stage = (Stage) continueButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void onBackAction() {
        try {
            String fxmlPath = "/vistas/venta_boletos/cartelera-view.fxml";
            URL fxmlUrl = getClass().getResource(fxmlPath);

            if (fxmlUrl == null) {
                System.err.println("Error crítico: No se pudo encontrar el archivo FXML en la ruta: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root);
//            ApuntadorTema.getInstance().applyTheme(scene);

            Stage stage = (Stage) headerBar.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error de I/O al cargar la vista de funciones:");
            e.printStackTrace();
        }
    }

    @FXML
    protected void onCloseAction() {
        ((Stage) headerBar.getScene().getWindow()).close();
    }
}
