package com.cinemax.venta_boletos.Controladores;

import com.cinemax.venta_boletos.Modelos.Producto;
import com.cinemax.venta_boletos.Servicios.ServicioGeneradorBoleto;
import com.cinemax.comun.ApuntadorTema;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ControladorBoleto {

    // --- Lógica de Negocio ---
    private final ServicioGeneradorBoleto servicioBoleto = new ServicioGeneradorBoleto();

    // --- Estado de la Vista ---
    private final int MAX_BOLETOS = 10; //TODO: Debería depender de la cantidad de butacas disponibles en la sala
    private int boletosSalaVIP = 0;
    private int boletosSalaNormal = 0;
    private double xOffset = 0;
    private double yOffset = 0;
    private String pelicula;
    private String funcion;

    private List<String> butacasOcupadas = new ArrayList<>(); // Lista de butacas ocupadas
    private double subtotal = 0.0;
    // TODO: Son datos que deben ser entregados por el modulo sala
    private double precioSalaVIP = 7.60;
    private double precioSalaNormal = 3.00;


    // --- Componentes FXML ---
    @FXML private HBox headerBar;
    @FXML private Label vipCountLabel;
    @FXML private Label normalCountLabel;
    @FXML private Label peliculaLabel;
    @FXML private Label salaLabel;
    @FXML private Label totalLabel;
    @FXML private Button continueButton;
    @FXML private Text tusBoletosTitle;
    @FXML private VBox ticketSummaryContainer;

    public void initData(String pelicula, String funcion) {
        this.pelicula = pelicula;
        this.funcion = funcion;
        peliculaLabel.setText(this.pelicula);
        salaLabel.setText(this.funcion);
    }

    @FXML
    public void initialize() {
        actualizarVista();
        headerBar.setOnMousePressed(event -> { xOffset = event.getSceneX(); yOffset = event.getSceneY(); });
        headerBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) headerBar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    @FXML private void onVipPlus() { if (boletosSalaVIP < MAX_BOLETOS) boletosSalaVIP++; actualizarVista(); }
    @FXML private void onVipMinus() { if (boletosSalaVIP > 0) boletosSalaVIP--; actualizarVista(); }
    @FXML private void onNormalPlus() { if (boletosSalaNormal < MAX_BOLETOS) boletosSalaNormal++; actualizarVista(); }
    @FXML private void onNormalMinus() { if (boletosSalaNormal > 0) boletosSalaNormal--; actualizarVista(); }

    private void actualizarVista() {
        vipCountLabel.setText(String.valueOf(boletosSalaVIP));
        normalCountLabel.setText(String.valueOf(boletosSalaNormal));
        actualizarResumenDinamico();
    }

    private void actualizarResumenDinamico() {
        ticketSummaryContainer.getChildren().clear();
        boolean hayBoletos = boletosSalaVIP > 0 || boletosSalaNormal > 0;
        tusBoletosTitle.setVisible(hayBoletos);
        tusBoletosTitle.setManaged(hayBoletos);

        
        // Se usa un boleto temporal para obtener el precio base del modelo.
        //double precioUnitario = new com.cinemax.venta_boletos.Modelos.Boleto("", "").getPrecio();
       
        subtotal = 0.0;

        if (boletosSalaVIP > 0) {
            subtotal += boletosSalaVIP * precioSalaVIP;
            ticketSummaryContainer.getChildren().add(crearFilaResumen("Sala 2D VIP", boletosSalaVIP, precioSalaVIP));
        }
        if (boletosSalaNormal > 0) {
            subtotal += boletosSalaNormal * precioSalaNormal;
            ticketSummaryContainer.getChildren().add(crearFilaResumen("Sala 2D Normal", boletosSalaNormal, precioSalaNormal));
        }

        DecimalFormat df = new DecimalFormat("$ #,##0.00");
        totalLabel.setText(df.format(subtotal));

        /*
        TODO: Buscar boletos en la BD usando la "función" para encontrar todos los boletos asociados. De ahí, extraer en una estructura de datos, los códigos alfanuméricos de las butacas asociadas a esos boletos. Esa estructura de datos servirá para saber la cantidad de asientos disponibles y para mostrar cuáles están ocupados en la pantalla de asignación de butacas. 
        */
        // butacasOcupadas = daoBoleto.buscarButacasOcupadasEnBoletosPorFuncion(funcion)
        // disponibilidadDeButacas = funcion.getSala().getButacas().len - butacasOcupadas.size();
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
        //System.out.println(fila);
        return fila;
    }

    @FXML
    protected void onContinuarAction() {
        if (boletosSalaVIP == 0 && boletosSalaNormal == 0) {
            showAlert("Sin Selección", "Por favor, selecciona al menos un boleto para continuar.");
            return;
        }
        try {
            // 1. Simular asignación de butacas
            //List<String> butacas = new ArrayList<>();
            int totalBoletos = boletosSalaVIP + boletosSalaNormal;
            String controladoDeConsultasSalas = "Controlador de Consultas de Salas"; // Simulación
            ControladorAsignadorButacas controladorAsignadorButacas = new ControladorAsignadorButacas();
            List<String> butacasAsignadas = controladorAsignadorButacas.asignarButacas(controladoDeConsultasSalas, funcion, butacasOcupadas, totalBoletos);

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
            ApuntadorTema.getInstance().applyTheme(scene);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML protected void onCloseAction() { ((Stage) headerBar.getScene().getWindow()).close(); }
    @FXML protected void onThemeToggleAction() { ApuntadorTema.getInstance().applyTheme(headerBar.getScene()); }

    // Método de alerta local (hasta que implementes AlertManager)
    private void showAlert(String title, String message) {
        try {
             //TODO: ¿Dónde exactamente debería estar esto?, ¿Por qué está en la carpeta shared, es porque debe ser un archivo compartido entre todo el curso como el singleton?
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cinemax/venta_boletos/Vistas/Shared/alert-view.fxml"));
            Parent root = loader.load();
            ControllerAlert controller = loader.getController();
            controller.setData(title, message);
            Stage alertStage = new Stage();
            alertStage.initOwner(continueButton.getScene().getWindow());
            alertStage.initStyle(StageStyle.TRANSPARENT);
            alertStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            scene.setFill(null);
            ApuntadorTema.getInstance().applyTheme(scene);
            alertStage.setScene(scene);
            alertStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
