module com.cinemax.empleados {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


opens com.cinemax.empleados to javafx.fxml;
opens com.cinemax.empleados.Modelo.Entidades to javafx.base;
exports com.cinemax.empleados;
opens com.cinemax.empleados.Controlador to javafx.fxml;
exports com.cinemax.empleados.Controlador;
}