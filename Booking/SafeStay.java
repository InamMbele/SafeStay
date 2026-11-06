import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * SafeStay - booking + payment demo, now backed by MySQL (database SAFESTAY).
 *
 * 1. Run SafeStay_Database.sql, then SafeStay_Setup.sql in MySQL.
 * 2. Download the MySQL JDBC driver (mysql-connector-j-x.y.z.jar) from https://dev.mysql.com/downloads/connector/j/
 * 3. Set your DB password, then run (JDK 17+):
 *      Windows (cmd):   set SAFESTAY_DB_PASSWORD=yourpassword
 *                       java -cp mysql-connector-j-9.0.0.jar SafeStay.java
 *      Mac/Linux:       SAFESTAY_DB_PASSWORD=yourpassword java -cp mysql-connector-j-9.0.0.jar SafeStay.java
 * 4. Open http://localhost:8080
 */
public class SafeStay {

    // ---------- Model ----------
    record Accommodation(long id, String title, String area, BigDecimal rent, boolean verified, String photo) {}

    static class Booking {
        long id; Accommodation acc; LocalDate moveIn, moveOut; String notes;
        String status = "Pending";                 // Pending | Confirmed | Rejected | Cancelled
    }

    static class Payment {
        long id; Booking booking; BigDecimal amount; String method;
        String status = "Pending";                 // Pending | Completed | Failed | Refunded
        String reference;
    }

    // ---------- Database configuration (override with environment variables) ----------
    static final String DB_URL  = env("SAFESTAY_DB_URL", "jdbc:mysql://localhost:3306/SAFESTAY?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false");
    static final String DB_USER = env("SAFESTAY_DB_USER", "root");
    static final String DB_PASS = env("SAFESTAY_DB_PASSWORD", "");
    /** No login screen yet, so bookings are made as this seeded student (created by SafeStay_Setup.sql). */
    static final String DEMO_STUDENT_EMAIL = "demo.student@safestay.test";
    static final Set<String> PAY_METHODS = Set.of("Card", "Instant EFT", "Bursary/NSFAS");

    static String env(String k, String def) { String v = System.getenv(k); return v == null ? def : v; }

