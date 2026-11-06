package safestay.dao;

import safestay.db.DBConnection;
import safestay.model.Landlord;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LandlordDAO {
    public boolean insertLandlord(Landlord landlord) throws SQLException {
        String sql = "INSERT INTO LANDLORD (firstName, lastName, email, password, phoneNumber, dateRegistered) VALUES (?,?,?,?,?,CURDATE())";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, landlord.firstName);
            ps.setString(2, landlord.lastName);
            ps.setString(3, landlord.email);
            ps.setString(4, landlord.password);
            ps.setString(5, landlord.phoneNumber);
            return ps.executeUpdate() == 1;
        }
    }

    public List<Landlord> getAllLandlords() throws SQLException {
        List<Landlord> list = new ArrayList<>();
        String sql = "SELECT landlordID, firstName, lastName, email, phoneNumber, dateRegistered FROM LANDLORD ORDER BY dateRegistered DESC";
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapLandlord(rs));
        }
        return list;
    }

    public Landlord getLandlordById(int id) throws SQLException {
        String sql = "SELECT landlordID, firstName, lastName, email, phoneNumber, dateRegistered FROM LANDLORD WHERE landlordID=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapLandlord(rs) : null;
            }
        }
    }

    private Landlord mapLandlord(ResultSet rs) throws SQLException {
        Landlord landlord = new Landlord();
        landlord.landlordID = rs.getInt("landlordID");
        landlord.firstName = rs.getString("firstName");
        landlord.lastName = rs.getString("lastName");
        landlord.email = rs.getString("email");
        landlord.phoneNumber = rs.getString("phoneNumber");
        landlord.dateRegistered = rs.getDate("dateRegistered");
        return landlord;
    }

    public boolean updateLandlord(Landlord landlord) throws SQLException {
        String sql = "UPDATE LANDLORD SET firstName=?, lastName=?, phoneNumber=? WHERE landlordID=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, landlord.firstName);
            ps.setString(2, landlord.lastName);
            ps.setString(3, landlord.phoneNumber);
            ps.setInt(4, landlord.landlordID);
            return ps.executeUpdate() == 1;
        }
    }

    /** Deletes dependent records in a transaction, following the supplied SQL foreign keys. */
    public boolean deleteLandlord(int landlordID) throws SQLException {
        String accommodationIds = "(SELECT accommodationID FROM ACCOMMODATION WHERE landlordID=?)";
        String[] statements = {
            "DELETE FROM PAYMENT WHERE bookingID IN (SELECT bookingID FROM BOOKING WHERE accommodationID IN " + accommodationIds + ")",
            "DELETE FROM BOOKING WHERE accommodationID IN " + accommodationIds,
            "DELETE FROM REVIEW WHERE accommodationID IN " + accommodationIds,
            "DELETE FROM REPORT WHERE accommodationID IN " + accommodationIds,
            "DELETE FROM SAVED_ACCOMMODATION WHERE accommodationID IN " + accommodationIds,
            "DELETE FROM ACCOMMODATION WHERE landlordID=?",
            "DELETE FROM VERIFICATION WHERE landlordID=?",
            "DELETE FROM LANDLORD WHERE landlordID=?"
        };
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int deletedLandlords = 0;
                for (int i = 0; i < statements.length; i++) {
                    try (PreparedStatement ps = con.prepareStatement(statements[i])) {
                        ps.setInt(1, landlordID);
                        int changed = ps.executeUpdate();
                        if (i == statements.length - 1) deletedLandlords = changed;
                    }
                }
                con.commit();
                return deletedLandlords == 1;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
