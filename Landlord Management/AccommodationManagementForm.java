package safestay.ui;

import safestay.dao.AccommodationDAO;
import safestay.dao.LandlordDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/** View, update the price of, and delete a landlord's accommodation listings. */
public class AccommodationManagementForm extends JFrame {
    private final JTextField landlordIdField = new JTextField(8);
    private final JTextField newPriceField = new JTextField(10);
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Accommodation ID", "Name", "Address", "Price (R)", "Rooms", "Room Type", "Status", "Date Listed"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public AccommodationManagementForm() {
        setTitle("SafeStay - Manage Accommodation Listings");
        setSize(1050, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Landlord ID:"));
        top.add(landlordIdField);
        JButton load = new JButton("Load Listings");
        load.addActionListener(e -> loadListings());
        top.add(load);
        add(top, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("New price (R) for selected listing:"));
        bottom.add(newPriceField);
        JButton update = new JButton("Update Selected Price");
        update.addActionListener(e -> updateSelectedPrice());
        JButton delete = new JButton("Delete Selected Listing");
        delete.addActionListener(e -> deleteSelected());
        bottom.add(update);
        bottom.add(delete);
        add(bottom, BorderLayout.SOUTH);
    }

    private Integer getLandlordId() {
        try {
            int id = Integer.parseInt(landlordIdField.getText().trim());
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid positive landlord ID.");
            return null;
        }
    }

    private void loadListings() {
        Integer landlordId = getLandlordId();
        if (landlordId == null) return;
        try {
            if (new LandlordDAO().getLandlordById(landlordId) == null) {
                JOptionPane.showMessageDialog(this, "No landlord exists with ID " + landlordId + ".");
                tableModel.setRowCount(0);
                return;
            }
            List<Object[]> rows = new AccommodationDAO().getAccommodationsByLandlord(landlordId);
            tableModel.setRowCount(0);
            for (Object[] row : rows) tableModel.addRow(row);
            if (rows.isEmpty()) JOptionPane.showMessageDialog(this, "No accommodation listings found for this landlord.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not load listings: " + ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelectedPrice() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Select an accommodation row first.");
            return;
        }
        try {
            int accommodationId = ((Number) tableModel.getValueAt(selected, 0)).intValue();
            BigDecimal parsed = new BigDecimal(newPriceField.getText().trim());
            if (parsed.signum() < 0) throw new NumberFormatException();
            boolean updated = new AccommodationDAO().updatePrice(accommodationId, parsed.doubleValue());
            if (updated) {
                JOptionPane.showMessageDialog(this, "Accommodation price updated.");
                loadListings();
            } else JOptionPane.showMessageDialog(this, "The accommodation listing was not found.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid non-negative price.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not update price: " + ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Select an accommodation row first.");
            return;
        }
        int accommodationId = ((Number) tableModel.getValueAt(selected, 0)).intValue();
        String name = String.valueOf(tableModel.getValueAt(selected, 1));
        int answer = JOptionPane.showConfirmDialog(this,
                "Delete '" + name + "' and its linked bookings, payments, reviews, reports and saved-listing records? This cannot be undone.",
                "Confirm listing deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;
        try {
            if (new AccommodationDAO().deleteAccommodation(accommodationId)) {
                JOptionPane.showMessageDialog(this, "Accommodation listing and linked records deleted.");
                loadListings();
            } else JOptionPane.showMessageDialog(this, "The accommodation listing was not found.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not delete listing: " + ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
