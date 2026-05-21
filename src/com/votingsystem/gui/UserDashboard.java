package com.votingsystem.gui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserDashboard extends JFrame {

    // ═══════════════════════════════════════════
    //  COLORS & FONTS
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
    //  USER INFO
    // ═══════════════════════════════════════════
    private int     loggedInUserId;
    private String  loggedInUser;
    private boolean hasVoted;

    // ═══════════════════════════════════════════
    //  CANDIDATE DATA  ← loaded from DB
    // ═══════════════════════════════════════════
    private Object[][] candidates = {};

    // ═══════════════════════════════════════════
    //  COMPONENTS
    // ═══════════════════════════════════════════
    private DefaultTableModel tableModel;
    private JTable            table;
    private JTextField        searchField;
    private JLabel            statusLabel;
    private JLabel            lblTotalCandidates;
    private JLabel            lblTotalVotes;
    private JLabel            lblLeader;
    private JLabel            lblVoteStatus;

    private int currentIndex = 0;

    // ═══════════════════════════════════════════
    //  CONSTRUCTOR
    // ═══════════════════════════════════════════
    public UserDashboard(int userId, String userName, boolean voted) {
        this.loggedInUserId = userId;
        this.loggedInUser   = userName;
        this.hasVoted       = voted;

        setTitle("User Dashboard - Online Voting System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(DARK_BG);

        loadCandidatesFromDB();   // ← load real data first

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // ═══════════════════════════════════════════
    //  DB — LOAD CANDIDATES
    // ═══════════════════════════════════════════
    private void loadCandidatesFromDB() {
        try {
            Connection conn = com.votingsystem.database.DBConnection.getConnection();
            if (conn == null) return;

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT id, name, party, area, vote_count FROM candidates ORDER BY id");

            java.util.List<Object[]> list = new java.util.ArrayList<>();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("party"),
                        rs.getString("area"),
                        rs.getInt("vote_count")
                });
            }
            candidates = list.toArray(new Object[0][]);

        } catch (SQLException e) {
            e.printStackTrace();
            candidates = new Object[][]{};
        }
    }

    // ═══════════════════════════════════════════
    //  1. HEADER
    // ═══════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(new EmptyBorder(15, 25, 15, 25));

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
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setBackground(DARK_BG);
        row.setPreferredSize(new Dimension(0, 90));

        lblTotalCandidates = new JLabel(String.valueOf(candidates.length));
        lblTotalVotes      = new JLabel(String.valueOf(calcTotalVotes()));
        lblLeader          = new JLabel(calcLeader());

        lblVoteStatus = new JLabel(hasVoted ? "Voted!" : "Not Voted");
        lblVoteStatus.setForeground(hasVoted ? BTN_GREEN : BTN_RED);

        row.add(buildStatCard("Total Candidates",  lblTotalCandidates, BTN_CYAN));
        row.add(buildStatCard("Total Votes Cast",  lblTotalVotes,      BTN_GREEN));
        row.add(buildStatCard("Leading Candidate", lblLeader,          BTN_YELLOW));
        row.add(buildStatCard("Your Vote Status",  lblVoteStatus,      hasVoted ? BTN_GREEN : BTN_RED));

        return row;
    }

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

        card.add(titleLbl,   BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // ═══════════════════════════════════════════
    //  4. TABLE
    // ═══════════════════════════════════════════
    private JScrollPane buildTable() {
        String[] columns = {"ID", "Name", "Party", "Area", "Votes", "Vote Progress"};

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
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

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_LABEL);
        header.setBackground(PANEL_BG);
        header.setForeground(TEXT_GRAY);
        header.setPreferredSize(new Dimension(0, 38));

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(180);

        DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
        centerAlign.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(4).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(5).setCellRenderer(new ProgressBarRenderer());

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

        JButton btnVote    = createButton("Cast My Vote", BTN_GREEN);
        JButton btnResults = createButton("View Results", BTN_YELLOW);
        JButton btnPrev    = createButton("< Previous",   BTN_CYAN);
        JButton btnNext    = createButton("Next >",       BTN_CYAN);
        JButton btnLogout  = createButton("Logout",       BTN_RED);

        btnVote.addActionListener(e    -> doCastVote());
        btnResults.addActionListener(e -> doViewResults());  // ← ONLY CHANGE
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
    //  CAST VOTE  ← saves to DB
    // ═══════════════════════════════════════════
    private void doCastVote() {
        if (hasVoted) {
            showError("You have already cast your vote!");
            return;
        }

        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Please select a candidate to vote for.");
            return;
        }

        int    candidateId   = (int)    candidates[row][0];
        String candidateName = (String) candidates[row][1];
        String party         = (String) candidates[row][2];

        int confirm = JOptionPane.showConfirmDialog(this,
                "You are voting for:\n\n"
                        + "  Name:  " + candidateName + "\n"
                        + "  Party: " + party + "\n\n"
                        + "Are you sure? This cannot be undone.",
                "Confirm Vote", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = com.votingsystem.database.DBConnection.getConnection();
                if (conn == null) { showError("DB connection failed."); return; }

                PreparedStatement ps1 = conn.prepareStatement(
                        "UPDATE candidates SET vote_count = vote_count + 1 WHERE id = ?");
                ps1.setInt(1, candidateId);
                ps1.executeUpdate();

                PreparedStatement ps2 = conn.prepareStatement(
                        "UPDATE users SET has_voted = TRUE WHERE id = ?");
                ps2.setInt(1, loggedInUserId);
                ps2.executeUpdate();

                PreparedStatement ps3 = conn.prepareStatement(
                        "INSERT INTO votes (user_id, candidate_id) VALUES (?, ?)");
                ps3.setInt(1, loggedInUserId);
                ps3.setInt(2, candidateId);
                ps3.executeUpdate();

                candidates[row][4] = (int) candidates[row][4] + 1;
                hasVoted = true;

                refreshTable();
                refreshStats();

                lblVoteStatus.setText("Voted!");
                lblVoteStatus.setForeground(BTN_GREEN);
                setStatus("Vote cast for " + candidateName + " successfully!", BTN_GREEN);

                JOptionPane.showMessageDialog(this,
                        "Your vote for " + candidateName + " has been recorded!",
                        "Vote Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                e.printStackTrace();
                showError("Database error: " + e.getMessage());
            }
        }
    }

    // ═══════════════════════════════════════════
    //  VIEW RESULTS  ← ONLY CHANGE: opens VoteProgress
    // ═══════════════════════════════════════════
    private void doViewResults() {
        dispose();
        new VoteProgress(loggedInUserId, loggedInUser, hasVoted);
    }

    // ═══════════════════════════════════════════
    //  PREV / NEXT  navigation
    // ═══════════════════════════════════════════
    private void doPrev() {
        if (candidates.length == 0) return;
        currentIndex = (currentIndex - 1 + candidates.length) % candidates.length;
        selectRow(currentIndex);
        setStatus("Viewing: " + candidates[currentIndex][1]
                + "  (" + (currentIndex + 1) + " / " + candidates.length + ")", BTN_CYAN);
    }

    private void doNext() {
        if (candidates.length == 0) return;
        currentIndex = (currentIndex + 1) % candidates.length;
        selectRow(currentIndex);
        setStatus("Viewing: " + candidates[currentIndex][1]
                + "  (" + (currentIndex + 1) + " / " + candidates.length + ")", BTN_CYAN);
    }

    private void doLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Logout and return to main menu?",
                "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new MainForm();
        }
    }

    // ═══════════════════════════════════════════
    //  SEARCH
    // ═══════════════════════════════════════════
    private void doSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) { refreshTable(); return; }

        tableModel.setRowCount(0);
        int maxV  = calcMaxVotes();
        int found = 0;

        for (Object[] c : candidates) {
            boolean match =
                    ((String) c[1]).toLowerCase().contains(query) ||
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

    // ═══════════════════════════════════════════
    //  BUBBLE SORT — DSA
    // ═══════════════════════════════════════════
    private void bubbleSortByVotes(Object[][] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++)
                if ((int) arr[j][4] < (int) arr[j + 1][4]) {
                    Object[] temp = arr[j];
                    arr[j]        = arr[j + 1];
                    arr[j + 1]    = temp;
                }
    }

    private Object[][] copyCandidates() {
        Object[][] copy = new Object[candidates.length][5];
        for (int i = 0; i < candidates.length; i++) copy[i] = candidates[i].clone();
        return copy;
    }

    // ═══════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════
    private void refreshTable() {
        tableModel.setRowCount(0);
        int maxV = calcMaxVotes();
        for (Object[] c : candidates) {
            int pct = maxV > 0 ? (int) c[4] * 100 / maxV : 0;
            tableModel.addRow(new Object[]{c[0], c[1], c[2], c[3], c[4], pct});
        }
    }

    private void refreshStats() {
        lblTotalCandidates.setText(String.valueOf(candidates.length));
        lblTotalVotes.setText(String.valueOf(calcTotalVotes()));
        lblLeader.setText(calcLeader());
    }

    private void selectRow(int index) {
        table.setRowSelectionInterval(index, index);
        table.scrollRectToVisible(table.getCellRect(index, 0, true));
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private int calcTotalVotes() {
        int total = 0;
        for (Object[] c : candidates) total += (int) c[4];
        return total;
    }

    private int calcMaxVotes() {
        int max = 0;
        for (Object[] c : candidates) max = Math.max(max, (int) c[4]);
        return max;
    }

    private String calcLeader() {
        String leader = "None";
        int max = 0;
        for (Object[] c : candidates) {
            if ((int) c[4] > max) { max = (int) c[4]; leader = (String) c[1]; }
        }
        return leader;
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

    // ═══════════════════════════════════════════
    //  PROGRESS BAR RENDERER
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

            g2.setColor(new Color(60, 60, 80));
            g2.fillRoundRect(pad, y, trackW, barH, barH, barH);

            int fillW      = (int)(trackW * percentage / 100.0);
            Color barColor = percentage > 66 ? BTN_GREEN
                    : percentage > 33 ? BTN_YELLOW : BTN_RED;
            g2.setColor(barColor);
            if (fillW > 0) g2.fillRoundRect(pad, y, fillW, barH, barH, barH);

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
    //  MAIN
    // ═══════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserDashboard(1, "Fahad", false));
    }
}