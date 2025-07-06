module com.cinemax.moduloboletos {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;

    opens com.cinemax.moduloboletos to javafx.fxml;

    exports com.cinemax.moduloboletos;
    exports com.cinemax.moduloboletos.Controladores.UI.VentaDeBoletos;

    opens com.cinemax.moduloboletos.Controladores.UI.VentaDeBoletos to javafx.fxml;

    exports com.cinemax.moduloboletos.Util;

    opens com.cinemax.moduloboletos.Util to javafx.fxml;

    exports com.cinemax.moduloboletos.Controladores.UI.Shared;

    opens com.cinemax.moduloboletos.Controladores.UI.Shared to javafx.fxml;
}