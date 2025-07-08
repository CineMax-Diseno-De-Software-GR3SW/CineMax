package Servicios;

import java.util.List;

import Modelos.Entidades.CineMax;
import Vistas.VistaCineMax;

public class ServicioCineMax {

    public int manejarInicio(CineMax cineMax, VistaCineMax vistaPaginaPrincipal) {
        List<String> opciones = cineMax.obtenerOpciones();
        int cerrado = 0;
        do{
            cerrado =vistaPaginaPrincipal.mostrar(opciones);
            switch (cerrado) {
                case 1:
                    // Aquí va la gestión de películas...
                    break;
                case 2:
                    //Aquí va la gestión de salas...
                    break;
                case 3:
                    //Aquí va la gestión de boletos...

                    
                    break;
                case 4:
                    //Aquí va la gestión de empleados...
                    break;
                case 5:
                    //Aquí va la gestión de reportes...
                    break;
                default:
                    break;
            }
        }while(cerrado != 6);
        return cerrado;
    }

    
}
