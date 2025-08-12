package com.cinemax.empleados.controladores;

import java.io.IOException;
import java.util.List;

import com.cinemax.empleados.modelos.entidades.Rol;
import com.cinemax.empleados.modelos.entidades.Usuario;
import com.cinemax.empleados.servicios.ServicioRoles;
import com.cinemax.empleados.servicios.ServicioSesionSingleton;
import com.cinemax.empleados.servicios.ServicioUsuarios;
import com.cinemax.utilidades.ManejadorMetodosComunes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;


import java.util.HashMap;
import java.util.Map;

public class ControladorGestionUsuarios {

    public Button btnAgregarUsuario;
    public Button btnBack;
    public Label lblNombreUsuario;
    public Label lblRolUsuario;
    public Button btnActualizar;

    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Label lblTotalUsuarios;

    // Tabla y columnas
    @FXML
    private TableView<Usuario> tableUsuarios;
    @FXML
    private TableColumn<Usuario, Boolean> colActivo;
    @FXML
    private TableColumn<Usuario, Long> colUsuario;
    @FXML
    private TableColumn<Usuario, String> colNombre;
    @FXML
    private TableColumn<Usuario, String> colEmail;
    @FXML
    private TableColumn<Usuario, Rol> colRol;
    @FXML
    private TableColumn<Usuario, String> colCedula; // Nueva columna
    @FXML
    private TableColumn<Usuario, String> colCelular; // Nueva columna
    // @FXML private TableColumn<Usuario, Void> colEditar;

    private ObservableList<Rol> rolesObservable; // lista para el combo
    private ObservableList<Usuario> listaUsuariosCompleta; // lista completa para búsqueda

    private ServicioUsuarios servicioUsuarios;

    private ServicioRoles servicioRoles;
    private ServicioSesionSingleton gestorSesion;

    // Mapa para acumular cambios pendientes
    private Map<Long, Usuario> cambiosPendientes = new HashMap<>();

