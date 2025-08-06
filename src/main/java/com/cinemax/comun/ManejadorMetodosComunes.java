package com.cinemax.comun;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

/**
 * Clase utilitaria que centraliza métodos comunes para la gestión de ventanas
 * y componentes de la interfaz gráfica.
 * 
 * Todas las ventanas se configuran automáticamente para maximizarse y
 * adaptarse a las dimensiones de la pantalla principal.
 * 
 * @author CineMax Development Team
 */
public class ManejadorMetodosComunes {

     /**
     * Cambia la ventana actual cargando una nueva vista FXML con título personalizado.
     * 
     * Configura la ventana para ocupar toda la pantalla y la maximiza automáticamente.
     * En caso de error al cargar la vista, muestra una ventana de error.
     * 
     * @param currentStage El Stage actual que será reemplazado
     * @param rutaFXML Ruta del archivo FXML a cargar (debe comenzar con "/")
     * @param titulo Título para la nueva ventana (actualmente no se usa, siempre muestra "CineMax")
     */
    public static void cambiarVentana(Stage currentStage, String rutaFXML, String titulo) {
        try {
            // Cargar el archivo FXML desde los recursos
            FXMLLoader loader = new FXMLLoader(ManejadorMetodosComunes.class.getResource(rutaFXML));
            Parent root = loader.load();

            // Obtener las dimensiones completas de la pantalla principal
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            // Configurar la posición y tamaño de la ventana para ocupar toda la pantalla
            currentStage.setX(screenBounds.getMinX());
            currentStage.setY(screenBounds.getMinY());
            currentStage.setWidth(screenBounds.getWidth());
            currentStage.setHeight(screenBounds.getHeight());
            

            currentStage.setTitle("CineMax");

            // Establecer la nueva escena con el contenido cargado
            currentStage.setScene(new Scene(root));

            // Maximizar la ventana para aprovechar todo el espacio disponible
            currentStage.setMaximized(true);


        } catch (IOException e) {
            // En caso de error, mostrar mensaje al usuario y registrar el error
            mostrarVentanaError("No se pudo cargar la interfaz de usuario.");
            e.printStackTrace();
        }
    }

    /**
     * Versión sobrecargada del método cambiarVentana sin parámetro de título.
     * 
     * Funciona de manera idéntica al método anterior pero sin especificar título,
     * utilizando por defecto "CineMAX" como título de la ventana.
     * 
     * @param currentStage El Stage actual que será reemplazado
     * @param rutaFXML Ruta del archivo FXML a cargar (debe comenzar con "/")
     */
    public static void cambiarVentana(Stage currentStage, String rutaFXML) {
        try {
            // Cargar el archivo FXML desde los recursos
            FXMLLoader loader = new FXMLLoader(ManejadorMetodosComunes.class.getResource(rutaFXML));
            Parent root = loader.load();

            // Obtener las dimensiones completas de la pantalla principal
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Configurar la posición y tamaño de la ventana para ocupar toda la pantalla
            currentStage.setX(screenBounds.getMinX());
            currentStage.setY(screenBounds.getMinY());
            currentStage.setWidth(screenBounds.getWidth());
            currentStage.setHeight(screenBounds.getHeight());
            
            // Establecer la nueva escena con el contenido cargado
            currentStage.setScene(new Scene(root));
            
            currentStage.setTitle("CineMAX");

            // Maximizar la ventana para aprovechar todo el espacio disponible
            currentStage.setMaximized(true);


        } catch (IOException e) {
            // En caso de error, mostrar mensaje al usuario y registrar el error
            mostrarVentanaError("No se pudo cargar la interfaz de usuario.");
            e.printStackTrace();
        }
    }

