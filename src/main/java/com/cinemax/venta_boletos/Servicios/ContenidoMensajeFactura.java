package com.cinemax.venta_boletos.Servicios;

import com.cinemax.empleados.servicios.ContenidoMensaje;
import com.cinemax.venta_boletos.Modelos.Factura;

public class ContenidoMensajeFactura {

    public static ContenidoMensaje crear(Factura factura) {
        String asunto = "Factura de su compra en CineMax";
        String cuerpo = "<div style='font-family:Arial,sans-serif;font-size:15px;color:#222;'>"
                + "<h2 style='color:#0078D7;'>¡Gracias por tu compra en CineMax!</h2>"
                + "<p>Estimado cliente,</p>"
                + "<p>Adjuntamos la factura correspondiente a tu reciente compra. Esperamos que disfrutes de tu experiencia en nuestro cine.</p>"
                + "<ul>"
                + "<li><b>Fecha de compra:</b> " + factura.getFecha() + "</li>"
                + "<li><b>Código de factura:</b> " + factura.getCodigoFactura() + "</li>"
                + "</ul>"
                + "<p>Si tienes alguna duda o necesitas asistencia, no dudes en contactarnos.</p>"
                + "<p style='margin-top:20px;'>¡Te esperamos pronto!</p>"
                + "<hr style='border:none;border-top:1px solid #eee;'>"
                + "<p style='font-size:13px;color:#888;'>CineMax - Tu mejor experiencia de cine</p>"
                + "</div>";
        return new ContenidoMensaje(asunto, cuerpo);
    }
}
