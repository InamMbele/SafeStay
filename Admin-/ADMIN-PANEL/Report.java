package za.ac.cput.safestay;

/** Represents a student-submitted accommodation report. */
public class Report {

    private int reportID;
    private int studentID;
    private int accommodationID;
    private String reason;
    private String description;
    private String evidence;
    private String reportDate;
    private String status;
    private String studentName;
    private String studentEmail;
    private String accommodationName;
    private String accommodationAddress;
    private String accommodationImagePath;

    public int getReportID() { return reportID; }
    public void setReportID(int reportID) { this.reportID = reportID; }
    public int getStudentID() { return studentID; }
    public void setStudentID(int studentID) { this.studentID = studentID; }
    public int getAccommodationID() { return accommodationID; }
    public void setAccommodationID(int accommodationID) { this.accommodationID = accommodationID; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEvidence() { return evidence; }
    public void setEvidence(String evidence) { this.evidence = evidence; }
    public String getReportDate() { return reportDate; }
    public void setReportDate(String reportDate) { this.reportDate = reportDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    public String getAccommodationName() { return accommodationName; }
    public void setAccommodationName(String accommodationName) { this.accommodationName = accommodationName; }
    public String getAccommodationAddress() { return accommodationAddress; }
    public void setAccommodationAddress(String accommodationAddress) { this.accommodationAddress = accommodationAddress; }
    public String getAccommodationImagePath() { return accommodationImagePath; }
    public void setAccommodationImagePath(String accommodationImagePath) { this.accommodationImagePath = accommodationImagePath; }
}
