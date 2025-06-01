package com.studentinfo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement; // For the closeQuietly method
import java.sql.ResultSet; // For the closeQuietly method

public class DatabaseConnector {

    static {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // This is a critical error if the driver is not in the classpath
            throw new RuntimeException("MySQL JDBC Driver not found. Ensure it's in the classpath.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = DatabaseConfig.getDbUrl();
        String user = DatabaseConfig.getDbUsername();
        String password = DatabaseConfig.getDbPassword();

        if (url == null || user == null || password == null) {
            throw new SQLException("Database configuration (URL, user, or password) not loaded. Check app.config and logs.");
        }

        return DriverManager.getConnection(url, user, password);
    }

    // Utility method to close a Connection
    public static void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // Log or handle, but typically don't let it interrupt main flow
                System.err.println("Error closing Connection: " + e.getMessage());
            }
        }
    }

    // Utility method to close a Statement
    public static void closeQuietly(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing Statement: " + e.getMessage());
            }
        }
    }

    // Utility method to close a ResultSet
    public static void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
    }
} 