package com.cinemax.venta_boletos.Modelos;

public class Boleto extends Producto {
    private String funcion;
    private String butaca;

    public Boleto(String funcion, String butaca) {
        this.funcion = funcion;
        this.butaca = butaca;
        calcularPrecio();
    }
    
    public void setFuncion(String funcion) {
        this.funcion = funcion;
    }

    public String getFuncion() {
        return funcion;
    }

    public void setButaca(String butaca) {
        this.butaca = butaca;
    }

    public String getButaca() {
        return butaca;
    }

    @Override
    public void calcularPrecio() {
        double precioBase = 10.0; // Precio base por boleto
        double precioTipoDeSala = 1.0;
        double precioFormatoFuncion = 1.0;
        double precioTipoFuncion = 1.0;
        double precioHorario = 1.0;
        setPrecio(precioBase + precioTipoDeSala + precioFormatoFuncion + precioTipoFuncion + precioHorario);
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
