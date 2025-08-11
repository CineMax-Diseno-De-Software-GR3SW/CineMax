
package com.cinemax.salas.controladores;

import com.cinemax.utilidades.ManejadorMetodosComunes;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.EstadoButaca;
import com.cinemax.salas.modelos.entidades.Sala;
import com.cinemax.salas.servicios.ServicioButaca;
import com.cinemax.venta_boletos.controladores.ControladorAsignadorButacas;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Este controlador se encarga de mostrar visualmente las butacas de una sala
 * y permitir que los usuarios seleccionen las butacas que desean para comprar boletos.
 *
 * 1. Recibe una sala y una lista de butacas ocupadas
 * 2. Crea botones para cada butaca en formato "FilaColumna" (ej: A1, B3, C5)
 * 3. Permite hacer clic para seleccionar/deseleccionar butacas disponibles
 * 4. Mantiene una lista de las butacas que el usuario ha seleccionado
 */
public class ControladorDeConsultaSalas implements Initializable {

    /* Este GridPane organiza las butacas como una matriz (filas y columnas) */
    @FXML
    private GridPane gridButacas;

    @FXML
    private VBox vbox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private final ServicioButaca servicioButaca = new ServicioButaca();
    private List<Butaca> butacasSeleccionadas = new ArrayList<>();

    private ControladorAsignadorButacas controladorAsignadorButacas;

    /**
     * 1. Limpia la cuadrícula anterior
     * 2. Obtiene todas las butacas de la sala desde la base de datos
     * 3. Crea un botón visual para cada butaca
     * 4. Asigna colores según el estado de cada butaca
     * 5. Coloca cada botón en su posición correcta (fila/columna)
     *
     * @param codigosButacasOcupadas IDs de las butacas que YA están ocupadas para esta función específica
     * @param salaSeleccionada La sala de la cual queremos mostrar las butacas
     */
    public void mostrarButacasDeSala(Set<Integer> codigosButacasOcupadas, Sala salaSeleccionada) {
        // PASO 1: Limpiar la cuadrícula antes de mostrar las nuevas butacas
        gridButacas.getChildren().clear();

        System.out.println("=== DEBUG MOSTRAR BUTACAS ===");
        System.out.println("Sala seleccionada ID: " + salaSeleccionada.getId());
        System.out.println("Códigos butacas ocupadas: " + codigosButacasOcupadas);

        try {
            // PASO 2: Obtener todas las butacas de esta sala desde la base de datos
            List<Butaca> butacas = butacaService.listarButacasPorSala(salaSeleccionada.getId());
            System.out.println("Total butacas obtenidas de la sala: " + butacas.size());
            
            // PASO 3: Procesar cada butaca y crear su botón visual
            for (Butaca butaca : butacas) {
                // Crear botón con el texto "FilaColumna" (ej: A1, B3, C5)
                Button btn = new Button(butaca.getFila().toUpperCase() + butaca.getColumna());
                btn.setMinSize(60, 60);
                btn.setPrefSize(60, 60);
                btn.setMaxSize(60, 60);

                System.out.println("Procesando butaca: " + butaca.getFila() + butaca.getColumna() + " Estado: " + butaca.getEstado() + " ID: " + butaca.getId());
                
                // CASO 1: Si el usuario ya seleccionó esta butaca (color azul)
                if (butacasSeleccionadas.contains(butaca)) {
                    // Butaca seleccionada por el usuario actual
                    btn.setStyle("-fx-background-color: #02487B; -fx-text-fill: white;");
                    btn.setOnAction(e -> deseleccionarButaca(butaca, btn));
                    continue;
                }

                // CASO 2: Si la butaca está en la lista de ocupadas (color rojo)
                if (codigosButacasOcupadas.contains(butaca.getId())) {

                    // Si la butaca no estaba marcada como ocupada, la marcamos ahora
                    if(!butaca.getEstado().equals("OCUPADA")) { 
                        butaca.setEstado(EstadoButaca.OCUPADA.toString());
                        servicioButaca.actualizarButaca(butaca);
                    }
                    btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    btn.setDisable(true); // No se puede hacer clic
                    
                } else { 
                    // CASO 3: Determinar color según el estado de la butaca
                    switch (butaca.getEstado()) {
                        case "DISPONIBLE": 
                            btn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                            btn.setOnAction(e -> seleccionarButaca(butaca, btn));
                            break;
                        case "INHABILITADA": 
                            btn.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
                            btn.setDisable(true); // No se puede hacer clic
                            break;
                        case "OCUPADA": // Puede estar disponible (ocupada en otra función)
                            btn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                            btn.setOnAction(e -> seleccionarButaca(butaca, btn));
                            break;
                        default:
                            ManejadorMetodosComunes.mostrarVentanaError("La butaca " + butaca.getFila() + butaca.getColumna() + " tiene un estado no reconocido: " + butaca.getEstado() + "o desde la base de datos su estado es 'OCUPADA'");
                            break;
                    }
                }

                // PASO 4: Calcular posición en la cuadrícula y agregar el botón
                // Convertir fila (A,B,C...) a número (0,1,2...)
                int fila = butaca.getFila().toUpperCase().charAt(0) - 'A';
                // Convertir columna (1,2,3...) a índice (0,1,2...)
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

    /**
     * Cuando el usuario hace clic en una butaca disponible (verde):
     * 1. Agrega la butaca a la lista de seleccionadas
     * 2. Cambia color a azul para mostrar que está seleccionada
     * 3. Cambia la acción del clic para que ahora deseleccione
     * 4. Notifica al controlador asignador sobre la nueva selección
     */
    private void seleccionarButaca(Butaca butaca, Button btn) {
        if (!butacasSeleccionadas.contains(butaca)) {
            butacasSeleccionadas.add(butaca); // Agregar a la lista
            btn.setStyle("-fx-background-color: #02487B; -fx-text-fill: white;"); // Color azul
            btn.setOnAction(e -> deseleccionarButaca(butaca, btn)); // Cambiar acción a deseleccionar
            controladorAsignadorButacas.agregarButacaSeleccionada(butaca); // Notificar
        }
    }

    /**
     * Cuando el usuario hace clic en una butaca que ya había seleccionado (azul):
     * 1. Remove la butaca de la lista de seleccionadas
     * 2. Cambia color a verde para mostrar que está disponible otra vez
     * 3. Cambia la acción del clic para que ahora seleccione
     * 4. Notifica al controlador asignador sobre la deselección
     */
    private void deseleccionarButaca(Butaca butaca, Button btn) {
        if(butacasSeleccionadas.contains(butaca)) {
            butacasSeleccionadas.remove(butaca); // Remover de la lista
            btn.setStyle("-fx-background-color: green; -fx-text-fill: white;"); // Color verde
            btn.setOnAction(e -> seleccionarButaca(butaca, btn)); // Cambiar acción a seleccionar
            controladorAsignadorButacas.quitarButacaDeseleccionada(butaca); // Notificar
        }
    }

    public List<Butaca> getButacasSeleccionadas() {
        return new ArrayList<>(butacasSeleccionadas);
    }

    public void setControladorAsignadorButacas(ControladorAsignadorButacas controladorAsignadorButacas) {
        this.controladorAsignadorButacas = controladorAsignadorButacas;
    }
}