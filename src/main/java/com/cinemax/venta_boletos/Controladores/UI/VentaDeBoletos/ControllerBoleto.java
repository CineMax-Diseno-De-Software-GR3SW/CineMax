package com.cinemax.venta_boletos.Controladores.UI.VentaDeBoletos;

import com.cinemax.venta_boletos.Controladores.UI.Shared.ControllerAlert;
import com.cinemax.venta_boletos.Modelos.VentaDeBoletos.Producto;
import com.cinemax.venta_boletos.Servicios.VentaDeBoletos.ServicioGeneradorBoleto;
import com.cinemax.venta_boletos.Util.ThemeManager;
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

public class ControllerBoleto {

    // --- Lógica de Negocio ---
    private final ServicioGeneradorBoleto servicioBoleto = new ServicioGeneradorBoleto();

    // --- Estado de la Vista ---
    private final int MAX_BOLETOS = 10;
    private int vipTickets = 0;
    private int normalTickets = 0;
    private double xOffset = 0;
    private double yOffset = 0;
    private String pelicula;
    private String funcion;

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

    @FXML private void onVipPlus() { if (vipTickets < MAX_BOLETOS) vipTickets++; actualizarVista(); }
    @FXML private void onVipMinus() { if (vipTickets > 0) vipTickets--; actualizarVista(); }
    @FXML private void onNormalPlus() { if (normalTickets < MAX_BOLETOS) normalTickets++; actualizarVista(); }
    @FXML private void onNormalMinus() { if (normalTickets > 0) normalTickets--; actualizarVista(); }

    private void actualizarVista() {
        vipCountLabel.setText(String.valueOf(vipTickets));
        normalCountLabel.setText(String.valueOf(normalTickets));
        actualizarResumenDinamico();
    }

    private void actualizarResumenDinamico() {
        ticketSummaryContainer.getChildren().clear();
        boolean hayBoletos = vipTickets > 0 || normalTickets > 0;
        tusBoletosTitle.setVisible(hayBoletos);
        tusBoletosTitle.setManaged(hayBoletos);

        double total = 0;
        // Se usa un boleto temporal para obtener el precio base del modelo.
        double precioUnitario = new com.cinemax.venta_boletos.Modelos.VentaDeBoletos.Boleto("", "").getPrecio();

        if (vipTickets > 0) {
            total += vipTickets * precioUnitario;
            ticketSummaryContainer.getChildren().add(crearFilaResumen("Boleto 2D VIP", vipTickets, precioUnitario));
        }
        if (normalTickets > 0) {
            total += normalTickets * precioUnitario;
            ticketSummaryContainer.getChildren().add(crearFilaResumen("Boleto 2D Normal", normalTickets, precioUnitario));
        }

        DecimalFormat df = new DecimalFormat("$ #,##0.00");
        totalLabel.setText(df.format(total));
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
        return fila;
    }

    @FXML
    protected void onContinuarAction() {
        if (vipTickets == 0 && normalTickets == 0) {
            showAlert("Sin Selección", "Por favor, selecciona al menos un boleto para continuar.");
            return;
        }
        try {
            // 1. Simular asignación de butacas
            List<String> butacas = new ArrayList<>();
            int totalBoletos = vipTickets + normalTickets;
            for (int i = 1; i <= totalBoletos; i++) butacas.add("F" + i);

            // 2. Usar tu servicio para generar los boletos reales
            List<Producto> boletosGenerados = servicioBoleto.generarBoleto(this.funcion, butacas);

            // 3. Cargar la siguiente pantalla y pasarle los boletos
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/resumen-view.fxml"));
            Parent root = loader.load();
            ControllerResumen controllerResumen = loader.getController();

            controllerResumen.initData(this.pelicula, this.funcion, boletosGenerados);
            controllerResumen.setPreviousScene(continueButton.getScene());

            Stage stage = (Stage) continueButton.getScene().getWindow();
            Scene scene = new Scene(root);
            ThemeManager.getInstance().applyTheme(scene);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML protected void onCloseAction() { ((Stage) headerBar.getScene().getWindow()).close(); }
    @FXML protected void onThemeToggleAction() { ThemeManager.getInstance().toggleTheme(headerBar.getScene()); }

    // Método de alerta local (hasta que implementes AlertManager)
    private void showAlert(String title, String message) {
        try {
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
            ThemeManager.getInstance().applyTheme(scene);
            alertStage.setScene(scene);
            alertStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
