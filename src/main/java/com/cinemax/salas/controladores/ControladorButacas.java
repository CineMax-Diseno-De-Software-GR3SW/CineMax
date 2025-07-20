package com.cinemax.salas.controladores;

import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.EstadoButaca;
import com.cinemax.salas.servicios.ButacaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class ControladorButacas {
    @FXML private TextField txtFila;
    @FXML private TextField txtColumna;
    @FXML private ComboBox<EstadoButaca> cmbEstado;
    @FXML private TextField txtIdSala;
    @FXML private TableView<Butaca> tablaButacas;
    @FXML
    private TableColumn<Butaca, Integer> colId;
    @FXML private TableColumn<Butaca, String> colFila;
    @FXML private TableColumn<Butaca, String> colColumna;
    @FXML private TableColumn<Butaca, String> colEstado;
    @FXML private TableColumn<Butaca, Integer> colIdSala;

    private final ButacaService butacaService = new ButacaService();
    private final ObservableList<Butaca> butacas = FXCollections.observableArrayList();

    @FXML
    public void initialize() throws Exception {
        cmbEstado.setItems(FXCollections.observableArrayList(EstadoButaca.values()));

        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colFila.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFila()));
        colColumna.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getColumna()));
        colEstado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEstado()));
        colIdSala.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getIdSala()).asObject());

        tablaButacas.setItems(butacas);

        tablaButacas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtFila.setText(newSel.getFila());
                txtColumna.setText(newSel.getColumna());
                cmbEstado.setValue(EstadoButaca.valueOf(newSel.getEstado()));
                txtIdSala.setText(String.valueOf(newSel.getIdSala()));
            }
        });
    }

    private void cargarButacas(int idSala) throws Exception {
        butacas.setAll(butacaService.listarButacasPorSala(idSala));
    }

    private void mostrarAviso(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 15px;");
        alert.showAndWait();
    }

    @FXML
    private void crearButaca() {
        try {
            Butaca butaca = new Butaca();
            butaca.setFila(txtFila.getText());
            butaca.setColumna(txtColumna.getText());
            butaca.setEstado(cmbEstado.getValue().name());
            butaca.setIdSala(Integer.parseInt(txtIdSala.getText()));
            butacaService.crearButaca(butaca);
            cargarButacas(butaca.getIdSala());
            limpiarCampos();
        } catch (NumberFormatException e) {
            mostrarAviso("Datos inválidos", "El ID de sala debe ser un número válido.");
        } catch (Exception e) {
            mostrarAviso("No se pudo crear la butaca", e.getMessage());
        }
    }

    @FXML
    private void actualizarButaca() {
        Butaca seleccionada = tablaButacas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            try {
                seleccionada.setFila(txtFila.getText());
                seleccionada.setColumna(txtColumna.getText());
                seleccionada.setEstado(cmbEstado.getValue().name());
                seleccionada.setIdSala(Integer.parseInt(txtIdSala.getText()));
                butacaService.actualizarButaca(seleccionada);
                cargarButacas(seleccionada.getIdSala());
                limpiarCampos();
            } catch (NumberFormatException e) {
                mostrarAviso("Datos inválidos", "El ID de sala debe ser un número válido.");
            } catch (Exception e) {
                mostrarAviso("No se pudo actualizar la butaca", e.getMessage());
            }
        }
    }

    @FXML
    private void eliminarButaca() throws Exception {
        Butaca seleccionada = tablaButacas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            butacaService.eliminarButaca(seleccionada.getId());
            cargarButacas(seleccionada.getIdSala());
            limpiarCampos();
        }
    }

    @FXML private TextField txtBuscarIdSala;

    @FXML
    private void listarButacasPorSala() throws Exception {
        String idSalaText = txtBuscarIdSala.getText().trim();
        if (idSalaText.isEmpty()) {
            butacas.clear();
            return;
        }
        try {
            int idSala = Integer.parseInt(idSalaText);
            cargarButacas(idSala);
        } catch (NumberFormatException e) {
            butacas.clear();
        }
    }

    @FXML
    private void limpiarCampos() {
        txtFila.clear();
        txtColumna.clear();
        cmbEstado.getSelectionModel().clearSelection();
        txtIdSala.clear();
        tablaButacas.getSelectionModel().clearSelection();
    }

}
