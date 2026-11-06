package safestay.ui;

import safestay.dao.AccommodationDAO;
import safestay.dao.LandlordDAO;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class AddAccommodationForm extends JFrame {
    private JTextField txtLandlordId, txtName, txtAddress, txtPrice, txtRoomType, txtRooms, txtDistance, txtMoveIn, txtImage;
    private JTextArea txtRules;

    public AddAccommodationForm() {
        setTitle("SafeStay - Add Accommodation");
        setSize(600, 590);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel header = new JPanel(); header.setBackground(new Color(30, 58, 138));
        JLabel label = new JLabel("ADD ACCOMMODATION - LINKED TO LANDLORD");
        label.setForeground(Color.WHITE); header.add(label); add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        form.add(new JLabel("Landlord ID (existing):")); txtLandlordId = new JTextField(); form.add(txtLandlordId);
        form.add(new JLabel("Property name:")); txtName = new JTextField(); form.add(txtName);
        form.add(new JLabel("Address:")); txtAddress = new JTextField(); form.add(txtAddress);
        form.add(new JLabel("Price (R):")); txtPrice = new JTextField(); form.add(txtPrice);
        form.add(new JLabel("Number of rooms:")); txtRooms = new JTextField("1"); form.add(txtRooms);
        form.add(new JLabel("Room type:")); txtRoomType = new JTextField("Single"); form.add(txtRoomType);
        form.add(new JLabel("Distance to campus (km):")); txtDistance = new JTextField("0"); form.add(txtDistance);
        form.add(new JLabel("Move-in date (yyyy-mm-dd, optional):")); txtMoveIn = new JTextField(); form.add(txtMoveIn);
        form.add(new JLabel("Image path (optional):")); txtImage = new JTextField(); form.add(txtImage);
        form.add(new JLabel("Property rules:")); txtRules = new JTextArea(3, 20); form.add(new JScrollPane(txtRules));
        add(new JScrollPane(form), BorderLayout.CENTER);

        JButton addButton = new JButton("Save Accommodation");
        addButton.addActionListener(e -> saveAccommodation());
        JPanel bottom = new JPanel(); bottom.add(addButton); add(bottom, BorderLayout.SOUTH);
    }

    private void saveAccommodation() {
        try {
            String landlordText = txtLandlordId.getText().trim();
            String name = txtName.getText().trim();
            String address = txtAddress.getText().trim();
            String priceText = txtPrice.getText().trim();
            String roomsText = txtRooms.getText().trim();
            String roomType = txtRoomType.getText().trim();
            String distanceText = txtDistance.getText().trim();
            if (landlordText.isEmpty() || name.isEmpty() || address.isEmpty() || priceText.isEmpty()
                    || roomsText.isEmpty() || roomType.isEmpty() || distanceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete all required fields.", "Missing information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int landlordId = Integer.parseInt(landlordText);
            double price = Double.parseDouble(priceText);
            int rooms = Integer.parseInt(roomsText);
            double distance = Double.parseDouble(distanceText);
            if (landlordId <= 0 || price < 0 || rooms <= 0 || distance < 0) {
                JOptionPane.showMessageDialog(this, "Landlord ID and room count must be positive. Price and distance cannot be negative.");
                return;
            }
            if (new LandlordDAO().getLandlordById(landlordId) == null) {
                JOptionPane.showMessageDialog(this, "No landlord exists with ID " + landlordId + ". Register or choose an existing landlord first.");
                return;
            }
            java.sql.Date moveIn = null;
            if (!txtMoveIn.getText().trim().isEmpty()) moveIn = java.sql.Date.valueOf(txtMoveIn.getText().trim());
            String image = txtImage.getText().trim();
            if (image.isEmpty()) image = null;
            boolean saved = new AccommodationDAO().addAccommodation(landlordId, name, address, price, rooms,
                    roomType, distance, moveIn, txtRules.getText().trim(), image);
            if (saved) {
                JOptionPane.showMessageDialog(this, "Accommodation saved successfully. Status: Available.");
                txtName.setText(""); txtAddress.setText(""); txtPrice.setText(""); txtRooms.setText("1");
                txtRoomType.setText("Single"); txtDistance.setText("0"); txtMoveIn.setText(""); txtImage.setText(""); txtRules.setText("");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Landlord ID and room count must be whole numbers. Price and distance must be valid numbers.", "Invalid number", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Enter the move-in date as yyyy-mm-dd (for example, 2026-12-01).", "Invalid date", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Could not save", JOptionPane.ERROR_MESSAGE);
        }
    }
}
