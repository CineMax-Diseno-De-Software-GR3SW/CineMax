package com.cinemax.venta_boletos.controladores.cadena_de_responsabilidad;

public class ManejadorRUC extends ManejadorBaseComun {

    @Override
    public void manejarPeticion(String peticion) {
        if (peticion.length() == 13 && peticion.matches("[0-9]+")) {
            validarRUC(peticion);
            return;
        } else {
            super.manejarPeticion(peticion);
        }
    }

    // Función que valida el RUC según las reglas establecidas
    private void validarRUC(String ruc) {
        // Implementación de la validación del RUC
        // Aquí se pueden agregar las reglas específicas para validar el RUC
        System.out.println("Validando RUC: " + ruc);
        // Lógica de validación...
    }

}
