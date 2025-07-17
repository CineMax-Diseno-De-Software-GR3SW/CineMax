package com.cinemax.venta_boletos.Util;

import javafx.scene.Scene;

public class ThemeManager {
    private static ThemeManager instance;
    private static final String LIGHT_THEME_PATH = "/vistas/temas/venta_boletos/synthwave-purpura.css";
    private static final String DARK_THEME_PATH = "/vistas/temas/venta_boletos/ayu-theme.css";
    private boolean isLightMode = true;

    private ThemeManager() {}

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        String themePath = isLightMode ? LIGHT_THEME_PATH : DARK_THEME_PATH;
        scene.getStylesheets().add(getClass().getResource(themePath).toExternalForm());
    }

    public void toggleTheme(Scene scene) {
        isLightMode = !isLightMode;
        applyTheme(scene);
    }
}
