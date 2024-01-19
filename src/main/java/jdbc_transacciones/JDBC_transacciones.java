package jdbc_transacciones;

import java.sql.*;

public class JDBC_transacciones {

    public static void main(String[] args) {

        String basedatos = "bbdd_ejemplos_libro";
        String host = "localhost";
        String port = "3306";
        String urlConnection = "jdbc:mysql://" + host + ":" + port + "/" + basedatos;
        String user = "root";
        String pwd = "pass";

        try (Connection c = DriverManager.getConnection(urlConnection, user, pwd)) {
            // Si c lo creamos en el mismo bloque try que sInsert no sería visible en el catch y no podría hacer el c.rollback()
            try (PreparedStatement sInsert = c.prepareStatement("INSERT INTO CLIENTES(DNI,APELLIDOS,CP) VALUES (?,?,?);")) {

                c.setAutoCommit(false);  //Comienza transacción

                int i = 0;

                sInsert.setString(++i, "64320198X");
                sInsert.setString(++i, "FERNÁNDEZ");
                sInsert.setString(++i, "10100");
                sInsert.executeUpdate();

                sInsert.setString(i = 1, "36543210X");
                sInsert.setString(++i, "NADEL");
                sInsert.setString(++i, "46987");
                sInsert.executeUpdate();

                //Error: clave duplicada. No inserta y salta a catch SQLException
                sInsert.setString(i = 1, "64320198X");
                sInsert.setString(++i, "MOLINA");
                sInsert.setString(++i, "35153");
                sInsert.executeUpdate();

                c.commit();   // Finaliza transacción

            } catch (SQLException e) {
                // Algo ha fallado durante la transacción
                muestraErrorSQL(e);
                try {
                    System.out.println("Haciendo ROLLBACK...");
                    c.rollback();   // Hacemos roolback
                } catch (Exception er) {
                    System.err.println("ERROR haciendo ROLLBACK");
                    er.printStackTrace(System.err);
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR de conexión");
            e.printStackTrace(System.err);
        }
    }

    public static void muestraErrorSQL(SQLException e) {
        System.err.println("SQL ERROR mensaje: " + e.getMessage());
        System.err.println("SQL Estado: " + e.getSQLState());
        System.err.println("SQL código específico: " + e.getErrorCode());
    }

}