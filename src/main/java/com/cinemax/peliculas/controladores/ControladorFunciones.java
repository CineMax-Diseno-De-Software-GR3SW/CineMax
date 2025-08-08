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
import java.util.Optional;
import java.util.ResourceBundle;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.peliculas.modelos.entidades.FormatoFuncion;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.entidades.TipoEstreno;
import com.cinemax.peliculas.modelos.persistencia.FuncionDAO;
import com.cinemax.peliculas.modelos.persistencia.PeliculaDAO;
import com.cinemax.peliculas.servicios.ServicioFuncion;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.servicios.SalaService;

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

public class ControladorFunciones implements Initializable {

    private ServicioFuncion servicioFuncion;
    private FuncionDAO funcionDAO;
    private PeliculaDAO peliculaDAO;
    private SalaService salaService;

    // Constructor
    public ControladorFunciones() {
        this.servicioFuncion = new ServicioFuncion();
        this.funcionDAO = new FuncionDAO();
        this.peliculaDAO = new PeliculaDAO();
        this.salaService = new SalaService();
    }

    // Componentes de la interfaz FXML
    @FXML
    private TextField txtBuscar;
    @FXML
    private TableView<Funcion> tablaFunciones;
    @FXML
    private TableColumn<Funcion, Integer> colId;
    @FXML
    private TableColumn<Funcion, String> colPelicula;
    @FXML
    private TableColumn<Funcion, String> colSala;
    @FXML
    private TableColumn<Funcion, String> colFechaHoraInicio;
    @FXML
    private TableColumn<Funcion, String> colFechaHoraFin;
    @FXML
    private TableColumn<Funcion, String> colFormato;
    @FXML
    private TableColumn<Funcion, String> colTipoEstreno;

    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnVerDetalles;
    @FXML
    private Button btnVolver;

    @FXML
    private Label lblTotalFunciones;
    @FXML
    private Label lblEstadisticas;

    // Campos del formulario de función integrado
    @FXML
    private ComboBox<Pelicula> cmbPelicula;
    @FXML
    private ComboBox<Sala> cmbSala;
    @FXML
    private DatePicker dateFecha;
    @FXML
    private TextField txtHora;
    @FXML
    private ComboBox<FormatoFuncion> cmbFormato;
    @FXML
    private ComboBox<TipoEstreno> cmbTipoEstreno;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnLimpiarFormulario;

    // Datos para la tabla
    private ObservableList<Funcion> listaFunciones;
    private ObservableList<Funcion> funcionesFiltradas;

