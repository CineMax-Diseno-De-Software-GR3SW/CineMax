package com.cinemax.salas.controladores;

import com.cinemax.comun.ManejadorMetodosComunes;
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
    @FXML private ComboBox<Sala> cmbSala;
    @FXML private Label lblEstado;
    @FXML private Label lblTotalButacas;

    private final ObservableList<Sala>   salas   = FXCollections.observableArrayList();
    private final ObservableList<Butaca> butacas = FXCollections.observableArrayList();


    @FXML
    public void initialize() throws Exception {
        // Inicializa ComboBox de estado de butaca
        cmbEstado.setItems(FXCollections.observableArrayList(EstadoButaca.values()));

        // Carga las salas disponibles en el ComboBox
        salas.setAll(salaService.listarSalas());
        cmbSala.setItems(salas);

        // Configura las columnas de la tabla
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colFila.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFila()));
        colColumna.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getColumna()));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstado()));
        colIdSala.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdSala()).asObject());

        // Asocia la lista de butacas a la tabla
        tablaButacas.setItems(butacas);

        // Listener para seleccionar una butaca y mostrar sus datos en el formulario
        tablaButacas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                txtFila.setText(sel.getFila());
                txtColumna.setText(sel.getColumna());
                cmbEstado.setValue(EstadoButaca.valueOf(sel.getEstado()));
                // Busca la sala correspondiente por ID
                Sala salaSeleccionada = salas.stream()
                        .filter(s -> s.getId() == sel.getIdSala())
                        .findFirst()
                        .orElse(null);
                cmbSala.setValue(salaSeleccionada);
            }
        });

        // Carga todas las butacas al iniciar la vista
        listarButacasPorSala(null);
    }

    @FXML
    private boolean validarCampos() {
        String fila = txtFila.getText().trim();
        String columna = txtColumna.getText().trim();
        EstadoButaca estado = cmbEstado.getValue();
        Sala sala = cmbSala.getValue();

        if (fila.isEmpty() || columna.isEmpty() || estado == null || sala == null) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, complete todos los campos obligatorios.");
            return false;
        }
        if (!fila.matches("[A-Za-z]")) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("El campo Fila solo debe contener una letra de la A a la Z.");
            return false;
        }
        if (!columna.matches("[1-9]")) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("El campo Columna solo debe contener un número del 0 al 9.");
            return false;
        }
        return true;
    }

    /**
     *     @FXML
     *     private void listarButacasPorSala(ActionEvent e) {
     *         try {
     *             String txt = txtBuscarIdSala.getText().trim();
     *             List<Butaca> lista = txt.isEmpty()
     *                     ? servicio.listarTodasButacas()
     *                     : servicio.listarButacasPorSala(Integer.parseInt(txt));
     *             butacas.setAll(lista);
     *             lblEstado.setText("Mostrando " + lista.size() + " butacas");
     *         } catch (Exception ex) {
     *             //ManejadorMetodosComunes.mostrarVentanaError(ex.getMessage());
     *
     *         }
     *     }
     * @param
     */
    @FXML
    private void listarButacasPorSala(ActionEvent e) {
        try {
            String txt = txtBuscarIdSala.getText().trim();
            List<Butaca> lista = txt.isEmpty()
                    ? servicio.listarTodasButacas()
                    : servicio.listarButacasPorSala(Integer.parseInt(txt));
            tablaButacas.getItems().setAll(lista);
            lblTotalButacas.setText("Total Butacas: " + lista.size());
            // Si tienes un label de estado, puedes actualizarlo aquí también
            // lblEstadoFooter.setText("Listo");
        } catch (Exception ex) {
            tablaButacas.getItems().clear();
            lblTotalButacas.setText("Total Butacas: 0");
            // lblEstadoFooter.setText("Error al cargar butacas");
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
            //limpiarCampos();
            ManejadorMetodosComunes.mostrarVentanaExito("Butaca creada correctamente.");
        } catch (Exception ex) {
            String msg = ex.getMessage().toLowerCase();
            if (msg.contains("ya existe")) {
                ManejadorMetodosComunes.mostrarVentanaError(ex.getMessage());
            } else if (msg.contains("capacidad")) {
                ManejadorMetodosComunes.mostrarVentanaAdvertencia(ex.getMessage());
            } else {
                ManejadorMetodosComunes.mostrarVentanaError(ex.getMessage());
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
            //limpiarCampos();
            ManejadorMetodosComunes.mostrarVentanaExito("Butaca actualizada correctamente.");
        } catch (Exception ex) {
            String msg = ex.getMessage().toLowerCase();
            if (msg.contains("ya existe")) {
                ManejadorMetodosComunes.mostrarVentanaError(ex.getMessage());
            } else if (msg.contains("capacidad")) {
                ManejadorMetodosComunes.mostrarVentanaAdvertencia(ex.getMessage());
            } else {
                ManejadorMetodosComunes.mostrarVentanaError(ex.getMessage());
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
           // limpiarCampos();
            ManejadorMetodosComunes.mostrarVentanaExito("Butaca eliminada correctamente.");
        } catch (Exception ex) {
            ManejadorMetodosComunes.mostrarVentanaError(ex.getMessage());
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

/**
 *     @FXML
 *     private void limpiarCampos() {
 *         txtFila.clear();
 *         txtColumna.clear();
 *         cmbEstado.getSelectionModel().clearSelection();
 *         cmbSala.getSelectionModel().clearSelection();
 *         tablaButacas.getSelectionModel().clearSelection();
 *     }
 */
}