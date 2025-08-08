# 📊 GUÍA DE ESTRUCTURA DEL DAO - MÓDULO DE REPORTES

## 🎯 **OBJETIVO**

Esta guía explica la estructura correcta del Data Access Object (DAO) para el módulo de Reportes de CineMax, siguiendo las mejores prácticas de programación y patrones de diseño.

---

## 🏗️ **ARQUITECTURA DEL DAO**

### **1. INTERFAZ (IReporteDAO)**
```java
public interface IReporteDAO {
    // Métodos para reportes de ventas
    List<ReporteVentaDTO> obtenerVentas(...);
    ReporteVentaDTO obtenerResumenVentas(...);
    
    // Métodos para reportes programados
    Long guardarReporteProgramado(ReporteGenerado reporte);
    List<ReporteGenerado> obtenerReportesProgramados();
    Optional<ReporteGenerado> obtenerReportePorId(Long id);
    boolean actualizarEstadoReporte(...);
    boolean eliminarReporteProgramado(Long id);
    List<ReporteGenerado> obtenerReportesPendientes(...);
}
```

**✅ Ventajas de usar interfaz:**
- **Principio de inversión de dependencias**
- **Facilita testing con mocks**
- **Permite múltiples implementaciones**
- **Desacoplamiento entre capas**

### **2. IMPLEMENTACIÓN (ReporteDAO)**
```java
public class ReporteDAO implements IReporteDAO {
    private static final Logger LOGGER = Logger.getLogger(ReporteDAO.class.getName());
    
    // Constantes SQL para evitar SQL injection y mejorar mantenimiento
    private static final String SQL_OBTENER_VENTAS = "...";
    
    // Implementación de todos los métodos de la interfaz
}
```

### **3. EXCEPCIÓN PERSONALIZADA (DAOException)**
```java
public class DAOException extends RuntimeException {
    // Constructores para diferentes tipos de errores
    public DAOException(String message);
    public DAOException(String message, Throwable cause);
    public DAOException(Throwable cause);
}
```

---

## 📋 **ESTRUCTURA DE MÉTODOS**

### **🔍 MÉTODOS DE CONSULTA**

#### **1. Obtener Ventas Filtradas**
```java
public List<ReporteVentaDTO> obtenerVentas(
    LocalDate desde, 
    LocalDate hasta, 
    String sala, 
    String tipoBoleto, 
    String horario
) throws DAOException
```

**Características:**
- ✅ **Validación de parámetros** al inicio
- ✅ **Construcción dinámica de SQL** según filtros
- ✅ **PreparedStatement** para prevenir SQL injection
- ✅ **Manejo de excepciones** con logging
- ✅ **Mapeo automático** de ResultSet a DTOs

#### **2. Obtener Resumen de Ventas**
```java
public ReporteVentaDTO obtenerResumenVentas(
    LocalDate desde, 
    LocalDate hasta
) throws DAOException
```

**Características:**
- ✅ **Consultas agregadas** (SUM, COUNT, AVG)
- ✅ **Optimización de rendimiento**
- ✅ **Retorno de DTO específico** para resúmenes

### **💾 MÉTODOS DE PERSISTENCIA**

#### **1. Guardar Reporte Programado**
```java
public Long guardarReporteProgramado(ReporteGenerado reporte) throws DAOException
```

**Características:**
- ✅ **Retorno del ID generado** automáticamente
- ✅ **Validación de entidad** antes de guardar
- ✅ **Manejo de transacciones** implícito

#### **2. Actualizar Estado**
```java
public boolean actualizarEstadoReporte(
    Long id, 
    String nuevoEstado, 
    LocalDateTime nuevaFecha
) throws DAOException
```

**Características:**
- ✅ **Retorno booleano** para confirmar éxito
- ✅ **Actualización de timestamps** automática
- ✅ **Logging de cambios** importantes

---

## 🛡️ **MEJORES PRÁCTICAS IMPLEMENTADAS**

### **1. SEGURIDAD**
```java
// ❌ MAL: Vulnerable a SQL injection
String sql = "SELECT * FROM ventas WHERE sala = '" + sala + "'";

// ✅ BIEN: PreparedStatement
PreparedStatement ps = conn.prepareStatement("SELECT * FROM ventas WHERE sala = ?");
ps.setString(1, sala);
```

### **2. LOGGING**
```java
private static final Logger LOGGER = Logger.getLogger(ReporteDAO.class.getName());

// Logging de operaciones importantes
LOGGER.info("Ejecutando consulta de ventas: " + sql);
LOGGER.log(Level.SEVERE, "Error al obtener ventas", e);
```

### **3. MANEJO DE RECURSOS**
```java
// ✅ Uso de try-with-resources
try (Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
     PreparedStatement ps = conn.prepareStatement(sql);
     ResultSet rs = ps.executeQuery()) {
    // Procesamiento
}
```

### **4. VALIDACIÓN DE PARÁMETROS**
```java
public List<ReporteVentaDTO> obtenerVentas(...) throws DAOException {
    if (desde == null || hasta == null) {
        throw new IllegalArgumentException("Las fechas no pueden ser null");
    }
    
    if (desde.isAfter(hasta)) {
        throw new IllegalArgumentException("Fecha desde no puede ser posterior a hasta");
    }
    // ...
}
```

### **5. CONSTANTES SQL**
```java
// ✅ SQL como constantes para mantenimiento
private static final String SQL_OBTENER_VENTAS = 
    "SELECT fecha, boletos_vendidos, ingresos FROM ventas WHERE fecha BETWEEN ? AND ?";
```

---

## 🗄️ **ESTRUCTURA DE BASE DE DATOS**

### **TABLAS PRINCIPALES**

