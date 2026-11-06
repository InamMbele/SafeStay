# SafeStay - Booking & Payment section (desktop app)

Runs as a normal Java window (Swing). There is **no web server and no link** to open.

## Run it
1. In MySQL run `SafeStay_Database.sql` (the group ERD script), then `SafeStay_Setup.sql` (sample data only).
2. Download the MySQL JDBC driver (`mysql-connector-j-x.y.z.jar`) from https://dev.mysql.com/downloads/connector/j/ and put it in the `lib` folder.
3. Double-click / run `build_and_run.bat` (Windows) or `./build_and_run.sh` (Mac/Linux). JDK 17+.
   In NetBeans/IntelliJ: add the jar as a library and run `safestay.SafeStayApp`.
4. Enter your MySQL password when asked (or set `SAFESTAY_DB_PASSWORD`; `SAFESTAY_DB_URL` and `SAFESTAY_DB_USER` are also supported).
5. Sign in with `demo.student@safestay.test` (temporary until the login section is integrated).

## Package layout (for integration)
- `safestay.Db` - the one place that opens the SAFESTAY connection
- `safestay.model` - Student, Accommodation, Booking, Payment, Pricing
- `safestay.dao` - StudentDAO, AccommodationDAO, BookingDAO, PaymentDAO (all SQL lives here)
- `safestay.ui.MainFrame` - the screens; `safestay.SafeStayApp` - starts the app

## Tables and columns used (all from the agreed ERD; no extra columns)
STUDENT(studentID, firstName, lastName, email) - ACCOMMODATION(accommodationID, landlordID, name, address, price, roomType, distanceToCampus, availabilityStatus) -
BOOKING(bookingID, studentID, accommodationID, bookingDate, startDate, endDate, status) - PAYMENT(paymentID, bookingID, amount, paymentDate, paymentMethod, paymentStatus) -
VERIFICATION(landlordID, status).
Status values the code relies on: availabilityStatus `Available`; verification `Approved`; booking `Pending` / `Confirmed` / `Cancelled`; payment `Completed`.

## Functionality
| Feature | How |
|---|---|
| View listings | Reads ACCOMMODATION (+ VERIFICATION for the Verified column) |
| Book | INSERT into BOOKING (Pending); blocks overlapping dates for the same accommodation |
| Pay | INSERT into PAYMENT + UPDATE BOOKING to Confirmed in one transaction; amount calculated from database values |
| Change dates (update) | UPDATE BOOKING, Pending bookings only |
| Delete | DELETE a Pending, unpaid booking |
| Cancel | UPDATE BOOKING to Cancelled, Confirmed bookings only |
| Validation | Empty/invalid dates, past dates, move-out before move-in, empty/non-16-digit card, nothing selected, double payment, DB errors shown as messages |

## Status against the integration checklist
- Database: uses SAFESTAY only (no safestay_db, no Derby). Connection test runs at start-up with a clear error if it fails.
- Structure: SQL uses only ERD tables/columns. The earlier `notes` column was removed.
- Compiles cleanly. Pricing logic checked.
- **NOT yet tested by the author against a live MySQL server or by clicking through the window.** Whoever runs it first must tick off section 4 of the checklist (run every button against SAFESTAY) and report problems to the group.

## Still incomplete / not part of this section
- Real login (the sign-in box only looks up a student by email; no password check).
- Real payment gateway (payment is simulated; card number is validated but never stored).
- Refunds: cancelling a Confirmed booking keeps the PAYMENT row as `Completed`.
- Reviews, reports, saved listings, landlord and admin screens.
