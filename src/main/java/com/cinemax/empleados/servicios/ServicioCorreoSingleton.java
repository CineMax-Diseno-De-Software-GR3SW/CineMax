package com.cinemax.empleados.servicios;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class ServicioCorreoSingleton {

    private static ServicioCorreoSingleton instancia;

    // Definimos remitente y clave como constantes o variables finales
    private final String remitente = "notificaciones.cinemax@gmail.com";
    private final String clave = "zcbdvjxpnngnptgs";

    private final Session sesion;
    private Transport transport;

    private ServicioCorreoSingleton() throws MessagingException {
        this.sesion = crearSesionSMTP();
        this.transport = null;
    }

    // Método para obtener la instancia singleton (sin parámetros)
    public static synchronized ServicioCorreoSingleton getInstancia() throws MessagingException {
        if (instancia == null) {
            instancia = new ServicioCorreoSingleton();
        }
        return instancia;
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

    public boolean enviarCorreo(String destinatario, ContenidoMensaje contenido) {
        try {
            Message mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(remitente));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject(contenido.getAsunto());
            // Asumimos HTML, puedes cambiar según necesidad:
            mensaje.setContent(contenido.getCuerpo(), "text/html; charset=utf-8");

            Transport.send(mensaje);  // Maneja conexión internamente

//            System.out.println("✅ Correo enviado a: " + destinatario);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
//            System.out.println("❌ Error al enviar el correo a: " + destinatario);
            return false;
        }
    }
}



