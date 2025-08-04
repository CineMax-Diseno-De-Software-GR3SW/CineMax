package com.cinemax.venta_boletos.Controladores;

public class ManejadorBaseComun implements Manejador{

    private Manejador siguienteManejador;

    @Override
    public void colocarSiguienteManejador(Manejador manejador) {
        this.siguienteManejador = manejador;
    }

    @Override
    public void manejarPeticion(String peticion) {
        if (peticion == null || peticion.isEmpty()) {
            System.out.println("Documento no puede estar vacío");
            return;
        }
        // Si hay un siguiente manejador, se delega la petición a él
        if (siguienteManejador == null) {
            // Manejo por defecto
            System.out.println("ManejadorBaseComun: Manejo de la petición " + peticion);
            return;
        }
    }

}
