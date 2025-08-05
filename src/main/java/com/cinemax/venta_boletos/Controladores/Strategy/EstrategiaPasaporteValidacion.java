package com.cinemax.venta_boletos.controladores.strategy;

public class EstrategiaPasaporteValidacion implements EstrategiaValidacion{

    @Override
    public boolean ejecutarEstrategia(String documento) {
        return validarPasaporte(documento);
    }

    private boolean validarPasaporte(String documento) {
        return documento.matches("^[A-Z]{1}[0-9]{6,9}$");
    }

    
    
}
