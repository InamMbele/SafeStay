package safestay;

import safestay.dao.StudentDAO;
import safestay.model.Student;
import safestay.ui.MainFrame;

import javax.swing.*;
import java.sql.SQLException;

/** Entry point. Opens a desktop window: no web server and no browser link are needed. */
public class SafeStayApp {
    static final String DEFAULT_EMAIL = "demo.student@safestay.test";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SafeStayApp::start);
    }

    private static void start() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }

        // 1. Connect to the agreed database (SAFESTAY).
        while (true) {
            if (Db.password() == null) {
                JPasswordField field = new JPasswordField(18);
                int ok = JOptionPane.showConfirmDialog(null, field, "MySQL password for the SAFESTAY database",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (ok != JOptionPane.OK_OPTION) return;
                Db.setPassword(new String(field.getPassword()));
            }
            try {
                Db.test();
                break;
            } catch (SQLException e) {
                int again = JOptionPane.showConfirmDialog(null,
                        "Could not connect to the SAFESTAY database:\n" + e.getMessage()
                        + "\n\nCheck that MySQL is running, the password is right and the MySQL JDBC jar is in the lib folder."
                        + "\n\nTry again?", "Connection failed", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (again != JOptionPane.YES_OPTION) return;
                Db.setPassword(null);
            }
        }

        // 2. Choose the student (temporary: the login section will replace this).
        StudentDAO students = new StudentDAO();
        while (true) {
            Object input = JOptionPane.showInputDialog(null, "Student email:", "SafeStay sign-in",
                    JOptionPane.QUESTION_MESSAGE, null, null, DEFAULT_EMAIL);
            if (input == null) return;
            String email = input.toString().trim();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter an email address.", "Missing email", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            try {
                Student s = students.findByEmail(email);
                if (s == null) {
                    JOptionPane.showMessageDialog(null, "No student is registered with that email.\n"
                            + "(Run SafeStay_Setup.sql to add the demo student.)", "Not found", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                new MainFrame(s).setVisible(true);
                return;
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }
}
