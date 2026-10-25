package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // Única instancia de la clase Conexion
    private static Conexion instancia;

    // Variable para guardar la conexión a MySQL
    Connection con;

    // Constructor privado: impide crear objetos con new desde otras clases
    private Conexion() {
    }

    // Devuelve siempre la misma instancia de Conexion
    public static synchronized Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    // Abre la conexión a la base de datos
    public Connection getConnection() {
        try {
            String myBD = "jdbc:mysql://localhost:3306/sistemaventa?serverTimezone=UTC";
            con = DriverManager.getConnection(myBD, "root", "");
            return con;
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return null;
    }
}
