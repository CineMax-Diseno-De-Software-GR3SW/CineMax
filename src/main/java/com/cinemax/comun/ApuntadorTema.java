package com.cinemax.comun;

import javafx.scene.Scene;

import java.util.Objects;

public class ApuntadorTema {
    private static ApuntadorTema instance;
    private static final String DARK_THEME_PATH = "/vistas/temas/ayu-theme.css";

    private ApuntadorTema() {}

    public static ApuntadorTema getInstance() {
        if (instance == null) {
            instance = new ApuntadorTema();
        }
        return instance;
    }

    public void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(DARK_THEME_PATH)).toExternalForm());
    }
}
