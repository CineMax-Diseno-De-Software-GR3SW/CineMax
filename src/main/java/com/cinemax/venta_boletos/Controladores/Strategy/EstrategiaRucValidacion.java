package com.cinemax.venta_boletos.controladores.strategy;

public class EstrategiaRucValidacion extends EstrategiaCedulaValidacion {

    @Override
    public boolean ejecutarEstrategia(String documento) {

        if (documento == null || documento.length() != 13) {
            System.out.println("El RUC " + documento + " no es válido, debe tener 13 caracteres");
            return false;
        }
        String cedula = documento.substring(0,10);
        
        boolean cedulaValida = super.ejecutarEstrategia(cedula);
        boolean rucValido = validarRUC(documento);
        return cedulaValida && rucValido;
    }

    private boolean validarRUC(String documento) {

        if(!documento.substring(10).matches("\\d{3}")) {
            System.out.println("El RUC " + documento + " no es un número válido de 3 dígitos");
            return false;
        }

        return true;
    }

}
