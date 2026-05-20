package com.votingsystem.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserLogin extends JFrame {

    private static final Color DARK_BG    = new Color(30, 30, 47);
    private static final Color PANEL_BG   = new Color(44, 44, 64);
    private static final Color BTN_GREEN  = new Color(50, 200, 120);
    private static final Color BTN_RED    = new Color(220, 70, 70);
    private static final Color BTN_CYAN   = new Color(50, 200, 220);
    private static final Color TEXT_WHITE = new Color(230, 230, 255);
    private static final Color TEXT_GRAY  = new Color(150, 150, 180);
    private static final Color FIELD_BG   = new Color(55, 55, 78);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);

    private JTextField     cnicField;
    private JPasswordField passwordField;
    private JLabel         statusLabel;

    public UserLogin() {
        setTitle("User Login - Online Voting System");
        setSize(480, 560);
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

    // ═══════════════════════════════════════════
    //  1. TOP BAR
    // ═══════════════════════════════════════════
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

    // ═══════════════════════════════════════════
    //  2. LOGIN CARD
    // ═══════════════════════════════════════════
    private JPanel buildLoginCard() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(DARK_BG);

        JPanel card = new JPanel();
        card.setBackground(PANEL_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BTN_GREEN, 2),
                new EmptyBorder(35, 40, 35, 40)
        ));
        card.setPreferredSize(new Dimension(380, 420));

        JLabel icon = new JLabel("👤", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("User Login", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(BTN_GREEN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter your CNIC and password", SwingConstants.CENTER);
        subtitle.setFont(FONT_SMALL);
        subtitle.setForeground(TEXT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel cnicLabel = new JLabel("CNIC (without dashes)");
        cnicLabel.setFont(FONT_LABEL);
        cnicLabel.setForeground(TEXT_GRAY);
        cnicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        cnicField = new JTextField();
        cnicField.setFont(FONT_NORMAL);
        cnicField.setForeground(TEXT_WHITE);
        cnicField.setBackground(FIELD_BG);
        cnicField.setCaretColor(TEXT_WHITE);
        cnicField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cnicField.setBorder(BorderFactory.createCompoundBorder(
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

        JButton btnLogin = createButton("Login", BTN_GREEN);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.addActionListener(e -> doLogin());

        JButton btnRegister = createButton("New User? Register Here", BTN_CYAN);
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnRegister.addActionListener(e -> goToRegister());

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
        card.add(cnicLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(cnicField);
        card.add(Box.createVerticalStrut(14));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(8));
        card.add(btnRegister);
        card.add(Box.createVerticalStrut(6));
        card.add(btnBack);

        wrapper.add(card);
        return wrapper;
    }

    // ═══════════════════════════════════════════
    //  3. BOTTOM BAR
    // ═══════════════════════════════════════════
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bar.setBackground(PANEL_BG);
        bar.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel note = new JLabel("Only verified users can vote. Contact admin if not verified.");
        note.setFont(FONT_SMALL);
        note.setForeground(TEXT_GRAY);

        bar.add(note);
        return bar;
    }

    // ═══════════════════════════════════════════
    //  DB LOGIN LOGIC  ← only this method changed
    // ═══════════════════════════════════════════
    private void doLogin() {
        String cnic     = cnicField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // ── Validation
        if (cnic.isEmpty() || password.isEmpty()) {
            setStatus("Please enter CNIC and password.", BTN_RED);
            return;
        }

        // ── DB Connection
        Connection conn = com.votingsystem.database.DBConnection.getConnection();
        if (conn == null) {
            setStatus("Database connection failed.", BTN_RED);
            return;
        }

        try {
            // ── Query: match cnic + password + role voter only
            String sql = "SELECT id, full_name, is_verified, has_voted " +
                    "FROM users " +
                    "WHERE cnic = ? AND password = ? AND role = 'voter'";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cnic);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // ── User found — check verified
                boolean isVerified = rs.getBoolean("is_verified");

                if (!isVerified) {
                    setStatus("Account not verified. Contact admin.", BTN_RED);
                    JOptionPane.showMessageDialog(this,
                            "Your account has not been verified by the admin yet.\n"
                                    + "Please wait for admin approval.",
                            "Not Verified", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // ── Verified — read user data and open dashboard
                int     userId   = rs.getInt("id");
                String  fullName = rs.getString("full_name");
                boolean hasVoted = rs.getBoolean("has_voted");

                setStatus("Welcome, " + fullName + "!", BTN_GREEN);
                JOptionPane.showMessageDialog(this,
                        "Welcome, " + fullName + "!\nRedirecting to dashboard...",
                        "Login Successful", JOptionPane.INFORMATION_MESSAGE);

                dispose();
                new UserDashboard(userId, fullName, hasVoted); // ← passes all 3 needed values

            } else {
                // ── No match
                setStatus("Invalid CNIC or password.", BTN_RED);
                passwordField.setText("");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            setStatus("Database error: " + e.getMessage(), BTN_RED);
        }
    }

    // ═══════════════════════════════════════════
    //  NAVIGATION
    // ═══════════════════════════════════════════
    private void goToRegister() {
        dispose();
        new UserRegisteration();
    }

    private void goBack() {
        dispose();
        new MainForm();
    }

    // ═══════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════
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
        SwingUtilities.invokeLater(UserLogin::new);
    }
}