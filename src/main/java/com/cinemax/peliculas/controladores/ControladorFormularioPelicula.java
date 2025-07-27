package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.peliculas.modelos.entidades.Genero;
import com.cinemax.peliculas.modelos.entidades.Idioma;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.servicios.ServicioPelicula;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class ControladorFormularioPelicula implements Initializable {

    @FXML private Label lblTituloPantalla;
    @FXML private Label lblModoEdicion;
    @FXML private Label lblEstadoFormulario;
    
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAnio;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtImagenUrl;
    @FXML private TextArea txtSinopsis;
    
    @FXML private ComboBox<Idioma> cmbIdioma;
    @FXML private ComboBox<String> cmbGenero;
    @FXML private ListView<String> listGeneros;
    
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnVolver;
    @FXML private Button btnLogOut;

    private ServicioPelicula servicioPelicula;
    private Pelicula peliculaEditando; // null para nueva película, objeto para edición
    private boolean modoEdicion = false;

    public ControladorFormularioPelicula() {
        this.servicioPelicula = new ServicioPelicula();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarFormulario();
        configurarValidaciones();
        actualizarEstadoFormulario();
    }

    private void configurarFormulario() {
        // Configurar TextArea sinopsis
        txtSinopsis.setWrapText(true);
        txtSinopsis.setEditable(true);
        txtSinopsis.setDisable(false);
        
        // Configurar ComboBox de idiomas
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

        // Configurar ComboBox de géneros
        cmbGenero.setItems(FXCollections.observableArrayList(Genero.obtenerTodosLosGeneros()));

        // Configurar ListView de géneros adicionales
        listGeneros.setItems(FXCollections.observableArrayList(Genero.obtenerTodosLosGeneros()));
        listGeneros.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Configurar validación de entrada numérica
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

    private void configurarValidaciones() {
        // Listener para validar formulario en tiempo real
        Runnable validarFormulario = this::actualizarEstadoFormulario;

        txtTitulo.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        txtSinopsis.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        txtDuracion.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        txtAnio.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        cmbIdioma.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
        cmbGenero.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
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
        return !txtTitulo.getText().trim().isEmpty() &&
               !txtSinopsis.getText().trim().isEmpty() &&
               !txtDuracion.getText().trim().isEmpty() &&
               !txtAnio.getText().trim().isEmpty() &&
               cmbIdioma.getValue() != null &&
               cmbGenero.getValue() != null;
    }

    public void configurarParaEdicion(Pelicula pelicula) {
        this.peliculaEditando = pelicula;
        this.modoEdicion = true;

        lblTituloPantalla.setText("Editar Película");
        lblModoEdicion.setText("Modo: Edición - ID: " + pelicula.getId());
        btnGuardar.setText("Guardar Cambios");

        // Cargar datos de la película
        txtTitulo.setText(pelicula.getTitulo());
        txtSinopsis.setText(pelicula.getSinopsis() != null ? pelicula.getSinopsis() : "");
        txtDuracion.setText(String.valueOf(pelicula.getDuracionMinutos()));
        txtAnio.setText(String.valueOf(pelicula.getAnio()));
        txtImagenUrl.setText(pelicula.getImagenUrl() != null ? pelicula.getImagenUrl() : "");

        cmbIdioma.setValue(pelicula.getIdioma());

        // Configurar géneros
        String generosActuales = pelicula.getGenerosComoString();
        if (generosActuales != null && !generosActuales.isEmpty()) {
            String primerGenero = generosActuales.split(",")[0].trim();
            cmbGenero.setValue(primerGenero);

            // Preseleccionar géneros en la lista
            String[] generos = generosActuales.split(",");
            for (String genero : generos) {
                String generoLimpio = genero.trim();
                if (listGeneros.getItems().contains(generoLimpio)) {
                    listGeneros.getSelectionModel().select(generoLimpio);
                }
            }
        }

        actualizarEstadoFormulario();
    }

    @FXML
    private void onGuardar(ActionEvent event) {
        try {
            // Validar datos numéricos
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            int anio = Integer.parseInt(txtAnio.getText().trim());

            if (duracion <= 0) {
                mostrarError("Error de validación", "La duración debe ser un número positivo");
                return;
            }

            if (anio < 1900 || anio > 2030) {
                mostrarError("Error de validación", "El año debe estar entre 1900 y 2030");
                return;
            }

            // Construir string de géneros
            StringBuilder generosBuilder = new StringBuilder();
            generosBuilder.append(cmbGenero.getValue());

            List<String> generosAdicionales = listGeneros.getSelectionModel().getSelectedItems();
            for (String genero : generosAdicionales) {
                if (!genero.equals(cmbGenero.getValue())) {
                    generosBuilder.append(", ").append(genero);
                }
            }

            String imagenUrl = txtImagenUrl.getText().trim();
            if (imagenUrl.isEmpty()) {
                imagenUrl = null;
            }

            if (modoEdicion) {
                // Actualizar película existente
                servicioPelicula.actualizarPelicula(
                    peliculaEditando.getId(),
                    txtTitulo.getText().trim(),
                    txtSinopsis.getText().trim(),
                    duracion,
                    anio,
                    cmbIdioma.getValue(),
                    generosBuilder.toString(),
                    imagenUrl
                );

                mostrarInformacion("Éxito", "Película actualizada exitosamente: " + txtTitulo.getText().trim());
            } else {
                // Crear nueva película
                Pelicula nuevaPelicula = servicioPelicula.crearPelicula(
                    txtTitulo.getText().trim(),
                    txtSinopsis.getText().trim(),
                    duracion,
                    anio,
                    cmbIdioma.getValue(),
                    generosBuilder.toString(),
                    imagenUrl
                );

                mostrarInformacion("Éxito", "Película creada exitosamente: " + nuevaPelicula.getTitulo());
            }

            // Volver a la pantalla principal
            volverAPantallaPrincipal();

        } catch (NumberFormatException e) {
            mostrarError("Error de formato", "La duración y el año deben ser números válidos");
        } catch (Exception e) {
            String operacion = modoEdicion ? "actualizar" : "crear";
            mostrarError("Error al " + operacion + " película", "Error: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        volverAPantallaPrincipal();
    }

    @FXML
    private void onLimpiar(ActionEvent event) {
        if (!modoEdicion) {
            limpiarFormulario();
        }
    }

    @FXML
    private void onVolver(ActionEvent event) {
        volverAPantallaPrincipal();
    }

    @FXML
    private void onLogOut(ActionEvent event) {
        ManejadorMetodosComunes.mostrarVentanaAdvertencia("Sesión cerrada");
        javafx.application.Platform.exit();
    }

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

    private void mostrarError(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaError(mensaje != null ? mensaje : "Error desconocido");
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaExito(mensaje != null ? mensaje : "Operación completada");
    }
}
