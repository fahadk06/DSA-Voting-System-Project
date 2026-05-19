package com.votingsystem.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class UserRegisteration extends JFrame {

    // ═══════════════════════════════════════════
    //  COLORS & FONTS  (same as all other forms)
    // ═══════════════════════════════════════════
    private static final Color DARK_BG    = new Color(30, 30, 47);
    private static final Color PANEL_BG   = new Color(44, 44, 64);
    private static final Color BTN_BLUE   = new Color(70, 130, 255);
    private static final Color BTN_GREEN  = new Color(50, 200, 120);
    private static final Color BTN_RED    = new Color(220, 70, 70);
    private static final Color BTN_CYAN   = new Color(50, 200, 220);
    private static final Color TEXT_WHITE = new Color(230, 230, 255);
    private static final Color TEXT_GRAY  = new Color(150, 150, 180);
    private static final Color FIELD_BG   = new Color(55, 55, 78);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);

    // ═══════════════════════════════════════════
    //  COMPONENTS
    // ═══════════════════════════════════════════
    private JTextField     nameField;
    private JTextField     cnicField;
    private JTextField     areaField;
    private JPasswordField passwordField;
    private JPasswordField confirmPassField;
    private JLabel         statusLabel;

    // ═══════════════════════════════════════════
    //  CONSTRUCTOR
    // ═══════════════════════════════════════════
    public UserRegisteration() {
        setTitle("User Registration - Online Voting System");
        setSize(480, 660);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(DARK_BG);
        setLayout(new BorderLayout());

        add(buildTopBar(),        BorderLayout.NORTH);
        add(buildRegisterCard(),  BorderLayout.CENTER);
        add(buildBottomBar(),     BorderLayout.SOUTH);

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
    //  2. REGISTRATION CARD
    //     Icon + Title + All Fields + Buttons
    // ═══════════════════════════════════════════
    private JPanel buildRegisterCard() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(DARK_BG);

        // Card panel
        JPanel card = new JPanel();
        card.setBackground(PANEL_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BTN_BLUE, 2),
                new EmptyBorder(30, 40, 30, 40)
        ));
        card.setPreferredSize(new Dimension(390, 560));

        // ── Icon
        JLabel icon = new JLabel("📋", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Title
        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(BTN_BLUE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Subtitle
        JLabel subtitle = new JLabel("Fill all fields to register as a voter", SwingConstants.CENTER);
        subtitle.setFont(FONT_SMALL);
        subtitle.setForeground(TEXT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Full Name Field
        nameField = buildField();
        JLabel nameLabel = buildLabel("Full Name");

        // ── CNIC Field
        cnicField = buildField();
        JLabel cnicLabel = buildLabel("CNIC (13 digits, no dashes)");

        // ── Area Field
        areaField = buildField();
        JLabel areaLabel = buildLabel("Area / City");

        // ── Password Field
        passwordField = new JPasswordField();
        stylePasswordField(passwordField);
        JLabel passLabel = buildLabel("Password");

        // ── Confirm Password Field
        confirmPassField = new JPasswordField();
        stylePasswordField(confirmPassField);
        JLabel confirmLabel = buildLabel("Confirm Password");

        // ── Status Label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(BTN_RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Register Button
        JButton btnRegister = createButton("Register", BTN_GREEN);
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnRegister.addActionListener(e -> doRegister());

        // ── Already have account button
        JButton btnLogin = createButton("Already have account? Login", BTN_CYAN);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnLogin.addActionListener(e -> goToLogin());

        // ── Back Button
        JButton btnBack = createButton("Back to Main Menu", BTN_RED);
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnBack.addActionListener(e -> goBack());

        // ── Add everything to card
        card.add(icon);
        card.add(Box.createVerticalStrut(6));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(18));

        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(nameField);
        card.add(Box.createVerticalStrut(12));

        card.add(cnicLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(cnicField);
        card.add(Box.createVerticalStrut(12));

        card.add(areaLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(areaField);
        card.add(Box.createVerticalStrut(12));

        card.add(passLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(12));

        card.add(confirmLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(confirmPassField);
        card.add(Box.createVerticalStrut(10));

        card.add(statusLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(btnRegister);
        card.add(Box.createVerticalStrut(8));
        card.add(btnLogin);
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

        JLabel note = new JLabel("After registration, wait for admin to verify your account.");
        note.setFont(FONT_SMALL);
        note.setForeground(TEXT_GRAY);

        bar.add(note);
        return bar;
    }

    // ═══════════════════════════════════════════
    //  ACTION — REGISTER
    // ═══════════════════════════════════════════
    private void doRegister() {
        // Get all field values
        String name        = nameField.getText().trim();
        String cnic        = cnicField.getText().trim();
        String area        = areaField.getText().trim();
        String password    = new String(passwordField.getPassword()).trim();
        String confirmPass = new String(confirmPassField.getPassword()).trim();

        // ── Step 1: Check no field is empty
        if (name.isEmpty() || cnic.isEmpty() || area.isEmpty()
                || password.isEmpty() || confirmPass.isEmpty()) {
            setStatus("All fields are required.", BTN_RED);
            return;
        }

        // ── Step 2: Validate CNIC — must be exactly 13 digits
        if (!cnic.matches("\\d{13}")) {
            setStatus("CNIC must be exactly 13 digits.", BTN_RED);
            cnicField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BTN_RED, 1),
                    new EmptyBorder(6, 10, 6, 10)
            ));
            return;
        }

        // ── Step 3: Check passwords match
        if (!password.equals(confirmPass)) {
            setStatus("Passwords do not match.", BTN_RED);
            confirmPassField.setText("");
            return;
        }

        // ── Step 4: Check password length
        if (password.length() < 6) {
            setStatus("Password must be at least 6 characters.", BTN_RED);
            return;
        }

        // ── Step 5: All valid — Registration successful
        setStatus("Registered successfully!", BTN_GREEN);

        JOptionPane.showMessageDialog(this,
                "Registration successful!\n\n"
                        + "Name:  " + name + "\n"
                        + "CNIC:  " + cnic + "\n"
                        + "Area:  " + area + "\n\n"
                        + "Please wait for admin to verify your account\n"
                        + "before you can login and vote.",
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Go to login after successful registration
        dispose();
        new UserLogin();
    }

    // ═══════════════════════════════════════════
    //  NAVIGATION
    // ═══════════════════════════════════════════

    // Go to UserLogin
    private void goToLogin() {
        dispose();
        new UserLogin();
    }

    // Go back to MainForm
    private void goBack() {
        dispose();
        new MainForm();
    }

    // ═══════════════════════════════════════════
    //  HELPER METHODS
    // ═══════════════════════════════════════════

    // Build a styled label
    private JLabel buildLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_GRAY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    // Build a styled text field
    private JTextField buildField() {
        JTextField field = new JTextField();
        field.setFont(FONT_NORMAL);
        field.setForeground(TEXT_WHITE);
        field.setBackground(FIELD_BG);
        field.setCaretColor(TEXT_WHITE);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BTN_CYAN, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    // Style a password field (same look as text field)
    private void stylePasswordField(JPasswordField field) {
        field.setFont(FONT_NORMAL);
        field.setForeground(TEXT_WHITE);
        field.setBackground(FIELD_BG);
        field.setCaretColor(TEXT_WHITE);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BTN_CYAN, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    // Update status label
    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    // ═══════════════════════════════════════════
    //  BUTTON FACTORY  (same as all other forms)
    // ═══════════════════════════════════════════
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

    // ═══════════════════════════════════════════
    //  MAIN  (test this form standalone)
    // ═══════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserRegisteration::new);
    }
}