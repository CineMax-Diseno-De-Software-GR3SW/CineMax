module com.cinemax.empleados {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Dependencies iText for PDF generation
    requires org.apache.pdfbox;


opens com.cinemax.empleados to javafx.fxml;
opens com.cinemax.empleados.modelos.entidades to javafx.base;
exports com.cinemax.empleados;
opens com.cinemax.empleados.controladores to javafx.fxml;
exports com.cinemax.empleados.controladores;
opens com.cinemax.reportes.modelos to javafx.base;
exports com.cinemax.reportes.controladores to javafx.fxml;
opens com.cinemax.reportes.controladores to javafx.fxml;
}