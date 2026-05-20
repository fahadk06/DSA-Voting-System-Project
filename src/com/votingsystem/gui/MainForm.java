package com.votingsystem.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MainForm extends JFrame {

    private static final Color DARK_BG  = new Color(30, 30, 47);
    private static final Color PANEL_BG = new Color(44, 44, 64);
    private static final Color BTN_GREEN = new Color(50, 200, 120);
    private static final Color BTN_RED   = new Color(220, 70, 70);
    private static final Color BTN_CYAN  = new Color(50, 200, 220);
    private static final Color TEXT_WHITE = new Color(230, 230, 255);
    private static final Color TEXT_GRAY  = new Color(150, 150, 180);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRole;

    public MainForm() {
        setTitle("Online Voting System - Login");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(DARK_BG);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildLoginForm(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 5));
        header.setBackground(DARK_BG);
        header.setBorder(new EmptyBorder(40, 20, 20, 20));

        JLabel lblTitle = new JLabel("E-VOTING PORTAL", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(BTN_CYAN);

        JLabel lblSub = new JLabel("Secure Authentication System", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSub.setForeground(TEXT_GRAY);

        header.add(lblTitle);
        header.add(lblSub);
        return header;
    }

    private JPanel buildLoginForm() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(PANEL_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 40, 10, 40),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
                        new EmptyBorder(30, 30, 30, 30)
                )
        ));

        formPanel.add(createLabel("Select Role:"));
        formPanel.add(Box.createVerticalStrut(5));
        comboRole = new JComboBox<>(new String[]{"Administrator", "Voter"});
        styleComboBox(comboRole);
        formPanel.add(comboRole);
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(createLabel("Username / CNIC:"));
        formPanel.add(Box.createVerticalStrut(5));
        txtUsername = new JTextField();
        styleTextField(txtUsername);
        formPanel.add(txtUsername);
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(createLabel("Password / PIN:"));
        formPanel.add(Box.createVerticalStrut(5));
        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        formPanel.add(txtPassword);
        formPanel.add(Box.createVerticalStrut(30));

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        btnPanel.setBackground(PANEL_BG);
        btnPanel.setMaximumSize(new Dimension(250, 40));
        btnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnLogin = createButton("Login", BTN_GREEN);
        JButton btnExit  = createButton("Exit",  BTN_RED);

        btnLogin.addActionListener(e -> doLogin());
        btnExit.addActionListener(e -> System.exit(0));

        btnPanel.add(btnLogin);
        btnPanel.add(btnExit);
        formPanel.add(btnPanel);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(DARK_BG);
        wrapper.add(formPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(DARK_BG);
        footer.setBorder(new EmptyBorder(10, 10, 20, 10));

        JLabel lblVersion = new JLabel("v1.0 | © 2026 Online Voting System");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblVersion.setForeground(TEXT_GRAY);

        footer.add(lblVersion);
        return footer;
    }

    private void doLogin() {
        String role     = (String) comboRole.getSelectedItem();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        java.sql.Connection conn = com.votingsystem.database.DBConnection.getConnection();

        if (conn == null) {
            showError("Database connection failed.");
            return;
        }

        try {
            if (role.equals("Administrator")) {
                String sql = "SELECT * FROM users WHERE cnic=? AND password=? AND role='admin' AND is_verified=TRUE";
                java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                java.sql.ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String name = rs.getString("full_name");
                    JOptionPane.showMessageDialog(this, "Welcome, " + name + "!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new AdminDashboard();
                } else {
                    showError("Invalid Administrator credentials.");
                }

            } else if (role.equals("Voter")) {
                String sql = "SELECT * FROM users WHERE cnic=? AND password=? AND role='voter' AND is_verified=TRUE";
                java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                java.sql.ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String name = rs.getString("full_name");
                    JOptionPane.showMessageDialog(this, "Welcome, " + name + "!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new UserDashboard(name);
                } else {
                    showError("Invalid Voter credentials or account not verified.");
                }
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Authentication Error", JOptionPane.ERROR_MESSAGE);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_WHITE);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private void styleTextField(JTextField field) {
        field.setFont(FONT_NORMAL);
        field.setForeground(TEXT_WHITE);
        field.setBackground(DARK_BG);
        field.setCaretColor(TEXT_WHITE);
        field.setMaximumSize(new Dimension(250, 35));
        field.setPreferredSize(new Dimension(250, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BTN_CYAN, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setHorizontalAlignment(JTextField.CENTER);
    }

    private void styleComboBox(JComboBox<String> box) {
        box.setFont(FONT_NORMAL);
        box.setForeground(Color.BLACK);
        box.setMaximumSize(new Dimension(250, 35));
        box.setPreferredSize(new Dimension(250, 35));
        box.setAlignmentX(Component.CENTER_ALIGNMENT);
        ((JLabel)box.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_LABEL);
        btn.setForeground(Color.BLACK);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(color.brighter()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(color); }
        });
        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(MainForm::new);
    }
}