package com.cinemax.venta_boletos.Modelos;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.modelos.entidades.Butaca;
import com.cinemax.salas.modelos.entidades.TipoSala;

public class Boleto extends Producto {
    private Funcion funcion;
    private Butaca butaca;

    public Boleto(Funcion funcion, Butaca butaca) {
        this.funcion = funcion;
        this.butaca = butaca;
        calcularPrecio();
    }
    
    public void setFuncion(Funcion funcion) {
        this.funcion = funcion;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public void setButaca(Butaca butaca) {
        this.butaca = butaca;
    }

    public Butaca getButaca() {
        return butaca;
    }

    @Override
    public void calcularPrecio() {        
        double precioBase = 1.0; // Precio base por boleto
        double multiplicadorTipoDeSala = funcion.getSala().getTipo().getMultiplicador();
        double multiplicadorFormatoFuncion = funcion.getFormato().getMultiplicadorPrecio().doubleValue();
        double multiplicadorTipoFuncion = funcion.getTipoEstreno().getMultiplicadorPrecio().doubleValue();
        double multiplicadorHorario = funcion.getDiaSemana().getPrecio().doubleValue();
        setPrecio(precioBase * multiplicadorTipoDeSala * multiplicadorFormatoFuncion * multiplicadorTipoFuncion * multiplicadorHorario);
    }

    @Override
    public String toString() {
        return "Boleto{" +
                "funcion='" + funcion + '\n' +
                ", butaca='" + butaca + '\n' +
                ", precioUnitario=" + getPrecio() +
                '}';
    }

}
