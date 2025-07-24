package com.cinemax.venta_boletos.Controladores;

import com.cinemax.venta_boletos.Modelos.Cliente;
import com.cinemax.venta_boletos.Modelos.Factura;
import com.cinemax.venta_boletos.Modelos.Producto;
import com.cinemax.venta_boletos.Modelos.Persistencia.ClienteDAO;
import com.cinemax.venta_boletos.Servicios.ServicioFacturacion;
import com.cinemax.comun.ApuntadorTema;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class ControladorFacturacion {

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
    @FXML
    private HBox headerBar;
    @FXML
    private TextField nombreField;
    @FXML
    private TextField apellidoField;
    @FXML
    private ComboBox<String> tipoDocumentoBox;
    @FXML
    private TextField documentoField;
    @FXML
    private TextField correoField;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label impuestosLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Button finalizarButton;
    @FXML
    private TextField identificacionField;
    @FXML
    private Text mensajeActualizacionCliente;
    @FXML
    private Text mensajeBusquedaCliente;

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    @FXML
    public void initialize() {
        tipoDocumentoBox.setItems(FXCollections.observableArrayList("Cédula", "Pasaporte", "RUC"));
        tipoDocumentoBox.setValue("Cédula");
        headerBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        headerBar.setOnMouseDragged(event -> {
            ((Stage) headerBar.getScene().getWindow()).setX(event.getScreenX() - xOffset);
            ((Stage) headerBar.getScene().getWindow()).setY(event.getScreenY() - yOffset);
        });
    }

    public void initData(String pelicula, String sala, List<Producto> boletos, double subtotal, double total,
            double impuestos) {
        this.pelicula = pelicula;
        this.sala = sala;
        this.boletos = boletos;

        // Crear una instancia de Factura para usar su lógica de cálculo
        // Factura facturaTemporal = new Factura();
        // facturaTemporal.setProductos(this.boletos);
        // facturaTemporal.calcularSubTotal();
        // facturaTemporal.calcularTotal(new CalculadorIVA());

        DecimalFormat df = new DecimalFormat("$ #,##0.00");

        subtotalLabel.setText(df.format(subtotal));
        impuestosLabel.setText(df.format(impuestos));
        totalLabel.setText(df.format(total));

    }

    @FXML
    void onBuscarCliente(ActionEvent event) {
        nombreField.clear();
        apellidoField.clear();
        documentoField.clear();
        correoField.clear();
        mensajeBusquedaCliente.setText("");
        mensajeActualizacionCliente.setText("");

        String texto = identificacionField.getText();

        if (texto.isEmpty()) {
            showAlert("Campo Incompleto", "Por favor, ingrese un número de identificación para buscar al cliente.");
            return;
        }

        try {
            long idcliente = Long.parseLong(texto);
            ClienteDAO clienteDAO = new ClienteDAO();
            try {
                Cliente cliente = clienteDAO.buscarPorId(idcliente);

                if (cliente != null) {
                    nombreField.setText(cliente.getNombre());
                    apellidoField.setText(cliente.getApellido());
                    documentoField.setText(String.valueOf(cliente.getIdCliente()));
                    correoField.setText(cliente.getCorreoElectronico());
                    mensajeBusquedaCliente.setText("Cliente encontrado.");
                } else {
                    mensajeBusquedaCliente.setText("Cliente no encontrado.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            showAlert("Dato inválido", "La identificación ingresada no es un número válido.");
        }
    }

    @FXML
    void onActualizarCliente(ActionEvent event) {
        if (nombreField.getText().isEmpty() || apellidoField.getText().isEmpty() || documentoField.getText().isEmpty()
                || correoField.getText().isEmpty()) {
            showAlert("Campo Incompleto", "Por favor, llene todos los campos para continuar.");
            return;
        }

        try {
            long idcliente = Long.parseLong(documentoField.getText());
            Cliente cliente = new Cliente(nombreField.getText(), apellidoField.getText(), idcliente,
                    correoField.getText());
            ClienteDAO clienteDAO = new ClienteDAO();
            clienteDAO.actualizarCliente(cliente);
            mensajeActualizacionCliente.setText("Cliente actualizado correctamente.");
        } catch (NumberFormatException e) {
            showAlert("Dato inválido", "El documento ingresado no es un número válido.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Ocurrió un error al actualizar el cliente.");
        }
    }

    @FXML
    protected void onFinalizarAction() {
        if (nombreField.getText().isEmpty() || apellidoField.getText().isEmpty() || documentoField.getText().isEmpty()
                || correoField.getText().isEmpty()) {
            showAlert("Campo Incompleto", "Por favor, llene todos los campos para continuar.");
            return;
        }

        ClienteDAO clienteDAO = new ClienteDAO();
        Cliente cliente = null;
        try {
            cliente = clienteDAO.buscarPorId(Long.parseLong(documentoField.getText()));
            if (cliente == null) {
                cliente = new Cliente(
                        nombreField.getText(),
                        apellidoField.getText(),
                        Long.parseLong(documentoField.getText()),
                        correoField.getText());
                clienteDAO.crearCliente(cliente);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Usar tu servicio para generar la factura final
        Factura facturaFinal = servicioFacturacion.generarFactura(this.boletos, cliente);
        // TODO: Dao debe guardar la factura

        // 3. Mostrar un mensaje de éxito y cerrar
        showAlert("Compra Exitosa", "Se ha generado la factura: " + facturaFinal.getCodigoFactura());

        System.out.println("--- FACTURA GENERADA ---");
        System.out.println(facturaFinal);

        Stage stage = (Stage) finalizarButton.getScene().getWindow();
        stage.close();
        // TODO: En vez de cerrar, redirigir a una vista de cartelera
    }

    @FXML
    protected void onBackAction() {
        if (previousScene != null) {
            ((Stage) finalizarButton.getScene().getWindow()).setScene(previousScene);
        }
    }

    @FXML
    protected void onCloseAction() {
        ((Stage) headerBar.getScene().getWindow()).close();
    }

    @FXML
    protected void onThemeToggleAction() {
        ApuntadorTema.getInstance().applyTheme(headerBar.getScene());
    }

    @FXML
    protected void onVerDetalle() {
        System.out.println("Acción para ver detalle del pedido...");
    }

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
            ApuntadorTema.getInstance().applyTheme(scene);
            alertStage.setScene(scene);
            alertStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
