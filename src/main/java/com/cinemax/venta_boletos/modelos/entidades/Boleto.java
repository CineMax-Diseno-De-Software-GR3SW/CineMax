package com.cinemax.venta_boletos.modelos.entidades;

import com.cinemax.peliculas.modelos.entidades.Funcion;
import com.cinemax.salas.modelos.entidades.Butaca;

/**
 * Entidad que representa un boleto de entrada al cine.
 * 
 * Un boleto asocia una función cinematográfica específica con una butaca particular,
 * calculando automáticamente el precio final basado en múltiples factores de la función.
 * 
 * Hereda de Producto para integrarse con el sistema de facturación y manejo de precios.
 * 
 * Componentes principales:
 * - Función: Contiene película, horario, sala y multiplicadores de precio
 * - Butaca: Asiento específico reservado para esta entrada
 * - Precio: Calculado automáticamente usando la fórmula del sistema
 * 
 * Fórmula de precio:
 * Precio = precio_base × multiplicador_sala × multiplicador_formato × multiplicador_tipo × multiplicador_horario
 * 
 * @author GR3SW
 * @version 1.0
 */
public class Boleto extends Producto {
    /** Función cinematográfica asociada a este boleto (película, horario, sala, etc.) */
    private Funcion funcion;
    
    /** Butaca específica reservada para este boleto */
    private Butaca butaca;

    /**
     * Constructor que crea un boleto asociando una función con una butaca específica.
     * 
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
    
    /**
     * Establece la función asociada a este boleto.
     * 
     * @param funcion La nueva función a asociar con este boleto
     */
    public void setFuncion(Funcion funcion) {
        this.funcion = funcion;
    }

    /**
     * Obtiene la función asociada a este boleto.
     * 
     * @return La función cinematográfica de este boleto
     */
    public Funcion getFuncion() {
        return funcion;
    }

    /**
     * Establece la butaca asociada a este boleto.
     * 
     * @param butaca La nueva butaca a asociar con este boleto
     */
    public void setButaca(Butaca butaca) {
        this.butaca = butaca;
    }

    /**
     * Obtiene la butaca asociada a este boleto.
     * 
     * @return La butaca reservada para este boleto
     */
    public Butaca getButaca() {
        return butaca;
    }

    /**
     * Calcula el precio final del boleto aplicando todos los multiplicadores.
     * 
     * Implementa la fórmula oficial de pricing del sistema CineMax:
     * Precio = precio_base × multiplicador_sala × multiplicador_formato × multiplicador_tipo × multiplicador_horario
     * 
     * Multiplicadores aplicados:
     * - Tipo de sala: Factor según VIP, Normal, etc.
     * - Formato de función: 2D, 3D, etc.
     * - Tipo de estreno: Pre-estreno, Estreno regular, etc.
     * - Horario: Factor según día de la semana
     * 
     * El precio calculado se almacena automáticamente en el atributo heredado.
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

    /**
     * Representación en cadena del boleto con información detallada.
     * 
     * Incluye información completa de la función, butaca y precio unitario
     * para facilitar debugging y logging del sistema.
     * 
     * @return String con formato detallado del boleto
     */
    @Override
    public String toString() {
        return "Boleto{" +
                "funcion='" + funcion + '\n' +
                ", butaca='" + butaca + '\n' +
                ", precioUnitario=" + getPrecio() +
                '}';
    }

}
