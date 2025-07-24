package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.cinemax.peliculas.modelos.entidades.Cartelera;
import com.cinemax.peliculas.modelos.entidades.FormatoFuncion;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Idioma;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.persistencia.FuncionDAO;
import com.cinemax.peliculas.modelos.persistencia.PeliculaDAO;
import com.cinemax.peliculas.servicios.ServicioFuncion;
import com.cinemax.peliculas.servicios.ServicioPelicula;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.servicios.SalaService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class ControladorSeleccionFuncionFX implements Initializable {

    private ServicioPelicula servicioPelicula;
    private ServicioFuncion servicioFuncion;
    private Cartelera cartelera;
    private FuncionDAO funcionDAO;
    private PeliculaDAO peliculaDAO;
    private SalaService salaService;

    // Variables de estado
    private Pelicula peliculaSeleccionada;
    private LocalDate fechaSeleccionada;
    private List<Funcion> funcionesDisponibles;
    private ToggleGroup grupoFechas;

    // Componentes de la interfaz FXML
    @FXML private TextField txtBuscarPelicula;
    @FXML private GridPane grillaPeliculas;
    @FXML private ComboBox<Idioma> cmbFiltroIdioma;
    @FXML private ComboBox<FormatoFuncion> cmbFiltroFormato;
    @FXML private ComboBox<Sala> cmbFiltroSala;
    @FXML private ToggleButton btnDiaHoy;
    @FXML private ToggleButton btnDiaManana;
    @FXML private ToggleButton btnDiaPasado;
    @FXML private VBox contenedorFunciones;

    @FXML private Button btnActualizarCartelera;
    @FXML private Button btnLimpiarBusqueda;

    @FXML private Label lblPeliculaSeleccionada;
    @FXML private Label lblFechaSeleccionada;
    @FXML private Label lblTotalPeliculas;
    @FXML private Label lblTotalFunciones;
    @FXML private Label lblEstadoSeleccion;

    public ControladorSeleccionFuncionFX() {
        this.servicioPelicula = new ServicioPelicula();
        this.servicioFuncion = new ServicioFuncion();
        this.cartelera = new Cartelera(new ArrayList<>());
        this.funcionDAO = new FuncionDAO();
        this.peliculaDAO = new PeliculaDAO();
        this.salaService = new SalaService();
        this.funcionesDisponibles = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarFiltros();
        configurarGrupoFechas();
        configurarEventos();
        actualizarCartelera();

        // Seleccionar hoy por defecto
        fechaSeleccionada = LocalDate.now();
        btnDiaHoy.setSelected(true);
        actualizarLabelFecha();
    }

    private void configurarFiltros() {
        // Configurar filtro de idioma
        cmbFiltroIdioma.getItems().add(null); // Opción "Todos"
        for (Idioma idioma : Idioma.values()) {
            cmbFiltroIdioma.getItems().add(idioma);
        }
        cmbFiltroIdioma.setConverter(new StringConverter<Idioma>() {
            @Override
            public String toString(Idioma idioma) {
                return idioma != null ? idioma.getNombre() : "Todos";
            }

            @Override
            public Idioma fromString(String string) {
                return null;
            }
        });

        // Configurar filtro de formato
        cmbFiltroFormato.getItems().add(null); // Opción "Todos"
        for (FormatoFuncion formato : FormatoFuncion.values()) {
            cmbFiltroFormato.getItems().add(formato);
        }
        cmbFiltroFormato.setConverter(new StringConverter<FormatoFuncion>() {
            @Override
            public String toString(FormatoFuncion formato) {
                return formato != null ? formato.toString() : "Todos";
            }

            @Override
            public FormatoFuncion fromString(String string) {
                return null;
            }
        });

        // Configurar filtro de sala
        try {
            List<Sala> salas = salaService.listarSalas();
            cmbFiltroSala.getItems().add(null); // Opción "Todas"
            cmbFiltroSala.getItems().addAll(salas);
            cmbFiltroSala.setConverter(new StringConverter<Sala>() {
                @Override
                public String toString(Sala sala) {
                    return sala != null ? sala.getNombre() + " (" + sala.getTipo() + ")" : "Todas";
                }

                @Override
                public Sala fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            mostrarError("Error", "No se pudieron cargar las salas: " + e.getMessage());
        }
    }

    private void configurarGrupoFechas() {
        grupoFechas = new ToggleGroup();
        btnDiaHoy.setToggleGroup(grupoFechas);
        btnDiaManana.setToggleGroup(grupoFechas);
        btnDiaPasado.setToggleGroup(grupoFechas);

        // Configurar textos con fechas
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        btnDiaHoy.setText("Hoy " + hoy.format(formatter));
        btnDiaManana.setText("Mañana " + hoy.plusDays(1).format(formatter));
        btnDiaPasado.setText("Pasado " + hoy.plusDays(2).format(formatter));
    }

    private void configurarEventos() {
        // Búsqueda en tiempo real
        txtBuscarPelicula.textProperty().addListener((obs, oldText, newText) -> filtrarPeliculas());

        // Filtros
        cmbFiltroIdioma.setOnAction(e -> cargarFuncionesPeliculaSeleccionada());
        cmbFiltroFormato.setOnAction(e -> cargarFuncionesPeliculaSeleccionada());
        cmbFiltroSala.setOnAction(e -> cargarFuncionesPeliculaSeleccionada());
    }

    @FXML
    private void onActualizarCartelera(ActionEvent event) {
        actualizarCartelera();
    }

    @FXML
    private void onLimpiarBusqueda(ActionEvent event) {
        txtBuscarPelicula.clear();
        cmbFiltroIdioma.setValue(null);
        cmbFiltroFormato.setValue(null);
        cmbFiltroSala.setValue(null);
    }

    @FXML
    private void onSeleccionarDia(ActionEvent event) {
        ToggleButton botonSeleccionado = (ToggleButton) event.getSource();

        if (botonSeleccionado == btnDiaHoy) {
            fechaSeleccionada = LocalDate.now();
        } else if (botonSeleccionado == btnDiaManana) {
            fechaSeleccionada = LocalDate.now().plusDays(1);
        } else if (botonSeleccionado == btnDiaPasado) {
            fechaSeleccionada = LocalDate.now().plusDays(2);
        }

        actualizarLabelFecha();
        cargarFuncionesPeliculaSeleccionada();
    }

    private void actualizarCartelera() {
        try {
            List<Integer> idsPeliculas = funcionDAO.listarIdsPeliculasDeFuncionesFuturas();
            List<Pelicula> nuevasPeliculas = new ArrayList<>();
            for (Integer id : idsPeliculas) {
                Pelicula p = peliculaDAO.buscarPorId(id);
                if (p != null && !nuevasPeliculas.contains(p)) {
                    nuevasPeliculas.add(p);
                }
            }
            cartelera.setPeliculas(nuevasPeliculas);
            mostrarPeliculasEnGrilla();
            lblEstadoSeleccion.setText("Cartelera actualizada correctamente");
        } catch (Exception e) {
            mostrarError("Error", "Error al actualizar la cartelera: " + e.getMessage());
        }
    }

    private void mostrarPeliculasEnGrilla() {
        grillaPeliculas.getChildren().clear();

        List<Pelicula> peliculasFiltradas = obtenerPeliculasFiltradas();
        int columnas = 2;
        int fila = 0;
        int columna = 0;

        for (Pelicula pelicula : peliculasFiltradas) {
            VBox tarjetaPelicula = crearTarjetaPelicula(pelicula);
            grillaPeliculas.add(tarjetaPelicula, columna, fila);

            columna++;
            if (columna >= columnas) {
                columna = 0;
                fila++;
            }
        }

        lblTotalPeliculas.setText("Películas mostradas: " + peliculasFiltradas.size());
    }

    private List<Pelicula> obtenerPeliculasFiltradas() {
        List<Pelicula> peliculasFiltradas = new ArrayList<>();
        String textoBusqueda = txtBuscarPelicula.getText().toLowerCase().trim();

        for (Pelicula pelicula : cartelera.getPeliculas()) {
            boolean coincideTexto = textoBusqueda.isEmpty() ||
                pelicula.getTitulo().toLowerCase().contains(textoBusqueda);

            if (coincideTexto) {
                peliculasFiltradas.add(pelicula);
            }
        }

        return peliculasFiltradas;
    }

    private void filtrarPeliculas() {
        mostrarPeliculasEnGrilla();
    }

    private VBox crearTarjetaPelicula(Pelicula pelicula) {
        VBox tarjeta = new VBox(8);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(10));
        tarjeta.getStyleClass().add("ticket-card");
        tarjeta.setPrefWidth(180);
        tarjeta.setMaxWidth(180);

        // Imagen de la película
        ImageView imagenPelicula = new ImageView();
        imagenPelicula.setFitWidth(140);
        imagenPelicula.setFitHeight(200);
        imagenPelicula.setPreserveRatio(false);

        try {
            if (pelicula.getImagenUrl() != null && !pelicula.getImagenUrl().isEmpty()) {
                Image imagen = new Image(pelicula.getImagenUrl(), true);
                imagenPelicula.setImage(imagen);
            } else {
                // Imagen por defecto
                Image imagenDefault = new Image(getClass().getResourceAsStream("/images/no-image.png"));
                imagenPelicula.setImage(imagenDefault);
            }
        } catch (Exception e) {
            // Si hay error cargando la imagen, usar una imagen por defecto o dejar vacío
            imagenPelicula.setImage(null);
        }

        // Título de la película
        Label lblTitulo = new Label(pelicula.getTitulo());
        lblTitulo.getStyleClass().add("ticket-price");
        lblTitulo.setWrapText(true);
        lblTitulo.setMaxWidth(160);
        lblTitulo.setAlignment(Pos.CENTER);

        // Información adicional
        Label lblInfo = new Label(String.format("%d min • %s",
            pelicula.getDuracionMinutos(),
            pelicula.getIdioma() != null ? pelicula.getIdioma().getNombre() : "N/A"));
        lblInfo.getStyleClass().add("summary-details");

        // Botón para seleccionar
        Button btnSeleccionar = new Button("Seleccionar");
        btnSeleccionar.setOnAction(e -> seleccionarPelicula(pelicula));
        btnSeleccionar.setPrefWidth(140);

        tarjeta.getChildren().addAll(imagenPelicula, lblTitulo, lblInfo, btnSeleccionar);
        return tarjeta;
    }

    private void seleccionarPelicula(Pelicula pelicula) {
        this.peliculaSeleccionada = pelicula;
        lblPeliculaSeleccionada.setText("Película: " + pelicula.getTitulo());
        cargarFuncionesPeliculaSeleccionada();
    }

    private void cargarFuncionesPeliculaSeleccionada() {
        if (peliculaSeleccionada == null || fechaSeleccionada == null) {
            contenedorFunciones.getChildren().clear();
            lblTotalFunciones.setText("Total de funciones: 0");
            lblEstadoSeleccion.setText("Seleccione una película y una fecha");
            return;
        }

        try {
            // Obtener todas las funciones de la película para la fecha seleccionada
            List<Funcion> todasLasFunciones = funcionDAO.listarTodas();
            funcionesDisponibles.clear();

            for (Funcion funcion : todasLasFunciones) {
                if (funcion.getPelicula().getId() == peliculaSeleccionada.getId() &&
                    funcion.getFechaHoraInicio().toLocalDate().equals(fechaSeleccionada)) {

                    // Aplicar filtros
                    boolean pasaFiltros = true;

                    if (cmbFiltroIdioma.getValue() != null) {
                        pasaFiltros = pasaFiltros && funcion.getPelicula().getIdioma() == cmbFiltroIdioma.getValue();
                    }

                    if (cmbFiltroFormato.getValue() != null) {
                        pasaFiltros = pasaFiltros && funcion.getFormato() == cmbFiltroFormato.getValue();
                    }

                    if (cmbFiltroSala.getValue() != null) {
                        pasaFiltros = pasaFiltros && funcion.getSala().getId() == cmbFiltroSala.getValue().getId();
                    }

                    if (pasaFiltros) {
                        funcionesDisponibles.add(funcion);
                    }
                }
            }

            mostrarFunciones();

        } catch (Exception e) {
            mostrarError("Error", "Error al cargar las funciones: " + e.getMessage());
        }
    }

    private void mostrarFunciones() {
        contenedorFunciones.getChildren().clear();

        if (funcionesDisponibles.isEmpty()) {
            Label lblSinFunciones = new Label("No hay funciones disponibles para los filtros seleccionados");
            lblSinFunciones.getStyleClass().add("summary-details");
            contenedorFunciones.getChildren().add(lblSinFunciones);
            lblTotalFunciones.setText("Total de funciones: 0");
            return;
        }

        for (Funcion funcion : funcionesDisponibles) {
            HBox tarjetaFuncion = crearTarjetaFuncion(funcion);
            contenedorFunciones.getChildren().add(tarjetaFuncion);
        }

        lblTotalFunciones.setText("Total de funciones: " + funcionesDisponibles.size());
        lblEstadoSeleccion.setText("Funciones cargadas para " + peliculaSeleccionada.getTitulo());
    }

    private HBox crearTarjetaFuncion(Funcion funcion) {
        HBox tarjeta = new HBox(15);
        tarjeta.setAlignment(Pos.CENTER_LEFT);
        tarjeta.setPadding(new Insets(10));
        tarjeta.getStyleClass().add("ticket-card");

        // Información de la función
        VBox infoFuncion = new VBox(5);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        Label lblHora = new Label(funcion.getFechaHoraInicio().format(timeFormatter));
        lblHora.getStyleClass().add("ticket-price");

        Label lblSala = new Label("Sala: " + funcion.getSala().getNombre());
        lblSala.getStyleClass().add("summary-details");

        Label lblFormato = new Label("Formato: " + funcion.getFormato().toString());
        lblFormato.getStyleClass().add("summary-details");

        Label lblIdioma = new Label("Idioma: " + funcion.getPelicula().getIdioma().getNombre());
        lblIdioma.getStyleClass().add("summary-details");

        infoFuncion.getChildren().addAll(lblHora, lblSala, lblFormato, lblIdioma);

        // Botón para seleccionar función
        Button btnSeleccionarFuncion = new Button("Seleccionar Función");
        btnSeleccionarFuncion.setOnAction(e -> seleccionarFuncion(funcion));

        HBox.setHgrow(infoFuncion, javafx.scene.layout.Priority.ALWAYS);
        tarjeta.getChildren().addAll(infoFuncion, btnSeleccionarFuncion);

        return tarjeta;
    }

    private void seleccionarFuncion(Funcion funcion) {
        Alert confirmacion = new Alert(Alert.AlertType.INFORMATION);
        confirmacion.setTitle("Función Seleccionada");
        confirmacion.setHeaderText("Ha seleccionado la siguiente función:");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String contenido = String.format(
            "Película: %s\n" +
            "Sala: %s\n" +
            "Fecha y Hora: %s\n" +
            "Formato: %s\n" +
            "Idioma: %s",
            funcion.getPelicula().getTitulo(),
            funcion.getSala().getNombre(),
            funcion.getFechaHoraInicio().format(formatter),
            funcion.getFormato().toString(),
            funcion.getPelicula().getIdioma().getNombre()
        );

        confirmacion.setContentText(contenido);
        confirmacion.showAndWait();

        // Aquí podrías agregar lógica adicional como navegar a la compra de boletos
        lblEstadoSeleccion.setText("Función seleccionada: " + funcion.getFechaHoraInicio().format(formatter));
    }

    private void actualizarLabelFecha() {
        if (fechaSeleccionada != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy");
            lblFechaSeleccionada.setText("Funciones para " + fechaSeleccionada.format(formatter));
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje != null ? mensaje : "Error desconocido");
        alert.showAndWait();
    }
}
