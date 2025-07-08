package com.cinemax.peliculas.modelos.entidades;

public enum FormatoFuncion {
    DOS_D, // 2D
    TRES_D; // 3D

    @Override
    public String toString() {
        switch (this) {
            case DOS_D: return "2D";
            case TRES_D: return "3D";
            default: return super.toString();
        }
    }

    public static FormatoFuncion fromString(String value) {
        switch (value) {
            case "2D": return DOS_D;
            case "3D": return TRES_D;
            default: throw new IllegalArgumentException("Formato desconocido: " + value);
        }
    }
}