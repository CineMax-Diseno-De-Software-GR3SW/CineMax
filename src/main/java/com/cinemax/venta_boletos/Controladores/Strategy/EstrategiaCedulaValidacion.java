package com.cinemax.venta_boletos.Controladores.Strategy;

import com.cinemax.comun.ManejadorMetodosComunes;

public class EstrategiaCedulaValidacion implements EstrategiaValidacion{

    @Override
    public boolean ejecutarEstrategia(String documento) {
        return validarCedula(documento);
    }

     // Función que valida la cédula según las reglas establecidas
    private boolean validarCedula(String cedula) {
        int digitosDeLaCedula;
        int acumulador = 0;
        String decimoDigitoCalculado;

        try {

            // Verificar si la longitud de la cédula es 10
            if (cedula.length() != 10) {
                System.out.println("La cédula " + cedula + " no consta de 10 dígitos");
                return false;
            }

            // Validar que los primeros 2 dígitos (provincia) están en el rango 1-24
            digitosDeLaCedula = Integer.parseInt(cedula.substring(0, 2));
            if (digitosDeLaCedula <= 0 || digitosDeLaCedula > 24) {
                System.out.println("La cédula " + cedula + " sus 2 primeros dígitos no son válidos (deben estar entre 1 y 24)");
                return false;
            }

            // Validar que el tercer dígito esté en el rango 0-5
            digitosDeLaCedula = Integer.parseInt(cedula.substring(2, 3));
            if (digitosDeLaCedula >= 6 || digitosDeLaCedula < 0) {
                System.out.println("La cédula " + cedula + " su tercer dígito no es válido"+" (debe estar entre 0 y 5)");
                return false;
            }

            // Algoritmo de cálculo relativo al décimo digito
            for (int contadorDígitos = 0; contadorDígitos <= 8; contadorDígitos++) {
                int coeficienteMultiplicador = (contadorDígitos % 2 == 0) ? 2 : 1;
                digitosDeLaCedula = Integer.parseInt(cedula.substring(contadorDígitos, contadorDígitos + 1));
                int coeficientePorDigitoDeCedula = coeficienteMultiplicador * digitosDeLaCedula;
                acumulador += (coeficientePorDigitoDeCedula >= 10) ? coeficientePorDigitoDeCedula - 9 : coeficientePorDigitoDeCedula;
            }

            // Calcular el décimo dígito
            decimoDigitoCalculado = (acumulador == 10) ? "10" : calcularDecimoDigito(acumulador);

            // Comparar el décimo dígito calculado con el de la cédula y mostrar el resultado
            boolean validacionFinal =
                    (!cedula.substring(9, 10).equals(decimoDigitoCalculado)) ?
                            false :
                            true;
            //System.out.println(validacionFinal);
            return validacionFinal;

        } catch (NumberFormatException e) {
            ManejadorMetodosComunes.mostrarVentanaError("La cédula '" + cedula + "' no tiene el formato válido");
            System.out.println("Error de formato en la cédula '" + cedula);
            return false;
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error inesperado al validar la cédula '" + cedula + "'");
            System.out.println("Error inesperado al validar la cédula '" + cedula);
            return false;
        }
    }

    // Funcion para calcular el décimo dígito de la cédula
    private String calcularDecimoDigito(int acumulador) {
        int decenaSuperior = (((acumulador / 10) % 10) + 1) * 10;
        acumulador = decenaSuperior - acumulador;
        return acumulador + "";
    }

}
