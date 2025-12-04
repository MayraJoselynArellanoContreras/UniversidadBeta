/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author contr
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static Connection conexion = null;

    private static final String URL =
        "jdbc:sqlserver://MAY\\SQLEXPRESS;databaseName=UniversidadBeta;encrypt=false;trustServerCertificate=true;";
    private static final String USUARIO = "Mayra";
    private static final String PASSWORD = "12345";

    private ConexionBD() {}

    public static Connection getConexion() {
    try {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
        return null;
    }
}
}
