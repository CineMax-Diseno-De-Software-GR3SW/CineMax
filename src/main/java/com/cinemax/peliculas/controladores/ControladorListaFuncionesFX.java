package com.cinemax.peliculas.controladores;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.persistencia.FuncionDAO;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.modelos.persistencia.SalaDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ControladorListaFuncionesFX implements Initializable {

    private FuncionDAO funcionDAO;
    private SalaDAO salaDAO;

    // Componentes para búsqueda y filtros
    @FXML private TextField txtBuscarId;
    @FXML private ComboBox<Sala> cmbFiltrarSala;
    @FXML private Button btnBuscarId;
    @FXML private Button btnFiltrarSala;
    @FXML private Button btnMostrarTodas;
    @FXML private Button btnActualizar;

    // Botones de acción
    @FXML private Button btnNuevaFuncion;
    @FXML private Button btnVerDetalles;
    @FXML private Button btnEditarFuncion;
    @FXML private Button btnEliminarFuncion;

    // Tabla de funciones
    @FXML private TableView<Funcion> tablaFunciones;
    @FXML private TableColumn<Funcion, Integer> colId;
    @FXML private TableColumn<Funcion, String> colPelicula;
    @FXML private TableColumn<Funcion, String> colSala;
    @FXML private TableColumn<Funcion, String> colFechaHora;
    @FXML private TableColumn<Funcion, String> colFormato;
    @FXML private TableColumn<Funcion, String> colTipoEstreno;
    @FXML private TableColumn<Funcion, String> colEstado;

    // Labels informativos
    @FXML private Label lblTotalFunciones;
    @FXML private Label lblEstadoOperacion;

    // Datos para la tabla
    private ObservableList<Funcion> listaFunciones;
    private ObservableList<Funcion> funcionesFiltradas;

    public ControladorListaFuncionesFX() {
        this.funcionDAO = new FuncionDAO();
        this.salaDAO = new SalaDAO();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listaFunciones = FXCollections.observableArrayList();
        funcionesFiltradas = FXCollections.observableArrayList();

        configurarTabla();
        try {
            configurarComboBoxes();
        } catch (SQLException e) {
            mostrarError("Error de inicialización", "Error al cargar datos: " + e.getMessage());
        }
        cargarDatos();
    }

    private void configurarTabla() {
        // Configurar las columnas de la tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colPelicula.setCellValueFactory(cellData -> {
            String titulo = cellData.getValue().getPelicula() != null ?
                cellData.getValue().getPelicula().getTitulo() : "Sin película";
            return new javafx.beans.property.SimpleStringProperty(titulo);
        });

        colSala.setCellValueFactory(cellData -> {
            Sala sala = cellData.getValue().getSala();
            String nombreSala = sala != null ? sala.getNombre() : "Sala no asignada";
            return new javafx.beans.property.SimpleStringProperty(nombreSala);
        });

        colFechaHora.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String fechaHora = cellData.getValue().getFechaHoraInicio() != null ?
                cellData.getValue().getFechaHoraInicio().format(formatter) : "Sin fecha";
            return new javafx.beans.property.SimpleStringProperty(fechaHora);
        });

        colFormato.setCellValueFactory(cellData -> {
            String formato = cellData.getValue().getFormato() != null ?
                cellData.getValue().getFormato().toString() : "Sin formato";
            return new javafx.beans.property.SimpleStringProperty(formato);
        });

        colTipoEstreno.setCellValueFactory(cellData -> {
            String tipoEstreno = cellData.getValue().getTipoEstreno() != null ?
                cellData.getValue().getTipoEstreno().name() : "Sin tipo";
            return new javafx.beans.property.SimpleStringProperty(tipoEstreno);
        });

        colEstado.setCellValueFactory(cellData -> {
            // Aquí puedes agregar lógica para determinar el estado de la función
            return new javafx.beans.property.SimpleStringProperty("Activa");
        });

        // Configurar selección de tabla
        tablaFunciones.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean funcionSeleccionada = newSelection != null;
                btnVerDetalles.setDisable(!funcionSeleccionada);
                btnEditarFuncion.setDisable(!funcionSeleccionada);
                btnEliminarFuncion.setDisable(!funcionSeleccionada);
            }
        );

        tablaFunciones.setItems(funcionesFiltradas);
    }

    private void configurarComboBoxes() throws SQLException {
        // Configurar ComboBox de salas para filtro
        List<Sala> salas = salaDAO.listarTodas();
        cmbFiltrarSala.setItems(FXCollections.observableArrayList(salas));
        cmbFiltrarSala.setConverter(new javafx.util.StringConverter<Sala>() {
            @Override
            public String toString(Sala sala) {
                return sala != null ? sala.getNombre() + " (" + sala.getTipoSala().name() + ")" : "";
            }

            @Override
            public Sala fromString(String string) {
                return null;
            }
        });
    }

    private void cargarDatos() {
        cargarFunciones();
    }

    private void cargarFunciones() {
        try {
            lblEstadoOperacion.setText("Cargando funciones...");
            List<Funcion> funciones = funcionDAO.listarTodas();
            listaFunciones.clear();
            listaFunciones.addAll(funciones);
            funcionesFiltradas.setAll(listaFunciones);
            actualizarEstadisticas();
            lblEstadoOperacion.setText("Funciones cargadas correctamente");
        } catch (Exception e) {
            lblEstadoOperacion.setText("Error al cargar funciones");
            mostrarError("Error al cargar funciones", e.getMessage());
        }
    }

    private void actualizarEstadisticas() {
        int totalFunciones = listaFunciones.size();
        int funcionesMostradas = funcionesFiltradas.size();
        lblTotalFunciones.setText("Funciones mostradas: " + funcionesMostradas + " de " + totalFunciones);
    }

    @FXML
    private void onNuevaFuncion(ActionEvent event) {
        abrirFormularioFuncion(null);
    }

    @FXML
    private void onEditarFuncion(ActionEvent event) {
        Funcion funcionSeleccionada = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcionSeleccionada != null) {
            abrirFormularioFuncion(funcionSeleccionada);
        }
    }

    @FXML
    private void onEliminarFuncion(ActionEvent event) {
        Funcion funcionSeleccionada = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcionSeleccionada != null) {
            eliminarFuncion(funcionSeleccionada);
        }
    }

    @FXML
    private void onVerDetalles(ActionEvent event) {
        Funcion funcionSeleccionada = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcionSeleccionada != null) {
            mostrarDetallesFuncion(funcionSeleccionada);
        }
    }

    @FXML
    private void onBuscarId(ActionEvent event) {
        buscarPorId();
    }

    @FXML
    private void onFiltrarSala(ActionEvent event) {
        filtrarPorSala();
    }

    @FXML
    private void onMostrarTodas(ActionEvent event) {
        mostrarTodasLasFunciones();
    }

    @FXML
    private void onActualizar(ActionEvent event) {
        cargarFunciones();
    }

    private void abrirFormularioFuncion(Funcion funcionAEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vista/Peliculas/PantallaFormularioFuncion.fxml"));
            Parent root = loader.load();

            ControladorFormularioFuncionFX controlador = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(funcionAEditar == null ? "Nueva Función" : "Editar Función");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnNuevaFuncion.getScene().getWindow());

            // Configurar el controlador del formulario
            if (funcionAEditar != null) {
                controlador.configurarParaEdicion(funcionAEditar);
            }

            // Configurar callback para actualizar la lista después de guardar
            controlador.setOnFuncionGuardada(() -> {
                cargarFunciones();
            });

            stage.showAndWait();

        } catch (IOException e) {
            mostrarError("Error", "No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    private void eliminarFuncion(Funcion funcion) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("Eliminar función");
        alert.setContentText("¿Está seguro de que desea eliminar la función del " +
            funcion.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
            " para la película \"" + funcion.getPelicula().getTitulo() + "\"?");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                funcionDAO.eliminar(funcion.getId());
                cargarFunciones();
                mostrarInformacion("Éxito", "Función eliminada correctamente");
            } catch (SQLException e) {
                mostrarError("Error al eliminar", "No se pudo eliminar la función: " + e.getMessage());
            }
        }
    }

    private void mostrarDetallesFuncion(Funcion funcion) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles de la Función");
        alert.setHeaderText("Información completa de la función");

        StringBuilder detalles = new StringBuilder();
        detalles.append("ID: ").append(funcion.getId()).append("\n");
        detalles.append("Película: ").append(funcion.getPelicula().getTitulo()).append("\n");
        detalles.append("Sala: ").append(funcion.getSala() != null ? funcion.getSala().getNombre() : "No asignada").append("\n");
        detalles.append("Fecha y Hora: ").append(funcion.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        detalles.append("Formato: ").append(funcion.getFormato()).append("\n");
        detalles.append("Tipo de Estreno: ").append(funcion.getTipoEstreno()).append("\n");

        alert.setContentText(detalles.toString());
        alert.showAndWait();
    }

    private void buscarPorId() {
        String idTexto = txtBuscarId.getText().trim();

        if (idTexto.isEmpty()) {
            mostrarTodasLasFunciones();
            return;
        }

        try {
            int id = Integer.parseInt(idTexto);
            funcionesFiltradas.clear();

            for (Funcion f : listaFunciones) {
                if (f.getId() == id) {
                    funcionesFiltradas.add(f);
                    tablaFunciones.getSelectionModel().select(f);
                    lblEstadoOperacion.setText("Función encontrada con ID: " + id);
                    actualizarEstadisticas();
                    return;
                }
            }

            lblEstadoOperacion.setText("No se encontró función con ID: " + id);
            actualizarEstadisticas();

        } catch (NumberFormatException e) {
            lblEstadoOperacion.setText("Por favor ingrese un ID válido (número entero)");
        }
    }

    private void filtrarPorSala() {
        Sala salaSeleccionada = cmbFiltrarSala.getValue();

        if (salaSeleccionada == null) {
            mostrarTodasLasFunciones();
            return;
        }

        funcionesFiltradas.clear();
        for (Funcion f : listaFunciones) {
            if (f.getSala() != null && f.getSala().getId() == salaSeleccionada.getId()) {
                funcionesFiltradas.add(f);
            }
        }

        lblEstadoOperacion.setText("Mostrando funciones de la sala: " + salaSeleccionada.getNombre());
        actualizarEstadisticas();
    }

    private void mostrarTodasLasFunciones() {
        funcionesFiltradas.setAll(listaFunciones);
        txtBuscarId.clear();
        cmbFiltrarSala.setValue(null);
        lblEstadoOperacion.setText("Mostrando todas las funciones");
        actualizarEstadisticas();
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
