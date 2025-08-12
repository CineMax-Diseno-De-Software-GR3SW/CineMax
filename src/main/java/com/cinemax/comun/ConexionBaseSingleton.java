package com.cinemax.comun;

<<<<<<< HEAD
import java.sql.*;


    public class ConexionBaseSingleton {

        private static ConexionBaseSingleton conexionBase;
        protected Connection conexion = null;
        protected Statement sentencia = null;

        protected ResultSet resultado = null;

        private ConexionBaseSingleton(){}

        public static ConexionBaseSingleton getInstancia(){
            if(conexionBase == null)
                conexionBase = new ConexionBaseSingleton();
//            else
//                System.out.println("El objeto ya ha sido creado");
            return conexionBase;
        }

        private final String URL = "jdbc:sqlserver://localhost:1433;database=BDCine; encrypt = false;";
        //private final String URL = "jdbc:sqlserver://localhost:1433;database=BDINTERCAMBIOS; encrypt = false;";
        private final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        private final String USER = "CineConnection";
        private final String PASSWORD = "QWE456";

        // MÉTODOS

        /**
         * Método que conecta el Driver JDBC y luego establece una conexión con el objeto Connection usando la URL configurada para SQL Server.
         *
         * @throws Exception
         */
        public void conectarBase() throws Exception {
            try {
                Class.forName(DRIVER); // Conecta al driver SQL Server
                conexion = DriverManager.getConnection(URL, USER, PASSWORD); // Establece la conexión
            } catch (ClassNotFoundException | SQLException e) {
                throw e;
            }
        }
        /**
         * Método que verifica si mis Objetos de Conexión, Sentencia y Resultado tienen valores asignados. Si es así, se los devuelve a un estado 'null'
         * @throws Exception
         */
        protected void desconectarBase() throws Exception {
            try {
                if (conexion != null) {
                    conexion.close();
                }
                if (sentencia != null) {
                    sentencia.close();
                }
                if (resultado != null) {
                    resultado.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }

        /**
         * Método que se conecta a la base, prepara el Objeto Statement y ejecuta con un método la query pasada como argumento
         * @param sql Sentencia query nativa de tipo INSERT, UPDATE, DELETE que no devuelve ninguna tabla
         * @throws Exception
         */
        public void insertarModificarEliminar(String sql) throws Exception {
            try {
                conectarBase();
                sentencia = conexion.createStatement();
                sentencia.executeUpdate(sql);
            } catch (SQLException | ClassNotFoundException e) {
                conexion.rollback();
                throw e;
            } finally {
                desconectarBase();
            }
        }

        /**
         * Método que se conecta a la base, prepara el Objeto Statement y luego guarda en el Objeto ResultSet las tablas devueltas al ejectuar la sentencia query que fue pasada como argumento
         * @param sql Sentencia query nativa de tipo consulta SELECT que devuelva una tabla
         * @throws Exception
         */
        public void consultarBase(String sql) throws Exception {
            try {
                conectarBase();
                sentencia = conexion.createStatement();
                resultado = sentencia.executeQuery(sql);
            } catch (Exception e) {
                throw e;
            }
        }

        public ResultSet getResultado() {
            return resultado;
        }
    }


=======
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
>>>>>>> 70777b19aee4af7a063e70b323e48454dd478cc0
