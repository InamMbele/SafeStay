package safestay.dao;

import safestay.Db;
import safestay.model.Accommodation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccommodationDAO {
    public List<Accommodation> listAvailable() throws SQLException {
        String sql = "SELECT a.accommodationID, a.name, a.address, a.price, a.roomType, a.distanceToCampus, "
                + "EXISTS (SELECT 1 FROM VERIFICATION v WHERE v.landlordID = a.landlordID AND v.status = 'Approved') AS verified "
                + "FROM ACCOMMODATION a WHERE a.availabilityStatus = 'Available' ORDER BY a.accommodationID";
        List<Accommodation> list = new ArrayList<>();
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Accommodation(rs.getLong("accommodationID"), rs.getString("name"),
                        rs.getString("address"), rs.getBigDecimal("price"), rs.getString("roomType"),
                        rs.getBigDecimal("distanceToCampus"), rs.getBoolean("verified")));
            }
        }
        return list;
    }
}
