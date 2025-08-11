package com.cinemax.salas.controladores;
import com.cinemax.utilidades.*;
import com.cinemax.utilidades.ManejadorMetodosComunes;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.EstadoButaca;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.servicios.ButacaService;
import com.cinemax.salas.servicios.SalaService;
import com.cinemax.utilidades.ManejadorMetodosComunes;
import com.cinemax.venta_boletos.controladores.ControladorAsignadorButacas;

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
        
        System.out.println("=== DEBUG MOSTRAR BUTACAS ===");
        System.out.println("Sala seleccionada ID: " + salaSeleccionada.getId());
        System.out.println("Códigos butacas ocupadas: " + codigosButacasOcupadas);
                
        try {
            List<Butaca> butacas = butacaService.listarButacasPorSala(salaSeleccionada.getId());
            System.out.println("Total butacas obtenidas de la sala: " + butacas.size());
            
            for (Butaca butaca : butacas) {
                Button btn = new Button(butaca.getFila().toUpperCase() + butaca.getColumna());
                btn.setMinSize(60, 60);
                btn.setPrefSize(60, 60);
                btn.setMaxSize(60, 60);

                System.out.println("Procesando butaca: " + butaca.getFila() + butaca.getColumna() + " Estado: " + butaca.getEstado() + " ID: " + butaca.getId());
                
                // Si selecciona una butaca que ya escogió, significa que la quiere deseleccionar
                if (butacasSeleccionadas.contains(butaca)) {
                    // Butaca seleccionada por el usuario actual
                    btn.setStyle("-fx-background-color: #02487B; -fx-text-fill: white;");
                    btn.setOnAction(e -> deseleccionarButaca(butaca, btn));
                    continue;
                }

                // Si la butaca se encuentra en la lista de butacas ocupadas de esta función, se la muestra como ocupada
                if (codigosButacasOcupadas.contains(butaca.getId())) {

                    if(!butaca.getEstado().equals("OCUPADA")) { // Si la butaca no fue marcada como ocupada, se la marca aquí
                        butaca.setEstado(EstadoButaca.OCUPADA.toString());
                        butacaService.actualizarButaca(butaca);
                    }
                    btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    btn.setDisable(true);
                    
                } else { 
                    switch (butaca.getEstado()) {
                        case "DISPONIBLE": // Si la butaca nunca fue seleccionada para una venta de boletos
                            btn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                            btn.setOnAction(e -> seleccionarButaca(butaca, btn));
                            break;
                        case "INHABILITADA": // Las butacas inhabilitadas se encuentra siempre inhabilitadas sin importar la funcion
                            btn.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
                            btn.setDisable(true);
                            break;
                        case "OCUPADA": // Si la butaca no está en la lista de butacas ocupadas, puede que esté disponible, inhabilitada o puede que sea una butaca ocupada de otra función, por lo que se la debe marcar como disponible
                            btn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                            btn.setOnAction(e -> seleccionarButaca(butaca, btn));
                            break;
                        default:
                            ManejadorMetodosComunes.mostrarVentanaError("La butaca " + butaca.getFila() + butaca.getColumna() + " tiene un estado no reconocido: " + butaca.getEstado() + "o desde la base de datos su estado es 'OCUPADA'");
                            break;
                    }
                }

                int fila = butaca.getFila().toUpperCase().charAt(0) - 'A';
                int columna = Integer.parseInt(butaca.getColumna()) - 1;
                System.out.println("Agregando butaca " + butaca.getFila() + butaca.getColumna() + " en posición grid (" + columna + "," + fila + ")");
                gridButacas.add(btn, columna, fila);
            }
            System.out.println("Butacas procesadas correctamente. Total agregadas al grid: " + butacas.size());
        } catch (Exception e) {
            System.err.println("Error al mostrar butacas: " + e.getMessage());
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