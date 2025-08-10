package com.cinemax.reportes.modelos;

import java.io.File;
import java.util.Map;

public interface Exportable {
    void exportar(ReporteGenerado reporte, File archivo, Map<String, Object> datos) throws Exception;
}
