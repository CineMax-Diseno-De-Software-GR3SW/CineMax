module com.cinemax.empleados {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jakarta.mail;
    requires transitive javafx.graphics;

    opens com.cinemax.empleados to javafx.fxml;
    opens com.cinemax.empleados.modelos.entidades to javafx.base;

    exports com.cinemax.empleados;

    opens com.cinemax.empleados.controladores to javafx.fxml;

    exports com.cinemax.empleados.controladores;

    requires org.apache.pdfbox;
    requires java.desktop;

    exports com.cinemax.venta_boletos.Controladores;

    opens com.cinemax.venta_boletos.Controladores to javafx.fxml;

    exports com.cinemax.venta_boletos.Servicios;
    opens com.cinemax.venta_boletos.Servicios to javafx.fxml;

    opens com.cinemax.peliculas to javafx.fxml;
    opens com.cinemax.peliculas.controladores to javafx.fxml;
    opens com.cinemax.peliculas.modelos.entidades to javafx.base;
    opens com.cinemax.comun to javafx.fxml;
    
    exports com.cinemax.peliculas;
    exports com.cinemax.peliculas.controladores;
    exports com.cinemax.peliculas.modelos.entidades;
    exports com.cinemax.comun;

    //opens com.cinemax.comun to javafx.fxml;

}