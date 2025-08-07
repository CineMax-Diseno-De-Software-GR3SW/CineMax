package com.cinemax.venta_boletos.Servicios.cadena_de_responsabilidad;
public interface Manejador {
    void colocarSiguienteManejador(Manejador manejador);
    void manejarPeticion(String peticion);
}
