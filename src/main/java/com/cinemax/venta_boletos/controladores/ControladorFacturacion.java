package com.cinemax.venta_boletos.controladores;

import com.cinemax.comun.ControladorCargaConDatos;
import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.venta_boletos.servicios.ServicioTemporizador;
import com.cinemax.venta_boletos.servicios.strategy.ContextoValidacion;
import com.cinemax.venta_boletos.servicios.strategy.EstrategiaCedulaValidacion;
import com.cinemax.venta_boletos.servicios.strategy.EstrategiaPasaporteValidacion;
import com.cinemax.venta_boletos.servicios.strategy.EstrategiaRucValidacion;
import com.cinemax.venta_boletos.modelos.entidades.Boleto;
import com.cinemax.venta_boletos.modelos.entidades.CalculadorIVA;
import com.cinemax.venta_boletos.modelos.entidades.Cliente;
import com.cinemax.venta_boletos.modelos.entidades.Factura;
import com.cinemax.venta_boletos.modelos.entidades.Producto;
import com.cinemax.venta_boletos.modelos.persistencia.BoletoDAO;
import com.cinemax.venta_boletos.modelos.persistencia.ClienteDAO;
import com.cinemax.venta_boletos.modelos.persistencia.FacturaDAO;
import com.cinemax.venta_boletos.servicios.ServicioGeneradorArchivoPDF;
import com.cinemax.venta_boletos.servicios.ServicioFacturacion;
import com.cinemax.venta_boletos.modelos.entidades.CalculadorImpuesto;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import com.cinemax.venta_boletos.servicios.ServicioGeneradorArchivo;

/**
 * Controlador principal para la facturación de boletos.
 * Maneja la interacción del usuario con la interfaz de facturación.
 */
public class ControladorFacturacion {

    // ===== ELEMENTOS DE LA INTERFAZ (FXML) =====
    @FXML
    private TextField apellidoField;

    /** Botón para realizar el proceso de pago. */
    @FXML
    private Button buttonPagar;

    @FXML
    private TextField correoField;

    @FXML
    private TextField documentoField;

    @FXML
    private HBox headerBar;

    /** Campo de texto para ingresar el número de identificación del cliente y buscarlo en base a eso. */
    @FXML
    private TextField identificacionField;

    @FXML
    private VBox informacionFuncionContainer;

    @FXML
    private TextField nombreField;

    /** Casilla de verificación para confirmar la compra de los boletos. */
    @FXML
    private CheckBox confirmCheckBox;

    @FXML
    private ComboBox<String> tipoDocumentoBox;

    @FXML
    private Label timerLabel;

    @FXML
    private Button buttonCrearOActualizar;

    @FXML
    private Button buttonNuevoCliente;

    @FXML
    private Button buttonLimpiarFormulario;

    // ===== ATRIBUTOS DE LÓGICA =====

    /** Lista de productos seleccionados, representando los boletos de cine. */
    private List<Producto> boletos;
    private double xOffset = 0;
    private double yOffset = 0;

    /** Servicio que gestiona la lógica de facturación (generación de factura, validaciones). */
    private final ServicioFacturacion servicioFacturacion = new ServicioFacturacion();

    /** Controlador del panel lateral que muestra información de la función. */
    private com.cinemax.venta_boletos.controladores.ControladorInformacionLateral controladorInformacionLateral;

    private Cliente clienteEnEdicion = null;

    /**
     * Inicializa los elementos gráficos y configura eventos personalizados.
     *
     * Proceso de inicialización:
     * 1. Llena el ComboBox de tipo de documento con opciones predefinidas.
     * 2. Selecciona por defecto el valor "Cédula" en el ComboBox.
     * 3. Configura eventos de mouse para permitir arrastrar la ventana desde la barra superior (headerBar).
     */
    @FXML
    public void initialize() {
        // 1. Establecer los valores disponibles para el tipo de documento.
        tipoDocumentoBox.setItems(FXCollections.observableArrayList("Cédula", "Pasaporte", "RUC"));

         // 2. Seleccionar "Cédula" como valor por defecto.
        tipoDocumentoBox.setValue("Seleccione un tipo de documento");

        // 3. Vincular el label del temporizador
        if (timerLabel != null) {
            timerLabel.textProperty().bind(ServicioTemporizador.getInstance().tiempoRestanteProperty());
        }

        // 4.. Configurar eventos para permitir mover la ventana arrastrando el header.
        headerBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        headerBar.setOnMouseDragged(event -> {
            ((Stage) headerBar.getScene().getWindow()).setX(event.getScreenX() - xOffset);
            ((Stage) headerBar.getScene().getWindow()).setY(event.getScreenY() - yOffset);
        });

        // Listeners para validar y actualizar el texto del botón
        nombreField.textProperty().addListener((o, ov, nv) -> actualizarEstadoFormulario());
        apellidoField.textProperty().addListener((o, ov, nv) -> actualizarEstadoFormulario());
        documentoField.textProperty().addListener((o, ov, nv) -> actualizarEstadoFormulario());
        correoField.textProperty().addListener((o, ov, nv) -> actualizarEstadoFormulario());
        tipoDocumentoBox.valueProperty().addListener((o, ov, nv) -> actualizarEstadoFormulario());

        // Primera validación al iniciar
        actualizarEstadoFormulario();
    }


