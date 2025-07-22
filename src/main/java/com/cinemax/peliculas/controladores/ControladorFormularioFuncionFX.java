package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControladorFormularioFuncionFX implements Initializable {

    private ServicioFuncion servicioFuncion;
    private FuncionDAO funcionDAO;
    private PeliculaDAO peliculaDAO;
    private SalaDAO salaDAO;

    private Funcion funcionAEditar; // null si es nueva función
    private Runnable onFuncionGuardada; // Callback para notificar cuando se guarda

    // Componentes del formulario
    @FXML private Label lblTituloVentana;
    @FXML private ComboBox<Pelicula> cmbPelicula;
    @FXML private ComboBox<Sala> cmbSala;
    @FXML private DatePicker dateFechaFuncion;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtDuracion;
    @FXML private ComboBox<FormatoFuncion> cmbFormato;
    @FXML private ComboBox<TipoEstreno> cmbTipoEstreno;
    @FXML private TextArea txtObservaciones;
    @FXML private TextField txtPrecioBase;
    @FXML private TextField txtDescuento;

    // Panel de información
    @FXML private VBox panelInfoPelicula;
    @FXML private Label lblInfoPelicula;

    // Botones
    @FXML private Button btnGuardar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnCancelar;

    public ControladorFormularioFuncionFX() {
        this.servicioFuncion = new ServicioFuncion();
        this.funcionDAO = new FuncionDAO();
        this.peliculaDAO = new PeliculaDAO();
        this.salaDAO = new SalaDAO();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            configurarComboBoxes();
            configurarEventos();
            configurarValidaciones();
        } catch (SQLException e) {
            mostrarError("Error de inicialización", "Error al cargar datos: " + e.getMessage());
        }
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
        cmbSala.setConverter(new javafx.util.StringConverter<Sala>() {
            @Override
            public String toString(Sala sala) {
                return sala != null ? sala.getNombre() + " (" + sala.getTipoSala().name() + ")" : "";
            }

            @Override
            public Sala fromString(String string) {
                return null;
            }
        });

        // Configurar ComboBox de formato
        cmbFormato.setItems(FXCollections.observableArrayList(FormatoFuncion.values()));

        // Configurar ComboBox de tipo de estreno
        cmbTipoEstreno.setItems(FXCollections.observableArrayList(TipoEstreno.values()));
    }

    private void configurarEventos() {
        // Evento cuando se selecciona una película
        cmbPelicula.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mostrarInfoPelicula(newVal);
                // Auto-rellenar duración si está disponible
                if (newVal.getDuracionMinutos() > 0) {
                    txtDuracion.setText(String.valueOf(newVal.getDuracionMinutos()));
                }
            } else {
                limpiarInfoPelicula();
                txtDuracion.clear();
            }
        });
    }

    private void configurarValidaciones() {
        // Validación en tiempo real de la hora
        txtHoraInicio.textProperty().addListener((obs, oldText, newText) -> {
            validarFormatoHora(newText);
        });

        // Validación de números en campos numéricos
        txtPrecioBase.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*(\\.\\d*)?")) {
                txtPrecioBase.setText(oldText);
            }
        });

        txtDescuento.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                txtDescuento.setText(oldText);
            }
        });
    }

    public void configurarParaEdicion(Funcion funcion) {
        this.funcionAEditar = funcion;
        lblTituloVentana.setText("Editar Función");
        btnGuardar.setText("Actualizar Función");
        cargarDatosFuncion(funcion);
    }

    private void cargarDatosFuncion(Funcion funcion) {
        cmbPelicula.setValue(funcion.getPelicula());
        cmbSala.setValue(funcion.getSala());
        dateFechaFuncion.setValue(funcion.getFechaHoraInicio().toLocalDate());
        txtHoraInicio.setText(funcion.getFechaHoraInicio().toLocalTime().toString());
        cmbFormato.setValue(funcion.getFormato());
        cmbTipoEstreno.setValue(funcion.getTipoEstreno());
    }

    public void setOnFuncionGuardada(Runnable callback) {
        this.onFuncionGuardada = callback;
    }

    @FXML
    private void onGuardar(ActionEvent event) {
        if (!validarFormulario()) {
            return;
        }

        try {
            if (funcionAEditar == null) {
                // Crear nueva función
                crearNuevaFuncion();
            } else {
                // Actualizar función existente
                actualizarFuncion();
            }
        } catch (Exception e) {
            mostrarError("Error al guardar", "No se pudo guardar la función: " + e.getMessage());
        }
    }

    private void crearNuevaFuncion() throws Exception {
        Pelicula pelicula = cmbPelicula.getValue();
        Sala sala = cmbSala.getValue();
        LocalDate fecha = dateFechaFuncion.getValue();
        LocalTime hora = LocalTime.parse(txtHoraInicio.getText());
        LocalDateTime fechaHoraInicio = LocalDateTime.of(fecha, hora);
        FormatoFuncion formato = cmbFormato.getValue();
        TipoEstreno tipoEstreno = cmbTipoEstreno.getValue();

        Funcion nuevaFuncion = servicioFuncion.programarNuevaFuncion(
            pelicula, sala, fechaHoraInicio, formato, tipoEstreno);

        mostrarInformacion("Éxito", "Función creada correctamente con ID: " + nuevaFuncion.getId());

        // Notificar que se guardó la función
        if (onFuncionGuardada != null) {
            onFuncionGuardada.run();
        }

        cerrarVentana();
    }

    private void actualizarFuncion() throws Exception {
        // Actualizar los datos de la función existente
        funcionAEditar.setPelicula(cmbPelicula.getValue());
        funcionAEditar.setSala(cmbSala.getValue());

        LocalDate fecha = dateFechaFuncion.getValue();
        LocalTime hora = LocalTime.parse(txtHoraInicio.getText());
        funcionAEditar.setFechaHoraInicio(LocalDateTime.of(fecha, hora));

        funcionAEditar.setFormato(cmbFormato.getValue());
        funcionAEditar.setTipoEstreno(cmbTipoEstreno.getValue());

        // Actualizar en la base de datos
        funcionDAO.guardar(funcionAEditar);

        mostrarInformacion("Éxito", "Función actualizada correctamente");

        // Notificar que se guardó la función
        if (onFuncionGuardada != null) {
            onFuncionGuardada.run();
        }

        cerrarVentana();
    }

    @FXML
    private void onLimpiar(ActionEvent event) {
        limpiarFormulario();
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private boolean validarFormulario() {
        if (cmbPelicula.getValue() == null) {
            mostrarError("Validación", "Debe seleccionar una película");
            cmbPelicula.requestFocus();
            return false;
        }
        if (cmbSala.getValue() == null) {
            mostrarError("Validación", "Debe seleccionar una sala");
            cmbSala.requestFocus();
            return false;
        }
        if (dateFechaFuncion.getValue() == null) {
            mostrarError("Validación", "Debe seleccionar una fecha");
            dateFechaFuncion.requestFocus();
            return false;
        }
        if (txtHoraInicio.getText().trim().isEmpty()) {
            mostrarError("Validación", "Debe ingresar una hora de inicio");
            txtHoraInicio.requestFocus();
            return false;
        }
        if (cmbFormato.getValue() == null) {
            mostrarError("Validación", "Debe seleccionar un formato");
            cmbFormato.requestFocus();
            return false;
        }
        if (cmbTipoEstreno.getValue() == null) {
            mostrarError("Validación", "Debe seleccionar un tipo de estreno");
            cmbTipoEstreno.requestFocus();
            return false;
        }

        // Validar fecha no sea en el pasado
        if (dateFechaFuncion.getValue().isBefore(LocalDate.now())) {
            mostrarError("Validación", "La fecha no puede ser en el pasado");
            dateFechaFuncion.requestFocus();
            return false;
        }

        try {
            LocalTime.parse(txtHoraInicio.getText());
        } catch (Exception e) {
            mostrarError("Validación", "Formato de hora inválido. Use HH:MM (ej: 14:30)");
            txtHoraInicio.requestFocus();
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

    private void mostrarInfoPelicula(Pelicula pelicula) {
        StringBuilder info = new StringBuilder();
        info.append("Título: ").append(pelicula.getTitulo()).append("\n");
        info.append("Duración: ").append(pelicula.getDuracionMinutos()).append(" minutos\n");
        info.append("Género: ").append(pelicula.getGeneros()).append("\n");
        info.append("Clasificación: ").append(pelicula.getClass()).append("\n");

        lblInfoPelicula.setText(info.toString());
    }

    private void limpiarInfoPelicula() {
        lblInfoPelicula.setText("Seleccione una película para ver su información");
    }

    private void limpiarFormulario() {
        cmbPelicula.setValue(null);
        cmbSala.setValue(null);
        dateFechaFuncion.setValue(null);
        txtHoraInicio.clear();
        txtDuracion.clear();
        cmbFormato.setValue(null);
        cmbTipoEstreno.setValue(null);
        txtObservaciones.clear();
        txtPrecioBase.clear();
        txtDescuento.clear();
        txtHoraInicio.setStyle("");
        limpiarInfoPelicula();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
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
