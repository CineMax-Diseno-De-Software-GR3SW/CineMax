package com.cinemax.venta_boletos.controladores;

import com.cinemax.comun.ControladorCargaConDatos;
import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.venta_boletos.servicios.strategy.ContextoValidacion;
import com.cinemax.venta_boletos.servicios.strategy.EstrategiaCedulaValidacion;
import com.cinemax.venta_boletos.servicios.strategy.EstrategiaPasaporteValidacion;
import com.cinemax.venta_boletos.servicios.strategy.EstrategiaRucValidacion;
import com.cinemax.venta_boletos.modelos.entidades.Boleto;
import com.cinemax.venta_boletos.modelos.entidades.Cliente;
import com.cinemax.venta_boletos.modelos.entidades.Factura;
import com.cinemax.venta_boletos.modelos.entidades.Producto;
import com.cinemax.venta_boletos.modelos.persistencia.BoletoDAO;
import com.cinemax.venta_boletos.modelos.persistencia.ClienteDAO;
import com.cinemax.venta_boletos.modelos.persistencia.FacturaDAO;
import com.cinemax.venta_boletos.servicios.ServicioGeneradorArchivoPDF;
import com.cinemax.venta_boletos.servicios.ServicioFacturacion;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.cinemax.venta_boletos.servicios.ServicioGeneradorArchivo;

public class ControladorFacturacion {

    // --- Lógica de Negocio ---
    private final ServicioFacturacion servicioFacturacion = new ServicioFacturacion();

    // --- Estado ---
    private Scene previousScene;
    private List<Producto> boletos;
    private double xOffset = 0;
    private double yOffset = 0;

    // --- Componentes FXML ---
    @FXML
    private TextField apellidoField;

    @FXML
    private Button buttonPagar;

    @FXML
    private TextField correoField;

    @FXML
    private TextField documentoField;

    @FXML
    private HBox headerBar;

    @FXML
    private TextField identificacionField;

    @FXML
    private VBox informacionFuncionContainer;

    @FXML
    private TextField nombreField;

    @FXML
    private CheckBox confirmCheckBox;

    @FXML
    private ComboBox<String> tipoDocumentoBox;
    //public void setPreviousScene(Scene scene) {
    //    this.previousScene = scene;
    //}

    private ControladorInformacionLateral controladorInformacionLateral;

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

    public void initData(List<Producto> boletos) {
        this.boletos = boletos;
        //ControladorAsignadorButacas controladorAsignadorButacas = new ControladorAsignadorButacas();
        //List<Butaca> butacasAsignadas = controladorAsignadorButacas.asignarButacas("", funcion, butacasAsignadas, totalBoletos);
        //List<Producto> boletosGenerados = servicioBoleto.generarBoleto(this.funcion, butacasAsignadas);

        // Crear una instancia de Factura para usar su lógica de cálculo
        // Factura facturaTemporal = new Factura();
        // facturaTemporal.setProductos(this.boletos);
        // facturaTemporal.calcularSubTotal();
        // facturaTemporal.calcularTotal(new CalculadorIVA());

        DecimalFormat df = new DecimalFormat("$ #,##0.00");

        //subtotalLabel.setText(df.format(subtotal));
        //impuestosLabel.setText(df.format(impuestos));
        //totalLabel.setText(df.format(total));

        // Cargar el FXML de la vista de información lateral
        if(controladorInformacionLateral == null) {
            ManejadorMetodosComunes.mostrarVentanaError("Controlador de información lateral no inicializado.");
            return;
        }

        Parent vistaInformacionLateral = controladorInformacionLateral.getRoot(); // Para obtener la vista cargada en el controlador anterior


        //FXMLLoader loader = new FXMLLoader();
        //loader.setLocation(getClass().getResource("/vistas/venta_boletos/VistaInformacionLateral.fxml"));
        //
        //loader.setController(controladorInformacionLateral);
        
        //Parent vistaInformacionLateral;
        //try {
        //    vistaInformacionLateral = loader.load();
        //} catch (IOException e) {
        //    ManejadorMetodosComunes.mostrarVentanaError("Error al cargar la vista de información lateral: " + e.getMessage());
        //    e.printStackTrace();
        //    return; // Salir del método si hay un error
        //}
        
        // Agregar el mapa al contenedor
        informacionFuncionContainer.getChildren().clear(); // Limpiar el contenedor antes de agregar
        informacionFuncionContainer.getChildren().add(vistaInformacionLateral);
        controladorInformacionLateral.calcularTotal(boletos);

        System.out.println("Boletos generados: " + ((Boleto) boletos.get(0)).getFuncion().getPelicula().getTitulo());
        System.out.println("Butacas asignadas: " + ((Boleto) boletos.get(0)).getButaca().getFila() + " " + ((Boleto) boletos.get(0)).getButaca().getColumna());

    }

