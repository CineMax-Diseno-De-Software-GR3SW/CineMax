module com.cinemax {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires jakarta.mail;

    exports com.cinemax to javafx.graphics;
    opens com.cinemax to javafx.fxml;

    opens com.cinemax.empleados.modelos.entidades to javafx.base;
    exports com.cinemax.empleados.modelos.entidades;
    opens com.cinemax.empleados.controladores to javafx.fxml;
    exports com.cinemax.empleados.controladores;

//
    exports com.cinemax.comun to javafx.graphics;
    opens com.cinemax.comun to javafx.fxml;

    opens com.cinemax.salas.controladores to javafx.fxml;
    exports com.cinemax.salas.controladores;
    opens com.cinemax.salas.modelos.entidades to javafx.base;
    exports com.cinemax.salas.modelos.entidades;


}