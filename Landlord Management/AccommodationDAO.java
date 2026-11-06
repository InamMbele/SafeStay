package safestay.dao;

import safestay.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccommodationDAO {
    public boolean addAccommodation(int landlordID, String name, String address, double price,
            int numberOfRooms, String roomType, double distanceToCampus,
            java.sql.Date moveInDate, String propertyRules, String imagePath) throws SQLException {
        String sql = "INSERT INTO ACCOMMODATION (landlordID, name, address, price, numberOfRooms, roomType, distanceToCampus, moveInDate, propertyRules, availabilityStatus, dateListed, imagePath) VALUES (?,?,?,?,?,?,?,?,?,'Available',CURDATE(),?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, landlordID);
            ps.setString(2, name);
            ps.setString(3, address);
            ps.setDouble(4, price);
            ps.setInt(5, numberOfRooms);
            ps.setString(6, roomType);
            ps.setDouble(7, distanceToCampus);
            if (moveInDate == null) ps.setNull(8, java.sql.Types.DATE); else ps.setDate(8, moveInDate);
            ps.setString(9, propertyRules);
            ps.setString(10, imagePath);
            return ps.executeUpdate() == 1;
        }
    }

    /** Returns copied rows; the connection, statement and result set are closed before returning. */
    public List<Object[]> getAccommodationsByLandlord(int landlordID) throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT accommodationID, name, address, price, numberOfRooms, roomType, availabilityStatus, dateListed FROM ACCOMMODATION WHERE landlordID=? ORDER BY dateListed DESC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, landlordID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{rs.getInt("accommodationID"), rs.getString("name"),
                        rs.getString("address"), rs.getBigDecimal("price"), rs.getInt("numberOfRooms"),
                        rs.getString("roomType"), rs.getString("availabilityStatus"), rs.getDate("dateListed")});
                }
            }
        }
        return rows;
    }

    public boolean updatePrice(int accommodationID, double newPrice) throws SQLException {
        if (accommodationID <= 0 || newPrice < 0) throw new IllegalArgumentException("Enter a valid accommodation ID and non-negative price.");
        String sql = "UPDATE ACCOMMODATION SET price=? WHERE accommodationID=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setInt(2, accommodationID);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteAccommodation(int accommodationID) throws SQLException {
        String[] sqls = {
            "DELETE FROM PAYMENT WHERE bookingID IN (SELECT bookingID FROM BOOKING WHERE accommodationID=?)",
            "DELETE FROM BOOKING WHERE accommodationID=?",
            "DELETE FROM REVIEW WHERE accommodationID=?",
            "DELETE FROM REPORT WHERE accommodationID=?",
            "DELETE FROM SAVED_ACCOMMODATION WHERE accommodationID=?",
            "DELETE FROM ACCOMMODATION WHERE accommodationID=?"
        };
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int deleted = 0;
                for (int i = 0; i < sqls.length; i++) {
                    try (PreparedStatement ps = con.prepareStatement(sqls[i])) {
                        ps.setInt(1, accommodationID);
                        int changed = ps.executeUpdate();
                        if (i == sqls.length - 1) deleted = changed;
                    }
                }
                con.commit();
                return deleted == 1;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
