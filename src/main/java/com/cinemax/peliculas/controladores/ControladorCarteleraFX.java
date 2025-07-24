package com.cinemax.peliculas.controladores;

import java.net.URL;
import java.sql.SQLException;
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
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ControladorCarteleraFX implements Initializable {

    private ServicioPelicula servicioPelicula;
    private Cartelera cartelera;
    private FuncionDAO funcionDAO;
    private PeliculaDAO peliculaDAO;

    // Componentes de la interfaz FXML
    @FXML private TextField txtBuscarTitulo;
    @FXML private TextField txtBuscarId;
    @FXML private GridPane grillaCartelera;

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
        this.listaPeliculasCartelera = FXCollections.observableArrayList();
        this.peliculasFiltradas = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            servicioPelicula = new ServicioPelicula();
            cartelera = new Cartelera(servicioPelicula.obtenerPeliculas());
            listaPeliculasCartelera.addAll(cartelera.getPeliculas());
            mostrarTodasLasPeliculas();
        } catch (Exception e) {
            mostrarError("Error al inicializar la cartelera", e.getMessage());
        }
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
        // Implementar lógica para mostrar detalles de la película seleccionada
        // Aquí se puede agregar un diálogo o una nueva vista para mostrar los detalles
        mostrarInformacion("Detalles de la Película", "Funcionalidad en desarrollo.");
    }

    private void actualizarGrilla(List<Pelicula> peliculas) {
        grillaCartelera.getChildren().clear();

        int row = 0;
        int col = 0;
        for (Pelicula pelicula : peliculas) {
            ImageView imagenPelicula = new ImageView(new Image(pelicula.getUrlImagen()));
            imagenPelicula.setFitWidth(150); // Ajustar tamaño de las imágenes
            imagenPelicula.setFitHeight(200);

            Label titulo = new Label(pelicula.getTitulo());
            Label genero = new Label(pelicula.getGenero());
            Label anio = new Label(String.valueOf(pelicula.getAnio()));

            VBox item = new VBox(10, imagenPelicula, titulo, genero, anio);
            item.setAlignment(Pos.CENTER);
            item.setPrefWidth(200); // Ajustar tamaño de las grillas individuales

            grillaCartelera.add(item, col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private void actualizarGrilla() {
        grillaCartelera.getChildren().clear();
        int columnas = 3;
        int fila = 0;
        int columna = 0;

        for (Pelicula pelicula : peliculasFiltradas) {
            VBox contenedor = new VBox();
            contenedor.setAlignment(Pos.CENTER);

            ImageView imagen = new ImageView(new Image(pelicula.getUrlImagen()));
            imagen.setFitWidth(150);
            imagen.setFitHeight(200);

            Label titulo = new Label(pelicula.getTitulo());
            contenedor.getChildren().addAll(imagen, titulo);

            grillaCartelera.add(contenedor, columna, fila);

            columna++;
            if (columna == columnas) {
                columna = 0;
                fila++;
            }
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

    @FXML
    private void buscarPorTitulo() {
        String titulo = txtBuscarTitulo.getText().trim().toLowerCase();
        if (titulo.isEmpty()) {
            mostrarTodasLasPeliculas();
            return;
        }

        peliculasFiltradas.clear();
        for (Pelicula pelicula : listaPeliculasCartelera) {
            if (pelicula.getTitulo().toLowerCase().contains(titulo)) {
                peliculasFiltradas.add(pelicula);
            }
        }

        actualizarGrilla();
        lblEstadoCartelera.setText("Películas encontradas: " + peliculasFiltradas.size());
    }

    @FXML
    private void buscarPorId() {
        String idTexto = txtBuscarId.getText().trim();
        if (idTexto.isEmpty()) {
            mostrarTodasLasPeliculas();
            return;
        }

        try {
            int id = Integer.parseInt(idTexto);
            peliculasFiltradas.clear();
            for (Pelicula pelicula : listaPeliculasCartelera) {
                if (pelicula.getId() == id) {
                    peliculasFiltradas.add(pelicula);
                    break;
                }
            }

            actualizarGrilla();
            lblEstadoCartelera.setText("Película encontrada con ID: " + id);
        } catch (NumberFormatException e) {
            lblEstadoCartelera.setText("Por favor ingrese un ID válido.");
        }
    }

    private void mostrarTodasLasPeliculas() {
        peliculasFiltradas.clear();
        peliculasFiltradas.addAll(listaPeliculasCartelera);
        actualizarGrilla();
        lblEstadoCartelera.setText("Mostrando todas las películas.");
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
