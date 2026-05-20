package com.votingsystem.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminLogin extends JFrame {

    private static final Color DARK_BG    = new Color(30, 30, 47);
    private static final Color PANEL_BG   = new Color(44, 44, 64);
    private static final Color BTN_GREEN  = new Color(50, 200, 120);
    private static final Color BTN_RED    = new Color(220, 70, 70);
    private static final Color BTN_CYAN   = new Color(50, 200, 220);
    private static final Color BTN_YELLOW = new Color(230, 180, 50);
    private static final Color TEXT_WHITE = new Color(230, 230, 255);
    private static final Color TEXT_GRAY  = new Color(150, 150, 180);
    private static final Color FIELD_BG   = new Color(55, 55, 78);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);

    private JTextField     emailField;
    private JPasswordField passwordField;
    private JLabel         statusLabel;

    public AdminLogin() {
        setTitle("Admin Login - Online Voting System");
        setSize(480, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(DARK_BG);
        setLayout(new BorderLayout());

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildLoginCard(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bar.setBackground(PANEL_BG);
        bar.setBorder(new EmptyBorder(12, 0, 12, 0));

        JLabel systemName = new JLabel("Online Voting System");
        systemName.setFont(FONT_SMALL);
        systemName.setForeground(TEXT_GRAY);

        bar.add(systemName);
        return bar;
    }

    private JPanel buildLoginCard() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(DARK_BG);

        JPanel card = new JPanel();
        card.setBackground(PANEL_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BTN_YELLOW, 2),
                new EmptyBorder(35, 40, 35, 40)
        ));
        card.setPreferredSize(new Dimension(380, 400));

        JLabel icon = new JLabel("🛡", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Admin Login", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(BTN_YELLOW);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter your CNIC and password", SwingConstants.CENTER);
        subtitle.setFont(FONT_SMALL);
        subtitle.setForeground(TEXT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = new JLabel("CNIC");
        emailLabel.setFont(FONT_LABEL);
        emailLabel.setForeground(TEXT_GRAY);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField = new JTextField();
        emailField.setFont(FONT_NORMAL);
        emailField.setForeground(TEXT_WHITE);
        emailField.setBackground(FIELD_BG);
        emailField.setCaretColor(TEXT_WHITE);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BTN_CYAN, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(FONT_LABEL);
        passLabel.setForeground(TEXT_GRAY);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setFont(FONT_NORMAL);
        passwordField.setForeground(TEXT_WHITE);
        passwordField.setBackground(FIELD_BG);
        passwordField.setCaretColor(TEXT_WHITE);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BTN_CYAN, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));

        passwordField.addActionListener(e -> doLogin());

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(BTN_RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnLogin = createButton("Login", BTN_YELLOW);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.addActionListener(e -> doLogin());

        JButton btnBack = createButton("Back to Main Menu", BTN_RED);
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnBack.addActionListener(e -> goBack());

        card.add(icon);
        card.add(Box.createVerticalStrut(8));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(22));
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(8));
        card.add(btnBack);

        wrapper.add(card);
        return wrapper;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bar.setBackground(PANEL_BG);
        bar.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel note = new JLabel("Authorized personnel only. All activity is logged.");
        note.setFont(FONT_SMALL);
        note.setForeground(TEXT_GRAY);

        bar.add(note);
        return bar;
    }

    private void doLogin() {
        String cnic     = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (cnic.isEmpty() || password.isEmpty()) {
            setStatus("Please enter CNIC and password.", BTN_RED);
            return;
        }

        Connection conn = com.votingsystem.database.DBConnection.getConnection();

        if (conn == null) {
            setStatus("Database connection failed.", BTN_RED);
            return;
        }

        try {
            String sql = "SELECT * FROM users WHERE cnic=? AND password=? AND role='admin' AND is_verified=TRUE";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cnic);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("full_name");
                setStatus("Welcome, " + name + "!", BTN_GREEN);
                JOptionPane.showMessageDialog(this,
                        "Welcome, " + name + "!",
                        "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new AdminDashboard();
            } else {
                setStatus("Invalid CNIC or password.", BTN_RED);
                passwordField.setText("");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            setStatus("Database error: " + e.getMessage(), BTN_RED);
        }
    }

    private void goBack() {
        dispose();
        new MainForm();
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_LABEL);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(color.brighter()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(color); }
        });
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminLogin::new);
    }
}