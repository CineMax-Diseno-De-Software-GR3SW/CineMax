package com.cinemax.comun;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** 
 * Esta clase maneja la lógica de ventanas emergentes de alerta que pueden mostrar
 * mensajes informativos, de error o de confirmación. Las ventanas son arrastrables
 * por el usuario y contienen un título, mensaje y botón de cierre.
 *
 * @author GR3SW
 * @version 1.0
 */
public class ControladorAlertas {

    /**
     * Contenedor principal de la ventana de alerta.
     * Se utiliza como área sensible para hacer la ventana arrastrable.
     */
    @FXML private VBox alertPane;
    
    @FXML private Label titleLabel;
    
    @FXML private Label messageLabel;
    
    @FXML private Button okButton;

    /**
     * Coordenada X del punto donde el usuario presionó el mouse.
     * Se utiliza para calcular el desplazamiento al arrastrar la ventana.
     */
    private double xOffset = 0;
    
    /**
     * Coordenada Y del punto donde el usuario presionó el mouse.
     * Se utiliza para calcular el desplazamiento al arrastrar la ventana.
     */
    private double yOffset = 0;

    /**
     * Método de inicialización que se ejecuta automáticamente al cargar el FXML.
     * 
     * Configura los eventos de mouse para hacer que la ventana sea arrastrable.
     * Esto permite al usuario mover la ventana de alerta haciendo clic y arrastrando
     * sobre el área del alertPane.
     */
    @FXML
    public void initialize() {
        // Configuar evento cuando el usuario presiona el mouse sobre el panel de alerta
        alertPane.setOnMousePressed(event -> {
            // Guardar las coordenadas iniciales del clic en la escena
            // Estas coordenadas se usan como punto de referencia for el arrastre
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        
        // Configurar evento cuando el usuario arrastra el mouse sobre el panel
        alertPane.setOnMouseDragged(event -> {
            // Obtener referencia a la ventana (Stage) actual
            Stage stage = (Stage) alertPane.getScene().getWindow();
            
            // Calcular nueva posición de la ventana basada en:
            // - Posición actual del mouse en pantalla (getScreenX/Y)
            // - Menos el offset inicial donde se hizo clic
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    /**
     * Configura el contenido de la ventana de alerta.
     * 
     * @param title El título que aparecerá en la parte superior de la alerta
     * @param message El mensaje principal que se mostrará al usuario
     */
    public void setData(String title, String message) {
        // Asignar el título a la etiqueta correspondiente
        titleLabel.setText(title);
        // Asignar el mensaje a la etiqueta correspondiente
        messageLabel.setText(message);
    }

    @FXML
    private void onOkAction() {
        // Obtener referencia a la ventana actual a través del botón
        Stage stage = (Stage) okButton.getScene().getWindow();
        // Cerrar la ventana de alerta
        stage.close();
    }
}
