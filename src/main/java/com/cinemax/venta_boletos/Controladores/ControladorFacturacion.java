package com.cinemax.venta_boletos.Controladores;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.venta_boletos.Modelos.Boleto;
//import com.cinemax.comun.ValidadadorCampos;
import com.cinemax.venta_boletos.Modelos.Cliente;
import com.cinemax.venta_boletos.Modelos.Factura;
import com.cinemax.venta_boletos.Modelos.Producto;
import com.cinemax.venta_boletos.Modelos.Persistencia.ClienteDAO;
import com.cinemax.venta_boletos.Servicios.ServicioFacturacion;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import com.cinemax.venta_boletos.Servicios.ContenidoMensaje;
import com.cinemax.venta_boletos.Modelos.Factura;
import com.cinemax.venta_boletos.Modelos.Cliente;
import com.cinemax.venta_boletos.Servicios.GeneradorArchivoPDF;
import com.cinemax.venta_boletos.Servicios.ServicioCorreoVentaBoletos;
import java.io.File;
import jakarta.mail.MessagingException;

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
        documentoField.setDisable(true);

        String texto = identificacionField.getText();

        if (texto.isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, ingrese un número de identificación para buscar al cliente.");
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
                    ManejadorMetodosComunes.mostrarVentanaExito("Cliente encontrado exitosamente.");

                } else {
                    ManejadorMetodosComunes.mostrarVentanaError("Cliente no encontrado.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("El número de identificación debe ser un número válido.");
        }
    }

    @FXML
    void onActualizarCliente(ActionEvent event) {
        // if (!validarCamposCliente()) {
        //     return;
        // }

        try {
            long idcliente = Long.parseLong(documentoField.getText());
            Cliente cliente = new Cliente(nombreField.getText(), apellidoField.getText(), idcliente,
                    correoField.getText());
            ClienteDAO clienteDAO = new ClienteDAO();
            clienteDAO.actualizarCliente(cliente);
            ManejadorMetodosComunes.mostrarVentanaExito("Cliente actualizado exitosamente.");
        } catch (NumberFormatException e) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("El documento ingresado no es un número válido.");
        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al actualizar el cliente.");
        }
    }

    @FXML
    protected void onFinalizarAction() {
        // if (!validarCamposCliente()) {
        //     return; // No continuar si los campos son inválidos
        // }

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
        Factura facturaFinal = generarFactura(this.boletos, cliente);
        // Generar el PDF de la factura
        GeneradorArchivoPDF generadorPDF = new GeneradorArchivoPDF();
        generadorPDF.generarFacturaPDF(facturaFinal);
        // Enviar la factura por correo
        String correoCliente = cliente.getCorreoElectronico();
        String asunto = "Factura de su compra en CineMax";
        String cuerpo = "<p>Estimado/a " + cliente.getNombre() + ",<br>Adjuntamos la factura de su compra. ¡Gracias por preferirnos!</p>";
        String nombreArchivo = "Factura_" + facturaFinal.getCodigoFactura() + ".pdf";
        File archivoAdjunto = new File(nombreArchivo);
        try {
            ServicioCorreoVentaBoletos correo = new ServicioCorreoVentaBoletos();
            correo.enviarCorreoConAdjunto(correoCliente, new ContenidoMensaje(asunto, cuerpo), archivoAdjunto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO: Dao debe guardar la factura

        // 3. Mostrar un mensaje de éxito y cerrar
        ManejadorMetodosComunes.mostrarVentanaExito("Se ha generado la factura: " + facturaFinal.getCodigoFactura());

        System.out.println("--- FACTURA GENERADA ---");
        System.out.println(facturaFinal);

        // TODO: En vez de cerrar, redirigir a una vista de cartelera
        ManejadorMetodosComunes.cambiarVentana((Stage) finalizarButton.getScene().getWindow(),"/Vista/venta_boletos/cartelera-view.fxml", "Cartelera");
    }

    public Factura generarFactura(List<Producto> boletos, Cliente cliente) {
        return servicioFacturacion.generarFactura(boletos, cliente);
    }

    /**
     * Elimina el estilo de error de todos los campos.
     */
    private void resetValidationStyles() {
        nombreField.getStyleClass().remove("error");
        apellidoField.getStyleClass().remove("error");
        documentoField.getStyleClass().remove("error");
        correoField.getStyleClass().remove("error");
    }

    /**
     * Aplica el estilo de error a un campo y muestra un tooltip.
     * @param field El TextField que tiene el error.
     * @param message El mensaje a mostrar.
     */
    private void showFieldError(TextField field, String message) {
        field.getStyleClass().add("error");
        Tooltip tooltip = new Tooltip(message);
        tooltip.setAutoHide(true);

        // Mostrar el tooltip a la derecha del campo
        Point2D p = field.localToScreen(field.getBoundsInLocal().getMaxX(), field.getBoundsInLocal().getMinY());
        tooltip.show(field, p.getX() + 5, p.getY());
        field.requestFocus(); // Poner el foco en el campo con error
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
    protected void onVerDetalle() {
        System.out.println("Acción para ver detalle del pedido...");
    }

    private boolean validarCamposCliente() {
        resetValidationStyles(); // Opcional si tienes estilos visuales de error

        // Validar campos obligatorios
        if (!ManejadorMetodosComunes.validarCampoObligatorio(identificacionField.getText(), "Identificación")) return false;
        if (!ManejadorMetodosComunes.validarCampoObligatorio(nombreField.getText(), "Nombre")) return false;
        if (!ManejadorMetodosComunes.validarCampoObligatorio(apellidoField.getText(), "Apellido")) return false;
        if (!ManejadorMetodosComunes.validarCampoObligatorio(tipoDocumentoBox.getValue(), "Tipo de documento")) return false;
        if (!ManejadorMetodosComunes.validarCampoObligatorio(documentoField.getText(), "Número de documento")) return false;
        if (!ManejadorMetodosComunes.validarCampoObligatorio(correoField.getText(), "Correo")) return false;

        // Validar que identificación sea numérica
        if (!ManejadorMetodosComunes.validarNumero(identificacionField.getText(), "Identificación")) return false;

        // Validar que número de documento también sea numérico si aplica (por ejemplo para cédula o RUC)
        String tipoDocumento = tipoDocumentoBox.getValue();
        if (tipoDocumento.equals("Cédula") || tipoDocumento.equals("RUC")) {
            if (!ManejadorMetodosComunes.validarNumero(documentoField.getText(), "Número de documento")) return false;
        }

        return true;
    }
}
