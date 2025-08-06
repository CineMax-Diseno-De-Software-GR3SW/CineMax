package com.cinemax.venta_boletos.servicios.cadena_de_responsabilidad;

import com.cinemax.comun.ManejadorMetodosComunes;

public class ManejadorCedula extends ManejadorBaseComun {

    @Override
    public void manejarPeticion(String peticion) {
        if(peticion.length() == 10 && peticion.matches("[0-9]+")) {
            validarCedula(peticion);
        } else {
            super.manejarPeticion(peticion);
        }
    }

    // Función que valida la cédula según las reglas establecidas
    private void validarCedula(String cedula) {
        int digitosDeLaCedula;
        int acumulador = 0;
        String decimoDigitoCalculado;

        try {

            // Validar que los primeros 2 dígitos (provincia) están en el rango 1-24
            digitosDeLaCedula = Integer.parseInt(cedula.substring(0, 2));
            if (digitosDeLaCedula <= 0 || digitosDeLaCedula > 24) {
                System.out.println("La cédula " + cedula + " sus 2 primeros dígitos no son válidos (deben estar entre 1 y 24)");
                return;
            }

            // Validar que el tercer dígito esté en el rango 0-5
            digitosDeLaCedula = Integer.parseInt(cedula.substring(2, 3));
            if (digitosDeLaCedula >= 6 || digitosDeLaCedula < 0) {
                System.out.println("La cédula " + cedula + " su tercer dígito no es válido"+" (debe estar entre 0 y 5)");
                return;
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
            String mensajeFinal =
                    (!cedula.substring(9, 10).equals(decimoDigitoCalculado)) ?
                            "La cédula "+cedula+" su décimo digito es incorrecto" :
                            "La cedula "+cedula+" es válida";
            System.out.println(mensajeFinal);

        } catch (NumberFormatException e) {
            ManejadorMetodosComunes.mostrarVentanaError("La cédula '" + cedula + "' no tiene el formato válido");
            System.out.println("Error de formato en la cédula '" + cedula);
        } catch (Exception e) {
            ManejadorMetodosComunes.mostrarVentanaError("Error inesperado al validar la cédula '" + cedula + "'");
            System.out.println("Error inesperado al validar la cédula '" + cedula);
        }
    }

    // Funcion para calcular el décimo dígito de la cédula
    private String calcularDecimoDigito(int acumulador) {
        int decenaSuperior = (((acumulador / 10) % 10) + 1) * 10;
        acumulador = decenaSuperior - acumulador;
        return acumulador + "";
    }

}
