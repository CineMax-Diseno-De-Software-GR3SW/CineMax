package com.cinemax.salas.controladores;

import com.cinemax.salas.modelos.entidades.Butaca;
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

public class ControladorDeConsultaSalas implements Initializable {

    @FXML
    private GridPane gridButacas;
    
    @FXML
    private VBox vbox;
    //@FXML
    //private Label lblNombreSala;

    private final ButacaService butacaService = new ButacaService();
    private List<Butaca> butacasSeleccionadas = new ArrayList<>();
    private List<Integer> butacasOcupadas = new ArrayList<>();
    private ControladorAsignadorButacas controladorAsignadorButacas = new ControladorAsignadorButacas();



    private Button btnContinuar = null;
    private Butaca butacaSeleccionada = null;
    private Sala salaSeleccionada = null;

    public ControladorDeConsultaSalas() {
        // Constructor vacío para inyección de dependencias
        salaSeleccionada = new Sala();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //try {
        //    List<Sala> salas = salaService.listarSalas();
        //    comboSalas.setItems(FXCollections.observableArrayList(salas));
        //    comboSalas.setOnAction(e -> mostrarButacasDeSala());
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }

    public void setButacasOcupadas(List<Integer> butacasOcupadas) {
        this.butacasOcupadas = butacasOcupadas;
        System.out.println("Total de butacas ocupadas: " + butacasOcupadas.size());
    }

    public void setSala(Sala sala){
        salaSeleccionada = sala;
        if (sala != null) {
            mostrarButacasDeSala();
        }
    }

    private void mostrarButacasDeSala() {
        gridButacas.getChildren().clear();
        //removeBtnContinuar();
        //Sala salaSeleccionada = this.salaSeleccionada;
        if (salaSeleccionada == null) return;
        //Podemos quitar "Sala: "  por que al momento de  visualizar aparece como "Sala: Sala:Sala1"
        //lblNombreSala.setText(salaSeleccionada.getNombre());
        try {
            List<Butaca> butacas = butacaService.listarButacasPorSala(salaSeleccionada.getId());
            for (Butaca butaca : butacas) {
                Button btn = new Button(butaca.getFila().toUpperCase() + butaca.getColumna());
                btn.setMinSize(60, 60);
                btn.setPrefSize(60, 60);
                btn.setMaxSize(60, 60);

                System.out.println("Procesando butaca: " + butaca.getFila() + butaca.getColumna() + " Estado: " + butaca.getEstado() + " ID: " + butaca.getId());

                // Verificar el estado de la butaca
                if (butacasOcupadas.contains(butaca.getId())) {
                    // Butaca ocupada por boletos existentes
                    btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    btn.setDisable(true);
                } else if (butacasSeleccionadas.contains(butaca)) {
                    // Butaca seleccionada por el usuario actual
                    btn.setStyle("-fx-background-color: #02487B; -fx-text-fill: white;");
                    btn.setOnAction(e -> deseleccionarButaca(butaca, btn));
                } else {
                    // Butaca disponible
                    btn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                    btn.setOnAction(e -> seleccionarButaca(butaca, btn));
                }

                int fila = butaca.getFila().toUpperCase().charAt(0) - 'A';
                int columna = Integer.parseInt(butaca.getColumna()) - 1;
                gridButacas.add(btn, columna, fila);
            }
            // System.out.println("Total de butacas ocupadas :D : " + butacasOcupadas.size() + " - Butaca actual: " + butacasOcupadas.get(0).getFila() + butacasOcupadas.get(0).getColumna() + " - Estado: " + butacasOcupadas.get(0).getEstado());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //private void showBtnContinuar() {
    //    removeBtnContinuar();
    //    btnContinuar = new Button("Continuar");
    //    btnContinuar.setOnAction(e -> actualizarButacaSeleccionada());
    //    vbox.getChildren().add(btnContinuar);
    //}

    private void removeBtnContinuar() {
        if (btnContinuar != null) {
            vbox.getChildren().remove(btnContinuar);
            btnContinuar = null;
        }
    }

    private void actualizarButacaSeleccionada() {
        if (butacaSeleccionada == null) return;
        butacaSeleccionada.setEstado("OCUPADA");
        try {
            butacaService.actualizarButaca(butacaSeleccionada);
            removeBtnContinuar();
            mostrarDialogoExito();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarDialogoExito() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("¡Éxito!");

        VBox dialogVBox = new VBox(15);
        dialogVBox.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Label label = new Label("¡Éxito! Butaca Actualizada exitosamente");
        Button btnEntendido = new Button("Entendido");
        btnEntendido.setOnAction(e -> {
            dialog.close();
            mostrarButacasDeSala();
        });

        dialogVBox.getChildren().addAll(label, btnEntendido);
        Scene dialogScene = new Scene(dialogVBox, 320, 120);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private void seleccionarButaca(Butaca butaca, Button btn) {
        if (!butacasSeleccionadas.contains(butaca)) {
            butacasSeleccionadas.add(butaca);
            btn.setStyle("-fx-background-color: #02487B; -fx-text-fill: white;");
            btn.setOnAction(e -> deseleccionarButaca(butaca, btn));
            
            // Notificar al controlador padre
            if (controladorAsignadorButacas != null) {
                controladorAsignadorButacas.agregarButacaSeleccionada(butaca);
            }
        }
    }

    private void deseleccionarButaca(Butaca butaca, Button btn) {
        butacasSeleccionadas.remove(butaca);
        btn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        btn.setOnAction(e -> seleccionarButaca(butaca, btn));
        
        // Notificar al controlador padre
        if (controladorAsignadorButacas != null) {
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