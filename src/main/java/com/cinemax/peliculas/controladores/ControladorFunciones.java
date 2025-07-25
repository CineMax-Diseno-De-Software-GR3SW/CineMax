package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import com.cinemax.comun.ManejadorMetodosComunes;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class ControladorFunciones implements Initializable {

    private ServicioFuncion servicioFuncion;
    private FuncionDAO funcionDAO;
    private PeliculaDAO peliculaDAO;
    private SalaService salaService;

    // Componentes de la interfaz FXML
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<Sala> cmbFiltroSala;
    @FXML private TableView<Funcion> tablaFunciones;
    @FXML private TableColumn<Funcion, Integer> colId;
    @FXML private TableColumn<Funcion, String> colPelicula;
    @FXML private TableColumn<Funcion, String> colSala;
    @FXML private TableColumn<Funcion, String> colFechaHoraInicio;
    @FXML private TableColumn<Funcion, String> colFechaHoraFin;
    @FXML private TableColumn<Funcion, String> colFormato;
    @FXML private TableColumn<Funcion, String> colTipoEstreno;

    @FXML private Button btnNuevaFuncion;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVerDetalles;

    @FXML private Label lblTotalFunciones;
    @FXML private Label lblEstadisticas;

    // Datos para la tabla
    private ObservableList<Funcion> listaFunciones;
    private ObservableList<Funcion> funcionesFiltradas;

    // Indicador de carga
    private ProgressIndicator indicadorCarga;
    private StackPane contenedorPrincipal;

    public ControladorFunciones() {
        this.servicioFuncion = new ServicioFuncion();
        this.funcionDAO = new FuncionDAO();
        this.peliculaDAO = new PeliculaDAO();
        this.salaService = new SalaService();
    }

    @FXML
    private void onNuevaFuncion(ActionEvent event) {
        mostrarFormularioNuevaFuncion();
    }

    private void mostrarFormularioNuevaFuncion() {
        mostrarFormularioFuncion(null);
    }

    private void mostrarFormularioFuncion(Funcion funcionExistente) {
        Dialog<ButtonType> dialog = new Dialog<>();
        boolean esEdicion = funcionExistente != null;
        dialog.setTitle(esEdicion ? "Editar Función" : "Agregar Nueva Función");
        dialog.setHeaderText(esEdicion ? "Modifique los datos de la función" : "Complete los datos de la nueva función");

        // Crear los campos del formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Campos del formulario
        ComboBox<Pelicula> cmbPelicula = new ComboBox<>();
        try {
            List<Pelicula> peliculas = peliculaDAO.listarTodas();
            cmbPelicula.setItems(FXCollections.observableArrayList(peliculas));
        } catch (SQLException e) {
            mostrarError("Error", "No se pudieron cargar las películas: " + e.getMessage());
            return;
        }
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
        cmbPelicula.setPrefWidth(300);

        ComboBox<Sala> cmbSala = new ComboBox<>();
        try {
            List<Sala> salas = salaService.listarSalas();
            cmbSala.setItems(FXCollections.observableArrayList(salas));
        } catch (Exception e) {
            mostrarError("Error", "No se pudieron cargar las salas: " + e.getMessage());
            return;
        }
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
        cmbSala.setPrefWidth(300);

        DatePicker dateFecha = new DatePicker();
        dateFecha.setPrefWidth(200);

        TextField txtHora = new TextField();
        txtHora.setPromptText("HH:MM (ej: 14:30)");
        txtHora.setPrefWidth(150);

        ComboBox<FormatoFuncion> cmbFormato = new ComboBox<>();
        cmbFormato.setItems(FXCollections.observableArrayList(FormatoFuncion.values()));
        cmbFormato.setPrefWidth(200);

        ComboBox<TipoEstreno> cmbTipoEstreno = new ComboBox<>();
        cmbTipoEstreno.setItems(FXCollections.observableArrayList(TipoEstreno.values()));
        cmbTipoEstreno.setPrefWidth(200);

        // Si es edición, cargar datos existentes
        if (esEdicion) {
            cmbPelicula.setValue(funcionExistente.getPelicula());
            cmbSala.setValue(funcionExistente.getSala());
            dateFecha.setValue(funcionExistente.getFechaHoraInicio().toLocalDate());
            txtHora.setText(funcionExistente.getFechaHoraInicio().toLocalTime().toString());
            cmbFormato.setValue(funcionExistente.getFormato());
            cmbTipoEstreno.setValue(funcionExistente.getTipoEstreno());
        }

        // Agregar campos al grid
        grid.add(new Label("Película *:"), 0, 0);
        grid.add(cmbPelicula, 1, 0);

        grid.add(new Label("Sala *:"), 0, 1);
        grid.add(cmbSala, 1, 1);

        grid.add(new Label("Fecha *:"), 0, 2);
        grid.add(dateFecha, 1, 2);

        grid.add(new Label("Hora *:"), 0, 3);
        grid.add(txtHora, 1, 3);

        grid.add(new Label("Formato *:"), 0, 4);
        grid.add(cmbFormato, 1, 4);

        grid.add(new Label("Tipo de Estreno *:"), 0, 5);
        grid.add(cmbTipoEstreno, 1, 5);

        // Agregar nota
        Label lblNota = new Label("* Campos obligatorios");
        // Usar clase CSS para el estilo de nota
        lblNota.getStyleClass().add("summary-details");
        grid.add(lblNota, 0, 6, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Botones
        ButtonType btnGuardar = new ButtonType(esEdicion ? "Actualizar" : "Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, btnCancelar);

        // Validación en tiempo real
        Button botonGuardar = (Button) dialog.getDialogPane().lookupButton(btnGuardar);
        botonGuardar.setDisable(true);

        // Listener para habilitar/deshabilitar botón guardar
        Runnable validarFormulario = () -> {
            boolean valido = cmbPelicula.getValue() != null &&
                           cmbSala.getValue() != null &&
                           dateFecha.getValue() != null &&
                           !txtHora.getText().trim().isEmpty() &&
                           cmbFormato.getValue() != null &&
                           cmbTipoEstreno.getValue() != null;
            botonGuardar.setDisable(!valido);
        };

        cmbPelicula.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
        cmbSala.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
        dateFecha.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
        txtHora.textProperty().addListener((obs, oldText, newText) -> validarFormulario.run());
        cmbFormato.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());
        cmbTipoEstreno.valueProperty().addListener((obs, oldValue, newValue) -> validarFormulario.run());

        // Validación inicial si es edición
        if (esEdicion) {
            validarFormulario.run();
        }

        // Mostrar el diálogo
        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == btnGuardar) {
            try {
                // Validar formato de hora
                LocalTime hora = LocalTime.parse(txtHora.getText().trim());
                LocalDateTime fechaHoraInicio = LocalDateTime.of(dateFecha.getValue(), hora);

                if (esEdicion) {
                    // Actualizar función existente
                    servicioFuncion.actualizarFuncion(
                        funcionExistente.getId(),
                        cmbPelicula.getValue(),
                        cmbSala.getValue(),
                        fechaHoraInicio,
                        cmbFormato.getValue(),
                        cmbTipoEstreno.getValue()
                    );

                    mostrarInformacion("Éxito", "Función actualizada correctamente");
                } else {
                    // Crear nueva función
                    Funcion nuevaFuncion = servicioFuncion.crearFuncion(
                        cmbPelicula.getValue(),
                        cmbSala.getValue(),
                        fechaHoraInicio,
                        cmbFormato.getValue(),
                        cmbTipoEstreno.getValue()
                    );

                    mostrarInformacion("Éxito", "Función creada exitosamente con ID: " + nuevaFuncion.getId());
                }

                // Recargar la tabla
                cargarFuncionesAsync();

            } catch (Exception e) {
                String mensaje = e.getMessage();
                if (mensaje != null && mensaje.contains("hora")) {
                    mostrarError("Error de formato", "Formato de hora inválido. Use HH:MM (ej: 14:30)");
                } else {
                    mostrarError("Error al " + (esEdicion ? "actualizar" : "crear") + " función",
                               "Error: " + (mensaje != null ? mensaje : "Error desconocido"));
                }
            }
        }
    }

    @FXML
    private void onEditarFuncion(ActionEvent event) {
        Funcion funcionSeleccionada = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcionSeleccionada != null) {
            mostrarFormularioFuncion(funcionSeleccionada);
        }
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
                cargarFuncionesAsync();
                ManejadorMetodosComunes.mostrarVentanaExito("Función eliminada correctamente");
            } catch (Exception e) {
                String mensajeError = e.getMessage();
                if (mensajeError != null && (mensajeError.contains("foreign key constraint") || mensajeError.contains("violates"))) {
                    mostrarErrorRestriccion(funcionSeleccionada);
                } else {
                    ManejadorMetodosComunes.mostrarVentanaError("No se pudo eliminar la función: " + (mensajeError != null ? mensajeError : "Error desconocido"));
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
            mostrarDetallesFuncion(funcionSeleccionada);
        }
    }

    @FXML
    private void onBuscar(ActionEvent event) {
        aplicarFiltros();
    }

    @FXML
    private void onLimpiar(ActionEvent event) {
        txtBuscar.clear();
        cmbFiltroSala.setValue(null);
        aplicarFiltros();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listaFunciones = FXCollections.observableArrayList();
        funcionesFiltradas = FXCollections.observableArrayList();

        configurarTabla();
        configurarEventos();
        cargarFuncionesAsync();
        configurarFiltrosAsync();
    }

    private void configurarIndicadorCarga() {
        // Crear indicador de carga si no existe
        if (indicadorCarga == null) {
            indicadorCarga = new ProgressIndicator();
            indicadorCarga.setVisible(false);
            indicadorCarga.setPrefSize(50, 50);
            // Usar clase CSS en lugar de estilo inline
            indicadorCarga.getStyleClass().add("progress-indicator");
        }

        // Enfoque simplificado: solo crear el indicador, no intentar modificar la estructura del layout
        System.out.println("Indicador de carga creado y configurado");
    }

    private void mostrarIndicadorCarga(boolean mostrar) {
        Platform.runLater(() -> {
            try {
                // Deshabilitar la tabla durante la carga para dar feedback visual
                if (tablaFunciones != null) {
                    tablaFunciones.setDisable(mostrar);
                    if (mostrar) {
                        tablaFunciones.setOpacity(0.5);
                    } else {
                        tablaFunciones.setOpacity(1.0);
                    }
                }

                // Deshabilitar botones durante la carga
                if (btnNuevaFuncion != null) btnNuevaFuncion.setDisable(mostrar);
                if (btnBuscar != null) btnBuscar.setDisable(mostrar);
                if (btnLimpiar != null) btnLimpiar.setDisable(mostrar);

                // Actualizar label de estado para indicar carga
                if (lblEstadisticas != null && !lblEstadisticas.textProperty().isBound()) {
                    if (mostrar) {
                        lblEstadisticas.setText("⏳ Cargando datos...");
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al mostrar indicador de carga: " + e.getMessage());
            }
        });
    }

    private void cargarFuncionesAsync() {
        Task<List<Funcion>> task = new Task<List<Funcion>>() {
            @Override
            protected List<Funcion> call() throws Exception {
                updateMessage("Cargando funciones...");

                try {
                    List<Funcion> funciones = funcionDAO.listarTodasLasFunciones();
                    updateMessage("Procesando datos de funciones...");
                    updateProgress(1, 1);
                    return funciones != null ? funciones : List.of();
                } catch (Exception e) {
                    updateMessage("Error al cargar funciones: " + e.getMessage());
                    throw e;
                }
            }
        };

        task.setOnRunning(e -> {
            mostrarIndicadorCarga(true);
            // Solo configurar una vez al inicio
            if (contenedorPrincipal == null) {
                configurarIndicadorCarga();
            }
        });

        task.setOnSucceeded(e -> {
            List<Funcion> funciones = task.getValue();
            Platform.runLater(() -> {
                try {
                    listaFunciones.clear();
                    listaFunciones.addAll(funciones);
                    aplicarFiltros();
                    mostrarIndicadorCarga(false);

                    // Actualizar estado
                    if (lblEstadisticas != null) {
                        lblEstadisticas.textProperty().unbind();
                        lblEstadisticas.setText("Funciones cargadas: " + funciones.size());
                    }
                } catch (Exception ex) {
                    System.err.println("Error al procesar funciones: " + ex.getMessage());
                    if (lblEstadisticas != null) {
                        lblEstadisticas.textProperty().unbind();
                        lblEstadisticas.setText("Error al procesar funciones");
                    }
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                try {
                    mostrarIndicadorCarga(false);
                    Throwable exception = task.getException();
                    String mensaje = exception != null ? exception.getMessage() : "Error desconocido";

                    if (lblEstadisticas != null) {
                        lblEstadisticas.textProperty().unbind();
                        lblEstadisticas.setText("Error al cargar funciones");
                    }

                    System.err.println("Error al cargar funciones: " + mensaje);
                    mostrarError("Error al cargar funciones", mensaje);
                } catch (Exception ex) {
                    System.err.println("Error en manejo de fallo: " + ex.getMessage());
                    if (lblEstadisticas != null) {
                        lblEstadisticas.setText("Error de carga");
                    }
                }
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void configurarFiltrosAsync() {
        // Solo configurar filtros si el ComboBox existe
        if (cmbFiltroSala == null) {
            return;
        }

        Task<List<Sala>> task = new Task<List<Sala>>() {
            @Override
            protected List<Sala> call() throws Exception {
                updateMessage("Cargando salas para filtros...");
                return salaService.listarSalas();
            }
        };

        task.setOnSucceeded(e -> {
            List<Sala> salas = task.getValue();
            Platform.runLater(() -> {
                try {
                    cmbFiltroSala.setItems(FXCollections.observableArrayList(salas));
                    cmbFiltroSala.setConverter(new StringConverter<Sala>() {
                        @Override
                        public String toString(Sala sala) {
                            return sala != null ? sala.getNombre() + " (" + sala.getTipo() + ")" : "";
                        }

                        @Override
                        public Sala fromString(String string) {
                            return null;
                        }
                    });

                    // Configurar evento de cambio en el filtro
                    cmbFiltroSala.setOnAction(ev -> aplicarFiltros());
                } catch (Exception ex) {
                    System.err.println("Error al configurar filtros: " + ex.getMessage());
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                System.err.println("Error al cargar salas para filtros: " + task.getException().getMessage());
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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
                btnEditar.setDisable(!funcionSeleccionada);
                btnEliminar.setDisable(!funcionSeleccionada);
                btnVerDetalles.setDisable(!funcionSeleccionada);
            }
        );

        tablaFunciones.setItems(funcionesFiltradas);
    }

    private void configurarFiltros() {
        // Configurar combo de salas
        try {
            List<Sala> salas = salaService.listarSalas();
            cmbFiltroSala.setItems(FXCollections.observableArrayList(salas));
            cmbFiltroSala.setConverter(new StringConverter<Sala>() {
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
            mostrarError("Error", "No se pudieron cargar las salas para el filtro: " + e.getMessage());
        }

        // Configurar evento de cambio en el filtro
        cmbFiltroSala.setOnAction(e -> aplicarFiltros());
    }

    private void configurarEventos() {
        // Configurar búsqueda en tiempo real
        txtBuscar.textProperty().addListener((obs, oldText, newText) -> aplicarFiltros());
    }


    private void aplicarFiltros() {
        funcionesFiltradas.clear();

        String textoBusqueda = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase().trim() : "";
        Sala salaSeleccionada = cmbFiltroSala.getValue();

        for (Funcion funcion : listaFunciones) {
            boolean coincideTexto = textoBusqueda.isEmpty() ||
                String.valueOf(funcion.getId()).contains(textoBusqueda) ||
                (funcion.getPelicula() != null && funcion.getPelicula().getTitulo() != null &&
                 funcion.getPelicula().getTitulo().toLowerCase().contains(textoBusqueda)) ||
                (funcion.getSala() != null && funcion.getSala().getNombre() != null &&
                 funcion.getSala().getNombre().toLowerCase().contains(textoBusqueda));

            boolean coincideSala = salaSeleccionada == null ||
                (funcion.getSala() != null && funcion.getSala().getId() == salaSeleccionada.getId());

            if (coincideTexto && coincideSala) {
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

    private void mostrarDetallesFuncion(Funcion funcion) {
        StringBuilder contenido = new StringBuilder();
        contenido.append("DETALLES DE LA FUNCIÓN\n");
        contenido.append("━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        contenido.append("ID: ").append(funcion.getId()).append("\n");

        if (funcion.getPelicula() != null) {
            contenido.append("Película: ").append(funcion.getPelicula().getTitulo()).append("\n");
        }

        if (funcion.getSala() != null) {
            contenido.append("Sala: ").append(funcion.getSala().getNombre()).append("\n");
            contenido.append("Tipo de Sala: ").append(funcion.getSala().getTipo()).append("\n");
        }

        if (funcion.getFechaHoraInicio() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            contenido.append("Fecha y Hora de Inicio: ").append(funcion.getFechaHoraInicio().format(formatter)).append("\n");

            if (funcion.getFechaHoraFin() != null) {
                contenido.append("Fecha y Hora de Fin: ").append(funcion.getFechaHoraFin().format(formatter)).append("\n");
            }
        }

        if (funcion.getFormato() != null) {
            contenido.append("Formato: ").append(funcion.getFormato().toString()).append("\n");
        }

        if (funcion.getTipoEstreno() != null) {
            contenido.append("Tipo de Estreno: ").append(funcion.getTipoEstreno().name()).append("\n");
        }

        ManejadorMetodosComunes.mostrarVentanaExito(contenido.toString());
    }

    private void mostrarError(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaError(mensaje != null ? mensaje : "Error desconocido");
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        ManejadorMetodosComunes.mostrarVentanaExito(mensaje != null ? mensaje : "Operación completada");
    }
}
