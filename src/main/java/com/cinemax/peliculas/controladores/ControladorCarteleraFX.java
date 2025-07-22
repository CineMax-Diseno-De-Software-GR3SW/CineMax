package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.cinemax.peliculas.modelos.entidades.Cartelera;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.peliculas.modelos.persistencia.FuncionDAO;
import com.cinemax.peliculas.modelos.persistencia.PeliculaDAO;
import com.cinemax.peliculas.servicios.ServicioPelicula;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ControladorCarteleraFX implements Initializable {

    private ServicioPelicula servicioPelicula;
    private Cartelera cartelera;
    private FuncionDAO funcionDAO;
    private PeliculaDAO peliculaDAO;

    // Componentes de la interfaz FXML
    @FXML private TextField txtBuscarTitulo;
    @FXML private TextField txtBuscarId;
    @FXML private TableView<Pelicula> tablaCartelera;
    @FXML private TableColumn<Pelicula, Integer> colId;
    @FXML private TableColumn<Pelicula, String> colTitulo;
    @FXML private TableColumn<Pelicula, Integer> colAnio;
    @FXML private TableColumn<Pelicula, String> colGenero;
    @FXML private TableColumn<Pelicula, Integer> colDuracion;
    @FXML private TableColumn<Pelicula, String> colIdioma;

    @FXML private Button btnActualizarCartelera;
    @FXML private Button btnBuscarTitulo;
    @FXML private Button btnBuscarId;
    @FXML private Button btnLimpiarBusqueda;
    @FXML private Button btnVerDetalles;

    @FXML private Label lblTotalPeliculas;
    @FXML private Label lblEstadoCartelera;

    // Datos para la tabla
    private ObservableList<Pelicula> listaPeliculasCartelera;
    private ObservableList<Pelicula> peliculasFiltradas;

    public ControladorCarteleraFX() {
        this.servicioPelicula = new ServicioPelicula();
        this.cartelera = new Cartelera(new ArrayList<>());
        this.funcionDAO = new FuncionDAO();
        this.peliculaDAO = new PeliculaDAO();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listaPeliculasCartelera = FXCollections.observableArrayList();
        peliculasFiltradas = FXCollections.observableArrayList();

        configurarTabla();
        configurarEventos();
        actualizarCartelera();
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
            var idioma = cellData.getValue().getIdioma();
            return new javafx.beans.property.SimpleStringProperty(
                idioma != null ? idioma.getNombre() : "N/A"
            );
        });

        // Configurar selección de tabla
        tablaCartelera.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean peliculaSeleccionada = newSelection != null;
                btnVerDetalles.setDisable(!peliculaSeleccionada);
            }
        );

        tablaCartelera.setItems(peliculasFiltradas);
    }

    private void configurarEventos() {
        // Configurar búsqueda en tiempo real por título
        txtBuscarTitulo.textProperty().addListener((obs, oldText, newText) -> {
            if (txtBuscarId.getText().trim().isEmpty()) {
                buscarPorTitulo();
            }
        });

        // Configurar búsqueda en tiempo real por ID
        txtBuscarId.textProperty().addListener((obs, oldText, newText) -> {
            if (txtBuscarTitulo.getText().trim().isEmpty()) {
                buscarPorId();
            }
        });
    }

    @FXML
    private void onActualizarCartelera(ActionEvent event) {
        actualizarCartelera();
    }

    @FXML
    private void onBuscarTitulo(ActionEvent event) {
        buscarPorTitulo();
    }

    @FXML
    private void onBuscarId(ActionEvent event) {
        buscarPorId();
    }

    @FXML
    private void onLimpiarBusqueda(ActionEvent event) {
        txtBuscarTitulo.clear();
        txtBuscarId.clear();
        mostrarTodasLasPeliculas();
    }

    @FXML
    private void onVerDetalles(ActionEvent event) {
        Pelicula peliculaSeleccionada = tablaCartelera.getSelectionModel().getSelectedItem();
        if (peliculaSeleccionada != null) {
            mostrarDetallesPelicula(peliculaSeleccionada);
        }
    }

    private void actualizarCartelera() {
        try {
            lblEstadoCartelera.setText("Actualizando cartelera...");

            List<Integer> idsPeliculas = funcionDAO.listarIdsPeliculasDeFuncionesFuturas();
            List<Pelicula> nuevasPeliculas = new ArrayList<>();

            for (Integer id : idsPeliculas) {
                Pelicula p = peliculaDAO.buscarPorId(id);
                if (p != null && !nuevasPeliculas.contains(p)) {
                    nuevasPeliculas.add(p);
                }
            }

            cartelera.setPeliculas(nuevasPeliculas);
            listaPeliculasCartelera.clear();
            listaPeliculasCartelera.addAll(nuevasPeliculas);

            mostrarTodasLasPeliculas();
            lblEstadoCartelera.setText("Cartelera actualizada correctamente");

            mostrarInformacion("Éxito", "Cartelera actualizada correctamente.");

        } catch (Exception e) {
            lblEstadoCartelera.setText("Error al actualizar la cartelera");
            mostrarError("Error al actualizar la cartelera", e.getMessage());
        }
    }

    private void buscarPorTitulo() {
        String titulo = txtBuscarTitulo.getText().trim().toLowerCase();

        if (titulo.isEmpty()) {
            mostrarTodasLasPeliculas();
            return;
        }

        peliculasFiltradas.clear();
        List<Pelicula> resultados = new ArrayList<>();

        for (Pelicula p : cartelera.getPeliculas()) {
            if (p.getTitulo().toLowerCase().contains(titulo)) {
                resultados.add(p);
            }
        }

        peliculasFiltradas.addAll(resultados);
        actualizarEstadisticas();

        if (resultados.isEmpty()) {
            lblEstadoCartelera.setText("No se encontraron películas con ese título en la cartelera");
        } else {
            lblEstadoCartelera.setText("Resultados de búsqueda por título: " + resultados.size() + " película(s)");
        }
    }

    private void buscarPorId() {
        String idTexto = txtBuscarId.getText().trim();

        if (idTexto.isEmpty()) {
            mostrarTodasLasPeliculas();
            return;
        }

        try {
            int id = Integer.parseInt(idTexto);
            peliculasFiltradas.clear();

            for (Pelicula p : cartelera.getPeliculas()) {
                if (p.getId() == id) {
                    peliculasFiltradas.add(p);
                    tablaCartelera.getSelectionModel().select(p);
                    lblEstadoCartelera.setText("Película encontrada con ID: " + id);
                    actualizarEstadisticas();
                    return;
                }
            }

            lblEstadoCartelera.setText("No se encontró película con ID: " + id + " en la cartelera");
            actualizarEstadisticas();

        } catch (NumberFormatException e) {
            lblEstadoCartelera.setText("Por favor ingrese un ID válido (número entero)");
        }
    }

    private void mostrarTodasLasPeliculas() {
        peliculasFiltradas.clear();
        peliculasFiltradas.addAll(listaPeliculasCartelera);
        actualizarEstadisticas();

        if (listaPeliculasCartelera.isEmpty()) {
            lblEstadoCartelera.setText("No hay películas en la cartelera");
        } else {
            lblEstadoCartelera.setText("Mostrando todas las películas en cartelera");
        }
    }

    private void actualizarEstadisticas() {
        int total = peliculasFiltradas.size();
        lblTotalPeliculas.setText("Películas mostradas: " + total + " de " + listaPeliculasCartelera.size());
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
