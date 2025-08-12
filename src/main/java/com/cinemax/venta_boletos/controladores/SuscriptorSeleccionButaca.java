package com.cinemax.venta_boletos.controladores;

import com.cinemax.salas.modelos.entidades.Butaca;

public interface SuscriptorSeleccionButaca {
    void agregarButacaSeleccionada(Butaca butaca);
    void eliminarButacaSeleccionada(Butaca butaca);
}
