package com.cinemax.venta_boletos.Modelos;


public class Boleto extends Producto {
    private long idBoleto;
    private int idFuncion;
    private long idFactura;
    private int idButaca;
    private String funcion;


    private String butaca;

    public Boleto(long idBoleto, int idFuncion, long idFactura, int idButaca) {
        this.idBoleto = idBoleto;
        this.idFuncion = idFuncion;
        this.idFactura = idFactura;
        this.idButaca = idButaca;
    }

    public Boleto(String funcion, String butaca) {
        this.funcion = funcion;
        this.butaca = butaca;
        calcularPrecio();
    }
    
    public long getIdBoleto() { return idBoleto; }
    public void setIdBoleto(long idBoleto) { this.idBoleto = idBoleto; }

    public int getIdFuncion() { return idFuncion; }
    public void setIdFuncion(int idFuncion) { this.idFuncion = idFuncion; }

    public long getIdFactura() { return idFactura; }
    public void setIdFactura(long idFactura) { this.idFactura = idFactura; }

    public int getIdButaca() { return idButaca; }
    public void setIdButaca(int idButaca) { this.idButaca = idButaca; }

    public String getButaca() {
        return butaca;
    }

    public String getFuncion() {
        return funcion;
    }

    @Override
    public void calcularPrecio() {        
        //double precioBase = 10.0; // Precio base por boleto
        double precioTipoDeSala = 1.0;
        double precioFormatoFuncion = 1.0;
        double precioTipoFuncion = 1.0;
        double precioHorario = 1.0;
        setPrecio(precioTipoDeSala + precioFormatoFuncion + precioTipoFuncion + precioHorario);
    }

    @Override
    public String toString() {
        return "Boleto{" +
                "idFuncion='" + idFuncion + '\n' +
                ", idButaca='" + idButaca + '\n' +
                ", precioUnitario=" + getPrecio() +
                '}';
    }

}
