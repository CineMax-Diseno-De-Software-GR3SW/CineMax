package com.cinemax.venta_boletos.Controladores;

public interface Manejador {
    void colocarSiguienteManejador(Manejador manejador);
    void manejarPeticion(String peticion);
}
