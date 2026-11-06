-- Run AFTER SafeStay_Database.sql. Run once.
USE SAFESTAY;

-- The booking form has a notes box, but BOOKING had no column for it.
ALTER TABLE BOOKING ADD COLUMN notes TEXT;

-- Sample users (passwords are placeholders, not real hashes; real login comes later)
INSERT IGNORE INTO LANDLORD (firstName, lastName, email, password, phoneNumber, dateRegistered) VALUES
 ('Sipho', 'Dlamini', 'sipho.landlord@safestay.test', 'placeholder', '0820000001', CURDATE()),
 ('Karen', 'Botha',   'karen.landlord@safestay.test',  'placeholder', '0820000002', CURDATE());

INSERT IGNORE INTO ADMIN (firstName, lastName, email, password) VALUES
 ('Admin', 'User', 'admin@safestay.test', 'placeholder');

INSERT IGNORE INTO STUDENT (firstName, lastName, email, password, institution, dateRegistered) VALUES
 ('Demo', 'Student', 'demo.student@safestay.test', 'placeholder', 'Demo University', CURDATE());

SET @l1 = (SELECT landlordID FROM LANDLORD WHERE email = 'sipho.landlord@safestay.test');
SET @l2 = (SELECT landlordID FROM LANDLORD WHERE email = 'karen.landlord@safestay.test');
SET @adm = (SELECT adminID FROM ADMIN WHERE email = 'admin@safestay.test');

-- imagePath holds the picture key used by the page: studio | residence | cottage | riverside
INSERT INTO ACCOMMODATION (landlordID, name, address, price, numberOfRooms, roomType, distanceToCampus, availabilityStatus, dateListed, imagePath) VALUES
 (@l1, 'Sunnyside Studio',     '12 Campus Road',   4200.00, 1, 'Studio',      0.4, 'Available', CURDATE(), 'studio'),
 (@l1, 'Green Oak Residence',  '5 Oak Avenue',     2950.00, 1, 'Shared room', 1.1, 'Available', CURDATE(), 'residence'),
 (@l2, 'Hilltop Cottage',      '88 Hill Street',   3600.00, 1, 'Single room', 2.0, 'Available', CURDATE(), 'cottage'),
 (@l1, 'Riverside Digs',       '3 River Lane',     4800.00, 1, 'Studio',      0.9, 'Available', CURDATE(), 'riverside');

-- Landlord 1 is verified (shows the "Verified" badge); landlord 2 is not.
INSERT INTO VERIFICATION (landlordID, adminID, idDocument, propertyProof, status, verificationDate) VALUES
 (@l1, @adm, 'id_sipho.pdf', 'deed_sipho.pdf', 'Approved', CURDATE());
