package za.ac.cput.safestay;

import java.sql.Connection;
import java.sql.SQLException;

/** Opens and closes one connection to confirm database connectivity. */
public class TestConnection {

    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection.isValid(3)) {
                System.out.println("Connected to SAFESTAY successfully.");
            } else {
                System.out.println("The connection was opened but did not pass validation.");
            }
        } catch (SQLException e) {
            System.err.println("Could not connect to SAFESTAY: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
