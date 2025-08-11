package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;

import com.cinemax.peliculas.modelos.entidades.FormatoFuncion;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.entidades.TipoEstreno;
import com.cinemax.peliculas.servicios.ServicioFuncion;
import com.cinemax.peliculas.servicios.ServicioPelicula;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.servicios.ServicioSala;
import com.cinemax.utilidades.ManejadorMetodosComunes;

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

/**
 * Controlador para el formulario de creación y edición de funciones cinematográficas.
 *
 * <p>Esta clase maneja la interfaz gráfica para crear nuevas funciones o editar
 * funciones existentes, incluyendo validaciones en tiempo real, cálculo automático
 * de horarios y manejo completo del ciclo de vida del formulario.
 *
 * <p>Funcionalidades principales:
 * <ul>
 *   <li>Creación de nuevas funciones con validaciones completas</li>
 *   <li>Edición de funciones existentes</li>
 *   <li>Validación en tiempo real de campos obligatorios</li>
 *   <li>Cálculo automático de hora de finalización</li>
 *   <li>Configuración dinámica de ComboBox con datos del sistema</li>
 *   <li>Manejo de modo creación vs. edición</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public class ControladorFormularioFuncion implements Initializable {

    /** Título de la pantalla */
    @FXML private Label lblTituloPantalla;
    
    /** Indicador de modo de edición */
    @FXML private Label lblModoEdicion;
    
    /** Estado actual del formulario */
    @FXML private Label lblEstadoFormulario;
    
    /** Duración calculada de la función */
    @FXML private Label lblDuracionCalculada;
    
    /** Capacidad de la sala seleccionada */
    @FXML private Label lblCapacidadSala;
    
    /** ComboBox para selección de película */
    @FXML private ComboBox<Pelicula> cmbPelicula;
    
    /** ComboBox para selección de sala */
    @FXML private ComboBox<Sala> cmbSala;
    
    /** ComboBox para selección de formato */
    @FXML private ComboBox<FormatoFuncion> cmbFormato;
    
    /** ComboBox para selección de tipo de estreno */
    @FXML private ComboBox<TipoEstreno> cmbTipoEstreno;
    
    /** Selector de fecha */
    @FXML private DatePicker dateFecha;
    
    /** Campo de hora de inicio */
    @FXML private TextField txtHoraInicio;
    
    /** Campo de hora de fin (calculado automáticamente) */
    @FXML private TextField txtHoraFin;
    
    /** Botón para guardar función */
    @FXML private Button btnGuardar;
    
    /** Botón para cancelar operación */
    @FXML private Button btnCancelar;
    
    /** Botón para limpiar formulario */
    @FXML private Button btnLimpiar;
    
    /** Botón para volver */
    @FXML private Button btnVolver;
    
    /** Botón para cerrar sesión */
    @FXML private Button btnLogOut;

    /** Servicio para operaciones con funciones */
    private ServicioFuncion servicioFuncion;
    
    /** Servicio para operaciones con películas */
    private ServicioPelicula servicioPelicula;
    
    /** Servicio para operaciones con salas */
    private ServicioSala servicioSala;
    
    /** Función en modo edición (null para creación) */
    private Funcion funcionEditando;
    
    /** Indicador de modo de edición */
    private boolean modoEdicion = false;

    /**
     * Constructor que inicializa los servicios necesarios.
     */
    public ControladorFormularioFuncion() {
        this.servicioFuncion = new ServicioFuncion();
        this.servicioPelicula = new ServicioPelicula();
        this.servicioSala = new ServicioSala();
    }

    /**
     * Inicializa el controlador después de que se ha cargado el FXML.
     * 
     * @param location La ubicación utilizada para resolver rutas relativas para el objeto raíz
     * @param resources Los recursos utilizados para localizar el objeto raíz
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarFormulario();
        configurarValidaciones();
        cargarDatos();
        actualizarEstadoFormulario();
    }

    /**
     * Configura los componentes del formulario con convertidores y validaciones.
     */
    private void configurarFormulario() {
        configurarComboBoxPeliculas();
        configurarComboBoxSalas();
        configurarComboBoxFormatos();
        configurarComboBoxTiposEstreno();
        configurarValidacionHora();
    }

    /**
     * Configura el ComboBox de películas con convertidor personalizado.
     */
    private void configurarComboBoxPeliculas() {
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
    }

    /**
     * Configura el ComboBox de salas con convertidor personalizado.
     */
    private void configurarComboBoxSalas() {
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
    }

    /**
     * Configura el ComboBox de formatos de función.
     */
    private void configurarComboBoxFormatos() {
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
    }

    /**
     * Configura el ComboBox de tipos de estreno.
     */
    private void configurarComboBoxTiposEstreno() {
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
    }

    /**
     * Configura la validación de entrada para el campo de hora.
     */
    private void configurarValidacionHora() {
        txtHoraInicio.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d{0,2}:?\\d{0,2}")) {
                txtHoraInicio.setText(oldText);
            } else {
                calcularHoraFin();
                actualizarEstadoFormulario();
            }
        });
    }

    /**
     * Configura las validaciones en tiempo real del formulario.
     */
    private void configurarValidaciones() {
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

    /**
     * Carga los datos necesarios para los ComboBox desde los servicios.
     */
    private void cargarDatos() {
        try {
            // Cargar películas
            List<Pelicula> peliculas = servicioPelicula.listarTodasLasPeliculas();
            cmbPelicula.setItems(FXCollections.observableArrayList(peliculas));

            // Cargar salas
            List<Sala> salas = servicioSala.listarSalas();
            cmbSala.setItems(FXCollections.observableArrayList(salas));
        } catch (Exception e) {
            mostrarError("Error al cargar datos", "No se pudieron cargar las películas o salas: " + e.getMessage());
        }
    }

    /**
     * Calcula automáticamente la hora de fin basándose en la duración de la película.
     * 
     * <p>Agrega 15 minutos adicionales para tiempo de limpieza entre funciones.
     */
    private void calcularHoraFin() {
        if (cmbPelicula.getValue() != null && !txtHoraInicio.getText().trim().isEmpty()) {
            try {
                LocalTime horaInicio = LocalTime.parse(txtHoraInicio.getText().trim(), DateTimeFormatter.ofPattern("H:mm"));
                LocalTime horaFin = horaInicio.plusMinutes(cmbPelicula.getValue().getDuracionMinutos() + 15);
                txtHoraFin.setText(horaFin.format(DateTimeFormatter.ofPattern("HH:mm")));
            } catch (DateTimeParseException e) {
                txtHoraFin.clear();
            }
        } else {
            txtHoraFin.clear();
        }
    }

    /**
     * Actualiza el estado visual del formulario basándose en la validación.
     */
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

    /**
     * Valida que todos los campos obligatorios estén completos y sean válidos.
     * 
     * @return true si el formulario es válido, false en caso contrario
     */
    private boolean esFormularioValido() {
        return cmbPelicula.getValue() != null &&
               cmbSala.getValue() != null &&
               dateFecha.getValue() != null &&
               !txtHoraInicio.getText().trim().isEmpty() &&
               cmbFormato.getValue() != null &&
               cmbTipoEstreno.getValue() != null &&
               esHoraValida(txtHoraInicio.getText().trim());
    }

    /**
     * Valida que una cadena represente una hora válida.
     * 
     * @param hora Cadena a validar
     * @return true si es una hora válida, false en caso contrario
     */
    private boolean esHoraValida(String hora) {
        try {
            LocalTime.parse(hora, DateTimeFormatter.ofPattern("H:mm"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Configura el formulario para editar una función existente.
     * 
     * @param funcion La función a editar
     */
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

    /**
     * Maneja el evento de guardar función (crear o actualizar).
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onGuardar(ActionEvent event) {
        try {
            if (!validarDatosFormulario()) {
                return;
            }

            LocalTime horaInicio = LocalTime.parse(txtHoraInicio.getText().trim(), DateTimeFormatter.ofPattern("H:mm"));
            LocalDateTime fechaHoraInicio = LocalDateTime.of(dateFecha.getValue(), horaInicio);

            if (modoEdicion) {
                actualizarFuncionExistente(fechaHoraInicio);
            } else {
                crearNuevaFuncion(fechaHoraInicio);
            }

            volverAGestionFunciones();

        } catch (Exception e) {
            String operacion = modoEdicion ? "actualizar" : "crear";
            mostrarError("Error al " + operacion + " función", "Error: " + e.getMessage());
        }
    }

    /**
     * Valida los datos del formulario antes de guardar.
     * 
     * @return true si los datos son válidos, false en caso contrario
     */
    private boolean validarDatosFormulario() {
        LocalDate fechaSeleccionada = dateFecha.getValue();
        if (fechaSeleccionada.isBefore(LocalDate.now())) {
            mostrarError("Error de validación", "La fecha no puede ser anterior a hoy");
            return false;
        }

        LocalTime horaInicio = LocalTime.parse(txtHoraInicio.getText().trim(), DateTimeFormatter.ofPattern("H:mm"));
        LocalDateTime fechaHoraInicio = LocalDateTime.of(fechaSeleccionada, horaInicio);

        if (fechaHoraInicio.isBefore(LocalDateTime.now())) {
            mostrarError("Error de validación", "La función no puede ser programada en el pasado");
            return false;
        }

        return true;
    }

    /**
     * Actualiza una función existente.
     * 
     * @param fechaHoraInicio Nueva fecha y hora de inicio
     * @throws Exception Si ocurre un error durante la actualización
     */
    private void actualizarFuncionExistente(LocalDateTime fechaHoraInicio) throws Exception {
        servicioFuncion.actualizarFuncion(
            funcionEditando.getId(),
            cmbPelicula.getValue(),
            cmbSala.getValue(),
            fechaHoraInicio,
            cmbFormato.getValue(),
            cmbTipoEstreno.getValue()
        );

        mostrarInformacion("Éxito", "Función actualizada exitosamente");
    }

    /**
     * Crea una nueva función.
     * 
     * @param fechaHoraInicio Fecha y hora de inicio
     * @throws Exception Si ocurre un error durante la creación
     */
    private void crearNuevaFuncion(LocalDateTime fechaHoraInicio) throws Exception {
        servicioFuncion.crearFuncion(
            cmbPelicula.getValue(),
            cmbSala.getValue(),
            fechaHoraInicio,
            cmbFormato.getValue(),
            cmbTipoEstreno.getValue()
        );

        mostrarInformacion("Éxito", "Función creada exitosamente");
    }

    /**
     * Maneja el evento de cancelar operación.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onCancelar(ActionEvent event) {
        volverAGestionFunciones();
    }

    /**
     * Maneja el evento de limpiar formulario.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onLimpiar(ActionEvent event) {
        if (!modoEdicion) {
            limpiarFormulario();
        }
    }

    /**
     * Maneja el evento de volver.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onVolver(ActionEvent event) {
        volverAGestionFunciones();
    }

    /**
     * Maneja el evento de cerrar sesión.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onLogOut(ActionEvent event) {
        ManejadorMetodosComunes.mostrarVentanaAdvertencia("Sesión cerrada");
        javafx.application.Platform.exit();
    }

    /**
     * Limpia todos los campos del formulario.
     */
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

    /**
     * Navega de vuelta a la pantalla de gestión de funciones.
     */
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

    /**
     * Muestra un mensaje de error al usuario.
     * 
     * @param titulo Título del mensaje
     * @param mensaje Contenido del mensaje de error
     */
    private void mostrarError(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaError(mensaje != null ? mensaje : "Error desconocido");
    }

    /**
     * Muestra un mensaje informativo al usuario.
     * 
     * @param titulo Título del mensaje
     * @param mensaje Contenido del mensaje informativo
     */
    private void mostrarInformacion(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaExito(mensaje != null ? mensaje : "Operación completada");
    }
}
