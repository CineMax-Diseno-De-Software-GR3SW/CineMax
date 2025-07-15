package com.cinemax.peliculas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBaseSingleton {
    private static ConexionBaseSingleton instancia;
    private Connection conexion;
    
    // URL de conexión - ajusta según tu configuración
    private final String URL = "jdbc:postgresql://localhost:5432/cinemax_db";
    private final String USUARIO = "postgres";
    private final String CONTRASEÑA = "123456";
    
    private ConexionBaseSingleton() {
        try {
            // Cargar el driver JDBC
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error al cargar el driver PostgreSQL: " + e.getMessage(), e);
        }
    }
    
    public static synchronized ConexionBaseSingleton getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBaseSingleton();
        }
        return instancia;
    }
    
    public Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA);
        }
        return conexion;
    }
    
    public void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}