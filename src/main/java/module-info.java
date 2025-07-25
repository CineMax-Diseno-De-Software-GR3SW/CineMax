module com.cinemax.peliculas {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;
    
    opens com.cinemax.peliculas to javafx.fxml;
    opens com.cinemax.peliculas.controladores to javafx.fxml;
    opens com.cinemax.peliculas.modelos.entidades to javafx.base;
    opens com.cinemax.comun to javafx.fxml;
    
    exports com.cinemax.peliculas;
    exports com.cinemax.peliculas.controladores;
    exports com.cinemax.peliculas.modelos.entidades;
    exports com.cinemax.comun;
}