    static Connection db() throws SQLException { return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS); }

    // ---------- Server ----------
    public static void main(String[] args) throws IOException {
        try (Connection c = db()) {
            System.out.println("Connected to MySQL: " + DB_URL);
        } catch (SQLException e) {
            System.err.println("Could not connect to the database: " + e.getMessage());
            System.err.println("Check MySQL is running, the SAFESTAY_DB_* settings, and that the JDBC driver jar is on the classpath (-cp).");
            return;
        }
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", SafeStay::route);
        server.start();
        System.out.println("SafeStay running at http://localhost:" + port);
    }

    static void route(HttpExchange ex) throws IOException {
        try {
            String path = ex.getRequestURI().getPath();
            String method = ex.getRequestMethod();
            Map<String, String> q = parseForm(ex.getRequestURI().getRawQuery());

            if (path.equals("/") && method.equals("GET"))               send(ex, 200, listingsPage());
            else if (path.equals("/book") && method.equals("GET"))      send(ex, 200, bookPage(findAcc(q.get("id")), null));
            else if (path.equals("/book") && method.equals("POST"))     handleBook(ex);
            else if (path.equals("/pay") && method.equals("GET"))       send(ex, 200, payPage(findBooking(q.get("booking")), null));
            else if (path.equals("/pay") && method.equals("POST"))      handlePay(ex);
            else if (path.equals("/confirmation"))                      send(ex, 200, confirmationPage(findPayment(q.get("payment"))));
            else send(ex, 404, errorPage("Page not found", null));
        } catch (SQLException e) {
            e.printStackTrace();                       // details stay in the server log, not on the page
            send(ex, 500, errorPage("Database error", "Please try again in a moment."));
        } catch (Exception e) {
            send(ex, 400, errorPage("Something went wrong", String.valueOf(e.getMessage())));
        }
    }

    static String errorPage(String title, String detail) {
        return page(0, title, "<div class='panel center'><h2>" + esc(title) + "</h2>"
            + (detail != null ? "<p class='help'>" + esc(detail) + "</p>" : "")
            + "<a class='btn btn-primary' href='/'>Back to listings</a></div>");
    }

    // ---------- Data access ----------
    // "verified" = the landlord has an Approved row in VERIFICATION
    static final String ACC_COLS =
        "a.accommodationID, a.name, a.price, a.roomType, a.distanceToCampus, a.imagePath, "
      + "EXISTS (SELECT 1 FROM VERIFICATION v WHERE v.landlordID = a.landlordID AND v.status = 'Approved') AS verified";

    static Accommodation mapAcc(ResultSet rs) throws SQLException {
        BigDecimal dist = rs.getBigDecimal("distanceToCampus");
        String room = rs.getString("roomType");
        String area = (dist == null ? "" : dist.stripTrailingZeros().toPlainString() + " km from campus")
                    + (room == null ? "" : (dist == null ? "" : " · ") + room);
        String photo = rs.getString("imagePath");
        return new Accommodation(rs.getLong("accommodationID"), rs.getString("name"), area,
                                 rs.getBigDecimal("price"), rs.getBoolean("verified"), photo == null ? "studio" : photo);
    }

    static List<Accommodation> listAccommodations() throws SQLException {
        List<Accommodation> list = new ArrayList<>();
        try (Connection c = db();
             PreparedStatement ps = c.prepareStatement("SELECT " + ACC_COLS + " FROM ACCOMMODATION a WHERE a.availabilityStatus = 'Available' ORDER BY a.accommodationID");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapAcc(rs));
        }
        return list;
    }

    static Accommodation findAcc(String id) throws SQLException {
        long n = Long.parseLong(id);
        try (Connection c = db();
             PreparedStatement ps = c.prepareStatement("SELECT " + ACC_COLS + " FROM ACCOMMODATION a WHERE a.accommodationID = ?")) {
            ps.setLong(1, n);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new NoSuchElementException("Listing not found");
                return mapAcc(rs);
            }
        }
    }

    static Booking mapBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.id = rs.getLong("bookingID");
        b.acc = mapAcc(rs);
        b.moveIn = rs.getDate("startDate").toLocalDate();
        b.moveOut = rs.getDate("endDate").toLocalDate();
        b.notes = rs.getString("notes");
        b.status = rs.getString("bookingStatus");
        return b;
    }

    static Booking findBooking(String id) throws SQLException {
        long n = Long.parseLong(id);
        try (Connection c = db();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT b.bookingID, b.startDate, b.endDate, b.notes, b.status AS bookingStatus, " + ACC_COLS
               + " FROM BOOKING b JOIN ACCOMMODATION a ON a.accommodationID = b.accommodationID WHERE b.bookingID = ?")) {
            ps.setLong(1, n);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new NoSuchElementException("Booking not found");
                return mapBooking(rs);
            }
        }
    }

    static Payment findPayment(String id) throws SQLException {
        long n = Long.parseLong(id);
        try (Connection c = db();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT p.paymentID, p.amount, p.paymentMethod, p.paymentStatus, "
               + "b.bookingID, b.startDate, b.endDate, b.notes, b.status AS bookingStatus, " + ACC_COLS
               + " FROM PAYMENT p JOIN BOOKING b ON b.bookingID = p.bookingID "
               + "JOIN ACCOMMODATION a ON a.accommodationID = b.accommodationID WHERE p.paymentID = ?")) {
            ps.setLong(1, n);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new NoSuchElementException("Payment not found");
                Payment p = new Payment();
                p.id = rs.getLong("paymentID");
                p.booking = mapBooking(rs);
                p.amount = rs.getBigDecimal("amount");
                p.method = rs.getString("paymentMethod");
                p.status = rs.getString("paymentStatus");
                p.reference = "SS-" + String.format("%06d", p.id);   // derived from the payment ID (no extra column needed)
                return p;
            }
        }
    }

    static long demoStudentId(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT studentID FROM STUDENT WHERE email = ?")) {
            ps.setString(1, DEMO_STUDENT_EMAIL);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new IllegalStateException("Demo student not found - run SafeStay_Setup.sql first.");
                return rs.getLong(1);
            }
        }
    }

    // ---------- Handlers ----------
    static void handleBook(HttpExchange ex) throws Exception {
        Map<String, String> f = parseForm(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
        Accommodation a = findAcc(f.get("id"));
        LocalDate in, out;
        try {
            in = LocalDate.parse(f.getOrDefault("moveIn", ""));
            out = LocalDate.parse(f.getOrDefault("moveOut", ""));
        } catch (Exception e) {
            send(ex, 200, bookPage(a, "Please choose both a move-in and move-out date.")); return;
        }
        if (!out.isAfter(in)) { send(ex, 200, bookPage(a, "Move-out must be after move-in.")); return; }

        long bookingId;
        try (Connection c = db()) {
            long studentId = demoStudentId(c);
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO BOOKING (studentID, accommodationID, bookingDate, startDate, endDate, status, notes) VALUES (?,?,?,?,?,'Pending',?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, studentId);
                ps.setLong(2, a.id());
                ps.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                ps.setDate(4, java.sql.Date.valueOf(in));
                ps.setDate(5, java.sql.Date.valueOf(out));
                ps.setString(6, f.getOrDefault("notes", ""));
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); bookingId = keys.getLong(1); }
            }
        }
        redirect(ex, "/pay?booking=" + bookingId);
    }

    static void handlePay(HttpExchange ex) throws Exception {
        Map<String, String> f = parseForm(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
        Booking b = findBooking(f.get("booking"));
        String method = f.getOrDefault("method", "Card");

        if (!PAY_METHODS.contains(method)) throw new IllegalArgumentException("Unknown payment method");
        if (!"Pending".equals(b.status)) {
            send(ex, 200, errorPage("Already processed", "This booking has already been paid or is no longer open.")); return;
        }
        if (method.equals("Card")) {
            String digits = f.getOrDefault("cardNumber", "").replaceAll("\\s", "");
            if (!digits.matches("\\d{16}")) { send(ex, 200, payPage(b, "Enter a valid 16-digit card number.")); return; }
        }
        // Card details are validated but never stored. Simulated gateway: call PayFast, Stripe, Yoco... here.
        long paymentId;
        try (Connection c = db()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO PAYMENT (bookingID, amount, paymentDate, paymentMethod, paymentStatus) VALUES (?,?,?,?,'Completed')",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, b.id);
                    ps.setBigDecimal(2, total(b));
                    ps.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                    ps.setString(4, method);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); paymentId = keys.getLong(1); }
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE BOOKING SET status = 'Confirmed' WHERE bookingID = ? AND status = 'Pending'")) {
                    ps.setLong(1, b.id);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
        redirect(ex, "/confirmation?payment=" + paymentId);
    }

    // ---------- Business logic ----------
    static long months(Booking b) {
        return Math.max(1, Math.round(ChronoUnit.DAYS.between(b.moveIn, b.moveOut) / 30.0));
    }
    static BigDecimal total(Booking b) { return b.acc.rent().multiply(BigDecimal.valueOf(months(b))); }

    // ---------- Pages ----------
    static String listingsPage() throws SQLException {
        StringBuilder cards = new StringBuilder();
        for (Accommodation a : listAccommodations()) {
            cards.append("<div class='card'><div class='photo'>").append(scene(a.photo())).append("</div><div class='body'>")
                 .append(a.verified() ? "<span class='badge'>&#10003; Verified</span>" : "")
                 .append("<h3>").append(esc(a.title())).append("</h3>")
                 .append("<div class='meta'>").append(esc(a.area())).append("</div>")
                 .append("<div class='price'>R").append(money(a.rent())).append("<small> / month</small></div>")
                 .append("<a class='btn-card' href='/book?id=").append(a.id()).append("'>Book now</a></div></div>");
        }
        return page(1, "Find a stay",
            "<h2>Find your stay near campus</h2><p class='help'>Verified listings have had ID and property documents checked by SafeStay admin.</p><div class='grid'>" + cards + "</div>");
    }

    static String bookPage(Accommodation a, String error) {
        return page(2, "Booking details",
            "<h2>Booking details</h2><div class='panel'>"
          + "<div class='strip'><div class='sw'>" + scene(a.photo()) + "</div><div><strong>" + esc(a.title()) + "</strong><small>" + esc(a.area()) + " · R" + money(a.rent()) + "/month</small></div></div>"
          + (error != null ? "<div class='alert'>" + esc(error) + "</div>" : "")
          + "<form method='post' action='/book'><input type='hidden' name='id' value='" + a.id() + "'>"
          + "<div class='row2'><div><label>Move-in date</label><input type='date' name='moveIn' required></div>"
          + "<div><label>Move-out date</label><input type='date' name='moveOut' required></div></div>"
          + "<label>Additional notes (optional)</label><textarea name='notes' placeholder='e.g. Arriving late in the evening'></textarea>"
          + "<div class='actions'><a class='btn btn-ghost' href='/'>&larr; Back</a><button class='btn btn-primary' type='submit'>Continue to payment</button></div></form></div>");
    }

    static String payPage(Booking b, String error) {
        return page(3, "Payment",
            "<h2>Payment</h2><div class='panel'>"
          + (error != null ? "<div class='alert'>" + esc(error) + "</div>" : "")
          + "<form method='post' action='/pay'><input type='hidden' name='booking' value='" + b.id + "'>"
          + "<label>Payment method</label><select name='method' onchange=\"document.getElementById('card').style.display=this.value==='Card'?'block':'none'\">"
          + "<option>Card</option><option>Instant EFT</option><option>Bursary/NSFAS</option></select>"
          + "<div id='card'><label>Name on card</label><input name='cardName' placeholder='e.g. Thandiwe Nkosi'>"
          + "<label>Card number</label><input name='cardNumber' placeholder='4242 4242 4242 4242' maxlength='19'>"
          + "<div class='row2'><div><label>Expiry</label><input name='cardExp' placeholder='MM/YY' maxlength='5'></div>"
          + "<div><label>CVC</label><input name='cardCvc' placeholder='123' maxlength='3'></div></div></div>"
          + "<div class='summary'><div class='line'><span>" + esc(b.acc.title()) + "</span><span>" + b.moveIn + " &rarr; " + b.moveOut + "</span></div>"
          + "<div class='line'><span>Rent</span><span>R" + money(b.acc.rent()) + " &times; " + months(b) + " month(s)</span></div>"
          + "<div class='line total'><span>Amount due today</span><span>R" + money(total(b)) + "</span></div></div>"
          + "<div class='danger-note'>Never pay a landlord directly outside SafeStay &mdash; payments here are held safely until you move in.</div>"
          + "<div class='actions'><a class='btn btn-ghost' href='/book?id=" + b.acc.id() + "'>&larr; Back</a><button class='btn btn-primary' type='submit'>Pay &amp; confirm booking</button></div></form></div>");
    }

    static String confirmationPage(Payment p) {
        return page(4, "Booking confirmed",
            "<div class='panel center'><div class='ok'>&#10003;</div><h2>Booking confirmed</h2>"
          + "<p class='help'>A receipt has been sent to your student email. Your landlord has been notified.</p><div class='receipt'>"
          + row("Booking ID", String.valueOf(p.booking.id)) + row("Accommodation", esc(p.booking.acc.title()))
          + row("Stay period", p.booking.moveIn + " &rarr; " + p.booking.moveOut) + row("Payment method", esc(p.method))
          + row("Amount paid", "R" + money(p.amount)) + row("Transaction ref", p.reference)
          + row("Booking status", p.booking.status) + row("Payment status", "<span class='pill'>" + p.status + "</span>")
          + "</div><div class='actions' style='justify-content:center'><a class='btn btn-primary' href='/'>Book another stay</a></div></div>");
    }

    static String row(String k, String v) { return "<div class='line'><span>" + k + "</span><span>" + v + "</span></div>"; }

    static String page(int step, String title, String body) {
        String[] names = {"Listing", "Details", "Payment", "Done"};
        StringBuilder steps = new StringBuilder();
        for (int i = 1; i <= 4; i++)
            steps.append("<span class='step").append(i == step ? " active" : i < step ? " done" : "").append("'>").append(names[i - 1]).append("</span>");
        return "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'><title>SafeStay — "
             + esc(title) + "</title><style>" + CSS + "</style></head><body><header><a class='logo' href='/'><span class='dot'></span>SafeStay</a><div class='steps'>"
             + steps + "</div></header><main>" + body + "</main></body></html>";
    }

    // ---------- Illustrated property "photos" (inline SVG) ----------
    static String scene(String key) {
        String sky = switch (key) { case "studio" -> "#7FA6E8"; case "residence" -> "#9FC1EE"; case "cottage" -> "#B9D3F0"; default -> "#8FB6EA"; };
        String body = switch (key) {
            case "studio" -> "<rect x='30' y='18' width='40' height='34' fill='#EDEFF3'/><rect x='35' y='28' width='9' height='9' fill='#2C5CD6'/><rect x='46' y='28' width='9' height='9' fill='#2C5CD6'/><rect x='57' y='28' width='9' height='9' fill='#2C5CD6'/><rect x='44' y='41' width='12' height='11' fill='#16305C'/>";
            case "residence" -> "<rect x='8' y='24' width='36' height='28' fill='#F4E7D3'/><polygon points='8,24 26,10 44,24' fill='#B5583F'/><rect x='52' y='16' width='40' height='36' fill='#E7E2D6'/><polygon points='52,16 72,4 92,16' fill='#8C4632'/><rect x='16' y='34' width='7' height='7' fill='#2C5CD6'/><rect x='29' y='34' width='7' height='7' fill='#2C5CD6'/><rect x='60' y='26' width='7' height='7' fill='#2C5CD6'/><rect x='72' y='26' width='7' height='7' fill='#2C5CD6'/><rect x='60' y='38' width='7' height='7' fill='#2C5CD6'/><rect x='72' y='38' width='7' height='7' fill='#2C5CD6'/>";
            case "cottage" -> "<polygon points='20,52 20,28 50,10 80,28 80,52' fill='#EFE3CF'/><polygon points='12,30 50,8 88,30 80,30 50,14 20,30' fill='#7A4E33'/><rect x='42' y='36' width='16' height='16' fill='#5A3A24'/><rect x='28' y='34' width='9' height='9' fill='#2C5CD6'/><rect x='63' y='34' width='9' height='9' fill='#2C5CD6'/><circle cx='10' cy='48' r='8' fill='#5C8A4A'/><circle cx='92' cy='46' r='7' fill='#5C8A4A'/>";
            default -> "<rect x='18' y='14' width='22' height='38' fill='#EDEFF3'/><rect x='44' y='6' width='22' height='46' fill='#DCE2EA'/><rect x='70' y='20' width='18' height='32' fill='#EDEFF3'/><rect x='23' y='20' width='6' height='6' fill='#2C5CD6'/><rect x='31' y='20' width='6' height='6' fill='#2C5CD6'/><rect x='49' y='12' width='6' height='6' fill='#2C5CD6'/><rect x='57' y='12' width='6' height='6' fill='#2C5CD6'/><rect x='49' y='24' width='6' height='6' fill='#2C5CD6'/><rect x='57' y='24' width='6' height='6' fill='#2C5CD6'/><rect y='44' width='100' height='16' fill='#4E86C4'/>";
        };
        String ground = key.equals("riverside") ? "" : "<rect y='52' width='100' height='8' fill='#5C7A4A'/>";
        return "<svg viewBox='0 0 100 60' preserveAspectRatio='xMidYMid slice'><rect width='100' height='60' fill='" + sky + "'/>" + body + ground + "</svg>";
    }

    // ---------- Helpers ----------
    static String money(BigDecimal v) { return String.format("%,d", v.longValue()); }

    static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }

    static Map<String, String> parseForm(String raw) {
        Map<String, String> m = new HashMap<>();
        if (raw == null || raw.isBlank()) return m;
        for (String pair : raw.split("&")) {
            int i = pair.indexOf('=');
            String k = i < 0 ? pair : pair.substring(0, i), v = i < 0 ? "" : pair.substring(i + 1);
            m.put(URLDecoder.decode(k, StandardCharsets.UTF_8), URLDecoder.decode(v, StandardCharsets.UTF_8));
        }
        return m;
    }

    static void send(HttpExchange ex, int code, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (var os = ex.getResponseBody()) { os.write(bytes); }
    }

    static void redirect(HttpExchange ex, String to) throws IOException {
        ex.getResponseHeaders().set("Location", to);
        ex.sendResponseHeaders(303, -1);
        ex.close();
    }

    static final String CSS = """
        :root{--bd:#16305C;--b:#2C5CD6;--bg:#F2F4F8;--w:#fff;--ink:#16202E;--mu:#5C6B7A;--ln:#DCE2EA;--ok:#1E8E5A;--okb:#E6F5EC;--er:#D6403F;--erb:#FCEAEA}
        *{box-sizing:border-box}body{margin:0;background:var(--bg);color:var(--ink);font-family:system-ui,'Segoe UI',Arial,sans-serif;line-height:1.5}
        a{text-decoration:none}h2,h3{margin:0;color:var(--bd)}
        header{position:sticky;top:0;z-index:5;padding:14px 20px;background:var(--w);border-bottom:1px solid var(--ln);display:flex;justify-content:space-between;align-items:center}
        .logo{display:flex;align-items:center;gap:8px;font-weight:700;font-size:1.15rem;color:var(--bd)}.dot{width:10px;height:10px;border-radius:50%;background:var(--ok)}
        .steps{display:flex;gap:6px;font-size:.78rem;color:var(--mu)}.step{padding:4px 10px;border-radius:100px;border:1px solid var(--ln)}
        .step.active{background:var(--b);border-color:var(--b);color:#fff}.step.done{background:var(--okb);border-color:var(--ok);color:var(--ok)}
        main{max-width:760px;margin:0 auto;padding:28px 20px 60px}.help{color:var(--mu);font-size:.82rem}
        .grid{display:grid;grid-template-columns:1fr 1fr;gap:16px;margin-top:18px}@media(max-width:560px){.grid{grid-template-columns:1fr}}
        .card{background:var(--w);border:1px solid var(--ln);border-radius:10px;overflow:hidden;display:flex;flex-direction:column}
        .photo{height:110px}.photo svg,.sw svg{display:block;width:100%;height:100%}
        .body{padding:14px;display:flex;flex-direction:column;gap:6px;flex:1}
        .badge{font-size:.72rem;font-weight:600;color:var(--ok);background:var(--okb);padding:3px 8px;border-radius:100px;width:fit-content}
        .meta{font-size:.82rem;color:var(--mu)}.price{font-weight:700;margin-top:auto;font-size:1.05rem}.price small{font-weight:400;color:var(--mu);font-size:.78rem}
        .btn-card{margin-top:10px;background:var(--b);color:#fff;text-align:center;border-radius:7px;padding:9px 0;font-weight:600;font-size:.88rem}.btn-card:hover{background:var(--bd)}
        .panel{background:var(--w);border:1px solid var(--ln);border-radius:12px;padding:24px;margin-top:16px}.center{text-align:center}
        .strip{display:flex;gap:12px;align-items:center;padding:12px 14px;background:var(--bg);border-radius:9px}
        .sw{width:44px;height:44px;border-radius:8px;overflow:hidden;flex-shrink:0}.strip small{display:block;color:var(--mu);font-size:.78rem}
        label{display:block;font-size:.82rem;font-weight:600;margin:16px 0 6px}
        input,select,textarea{width:100%;padding:10px 12px;border:1px solid var(--ln);border-radius:8px;font-size:.92rem;font-family:inherit;background:var(--w);color:var(--ink)}
        input:focus,select:focus,textarea:focus{outline:2px solid var(--b);outline-offset:1px}textarea{min-height:70px;resize:vertical}
        .row2{display:grid;grid-template-columns:1fr 1fr;gap:14px}@media(max-width:520px){.row2{grid-template-columns:1fr}}
        .summary{background:var(--bg);border-radius:9px;padding:14px 16px;margin-top:20px;font-size:.88rem}
        .line{display:flex;justify-content:space-between;padding:5px 0}.line.total{border-top:1px solid var(--ln);margin-top:6px;padding-top:10px;font-weight:700;color:var(--bd)}
        .alert{background:var(--erb);color:var(--er);border-radius:8px;padding:10px 12px;font-size:.85rem;margin-top:14px}
        .danger-note{background:var(--erb);color:var(--er);border-radius:8px;padding:10px 12px;font-size:.8rem;margin-top:16px}
        .actions{display:flex;justify-content:space-between;gap:10px;margin-top:24px}
        .btn{padding:11px 20px;border-radius:8px;font-weight:600;font-size:.9rem;cursor:pointer;border:1px solid transparent;display:inline-block}
        .btn-primary{background:var(--b);color:#fff}.btn-primary:hover{background:var(--bd)}.btn-ghost{background:transparent;color:var(--mu);border-color:var(--ln)}
        .ok{width:56px;height:56px;border-radius:50%;background:var(--okb);color:var(--ok);font-size:1.8rem;display:flex;align-items:center;justify-content:center;margin:6px auto}
        .receipt{background:var(--bg);border-radius:9px;padding:16px;margin-top:20px;font-size:.86rem;text-align:left}
        .receipt .line{border-bottom:1px dashed var(--ln);padding:6px 0}.receipt .line:last-child{border-bottom:none}.receipt .line span:first-child{color:var(--mu)}
        .pill{font-size:.72rem;font-weight:700;padding:3px 10px;border-radius:100px;background:var(--okb);color:var(--ok)}
        """;
}
