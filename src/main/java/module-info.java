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



    exports com.cinemax.venta_boletos.Controladores;
    opens com.cinemax.venta_boletos.Controladores to javafx.fxml;

//    exports com.cinemax.venta_boletos;

//    opens com.cinemax.venta_boletos.Util to javafx.fxml;
    exports com.cinemax.comun;
    opens com.cinemax.comun to javafx.fxml;


}