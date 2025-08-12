package com.cinemax.empleados.servicios;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ServicioClave {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String hashClave(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    public static boolean verificarClave(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}