package com.cinemax.venta_boletos.Modelos.entidades;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.modelos.entidades.Butaca;

/**
 * Un boleto asocia una función cinematográfica específica con una butaca particular,
 * calculando automáticamente el precio final basado en múltiples factores de la función.
 * 
 * Hereda de Producto para integrarse con el sistema de facturación y manejo de precios.
 * 
 * @author GR3SW
 * @version 1.0
 */
public class Boleto extends Producto {
    private Funcion funcion;
    
    private Butaca butaca;

    /**
     * Inicializa los atributos principales y calcula automáticamente el precio
     * final basado en los multiplicadores de la función. El precio se calcula
     * inmediatamente para garantizar consistencia.
     * 
     * @param funcion La función cinematográfica para este boleto
     * @param butaca La butaca específica que se reserva
     */
    public Boleto(Funcion funcion, Butaca butaca) {
        this.funcion = funcion;
        this.butaca = butaca;
        calcularPrecio(); // Calcular precio inmediatamente tras inicialización
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

    /**
     * Implementa la fórmula:
     * Precio = precio_base × multiplicador_sala × multiplicador_formato × multiplicador_tipo × multiplicador_horario
     */
    @Override
    public void calcularPrecio() {        
        double precioBase = 1.0; // Precio base unitario por boleto
        
        // Obtener multiplicadores de la función asociada
        double multiplicadorTipoDeSala = funcion.getSala().getTipo().getMultiplicador();
        double multiplicadorFormatoFuncion = funcion.getFormato().getMultiplicadorPrecio().doubleValue();
        double multiplicadorTipoFuncion = funcion.getTipoEstreno().getMultiplicadorPrecio().doubleValue();
        double multiplicadorHorario = funcion.getDiaSemana().getPrecio().doubleValue();
        
        // Aplicar fórmula completa y establecer precio final
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
