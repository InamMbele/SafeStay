package safestay.model;

import java.math.BigDecimal;

/** Row of ACCOMMODATION plus "verified" (landlord has an Approved row in VERIFICATION). */
public record Accommodation(long id, String name, String address, BigDecimal price,
                            String roomType, BigDecimal distanceToCampus, boolean verified) {}
