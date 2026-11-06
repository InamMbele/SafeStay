package safestay.dao;

import safestay.BusinessException;
import safestay.Db;
import safestay.model.Booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private static final String OVERLAP_SQL =
            "SELECT COUNT(*) FROM BOOKING WHERE accommodationID = ? AND status IN ('Pending','Confirmed') "
          + "AND startDate < ? AND endDate > ? AND bookingID <> ?";

    /** CREATE: saves a Pending booking. Rejects dates that overlap another active booking for the same place. */
    public long create(long studentId, long accommodationId, LocalDate start, LocalDate end)
            throws SQLException, BusinessException {
        try (Connection c = Db.get()) {
            c.setAutoCommit(false);
            try {
                lockAccommodation(c, accommodationId);
                requireNoOverlap(c, accommodationId, start, end, 0);
                String sql = "INSERT INTO BOOKING (studentID, accommodationID, bookingDate, startDate, endDate, status) "
                           + "VALUES (?,?,?,?,?,'Pending')";
                long id;
                try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, studentId);
                    ps.setLong(2, accommodationId);
                    ps.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                    ps.setDate(4, java.sql.Date.valueOf(start));
                    ps.setDate(5, java.sql.Date.valueOf(end));
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); id = keys.getLong(1); }
                }
                c.commit();
                return id;
            } catch (SQLException | BusinessException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** READ: all bookings of one student with accommodation and payment details. */
    public List<Booking> listForStudent(long studentId) throws SQLException {
        String sql = "SELECT b.bookingID, b.studentID, b.accommodationID, a.name, a.price, b.bookingDate, "
                   + "b.startDate, b.endDate, b.status, p.paymentID, p.paymentStatus "
                   + "FROM BOOKING b JOIN ACCOMMODATION a ON a.accommodationID = b.accommodationID "
                   + "LEFT JOIN PAYMENT p ON p.bookingID = b.bookingID "
                   + "WHERE b.studentID = ? ORDER BY b.bookingID DESC";
        List<Booking> list = new ArrayList<>();
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long pid = rs.getLong("paymentID");
                    Long paymentId = rs.wasNull() ? null : pid;
                    java.sql.Date booked = rs.getDate("bookingDate");
                    list.add(new Booking(rs.getLong("bookingID"), rs.getLong("studentID"), rs.getLong("accommodationID"),
                            rs.getString("name"), rs.getBigDecimal("price"),
                            booked == null ? null : booked.toLocalDate(),
                            rs.getDate("startDate").toLocalDate(), rs.getDate("endDate").toLocalDate(),
                            rs.getString("status"), paymentId, rs.getString("paymentStatus")));
                }
            }
        }
        return list;
    }

    /** UPDATE: change the dates of a Pending booking. */
    public void updateDates(Booking b, LocalDate start, LocalDate end) throws SQLException, BusinessException {
        try (Connection c = Db.get()) {
            c.setAutoCommit(false);
            try {
                lockAccommodation(c, b.accommodationId());
                requireNoOverlap(c, b.accommodationId(), start, end, b.id());
                String sql = "UPDATE BOOKING SET startDate = ?, endDate = ? WHERE bookingID = ? AND status = 'Pending'";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setDate(1, java.sql.Date.valueOf(start));
                    ps.setDate(2, java.sql.Date.valueOf(end));
                    ps.setLong(3, b.id());
                    if (ps.executeUpdate() == 0)
                        throw new BusinessException("Only bookings that are still Pending can be changed.");
                }
                c.commit();
            } catch (SQLException | BusinessException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** DELETE: removes a Pending booking that has no payment. */
    public void deletePending(long bookingId) throws SQLException, BusinessException {
        String sql = "DELETE FROM BOOKING WHERE bookingID = ? AND status = 'Pending' "
                   + "AND NOT EXISTS (SELECT 1 FROM PAYMENT p WHERE p.bookingID = ?)";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, bookingId);
            ps.setLong(2, bookingId);
            if (ps.executeUpdate() == 0)
                throw new BusinessException("Only an unpaid, Pending booking can be deleted.");
        }
    }

    /** UPDATE: marks a Confirmed (paid) booking as Cancelled. The payment row is kept for the records. */
    public void cancelConfirmed(long bookingId) throws SQLException, BusinessException {
        String sql = "UPDATE BOOKING SET status = 'Cancelled' WHERE bookingID = ? AND status = 'Confirmed'";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, bookingId);
            if (ps.executeUpdate() == 0)
                throw new BusinessException("Only a Confirmed booking can be cancelled.");
        }
    }

    // Serialises bookings for one accommodation so two students cannot take the same dates at once.
    private void lockAccommodation(Connection c, long accommodationId) throws SQLException, BusinessException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT accommodationID FROM ACCOMMODATION WHERE accommodationID = ? FOR UPDATE")) {
            ps.setLong(1, accommodationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new BusinessException("That accommodation no longer exists.");
            }
        }
    }

    private void requireNoOverlap(Connection c, long accommodationId, LocalDate start, LocalDate end, long excludeId)
            throws SQLException, BusinessException {
        try (PreparedStatement ps = c.prepareStatement(OVERLAP_SQL)) {
            ps.setLong(1, accommodationId);
            ps.setDate(2, java.sql.Date.valueOf(end));
            ps.setDate(3, java.sql.Date.valueOf(start));
            ps.setLong(4, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0)
                    throw new BusinessException("This accommodation is already booked for some of those dates.");
            }
        }
    }
}
