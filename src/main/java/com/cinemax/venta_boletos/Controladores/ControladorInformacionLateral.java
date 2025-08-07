package com.cinemax.venta_boletos.Controladores;

import javafx.scene.control.Label;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.venta_boletos.Modelos.entidades.CalculadorIVA;
import com.cinemax.venta_boletos.Modelos.entidades.Producto;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Controlador para el panel lateral de información en el proceso de venta de boletos.
 * 
 * Este componente maneja la visualización de:
 * - Información detallada de la función cinematográfica
 * - Lista dinámica de butacas seleccionadas
 * - Cálculos de precios (subtotal, impuestos, total)
 * - Imagen promocional de la película
 * 
 * Funcionalidades principales:
 * - Mostrar/ocultar elementos de precio según el contexto
 * - Gestión visual de butacas seleccionadas con numeración automática
 * - Cálculo dinámico de precios con múltiples multiplicadores
 * - Renumeración automática al remover butacas
 * 
 * Se utiliza tanto en la selección de butacas como en la facturación,
 * adaptando su visualización según las necesidades de cada pantalla.
 * 
 * @author GR3SW
 * @version 1.0
 */
public class ControladorInformacionLateral {

    // ===== ELEMENTOS DE LA INTERFAZ (FXML) =====
    
    /** Contenedor principal donde se muestran los boletos seleccionados */
    @FXML
    private VBox boletosContainer;

    /** Contenedor de la información detallada de la función */
    @FXML
    private VBox funcionInfoContainer;

    /** Imagen promocional de la película */
    @FXML
    private ImageView imagenPelicula;

    /** Etiqueta que muestra la fecha de la función */
    @FXML
    private Label labelFechaFuncion;

    /** Etiqueta que muestra el formato de proyección (2D, 3D, IMAX, etc.) */
    @FXML
    private Label labelFormato;

    /** Etiqueta que muestra el género de la película */
    @FXML
    private Label labelGeneroPelicula;

    /** Etiqueta que muestra la hora de inicio de la función */
    @FXML
    private Label labelHoraFuncion;

    /** Etiqueta que muestra la sala y su tipo */
    @FXML
    private Label labelLugarSala;

    /** Etiqueta que muestra el título de la película */
    @FXML
    private Label labelNombrePelicula;

    /** Etiqueta que muestra el tipo de estreno */
    @FXML
    private Label labelTipoEstreno;

    /** Lista scrolleable de butacas seleccionadas */
    @FXML
    private VBox listaBoletos;

    /** Panel de scroll para la lista de boletos */
    @FXML
    private ScrollPane scrollBoletos;

    /** Etiqueta que muestra el total final */
    @FXML
    private Label totalLabel;

    /** Etiqueta de texto "Total" */
    @FXML
    private Label labelTotalTexto;

    /** Contenedor del desglose de impuestos */
    @FXML
    private HBox HBoxImpuesto;

    /** Contenedor del desglose de subtotal */
    @FXML
    private HBox HBoxSubtotal;

    /** Etiqueta que muestra el valor de los impuestos */
    @FXML
    private Label impuestoLabel;

    /** Etiqueta que muestra el subtotal antes de impuestos */
    @FXML
    private Label subtotalLabel;

    // ===== ATRIBUTOS DE LÓGICA =====
    
    /** Contador para numerar las butacas seleccionadas secuencialmente */
    private int cantidad;
    
    /** Referencia a la vista raíz para acceso externo si es necesario */
    private Parent root;


    /**
     * Constructor que inicializa el contador de butacas.
     * 
     * Establece el contador en 1 para comenzar la numeración
     * de butacas seleccionadas desde el número 1.
     */
    public ControladorInformacionLateral() {
        this.cantidad = 1;
    }

    /**
     * Configura la vista para mostrar solo el precio total.
     * 
     * Oculta los elementos de desglose (subtotal e impuestos) para
     * mostrar una vista simplificada durante la selección de butacas.
     * Útil cuando solo se necesita ver el precio final sin detalles.
     */
    public void mostrarSoloPrecio() {
        HBoxImpuesto.setVisible(false);
        HBoxSubtotal.setVisible(false);
        labelTotalTexto.setVisible(false);
    }

