package com.cinemax.venta_boletos.Servicios.cadena_de_responsabilidad;

public class ValidadorDocumentos {
    private Manejador manejador;

    public ValidadorDocumentos(Manejador manejador) {
        this.manejador = manejador;
    }

    public void validarDocumento(String documento) {
        

        // Delegar la validación al manejador correspondiente
        manejador.manejarPeticion(documento);
    }
}
