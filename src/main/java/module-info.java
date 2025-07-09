module com.cinemax.empleados {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;

    // Exportar paquetes principales
    exports com.cinemax.empleados;
    exports com.cinemax.empleados.controlador;
    exports com.cinemax.peliculas.controladores;
    exports com.cinemax.comun.vistas;

    // Abrir paquetes para JavaFX FXML
    opens com.cinemax.empleados to javafx.fxml;
    opens com.cinemax.empleados.controlador to javafx.fxml;
    opens com.cinemax.peliculas.controladores to javafx.fxml;
    opens com.cinemax.comun.vistas to javafx.fxml;
    
    // Abrir entidades para JavaFX Base (reflection)
    opens com.cinemax.empleados.modelo.Entidades to javafx.base;
    opens com.cinemax.peliculas.modelos.entidades to javafx.base;
}