/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package safestayapp;

/**
 *
 * @author Admin
 */
import java.awt.GridLayout;
import javax.swing.*;

public class EditProfileFrame extends JFrame {

    private Student student;

    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JTextField txtPhoneNumber;
    private JTextField txtInstitution;
    private JTextField txtCourse;
    private JTextField txtBudgetRange;
    private JTextField txtPreferredArea;

    private JButton btnSave;
    private JButton btnCancel;

    public EditProfileFrame(Student student) {

        this.student = student;

        setTitle("SafeStay - Edit Profile");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(
                new GridLayout(10, 2, 10, 10)
        );

        panel.add(new JLabel("First Name:"));
        txtFirstName = new JTextField(student.getFirstName());
        panel.add(txtFirstName);

        panel.add(new JLabel("Last Name:"));
        txtLastName = new JTextField(student.getLastName());
        panel.add(txtLastName);

        panel.add(new JLabel("Email:"));
        txtEmail = new JTextField(student.getEmail());
        panel.add(txtEmail);

        panel.add(new JLabel("Phone Number:"));
        txtPhoneNumber = new JTextField(student.getPhoneNumber());
        panel.add(txtPhoneNumber);

        panel.add(new JLabel("Institution:"));
        txtInstitution = new JTextField(student.getInstitution());
        panel.add(txtInstitution);

        panel.add(new JLabel("Course:"));
        txtCourse = new JTextField(student.getCourse());
        panel.add(txtCourse);

        panel.add(new JLabel("Budget Range:"));
        txtBudgetRange = new JTextField(student.getBudgetRange());
        panel.add(txtBudgetRange);

        panel.add(new JLabel("Preferred Area:"));
        txtPreferredArea = new JTextField(student.getPreferredArea());
        panel.add(txtPreferredArea);

        btnSave = new JButton("Save Changes");
        btnCancel = new JButton("Cancel");

        panel.add(btnSave);
        panel.add(btnCancel);

        add(panel);

        // SAVE BUTTON
        btnSave.addActionListener(e -> {

            student.setFirstName(txtFirstName.getText().trim());
            student.setLastName(txtLastName.getText().trim());
            student.setEmail(txtEmail.getText().trim());
            student.setPhoneNumber(txtPhoneNumber.getText().trim());
            student.setInstitution(txtInstitution.getText().trim());
            student.setCourse(txtCourse.getText().trim());
            student.setBudgetRange(txtBudgetRange.getText().trim());
            student.setPreferredArea(txtPreferredArea.getText().trim());

            StudentDAO studentDAO = new StudentDAO();

            boolean updated = studentDAO.updateStudent(student);

            if (updated) {

                JOptionPane.showMessageDialog(
                        this,
                        "Profile updated successfully!"
                );

                dispose();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Profile update failed."
                );
            }
        });

        // CANCEL BUTTON
        btnCancel.addActionListener(e -> {

            dispose();

        });
    }
}
