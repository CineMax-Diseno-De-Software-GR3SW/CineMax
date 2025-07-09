package com.cinemax.empleados.controlador;

import com.cinemax.empleados.modelo.entidades.Rol;
import com.cinemax.empleados.modelo.entidades.Usuario;
import com.cinemax.empleados.servicios.ServicioRoles;
import com.cinemax.empleados.servicios.ServicioSesionSingleton;
import com.cinemax.empleados.servicios.ServicioUsuarios;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;

public class ControladorGestionUsuarios {

    @FXML private Button btnAgregarUsuario;
    @FXML private Button btnBack;
    @FXML private Label lblNombreUsuario;
    @FXML private Label lblRolUsuario;

    @FXML private TableView<Usuario> tableUsuarios;
    @FXML private TableColumn<Usuario, Boolean> colActivo;
    @FXML private TableColumn<Usuario, Long> colUsuario;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, Rol> colRol;

    private ObservableList<Rol> rolesObservable;
    private ServicioUsuarios servicioUsuarios;
    private ServicioRoles servicioRoles;
    private ServicioSesionSingleton gestorSesion;

    @FXML
    public void initialize() {
        servicioUsuarios = new ServicioUsuarios();
        servicioRoles = new ServicioRoles();
        gestorSesion = ServicioSesionSingleton.getInstancia();

        Usuario usuarioActual = gestorSesion.getUsuarioActivo();
        lblNombreUsuario.setText(usuarioActual.getNombreCompleto());
        lblRolUsuario.setText(usuarioActual.getDescripcionRol());

        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        configurarColumnaActivo();
        configurarColumnaRol();

        cargarUsuarios();
    }

