package com.cinemax.venta_boletos.controladores;

import com.cinemax.venta_boletos.servicios.ServicioTemporizador;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.utilidades.ControladorCargaConDatos;
import com.cinemax.utilidades.ManejadorMetodosComunes;
import com.cinemax.utilidades.estrategiaValidacionDocumentos.ContextoValidacion;
import com.cinemax.utilidades.estrategiaValidacionDocumentos.EstrategiaCedulaValidacion;
import com.cinemax.utilidades.estrategiaValidacionDocumentos.EstrategiaPasaporteValidacion;
import com.cinemax.utilidades.estrategiaValidacionDocumentos.EstrategiaRucValidacion;
import com.cinemax.venta_boletos.modelos.entidades.Boleto;
import com.cinemax.venta_boletos.modelos.entidades.CalculadorIVA;
import com.cinemax.venta_boletos.modelos.entidades.Cliente;
import com.cinemax.venta_boletos.modelos.entidades.Producto;
import com.cinemax.venta_boletos.modelos.persistencia.BoletoDAO;
import com.cinemax.venta_boletos.servicios.ServicioContenidoFactura;
import com.cinemax.venta_boletos.servicios.ServicioCliente;
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
import java.util.regex.Pattern;

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

    /** Botón para crear o actualizar un cliente. */
    @FXML
    private Button buttonCrearOActualizar;

    /** Botón para solicitar crear un nuevo cliente. */
    @FXML
    private Button buttonNuevoCliente;

    /** Botón para limpiar el formulario. */
    @FXML
    private Button buttonLimpiarFormulario;

    // ===== ATRIBUTOS DE LÓGICA =====

    /** Lista de productos seleccionados, representando los boletos de cine. */
    private List<Producto> boletos;

    /** Cliente que está siendo editado (si aplica). */
    private Cliente clienteEnEdicion = null;

    private double xOffset = 0;
    private double yOffset = 0;

    /** Servicio que gestiona la lógica de facturación (generación de factura con los boletos asociados, validaciones). */
    private final ServicioFacturacion servicioFacturacion = new ServicioFacturacion();

    /** Servicio que gestiona la lógica de cliente (creación, actualización, búsqueda de clientes). */
    private final ServicioCliente servicioCliente = new ServicioCliente();

    /** Controlador del panel lateral que muestra información de la función. */
    private ControladorInformacionDeVenta controladorInformacionDeVenta;

    private BoletoDAO boletoDAO = new BoletoDAO();

    /**
     * Inicializa los elementos gráficos de la vista y configura los eventos asociados.
     *
     * Proceso de inicialización:
     * 1. Configura el ComboBox de tipo de documento con opciones predefinidas.
     * 2. Establece el valor por defecto "Seleccione un tipo de documento".
     * 3. Vincula el Label del temporizador a la propiedad del ServicioTemporizador.
     * 4. Configura eventos para permitir mover la ventana arrastrando el header.
     * 5. Añade listeners para validar campos y actualizar el estado del formulario.
     * 6. Aplica filtros de entrada a los campos para aceptar únicamente caracteres válidos.
     */
    @FXML
    public void initialize() {
        // 1. Establecer los valores disponibles para el tipo de documento.
        tipoDocumentoBox.setItems(FXCollections.observableArrayList("Cédula", "Pasaporte", "RUC"));

         // 2. Seleccionar "Cédula" como valor por defecto.
        tipoDocumentoBox.setValue("Seleccione un tipo de documento");

        // 3. Vincular el label del temporizador
        if (timerLabel != null) {
            timerLabel.textProperty().bind(ServicioTemporizador.getInstancia().tiempoRestanteProperty());
        }

        // 4. Configurar eventos para permitir mover la ventana arrastrando el header.
        headerBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        headerBar.setOnMouseDragged(event -> {
            ((Stage) headerBar.getScene().getWindow()).setX(event.getScreenX() - xOffset);
            ((Stage) headerBar.getScene().getWindow()).setY(event.getScreenY() - yOffset);
        });

        // 5. Añadir listeners para validar campos y actualizar el estado del formulario.
        nombreField.textProperty().addListener((o, ov, nv) -> actualizarEstadoFormulario());
        apellidoField.textProperty().addListener((o, ov, nv) -> actualizarEstadoFormulario());
        documentoField.textProperty().addListener((o, ov, nv) -> actualizarEstadoFormulario());
        correoField.textProperty().addListener((o, ov, nv) -> actualizarEstadoFormulario());
        tipoDocumentoBox.valueProperty().addListener((o, ov, nv) -> actualizarEstadoFormulario());

        actualizarEstadoFormulario();

        // 6. Filtros de entrada para los campos de texto.
        nombreField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            String filteredValue = newValue.replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚñÑ ]", "");
            if (!nombreField.getText().equals(filteredValue)) {
                nombreField.setText(filteredValue);
            }
        });

        apellidoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            String filteredValue = newValue.replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚñÑ ]", "");
            if (!apellidoField.getText().equals(filteredValue)) {
                apellidoField.setText(filteredValue);
            }
        });

        documentoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            String filteredValue = newValue.replaceAll("[^0-9]", "");
            if (!documentoField.getText().equals(filteredValue)) {
                documentoField.setText(filteredValue);
            }
        });

        identificacionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            String filteredValue = newValue.replaceAll("[^0-9]", "");
            if (!identificacionField.getText().equals(filteredValue)) {
                identificacionField.setText(filteredValue);
            }
        });

        // Configurar el cierre de ventana cuando esté disponible
        configurarCierreDeVentanaConListener();
    }


    /**
     * Carga los boletos seleccionados en la vista y actualiza la sección lateral con la información de la venta.
     *
     * Proceso de inicialización:
     * 1. Guarda la lista de boletos recibida en la variable local.
     * 2. Verifica que el controlador de información lateral esté inicializado.
     * 3. Obtiene la vista asociada al controlador lateral.
     * 4. Limpia el contenedor lateral y agrega la vista obtenida.
     * 5. Calcula el total a pagar según los boletos seleccionados.
     *
     * @param boletos Lista de productos (boletos) seleccionados por el usuario.
     */
    public void cargarBoletosSeleccionados(List<Producto> boletos) {
        this.boletos = boletos;

        // 2. Validar si el controlador lateral está inicializado correctamente.
        if(controladorInformacionDeVenta == null) {
            ManejadorMetodosComunes.mostrarVentanaError("Controlador de información lateral no inicializado.");
            return;
        }

        // 3. Obtener la vista asociada al controlador lateral.
        Parent vistaInformacionLateral = controladorInformacionDeVenta.getRoot(); 
        
        // 4. Limpiar el contenedor y cargar la vista de información lateral.
        informacionFuncionContainer.getChildren().clear(); 
        informacionFuncionContainer.getChildren().add(vistaInformacionLateral);

        // 5. Calcular el total a pagar por los boletos seleccionados.
        //controladorInformacionDeVenta.calcularTotal();
    }

    /**
    * Maneja el evento de búsqueda de cliente por número de identificación.
    * Limpia los campos de texto y busca al cliente en la base de datos.
    * Si el cliente es encontrado, llena los campos con su información.
    * Si no se encuentra, muestra un mensaje de advertencia.
    * @param event Evento de acción al hacer clic en el botón de búsqueda.
    */
    @FXML
    private void buscarCliente(ActionEvent event) {
        // Validar que el campo de identificación no esté vacío.
        if (identificacionField.getText().isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, ingrese un número de identificación para buscar al cliente.");
            return;
        }

        // Buscar al cliente por su número de identificación y colocarlo en la variable local clienteEnEdicion.
        clienteEnEdicion = servicioCliente.buscarCliente(identificacionField.getText());

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
    }

    /**
     * Valida el documento ingresado por el usuario según el tipo de documento seleccionado.
     * Limpia el campo de texto de espacios innecesarios y aplica la estrategia de validación correspondiente.
     * Si el documento es inválido, muestra un mensaje de error y retorna false.
     * @return true si el documento es válido, false en caso contrario.
     */
    private boolean validarNumeroDocumentoCliente() {

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
        // Si no es válido, muestra un m90ensaje de error y retorna false.
        if(!contextoValidacion.ejecutarEstrategia(documentoField.getText())) {
            ManejadorMetodosComunes.mostrarVentanaError("Documento inválido: " + documentoField.getText());
            return false;
        }

        return true;
    }

    /**
     * Maneja el evento de creación o actualización de cliente.
     * Valida los campos de entrada y crea o actualiza la información del cliente en la base de datos.
     * Si hay un error al crear o actualizar, muestra un mensaje de error.
     * @param event Evento de acción al hacer clic en el botón de crear o actualizar cliente.
     */
    @FXML
    void crearOActualizarCliente(ActionEvent event) {

        // Validar que el documento sea válido antes de continuar con la creación o actualización.
        if (!validarNumeroDocumentoCliente()) {
            return; 
        }

        // Validar que todos los campos estén llenos antes de proceder con la creación o actualización.
        if (!validarFormularioCompleto()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Llene todos los campos para continuar");
            return;
        }

        // Validar el formato del correo electrónico.
        if (!Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                    .matcher(correoField.getText().trim())
                    .matches()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Formato de correo inválido");
            return;
        }

        if (clienteEnEdicion == null) {
            crearCliente();
        }
        else {
            actualizarCliente();
        }
        
    }

    /**
     * Maneja la acción de pago al hacer clic en el botón de pagar.
     * Valida los campos de entrada, verifica si se ha confirmado la compra y si el documento es válido.
     * Si todo es correcto, genera la factura y los boletos, y muestra un mensaje de éxito.
     * Si hay algún error durante el proceso, muestra un mensaje de error.
     * @param event Evento de acción al hacer clic en el botón de pagar.
     */
    @FXML
    private void pagarBoletos(ActionEvent event) {
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
        if(!validarNumeroDocumentoCliente()) {
            return;
        }

        // Crear al cliente en caso de no haber dado clic en el botón de crear.
        Cliente cliente = null;
        
        cliente = new Cliente(
        nombreField.getText(),
        apellidoField.getText(),
        documentoField.getText(),
        correoField.getText(),
        tipoDocumentoBox.getValue());

        // Si el cliente no existe, crear uno nuevo con los datos ingresados.
        if (!servicioCliente.existeCliente(documentoField.getText())) {
            servicioCliente.crearCliente(cliente);
        }

        // Redirigir al usuario a la pantalla principal del portal de empleados.
        ManejadorMetodosComunes.mostrarPantallaDeCargaOptimizada((Stage) buttonPagar.getScene().getWindow(), "/vistas/empleados/PantallaPortalPrincipal.fxml", 30, 225);
        //ManejadorMetodosComunes.cambiarVentana((Stage) buttonPagar.getScene().getWindow(), "/vistas/empleados/PantallaPortalPrincipal.fxml", "CineMAX");

        // Generar los boletos y la factura.
        ServicioContenidoFactura generador = new ServicioContenidoFactura();
        generador.generarBoletos(boletos);
        CalculadorImpuesto calculadorImpuesto = new CalculadorIVA();
        servicioFacturacion.generarFactura(this.boletos, cliente,calculadorImpuesto);

        // Detener el temporizador después de un pago exitoso.
        ServicioTemporizador.getInstancia().detenerTemporizador();

        realizarAccionesAntesDeCerrarVentana();

    }

    /**
     * Maneja la acción de retroceso al hacer clic en el botón de retroceso.
     * Cierra la ventana actual y redirige al usuario a la pantalla de selección de butacas.
     * Si ocurre un error durante el proceso, muestra un mensaje de error.
     * @param event Evento de acción al hacer clic en el botón de retroceso.
     */
    @FXML
    protected void regresarAMapaButacas(ActionEvent event) {
        try {

            // Obtener la ventana actual desde el headerBar.
            Stage currentStage = (Stage) headerBar.getScene().getWindow();

            List<Butaca> butacasSeleccionadas = new ArrayList<>();
            for (Producto boleto : boletos) {
                butacasSeleccionadas.add(((Boleto) boleto).getButaca());
            }

            // Crear el controlador de carga para la vista de selección de butacas,
            // pasando la ruta FXML, la ventana actual y la función asociada al primer boleto
            List<Object> datosTransferencia = new ArrayList<>();
            datosTransferencia.add(((Boleto) boletos.get(0)).getFuncion());
            datosTransferencia.add(butacasSeleccionadas);
            
            // Debug: Verificar que se están pasando los datos correctamente
            System.out.println("DEBUG - Datos transferencia size: " + datosTransferencia.size());
            System.out.println("DEBUG - Butacas seleccionadas size: " + butacasSeleccionadas.size());
            
            ControladorCargaConDatos controladorCargaConDatos = new ControladorCargaAsignacionButacas(
                "/vistas/venta_boletos/VistaSeleccionButacas.fxml",
                currentStage,
                datosTransferencia
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
    public void setControladorInformacionDeVenta(ControladorInformacionDeVenta controladorInformacionLateral) {
        this.controladorInformacionDeVenta = controladorInformacionLateral;
    }

    /**
     * Maneja la acción de limpiar el formulario al hacer clic en el botón de limpiar.
     * @param event Evento de acción al hacer clic en el botón de limpiar.
     */
    @FXML
    private void onLimpiarFormulario(ActionEvent event) {
        limpiarFormulario();
    }

    /**
     * Limpia los campos del formulario.
     */
    private void limpiarFormulario() {
        nombreField.clear();
        apellidoField.clear();
        documentoField.clear();
        correoField.clear();
        identificacionField.clear();
        tipoDocumentoBox.setValue("Seleccione un tipo de documento");

        clienteEnEdicion = null;
        actualizarModoFormulario();
    }

    /**
     * Actualiza el modo del formulario (crear/editar).
     */
    private void actualizarModoFormulario() {
        if (clienteEnEdicion == null) {
            // Modo crear
            buttonCrearOActualizar.setText("Crear");
            tipoDocumentoBox.setDisable(false);
            documentoField.setDisable(false);
            // Ocultar el botón "Nuevo" cuando está en modo crear
            if (buttonNuevoCliente != null) {
                buttonNuevoCliente.setVisible(false);
                buttonNuevoCliente.setManaged(false);
            }
        } else {
            // Modo editar
            buttonCrearOActualizar.setText("Actualizar");
            tipoDocumentoBox.setDisable(true);
            documentoField.setDisable(true);
            // Mostrar el botón "Nuevo" cuando está en modo editar
            if (buttonNuevoCliente != null) {
                buttonNuevoCliente.setVisible(true);
                buttonNuevoCliente.setManaged(true);
            }
        }
        // Asegurar que se ejecute la validación del formulario
        actualizarEstadoFormulario();
    }

    /**
     * Actualiza el estado del formulario (habilitar/deshabilitar el botón de crear/actualizar).
     */
    private void actualizarEstadoFormulario() {
        if (buttonCrearOActualizar != null) {
            boolean formularioValido = validarFormularioCompleto();
            buttonCrearOActualizar.setDisable(!formularioValido);
        }
    }

    /**
     * Valida que todos los campos del formulario estén completos.
     * @return true si el formulario es válido, false en caso contrario.
     */
    private boolean validarFormularioCompleto() {
        return !nombreField.getText().isEmpty() &&
               !apellidoField.getText().isEmpty() &&
               !tipoDocumentoBox.getValue().equals("Seleccione un tipo de documento") &&
               !documentoField.getText().isEmpty() &&
               !correoField.getText().isEmpty();
    }

    /**
     * Crea un nuevo cliente.
     */
    private void crearCliente(){
        if (servicioCliente.existeCliente(documentoField.getText())) {
            ManejadorMetodosComunes.mostrarVentanaError("El cliente que intenta crear ya existe.");
            return;
        }
        Cliente cliente = new Cliente(nombreField.getText(), apellidoField.getText(), documentoField.getText(),
                correoField.getText(), tipoDocumentoBox.getValue());
        servicioCliente.crearCliente(cliente);
        ManejadorMetodosComunes.mostrarVentanaExito("Cliente creado exitosamente.");
    }

    /**
     * Actualiza un cliente existente.
     */
    private void actualizarCliente(){
        clienteEnEdicion = new Cliente(nombreField.getText(), apellidoField.getText(), documentoField.getText(),
                correoField.getText(), tipoDocumentoBox.getValue());
        // Verificar si el cliente existe en la base de datos.
        if (!servicioCliente.existeCliente(clienteEnEdicion.getIdCliente())) {
            // Si el cliente no existe, mostrar un mensaje de advertencia.
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("El cliente no existe, tiene que registrarlo primero.");
        } else {
            // Si el cliente existe, actualizar su información en la base de datos.
            servicioCliente.actualizarCliente(clienteEnEdicion);
            actualizarModoFormulario();
            ManejadorMetodosComunes.mostrarVentanaExito("Cliente actualizado exitosamente.");
        }
    }

    /**
     * Configura el manejo del evento de cierre de ventana usando un listener
     * que se activa cuando la escena está disponible
     */
    private void configurarCierreDeVentanaConListener() {
        // Agregar un listener a la propiedad scene del botón
        buttonPagar.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                System.out.println("Escena detectada en ControladorFacturacion, configurando cierre de ventana...");
                // Agregar un listener adicional para cuando la ventana esté disponible
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        System.out.println("Ventana detectada en ControladorFacturacion, configurando evento de cierre...");
                        configurarCierreDeVentana((Stage) newWindow);
                    }
                });
                
                // Si la ventana ya está disponible, configurar inmediatamente
                if (newScene.getWindow() != null) {
                    System.out.println("Ventana ya disponible en ControladorFacturacion, configurando evento de cierre...");
                    configurarCierreDeVentana((Stage) newScene.getWindow());
                }
            }
        });
        
        // Si la escena ya está disponible, configurar inmediatamente
        if (buttonPagar.getScene() != null) {
            System.out.println("Escena ya disponible en ControladorFacturacion, configurando cierre...");
            if (buttonPagar.getScene().getWindow() != null) {
                configurarCierreDeVentana((Stage) buttonPagar.getScene().getWindow());
            }
        }
    }

    /**
     * Configura el manejo del evento de cierre de ventana
     */
    private void configurarCierreDeVentana(Stage stage) {
        try {
            System.out.println("Configurando evento setOnCloseRequest en ControladorFacturacion...");
            // Interceptar el evento de cierre (cuando presionan la "X")
            stage.setOnCloseRequest(e -> {
                System.out.println("Usuario cerró la ventana desde ControladorFacturacion - Liberando reservas...");
                realizarAccionesAntesDeCerrarVentana();
            });
            System.out.println("Evento de cierre configurado exitosamente en ControladorFacturacion!");
            
        } catch (Exception e) {
            System.err.println("Error al configurar el cierre de ventana en ControladorFacturacion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Libera las reservas cuando se cierra la ventana
     */
    private void realizarAccionesAntesDeCerrarVentana() {
        try {
            // Detener temporizador
            ServicioTemporizador.getInstancia().detenerTemporizador();
            
            // Liberar reservas de este session
            if (ServicioTemporizador.getInstancia().getIdDeSesion() != null) {
                boletoDAO.liberarTodasButacasReservadasTemporalmentePorSession(ServicioTemporizador.getInstancia().getIdDeSesion());
                System.out.println("Reservas liberadas para session: " + ServicioTemporizador.getInstancia().getIdDeSesion());
            }
            
        } catch (Exception e) {
            System.err.println("Error al liberar reservas al cerrar: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