    /**
     * Cambia de ventana y retorna una referencia al controlador de la nueva vista.
     * 
     * Este método permite obtener acceso al controlador antes de mostrar la ventana,
     * útil para configurar datos o estado inicial en el controlador.
     * 
     * NOTA: Si la vista destino es muy pesada, puede causar retrasos en la carga.
     * 
     * @param <T> Tipo genérico del controlador esperado
     * @param currentStage El Stage actual que será reemplazado
     * @param rutaFXML Ruta del archivo FXML a cargar
     * @param titulo Título para la ventana (actualmente no se usa)
     * @return El controlador de la nueva vista, o null si ocurre un error
     */
    // Método sobrecargado que permite acceso al controlador antes de mostrar la ventana
    // Desventaja: Si la siguiente ventana es muy pesada, puede causar un retraso al cargar
    public static <T> T cambiarVentanaConControlador(Stage currentStage, String rutaFXML, String titulo) {
        try {
            // Cargar el archivo FXML desde los recursos
            FXMLLoader loader = new FXMLLoader(ManejadorMetodosComunes.class.getResource(rutaFXML));
            Parent root = loader.load();

            // Obtener el controlador para permitir configuración previa
            T controller = loader.getController();

            // Obtener las dimensiones completas de la pantalla principal
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Configurar la posición y tamaño de la ventana para ocupar toda la pantalla
            currentStage.setX(screenBounds.getMinX());
            currentStage.setY(screenBounds.getMinY());
            currentStage.setWidth(screenBounds.getWidth());
            currentStage.setHeight(screenBounds.getHeight());
            
            // Establecer la nueva escena con el contenido cargado
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("CineMAX");
            
            // Maximizar la ventana para aprovechar todo el espacio disponible
            currentStage.setMaximized(true);

            return controller; // Retorna el controlador para configuración adicional

        } catch (IOException e) {
            // En caso de error, mostrar mensaje al usuario y registrar el error
            mostrarVentanaError("No se pudo cargar la interfaz de usuario.");
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Muestra una ventana emergente de éxito con el mensaje especificado.
     * 
     * @param mensaje Texto a mostrar en la ventana de éxito
     */
    public static void mostrarVentanaExito(String mensaje) {
        mostrarVentanaEmergente("Éxito", mensaje, "/vistas/comun/VistaExito.fxml");
    }

    /**
     * Muestra una ventana emergente de error con el mensaje especificado.
     * 
     * @param mensaje Texto a mostrar en la ventana de error
     */
    public static void mostrarVentanaError(String mensaje) {
        mostrarVentanaEmergente("Error", mensaje, "/vistas/comun/VistaError.fxml");
    }

    /**
     * Muestra una ventana emergente de advertencia con el mensaje especificado.
     * 
     * @param mensaje Texto a mostrar en la ventana de advertencia
     */
    public static void mostrarVentanaAdvertencia(String mensaje) {
        mostrarVentanaEmergente("Advertencia", mensaje, "/vistas/comun/VistaAdvertencia.fxml");
    }

    /**
     * Método privado que centraliza la lógica para mostrar ventanas emergentes.
     * 
     * Crea ventanas modales con estilo transparente y tamaño fijo,
     * configuradas para bloquear la interacción con la ventana principal
     * hasta que el usuario cierre la ventana emergente.
     * 
     * @param titulo Título de la ventana emergente
     * @param mensaje Mensaje a mostrar en la ventana
     * @param fxmlPath Ruta del archivo FXML de la vista emergente
     */
    private static void mostrarVentanaEmergente(String titulo, String mensaje, String fxmlPath) {
        try {
            // Cargar la vista FXML para la ventana emergente
            FXMLLoader loader = new FXMLLoader(ManejadorMetodosComunes.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Obtener el controlador y configurar los datos a mostrar
            ControladorAlertas controller = loader.getController();
            controller.setData(titulo, mensaje);

            // Crear un nuevo Stage para la ventana emergente
            Stage alertStage = new Stage();
            alertStage.initStyle(StageStyle.TRANSPARENT); // Estilo sin decoraciones del sistema
            alertStage.initModality(Modality.APPLICATION_MODAL); // Bloquea interacción con otras ventanas

            // Configurar dimensiones fijas para la ventana emergente
            double ancho = 400;
            double alto = 200;

            Scene scene = new Scene(root, ancho, alto);
            scene.setFill(null); // Fondo transparente

            //ApuntadorTema.getInstance().applyTheme(scene); // Aplicación de tema comentada

            // Configurar y mostrar la ventana emergente
            alertStage.setScene(scene);
            alertStage.setWidth(ancho);
            alertStage.setHeight(alto);
            alertStage.setResizable(false); // Evita que el usuario cambie el tamaño
            alertStage.showAndWait(); // Muestra la ventana y espera hasta que se cierre

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra una pantalla de carga que después navega a una vista específica.
     * 
     * Este método es útil para operaciones que requieren tiempo de procesamiento,
     * mostrando al usuario una barra de progreso antes de cambiar a la vista destino.
     * 
     * @param stage El Stage donde mostrar la pantalla de carga
     * @param rutaFXML Ruta de la vista FXML de destino
     * @param saltosEnElProgreso Número de incrementos en la barra de progreso
     * @param tiempoPorSalto Tiempo en milisegundos entre cada incremento
     */
    public static void mostrarPantallaDeCarga(Stage stage, String rutaFXML, int saltosEnElProgreso, int tiempoPorSalto) {
        try {
            // Cargar y configurar la pantalla de carga
            ControladorCarga controladorCarga = cargarPantallaDeCarga(stage);
            
            // Configurar la carga para ir a la siguiente ventana
            // Parámetros: stage, ruta FXML destino, título de ventana destino
            controladorCarga.iniciarCarga(stage, rutaFXML, saltosEnElProgreso, tiempoPorSalto);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga y configura la pantalla de carga básica.
     * 
     * Este método auxiliar se encarga de cargar la vista FXML de la pantalla de carga,
     * configurar la escena y mostrar la ventana maximizada.
     * 
     * @param stage El Stage donde mostrar la pantalla de carga
     * @return El controlador de la pantalla de carga para configuración adicional
     * @throws IOException Si ocurre un error al cargar el archivo FXML
     */
    public static ControladorCarga cargarPantallaDeCarga(Stage stage) throws IOException {
        // Cargar el FXML de la pantalla de carga desde los recursos
        FXMLLoader loader = new FXMLLoader(ManejadorMetodosComunes.class.getResource("/vistas/comun/VistaCarga.fxml"));
        Parent root = loader.load();
        
        // Obtener el controlador para configuración posterior
        ControladorCarga controladorCarga = loader.getController();
        
        // Crear y configurar la escena de carga
        Scene escenaCarga = new Scene(root);
        stage.setScene(escenaCarga);
        stage.setTitle("CineMAX");

        // Maximizar la ventana y mostrarla
        stage.setMaximized(true);
        stage.show();

        return controladorCarga;
    }

    /**
     * Muestra una pantalla de carga con datos personalizados para procesamiento.
     * 
     * Este método permite pasar un controlador personalizado que implementa
     * ControladorCargaConDatos para realizar operaciones específicas durante la carga.
     * Es útil cuando se necesita procesar datos o realizar operaciones complejas
     * antes de mostrar la siguiente vista.
     * 
     * @param stage El Stage donde mostrar la pantalla de carga
     * @param controladorCargaConDatos Controlador que maneja el procesamiento de datos
     * @param saltosEnElProgreso Número de incrementos en la barra de progreso
     * @param tiempoPorSalto Tiempo en milisegundos entre cada incremento
     */
    public static void mostrarVistaDeCargaPasandoDatos(Stage stage, ControladorCargaConDatos controladorCargaConDatos, int saltosEnElProgreso, int tiempoPorSalto) {
        try {
        
            // Cargar y configurar la pantalla de carga básica
            ControladorCarga controladorCarga = cargarPantallaDeCarga(stage);
        
            // Pasar los datos y configuración al controlador de carga
            controladorCarga.iniciarCarga(stage, controladorCargaConDatos, saltosEnElProgreso, tiempoPorSalto);
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
