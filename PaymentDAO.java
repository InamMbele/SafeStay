package safestay.dao;

import safestay.BusinessException;
import safestay.Db;
import safestay.model.Payment;
import safestay.model.Pricing;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class PaymentDAO {

    /**
     * Records a completed payment and confirms the booking in ONE transaction.
     * The amount is calculated here from the database values, not taken from the screen.
     */
    public Payment payAndConfirm(long bookingId, String method) throws SQLException, BusinessException {
        try (Connection c = Db.get()) {
            c.setAutoCommit(false);
            try {
                BigDecimal amount;
                String lockSql = "SELECT b.status, b.startDate, b.endDate, a.price FROM BOOKING b "
                               + "JOIN ACCOMMODATION a ON a.accommodationID = b.accommodationID "
                               + "WHERE b.bookingID = ? FOR UPDATE";
                try (PreparedStatement ps = c.prepareStatement(lockSql)) {
                    ps.setLong(1, bookingId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new BusinessException("Booking not found.");
                        if (!"Pending".equals(rs.getString("status")))
                            throw new BusinessException("This booking has already been paid or is no longer open.");
                        amount = Pricing.total(rs.getBigDecimal("price"),
                                rs.getDate("startDate").toLocalDate(), rs.getDate("endDate").toLocalDate());
                    }
                }
                LocalDate today = LocalDate.now();
                long paymentId;
                String ins = "INSERT INTO PAYMENT (bookingID, amount, paymentDate, paymentMethod, paymentStatus) "
                           + "VALUES (?,?,?,?,'Completed')";
                try (PreparedStatement ps = c.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, bookingId);
                    ps.setBigDecimal(2, amount);
                    ps.setDate(3, java.sql.Date.valueOf(today));
                    ps.setString(4, method);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); paymentId = keys.getLong(1); }
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE BOOKING SET status = 'Confirmed' WHERE bookingID = ? AND status = 'Pending'")) {
                    ps.setLong(1, bookingId);
                    ps.executeUpdate();
                }
                c.commit();
                return new Payment(paymentId, bookingId, amount, today, method, "Completed");
            } catch (SQLException | BusinessException e) {
                c.rollback();
                throw e;
            }
        }
    }
}