    @FXML
    public void initialize() {
        // Configurar columnas…
        servicioUsuarios = new ServicioUsuarios();
        servicioRoles = new ServicioRoles();
        tableUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Deshabilitar el botón Eliminar por defecto
        btnEliminar.setDisable(true);
        btnActualizar.setDisable(true); // Deshabilitar botón guardar cambios inicialmente
        // Habilitar solo cuando hay selección
        tableUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            btnEliminar.setDisable(newSel == null);
        });

        gestorSesion = ServicioSesionSingleton.getInstancia();
        Usuario use = gestorSesion.getUsuarioActivo();
        lblNombreUsuario.setText(use.getNombreCompleto());
        lblRolUsuario.setText(use.getDescripcionRol());
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula")); // Nueva columna
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colCelular.setCellValueFactory(new PropertyValueFactory<>("celular")); // Nueva columna
        colRol.setCellValueFactory(new PropertyValueFactory<>("nombreRol"));

        // Columna de botones de edición
        // colEditar.setCellFactory(tc -> new TableCell<>() {
        // private final Button btn = new Button("✎");
        // {
        // btn.getStyleClass().add("icon-button");
        // btn.setOnAction(e ->
        // editarUsuario(getTableView().getItems().get(getIndex())));
        // }
        // @Override protected void updateItem(Void itm, boolean empty) {
        // super.updateItem(itm, empty);
        // setGraphic(empty ? null : btn);
        // }
        // });

        // Cargar datos…
        // Carga asíncrona para evitar bloqueo UI
        new Thread(() -> {
            try {
                Usuario usuarioActual = gestorSesion.getUsuarioActivo();

                List<Usuario> soloOtros = servicioUsuarios.listarUsuarios()
                        .stream()
                        .filter(u -> !u.getId().equals(usuarioActual.getId()))
                        .toList();

                listaUsuariosCompleta = FXCollections.observableArrayList(soloOtros);

                // Actualizar UI en hilo JavaFX
                javafx.application.Platform.runLater(() -> {
                    tableUsuarios.setItems(listaUsuariosCompleta);
                    actualizarContadorUsuarios();
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

        // Modificar cell factory para acumular cambios en lugar de actualizar inmediatamente
        colActivo.setCellFactory(tc -> new TableCell<>() {
            private ToggleButton toggle;

            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);

                if (empty || activo == null) {
                    setGraphic(null);
                    return;
                }

                if (toggle == null) {
                    toggle = new ToggleButton();
                    toggle.getStyleClass().add("switch");
                    toggle.setMinWidth(70);

                    toggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        Usuario u = getTableRow().getItem();
                        if (u != null) {
                                if (oldVal != newVal) {  // Aquí agregas la comparación
                                    if (u.isActivo() != newVal) {
                                        u.setActivo(newVal);
                                        cambiosPendientes.put(u.getId(), u);
                                        btnActualizar.setDisable(false);
                                    }
                                    toggle.setText(newVal ? "ON  " : "  OFF");
                                }
                        }
//                        if (u != null) {
//                            u.setActivo(newVal);
//                            toggle.setText(newVal ? "ON  " : "  OFF");
//
//                            // Guardar cambio pendiente
//                            cambiosPendientes.put(u.getId(), u);
//                            btnActualizar.setDisable(false);
//                        }
                    });
                }

                toggle.setSelected(activo);
                toggle.setText(activo ? "ON  " : "  OFF");
                setGraphic(toggle);
            }
        });

        try {
            rolesObservable = FXCollections.observableArrayList(servicioRoles.listarRoles());
        } catch (Exception e) {
            e.printStackTrace();
            rolesObservable = FXCollections.observableArrayList();
        }

        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        colRol.setCellFactory(col -> {
            ComboBoxTableCell<Usuario, Rol> cell = new ComboBoxTableCell<>(new StringConverter<>() {
                @Override
                public String toString(Rol r) {
                    return r == null ? "" : r.getNombre();
                }

                @Override
                public Rol fromString(String s) {
                    return rolesObservable.stream()
                            .filter(r -> r.getNombre().equals(s))
                            .findFirst().orElse(null);
                }
            }, rolesObservable);

            colRol.setOnEditCommit(evt -> {
                Usuario u = evt.getRowValue();
                Rol nuevo = evt.getNewValue();
                if (nuevo != null && !nuevo.equals(u.getRol())) {
                    u.setRol(nuevo);
                    cambiosPendientes.put(u.getId(), u);
                    btnActualizar.setDisable(false);
                }
            });
            return cell;
        });
        tableUsuarios.setEditable(true);
    }

    @FXML
    private void actualizarUsuarios() {
        for (Usuario u : cambiosPendientes.values()) {
            try {
                servicioUsuarios.actualizarEstado(u.getId(), u.isActivo());
                servicioUsuarios.actualizarRolUsuario(u.getId(), u.getRol());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cambiosPendientes.clear();
        btnActualizar.setDisable(true);
        ManejadorMetodosComunes.mostrarVentanaExito("Cambios guardados correctamente.");
    }



    // Método para actualizar el contador de usuarios
    private void actualizarContadorUsuarios() {
        int totalUsuarios = tableUsuarios.getItems().size();
        lblTotalUsuarios.setText("Total de usuarios: " + totalUsuarios);
    }

    private void editarUsuario(Usuario u) {
        // abrir diálogo / escena de edición
    }

    // Método para buscar usuarios por cédula o rol
    @FXML
    private void onBuscar() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();

        if (textoBusqueda.isEmpty()) {
            // Si no hay texto, mostrar todos
            tableUsuarios.setItems(listaUsuariosCompleta);
        } else {
            // Filtrar por cédula o rol
            ObservableList<Usuario> usuariosFiltrados = listaUsuariosCompleta.stream()
                    .filter(usuario -> (usuario.getCedula() != null
                            && usuario.getCedula().toLowerCase().contains(textoBusqueda)) ||
                            (usuario.getNombreRol() != null
                                    && usuario.getNombreRol().toLowerCase().contains(textoBusqueda)))
                    .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);

            tableUsuarios.setItems(usuariosFiltrados);
        }

        // Actualizar contador después de filtrar
        actualizarContadorUsuarios();
    }

    // Método para limpiar la búsqueda
    @FXML
    private void onLimpiar() {
        txtBuscar.clear();
        tableUsuarios.setItems(listaUsuariosCompleta);
        actualizarContadorUsuarios();
    }

    // Método para eliminar usuario seleccionado
    @FXML
    private void eliminarUsuario() {
        Usuario usuarioSeleccionado = tableUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Por favor, selecciona un usuario para eliminar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/comun/VistaConfirmacion.fxml"));
            Parent root = loader.load();
            com.cinemax.utilidades.ControladorConfirmacion controlador = loader.getController();
            controlador.setTitulo("Confirmar Eliminación");
            controlador.setMensaje("¿Estás seguro de que deseas eliminar este usuario?\n\nUsuario: "
                    + usuarioSeleccionado.getNombreCompleto() + "\n\nEsta acción no se puede deshacer.");

            Stage stage = new Stage();
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.setTitle("Confirmar Eliminación");
            stage.initOwner(tableUsuarios.getScene().getWindow());
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controlador.isConfirmado()) {
                // Eliminar de la base de datos
                servicioUsuarios.eliminarUsuario(usuarioSeleccionado.getId());
                // Remover de las listas locales
                listaUsuariosCompleta.remove(usuarioSeleccionado);
                tableUsuarios.getItems().remove(usuarioSeleccionado);
                // Actualizar contador
                actualizarContadorUsuarios();
                ManejadorMetodosComunes.mostrarVentanaExito("Usuario eliminado correctamente.");
            }
        } catch (IOException e) {
            ManejadorMetodosComunes
                    .mostrarVentanaError("Error al mostrar la ventana de confirmación: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error al eliminar el usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onBackAction() {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnBack.getScene().getWindow(),
                "/vistas/empleados/PantallaPortalPrincipal.fxml");
    }

    public void onAgregarUsuario() {
        ManejadorMetodosComunes.cambiarVentana((Stage) btnAgregarUsuario.getScene().getWindow(),
                "/vistas/empleados/PantallaRegistrarUsuario.fxml");
    }

}