#### **1. Tabla `ventas`**
```sql
CREATE TABLE ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    boletos_vendidos INT NOT NULL DEFAULT 0,
    ingresos DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    sala VARCHAR(10) NOT NULL,
    tipo_boleto VARCHAR(20) NOT NULL,
    horario VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### **2. Tabla `reportes_programados`**
```sql
CREATE TABLE reportes_programados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_reporte VARCHAR(255) NOT NULL,
    fecha_generacion TIMESTAMP NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'Programado',
    ruta_archivo VARCHAR(500),
    frecuencia VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### **ÍNDICES OPTIMIZADOS**
```sql
-- Índices para consultas frecuentes
INDEX idx_fecha (fecha),
INDEX idx_sala (sala),
INDEX idx_tipo_boleto (tipo_boleto),
INDEX idx_horario (horario),
INDEX idx_fecha_sala (fecha, sala),
INDEX idx_estado_fecha (estado, fecha_generacion)
```

---

## 🔄 **PATRÓN DE USO**

### **1. EN EL CONTROLADOR**
```java
public class ControladorReportesPrincipal {
    private IReporteDAO reporteDAO = new ReporteDAO();
    
    @FXML
    private void onFiltrar(ActionEvent event) {
        try {
            LocalDate desde = dateDesde.getValue();
            LocalDate hasta = dateHasta.getValue();
            
            List<ReporteVentaDTO> ventas = reporteDAO.obtenerVentas(
                desde, hasta, sala, tipoBoleto, horario
            );
            
            // Actualizar UI con los datos
            actualizarTabla(ventas);
            
        } catch (DAOException e) {
            mostrarError("Error al obtener datos: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            mostrarError("Datos de entrada inválidos: " + e.getMessage());
        }
    }
}
```

### **2. EN EL SERVICIO**
```java
public class ServicioReportes {
    private IReporteDAO reporteDAO = new ReporteDAO();
    
    public void programarReporte(ReporteGenerado reporte) {
        try {
            Long id = reporteDAO.guardarReporteProgramado(reporte);
            LOGGER.info("Reporte programado con ID: " + id);
            
        } catch (DAOException e) {
            LOGGER.severe("Error al programar reporte: " + e.getMessage());
            throw new ServicioException("No se pudo programar el reporte", e);
        }
    }
}
```

---

## 🧪 **TESTING**

### **1. MOCK PARA TESTING**
```java
@Test
public void testObtenerVentas() {
    // Arrange
    IReporteDAO mockDAO = mock(IReporteDAO.class);
    List<ReporteVentaDTO> ventasEsperadas = Arrays.asList(
        new ReporteVentaDTO("2024-01-01", 100, 3000.0)
    );
    
    when(mockDAO.obtenerVentas(any(), any(), any(), any(), any()))
        .thenReturn(ventasEsperadas);
    
    // Act
    List<ReporteVentaDTO> resultado = mockDAO.obtenerVentas(
        LocalDate.now(), LocalDate.now(), null, null, null
    );
    
    // Assert
    assertEquals(ventasEsperadas, resultado);
}
```

### **2. TEST DE INTEGRACIÓN**
```java
@Test
public void testConexionReal() {
    ReporteDAO dao = new ReporteDAO();
    
    // Test con base de datos real
    List<ReporteVentaDTO> ventas = dao.obtenerVentas(
        LocalDate.now().minusDays(7), 
        LocalDate.now(), 
        null, null, null
    );
    
    assertNotNull(ventas);
    assertFalse(ventas.isEmpty());
}
```

---

## 📈 **OPTIMIZACIONES**

### **1. CONSULTAS DINÁMICAS**
```java
private String construirQueryVentas(String sala, String tipoBoleto, String horario) {
    if (sala != null && !sala.equalsIgnoreCase("Todas")) {
        if (tipoBoleto != null && !tipoBoleto.equalsIgnoreCase("Todos")) {
            return SQL_OBTENER_VENTAS_COMPLETO;
        }
        return SQL_OBTENER_VENTAS_POR_SALA;
    }
    // ... más lógica
    return SQL_OBTENER_VENTAS;
}
```

### **2. MAPEO EFICIENTE**
```java
private ReporteVentaDTO mapearResultSetAVentaDTO(ResultSet rs) throws SQLException {
    return new ReporteVentaDTO(
        rs.getString("fecha"),
        rs.getInt("boletos_vendidos"),
        rs.getDouble("ingresos")
    );
}
```

### **3. POOL DE CONEXIONES**
```java
// Usar el singleton de conexión que maneja el pool
Connection conn = ConexionBaseSingleton.getInstancia().getConexion();
```

---

## 🚀 **PRÓXIMOS PASOS**

### **1. IMPLEMENTACIONES FUTURAS**
- [ ] **Cache de consultas** frecuentes
- [ ] **Paginación** para grandes volúmenes de datos
- [ ] **Transacciones** explícitas para operaciones complejas
- [ ] **Métricas de rendimiento** de consultas

### **2. MEJORAS SUGERIDAS**
- [ ] **Query Builder** para consultas complejas
- [ ] **Batch operations** para inserciones masivas
- [ ] **Auditoría** automática de cambios
- [ ] **Compresión** de datos históricos

---

## 📚 **RECURSOS ADICIONALES**

- **Script SQL completo**: `src/main/resources/sql/reportes_tables.sql`
- **Documentación de la API**: Javadoc en cada método
- **Ejemplos de uso**: Tests unitarios e integración
- **Diagramas de base de datos**: En carpeta `Documentacion/Diagramas/`

---

*Esta estructura del DAO sigue las mejores prácticas de la industria y proporciona una base sólida para el crecimiento futuro del módulo de reportes.* 🎯 