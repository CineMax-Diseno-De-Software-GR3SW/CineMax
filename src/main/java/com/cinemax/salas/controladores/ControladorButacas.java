package com.cinemax.salas.controladores;

import com.cinemax.utilidades.ManejadorMetodosComunes;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.EstadoButaca;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.servicios.ServicioButaca;
import com.cinemax.salas.servicios.ServicioSala;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
/**
 * Controlador para la gestión de butacas en la interfaz de administración.
 *
 * Responsabilidades:
 * - Cargar salas/estados y poblar la tabla de butacas
 * - Crear, actualizar y eliminar butacas
 * - Validar datos del formulario
 * - Mantener sincronizada la vista con la base de datos
 * - Navegar de regreso al portal principal
 *
 * Flujo:
 * 1) initialize(): carga de catálogos y bindings de tabla/formulario
 * 2) listarButacasPorSala(): consulta por sala (o todas)
 * 3) crear/actualizar/eliminar: operaciones CRUD
 * 4) onGuardar(): decide crear vs actualizar según modo
 * 5) onBackAction(): regresar al portal
 */
public class ControladorButacas {

    // ===== ELEMENTOS DE LA INTERFAZ (FXML) =====
    /** Búsqueda por ID de sala (o criterios simples) */
    @FXML private TextField txtBuscarIdSala;
    /** Tabla principal de butacas */
    @FXML private TableView<Butaca> tablaButacas;
    /** Columna: ID butaca */
    @FXML private TableColumn<Butaca, Integer> colId;
    /** Columna: Fila (letra) */
    @FXML private TableColumn<Butaca, String> colFila;
    /** Columna: Columna (número) */
    @FXML private TableColumn<Butaca, String> colColumna;
    /** Columna: Estado (DISPONIBLE, OCUPADA, etc.) */
    @FXML private TableColumn<Butaca, String> colEstado;
    /** Columna: ID de sala */
    @FXML private TableColumn<Butaca, String> colIdSala;
    // ===== CAMPOS DEL FORMULARIO =====
    /** Campo: fila de la butaca (letra A-Z) */
    @FXML private TextField txtFila;
    /** Campo: columna de la butaca (número) */
    @FXML private TextField txtColumna;
    /** Selector: estado de la butaca */
    @FXML private ComboBox<EstadoButaca> cmbEstado;
    /** Selector: sala asociada */
    @FXML private ComboBox<Sala> cmbSala;

    // ===== BOTONES Y ETIQUETAS =====
    /** Botón: eliminar butaca seleccionada */
    @FXML private Button btnEliminar;
    /** Etiqueta: total de butacas en la tabla */
    @FXML private Label lblTotalButacas;
    /** Botón: crear/actualizar butaca según modo */
    @FXML private Button btnGuardar;
    /** Botón: limpiar formulario / nuevo registro */
    @FXML private Button btnNuevo;

    // ===== MODELOS OBSERVABLES Y SERVICIOS =====
    /** Catálogo de salas para el ComboBox */
    private final ObservableList<Sala>   salas   = FXCollections.observableArrayList();
    /** Lista observable de butacas para la tabla */
    private final ObservableList<Butaca> butacas = FXCollections.observableArrayList();
    /** Referencia a la butaca en edición (null = modo crear) */
    private Butaca butacaEnEdicion = null;
    /** Servicio de salas (catálogo) */
    private final ServicioSala servicioSala = new ServicioSala();
    /** Servicio de butacas (CRUD) */
    private final ServicioButaca servicio     = new ServicioButaca();

