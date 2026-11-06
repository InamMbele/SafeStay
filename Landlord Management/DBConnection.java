package safestay.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Central MySQL connection for the agreed SAFESTAY database. */
public final class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/SAFESTAY?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = System.getProperty("safestay.db.user", "root");
    private static final String PASSWORD = System.getProperty("safestay.db.password", "");

    private DBConnection() { }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException("MySQL Connector/J is missing. Check the project's lib folder and Libraries.", ex);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
