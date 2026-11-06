CREATE DATABASE IF NOT EXISTS SAFESTAY;

USE SAFESTAY;

-- 1. STUDENT

CREATE TABLE STUDENT (
    studentID INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phoneNumber VARCHAR(20),
    institution VARCHAR(100),
    course VARCHAR(100),
    budgetRange VARCHAR(50),
    preferredArea VARCHAR(100),
    dateRegistered DATE
);

-- 2. LANDLORD


CREATE TABLE LANDLORD (
    landlordID INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phoneNumber VARCHAR(20),
    dateRegistered DATE
);

-- 3. ADMIN


CREATE TABLE ADMIN (
    adminID INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- 4. ACCOMMODATION

CREATE TABLE ACCOMMODATION (
    accommodationID INT AUTO_INCREMENT PRIMARY KEY,
    landlordID INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    numberOfRooms INT,
    roomType VARCHAR(50),
    distanceToCampus DECIMAL(6,2),
    moveInDate DATE,
    propertyRules TEXT,
    availabilityStatus VARCHAR(30),
    dateListed DATE,
    imagePath VARCHAR(255),

    CONSTRAINT fk_accommodation_landlord
        FOREIGN KEY (landlordID)
        REFERENCES LANDLORD(landlordID)
);


-- 5. BOOKING


CREATE TABLE BOOKING (
    bookingID INT AUTO_INCREMENT PRIMARY KEY,
    studentID INT NOT NULL,
    accommodationID INT NOT NULL,
    bookingDate DATE,
    startDate DATE,
    endDate DATE,
    status VARCHAR(30),

    CONSTRAINT fk_booking_student
        FOREIGN KEY (studentID)
        REFERENCES STUDENT(studentID),

    CONSTRAINT fk_booking_accommodation
        FOREIGN KEY (accommodationID)
        REFERENCES ACCOMMODATION(accommodationID)
);


-- 6. PAYMENT


CREATE TABLE PAYMENT (
    paymentID INT AUTO_INCREMENT PRIMARY KEY,
    bookingID INT NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    paymentDate DATE,
    paymentMethod VARCHAR(50),
    paymentStatus VARCHAR(30),

    CONSTRAINT fk_payment_booking
        FOREIGN KEY (bookingID)
        REFERENCES BOOKING(bookingID)
);


-- 7. REVIEW


CREATE TABLE REVIEW (
    reviewID INT AUTO_INCREMENT PRIMARY KEY,
    studentID INT NOT NULL,
    accommodationID INT NOT NULL,
    safetyRating INT,
    cleanlinessRating INT,
    wifiRating INT,
    landlordBehaviourRating INT,
    comment TEXT,
    reviewDate DATE,

    CONSTRAINT fk_review_student
        FOREIGN KEY (studentID)
        REFERENCES STUDENT(studentID),

    CONSTRAINT fk_review_accommodation
        FOREIGN KEY (accommodationID)
        REFERENCES ACCOMMODATION(accommodationID),

    CONSTRAINT unique_student_accommodation_review
        UNIQUE (studentID, accommodationID)
);


-- 8. REPORT


CREATE TABLE REPORT (
    reportID INT AUTO_INCREMENT PRIMARY KEY,
    studentID INT NOT NULL,
    accommodationID INT NOT NULL,
    reason VARCHAR(100) NOT NULL,
    description TEXT,
    evidence VARCHAR(255),
    reportDate DATE,
    status VARCHAR(30),

    CONSTRAINT fk_report_student
        FOREIGN KEY (studentID)
        REFERENCES STUDENT(studentID),

    CONSTRAINT fk_report_accommodation
        FOREIGN KEY (accommodationID)
        REFERENCES ACCOMMODATION(accommodationID)
);


-- 9. SAVED_ACCOMMODATION


CREATE TABLE SAVED_ACCOMMODATION (
    savedID INT AUTO_INCREMENT PRIMARY KEY,
    studentID INT NOT NULL,
    accommodationID INT NOT NULL,
    savedDate DATE,

    CONSTRAINT fk_saved_student
        FOREIGN KEY (studentID)
        REFERENCES STUDENT(studentID),

    CONSTRAINT fk_saved_accommodation
        FOREIGN KEY (accommodationID)
        REFERENCES ACCOMMODATION(accommodationID),

    CONSTRAINT unique_student_saved_accommodation
        UNIQUE (studentID, accommodationID)
);


-- 10. VERIFICATION


CREATE TABLE VERIFICATION (
    verificationID INT AUTO_INCREMENT PRIMARY KEY,
    landlordID INT NOT NULL,
    adminID INT NOT NULL,
    idDocument VARCHAR(255),
    propertyProof VARCHAR(255),
    status VARCHAR(30),
    verificationDate DATE,

    CONSTRAINT fk_verification_landlord
        FOREIGN KEY (landlordID)
        REFERENCES LANDLORD(landlordID),

    CONSTRAINT fk_verification_admin
        FOREIGN KEY (adminID)
        REFERENCES ADMIN(adminID)
);

USE SAFESTAY;

SHOW TABLES;