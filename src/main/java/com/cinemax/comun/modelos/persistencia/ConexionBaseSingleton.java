package com.cinemax.comun.modelos.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Gestor de conexiones JDBC (Patrón Singleton) con soporte
 * para transacciones seguras y utilidades de cierre de recursos.
 */
public final class ConexionBaseSingleton {

    // ---------- Configuración ----------
    private static final String URL      = "jdbc:postgresql://tramway.proxy.rlwy.net:18687/railway";
    private static final String DRIVER   = "org.postgresql.Driver";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "tTCOwIHvtDnIJQZjalEcwvKbbKhGQJvl";

    // ---------- Singleton ----------
    private static ConexionBaseSingleton instancia;
    private Connection conexion;

    private ConexionBaseSingleton() {
        try {
            Class.forName(DRIVER);               // Cargar driver
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

    // ---------- Conexión ----------
    public Connection conectar() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASSWORD);
            props.setProperty("ssl", "false");
            conexion = DriverManager.getConnection(URL, props);
            conexion.setAutoCommit(true);        // Por defecto, autocommit
        }
        return conexion;
    }

    public Connection getConexion() throws SQLException {
        return conectar();                       // Alias
    }

    public void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }

    // ---------- Transacciones ----------
    public void iniciarTx() throws SQLException {
        conectar().setAutoCommit(false);
    }

    public void commitTx() throws SQLException {
        if (conexion != null) {
            conexion.commit();
            conexion.setAutoCommit(true);
        }
    }

    public void rollbackTx() throws SQLException {
        if (conexion != null) {
            conexion.rollback();
            conexion.setAutoCommit(true);
        }
    }

    // ---------- CRUD genéricos ----------
    /** Ejecuta INSERT / UPDATE / DELETE con control de transacción */
    public void ejecutarActualizacion(String sql) throws SQLException {
        Statement st = null;
        try {
            iniciarTx();
            st = getConexion().createStatement();
            st.executeUpdate(sql);
            commitTx();
        } catch (SQLException e) {
            rollbackTx();
            throw e;
        } finally {
            cerrarRecursos(null, st);
        }
    }

    /** Ejecuta SELECT y devuelve ResultSet (recuerda cerrarlo después) */
    public ResultSet ejecutarConsulta(String sql) throws SQLException {
        Statement st = getConexion().createStatement();
        return st.executeQuery(sql);   // El llamador debe cerrar ResultSet y Statement
    }

    // ---------- utilidades ----------
    public static void cerrarRecursos(ResultSet rs, Statement st) {
        try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
        try { if (st != null) st.close(); } catch (SQLException ignored) {}
    }
}