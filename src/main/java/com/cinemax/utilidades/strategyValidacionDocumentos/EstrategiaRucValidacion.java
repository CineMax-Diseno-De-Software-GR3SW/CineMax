package com.cinemax.utilidades.strategyValidacionDocumentos;

/**
 * Estrategia de validación específica para RUC (Registro Único de Contribuyentes) ecuatoriano.
 * 
 * @author GR3SW
 * @version 1.0
 */
public class EstrategiaRucValidacion extends EstrategiaCedulaValidacion {

    /**
     * Ejecuta la validación completa del RUC ecuatoriano.
     *
     * @param documento Número de RUC completo de 13 dígitos
     * @return true si tanto la cédula como el código de establecimiento son válidos
     */
    @Override
    public boolean ejecutarEstrategia(String documento) {

        // VALIDACIÓN 1: Verificar longitud exacta de 13 dígitos
        if (documento == null || documento.length() != 13) {
            System.out.println("El RUC " + documento + " no es válido, debe tener 13 caracteres");
            return false;
        }
        
        // VALIDACIÓN 2: Extraer y validar la cédula (primeros 10 dígitos)
        String cedula = documento.substring(0,10);
        boolean cedulaValida = super.ejecutarEstrategia(cedula);
        
        // VALIDACIÓN 3: Validar código de establecimiento (últimos 3 dígitos)
        // Extraer últimos 3 dígitos y verificar que sean numéricos
        boolean rucValido = documento.substring(10).matches("\\d{3}");
        if(!rucValido) {
            System.out.println("El RUC " + documento + " no es un número válido de 3 dígitos");
            return false;
        }
        
        // El RUC es válido solo si ambas partes son válidas
        return cedulaValida && rucValido;
    }

}
