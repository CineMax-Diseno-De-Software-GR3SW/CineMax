package com.cinemax.empleados.Servicios;

import java.util.regex.Pattern;

public class ValidadorUsuario {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    public boolean validarCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(correo).matches();
    }
    
    public boolean validarClave(String clave) {
        if (clave == null || clave.length() < 6) {
            return false;
        }
        
        // Validar que contenga al menos una letra y un número
        boolean tieneLetra = clave.matches(".*[a-zA-Z].*");
        boolean tieneNumero = clave.matches(".*\\d.*");
        boolean tieneEspecial = clave.matches(".*[^a-zA-Z0-9].*"); // cualquier caracter que no sea letra o número

        return tieneLetra && tieneNumero && tieneEspecial;
    }
} 