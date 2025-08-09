package com.cinemax.salas.controladores;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.salas.modelos.entidades.*;
import com.cinemax.salas.servicios.ButacaService;
import com.cinemax.salas.servicios.SalaService;
import com.cinemax.salas.modelos.entidades.SalaFactory;
import com.cinemax.salas.modelos.entidades.SalaNormalFactory;
import com.cinemax.salas.modelos.entidades.SalaVIPFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controlador de administración de Salas.
 *
 * Responsabilidades:
 * - Listar, crear, actualizar y eliminar salas
 * - Validar entradas del formulario (nombre, capacidad, tipo, estado)
 * - Sincronizar la tabla y el formulario con la base de datos
 * - Generar butacas automáticamente al crear una sala (según tipo/capacidad)
 * - Buscar sala por ID y mostrar resultados
 *
 * Flujo general:
 * 1) initialize(): configura combos, columnas, listeners y carga salas
 * 2) onGuardar(): decide crear/actualizar según haya selección
 * 3) crearSala()/actualizarSala()/eliminarSala(): operaciones CRUD
 * 4) listarTodasSalas()/buscarSalaPorId(): consultas
 * 5) onBackAction(): vuelve al portal principal
 */
public class ControladorSalas {

    // ===== CAMPOS VINCULADOS A LA VISTA (FXML) =====

    /** Campo: nombre de la sala (solo letras y espacios) */
    @FXML private TextField txtNombre;
    /** Combo: capacidad predefinida (e.g., 36, 42, 48) */
    @FXML private ComboBox<Integer> cmbCapacidad;
    /** Combo: tipo de sala (NORMAL/VIP...) */
    @FXML private ComboBox<TipoSala> cmbTipo;
    /** Combo: estado de sala (ACTIVA/INACTIVA/...) */
    @FXML private ComboBox<EstadoSala> cmbEstado;

    /** Tabla principal con el listado de salas */
    @FXML private TableView<Sala> tablaSalas;
    /** Columna: ID de la sala */
    @FXML private TableColumn<Sala, Integer> colId;
    /** Columna: Nombre de la sala */
    @FXML private TableColumn<Sala, String> colNombre;
    /** Columna: Capacidad total */
    @FXML private TableColumn<Sala, Integer> colCapacidad;
    /** Columna: Tipo (NORMAL, VIP) */
    @FXML private TableColumn<Sala, String> colTipo;
    /** Columna: Estado (ACTIVA, INACTIVA, etc.) */
    @FXML private TableColumn<Sala, String> colEstado;

    /** Etiqueta con el total de salas mostradas */
    @FXML private Label lblTotalSalas;
    /** Campo: búsqueda por ID exacto */
    @FXML private TextField txtBuscarId;

    /** Botón: eliminar sala seleccionada */
    @FXML private Button btnEliminar;
    /** Botón: guardar (crear/actualizar según modo) */
    @FXML private Button btnGuardar;
    /** Botón: limpiar formulario / nuevo registro */
    @FXML private Button btnNuevo;

    // ===== SERVICIOS, LISTAS Y ESTADO INTERNO =====

    /** Servicio de salas (acceso a datos y lógica de negocio) */
    private final SalaService salaService = new SalaService();
    /** Lista observable que respalda la tabla de salas */
    private final ObservableList<Sala> salas = FXCollections.observableArrayList();
    /** Servicio de butacas (para autogenerar asientos cuando aplique) */
    private final ButacaService butacaService = new ButacaService();
    /** Referencia a la sala en edición (null = modo crear) */
    private Sala salaEnEdicion = null;

