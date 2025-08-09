package com.cinemax.comun.EstrategiaValidacionDocumentos;

/**
 * Estrategia de validación específica para RUC (Registro Único de Contribuyentes) ecuatoriano.
 * 
 * Extiende la validación de cédula para aprovechar su algoritmo y añade las
 * validaciones específicas del RUC. Un RUC válido consiste en:
 * - 10 primeros dígitos: cédula válida (reutiliza EstrategiaCedulaValidacion)
 * - 3 últimos dígitos: código de establecimiento (000-999)
 * 
 * Formato: CCCCCCCCCCXXX donde C=dígitos de cédula, X=código establecimiento
 * Ejemplo: 1234567890001
 * 
 * @author GR3SW
 * @version 1.0
 */
public class EstrategiaRucValidacion extends EstrategiaCedulaValidacion {

    /**
     * Ejecuta la validación completa del RUC ecuatoriano.
     * 
     * Realiza validación en dos etapas:
     * 1. Valida los primeros 10 dígitos como cédula (hereda algoritmo de la clase padre)
     * 2. Valida los últimos 3 dígitos como código de establecimiento  
     * El código de establecimiento debe ser un número de exactamente 3 dígitos
     * que representa la sucursal o establecimiento del contribuyente.
     * Rango válido: 000-999
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
