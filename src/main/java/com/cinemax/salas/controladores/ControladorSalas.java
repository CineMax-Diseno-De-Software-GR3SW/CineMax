package com.cinemax.salas.controladores;

import com.cinemax.salas.modelos.entidades.EstadoSala;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.modelos.entidades.TipoSala;
import com.cinemax.salas.servicios.SalaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ControladorSalas {
    @FXML
    private TextField txtNombre;
    @FXML private TextField txtCapacidad;
    @FXML private ComboBox<TipoSala> cmbTipo;
    @FXML private ComboBox<EstadoSala> cmbEstado;
    @FXML private TableView<Sala> tablaSalas;
    @FXML private TableColumn<Sala, Integer> colId;
    @FXML private TableColumn<Sala, String> colNombre;
    @FXML private TableColumn<Sala, Integer> colCapacidad;
    @FXML private TableColumn<Sala, String> colTipo;
    @FXML private TableColumn<Sala, String> colEstado;

    private final SalaService salaService = new SalaService();
    private final ObservableList<Sala> salas = FXCollections.observableArrayList();

    @FXML
    public void initialize() throws Exception {
        cmbTipo.setItems(FXCollections.observableArrayList(TipoSala.values()));
        cmbEstado.setItems(FXCollections.observableArrayList(EstadoSala.values()));

        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        colCapacidad.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCapacidad()).asObject());
        colTipo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTipo().name()));
        colEstado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEstado().name()));

        tablaSalas.setItems(salas);
        cargarSalas();

        tablaSalas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtNombre.setText(newSel.getNombre());
                txtCapacidad.setText(String.valueOf(newSel.getCapacidad()));
                cmbTipo.setValue(newSel.getTipo());
                cmbEstado.setValue(newSel.getEstado());
            }
        });
    }

    private void cargarSalas() throws Exception {
        salas.setAll(salaService.listarSalas());
    }

    // Método auxiliar para mostrar avisos personalizados
    private void mostrarAviso(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 15px;");
        alert.showAndWait();
    }

    @FXML
    private void crearSala() {
        try {
            Sala sala = new Sala();
            sala.setNombre(txtNombre.getText());
            sala.setCapacidad(Integer.parseInt(txtCapacidad.getText()));
            sala.setTipo(cmbTipo.getValue());
            sala.setEstado(cmbEstado.getValue());
            salaService.crearSala(sala);
            cargarSalas();
            limpiarCampos();
        } catch (NumberFormatException e) {
            mostrarAviso("Datos inválidos", "La capacidad debe ser un número válido.");
        } catch (Exception e) {
            mostrarAviso("No se pudo crear la sala", e.getMessage());
        }
    }

    @FXML
    private void actualizarSala() {
        Sala seleccionada = tablaSalas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            try {
                seleccionada.setNombre(txtNombre.getText());
                seleccionada.setCapacidad(Integer.parseInt(txtCapacidad.getText()));
                seleccionada.setTipo(cmbTipo.getValue());
                seleccionada.setEstado(cmbEstado.getValue());
                salaService.actualizarSala(seleccionada);
                cargarSalas();
                limpiarCampos();
            } catch (NumberFormatException e) {
                mostrarAviso("Datos inválidos", "La capacidad debe ser un número válido.");
            } catch (Exception e) {
                mostrarAviso("No se pudo actualizar la sala", e.getMessage());
            }
        }
    }

    @FXML
    private void eliminarSala() throws Exception {
        Sala seleccionada = tablaSalas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            salaService.eliminarSala(seleccionada.getId());
            cargarSalas();
            limpiarCampos();
        }
    }
    @FXML private TextField txtBuscarId; // Asegúrate de tenerlo en el FXML

    @FXML
    private void buscarSalaPorId() throws Exception {
        String idText = txtBuscarId.getText().trim();
        if (idText.isEmpty()) {
            cargarSalas();
            return;
        }
        try {
            int id = Integer.parseInt(idText);
            Sala sala = salaService.obtenerSalaPorId(id);
            if (sala != null) {
                salas.setAll(sala);
            } else {
                salas.clear();
            }
        } catch (NumberFormatException e) {
            // Maneja el error de formato de número
            salas.clear();
        }
    }
    @FXML
    private void limpiarCampos() {
        txtNombre.clear();
        txtCapacidad.clear();
        cmbTipo.getSelectionModel().clearSelection();
        cmbEstado.getSelectionModel().clearSelection();
        tablaSalas.getSelectionModel().clearSelection();
    }

}
