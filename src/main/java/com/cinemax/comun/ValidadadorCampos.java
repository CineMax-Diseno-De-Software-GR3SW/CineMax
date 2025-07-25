package com.cinemax.comun;
import java.util.regex.Pattern;

/**
 * Clase de utilidad para realizar validaciones de campos de texto.
 */
public class ValidadadorCampos {

    //Valida un correo electrónico.
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Valida que un texto contenga únicamente letras y espacios.
     * @param texto El texto a validar.
     * @return true si solo contiene letras y espacios, false en caso contrario.
     */
    public static boolean esSoloTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return false;
        }
        return texto.matches("[a-zA-Z ]+");
    }

    /**
     * Valida que un texto contenga únicamente números.
     * @param texto El texto a validar.
     * @return true si solo contiene números, false en caso contrario.
     */
    public static boolean esSoloNumeros(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return false;
        }
        return texto.matches("\\d+");
    }

    /**
     * Valida que un texto cumpla con un formato de correo electrónico válido.
     * @param correo El correo a validar.
     * @return true si el formato es válido, false en caso contrario.
     */
    public static boolean esCorreoValido(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(correo).matches();
    }
}
