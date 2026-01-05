/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package connection;

/**
 *
 * @author noqok
 */

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Added properties for robustness
            String url = "jdbc:mysql://localhost:3306/quanlydiem?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            String user = "root";
            String password = "";
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successfully.");
        } catch (Exception e) {
            System.out.println("Failed to connect: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }
}