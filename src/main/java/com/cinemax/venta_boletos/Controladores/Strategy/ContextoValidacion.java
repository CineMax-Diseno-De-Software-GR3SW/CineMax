package com.cinemax.venta_boletos.Controladores.Strategy;

public class ContextoValidacion {
    private EstrategiaValidacion estrategia;

    public void setEstrategia(EstrategiaValidacion estrategia) {
        this.estrategia = estrategia;
    }

    public boolean ejecutarEstrategia(String documento) {
        return estrategia.ejecutarEstrategia(documento);
    }
}
