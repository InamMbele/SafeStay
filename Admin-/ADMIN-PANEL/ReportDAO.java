package za.ac.cput.safestay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Database operations for accommodation reports. */
public class ReportDAO {

    /** Returns full report data with the reporting student and accommodation details. */
    public List<Report> getAllReports() throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = """
                SELECT r.reportID, r.studentID, r.accommodationID, r.reason,
                       r.description, r.evidence, r.reportDate, r.status,
                       CONCAT(s.firstName, ' ', s.lastName) AS studentName,
                       s.email AS studentEmail,
                       a.name AS accommodationName,
                       a.address AS accommodationAddress,
                       a.imagePath AS accommodationImagePath
                FROM report r
                JOIN student s ON r.studentID = s.studentID
                JOIN accommodation a ON r.accommodationID = a.accommodationID
                ORDER BY r.reportDate DESC, r.reportID DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Report report = new Report();
                report.setReportID(rs.getInt("reportID"));
                report.setStudentID(rs.getInt("studentID"));
                report.setAccommodationID(rs.getInt("accommodationID"));
                report.setReason(rs.getString("reason"));
                report.setDescription(rs.getString("description"));
                report.setEvidence(rs.getString("evidence"));
                report.setReportDate(rs.getString("reportDate"));
                report.setStatus(rs.getString("status"));
                report.setStudentName(rs.getString("studentName"));
                report.setStudentEmail(rs.getString("studentEmail"));
                report.setAccommodationName(rs.getString("accommodationName"));
                report.setAccommodationAddress(rs.getString("accommodationAddress"));
                report.setAccommodationImagePath(rs.getString("accommodationImagePath"));
                reports.add(report);
            }
        }
        return reports;
    }

    public boolean flagReport(int reportID) {
        if (reportID <= 0) {
            return false;
        }
        String sql = "UPDATE report SET status = 'Flagged' WHERE reportID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reportID);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReport(int reportID) {
        if (reportID <= 0) {
            return false;
        }
        String sql = "DELETE FROM report WHERE reportID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reportID);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
