package safestay.ui;

import safestay.BusinessException;
import safestay.dao.AccommodationDAO;
import safestay.dao.BookingDAO;
import safestay.dao.PaymentDAO;
import safestay.model.Accommodation;
import safestay.model.Booking;
import safestay.model.Payment;
import safestay.model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Student booking section: browse listings, book, pay, and manage own bookings. */
public class MainFrame extends JFrame {
    private final Student student;
    private final AccommodationDAO accommodationDao = new AccommodationDAO();
    private final BookingDAO bookingDao = new BookingDAO();
    private final PaymentDAO paymentDao = new PaymentDAO();

    private List<Accommodation> accommodations = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();

    private final DefaultTableModel accModel = readOnlyModel("ID", "Name", "Address", "Room type", "Distance (km)", "Price / month", "Verified");
    private final DefaultTableModel bookModel = readOnlyModel("Booking", "Accommodation", "Move-in", "Move-out", "Months", "Total", "Status", "Payment");
    private final JTable accTable = new JTable(accModel);
    private final JTable bookTable = new JTable(bookModel);

    public MainFrame(Student student) {
        super("SafeStay - " + student.fullName());
        this.student = student;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Find accommodation", buildListingsTab());
        tabs.addTab("My bookings", buildBookingsTab());
        add(tabs);

        setSize(960, 520);
        setLocationRelativeTo(null);
        reloadAccommodations();
        reloadBookings();
    }

    // ---------- Tabs ----------
    private JPanel buildListingsTab() {
        accTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accTable.setRowHeight(24);
        accTable.setAutoCreateRowSorter(true);

        JButton book = new JButton("Book selected");
        JButton refresh = new JButton("Refresh");
        book.addActionListener(e -> onBook());
        refresh.addActionListener(e -> reloadAccommodations());
        return tabPanel(accTable, "Only verified landlords show \"Yes\" under Verified.", book, refresh);
    }

    private JPanel buildBookingsTab() {
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setRowHeight(24);

        JButton pay = new JButton("Pay");
        JButton change = new JButton("Change dates");
        JButton cancel = new JButton("Cancel / delete");
        JButton refresh = new JButton("Refresh");
        pay.addActionListener(e -> onPay());
        change.addActionListener(e -> onChangeDates());
        cancel.addActionListener(e -> onCancelOrDelete());
        refresh.addActionListener(e -> reloadBookings());
        return tabPanel(bookTable, "Pending bookings can be paid, changed or deleted. Paid (Confirmed) bookings can be cancelled.",
                pay, change, cancel, refresh);
    }

