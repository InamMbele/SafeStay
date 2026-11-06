package safestay.model;

/** Row of STUDENT (only the columns the booking section needs). */
public record Student(long id, String firstName, String lastName, String email) {
    public String fullName() { return firstName + " " + lastName; }
}
