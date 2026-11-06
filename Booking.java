package safestay.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Row of BOOKING joined with its accommodation and (optional) payment. */
public record Booking(long id, long studentId, long accommodationId, String accommodationName,
                      BigDecimal monthlyRent, LocalDate bookingDate, LocalDate startDate, LocalDate endDate,
                      String status, Long paymentId, String paymentStatus) {

    public long months() { return Pricing.months(startDate, endDate); }
    public BigDecimal total() { return Pricing.total(monthlyRent, startDate, endDate); }
    public boolean isPending() { return "Pending".equals(status); }
    public boolean hasPayment() { return paymentId != null; }
}
