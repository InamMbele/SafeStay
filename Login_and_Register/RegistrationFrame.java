/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package safestayapp;

/**
 *
 * @author Admin
 */
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class RegistrationFrame extends JFrame {

    private JLabel lblTitle;
    private JLabel lblFirstName;
    private JLabel lblLastName;
    private JLabel lblEmail;
    private JLabel lblPassword;
    private JLabel lblPhoneNumber;
    private JLabel lblInstitution;
    private JLabel lblCourse;
    private JLabel lblBudgetRange;
    private JLabel lblPreferredArea;

    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JTextField txtPhoneNumber;
    private JTextField txtInstitution;
    private JTextField txtCourse;
    private JTextField txtBudgetRange;
    private JTextField txtPreferredArea;

    private JButton btnRegister;
    private JButton btnBack;

    public RegistrationFrame() {

        setTitle("SafeStay - Student Registration");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Title
        lblTitle = new JLabel("SafeStay - Student Registration");

        // Labels
        lblFirstName = new JLabel("First Name:");
        lblLastName = new JLabel("Last Name:");
        lblEmail = new JLabel("Email:");
        lblPassword = new JLabel("Password:");
        lblPhoneNumber = new JLabel("Phone Number:");
        lblInstitution = new JLabel("Institution:");
        lblCourse = new JLabel("Course:");
        lblBudgetRange = new JLabel("Budget Range:");
        lblPreferredArea = new JLabel("Preferred Area:");

        // Text fields
        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtEmail = new JTextField();
        txtPassword = new JPasswordField();
        txtPhoneNumber = new JTextField();
        txtInstitution = new JTextField();
        txtCourse = new JTextField();
        txtBudgetRange = new JTextField();
        txtPreferredArea = new JTextField();

        // Buttons
        btnRegister = new JButton("Register");
        btnBack = new JButton("Back");

        // Title panel
        JPanel topPanel = new JPanel();
        topPanel.add(lblTitle);

        // Layout   
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(9, 2, 10, 10));

        formPanel.add(lblFirstName);
        formPanel.add(txtFirstName);

        formPanel.add(lblLastName);
        formPanel.add(txtLastName);

        formPanel.add(lblEmail);
        formPanel.add(txtEmail);

        formPanel.add(lblPassword);
        formPanel.add(txtPassword);

        formPanel.add(lblPhoneNumber);
        formPanel.add(txtPhoneNumber);

        formPanel.add(lblInstitution);
        formPanel.add(txtInstitution);

        formPanel.add(lblCourse);
        formPanel.add(txtCourse);

        formPanel.add(lblBudgetRange);
        formPanel.add(txtBudgetRange);

        formPanel.add(lblPreferredArea);
        formPanel.add(txtPreferredArea);

        // Button panel
        JPanel buttonPanel = new JPanel();

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnBack);

        // Add panels to JFrame
        add(topPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Register button
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        // Back button
        btnBack.addActionListener(e -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
            dispose();
        });
    }

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {

        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String phoneNumber = txtPhoneNumber.getText().trim();
        String institution = txtInstitution.getText().trim();
        String course = txtCourse.getText().trim();
        String budgetRange = txtBudgetRange.getText().trim();
        String preferredArea = txtPreferredArea.getText().trim();

        // Check required fields
        if (firstName.isEmpty()
                || lastName.isEmpty()
                || email.isEmpty()
                || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all required fields."
            );

            return;
        }

        // Create Student object
        Student student = new Student(
                0,
                firstName,
                lastName,
                email,
                phoneNumber,
                password,
                institution,
                course,
                budgetRange,
                preferredArea,
                LocalDate.now()
        );

        // Register student in database
        StudentDAO studentDAO = new StudentDAO();

        boolean success = studentDAO.registerStudent(student);

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Registration successful!"
            );

            // Clear the fields
            txtFirstName.setText("");
            txtLastName.setText("");
            txtEmail.setText("");
            txtPassword.setText("");
            txtPhoneNumber.setText("");
            txtInstitution.setText("");
            txtCourse.setText("");
            txtBudgetRange.setText("");
            txtPreferredArea.setText("");

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Registration failed. Please check your details."
            );
        }
    }

    public static void main(String[] args) {

        RegistrationFrame frame = new RegistrationFrame();

        frame.setVisible(true);
    }
}
