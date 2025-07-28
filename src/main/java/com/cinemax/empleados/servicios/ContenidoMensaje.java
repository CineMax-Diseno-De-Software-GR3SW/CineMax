package com.cinemax.empleados.servicios;

public class ContenidoMensaje {

    private String asunto;
    private String cuerpo;  // puede ser texto plano o HTML

    public ContenidoMensaje(String asunto, String cuerpo) {
        this.asunto = asunto;
        this.cuerpo = cuerpo;
    }

    // Getters y setters
    public String getAsunto() {
        return asunto;
    }

    public String getCuerpo() {
        return cuerpo;
    }


    // Método estático para crear contenido para la creación de un nuevo usuario
    public static ContenidoMensaje crearMensajeCreacionUsuario(String nombreCompleto, String nombreUsuario, String nuevaContrasena) {
        String asunto = "Bienvenido a Cinemax!";

        String cuerpoHtml = "<html>" +
                "<body style='font-family: Arial, sans-serif; color:#333;'>" +
                "<h2>¡Hola " + nombreCompleto + "!</h2>" +
                "<p>Tu cuenta en <b>Cinemax</b> ha sido creada exitosamente.</p>" +
                "<p>A continuación encontrarás tus credenciales de acceso al sistema:</p>" +
                "<ul>" +
                "<li><b>Nombre de usuario:</b> " + nombreUsuario + "</li>" +
                "<li><b>Contraseña temporal:</b> <code>" + nuevaContrasena + "</code></li>" +
                "</ul>" +
                "<p>Te recomendamos cambiar tu contraseña en tu primer ingreso para mantener segura tu cuenta.</p>" +
                "<br>" +
                "<p>Saludos,<br>Equipo de soporte Cinemax</p>" +
                "<br>" +
                "<p><i>Por favor, no respondas a este mensaje. Este correo ha sido generado automáticamente.</i></p>" +
                "</body>" +
                "</html>";

        return new ContenidoMensaje(asunto, cuerpoHtml);
    }

    // Método estático para crear contenido de correo para recuperación de contraseña
    public static ContenidoMensaje crearMensajeRecuperacionContrasena(String nombreCompleto, String nombreUsuario, String contrasenaTemporal) {
        String asunto = "Recuperación de contraseña - Cinemax";

        String cuerpoHtml = "<html>" +
                "<body style='font-family: Arial, sans-serif; color:#333;'>" +
                "<h2>Hola " + nombreCompleto + ",</h2>" +
                "<p>Hemos recibido una solicitud para restablecer tu contraseña de <b>Cinemax</b>.</p>" +
                "<p>A continuación te proporcionamos una contraseña temporal para que puedas acceder nuevamente:</p>" +
                "<ul>" +
                "<li><b>Nombre de usuario:</b> " + nombreUsuario + "</li>" +
                "<li><b>Contraseña temporal:</b> <code>" + contrasenaTemporal + "</code></li>" +
                "</ul>" +
                "<p>Te recomendamos cambiar tu contraseña inmediatamente después de iniciar sesión por seguridad.</p>" +
                "<br>" +
                "<p>Si tú no solicitaste este cambio, por favor contáctanos.</p>" +
                "<br>" +
                "<p>Saludos,<br>Equipo de soporte Cinemax</p>" +
                "<br>" +
                "<p><i>Por favor, no respondas a este mensaje. Este correo ha sido generado automáticamente.</i></p>" +
                "</body>" +
                "</html>";

        return new ContenidoMensaje(asunto, cuerpoHtml);
    }


}

