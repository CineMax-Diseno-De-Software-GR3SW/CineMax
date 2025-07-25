package com.cinemax.venta_boletos.Servicios;

public class ContenidoMensaje {
    private String asunto;
    private String cuerpo;

    public ContenidoMensaje(String asunto, String cuerpo) {
        this.asunto = asunto;
        this.cuerpo = cuerpo;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getCuerpo() {
        return cuerpo;
    }
} 