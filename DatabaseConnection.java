package library.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Клас за връзка с базата данни
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/library_db?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "password"; // Променете това със собствената си парола
    
    /**
     * Осъществява връзка с базата данни
     * @return Connection обект за връзка с базата данни
     * @throws SQLException при грешка във връзката
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver не е намерен", e);
        }
    }
    
    /**
     * Затваря connection към базата данни
     * @param connection връзката, която трябва да бъде затворена
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Грешка при затваряне на връзката: " + e.getMessage());
            }
        }
    }
}