    // Variable para controlar el modo de edición
    private Funcion funcionEnEdicion = null;

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
                            cmbTipoEstreno.getValue()
                    );

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
                            cmbTipoEstreno.getValue()
                    );

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

    @FXML
    private void onLimpiarFormulario(ActionEvent event) {
        limpiarFormulario();
    }

    private void limpiarFormulario() {
        if (cmbPelicula != null) cmbPelicula.setValue(null);
        if (cmbSala != null) cmbSala.setValue(null);
        if (dateFecha != null) dateFecha.setValue(null);
        if (txtHora != null) txtHora.clear();
        if (cmbFormato != null) cmbFormato.setValue(null);
        if (cmbTipoEstreno != null) cmbTipoEstreno.setValue(null);

        // Resetear modo de edición
        funcionEnEdicion = null;
        actualizarModoFormulario();

        // Limpiar selección de tabla
        tablaFunciones.getSelectionModel().clearSelection();
    }

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

    private void cargarDatosEnFormulario(Funcion funcion) {
        if (funcion == null) return;

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

    @FXML
    private void onEliminarFuncion(ActionEvent event) {
        Funcion funcionSeleccionada = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcionSeleccionada != null) {
            String mensaje = "¿Está seguro de eliminar esta función?\n\n" +
                           "Función ID: " + funcionSeleccionada.getId() +
                           "\nPelícula: " + funcionSeleccionada.getPelicula().getTitulo() +
                           "\n\nATENCIÓN: Esta acción no se puede deshacer.";
            ManejadorMetodosComunes.mostrarVentanaAdvertencia(mensaje);

            // Proceder con la eliminación
            try {
                funcionDAO.eliminar(funcionSeleccionada.getId());
                cargarFunciones();
                mostrarInformacion("Éxito", "Función eliminada correctamente");
            } catch (Exception e) {
                String mensajeError = e.getMessage();
                if (mensajeError != null && (mensajeError.contains("foreign key constraint") || mensajeError.contains("violates"))) {
                    mostrarErrorRestriccion(funcionSeleccionada);
                } else {
                    mostrarError("Error", "No se pudo eliminar la función: " + mensajeError);
                }
            }
        }
    }

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

    @FXML
    private void onVerDetalles(ActionEvent event) {
        Funcion funcionSeleccionada = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcionSeleccionada != null) {
            navegarADetallesFuncion(funcionSeleccionada);
        }
    }

    @FXML
    private void onBuscar(ActionEvent event) {
        aplicarFiltros();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listaFunciones = FXCollections.observableArrayList();
        funcionesFiltradas = FXCollections.observableArrayList();

        configurarTabla();
        configurarEventos();
        configurarFormularioFuncion();
        cargarFunciones();
    }

    private void configurarTabla() {
        // Configurar las columnas de la tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colPelicula.setCellValueFactory(cellData -> {
            Pelicula pelicula = cellData.getValue().getPelicula();
            return new javafx.beans.property.SimpleStringProperty(
                pelicula != null ? pelicula.getTitulo() : "N/A"
            );
        });

        colSala.setCellValueFactory(cellData -> {
            Sala sala = cellData.getValue().getSala();
            return new javafx.beans.property.SimpleStringProperty(
                sala != null ? sala.getNombre() : "N/A"
            );
        });

        colFechaHoraInicio.setCellValueFactory(cellData -> {
            LocalDateTime fechaHora = cellData.getValue().getFechaHoraInicio();
            if (fechaHora != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                return new javafx.beans.property.SimpleStringProperty(fechaHora.format(formatter));
            } else {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });

        colFechaHoraFin.setCellValueFactory(cellData -> {
            LocalDateTime fechaHora = cellData.getValue().getFechaHoraFin();
            if (fechaHora != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                return new javafx.beans.property.SimpleStringProperty(fechaHora.format(formatter));
            } else {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });

        colFormato.setCellValueFactory(cellData -> {
            FormatoFuncion formato = cellData.getValue().getFormato();
            return new javafx.beans.property.SimpleStringProperty(
                formato != null ? formato.toString() : "N/A"
            );
        });

        colTipoEstreno.setCellValueFactory(cellData -> {
            TipoEstreno tipo = cellData.getValue().getTipoEstreno();
            return new javafx.beans.property.SimpleStringProperty(
                tipo != null ? tipo.name() : "N/A"
            );
        });

        // Configurar selección de tabla
        tablaFunciones.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean funcionSeleccionada = newSelection != null;
                    btnEliminar.setDisable(!funcionSeleccionada);
                    btnVerDetalles.setDisable(!funcionSeleccionada);

                    // Cargar datos en el formulario cuando se selecciona una función
                    if (funcionSeleccionada) {
                        cargarDatosEnFormulario(newSelection);
                    } else {
                        // Si no hay selección, limpiar formulario y volver a modo crear
                        limpiarFormulario();
                        funcionEnEdicion = null;
                        actualizarModoFormulario();
                    }
                }
        );

        tablaFunciones.setItems(funcionesFiltradas);
    }

    private void configurarEventos() {
        // Configurar búsqueda en tiempo real
        txtBuscar.textProperty().addListener((obs, oldText, newText) -> aplicarFiltros());
    }

    private void configurarFormularioFuncion() {
        // Configurar ComboBox de películas
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
                System.err.println("Error al cargar películas: " + e.getMessage());
            }
        }

        // Configurar ComboBox de salas
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
                System.err.println("Error al cargar salas: " + e.getMessage());
            }
        }

        // Configurar ComboBox de formatos
        if (cmbFormato != null) {
            cmbFormato.setItems(FXCollections.observableArrayList(FormatoFuncion.values()));
        }

        // Configurar ComboBox de tipos de estreno
        if (cmbTipoEstreno != null) {
            cmbTipoEstreno.setItems(FXCollections.observableArrayList(TipoEstreno.values()));
        }

        // Configurar validación de entrada de hora
        if (txtHora != null) {
            txtHora.textProperty().addListener((obs, oldText, newText) -> {
                // Permitir solo formato HH:MM
                if (!newText.matches("\\d{0,2}:?\\d{0,2}")) {
                    txtHora.setText(oldText);
                }
            });
        }

        // Configurar validaciones del formulario
        configurarValidacionesFormulario();
    }

    private void configurarValidacionesFormulario() {
        // Listener para validar formulario en tiempo real
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

    private void actualizarEstadoFormulario() {
        if (btnGuardar != null) {
            boolean formularioValido = validarFormularioCompleto();
            btnGuardar.setDisable(!formularioValido);
        }
    }

    private boolean validarFormularioCompleto() {
        return cmbPelicula != null && cmbPelicula.getValue() != null &&
               cmbSala != null && cmbSala.getValue() != null &&
               dateFecha != null && dateFecha.getValue() != null &&
               txtHora != null && !txtHora.getText().trim().isEmpty() &&
               cmbFormato != null && cmbFormato.getValue() != null &&
               cmbTipoEstreno != null && cmbTipoEstreno.getValue() != null;
    }

    private void cargarFunciones() {
        try {
            listaFunciones.clear();
            listaFunciones.addAll(funcionDAO.listarTodasLasFunciones());
            aplicarFiltros();
        } catch (Exception e) {
            mostrarError("Error al cargar funciones", e.getMessage());
        }
    }

    private void aplicarFiltros() {
        funcionesFiltradas.clear();

        String textoBusqueda = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase().trim() : "";

        for (Funcion funcion : listaFunciones) {
            boolean coincideTexto = textoBusqueda.isEmpty() ||
                String.valueOf(funcion.getId()).contains(textoBusqueda) ||
                (funcion.getPelicula() != null && funcion.getPelicula().getTitulo() != null &&
                 funcion.getPelicula().getTitulo().toLowerCase().contains(textoBusqueda)) ||
                (funcion.getSala() != null && funcion.getSala().getNombre() != null &&
                 funcion.getSala().getNombre().toLowerCase().contains(textoBusqueda));

            if (coincideTexto) {
                funcionesFiltradas.add(funcion);
            }
        }

        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        int total = funcionesFiltradas.size();
        lblTotalFunciones.setText("Total de funciones: " + total);

        if (total > 0) {
            long funcionesHoy = funcionesFiltradas.stream()
                .filter(f -> f.getFechaHoraInicio() != null)
                .filter(f -> f.getFechaHoraInicio().toLocalDate().equals(LocalDate.now()))
                .count();
            lblEstadisticas.setText("Funciones hoy: " + funcionesHoy);
        } else {
            lblEstadisticas.setText("No hay funciones que mostrar");
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaError(mensaje != null ? mensaje : "Error desconocido");
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaExito(mensaje != null ? mensaje : "Operación completada");
    }

    /**
     * Método público para obtener funciones por nombre de película
     * Delega la lógica al servicio correspondiente
     * @param nombrePelicula El título de la película
     * @return Lista de funciones de la película especificada
     */
    public List<Funcion> obtenerFuncionesPorNombrePelicula(String nombrePelicula) {
        try {
            return servicioFuncion.obtenerFuncionesPorNombrePelicula(nombrePelicula);
        } catch (Exception e) {
            // Log del error pero no mostrar UI desde aquí
            System.err.println("Error al obtener funciones por película: " + e.getMessage());
            return new ArrayList<>(); // Retornar lista vacía en caso de error
        }
    }

    private void navegarADetallesFuncion(Funcion funcion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/peliculas/PantallaDetallesFuncion.fxml"));
            Parent root = loader.load();

            // Configurar el controlador con la función seleccionada
            ControladorDetallesFuncion controlador = loader.getController();
            controlador.cargarFuncion(funcion);

            Stage stage = (Stage) btnVerDetalles.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("Error de navegación", "No se pudo abrir los detalles: " + e.getMessage());
        }
    }

    @FXML
    private void onVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("No se pudo volver al portal: " + e.getMessage());
        }
    }
}
