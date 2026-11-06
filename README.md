# SafeStay – Reports & Reviews pages (Java Swing + JDBC + MySQL)

Two admin pages for the SafeStay accommodation system:

| Page | What it does |
|---|---|
| **Reports** (Admin Reports Dashboard) | Status cards, status filter, table, *View Report* details window, **create** a report, **change status**, **delete**, **export to CSV** |
| **Reviews** | Rating summary cards, accommodation filter, table, **add / edit / delete** a review (4 ratings 1–5 + comment) |

No Spring Boot. Plain Swing + JDBC. The only external library is the MySQL JDBC driver.

---

## 1. Project layout

```
Programming_assignment/
├── SafeStay_Database.sql      <- the original schema (unchanged)
├── SafeStay_SeedData.sql      <- sample data (new)
├── db.properties              <- DB url / user / password (edit this)
├── pom.xml                    <- Maven build (pulls the JDBC driver for you)
├── AdminReportsDashboard/     <- the original single-file version, kept for reference
└── src/main/java/com/safestay/
    ├── Main.java              <- standalone launcher (has the main method)
    ├── SafeStayModule.java    <- ONE-LINE wiring point for integration
    ├── db/          ConnectionProvider, DatabaseConnection     (JDBC setup)
    ├── model/       Report, ReportStatus, Review, RatingSummary, Option   (plain data)
    ├── dao/         ReportDao, ReviewDao, LookupDao            (ALL the SQL lives here)
    ├── service/     ReportService, ReviewService, LookupService   (ALL the rules live here)
    ├── exception/   SafeStayException, ValidationException, DataAccessException
    └── ui/          ReportsPanel, ReviewsPanel + dialogs, Theme   (Swing only, no SQL)
```

### Separation of concerns

```
 ui  ──►  service  ──►  dao  ──►  ConnectionProvider ──► MySQL
(Swing)  (validation,  (SQL via
          rules, CSV)   PreparedStatement)
```

* **ui** never touches `java.sql`. It calls a service and displays the result or the error message.
* **service** holds business rules (ratings must be 1–5, one review per student per accommodation, new reports start *Pending*, CSV escaping). It has no Swing and no SQL.
* **dao** holds every SQL statement (all `PreparedStatement`s with try-with-resources) and converts `SQLException` into `DataAccessException`.
* **db** only knows how to open a connection.

---

## 2. Run the project

### Prerequisites
* JDK 11 or newer (`java -version`)
* Apache Maven (`mvn -version`) – downloads the MySQL driver automatically
* MySQL Server + MySQL Workbench

### Step 1 – Create the database in MySQL Workbench
1. Open Workbench and connect to your local server.
2. **File → Open SQL Script…** → choose `SafeStay_Database.sql` → click the ⚡ **Execute** button.
   This creates the `SAFESTAY` database and all 10 tables.
3. Open `SafeStay_SeedData.sql` the same way and execute it (sample students, accommodations, 4 reports and 5 reviews so the pages are not empty).
4. Refresh the *Schemas* panel; you should see `safestay` with its tables.
   **Check this before running the app**: expand *Tables*, or run `SHOW TABLES;`. You need 10 tables. An empty `safestay` schema is the cause of "table not loaded" errors.

> `SafeStay_Database.sql` has no `IF NOT EXISTS`, so run it **once**. To start over, run `DROP DATABASE SAFESTAY;` first. `SafeStay_SeedData.sql` can be re-run safely.

### Step 2 – Put your MySQL login in `db.properties`

```properties
db.url=jdbc:mysql://localhost:3306/SAFESTAY?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=5000
db.user=root
db.password=YOUR_PASSWORD
```

Alternatively set the environment variables `SAFESTAY_DB_URL`, `SAFESTAY_DB_USER`, `SAFESTAY_DB_PASSWORD` (they override the file). Do not commit a real password.

### Step 3 – Run

From the project folder (the one containing `pom.xml`):

```bash
mvn compile exec:java
```

or build a single runnable jar:

```bash
mvn package
java -jar target/safestay-app.jar
```

`db.properties` is read from the **current working directory**, so run these commands from the project folder.

If the database cannot be reached, a dialog shows the URL used and the MySQL error instead of an empty window.

### Running without Maven (plain `javac`)
1. Download `mysql-connector-j-8.4.0.jar` from Maven Central and put it in a `lib/` folder.
2. Compile and run (Windows uses `;` in the classpath, macOS/Linux use `:`):
```bash
javac -cp lib/mysql-connector-j-8.4.0.jar -d out $(find src/main/java -name "*.java")
java  -cp "out;lib/mysql-connector-j-8.4.0.jar" com.safestay.Main
```
(In an IDE: add the jar as a library, mark `src/main/java` as the source root, run `com.safestay.Main`.)

### Troubleshooting