    /**
     * Inicializa la vista con los boletos seleccionados y muestra la información lateral correspondiente.
     * 
     * Proceso de inicialización:
     * 1. Asigna la lista de boletos a la variable local.
     * 2. Verifica si el controlador lateral está inicializado.
     * 3. Carga la vista de información lateral desde el controlador correspondiente.
     * 4. Limpia y agrega la vista al contenedor principal.
     * 5. Calcula el total a pagar con base en los boletos seleccionados.
     * 
     * @param boletos Lista de productos (boletos) que fueron seleccionados por el usuario.
     */
    public void cargarBoletosSeleccionados(List<Producto> boletos) {
        this.boletos = boletos;

        // 2. Validar si el controlador lateral está inicializado correctamente.
        if(controladorInformacionLateral == null) {
            ManejadorMetodosComunes.mostrarVentanaError("Controlador de información lateral no inicializado.");
            return;
        }

        // 3. Obtener la vista asociada al controlador lateral.
        Parent vistaInformacionLateral = controladorInformacionLateral.getRoot(); 
        
        // 4. Limpiar el contenedor y cargar la vista de información lateral.
        informacionFuncionContainer.getChildren().clear(); 
        informacionFuncionContainer.getChildren().add(vistaInformacionLateral);

        // 5. Calcular el total a pagar por los boletos seleccionados.
        controladorInformacionLateral.calcularTotal(boletos);
    }