    /**
     * Hook de JavaFX: se ejecuta al cargar la vista.
     * - Pobla combos (capacidad/tipo/estado)
     * - Configura columnas de la tabla
     * - Enlaza selección de tabla -> formulario (modo edición)
     * - Carga todas las salas
     * - Activa validaciones reactivas para habilitar/deshabilitar Guardar
     */
    @FXML
    public void initialize() throws Exception {
        // Opciones de capacidad disponibles y una selección inicial
        cmbCapacidad.setItems(FXCollections.observableArrayList(36, 42, 48));
        cmbCapacidad.getSelectionModel().selectFirst();

        // Tipos y estados desde enumeraciones
        cmbTipo.setItems(FXCollections.observableArrayList(TipoSala.values()));
        cmbEstado.setItems(FXCollections.observableArrayList(EstadoSala.values()));

        // Bindings de columnas a propiedades del modelo Sala
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        colCapacidad.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCapacidad()).asObject());
        colTipo.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTipo().name()));
        colEstado.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEstado().name()));

        // La tabla usa la lista observable "salas"
        tablaSalas.setItems(salas);

        // Cargar datos iniciales
        listarTodasSalas();

        // Al seleccionar una fila, poblar formulario y pasar a modo edición
        tablaSalas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtNombre.setText(newSel.getNombre());
                cmbCapacidad.setValue(newSel.getCapacidad());
                cmbTipo.setValue(newSel.getTipo());
                cmbEstado.setValue(newSel.getEstado());
                btnEliminar.setDisable(false);
                salaEnEdicion = newSel;
                actualizarModoFormulario();
            } else {
                limpiarFormulario();
            }
        });

        // Estado inicial de botones y validaciones reactivas
        btnEliminar.setDisable(true);
        txtNombre.textProperty().addListener((obs, oldVal, newVal) -> actualizarEstadoFormulario());
        cmbCapacidad.valueProperty().addListener((obs, oldVal, newVal) -> actualizarEstadoFormulario());
        cmbTipo.valueProperty().addListener((obs, oldVal, newVal) -> actualizarEstadoFormulario());
        cmbEstado.valueProperty().addListener((obs, oldVal, newVal) -> actualizarEstadoFormulario());
        actualizarEstadoFormulario();
    }

    // ===== UTILIDADES DE FORMULARIO / MODO =====

    /** Limpia el formulario y restablece selección/botones a estado inicial. */
    private void limpiarFormulario() {
        txtNombre.clear();
        cmbCapacidad.getSelectionModel().clearSelection();
        cmbTipo.getSelectionModel().clearSelection();
        cmbEstado.getSelectionModel().clearSelection();
        tablaSalas.getSelectionModel().clearSelection();
        btnEliminar.setDisable(true);
    }

    /**
     * Cambia etiqueta y visibilidad según el modo (crear vs actualizar).
     * - Crear: botón dice "Crear", oculta "Nuevo"
     * - Actualizar: botón dice "Actualizar", muestra "Nuevo"
     */
    private void actualizarModoFormulario() {
        if (salaEnEdicion == null) {
            btnGuardar.setText("Crear");
            btnNuevo.setVisible(false);
            btnNuevo.setManaged(false);
        } else {
            btnGuardar.setText("Actualizar");
            btnNuevo.setVisible(true);
            btnNuevo.setManaged(true);
        }
        actualizarEstadoFormulario();
    }

    /** Acción del botón "Nuevo/Limpiar": reset a modo crear y limpia campos. */
    @FXML
    private void onLimpiarFormulario(ActionEvent event) {
        limpiarFormulario();
        salaEnEdicion = null;
        actualizarModoFormulario();
    }

    /** Habilita/Deshabilita el botón Guardar según si el formulario está completo. */
    private void actualizarEstadoFormulario() {
        if (btnGuardar != null) {
            boolean formularioValido = validarFormularioCompleto();
            btnGuardar.setDisable(!formularioValido);
        }
    }

    /** Verificación rápida de presencia de datos requeridos (no vacíos / no nulos). */
    private boolean validarFormularioCompleto() {
        return txtNombre != null && !txtNombre.getText().trim().isEmpty()
                && cmbCapacidad != null && cmbCapacidad.getValue() != null
                && cmbTipo != null && cmbTipo.getValue() != null
                && cmbEstado != null && cmbEstado.getValue() != null;
    }

    // ===== ACCIONES PRINCIPALES =====

    /**
     * Acción del botón Guardar.
     * - Si no hay selección -> crearSala()
     * - Si hay selección -> actualizarSala()
     * - Tras actualizar, vuelve a modo crear
     */
    @FXML
    private void onGuardar(ActionEvent event) {
        if (salaEnEdicion == null) {
            // Crear nueva sala
            crearSala();
        } else {
            // Actualizar sala existente
            actualizarSala();
            salaEnEdicion = null;
            actualizarModoFormulario();
        }
    }

    /**
     * Regresa al portal principal cargando la vista correspondiente.
     */
    public void onBackAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== CARGA Y LISTADO =====

    /** Carga todas las salas desde el servicio y limpia el formulario. */
    private void cargarSalas() throws Exception {
        salas.setAll(salaService.listarSalas());
        limpiarFormulario();
    }

    /**
     * Lista todas las salas y actualiza el contador. Maneja errores mostrando alertas.
     */
    @FXML
    private void listarTodasSalas() {
        try {
            cargarSalas();
            lblTotalSalas.setText("Total Salas: " + salas.size());
        } catch (Exception e) {
            lblTotalSalas.setText("Total Salas: 0");
            ManejadorMetodosComunes.mostrarVentanaError("Hubo un error inesperado cargando las salas: " + e.getMessage());
        }
    }

    // ===== CREAR / ACTUALIZAR / ELIMINAR =====

    /**
     * Crea una sala nueva:
     * - Valida nombre (solo letras y espacios)
     * - Verifica duplicidad por nombre
     * - Usa Factories (VIP/Normal) para instanciar la sala según tipo
     * - Persiste y regenera listado
     * - Intenta crear butacas automáticamente (si aplica) y muestra mensajes
     */
    @FXML
    private void crearSala() {
        try {
            String nombreSala = txtNombre.getText().trim();

            // Validación: solo letras y espacios (incluye acentos y ñ/Ñ)
            if (!nombreSala.matches("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s]+")) {
                ManejadorMetodosComunes.mostrarVentanaAdvertencia("El nombre de la sala solo puede contener letras y espacios.");
                return;
            }

            // Validación de duplicados (por nombre, case-insensitive)
            boolean existe = salaService.listarSalas().stream()
                    .anyMatch(s -> s.getNombre().equalsIgnoreCase(nombreSala));
            if (existe) {
                ManejadorMetodosComunes.mostrarVentanaAdvertencia("Ya existe una sala con el nombre \"" + nombreSala + "\". Por favor elige otro nombre.");
                return;
            }

            // Selección de fábrica según tipo de sala
            SalaFactory factory = (cmbTipo.getValue() == TipoSala.VIP)
                    ? new SalaVIPFactory()
                    : new SalaNormalFactory();

            // Construcción del objeto Sala
            Sala sala = factory.crearSala(
                    0,
                    nombreSala,
                    cmbCapacidad.getValue(),
                    cmbEstado.getValue()
            );

            // Persistencia
            salaService.crearSala(sala);

            // Recarga y confirmación
            listarTodasSalas();
            ManejadorMetodosComunes.mostrarVentanaExito("Sala creada exitosamente.\nButacas creadas exitosamente.");

        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("Ya existe una sala con ese nombre")) {
                // Si la sala ya existía en otra transacción, intentamos generar butacas para esa sala
                try {
                    Sala existente = salaService.listarSalas().stream()
                            .filter(s -> s.getNombre().equalsIgnoreCase(txtNombre.getText().trim()))
                            .findFirst()
                            .orElseThrow(() -> new Exception("No se encontró la sala existente"));

                    butacaService.generarButacasAutomatica(existente.getId());

                    listarTodasSalas();
                    ManejadorMetodosComunes.mostrarVentanaExito("Butacas creadas exitosamente para la sala existente \"" +
                            existente.getNombre() + "\".");

                } catch (Exception ex2) {
                    ManejadorMetodosComunes.mostrarVentanaError("No se pudo generar butacas: " + ex2.getMessage());
                }
            } else if (e instanceof NumberFormatException) {
                ManejadorMetodosComunes.mostrarVentanaAdvertencia("La capacidad debe ser un número válido.");
            } else {
                ManejadorMetodosComunes.mostrarVentanaError("" + msg);
            }
        }

        // Siempre limpiar y volver a modo crear al final
        limpiarFormulario();
        actualizarModoFormulario();
    }

    /**
     * Actualiza la sala seleccionada:
     * - Valida nombre (solo letras y espacios)
     * - Verifica duplicidad del nombre excluyendo la propia sala
     * - Actualiza campos y persiste
     * - Refresca lista y muestra confirmación
     */
    @FXML
    private void actualizarSala() {
        Sala seleccionada = tablaSalas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            try {
                String nombreSala = txtNombre.getText().trim();

                // Validación: solo letras y espacios
                if (!nombreSala.matches("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s]+")) {
                    ManejadorMetodosComunes.mostrarVentanaAdvertencia("El nombre de la sala solo puede contener letras y espacios.");
                    return;
                }

                // Evitar duplicados por nombre (ignorando la misma sala)
                boolean existe = salaService.listarSalas().stream()
                        .anyMatch(s -> s.getNombre().equalsIgnoreCase(nombreSala) && s.getId() != seleccionada.getId());
                if (existe) {
                    ManejadorMetodosComunes.mostrarVentanaAdvertencia("Ya existe una sala con el nombre \"" + nombreSala + "\". Por favor elige otro nombre.");
                    return;
                }

                // Aplicar cambios del formulario al objeto seleccionado
                seleccionada.setNombre(nombreSala);
                seleccionada.setCapacidad(cmbCapacidad.getValue());
                seleccionada.setTipo(cmbTipo.getValue());
                seleccionada.setEstado(cmbEstado.getValue());

                // Persistir y refrescar
                salaService.actualizarSala(seleccionada);
                listarTodasSalas();

                ManejadorMetodosComunes.mostrarVentanaExito("Sala actualizada correctamente.");
            } catch (Exception e) {
                if (e instanceof NumberFormatException) {
                    ManejadorMetodosComunes.mostrarVentanaAdvertencia("La capacidad debe ser un número válido.");
                } else {
                    ManejadorMetodosComunes.mostrarVentanaError("" + e.getMessage());
                }
            }
        } else {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Selecciona una sala para actualizar.");
        }
        limpiarFormulario();
    }

    /**
     * Elimina la sala seleccionada (si hay una seleccionada).
     * - Valida que el nombre no esté vacío (ayuda a evitar eliminar "accidentalmente")
     * - Llama al servicio y refresca lista
     * - Muestra mensajes de éxito/error
     */
    @FXML
    private void eliminarSala() {
        Sala seleccionada = tablaSalas.getSelectionModel().getSelectedItem();
        if (txtNombre.getText().trim().isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("El campo 'Nombre de sala' está vacío. Selecciona una sala válida.");
            return;
        }
        if (seleccionada != null) {
            try {
                salaService.eliminarSala(seleccionada.getId());
                listarTodasSalas();
                // limpiarCampos(); // Si tuvieras un método adicional de limpieza
                ManejadorMetodosComunes.mostrarVentanaExito("Sala eliminada correctamente.");
            } catch (Exception e) {
                ManejadorMetodosComunes.mostrarVentanaError("Error inesperado en eliminarSala: " + e.getMessage());
            }
        } else {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Selecciona una sala para eliminar.");
        }
        limpiarFormulario();
    }

    // ===== BÚSQUEDA =====

    /**
     * Busca una sala por ID exacto:
     * - Si el campo está vacío, recarga todas
     * - Si hay un ID válido, muestra solo esa sala (o 0 resultados si no existe)
     * - Maneja errores de formato/consulta con mensajes claros
     */
    @FXML
    private void buscarSalaPorId() {
        String idText = txtBuscarId.getText().trim();
        if (idText.isEmpty()) {
            listarTodasSalas();
            salaEnEdicion = null;
            actualizarModoFormulario();
            return;
        }
        try {
            int id = Integer.parseInt(idText);
            Sala sala = salaService.obtenerSalaPorId(id);
            if (sala != null) {
                salas.setAll(sala);
                lblTotalSalas.setText("Total Salas: 1");
                ManejadorMetodosComunes.mostrarVentanaExito("Sala encontrada.");
            } else {
                salas.clear();
                lblTotalSalas.setText("Total Salas: 0");
                ManejadorMetodosComunes.mostrarVentanaAdvertencia("No existe sala con ID " + id);
            }
        } catch (NumberFormatException e) {
            salas.clear();
            lblTotalSalas.setText("Total Salas: 0");
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("El ID debe ser un número válido.");
        } catch (Exception e) {
            lblTotalSalas.setText("Total Salas: 0");
            ManejadorMetodosComunes.mostrarVentanaError("Error en buscarSalaPorId: " + e.getMessage());
        }
    }
}