| Symptom | Cause and fix |
|---|---|
| `Table 'safestay.report' doesn't exist` / reports or reviews fail to load | The database exists but the tables were never created (the schema script was not run, or it stopped partway in Workbench). Run `SafeStay_Database.sql`, then `SafeStay_SeedData.sql`. To confirm: `SHOW TABLES;` in the `SAFESTAY` schema should list 10 tables. |
| *Database Connection Failed* dialog on start | MySQL is not running, or `db.user` / `db.password` in `db.properties` is wrong. The dialog shows the URL and MySQL's own message. |
| Same errors even though `db.properties` is correct | The app reads `db.properties` from the **folder you launch it in**. Run from the project folder. Environment variables `SAFESTAY_DB_*` override the file, so check they are not set to something else. |
| `Access denied for user` | Wrong password, or that MySQL user cannot access the `SAFESTAY` database. |
| `No suitable driver` / `ClassNotFoundException: com.mysql...` | The MySQL driver jar is not on the classpath (only happens without Maven; see "Running without Maven"). |
| Partially created schema, want a clean restart | `DROP DATABASE SAFESTAY;` then re-run both scripts. |

---

## 3. Integrate into another Java project

You do **not** need `Main.java`. Everything is reached through `SafeStayModule`.

### A. Copy the code
1. Copy the whole `src/main/java/com/safestay` folder into the other project's source root (keep the folder `com/safestay` intact).
2. Add the MySQL driver to that project:
   * **Maven** – add to its `pom.xml`:
     ```xml
     <dependency>
         <groupId>com.mysql</groupId>
         <artifactId>mysql-connector-j</artifactId>
         <version>8.4.0</version>
     </dependency>
     ```
   * **Plain / IDE project** – add `mysql-connector-j-8.4.0.jar` to the classpath / libraries.
3. Make sure the host project targets **Java 11+** (no Java 17 features are used).
4. Run `SafeStay_Database.sql` (+ optionally the seed file) against the shared MySQL server, and copy `db.properties` next to wherever the app is launched (or use the env variables).

### B. Show the pages (the only code you write)

```java
import com.safestay.SafeStayModule;
import com.safestay.db.DatabaseConnection;

SafeStayModule module = new SafeStayModule(new DatabaseConnection());

JTabbedPane tabs = new JTabbedPane();           // or any container / CardLayout
tabs.addTab("Reports", module.createReportsPanel());
tabs.addTab("Reviews", module.createReviewsPanel());

frame.add(tabs);                                // frame = their existing JFrame
```

`createReportsPanel()` and `createReviewsPanel()` return ordinary `JPanel`s, so they work in a `JTabbedPane`, a `CardLayout`, a sidebar menu, or alone in a `JFrame`. Call this on the Swing thread (`SwingUtilities.invokeLater`).

### C. If the host project already has its own database connection

Skip `DatabaseConnection` and pass any `ConnectionProvider` (a one-method interface):

```java
SafeStayModule module = new SafeStayModule(() -> MyDb.getConnection());
// or: new SafeStayModule(() -> myDataSource.getConnection());
```

Each DAO call opens and closes its own connection (try-with-resources), so the provider must return a **new / pooled** connection each time and not a shared one that gets closed.

### D. Package names – what to change if you rename them

Everything lives under `com.safestay`. If the other project uses a different base package (say `org.school.app.safestay`):

1. Move the folders to match, and change the `package` line at the top of every file.
2. Change every `import com.safestay.…` to the new base package.
   *In IntelliJ / Eclipse / VS Code use **Refactor → Rename/Move package**; it updates both automatically.*
3. Update `main.class` in `pom.xml` only if you still want to use `Main` as the launcher.

Dependency order between the packages (useful when copying only part of it):

```
model, exception  ← no dependencies
db                ← java.sql only
dao               ← db, model, exception
service           ← dao, model, exception
ui                ← service, model, exception   (+ javax.swing)
SafeStayModule    ← everything above
```

### E. Use the logic without the screens

If the other project has its own UI, take only `db`, `model`, `dao`, `service`, `exception` and call the services:

```java
SafeStayModule module = new SafeStayModule(new DatabaseConnection());

List<Report> pending = module.getReportService().listReports(ReportStatus.PENDING);
module.getReviewService().createReview(review);   // throws ValidationException with a user-friendly message
```

### F. Joining an existing `Main`

If the other project's own `main` method opens its own window, add the two lines from section B where it builds its UI. Nothing else needs to start. There is no static state, so calling `new SafeStayModule(...)` more than once is fine.

---

## 4. Conventions kept from the original code

* Same colours, Arial fonts, stat cards, "View Report" column, status names (*Pending, Under Review, Resolved, Rejected*) and CSV export format.
* Same code style: banner comments (`// ===== Section =====`), one statement per line, no wildcard magic beyond `javax.swing.*` / `java.awt.*`.
* Report ids display as `#1045` (the `REPORT.reportID`). The reporter shows as `Thabo M.` (first name + last initial from `STUDENT`).

## 5. Notes / limitations

* Database calls run on the Swing thread (simple, and fast on a local MySQL). For a remote or slow database, move the calls in the panels' `refresh…` methods into a `SwingWorker`.
* Passwords in `SeedData` are dummy text, not hashes. Login/registration is outside the scope of these two pages.
* **Table names are upper-case** in the schema (`REPORT`, `REVIEW`, …) and the SQL uses the same spelling, which matters on Linux/macOS MySQL where table names are case-sensitive.
* The reasons offered in *New Report* are suggestions; the field is editable, so any reason up to 100 characters works.
