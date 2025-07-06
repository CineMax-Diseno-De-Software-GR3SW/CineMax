package com.cinemax.comun.controladores;

import com.cinemax.comun.modelos.entidades.CineMax;
import com.cinemax.comun.servicios.ServicioCineMax;
import com.cinemax.comun.vistas.VistaCineMax;

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
        servicioCineMax.manejarInicio(cineMax, vistaPaginaPrincipal);
    }

    public void cerrarPaginaPrincipal() {
        cineMax.cerrar();
    }
}