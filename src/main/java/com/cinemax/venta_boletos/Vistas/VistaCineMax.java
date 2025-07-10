package com.cinemax.venta_boletos.Vistas;

import java.util.List;
import java.util.Scanner;

public class VistaCineMax {

    //ControladorCineMax controladorPaginaPrincipal;
    private Scanner scanner = new Scanner(System.in);

    //public VistaCineMax(ControladorCineMax controladorPaginaPrincipal){
    //    this.controladorPaginaPrincipal = controladorPaginaPrincipal;
    //}

    public VistaCineMax(){

    }

    public int mostrar(List<String> opciones) {
        System.out.println("Bienvenido a CineMax");
        System.out.println("Seleccione una opción:");
        for (String opcion : opciones) {
            System.out.println(opcion);
        }
        System.out.println("Ingrese el número de la opción deseada:");
        
        int seleccion = scanner.nextInt();

        switch (seleccion) {
            case 1:
                System.out.println("Aquí va la gestión de películas...");
                return 1;
            case 2:
                System.out.println("Aquí va la gestión de salas...");
                return 2;
            case 3:
                System.out.println("Aquí va la gestión de boletos...");
                return 3;
            case 4:
                System.out.println("Aquí va la gestión de empleados...");
                return 4;
            case 5:
                System.out.println("Aquí va la gestión de reportes...");
                return 5;
            case 6:
                System.out.println("Saliendo del sistema...");
                //controladorPaginaPrincipal.cerrarPaginaPrincipal();
                return 6; // Indica que se debe cerrar la aplicación
            default:
                System.out.println("Opción no válida. Por favor, intente de nuevo.");
                break;
            
        }
        return 0; // Indica que la aplicación no se cierra
    }

}
