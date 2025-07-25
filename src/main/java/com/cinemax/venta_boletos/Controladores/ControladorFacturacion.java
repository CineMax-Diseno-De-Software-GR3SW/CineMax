package com.cinemax.venta_boletos.Controladores;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.comun.ValidadadorCampos;
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
                    mensajeBusquedaCliente.setText("Cliente encontrado.");
                } else {
                    mensajeBusquedaCliente.setText("Cliente no encontrado.");
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
        if (nombreField.getText().isEmpty() || apellidoField.getText().isEmpty() || documentoField.getText().isEmpty()
                || correoField.getText().isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Llene todos los campos para continuar");
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
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("El documento ingresado no es un número válido.");
        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("Ocurrió un error al actualizar el cliente.");
        }
    }

    @FXML
    protected void onFinalizarAction() {
        if (!validarCamposCliente()) {
            return; // Si la validación falla, no continuar
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

        Stage stage = (Stage) finalizarButton.getScene().getWindow();
        stage.close();
        // TODO: En vez de cerrar, redirigir a una vista de cartelera
    }

    private boolean validarCamposCliente() {
        //Resetear estilos de todos los campos
        resetValidationStyles();

        String nombre = nombreField.getText();
        String apellido = apellidoField.getText();
        String documento = documentoField.getText();
        String correo = correoField.getText();
        String tipoDocumento = tipoDocumentoBox.getValue();

        if (nombreField.getText().isEmpty() || apellidoField.getText().isEmpty() || documentoField.getText().isEmpty()
                || correoField.getText().isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Llene todos los campos para continuar");
        }if (!ValidadadorCampos.esSoloTexto(nombre)) {
            showFieldError(nombreField, "El nombre solo debe contener letras.");
            return false;
        }if (!ValidadadorCampos.esSoloTexto(apellido)) {
            showFieldError(apellidoField, "El apellido solo debe contener letras.");
            return false;
        }if (documento.isEmpty()) {
            showFieldError(documentoField, "El documento no puede estar vacío.");
            return false;
        }switch (tipoDocumento) {
            case "Cédula":
                if (!ValidadadorCampos.esSoloNumeros(documento) || documento.length() != 10) {
                    showFieldError(documentoField, "La Cédula debe tener 10 números.");
                    return false;
                }
                break;
            case "RUC":
                if (!ValidadadorCampos.esSoloNumeros(documento) || documento.length() != 13) {
                    showFieldError(documentoField, "El RUC debe tener 13 números.");
                    return false;
                }
                break;
            case "Pasaporte":
                if (documento.length() < 6 || documento.length() > 15) {
                    showFieldError(documentoField, "El Pasaporte debe tener entre 6 y 15 caracteres.");
                    return false;
                }
                break;
        }if (correo.isEmpty()) {
            showFieldError(correoField, "El correo no puede estar vacío.");
            return false;
        }if (!ValidadadorCampos.esCorreoValido(correo)) {
            showFieldError(correoField, "El formato del correo no es válido.");
            return false;
        }

        return true; // Solo si los campos son válidos
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
}
