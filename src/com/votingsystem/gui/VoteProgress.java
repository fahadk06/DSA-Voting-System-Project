package com.votingsystem.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class VoteProgress extends JFrame {

    // ═══════════════════════════════════════════
    //  COLORS & FONTS  (same as all other forms)
    // ═══════════════════════════════════════════
    private static final Color DARK_BG    = new Color(30, 30, 47);
    private static final Color PANEL_BG   = new Color(44, 44, 64);
    private static final Color BTN_GREEN  = new Color(50, 200, 120);
    private static final Color BTN_RED    = new Color(220, 70, 70);
    private static final Color BTN_YELLOW = new Color(230, 180, 50);
    private static final Color BTN_CYAN   = new Color(50, 200, 220);
    private static final Color BTN_BLUE   = new Color(70, 130, 255);
    private static final Color TEXT_WHITE = new Color(230, 230, 255);
    private static final Color TEXT_GRAY  = new Color(150, 150, 180);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_BIG    = new Font("Segoe UI", Font.BOLD, 16);

    // ═══════════════════════════════════════════
    //  CANDIDATE DATA
    //  Format: {Name, Party, Area, Votes}
    //  In real app this comes from shared data
    // ═══════════════════════════════════════════
    private Object[][] candidates = {
            {"Muhammad Ali",  "PTI",  "Rawalpindi", 342},
            {"Sara Ahmed",    "PMLN", "Islamabad",  289},
            {"Zain ul Abdin", "PPP",  "Lahore",     198},
            {"Fatima Khan",   "MQM",  "Karachi",    415},
            {"Omar Sheikh",   "PTI",  "Peshawar",   167},
            {"Ayesha Raza",   "PMLN", "Multan",     231},
    };

    // ═══════════════════════════════════════════
    //  COMPONENTS
    // ═══════════════════════════════════════════
    private JPanel  resultsPanel;  // holds all candidate result rows
    private JLabel  statusLabel;
    private JLabel  leaderLabel;
    private JLabel  totalVotesLabel;

    // ═══════════════════════════════════════════
    //  CONSTRUCTOR
    // ═══════════════════════════════════════════
    public VoteProgress() {
        setTitle("Vote Progress - Online Voting System");
        setSize(700, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(DARK_BG);
        setLayout(new BorderLayout(0, 0));

        add(buildTopBar(),     BorderLayout.NORTH);
        add(buildCenter(),     BorderLayout.CENTER);
        add(buildBottomBar(),  BorderLayout.SOUTH);

        setVisible(true);
    }

    // ═══════════════════════════════════════════
    //  1. TOP BAR
    //     Title + subtitle
    // ═══════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(PANEL_BG);
        bar.setBorder(new EmptyBorder(18, 25, 18, 25));

        // Left: Title
        JPanel leftSide = new JPanel();
        leftSide.setBackground(PANEL_BG);
        leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Vote Progress");
        title.setFont(FONT_TITLE);
        title.setForeground(BTN_YELLOW);

        JLabel subtitle = new JLabel("Live election results — sorted by votes");
        subtitle.setFont(FONT_SMALL);
        subtitle.setForeground(TEXT_GRAY);

        leftSide.add(title);
        leftSide.add(Box.createVerticalStrut(3));
        leftSide.add(subtitle);

        // Right: Total votes info
        JPanel rightSide = new JPanel();
        rightSide.setBackground(PANEL_BG);
        rightSide.setLayout(new BoxLayout(rightSide, BoxLayout.Y_AXIS));

        JLabel totalLabel = new JLabel("Total Votes Cast");
        totalLabel.setFont(FONT_SMALL);
        totalLabel.setForeground(TEXT_GRAY);
        totalLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        totalVotesLabel = new JLabel(String.valueOf(calcTotalVotes()));
        totalVotesLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalVotesLabel.setForeground(BTN_CYAN);
        totalVotesLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightSide.add(totalLabel);
        rightSide.add(totalVotesLabel);

        bar.add(leftSide,  BorderLayout.WEST);
        bar.add(rightSide, BorderLayout.EAST);
        return bar;
    }

    // ═══════════════════════════════════════════
    //  2. CENTER
    //     Leader card on top + results list below
    // ═══════════════════════════════════════════
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setBackground(DARK_BG);
        center.setBorder(new EmptyBorder(16, 20, 10, 20));

        center.add(buildLeaderCard(),   BorderLayout.NORTH);
        center.add(buildResultsList(),  BorderLayout.CENTER);

        return center;
    }

    // ═══════════════════════════════════════════
    //  3. LEADER CARD
    //     Highlights the winning candidate
    // ═══════════════════════════════════════════
    private JPanel buildLeaderCard() {
        // Sort to find leader
        Object[][] sorted = getSortedCandidates();
        String leaderName  = (String) sorted[0][0];
        String leaderParty = (String) sorted[0][1];
        int    leaderVotes = (int)    sorted[0][3];
        int    totalVotes  = calcTotalVotes();
        int    leaderPct   = totalVotes > 0 ? leaderVotes * 100 / totalVotes : 0;

        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(new Color(55, 65, 40));  // dark green tint for winner
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BTN_GREEN, 2),
                new EmptyBorder(16, 22, 16, 22)
        ));
        card.setPreferredSize(new Dimension(0, 85));

        // Left: trophy + name
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel trophy = new JLabel("LEADING CANDIDATE");
        trophy.setFont(new Font("Segoe UI", Font.BOLD, 10));
        trophy.setForeground(BTN_GREEN);

        leaderLabel = new JLabel(leaderName + "  (" + leaderParty + ")");
        leaderLabel.setFont(FONT_BIG);
        leaderLabel.setForeground(TEXT_WHITE);

        JLabel pctLabel = new JLabel(leaderPct + "% of total votes");
        pctLabel.setFont(FONT_SMALL);
        pctLabel.setForeground(BTN_GREEN);

        leftPanel.add(trophy);
        leftPanel.add(Box.createVerticalStrut(4));
        leftPanel.add(leaderLabel);
        leftPanel.add(Box.createVerticalStrut(2));
        leftPanel.add(pctLabel);

        // Right: vote count
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel votesTitle = new JLabel("Votes");
        votesTitle.setFont(FONT_SMALL);
        votesTitle.setForeground(TEXT_GRAY);
        votesTitle.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel votesCount = new JLabel(String.valueOf(leaderVotes));
        votesCount.setFont(new Font("Segoe UI", Font.BOLD, 28));
        votesCount.setForeground(BTN_GREEN);
        votesCount.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(votesTitle);
        rightPanel.add(votesCount);

        card.add(leftPanel,  BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);
        return card;
    }

    // ═══════════════════════════════════════════
    //  4. RESULTS LIST
    //     One row per candidate, sorted by votes
    //     Each row has: Rank | Name | Party | Bar | Votes
    // ═══════════════════════════════════════════
    private JScrollPane buildResultsList() {
        resultsPanel = new JPanel();
        resultsPanel.setBackground(DARK_BG);
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));

        // Sort candidates by votes using Bubble Sort (DSA)
        Object[][] sorted = getSortedCandidates();
        int maxVotes = (int) sorted[0][3]; // highest votes after sort

        // Build one row for each candidate
        for (int i = 0; i < sorted.length; i++) {
            String name  = (String) sorted[i][0];
            String party = (String) sorted[i][1];
            String area  = (String) sorted[i][2];
            int    votes = (int)    sorted[i][3];
            int    pct   = maxVotes > 0 ? votes * 100 / maxVotes : 0;
            int    rank  = i + 1;

            resultsPanel.add(buildCandidateRow(rank, name, party, area, votes, pct));
            resultsPanel.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(resultsPanel);
        scroll.setBackground(DARK_BG);
        scroll.getViewport().setBackground(DARK_BG);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setBackground(PANEL_BG);
        return scroll;
    }

    // ═══════════════════════════════════════════
    //  ONE CANDIDATE ROW
    //  Rank | Name + Party + Area | Progress Bar | Votes
    // ═══════════════════════════════════════════
    private JPanel buildCandidateRow(int rank, String name, String party,
                                     String area, int votes, int pct) {

        // Pick color based on rank
        Color rankColor = rank == 1 ? BTN_GREEN
                : rank == 2 ? BTN_CYAN
                  : rank == 3 ? BTN_YELLOW
                    : TEXT_GRAY;

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(PANEL_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        row.setPreferredSize(new Dimension(0, 75));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(rankColor, rank == 1 ? 2 : 1),
                new EmptyBorder(10, 16, 10, 16)
        ));

        // ── LEFT: Rank number
        JLabel rankLabel = new JLabel("#" + rank, SwingConstants.CENTER);
        rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rankLabel.setForeground(rankColor);
        rankLabel.setPreferredSize(new Dimension(40, 0));

        // ── CENTER: Name, Party, Area + Progress bar
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Name + Party
        JLabel nameLabel = new JLabel(name + "  |  " + party + "  |  " + area);
        nameLabel.setFont(FONT_LABEL);
        nameLabel.setForeground(TEXT_WHITE);

        // Progress bar (custom drawn)
        JPanel progressBar = buildProgressBar(pct, rankColor);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));

        // Percentage text
        JLabel pctLabel = new JLabel(pct + "% relative to leader");
        pctLabel.setFont(FONT_SMALL);
        pctLabel.setForeground(TEXT_GRAY);

        centerPanel.add(nameLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(progressBar);
        centerPanel.add(Box.createVerticalStrut(2));
        centerPanel.add(pctLabel);

        // ── RIGHT: Vote count
        JLabel voteLabel = new JLabel(String.valueOf(votes), SwingConstants.RIGHT);
        voteLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        voteLabel.setForeground(rankColor);
        voteLabel.setPreferredSize(new Dimension(60, 0));

        JLabel votesText = new JLabel("votes", SwingConstants.RIGHT);
        votesText.setFont(FONT_SMALL);
        votesText.setForeground(TEXT_GRAY);

        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(voteLabel);
        rightPanel.add(votesText);

        row.add(rankLabel,    BorderLayout.WEST);
        row.add(centerPanel,  BorderLayout.CENTER);
        row.add(rightPanel,   BorderLayout.EAST);

        return row;
    }

    // ═══════════════════════════════════════════
    //  PROGRESS BAR  (custom drawn)
    // ═══════════════════════════════════════════
    private JPanel buildProgressBar(int pct, Color barColor) {
        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Gray background track
                g2.setColor(new Color(60, 60, 80));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Colored fill based on percentage
                int fillW = (int)(getWidth() * pct / 100.0);
                g2.setColor(barColor);
                if (fillW > 0) {
                    g2.fillRoundRect(0, 0, fillW, getHeight(), 8, 8);
                }

                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 12));
        return bar;
    }

    // ═══════════════════════════════════════════
    //  5. BOTTOM BAR
    //     Refresh + Back buttons + status
    // ═══════════════════════════════════════════
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(PANEL_BG);
        bar.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Left: status label
        statusLabel = new JLabel("Results sorted by votes (Bubble Sort)");
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(TEXT_GRAY);

        // Right: buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(PANEL_BG);

        JButton btnRefresh = createButton("Refresh", BTN_BLUE);
        JButton btnBack    = createButton("Back",     BTN_RED);

        // Refresh reloads the results panel
        btnRefresh.addActionListener(e -> {
            getContentPane().removeAll();
            add(buildTopBar(),    BorderLayout.NORTH);
            add(buildCenter(),    BorderLayout.CENTER);
            add(buildBottomBar(), BorderLayout.SOUTH);
            revalidate();
            repaint();
            setStatus("Results refreshed!", BTN_GREEN);
        });

        // Back goes to UserDashboard
        btnBack.addActionListener(e -> {
            dispose();
            //new UserDashboard("User");
        });

        btnPanel.add(btnRefresh);
        btnPanel.add(btnBack);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(btnPanel,    BorderLayout.EAST);
        return bar;
    }

    // ═══════════════════════════════════════════
    //  DSA — BUBBLE SORT (descending by votes)
    //  Sorts candidates from highest to lowest votes
    // ═══════════════════════════════════════════
    private Object[][] getSortedCandidates() {
        // Deep copy so original data is not changed
        Object[][] sorted = new Object[candidates.length][4];
        for (int i = 0; i < candidates.length; i++) {
            sorted[i] = candidates[i].clone();
        }

        // Bubble Sort — compare adjacent, swap if out of order
        int n = sorted.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if ((int) sorted[j][3] < (int) sorted[j + 1][3]) {
                    // Swap
                    Object[] temp  = sorted[j];
                    sorted[j]      = sorted[j + 1];
                    sorted[j + 1]  = temp;
                }
            }
        }
        return sorted;
    }

    // ═══════════════════════════════════════════
    //  HELPER METHODS
    // ═══════════════════════════════════════════

    // Sum all votes
    private int calcTotalVotes() {
        int total = 0;
        for (Object[] c : candidates) total += (int) c[3];
        return total;
    }

    // Update status bar text
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
        SwingUtilities.invokeLater(VoteProgress::new);
    }
}