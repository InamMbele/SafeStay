package safestay.ui;

import safestay.dao.LandlordDAO;
import safestay.model.Landlord;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class LandlordDashboard extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearchId, txtUpdateFirst, txtUpdateLast, txtUpdatePhone;

    public LandlordDashboard() {
        setTitle("SafeStay - Landlord Dashboard - CRUD Evidence (Member 3)");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(243,244,246));
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(new Color(30,58,138));
        JLabel title = new JLabel("LANDLORD MANAGEMENT - SELECT / UPDATE / DELETE - ERD ALIGNED");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Inter", Font.BOLD, 14));
        top.add(title);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"LandlordID","FirstName","LastName","Email","Phone","DateRegistered"},0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel editPanel = new JPanel();
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
        editPanel.setBorder(BorderFactory.createTitledBorder("UPDATE / DELETE"));
        editPanel.setPreferredSize(new Dimension(300,0));

        txtSearchId = new JTextField();
        txtUpdateFirst = new JTextField();
        txtUpdateLast = new JTextField();
        txtUpdatePhone = new JTextField();

        editPanel.add(new JLabel("Enter LandlordID:"));
        editPanel.add(txtSearchId);
        
        JButton btnLoad = new JButton("Load (SELECT by ID)");
        btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { loadOne(); }
        });
        editPanel.add(btnLoad);
        editPanel.add(new JLabel("First Name:")); editPanel.add(txtUpdateFirst);
        editPanel.add(new JLabel("Last Name:")); editPanel.add(txtUpdateLast);
        editPanel.add(new JLabel("Phone:")); editPanel.add(txtUpdatePhone);

        JButton btnUpdate = new JButton("UPDATE Landlord");
        btnUpdate.setBackground(new Color(16,185,129));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { doUpdate(); }
        });

        JButton btnDelete = new JButton("DELETE Landlord");
        btnDelete.setBackground(new Color(239,68,68));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { doDelete(); }
        });

        JButton btnRefresh = new JButton("Refresh Table (SELECT ALL)");
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { loadAll(); }
        });

        editPanel.add(btnUpdate);
        editPanel.add(btnDelete);
        editPanel.add(Box.createVerticalStrut(20));
        editPanel.add(btnRefresh);

        add(editPanel, BorderLayout.EAST);
        loadAll();
    }

    private void loadAll(){
        try {
            model.setRowCount(0);
            List<Landlord> list = new LandlordDAO().getAllLandlords();
            for(Landlord l: list){
                model.addRow(new Object[]{l.landlordID, l.firstName, l.lastName, l.email, l.phoneNumber, l.dateRegistered});
            }
            System.out.println("SELECT: Retrieved " + list.size() + " landlords");
        } catch (SQLException ex){ 
            ex.printStackTrace(); 
            JOptionPane.showMessageDialog(this, ex.getMessage()); 
        }
    }

    private void loadOne(){
        try {
            int id = Integer.parseInt(txtSearchId.getText().trim());
            Landlord l = new LandlordDAO().getLandlordById(id);
            if(l!=null){
                txtUpdateFirst.setText(l.firstName);
                txtUpdateLast.setText(l.lastName);
                txtUpdatePhone.setText(l.phoneNumber);
                JOptionPane.showMessageDialog(this, "SELECT SUCCESS: Landlord " + id + " loaded");
            } else {
                JOptionPane.showMessageDialog(this, "Not found");
            }
        } catch (Exception ex){ 
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void doUpdate(){
        try {
            int id = Integer.parseInt(txtSearchId.getText().trim());
            String first = txtUpdateFirst.getText().trim();
            String last = txtUpdateLast.getText().trim();
            if (id <= 0 || first.isEmpty() || last.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a positive landlord ID, first name and last name.");
                return;
            }
            Landlord l = new Landlord();
            l.landlordID = id;
            l.firstName = first;
            l.lastName = last;
            l.phoneNumber = txtUpdatePhone.getText().trim();
            if(new LandlordDAO().updateLandlord(l)){
                JOptionPane.showMessageDialog(this, "Landlord updated successfully.");
                loadAll();
            } else {
                JOptionPane.showMessageDialog(this, "No landlord found with ID " + id + ".");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid numeric landlord ID.");
        } catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void doDelete(){
        try {
            int id = Integer.parseInt(txtSearchId.getText().trim());
            if (id <= 0) {
                JOptionPane.showMessageDialog(this, "Enter a positive landlord ID.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Delete landlord ID " + id + " and its linked accommodation, bookings, payments, reviews, reports, saved listings and verifications? This cannot be undone.", "CONFIRM DELETE", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(confirm==JOptionPane.YES_OPTION){
                if(new LandlordDAO().deleteLandlord(id)){
                    JOptionPane.showMessageDialog(this, "Landlord and linked records deleted successfully.");
                    loadAll();
                } else {
                    JOptionPane.showMessageDialog(this, "No landlord found with ID " + id + ".");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid numeric landlord ID.");
        } catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}