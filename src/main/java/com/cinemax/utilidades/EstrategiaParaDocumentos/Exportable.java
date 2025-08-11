package com.cinemax.utilidades.estrategiaParaDocumentos;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.cinemax.reportes.modelos.entidades.ReporteGenerado;

public interface Exportable {
    void exportar(ReporteGenerado reporte, File archivo, Map<String, Object> datos) throws Exception;

    void exportarFormatoPrincipal(List<Map<String, Object>> datos, File destino, String tituloReporte,
            Map<String, Object> infoExtra) throws Exception;
}
