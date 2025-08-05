package com.cinemax.venta_boletos.controladores.strategy;

public class ContextoValidacion {
    private EstrategiaValidacion estrategia;

    public void setEstrategia(EstrategiaValidacion estrategia) {
        this.estrategia = estrategia;
    }

    public boolean ejecutarEstrategia(String documento) {
        return estrategia.ejecutarEstrategia(documento);
    }
}
