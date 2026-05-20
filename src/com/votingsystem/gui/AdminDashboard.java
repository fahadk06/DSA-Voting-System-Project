package com.votingsystem.gui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import com.votingsystem.gui.MainForm;

public class AdminDashboard extends JFrame {

    // ═══════════════════════════════════════════
    //  COLORS & FONTS
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
    //  CANDIDATE DATA  (will connect to DB later)
    //  Format: {ID, Name, Party, Area, Votes}
    // ═══════════════════════════════════════════
    private Object[][] candidates = {
            {1, "Muhammad Ali",  "PTI",  "Rawalpindi", 342},
            {2, "Sara Ahmed",    "PMLN", "Islamabad",  289},
            {3, "Zain ul Abdin", "PPP",  "Lahore",     198},
            {4, "Fatima Khan",   "MQM",  "Karachi",    415},
            {5, "Omar Sheikh",   "PTI",  "Peshawar",   167},
            {6, "Ayesha Raza",   "PMLN", "Multan",     231},
    };

    // ═══════════════════════════════════════════
    //  COMPONENTS (used across methods)
    // ═══════════════════════════════════════════
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JLabel statusLabel;
    private JLabel lblTotalCandidates;
    private JLabel lblTotalVotes;
    private JLabel lblLeader;

    // Linked List index for Prev/Next navigation
    private int currentIndex = 0;

    // ═══════════════════════════════════════════
    //  CONSTRUCTOR
    // ═══════════════════════════════════════════
    public AdminDashboard() {
        setTitle("Admin Dashboard - Online Voting System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Main layout: Header on top, content in center, status at bottom
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(DARK_BG);

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // ═══════════════════════════════════════════
    //  1. HEADER
    //     - Title on left
    //     - Search bar on right
    // ═══════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(new EmptyBorder(15, 25, 15, 25));

        // Left: Title
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(FONT_TITLE);
        title.setForeground(BTN_CYAN);

        // Right: Search field + buttons
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
                new EmptyBorder(5, 8, 5, 8)
        ));

        JButton btnSearch = createButton("Search",   BUTTON_BLUE);
        JButton btnReset  = createButton("Show All", BTN_CYAN);

        btnSearch.addActionListener(e -> doSearch());
        btnReset.addActionListener(e -> {
            searchField.setText("");
            refreshTable();
            setStatus("Showing all candidates", BTN_CYAN);
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
    //  2. CENTER AREA
    //     - Stats row on top
    //     - Table in middle
    //     - Action buttons at bottom
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
    //     Three cards: Total Candidates, Total Votes, Leader
    // ═══════════════════════════════════════════
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 15, 0));
        row.setBackground(DARK_BG);
        row.setPreferredSize(new Dimension(0, 90));

        // Labels stored so we can update them after add/remove
        lblTotalCandidates = new JLabel(String.valueOf(candidates.length));
        lblTotalVotes      = new JLabel(String.valueOf(calcTotalVotes()));
        lblLeader          = new JLabel(calcLeader());

        row.add(buildStatCard("Total Candidates",  lblTotalCandidates, BTN_CYAN));
        row.add(buildStatCard("Total Votes",       lblTotalVotes,      BTN_GREEN));
        row.add(buildStatCard("Leading Candidate", lblLeader,          BTN_YELLOW));

