package com.cinemax.venta_boletos.Controladores;

import com.cinemax.venta_boletos.Modelos.Boleto;
import com.cinemax.venta_boletos.Modelos.Producto;
import com.cinemax.venta_boletos.Modelos.CalculadorIVA;
//import com.cinemax.venta_boletos.Modelos.Factura;
import com.cinemax.venta_boletos.Modelos.Producto;
//import com.cinemax.venta_boletos.Modelos.*;
import com.cinemax.venta_boletos.Util.ThemeManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorResumen {

    @FXML private HBox headerBar;
    @FXML private VBox ticketDetailsContainer;
    @FXML private Label subtotalLabel;
    @FXML private Label impuestosLabel;
    @FXML private Label totalLabel;
    @FXML private CheckBox confirmCheckBox;
    @FXML private Button continueButton;

    private Scene previousScene;
    private String pelicula;
    private String sala;
    private List<Producto> boletos;
    private double xOffset = 0;
    private double yOffset = 0;

    private double subtotal = 0.0;
    private double total = 0.0;
    private double impuesto = 0.0;

    public void setPreviousScene(Scene scene) { this.previousScene = scene; }

    @FXML
    public void initialize() {
        headerBar.setOnMousePressed(event -> { xOffset = event.getSceneX(); yOffset = event.getSceneY(); });
        headerBar.setOnMouseDragged(event -> { ((Stage) headerBar.getScene().getWindow()).setX(event.getScreenX() - xOffset); ((Stage) headerBar.getScene().getWindow()).setY(event.getScreenY() - yOffset); });
    }

    public void initData(String pelicula, String sala, List<Producto> boletos, double subtotal) {
        this.pelicula = pelicula;
        this.sala = sala;
        this.boletos = boletos;
        this.subtotal = subtotal;

        //Factura facturaTemporal = new Factura();
        //facturaTemporal.setProductos(this.boletos);
        //facturaTemporal.calcularSubTotal();
        //facturaTemporal.calcularTotal(new CalculadorIVA());
        impuesto = subtotal * CalculadorIVA.getIVA_TASA();
        total = subtotal + impuesto;

        DecimalFormat df = new DecimalFormat("$ #,##0.00");
        //subtotalLabel.setText(df.format(facturaTemporal.getSubTotal()));
        //impuestosLabel.setText(df.format(facturaTemporal.getTotal() - facturaTemporal.getSubTotal()));
        //totalLabel.setText(df.format(facturaTemporal.getTotal()));
        subtotalLabel.setText(df.format(subtotal));
        impuestosLabel.setText(df.format(impuesto));
        totalLabel.setText(df.format(total));

        construirDetalleTickets();
    }

    private void construirDetalleTickets() {
        ticketDetailsContainer.getChildren().clear();

        String butacasStr = boletos.stream()
                .map(p -> ((Boleto)p).getButaca())
                .collect(Collectors.joining(", "));

        Label tituloTicket = new Label(String.format("Boletos (%d)", boletos.size()));
        tituloTicket.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        ticketDetailsContainer.getChildren().add(tituloTicket);
        ticketDetailsContainer.getChildren().add(crearFilaDetalle("▶", pelicula + ", 2D - Esp"));
        ticketDetailsContainer.getChildren().add(crearFilaDetalle("⚲", sala));
        ticketDetailsContainer.getChildren().add(crearFilaDetalle("💺", "Butacas: " + butacasStr));
    }

    private HBox crearFilaDetalle(String icono, String texto) {
        HBox fila = new HBox(5);
        fila.setAlignment(Pos.CENTER_LEFT);
        Text iconText = new Text(icono);
        iconText.setStyle("-fx-font-size: 14px; -fx-fill: #F25F00;");
        Label lblTexto = new Label(texto);
        fila.getChildren().addAll(iconText, lblTexto);
        return fila;
    }

    @FXML
    protected void onContinuarAction() {
        if (!confirmCheckBox.isSelected()) {
            showAlert("Confirmación Requerida", "Por favor, confirme la compra para continuar.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/datos-cliente-view.fxml"));
            Parent root = loader.load();
            ControladorFacturacion controllerDatosCliente = loader.getController();

            controllerDatosCliente.initData(this.pelicula, this.sala, this.boletos, this.subtotal, this.total, this.impuesto);
            controllerDatosCliente.setPreviousScene(continueButton.getScene());

            Stage stage = (Stage) continueButton.getScene().getWindow();
            Scene scene = new Scene(root);
            ThemeManager.getInstance().applyTheme(scene);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * CORRECCIÓN: Este es el método que faltaba.
     */
    @FXML
    protected void onVerDetalle() {
        System.out.println("Acción para ver detalle del pedido...");
    }

    @FXML protected void onBackAction() { if (previousScene != null) { ((Stage) continueButton.getScene().getWindow()).setScene(previousScene); } }
    @FXML protected void onCloseAction() { ((Stage) headerBar.getScene().getWindow()).close(); }
    @FXML protected void onThemeToggleAction() { ThemeManager.getInstance().toggleTheme(headerBar.getScene()); }

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
