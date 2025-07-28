module com.cinemax.empleados {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires jakarta.mail;


    opens com.cinemax.empleados to javafx.fxml;
opens com.cinemax.empleados.modelos.entidades to javafx.base;
exports com.cinemax.empleados;
opens com.cinemax.empleados.controladores to javafx.fxml;
exports com.cinemax.empleados.controladores;
}