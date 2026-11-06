package za.ac.cput.safestay;

import java.sql.SQLException;
import java.util.List;

/** Simple read-only console check for verification records. */
public class VerificationTest {

    public static void main(String[] args) throws SQLException {
        VerificationDAO dao = new VerificationDAO();
        List<Verification> verifications = dao.getAllVerifications();

        System.out.println("---- ALL VERIFICATIONS ----");
        for (Verification verification : verifications) {
            System.out.println("ID: " + verification.getVerificationID()
                    + " | Landlord: " + verification.getLandlordName()
                    + " | Status: " + verification.getStatus());
        }
        System.out.println("Records found: " + verifications.size());
    }
}
