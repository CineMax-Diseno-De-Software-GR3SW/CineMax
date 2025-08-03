package com.cinemax.salas.controladores;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.salas.modelos.entidades.*;
import com.cinemax.salas.servicios.ButacaService;
import com.cinemax.salas.servicios.SalaService;
import com.cinemax.salas.modelos.entidades.SalaFactory;
import com.cinemax.salas.modelos.entidades.SalaNormalFactory;
import com.cinemax.salas.modelos.entidades.SalaVIPFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class ControladorSalas {
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Integer> cmbCapacidad;
    @FXML private ComboBox<TipoSala> cmbTipo;
    @FXML private ComboBox<EstadoSala> cmbEstado;
    @FXML private TableView<Sala> tablaSalas;
    @FXML private TableColumn<Sala, Integer> colId;
    @FXML private TableColumn<Sala, String> colNombre;
    @FXML private TableColumn<Sala, Integer> colCapacidad;
    @FXML private TableColumn<Sala, String> colTipo;
    @FXML private TableColumn<Sala, String> colEstado;
    @FXML private Label lblEstado;

    private final SalaService salaService = new SalaService();
    private final ObservableList<Sala> salas = FXCollections.observableArrayList();
    private final ButacaService butacaService = new ButacaService();

    @FXML
    public void initialize() throws Exception {
        cmbCapacidad.setItems(FXCollections.observableArrayList(36, 42, 48));
        cmbCapacidad.getSelectionModel().selectFirst();

        cmbTipo.setItems(FXCollections.observableArrayList(TipoSala.values()));
        cmbEstado.setItems(FXCollections.observableArrayList(EstadoSala.values()));

        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        colCapacidad.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCapacidad()).asObject());
        colTipo.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTipo().name()));
        colEstado.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEstado().name()));

        tablaSalas.setItems(salas);
        listarTodasSalas();

        tablaSalas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtNombre.setText(newSel.getNombre());
                cmbCapacidad.setValue(newSel.getCapacidad());
                cmbTipo.setValue(newSel.getTipo());
                cmbEstado.setValue(newSel.getEstado());
            }
        });
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

    private void cargarSalas() throws Exception {
        salas.setAll(salaService.listarSalas());
    }

    @FXML
    private void listarTodasSalas() {
        try {
            cargarSalas();
            lblEstado.setText("Salas cargadas exitosamente.");
            ManejadorMetodosComunes.mostrarVentanaExito("Salas cargadas exitosamente.");
        } catch (Exception e) {
            lblEstado.setText("Error cargando salas.");
            ManejadorMetodosComunes.mostrarVentanaError("Hubo un error inesperado cargando las salas: " + e.getMessage());
        }
    }

    @FXML
    private void crearSala() {
        try {
            String nombreSala = txtNombre.getText().trim();
            // Validación: solo letras y espacios
            if (!nombreSala.matches("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s]+")) {
                ManejadorMetodosComunes.mostrarVentanaAdvertencia("El nombre de la sala solo puede contener letras y espacios.");
                return;
            }
            boolean existe = salaService.listarSalas().stream()
                    .anyMatch(s -> s.getNombre().equalsIgnoreCase(nombreSala));
            if (existe) {
                ManejadorMetodosComunes.mostrarVentanaAdvertencia("Ya existe una sala con el nombre \"" + nombreSala + "\". Por favor elige otro nombre.");
                return;
            }

            SalaFactory factory = (cmbTipo.getValue() == TipoSala.VIP)
                    ? new SalaVIPFactory()
                    : new SalaNormalFactory();

            Sala sala = factory.crearSala(
                    0,
                    nombreSala,
                    cmbCapacidad.getValue(),
                    cmbEstado.getValue()
            );

            salaService.crearSala(sala);

            listarTodasSalas();
            limpiarCampos();
            ManejadorMetodosComunes.mostrarVentanaExito("Sala creada exitosamente.\nButacas creadas exitosamente.");

        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("Ya existe una sala con ese nombre")) {
                try {
                    Sala existente = salaService.listarSalas().stream()
                            .filter(s -> s.getNombre().equalsIgnoreCase(txtNombre.getText().trim()))
                            .findFirst()
                            .orElseThrow(() -> new Exception("No se encontró la sala existente"));

                    butacaService.generarButacasAutomatica(existente.getId());

                    listarTodasSalas();
                    limpiarCampos();
                    ManejadorMetodosComunes.mostrarVentanaExito("Butacas creadas exitosamente para la sala existente \"" +
                            existente.getNombre() + "\".");

                } catch (Exception ex2) {
                    ManejadorMetodosComunes.mostrarVentanaError("No se pudo generar butacas: " + ex2.getMessage());
                }
            } else if (e instanceof NumberFormatException) {
                ManejadorMetodosComunes.mostrarVentanaAdvertencia("La capacidad debe ser un número válido.");
            } else {
                ManejadorMetodosComunes.mostrarVentanaError("" + msg);
            }
        }
    }

    @FXML
    private void actualizarSala() {
        Sala seleccionada = tablaSalas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            try {
                String nombreSala = txtNombre.getText().trim();
                // Validación: solo letras y espacios
                if (!nombreSala.matches("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s]+")) {
                    ManejadorMetodosComunes.mostrarVentanaAdvertencia("El nombre de la sala solo puede contener letras y espacios.");
                    return;
                }
                // Validación de nombre duplicado (excluyendo la sala seleccionada)
                boolean existe = salaService.listarSalas().stream()
                        .anyMatch(s -> s.getNombre().equalsIgnoreCase(nombreSala) && s.getId() != seleccionada.getId());
                if (existe) {
                    ManejadorMetodosComunes.mostrarVentanaAdvertencia("Ya existe una sala con el nombre \"" + nombreSala + "\". Por favor elige otro nombre.");
                    return;
                }

                seleccionada.setNombre(nombreSala);
                seleccionada.setCapacidad(cmbCapacidad.getValue());
                seleccionada.setTipo(cmbTipo.getValue());
                seleccionada.setEstado(cmbEstado.getValue());

                salaService.actualizarSala(seleccionada);
                listarTodasSalas();
                limpiarCampos();

                ManejadorMetodosComunes.mostrarVentanaExito("Sala actualizada correctamente.");
            } catch (Exception e) {
                if (e instanceof NumberFormatException) {
                    ManejadorMetodosComunes.mostrarVentanaAdvertencia("La capacidad debe ser un número válido.");
                } else {
                    ManejadorMetodosComunes.mostrarVentanaError("" + e.getMessage());
                }
            }
        } else {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Selecciona una sala para actualizar.");
        }
    }

    @FXML
    private void eliminarSala() {
        Sala seleccionada = tablaSalas.getSelectionModel().getSelectedItem();
        if (txtNombre.getText().trim().isEmpty()) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("El campo 'Nombre de sala' está vacío. Selecciona una sala válida.");
            return;
        }
        if (seleccionada != null) {
            try {
                salaService.eliminarSala(seleccionada.getId());
                listarTodasSalas();
                limpiarCampos();
                ManejadorMetodosComunes.mostrarVentanaExito("Sala eliminada correctamente.");
            } catch (Exception e) {
                ManejadorMetodosComunes.mostrarVentanaError("Error inesperado en eliminarSala: " + e.getMessage());
            }
        } else {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Selecciona una sala para eliminar.");
        }
    }

    @FXML private TextField txtBuscarId;

    @FXML
    private void buscarSalaPorId() {
        String idText = txtBuscarId.getText().trim();
        if (idText.isEmpty()) {
            listarTodasSalas();
            return;
        }
        try {
            int id = Integer.parseInt(idText);
            Sala sala = salaService.obtenerSalaPorId(id);
            if (sala != null) {
                salas.setAll(sala);
                lblEstado.setText("Sala encontrada.");
                ManejadorMetodosComunes.mostrarVentanaExito("Sala encontrada.");
            } else {
                salas.clear();
                lblEstado.setText("No existe sala con ID " + id);
                ManejadorMetodosComunes.mostrarVentanaAdvertencia("No existe sala con ID " + id);
            }
        } catch (NumberFormatException e) {
            salas.clear();
            lblEstado.setText("ID inválido.");
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("El ID debe ser un número válido.");
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error en buscarSalaPorId: " + e.getMessage());
        }
    }

    @FXML
    private void limpiarCampos() {
        txtNombre.clear();
        cmbCapacidad.getSelectionModel().selectFirst();
        cmbTipo.getSelectionModel().clearSelection();
        cmbEstado.getSelectionModel().clearSelection();
        tablaSalas.getSelectionModel().clearSelection();
        lblEstado.setText("Listo");
    }
}