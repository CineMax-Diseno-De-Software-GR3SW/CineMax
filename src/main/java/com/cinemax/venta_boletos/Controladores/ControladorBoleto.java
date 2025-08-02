package com.cinemax.venta_boletos.Controladores;

import com.cinemax.comun.ManejadorMetodosComunes;
import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.peliculas.modelos.entidades.Pelicula;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.EstadoButaca;
import com.cinemax.salas.servicios.ButacaService;
import com.cinemax.venta_boletos.Modelos.Producto;
import com.cinemax.venta_boletos.Modelos.Persistencia.BoletoDAO;
import com.cinemax.venta_boletos.Servicios.ServicioGeneradorBoleto;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ControladorBoleto {

    // --- Lógica de Negocio ---
    //private final ServicioGeneradorBoleto servicioBoleto = new ServicioGeneradorBoleto();

    // --- Estado de la Vista ---
    private final int MAX_BOLETOS = 10; // TODO: Debería depender de la cantidad de butacas disponibles en la sala
    //private int boletosSalaVIP = 0;
    //private int boletosSalaNormal = 0;
    private int cantidadBoletos = 0;
    private double xOffset = 0;
    private double yOffset = 0;
        
    private double subtotal = 0.0;
    // TODO: Son datos que deben ser entregados por el modulo sala
    private double precioSalaVIP = 7.60;
    private double precioSalaNormal = 3.00;

    List<Butaca> butacasDeSala;

    private BoletoDAO boletoDAO;

    private Funcion funcionSeleccionada;

    private List<Butaca> butacasOcupadas;

    // --- Componentes FXML ---
    @FXML
    private Button buttonVolver;

    @FXML
    private Button continueButton;

    @FXML
    private Label countLabel;

    @FXML
    private VBox funcionInfoContainer;

    @FXML
    private HBox headerBar;

    @FXML
    private ImageView imagenPelicula;

    @FXML
    private Label labelCantidadButacasDisponiboes;

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
    private Label labelPrecio;

    @FXML
    private Label labelTipoEstreno;

    @FXML
    private Label labelTipoSala;

    @FXML
    private Button minusButton;

    @FXML
    private Button plusButton;

    @FXML
    private VBox ticketSummaryContainer;

    @FXML
    private Label totalLabel;

    @FXML
    private Text tusBoletosTitle;

    public ControladorBoleto() {
        this.boletoDAO = new BoletoDAO();
        //butacasOcupadasNormal = new ArrayList<>();
        //butacasOcupadasVIP = new ArrayList<>();
    }

    public void initData(String pelicula, String funcion, Funcion seleccion, Funcion funcionEnSalaVIP, Funcion funcionEnSalaNormal) {
        //this.pelicula = pelicula;
        //this.funcion = funcion;
        //this.funcion1 = seleccion;
        //peliculaLabel.setText(seleccion.getPelicula().getTitulo());
        //salaLabel.setText(seleccion.getSala().getNombre());

        //try {
        //    butacasOcupadasNormal = boletoDAO.listarButacasDeBoletosPorFuncion(funcionEnSalaNormal);
        //    butacasOcupadasVIP = boletoDAO.listarButacasDeBoletosPorFuncion(funcionEnSalaVIP);
        //} catch (Exception e) {
        //    ManejadorMetodosComunes.mostrarVentanaError("Error al cargar butacas ocupadas");
        //    e.getMessage();
        //}

        //vipDisponiblesLabel.setText(String.valueOf(funcionEnSalaVIP.getSala().getCapacidad() - butacasOcupadasVIP.size()));
        //normalDisponiblesLabel.setText(String.valueOf(funcionEnSalaNormal.getSala().getCapacidad() - butacasOcupadasNormal.size()));
//
        //this.funcionEnSalaNormal = funcionEnSalaNormal;
        //this.funcionEnSalaVIP = funcionEnSalaVIP;
    }

    @FXML
    public void initialize() {
        actualizarVista();
        headerBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        headerBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) headerBar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    @FXML
    private void onPlus() {
        if (cantidadBoletos < MAX_BOLETOS)
            cantidadBoletos++;
        actualizarVista();
    }

    @FXML
    private void onMinus() {
        if (cantidadBoletos > 0)
            cantidadBoletos--;
        actualizarVista();
    }

    private void actualizarVista() {
        countLabel.setText(String.valueOf(cantidadBoletos));
        //normalCountLabel.setText(String.valueOf(boletosSalaNormal));
        actualizarResumenDinamico();
    }

    private void actualizarResumenDinamico() {
        ticketSummaryContainer.getChildren().clear();
        boolean hayBoletos = cantidadBoletos > 0;
        tusBoletosTitle.setVisible(hayBoletos);
        tusBoletosTitle.setManaged(hayBoletos);

        // Se usa un boleto temporal para obtener el precio base del modelo.
        // double precioUnitario = new com.cinemax.venta_boletos.Modelos.Boleto("",
        // "").getPrecio();

        subtotal = 0.0;

        if (cantidadBoletos > 0) {
            subtotal += cantidadBoletos * precioSalaVIP;
            ticketSummaryContainer.getChildren().add(crearFilaResumen("Sala "+funcionSeleccionada.getSala().getTipo(), cantidadBoletos, precioSalaVIP));
            //vipDisponiblesLabel.setText(String.valueOf(funcionEnSalaVIP.getSala().getCapacidad() - (butacasOcupadasVIP.size() + boletosSalaVIP)));


        }

        DecimalFormat df = new DecimalFormat("$ #,##0.00");
        totalLabel.setText(df.format(subtotal));

        
        // butacasOcupadas = daoBoleto.buscarButacasOcupadasEnBoletosPorFuncion(funcion)
        // disponibilidadDeButacas = funcion.getSala().getButacas().len -
        // butacasOcupadas.size();
    }

    private HBox crearFilaResumen(String nombreBoleto, int cantidad, double precioUnitario) {
        HBox fila = new HBox();
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setSpacing(10.0);
        Label lblCantidad = new Label(String.valueOf(cantidad));
        lblCantidad.getStyleClass().add("summary-ticket-count");
        Label lblNombre = new Label(nombreBoleto);
        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);
        DecimalFormat df = new DecimalFormat("$ #,##0.00");
        Label lblPrecio = new Label(df.format(cantidad * precioUnitario));
        lblPrecio.getStyleClass().add("summary-price");
        fila.getChildren().addAll(lblCantidad, lblNombre, espaciador, lblPrecio);
        // System.out.println(fila);
        return fila;
    }

    @FXML
    protected void onContinuarAction() {
        if (cantidadBoletos == 0) {
            ManejadorMetodosComunes.mostrarVentanaAdvertencia("Selecciona al menos un boleto para continuar.");
            return;
        }
        try {
            // 1. Simular asignación de butacas
            //List<String> butacasAsignadas = new ArrayList<>();
            //int totalBoletos = boletosSalaVIP + boletosSalaNormal;
            //String controladoDeConsultasSalas = "Controlador de Consultas de Salas"; // Simulación
            //ControladorAsignadorButacas controladorAsignadorButacas = new ControladorAsignadorButacas();
            //butacasAsignadas = controladorAsignadorButacas.asignarButacas(controladoDeConsultasSalas, funcion, butacasAsignadas, totalBoletos);
            //List<String> butacasAsignadas = controladorAsignadorButacas.asignarButacas(controladoDeConsultasSalas,funcion, butacasOcupadasNormal, totalBoletos);
            //List<Butaca> butacasAsignadas = controladorAsignadorButacas.asignarButacas(funcionEnSalaVIP, funcionEnSalaNormal, butacasOcupadasNormal, butacasOcupadasVIP, boletosSalaVIP, boletosSalaNormal);

            // 2. Usar servicio para generar los boletos reales
            //List<Producto> boletosGenerados = servicioBoleto.generarBoleto(this.funcion, butacasAsignadas);

            // 3. Cargar la siguiente pantalla y pasarle los boletos
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/resumen-view.fxml"));
            //Parent root = loader.load();
            //ControladorResumen controllerResumen = loader.getController();
//
            //controllerResumen.initData(this.pelicula, this.funcion, boletosGenerados, subtotal);
            //controllerResumen.setPreviousScene(continueButton.getScene());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/venta_boletos/VistaSeleccionButacas.fxml"));
            Parent root = loader.load();
            ControladorAsignadorButacas controladorAsignadorButacas = loader.getController();

            controladorAsignadorButacas.inicializarDatos(funcionSeleccionada);
            //controllerSeleccionButacas.initData(this.pelicula, this.funcion, boletosGenerados, subtotal);
            //controllerSeleccionButacas.setPreviousScene(continueButton.getScene());

            Stage stage = (Stage) continueButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onCloseAction() {
        ((Stage) headerBar.getScene().getWindow()).close();
    }

    public void inicializarInformacion(Funcion funcionSeleccionada) {
        this.funcionSeleccionada = funcionSeleccionada;
        labelTipoSala.setText(funcionSeleccionada.getSala().getTipo().name());
        labelPrecio.setText(String.format("$%.2f", ":D"));//funcionSeleccionada.getSala().getTipo()));
        colocarInformacionFuncion(funcionSeleccionada);
        
        labelCantidadButacasDisponiboes.setText(String.valueOf(funcionSeleccionada.getSala().getCapacidad() - butacasOcupadas.size()));       
    }

    private void colocarInformacionFuncion(Funcion funcionSeleccionada) {
        labelNombrePelicula.setText(funcionSeleccionada.getPelicula() != null ? funcionSeleccionada.getPelicula().getTitulo() : "Título no disponible");
        labelGeneroPelicula.setText(funcionSeleccionada.getPelicula() != null && funcionSeleccionada.getPelicula().getGenero() != null ? funcionSeleccionada.getPelicula().getGenero() : "Género no disponible");
        labelLugarSala.setText(funcionSeleccionada.getSala() != null ? funcionSeleccionada.getSala().getNombre() : "Sala no disponible");
        labelFechaFuncion.setText(funcionSeleccionada.getFechaHoraInicio() != null ? funcionSeleccionada.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Fecha no disponible");
        labelHoraFuncion.setText(funcionSeleccionada.getFechaHoraInicio() != null ? funcionSeleccionada.getFechaHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")) : "Hora no disponible");
        labelFormato.setText(funcionSeleccionada.getFormato() != null ? funcionSeleccionada.getFormato().name().replace("_", " ") : "Formato no disponible");
        labelTipoEstreno.setText(funcionSeleccionada.getTipoEstreno() != null ? funcionSeleccionada.getTipoEstreno().name().replace("_", " ") : "Tipo de estreno no disponible");
    }

    @FXML
    void onBackAction(ActionEvent event) {
        System.out.println("Volviendo a la pantalla anterior");

    }
}