    /**
     * Maneja el evento de búsqueda de cliente por número de identificación.
     * Limpia los campos de texto y busca al cliente en la base de datos.
     * Si el cliente es encontrado, llena los campos con su información.
     * Si no se encuentra, muestra un mensaje de advertencia.
     * @param event Evento de acción al hacer clic en el botón de búsqueda.
     */
    @FXML
    void onBuscarCliente(ActionEvent event) {
        String texto = identificacionField.getText();

        // Validar que el campo de identificación no esté vacío.
        if (texto.isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, ingrese un número de identificación para buscar al cliente.");
            return;
        }

        try {
            String idcliente = texto;
            ClienteDAO clienteDAO = new ClienteDAO();
            try {
                // Buscar al cliente por su número de identificación.
                clienteEnEdicion = clienteDAO.buscarPorId(idcliente);

                // Si el cliente es encontrado, se llenan los campos con su información.
                if (clienteEnEdicion != null) {
                    nombreField.setText(clienteEnEdicion.getNombre());
                    apellidoField.setText(clienteEnEdicion.getApellido());
                    documentoField.setText(String.valueOf(clienteEnEdicion.getIdCliente()));
                    tipoDocumentoBox.setValue(clienteEnEdicion.getTipoDocumento());
                    correoField.setText(clienteEnEdicion.getCorreoElectronico());
                    actualizarModoFormulario();
                    ManejadorMetodosComunes.mostrarVentanaExito("Cliente encontrado exitosamente.");
                } else { 
                    // Si no se encuentra al cliente, se muestra un mensaje de advertencia.
                    ManejadorMetodosComunes.mostrarVentanaAdvertencia("Cliente no encontrado.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("El número de identificación debe ser un número válido.");
        }
    }

    /**
     * Valida el documento ingresado por el usuario según el tipo de documento seleccionado.
     * Limpia el campo de texto de espacios innecesarios y aplica la estrategia de validación correspondiente.
     * Si el documento es inválido, muestra un mensaje de error y retorna false.
     * @return true si el documento es válido, false en caso contrario.
     */
    private boolean validarDocumento() {

        // Limpieza PROFUNDA del input (incluye espacios Unicode y múltiples espacios).
        String documento = documentoField.getText()
            .replaceAll("^\\s+", "")  // Espacios al inicio.
            .replaceAll("\\s+$", "")  // Espacios al final.
            .replaceAll("\\s+", " "); // Espacios múltiples internos.
        
        documentoField.setText(documento); // Actualiza el campo con el texto limpio
    
        ContextoValidacion contextoValidacion = new ContextoValidacion();

        // Selecciona la estrategia de validación según el tipo de documento.
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

        documentoField.setText(documentoField.getText());
        // Ejecuta la estrategia de validación y verifica si el documento es válido.
        // Si no es válido, muestra un mensaje de error y retorna false.
        if(!contextoValidacion.ejecutarEstrategia(documentoField.getText())) {
            ManejadorMetodosComunes.mostrarVentanaError("Documento inválido: " + documentoField.getText());
            return false;
        }

        return true;

    }

    /**
     * Maneja el evento de actualización de cliente.
     * Valida los campos de entrada y actualiza la información del cliente en la base de datos.
     * Si el cliente no existe, muestra un mensaje de advertencia.
     * Si hay un error al actualizar, muestra un mensaje de error.
     * @param event Evento de acción al hacer clic en el botón de actualizar cliente.
     */
    @FXML
    void onCrearOActualizarCliente(ActionEvent event) {

        // Validar que el documento sea válido antes de continuar con la actualización.
        if (!validarDocumento()) {
            return; 
        }

        // Validar que todos los campos estén llenos antes de proceder con la actualización.
        if (!validarFormularioCompleto()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Llene todos los campos para continuar");
            return;
        }

        if (clienteEnEdicion == null) {
            try {
                Cliente cliente = new Cliente(nombreField.getText(), apellidoField.getText(), documentoField.getText(),
                        correoField.getText(), tipoDocumentoBox.getValue());
                ClienteDAO clienteDAO = new ClienteDAO();
                clienteDAO.crearCliente(cliente);
                ManejadorMetodosComunes.mostrarVentanaExito("Cliente creado exitosamente.");
            } catch (NumberFormatException e) {
                ManejadorMetodosComunes.mostrarVentanaAdvertencia("El documento ingresado no es un número válido.");
            } catch (Exception e) {
                e.printStackTrace();
                ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al crear al cliente.");
            }
        }
        else {
            try {
                Cliente cliente = new Cliente(nombreField.getText(), apellidoField.getText(), documentoField.getText(),
                        correoField.getText(), tipoDocumentoBox.getValue());
                ClienteDAO clienteDAO = new ClienteDAO();

                Cliente clienteExiste = clienteDAO.buscarPorId(identificacionField.getText());

                // Verificar si el cliente existe en la base de datos.
                if (clienteExiste == null) {
                    // Si el cliente no existe, mostrar un mensaje de advertencia.
                    ManejadorMetodosComunes.mostrarVentanaAdvertencia("El cliente no existe, tiene que registrarlo primero.");
                } else {
                    // Si el cliente existe, actualizar su información en la base de datos.
                    clienteDAO.actualizarCliente(cliente);
                    actualizarModoFormulario();
                    ManejadorMetodosComunes.mostrarVentanaExito("Cliente actualizado exitosamente.");
                }

            } catch (NumberFormatException e) {
                ManejadorMetodosComunes.mostrarVentanaAdvertencia("El documento ingresado no es un número válido.");
            } catch (Exception e) {
                e.printStackTrace();
                ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al actualizar al cliente.");
            }
        }
        
    }

    /**
     * Maneja la acción de pago al hacer clic en el botón de pagar.
     * Valida los campos de entrada, verifica si se ha confirmado la compra y si el documento es válido.
     * Si todo es correcto, genera la factura y los boletos, y muestra un mensaje de éxito.
     * Si hay algún error durante el proceso, muestra un mensaje de error.
     *  event Evento de acción al hacer clic en el botón de pagar.
     */

    @FXML
    protected void onPagarAction() {
        // Validar que todos los campos estén llenos antes de proceder con la compra.
        if (!validarFormularioCompleto()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Llene todos los campos para continuar");
            return;
        }

        // Validar que se haya confirmado la compra antes de continuar.
        if(confirmCheckBox.isSelected() == false) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Debe confirmar la compra para continuar.");
            return;
        }

        // Validar el documento ingresado por el usuario.
        if(!validarDocumento()) {
            return;
        }

        ClienteDAO clienteDAO = new ClienteDAO();
        Cliente cliente = null;
        try {
            // Buscar al cliente por su número de identificación.
            cliente = clienteDAO.buscarPorId(documentoField.getText());

            // Si el cliente no existe, crear uno nuevo con los datos ingresados.
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

        // Generar los boletos en formato PDF.
        ServicioGeneradorArchivo generador = new ServicioGeneradorArchivoPDF();
        generador.generarBoletos(boletos);

        FacturaDAO facturaDAO = new FacturaDAO();
        CalculadorImpuesto calculadorImpuesto = new CalculadorIVA();
        // Generar la factura con los boletos y el cliente.
        Factura facturaFinal = servicioFacturacion.generarFactura(this.boletos, cliente,calculadorImpuesto);
        try {
            // Guardar la factura en la base de datos.
            facturaDAO.crearFactura(facturaFinal);
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al crear la factura: " + e.getMessage());
            e.printStackTrace();
        }

        BoletoDAO boletoDAO = new BoletoDAO();

        // Guardar los boletos asociados a la factura en la base de datos.
        for (Producto boleto : boletos) {
            try {
                boletoDAO.crearBoleto((Boleto) boleto, facturaFinal);
            } catch (Exception e) {
                ManejadorMetodosComunes.mostrarVentanaError("Sucedió algo inesperado al crear el boleto: " + e.getMessage());
                e.printStackTrace();
                return; 
            }
        }

        // Detener el temporizador después de un pago exitoso
        ServicioTemporizador.getInstance().detenerTemporizador();

        // Mostrar un mensaje de éxito al usuario indicando que la factura se ha creado exitosamente.
        ManejadorMetodosComunes.mostrarVentanaExito("Factura creada exitosamente: " + facturaFinal.getCodigoFactura());

        // Redirigir al usuario a la pantalla principal del portal de empleados.
        ManejadorMetodosComunes.cambiarVentana((Stage) buttonPagar.getScene().getWindow(), "/vistas/empleados/PantallaPortalPrincipal.fxml", "CineMAX");
    }

    /**
     * Maneja la acción de retroceso al hacer clic en el botón de retroceso.
     * Cierra la ventana actual y redirige al usuario a la pantalla de selección de butacas.
     * Si ocurre un error durante el proceso, muestra un mensaje de error.
     * param event Evento de acción al hacer clic en el botón de retroceso.
     */
    @FXML
    protected void onBackAction() {

        try {

            // Obtener la ventana actual desde el headerBar.
            Stage currentStage = (Stage) headerBar.getScene().getWindow();

            // Crear el controlador de carga para la vista de selección de butacas,
            // pasando la ruta FXML, la ventana actual y la función asociada al primer boleto
            ControladorCargaConDatos controladorCargaConDatos = new ControladorCargaAsignacionButacas(
                "/vistas/venta_boletos/VistaSeleccionButacas.fxml",
                currentStage,
                new ArrayList<>(List.of(((Boleto) boletos.get(0)).getFuncion()))
            );            

            // 3. Llamar al manejador de métodos comunes para mostrar la pantalla de carga
            ManejadorMetodosComunes.mostrarVistaDeCargaPasandoDatosOptimizada(currentStage, controladorCargaConDatos, 8, 325);

        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al confirmar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Establece el controlador de información lateral para mostrar detalles de la función y el total a pagar.
     * 
     * @param controladorInformacionLateral El controlador de información lateral que maneja la vista de detalles de la función.
     */
    public void setControladorInformacionLateral(ControladorInformacionLateral controladorInformacionLateral) {
        this.controladorInformacionLateral = controladorInformacionLateral;
        controladorInformacionLateral.mostrarTodaLaInformacionDePago();
    }
    
    @FXML
    void onLimpiarFormulario(ActionEvent event) {
        limpiarFormulario();
    }

    private void limpiarFormulario() {
        nombreField.clear();
        apellidoField.clear();
        tipoDocumentoBox.setValue("Seleccione un tipo de documento");
        documentoField.clear();
        correoField.clear();
        identificacionField.clear();

        clienteEnEdicion = null;
        actualizarModoFormulario();
    }

    private void actualizarModoFormulario() {
        if (clienteEnEdicion == null) {
            // Modo crear
            buttonCrearOActualizar.setText("Crear");
            // Ocultar el botón "Nuevo" cuando está en modo crear
            if (buttonNuevoCliente != null) {
                buttonNuevoCliente.setVisible(false);
                buttonNuevoCliente.setManaged(false);
            }
        } else {
            // Modo editar
            buttonCrearOActualizar.setText("Actualizar");
            // Mostrar el botón "Nuevo" cuando está en modo editar
            if (buttonNuevoCliente != null) {
                buttonNuevoCliente.setVisible(true);
                buttonNuevoCliente.setManaged(true);
            }
        }
        // Asegurar que se ejecute la validación del formulario
        actualizarEstadoFormulario();
    }

    private void actualizarEstadoFormulario() {
        if (buttonCrearOActualizar != null) {
            boolean formularioValido = validarFormularioCompleto();
            buttonCrearOActualizar.setDisable(!formularioValido);
        }
    }

    private boolean validarFormularioCompleto() {
        return !nombreField.getText().isEmpty() &&
               !apellidoField.getText().isEmpty() &&
               !tipoDocumentoBox.getValue().equals("Seleccione un tipo de documento") &&
               !documentoField.getText().isEmpty() &&
               !correoField.getText().isEmpty();
    }

    
}
