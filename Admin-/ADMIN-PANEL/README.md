# SafeStay Admin Dashboard

This NetBeans Maven project contains the SafeStay administrator dashboard.

## Requirements
- JDK 17 or newer
- MySQL Server
- MySQL Connector/J 8.4.0 (provided through Maven)
- The SafeStay database schema

## Database connection
`DBConnection.java` connects to `jdbc:mysql://localhost:3306/SAFESTAY`.
The database username and password are read from `SAFESTAY_DB_USER` and
`SAFESTAY_DB_PASSWORD` environment variables. If those variables are not set,
the defaults are `root` and `password`. Change the defaults or set environment
variables to match your local MySQL account.

## Admin dashboard behaviour
- **Pending Listings** loads accommodation rows whose `accommodation.status` is `Pending`.
- Approve and Reject update that accommodation row's `status` using its actual `accommodationID`.
- **Pending Landlord Verifications** remains a separate queue backed by the `verification` table.
- **Reported Listings** displays each report row separately, including its report ID, student and accommodation details.
- `availabilityStatus` is not changed by the admin approval workflow.

## Property images
The `accommodation.imagePath` column stores image paths, not image files. Put the actual image files in the project resources folder, for example:

`src/main/resources/images/sunset.jpg`

The dashboard checks the path as stored and also checks `images/` for paths such as `sunset.jpg`. The existing database paths are preserved; no image files have been fabricated or added.

## Build
Open the project in NetBeans as a Maven project, then use **Clean and Build**.
