module com.cinemax.venta_boletos {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;

    opens com.cinemax.venta_boletos to javafx.fxml;

    exports com.cinemax.venta_boletos;
    exports com.cinemax.venta_boletos.Controladores.UI.VentaDeBoletos;

    opens com.cinemax.venta_boletos.Controladores.UI.VentaDeBoletos to javafx.fxml;

    exports com.cinemax.venta_boletos.Util;

    opens com.cinemax.venta_boletos.Util to javafx.fxml;

    exports com.cinemax.venta_boletos.Controladores.UI.Shared;

    opens com.cinemax.venta_boletos.Controladores.UI.Shared to javafx.fxml;
}