package com.cinemax.venta_boletos.Controladores;

import java.util.List;

import com.cinemax.venta_boletos.Modelos.Producto;
import com.cinemax.venta_boletos.Servicios.ServicioGeneradorBoleto;

public class ControladorBoleto {

    private ServicioGeneradorBoleto servicioGeneradorBoleto;
    //private ControladorAsignadorButacas controladorAsignadorButacas;
    //private ControladorAsignadorFuncion controladorAsignadorFuncion;

    public ControladorBoleto() {
        this.servicioGeneradorBoleto = new ServicioGeneradorBoleto();
        //this.controladorAsignadorButacas = new ControladorAsignadorButacas();
        //this.controladorAsignadorFuncion = new ControladorAsignadorFuncion();
    }

    public List<Producto> generarBoleto(String funcion, List<String> butacas) {
        return servicioGeneradorBoleto.generarBoleto(funcion, butacas);
    }

    //public void gestionarBoletos() {
    //    String funcion = controladorAsignadorFuncion.asignarFuncion("ControladorCartelera");
    //    List<String> butacas = controladorAsignadorButacas.asignarButacas("ControladorDeConsultaSalas", funcion);
//
    //    generarBoleto(funcion, butacas);    
    //}
}
