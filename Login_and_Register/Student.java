/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package safestayapp;

/**
 *
 * @author 240910591
 */
import java.time.LocalDate;

public class Student {

    private int studentID;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private String institution;
    private String course;
    private String budgetRange;
    private String preferredArea;
    private LocalDate dateRegistered;

    public Student(int studentID, String firstName, String lastName,
            String email, String phoneNumber, String password,
            String institution, String course, String budgetRange,
            String preferredArea, LocalDate dateRegistered) {

        this.studentID = studentID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.institution = institution;
        this.course = course;
        this.budgetRange = budgetRange;
        this.preferredArea = preferredArea;
        this.dateRegistered = dateRegistered;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getBudgetRange() {
        return budgetRange;
    }

    public void setBudgetRange(String budgetRange) {
        this.budgetRange = budgetRange;
    }

    public String getPreferredArea() {
        return preferredArea;
    }

    public void setPreferredArea(String preferredArea) {
        this.preferredArea = preferredArea;
    }

    public LocalDate getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(LocalDate dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
