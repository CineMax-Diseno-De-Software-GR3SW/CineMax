package com.cinemax.empleados.servicios;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioClave {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String hashClave(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    public static boolean verificarClave(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }

    public static String generarClaveAleatoria() {
        final String MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";
        final String NUMEROS = "0123456789";
        final String ESPECIALES = "!@#$%&*";
        final String TODOS = MAYUSCULAS + MINUSCULAS + NUMEROS + ESPECIALES;
        final int LONGITUD = 12;

        SecureRandom random = new SecureRandom();
        StringBuilder clave = new StringBuilder();

        // Garantiza al menos un carácter de cada tipo
        clave.append(MAYUSCULAS.charAt(random.nextInt(MAYUSCULAS.length())));
        clave.append(MINUSCULAS.charAt(random.nextInt(MINUSCULAS.length())));
        clave.append(NUMEROS.charAt(random.nextInt(NUMEROS.length())));
        clave.append(ESPECIALES.charAt(random.nextInt(ESPECIALES.length())));

        // Completa el resto aleatoriamente
        for (int i = 4; i < LONGITUD; i++) {
            clave.append(TODOS.charAt(random.nextInt(TODOS.length())));
        }

        // Mezcla los caracteres para que no sigan un patrón predecible
        List<Character> caracteres = clave.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(caracteres, random);

        StringBuilder claveFinal = new StringBuilder();
        caracteres.forEach(claveFinal::append);

        return claveFinal.toString();
    }

}