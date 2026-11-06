package za.ac.cput.safestay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Database operations for landlord verification requests. */
public class VerificationDAO {

    /** Returns verification records together with the related landlord details. */
    public List<Verification> getAllVerifications() throws SQLException {
        List<Verification> verifications = new ArrayList<>();
        String sql = """
                SELECT v.verificationID, v.landlordID, v.adminID,
                       v.idDocument, v.propertyProof, v.status, v.verificationDate,
                       CONCAT(l.firstName, ' ', l.lastName) AS landlordName,
                       l.email AS landlordEmail,
                       (SELECT a.name FROM accommodation a WHERE a.landlordID = v.landlordID ORDER BY a.accommodationID LIMIT 1) AS accommodationName,
                       (SELECT a.address FROM accommodation a WHERE a.landlordID = v.landlordID ORDER BY a.accommodationID LIMIT 1) AS accommodationAddress,
                       (SELECT a.imagePath FROM accommodation a WHERE a.landlordID = v.landlordID ORDER BY a.accommodationID LIMIT 1) AS accommodationImagePath
                FROM verification v
                JOIN landlord l ON v.landlordID = l.landlordID
                ORDER BY v.verificationDate DESC, v.verificationID DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Verification verification = new Verification();
                verification.setVerificationID(rs.getInt("verificationID"));
                verification.setLandlordID(rs.getInt("landlordID"));
                verification.setAdminID(rs.getInt("adminID"));
                verification.setIdDocument(rs.getString("idDocument"));
                verification.setPropertyProof(rs.getString("propertyProof"));
                verification.setStatus(rs.getString("status"));
                verification.setVerificationDate(rs.getString("verificationDate"));
                verification.setLandlordName(rs.getString("landlordName"));
                verification.setLandlordEmail(rs.getString("landlordEmail"));
                verification.setAccommodationName(rs.getString("accommodationName"));
                verification.setAccommodationAddress(rs.getString("accommodationAddress"));
                verification.setAccommodationImagePath(rs.getString("accommodationImagePath"));
                verifications.add(verification);
            }
        }
        return verifications;
    }

    public boolean addVerification(Verification verification) {
        if (verification == null || verification.getLandlordID() <= 0
                || verification.getAdminID() <= 0) {
            return false;
        }

        String sql = """
                INSERT INTO verification
                (landlordID, adminID, idDocument, propertyProof, status, verificationDate)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, verification.getLandlordID());
            stmt.setInt(2, verification.getAdminID());
            stmt.setString(3, verification.getIdDocument());
            stmt.setString(4, verification.getPropertyProof());
            stmt.setString(5, verification.getStatus());
            if (verification.getVerificationDate() == null
                    || verification.getVerificationDate().isBlank()) {
                stmt.setNull(6, java.sql.Types.DATE);
            } else {
                stmt.setDate(6, java.sql.Date.valueOf(verification.getVerificationDate()));
            }
            return stmt.executeUpdate() == 1;
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(int verificationID, String status) {
        if (verificationID <= 0 || status == null
                || !(status.equalsIgnoreCase("Approved") || status.equalsIgnoreCase("Rejected"))) {
            return false;
        }

        String sql = "UPDATE verification SET status = ? WHERE verificationID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase());
            stmt.setInt(2, verificationID);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteVerification(int verificationID) {
        if (verificationID <= 0) {
            return false;
        }
        String sql = "DELETE FROM verification WHERE verificationID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, verificationID);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
