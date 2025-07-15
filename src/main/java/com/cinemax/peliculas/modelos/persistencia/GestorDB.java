package com.cinemax.peliculas.modelos.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class GestorDB {
    
    // Patrón Singleton
    private static GestorDB instancia;
    private Connection conexion;
    
    // Configuración de la base de datos PostgreSQL en Railway
    private static final String URL = "jdbc:postgresql://tramway.proxy.rlwy.net:18687/railway";
    private static final String USUARIO = "postgres";
    private static final String CONTRASEÑA = "tTCOwIHvtDnIJQZjalEcwvKbbKhGQJvl";
    private static final String DRIVER = "org.postgresql.Driver";
    
    // Constructor privado para Singleton
    private GestorDB() {
        try {
            // Cargar el driver de PostgreSQL
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error al cargar el driver de PostgreSQL: " + e.getMessage());
        }
    }
    
    // Método para obtener la instancia única (Singleton)
    public static synchronized GestorDB obtenerInstancia() {
        if (instancia == null) {
            instancia = new GestorDB();
        }
        return instancia;
    }
    
    // Método para conectar a la base de datos
    public Connection conectarDB() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            Properties propiedades = new Properties();
            propiedades.setProperty("user", USUARIO);
            propiedades.setProperty("password", CONTRASEÑA);
            propiedades.setProperty("ssl", "false");
            propiedades.setProperty("autoReconnect", "true");
            
            conexion = DriverManager.getConnection(URL, propiedades);
            conexion.setAutoCommit(true); // Por defecto autocommit habilitado
            
            System.out.println("Conexión establecida con PostgreSQL");
        }
        return conexion;
    }
    
    // Método para obtener la conexión actual
    public Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            return conectarDB();
        }
        return conexion;
    }
    
    // Método para cerrar la conexión
    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
    
    // Método para iniciar una transacción
    public void iniciarTransaccion() throws SQLException {
        Connection conn = obtenerConexion();
        conn.setAutoCommit(false);
    }
    
    // Método para confirmar una transacción
    public void confirmarTransaccion() throws SQLException {
        if (conexion != null) {
            conexion.commit();
            conexion.setAutoCommit(true);
        }
    }
    
    // Método para cancelar una transacción
    public void cancelarTransaccion() throws SQLException {
        if (conexion != null) {
            conexion.rollback();
            conexion.setAutoCommit(true);
        }
    }
    
    // Método para verificar la conectividad
    public boolean probarConexion() {
        try {
            Connection conn = conectarDB();
            if (conn != null && !conn.isClosed()) {
                // Ejecutar una consulta simple para verificar
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1");
                boolean resultado = rs.next();
                rs.close();
                stmt.close();
                return resultado;
            }
        } catch (SQLException e) {
            System.err.println("Error al probar la conexión: " + e.getMessage());
        }
        return false;
    }
    
    
    // Método de utilidad para cerrar recursos
    public static void cerrarRecursos(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }
    
    // Método de utilidad para cerrar recursos con conexión
    public static void cerrarRecursos(ResultSet rs, Statement stmt, Connection conn) {
        cerrarRecursos(rs, stmt);
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
