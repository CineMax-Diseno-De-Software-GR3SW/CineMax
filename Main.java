import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(new java.io.File("C:/ProyectoDiseño/CineMax/Vistas/VistaSala.fxml").toURI().toURL());        
        primaryStage.setTitle("Gestión de Salas");
        primaryStage.setTitle("Gestión de Butacas");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
