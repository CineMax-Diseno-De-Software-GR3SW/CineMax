package com.cinemax.empleados.Comun;

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


