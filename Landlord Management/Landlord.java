package safestay.model;

import java.util.Date;

public class Landlord {
    public int landlordID;
    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public String phoneNumber;
    public Date dateRegistered;

    public Landlord() {}

    public Landlord(String firstName, String lastName, String email, String password, String phone){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phone;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhoneNumber() { return phoneNumber; }
    public int getLandlordID() { return landlordID; }
    public String getFullName() { return firstName + " " + lastName; }
}