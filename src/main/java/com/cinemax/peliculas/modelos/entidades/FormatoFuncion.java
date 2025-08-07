package com.cinemax.peliculas.modelos.entidades;

import java.math.BigDecimal;

/**
 * Enumeración que representa los diferentes formatos de proyección disponibles
 * para las funciones de cine, con sus respectivos multiplicadores de precio.
 *
 * <p>Esta enumeración define los tipos de formato tecnológico que pueden
 * utilizarse para proyectar películas, cada uno con un costo diferenciado
 * que se refleja en el precio del boleto.
 *
 * <p>Formatos disponibles:
 * <ul>
 *   <li>2D: Formato estándar tradicional sin recargo adicional</li>
 *   <li>3D: Formato tridimensional con 50% de recargo sobre el precio base</li>
 * </ul>
 *
 * @author GR3SW
 * @version 1.0
 * @since 1.0
 */
public enum FormatoFuncion {
    /** Formato 2D estándar - sin recargo adicional */
    DOS_D(new BigDecimal("1.0")),

    /** Formato 3D - 50% de recargo sobre el precio base */
    TRES_D(new BigDecimal("1.50"));

    /** Multiplicador de precio que se aplica al costo base del boleto */
    private final BigDecimal multiplicadorPrecio;

    /**
     * Constructor privado para inicializar cada formato con su multiplicador de precio.
     *
     * @param multiplicadorPrecio Factor multiplicativo que se aplicará al precio base.
     *                           Debe ser mayor que cero.
     */
    FormatoFuncion(BigDecimal multiplicadorPrecio) {
        this.multiplicadorPrecio = multiplicadorPrecio;
    }

    /**
     * Obtiene el multiplicador de precio para este formato de función.
     *
     * <p>Este multiplicador se utiliza para calcular el precio final del boleto
     * multiplicando el precio base por este valor.
     *
     * @return El multiplicador que se aplicará al precio base del boleto
     */
    public BigDecimal getMultiplicadorPrecio() {
        return multiplicadorPrecio;
    }

    /**
     * Convierte el formato a su representación textual legible.
     *
     * @return Representación en cadena del formato (ej: "2D", "3D")
     */
    @Override
    public String toString() {
        switch (this) {
            case DOS_D: return "2D";
            case TRES_D: return "3D";
            default: return super.toString();
        }
    }

    /**
     * Convierte una cadena de texto a su enum FormatoFuncion correspondiente.
     *
     * <p>Este método permite crear instancias del enum a partir de representaciones
     * textuales, útil para deserialización o procesamiento de datos externos.
     *
     * @param value Cadena que representa el formato ("2D" o "3D")
     * @return El enum FormatoFuncion correspondiente
     * @throws IllegalArgumentException Si el valor proporcionado no corresponde
     *                                  a ningún formato conocido
     */
    public static FormatoFuncion fromString(String value) {
        switch (value) {
            case "2D": return DOS_D;
            case "3D": return TRES_D;
            default: throw new IllegalArgumentException("Formato desconocido: " + value);
        }
    }
}