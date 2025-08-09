package com.cinemax.comun.EstrategiaValidacionDocumentos;

/**
 * Estrategia de validación específica para números de pasaporte.
 * 
 * Implementa una validación básica de formato para pasaportes usando
 * expresiones regulares. El formato validado consiste en:
 * - Una letra mayúscula inicial
 * - Seguida de 6 a 9 dígitos numéricos
 * 
 * Ejemplo de formato válido: A1234567, B123456789
 * 
 * @author GR3SW
 * @version 1.0
 */
public class EstrategiaPasaporteValidacion implements EstrategiaValidacion{

    /**
     * Ejecuta la validación de formato para pasaportes.
     * 
     * Patrón de validación: ^[A-Z]{1}[0-9]{6,9}$
     * - ^ : Inicio de cadena
     * - [A-Z]{1} : Exactamente una letra mayúscula
     * - [0-9]{6,9} : Entre 6 y 9 dígitos numéricos
     * - $ : Final de cadena
     * 
     * @param documento Número de pasaporte a validar
     * @return true si el formato es válido, false en caso contrario
     */
    @Override
    public boolean ejecutarEstrategia(String documento) {
        // Validar formato: 1 letra mayúscula + 6-9 dígitos
        return documento.matches("^[A-Z]{1}[0-9]{6,9}$");
    }
}