    /**
     * Configura la vista para mostrar toda la información de pago.
     * 
     * Muestra el desglose completo con subtotal, impuestos y total.
     * Calcula automáticamente el subtotal e impuesto basado en el total actual.
     * Se utiliza típicamente en la pantalla de facturación.
     */
    public void mostrarTodaLaInformacionDePago() {
        HBoxImpuesto.setVisible(true);
        HBoxSubtotal.setVisible(true);
        labelTotalTexto.setVisible(true);
        
        // Transferir el total actual como subtotal
        subtotalLabel.setText((totalLabel.getText()));
        // Calcular impuesto basado en el total y la tasa de IVA
        impuestoLabel.setText(String.format("%.2f", Double.parseDouble(totalLabel.getText()) * CalculadorIVA.getIVA_TASA()));    
    }


    /**
     * Obtiene la referencia a la vista raíz.
     * 
     * @return Parent que representa la vista raíz de este controlador
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Establece la referencia a la vista raíz.
     * 
     * Permite que otros controladores mantengan una referencia
     * a esta vista para manipulaciones externas si es necesario.
     * 
     * @param root La vista raíz a establecer
     */
    public void setRoot(Parent root) {
        this.root = root;
    }

    /**
     * Llena todas las etiquetas con la información de la función seleccionada.
     * 
     * Extrae y formatea los datos de la función para mostrarlos en la interfaz:
     * - Información de la película (título, género, imagen)
     * - Detalles de la función (formato, tipo de estreno, fecha, hora)
     * - Información de la sala (nombre y tipo)
     * 
     * Incluye validaciones para mostrar textos por defecto si algún dato no está disponible.
     * 
     * @param funcionSeleccionada La función de la cual extraer y mostrar la información
     */
    public void colocarInformacionFuncion(Funcion funcionSeleccionada) {
        // Información de la película
        labelNombrePelicula.setText(funcionSeleccionada.getPelicula() != null ? 
            funcionSeleccionada.getPelicula().getTitulo() : "Título no disponible");
        labelGeneroPelicula.setText(funcionSeleccionada.getPelicula() != null && funcionSeleccionada.getPelicula().getGenero() != null ? 
            funcionSeleccionada.getPelicula().getGenero() : "Género no disponible");
        
        // Detalles técnicos de la función
        labelFormato.setText(funcionSeleccionada.getFormato() != null ? 
            funcionSeleccionada.getFormato().toString() : "Formato no disponible");
        labelTipoEstreno.setText(funcionSeleccionada.getTipoEstreno() != null ? 
            funcionSeleccionada.getTipoEstreno().toString() : "Tipo de estreno no disponible");
        
        // Información de ubicación y horario
        labelLugarSala.setText(funcionSeleccionada.getSala() != null ? 
            funcionSeleccionada.getSala().getNombre() + " - " + funcionSeleccionada.getSala().getTipo().toString() : "Sala no disponible");
        labelFechaFuncion.setText(funcionSeleccionada.getFechaHoraInicio() != null ? 
            funcionSeleccionada.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Fecha no disponible");
        labelHoraFuncion.setText(funcionSeleccionada.getFechaHoraInicio() != null ? 
            funcionSeleccionada.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")) : "Hora no disponible");
        
        // Imagen promocional con fallback
        imagenPelicula.setImage(new Image(funcionSeleccionada.getPelicula() != null && funcionSeleccionada.getPelicula().getImagenUrl() != null ? 
            funcionSeleccionada.getPelicula().getImagenUrl() : "/images/no-image.png"));
    }

    /**
     * Calcula el subtotal basado en las butacas seleccionadas y los multiplicadores de la función.
     * 
     * Aplica la fórmula de pricing del sistema:
     * Subtotal = cantidad_butacas × multiplicador_sala × multiplicador_formato × multiplicador_tipo × multiplicador_horario
     * 
     * Multiplicadores considerados:
     * - Tipo de sala (Estándar, VIP, etc.)
     * - Formato de proyección (2D, 3D, IMAX, etc.)
     * - Tipo de estreno (Pre-estreno, Estreno, etc.)
     * - Día de la semana y horario
     * 
     * @param butacasSeleccionadas Lista de butacas que el usuario ha seleccionado
     * @param funcion Función que contiene los multiplicadores de precio
     */
    public void calcularSubtotal(List<Butaca> butacasSeleccionadas, Funcion funcion) {

        // Obtener todos los multiplicadores que afectan el precio
        double multiplicadorTipoDeSala = funcion.getSala().getTipo().getMultiplicador();
        double multiplicadorFormatoFuncion = funcion.getFormato().getMultiplicadorPrecio().doubleValue();
        double multiplicadorTipoFuncion = funcion.getTipoEstreno().getMultiplicadorPrecio().doubleValue();
        double multiplicadorHorario = funcion.getDiaSemana().getPrecio().doubleValue();

        // Calcular subtotal aplicando todos los multiplicadores
        double subtotal = butacasSeleccionadas.size() * multiplicadorTipoDeSala * multiplicadorFormatoFuncion * multiplicadorTipoFuncion * multiplicadorHorario;
        totalLabel.setText(String.format("%.2f", subtotal));
    }

    /**
     * Calcula el total final sumando subtotal e impuestos.
     * 
     * Toma los valores ya calculados de subtotal e impuesto desde las etiquetas
     * correspondientes y calcula el total final para mostrar en la facturación.
     * 
     * @param boletos Lista de productos/boletos (parámetro para compatibilidad)
     */
    public void calcularTotal(List<Producto> boletos) {
        // Sumar subtotal + impuesto para obtener el total final
        double total = Double.parseDouble(subtotalLabel.getText()) + Double.parseDouble(impuestoLabel.getText());
        totalLabel.setText(String.format("%.2f", total));
    }

    /**
     * Agrega visualmente una butaca seleccionada a la lista de boletos.
     * 
     * Crea un elemento visual personalizado que incluye:
     * - Número secuencial de la butaca
     * - Separador visual
     * - Código alfanumérico de la butaca (fila + columna)
     * - Estilo visual consistente con el diseño del sistema
     * 
     * Cada elemento tiene un ID único para facilitar su posterior eliminación.
     * 
     * @param butaca La butaca a agregar visualmente a la lista
     */
    public void mostrarButacaSeleccionada(Butaca butaca) {
        

        // Crear contenedor principal con estilo de tarjeta
        HBox butacaItem = new HBox();
        butacaItem.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        butacaItem.setSpacing(0); // Control manual del espaciado
        butacaItem.setPrefHeight(40);
        butacaItem.setStyle(
            "-fx-background-color: #e8f4f8; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #b3d9e6; " +
            "-fx-border-radius: 8; " +
            "-fx-border-width: 1; " +
            "-fx-padding: 8 12 8 12;"
        );
        
        // Número secuencial de la butaca
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
        
        // Espaciadores para layout preciso
        Region spacerPequeno = new Region();
        spacerPequeno.setPrefWidth(2);
        
        // Separador visual entre número y código
        Region spacer = new Region();
        spacer.setPrefWidth(5);
        spacer.setStyle("-fx-background-color: #b3d9e6; -fx-pref-height: 20px; -fx-max-width: 1px;");
        
        Region spacerGrande = new Region();
        spacerGrande.setPrefWidth(15);

        // Código alfanumérico de la butaca (ej: A1, B3, etc.)
        Label labelButaca = new Label(butaca.getFila().toUpperCase() + butaca.getColumna());
        labelButaca.setStyle(
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-text-fill: #2c5aa0; " +
            "-fx-min-width: 50px;"
        );

        // Ensamblar todos los elementos con espaciado controlado
        butacaItem.getChildren().addAll(labelCantidad, spacerPequeno, spacer, spacerGrande, labelButaca);
        
        // ID único para identificación y eliminación posterior
        butacaItem.setId("butaca-" + butaca.getId());
        
        // Agregar a la lista visual
        listaBoletos.getChildren().add(butacaItem);
    }

    /**
     * Remueve una butaca de la lista visual y renumera las restantes.
     * 
     * Busca el elemento visual correspondiente a la butaca por su ID único,
     * lo elimina de la lista, ajusta el contador y ejecuta la renumeración
     * automática para mantener la secuencia correcta.
     * 
     * @param butaca La butaca a remover de la visualización
     */
    public void removerButacaDeLista(Butaca butaca) {
        // Buscar y eliminar el elemento visual con el ID correspondiente
        listaBoletos.getChildren().removeIf(node ->
            ("butaca-" + butaca.getId()).equals(node.getId()));
        cantidad--;
        
        // Renumerar elementos restantes para mantener secuencia
        renumerarButacas();
    }
    
    /**
     * Renumera secuencialmente todos los elementos de butaca en la lista visual.
     * 
     * Recorre todos los elementos HBox en la lista y actualiza el número
     * secuencial del primer Label (número de butaca) para mantener una
     * numeración consecutiva después de eliminaciones.
     */
    private void renumerarButacas() {
        int numeroActual = 1;
        
        // Recorrer todos los elementos de la lista
        for (javafx.scene.Node node : listaBoletos.getChildren()) {
            if (node instanceof HBox) {
                HBox butacaItem = (HBox) node;
                
                // Buscar el primer Label (que contiene el número secuencial)
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
