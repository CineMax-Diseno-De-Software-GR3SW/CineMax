package com.cinemax.reportes.modelos;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface Exportable {
    void exportar(ReporteGenerado reporte, File archivo, Map<String, Object> datos) throws Exception;

    void exportarFormatoPrincipal(List<Map<String, Object>> datos, File destino, String tituloReporte,
            Map<String, Object> infoExtra) throws Exception;
}
