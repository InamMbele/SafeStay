SAFESTAY - LANDLORD MEMBER 3
============================

PROJECT DATABASE
----------------
Database name: SAFESTAY
Database script: database/SafeStay_Database.sql
Java main class: safestay.ui.LandlordRegistrationForm
Database test class: safestay.db.DatabaseConnectionTest

IMPORTANT DATABASE CREDENTIAL SETUP
-----------------------------------
The project does NOT contain a real MySQL password. It reads credentials from Java system properties.
In NetBeans:
1. Right-click the project > Properties.
2. Select Run.
3. In VM Options, enter:
   -Dsafestay.db.user=root -Dsafestay.db.password=YOUR_LOCAL_MYSQL_PASSWORD
4. Replace YOUR_LOCAL_MYSQL_PASSWORD with the password for your local MySQL root account.
   If your root account has no password, use an empty value after the equals sign:
   -Dsafestay.db.user=root -Dsafestay.db.password=
5. Click OK. Do not commit or send your real password in the project ZIP.

SETUP AND TESTING (USE A TEST DATABASE COPY IF YOU ALREADY HAVE IMPORTANT DATA)
-------------------------------------------------------------------------------
1. Start MySQL in XAMPP / MySQL Workbench / your local MySQL service.
2. Open MySQL Workbench and connect to your local server.
3. Check whether SAFESTAY already exists and contains the agreed 10 tables. If it does, DO NOT execute the
   CREATE TABLE script again; use the existing group database. If SAFESTAY is new/empty, open
   database/SafeStay_Database.sql and execute it once. The script creates/uses SAFESTAY and its 10 tables.
   WARNING: The script uses CREATE TABLE without DROP TABLE. It will not erase existing tables, but trying
   to run it over tables that already exist can produce “table already exists” errors. Back up important data.
4. In NetBeans choose File > Open Project and select this project folder.
5. Confirm the Libraries node contains lib/mysql-connector-j-8.0.33.jar.
6. Set the VM Options as described above.
7. Right-click DatabaseConnectionTest.java > Run File. The Output window must say:
   Connected database: SAFESTAY
   SUCCESS: Java is connected to SAFESTAY.
8. Run the project (F6). The Landlord Registration window should open.

FUNCTIONAL TESTS - USE CLEARLY IDENTIFIABLE TEST DATA
----------------------------------------------------
A. LANDLORD CREATE
1. Enter a test first name, last name, unique email address, password of at least 8 characters and phone number.
2. Click Register Landlord.
3. Expect a success message and cleared fields.
4. In Workbench run: SELECT landlordID, firstName, lastName, email, password FROM SAFESTAY.LANDLORD;
5. Confirm your test row exists and the password column starts with PBKDF2$ (it must NOT show the password you typed).

B. EMPTY / INVALID REGISTRATION
1. Try registering with a required field empty. Expect a warning and no row inserted.
2. Try an invalid email. Expect a warning and no row inserted.
3. Try a password shorter than 8 characters. Expect a warning and no row inserted.
4. Try the same email again. Expect the duplicate-email message and no duplicate row.

C. LANDLORD READ / UPDATE
1. Click View / Update / Delete Landlords.
2. Confirm the test landlord appears in the table.
3. Enter that landlordID and click Load.
4. Change the first name or phone number and click UPDATE Landlord.
5. Confirm the table refreshes and Workbench shows the changed value:
   SELECT * FROM SAFESTAY.LANDLORD WHERE landlordID = YOUR_TEST_ID;

D. ADD ACCOMMODATION
1. Return to the main registration window and click Add Accommodation.
2. Enter the test landlordID from section A, property name, address, price, room count, room type and distance.
3. Move-in date is optional; if supplied, use yyyy-mm-dd (example: 2026-12-01).
4. Click Save Accommodation. Expect a success message.
5. In Workbench run:
   SELECT * FROM SAFESTAY.ACCOMMODATION WHERE landlordID = YOUR_TEST_ID;
6. Confirm the row exists, availabilityStatus is Available and dateListed is today's date.
7. Try a non-existent landlordID; the form should reject it. Try blank required fields, negative price, zero rooms and invalid date; each should be rejected.

E. ACCOMMODATION READ / UPDATE / DELETE
1. From the main window click Manage Accommodation Listings.
2. Enter the test landlordID and click Load Listings. Confirm the test listing appears.
3. Select the test listing row, enter a new non-negative price and click Update Selected Price.
4. Confirm the changed price in the table and in Workbench:
   SELECT accommodationID, price FROM SAFESTAY.ACCOMMODATION WHERE landlordID = YOUR_TEST_ID;
5. Select only the disposable test listing and click Delete Selected Listing. Read the warning and confirm.
6. Confirm the test listing and its linked dependent records are removed. Do not do this with real group data.

F. LANDLORD DELETE - DESTRUCTIVE
1. ONLY use the disposable test landlord and its test accommodation. Never test deletion on real group data.
2. In the dashboard enter the test landlordID and click DELETE Landlord.
3. Read the warning carefully and confirm only if it is the test record.
4. Confirm the landlord and linked accommodation are gone:
   SELECT * FROM SAFESTAY.LANDLORD WHERE landlordID = YOUR_TEST_ID;
   SELECT * FROM SAFESTAY.ACCOMMODATION WHERE landlordID = YOUR_TEST_ID;
5. Check linked rows by the test accommodationID if you created bookings/reviews/reports/saved rows for it.

WHAT THIS PROJECT DOES NOT CLAIM
--------------------------------
- The code has been compiled, but a live MySQL connection and GUI tests must be completed on your computer.
- The supplied ERD SQL has no login table/workflow, so this module hashes passwords on registration but does not implement password login/verification.
- The accommodation management screen supports listing retrieval, price updates and transactional deletion. Test these only with disposable records.
- Test the delete functions only with disposable test records. Deleting a landlord deliberately deletes linked accommodation and dependent records to satisfy the supplied foreign keys.
