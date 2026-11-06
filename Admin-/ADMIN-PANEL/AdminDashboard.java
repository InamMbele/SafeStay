package za.ac.cput.safestay;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Responsive-style Swing administrator dashboard backed by the SafeStay database. */
public class AdminDashboard extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Color NAVY = new Color(17, 65, 166);
    private static final Color BLUE = new Color(30, 86, 205);
    private static final Color TEXT = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color GREEN = new Color(34, 139, 84);
    private static final Color RED = new Color(210, 65, 75);
    private static final Color ORANGE = new Color(225, 133, 45);

    private JLabel totalListingsValue, verifiedValue, reportsValue, usersValue;
    private JPanel pendingCards, verificationCards, reportCards;
    private JLabel pendingCount, verificationCount, reportCount;

    public AdminDashboard() {
        super("SafeStay | Admin Panel");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 720));
        setSize(1120, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);
        add(buildNavigation(), BorderLayout.SOUTH);
        refreshAllData();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(new EmptyBorder(17, 24, 17, 24));
        JLabel menu = new JLabel("☰"); menu.setForeground(Color.WHITE); menu.setFont(new Font("SansSerif", Font.BOLD, 25));
        JLabel title = new JLabel("Admin Panel", SwingConstants.CENTER);
        title.setForeground(Color.WHITE); title.setFont(new Font("SansSerif", Font.BOLD, 25));
        JLabel bell = new JLabel("♧"); bell.setForeground(Color.WHITE); bell.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.add(menu, BorderLayout.WEST); header.add(title, BorderLayout.CENTER); header.add(bell, BorderLayout.EAST);
        return header;
    }

    private JScrollPane buildMainContent() {
        JPanel content = new JPanel();
        content.setBackground(new Color(248, 250, 253));
        content.setBorder(new EmptyBorder(20, 22, 24, 22));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel statsTitle = sectionTitle("System Statistics");
        content.add(statsTitle); content.add(Box.createVerticalStrut(12));
        JPanel stats = new JPanel(new GridLayout(1, 4, 12, 0)); stats.setOpaque(false);
        totalListingsValue = new JLabel("—", SwingConstants.CENTER);
        verifiedValue = new JLabel("—", SwingConstants.CENTER);
        reportsValue = new JLabel("—", SwingConstants.CENTER);
        usersValue = new JLabel("—", SwingConstants.CENTER);
        stats.add(statCard("⌂", totalListingsValue, "Total Listings", BLUE));
        stats.add(statCard("✓", verifiedValue, "Verified Landlords", GREEN));
        stats.add(statCard("⚑", reportsValue, "Reports", ORANGE));
        stats.add(statCard("♟", usersValue, "Registered Users", new Color(126, 82, 190)));
        content.add(stats); content.add(Box.createVerticalStrut(22));

        JPanel pendingHeader = sectionHeader("Pending Listings", "Refresh", e -> refreshPendingListings());
        pendingCount = new JLabel(" "); pendingCount.setForeground(MUTED); pendingHeader.add(pendingCount);
        content.add(pendingHeader); content.add(Box.createVerticalStrut(8));
        pendingCards = new JPanel(); pendingCards.setOpaque(false); pendingCards.setLayout(new BoxLayout(pendingCards, BoxLayout.Y_AXIS));
        JScrollPane pendingScroll = new JScrollPane(pendingCards);
        pendingScroll.setPreferredSize(new Dimension(900, 270));
        pendingScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 270));
        pendingScroll.setBorder(BorderFactory.createEmptyBorder());
        pendingScroll.getVerticalScrollBar().setUnitIncrement(16);
        pendingScroll.getViewport().setBackground(new Color(248, 250, 253));
        content.add(pendingScroll); content.add(Box.createVerticalStrut(22));

        JPanel verificationHeader = sectionHeader("Pending Landlord Verifications", "Refresh", e -> refreshVerifications());
        verificationCount = new JLabel(" "); verificationCount.setForeground(MUTED); verificationHeader.add(verificationCount);
        content.add(verificationHeader); content.add(Box.createVerticalStrut(8));
        verificationCards = new JPanel(); verificationCards.setOpaque(false); verificationCards.setLayout(new BoxLayout(verificationCards, BoxLayout.Y_AXIS));
        JScrollPane verificationScroll = new JScrollPane(verificationCards);
        verificationScroll.setPreferredSize(new Dimension(900, 220));
        verificationScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        verificationScroll.setBorder(BorderFactory.createEmptyBorder());
        verificationScroll.getVerticalScrollBar().setUnitIncrement(16);
        verificationScroll.getViewport().setBackground(new Color(248, 250, 253));
        content.add(verificationScroll); content.add(Box.createVerticalStrut(22));

        JPanel reportsHeader = sectionHeader("Reported Listings", "Refresh", e -> refreshReports());
        reportCount = new JLabel(" "); reportCount.setForeground(MUTED); reportsHeader.add(reportCount);
        content.add(reportsHeader); content.add(Box.createVerticalStrut(8));
        reportCards = new JPanel(); reportCards.setOpaque(false); reportCards.setLayout(new BoxLayout(reportCards, BoxLayout.Y_AXIS));
        JScrollPane reportsScroll = new JScrollPane(reportCards);
        reportsScroll.setPreferredSize(new Dimension(900, 270));
        reportsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 270));
        reportsScroll.setBorder(BorderFactory.createEmptyBorder());
        reportsScroll.getVerticalScrollBar().setUnitIncrement(16);
        reportsScroll.getViewport().setBackground(new Color(248, 250, 253));
        content.add(reportsScroll); content.add(Box.createVerticalStrut(8));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null); scroll.getViewport().setBackground(new Color(248, 250, 253));
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        return scroll;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text); label.setFont(new Font("SansSerif", Font.BOLD, 19)); label.setForeground(TEXT); return label;
    }

    private JPanel sectionHeader(String title, String buttonText, java.awt.event.ActionListener action) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); row.setOpaque(false);
        JLabel label = sectionTitle(title); row.add(label);
        JButton refresh = new JButton(buttonText); styleButton(refresh, BLUE, Color.WHITE); refresh.addActionListener(action); row.add(refresh);
        return row;
    }

    private JPanel statCard(String icon, JLabel value, String caption, Color iconColor) {
        JPanel card = new JPanel(); card.setBackground(Color.WHITE); card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(13, 8, 13, 8)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel glyph = new JLabel(icon, SwingConstants.CENTER); glyph.setAlignmentX(Component.CENTER_ALIGNMENT);
        glyph.setForeground(iconColor); glyph.setFont(new Font("SansSerif", Font.BOLD, 25));
        value.setAlignmentX(Component.CENTER_ALIGNMENT); value.setForeground(Color.BLACK); value.setFont(new Font("SansSerif", Font.BOLD, 25));
        JLabel name = new JLabel(caption, SwingConstants.CENTER); name.setAlignmentX(Component.CENTER_ALIGNMENT); name.setForeground(MUTED); name.setFont(new Font("SansSerif", Font.PLAIN, 12));
        card.add(glyph); card.add(Box.createVerticalStrut(5)); card.add(value); card.add(Box.createVerticalStrut(4)); card.add(name);
        return card;
    }

    private JPanel buildNavigation() {
        JPanel nav = new JPanel(new GridLayout(1, 5, 4, 0)); nav.setBackground(Color.WHITE);
        nav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        String[] labels = {"⌂\nDashboard", "▣\nListings", "▤\nReports", "♙\nUsers", "⚙\nSettings"};
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            JButton button = new JButton("<html><center>" + labels[i].replace("\n", "<br>") + "</center></html>");
            button.setBorderPainted(false); button.setFocusPainted(false); button.setBackground(Color.WHITE);
            button.setForeground(i == 0 ? BLUE : MUTED); button.setFont(new Font("SansSerif", Font.PLAIN, 12));
            button.addActionListener(e -> {
                if (index != 0) JOptionPane.showMessageDialog(this,
                        "The " + labels[index].substring(labels[index].indexOf('\n') + 1)
                        + " workspace will be connected when the group modules are integrated.",
                        "SafeStay", JOptionPane.INFORMATION_MESSAGE);
            });
            nav.add(button);
        }
        return nav;
    }

    private void refreshAllData() { refreshStatistics(); refreshPendingListings(); refreshVerifications(); refreshReports(); }

    private void refreshStatistics() {
        AdminDAO dao = new AdminDAO();
        totalListingsValue.setText(stat(dao.getTotalListings()));
        verifiedValue.setText(stat(dao.getVerifiedListings()));
        reportsValue.setText(stat(dao.getTotalReports()));
        usersValue.setText(stat(dao.getRegisteredUsers()));
    }
    private String stat(int n) { return n < 0 ? "—" : Integer.toString(n); }

    private void refreshPendingListings() {
        if (pendingCards == null) return;
        pendingCards.removeAll();
        try {
            List<AccommodationListing> all = new AdminDAO().getPendingListings();
            int shown = 0;
            for (AccommodationListing listing : all) {
                pendingCards.add(listingCard(listing));
                pendingCards.add(Box.createVerticalStrut(9));
                shown++;
            }
            pendingCount.setText("(" + shown + ")");
            if (shown == 0) pendingCards.add(emptyMessage("There are no accommodation listings awaiting approval."));
        } catch (Exception ex) {
            showError("Could not load pending accommodation listings", ex);
            pendingCards.add(emptyMessage("Unable to load pending accommodation listings."));
        }
        pendingCards.revalidate(); pendingCards.repaint(); refreshStatistics();
    }

    private JPanel listingCard(AccommodationListing listing) {
        JPanel card = cardPanel(); card.setLayout(new BorderLayout(14, 8));
        card.add(imageLabel(listing.getImagePath(), 112, 88), BorderLayout.WEST);
        JPanel details = new JPanel(); details.setOpaque(false); details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.add(boldLabel(safe(listing.getName()), 15));
        details.add(textLabel(safe(listing.getAddress())));
        details.add(textLabel("Landlord: " + safe(listing.getLandlordName()) + "  •  " + safe(listing.getLandlordEmail())));
        details.add(textLabel(String.format("Rent: R%.2f  •  Rooms: %d  •  Type: %s", listing.getPrice(), listing.getNumberOfRooms(), safe(listing.getRoomType()))));
        details.add(textLabel("Listing #" + listing.getAccommodationID() + "  •  Listed: " + safe(listing.getDateListed())));
        card.add(details, BorderLayout.CENTER);
        JPanel actions = new JPanel(new GridLayout(2, 1, 0, 7)); actions.setOpaque(false);
        JButton approve = new JButton("Approve"); styleButton(approve, GREEN, Color.WHITE);
        JButton reject = new JButton("Reject"); styleOutlineButton(reject, RED);
        approve.addActionListener(e -> updateListing(listing, "Approved"));
        reject.addActionListener(e -> updateListing(listing, "Rejected"));
        actions.add(approve); actions.add(reject); card.add(actions, BorderLayout.EAST);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 132)); return card;
    }

    private void updateListing(AccommodationListing listing, String status) {
        int answer = JOptionPane.showConfirmDialog(this,
                status + " accommodation listing '" + safe(listing.getName()) + "' (Listing #" + listing.getAccommodationID() + ")?",
                "Confirm listing decision", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;
        if (new AdminDAO().updateListingStatus(listing.getAccommodationID(), status)) {
            JOptionPane.showMessageDialog(this, "Accommodation listing " + status.toLowerCase() + ".");
            refreshAllData();
        } else {
            JOptionPane.showMessageDialog(this, "The accommodation listing could not be updated. It may already have been processed.",
                    "Update failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshVerifications() {
        if (verificationCards == null) return;
        verificationCards.removeAll();
        try {
            List<Verification> all = new VerificationDAO().getAllVerifications();
            int shown = 0;
            for (Verification v : all) {
                if (!"Pending".equalsIgnoreCase(v.getStatus())) continue;
                verificationCards.add(verificationCard(v));
                verificationCards.add(Box.createVerticalStrut(9));
                shown++;
            }
            verificationCount.setText("(" + shown + ")");
            if (shown == 0) verificationCards.add(emptyMessage("There are no pending landlord verification requests."));
        } catch (Exception ex) {
            showError("Could not load pending landlord verifications", ex);
            verificationCards.add(emptyMessage("Unable to load landlord verification records."));
        }
        verificationCards.revalidate(); verificationCards.repaint();
    }

    private JPanel verificationCard(Verification v) {
        JPanel card = cardPanel(); card.setLayout(new BorderLayout(14, 8));
        JLabel image = imageLabel(v.getAccommodationImagePath(), 112, 88); card.add(image, BorderLayout.WEST);
        JPanel details = new JPanel(); details.setOpaque(false); details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        String property = blank(v.getAccommodationName()) == null ? "Landlord verification" : v.getAccommodationName();
        details.add(boldLabel(property, 15)); details.add(textLabel(blank(v.getAccommodationAddress()) == null ? v.getLandlordName() : v.getAccommodationAddress()));
        details.add(textLabel("Landlord: " + safe(v.getLandlordName()) + "  •  " + safe(v.getLandlordEmail())));
        details.add(textLabel("Submitted: " + safe(v.getVerificationDate()) + "  •  Verification #" + v.getVerificationID()));
        card.add(details, BorderLayout.CENTER);
        JPanel actions = new JPanel(new GridLayout(2, 1, 0, 7)); actions.setOpaque(false);
        JButton approve = new JButton("Approve"); styleButton(approve, GREEN, Color.WHITE);
        JButton reject = new JButton("Reject"); styleOutlineButton(reject, RED);
        approve.addActionListener(e -> updateVerification(v, "Approved")); reject.addActionListener(e -> updateVerification(v, "Rejected"));
        actions.add(approve); actions.add(reject); card.add(actions, BorderLayout.EAST);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118)); return card;
    }

    private void updateVerification(Verification v, String status) {
        int answer = JOptionPane.showConfirmDialog(this, "" + status + " verification for " + safe(v.getLandlordName()) + "?", "Confirm action", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;
        if (new VerificationDAO().updateStatus(v.getVerificationID(), status)) { JOptionPane.showMessageDialog(this, "Verification " + status.toLowerCase() + "."); refreshAllData(); }
        else JOptionPane.showMessageDialog(this, "The verification could not be updated.", "Update failed", JOptionPane.ERROR_MESSAGE);
    }

    private void refreshReports() {
        if (reportCards == null) return;
        reportCards.removeAll();
        try {
            List<Report> all = new ReportDAO().getAllReports(); reportCount.setText("(" + all.size() + ")");
            for (Report r : all) { reportCards.add(reportCard(r)); reportCards.add(Box.createVerticalStrut(9)); }
            if (all.isEmpty()) reportCards.add(emptyMessage("There are no reports to display."));
        } catch (Exception ex) { showError("Could not load reports", ex); reportCards.add(emptyMessage("Unable to load report records.")); }
        reportCards.revalidate(); reportCards.repaint(); refreshStatistics();
    }

    private JPanel reportCard(Report r) {
        JPanel card = cardPanel(); card.setLayout(new BorderLayout(14, 8));
        card.add(imageLabel(r.getAccommodationImagePath(), 112, 88), BorderLayout.WEST);
        JPanel details = new JPanel(); details.setOpaque(false); details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.add(boldLabel(safe(r.getAccommodationName()), 15));
        details.add(textLabel("Reported: " + safe(r.getReportDate()) + "  •  Report #" + r.getReportID()));
        details.add(textLabel("Reason: " + safe(r.getReason())));
        details.add(textLabel("Student: " + safe(r.getStudentName()) + "  •  Status: " + safe(r.getStatus())));
        card.add(details, BorderLayout.CENTER);
        JPanel actions = new JPanel(new GridLayout(1, 3, 6, 0)); actions.setOpaque(false);
        JButton view = new JButton("View"); styleOutlineButton(view, BLUE);
        JButton remove = new JButton("Remove"); styleOutlineButton(remove, RED);
        JButton flag = new JButton("Flag"); styleOutlineButton(flag, ORANGE);
        view.addActionListener(e -> viewReport(r)); remove.addActionListener(e -> removeReport(r)); flag.addActionListener(e -> flagReport(r));
        actions.add(view); actions.add(remove); actions.add(flag); card.add(actions, BorderLayout.EAST);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118)); return card;
    }

    private void viewReport(Report r) {
        String details = "Report ID: " + r.getReportID() + "\nAccommodation: " + safe(r.getAccommodationName())
                + "\nAddress: " + safe(r.getAccommodationAddress()) + "\nReason: " + safe(r.getReason())
                + "\nDescription: " + safe(r.getDescription()) + "\nStudent: " + safe(r.getStudentName())
                + "\nStudent email: " + safe(r.getStudentEmail()) + "\nReported: " + safe(r.getReportDate())
                + "\nStatus: " + safe(r.getStatus()) + "\nEvidence: " + safe(r.getEvidence());
        JTextArea area = new JTextArea(details, 14, 48); area.setEditable(false); area.setLineWrap(true); area.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Report details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void flagReport(Report r) {
        if (new ReportDAO().flagReport(r.getReportID())) { JOptionPane.showMessageDialog(this, "Report flagged."); refreshAllData(); }
        else JOptionPane.showMessageDialog(this, "The report could not be flagged.", "Action failed", JOptionPane.ERROR_MESSAGE);
    }

    private void removeReport(Report r) {
        int answer = JOptionPane.showConfirmDialog(this, "Permanently remove report #" + r.getReportID() + "?", "Confirm removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;
        if (new ReportDAO().deleteReport(r.getReportID())) { JOptionPane.showMessageDialog(this, "Report removed."); refreshAllData(); }
        else JOptionPane.showMessageDialog(this, "The report could not be removed.", "Removal failed", JOptionPane.ERROR_MESSAGE);
    }

    private JPanel cardPanel() {
        JPanel p = new JPanel(); p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER), new EmptyBorder(10, 11, 10, 11)));
        return p;
    }
    private JPanel emptyMessage(String message) {
        JPanel p = cardPanel(); p.add(textLabel(message)); p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55)); return p;
    }
    private JLabel boldLabel(String text, int size) { JLabel l = new JLabel(text); l.setFont(new Font("SansSerif", Font.BOLD, size)); l.setForeground(TEXT); return l; }
    private JLabel textLabel(String text) { JLabel l = new JLabel(text); l.setFont(new Font("SansSerif", Font.PLAIN, 12)); l.setForeground(MUTED); l.setBorder(new EmptyBorder(3, 0, 0, 0)); return l; }
    private void styleButton(JButton b, Color bg, Color fg) { b.setBackground(bg); b.setForeground(fg); b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false); b.setFont(new Font("SansSerif", Font.BOLD, 12)); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
    private void styleOutlineButton(JButton b, Color fg) { b.setBackground(Color.WHITE); b.setForeground(fg); b.setOpaque(true); b.setBorder(BorderFactory.createLineBorder(fg, 1, true)); b.setFocusPainted(false); b.setFont(new Font("SansSerif", Font.BOLD, 11)); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
    private String blank(String s) { return s == null || s.isBlank() ? null : s; }
    private String safe(String s) { return s == null || s.isBlank() ? "Not provided" : s; }

    private JLabel imageLabel(String path, int width, int height) {
        JLabel label = new JLabel(); label.setPreferredSize(new Dimension(width, height)); label.setMinimumSize(new Dimension(width, height));
        label.setHorizontalAlignment(SwingConstants.CENTER); label.setOpaque(true); label.setBackground(new Color(235, 241, 249));
        label.setBorder(BorderFactory.createLineBorder(BORDER)); label.setText("⌂"); label.setFont(new Font("SansSerif", Font.BOLD, 34)); label.setForeground(new Color(135, 153, 177));
        if (path == null || path.isBlank()) return label;
        try {
            File file = new File(path);
            if (!file.isAbsolute()) file = new File(System.getProperty("user.dir"), path);
            BufferedImage img = file.isFile() ? ImageIO.read(file) : null;
            if (img == null) {
                String resourcePath = path.replace('\\', '/');
                URL resource = getClass().getClassLoader().getResource(resourcePath);
                if (resource == null && !resourcePath.startsWith("images/")) {
                    resource = getClass().getClassLoader().getResource("images/" + resourcePath);
                }
                if (resource != null) img = ImageIO.read(resource);
            }
            if (img != null) { Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH); label.setIcon(new ImageIcon(scaled)); label.setText(""); }
        } catch (Exception ignored) { /* Keep the neutral placeholder when a path is unavailable. */ }
        return label;
    }

    private void showError(String message, Exception ex) {
        ex.printStackTrace(); JOptionPane.showMessageDialog(this, message + ".\n" + ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }
            new AdminDashboard().setVisible(true);
        });
    }
}
