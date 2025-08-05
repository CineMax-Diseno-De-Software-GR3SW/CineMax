package com.cinemax.venta_boletos.controladores.cadena_de_responsabilidad;
public interface Manejador {
    void colocarSiguienteManejador(Manejador manejador);
    void manejarPeticion(String peticion);
}
