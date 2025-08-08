package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.peliculas.modelos.entidades.FormatoFuncion;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.entidades.TipoEstreno;
import com.cinemax.peliculas.servicios.ServicioFuncion;
import com.cinemax.peliculas.servicios.ServicioPelicula;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.servicios.SalaService;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class ControladorFormularioFuncion implements Initializable {

    @FXML private Label lblTituloPantalla;
    @FXML private Label lblModoEdicion;
    @FXML private Label lblEstadoFormulario;
    @FXML private Label lblDuracionCalculada;
    @FXML private Label lblCapacidadSala;
    
    @FXML private ComboBox<Pelicula> cmbPelicula;
    @FXML private ComboBox<Sala> cmbSala;
    @FXML private ComboBox<FormatoFuncion> cmbFormato;
    @FXML private ComboBox<TipoEstreno> cmbTipoEstreno;
    @FXML private DatePicker dateFecha;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraFin;
    
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnVolver;
    @FXML private Button btnLogOut;

    private ServicioFuncion servicioFuncion;
    private ServicioPelicula servicioPelicula;
    private SalaService salaService;
    private Funcion funcionEditando;
    private boolean modoEdicion = false;

    public ControladorFormularioFuncion() {
        this.servicioFuncion = new ServicioFuncion();
        this.servicioPelicula = new ServicioPelicula();
        this.salaService = new SalaService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarFormulario();
        configurarValidaciones();
        cargarDatos();
        actualizarEstadoFormulario();
    }

    private void configurarFormulario() {
        // Configurar ComboBox de películas
        cmbPelicula.setConverter(new StringConverter<Pelicula>() {
            @Override
            public String toString(Pelicula pelicula) {
                return pelicula != null ? pelicula.getTitulo() + " (" + pelicula.getAnio() + ")" : "";
            }

            @Override
            public Pelicula fromString(String string) {
                return null;
            }
        });

        // Configurar ComboBox de salas
        cmbSala.setConverter(new StringConverter<Sala>() {
            @Override
            public String toString(Sala sala) {
                return sala != null ? sala.getNombre() + " - " + sala.getCapacidad() + " asientos" : "";
            }

            @Override
            public Sala fromString(String string) {
                return null;
            }
        });

        // Configurar ComboBox de formatos
        cmbFormato.setItems(FXCollections.observableArrayList(FormatoFuncion.values()));
        cmbFormato.setConverter(new StringConverter<FormatoFuncion>() {
            @Override
            public String toString(FormatoFuncion formato) {
                return formato != null ? formato.name() : "";
            }

            @Override
            public FormatoFuncion fromString(String string) {
                return null;
            }
        });

        // Configurar ComboBox de tipos de estreno
        cmbTipoEstreno.setItems(FXCollections.observableArrayList(TipoEstreno.values()));
        cmbTipoEstreno.setConverter(new StringConverter<TipoEstreno>() {
            @Override
            public String toString(TipoEstreno tipo) {
                return tipo != null ? tipo.name() : "";
            }

            @Override
            public TipoEstreno fromString(String string) {
                return null;
            }
        });

        // Configurar validación de hora
        txtHoraInicio.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d{0,2}:?\\d{0,2}")) {
                txtHoraInicio.setText(oldText);
            } else {
                calcularHoraFin();
                actualizarEstadoFormulario();
            }
        });
    }

    private void configurarValidaciones() {
        // Listeners para validar formulario en tiempo real
        Runnable validarFormulario = this::actualizarEstadoFormulario;

        cmbPelicula.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                lblDuracionCalculada.setText(newValue.getDuracionMinutos() + " minutos");
                calcularHoraFin();
            } else {
                lblDuracionCalculada.setText("Seleccione una película");
            }
            validarFormulario.run();
        });

        cmbSala.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                lblCapacidadSala.setText(newValue.getCapacidad() + " asientos disponibles");
            } else {
                lblCapacidadSala.setText("Seleccione una sala");
            }
            validarFormulario.run();
        });

        dateFecha.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
        cmbFormato.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
        cmbTipoEstreno.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
    }

    private void cargarDatos() {
        try {
            // Cargar películas
            List<Pelicula> peliculas = servicioPelicula.listarTodasLasPeliculas();
            cmbPelicula.setItems(FXCollections.observableArrayList(peliculas));

            // Cargar salas
            List<Sala> salas = salaService.listarSalas();
            cmbSala.setItems(FXCollections.observableArrayList(salas));
        } catch (Exception e) {
            mostrarError("Error al cargar datos", "No se pudieron cargar las películas o salas: " + e.getMessage());
        }
    }

    private void calcularHoraFin() {
        if (cmbPelicula.getValue() != null && !txtHoraInicio.getText().trim().isEmpty()) {
            try {
                LocalTime horaInicio = LocalTime.parse(txtHoraInicio.getText().trim(), DateTimeFormatter.ofPattern("H:mm"));
                LocalTime horaFin = horaInicio.plusMinutes(cmbPelicula.getValue().getDuracionMinutos() + 15); // +15 min para limpieza
                txtHoraFin.setText(horaFin.format(DateTimeFormatter.ofPattern("HH:mm")));
            } catch (DateTimeParseException e) {
                txtHoraFin.clear();
            }
        } else {
            txtHoraFin.clear();
        }
    }

    private void actualizarEstadoFormulario() {
        boolean valido = esFormularioValido();
        btnGuardar.setDisable(!valido);

        if (valido) {
            lblEstadoFormulario.setText("Formulario completado - Listo para guardar");
            lblEstadoFormulario.setStyle("-fx-text-fill: #28a745;");
        } else {
            lblEstadoFormulario.setText("Complete todos los campos obligatorios");
            lblEstadoFormulario.setStyle("-fx-text-fill: #6c757d;");
        }
    }

    private boolean esFormularioValido() {
        return cmbPelicula.getValue() != null &&
               cmbSala.getValue() != null &&
               dateFecha.getValue() != null &&
               !txtHoraInicio.getText().trim().isEmpty() &&
               cmbFormato.getValue() != null &&
               cmbTipoEstreno.getValue() != null &&
               esHoraValida(txtHoraInicio.getText().trim());
    }

    private boolean esHoraValida(String hora) {
        try {
            LocalTime.parse(hora, DateTimeFormatter.ofPattern("H:mm"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public void configurarParaEdicion(Funcion funcion) {
        this.funcionEditando = funcion;
        this.modoEdicion = true;

        lblTituloPantalla.setText("Editar Función");
        lblModoEdicion.setText("Modo: Edición - ID: " + funcion.getId());
        btnGuardar.setText("Guardar Cambios");

        // Cargar datos de la función
        cmbPelicula.setValue(funcion.getPelicula());
        cmbSala.setValue(funcion.getSala());
        dateFecha.setValue(funcion.getFechaHoraInicio().toLocalDate());
        txtHoraInicio.setText(funcion.getFechaHoraInicio().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        cmbFormato.setValue(funcion.getFormato());
        cmbTipoEstreno.setValue(funcion.getTipoEstreno());

        calcularHoraFin();
        actualizarEstadoFormulario();
    }

    @FXML
    private void onGuardar(ActionEvent event) {
        try {
            // Validar fecha no sea en el pasado
            LocalDate fechaSeleccionada = dateFecha.getValue();
            if (fechaSeleccionada.isBefore(LocalDate.now())) {
                mostrarError("Error de validación", "La fecha no puede ser anterior a hoy");
                return;
            }

            // Construir fecha y hora de inicio
            LocalTime horaInicio = LocalTime.parse(txtHoraInicio.getText().trim(), DateTimeFormatter.ofPattern("H:mm"));
            LocalDateTime fechaHoraInicio = LocalDateTime.of(fechaSeleccionada, horaInicio);

            // Validar que no sea en el pasado
            if (fechaHoraInicio.isBefore(LocalDateTime.now())) {
                mostrarError("Error de validación", "La función no puede ser programada en el pasado");
                return;
            }

            // Calcular fecha y hora de fin
            LocalDateTime fechaHoraFin = fechaHoraInicio.plusMinutes(cmbPelicula.getValue().getDuracionMinutos() + 15);

            if (modoEdicion) {
                // Actualizar función existente
                servicioFuncion.actualizarFuncion(
                    funcionEditando.getId(),
                    cmbPelicula.getValue(),
                    cmbSala.getValue(),
                    fechaHoraInicio,
                    cmbFormato.getValue(),
                    cmbTipoEstreno.getValue()
                );

                mostrarInformacion("Éxito", "Función actualizada exitosamente");
            } else {
                // Crear nueva función
                servicioFuncion.crearFuncion(
                    cmbPelicula.getValue(),
                    cmbSala.getValue(),
                    fechaHoraInicio,
                    cmbFormato.getValue(),
                    cmbTipoEstreno.getValue()
                );

                mostrarInformacion("Éxito", "Función creada exitosamente");
            }

            // Volver a la pantalla principal
            volverAGestionFunciones();

        } catch (Exception e) {
            String operacion = modoEdicion ? "actualizar" : "crear";
            mostrarError("Error al " + operacion + " función", "Error: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        volverAGestionFunciones();
    }

    @FXML
    private void onLimpiar(ActionEvent event) {
        if (!modoEdicion) {
            limpiarFormulario();
        }
    }

    @FXML
    private void onVolver(ActionEvent event) {
        volverAGestionFunciones();
    }

    @FXML
    private void onLogOut(ActionEvent event) {
        ManejadorMetodosComunes.mostrarVentanaAdvertencia("Sesión cerrada");
        javafx.application.Platform.exit();
    }

    private void limpiarFormulario() {
        cmbPelicula.setValue(null);
        cmbSala.setValue(null);
        dateFecha.setValue(null);
        txtHoraInicio.clear();
        txtHoraFin.clear();
        cmbFormato.setValue(null);
        cmbTipoEstreno.setValue(null);
        lblDuracionCalculada.setText("Seleccione una película");
        lblCapacidadSala.setText("Seleccione una sala");
        actualizarEstadoFormulario();
    }

    private void volverAGestionFunciones() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/peliculas/PantallaGestionFunciones.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("Error de navegación", "No se pudo volver a la gestión de funciones: " + e.getMessage());
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaError(mensaje != null ? mensaje : "Error desconocido");
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaExito(mensaje != null ? mensaje : "Operación completada");
    }
}
