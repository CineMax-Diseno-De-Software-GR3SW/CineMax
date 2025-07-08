package Controladores;

import Modelos.Entidades.CineMax;
import Vistas.VistaCineMax;
import Servicios.ServicioCineMax;

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
