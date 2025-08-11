package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.cinemax.peliculas.modelos.entidades.FormatoFuncion;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.entidades.TipoEstreno;
import com.cinemax.peliculas.modelos.persistencia.FuncionDAO;
import com.cinemax.peliculas.modelos.persistencia.PeliculaDAO;
import com.cinemax.peliculas.servicios.ServicioFuncion;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.servicios.SalaService;
import com.cinemax.utilidades.ManejadorMetodosComunes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Controlador para la gestión integral de funciones cinematográficas.
 *
 * <p>
 * Esta clase maneja la interfaz gráfica que combina la gestión de funciones
 * con un formulario integrado para creación y edición. Proporciona una
 * experiencia
 * unificada donde los usuarios pueden ver, buscar, crear, editar y eliminar
 * funciones desde una sola pantalla.
 *
 * <p>
 * Funcionalidades principales:
 * <ul>
 * <li>Gestión completa de funciones (CRUD) con formulario integrado</li>
 * <li>Visualización en tabla con filtrado y búsqueda en tiempo real</li>
 * <li>Formulario de creación/edición embebido en la misma vista</li>
 * <li>Validaciones en tiempo real y cálculo automático de horarios</li>
 * <li>Modo dual: creación de nuevas funciones y edición de existentes</li>
 * <li>Navegación a detalles completos de funciones</li>
 * <li>Estadísticas y resúmenes informativos</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public class ControladorFunciones implements Initializable {

    /** Servicio para operaciones de negocio con funciones */
    private ServicioFuncion servicioFuncion;

    /** DAO para acceso directo a datos de funciones */
    private FuncionDAO funcionDAO;

    /** DAO para acceso a datos de películas */
    private PeliculaDAO peliculaDAO;

    /** Servicio para operaciones con salas */
    private SalaService salaService;

    // Componentes de la interfaz FXML para búsqueda y tabla
    /** Campo de texto para búsqueda general */
    @FXML
    private TextField txtBuscar;

    /** Tabla principal de funciones */
    @FXML
    private TableView<Funcion> tablaFunciones;

    /** Columna de ID de función */
    @FXML
    private TableColumn<Funcion, Integer> colId;

    /** Columna de película */
    @FXML
    private TableColumn<Funcion, String> colPelicula;

    /** Columna de sala */
    @FXML
    private TableColumn<Funcion, String> colSala;

    /** Columna de fecha y hora de inicio */
    @FXML
    private TableColumn<Funcion, String> colFechaHoraInicio;

    /** Columna de fecha y hora de fin */
    @FXML
    private TableColumn<Funcion, String> colFechaHoraFin;

    /** Columna de formato de proyección */
    @FXML
    private TableColumn<Funcion, String> colFormato;

    /** Columna de tipo de estreno */
    @FXML
    private TableColumn<Funcion, String> colTipoEstreno;

    /** Botón para realizar búsqueda */
    @FXML
    private Button btnBuscar;

    /** Botón para eliminar función seleccionada */
    @FXML
    private Button btnEliminar;

    /** Botón para ver detalles de función */
    @FXML
    private Button btnVerDetalles;

    /** Botón para volver al menú principal */
    @FXML
    private Button btnVolver;

    /** Label que muestra el total de funciones */
    @FXML
    private Label lblTotalFunciones;

    /** Label que muestra estadísticas adicionales */
    @FXML
    private Label lblEstadisticas;

    // Campos del formulario de función integrado
    /** ComboBox para selección de película */
    @FXML
    private ComboBox<Pelicula> cmbPelicula;

    /** ComboBox para selección de sala */
    @FXML
    private ComboBox<Sala> cmbSala;

    /** Selector de fecha */
    @FXML
    private DatePicker dateFecha;

    /** Campo de texto para hora */
    @FXML
    private TextField txtHora;

    /** ComboBox para formato de función */
    @FXML
    private ComboBox<FormatoFuncion> cmbFormato;

    /** ComboBox para tipo de estreno */
    @FXML
    private ComboBox<TipoEstreno> cmbTipoEstreno;

    /** Botón para guardar función */
    @FXML
    private Button btnGuardar;

    /** Botón para crear nueva función */
    @FXML
    private Button btnNuevo;

    /** Botón para limpiar formulario */
    @FXML
    private Button btnLimpiarFormulario;

    // Datos para la gestión de funciones
    /** Lista observable de todas las funciones */
    private ObservableList<Funcion> listaFunciones;

    /** Lista observable de funciones filtradas */
    private ObservableList<Funcion> funcionesFiltradas;

    /** Función actualmente en modo de edición */
    private Funcion funcionEnEdicion = null;

    /**
     * Constructor que inicializa todos los servicios necesarios.
     */
    public ControladorFunciones() {
        this.servicioFuncion = new ServicioFuncion();
        this.funcionDAO = new FuncionDAO();
        this.peliculaDAO = new PeliculaDAO();
        this.salaService = new SalaService();
    }

    /**
     * Inicializa el controlador después de que se ha cargado el FXML.
     * 
     * @param location  La ubicación utilizada para resolver rutas relativas para el
     *                  objeto raíz
     * @param resources Los recursos utilizados para localizar el objeto raíz
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listaFunciones = FXCollections.observableArrayList();
        funcionesFiltradas = FXCollections.observableArrayList();

        configurarTabla();
        configurarEventos();
        configurarFormularioFuncion();
        cargarFunciones();
    }

    /**
     * Maneja el evento de guardar función (crear o actualizar).
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onGuardar(ActionEvent event) {
        try {
            // Validar que todos los campos obligatorios estén completos
            if (!validarFormularioCompleto()) {
                mostrarError("Formulario incompleto", "Por favor complete todos los campos obligatorios");
                return;
            }

            // Validar formato de hora
            LocalTime hora;
            try {
                hora = LocalTime.parse(txtHora.getText().trim());
            } catch (DateTimeParseException e) {
                mostrarError("Error de formato", "Formato de hora inválido. Use HH:MM (ej: 14:30)");
                return;
            }

            LocalDateTime fechaHoraInicio = LocalDateTime.of(dateFecha.getValue(), hora);

            // Validar que la fecha no sea en el pasado
            if (fechaHoraInicio.isBefore(LocalDateTime.now())) {
                mostrarError("Error de validación", "La fecha y hora no puede ser en el pasado");
                return;
            }

            if (funcionEnEdicion == null) {
                // Crear nueva función
                try {
                    Funcion nuevaFuncion = servicioFuncion.crearFuncion(
                            cmbPelicula.getValue(),
                            cmbSala.getValue(),
                            fechaHoraInicio,
                            cmbFormato.getValue(),
                            cmbTipoEstreno.getValue());

                    // Recargar la tabla
                    cargarFunciones();

                    // Seleccionar la nueva función
                    for (Funcion funcion : funcionesFiltradas) {
                        if (funcion.getId() == nuevaFuncion.getId()) {
                            tablaFunciones.getSelectionModel().select(funcion);
                            break;
                        }
                    }

                    // Limpiar el formulario
                    limpiarFormulario();
                    mostrarInformacion("Éxito", "Función creada exitosamente con ID: " + nuevaFuncion.getId());

                } catch (Exception e) {
                    String mensaje = e.getMessage();
                    if (mensaje != null && mensaje.contains("conflicto")) {
                        mostrarError("Conflicto de horarios",
                                "Ya existe una función en esa sala en el horario especificado");
                    } else {
                        mostrarError("Error al crear función", "Error: " + mensaje);
                    }
                }

            } else {
                // Actualizar función existente
                // Guardar el ID antes de cualquier operación
                int idFuncionEditada = funcionEnEdicion.getId();

                try {
                    servicioFuncion.actualizarFuncion(
                            idFuncionEditada,
                            cmbPelicula.getValue(),
                            cmbSala.getValue(),
                            fechaHoraInicio,
                            cmbFormato.getValue(),
                            cmbTipoEstreno.getValue());

                    // Salir del modo edición ANTES de recargar
                    funcionEnEdicion = null;
                    actualizarModoFormulario();

                    // Recargar la tabla
                    cargarFunciones();

                    // Intentar mantener la selección en la función editada usando el ID guardado
                    for (Funcion funcion : funcionesFiltradas) {
                        if (funcion.getId() == idFuncionEditada) {
                            tablaFunciones.getSelectionModel().select(funcion);
                            break;
                        }
                    }

                    mostrarInformacion("Éxito", "Función actualizada correctamente");

                } catch (Exception e) {
                    String mensaje = e.getMessage();
                    if (mensaje != null && mensaje.contains("conflicto")) {
                        mostrarError("Conflicto de horarios",
                                "Ya existe una función en esa sala en el horario especificado");
                    } else {
                        mostrarError("Error al actualizar función", "Error: " + mensaje);
                    }
                }
            }

        } catch (Exception e) {
            mostrarError("Error al guardar función", "Error: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de limpiar formulario.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onLimpiarFormulario(ActionEvent event) {
        limpiarFormulario();
    }

    /**
     * Limpia todos los campos del formulario y resetea el estado.
     */
    private void limpiarFormulario() {
        if (cmbPelicula != null)
            cmbPelicula.setValue(null);
        if (cmbSala != null)
            cmbSala.setValue(null);
        if (dateFecha != null)
            dateFecha.setValue(null);
        if (txtHora != null)
            txtHora.clear();
        if (cmbFormato != null)
            cmbFormato.setValue(null);
        if (cmbTipoEstreno != null)
            cmbTipoEstreno.setValue(null);

        // Resetear modo de edición
        funcionEnEdicion = null;
        actualizarModoFormulario();

        // Limpiar selección de tabla
        tablaFunciones.getSelectionModel().clearSelection();
    }

    /**
     * Actualiza el modo visual del formulario (crear vs editar).
     */
    private void actualizarModoFormulario() {
        if (funcionEnEdicion == null) {
            // Modo crear
            btnGuardar.setText("Crear");
            // Ocultar el botón "Nuevo" cuando está en modo crear
            if (btnNuevo != null) {
                btnNuevo.setVisible(false);
                btnNuevo.setManaged(false);
            }
        } else {
            // Modo editar
            btnGuardar.setText("Actualizar");
            // Mostrar el botón "Nuevo" cuando está en modo editar
            if (btnNuevo != null) {
                btnNuevo.setVisible(true);
                btnNuevo.setManaged(true);
            }
        }
        // Asegurar que se ejecute la validación del formulario
        actualizarEstadoFormulario();
    }

    /**
     * Carga los datos de una función en el formulario para edición.
     * 
     * @param funcion Función a cargar en el formulario
     */
    private void cargarDatosEnFormulario(Funcion funcion) {
        if (funcion == null)
            return;

        // Cargar datos básicos
        cmbPelicula.setValue(funcion.getPelicula());
        cmbSala.setValue(funcion.getSala());
        if (funcion.getFechaHoraInicio() != null) {
            dateFecha.setValue(funcion.getFechaHoraInicio().toLocalDate());
            txtHora.setText(funcion.getFechaHoraInicio().toLocalTime().toString());
        }
        cmbFormato.setValue(funcion.getFormato());
        cmbTipoEstreno.setValue(funcion.getTipoEstreno());

        funcionEnEdicion = funcion;
        actualizarModoFormulario();
    }

    /**
     * Maneja el evento de eliminar función seleccionada.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onEliminarFuncion(ActionEvent event) {
        Funcion funcionSeleccionada = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcionSeleccionada != null) {
            confirmarYEliminarFuncion(funcionSeleccionada);
        }
    }

    /**
     * Confirma y procede con la eliminación de una función.
     * 
     * @param funcion Función a eliminar
     */
    private void confirmarYEliminarFuncion(Funcion funcion) {
        String mensaje = "¿Está seguro de eliminar esta función?\n\n" +
                "Función ID: " + funcion.getId() +
                "\nPelícula: " + funcion.getPelicula().getTitulo() +
                "\n\nATENCIÓN: Esta acción no se puede deshacer.";
        ManejadorMetodosComunes.mostrarVentanaAdvertencia(mensaje);

        try {
            funcionDAO.eliminar(funcion.getId());
            cargarFunciones();
            mostrarInformacion("Éxito", "Función eliminada correctamente");
        } catch (Exception e) {
            manejarErrorEliminacion(e, funcion);
        }
    }

    /**
     * Maneja errores específicos de eliminación de funciones.
     * 
     * @param e       Excepción ocurrida
     * @param funcion Función que se intentó eliminar
     */
    private void manejarErrorEliminacion(Exception e, Funcion funcion) {
        String mensajeError = e.getMessage();
        if (mensajeError != null
                && (mensajeError.contains("foreign key constraint") || mensajeError.contains("violates"))) {
            mostrarErrorRestriccion(funcion);
        } else {
            mostrarError("Error", "No se pudo eliminar la función: " + mensajeError);
        }
    }

    /**
     * Muestra un mensaje de error específico para restricciones de eliminación.
     * 
     * @param funcion Función que no se pudo eliminar
     */
    private void mostrarErrorRestriccion(Funcion funcion) {
        String mensaje = "No se puede eliminar la función ID " + funcion.getId() +
                " porque está asociada con:\n\n" +
                "• Boletos vendidos\n" +
                "• Reservas existentes\n" +
                "• Asientos ocupados\n\n" +
                "OPCIONES:\n" +
                "1. Cancelar todas las reservas primero\n" +
                "2. Esperar a que termine la función\n" +
                "3. Contactar al administrador del sistema";

        ManejadorMetodosComunes.mostrarVentanaError(mensaje);
    }

    /**
     * Maneja el evento de ver detalles de función.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onVerDetalles(ActionEvent event) {
        Funcion funcionSeleccionada = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcionSeleccionada != null) {
            navegarADetallesFuncion(funcionSeleccionada);
        }
    }

    /**
     * Maneja el evento de búsqueda.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onBuscar(ActionEvent event) {
        aplicarFiltros();
    }

    /**
     * Configura la tabla de funciones con sus columnas y eventos.
     */
    private void configurarTabla() {
        configurarColumnas();
        configurarSeleccionTabla();
        tablaFunciones.setItems(funcionesFiltradas);
    }

    /**
     * Configura las columnas de la tabla con sus respectivos cell value factories.
     */
    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colPelicula.setCellValueFactory(cellData -> {
            Pelicula pelicula = cellData.getValue().getPelicula();
            return new javafx.beans.property.SimpleStringProperty(
                    pelicula != null ? pelicula.getTitulo() : "N/A");
        });

        colSala.setCellValueFactory(cellData -> {
            Sala sala = cellData.getValue().getSala();
            return new javafx.beans.property.SimpleStringProperty(
                    sala != null ? sala.getNombre() : "N/A");
        });

        configurarColumnasFechaHora();
        configurarColumnasEnum();
    }

    /**
     * Configura las columnas de fecha y hora con formato apropiado.
     */
    private void configurarColumnasFechaHora() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        colFechaHoraInicio.setCellValueFactory(cellData -> {
            LocalDateTime fechaHora = cellData.getValue().getFechaHoraInicio();
            return new javafx.beans.property.SimpleStringProperty(
                    fechaHora != null ? fechaHora.format(formatter) : "N/A");
        });

        colFechaHoraFin.setCellValueFactory(cellData -> {
            LocalDateTime fechaHora = cellData.getValue().getFechaHoraFin();
            return new javafx.beans.property.SimpleStringProperty(
                    fechaHora != null ? fechaHora.format(formatter) : "N/A");
        });
    }

    /**
     * Configura las columnas de enumeraciones (formato, tipo de estreno).
     */
    private void configurarColumnasEnum() {
        colFormato.setCellValueFactory(cellData -> {
            FormatoFuncion formato = cellData.getValue().getFormato();
            return new javafx.beans.property.SimpleStringProperty(
                    formato != null ? formato.toString() : "N/A");
        });

        colTipoEstreno.setCellValueFactory(cellData -> {
            TipoEstreno tipo = cellData.getValue().getTipoEstreno();
            return new javafx.beans.property.SimpleStringProperty(
                    tipo != null ? tipo.name() : "N/A");
        });
    }

    /**
     * Configura el comportamiento de selección de la tabla.
     */
    private void configurarSeleccionTabla() {
        tablaFunciones.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean funcionSeleccionada = newSelection != null;
                    btnEliminar.setDisable(!funcionSeleccionada);
                    btnVerDetalles.setDisable(!funcionSeleccionada);

                    if (funcionSeleccionada) {
                        cargarDatosEnFormulario(newSelection);
                    } else {
                        limpiarFormulario();
                        funcionEnEdicion = null;
                        actualizarModoFormulario();
                    }
                });
    }

    /**
     * Configura los eventos de la interfaz.
     */
    private void configurarEventos() {
        // Configurar búsqueda en tiempo real
        txtBuscar.textProperty().addListener((obs, oldText, newText) -> aplicarFiltros());
    }

    /**
     * Configura el formulario integrado de función.
     */
    private void configurarFormularioFuncion() {
        configurarComboBoxPeliculas();
        configurarComboBoxSalas();
        configurarComboBoxFormatos();
        configurarComboBoxTiposEstreno();
        configurarValidacionHora();
        configurarValidacionesFormulario();
    }

    /**
     * Configura el ComboBox de películas.
     */
    private void configurarComboBoxPeliculas() {
        if (cmbPelicula != null) {
            try {
                List<Pelicula> peliculas = peliculaDAO.listarTodas();
                cmbPelicula.setItems(FXCollections.observableArrayList(peliculas));
                cmbPelicula.setConverter(new StringConverter<Pelicula>() {
                    @Override
                    public String toString(Pelicula pelicula) {
                        return pelicula != null ? pelicula.getTitulo() : "";
                    }

                    @Override
                    public Pelicula fromString(String string) {
                        return null;
                    }
                });
            } catch (SQLException e) {
                // Error manejado silenciosamente
            }
        }
    }

    /**
     * Configura el ComboBox de salas.
     */
    private void configurarComboBoxSalas() {
        if (cmbSala != null) {
            try {
                List<Sala> salas = salaService.listarSalas();
                cmbSala.setItems(FXCollections.observableArrayList(salas));
                cmbSala.setConverter(new StringConverter<Sala>() {
                    @Override
                    public String toString(Sala sala) {
                        return sala != null ? sala.getNombre() + " (" + sala.getTipo() + ")" : "";
                    }

                    @Override
                    public Sala fromString(String string) {
                        return null;
                    }
                });
            } catch (Exception e) {
                // Error manejado silenciosamente
            }
        }
    }

    /**
     * Configura el ComboBox de formatos.
     */
    private void configurarComboBoxFormatos() {
        if (cmbFormato != null) {
            cmbFormato.setItems(FXCollections.observableArrayList(FormatoFuncion.values()));
        }
    }

    /**
     * Configura el ComboBox de tipos de estreno.
     */
    private void configurarComboBoxTiposEstreno() {
        if (cmbTipoEstreno != null) {
            cmbTipoEstreno.setItems(FXCollections.observableArrayList(TipoEstreno.values()));
        }
    }

    /**
     * Configura la validación de entrada para el campo de hora.
     */
    private void configurarValidacionHora() {
        if (txtHora != null) {
            txtHora.textProperty().addListener((obs, oldText, newText) -> {
                // Permitir solo formato HH:MM
                if (!newText.matches("\\d{0,2}:?\\d{0,2}")) {
                    txtHora.setText(oldText);
                }
            });
        }
    }

    /**
     * Configura las validaciones del formulario en tiempo real.
     */
    private void configurarValidacionesFormulario() {
        Runnable validarFormulario = this::actualizarEstadoFormulario;

        if (cmbPelicula != null) {
            cmbPelicula.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
        }
        if (cmbSala != null) {
            cmbSala.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
        }
        if (dateFecha != null) {
            dateFecha.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
        }
        if (txtHora != null) {
            txtHora.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        }
        if (cmbFormato != null) {
            cmbFormato.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
        }
        if (cmbTipoEstreno != null) {
            cmbTipoEstreno.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
        }
    }

    /**
     * Actualiza el estado del formulario y habilita/deshabilita el botón guardar.
     */
    private void actualizarEstadoFormulario() {
        if (btnGuardar != null) {
            boolean formularioValido = validarFormularioCompleto();
            btnGuardar.setDisable(!formularioValido);
        }
    }

    /**
     * Valida que todos los campos obligatorios del formulario estén completos.
     * 
     * @return true si el formulario es válido, false en caso contrario
     */
    private boolean validarFormularioCompleto() {
        return cmbPelicula != null && cmbPelicula.getValue() != null &&
                cmbSala != null && cmbSala.getValue() != null &&
                dateFecha != null && dateFecha.getValue() != null &&
                txtHora != null && !txtHora.getText().trim().isEmpty() &&
                cmbFormato != null && cmbFormato.getValue() != null &&
                cmbTipoEstreno != null && cmbTipoEstreno.getValue() != null;
    }

    /**
     * Carga todas las funciones desde la base de datos.
     */
    private void cargarFunciones() {
        try {
            listaFunciones.clear();
            listaFunciones.addAll(funcionDAO.listarTodasLasFunciones());
            aplicarFiltros();
        } catch (Exception e) {
            mostrarError("Error al cargar funciones", e.getMessage());
        }
    }

    /**
     * Aplica los filtros de búsqueda a la lista de funciones.
     */
    private void aplicarFiltros() {
        funcionesFiltradas.clear();

        String textoBusqueda = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase().trim() : "";

        for (Funcion funcion : listaFunciones) {
            if (cumpleCriteriosBusqueda(funcion, textoBusqueda)) {
                funcionesFiltradas.add(funcion);
            }
        }

        actualizarEstadisticas();
    }

    /**
     * Verifica si una función cumple con los criterios de búsqueda.
     * 
     * @param funcion       Función a evaluar
     * @param textoBusqueda Texto de búsqueda
     * @return true si cumple los criterios, false en caso contrario
     */
    private boolean cumpleCriteriosBusqueda(Funcion funcion, String textoBusqueda) {
        if (textoBusqueda.isEmpty()) {
            return true;
        }

        return String.valueOf(funcion.getId()).contains(textoBusqueda) ||
                (funcion.getPelicula() != null && funcion.getPelicula().getTitulo() != null &&
                        funcion.getPelicula().getTitulo().toLowerCase().contains(textoBusqueda))
                ||
                (funcion.getSala() != null && funcion.getSala().getNombre() != null &&
                        funcion.getSala().getNombre().toLowerCase().contains(textoBusqueda));
    }

    /**
     * Actualiza las estadísticas mostradas en la interfaz.
     */
    private void actualizarEstadisticas() {
        int total = funcionesFiltradas.size();
        lblTotalFunciones.setText("Total de funciones: " + total);

        if (total == 0) {
            lblEstadisticas.setText("No hay funciones que mostrar");
        } else {
            lblEstadisticas.setText("");
        }
    }

    /**
     * Obtiene las funciones de una película específica por su nombre.
     * 
     * <p>
     * Método público que delega la lógica al servicio correspondiente
     * para búsquedas especializadas por película.
     *
     * @param nombrePelicula El título de la película
     * @return Lista de funciones de la película especificada
     */
    public List<Funcion> obtenerFuncionesPorNombrePelicula(String nombrePelicula) {
        try {
            List<Funcion> todasFunciones = servicioFuncion.obtenerFuncionesPorNombrePelicula(nombrePelicula);
            LocalDateTime ahora = LocalDateTime.now();

            // Filtrar funciones para que solo incluyan las que empiezan a partir de ahora
            List<Funcion> funcionesFiltradas = todasFunciones.stream()
                    .filter(funcion -> funcion.getFechaHoraInicio() != null)
                    .filter(funcion -> !funcion.getFechaHoraInicio().isBefore(ahora))
                    .collect(Collectors.toList());

            return funcionesFiltradas;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Navega a la pantalla de detalles de una función específica.
     * 
     * @param funcion Función de la cual mostrar detalles
     */
    private void navegarADetallesFuncion(Funcion funcion) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/peliculas/PantallaDetallesFuncion.fxml"));
            Parent root = loader.load();

            ControladorDetallesFuncion controlador = loader.getController();
            controlador.cargarFuncion(funcion);

            Stage stage = (Stage) btnVerDetalles.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("Error de navegación", "No se pudo abrir los detalles: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de volver al portal principal.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo volver al portal: " + e.getMessage());
        }
    }

    /**
     * Muestra un mensaje de error al usuario.
     * 
     * @param titulo  Título del mensaje
     * @param mensaje Contenido del mensaje de error
     */
    private void mostrarError(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaError(mensaje != null ? mensaje : "Error desconocido");
    }

    /**
     * Muestra un mensaje informativo al usuario.
     * 
     * @param titulo  Título del mensaje
     * @param mensaje Contenido del mensaje informativo
     */
    private void mostrarInformacion(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaExito(mensaje != null ? mensaje : "Operación completada");
    }
}