    private JPanel tabPanel(JTable table, String hint, JButton... buttons) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel(hint), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JButton b : buttons) bar.add(b);
        panel.add(bar, BorderLayout.SOUTH);
        return panel;
    }

    // ---------- Loading data ----------
    private void reloadAccommodations() {
        try {
            accommodations = accommodationDao.listAvailable();
            accModel.setRowCount(0);
            for (Accommodation a : accommodations) {
                accModel.addRow(new Object[]{a.id(), a.name(), a.address(), nvl(a.roomType()),
                        a.distanceToCampus() == null ? "" : a.distanceToCampus().stripTrailingZeros().toPlainString(),
                        money(a.price()), a.verified() ? "Yes" : "No"});
            }
        } catch (SQLException e) {
            dbError(e);
        }
    }

    private void reloadBookings() {
        try {
            bookings = bookingDao.listForStudent(student.id());
            bookModel.setRowCount(0);
            for (Booking b : bookings) {
                bookModel.addRow(new Object[]{"#" + b.id(), b.accommodationName(), b.startDate(), b.endDate(),
                        b.months(), money(b.total()), b.status(), b.hasPayment() ? b.paymentStatus() : "Unpaid"});
            }
        } catch (SQLException e) {
            dbError(e);
        }
    }

    // ---------- Button actions ----------
    private void onBook() {
        int row = accTable.getSelectedRow();
        if (row < 0) { warn("Select an accommodation first."); return; }
        Accommodation a = accommodations.get(accTable.convertRowIndexToModel(row));

        LocalDate[] dates = askDates("Book " + a.name(), LocalDate.now().plusDays(7), LocalDate.now().plusDays(7).plusMonths(6));
        if (dates == null) return;
        try {
            long id = bookingDao.create(student.id(), a.id(), dates[0], dates[1]);
            reloadBookings();
            int pay = JOptionPane.showConfirmDialog(this, "Booking #" + id + " saved as Pending.\nPay now?",
                    "Booking saved", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (pay == JOptionPane.YES_OPTION) payBooking(findBooking(id));
        } catch (BusinessException e) {
            warn(e.getMessage());
        } catch (SQLException e) {
            dbError(e);
        }
    }

    private void onPay() {
        Booking b = selectedBooking();
        if (b != null) payBooking(b);
    }

    private void payBooking(Booking b) {
        if (b == null) return;
        if (!b.isPending()) { warn("Only Pending bookings can be paid. This one is " + b.status() + "."); return; }

        JComboBox<String> method = new JComboBox<>(new String[]{"Card", "Instant EFT", "Bursary/NSFAS"});
        JPasswordField card = new JPasswordField(16);
        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel("Accommodation:")); p.add(new JLabel(b.accommodationName()));
        p.add(new JLabel("Stay:")); p.add(new JLabel(b.startDate() + " to " + b.endDate() + " (" + b.months() + " month(s))"));
        p.add(new JLabel("Amount due:")); p.add(new JLabel(money(b.total())));
        p.add(new JLabel("Payment method:")); p.add(method);
        p.add(new JLabel("Card number (card only):")); p.add(card);
        method.addActionListener(e -> card.setEnabled("Card".equals(method.getSelectedItem())));

        while (true) {
            if (JOptionPane.showConfirmDialog(this, p, "Pay for booking #" + b.id(),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
            String chosen = (String) method.getSelectedItem();
            if ("Card".equals(chosen)) {
                String digits = new String(card.getPassword()).replaceAll("\\s", "");
                if (digits.isEmpty()) { warn("Enter your card number."); continue; }
                if (!digits.matches("\\d{16}")) { warn("The card number must be 16 digits."); continue; }
            }
            // Card details are validated here but never stored. A real gateway (PayFast, Yoco...) would be called here.
            try {
                Payment pay = paymentDao.payAndConfirm(b.id(), chosen);
                reloadBookings();
                JOptionPane.showMessageDialog(this,
                        "Payment successful!\n\nReference: " + pay.reference() + "\nBooking: #" + b.id()
                        + "\nAccommodation: " + b.accommodationName() + "\nAmount: " + money(pay.amount())
                        + "\nMethod: " + pay.paymentMethod() + "\nDate: " + pay.paymentDate(),
                        "Booking confirmed", JOptionPane.INFORMATION_MESSAGE);
            } catch (BusinessException e) {
                warn(e.getMessage());
                reloadBookings();
            } catch (SQLException e) {
                dbError(e);
            }
            return;
        }
    }

    private void onChangeDates() {
        Booking b = selectedBooking();
        if (b == null) return;
        if (!b.isPending()) { warn("Only Pending bookings can be changed. This one is " + b.status() + "."); return; }
        LocalDate[] dates = askDates("Change dates for booking #" + b.id(), b.startDate(), b.endDate());
        if (dates == null) return;
        try {
            bookingDao.updateDates(b, dates[0], dates[1]);
            reloadBookings();
            JOptionPane.showMessageDialog(this, "Dates updated.", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (BusinessException e) {
            warn(e.getMessage());
        } catch (SQLException e) {
            dbError(e);
        }
    }

    private void onCancelOrDelete() {
        Booking b = selectedBooking();
        if (b == null) return;
        try {
            if (b.isPending() && !b.hasPayment()) {
                if (confirm("Delete booking #" + b.id() + "? This cannot be undone.")) {
                    bookingDao.deletePending(b.id());
                    reloadBookings();
                }
            } else if ("Confirmed".equals(b.status())) {
                if (confirm("Cancel the paid booking #" + b.id() + "? Any refund must be arranged separately.")) {
                    bookingDao.cancelConfirmed(b.id());
                    reloadBookings();
                }
            } else {
                warn("A " + b.status() + " booking cannot be cancelled or deleted.");
            }
        } catch (BusinessException e) {
            warn(e.getMessage());
            reloadBookings();
        } catch (SQLException e) {
            dbError(e);
        }
    }

    // ---------- Helpers ----------
    private Booking selectedBooking() {
        int row = bookTable.getSelectedRow();
        if (row < 0) { warn("Select a booking first."); return null; }
        return bookings.get(bookTable.convertRowIndexToModel(row));
    }

    private Booking findBooking(long id) {
        for (Booking b : bookings) if (b.id() == id) return b;
        return null;
    }

    /** Asks for move-in/move-out dates, validating until correct. Returns null if cancelled. */
    private LocalDate[] askDates(String title, LocalDate defaultStart, LocalDate defaultEnd) {
        JTextField start = new JTextField(defaultStart.toString(), 10);
        JTextField end = new JTextField(defaultEnd.toString(), 10);
        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel("Move-in (yyyy-mm-dd):")); p.add(start);
        p.add(new JLabel("Move-out (yyyy-mm-dd):")); p.add(end);
        while (true) {
            if (JOptionPane.showConfirmDialog(this, p, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)
                    != JOptionPane.OK_OPTION) return null;
            String s = start.getText().trim(), e = end.getText().trim();
            if (s.isEmpty() || e.isEmpty()) { warn("Both dates are required."); continue; }
            try {
                LocalDate in = LocalDate.parse(s), out = LocalDate.parse(e);
                if (in.isBefore(LocalDate.now())) { warn("Move-in cannot be in the past."); continue; }
                if (!out.isAfter(in)) { warn("Move-out must be after move-in."); continue; }
                return new LocalDate[]{in, out};
            } catch (DateTimeParseException ex) {
                warn("Use the format yyyy-mm-dd, for example 2027-02-01.");
            }
        }
    }

    private boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Please confirm", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Please check", JOptionPane.WARNING_MESSAGE);
    }

    private void dbError(SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "A database error occurred:\n" + e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
    }

    private static String money(BigDecimal v) { return v == null ? "" : String.format(Locale.US, "R%,.2f", v); }
    private static String nvl(String s) { return s == null ? "" : s; }

    private static DefaultTableModel readOnlyModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }
}
