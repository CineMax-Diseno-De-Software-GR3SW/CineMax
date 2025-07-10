package com.cinemax.venta_boletos.Modelos;

import java.util.List;

public class CineMax {

    private boolean cerrado;

    List<String> opciones;
    
    public CineMax() {
        cerrado = false;
        opciones = List.of("1. Gestionar películas","2. Gestionar Salas", "3. Gestionar boletos", "4. Gestionar Empleados","5. Gestionar Reportes","6. Salir");
    }

    public List<String> obtenerOpciones() {
        return opciones;
    }

    public void cerrar(){
        cerrado = true;
    }

    public boolean verificarSiEstaCerrado(){
        return cerrado;
    }

}
