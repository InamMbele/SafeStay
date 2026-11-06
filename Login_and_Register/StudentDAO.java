/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package safestayapp;

/**
 *
 * @author Admin
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class StudentDAO {

    // Ensures the MySQL driver class is explicitly registered into execution memory
    private void loadDriverSafely() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver Missing from Classpath: " + e.getMessage());
        }
    }

    public boolean registerStudent(Student student) {
        loadDriverSafely();
        String sql = """
            INSERT INTO STUDENT
            (firstName, lastName, email, password, phoneNumber,
             institution, course, budgetRange, preferredArea, dateRegistered)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseConnection.getConnection(); 
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getLastName());
            statement.setString(3, student.getEmail());
            statement.setString(4, student.getPassword());
            statement.setString(5, student.getPhoneNumber());
            statement.setString(6, student.getInstitution());
            statement.setString(7, student.getCourse());
            statement.setString(8, student.getBudgetRange());
            statement.setString(9, student.getPreferredArea());

            statement.setDate(
                    10,
                    java.sql.Date.valueOf(student.getDateRegistered())
            );

            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    public Student loginStudent(String email, String password) {
        loadDriverSafely();
        String sql = """
        SELECT *
        FROM STUDENT
        WHERE email = ? AND password = ?
        """;

        try (Connection connection = DatabaseConnection.getConnection(); 
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return new Student(
                        result.getInt("studentID"),
                        result.getString("firstName"),
                        result.getString("lastName"),
                        result.getString("email"),
                        result.getString("phoneNumber"),
                        result.getString("password"),
                        result.getString("institution"),
                        result.getString("course"),
                        result.getString("budgetRange"),
                        result.getString("preferredArea"),
                        result.getDate("dateRegistered").toLocalDate()
                );
            }

        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
        }

        return null;
    }
    
    public boolean updateStudent(Student student) {
        loadDriverSafely();
        String sql = """
            UPDATE STUDENT
            SET firstName = ?,
                lastName = ?,
                email = ?,
                phoneNumber = ?,
                institution = ?,
                course = ?,
                budgetRange = ?,
                preferredArea = ?
            WHERE studentID = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getLastName());
            statement.setString(3, student.getEmail());
            statement.setString(4, student.getPhoneNumber());
            statement.setString(5, student.getInstitution());
            statement.setString(6, student.getCourse());
            statement.setString(7, student.getBudgetRange());
            statement.setString(8, student.getPreferredArea());
            statement.setInt(9, student.getStudentID());

            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteStudent(int studentID) {
        loadDriverSafely();
        String sql = """
            DELETE FROM STUDENT
            WHERE studentID = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentID);

            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
            return false;
        }
    }
}


