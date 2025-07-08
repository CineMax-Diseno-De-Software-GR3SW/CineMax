import Modelos.Entidades.CineMax;
import Controladores.ControladorCineMax;
import Vistas.VistaCineMax;

public class Main{
    public static void main(String[] args) {
        VistaCineMax vistaCineMax = new VistaCineMax();

        CineMax cineMax = new CineMax();
        
        ControladorCineMax controladorCineMax = new ControladorCineMax(cineMax, vistaCineMax);
        
        controladorCineMax.mostrarPaginaPrincipal();
        //cineMax.iniciar();
    }
}