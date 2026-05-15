package com.votingsystem.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminLogin {
    private JPanel AdminLoginPanel;
    private JPanel leftpanel;
    private JLabel Heading1;
    private JLabel Heading2;
    private JPanel Rightpanel;
    private JLabel EmailLb;
    private JTextField tfEmail;
    private JLabel PasswordLb;
    private JPasswordField passwordF1;
    private JButton btnOK;
    private JButton btnCancel;

    // Constructor to set up the form
    public AdminLogin() {

        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    // Main method to launch the window
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Admin Login");

            AdminLogin adminLogin = new AdminLogin();


            frame.setContentPane(adminLogin.AdminLoginPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 400);       // Set your preferred size
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }
}