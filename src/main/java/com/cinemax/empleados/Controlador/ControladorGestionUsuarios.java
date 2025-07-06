package com.cinemax.empleados.Controlador;

import com.cinemax.empleados.Servicios.ServicioRoles;
import com.cinemax.empleados.Servicios.GestorSesionSingleton;
import com.cinemax.empleados.Modelo.Entidades.Usuario;
import com.cinemax.empleados.Modelo.Entidades.*;


import com.cinemax.empleados.Servicios.ServicioUsuarios;
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

import java.io.IOException;
import java.util.List;

import javafx.util.StringConverter;

public class ControladorGestionUsuarios {

    public Button btnAgregarUsuario;
    public Button btnBack;
    public Label lblNombreUsuario;
    public Label lblRolUsuario;
    @FXML private TableView<Usuario> tableUsuarios;
    @FXML private TableColumn<Usuario, Boolean> colActivo;
    @FXML private TableColumn<Usuario, Long> colUsuario;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, Rol> colRol;
//    @FXML private TableColumn<Usuario, Void> colEditar;

    private ObservableList<Rol> rolesObservable;      // lista para el combo

    private ServicioUsuarios servicioUsuarios;

    private ServicioRoles servicioRoles;
    private GestorSesionSingleton gestorSesion;


    @FXML
    public void initialize() {
        // Configurar columnas…
        servicioUsuarios = new ServicioUsuarios();
        servicioRoles = new ServicioRoles();


        gestorSesion = GestorSesionSingleton.getInstancia();
        Usuario use = gestorSesion.getUsuarioActivo();
        lblNombreUsuario.setText(use.getNombreCompleto());
        lblRolUsuario.setText(use.getDescripcionRol());
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("nombreRol"));


        // Columna de botones de edición
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

        // Cargar datos…
        try {
            // 1.  Usuario logeado (lo obtienes de tu singleton de sesión)
            Usuario usuarioActual = gestorSesion.getUsuarioActivo();

            // 2.  Filtras la lista que viene de la BD
            List<Usuario> soloOtros = servicioUsuarios.listarUsuarios()
                    .stream()
                    .filter(u -> !u.getId().equals(usuarioActual.getId())) // ≠ usuario conectado
                    .toList();                                             // Java 16+; o collect(Collectors.toList())

            // 3.  Cargas la tabla con la lista filtrada
            tableUsuarios.setItems(FXCollections.observableArrayList(soloOtros));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        colActivo.setCellFactory(tc -> new TableCell<>() {

            private final ToggleButton toggle = new ToggleButton();

            {
                // ‑‑‑ estilos opcionales
                toggle.getStyleClass().add("switch");   // pon tu estilo en CSS
                toggle.setMinWidth(70);

                // Cuando el usuario haga clic, actualiza el modelo y persiste
                toggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    Usuario u = getTableRow().getItem();
                    if (u != null) {
                        u.setActivo(newVal);            // actualiza el POJO

                        // ⇣  Si manejas BD o servicio, persiste aquí
                        try {
                            servicioUsuarios.actualizarEstado(u.getId(), newVal);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // Texto opcional
                    toggle.setText(newVal ? "ON  " : "  OFF");
                });
            }

            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);

                if (empty || activo == null) {
                    setGraphic(null);
                } else {
                    toggle.setSelected(activo);
                    toggle.setText(activo ? "ON  " : "  OFF");
                    setGraphic(toggle);
                }
            }
        });

        /* ----- 1. cargar roles una sola vez ----- */
        try {
            rolesObservable = FXCollections.observableArrayList(servicioRoles.listarRoles());
        } catch (Exception e) {
            e.printStackTrace();
            rolesObservable = FXCollections.observableArrayList();
        }

        /* ----- 2. value factory: muestra el rol actual ----- */
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        /* ----- 3. cell factory: ComboBox editable ----- */
        colRol.setCellFactory(col -> {
            ComboBoxTableCell<Usuario, Rol> cell =
                    new ComboBoxTableCell<>(new StringConverter<>() {
                        @Override public String toString(Rol r)   { return r == null ? "" : r.getNombre(); }
                        @Override public Rol fromString(String s) { return rolesObservable.stream()
                                .filter(r -> r.getNombre().equals(s))
                                .findFirst().orElse(null); }
                    }, rolesObservable);


//            /* al confirmar la edición */
            colRol.setOnEditCommit(evt -> {
                Usuario u = evt.getRowValue();
                Rol nuevo   = evt.getNewValue();
                if (nuevo != null && !nuevo.equals(u.getRol())) {
                    u.setRol(nuevo);                         // 1) actualiza modelo
                    try {
                        servicioUsuarios.actualizarRolUsuario(u.getId(), nuevo); // 2) guarda en BD
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    tableUsuarios.refresh();                 // refresca por si hay renderizado
                }
            });
            return cell;
        });
        tableUsuarios.setEditable(true);  // imprescindible para ComboBoxTableCell
    }


    private void editarUsuario(Usuario u) {
        // abrir diálogo / escena de edición
    }

    public void onBackAction(ActionEvent actionEvent) {
    }

    public void onAgregarUsuario(ActionEvent actionEvent) {
    }

    @FXML
    private void onCerrarSesion(ActionEvent event) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vista/empleados/PantallaLogin.fxml"));
        try {
            Parent root = loader.load();

            // Obtener el Stage actual desde el botón o cualquier nodo
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Portal del Administrador");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        // Ejemplo de cerrar ventana actual (si fuera necesario)
        // Stage stage = (Stage) txtBienvenida.getScene().getWindow();
        // stage.close();


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