    @FXML
    void onBuscarCliente(ActionEvent event) {
        nombreField.clear();
        apellidoField.clear();
        documentoField.clear();
        correoField.clear();

        String texto = identificacionField.getText().strip().replaceAll("\\s+", " "); // Limpieza profunda del input

        if (texto.isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, ingrese un número de identificación para buscar al cliente.");
            return;
        }

        try {
            String idcliente = texto;
            ClienteDAO clienteDAO = new ClienteDAO();
            try {
                Cliente cliente = clienteDAO.buscarPorId(idcliente);

                if (cliente != null) {
                    nombreField.setText(cliente.getNombre());
                    apellidoField.setText(cliente.getApellido());
                    documentoField.setText(String.valueOf(cliente.getIdCliente()));
                    tipoDocumentoBox.setValue(cliente.getTipoDocumento());
                    correoField.setText(cliente.getCorreoElectronico());
                    ManejadorMetodosComunes.mostrarVentanaExito("Cliente encontrado exitosamente.");
                } else {
                    ManejadorMetodosComunes.mostrarVentanaAdvertencia("Cliente no encontrado.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("El número de identificación debe ser un número válido.");
        }
    }

    private boolean validarDocumento() {

        // Limpieza PROFUNDA del input (incluye espacios Unicode y múltiples espacios)
        String documento = documentoField.getText()
            .replaceAll("^\\s+", "")  // Espacios al inicio
            .replaceAll("\\s+$", "")  // Espacios al final
            .replaceAll("\\s+", " "); // Espacios múltiples internos
        
        documentoField.setText(documento); // Actualiza el campo con el texto limpio
    
        ContextoValidacion contextoValidacion = new ContextoValidacion();

        switch (tipoDocumentoBox.getValue()) {
            case "Cédula":
                contextoValidacion.setEstrategia(new EstrategiaCedulaValidacion());
                break;

            case "RUC":
                contextoValidacion.setEstrategia(new EstrategiaRucValidacion());
                break;
            case "Pasaporte":
                contextoValidacion.setEstrategia(new EstrategiaPasaporteValidacion());
                break;
            default:
                ManejadorMetodosComunes.mostrarVentanaError("Tipo de documento no soportado.");
                return false;
        }

        System.out.println("Validando documento: " + documentoField);
        System.out.println("Estrategia seleccionada: " + tipoDocumentoBox.getValue());
        System.out.println("Estrategia ejecutada: " + contextoValidacion.ejecutarEstrategia(documentoField.getText()));

        documentoField.setText(documentoField.getText());
        if(!contextoValidacion.ejecutarEstrategia(documentoField.getText())) {
            ManejadorMetodosComunes.mostrarVentanaError("Documento inválido: " + documentoField.getText());
            return false;
        }

        return true;

    }

    @FXML
    void onActualizarCliente(ActionEvent event) {

        if (!validarDocumento()) {
            return; // Si el documento no es válido, no continuar con la actualización
        }

        if (nombreField.getText().isEmpty() || apellidoField.getText().isEmpty() || documentoField.getText().isEmpty()
                || correoField.getText().isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Llene todos los campos para continuar");
            return;
        }
        
        try {
            Cliente cliente = new Cliente(nombreField.getText(), apellidoField.getText(), documentoField.getText(),
                    correoField.getText(), tipoDocumentoBox.getValue());
            ClienteDAO clienteDAO = new ClienteDAO();

            Cliente clienteExiste = clienteDAO.buscarPorId(identificacionField.getText());

            if (clienteExiste == null) {
                ManejadorMetodosComunes.mostrarVentanaAdvertencia("El cliente no existe, tiene que registrarlo primero.");
            } else {
                clienteDAO.actualizarCliente(cliente);
                ManejadorMetodosComunes.mostrarVentanaExito("Cliente actualizado exitosamente.");
            }

        } catch (NumberFormatException e) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("El documento ingresado no es un número válido.");
        } catch (Exception e) {
            e.printStackTrace();
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al actualizar al cliente.");
        }
    }

    @FXML
    protected void onPagarAction() {
        if (nombreField.getText().isEmpty() || apellidoField.getText().isEmpty() || documentoField.getText().isEmpty()
                || correoField.getText().isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Llene todos los campos para continuar");
            return;
        }

        if(confirmCheckBox.isSelected() == false) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Debe confirmar la compra para continuar.");
            return;
        }

        if(!validarDocumento()) {
            return; // Si el documento no es válido, no continuar con la compra
        }

        // 1. Validar la cédula
        //Manejador manejadorCedula = new ManejadorCedula();
        //Manejador manejadorRUC = new ManejadorRUC();
//
        //manejadorCedula.colocarSiguienteManejador(manejadorRUC);
        //manejadorCedula.manejarPeticion(documentoField.getText());

        ClienteDAO clienteDAO = new ClienteDAO();
        Cliente cliente = null;
        try {
            cliente = clienteDAO.buscarPorId(documentoField.getText());
            if (cliente == null) {
                cliente = new Cliente(
                        nombreField.getText(),
                        apellidoField.getText(),
                        documentoField.getText(),
                        correoField.getText(),
                        tipoDocumentoBox.getValue());
                clienteDAO.crearCliente(cliente);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServicioGeneradorArchivo generador = new ServicioGeneradorArchivoPDF();
        generador.generarBoletosPDF(boletos);

        
        BoletoDAO boletoDAO = new BoletoDAO();


        //ButacaService butacaService = new ButacaService();
        //for (Producto boleto : boletos) {
        //    ((Boleto) boleto).getButaca().setEstado(EstadoButaca.OCUPADA.name());
        //    try {
        //        butacaService.actualizarButaca(((Boleto) boleto).getButaca());
        //    } catch (Exception e) {
        //        // TODO Auto-generated catch block
        //        e.printStackTrace();
        //    }
        //}

        FacturaDAO facturaDAO = new FacturaDAO();
        // 2. Usar tu servicio para generar la factura final
        Factura facturaFinal = servicioFacturacion.generarFactura(this.boletos, cliente);
        try {
            facturaDAO.crearFactura(facturaFinal);
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al crear la factura: " + e.getMessage());
            e.printStackTrace();
        }

        for (Producto boleto : boletos) {
            try {
                boletoDAO.crearBoleto((Boleto) boleto, facturaFinal);
            } catch (Exception e) {
                ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al crear el boleto: " + e.getMessage());
                e.printStackTrace();
                return; 
            }
        }

        // TODO: Dao debe guardar la factura

        // 3. Mostrar un mensaje de éxito y cerrar
        ManejadorMetodosComunes.mostrarVentanaExito("Factura creada exitosamente: " + facturaFinal.getCodigoFactura());

        System.out.println("--- FACTURA GENERADA ---");
        System.out.println(facturaFinal);

        //Stage stage = (Stage) buttonPagar.getScene().getWindow();
        //stage.close();
        ManejadorMetodosComunes.cambiarVentana((Stage) buttonPagar.getScene().getWindow(), "/vistas/empleados/PantallaPortalPrincipal.fxml", "CineMAX");
    }

    @FXML
    protected void onBackAction() {

        try {

            // 1. ventana actual
            Stage currentStage = (Stage) headerBar.getScene().getWindow();

            // 2. Objetos para pasar los datos a la siguiente pantalla usando la pantalla de carga
            ControladorCargaConDatos controladorCargaConDatos = new ControladorCargaAsignacionButacas(
                "/vistas/venta_boletos/VistaSeleccionButacas.fxml",
                currentStage,
                new ArrayList<>(List.of(((Boleto) boletos.get(0)).getFuncion()))
            );            

            // 3. Llamar al manejador de métodos comunes para mostrar la pantalla de carga
            ManejadorMetodosComunes.mostrarVistaDeCargaPasandoDatos(currentStage, controladorCargaConDatos, 8, 100);

        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al confirmar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setControladorInformacionLateral(ControladorInformacionLateral controladorInformacionLateral) {
        this.controladorInformacionLateral = controladorInformacionLateral;
        controladorInformacionLateral.mostrarTodaLaInformacionDePago();
        
    }

}