    /**
     * Hook de JavaFX: se ejecuta al cargar la vista.
     * - Carga estados y salas en ComboBox
     * - Configura columnas de la tabla
     * - Enlaza selección de tabla -> formulario
     * - Carga butacas iniciales
     * - Configura validaciones reactivas para habilitar/deshabilitar Guardar
     */
    public void initialize() throws Exception {
        // Estados disponibles (enum)
        cmbEstado.setItems(FXCollections.observableArrayList(
                Arrays.stream(EstadoButaca.values())
                        .filter(e -> !e.name().equals("OCUPADA"))
                        .toList()
        ));
        // Catálogo de salas
        salas.setAll(servicioSala.listarSalas());
        cmbSala.setItems(salas);

        // Bindings de columnas a propiedades del modelo Butaca
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colFila.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFila()));
        colColumna.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getColumna()));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstado()));
        colIdSala.setCellValueFactory(data -> {
            int idSala = data.getValue().getIdSala();
            Sala sala = salas.stream()
                    .filter(s -> s.getId() == idSala)
                    .findFirst()
                    .orElse(null);
            String nombreSala = (sala != null) ? sala.getNombre() : "Desconocida";
            return new SimpleStringProperty(nombreSala);
        });
        // Origen de datos de la tabla
        tablaButacas.setItems(butacas);

        // Al seleccionar en la tabla, poblar el formulario y cambiar a modo edición
        tablaButacas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                txtFila.setText(sel.getFila());
                txtColumna.setText(sel.getColumna());
                cmbEstado.setValue(EstadoButaca.valueOf(sel.getEstado()));
                // Resolver la sala por ID para mostrar el objeto seleccionado
                Sala salaSeleccionada = salas.stream()
                        .filter(s -> s.getId() == sel.getIdSala())
                        .findFirst()
                        .orElse(null);
                cmbSala.setValue(salaSeleccionada);
                btnEliminar.setDisable(false);
                butacaEnEdicion = sel;
                actualizarModoFormulario();
            } else {
                limpiarFormulario();
            }
        });

        // Carga inicial (todas las butacas)
        listarButacasPorSala(null);

        // Validación reactiva: al cambiar campos, recalcular estado del botón Guardar
        txtFila.textProperty().addListener((obs, o, n) -> actualizarEstadoFormulario());
        txtColumna.textProperty().addListener((obs, o, n) -> actualizarEstadoFormulario());
        cmbEstado.valueProperty().addListener((obs, o, n) -> actualizarEstadoFormulario());
        cmbSala.valueProperty().addListener((obs, o, n) -> actualizarEstadoFormulario());

        actualizarEstadoFormulario();
    }

    /**
     * Valida campos del formulario antes de crear/actualizar.
     * - Fila: letra A-Z
     * - Columna: dígito 1-9 (nota: el mensaje dice 0-9 pero regex actual es [1-9])
     * - Estado y Sala: obligatorios
     *
     * @return true si los campos pasan validación; false si hay errores (y muestra alerta)
     */
    private boolean validarCampos() {
        String fila = txtFila.getText().trim();
        String columna = txtColumna.getText().trim();
        EstadoButaca estado = cmbEstado.getValue();
        Sala sala = cmbSala.getValue();

        if (fila.isEmpty() || columna.isEmpty() || estado == null || sala == null) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, complete todos los campos obligatorios.");
            return false;
        }
        if (!fila.matches("[A-Za-z]")) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("El campo Fila solo debe contener una letra de la A a la Z.");
            return false;
        }
        if (!columna.matches("[1-9]")) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("El campo Columna solo debe contener un número del 0 al 9.");
            return false;
        }
        return true;
    }

    /**
     * Lista butacas filtrando por ID de sala (si se ingresó), o todas si está vacío.
     * - Actualiza tabla y etiqueta de total
     * - Maneja errores limpiando la tabla y mostrando total 0
     */
    @FXML
    private void listarButacasPorSala(ActionEvent e) {
        try {
            String txt = txtBuscarIdSala.getText().trim();
            List<Butaca> lista;
            if (txt.isEmpty()) {
                lista = servicio.listarTodasButacas();
            } else {
                try {
                    int id = Integer.parseInt(txt);
                    lista = servicio.listarButacasPorSala(id);
                } catch (NumberFormatException ex) {
                    lista = servicio.buscarButacasPorNombreSalaParcial(txt);
                }
            }
            tablaButacas.getItems().setAll(lista);
            lblTotalButacas.setText("Total Butacas: " + lista.size());
        } catch (Exception ex) {
            tablaButacas.getItems().clear();
            lblTotalButacas.setText("Total Butacas: 0");
        }
    }

    /**
     * Crea una nueva butaca con los datos del formulario.
     * - Valida campos
     * - Llama al servicio crearButaca
     * - Refresca la tabla y muestra confirmación
     * - Maneja errores de unicidad/capacidad/otros con mensajes adecuados
     */
    private void crearButaca(ActionEvent e) {
        if (!validarCampos()) return;
        try {
            Butaca b = new Butaca();
            b.setFila(txtFila.getText());
            b.setColumna(txtColumna.getText());
            b.setEstado(cmbEstado.getValue().name());
            b.setIdSala(cmbSala.getValue().getId());

            servicio.crearButaca(b);

            listarButacasPorSala(null);
            ManejadorMetodosComunes.mostrarVentanaExito("Butaca creada correctamente.");
        } catch (Exception ex) {
            String msg = ex.getMessage().toLowerCase();
            if (msg.contains("ya existe")) {
                ManejadorMetodosComunes.mostrarVentanaError(ex.getMessage());
            } else if (msg.contains("capacidad")) {
                ManejadorMetodosComunes.mostrarVentanaAdvertencia(ex.getMessage());
            } else {
                ManejadorMetodosComunes.mostrarVentanaError(ex.getMessage());
            }
        }
    }

    /**
     * Actualiza la butaca seleccionada con los datos del formulario.
     * - Requiere una selección previa
     * - Valida campos
     * - Llama al servicio actualizarButaca
     * - Refresca la tabla y muestra confirmación
     */

    private void actualizarButaca(ActionEvent e) {
        Butaca sel = tablaButacas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            limpiarFormulario();
            return;
        }
        if (!validarCampos()) {
            limpiarFormulario();
            return;
        }

        // Guardar valores originales
        String filaOriginal = sel.getFila();
        String columnaOriginal = sel.getColumna();
        String estadoOriginal = sel.getEstado();
        int idSalaOriginal = sel.getIdSala();

        try {
            sel.setFila(txtFila.getText());
            sel.setColumna(txtColumna.getText());
            sel.setEstado(cmbEstado.getValue().name());
            sel.setIdSala(cmbSala.getValue().getId());

            servicio.actualizarButaca(sel);

            listarButacasPorSala(null);
            ManejadorMetodosComunes.mostrarVentanaExito("Butaca actualizada correctamente.");
        } catch (Exception ex) {
            // Restaurar valores originales
            sel.setFila(filaOriginal);
            sel.setColumna(columnaOriginal);
            sel.setEstado(estadoOriginal);
            sel.setIdSala(idSalaOriginal);

            String msg = ex.getMessage().toLowerCase();
            if (msg.contains("ya existe")) {
                ManejadorMetodosComunes.mostrarVentanaError(ex.getMessage());
            } else if (msg.contains("capacidad")) {
                ManejadorMetodosComunes.mostrarVentanaAdvertencia(ex.getMessage());
            } else {
                ManejadorMetodosComunes.mostrarVentanaError(ex.getMessage());
            }
        } finally {
            limpiarFormulario();
            butacaEnEdicion = null;
            actualizarModoFormulario();
        }
    }

    /**
     * Acción unificada del botón Guardar.
     * - Si no hay butaca en edición => crear
     * - Si hay butaca en edición => actualizar
     * - Luego restablece el modo a "crear"
     */
    @FXML
    private void onGuardar(ActionEvent event) {
        if (butacaEnEdicion == null) {
            crearButaca(event);
        } else {
            actualizarButaca(event);
            butacaEnEdicion = null;
            actualizarModoFormulario();
        }
    }

    /**
     * Ajusta la UI según el modo del formulario.
     * - Modo crear: botón dice "Crear", ocultar botón Nuevo
     * - Modo editar: botón dice "Actualizar", mostrar botón Nuevo
     * - Recalcula si el formulario permite guardar
     */
    private void actualizarModoFormulario() {
        if (butacaEnEdicion == null) {
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

    /**
     * Limpia formulario y vuelve a modo "crear".
     * - Se usa típicamente desde el botón Nuevo/Limpiar
     */
    @FXML
    private void onLimpiarFormulario(ActionEvent event) {
        limpiarFormulario();
        butacaEnEdicion = null;
        actualizarModoFormulario();
    }

    /**
     * Habilita o deshabilita el botón Guardar según si el formulario está completo.
     * - No valida reglas de negocio, solo presencia de datos requeridos.
     */
    private void actualizarEstadoFormulario() {
        if (btnGuardar != null) {
            boolean formularioValido = validarFormularioCompleto();
            btnGuardar.setDisable(!formularioValido);
        }
    }

    /**
     * Verificación rápida de campos requeridos (no vacíos / no nulos).
     * @return true si todos los campos esenciales están presentes
     */
    private boolean validarFormularioCompleto() {
        return txtFila != null && !txtFila.getText().trim().isEmpty()
                && txtColumna != null && !txtColumna.getText().trim().isEmpty()
                && cmbEstado != null && cmbEstado.getValue() != null
                && cmbSala != null && cmbSala.getValue() != null;
    }

    /**
     * Restablece el formulario a valores vacíos y deselecciona en tabla.
     * - Limpia campos y ComboBox
     * - Deshabilita botón Eliminar
     * - Reaplica modo formulario
     */
    private void limpiarFormulario() {
        txtFila.clear();
        txtColumna.clear();
        cmbEstado.getSelectionModel().clearSelection();
        cmbSala.getSelectionModel().clearSelection();
        tablaButacas.getSelectionModel().clearSelection();
        butacaEnEdicion = null;
        btnEliminar.setDisable(true);
        actualizarModoFormulario();
    }

    /**
     * Elimina la butaca seleccionada (si existe).
     * - Llama al servicio eliminarButaca
     * - Refresca tabla y muestra confirmación
     */
    @FXML
    private void eliminarButaca(ActionEvent e) {
        try {
            Butaca sel = tablaButacas.getSelectionModel().getSelectedItem();
            if (sel == null) return;

            servicio.eliminarButaca(sel.getId());
            listarButacasPorSala(null);
            // limpiarCampos(); // si existiera un método de limpieza adicional
            ManejadorMetodosComunes.mostrarVentanaExito("Butaca eliminada correctamente.");
        }
        catch (Exception ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.toLowerCase().contains("violates foreign key")) {
                ManejadorMetodosComunes.mostrarVentanaAdvertencia(
                        "No se puede eliminar porque está asociada a la venta de un boleto o una función."
                );
            } else {
                ManejadorMetodosComunes.mostrarVentanaError(msg);
            }
        }
    }

    /**
     * Acción del botón Volver: cambia la escena al portal principal.
     */
    public void onBackAction(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        ManejadorMetodosComunes.cambiarVentana(stage, "/vistas/empleados/PantallaPortalPrincipal.fxml");
    }
}