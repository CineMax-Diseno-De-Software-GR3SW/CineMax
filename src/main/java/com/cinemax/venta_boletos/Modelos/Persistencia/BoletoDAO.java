package com.cinemax.venta_boletos.Modelos.Persistencia;

import com.cinemax.comun.ConexionBaseSingleton;

public class BoletoDAO {
    
    private final ConexionBaseSingleton conexionBase;

    public BoletoDAO() {
        this.conexionBase = ConexionBaseSingleton.getInstancia();
    }
}
