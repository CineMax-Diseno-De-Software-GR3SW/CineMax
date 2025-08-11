package com.cinemax.utilidades.EstrategiaValidacionDocumentos;

/**
 * Estrategia de validación específica para números de pasaporte.
 * 
 * @author GR3SW
 * @version 1.0
 */
public class EstrategiaPasaporteValidacion implements EstrategiaValidacion{

    /**
     * Ejecuta la validación de formato para pasaportes.
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
