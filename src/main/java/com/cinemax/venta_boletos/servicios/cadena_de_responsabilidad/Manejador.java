package com.cinemax.venta_boletos.servicios.cadena_de_responsabilidad;
public interface Manejador {
    void colocarSiguienteManejador(Manejador manejador);
    void manejarPeticion(String peticion);
}
