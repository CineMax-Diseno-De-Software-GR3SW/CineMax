package com.cinemax.comun.EstrategiaValidacionDocumentos;

import com.cinemax.comun.ManejadorMetodosComunes;

/**
 * Estrategia de validación específica para cédulas ecuatorianas.
 * 
 * Implementa el algoritmo oficial de validación de cédulas de identidad del Ecuador,
 * que incluye verificación de formato, código de provincia y dígito verificador.
 * 
 * Reglas de validación implementadas:
 * - Longitud exacta de 10 dígitos
 * - Solo caracteres numéricos
 * - Código de provincia válido (01-24)
 * - Tercer dígito entre 0-5
 * - Algoritmo de módulo 10 para el dígito verificador
 * 
 * @author GR3SW
 * @version 1.0
 */
public class EstrategiaCedulaValidacion implements EstrategiaValidacion{

    /**
     * Ejecuta la validación de cédula ecuatoriana.
     * 
     * Implementa todas las reglas de validación requeridas:
     * 1. Verificación de longitud (10 dígitos)
     * 2. Validación de caracteres numéricos
     * 3. Verificación del código de provincia (01-24)
     * 4. Validación del tercer dígito (0-5)
     * 5. Cálculo y verificación del dígito verificador
     * 
     * @param documento Número de cédula a validar
     * @return true si la cédula es válida, false en caso contrario
     */
    @Override
    public boolean ejecutarEstrategia(String documento) {
        int digitosDeLaCedula;        // Variable para almacenar dígitos extraídos
        int acumulador = 0;           // Suma acumulada para el algoritmo del dígito verificador
        String decimoDigitoCalculado; // Dígito verificador calculado

        try {

            // VALIDACIÓN 1: Verificar longitud exacta de 10 dígitos
            if (documento.length() != 10) {
                System.out.println("La cédula " + documento + " no consta de 10 dígitos");
                return false;
            }

            // VALIDACIÓN 2: Verificar que todos los caracteres sean dígitos numéricos
            if (!documento.matches("\\d+")) {
                System.out.println("La cédula debe contener solo números");
                return false;
            }

            // VALIDACIÓN 3: Verificar código de provincia (primeros 2 dígitos: 01-24)
            digitosDeLaCedula = Integer.parseInt(documento.substring(0, 2));
            if (digitosDeLaCedula < 1 || digitosDeLaCedula > 24) {
                System.out.println("La cédula " + documento + " sus 2 primeros dígitos no son válidos (deben estar entre 1 y 24)");
                return false;
            }

            // VALIDACIÓN 4: Verificar tercer dígito (debe estar entre 0-5)
            digitosDeLaCedula = Integer.parseInt(documento.substring(2, 3));
            if (digitosDeLaCedula < 0 || digitosDeLaCedula > 5) {
                System.out.println("El tercer dígito debe estar entre 0 y 5");
                return false;
            }

            // VALIDACIÓN 5: Algoritmo de cálculo del dígito verificador (módulo 10)
            // Procesar los primeros 9 dígitos con coeficientes alternados (2,1,2,1...)
            for (int contadorDígitos = 0; contadorDígitos <= 8; contadorDígitos++) {
                // Coeficiente multiplicador: 2 para posiciones pares, 1 para impares
                int coeficienteMultiplicador = (contadorDígitos % 2 == 0) ? 2 : 1;
                
                // Extraer el dígito actual de la cédula
                digitosDeLaCedula = Integer.parseInt(documento.substring(contadorDígitos, contadorDígitos + 1));
                
                // Multiplicar dígito por su coeficiente
                int coeficientePorDigitoDeCedula = coeficienteMultiplicador * digitosDeLaCedula;
                
                // Si el producto es >= 10, sumar sus dígitos (equivale a restar 9)
                // Ejemplo: 18 -> 1+8 = 9, o 18-9 = 9
                acumulador += (coeficientePorDigitoDeCedula >= 10) ? (coeficientePorDigitoDeCedula - 9) : coeficientePorDigitoDeCedula;                
            }

            // Calcular el dígito verificador usando módulo 10
            int residuo = acumulador % 10;
            // Si residuo es 0, el dígito verificador es 0; sino es (10 - residuo)
            decimoDigitoCalculado = residuo == 0 ? "0" : String.valueOf(10-residuo);

            // Comparar dígito verificador calculado con el décimo dígito de la cédula
            boolean validacionFinal = documento.substring(9, 10).equals(decimoDigitoCalculado);
            
            // Log para depuración y seguimiento de la validación
            System.out.println("Validación final: décimo digito calculado-> " + decimoDigitoCalculado + 
                             ", décimo digito ingresado-> " + documento.substring(9, 10) + 
                             ", resultado-> " + validacionFinal);
            return validacionFinal;

        } catch (NumberFormatException e) {
            // Error al convertir string a número - formato inválido
            ManejadorMetodosComunes.mostrarVentanaError("La cédula '" + documento + "' no tiene el formato válido");
            System.out.println("Error de formato en la cédula '" + documento + "'");
            return false;
        } catch (Exception e) {
            // Error inesperado durante la validación
            ManejadorMetodosComunes.mostrarVentanaError("Error inesperado al validar la cédula '" + documento + "'");
            System.out.println("Error inesperado al validar la cédula " + documento);
            return false;
        }
    } 

}
