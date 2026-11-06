package za.ac.cput.safestay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Database queries used by the administrator dashboard. */
public class AdminDAO {


    /** Returns accommodation listings waiting for an administrator decision. */
    public List<AccommodationListing> getPendingListings() throws SQLException {
        List<AccommodationListing> listings = new ArrayList<>();
        String sql = """
                SELECT a.accommodationID, a.landlordID, a.name, a.address, a.price,
                       a.numberOfRooms, a.roomType, a.distanceToCampus, a.moveInDate,
                       a.propertyRules, a.availabilityStatus, a.dateListed, a.imagePath,
                       a.status,
                       CONCAT(l.firstName, ' ', l.lastName) AS landlordName,
                       l.email AS landlordEmail
                FROM accommodation a
                JOIN landlord l ON a.landlordID = l.landlordID
                WHERE LOWER(a.status) = 'pending'
                ORDER BY a.dateListed DESC, a.accommodationID DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                AccommodationListing listing = new AccommodationListing();
                listing.setAccommodationID(rs.getInt("accommodationID"));
                listing.setLandlordID(rs.getInt("landlordID"));
                listing.setName(rs.getString("name"));
                listing.setAddress(rs.getString("address"));
                listing.setPrice(rs.getDouble("price"));
                listing.setNumberOfRooms(rs.getInt("numberOfRooms"));
                listing.setRoomType(rs.getString("roomType"));
                listing.setDistanceToCampus(rs.getDouble("distanceToCampus"));
                listing.setMoveInDate(rs.getString("moveInDate"));
                listing.setPropertyRules(rs.getString("propertyRules"));
                listing.setAvailabilityStatus(rs.getString("availabilityStatus"));
                listing.setDateListed(rs.getString("dateListed"));
                listing.setImagePath(rs.getString("imagePath"));
                listing.setStatus(rs.getString("status"));
                listing.setLandlordName(rs.getString("landlordName"));
                listing.setLandlordEmail(rs.getString("landlordEmail"));
                listings.add(listing);
            }
        }
        return listings;
    }

    /** Changes only the administrator review status, not accommodation availability. */
    public boolean updateListingStatus(int accommodationID, String status) {
        if (accommodationID <= 0 || status == null
                || !(status.equalsIgnoreCase("Approved") || status.equalsIgnoreCase("Rejected"))) {
            return false;
        }
        String sql = "UPDATE accommodation SET status = ? WHERE accommodationID = ? AND LOWER(status) = 'pending'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.equalsIgnoreCase("Approved") ? "Approved" : "Rejected");
            stmt.setInt(2, accommodationID);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getTotalListings() {
        return getCount("SELECT COUNT(*) FROM accommodation");
    }

    /** Counts distinct landlords with an approved verification. */
    public int getVerifiedListings() {
        return getCount("SELECT COUNT(DISTINCT landlordID) FROM verification WHERE status = 'Approved'");
    }

    public int getTotalReports() {
        return getCount("SELECT COUNT(*) FROM report");
    }

    /** Counts both student and landlord accounts as registered users. */
    public int getRegisteredUsers() {
        return getCount("SELECT (SELECT COUNT(*) FROM student) + (SELECT COUNT(*) FROM landlord)");
    }

    private int getCount(String sql) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
