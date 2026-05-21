package com.votingsystem.gui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.votingsystem.dsa.BinarySearch;
import com.votingsystem.dsa.CandidateLinkedList;

public class UserConfirmation extends JFrame {

    // ═══════════════════════════════════════════
    //  COLORS & FONTS  (identical to AdminDashboard)
    // ═══════════════════════════════════════════
    private static final Color DARK_BG     = new Color(30, 30, 47);
    private static final Color PANEL_BG    = new Color(44, 44, 64);
    private static final Color BUTTON_BLUE = new Color(70, 130, 255);
    private static final Color BTN_GREEN   = new Color(50, 200, 120);
    private static final Color BTN_RED     = new Color(220, 70, 70);
    private static final Color BTN_YELLOW  = new Color(230, 180, 50);
    private static final Color BTN_CYAN    = new Color(50, 200, 220);
    private static final Color TEXT_WHITE  = new Color(230, 230, 255);
    private static final Color TEXT_GRAY   = new Color(150, 150, 180);
    private static final Color ROW_EVEN    = new Color(44, 44, 64);
    private static final Color ROW_ODD     = new Color(52, 52, 75);
    private static final Color ROW_SELECT  = new Color(70, 130, 255, 80);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 12);

    // ═══════════════════════════════════════════
    //  DSA — Linked List for Prev / Next navigation
    //  Columns: id, username, cnic, area, status
    // ═══════════════════════════════════════════
    private final CandidateLinkedList linkedList = new CandidateLinkedList();

    // ═══════════════════════════════════════════
    //  USER DATA
    //  users[]       = full unfiltered set from DB
    //  displayData[] = what the table currently shows
    // ═══════════════════════════════════════════
    private Object[][] users       = {};
    private Object[][] displayData = {};

    // ═══════════════════════════════════════════
    //  COMPONENTS
    // ═══════════════════════════════════════════
    private DefaultTableModel tableModel;
    private JTable            table;
    private JTextField        searchField;
    private JLabel            statusLabel;
    private JLabel            lblTotalUsers;
    private JLabel            lblConfirmed;
    private JLabel            lblPending;

    private int currentIndex = 0;

    // ═══════════════════════════════════════════
    //  CONSTRUCTOR
    // ═══════════════════════════════════════════
    public UserConfirmation() {
        setTitle("User Confirmation - Online Voting System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(DARK_BG);

        loadUsersFromDB();        // DB first → fills users[] + linkedList

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // ═══════════════════════════════════════════
    //  DB — LOAD ALL USERS
    //  Expects a `users` table with columns:
    //  Actual DB columns:
    //    id, full_name, cnic, area, role, is_verified
    //  is_verified: 0 = Pending, 1 = Confirmed
    //  Only load role='voter' rows (admins don't need confirmation)
    // ═══════════════════════════════════════════
    private void loadUsersFromDB() {
        try {
            Connection conn = com.votingsystem.database.DBConnection.getConnection();
            if (conn == null) return;

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT id, full_name, cnic, area, is_verified FROM users " +
                            "WHERE role = 'voter' ORDER BY id");

            java.util.List<Object[]> list = new java.util.ArrayList<>();
            linkedList.clear();

            while (rs.next()) {
                int    id         = rs.getInt("id");
                String fullName   = rs.getString("full_name");
                String cnic       = rs.getString("cnic");
                String area       = rs.getString("area");
                int    verified   = rs.getInt("is_verified");   // 0 or 1
                String statusStr  = verified == 1 ? "Confirmed" : "Pending";

                list.add(new Object[]{id, fullName, cnic, area, statusStr});

                // CandidateLinkedList reuse:
                // id=id, name=full_name, party=cnic, area=area, votes=verified
                linkedList.add(id, fullName, cnic, area, verified);
            }
            rs.close();
            st.close();

            users       = list.toArray(new Object[0][]);
            displayData = users;

        } catch (SQLException e) {
            e.printStackTrace();
            users       = new Object[][]{};
            displayData = users;
        }
    }

    // ═══════════════════════════════════════════
    //  DB — CONFIRM USER  (set is_verified = 1)
    // ═══════════════════════════════════════════
    private boolean dbConfirmUser(int id) {
        try {
            Connection conn = com.votingsystem.database.DBConnection.getConnection();
            if (conn == null) return false;

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET is_verified = 1 WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            showError("DB error: " + e.getMessage());
            return false;
        }
    }

    // ═══════════════════════════════════════════
    //  DB — REVOKE USER  (set is_verified = 0)
    // ═══════════════════════════════════════════
    private boolean dbRevokeUser(int id) {
        try {
            Connection conn = com.votingsystem.database.DBConnection.getConnection();
            if (conn == null) return false;

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET is_verified = 0 WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            showError("DB error: " + e.getMessage());
            return false;
        }
    }

    // ═══════════════════════════════════════════
    //  DB — DELETE USER
    // ═══════════════════════════════════════════
    private boolean dbDeleteUser(int id) {
        try {
            Connection conn = com.votingsystem.database.DBConnection.getConnection();
            if (conn == null) return false;

            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM users WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            showError("DB error: " + e.getMessage());
            return false;
        }
    }

    // ═══════════════════════════════════════════
    //  1. HEADER
    // ═══════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel("User Confirmation");
        title.setFont(FONT_TITLE);
        title.setForeground(BTN_CYAN);

        JPanel searchArea = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchArea.setBackground(PANEL_BG);

        JLabel searchLbl = new JLabel("Search: ");
        searchLbl.setForeground(TEXT_GRAY);
        searchLbl.setFont(FONT_NORMAL);

        searchField = new JTextField(18);
        searchField.setFont(FONT_NORMAL);
        searchField.setForeground(TEXT_WHITE);
        searchField.setBackground(DARK_BG);
        searchField.setCaretColor(TEXT_WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BTN_CYAN, 1),
                new EmptyBorder(5, 8, 5, 8)));
        searchField.addActionListener(e -> doSearch());

        JButton btnSearch = createButton("Search",   BUTTON_BLUE);
        JButton btnReset  = createButton("Show All", BTN_CYAN);

        btnSearch.addActionListener(e -> doSearch());
        btnReset.addActionListener(e -> {
            searchField.setText("");
            displayData = users;
            refreshTable();
            setStatus("Showing all users", BTN_CYAN);
        });

        searchArea.add(searchLbl);
        searchArea.add(searchField);
        searchArea.add(btnSearch);
        searchArea.add(btnReset);

        header.add(title,      BorderLayout.WEST);
        header.add(searchArea, BorderLayout.EAST);
        return header;
    }

    // ═══════════════════════════════════════════
    //  2. CENTER
    // ═══════════════════════════════════════════
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setBackground(DARK_BG);
        center.setBorder(new EmptyBorder(15, 20, 15, 20));
        center.add(buildStatsRow(),  BorderLayout.NORTH);
        center.add(buildTable(),     BorderLayout.CENTER);
        center.add(buildButtonRow(), BorderLayout.SOUTH);
        return center;
    }

    // ═══════════════════════════════════════════
    //  3. STATS ROW
    // ═══════════════════════════════════════════
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 15, 0));
        row.setBackground(DARK_BG);
        row.setPreferredSize(new Dimension(0, 90));

        lblTotalUsers = new JLabel(String.valueOf(users.length));
        lblConfirmed  = new JLabel(String.valueOf(calcConfirmedCount()));
        lblPending    = new JLabel(String.valueOf(calcPendingCount()));

        row.add(buildStatCard("Total Users",      lblTotalUsers, BTN_CYAN));
        row.add(buildStatCard("Confirmed Users",  lblConfirmed,  BTN_GREEN));
        row.add(buildStatCard("Pending Users",    lblPending,    BTN_YELLOW));
        return row;
    }

    private JPanel buildStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(PANEL_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                new EmptyBorder(12, 18, 12, 18)));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_NORMAL);
        titleLbl.setForeground(TEXT_GRAY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(color);

        card.add(titleLbl,   BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // ═══════════════════════════════════════════
    //  4. TABLE
    //  Columns: ID | Username | CNIC | Area | Status
    // ═══════════════════════════════════════════
    private JScrollPane buildTable() {
        String[] columns = {"ID", "Full Name", "CNIC", "Area", "Status"};

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        refreshTable();

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                } else {
                    c.setBackground(ROW_SELECT);
                }
                // Color-code the Status column
                if (col == 4 && !isRowSelected(row)) {
                    String val = (String) getValueAt(row, col);
                    c.setForeground("Confirmed".equals(val) ? BTN_GREEN : BTN_YELLOW);
                } else {
                    c.setForeground(TEXT_WHITE);
                }
                return c;
            }
        };

        table.setFont(FONT_NORMAL);
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setBackground(ROW_EVEN);
        table.setForeground(TEXT_WHITE);
        table.setSelectionBackground(ROW_SELECT);
        table.getTableHeader().setReorderingAllowed(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_LABEL);
        header.setBackground(PANEL_BG);
        header.setForeground(TEXT_GRAY);
        header.setPreferredSize(new Dimension(0, 38));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);

        // Center-align ID and Status columns
        DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
        centerAlign.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerAlign);

        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer();
        table.getColumnModel().getColumn(4).setCellRenderer(statusRenderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(DARK_BG);
        scroll.getViewport().setBackground(ROW_EVEN);
        scroll.setBorder(BorderFactory.createLineBorder(PANEL_BG, 1));
        return scroll;
    }

    // ═══════════════════════════════════════════
    //  5. BUTTON ROW
    // ═══════════════════════════════════════════
    private JPanel buildButtonRow() {
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        btnRow.setBackground(DARK_BG);

        JButton btnConfirm = createButton("✔ Confirm",   BTN_GREEN);
        JButton btnRevoke  = createButton("✖ Revoke",    BTN_YELLOW);
        JButton btnDelete  = createButton("Delete User", BTN_RED);
        JButton btnPrev    = createButton("< Previous",  BTN_CYAN);
        JButton btnNext    = createButton("Next >",      BTN_CYAN);
        JButton btnBack    = createButton("Back",        BTN_RED);

        btnConfirm.addActionListener(e -> doConfirm());
        btnRevoke.addActionListener(e  -> doRevoke());
        btnDelete.addActionListener(e  -> doDelete());
        btnPrev.addActionListener(e    -> doPrev());
        btnNext.addActionListener(e    -> doNext());
        btnBack.addActionListener(e    -> doBack());

        btnRow.add(btnConfirm);
        btnRow.add(btnRevoke);
        btnRow.add(btnDelete);
        btnRow.add(Box.createHorizontalStrut(20));
        btnRow.add(btnPrev);
        btnRow.add(btnNext);
        btnRow.add(Box.createHorizontalStrut(20));
        btnRow.add(btnBack);
        return btnRow;
    }

    // ═══════════════════════════════════════════
    //  6. STATUS BAR
    // ═══════════════════════════════════════════
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(PANEL_BG);
        bar.setBorder(new EmptyBorder(5, 20, 5, 20));

        statusLabel = new JLabel("System Ready");
        statusLabel.setFont(FONT_NORMAL);
        statusLabel.setForeground(BTN_GREEN);

        JLabel version = new JLabel("Online Voting System v1.0");
        version.setFont(FONT_NORMAL);
        version.setForeground(TEXT_GRAY);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(version,     BorderLayout.EAST);
        return bar;
    }

    // ═══════════════════════════════════════════
    //  CONFIRM  → sets is_confirmed = 1 in DB
    // ═══════════════════════════════════════════
    private void doConfirm() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { showError("Select a user to confirm."); return; }

        Object[] selected = displayData[viewRow];
        String   username = (String) selected[1];
        String   status   = (String) selected[4];

        if ("Confirmed".equals(status)) {
            setStatus("'" + username + "' is already confirmed.", BTN_CYAN);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm user '" + username + "'?\nThis will allow them to log in.",
                "Confirm User", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) selected[0];
            if (dbConfirmUser(id)) {
                loadUsersFromDB();
                displayData = users;
                searchField.setText("");
                refreshTable();
                refreshStats();
                setStatus("User '" + username + "' confirmed. They can now log in.", BTN_GREEN);
            }
        }
    }

    // ═══════════════════════════════════════════
    //  REVOKE  → sets is_confirmed = 0 in DB
    // ═══════════════════════════════════════════
    private void doRevoke() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { showError("Select a user to revoke."); return; }

        Object[] selected = displayData[viewRow];
        String   username = (String) selected[1];
        String   status   = (String) selected[4];

        if ("Pending".equals(status)) {
            setStatus("'" + username + "' is already pending / not confirmed.", BTN_YELLOW);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Revoke access for '" + username + "'?\nThey will no longer be able to log in.",
                "Revoke User", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) selected[0];
            if (dbRevokeUser(id)) {
                loadUsersFromDB();
                displayData = users;
                searchField.setText("");
                refreshTable();
                refreshStats();
                setStatus("Access revoked for '" + username + "'.", BTN_YELLOW);
            }
        }
    }

    // ═══════════════════════════════════════════
    //  DELETE  → removes user from DB permanently
    // ═══════════════════════════════════════════
    private void doDelete() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { showError("Select a user to delete."); return; }

        Object[] selected = displayData[viewRow];
        String   username = (String) selected[1];

        int confirm = JOptionPane.showConfirmDialog(this,
                "Permanently delete user '" + username + "'?\nThis cannot be undone.",
                "Delete User", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) selected[0];
            if (dbDeleteUser(id)) {
                loadUsersFromDB();
                currentIndex = 0;
                displayData  = users;
                searchField.setText("");
                refreshTable();
                refreshStats();
                setStatus("User '" + username + "' deleted.", BTN_RED);
            }
        }
    }

    // ═══════════════════════════════════════════
    //  SEARCH  ← DSA: BinarySearch
    //  Searches by username (binary) + area (linear)
    //  stores result in displayData so Edit/Delete work
    // ═══════════════════════════════════════════
    private void doSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            displayData = users;
            refreshTable();
            setStatus("Showing all users", BTN_CYAN);
            return;
        }

        // BinarySearch.search() expects Object[][] where [1]=name, [3]=area
        // Our layout: [0]=id, [1]=username, [2]=cnic, [3]=area, [4]=status
        // ← DSA: reuse BinarySearch — username maps to [1], area maps to [3]
        Object[][] results = BinarySearch.search(users, query);
        displayData = results;

        tableModel.setRowCount(0);
        for (Object[] u : results) {
            tableModel.addRow(new Object[]{u[0], u[1], u[2], u[3], u[4]});
        }

        setStatus("Found " + results.length + " result(s) for '" + query + "'",
                results.length > 0 ? BTN_CYAN : BTN_RED);
    }

    // ═══════════════════════════════════════════
    //  PREV  ← DSA: CandidateLinkedList (circular)
    // ═══════════════════════════════════════════
    private void doPrev() {
        if (linkedList.size() == 0) return;

        // ← DSA: circular prev index from linked list
        currentIndex = linkedList.prevIndex(currentIndex);

        if (displayData == users) {
            selectRow(currentIndex);
        }
        setStatus("Previous: " + users[currentIndex][1]
                + "  (" + (currentIndex + 1) + " / " + linkedList.size() + ")", BTN_CYAN);
    }

    // ═══════════════════════════════════════════
    //  NEXT  ← DSA: CandidateLinkedList (circular)
    // ═══════════════════════════════════════════
    private void doNext() {
        if (linkedList.size() == 0) return;

        // ← DSA: circular next index from linked list
        currentIndex = linkedList.nextIndex(currentIndex);

        if (displayData == users) {
            selectRow(currentIndex);
        }
        setStatus("Next: " + users[currentIndex][1]
                + "  (" + (currentIndex + 1) + " / " + linkedList.size() + ")", BTN_CYAN);
    }

    // ═══════════════════════════════════════════
    //  BACK  → return to AdminDashboard
    // ═══════════════════════════════════════════
    private void doBack() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Return to Admin Dashboard?", "Back", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new AdminDashboard();
        }
    }

    // ═══════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Object[] u : displayData) {
            tableModel.addRow(new Object[]{u[0], u[1], u[2], u[3], u[4]});
        }
    }

    private void refreshStats() {
        lblTotalUsers.setText(String.valueOf(users.length));
        lblConfirmed.setText(String.valueOf(calcConfirmedCount()));
        lblPending.setText(String.valueOf(calcPendingCount()));
    }

    private void selectRow(int index) {
        if (index >= 0 && index < table.getRowCount()) {
            table.setRowSelectionInterval(index, index);
            table.scrollRectToVisible(table.getCellRect(index, 0, true));
        }
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private int calcConfirmedCount() {
        int count = 0;
        for (Object[] u : users)
            if ("Confirmed".equals(u[4])) count++;
        return count;
    }

    private int calcPendingCount() {
        int count = 0;
        for (Object[] u : users)
            if ("Pending".equals(u[4])) count++;
        return count;
    }

    // ═══════════════════════════════════════════
    //  BUTTON FACTORY  (identical to AdminDashboard)
    // ═══════════════════════════════════════════
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_LABEL);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                new EmptyBorder(8, 16, 8, 16)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(color.brighter()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(color); }
        });
        return btn;
    }

    // ═══════════════════════════════════════════
    //  STATUS BADGE RENDERER
    //  Renders "Confirmed" in green, "Pending" in yellow
    //  with a pill-style badge — unique to this screen
    // ═══════════════════════════════════════════
    class StatusBadgeRenderer extends JPanel implements TableCellRenderer {
        private String statusText = "";
        private Color  badgeColor = BTN_YELLOW;

        public StatusBadgeRenderer() { setOpaque(true); }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
            statusText = value != null ? value.toString() : "";
            badgeColor = "Confirmed".equals(statusText) ? BTN_GREEN : BTN_YELLOW;
            setBackground(isSelected ? ROW_SELECT : (row % 2 == 0 ? ROW_EVEN : ROW_ODD));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            FontMetrics fm  = g2.getFontMetrics();
            int tw  = fm.stringWidth(statusText);
            int th  = fm.getAscent();
            int pw  = tw + 20;
            int ph  = th + 8;
            int px  = (getWidth()  - pw) / 2;
            int py  = (getHeight() - ph) / 2;

            // pill background
            g2.setColor(new Color(badgeColor.getRed(), badgeColor.getGreen(),
                    badgeColor.getBlue(), 40));
            g2.fillRoundRect(px, py, pw, ph, ph, ph);

            // pill border
            g2.setColor(badgeColor);
            g2.drawRoundRect(px, py, pw, ph, ph, ph);

            // text
            g2.setColor(badgeColor);
            g2.drawString(statusText,
                    px + (pw - tw) / 2,
                    py + ph / 2 + th / 2 - 1);
            g2.dispose();
        }
    }

    // ═══════════════════════════════════════════
    //  MAIN  (for standalone testing)
    // ═══════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserConfirmation::new);
    }
}