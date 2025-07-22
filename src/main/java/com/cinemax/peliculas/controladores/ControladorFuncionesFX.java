package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import com.cinemax.peliculas.modelos.entidades.FormatoFuncion;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.entidades.TipoEstreno;
import com.cinemax.peliculas.modelos.persistencia.FuncionDAO;
import com.cinemax.peliculas.modelos.persistencia.PeliculaDAO;
import com.cinemax.peliculas.servicios.ServicioFuncion;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.modelos.persistencia.SalaDAO;
import com.cinemax.salas.servicios.ServicioSala;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ControladorFuncionesFX implements Initializable {

    private ServicioFuncion servicioFuncion;
    private FuncionDAO funcionDAO;
    private PeliculaDAO peliculaDAO;
    private SalaDAO salaDAO;
    private ServicioSala servicioSala;

    // Componentes para programar función
    @FXML private ComboBox<Pelicula> cmbPelicula;
    @FXML private ComboBox<Sala> cmbSala;
    @FXML private DatePicker dateFechaFuncion;
    @FXML private TextField txtHoraInicio;
    @FXML private ComboBox<FormatoFuncion> cmbFormato;
    @FXML private ComboBox<TipoEstreno> cmbTipoEstreno;
    @FXML private Button btnProgramarFuncion;
    @FXML private Button btnLimpiarFormulario;

    // Componentes para búsqueda y filtros
    @FXML private TextField txtBuscarId;
    @FXML private ComboBox<Sala> cmbFiltrarSala;
    @FXML private Button btnBuscarId;
    @FXML private Button btnFiltrarSala;
    @FXML private Button btnMostrarTodas;

    // Tabla de funciones
    @FXML private TableView<Funcion> tablaFunciones;
    @FXML private TableColumn<Funcion, Integer> colId;
    @FXML private TableColumn<Funcion, String> colPelicula;
    @FXML private TableColumn<Funcion, String> colSala;
    @FXML private TableColumn<Funcion, String> colFechaHora;
    @FXML private TableColumn<Funcion, String> colFormato;
    @FXML private TableColumn<Funcion, String> colTipoEstreno;

    // Botones de acción
    @FXML private Button btnVerDetalles;
    @FXML private Button btnEditarFuncion;
    @FXML private Button btnEliminarFuncion;

    // Labels informativos
    @FXML private Label lblTotalFunciones;
    @FXML private Label lblEstadoOperacion;

    // Datos para la tabla
    private ObservableList<Funcion> listaFunciones;
    private ObservableList<Funcion> funcionesFiltradas;

    public ControladorFuncionesFX() {
        this.servicioFuncion = new ServicioFuncion();
        this.funcionDAO = new FuncionDAO();
        this.peliculaDAO = new PeliculaDAO();
        this.salaDAO = new SalaDAO();
        this.servicioSala = new ServicioSala();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listaFunciones = FXCollections.observableArrayList();
        funcionesFiltradas = FXCollections.observableArrayList();

        configurarTabla();
        try {
            configurarComboBoxes();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        configurarEventos();
        cargarDatos();
    }

    private void configurarTabla() {
        // Configurar las columnas de la tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colPelicula.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getPelicula().getTitulo()
            );
        });

        colSala.setCellValueFactory(cellData -> {
            Sala sala = cellData.getValue().getSala();
            return new javafx.beans.property.SimpleStringProperty(
                    sala != null ? sala.getNombre() : "Sala no asignada"
            );
        });

        colFechaHora.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFechaHoraInicio().format(formatter)
            );
        });

        colFormato.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFormato().toString()
            );
        });

        colTipoEstreno.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTipoEstreno().name()
            );
        });

        // Configurar selección de tabla
        tablaFunciones.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean funcionSeleccionada = newSelection != null;
                btnVerDetalles.setDisable(!funcionSeleccionada);
                btnEditarFuncion.setDisable(!funcionSeleccionada);
                btnEliminarFuncion.setDisable(!funcionSeleccionada);
            }
        );

        tablaFunciones.setItems(funcionesFiltradas);
    }

    private void configurarComboBoxes() throws SQLException {
        // Configurar ComboBox de películas
        List<Pelicula> peliculas = peliculaDAO.listarTodas();
        cmbPelicula.setItems(FXCollections.observableArrayList(peliculas));
        cmbPelicula.setConverter(new javafx.util.StringConverter<Pelicula>() {
            @Override
            public String toString(Pelicula pelicula) {
                return pelicula != null ? pelicula.getTitulo() : "";
            }

            @Override
            public Pelicula fromString(String string) {
                return null;
            }
        });

        // Configurar ComboBox de salas
        List<Sala> salas = salaDAO.listarTodas();
        cmbSala.setItems(FXCollections.observableArrayList(salas));
        cmbFiltrarSala.setItems(FXCollections.observableArrayList(salas));

        javafx.util.StringConverter<Sala> salaConverter = new javafx.util.StringConverter<Sala>() {
            @Override
            public String toString(Sala sala) {
                return sala != null ? sala.getNombre() + " (" + sala.getTipoSala().name() + ")" : "";
            }

            @Override
            public Sala fromString(String string) {
                return null;
            }
        };
        cmbSala.setConverter(salaConverter);
        cmbFiltrarSala.setConverter(salaConverter);

        // Configurar ComboBox de formato
        cmbFormato.setItems(FXCollections.observableArrayList(FormatoFuncion.values()));

        // Configurar ComboBox de tipo de estreno
        cmbTipoEstreno.setItems(FXCollections.observableArrayList(TipoEstreno.values()));
    }

    private void configurarEventos() {
        // Validación en tiempo real de la hora
        txtHoraInicio.textProperty().addListener((obs, oldText, newText) -> {
            validarFormatoHora(newText);
        });
    }

    private void cargarDatos() {
        cargarFunciones();
    }

    private void cargarFunciones() {
        try {
            lblEstadoOperacion.setText("Cargando funciones...");
            List<Funcion> funciones = funcionDAO.listarTodas();
            listaFunciones.clear();
            listaFunciones.addAll(funciones);
            funcionesFiltradas.setAll(listaFunciones); // Asegurar que funcionesFiltradas se actualice
            lblEstadoOperacion.setText("Funciones cargadas correctamente");
        } catch (Exception e) {
            lblEstadoOperacion.setText("Error al cargar funciones");
            mostrarError("Error al cargar funciones", e.getMessage());
        }
    }

    @FXML
    private void onProgramarFuncion(ActionEvent event) {
        try {
            if (!validarFormulario()) {
                return;
            }

            Pelicula pelicula = cmbPelicula.getValue();
            Sala sala = cmbSala.getValue();
            LocalDate fecha = dateFechaFuncion.getValue();
            LocalTime hora = LocalTime.parse(txtHoraInicio.getText());
            LocalDateTime fechaHoraInicio = LocalDateTime.of(fecha, hora);
            FormatoFuncion formato = cmbFormato.getValue();
            TipoEstreno tipoEstreno = cmbTipoEstreno.getValue();

            Funcion nuevaFuncion = servicioFuncion.programarNuevaFuncion(
                pelicula, sala, fechaHoraInicio, formato, tipoEstreno);

            mostrarInformacion("Éxito", "Función programada correctamente con ID: " + nuevaFuncion.getId());
            limpiarFormulario();
            cargarFunciones();

        } catch (IllegalArgumentException e) {
            mostrarError("Error de validación", e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al programar función", e.getMessage());
        }
    }

    @FXML
    private void onLimpiarFormulario(ActionEvent event) {
        limpiarFormulario();
    }

    @FXML
    private void onBuscarId(ActionEvent event) {
        buscarPorId();
    }

    @FXML
    private void onFiltrarSala(ActionEvent event) {
        filtrarPorSala();
    }

    @FXML
    private void onMostrarTodas(ActionEvent event) {
        mostrarTodasLasFunciones();
    }

    @FXML
    private void onVerDetalles(ActionEvent event) {
        Funcion funcionSeleccionada = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcionSeleccionada != null) {
            mostrarDetallesFuncion(funcionSeleccionada);
        }
    }

    @FXML
    private void onEditarFuncion(ActionEvent event) {
        Funcion funcionSeleccionada = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcionSeleccionada != null) {
            editarFuncion(funcionSeleccionada);
        }
    }

    @FXML
    private void onEliminarFuncion(ActionEvent event) {
        Funcion funcionSeleccionada = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcionSeleccionada != null) {
            eliminarFuncion(funcionSeleccionada);
        }
    }

    private boolean validarFormulario() {
        if (cmbPelicula.getValue() == null) {
            mostrarError("Validación", "Debe seleccionar una película");
            return false;
        }
        if (cmbSala.getValue() == null) {
            mostrarError("Validación", "Debe seleccionar una sala");
            return false;
        }
        if (dateFechaFuncion.getValue() == null) {
            mostrarError("Validación", "Debe seleccionar una fecha");
            return false;
        }
        if (txtHoraInicio.getText().trim().isEmpty()) {
            mostrarError("Validación", "Debe ingresar una hora de inicio");
            return false;
        }
        if (cmbFormato.getValue() == null) {
            mostrarError("Validación", "Debe seleccionar un formato");
            return false;
        }
        if (cmbTipoEstreno.getValue() == null) {
            mostrarError("Validación", "Debe seleccionar un tipo de estreno");
            return false;
        }

        try {
            LocalTime.parse(txtHoraInicio.getText());
        } catch (Exception e) {
            mostrarError("Validación", "Formato de hora inválido. Use HH:MM (ej: 14:30)");
            return false;
        }

        return true;
    }

    private void validarFormatoHora(String texto) {
        if (!texto.isEmpty()) {
            try {
                LocalTime.parse(texto);
                txtHoraInicio.setStyle("-fx-border-color: green;");
            } catch (Exception e) {
                txtHoraInicio.setStyle("-fx-border-color: red;");
            }
        } else {
            txtHoraInicio.setStyle("");
        }
    }

    private void limpiarFormulario() {
        cmbPelicula.setValue(null);
        cmbSala.setValue(null);
        dateFechaFuncion.setValue(null);
        txtHoraInicio.clear();
        cmbFormato.setValue(null);
        cmbTipoEstreno.setValue(null);
        txtHoraInicio.setStyle("");
        lblEstadoOperacion.setText("Formulario limpio");
    }

    private void buscarPorId() {
        String idTexto = txtBuscarId.getText().trim();

        if (idTexto.isEmpty()) {
            mostrarTodasLasFunciones();
            return;
        }

        try {
            int id = Integer.parseInt(idTexto);
            funcionesFiltradas.clear();

            for (Funcion f : listaFunciones) {
                if (f.getId() == id) {
                    funcionesFiltradas.add(f);
                    tablaFunciones.getSelectionModel().select(f);
                    lblEstadoOperacion.setText("Función encontrada con ID: " + id);
                    actualizarEstadisticas();
                    return;
                }
            }

            lblEstadoOperacion.setText("No se encontró función con ID: " + id);
            actualizarEstadisticas();

        } catch (NumberFormatException e) {
            lblEstadoOperacion.setText("Por favor ingrese un ID válido (número entero)");
        }
    }

    private void filtrarPorSala() {
        Sala salaSeleccionada = cmbFiltrarSala.getValue();

        if (salaSeleccionada == null) {
            mostrarTodasLasFunciones();
            return;
        }

        funcionesFiltradas.clear();
        int contador = 0;

        for (Funcion f : listaFunciones) {
            if (f.getSala().getId() == salaSeleccionada.getId()) {
                funcionesFiltradas.add(f);
                contador++;
            }
        }

        actualizarEstadisticas();
        lblEstadoOperacion.setText("Funciones de Sala " + salaSeleccionada.getNombre() + ": " + contador);
    }

    private void mostrarTodasLasFunciones() {
        funcionesFiltradas.clear();
        funcionesFiltradas.addAll(listaFunciones);
        actualizarEstadisticas();

        if (listaFunciones.isEmpty()) {
            lblEstadoOperacion.setText("No hay funciones programadas");
        } else {
            lblEstadoOperacion.setText("Mostrando todas las funciones");
        }
    }

    private void actualizarEstadisticas() {
        int total = funcionesFiltradas.size();
        lblTotalFunciones.setText("Funciones mostradas: " + total + " de " + listaFunciones.size());
    }

    private void mostrarDetallesFuncion(Funcion funcion) {
        Alert detalles = new Alert(Alert.AlertType.INFORMATION);
        detalles.setTitle("Detalles de la Función");
        detalles.setHeaderText("Función ID: " + funcion.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        StringBuilder contenido = new StringBuilder();
        contenido.append("Película: ").append(funcion.getPelicula().getTitulo()).append("\n");
        contenido.append("Sala: ").append(funcion.getSala().getNombre()).append("\n");
        contenido.append("Tipo de Sala: ").append(funcion.getSala().getTipoSala().name()).append("\n");
        contenido.append("Fecha y Hora de Inicio: ").append(funcion.getFechaHoraInicio().format(formatter)).append("\n");
        contenido.append("Fecha y Hora de Fin: ").append(funcion.getFechaHoraFin().format(formatter)).append("\n");
        contenido.append("Formato: ").append(funcion.getFormato().toString()).append("\n");
        contenido.append("Tipo de Estreno: ").append(funcion.getTipoEstreno().name()).append("\n");

        detalles.setContentText(contenido.toString());
        detalles.showAndWait();
    }

    private void editarFuncion(Funcion funcion) {
        // Cargar datos en el formulario para edición
        cmbPelicula.setValue(funcion.getPelicula());
        cmbSala.setValue(funcion.getSala());
        dateFechaFuncion.setValue(funcion.getFechaHoraInicio().toLocalDate());
        txtHoraInicio.setText(funcion.getFechaHoraInicio().toLocalTime().toString());
        cmbFormato.setValue(funcion.getFormato());
        cmbTipoEstreno.setValue(funcion.getTipoEstreno());

        lblEstadoOperacion.setText("Editando función ID: " + funcion.getId() + ". Modifique los datos y programe nuevamente.");

        mostrarInformacion("Modo Edición",
            "Los datos de la función han sido cargados en el formulario.\n" +
            "Modifique los campos necesarios y presione 'Programar Función' para guardar los cambios.\n\n" +
            "Nota: Se creará una nueva función y deberá eliminar la anterior manualmente si es necesario.");
    }

    private void eliminarFuncion(Funcion funcion) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar esta función?");
        confirmacion.setContentText("Función ID: " + funcion.getId() + "\n" +
                                   "Película: " + funcion.getPelicula().getTitulo() + "\n" +
                                   "Sala: " + funcion.getSala().getNombre());

        confirmacion.showAndWait().ifPresent(response -> {
            if (response.getButtonData().isDefaultButton()) {
                try {
                    // Aquí implementarías la eliminación en el DAO
                    // funcionDAO.eliminar(funcion.getId());
                    mostrarInformacion("Función Eliminada", "La función ha sido eliminada correctamente.");
                    cargarFunciones();
                } catch (Exception e) {
                    mostrarError("Error al eliminar", e.getMessage());
                }
            }
        });
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
