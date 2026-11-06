package za.ac.cput.safestay;

/** Represents a landlord verification request stored in the database. */
public class Verification {

    private int verificationID;
    private int landlordID;
    private int adminID;
    private String idDocument;
    private String propertyProof;
    private String status;
    private String verificationDate;
    private String landlordName;
    private String landlordEmail;
    private String accommodationName;
    private String accommodationAddress;
    private String accommodationImagePath;

    public Verification() {
    }

    public Verification(int verificationID, int landlordID, int adminID,
                        String idDocument, String propertyProof,
                        String status, String verificationDate) {
        this.verificationID = verificationID;
        this.landlordID = landlordID;
        this.adminID = adminID;
        this.idDocument = idDocument;
        this.propertyProof = propertyProof;
        this.status = status;
        this.verificationDate = verificationDate;
    }

    public int getVerificationID() { return verificationID; }
    public void setVerificationID(int verificationID) { this.verificationID = verificationID; }
    public int getLandlordID() { return landlordID; }
    public void setLandlordID(int landlordID) { this.landlordID = landlordID; }
    public int getAdminID() { return adminID; }
    public void setAdminID(int adminID) { this.adminID = adminID; }
    public String getIdDocument() { return idDocument; }
    public void setIdDocument(String idDocument) { this.idDocument = idDocument; }
    public String getPropertyProof() { return propertyProof; }
    public void setPropertyProof(String propertyProof) { this.propertyProof = propertyProof; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getVerificationDate() { return verificationDate; }
    public void setVerificationDate(String verificationDate) { this.verificationDate = verificationDate; }
    public String getLandlordName() { return landlordName; }
    public void setLandlordName(String landlordName) { this.landlordName = landlordName; }
    public String getLandlordEmail() { return landlordEmail; }
    public void setLandlordEmail(String landlordEmail) { this.landlordEmail = landlordEmail; }
    public String getAccommodationName() { return accommodationName; }
    public void setAccommodationName(String accommodationName) { this.accommodationName = accommodationName; }
    public String getAccommodationAddress() { return accommodationAddress; }
    public void setAccommodationAddress(String accommodationAddress) { this.accommodationAddress = accommodationAddress; }
    public String getAccommodationImagePath() { return accommodationImagePath; }
    public void setAccommodationImagePath(String accommodationImagePath) { this.accommodationImagePath = accommodationImagePath; }
}
