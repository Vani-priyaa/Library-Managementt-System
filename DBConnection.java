package library.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Singleton class for managing MySQL JDBC connection.
 * Ensures only one active connection exists throughout the application lifecycle.
 */
public class DBConnection {

    // ── Database credentials ──────────────────────────────────────────────────
    private static final String URL      = "jdbc:mysql://localhost:3306/librarydb?useSSL=false&serverTimezone=UTC";
    private static final String USER     = "root";       // Change to your MySQL username
    private static final String PASSWORD = "root";       // Change to your MySQL password

    // Singleton connection instance
    private static Connection connection = null;

    // Private constructor – prevents external instantiation
    private DBConnection() {}

    /**
     * Returns the singleton Connection object.
     * Creates a new connection if one doesn't exist or has been closed.
     *
     * @return live java.sql.Connection
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load the MySQL JDBC driver (required for older Java versions)
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Connected to librarydb successfully.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-j to classpath.", e);
            }
        }
        return connection;
    }

    /**
     * Closes the active connection gracefully.
     * Call this at application exit.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}
