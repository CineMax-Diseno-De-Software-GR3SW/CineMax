package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.cinemax.peliculas.modelos.entidades.Genero;
import com.cinemax.peliculas.modelos.entidades.Idioma;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.servicios.ServicioPelicula;
import com.cinemax.utilidades.ManejadorMetodosComunes;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Controlador para el formulario de creación y edición de películas.
 *
 * <p>Esta clase maneja la interfaz gráfica para crear nuevas películas o editar
 * películas existentes, incluyendo validaciones en tiempo real, manejo de géneros
 * múltiples y configuración completa de todos los datos de una película.
 *
 * <p>Funcionalidades principales:
 * <ul>
 *   <li>Creación de nuevas películas con validaciones completas</li>
 *   <li>Edición de películas existentes</li>
 *   <li>Validación en tiempo real de campos obligatorios</li>
 *   <li>Manejo de selección múltiple de géneros</li>
 *   <li>Validación de rangos numéricos (año, duración)</li>
 *   <li>Configuración dinámica de ComboBox con datos estáticos</li>
 *   <li>Manejo de modo creación vs. edición</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public class ControladorFormularioPelicula implements Initializable {

    /** Título de la pantalla */
    @FXML private Label lblTituloPantalla;
    
    /** Indicador de modo de edición */
    @FXML private Label lblModoEdicion;
    
    /** Estado actual del formulario */
    @FXML private Label lblEstadoFormulario;
    
    /** Campo de título de la película */
    @FXML private TextField txtTitulo;
    
    /** Campo de año de estreno */
    @FXML private TextField txtAnio;
    
    /** Campo de duración en minutos */
    @FXML private TextField txtDuracion;
    
    /** Campo de URL de imagen */
    @FXML private TextField txtImagenUrl;
    
    /** Área de texto para sinopsis */
    @FXML private TextArea txtSinopsis;
    
    /** ComboBox para selección de idioma */
    @FXML private ComboBox<Idioma> cmbIdioma;
    
    /** ComboBox para selección de género principal */
    @FXML private ComboBox<String> cmbGenero;
    
    /** Lista para selección múltiple de géneros */
    @FXML private ListView<String> listGeneros;
    
    /** Botón para guardar película */
    @FXML private Button btnGuardar;
    
    /** Botón para cancelar operación */
    @FXML private Button btnCancelar;
    
    /** Botón para limpiar formulario */
    @FXML private Button btnLimpiar;
    
    /** Botón para volver */
    @FXML private Button btnVolver;
    
    /** Botón para cerrar sesión */
    @FXML private Button btnLogOut;

    /** Servicio para operaciones con películas */
    private ServicioPelicula servicioPelicula;
    
    /** Película en modo edición (null para creación) */
    private Pelicula peliculaEditando;
    
    /** Indicador de modo de edición */
    private boolean modoEdicion = false;

    /**
     * Constructor que inicializa el servicio de películas.
     */
    public ControladorFormularioPelicula() {
        this.servicioPelicula = new ServicioPelicula();
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
        actualizarEstadoFormulario();
    }

    /**
     * Configura los componentes del formulario con validaciones y convertidores.
     */
    private void configurarFormulario() {
        configurarTextArea();
        configurarComboBoxIdiomas();
        configurarComboBoxGeneros();
        configurarListaGeneros();
        configurarValidacionesNumericas();
    }

    /**
     * Configura el área de texto para sinopsis.
     */
    private void configurarTextArea() {
        txtSinopsis.setWrapText(true);
        txtSinopsis.setEditable(true);
        txtSinopsis.setDisable(false);
    }

    /**
     * Configura el ComboBox de idiomas con convertidor personalizado.
     */
    private void configurarComboBoxIdiomas() {
        cmbIdioma.setItems(FXCollections.observableArrayList(Idioma.values()));
        cmbIdioma.setConverter(new StringConverter<Idioma>() {
            @Override
            public String toString(Idioma idioma) {
                return idioma != null ? idioma.getNombre() : "";
            }

            @Override
            public Idioma fromString(String string) {
                return null;
            }
        });
    }

    /**
     * Configura el ComboBox de géneros con datos estáticos.
     */
    private void configurarComboBoxGeneros() {
        cmbGenero.setItems(FXCollections.observableArrayList(Genero.obtenerTodosLosGeneros()));
    }

    /**
     * Configura la lista de géneros para selección múltiple.
     */
    private void configurarListaGeneros() {
        listGeneros.setItems(FXCollections.observableArrayList(Genero.obtenerTodosLosGeneros()));
        listGeneros.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Configura las validaciones para campos numéricos.
     */
    private void configurarValidacionesNumericas() {
        txtAnio.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                txtAnio.setText(newText.replaceAll("[^\\d]", ""));
            }
        });

        txtDuracion.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                txtDuracion.setText(newText.replaceAll("[^\\d]", ""));
            }
        });
    }

    /**
     * Configura las validaciones en tiempo real del formulario.
     */
    private void configurarValidaciones() {
        Runnable validarFormulario = this::actualizarEstadoFormulario;

        txtTitulo.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        txtSinopsis.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        txtDuracion.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        txtAnio.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        cmbIdioma.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
        cmbGenero.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
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
     * Valida que todos los campos obligatorios estén completos.
     * 
     * @return true si el formulario es válido, false en caso contrario
     */
    private boolean esFormularioValido() {
        return !txtTitulo.getText().trim().isEmpty() &&
               !txtSinopsis.getText().trim().isEmpty() &&
               !txtDuracion.getText().trim().isEmpty() &&
               !txtAnio.getText().trim().isEmpty() &&
               cmbIdioma.getValue() != null &&
               cmbGenero.getValue() != null;
    }

    /**
     * Configura el formulario para editar una película existente.
     * 
     * @param pelicula La película a editar
     */
    public void configurarParaEdicion(Pelicula pelicula) {
        this.peliculaEditando = pelicula;
        this.modoEdicion = true;

        lblTituloPantalla.setText("Editar Película");
        lblModoEdicion.setText("Modo: Edición - ID: " + pelicula.getId());
        btnGuardar.setText("Guardar Cambios");

        cargarDatosPelicula(pelicula);
        actualizarEstadoFormulario();
    }

    /**
     * Carga los datos de una película en el formulario.
     * 
     * @param pelicula Película cuyos datos cargar
     */
    private void cargarDatosPelicula(Pelicula pelicula) {
        txtTitulo.setText(pelicula.getTitulo());
        txtSinopsis.setText(pelicula.getSinopsis() != null ? pelicula.getSinopsis() : "");
        txtDuracion.setText(String.valueOf(pelicula.getDuracionMinutos()));
        txtAnio.setText(String.valueOf(pelicula.getAnio()));
        txtImagenUrl.setText(pelicula.getImagenUrl() != null ? pelicula.getImagenUrl() : "");

        cmbIdioma.setValue(pelicula.getIdioma());

        cargarGenerosPelicula(pelicula);
    }

    /**
     * Carga los géneros de la película en los controles correspondientes.
     * 
     * @param pelicula Película cuyos géneros cargar
     */
    private void cargarGenerosPelicula(Pelicula pelicula) {
        String generosActuales = pelicula.getGenerosComoString();
        if (generosActuales != null && !generosActuales.isEmpty()) {
            String primerGenero = generosActuales.split(",")[0].trim();
            cmbGenero.setValue(primerGenero);

            // Preseleccionar géneros en la lista
            listGeneros.getSelectionModel().clearSelection();
            String[] generos = generosActuales.split(",");
            for (String genero : generos) {
                String generoLimpio = genero.trim();
                if (listGeneros.getItems().contains(generoLimpio)) {
                    listGeneros.getSelectionModel().select(generoLimpio);
                }
            }
        }
    }

    /**
     * Maneja el evento de guardar película (crear o actualizar).
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onGuardar(ActionEvent event) {
        try {
            if (!validarDatosNumericos()) {
                return;
            }

            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            int anio = Integer.parseInt(txtAnio.getText().trim());

            if (!validarRangosNumericos(duracion, anio)) {
                return;
            }

            String generosString = construirStringGeneros();
            String imagenUrl = txtImagenUrl.getText().trim().isEmpty() ? null : txtImagenUrl.getText().trim();

            if (modoEdicion) {
                actualizarPeliculaExistente(duracion, anio, generosString, imagenUrl);
            } else {
                crearNuevaPelicula(duracion, anio, generosString, imagenUrl);
            }

            volverAPantallaPrincipal();

        } catch (NumberFormatException e) {
            mostrarError("Error de formato", "La duración y el año deben ser números válidos");
        } catch (Exception e) {
            String operacion = modoEdicion ? "actualizar" : "crear";
            mostrarError("Error al " + operacion + " película", "Error: " + e.getMessage());
        }
    }

    /**
     * Valida que los campos numéricos contengan números válidos.
     * 
     * @return true si son válidos, false en caso contrario
     */
    private boolean validarDatosNumericos() {
        try {
            Integer.parseInt(txtDuracion.getText().trim());
            Integer.parseInt(txtAnio.getText().trim());
            return true;
        } catch (NumberFormatException e) {
            mostrarError("Error de formato", "La duración y el año deben ser números válidos");
            return false;
        }
    }

    /**
     * Valida que los valores numéricos estén en rangos aceptables.
     * 
     * @param duracion Duración a validar
     * @param anio Año a validar
     * @return true si están en rango válido, false en caso contrario
     */
    private boolean validarRangosNumericos(int duracion, int anio) {
        if (duracion <= 0) {
            mostrarError("Error de validación", "La duración debe ser un número positivo");
            return false;
        }

        if (anio < 1900 || anio > 2030) {
            mostrarError("Error de validación", "El año debe estar entre 1900 y 2030");
            return false;
        }

        return true;
    }

    /**
     * Construye la cadena de géneros basándose en las selecciones del usuario.
     * 
     * @return Cadena con los géneros separados por comas
     */
    private String construirStringGeneros() {
        StringBuilder generosBuilder = new StringBuilder();
        generosBuilder.append(cmbGenero.getValue());

        List<String> generosAdicionales = listGeneros.getSelectionModel().getSelectedItems();
        for (String genero : generosAdicionales) {
            if (!genero.equals(cmbGenero.getValue())) {
                generosBuilder.append(", ").append(genero);
            }
        }

        return generosBuilder.toString();
    }

    /**
     * Actualiza una película existente.
     * 
     * @param duracion Nueva duración
     * @param anio Nuevo año
     * @param generosString Nuevos géneros
     * @param imagenUrl Nueva URL de imagen
     * @throws Exception Si ocurre un error durante la actualización
     */
    private void actualizarPeliculaExistente(int duracion, int anio, String generosString, String imagenUrl) throws Exception {
        servicioPelicula.actualizarPelicula(
            peliculaEditando.getId(),
            txtTitulo.getText().trim(),
            txtSinopsis.getText().trim(),
            duracion,
            anio,
            cmbIdioma.getValue(),
            generosString,
            imagenUrl
        );

        mostrarInformacion("Éxito", "Película actualizada exitosamente: " + txtTitulo.getText().trim());
    }

    /**
     * Crea una nueva película.
     * 
     * @param duracion Duración de la película
     * @param anio Año de estreno
     * @param generosString Géneros de la película
     * @param imagenUrl URL de la imagen
     * @throws Exception Si ocurre un error durante la creación
     */
    private void crearNuevaPelicula(int duracion, int anio, String generosString, String imagenUrl) throws Exception {
        Pelicula nuevaPelicula = servicioPelicula.crearPelicula(
            txtTitulo.getText().trim(),
            txtSinopsis.getText().trim(),
            duracion,
            anio,
            cmbIdioma.getValue(),
            generosString,
            imagenUrl
        );

        mostrarInformacion("Éxito", "Película creada exitosamente: " + nuevaPelicula.getTitulo());
    }

    /**
     * Maneja el evento de cancelar operación.
     * 
     * @param event Evento de acción del botón
     */
    @FXML
    private void onCancelar(ActionEvent event) {
        volverAPantallaPrincipal();
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
        volverAPantallaPrincipal();
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
        txtTitulo.clear();
        txtSinopsis.clear();
        txtDuracion.clear();
        txtAnio.clear();
        txtImagenUrl.clear();
        cmbIdioma.setValue(null);
        cmbGenero.setValue(null);
        listGeneros.getSelectionModel().clearSelection();
        actualizarEstadoFormulario();
    }

    /**
     * Navega de vuelta a la pantalla principal de gestión de películas.
     */
    private void volverAPantallaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/peliculas/PantallaGestionPeliculas.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("Error de navegación", "No se pudo volver a la pantalla principal: " + e.getMessage());
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
