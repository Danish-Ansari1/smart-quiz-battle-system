import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class DashboardFrame extends JFrame {
    private Player player;
    private JComboBox<String> categoryDropdown;
    private JComboBox<String> filterCategoryBox;
    private JTextField searchField;
    private JCheckBox chkMyAttempts;
    private JCheckBox chkAutoSync;
    private JTable leaderboardTable;
    private DefaultTableModel tableModel;
    private Timer autoSyncTimer;

    // KPI & Profile Labels
    private JLabel lblTotalBattlesVal, lblChampionVal, lblBestScoreVal, lblPlatformAvgVal;
    private JLabel lblRankBadge, lblPersonalAvg, lblStudentStatsSub;
    private JProgressBar accuracyMeter;

    // Enterprise Color Palette
    private final Color BG_COLOR = new Color(241, 245, 249);        // Slate 100
    private final Color CARD_BG = Color.WHITE;
    private final Color HEADER_BG = new Color(15, 23, 42);          // Slate 900
    private final Color PROFILE_BG = new Color(30, 41, 59);         // Slate 800
    private final Color PRIMARY_COLOR = new Color(37, 99, 235);     // Royal Blue
    private final Color PRIMARY_HOVER = new Color(29, 78, 216);
    private final Color SUCCESS_COLOR = new Color(22, 163, 74);     // Emerald Green
    private final Color SUCCESS_HOVER = new Color(21, 128, 61);
    private final Color WARNING_COLOR = new Color(217, 119, 6);     // Amber Gold
    private final Color DANGER_COLOR = new Color(220, 38, 38);      // Crimson Red
    private final Color PURPLE_COLOR = new Color(124, 58, 237);     // Violet
    private final Color TEXT_DARK = new Color(15, 23, 42);
    private final Color TEXT_MUTED = new Color(100, 116, 139);
    private final Color BORDER_COLOR = new Color(226, 232, 240);

    public DashboardFrame(Player player) {
        this.player = player;

        setTitle("Student Dashboard - Smart Quiz Battle System");
        setSize(1220, 780);
        setMinimumSize(new Dimension(1000, 680));
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full Screen Ready
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout(0, 0));

        // ================= 1. TOP ENTERPRISE NAVBAR =================
        JPanel topNav = new JPanel(new BorderLayout());
        topNav.setBackground(HEADER_BG);
        topNav.setBorder(new EmptyBorder(14, 28, 14, 28));

        // Left: Avatar + Welcome Title
        JPanel navLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        navLeft.setOpaque(false);

        JPanel avatarCircle = createAvatarBadge(player.getUsername());

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel lblWelcome = new JLabel("Welcome, " + player.getUsername() + "  |  Student Arena");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblWelcome.setForeground(Color.WHITE);

        JLabel lblSubStatus = new JLabel("● Online Session Active  •  Smart Assessment Portal");
        lblSubStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubStatus.setForeground(new Color(74, 222, 128)); // Soft Green Dot

        titleBox.add(lblWelcome);
        titleBox.add(lblSubStatus);

        navLeft.add(avatarCircle);
        navLeft.add(titleBox);

        // Right: Role Tag + Logout Button
        JPanel navRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 4));
        navRight.setOpaque(false);

        JLabel lblRolePill = new JLabel(" CANDIDATE PORTAL ");
        lblRolePill.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRolePill.setForeground(new Color(147, 197, 253));
        lblRolePill.setBorder(new CompoundBorder(
                new LineBorder(new Color(59, 130, 246), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        JButton btnLogout = createInteractiveButton("Sign Out →", DANGER_COLOR, new Color(185, 28, 28), 115, 34);
        btnLogout.addActionListener(e -> {
            if (autoSyncTimer != null) autoSyncTimer.stop();
            dispose();
            new LoginSignUpFrame().setVisible(true);
        });

        navRight.add(lblRolePill);
        navRight.add(btnLogout);

        topNav.add(navLeft, BorderLayout.WEST);
        topNav.add(navRight, BorderLayout.EAST);
        add(topNav, BorderLayout.NORTH);

        // ================= 2. MAIN CONTENT CONTAINER =================
        JPanel mainContainer = new JPanel(new BorderLayout(0, 14));
        mainContainer.setBackground(BG_COLOR);
        mainContainer.setBorder(new EmptyBorder(16, 24, 18, 24));

        // Top Stack inside Main Container (Profile Banner + KPI Row + Battle Launcher)
        JPanel upperStack = new JPanel();
        upperStack.setLayout(new BoxLayout(upperStack, BoxLayout.Y_AXIS));
        upperStack.setBackground(BG_COLOR);

        // 2A. Interactive Student Gamified Profile Card
        JPanel profileStrip = createStudentProfileBanner();
        upperStack.add(profileStrip);
        upperStack.add(Box.createRigidArea(new Dimension(0, 12)));

        // 2B. 4 Executive KPI Cards
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        kpiGrid.setBackground(BG_COLOR);
        kpiGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 82));

        lblTotalBattlesVal = new JLabel("0");
        lblChampionVal = new JLabel("N/A");
        lblBestScoreVal = new JLabel("0 Marks");
        lblPlatformAvgVal = new JLabel("0.0%");

        kpiGrid.add(createKpiCard("TOTAL BATTLES PLAYED", lblTotalBattlesVal, PRIMARY_COLOR));
        kpiGrid.add(createKpiCard("CURRENT CHAMPION", lblChampionVal, WARNING_COLOR));
        kpiGrid.add(createKpiCard("YOUR BEST SCORE", lblBestScoreVal, SUCCESS_COLOR));
        kpiGrid.add(createKpiCard("PLATFORM AVERAGE", lblPlatformAvgVal, PURPLE_COLOR));

        upperStack.add(kpiGrid);
        upperStack.add(Box.createRigidArea(new Dimension(0, 12)));

        // 2C. Start New Quiz Battle Action Bar
        JPanel launchCard = createBattleLauncherCard();
        upperStack.add(launchCard);

        mainContainer.add(upperStack, BorderLayout.NORTH);

        // ================= 3. LEADERBOARD & ANALYTICS TABLE SECTION =================
        JPanel tableCard = new JPanel(new BorderLayout(0, 10));
        tableCard.setBackground(CARD_BG);
        tableCard.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(14, 18, 14, 18)
        ));

        // Filter & Search Toolbar
        JPanel filterBar = new JPanel(new BorderLayout(10, 0));
        filterBar.setBackground(CARD_BG);

        JPanel filterLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterLeft.setBackground(CARD_BG);

        JLabel lblTableTitle = new JLabel("★ Live Global Leaderboard & Attempt History");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTableTitle.setForeground(TEXT_DARK);

        JLabel lblSearch = new JLabel("   Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSearch.setForeground(TEXT_MUTED);

        searchField = new JTextField(14);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(new CompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadDashboardData();
            }
        });

        JLabel lblCatFilter = new JLabel("Category:");
        lblCatFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCatFilter.setForeground(TEXT_MUTED);

        filterCategoryBox = new JComboBox<>();
        filterCategoryBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCategoryBox.setBackground(Color.WHITE);
        filterCategoryBox.addItem("All Categories");
        List<String> cats = DatabaseDAO.getCategories();
        for (String c : cats) filterCategoryBox.addItem(c);
        filterCategoryBox.addActionListener(e -> loadDashboardData());

        chkMyAttempts = new JCheckBox("My Attempts Only");
        chkMyAttempts.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkMyAttempts.setForeground(TEXT_DARK);
        chkMyAttempts.setBackground(CARD_BG);
        chkMyAttempts.setFocusPainted(false);
        chkMyAttempts.addActionListener(e -> loadDashboardData());

        chkAutoSync = new JCheckBox("Live Auto-Sync (10s)");
        chkAutoSync.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkAutoSync.setForeground(PRIMARY_COLOR);
        chkAutoSync.setBackground(CARD_BG);
        chkAutoSync.setFocusPainted(false);
        chkAutoSync.addActionListener(e -> {
            if (chkAutoSync.isSelected()) autoSyncTimer.start();
            else autoSyncTimer.stop();
        });

        filterLeft.add(lblTableTitle);
        filterLeft.add(lblSearch);
        filterLeft.add(searchField);
        filterLeft.add(lblCatFilter);
        filterLeft.add(filterCategoryBox);
        filterLeft.add(chkMyAttempts);
        filterLeft.add(chkAutoSync);

        // Right Toolbar Buttons (Refresh + Export CSV)
        JPanel filterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterRight.setBackground(CARD_BG);

        JButton btnRefresh = createInteractiveButton("↻ Refresh", PRIMARY_COLOR, PRIMARY_HOVER, 105, 32);
        btnRefresh.addActionListener(e -> loadDashboardData());

        JButton btnExportCsv = createInteractiveButton("⬇ Export CSV", new Color(71, 85, 105), new Color(51, 65, 85), 115, 32);
        btnExportCsv.addActionListener(e -> exportLeaderboardToCSV());

        filterRight.add(btnRefresh);
        filterRight.add(btnExportCsv);

        filterBar.add(filterLeft, BorderLayout.WEST);
        filterBar.add(filterRight, BorderLayout.EAST);
        tableCard.add(filterBar, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"Rank / ID", "Student Name", "Category", "Total Q", "Correct", "Wrong", "Score", "Accuracy (%)", "Attempt Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setRowHeight(36);
        leaderboardTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        leaderboardTable.setGridColor(new Color(226, 232, 240));
        leaderboardTable.setSelectionBackground(new Color(219, 234, 254));
        leaderboardTable.setSelectionForeground(TEXT_DARK);
        leaderboardTable.setShowVerticalLines(true);
        leaderboardTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leaderboardTable.setToolTipText("Double-click any row to inspect detailed performance card");

        // Table Header Styling
        JTableHeader header = leaderboardTable.getTableHeader();
        header.setPreferredSize(new Dimension(100, 38));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(HEADER_BG);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(new MatteBorder(0, 0, 2, 1, new Color(51, 65, 85)));
                return lbl;
            }
        });

        // Custom Cell Renderer for Medals, (YOU) Tag & Color-Coded Percentages
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(column == 1 || column == 2 ? SwingConstants.LEFT : SwingConstants.CENTER);

                String studentCell = String.valueOf(table.getValueAt(row, 1));
                boolean isCurrentUser = studentCell.contains("(YOU)");

                if (!isSelected) {
                    if (row == 0) c.setBackground(new Color(254, 249, 195));      // Soft Gold Tint
                    else if (row == 1) c.setBackground(new Color(241, 245, 249)); // Soft Silver Tint
                    else if (row == 2) c.setBackground(new Color(255, 237, 213)); // Soft Bronze Tint
                    else if (isCurrentUser) c.setBackground(new Color(239, 246, 255)); // Light Blue for Current User
                    else c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }

                // Default Font & Color
                setFont(new Font("Segoe UI", isCurrentUser || column == 0 ? Font.BOLD : Font.PLAIN, 13));
                setForeground(TEXT_DARK);

                // Highlight Accuracy Column
                if (column == 7 && value != null) {
                    String pctStr = value.toString().replace("%", "").trim();
                    try {
                        double pct = Double.parseDouble(pctStr);
                        setFont(new Font("Segoe UI", Font.BOLD, 13));
                        if (pct >= 75) setForeground(SUCCESS_COLOR);
                        else if (pct >= 50) setForeground(WARNING_COLOR);
                        else setForeground(DANGER_COLOR);
                    } catch (NumberFormatException ignored) {}
                }

                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        };

        for (int i = 0; i < leaderboardTable.getColumnCount(); i++) {
            leaderboardTable.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        // Double-Click Row to Open Performance Summary Modal
        leaderboardTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && leaderboardTable.getSelectedRow() != -1) {
                    showAttemptDetailsModal(leaderboardTable.getSelectedRow());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Subtle Footer Hint Bar
        JPanel tableFooter = new JPanel(new BorderLayout());
        tableFooter.setBackground(CARD_BG);
        JLabel lblHint = new JLabel("💡 Pro Tip: Double-click on any leaderboard row to inspect the candidate's attempt summary card.");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHint.setForeground(TEXT_MUTED);
        tableFooter.add(lblHint, BorderLayout.WEST);
        tableCard.add(tableFooter, BorderLayout.SOUTH);

        mainContainer.add(tableCard, BorderLayout.CENTER);
        add(mainContainer, BorderLayout.CENTER);

        // Auto-Sync Timer (every 10 seconds)
        autoSyncTimer = new Timer(10000, e -> loadDashboardData());

        // Initial Load
        loadDashboardData();
    }

    // ================= UI BUILDER HELPERS =================

    private JPanel createAvatarBadge(String name) {
        String initials = name != null && !name.trim().isEmpty()
                ? name.trim().substring(0, 1).toUpperCase()
                : "S";

        JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_COLOR);
                g2.fillOval(0, 0, 42, 42);
                g2.setColor(new Color(147, 197, 253));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(1, 1, 40, 40);
            }
        };
        badge.setPreferredSize(new Dimension(42, 42));
        badge.setOpaque(false);
        badge.setLayout(new GridBagLayout());

        JLabel lblInit = new JLabel(initials);
        lblInit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblInit.setForeground(Color.WHITE);
        badge.add(lblInit);
        return badge;
    }

    private JPanel createStudentProfileBanner() {
        JPanel banner = new JPanel(new BorderLayout(20, 0));
        banner.setBackground(PROFILE_BG);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        banner.setBorder(new CompoundBorder(
                new LineBorder(new Color(51, 65, 85), 1, true),
                new EmptyBorder(12, 22, 12, 22)
        ));

        // Left: Student Identity & Dynamic Rank Title
        JPanel leftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 2));
        leftInfo.setOpaque(false);

        lblRankBadge = new JLabel(" ★ RISING CONTENDER ");
        lblRankBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRankBadge.setForeground(new Color(253, 224, 71)); // Gold text
        lblRankBadge.setOpaque(true);
        lblRankBadge.setBackground(new Color(51, 65, 85));
        lblRankBadge.setBorder(new CompoundBorder(
                new LineBorder(new Color(234, 179, 8), 1, true),
                new EmptyBorder(5, 12, 5, 12)
        ));

        JPanel textCol = new JPanel();
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.setOpaque(false);

        JLabel lblProfileHeading = new JLabel("Student Performance Profile: " + player.getUsername());
        lblProfileHeading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblProfileHeading.setForeground(Color.WHITE);

        lblStudentStatsSub = new JLabel("Attempts: 0  |  Personal Best: 0 Marks  |  Keep practicing to reach #1!");
        lblStudentStatsSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStudentStatsSub.setForeground(new Color(148, 163, 184));

        textCol.add(lblProfileHeading);
        textCol.add(Box.createRigidArea(new Dimension(0, 2)));
        textCol.add(lblStudentStatsSub);

        leftInfo.add(lblRankBadge);
        leftInfo.add(textCol);

        // Right: Personal Accuracy Progress Meter
        JPanel rightMeter = new JPanel();
        rightMeter.setLayout(new BoxLayout(rightMeter, BoxLayout.Y_AXIS));
        rightMeter.setOpaque(false);
        rightMeter.setPreferredSize(new Dimension(260, 44));

        lblPersonalAvg = new JLabel("Your Overall Accuracy: 0.0%");
        lblPersonalAvg.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPersonalAvg.setForeground(new Color(226, 232, 240));
        lblPersonalAvg.setAlignmentX(Component.LEFT_ALIGNMENT);

        accuracyMeter = new JProgressBar(0, 100);
        accuracyMeter.setValue(0);
        accuracyMeter.setStringPainted(false);
        accuracyMeter.setForeground(SUCCESS_COLOR);
        accuracyMeter.setBackground(new Color(51, 65, 85));
        accuracyMeter.setBorderPainted(false);
        accuracyMeter.setPreferredSize(new Dimension(250, 10));
        accuracyMeter.setMaximumSize(new Dimension(250, 10));
        accuracyMeter.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightMeter.add(lblPersonalAvg);
        rightMeter.add(Box.createRigidArea(new Dimension(0, 6)));
        rightMeter.add(accuracyMeter);

        banner.add(leftInfo, BorderLayout.WEST);
        banner.add(rightMeter, BorderLayout.EAST);
        return banner;
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new MatteBorder(0, 4, 0, 0, accentColor),
                new CompoundBorder(
                        new LineBorder(BORDER_COLOR, 1, false),
                        new EmptyBorder(12, 16, 12, 16)
                )
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 21));
        valueLabel.setForeground(TEXT_DARK);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createBattleLauncherCard() {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(CARD_BG);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 20, 12, 20)
        ));

        // Left: Topic Selection & Start Button
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 2));
        leftControls.setBackground(CARD_BG);

        JLabel lblIconTitle = new JLabel("► Start New Quiz Battle:");
        lblIconTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblIconTitle.setForeground(PRIMARY_COLOR);

        JLabel lblSelect = new JLabel("Select Topic Category:");
        lblSelect.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSelect.setForeground(TEXT_DARK);

        categoryDropdown = new JComboBox<>();
        categoryDropdown.setFont(new Font("Segoe UI", Font.BOLD, 13));
        categoryDropdown.setBackground(new Color(248, 250, 252));
        categoryDropdown.setPreferredSize(new Dimension(210, 36));

        List<String> categories = DatabaseDAO.getCategories();
        if (categories.isEmpty()) {
            categoryDropdown.addItem("Java Programming");
            categoryDropdown.addItem("Computer Science");
        } else {
            for (String cat : categories) {
                categoryDropdown.addItem(cat);
            }
        }

        JButton btnStartQuiz = createInteractiveButton("Start Quiz Battle Now ►", SUCCESS_COLOR, SUCCESS_HOVER, 210, 36);
        btnStartQuiz.addActionListener(e -> startSelectedQuiz());

        leftControls.add(lblIconTitle);
        leftControls.add(lblSelect);
        leftControls.add(categoryDropdown);
        leftControls.add(btnStartQuiz);

        // Right: Exam Rules Info Badge
        JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        rightInfo.setBackground(CARD_BG);

        JLabel lblRules = new JLabel("⏱ 15s / Question   •   🔀 Shuffled Questions & Options   •   📄 Instant PDF Report");
        lblRules.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRules.setForeground(TEXT_MUTED);
        rightInfo.add(lblRules);

        card.add(leftControls, BorderLayout.WEST);
        card.add(rightInfo, BorderLayout.EAST);
        return card;
    }

    private JButton createInteractiveButton(String text, Color normalBg, Color hoverBg, int width, int height) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(normalBg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, height));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBg);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(normalBg);
            }
        });
        return btn;
    }

    // ================= DATA LOADING & ANALYTICS ENGINE =================

    private void startSelectedQuiz() {
        String selectedCategory = (String) categoryDropdown.getSelectedItem();
        if (selectedCategory == null || selectedCategory.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a quiz category first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Question> questions = DatabaseDAO.getQuestionsByCategory(selectedCategory);
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions found in '" + selectedCategory + "' category!", "Empty Category", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (autoSyncTimer != null) autoSyncTimer.stop();
        dispose();
        new QuizEngineFrame(player, selectedCategory, questions).setVisible(true);
    }

    private void loadDashboardData() {
        tableModel.setRowCount(0);
        List<String[]> allResults = DatabaseDAO.getLeaderboard();

        int totalBattles = allResults.size();
        String championName = "No Attempts Yet";
        int myBestScore = 0;
        int myAttemptsCount = 0;
        double myTotalPct = 0.0;
        double platformTotalPct = 0.0;

        if (!allResults.isEmpty()) {
            String[] topRow = allResults.get(0);
            championName = topRow[1] + " (" + topRow[6] + " Marks)";
        }

        String searchText = searchField.getText().trim().toLowerCase();
        String selectedCatFilter = (String) filterCategoryBox.getSelectedItem();
        boolean onlyMine = chkMyAttempts.isSelected();

        int rankCounter = 1;
        for (String[] row : allResults) {
            // Expected columns: 0:id, 1:username, 2:category, 3:total_q, 4:correct, 5:wrong, 6:score, 7:percentage, 8:date
            String uname = row[1];
            String cat = row[2];
            int scoreVal = 0;
            double pctVal = 0.0;

            try {
                scoreVal = Integer.parseInt(row[6]);
                pctVal = Double.parseDouble(row[7].replace("%", "").trim());
            } catch (Exception ignored) {}

            platformTotalPct += pctVal;

            boolean isMe = uname.equalsIgnoreCase(player.getUsername());
            if (isMe) {
                myAttemptsCount++;
                myTotalPct += pctVal;
                if (scoreVal > myBestScore) {
                    myBestScore = scoreVal;
                }
            }

            // Apply UI Filters
            boolean matchesSearch = searchText.isEmpty()
                    || uname.toLowerCase().contains(searchText)
                    || cat.toLowerCase().contains(searchText);
            boolean matchesCat = selectedCatFilter == null
                    || selectedCatFilter.equals("All Categories")
                    || cat.equalsIgnoreCase(selectedCatFilter);
            boolean matchesMine = !onlyMine || isMe;

            if (matchesSearch && matchesCat && matchesMine) {
                String rankBadge;
                if (rankCounter == 1) rankBadge = "★ 1st";
                else if (rankCounter == 2) rankBadge = "★ 2nd";
                else if (rankCounter == 3) rankBadge = "★ 3rd";
                else rankBadge = "#" + rankCounter;

                String displayUser = isMe ? uname + " ★ (YOU)" : uname;
                String formattedPct = String.format("%.2f%%", pctVal);

                tableModel.addRow(new Object[]{
                        rankBadge, displayUser, cat, row[3], row[4], row[5], row[6], formattedPct, row[8]
                });
            }
            rankCounter++;
        }

        // Update Top KPI Cards
        double platformAvg = totalBattles > 0 ? (platformTotalPct / totalBattles) : 0.0;
        lblTotalBattlesVal.setText(String.valueOf(totalBattles));
        lblChampionVal.setText(championName);
        lblBestScoreVal.setText(myBestScore + " Marks");
        lblPlatformAvgVal.setText(String.format("%.1f%%", platformAvg));

        // Update Gamified Profile Strip
        double myAvgPct = myAttemptsCount > 0 ? (myTotalPct / myAttemptsCount) : 0.0;
        lblPersonalAvg.setText(String.format("Your Overall Accuracy: %.1f%%", myAvgPct));
        accuracyMeter.setValue((int) Math.round(myAvgPct));

        if (myAvgPct >= 80) {
            lblRankBadge.setText(" ★ GRANDMASTER TIER ");
            accuracyMeter.setForeground(SUCCESS_COLOR);
        } else if (myAvgPct >= 50) {
            lblRankBadge.setText(" ▲ PRO CONTENDER ");
            accuracyMeter.setForeground(WARNING_COLOR);
        } else if (myAttemptsCount > 0) {
            lblRankBadge.setText(" ● APPRENTICE TIER ");
            accuracyMeter.setForeground(PRIMARY_COLOR);
        } else {
            lblRankBadge.setText(" ★ NEW CHALLENGER ");
            accuracyMeter.setForeground(PRIMARY_COLOR);
        }

        lblStudentStatsSub.setText("Your Attempts: " + myAttemptsCount
                + "   |   Personal Best: " + myBestScore + " Marks"
                + "   |   Average Accuracy: " + String.format("%.1f%%", myAvgPct));
    }

    // ================= INTERACTIVE DRILL-DOWN MODAL & CSV EXPORT =================

    private void showAttemptDetailsModal(int row) {
        String rank = String.valueOf(leaderboardTable.getValueAt(row, 0));
        String student = String.valueOf(leaderboardTable.getValueAt(row, 1));
        String category = String.valueOf(leaderboardTable.getValueAt(row, 2));
        String totalQ = String.valueOf(leaderboardTable.getValueAt(row, 3));
        String correct = String.valueOf(leaderboardTable.getValueAt(row, 4));
        String wrong = String.valueOf(leaderboardTable.getValueAt(row, 5));
        String score = String.valueOf(leaderboardTable.getValueAt(row, 6));
        String accuracy = String.valueOf(leaderboardTable.getValueAt(row, 7));
        String date = String.valueOf(leaderboardTable.getValueAt(row, 8));

        String html = "<html><body style='width: 340px; font-family: Segoe UI; padding: 6px;'>"
                + "<h3 style='margin:0; color:#1e293b;'>Attempt Performance Breakdown</h3>"
                + "<hr style='border:1px solid #cbd5e1;'>"
                + "<p><b>Leaderboard Rank:</b> " + rank + "</p>"
                + "<p><b>Candidate:</b> " + student + "</p>"
                + "<p><b>Topic Category:</b> " + category + "</p>"
                + "<p><b>Total Questions:</b> " + totalQ + "</p>"
                + "<p style='color:#16a34a;'><b>Correct Answers:</b> " + correct + "</p>"
                + "<p style='color:#dc2626;'><b>Wrong / Skipped:</b> " + wrong + "</p>"
                + "<p style='color:#2563eb; font-size:14px;'><b>Final Score:</b> " + score + " Marks (" + accuracy + ")</p>"
                + "<p style='color:#64748b; font-size:11px;'><b>Timestamp:</b> " + date + "</p>"
                + "</body></html>";

        JOptionPane.showMessageDialog(this, new JLabel(html), "Candidate Attempt Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportLeaderboardToCSV() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data available to export!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Leaderboard Data as CSV");
        chooser.setSelectedFile(new File("SmartQuiz_Leaderboard_" + player.getUsername().replaceAll("\\s+", "_") + ".csv"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            try (FileWriter fw = new FileWriter(file)) {
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    fw.write("\"" + tableModel.getColumnName(i) + "\"" + (i < tableModel.getColumnCount() - 1 ? "," : "\n"));
                }
                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    for (int c = 0; c < tableModel.getColumnCount(); c++) {
                        String val = String.valueOf(tableModel.getValueAt(r, c)).replace("\"", "\"\"");
                        fw.write("\"" + val + "\"" + (c < tableModel.getColumnCount() - 1 ? "," : "\n"));
                    }
                }
                JOptionPane.showMessageDialog(this, "Leaderboard exported successfully to:\n" + file.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}