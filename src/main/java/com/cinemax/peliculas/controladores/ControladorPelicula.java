package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import com.cinemax.peliculas.modelos.entidades.Idioma;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.servicios.ServicioPelicula;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;


public class ControladorPelicula implements Initializable {
    
    private ServicioPelicula servicioPelicula;
    
    // Constructor
    public ControladorPelicula() {
        this.servicioPelicula = new ServicioPelicula();
    }

    // Componentes de la interfaz FXML
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroGenero;
    @FXML private TableView<Pelicula> tablaPeliculas;
    @FXML private TableColumn<Pelicula, Integer> colId;
    @FXML private TableColumn<Pelicula, String> colTitulo;
    @FXML private TableColumn<Pelicula, Integer> colAnio;
    @FXML private TableColumn<Pelicula, String> colGenero;
    @FXML private TableColumn<Pelicula, Integer> colDuracion;
    @FXML private TableColumn<Pelicula, String> colIdioma;
    
    @FXML private Button btnNuevaPelicula;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVerDetalles;
    
    @FXML private Label lblTotalPeliculas;
    @FXML private Label lblEstadisticas;

    // Datos para la tabla
    private ObservableList<Pelicula> listaPeliculas;
    private ObservableList<Pelicula> peliculasFiltradas;

    @FXML
    private void onNuevaPelicula(ActionEvent event) {
        mostrarFormularioNuevaPelicula();
    }

    private void mostrarFormularioNuevaPelicula() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Agregar Nueva Película");
        dialog.setHeaderText("Complete los datos de la nueva película");

        // Crear los campos del formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Campos del formulario
        TextField txtTitulo = new TextField();
        txtTitulo.setPromptText("Título de la película");
        txtTitulo.setPrefWidth(300);

        TextArea txtSinopsis = new TextArea();
        txtSinopsis.setPromptText("Sinopsis de la película");
        txtSinopsis.setPrefRowCount(3);
        txtSinopsis.setPrefWidth(300);
        txtSinopsis.setWrapText(true);

        TextField txtDuracion = new TextField();
        txtDuracion.setPromptText("Duración en minutos");
        txtDuracion.setPrefWidth(150);

        TextField txtAnio = new TextField();
        txtAnio.setPromptText("Año de estreno");
        txtAnio.setPrefWidth(150);

        // ComboBox para idioma
        ComboBox<Idioma> cmbIdioma = new ComboBox<>();
        cmbIdioma.getItems().addAll(Idioma.values());
        cmbIdioma.setPromptText("Seleccione idioma");
        cmbIdioma.setPrefWidth(200);
        cmbIdioma.setConverter(new StringConverter<Idioma>() {
            @Override
            public String toString(Idioma idioma) {
                return idioma != null ? idioma.getNombre() : "";
            }

            @Override
            public Idioma fromString(String string) {
                return null; // No necesario para ComboBox
            }
        });

        // ComboBox para géneros múltiples
        ComboBox<String> cmbGenero = new ComboBox<>();
        cmbGenero.getItems().addAll(
            "Acción", "Aventura", "Comedia", "Drama", "Terror", 
            "Ciencia Ficción", "Romance", "Thriller", "Animación", 
            "Documental", "Musical", "Western", "Biografía", "Historia"
        );
        cmbGenero.setPromptText("Seleccione género principal");
        cmbGenero.setPrefWidth(200);

        // Lista para géneros adicionales
        ListView<String> listGeneros = new ListView<>();
        listGeneros.getItems().addAll(
            "Acción", "Aventura", "Comedia", "Drama", "Terror", 
            "Ciencia Ficción", "Romance", "Thriller", "Animación", 
            "Documental", "Musical", "Western", "Biografía", "Historia"
        );
        listGeneros.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listGeneros.setPrefHeight(100);
        listGeneros.setPrefWidth(200);

        TextField txtImagenUrl = new TextField();
        txtImagenUrl.setPromptText("URL de la imagen (opcional)");
        txtImagenUrl.setPrefWidth(300);

        // Agregar campos al grid
        grid.add(new Label("Título *:"), 0, 0);
        grid.add(txtTitulo, 1, 0);

        grid.add(new Label("Sinopsis *:"), 0, 1);
        grid.add(txtSinopsis, 1, 1);

