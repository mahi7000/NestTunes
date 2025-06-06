package com.example.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/ap?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // change if needed
    private static final String PASSWORD = ""; // change if needed

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // TEMPORARY TEST METHOD
   /* public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ Connection successful!");
            }
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }
    } */
}