        return row;
    }

    // Single stat card
    private JPanel buildStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(PANEL_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                new EmptyBorder(12, 18, 12, 18)
        ));

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
    //     Columns: ID | Name | Party | Area | Votes | Progress
    // ═══════════════════════════════════════════
    private JScrollPane buildTable() {
        String[] columns = {"ID", "Name", "Party", "Area", "Votes", "Vote Progress"};

        // isCellEditable = false makes table read-only
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        refreshTable(); // load candidates into table

        table = new JTable(tableModel) {
            // Alternate row colors
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(ROW_SELECT);
                } else {
                    c.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                }
                c.setForeground(TEXT_WHITE);
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

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_LABEL);
        header.setBackground(PANEL_BG);
        header.setForeground(TEXT_GRAY);
        header.setPreferredSize(new Dimension(0, 38));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(180);

        // Center-align ID and Votes columns
        DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
        centerAlign.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(4).setCellRenderer(centerAlign);

        // Custom progress bar for last column
        table.getColumnModel().getColumn(5).setCellRenderer(new ProgressBarRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(DARK_BG);
        scroll.getViewport().setBackground(ROW_EVEN);
        scroll.setBorder(BorderFactory.createLineBorder(PANEL_BG, 1));
        return scroll;
    }

    // ═══════════════════════════════════════════
    //  5. BUTTON ROW
    //     Add | Update | Remove | Prev | Next | Logout
    // ═══════════════════════════════════════════
    private JPanel buildButtonRow() {
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        btnRow.setBackground(DARK_BG);

        JButton btnAdd    = createButton("+ Add",      BTN_GREEN);
        JButton btnUpdate = createButton("Edit",       BTN_YELLOW);
        JButton btnRemove = createButton("Remove",     BTN_RED);
        JButton btnPrev   = createButton("< Previous", BTN_CYAN);
        JButton btnNext   = createButton("Next >",     BTN_CYAN);
        JButton btnLogout = createButton("Logout",     BTN_RED);

        btnAdd.addActionListener(e    -> doAdd());
        btnUpdate.addActionListener(e -> doUpdate());
        btnRemove.addActionListener(e -> doRemove());
        btnPrev.addActionListener(e   -> doPrev());
        btnNext.addActionListener(e   -> doNext());
        btnLogout.addActionListener(e -> doLogout());

        btnRow.add(btnAdd);
        btnRow.add(btnUpdate);
        btnRow.add(btnRemove);
        btnRow.add(Box.createHorizontalStrut(20));
        btnRow.add(btnPrev);
        btnRow.add(btnNext);
        btnRow.add(Box.createHorizontalStrut(20));
        btnRow.add(btnLogout);

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
    //  ACTIONS
    // ═══════════════════════════════════════════

    // ADD
    private void doAdd() {
        JTextField fName  = new JTextField(15);
        JTextField fParty = new JTextField(15);
        JTextField fArea  = new JTextField(15);

        Object[] fields = {"Name:", fName, "Party:", fParty, "Area:", fArea};

        int result = JOptionPane.showConfirmDialog(this, fields,
                "Add New Candidate", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String name  = fName.getText().trim();
            String party = fParty.getText().trim();
            String area  = fArea.getText().trim();

            if (name.isEmpty() || party.isEmpty() || area.isEmpty()) {
                showError("Please fill all fields."); return;
            }

            // Expand array by 1 and add new candidate
            Object[][] newArr = new Object[candidates.length + 1][5];
            System.arraycopy(candidates, 0, newArr, 0, candidates.length);
            newArr[candidates.length] = new Object[]{candidates.length + 1, name, party, area, 0};
            candidates = newArr;

            refreshTable();
            refreshStats();
            setStatus("Candidate '" + name + "' added.", BTN_GREEN);
        }
    }

    // UPDATE
    private void doUpdate() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Select a candidate to update."); return; }

        JTextField fName  = new JTextField((String) candidates[row][1], 15);
        JTextField fParty = new JTextField((String) candidates[row][2], 15);
        JTextField fArea  = new JTextField((String) candidates[row][3], 15);

        Object[] fields = {"Name:", fName, "Party:", fParty, "Area:", fArea};

        int result = JOptionPane.showConfirmDialog(this, fields,
                "Update Candidate", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            candidates[row][1] = fName.getText().trim();
            candidates[row][2] = fParty.getText().trim();
            candidates[row][3] = fArea.getText().trim();
            refreshTable();
            refreshStats();
            setStatus("Candidate updated.", BTN_YELLOW);
        }
    }

    // REMOVE
    private void doRemove() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Select a candidate to remove."); return; }

        String name = (String) candidates[row][1];
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove '" + name + "'?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Shrink array by 1, skipping removed row
            Object[][] newArr = new Object[candidates.length - 1][5];
            int i = 0;
            for (int j = 0; j < candidates.length; j++) {
                if (j != row) newArr[i++] = candidates[j];
            }
            candidates = newArr;
            refreshTable();
            refreshStats();
            setStatus("'" + name + "' removed.", BTN_RED);
        }
    }

    // SEARCH (by name, party, or area)
    private void doSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) { refreshTable(); return; }

        tableModel.setRowCount(0);
        int maxV  = calcMaxVotes();
        int found = 0;

        for (Object[] c : candidates) {
            boolean match =
                    ((String) c[1]).toLowerCase().contains(query) ||
                            ((String) c[2]).toLowerCase().contains(query) ||
                            ((String) c[3]).toLowerCase().contains(query);

            if (match) {
                int pct = maxV > 0 ? (int) c[4] * 100 / maxV : 0;
                tableModel.addRow(new Object[]{c[0], c[1], c[2], c[3], c[4], pct});
                found++;
            }
        }
        setStatus("Found " + found + " result(s) for '" + query + "'",
                found > 0 ? BTN_CYAN : BTN_RED);
    }

    // PREV — Linked List navigation going backward
    private void doPrev() {
        if (candidates.length == 0) return;
        currentIndex = (currentIndex - 1 + candidates.length) % candidates.length;
        selectRow(currentIndex);
        setStatus("Previous: " + candidates[currentIndex][1]
                + "  (" + (currentIndex + 1) + " / " + candidates.length + ")", BTN_CYAN);
    }

    // NEXT — Linked List navigation going forward
    private void doNext() {
        if (candidates.length == 0) return;
        currentIndex = (currentIndex + 1) % candidates.length;
        selectRow(currentIndex);
        setStatus("Next: " + candidates[currentIndex][1]
                + "  (" + (currentIndex + 1) + " / " + candidates.length + ")", BTN_CYAN);
    }

    // LOGOUT
    private void doLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Logout and return to main menu?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new MainForm();
        }
    }

    // ═══════════════════════════════════════════
    //  HELPER METHODS
    // ═══════════════════════════════════════════

    // Reload all rows in the table from candidates array
    private void refreshTable() {
        tableModel.setRowCount(0);
        int maxV = calcMaxVotes();
        for (Object[] c : candidates) {
            int pct = maxV > 0 ? (int) c[4] * 100 / maxV : 0;
            tableModel.addRow(new Object[]{c[0], c[1], c[2], c[3], c[4], pct});
        }
    }

    // Update the 3 stat cards
    private void refreshStats() {
        lblTotalCandidates.setText(String.valueOf(candidates.length));
        lblTotalVotes.setText(String.valueOf(calcTotalVotes()));
        lblLeader.setText(calcLeader());
    }

    // Highlight and scroll to a specific row
    private void selectRow(int index) {
        table.setRowSelectionInterval(index, index);
        table.scrollRectToVisible(table.getCellRect(index, 0, true));
    }

    // Update status bar
    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    // Show error popup
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Sum all votes
    private int calcTotalVotes() {
        int total = 0;
        for (Object[] c : candidates) total += (int) c[4];
        return total;
    }

    // Get max votes (for progress bar calculation)
    private int calcMaxVotes() {
        int max = 0;
        for (Object[] c : candidates) max = Math.max(max, (int) c[4]);
        return max;
    }

    // Get name of candidate with most votes
    private String calcLeader() {
        String leader = "None";
        int max = 0;
        for (Object[] c : candidates) {
            if ((int) c[4] > max) { max = (int) c[4]; leader = (String) c[1]; }
        }
        return leader;
    }

    // ═══════════════════════════════════════════
    //  BUTTON FACTORY
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
    //  PROGRESS BAR RENDERER
    //  Used in the "Vote Progress" column
    // ═══════════════════════════════════════════
    class ProgressBarRenderer extends JPanel implements TableCellRenderer {

        private int percentage = 0;

        public ProgressBarRenderer() { setOpaque(true); }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
            percentage = (value instanceof Integer) ? (Integer) value : 0;
            setBackground(isSelected ? ROW_SELECT : (row % 2 == 0 ? ROW_EVEN : ROW_ODD));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int pad   = 10;
            int trackW = getWidth() - pad * 2;
            int barH  = 10;
            int y     = (getHeight() - barH) / 2;

            // Background track (gray)
            g2.setColor(new Color(60, 60, 80));
            g2.fillRoundRect(pad, y, trackW, barH, barH, barH);

            // Colored fill based on percentage
            int fillW     = (int)(trackW * percentage / 100.0);
            Color barColor = percentage > 66 ? BTN_GREEN
                    : percentage > 33 ? BTN_YELLOW
                      : BTN_RED;
            g2.setColor(barColor);
            if (fillW > 0) g2.fillRoundRect(pad, y, fillW, barH, barH, barH);

            // Percentage text on right side
            g2.setColor(TEXT_WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String txt = percentage + "%";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(txt, getWidth() - fm.stringWidth(txt) - pad,
                    getHeight() / 2 + fm.getAscent() / 2 - 2);
            g2.dispose();
        }
    }

    // ═══════════════════════════════════════════
    //  MAIN  (run this to test standalone)
    // ═══════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminDashboard::new);
    }
}