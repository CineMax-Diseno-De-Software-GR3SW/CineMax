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
        exports com.cinemax.empleados.modelos.entidades;

        // Paquetes del módulo peliculas
        opens com.cinemax.peliculas to javafx.fxml;
        opens com.cinemax.peliculas.controladores to javafx.fxml;
        opens com.cinemax.peliculas.modelos.entidades to javafx.base;
        exports com.cinemax.peliculas;
        exports com.cinemax.peliculas.controladores;
        exports com.cinemax.peliculas.modelos.entidades;

        // Paquetes comunes
        opens com.cinemax.comun to javafx.fxml;
        exports com.cinemax.comun to javafx.graphics;

        // Paquetes del módulo salas
        opens com.cinemax.salas.controladores to javafx.fxml;
        opens com.cinemax.salas.modelos.entidades to javafx.base;
        exports com.cinemax.salas.controladores;
        exports com.cinemax.salas.modelos.entidades;

        // Exportación general
        exports com.cinemax to javafx.graphics;
        opens com.cinemax to javafx.fxml;

        requires org.apache.pdfbox; 

        // Paquetes del módulo venta-boletos
        exports com.cinemax.venta_boletos.Controladores;
        opens com.cinemax.venta_boletos.Controladores to javafx.fxml;
        opens com.cinemax.venta_boletos.Servicios to javafx.base, javafx.fxml;
    }