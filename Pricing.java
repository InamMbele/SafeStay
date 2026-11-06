package safestay.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Rent is charged per month; a stay is rounded to the nearest month (minimum 1). */
public final class Pricing {
    private Pricing() {}

    public static long months(LocalDate start, LocalDate end) {
        return Math.max(1, Math.round(ChronoUnit.DAYS.between(start, end) / 30.0));
    }

    public static BigDecimal total(BigDecimal monthlyRent, LocalDate start, LocalDate end) {
        return monthlyRent.multiply(BigDecimal.valueOf(months(start, end)));
    }
}
