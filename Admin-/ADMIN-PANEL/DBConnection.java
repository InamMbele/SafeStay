package za.ac.cput.safestay;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Provides connections to the local SafeStay MySQL database. */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/SAFESTAY";
    private static final String USER = System.getenv().getOrDefault("SAFESTAY_DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("SAFESTAY_DB_PASSWORD", "password");

    private DBConnection() {
        // Utility class; do not instantiate.
    }

    /**
     * Opens a database connection. SQLExceptions are passed to the caller so
     * that the GUI can display a useful error instead of receiving null.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
