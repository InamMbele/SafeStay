package safestay.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Row of PAYMENT. */
public record Payment(long id, long bookingId, BigDecimal amount, LocalDate paymentDate,
                      String paymentMethod, String paymentStatus) {
    /** Reference shown to the student; derived from the ID so no extra column is needed. */
    public String reference() { return String.format("SS-%06d", id); }
}
