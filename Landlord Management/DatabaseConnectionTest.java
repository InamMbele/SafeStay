package safestay.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/** Run this class to verify the connection and all ten agreed SAFESTAY tables. */
public class DatabaseConnectionTest {
    public static void main(String[] args) {
        String sql = "SELECT COUNT(*) FROM information_schema.tables "
                + "WHERE table_schema = DATABASE() AND table_name IN "
                + "('STUDENT','LANDLORD','ADMIN','ACCOMMODATION','BOOKING','PAYMENT','REVIEW','REPORT','SAVED_ACCOMMODATION','VERIFICATION')";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {
            String database = connection.getCatalog();
            System.out.println("Connected database: " + database);
            if (!"SAFESTAY".equalsIgnoreCase(database)) {
                throw new IllegalStateException("Wrong database. Expected SAFESTAY but connected to " + database);
            }
            try (ResultSet result = statement.executeQuery(sql)) {
                result.next();
                int tableCount = result.getInt(1);
                System.out.println("Agreed tables found: " + tableCount + " / 10");
                if (tableCount != 10) {
                    throw new IllegalStateException("Expected all 10 agreed tables. Check that SafeStay_Database.sql was executed.");
                }
            }
            System.out.println("SUCCESS: Java is connected to SAFESTAY and all 10 agreed tables exist.");
        } catch (Exception ex) {
            System.err.println("DATABASE TEST FAILED: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
