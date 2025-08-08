package com.cinemax.venta_boletos.servicios.strategy;

/**
 * Contexto del patrón Strategy para validación de documentos en el módulo de venta de boletos.
 * 
 * Esta clase implementa el patrón de diseño Strategy, permitiendo cambiar dinámicamente
 * el algoritmo de validación de documentos según el tipo requerido (cédula, pasaporte, etc.).
 * 
 * El patrón Strategy permite:
 * - Intercambiar algoritmos de validación en tiempo de ejecución
 * - Agregar nuevos tipos de validación sin modificar código existente
 * - Separar la lógica de validación de la lógica de negocio
 * 
 * Uso típico:
 * 1. Crear una instancia de ContextoValidacion
 * 2. Establecer la estrategia apropiada con setEstrategia()
 * 3. Ejecutar la validación con ejecutarEstrategia()
 * 
 * @author GR3SW
 * @version 1.0
 */
public class ContextoValidacion {
    /**
     * Referencia a la estrategia de validación actualmente configurada.
     * 
     * Esta variable almacena la implementación específica de EstrategiaValidacion
     * que se utilizará para validar documentos. Puede ser cambiada dinámicamente
     * para alternar entre diferentes tipos de validación (cédula, pasaporte, etc.).
     */
    private EstrategiaValidacion estrategia;

    /**
     * Establece la estrategia de validación a utilizar.
     * 
     * Este método permite cambiar el algoritmo de validación en tiempo de ejecución,
     * implementando el núcleo del patrón Strategy. Cada estrategia implementa
     * una forma específica de validar documentos.
     * 
     * @param estrategia La implementación de EstrategiaValidacion a utilizar
     *                  para las próximas validaciones. No debe ser null.
     */
    public void setEstrategia(EstrategiaValidacion estrategia) {
        // Asignar la nueva estrategia de validación
        this.estrategia = estrategia;
    }

    /**
     * Ejecuta la validación del documento utilizando la estrategia configurada.
     * 
     * Delega la validación a la estrategia actualmente establecida, permitiendo
     * que diferentes tipos de documentos sean validados con sus respectivas reglas
     * sin que esta clase necesite conocer los detalles específicos de cada validación.
     * 
     * Importante: Debe haberse configurado una estrategia válida mediante setEstrategia()
     * antes de llamar este método, de lo contrario se producirá una NullPointerException.
     * 
     * @param documento El número o código del documento a validar (cédula, pasaporte, etc.)
     * @return true si el documento es válido según la estrategia configurada,
     *         false en caso contrario
     * @throws NullPointerException si no se ha configurado ninguna estrategia
     */
    public boolean ejecutarEstrategia(String documento) {
        // Delegar la validación a la estrategia configurada
        return estrategia.ejecutarEstrategia(documento);
    }
}
