package com.votingsystem.gui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class UserDashboard extends JFrame {

    // ═══════════════════════════════════════════
    //  COLORS & FONTS  (same as AdminDashboard)
    // ═══════════════════════════════════════════
    private static final Color DARK_BG    = new Color(30, 30, 47);
    private static final Color PANEL_BG   = new Color(44, 44, 64);
    private static final Color BTN_BLUE   = new Color(70, 130, 255);
    private static final Color BTN_GREEN  = new Color(50, 200, 120);
    private static final Color BTN_RED    = new Color(220, 70, 70);
    private static final Color BTN_YELLOW = new Color(230, 180, 50);
    private static final Color BTN_CYAN   = new Color(50, 200, 220);
    private static final Color TEXT_WHITE = new Color(230, 230, 255);
    private static final Color TEXT_GRAY  = new Color(150, 150, 180);
    private static final Color ROW_EVEN   = new Color(44, 44, 64);
    private static final Color ROW_ODD    = new Color(52, 52, 75);
    private static final Color ROW_SELECT = new Color(70, 130, 255, 80);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 12);

    // ═══════════════════════════════════════════
    //  CANDIDATE DATA
    //  Same data shared with AdminDashboard
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
    //  USER INFO  (passed from login screen)
    // ═══════════════════════════════════════════
    private String loggedInUser;   // voter's name
    private boolean hasVoted = false; // track if user already voted

    // ═══════════════════════════════════════════
    //  COMPONENTS
    // ═══════════════════════════════════════════
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JLabel statusLabel;
    private JLabel lblTotalCandidates;
    private JLabel lblTotalVotes;
    private JLabel lblLeader;
    private JLabel lblVoteStatus; // shows "Voted" or "Not Voted"

    // Linked List index for Prev/Next navigation
    private int currentIndex = 0;

    // ═══════════════════════════════════════════
    //  CONSTRUCTOR
    //  Accepts logged-in user's name from UserLogin
    // ═══════════════════════════════════════════
    public UserDashboard(String userName) {
        this.loggedInUser = userName;

        setTitle("User Dashboard - Online Voting System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(DARK_BG);

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // ═══════════════════════════════════════════
    //  1. HEADER
    //     - Title + welcome message on left
    //     - Search bar on right
    // ═══════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(new EmptyBorder(15, 25, 15, 25));

        // Left side: title + welcome
        JPanel leftSide = new JPanel();
        leftSide.setBackground(PANEL_BG);
        leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("User Dashboard");
        title.setFont(FONT_TITLE);
        title.setForeground(BTN_GREEN);

        JLabel welcome = new JLabel("Welcome, " + loggedInUser + "!");
        welcome.setFont(FONT_NORMAL);
        welcome.setForeground(TEXT_GRAY);

        leftSide.add(title);
        leftSide.add(welcome);

        // Right side: search
        JPanel searchArea = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchArea.setBackground(PANEL_BG);

        JLabel searchLbl = new JLabel("Search by Area / Name: ");
        searchLbl.setForeground(TEXT_GRAY);
        searchLbl.setFont(FONT_NORMAL);

        searchField = new JTextField(16);
        searchField.setFont(FONT_NORMAL);
        searchField.setForeground(TEXT_WHITE);
        searchField.setBackground(DARK_BG);
        searchField.setCaretColor(TEXT_WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BTN_GREEN, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));

        JButton btnSearch = createButton("Search",   BTN_BLUE);
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

        header.add(leftSide,   BorderLayout.WEST);
        header.add(searchArea, BorderLayout.EAST);
        return header;
    }

    // ═══════════════════════════════════════════
    //  2. CENTER AREA
    //     - Stats on top
    //     - Table in middle
    //     - Buttons at bottom
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
    //     Total Candidates | Total Votes | Leader | Your Status
    // ═══════════════════════════════════════════
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setBackground(DARK_BG);
        row.setPreferredSize(new Dimension(0, 90));

        lblTotalCandidates = new JLabel(String.valueOf(candidates.length));
        lblTotalVotes      = new JLabel(String.valueOf(calcTotalVotes()));
        lblLeader          = new JLabel(calcLeader());
        lblVoteStatus      = new JLabel("Not Voted");
        lblVoteStatus.setForeground(BTN_RED); // will turn green after voting

        row.add(buildStatCard("Total Candidates",  lblTotalCandidates, BTN_CYAN));
        row.add(buildStatCard("Total Votes Cast",  lblTotalVotes,      BTN_GREEN));
        row.add(buildStatCard("Leading Candidate", lblLeader,          BTN_YELLOW));
        row.add(buildStatCard("Your Vote Status",  lblVoteStatus,      BTN_RED));

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

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        // Note: color set by caller or dynamically (for vote status)

        card.add(titleLbl,   BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // ═══════════════════════════════════════════
    //  4. TABLE
    //     Columns: ID | Name | Party | Area | Votes | Vote Progress
    // ═══════════════════════════════════════════
    private JScrollPane buildTable() {
        String[] columns = {"ID", "Name", "Party", "Area", "Votes", "Vote Progress"};

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; } // read-only for user
        };

        refreshTable();

        table = new JTable(tableModel) {
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

        // Header style
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

        // Center align ID and Votes
        DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
        centerAlign.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(4).setCellRenderer(centerAlign);

        // Progress bar for vote progress column
        table.getColumnModel().getColumn(5).setCellRenderer(new ProgressBarRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(DARK_BG);
        scroll.getViewport().setBackground(ROW_EVEN);
        scroll.setBorder(BorderFactory.createLineBorder(PANEL_BG, 1));
        return scroll;
    }

    // ═══════════════════════════════════════════
    //  5. BUTTON ROW
    //     Cast Vote | View Results | Prev | Next | Logout
    // ═══════════════════════════════════════════
    private JPanel buildButtonRow() {
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        btnRow.setBackground(DARK_BG);

        JButton btnVote    = createButton("Cast My Vote",    BTN_GREEN);
        JButton btnResults = createButton("View Results",    BTN_YELLOW);
        JButton btnPrev    = createButton("< Previous",      BTN_CYAN);
        JButton btnNext    = createButton("Next >",          BTN_CYAN);
        JButton btnLogout  = createButton("Logout",          BTN_RED);

        btnVote.addActionListener(e    -> doCastVote());
        btnResults.addActionListener(e -> doViewResults());
        btnPrev.addActionListener(e    -> doPrev());
        btnNext.addActionListener(e    -> doNext());
        btnLogout.addActionListener(e  -> doLogout());

        btnRow.add(btnVote);
        btnRow.add(btnResults);
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

        statusLabel = new JLabel("Select a candidate and cast your vote.");
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

    // CAST VOTE — user selects a row and votes
    private void doCastVote() {

        // Check if user already voted
        if (hasVoted) {
            showError("You have already cast your vote!");
            return;
        }

        // Check if a candidate is selected
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Please select a candidate to vote for.");
            return;
        }

        String candidateName = (String) candidates[row][1];
        String party         = (String) candidates[row][2];

        // Confirm vote
        int confirm = JOptionPane.showConfirmDialog(this,
                "You are voting for:\n\n"
                        + "  Name:  " + candidateName + "\n"
                        + "  Party: " + party + "\n\n"
                        + "Are you sure? This cannot be undone.",
                "Confirm Vote",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Add 1 vote to that candidate
            candidates[row][4] = (int) candidates[row][4] + 1;

            // Mark user as voted
            hasVoted = true;

            // Update table and stats
            refreshTable();
            refreshStats();

            // Update vote status card
            lblVoteStatus.setText("Voted!");
            lblVoteStatus.setForeground(BTN_GREEN);

            setStatus("Vote cast for " + candidateName + " successfully!", BTN_GREEN);

            // Show success message
            JOptionPane.showMessageDialog(this,
                    "Your vote for " + candidateName + " has been recorded!",
                    "Vote Successful", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // VIEW RESULTS — shows sorted results in a dialog
    private void doViewResults() {
        // Sort candidates by votes (Bubble Sort — DSA concept)
        Object[][] sorted = copyCandidates();
        bubbleSortByVotes(sorted);

        // Build result message
        StringBuilder sb = new StringBuilder();
        sb.append("=== VOTE RESULTS (Sorted by Votes) ===\n\n");

        for (int i = 0; i < sorted.length; i++) {
            String rank  = (i + 1) + ".  ";
            String name  = (String) sorted[i][1];
            String party = (String) sorted[i][2];
            int votes    = (int) sorted[i][4];

            sb.append(rank)
                    .append(name)
                    .append("  (").append(party).append(")")
                    .append("  —  ").append(votes).append(" votes");

            if (i == 0) sb.append("  🏆 LEADING");
            sb.append("\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setBackground(PANEL_BG);
        textArea.setForeground(TEXT_WHITE);
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(420, 280));

        JOptionPane.showMessageDialog(this, scroll,
                "Election Results", JOptionPane.PLAIN_MESSAGE);
    }

    // PREVIOUS — Linked List navigation going backward
    private void doPrev() {
        if (candidates.length == 0) return;
        currentIndex = (currentIndex - 1 + candidates.length) % candidates.length;
        selectRow(currentIndex);
        setStatus("Viewing: " + candidates[currentIndex][1]
                + "  (" + (currentIndex + 1) + " / " + candidates.length + ")", BTN_CYAN);
    }

    // NEXT — Linked List navigation going forward
    private void doNext() {
        if (candidates.length == 0) return;
        currentIndex = (currentIndex + 1) % candidates.length;
        selectRow(currentIndex);
        setStatus("Viewing: " + candidates[currentIndex][1]
                + "  (" + (currentIndex + 1) + " / " + candidates.length + ")", BTN_CYAN);
    }

    // LOGOUT — go back to MainForm
    private void doLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Logout and return to main menu?",
                "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new MainForm();
        }
    }

    // SEARCH — by name or area (Binary Search concept)
    private void doSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) { refreshTable(); return; }

        tableModel.setRowCount(0);
        int maxV  = calcMaxVotes();
        int found = 0;

        for (Object[] c : candidates) {
            boolean match =
                    ((String) c[1]).toLowerCase().contains(query) || // name
                            ((String) c[3]).toLowerCase().contains(query);   // area

            if (match) {
                int pct = maxV > 0 ? (int) c[4] * 100 / maxV : 0;
                tableModel.addRow(new Object[]{c[0], c[1], c[2], c[3], c[4], pct});
                found++;
            }
        }
        setStatus("Found " + found + " result(s) for '" + query + "'",
                found > 0 ? BTN_CYAN : BTN_RED);
    }

    // ═══════════════════════════════════════════
    //  DSA — BUBBLE SORT by votes (descending)
    //  Used in View Results
    // ═══════════════════════════════════════════
    private void bubbleSortByVotes(Object[][] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if ((int) arr[j][4] < (int) arr[j + 1][4]) {
                    // Swap
                    Object[] temp = arr[j];
                    arr[j]        = arr[j + 1];
                    arr[j + 1]    = temp;
                }
            }
        }
    }

    // Deep copy of candidates array (so sorting doesn't affect original)
    private Object[][] copyCandidates() {
        Object[][] copy = new Object[candidates.length][5];
        for (int i = 0; i < candidates.length; i++) {
            copy[i] = candidates[i].clone();
        }
        return copy;
    }

    // ═══════════════════════════════════════════
    //  HELPER METHODS
    // ═══════════════════════════════════════════

    // Reload table from candidates array
    private void refreshTable() {
        tableModel.setRowCount(0);
        int maxV = calcMaxVotes();
        for (Object[] c : candidates) {
            int pct = maxV > 0 ? (int) c[4] * 100 / maxV : 0;
            tableModel.addRow(new Object[]{c[0], c[1], c[2], c[3], c[4], pct});
        }
    }

    // Update stat cards
    private void refreshStats() {
        lblTotalCandidates.setText(String.valueOf(candidates.length));
        lblTotalVotes.setText(String.valueOf(calcTotalVotes()));
        lblLeader.setText(calcLeader());
    }

    // Highlight a row in the table
    private void selectRow(int index) {
        table.setRowSelectionInterval(index, index);
        table.scrollRectToVisible(table.getCellRect(index, 0, true));
    }

    // Update status bar text
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

    // Get max votes (used for progress bar %)
    private int calcMaxVotes() {
        int max = 0;
        for (Object[] c : candidates) max = Math.max(max, (int) c[4]);
        return max;
    }

    // Get name of leading candidate
    private String calcLeader() {
        String leader = "None";
        int max = 0;
        for (Object[] c : candidates) {
            if ((int) c[4] > max) { max = (int) c[4]; leader = (String) c[1]; }
        }
        return leader;
    }

    // ═══════════════════════════════════════════
    //  BUTTON FACTORY  (same style as AdminDashboard)
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
    //  (same as AdminDashboard for consistency)
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

            int pad    = 10;
            int trackW = getWidth() - pad * 2;
            int barH   = 10;
            int y      = (getHeight() - barH) / 2;

            // Gray background track
            g2.setColor(new Color(60, 60, 80));
            g2.fillRoundRect(pad, y, trackW, barH, barH, barH);

            // Colored fill
            int fillW      = (int)(trackW * percentage / 100.0);
            Color barColor = percentage > 66 ? BTN_GREEN
                    : percentage > 33 ? BTN_YELLOW
                      : BTN_RED;
            g2.setColor(barColor);
            if (fillW > 0) g2.fillRoundRect(pad, y, fillW, barH, barH, barH);

            // Percentage text
            g2.setColor(TEXT_WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String txt = percentage + "%";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(txt,
                    getWidth() - fm.stringWidth(txt) - pad,
                    getHeight() / 2 + fm.getAscent() / 2 - 2);
            g2.dispose();
        }
    }

    // ═══════════════════════════════════════════
    //  MAIN  (test this form standalone)
    // ═══════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserDashboard("Fahad"));
    }
}