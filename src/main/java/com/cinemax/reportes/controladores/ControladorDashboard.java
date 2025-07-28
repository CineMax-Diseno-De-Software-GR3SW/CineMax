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

    @FXML private AreaChart<String, Number> areaChartIngresos;
    @FXML private BarChart<String, Number> barChartEntradas;
    @FXML private PieChart pieChartCategorias;
    @FXML private BarChart<String, Number> barChartTopPeliculas;

    @FXML
    public void initialize() {
        cargarGraficoIngresosMensuales();
        cargarGraficoEntradasPorMes();
        cargarGraficoCategorias();
        cargarGraficoTopPeliculas();
    }

    private void cargarGraficoIngresosMensuales() {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Ingresos ($)");
        serie.getData().add(new XYChart.Data<>("Enero", 1200));
        serie.getData().add(new XYChart.Data<>("Febrero", 1600));
        serie.getData().add(new XYChart.Data<>("Marzo", 1300));
        areaChartIngresos.getData().add(serie);
    }

    private void cargarGraficoEntradasPorMes() {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Entradas");
        serie.getData().add(new XYChart.Data<>("Enero", 300));
        serie.getData().add(new XYChart.Data<>("Febrero", 200));
        serie.getData().add(new XYChart.Data<>("Marzo", 400));
        barChartEntradas.getData().add(serie);
    }

    private void cargarGraficoCategorias() {
        pieChartCategorias.getData().add(new PieChart.Data("Taquilla", 65));
        pieChartCategorias.getData().add(new PieChart.Data("Snacks", 25));
        pieChartCategorias.getData().add(new PieChart.Data("Otros", 10));
    }

    private void cargarGraficoTopPeliculas() {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Películas más vistas");
        serie.getData().add(new XYChart.Data<>("Batman", 120));
        serie.getData().add(new XYChart.Data<>("Avatar", 110));
        serie.getData().add(new XYChart.Data<>("Oppenheimer", 95));
        serie.getData().add(new XYChart.Data<>("Spiderman", 90));
        serie.getData().add(new XYChart.Data<>("Mario Bros", 80));
        barChartTopPeliculas.getData().add(serie);
    }

    // Navegación entre pantallas
    @FXML private void onGoToReportes(ActionEvent event) {
        cambiarEscena(event, "/vistas/reportes/PantallaModuloReportesPrincipal.fxml", "Gestión de Reportes");
    }

    @FXML private void onGoToGeneracion(ActionEvent event) {
        cambiarEscena(event, "/vistas/reportes/ReporteProgramado.fxml", "Generación de Reportes");
    }

    @FXML private void goToDashboard(ActionEvent event) {
        System.out.println("Ya estás en el Dashboard.");
    }

    @FXML private void onSalir(ActionEvent event) {
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
