package za.ac.cput.safestay;

/** Accommodation details shown in the administrator's pending-listings queue. */
public class AccommodationListing {
    private int accommodationID;
    private int landlordID;
    private String name;
    private String address;
    private double price;
    private int numberOfRooms;
    private String roomType;
    private double distanceToCampus;
    private String moveInDate;
    private String propertyRules;
    private String availabilityStatus;
    private String dateListed;
    private String imagePath;
    private String status;
    private String landlordName;
    private String landlordEmail;

    public int getAccommodationID() { return accommodationID; }
    public void setAccommodationID(int value) { accommodationID = value; }
    public int getLandlordID() { return landlordID; }
    public void setLandlordID(int value) { landlordID = value; }
    public String getName() { return name; }
    public void setName(String value) { name = value; }
    public String getAddress() { return address; }
    public void setAddress(String value) { address = value; }
    public double getPrice() { return price; }
    public void setPrice(double value) { price = value; }
    public int getNumberOfRooms() { return numberOfRooms; }
    public void setNumberOfRooms(int value) { numberOfRooms = value; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String value) { roomType = value; }
    public double getDistanceToCampus() { return distanceToCampus; }
    public void setDistanceToCampus(double value) { distanceToCampus = value; }
    public String getMoveInDate() { return moveInDate; }
    public void setMoveInDate(String value) { moveInDate = value; }
    public String getPropertyRules() { return propertyRules; }
    public void setPropertyRules(String value) { propertyRules = value; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String value) { availabilityStatus = value; }
    public String getDateListed() { return dateListed; }
    public void setDateListed(String value) { dateListed = value; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String value) { imagePath = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
    public String getLandlordName() { return landlordName; }
    public void setLandlordName(String value) { landlordName = value; }
    public String getLandlordEmail() { return landlordEmail; }
    public void setLandlordEmail(String value) { landlordEmail = value; }
}
