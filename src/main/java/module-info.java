module com.cinemax {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires jakarta.mail;

    // Paquetes del módulo empleados
    opens com.cinemax.empleados to javafx.fxml;
    opens com.cinemax.empleados.modelos.entidades to javafx.base;
    opens com.cinemax.empleados.controladores to javafx.fxml;
    exports com.cinemax.empleados;
    exports com.cinemax.empleados.controladores;

    // Paquetes del módulo peliculas
    opens com.cinemax.peliculas to javafx.fxml;
    opens com.cinemax.peliculas.controladores to javafx.fxml;
    opens com.cinemax.peliculas.modelos.entidades to javafx.base;
    opens com.cinemax.comun to javafx.fxml;
    exports com.cinemax.peliculas;
    exports com.cinemax.peliculas.controladores;
    exports com.cinemax.peliculas.modelos.entidades;
    exports com.cinemax.comun;
}