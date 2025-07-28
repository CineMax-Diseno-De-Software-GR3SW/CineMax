package com.cinemax.reportes.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;

import java.io.IOException;

public class ControladorDashboard {

    @FXML private AreaChart<String, Number> areaChart;
    @FXML private BarChart<String, Number> barChart;
    @FXML private PieChart pieChart;

    @FXML
    public void initialize() {
        // Gráfico de barras
        XYChart.Series<String, Number> barData = new XYChart.Series<>();
        barData.setName("Entradas");
        barData.getData().add(new XYChart.Data<>("Enero", 300));
        barData.getData().add(new XYChart.Data<>("Febrero", 200));
        barData.getData().add(new XYChart.Data<>("Marzo", 400));
        barChart.getData().add(barData);

        // Gráfico de área
        XYChart.Series<String, Number> areaData = new XYChart.Series<>();
        areaData.setName("Ventas");
        areaData.getData().add(new XYChart.Data<>("Enero", 1200));
        areaData.getData().add(new XYChart.Data<>("Febrero", 1600));
        areaData.getData().add(new XYChart.Data<>("Marzo", 1300));
        areaChart.getData().add(areaData);

        // Gráfico de pastel
        pieChart.getData().add(new PieChart.Data("Taquilla", 60));
        pieChart.getData().add(new PieChart.Data("Snacks", 30));
        pieChart.getData().add(new PieChart.Data("Otros", 10));
    }

    @FXML
    private void onGoToReportes(ActionEvent event) {
        cambiarEscena(event, "/vistas/reportes/PantallaModuloReportesPrincipal.fxml", "Gestión de Reportes");
    }

    @FXML
    private void onGoToGeneracion(ActionEvent event) {
        cambiarEscena(event, "/vistas/reportes/ReporteProgramado.fxml", "Generación de Reportes");
    }

    @FXML
    private void goToDashboard(ActionEvent event) {
        // Ya estás en el dashboard. Puedes mostrar un mensaje o no hacer nada.
        System.out.println("Ya estás en el Dashboard.");
    }

    @FXML
    private void onSalir(ActionEvent event) {
        cambiarEscena(event, "/vistas/PantallaPortalPrincipal.fxml", "Portal Principal");
    }

    private void cambiarEscena(ActionEvent event, String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
