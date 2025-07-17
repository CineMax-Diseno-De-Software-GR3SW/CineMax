package com.cinemax.venta_boletos.Controladores;

import com.cinemax.venta_boletos.Modelos.Cliente;
import com.cinemax.venta_boletos.Modelos.Factura;
import com.cinemax.venta_boletos.Modelos.Producto;
import com.cinemax.venta_boletos.Servicios.ServicioFacturacion;
import com.cinemax.venta_boletos.Util.ThemeManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class ControladorDatosCliente {

    // --- Lógica de Negocio ---
    private final ServicioFacturacion servicioFacturacion = new ServicioFacturacion();

    // --- Estado ---
    private Scene previousScene;
    private List<Producto> boletos;
    private String pelicula;
    private String sala;
    private double xOffset = 0;
    private double yOffset = 0;

    // --- Componentes FXML ---
    @FXML private HBox headerBar;
    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;
    @FXML private ComboBox<String> tipoDocumentoBox;
    @FXML private TextField documentoField;
    @FXML private TextField correoField;
    @FXML private Label subtotalLabel;
    @FXML private Label impuestosLabel;
    @FXML private Label totalLabel;
    @FXML private Button finalizarButton;

    public void setPreviousScene(Scene scene) { this.previousScene = scene; }

    @FXML
    public void initialize() {
        tipoDocumentoBox.setItems(FXCollections.observableArrayList("Cédula de Identidad", "Pasaporte", "RUC"));
        tipoDocumentoBox.setValue("Cédula de Identidad");
        headerBar.setOnMousePressed(event -> { xOffset = event.getSceneX(); yOffset = event.getSceneY(); });
        headerBar.setOnMouseDragged(event -> { ((Stage) headerBar.getScene().getWindow()).setX(event.getScreenX() - xOffset); ((Stage) headerBar.getScene().getWindow()).setY(event.getScreenY() - yOffset); });
    }

    public void initData(String pelicula, String sala, List<Producto> boletos, double subtotal, double total, double impuestos) {
        this.pelicula = pelicula;
        this.sala = sala;
        this.boletos = boletos;

        // Crear una instancia de Factura para usar su lógica de cálculo
        //Factura facturaTemporal = new Factura();
        //facturaTemporal.setProductos(this.boletos);
        //facturaTemporal.calcularSubTotal();
        //facturaTemporal.calcularTotal(new CalculadorIVA());

        DecimalFormat df = new DecimalFormat("$ #,##0.00");

        subtotalLabel.setText(df.format(subtotal));
        impuestosLabel.setText(df.format(impuestos));
        totalLabel.setText(df.format(total));

        
    }

    @FXML
    protected void onFinalizarAction() {
        if (nombreField.getText().isEmpty() || apellidoField.getText().isEmpty() || documentoField.getText().isEmpty() || correoField.getText().isEmpty()) {
            showAlert("Campo Incompleto", "Por favor, llene todos los campos para continuar.");
            return;
        }

        // 1. Crear el modelo Cliente con los datos del formulario
        Cliente cliente = new Cliente(
                nombreField.getText(),
                apellidoField.getText(),
                documentoField.getText(),
                correoField.getText()
        );

        // TODO: dao debe guardar el cliente

        // 2. Usar tu servicio para generar la factura final
        Factura facturaFinal = servicioFacturacion.generarFactura(this.boletos, cliente);
        //TODO: Dao debe guardar la factura

        // 3. Mostrar un mensaje de éxito y cerrar
        showAlert("Compra Exitosa", "Se ha generado la factura: " + facturaFinal.getCodigoFactura());

        System.out.println("--- FACTURA GENERADA ---");
        System.out.println(facturaFinal);

        Stage stage = (Stage) finalizarButton.getScene().getWindow();
        stage.close();
        // TODO: En vez de cerrar, redirigir a una vista de cartelera
    }

    @FXML protected void onBackAction() { if (previousScene != null) { ((Stage) finalizarButton.getScene().getWindow()).setScene(previousScene); } }
    @FXML protected void onCloseAction() { ((Stage) headerBar.getScene().getWindow()).close(); }
    @FXML protected void onThemeToggleAction() { ThemeManager.getInstance().toggleTheme(headerBar.getScene()); }
    @FXML protected void onVerDetalle() { System.out.println("Acción para ver detalle del pedido..."); }

    private void showAlert(String title, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/shared/alert-view.fxml"));
            Parent root = loader.load();
            ControllerAlert controller = loader.getController();
            controller.setData(title, message);
            Stage alertStage = new Stage();
            alertStage.initOwner(finalizarButton.getScene().getWindow());
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