        grid.add(new Label("Duración (min) *:"), 0, 2);
        grid.add(txtDuracion, 1, 2);

        grid.add(new Label("Año *:"), 0, 3);
        grid.add(txtAnio, 1, 3);

        grid.add(new Label("Idioma *:"), 0, 4);
        grid.add(cmbIdioma, 1, 4);

        grid.add(new Label("Género principal *:"), 0, 5);
        grid.add(cmbGenero, 1, 5);

        grid.add(new Label("Géneros adicionales:"), 0, 6);
        grid.add(listGeneros, 1, 6);

        grid.add(new Label("URL imagen:"), 0, 7);
        grid.add(txtImagenUrl, 1, 7);

        // Agregar nota
        Label lblNota = new Label("* Campos obligatorios");
        lblNota.setStyle("-fx-font-style: italic; -fx-text-fill: #666;");
        grid.add(lblNota, 0, 8, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Botones
        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        // Validación en tiempo real
        Button botonGuardar = (Button) dialog.getDialogPane().lookupButton(btnGuardar);
        botonGuardar.setDisable(true);

        // Listener para habilitar/deshabilitar botón guardar
        Runnable validarFormulario = () -> {
            boolean valido = !txtTitulo.getText().trim().isEmpty() &&
                           !txtSinopsis.getText().trim().isEmpty() &&
                           !txtDuracion.getText().trim().isEmpty() &&
                           !txtAnio.getText().trim().isEmpty() &&
                           cmbIdioma.getValue() != null &&
                           cmbGenero.getValue() != null;
            botonGuardar.setDisable(!valido);
        };

        txtTitulo.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        txtSinopsis.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        txtDuracion.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        txtAnio.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        cmbIdioma.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
        cmbGenero.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());

