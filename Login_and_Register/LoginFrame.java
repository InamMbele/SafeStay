/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package safestayapp;

/**
 *  
 * @author Admin
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    private Color blue = new Color(20, 70, 190); //so we dont have to set the color all the time 
    private Color lightBlue = new Color(235, 243, 255);//so we dont have to set the color all the time 
    private Color borderColor = new Color(215, 220, 235);
    private Color textColor = new Color(45, 55, 85);
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnRegister;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblForgotPassword;
    private javax.swing.JLabel lblLogin;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtEmail;

    public LoginFrame() {

        initComponents();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        lblLogin = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        btnRegister = new javax.swing.JButton();
        lblForgotPassword = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SafeStay - Student Login");
        setResizable(false);

        lblTitle.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 28));
        lblTitle.setForeground(new java.awt.Color(20, 70, 190));
        lblTitle.setText("SafeStay");

        lblSubtitle.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        lblSubtitle.setForeground(new java.awt.Color(20, 70, 190));
        lblSubtitle.setText("Student Accommodation");

        lblLogin.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        lblLogin.setText("Login");

        lblEmail.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        lblEmail.setText("Email:");

        lblPassword.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        lblPassword.setText("Password:");

        btnLogin.setBackground(new java.awt.Color(20, 70, 190));
        btnLogin.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        btnLogin.setForeground(java.awt.Color.WHITE);
        btnLogin.setText("Login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        btnRegister.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        btnRegister.setText("Register");
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        lblForgotPassword.setForeground(new java.awt.Color(20, 70, 190));
        lblForgotPassword.setText("Forgot Password?");

        javax.swing.GroupLayout layout
                = new javax.swing.GroupLayout(getContentPane());

        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING
                )
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addGap(50, 50, 50)
                                        .addGroup(
                                                layout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING
                                                )
                                                        .addComponent(lblTitle)
                                                        .addComponent(lblSubtitle)
                                                        .addComponent(lblLogin)
                                                        .addComponent(lblEmail)
                                                        .addComponent(txtEmail)
                                                        .addComponent(lblPassword)
                                                        .addComponent(txtPassword)
                                                        .addGroup(
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                layout.createSequentialGroup()
                                                                        .addComponent(
                                                                                btnLogin,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                130,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                                                        )
                                                                        .addGap(20, 20, 20)
                                                                        .addComponent(
                                                                                btnRegister,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                130,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                                                        )
                                                        )
                                                        .addComponent(lblForgotPassword)
                                        )
                                        .addGap(50, 50, 50)
                        )
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(
                                lblTitle,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                40,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(0, 0, 0)
                        .addComponent(
                                lblSubtitle,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                25,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(40, 40, 40)
                        .addComponent(
                                lblLogin,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                30,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(25, 25, 25)
                        .addComponent(lblEmail)
                        .addGap(5, 5, 5)
                        .addComponent(
                                txtEmail,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                35,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(20, 20, 20)
                        .addComponent(lblPassword)
                        .addGap(5, 5, 5)
                        .addComponent(
                                txtPassword,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                35,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(10, 10, 10)
                        .addComponent(lblForgotPassword)
                        .addGap(30, 30, 30)
                        .addGroup(
                                layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.BASELINE
                                )
                                        .addComponent(
                                                btnLogin,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                40,
                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                                        .addComponent(
                                                btnRegister,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                40,
                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                        )
                        .addContainerGap(50, Short.MAX_VALUE)
        );

        pack();
    }

    // LOGIN BUTTON
    private void btnLoginActionPerformed(
            java.awt.event.ActionEvent evt) {

        String email = txtEmail.getText().trim();

        String password
                = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your email and password."
            );

            return;
        }

        StudentDAO studentDAO = new StudentDAO();

        Student student
                = studentDAO.loginStudent(email, password);

        if (student != null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Login successful!\nWelcome, "
                    + student.getFirstName()
                    + " " + student.getLastName()
            );

            ProfileFrame profile = new ProfileFrame(student);

            profile.setVisible(true);

            this.dispose();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Login failed. Incorrect email or password."
            );
        }

    }
    // REGISTER BUTTON

    private void btnRegisterActionPerformed(
            java.awt.event.ActionEvent evt) {

        RegistrationFrame registration = new RegistrationFrame();

        registration.setVisible(true);

        this.dispose();
    }

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(
                new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        }
        );
    }

}
