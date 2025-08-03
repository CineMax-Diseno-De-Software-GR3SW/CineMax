package com.cinemax.salas.controladores;

import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.EstadoButaca;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.servicios.ButacaService;
import com.cinemax.salas.servicios.SalaService;
import com.cinemax.venta_boletos.Controladores.ControladorAsignadorButacas;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class ControladorDeConsultaSalas implements Initializable {

    @FXML
    private GridPane gridButacas;
    
    @FXML
    private VBox vbox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar componentes de la vista
    }

    private final ButacaService butacaService = new ButacaService();
    private List<Butaca> butacasSeleccionadas = new ArrayList<>();

    private ControladorAsignadorButacas controladorAsignadorButacas;

    public void mostrarButacasDeSala(Set<Integer> codigosButacasOcupadas, Sala salaSeleccionada) {
        gridButacas.getChildren().clear();
                
        try {
            List<Butaca> butacas = butacaService.listarButacasPorSala(salaSeleccionada.getId());
            for (Butaca butaca : butacas) {
                Button btn = new Button(butaca.getFila().toUpperCase() + butaca.getColumna());
                btn.setMinSize(60, 60);
                btn.setPrefSize(60, 60);
                btn.setMaxSize(60, 60);

                //System.out.println("Procesando butaca: " + butaca.getFila() + butaca.getColumna() + " Estado: " + butaca.getEstado() + " ID: " + butaca.getId());
                
                if (butacasSeleccionadas.contains(butaca)) {
                    // Butaca seleccionada por el usuario actual
                    btn.setStyle("-fx-background-color: #02487B; -fx-text-fill: white;");
                    btn.setOnAction(e -> deseleccionarButaca(butaca, btn));
                    continue;
                }

                if (codigosButacasOcupadas.contains(butaca.getId()) && !EstadoButaca.OCUPADA.toString().equals(butaca.getEstado())) {
                    butaca.setEstado(EstadoButaca.OCUPADA.toString());
                }

                switch (butaca.getEstado()) {
                    case "DISPONIBLE":
                        btn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                        btn.setOnAction(e -> seleccionarButaca(butaca, btn));
                        break;
                    case "OCUPADA":
                        btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                        btn.setDisable(true);
                        break;
                    case "INHABILITADA":
                        btn.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
                        btn.setDisable(true);
                        break;
                    default:
                        break;
                }

                int fila = butaca.getFila().toUpperCase().charAt(0) - 'A';
                int columna = Integer.parseInt(butaca.getColumna()) - 1;
                gridButacas.add(btn, columna, fila);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seleccionarButaca(Butaca butaca, Button btn) {
        if (!butacasSeleccionadas.contains(butaca)) {
            butacasSeleccionadas.add(butaca);
            btn.setStyle("-fx-background-color: #02487B; -fx-text-fill: white;");
            btn.setOnAction(e -> deseleccionarButaca(butaca, btn));
            controladorAsignadorButacas.agregarButacaSeleccionada(butaca);
        }
    }

    private void deseleccionarButaca(Butaca butaca, Button btn) {
        if(butacasSeleccionadas.contains(butaca)) {
            butacasSeleccionadas.remove(butaca);
            btn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
            btn.setOnAction(e -> seleccionarButaca(butaca, btn));
            controladorAsignadorButacas.quitarButacaDeseleccionada(butaca);
        }
    }

    public List<Butaca> getButacasSeleccionadas() {
        return new ArrayList<>(butacasSeleccionadas);
    }

    public void setControladorAsignadorButacas(ControladorAsignadorButacas controladorAsignadorButacas) {
        this.controladorAsignadorButacas = controladorAsignadorButacas;
    }
}