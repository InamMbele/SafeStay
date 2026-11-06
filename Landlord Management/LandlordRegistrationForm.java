package safestay.ui;

import safestay.dao.LandlordDAO;
import safestay.model.Landlord;
import safestay.util.PasswordUtil;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LandlordRegistrationForm extends JFrame {
    private JTextField txtFirstName, txtLastName, txtEmail, txtPhone;
    private JPasswordField txtPass;

    public LandlordRegistrationForm() {
        setTitle("SafeStay - Landlord Registration");
        setSize(520, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel header = new JPanel();
        header.setBackground(new Color(30, 58, 138));
        JLabel lbl = new JLabel("SAFESTAY - LANDLORD REGISTRATION");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(lbl);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        form.setBackground(Color.WHITE);
        form.add(new JLabel("First Name:")); txtFirstName = new JTextField(); form.add(txtFirstName);
        form.add(new JLabel("Last Name:")); txtLastName = new JTextField(); form.add(txtLastName);
        form.add(new JLabel("Email:")); txtEmail = new JTextField(); form.add(txtEmail);
        form.add(new JLabel("Password:")); txtPass = new JPasswordField(); form.add(txtPass);
        form.add(new JLabel("Phone Number:")); txtPhone = new JTextField(); form.add(txtPhone);
        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(2, 2, 8, 8));
        buttons.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JButton register = new JButton("Register Landlord");
        register.setBackground(new Color(30, 58, 138));
        register.setForeground(Color.WHITE);
        register.addActionListener(e -> registerLandlord());
        JButton view = new JButton("View / Update / Delete Landlords");
        view.addActionListener(e -> new LandlordDashboard().setVisible(true));
        JButton addAccommodation = new JButton("Add Accommodation");
        addAccommodation.addActionListener(e -> new AddAccommodationForm().setVisible(true));
        JButton manageAccommodation = new JButton("Manage Accommodation Listings");
        manageAccommodation.addActionListener(e -> new AccommodationManagementForm().setVisible(true));
        buttons.add(register); buttons.add(view); buttons.add(addAccommodation); buttons.add(manageAccommodation);
        add(buttons, BorderLayout.SOUTH);
    }

    private void registerLandlord() {
        String first = txtFirstName.getText().trim();
        String last = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        char[] password = txtPass.getPassword();
        if (first.isEmpty() || last.isEmpty() || email.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(this, "First name, last name, email and password are required.", "Missing information", JOptionPane.WARNING_MESSAGE);
            java.util.Arrays.fill(password, '\0');
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Enter a valid email address.", "Invalid email", JOptionPane.WARNING_MESSAGE);
            java.util.Arrays.fill(password, '\0');
            return;
        }
        if (password.length < 8) {
            JOptionPane.showMessageDialog(this, "Password must contain at least 8 characters.", "Weak password", JOptionPane.WARNING_MESSAGE);
            java.util.Arrays.fill(password, '\0');
            return;
        }
        try {
            String passwordHash = PasswordUtil.hash(password);
            Landlord landlord = new Landlord(first, last, email, passwordHash, phone);
            if (new LandlordDAO().insertLandlord(landlord)) {
                JOptionPane.showMessageDialog(this, "Landlord registered successfully. The password was stored as a secure hash.");
                txtFirstName.setText(""); txtLastName.setText(""); txtEmail.setText(""); txtPass.setText(""); txtPhone.setText("");
            }
        } catch (SQLException ex) {
            String message = ex.getErrorCode() == 1062 ? "That email address is already registered." : "Could not save landlord: " + ex.getMessage();
            JOptionPane.showMessageDialog(this, message, "Database error", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Could not secure the password: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            java.util.Arrays.fill(password, '\0');
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LandlordRegistrationForm().setVisible(true));
    }
}