    private void configurarColumnaActivo() {
        colActivo.setCellFactory(tc -> new TableCell<>() {
            private final ToggleButton toggle = new ToggleButton();

            {
                toggle.getStyleClass().add("switch");
                toggle.setMinWidth(70);
                toggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    Usuario u = getTableRow().getItem();
                    if (u != null) {
                        u.setActivo(newVal);
                        try {
                            servicioUsuarios.actualizarEstado(u.getId(), newVal);
                        } catch (Exception e) {
                            mostrarError("Error al actualizar estado", e.getMessage());
                        }
                    }
                    toggle.setText(newVal ? "ON" : "OFF");
                });
            }

            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty || activo == null) {
                    setGraphic(null);
                } else {
                    toggle.setSelected(activo);
                    toggle.setText(activo ? "ON" : "OFF");
                    setGraphic(toggle);
                }
            }
        });
    }

    private void configurarColumnaRol() {
        try {
            rolesObservable = FXCollections.observableArrayList(servicioRoles.listarRoles());
        } catch (Exception e) {
            rolesObservable = FXCollections.observableArrayList();
            mostrarError("Error al cargar roles", e.getMessage());
        }

        colRol.setCellFactory(col -> {
            ComboBoxTableCell<Usuario, Rol> cell = new ComboBoxTableCell<>(
                    new StringConverter<>() {
                        @Override public String toString(Rol r) { return r == null ? "" : r.getNombre(); }
                        @Override public Rol fromString(String s) {
                            return rolesObservable.stream()
                                    .filter(r -> r.getNombre().equals(s))
                                    .findFirst().orElse(null);
                        }
                    },
                    rolesObservable
            );

            colRol.setOnEditCommit(evt -> {
                Usuario u = evt.getRowValue();
                Rol nuevo = evt.getNewValue();
                if (nuevo != null && !nuevo.equals(u.getRol())) {
                    u.setRol(nuevo);
                    try {
                        servicioUsuarios.actualizarRolUsuario(u.getId(), nuevo);
                        tableUsuarios.refresh();
                    } catch (Exception e) {
                        mostrarError("Error al actualizar rol", e.getMessage());
                    }
                }
            });

            return cell;
        });

        tableUsuarios.setEditable(true);
    }

    private void cargarUsuarios() {
        try {
            Usuario actual = gestorSesion.getUsuarioActivo();
            List<Usuario> otrosUsuarios = servicioUsuarios.listarUsuarios().stream()
                    .filter(u -> !u.getId().equals(actual.getId()))
                    .toList();
            tableUsuarios.setItems(FXCollections.observableArrayList(otrosUsuarios));
        } catch (Exception e) {
            mostrarError("Error al cargar usuarios", e.getMessage());
        }
    }

    @FXML
    public void onBackAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vista/empleados/PantallaPortalPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarError("Error al volver al portal principal", e.getMessage());
        }
    }

    @FXML
    public void onAgregarUsuario(ActionEvent event) {
        // Aquí iría un formulario emergente o diálogo para ingresar datos de nuevo usuario
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nuevo Usuario");
        dialog.setHeaderText("Ingrese los datos del nuevo usuario (formato: usuario,correo,nombre,cedula,celular,rol_id):");
        dialog.setContentText("Datos:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                String[] datos = input.split(",");
                if (datos.length != 6) {
                    throw new IllegalArgumentException("Debe ingresar exactamente 6 valores.");
                }

                String nombreUsuario = datos[0].trim();
                String correo = datos[1].trim();
                String nombreCompleto = datos[2].trim();
                String cedula = datos[3].trim();
                String celular = datos[4].trim();
                Long idRol = Long.parseLong(datos[5].trim());

                Rol rol = rolesObservable.stream()
                        .filter(r -> r.getId().equals(idRol))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado."));

                Usuario nuevo = servicioUsuarios.crearUsuario(nombreUsuario, correo, nombreCompleto, cedula, celular, rol);
                servicioUsuarios.crearUsuario(nuevo); // persistir

                tableUsuarios.getItems().add(nuevo);
                mostrarInfo("Usuario creado", "Contraseña temporal: " + nuevo.getClave());

            } catch (Exception ex) {
                mostrarError("Error al crear usuario", ex.getMessage());
            }
        });
    }

    @FXML
    private void onCerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vista/empleados/PantallaLogin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Portal del Administrador");
            stage.show();
        } catch (IOException e) {
            mostrarError("Error al cerrar sesión", e.getMessage());
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

//
//package com.cinemax.empleados.Controlador;
//
//import com.cinemax.empleados.Servicios.GestorSesionSingleton;
//import com.cinemax.empleados.Modelo.Entidades.Usuario;
//import com.cinemax.empleados.Modelo.Entidades.*;
//
//
//import com.cinemax.empleados.Servicios.GestorUsuarios;
//import javafx.collections.FXCollections;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.concurrent.ForkJoinPool;
//
//import javafx.fxml.FXML;
//
//public class ControladorGestionUsuarios {
//
//    public Button btnAgregarUsuario;
//    public Button btnBack;
//    public Label lblNombreUsuario;
//    public Label lblRolUsuario;
//    @FXML private TableView<Usuario> tableUsuarios;
//    @FXML private TableColumn<Usuario, Boolean> colActivo;
//    @FXML private TableColumn<Usuario, Long> colUsuario;
//    @FXML private TableColumn<Usuario, String> colNombre;
//    @FXML private TableColumn<Usuario, String> colEmail;
//    @FXML private TableColumn<Usuario, String> colRol;
//    @FXML private TableColumn<Usuario, Void> colEditar;
//    private GestorUsuarios gestorUsuarios;
//
//
//    @FXML
//    public void initialize() {
//        // Configurar columnas…
//        gestorUsuarios = new GestorUsuarios();
//
//        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
//        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
//        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
//        colEmail.setCellValueFactory(new PropertyValueFactory<>("correo"));
//        colRol.setCellValueFactory(new PropertyValueFactory<>("nombreRol"));
//
//
//        // Columna de botones de edición
//        colEditar.setCellFactory(tc -> new TableCell<>() {
//            private final Button btn = new Button("✎");
//            {
//                btn.getStyleClass().add("icon-button");
//                btn.setOnAction(e -> editarUsuario(getTableView().getItems().get(getIndex())));
//            }
//            @Override protected void updateItem(Void itm, boolean empty) {
//                super.updateItem(itm, empty);
//                setGraphic(empty ? null : btn);
//            }
//        });
//
//        // Cargar datos…
//        try {
//            tableUsuarios.setItems(FXCollections.observableArrayList(gestorUsuarios.listarUsuarios()));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        colActivo.setCellFactory(tc -> new TableCell<>() {
//
//            private final ToggleButton toggle = new ToggleButton();
//
//            {
//                // ‑‑‑ estilos opcionales
//                toggle.getStyleClass().add("switch");   // pon tu estilo en CSS
//                toggle.setMinWidth(70);
//
//                // Cuando el usuario haga clic, actualiza el modelo y persiste
//                toggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
//                    Usuario u = getTableRow().getItem();
//                    if (u != null) {
//                        u.setActivo(newVal);            // actualiza el POJO
//
//                        // ⇣  Si manejas BD o servicio, persiste aquí
//                        try {
//                            gestorUsuarios.actualizarEstado(u.getId(), newVal);
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                    // Texto opcional
//                    toggle.setText(newVal ? "ON  " : "  OFF");
//                });
//            }
//
//            @Override
//            protected void updateItem(Boolean activo, boolean empty) {
//                super.updateItem(activo, empty);
//
//                if (empty || activo == null) {
//                    setGraphic(null);
//                } else {
//                    toggle.setSelected(activo);
//                    toggle.setText(activo ? "ON  " : "  OFF");
//                    setGraphic(toggle);
//                }
//            }
//        });
//    }
//
//    private void editarUsuario(Usuario u) {
//        // abrir diálogo / escena de edición
//    }
//
//    public void onBackAction(ActionEvent actionEvent) {
//    }
//
//    public void onAgregarUsuario(ActionEvent actionEvent) {
//    }
//
//    @FXML
//    private void onCerrarSesion(ActionEvent event) {
//
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vista/PantallaLogin.fxml"));
//        try {
//            Parent root = loader.load();
//
//            // Obtener el Stage actual desde el botón o cualquier nodo
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//            stage.setTitle("Portal del Administrador");
//            stage.setScene(new Scene(root));
//            stage.show();
//
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//        // Ejemplo de cerrar ventana actual (si fuera necesario)
//        // Stage stage = (Stage) txtBienvenida.getScene().getWindow();
//        // stage.close();
//
//
//    }
//
//}
