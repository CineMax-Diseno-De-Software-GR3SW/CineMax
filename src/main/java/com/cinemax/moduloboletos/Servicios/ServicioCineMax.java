package com.cinemax.moduloboletos.Servicios;

import java.util.List;

import com.cinemax.moduloboletos.Controladores.ControladorCineMax;
import com.cinemax.moduloboletos.Controladores.VentaDeBoletos.ControladorVentaDeBoletos;
import com.cinemax.moduloboletos.Modelos.CineMax;
import com.cinemax.moduloboletos.Vistas.VistaCineMax;

public class ServicioCineMax {
    ControladorVentaDeBoletos controladorVentaDeBoletos;

    public ServicioCineMax() {
        controladorVentaDeBoletos = new ControladorVentaDeBoletos();
    }

    public int manejarInicio(CineMax cineMax, VistaCineMax vistaPaginaPrincipal,
            ControladorCineMax controladorCineMax) {
        List<String> opciones = cineMax.obtenerOpciones();
        int cerrado = 0;
        do {
            cerrado = vistaPaginaPrincipal.mostrar(opciones);
            switch (cerrado) {
                case 1:
                    // Aquí va la gestión de películas...
                    break;
                case 2:
                    // Aquí va la gestión de salas...
                    break;
                case 3:
                    // Aquí va la gestión de boletos...

                    // controladorVentaDeBoletos.gestionarVentaDeBoletos("Controlador de Cartelera",
                    // "Controlador de Consulta de Salas");
                    controladorCineMax.iniciarModuloVentaBoletos();
                    break;
                case 4:
                    // Aquí va la gestión de empleados...
                    break;
                case 5:
                    // Aquí va la gestión de reportes...
                    break;
                default:
                    break;
            }
        } while (cerrado != 6);
        return cerrado;
    }

}