        // Mostrar el diálogo
        Optional<ButtonType> resultado = dialog.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == btnGuardar) {
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

                // Verificar duplicados
                boolean existe = servicioPelicula.existePeliculaDuplicada(
                    txtTitulo.getText().trim(), anio);
                
                if (existe) {
                    Alert confirmacion = new Alert(Alert.AlertType.WARNING);
                    confirmacion.setTitle("Película duplicada");
                    confirmacion.setHeaderText("Ya existe una película con ese título y año");
                    confirmacion.setContentText("¿Desea continuar de todas formas?");
                    
                    ButtonType btnContinuar = new ButtonType("Continuar", ButtonBar.ButtonData.YES);
                    ButtonType btnCancelarDup = new ButtonType("Cancelar", ButtonBar.ButtonData.NO);
                    confirmacion.getButtonTypes().setAll(btnContinuar, btnCancelarDup);
                    
                    Optional<ButtonType> respuesta = confirmacion.showAndWait();
                    if (respuesta.isEmpty() || respuesta.get() != btnContinuar) {
                        return;
                    }
                }

                // Crear la película
                String imagenUrl = txtImagenUrl.getText().trim();
                if (imagenUrl.isEmpty()) {
                    imagenUrl = null;
                }

                Pelicula nuevaPelicula = servicioPelicula.crearPelicula(
                    txtTitulo.getText().trim(),
                    txtSinopsis.getText().trim(),
                    duracion,
                    anio,
                    cmbIdioma.getValue(),
                    generosBuilder.toString(),
                    imagenUrl
                );

                // Recargar la tabla
                cargarPeliculas();
                
                // Seleccionar la nueva película
                for (Pelicula pelicula : peliculasFiltradas) {
                    if (pelicula.getId() == nuevaPelicula.getId()) {
                        tablaPeliculas.getSelectionModel().select(pelicula);
                        break;
                    }
                }

                mostrarInformacion("Éxito", "Película creada exitosamente:\n" + nuevaPelicula.getTitulo());

            } catch (NumberFormatException e) {
                mostrarError("Error de formato", "La duración y el año deben ser números válidos");
            } catch (Exception e) {
                mostrarError("Error al crear película", "Error: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onEditarPelicula(ActionEvent event) {
        Pelicula peliculaSeleccionada = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (peliculaSeleccionada != null) {
            // TODO: Implementar formulario de edición
            mostrarInformacion("Funcionalidad en desarrollo", "El formulario de edición estará disponible pronto");
        }
    }

    @FXML
    private void onEliminarPelicula(ActionEvent event) {
        Pelicula peliculaSeleccionada = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (peliculaSeleccionada != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Está seguro de eliminar esta película?");
            confirmacion.setContentText("Título: " + peliculaSeleccionada.getTitulo() + 
                                      "\n\nATENCIÓN: Esta acción no se puede deshacer.");
            
            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    servicioPelicula.eliminarPelicula(peliculaSeleccionada.getId());
                    cargarPeliculas();
                    mostrarInformacion("Éxito", "Película eliminada correctamente");
                } catch (Exception e) {
                    String mensaje = e.getMessage();
                    if (mensaje.contains("foreign key constraint") || mensaje.contains("violates")) {
                        mostrarErrorRestriccion(peliculaSeleccionada);
                    } else {
                        mostrarError("Error", "No se pudo eliminar la película: " + mensaje);
                    }
                }
            }
        }
    }

    private void mostrarErrorRestriccion(Pelicula pelicula) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("No se puede eliminar la película");
        alerta.setHeaderText("La película está siendo utilizada en el sistema");
        alerta.setContentText("No se puede eliminar la película '" + pelicula.getTitulo() + 
                             "' porque está asociada con:\n\n" +
                             "• Funciones programadas\n" +
                             "• Boletos vendidos\n" +
                             "• Reservas existentes\n\n" +
                             "OPCIONES:\n" +
                             "1. Desactivar la película (recomendado)\n" +
                             "2. Eliminar primero las funciones y boletos asociados\n" +
                             "3. Contactar al administrador del sistema");
        
        ButtonType btnDesactivar = new ButtonType("Desactivar Película", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alerta.getButtonTypes().setAll(btnDesactivar, btnCancelar);
        
        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnDesactivar) {
            // Aquí podrías implementar la lógica para desactivar la película
            // Por ahora solo mostramos un mensaje
            mostrarInformacion("Información", 
                "Funcionalidad de desactivación no implementada aún.\n" +
                "Contacte al administrador del sistema para desactivar la película.");
        }
    }

    @FXML
    private void onVerDetalles(ActionEvent event) {
        Pelicula peliculaSeleccionada = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (peliculaSeleccionada != null) {
            mostrarDetallesPelicula(peliculaSeleccionada);
        }
    }

    @FXML
    private void onBuscar(ActionEvent event) {
        aplicarFiltros();
    }

    @FXML
    private void onLimpiar(ActionEvent event) {
        txtBuscar.clear();
        cmbFiltroGenero.setValue("Todos");
        aplicarFiltros();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listaPeliculas = FXCollections.observableArrayList();
        peliculasFiltradas = FXCollections.observableArrayList();
        
        configurarTabla();
        configurarFiltros();
        configurarEventos();
        cargarPeliculas();
    }
    
    private void configurarTabla() {
        // Configurar las columnas de la tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        
        // Para el género, necesitamos un cellValueFactory personalizado
        colGenero.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getGenerosComoString()
            );
        });
        
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracionMinutos"));
        
        // Para el idioma, necesitamos un cellValueFactory personalizado
        colIdioma.setCellValueFactory(cellData -> {
            Idioma idioma = cellData.getValue().getIdioma();
            return new javafx.beans.property.SimpleStringProperty(
                idioma != null ? idioma.getNombre() : "N/A"
            );
        });

        // Configurar selección de tabla
        tablaPeliculas.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean peliculaSeleccionada = newSelection != null;
                btnEditar.setDisable(!peliculaSeleccionada);
                btnEliminar.setDisable(!peliculaSeleccionada);
                btnVerDetalles.setDisable(!peliculaSeleccionada);
            }
        );

        tablaPeliculas.setItems(peliculasFiltradas);
    }

    private void configurarFiltros() {
        // Llenar el combo de géneros con géneros estáticos iniciales
        cmbFiltroGenero.getItems().addAll(
            "Todos", "Acción", "Aventura", "Comedia", "Drama", 
            "Terror", "Ciencia Ficción", "Romance", "Thriller", "Animación",
            "Documental", "Musical", "Western", "Biografía", "Historia"
        );
        cmbFiltroGenero.setValue("Todos");
        
        // Configurar evento de cambio en el filtro
        cmbFiltroGenero.setOnAction(e -> aplicarFiltros());
    }

    /**
     * Actualiza dinámicamente los géneros del filtro basándose en las películas cargadas
     */
    private void actualizarFiltroGeneros() {
        String valorActual = cmbFiltroGenero.getValue();
        cmbFiltroGenero.getItems().clear();
        
        // Agregar "Todos" siempre
        cmbFiltroGenero.getItems().add("Todos");
        
        // Obtener géneros únicos de las películas
        Set<String> generosUnicos = new HashSet<>();
        for (Pelicula pelicula : listaPeliculas) {
            if (!pelicula.getGeneros().isEmpty()) {
                // Agregar todos los géneros de la película
                for (com.cinemax.peliculas.modelos.entidades.Genero genero : pelicula.getGeneros()) {
                    generosUnicos.add(genero.getNombre());
                }
            }
        }
        
        // Agregar géneros únicos ordenados alfabéticamente
        List<String> generosOrdenados = new ArrayList<>(generosUnicos);
        Collections.sort(generosOrdenados);
        cmbFiltroGenero.getItems().addAll(generosOrdenados);
        
        // Restaurar el valor seleccionado si aún existe
        if (valorActual != null && cmbFiltroGenero.getItems().contains(valorActual)) {
            cmbFiltroGenero.setValue(valorActual);
        } else {
            cmbFiltroGenero.setValue("Todos");
        }
    }

    private void configurarEventos() {
        // Configurar búsqueda en tiempo real
        txtBuscar.textProperty().addListener((obs, oldText, newText) -> aplicarFiltros());
    }

    private void cargarPeliculas() {
        try {
            listaPeliculas.clear();
            listaPeliculas.addAll(servicioPelicula.listarTodasLasPeliculas());
            actualizarFiltroGeneros(); // Actualizar géneros disponibles
            aplicarFiltros();
        } catch (Exception e) {
            mostrarError("Error al cargar películas", e.getMessage());
        }
    }

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
     * Verifica si una película coincide con el género seleccionado
     * Maneja géneros múltiples separados por comas
     */
    private boolean coincideConGenero(String generosPelicula, String generoFiltro) {
        // Si no hay filtro seleccionado o es "Todos", mostrar todas
        if (generoFiltro == null || "Todos".equals(generoFiltro)) {
            return true;
        }
        
        // Si la película no tiene géneros, no coincide
        if (generosPelicula == null || generosPelicula.trim().isEmpty()) {
            return false;
        }
        
        // Convertir a minúsculas para comparación insensible a mayúsculas
        String generosPeliculaLower = generosPelicula.toLowerCase();
        String generoFiltroLower = generoFiltro.toLowerCase();
        
        // Separar los géneros de la película por comas y limpiar espacios
        String[] generos = generosPeliculaLower.split(",");
        
        // Verificar si alguno de los géneros de la película coincide con el filtro
        for (String genero : generos) {
            String generoLimpio = genero.trim();
            if (generoLimpio.equals(generoFiltroLower)) {
                return true;
            }
        }
        
        return false;
    }

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

    private void mostrarDetallesPelicula(Pelicula pelicula) {
        Alert detalles = new Alert(Alert.AlertType.INFORMATION);
        detalles.setTitle("Detalles de la Película");
        detalles.setHeaderText(pelicula.getTitulo());
        
        StringBuilder contenido = new StringBuilder();
        contenido.append("ID: ").append(pelicula.getId()).append("\n");
        contenido.append("Año: ").append(pelicula.getAnio()).append("\n");
        contenido.append("Género: ").append(pelicula.getGenerosComoString()).append("\n");
        contenido.append("Duración: ").append(pelicula.getDuracionMinutos()).append(" minutos\n");
        if (pelicula.getIdioma() != null) {
            contenido.append("Idioma: ").append(pelicula.getIdioma().getNombre()).append("\n");
        }
        if (pelicula.getSinopsis() != null && !pelicula.getSinopsis().isEmpty()) {
            contenido.append("\nSinopsis:\n").append(pelicula.getSinopsis());
        }
        
        detalles.setContentText(contenido.toString());
        detalles.showAndWait();
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
