package com.cinemax.venta_boletos.Controladores;

import com.cinemax.venta_boletos.Main;
import com.cinemax.venta_boletos.Modelos.CineMax;
import com.cinemax.venta_boletos.Servicios.ServicioCineMax;
import com.cinemax.venta_boletos.Vistas.VistaCineMax;

public class ControladorCineMax {

    private VistaCineMax vistaPaginaPrincipal;
    private CineMax cineMax;
    private ServicioCineMax servicioCineMax;

    public ControladorCineMax(CineMax cineMax, VistaCineMax vistaCineMax) {
        vistaPaginaPrincipal = vistaCineMax;
        this.cineMax = cineMax;
        this.servicioCineMax = new ServicioCineMax();
    }

    public void mostrarPaginaPrincipal() {
        servicioCineMax.manejarInicio(cineMax, vistaPaginaPrincipal, this);
    }

    public void iniciarModuloVentaBoletos() {
        // En un futuro, estos datos vendrán de los otros módulos
        String peliculaSeleccionada = "Como entrenar a tu Dragon";
        String salaSeleccionada = "Sala 7 - Dom 29 - 13:40";

        Main.launchWithData(peliculaSeleccionada, salaSeleccionada);
    }

    public void cerrarPaginaPrincipal() {
        cineMax.cerrar();
    }
}
