module com.cinemax.empleados {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.cinemax.empleados to javafx.fxml;
    opens com.cinemax.empleados.modelos.entidades to javafx.base;
    exports com.cinemax.empleados;
    opens com.cinemax.empleados.controladores to javafx.fxml;
    exports com.cinemax.empleados.controladores;

    requires org.apache.pdfbox; 

    opens com.cinemax.venta_boletos to javafx.fxml;

    exports com.cinemax.venta_boletos;
    exports com.cinemax.venta_boletos.Controladores.UI;

    opens com.cinemax.venta_boletos.Controladores.UI to javafx.fxml;

    exports com.cinemax.venta_boletos.Util;

    opens com.cinemax.venta_boletos.Util to javafx.fxml;

    exports com.cinemax.venta_boletos.Controladores.UI.Shared;

opens com.cinemax.venta_boletos.Controladores.UI.Shared to javafx.fxml;

}