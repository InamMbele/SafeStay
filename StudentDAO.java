package safestay.dao;

import safestay.Db;
import safestay.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {
    /** @return the student with this email, or null if there is none. */
    public Student findByEmail(String email) throws SQLException {
        String sql = "SELECT studentID, firstName, lastName, email FROM STUDENT WHERE email = ?";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Student(rs.getLong("studentID"), rs.getString("firstName"),
                        rs.getString("lastName"), rs.getString("email"));
            }
        }
    }
}
