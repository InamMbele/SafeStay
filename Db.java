package safestay;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Single place that knows how to reach the agreed MySQL database: SAFESTAY. */
public final class Db {
    private static final String URL = env("SAFESTAY_DB_URL",
            "jdbc:mysql://localhost:3306/SAFESTAY?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false");
    private static final String USER = env("SAFESTAY_DB_USER", "root");
    private static String password = env("SAFESTAY_DB_PASSWORD", null);

    private Db() {}

    private static String env(String key, String def) {
        String v = System.getenv(key);
        return v == null ? def : v;
    }

    public static String password() { return password; }
    public static void setPassword(String p) { password = p; }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, password == null ? "" : password);
    }

    /** Opens and closes a connection; throws if the database cannot be reached. */
    public static void test() throws SQLException {
        try (Connection c = get()) {
            c.isValid(2);
        }
    }
}
