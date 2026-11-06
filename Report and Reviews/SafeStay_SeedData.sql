-- =====================================================
-- SafeStay sample data
-- Run AFTER SafeStay_Database.sql. Safe to run more than once
-- (INSERT IGNORE skips rows whose id already exists).
-- Passwords are dummy placeholders, not real hashes.
-- =====================================================

USE SAFESTAY;

-- Students

INSERT IGNORE INTO STUDENT
    (studentID, firstName, lastName, email, password, phoneNumber, institution, course, dateRegistered)
VALUES
    (1, 'Thabo',  'Mokoena', 'thabo.m@example.com',  'placeholder', '0711111111', 'University of Cape Town', 'Computer Science', '2026-01-15'),
    (2, 'Anita',  'Khumalo', 'anita.k@example.com',  'placeholder', '0722222222', 'Stellenbosch University', 'Law',              '2026-01-20'),
    (3, 'James',  'Botha',   'james.b@example.com',  'placeholder', '0733333333', 'University of the Western Cape', 'Commerce',  '2026-02-02'),
    (4, 'Sarah',  'Layton',  'sarah.l@example.com',  'placeholder', '0744444444', 'University of Cape Town', 'Architecture',     '2026-02-10'),
    (5, 'Naledi', 'Dlamini', 'naledi.d@example.com', 'placeholder', '0755555555', 'CPUT',                    'Engineering',      '2026-03-01');

-- Landlords

INSERT IGNORE INTO LANDLORD
    (landlordID, firstName, lastName, email, password, phoneNumber, dateRegistered)
VALUES
    (1, 'Pieter', 'van Wyk', 'pieter.vw@example.com', 'placeholder', '0821111111', '2025-11-01'),
    (2, 'Lindiwe', 'Nkosi',  'lindiwe.n@example.com', 'placeholder', '0822222222', '2025-11-12');

-- Accommodations

INSERT IGNORE INTO ACCOMMODATION
    (accommodationID, landlordID, name, address, price, numberOfRooms, roomType,
     distanceToCampus, availabilityStatus, dateListed)
VALUES
    (1, 1, 'Campus View',       '12 University Rd, Rondebosch', 4200.00, 20, 'Single', 0.50, 'Available', '2026-01-05'),
    (2, 1, 'Student Haven',     '48 Main St, Observatory',      3600.00, 35, 'Shared', 1.20, 'Available', '2026-01-08'),
    (3, 2, 'University Lodge',  '7 Kloof Ave, Mowbray',         3900.00, 15, 'Single', 0.80, 'Available', '2026-01-10'),
    (4, 2, 'Modern Heights',    '101 High St, Woodstock',       5100.00, 40, 'Studio', 2.10, 'Full',      '2026-01-12');

-- Reports (ids match the original dashboard sample: #1042 - #1045)

INSERT IGNORE INTO REPORT
    (reportID, studentID, accommodationID, reason, description, evidence, reportDate, status)
VALUES
    (1045, 1, 1, 'Fake Listing',     'The photos do not match the actual building and the listed amenities do not exist.', 'photos-comparison.zip', '2026-08-12', 'Pending'),
    (1044, 2, 2, 'Offensive Review', 'A review on this listing contains abusive language aimed at the landlord.',           NULL,                    '2026-08-11', 'Rejected'),
    (1043, 3, 3, 'Scam Listing',     'Asked to pay a deposit before viewing and the landlord stopped responding.',          'chat-screenshots.png',  '2026-08-10', 'Pending'),
    (1042, 4, 4, 'Unsafe Listing',   'Broken front-door lock and exposed wiring in the common area.',                       'wiring.jpg',            '2026-08-09', 'Rejected');

-- Reviews

INSERT IGNORE INTO REVIEW
    (reviewID, studentID, accommodationID, safetyRating, cleanlinessRating,
     wifiRating, landlordBehaviourRating, comment, reviewDate)
VALUES
    (1, 1, 2, 4, 4, 3, 5, 'Friendly landlord and a short walk to campus. WiFi drops in the evenings.', '2026-05-20'),
    (2, 2, 1, 5, 4, 5, 4, 'Very safe building with security at the gate. Fast WiFi.',                   '2026-05-22'),
    (3, 3, 3, 3, 3, 2, 3, 'Fine for the price but the WiFi is unreliable.',                             '2026-06-01'),
    (4, 4, 4, 2, 3, 4, 2, 'Maintenance requests take weeks to be handled.',                             '2026-06-14'),
    (5, 5, 1, 4, 5, 4, 4, 'Spotless rooms and helpful staff.',                                          '2026-06-30');
