/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package universidadbeta_escritorio;

/**
 *
 * @author contr
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    
    private static final String URL = 
        "jdbc:sqlserver://MAY\\SQLEXPRESS:1433;" +
        "databaseName=UniversidadBeta;" +
        "encrypt=true;" +
        "trustServerCertificate=true;" +
        "loginTimeout=30;";
    
    private static final String USUARIO = "usuarioApp";
    private static final String PASSWORD = "12345";
    
    private ConexionBD() {} // Evita instancias

    public static Connection getConexion() {
        try {
            // Class.forName() ya NO se necesita → eliminado
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
            // Cada vez que llames este método, te da una conexión fresca
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Método útil para cerrar conexiones en tus DAOs
    public static void cerrarConexion(Connection con) {
        if (con != null) {
            try {
                con.close();
                System.out.println("Conexión cerrada correctamente");
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}