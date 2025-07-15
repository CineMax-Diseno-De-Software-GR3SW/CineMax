module com.cinemax.peliculas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    opens com.cinemax.peliculas to javafx.fxml;
    opens com.cinemax.peliculas.controladores to javafx.fxml;
    opens com.cinemax.peliculas.modelos.entidades to javafx.base;
    
    exports com.cinemax.peliculas;
    exports com.cinemax.peliculas.controladores;
    exports com.cinemax.peliculas.modelos.entidades;
}