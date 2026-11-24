package com.gymbooking.util;

import com.gymbooking.exception.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC connection utility for MySQL (XAMPP default configuration).
 * Update DB_URL, DB_USER, and DB_PASSWORD if your MySQL credentials differ.
 */
public final class DBConnection {

    private static final String DB_URL =
        "jdbc:mysql://localhost:3306/gym_booking_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("MySQL JDBC Driver not found. Add mysql-connector-j to your project.", e);
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
