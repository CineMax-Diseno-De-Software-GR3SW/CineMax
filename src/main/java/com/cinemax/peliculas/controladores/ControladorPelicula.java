package com.cinemax.peliculas.controladores;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.comun.conexiones.ConexionFirebaseStorage;
import com.cinemax.peliculas.modelos.entidades.Genero;
import com.cinemax.peliculas.modelos.entidades.Idioma;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.servicios.ServicioPelicula;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

/**
 * Controlador para la gestión integral de películas cinematográficas.
 *
 * <p>Esta clase maneja la interfaz gráfica que combina la gestión de películas
 * con un formulario integrado para creación y edición. Proporciona una experiencia
 * unificada donde los usuarios pueden ver, buscar, crear, editar y eliminar
 * películas desde una sola pantalla.
 *
 * <p>Funcionalidades principales:
 * <ul>
 *   <li>Gestión completa de películas (CRUD) con formulario integrado</li>
 *   <li>Visualización en tabla con filtrado dinámico por género</li>
 *   <li>Formulario de creación/edición embebido en la misma vista</li>
 *   <li>Validaciones en tiempo real y manejo de géneros múltiples</li>
 *   <li>Modo dual: creación de nuevas películas y edición de existentes</li>
 *   <li>Búsqueda avanzada con filtros combinados</li>
 *   <li>Estadísticas y resúmenes informativos</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public class ControladorPelicula implements Initializable {

    /** Servicio para operaciones de negocio con películas */
    private ServicioPelicula servicioPelicula;
    private ConexionFirebaseStorage conexionStorage;
    private File archivoImagenSeleccionada;
    
    // Componentes de la interfaz FXML para búsqueda y tabla
    /** Campo de texto para búsqueda general */
    @FXML private TextField txtBuscar;

    /** ComboBox para filtrar por género */
    @FXML private ComboBox<String> cmbFiltroGenero;

    /** Tabla principal de películas */
    @FXML private TableView<Pelicula> tablaPeliculas;

    /** Columna de ID de película */
    @FXML private TableColumn<Pelicula, Integer> colId;

    /** Columna de título */
    @FXML private TableColumn<Pelicula, String> colTitulo;

    /** Columna de año de estreno */
    @FXML private TableColumn<Pelicula, Integer> colAnio;

    /** Columna de género */
    @FXML private TableColumn<Pelicula, String> colGenero;

    /** Columna de duración */
    @FXML private TableColumn<Pelicula, Integer> colDuracion;

    /** Columna de idioma */
    @FXML private TableColumn<Pelicula, String> colIdioma;

    /** Botón para crear nueva película */
    @FXML private Button btnNuevaPelicula;

    /** Botón para realizar búsqueda */
    @FXML private Button btnBuscar;

    /** Botón para limpiar filtros */
    @FXML private Button btnLimpiar;

    /** Botón para eliminar película seleccionada */
    @FXML private Button btnEliminar;

    /** Botón para ver detalles de película */
    @FXML private Button btnVerDetalles;

    /** Botón para volver al menú principal */
    @FXML private Button btnVolver;

    /** Label que muestra el total de películas */
    @FXML private Label lblTotalPeliculas;

    /** Label que muestra estadísticas adicionales */
    @FXML private Label lblEstadisticas;

    // Campos del formulario de película integrado
    /** Campo de texto para título */
    @FXML private TextField txtTitulo;

    /** Campo de texto para año */
    @FXML private TextField txtAnio;

    /** Campo de texto para duración */
    @FXML private TextField txtDuracion;

    /** ComboBox para idioma */
    @FXML private ComboBox<Idioma> cmbIdioma;

    /** Área de texto para sinopsis */
    @FXML private TextArea txtSinopsis;

    /** ComboBox para género principal */
    @FXML private ComboBox<String> cmbGenero;

    /** Lista para géneros adicionales */
    @FXML private ListView<String> listGeneros;

    /** Campo de texto para URL de imagen */
    //@FXML private TextField txtImagenUrl;

    /** Botón para guardar película */
    @FXML private Button btnGuardar;

    /** Botón para crear nueva película desde formulario */
    @FXML private Button btnNuevo;

    /** Botón para cancelar operación */
    @FXML private Button btnCancelar;

    /** Botón para limpiar formulario */
    @FXML private Button btnLimpiarFormulario;

    @FXML private Button btnSeleccionarImagen;
    @FXML private ImageView imgVistaPreviaPelicula;

    // Datos para la gestión de películas
    /** Lista observable de todas las películas */
    private ObservableList<Pelicula> listaPeliculas;

    /** Lista observable de películas filtradas */
    private ObservableList<Pelicula> peliculasFiltradas;

    /** Película actualmente en modo de edición */
    private Pelicula peliculaEnEdicion = null;

    /**
     * Constructor que inicializa el servicio de películas.
     */
    public ControladorPelicula() {
        this.servicioPelicula = new ServicioPelicula();
        this.conexionStorage = ConexionFirebaseStorage.getInstancia();
    }

    /**
     * Inicializa el controlador después de que se ha cargado el FXML.
     *
     * @param location La ubicación utilizada para resolver rutas relativas para el objeto raíz
     * @param resources Los recursos utilizados para localizar el objeto raíz
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listaPeliculas = FXCollections.observableArrayList();
        peliculasFiltradas = FXCollections.observableArrayList();

        configurarTabla();
        configurarFiltros();
        configurarEventos();
        configurarFormularioPelicula();
        cargarPeliculas();
    }

    /**
     * Maneja el evento de crear nueva película (navegación).
     *
     * @param event Evento de acción del botón
     */
    @FXML
    private void onNuevaPelicula(ActionEvent event) {
        navegarAFormularioPelicula(null);
    }

    /**
     * Maneja el evento de guardar película (crear o actualizar).
     *
     * @param event Evento de acción del botón
     */
    @FXML
    private void onGuardar(ActionEvent event) {
        try {
            if (!validarFormularioCompleto()) {
                mostrarError("Formulario incompleto", "Por favor complete todos los campos obligatorios");
                return;
            }

            if (!validarDatosNumericos()) {
                return;
            }

            
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            int anio = Integer.parseInt(txtAnio.getText().trim());
            
            if (!validarRangosNumericos(duracion, anio)) {
                return;
            }

           // String imagenUrlParaGuardar = txtImagenUrl.getText().trim(); 
           String imagenUrlParaGuardar = "";
           
            if (peliculaEnEdicion != null) {
             imagenUrlParaGuardar = peliculaEnEdicion.getImagenUrl();
}
            if (archivoImagenSeleccionada != null) {
                try {
                    // Aquí podrías poner un label de "Subiendo..."
                    imagenUrlParaGuardar = conexionStorage.subirImagenYObtenerUrl(archivoImagenSeleccionada);
                } catch (Exception e) {
                    mostrarError("Error de Carga", "No se pudo subir la imagen a Firebase: " + e.getMessage());
                    e.printStackTrace();
                    return; // Detiene el guardado si la imagen falla
                }
            }

            String generosString = construirStringGeneros();
            //String imagenUrl = txtImagenUrl.getText().trim().isEmpty() ? null : txtImagenUrl.getText().trim();

            if (peliculaEnEdicion == null) {
                crearNuevaPelicula(duracion, anio, generosString, imagenUrlParaGuardar);
            } else {
                actualizarPeliculaExistente(duracion, anio, generosString, imagenUrlParaGuardar);
            }

        } catch (NumberFormatException e) {
            mostrarError("Error de formato", "La duración y el año deben ser números válidos");
        } catch (Exception e) {
            String operacion = peliculaEnEdicion != null ? "actualizar" : "crear";
            mostrarError("Error al " + operacion + " película", "Error: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de selección de imagen para la película.
     */
    @FXML
    private void onSeleccionarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen de la Película");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg", "*.gif"),
            new FileChooser.ExtensionFilter("Todos los Archivos", "*.*")
        );
        
        Stage stage = (Stage) btnSeleccionarImagen.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(stage);
        
        if (archivo != null) {
            this.archivoImagenSeleccionada = archivo;
            Image imagen = new Image(archivo.toURI().toString());
            imgVistaPreviaPelicula.setImage(imagen);
            //txtImagenUrl.clear();
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
     * Crea una nueva película con los datos del formulario.
     *
     * @param duracion Duración de la película
     * @param anio Año de estreno
     * @param generosString Géneros de la película
     * @param imagenUrl URL de la imagen
     */
    private void crearNuevaPelicula(int duracion, int anio, String generosString, String imagenUrl) {
        try {
            verificarDuplicados(anio);

            Pelicula nuevaPelicula = servicioPelicula.crearPelicula(
                    txtTitulo.getText().trim(),
                    txtSinopsis.getText().trim(),
                    duracion,
                    anio,
                    cmbIdioma.getValue(),
                    generosString,
                    imagenUrl
            );

            cargarPeliculas();
            seleccionarPeliculaEnTabla(nuevaPelicula.getId());
            limpiarFormulario();
            mostrarInformacion("Éxito", "Película creada exitosamente:\n" + nuevaPelicula.getTitulo());

        } catch (Exception e) {
            mostrarError("Error al crear película", "Error: " + e.getMessage());
        }
    }

    /**
     * Actualiza una película existente with los datos del formulario.
     *
     * @param duracion Nueva duración
     * @param anio Nuevo año
     * @param generosString Nuevos géneros
     * @param imagenUrl Nueva URL de imagen
     */
    private void actualizarPeliculaExistente(int duracion, int anio, String generosString, String imagenUrl) {
        try {
            int idPeliculaEditada = peliculaEnEdicion.getId();

            if (cambiaronDatosIdentificadores(anio)) {
                verificarDuplicados(anio);
            }

            servicioPelicula.actualizarPelicula(
                    idPeliculaEditada,
                    txtTitulo.getText().trim(),
                    txtSinopsis.getText().trim(),
                    duracion,
                    anio,
                    cmbIdioma.getValue(),
                    generosString,
                    imagenUrl
            );

            peliculaEnEdicion = null;
            actualizarModoFormulario();
            cargarPeliculas();
            seleccionarPeliculaEnTabla(idPeliculaEditada);
            mostrarInformacion("Éxito", "Película actualizada exitosamente:\n" + txtTitulo.getText().trim());

        } catch (Exception e) {
            mostrarError("Error al actualizar película", "Error: " + e.getMessage());
        }
    }

    /**
     * Verifica si cambiaron los datos identificadores de la película.
     *
     * @param anio Nuevo año
     * @return true si cambiaron, false en caso contrario
     */
    private boolean cambiaronDatosIdentificadores(int anio) {
        return !txtTitulo.getText().trim().equals(peliculaEnEdicion.getTitulo()) ||
               anio != peliculaEnEdicion.getAnio();
    }

    /**
     * Verifica si existe una película duplicada y muestra advertencia.
     *
     * @param anio Año de la película
     * @throws Exception Si ocurre un error en la verificación
     */
    private void verificarDuplicados(int anio) throws Exception {
        boolean existe = servicioPelicula.existePeliculaDuplicada(txtTitulo.getText().trim(), anio);
        if (existe) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia(
                    "Ya existe una película con ese título y año. Se continuará con el registro.");
        }
    }

    /**
     * Selecciona una película específica en la tabla por su ID.
     *
     * @param idPelicula ID de la película a seleccionar
     */
    private void seleccionarPeliculaEnTabla(int idPelicula) {
        for (Pelicula pelicula : peliculasFiltradas) {
            if (pelicula.getId() == idPelicula) {
                tablaPeliculas.getSelectionModel().select(pelicula);
                break;
            }
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
        if (txtTitulo != null) txtTitulo.clear();
        if (txtAnio != null) txtAnio.clear();
        if (txtDuracion != null) txtDuracion.clear();
        if (txtSinopsis != null) txtSinopsis.clear();
       // if (txtImagenUrl != null) txtImagenUrl.clear();
        if (cmbIdioma != null) cmbIdioma.setValue(null);
        if (cmbGenero != null) cmbGenero.setValue(null);
        if (listGeneros != null) listGeneros.getSelectionModel().clearSelection();

        if (imgVistaPreviaPelicula != null) {
        imgVistaPreviaPelicula.setImage(null);
        }
        this.archivoImagenSeleccionada = null;
        peliculaEnEdicion = null;
        actualizarModoFormulario();
        tablaPeliculas.getSelectionModel().clearSelection();
    }

    /**
     * Actualiza el modo visual del formulario (crear vs editar).
     */
    private void actualizarModoFormulario() {
        if (peliculaEnEdicion == null) {
            btnGuardar.setText("Crear");
            if (btnNuevo != null) {
                btnNuevo.setVisible(false);
                btnNuevo.setManaged(false);
            }
        } else {
            btnGuardar.setText("Actualizar");
            if (btnNuevo != null) {
                btnNuevo.setVisible(true);
                btnNuevo.setManaged(true);
            }
        }
        actualizarEstadoFormulario();
    }

    /**
     * Carga los datos de una película en el formulario para edición.
     * (Versión con Debugging)
     *
     * @param pelicula Película a cargar en el formulario
     */
    private void cargarDatosEnFormulario(Pelicula pelicula) {

        if (pelicula == null) {
            System.out.println("-> [DEBUG] ERROR: La película recibida es nula. Saliendo del método.");
            System.out.println("=======================================================");
            return;
        }

        txtTitulo.setText(pelicula.getTitulo());
        txtAnio.setText(String.valueOf(pelicula.getAnio()));
        txtDuracion.setText(String.valueOf(pelicula.getDuracionMinutos()));
        txtSinopsis.setText(pelicula.getSinopsis() != null ? pelicula.getSinopsis() : "");
        
        // txtImagenUrl.setText(pelicula.getImagenUrl() != null ? pelicula.getImagenUrl() : "");

        cmbIdioma.setValue(pelicula.getIdioma());
        this.archivoImagenSeleccionada = null; 
        imgVistaPreviaPelicula.setImage(null);   

        if (pelicula.getImagenUrl() != null && !pelicula.getImagenUrl().isEmpty()) {
            String urlParaCargar = pelicula.getImagenUrl();
            try {
                Image imagen = new Image(urlParaCargar, true); 
                imgVistaPreviaPelicula.setImage(imagen);
            } catch (Exception e) {
                e.printStackTrace(); // Imprime el error completo en la consola
            }
        } 
        cargarGenerosPelicula(pelicula);
        peliculaEnEdicion = pelicula;
        actualizarModoFormulario();
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
     * Maneja el evento de eliminar película seleccionada.
     *
     * @param event Evento de acción del botón
     */
    @FXML
    private void onEliminarPelicula(ActionEvent event) {
        Pelicula peliculaSeleccionada = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (peliculaSeleccionada != null) {
            confirmarYEliminarPelicula(peliculaSeleccionada);
        }
    }

    /**
     * Confirma y procede con la eliminación de una película.
     *
     * @param pelicula Película a eliminar
     */
    private void confirmarYEliminarPelicula(Pelicula pelicula) {
        String mensaje = "¿Está seguro de eliminar esta película?\n\n" +
                "Título: " + pelicula.getTitulo() +
                "\n\nATENCIÓN: Esta acción no se puede deshacer.";
        ManejadorMetodosComunes.mostrarVentanaAdvertencia(mensaje);

        try {
            servicioPelicula.eliminarPelicula(pelicula.getId());
            cargarPeliculas();
            mostrarInformacion("Éxito", "Película eliminada correctamente");
        } catch (Exception e) {
            manejarErrorEliminacion(e, pelicula);
        }
    }

    /**
     * Maneja errores específicos de eliminación de películas.
     *
     * @param e Excepción ocurrida
     * @param pelicula Película que se intentó eliminar
     */
    private void manejarErrorEliminacion(Exception e, Pelicula pelicula) {
        String mensajeError = e.getMessage();
        if (mensajeError.contains("foreign key constraint") || mensajeError.contains("violates")) {
            mostrarErrorRestriccion(pelicula);
        } else {
            mostrarError("Error", "No se pudo eliminar la película: " + mensajeError);
        }
    }

    /**
     * Muestra un mensaje de error específico para restricciones de eliminación.
     *
     * @param pelicula Película que no se pudo eliminar
     */
    private void mostrarErrorRestriccion(Pelicula pelicula) {
        String mensaje = "No se puede eliminar la película '" + pelicula.getTitulo() +
                "' porque está asociada con:\n\n" +
                "• Funciones programadas\n" +
                "• Cartelera\n" +
                "• Boletos vendidos\n" +
                "• Reservas existentes\n\n" +
                "ACCIÓN REQUERIDA:\n" +
                "Para eliminar esta película, primero debe eliminar todas las funciones\n" +
                "y entradas de cartelera asociadas en el gestor correspondiente.";

        ManejadorMetodosComunes.mostrarVentanaError(mensaje);
    }

    /**
     * Maneja el evento de ver detalles de película.
     *
     * @param event Evento de acción del botón
     */
    @FXML
    private void onVerDetalles(ActionEvent event) {
        Pelicula peliculaSeleccionada = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (peliculaSeleccionada != null) {
            navegarADetallesPelicula(peliculaSeleccionada);
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
     * Maneja el evento de limpiar filtros.
     *
     * @param event Evento de acción del botón
     */
    @FXML
    private void onLimpiar(ActionEvent event) {
        txtBuscar.clear();
        cmbFiltroGenero.setValue("Todos");
        aplicarFiltros();

        if (imgVistaPreviaPelicula != null) {
        imgVistaPreviaPelicula.setImage(null);
        }
        this.archivoImagenSeleccionada = null;

        peliculaEnEdicion = null;
        actualizarModoFormulario();
        tablaPeliculas.getSelectionModel().clearSelection();
    }

    /**
     * Configura la tabla de películas con sus columnas y eventos.
     */
    private void configurarTabla() {
        configurarColumnas();
        configurarSeleccionTabla();
        tablaPeliculas.setItems(peliculasFiltradas);
    }

    /**
     * Configura las columnas de la tabla con sus respectivos cell value factories.
     */
    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracionMinutos"));

        colGenero.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getGenerosComoString()
            );
        });

        colIdioma.setCellValueFactory(cellData -> {
            Idioma idioma = cellData.getValue().getIdioma();
            return new javafx.beans.property.SimpleStringProperty(
                    idioma != null ? idioma.getNombre() : "N/A"
            );
        });
    }

    /**
     * Configura el comportamiento de selección de la tabla.
     */
    private void configurarSeleccionTabla() {
        tablaPeliculas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean peliculaSeleccionada = newSelection != null;
                    btnEliminar.setDisable(!peliculaSeleccionada);
                    btnVerDetalles.setDisable(!peliculaSeleccionada);

                    if (peliculaSeleccionada) {
                        cargarDatosEnFormulario(newSelection);
                    } else {
                        limpiarFormulario();
                        peliculaEnEdicion = null;
                        actualizarModoFormulario();
                    }
                }
        );
    }

    /**
     * Configura los filtros de búsqueda y género.
     */
    private void configurarFiltros() {
        cmbFiltroGenero.getItems().addAll(Genero.obtenerTodosLosGeneros());
        cmbFiltroGenero.setValue("Todos");
        cmbFiltroGenero.setOnAction(e -> aplicarFiltros());
    }

    /**
     * Actualiza dinámicamente los géneros del filtro basándose en las películas cargadas.
     */
    private void actualizarFiltroGeneros() {
        String valorActual = cmbFiltroGenero.getValue();
        cmbFiltroGenero.getItems().clear();
        cmbFiltroGenero.getItems().add("Todos");

        Set<String> generosUnicos = new HashSet<>();
        for (Pelicula pelicula : listaPeliculas) {
            if (!pelicula.getGeneros().isEmpty()) {
                for (com.cinemax.peliculas.modelos.entidades.Genero genero : pelicula.getGeneros()) {
                    generosUnicos.add(genero.getNombre());
                }
            }
        }

        List<String> generosOrdenados = new ArrayList<>(generosUnicos);
        Collections.sort(generosOrdenados);
        cmbFiltroGenero.getItems().addAll(generosOrdenados);

        if (valorActual != null && cmbFiltroGenero.getItems().contains(valorActual)) {
            cmbFiltroGenero.setValue(valorActual);
        } else {
            cmbFiltroGenero.setValue("Todos");
        }
    }

    /**
     * Configura los eventos de la interfaz.
     */
    private void configurarEventos() {
        txtBuscar.textProperty().addListener((obs, oldText, newText) -> aplicarFiltros());
    }

    /**
     * Configura el formulario integrado de película.
     */
    private void configurarFormularioPelicula() {
        configurarComboBoxIdiomas();
        configurarComboBoxGeneros();
        configurarListaGeneros();
        configurarValidacionesNumericas();
        configurarTextArea();
        configurarValidacionesFormulario();
    }

    /**
     * Configura el ComboBox de idiomas.
     */
    private void configurarComboBoxIdiomas() {
        if (cmbIdioma != null) {
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
    }

    /**
     * Configura el ComboBox de géneros.
     */
    private void configurarComboBoxGeneros() {
        if (cmbGenero != null) {
            cmbGenero.setItems(FXCollections.observableArrayList(Genero.obtenerTodosLosGeneros()));
        }
    }

    /**
     * Configura la lista de géneros adicionales.
     */
    private void configurarListaGeneros() {
        if (listGeneros != null) {
            listGeneros.setItems(FXCollections.observableArrayList(Genero.obtenerTodosLosGeneros()));
            listGeneros.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
    }

    /**
     * Configura las validaciones para campos numéricos.
     */
    private void configurarValidacionesNumericas() {
        if (txtAnio != null) {
            txtAnio.textProperty().addListener((obs, oldText, newText) -> {
                if (!newText.matches("\\d*")) {
                    txtAnio.setText(newText.replaceAll("[^\\d]", ""));
                }
            });
        }

        if (txtDuracion != null) {
            txtDuracion.textProperty().addListener((obs, oldText, newText) -> {
                if (!newText.matches("\\d*")) {
                    txtDuracion.setText(newText.replaceAll("[^\\d]", ""));
                }
            });
        }
    }

    /**
     * Configura el área de texto para sinopsis.
     */
    private void configurarTextArea() {
        if (txtSinopsis != null) {
            txtSinopsis.setWrapText(true);
            txtSinopsis.setEditable(true);
            txtSinopsis.setDisable(false);
        }
    }

    /**
     * Configura las validaciones del formulario en tiempo real.
     */
    private void configurarValidacionesFormulario() {
        Runnable validarFormulario = this::actualizarEstadoFormulario;

        if (txtTitulo != null) {
            txtTitulo.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        }
        if (txtSinopsis != null) {
            txtSinopsis.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        }
        if (txtDuracion != null) {
            txtDuracion.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        }
        if (txtAnio != null) {
            txtAnio.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        }
        if (cmbIdioma != null) {
            cmbIdioma.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
        }
        if (cmbGenero != null) {
            cmbGenero.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
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
        return txtTitulo != null && !txtTitulo.getText().trim().isEmpty() &&
                txtAnio != null && !txtAnio.getText().trim().isEmpty() &&
                txtDuracion != null && !txtDuracion.getText().trim().isEmpty() &&
                txtSinopsis != null && !txtSinopsis.getText().trim().isEmpty() &&
                cmbIdioma != null && cmbIdioma.getValue() != null &&
                cmbGenero != null && cmbGenero.getValue() != null;
    }

    /**
     * Carga todas las películas desde la base de datos.
     */
    private void cargarPeliculas() {
        try {
            listaPeliculas.clear();
            listaPeliculas.addAll(servicioPelicula.listarTodasLasPeliculas());
            actualizarFiltroGeneros();
            aplicarFiltros();
        } catch (Exception e) {
            mostrarError("Error al cargar películas", e.getMessage());
        }
    }

    /**
     * Aplica los filtros de búsqueda y género a la lista de películas.
     */
    private void aplicarFiltros() {
        peliculasFiltradas.clear();

        String textoBusqueda = txtBuscar.getText().toLowerCase().trim();
        String generoSeleccionado = cmbFiltroGenero.getValue();

        for (Pelicula pelicula : listaPeliculas) {
            boolean coincideTexto = textoBusqueda.isEmpty() ||
                    pelicula.getTitulo().toLowerCase().contains(textoBusqueda) ||
                    pelicula.getGenerosComoString().toLowerCase().contains(textoBusqueda);

            boolean coincideGenero = coincideConGenero(pelicula.getGenerosComoString(), generoSeleccionado);

            if (coincideTexto && coincideGenero) {
                peliculasFiltradas.add(pelicula);
            }
        }

        actualizarEstadisticas();
    }

    /**
     * Verifica si una película coincide con el género seleccionado.
     *
     * @param generosPelicula Géneros de la película
     * @param generoFiltro Género del filtro
     * @return true si coincide, false en caso contrario
     */
    private boolean coincideConGenero(String generosPelicula, String generoFiltro) {
        if (generoFiltro == null || "Todos".equals(generoFiltro)) {
            return true;
        }

        if (generosPelicula == null || generosPelicula.trim().isEmpty()) {
            return false;
        }

        String generosPeliculaLower = generosPelicula.toLowerCase();
        String generoFiltroLower = generoFiltro.toLowerCase();

        String[] generos = generosPeliculaLower.split(",");
        for (String genero : generos) {
            String generoLimpio = genero.trim();
            if (generoLimpio.equals(generoFiltroLower)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Actualiza las estadísticas mostradas en la interfaz.
     */
    private void actualizarEstadisticas() {
        int total = peliculasFiltradas.size();
        lblTotalPeliculas.setText("Total de películas: " + total);

        if (total > 0) {
            double duracionPromedio = peliculasFiltradas.stream()
                    .mapToInt(Pelicula::getDuracionMinutos)
                    .average()
                    .orElse(0);
            lblEstadisticas.setText(String.format("Duración promedio: %.1f min", duracionPromedio));
        } else {
            lblEstadisticas.setText("No hay películas que mostrar");
        }
    }

    /**
     * Navega a la pantalla de formulario de película.
     *
     * @param pelicula Película a editar (null para crear nueva)
     */
    private void navegarAFormularioPelicula(Pelicula pelicula) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/peliculas/PantallaFormularioPelicula.fxml"));
            Parent root = loader.load();

            if (pelicula != null) {
                ControladorFormularioPelicula controlador = loader.getController();
                controlador.configurarParaEdicion(pelicula);
            }

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("Error de navegación", "No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    /**
     * Navega a la pantalla de detalles de una película específica.
     *
     * @param pelicula Película de la cual mostrar detalles
     */
    private void navegarADetallesPelicula(Pelicula pelicula) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/peliculas/PantallaDetallesPelicula.fxml"));
            Parent root = loader.load();

            ControladorDetallesPelicula controlador = loader.getController();
            controlador.cargarPelicula(pelicula);

            Stage stage = (Stage) btnVerDetalles.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("Error de navegación", "No se pudo abrir los detalles: " + e.getMessage());
        }
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
     * Maneja el evento de volver al portal principal.
     *
     * @param event Evento de acción del botón
     */
    @FXML
    private void onVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarError("Error de navegación", "No se pudo volver al portal: " + e.getMessage());
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
