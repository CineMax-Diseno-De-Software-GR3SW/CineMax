module com.cinemax {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires jakarta.mail;
    requires org.apache.pdfbox;

    //paquetes del módulo común
    exports com.cinemax.comun;
    opens com.cinemax.comun to javafx.fxml;

    // Paquetes del módulo empleados
    opens com.cinemax.empleados.modelos.entidades to javafx.base;
    opens com.cinemax.empleados.controladores to javafx.fxml;

    exports com.cinemax.empleados.controladores;
    exports com.cinemax.empleados.modelos.entidades;

    // Paquetes del módulo peliculas
    opens com.cinemax.peliculas.controladores to javafx.fxml;
    opens com.cinemax.peliculas.modelos.entidades to javafx.base;

    exports com.cinemax.peliculas.controladores;
    exports com.cinemax.peliculas.modelos.entidades;

    // Paquetes del módulo salas
    opens com.cinemax.salas.controladores to javafx.fxml;
    opens com.cinemax.salas.modelos.entidades to javafx.base;

    exports com.cinemax.salas.controladores;
    exports com.cinemax.salas.modelos.entidades;

    // Exportación general
    exports com.cinemax to javafx.graphics;

    opens com.cinemax to javafx.fxml;

    // Paquetes del módulo venta-boletos
    exports com.cinemax.venta_boletos.Controladores;
    opens com.cinemax.venta_boletos.Controladores to javafx.fxml;


    exports com.cinemax.venta_boletos.Modelos.entidades;

    opens com.cinemax.venta_boletos.Modelos.entidades to javafx.base;
    opens com.cinemax.venta_boletos.Servicios to javafx.base, javafx.fxml;

    // Paquetes del módulo reportes
    opens com.cinemax.reportes.modelos to javafx.base;

    exports com.cinemax.reportes.controladores to javafx.fxml;

    opens com.cinemax.reportes.controladores to javafx.fxml;

}