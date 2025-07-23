package com.cinemax.salas.controladores;

import com.cinemax.salas.modelos.entidades.*;
import com.cinemax.salas.servicios.ButacaService;
import com.cinemax.salas.servicios.SalaService;
import com.cinemax.salas.modelos.entidades.SalaFactory;
import com.cinemax.salas.modelos.entidades.SalaNormalFactory;
import com.cinemax.salas.modelos.entidades.SalaVIPFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ControladorSalas {
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Integer> cmbCapacidad;       // ← cambiado
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
        // 1) Poblar capacidades fijas
        System.out.println("cmbCapacidad = " + cmbCapacidad);
        System.out.println("txtNombre    = " + txtNombre);
// …etc…

        cmbCapacidad.setItems(FXCollections.observableArrayList(36, 42, 48));
        cmbCapacidad.getSelectionModel().selectFirst();

        // 2) Poblar tipo y estado
        cmbTipo.setItems(FXCollections.observableArrayList(TipoSala.values()));
        cmbEstado.setItems(FXCollections.observableArrayList(EstadoSala.values()));

        // 3) Configurar columnas de la tabla
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
        listarTodasSalas();  // usa tu wrapper para no modificar cargarSalas()

        // 4) Cuando seleccionas una fila, llenas el formulario
        tablaSalas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtNombre.setText(newSel.getNombre());
                cmbCapacidad.setValue(newSel.getCapacidad()); // ← ahora ComboBox
                cmbTipo.setValue(newSel.getTipo());
                cmbEstado.setValue(newSel.getEstado());
            }
        });
    }

    // Este es tu método original, sin cambiar
    private void cargarSalas() throws Exception {
        salas.setAll(salaService.listarSalas());
    }

    // Wrapper anotado para FXML
    @FXML
    private void listarTodasSalas() {
        try {
            cargarSalas();
            lblEstado.setText("Salas cargadas exitosamente.");
        } catch (Exception e) {
            mostrarAviso("Error al cargar salas",
                    "Hubo un error inesperado cargando las salas: " + e.getMessage());
            lblEstado.setText("Error cargando salas.");
        }
    }

    // Muestra mensajes de error
    private void mostrarAviso(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 15px;");
        alert.showAndWait();
    }

    // Muestra mensajes de información
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.getDialogPane().setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 15px;");
        alert.showAndWait();
    }

    @FXML
    private void crearSala() {
        try {
            SalaFactory factory = (cmbTipo.getValue() == TipoSala.VIP)
                    ? new SalaVIPFactory()
                    : new SalaNormalFactory();

            Sala sala = factory.crearSala(
                    0,
                    txtNombre.getText(),
                    cmbCapacidad.getValue(),
                    cmbEstado.getValue()
            );

            salaService.crearSala(sala);      // persiste y genera butacas la primera vez

            listarTodasSalas();
            limpiarCampos();
            mostrarInfo("Operación Exitosa",
                    "Sala creada exitosamente.\nButacas creadas exitosamente.");

        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg.contains("Ya existe una sala con ese nombre")) {
                // En lugar de fallar, regeneramos butacas para la sala existente
                try {
                    // Buscamos la sala por nombre en la lista actual
                    Sala existente = salaService.listarSalas().stream()
                            .filter(s -> s.getNombre().equalsIgnoreCase(txtNombre.getText().trim()))
                            .findFirst()
                            .orElseThrow(() -> new Exception("No se encontró la sala existente"));

                    butacaService.generarButacasAutomatica(existente.getId());

                    listarTodasSalas();
                    limpiarCampos();
                    mostrarInfo("Butacas Generadas",
                            "Butacas creadas exitosamente para la sala existente \"" +
                                    existente.getNombre() + "\".");

                } catch (Exception ex2) {
                    mostrarAviso("Error al regenerar butacas",
                            "No se pudo generar butacas: " + ex2.getMessage());
                }
            } else if (e instanceof NumberFormatException) {
                mostrarAviso("Datos inválidos", "La capacidad debe ser un número válido.");
            } else {
                mostrarAviso("Error inesperado en crearSala",
                        "Hubo un error inesperado en crearSala: " + msg);
            }
        }
    }

    @FXML
    private void actualizarSala() {
        Sala seleccionada = tablaSalas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            try {
                seleccionada.setNombre(txtNombre.getText());
                seleccionada.setCapacidad(cmbCapacidad.getValue());  // ← ComboBox
                seleccionada.setTipo(cmbTipo.getValue());
                seleccionada.setEstado(cmbEstado.getValue());

                salaService.actualizarSala(seleccionada);
                listarTodasSalas();
                limpiarCampos();

                mostrarInfo("Operación Exitosa", "Sala actualizada correctamente.");
            } catch (Exception e) {
                if (e instanceof NumberFormatException) {
                    mostrarAviso("Datos inválidos", "La capacidad debe ser un número válido.");
                } else {
                    mostrarAviso("Error inesperado en actualizarSala",
                            "Hubo un error inesperado en actualizarSala: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void eliminarSala() {
        Sala seleccionada = tablaSalas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            try {
                salaService.eliminarSala(seleccionada.getId());
                listarTodasSalas();
                limpiarCampos();
                mostrarInfo("Operación Exitosa", "Sala eliminada correctamente.");
            } catch (Exception e) {
                mostrarAviso("Error inesperado en eliminarSala",
                        "Hubo un error inesperado en eliminarSala: " + e.getMessage());
            }
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
            } else {
                salas.clear();
                lblEstado.setText("No existe sala con ID " + id);
            }
        } catch (NumberFormatException e) {
            salas.clear();
            lblEstado.setText("ID inválido.");
        } catch (Exception e) {
            mostrarAviso("Error en buscarSalaPorId",
                    "Hubo un error inesperado: " + e.getMessage());
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
