package com.cinemax.venta_boletos.controladores;

import javafx.scene.control.Label;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.venta_boletos.modelos.entidades.CalculadorIVA;
import com.cinemax.venta_boletos.modelos.entidades.Producto;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ControladorInformacionLateral {

    @FXML
    private VBox boletosContainer;

    @FXML
    private VBox funcionInfoContainer;

    @FXML
    private ImageView imagenPelicula;

    @FXML
    private Label labelFechaFuncion;

    @FXML
    private Label labelFormato;

    @FXML
    private Label labelGeneroPelicula;

    @FXML
    private Label labelHoraFuncion;

    @FXML
    private Label labelLugarSala;

    @FXML
    private Label labelNombrePelicula;

    @FXML
    private Label labelTipoEstreno;

    @FXML
    private VBox listaBoletos;

    @FXML
    private ScrollPane scrollBoletos;

    @FXML
    private Label totalLabel;

    @FXML
    private Label labelTotalTexto;

    @FXML
    private HBox HBoxImpuesto;

    @FXML
    private HBox HBoxSubtotal;

    @FXML
    private Label impuestoLabel;

    @FXML
    private Label subtotalLabel;


    // ---- Atributos ---
    private int cantidad;
    private Parent root; // Atributo para mantener referencia a la vista


    public ControladorInformacionLateral() {
        this.cantidad = 1;
    }

    public void mostrarSoloPrecio() {
        HBoxImpuesto.setVisible(false);
        HBoxSubtotal.setVisible(false);
        labelTotalTexto.setVisible(false);
    }

    public void mostrarTodaLaInformacionDePago() {
        HBoxImpuesto.setVisible(true);
        HBoxSubtotal.setVisible(true);
        labelTotalTexto.setVisible(true);
        
        subtotalLabel.setText((totalLabel.getText()));
        impuestoLabel.setText(String.format("%.2f", Double.parseDouble(totalLabel.getText()) * CalculadorIVA.getIVA_TASA()));    
    }


    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        this.root = root;
    }

    public void colocarInformacionFuncion(Funcion funcionSeleccionada) {
        labelNombrePelicula.setText(funcionSeleccionada.getPelicula() != null ? funcionSeleccionada.getPelicula().getTitulo() : "Título no disponible");
        labelGeneroPelicula.setText(funcionSeleccionada.getPelicula() != null && funcionSeleccionada.getPelicula().getGenero() != null ? funcionSeleccionada.getPelicula().getGenero() : "Género no disponible");
        labelFormato.setText(funcionSeleccionada.getFormato() != null ? funcionSeleccionada.getFormato().name().replace("_", " ") : "Formato no disponible");
        labelTipoEstreno.setText(funcionSeleccionada.getTipoEstreno() != null ? funcionSeleccionada.getTipoEstreno().name().replace("_", " ").toLowerCase() : "Tipo de estreno no disponible");
        labelLugarSala.setText(funcionSeleccionada.getSala() != null ? funcionSeleccionada.getSala().getNombre() + " - " + funcionSeleccionada.getSala().getTipo().toString().toLowerCase() : "Sala no disponible");
        labelFechaFuncion.setText(funcionSeleccionada.getFechaHoraInicio() != null ? funcionSeleccionada.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Fecha no disponible");
        labelHoraFuncion.setText(funcionSeleccionada.getFechaHoraInicio() != null ? funcionSeleccionada.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")) : "Hora no disponible");
        imagenPelicula.setImage(new Image(funcionSeleccionada.getPelicula() != null && funcionSeleccionada.getPelicula().getImagenUrl() != null ? funcionSeleccionada.getPelicula().getImagenUrl() : "/images/no-image.png"));
    }

    public void calcularSubtotal(List<Butaca> butacasSeleccionadas, Funcion funcion) {

        double multiplicadorTipoDeSala = funcion.getSala().getTipo().getMultiplicador();
        double multiplicadorFormatoFuncion = funcion.getFormato().getMultiplicadorPrecio().doubleValue();
        double multiplicadorTipoFuncion = funcion.getTipoEstreno().getMultiplicadorPrecio().doubleValue();
        double multiplicadorHorario = funcion.getDiaSemana().getPrecio().doubleValue();

        double subtotal = butacasSeleccionadas.size() * multiplicadorTipoDeSala * multiplicadorFormatoFuncion * multiplicadorTipoFuncion * multiplicadorHorario;
        totalLabel.setText(String.format("%.2f", subtotal));
    }

    public void calcularTotal(List<Producto> boletos) {
        double total = Double.parseDouble(subtotalLabel.getText()) + Double.parseDouble(impuestoLabel.getText());
        totalLabel.setText(String.format("%.2f", total));
    }

    // Método para mostrar la butaca seleccionada en la lista de boletos
    public void mostrarButacaSeleccionada(Butaca butaca) {
        

        // Crear el contenedor principal como un rectángulo
        HBox butacaItem = new HBox();
        butacaItem.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        butacaItem.setSpacing(0); // Eliminar spacing uniforme para control personalizado
        butacaItem.setPrefHeight(40);
        butacaItem.setStyle(
            "-fx-background-color: #e8f4f8; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #b3d9e6; " +
            "-fx-border-radius: 8; " +
            "-fx-border-width: 1; " +
            "-fx-padding: 8 12 8 12;"
        );
        
        // Label de cantidad
        Label labelCantidad = new Label(String.valueOf(cantidad++));
        labelCantidad.setStyle(
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-text-fill: #2c5aa0; " +
            "-fx-min-width: 30px; " +
            "-fx-max-width: 30px; " +
            "-fx-pref-width: 30px; " +
            "-fx-alignment: center;"
        );
        
        // Espaciado pequeño entre número y separador
        Region spacerPequeno = new Region();
        spacerPequeno.setPrefWidth(2);
        
        // Separador visual
        Region spacer = new Region();
        spacer.setPrefWidth(5);
        spacer.setStyle("-fx-background-color: #b3d9e6; -fx-pref-height: 20px; -fx-max-width: 1px;");
        
        // Espaciado grande entre separador y código alfanumérico
        Region spacerGrande = new Region();
        spacerGrande.setPrefWidth(15);

        // Label con el código alfanumérico en la parte izquierda
        Label labelButaca = new Label(butaca.getFila().toUpperCase() + butaca.getColumna());
        labelButaca.setStyle(
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-text-fill: #2c5aa0; " +
            "-fx-min-width: 50px;"
        );

        // Agregar todos los elementos al contenedor con espaciado personalizado
        butacaItem.getChildren().addAll(labelCantidad, spacerPequeno, spacer, spacerGrande, labelButaca);
        
        // Asignar un ID único para poder encontrar y eliminar el elemento después
        butacaItem.setId("butaca-" + butaca.getId());
        
        // Agregar al contenedor de boletos
        listaBoletos.getChildren().add(butacaItem);
    }

    public void removerButacaDeLista(Butaca butaca) {
        // Remover el elemento con el ID correspondiente
        listaBoletos.getChildren().removeIf(node ->
            ("butaca-" + butaca.getId()).equals(node.getId()));
        cantidad--;
        
        // Renumerar todos los elementos restantes
        renumerarButacas();
    }
    
    // Método para renumerar todas las butacas en la lista
    private void renumerarButacas() {
        int numeroActual = 1;
        
        for (javafx.scene.Node node : listaBoletos.getChildren()) {
            if (node instanceof HBox) {
                HBox butacaItem = (HBox) node;
                
                // Buscar el primer Label (que es el de cantidad)
                for (javafx.scene.Node child : butacaItem.getChildren()) {
                    if (child instanceof Label) {
                        Label labelCantidad = (Label) child;
                        labelCantidad.setText(String.valueOf(numeroActual));
                        numeroActual++;
                        break; // Solo actualizar el primer Label encontrado
                    }
                }
            }
        }
    }

}
