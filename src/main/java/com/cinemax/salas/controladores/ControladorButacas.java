package com.cinemax.salas.controladores;

import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.EstadoButaca;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.servicios.ButacaService;
import com.cinemax.salas.servicios.SalaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ControladorButacas {
    private final SalaService salaService    = new SalaService();
    private final ButacaService servicio     = new ButacaService();

    @FXML private TextField txtBuscarIdSala;
    @FXML private TableView<Butaca> tablaButacas;
    @FXML private TableColumn<Butaca, Integer> colId;
    @FXML private TableColumn<Butaca, String>  colFila;
    @FXML private TableColumn<Butaca, String>  colColumna;
    @FXML private TableColumn<Butaca, String>  colEstado;
    @FXML private TableColumn<Butaca, Integer> colIdSala;

    @FXML private TextField txtFila;
    @FXML private TextField txtColumna;
    @FXML private ComboBox<EstadoButaca> cmbEstado;
    @FXML private ComboBox<Sala> cmbSala;              // <-- agregado
    @FXML private Label lblEstado;

    private final ObservableList<Sala>   salas   = FXCollections.observableArrayList();
    private final ObservableList<Butaca> butacas = FXCollections.observableArrayList();

    @FXML
    public void initialize() throws Exception {
        salas.setAll(salaService.listarSalas());
        cmbSala.setItems(salas);

        // ——— Aquí pones el CellFactory ———
        // Renderiza cada Sala mostrando sólo su ID
        cmbSala.setCellFactory(listView -> new ListCell<Sala>() {
            @Override
            protected void updateItem(Sala sala, boolean empty) {
                super.updateItem(sala, empty);
                setText(empty || sala == null
                        ? null
                        : String.valueOf(sala.getId()));
                setStyle("-fx-text-fill: #000000;");
            }
        });
        // Asegura que al colapsar el combo también muestre sólo el ID
        cmbSala.setButtonCell(new ListCell<Sala>() {
            @Override
            protected void updateItem(Sala sala, boolean empty) {
                super.updateItem(sala, empty);
                setText(empty || sala == null
                        ? null
                        : String.valueOf(sala.getId()));
                setStyle("-fx-text-fill: #ffffff;");
            }
        });
        // —— Configuro las columnas de la tabla ——
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colFila.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFila()));
        colColumna.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getColumna()));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstado()));
        colIdSala.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdSala()).asObject());

        // —— Items de la tabla y combo de estados ——
        tablaButacas.setItems(butacas);
        cmbEstado.setItems(FXCollections.observableArrayList(EstadoButaca.values()));

        // —— Listener para cargar la butaca seleccionada en el formulario ——
        tablaButacas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                txtFila.setText(sel.getFila());
                txtColumna.setText(sel.getColumna());
                cmbEstado.setValue(EstadoButaca.valueOf(sel.getEstado()));
                // en vez de new Sala(...), buscamos en la lista existente:
                salas.stream()
                        .filter(s -> s.getId() == sel.getIdSala())
                        .findFirst()
                        .ifPresent(s -> cmbSala.setValue(s));
            }
        });
    }

    @FXML
    private boolean validarCampos() {
        String fila = txtFila.getText().trim();
        String columna = txtColumna.getText().trim();
        EstadoButaca estado = cmbEstado.getValue();
        Sala sala = cmbSala.getValue();

        if (fila.isEmpty() || columna.isEmpty() || estado == null || sala == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor, complete todos los campos obligatorios.");
            return false;
        }
        // Validación de datos erróneos (ejemplo: solo letras/números, longitud, etc.)
        // Validación de Fila: solo letras de la A a la Z
        if (!fila.matches("[A-Za-z]")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Dato erróneo", "El campo Fila solo debe contener una letra de la A a la Z.");
            return false;
        }
// Validación de Columna: solo un dígito del 0 al 9
        if (!columna.matches("[0-9]")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Dato erróneo", "El campo Columna solo debe contener un número del 0 al 9.");
            return false;
        }
        return true;
    }

    @FXML
    private void listarButacasPorSala(ActionEvent e) {
        try {
            String txt = txtBuscarIdSala.getText().trim();
            List<Butaca> lista = txt.isEmpty()
                    ? servicio.listarTodasButacas()
                    : servicio.listarButacasPorSala(Integer.parseInt(txt));
            butacas.setAll(lista);
            lblEstado.setText("Mostrando " + lista.size() + " butacas");
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al listar", ex.getMessage());
        }
    }

    @FXML
    private void listarTodasButacas(ActionEvent e) {
        listarButacasPorSala(e);
    }


    @FXML
    private void crearButaca(ActionEvent e) {
        if (!validarCampos()) return;
        try {
            Butaca b = new Butaca();
            b.setFila(txtFila.getText());
            b.setColumna(txtColumna.getText());
            b.setEstado(cmbEstado.getValue().name());
            b.setIdSala(cmbSala.getValue().getId());

            servicio.crearButaca(b);

            listarButacasPorSala(null);
            limpiarCampos();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Butaca creada correctamente.");
        } catch (Exception ex) {
            String msg = ex.getMessage().toLowerCase();
            if (msg.contains("ya existe")) {
                mostrarAlerta(Alert.AlertType.ERROR, "Butaca duplicada", ex.getMessage());
            } else if (msg.contains("capacidad")) {
                mostrarAlerta(Alert.AlertType.WARNING, "Capacidad excedida", ex.getMessage());
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al crear butaca", ex.getMessage());
            }
        }
    }

    @FXML
    private void actualizarButaca(ActionEvent e) {
        Butaca sel = tablaButacas.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        if (!validarCampos()) return;
        try {
            sel.setFila(txtFila.getText());
            sel.setColumna(txtColumna.getText());
            sel.setEstado(cmbEstado.getValue().name());
            sel.setIdSala(cmbSala.getValue().getId());

            servicio.actualizarButaca(sel);

            listarButacasPorSala(null);
            limpiarCampos();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Butaca actualizada correctamente.");
        } catch (Exception ex) {
            String msg = ex.getMessage().toLowerCase();
            if (msg.contains("ya existe")) {
                mostrarAlerta(Alert.AlertType.ERROR, "Butaca duplicada", ex.getMessage());
            } else if (msg.contains("capacidad")) {
                mostrarAlerta(Alert.AlertType.WARNING, "Capacidad excedida", ex.getMessage());
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al actualizar butaca", ex.getMessage());
            }
        }
    }


    @FXML
    private void eliminarButaca(ActionEvent e) {
        try {
            Butaca sel = tablaButacas.getSelectionModel().getSelectedItem();
            if (sel == null) return;

            servicio.eliminarButaca(sel.getId());
            listarButacasPorSala(null);
            limpiarCampos();
            lblEstado.setText("Butaca eliminada correctamente");
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar", ex.getMessage());
        }
    }
    public void onBackAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/empleados/PantallaPortalPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void limpiarCampos() {
        txtFila.clear();
        txtColumna.clear();
        cmbEstado.getSelectionModel().clearSelection();
        cmbSala.getSelectionModel().clearSelection();   // <-- limpio el combo
        tablaButacas.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType type, String titulo, String mensaje) {
        Alert a = new Alert(type);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.getDialogPane().setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 15px;");
        a.showAndWait();
    }
}
