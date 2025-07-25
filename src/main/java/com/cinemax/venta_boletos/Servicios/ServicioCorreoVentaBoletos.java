package com.cinemax.venta_boletos.Servicios;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

public class ServicioCorreoVentaBoletos {
    private final String remitente = "notificaciones.cinemax@gmail.com";
    private final String clave = "zcbdvjxpnngnptgs";
    private final Session sesion;

    public ServicioCorreoVentaBoletos() throws MessagingException {
        this.sesion = crearSesionSMTP();
    }

    private Session crearSesionSMTP() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });
    }

    public boolean enviarCorreoConAdjunto(String destinatario, ContenidoMensaje contenido, File adjunto) {
        try {
            Message mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(remitente));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject(contenido.getAsunto());

            MimeBodyPart cuerpoMensaje = new MimeBodyPart();
            cuerpoMensaje.setContent(contenido.getCuerpo(), "text/html; charset=utf-8");

            MimeBodyPart adjuntoPart = new MimeBodyPart();
            adjuntoPart.attachFile(adjunto);

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(cuerpoMensaje);
            multipart.addBodyPart(adjuntoPart);

            mensaje.setContent(multipart);

            Transport.send(mensaje);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
} 