import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// --- 1. Pro Leaderboard Table Cell Renderer for Gold, Silver, Bronze & Striped Rows ---
class ProLeaderboardRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (column == 0 || column >= 3) {
            setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        if (!isSelected) {
            if (row == 0) {
                c.setBackground(new Color(254, 240, 138)); // Rank 1: Gold
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else if (row == 1) {
                c.setBackground(new Color(226, 232, 240)); // Rank 2: Silver
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else if (row == 2) {
                c.setBackground(new Color(254, 215, 170)); // Rank 3: Bronze
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            }
            c.setForeground(new Color(30, 41, 59));

            // Highlight Correct (Green) and Wrong (Red) counts
            if (column == 4) {
                c.setForeground(new Color(22, 163, 74));
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else if (column == 5) {
                c.setForeground(new Color(220, 38, 38));
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
            }
        }
        return c;
    }
}

// --- 2. Answer Sheet Renderer for CORRECT (Green), WRONG (Red), SKIPPED (Amber) ---
class AnswerLogRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String status = String.valueOf(table.getValueAt(row, 7));

        if (column == 4 || column == 5 || column == 6) {
            setHorizontalAlignment(SwingConstants.LEFT);
        } else {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        if (!isSelected) {
            c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
            c.setForeground(new Color(30, 41, 59));
            c.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            if (column == 5) { // Student's Answer
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                c.setForeground("CORRECT".equalsIgnoreCase(status) ? new Color(22, 163, 74) : new Color(220, 38, 38));
            } else if (column == 6) { // Correct Answer
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                c.setForeground(new Color(22, 163, 74));
            } else if (column == 7) { // Status Badge
                c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                if ("CORRECT".equalsIgnoreCase(status)) {
                    c.setBackground(new Color(220, 252, 231));
                    c.setForeground(new Color(21, 128, 61));
                } else if ("SKIPPED".equalsIgnoreCase(status)) {
                    c.setBackground(new Color(254, 243, 199));
                    c.setForeground(new Color(180, 83, 9));
                } else {
                    c.setBackground(new Color(254, 226, 226));
                    c.setForeground(new Color(185, 28, 28));
                }
            }
        }
        return c;
    }
}

public class AdminFrame extends JFrame {
    private JTable studentResultsTable;
    private TableRowSorter<TableModel> rowSorter;

    private JTable answerLogsTable;
    private TableRowSorter<TableModel> logsRowSorter;

    private JLabel lblTotalAttemptsVal, lblTopScorerVal, lblAvgAccuracyVal;

    // Production Color Palette
    private final Color BG_COLOR = new Color(241, 245, 249);
    private final Color HEADER_BG = new Color(15, 23, 42);         // Deep Navy Slate
    private final Color PRIMARY_COLOR = new Color(37, 99, 235);    // Royal Blue
    private final Color SUCCESS_COLOR = new Color(22, 163, 74);    // Green
    private final Color DANGER_COLOR = new Color(220, 38, 38);     // Red
    private final Color WARNING_COLOR = new Color(217, 119, 6);    // Amber
    private final Color PURPLE_ACCENT = new Color(124, 58, 237);   // Purple
    private final Color TEXT_DARK = new Color(30, 41, 59);
    private final Color TEXT_MUTED = new Color(100, 116, 139);
    private final Color BORDER_COLOR = new Color(203, 213, 225);

    public AdminFrame(Player admin) {
        setTitle("Teacher / Admin Panel - Smart Quiz Battle System");
        setSize(1250, 750);
        setMinimumSize(new Dimension(980, 620));
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full Screen Production Mode
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        // ================= 1. TOP EXECUTIVE HEADER =================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(HEADER_BG);
        topPanel.setBorder(new EmptyBorder(14, 28, 14, 28));

        JLabel lblTitle = new JLabel("Teacher Admin Panel (Logged in: " + admin.getUsername() + ")");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JButton btnLogout = new JButton("Logout");
        styleProButton(btnLogout, DANGER_COLOR);
        btnLogout.setPreferredSize(new Dimension(105, 36));
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginSignUpFrame().setVisible(true);
        });

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(btnLogout, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ================= 2. TABS FOR TEACHER =================
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBorder(new EmptyBorder(10, 15, 10, 15));

        tabbedPane.addTab("  📊 Live Records  ", createLiveRecordsTab());
        tabbedPane.addTab("  📝 Student Answer Sheets (Correct / Wrong)  ", createStudentAnswerLogsTab());
        tabbedPane.addTab("  ➕ Add New Quiz Question  ", createAddQuestionTab());

        add(tabbedPane, BorderLayout.CENTER);

        updateKpiSummary();
    }

    // =========================================================================
    // TAB 1: LIVE RECORDS & KPI DASHBOARD
    // =========================================================================
    private JPanel createLiveRecordsTab() {
        JPanel resultsPanel = new JPanel(new BorderLayout(0, 14));
        resultsPanel.setBackground(BG_COLOR);
        resultsPanel.setBorder(new EmptyBorder(15, 18, 15, 18));

        // Top Container (KPI Cards + Search/Action Bar)
        JPanel northWrapper = new JPanel(new BorderLayout(0, 14));
        northWrapper.setBackground(BG_COLOR);

        JPanel kpiGrid = new JPanel(new GridLayout(1, 3, 18, 0));
        kpiGrid.setBackground(BG_COLOR);

        lblTotalAttemptsVal = new JLabel("0");
        lblTopScorerVal = new JLabel("N/A");
        lblAvgAccuracyVal = new JLabel("0.0%");

        kpiGrid.add(createKpiCard("TOTAL QUIZ ATTEMPTS", lblTotalAttemptsVal, PRIMARY_COLOR));
        kpiGrid.add(createKpiCard("TOP PERFORMER (RANK #1)", lblTopScorerVal, SUCCESS_COLOR));
        kpiGrid.add(createKpiCard("CLASS AVERAGE ACCURACY", lblAvgAccuracyVal, WARNING_COLOR));

        northWrapper.add(kpiGrid, BorderLayout.NORTH);

        // Control Bar
        JPanel controlSubPanel = new JPanel(new BorderLayout());
        controlSubPanel.setBackground(Color.WHITE);
        controlSubPanel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));

        JLabel lblTip = new JLabel("💡 Tip: Double-click any student row (or click 'Inspect Student Answers') to see which questions they got Correct & Wrong.");
        lblTip.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblTip.setForeground(PRIMARY_COLOR);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightControls.setOpaque(false);

        JTextField txtSearch = new JTextField(16);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(200, 34));
        txtSearch.setToolTipText("Search student name or category...");

        JButton btnInspect = new JButton("🔍 Inspect Student Answers");
        styleProButton(btnInspect, PURPLE_ACCENT);
        btnInspect.setPreferredSize(new Dimension(210, 34));

        JButton btnRefresh = new JButton("🔄 Refresh Records");
        styleProButton(btnRefresh, PRIMARY_COLOR);
        btnRefresh.setPreferredSize(new Dimension(160, 34));

        JLabel lblSearch = new JLabel("🔍 Search Student/Category:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));

        rightControls.add(lblSearch);
        rightControls.add(txtSearch);
        rightControls.add(btnInspect);
        rightControls.add(btnRefresh);

        controlSubPanel.add(lblTip, BorderLayout.WEST);
        controlSubPanel.add(rightControls, BorderLayout.EAST);
        northWrapper.add(controlSubPanel, BorderLayout.SOUTH);

        resultsPanel.add(northWrapper, BorderLayout.NORTH);

        // Leaderboard Table Setup
        studentResultsTable = new JTable(DatabaseDAO.getLeaderboardModel());
        styleTableAppearance(studentResultsTable);
        applyLeaderboardRenderer();

        rowSorter = new TableRowSorter<>(studentResultsTable.getModel());
        studentResultsTable.setRowSorter(rowSorter);

        // Double-click on student row to open their Correct/Wrong answer popup
        studentResultsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && studentResultsTable.getSelectedRow() != -1) {
                    openStudentDrilldownPopup();
                }
            }
        });

        btnInspect.addActionListener(e -> openStudentDrilldownPopup());

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = txtSearch.getText().trim();
                if (text.isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        btnRefresh.addActionListener(e -> {
            refreshAllTables();
            JOptionPane.showMessageDialog(this, "Records and Answer Sheets refreshed successfully!", "Live Update", JOptionPane.INFORMATION_MESSAGE);
        });

        JScrollPane scrollPane = new JScrollPane(studentResultsTable);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        return resultsPanel;
    }

    // =========================================================================
    // TAB 2: DEDICATED SECTION FOR STUDENT CORRECT / WRONG QUESTIONS
    // =========================================================================
    private JPanel createStudentAnswerLogsTab() {
        JPanel logsPanel = new JPanel(new BorderLayout(0, 14));
        logsPanel.setBackground(BG_COLOR);
        logsPanel.setBorder(new EmptyBorder(15, 18, 15, 18));

        // Top Filter & Action Bar
        JPanel filterBar = new JPanel(new BorderLayout());
        filterBar.setBackground(Color.WHITE);
        filterBar.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 18, 12, 18)
        ));

        JLabel lblHeading = new JLabel("📋 Complete Student Question-by-Question Audit (Correct / Wrong / Skipped)");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHeading.setForeground(TEXT_DARK);

        JPanel rightFilterBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightFilterBox.setOpaque(false);

        JTextField txtSearchLogs = new JTextField(16);
        txtSearchLogs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearchLogs.setPreferredSize(new Dimension(200, 34));

        JButton btnOnlyCorrect = new JButton("✔ Only Correct");
        styleProButton(btnOnlyCorrect, SUCCESS_COLOR);
        btnOnlyCorrect.setPreferredSize(new Dimension(130, 34));

        JButton btnOnlyWrong = new JButton("✖ Only Wrong");
        styleProButton(btnOnlyWrong, DANGER_COLOR);
        btnOnlyWrong.setPreferredSize(new Dimension(130, 34));

        JButton btnShowAll = new JButton("Show All");
        styleProButton(btnShowAll, PRIMARY_COLOR);
        btnShowAll.setPreferredSize(new Dimension(105, 34));

        JButton btnRefreshLogs = new JButton("🔄 Refresh");
        styleProButton(btnRefreshLogs, new Color(71, 85, 105));
        btnRefreshLogs.setPreferredSize(new Dimension(110, 34));

        JLabel lblSearchLogs = new JLabel("🔍 Search Student / Question:");
        lblSearchLogs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        rightFilterBox.add(lblSearchLogs);
        rightFilterBox.add(txtSearchLogs);
        rightFilterBox.add(btnOnlyCorrect);
        rightFilterBox.add(btnOnlyWrong);
        rightFilterBox.add(btnShowAll);
        rightFilterBox.add(btnRefreshLogs);

        filterBar.add(lblHeading, BorderLayout.WEST);
        filterBar.add(rightFilterBox, BorderLayout.EAST);
        logsPanel.add(filterBar, BorderLayout.NORTH);

        // Answer Logs Table Setup
        answerLogsTable = new JTable(DatabaseDAO.getStudentAnswerLogsModel());
        styleTableAppearance(answerLogsTable);
        applyAnswerLogsRenderer(answerLogsTable);

        logsRowSorter = new TableRowSorter<>(answerLogsTable.getModel());
        answerLogsTable.setRowSorter(logsRowSorter);

        // Live Search & Quick Filter Buttons
        txtSearchLogs.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = txtSearchLogs.getText().trim();
                if (text.isEmpty()) {
                    logsRowSorter.setRowFilter(null);
                } else {
                    logsRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        btnOnlyCorrect.addActionListener(e -> {
            txtSearchLogs.setText("CORRECT");
            logsRowSorter.setRowFilter(RowFilter.regexFilter("(?i)^CORRECT$", 7));
        });

        btnOnlyWrong.addActionListener(e -> {
            txtSearchLogs.setText("WRONG");
            logsRowSorter.setRowFilter(RowFilter.regexFilter("(?i)^WRONG$", 7));
        });

        btnShowAll.addActionListener(e -> {
            txtSearchLogs.setText("");
            logsRowSorter.setRowFilter(null);
        });

        btnRefreshLogs.addActionListener(e -> refreshAllTables());

        JScrollPane scrollPane = new JScrollPane(answerLogsTable);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        logsPanel.add(scrollPane, BorderLayout.CENTER);

        return logsPanel;
    }

    // =========================================================================
    // TAB 3: ADD NEW QUIZ QUESTION (CENTERED PRODUCTION CARD)
    // =========================================================================
    private JPanel createAddQuestionTab() {
        JPanel outerWrapper = new JPanel(new GridBagLayout());
        outerWrapper.setBackground(BG_COLOR);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(680, 520));
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(28, 38, 28, 38)
        ));

        JLabel lblFormTitle = new JLabel("Add New Question to Question Bank");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblFormTitle.setForeground(PRIMARY_COLOR);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblFormTitle);
        card.add(Box.createRigidArea(new Dimension(0, 18)));

        JComboBox<String> catCombo = new JComboBox<>(new String[]{"Java Programming", "Computer Science", "General Knowledge"});
        catCombo.setEditable(true);
        catCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        catCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        catCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtQuestion = createStyledTextField();
        JTextField txtOptA = createStyledTextField();
        JTextField txtOptB = createStyledTextField();
        JTextField txtOptC = createStyledTextField();
        JTextField txtOptD = createStyledTextField();

        JComboBox<String> correctCombo = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        correctCombo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        correctCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        correctCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(createFieldLabel("Select or Type Category:"));
        card.add(catCombo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        card.add(createFieldLabel("Question Text:"));
        card.add(txtQuestion);
        card.add(Box.createRigidArea(new Dimension(0, 14)));

        JPanel optionsGrid = new JPanel(new GridLayout(2, 2, 16, 12));
        optionsGrid.setBackground(Color.WHITE);
        optionsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        optionsGrid.add(createLabeledBox("Option A:", txtOptA));
        optionsGrid.add(createLabeledBox("Option B:", txtOptB));
        optionsGrid.add(createLabeledBox("Option C:", txtOptC));
        optionsGrid.add(createLabeledBox("Option D:", txtOptD));

        card.add(optionsGrid);
        card.add(Box.createRigidArea(new Dimension(0, 14)));

        card.add(createFieldLabel("Correct Option (A / B / C / D):"));
        card.add(correctCombo);
        card.add(Box.createRigidArea(new Dimension(0, 24)));

        JButton btnSaveQ = new JButton("Add Question to Database ➕");
        styleProButton(btnSaveQ, SUCCESS_COLOR);
        btnSaveQ.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSaveQ.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnSaveQ.setPreferredSize(new Dimension(300, 42));
        btnSaveQ.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnSaveQ.addActionListener(e -> {
            String cat = String.valueOf(catCombo.getSelectedItem()).trim();
            String qText = txtQuestion.getText().trim();
            String a = txtOptA.getText().trim();
            String b = txtOptB.getText().trim();
            String c = txtOptC.getText().trim();
            String d = txtOptD.getText().trim();
            String corr = (String) correctCombo.getSelectedItem();

            if (cat.isEmpty() || qText.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in the question and all 4 options!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = DatabaseDAO.addQuestion(cat, qText, a, b, c, d, corr);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Question added successfully!");
                txtQuestion.setText("");
                txtOptA.setText("");
                txtOptB.setText("");
                txtOptC.setText("");
                txtOptD.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Error adding question!");
            }
        });

        card.add(btnSaveQ);
        outerWrapper.add(card, new GridBagConstraints());
        return outerWrapper;
    }

    // =========================================================================
    // STUDENT DRILLDOWN POPUP (DOUBLE CLICK ANY STUDENT ROW IN TAB 1)
    // =========================================================================
    private void openStudentDrilldownPopup() {
        int viewRow = studentResultsTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student row from the table first!", "Select Student", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = studentResultsTable.convertRowIndexToModel(viewRow);
        String studentName = String.valueOf(studentResultsTable.getModel().getValueAt(modelRow, 1));
        String category = String.valueOf(studentResultsTable.getModel().getValueAt(modelRow, 2));

        JDialog popup = new JDialog(this, "Student Answer Sheet - " + studentName + " (" + category + ")", true);
        popup.setSize(1020, 560);
        popup.setLocationRelativeTo(this);
        popup.setLayout(new BorderLayout());

        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(HEADER_BG);
        banner.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel lblBanner = new JLabel("📝 Detailed Correct & Wrong Questions for: " + studentName + "   |   Category: " + category);
        lblBanner.setForeground(Color.WHITE);
        lblBanner.setFont(new Font("Segoe UI", Font.BOLD, 17));
        banner.add(lblBanner, BorderLayout.WEST);
        popup.add(banner, BorderLayout.NORTH);

        DefaultTableModel fullLogsModel = DatabaseDAO.getStudentAnswerLogsModel();
        JTable popupTable = new JTable(fullLogsModel);
        styleTableAppearance(popupTable);
        applyAnswerLogsRenderer(popupTable);

        // Filter popup to show only the selected student's questions
        TableRowSorter<TableModel> popupSorter = new TableRowSorter<>(fullLogsModel);
        popupTable.setRowSorter(popupSorter);
        popupSorter.setRowFilter(RowFilter.regexFilter("(?i)^" + java.util.regex.Pattern.quote(studentName) + "$", 1));

        popup.add(new JScrollPane(popupTable), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        footer.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Close Window");
        styleProButton(btnClose, PRIMARY_COLOR);
        btnClose.setPreferredSize(new Dimension(140, 36));
        btnClose.addActionListener(e -> popup.dispose());
        footer.add(btnClose);
        popup.add(footer, BorderLayout.SOUTH);

        popup.setVisible(true);
    }

    // =========================================================================
    // HELPERS & STYLING
    // =========================================================================
    private void refreshAllTables() {
        // Refresh Tab 1
        studentResultsTable.setModel(DatabaseDAO.getLeaderboardModel());
        rowSorter = new TableRowSorter<>(studentResultsTable.getModel());
        studentResultsTable.setRowSorter(rowSorter);
        applyLeaderboardRenderer();

        // Refresh Tab 2
        answerLogsTable.setModel(DatabaseDAO.getStudentAnswerLogsModel());
        logsRowSorter = new TableRowSorter<>(answerLogsTable.getModel());
        answerLogsTable.setRowSorter(logsRowSorter);
        applyAnswerLogsRenderer(answerLogsTable);

        updateKpiSummary();
    }

    private void updateKpiSummary() {
        TableModel model = studentResultsTable.getModel();
        int total = model.getRowCount();
        lblTotalAttemptsVal.setText(String.valueOf(total));

        if (total > 0) {
            String topName = String.valueOf(model.getValueAt(0, 1));
            String topScore = String.valueOf(model.getValueAt(0, 6));
            lblTopScorerVal.setText(topName + " (" + topScore + " Marks)");

            double sumPct = 0;
            for (int i = 0; i < total; i++) {
                String pctStr = String.valueOf(model.getValueAt(i, 7)).replace("%", "").trim();
                try {
                    sumPct += Double.parseDouble(pctStr);
                } catch (NumberFormatException ignored) {}
            }
            lblAvgAccuracyVal.setText(String.format("%.2f%%", sumPct / total));
        } else {
            lblTopScorerVal.setText("N/A");
            lblAvgAccuracyVal.setText("0.0%");
        }
    }

    private void applyLeaderboardRenderer() {
        ProLeaderboardRenderer renderer = new ProLeaderboardRenderer();
        for (int i = 0; i < studentResultsTable.getColumnCount(); i++) {
            studentResultsTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private void applyAnswerLogsRenderer(JTable table) {
        AnswerLogRenderer renderer = new AnswerLogRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        if (table.getColumnCount() >= 9) {
            table.getColumnModel().getColumn(0).setPreferredWidth(60);
            table.getColumnModel().getColumn(1).setPreferredWidth(110);
            table.getColumnModel().getColumn(2).setPreferredWidth(130);
            table.getColumnModel().getColumn(3).setPreferredWidth(55);
            table.getColumnModel().getColumn(4).setPreferredWidth(310);
            table.getColumnModel().getColumn(5).setPreferredWidth(180);
            table.getColumnModel().getColumn(6).setPreferredWidth(180);
            table.getColumnModel().getColumn(7).setPreferredWidth(95);
            table.getColumnModel().getColumn(8).setPreferredWidth(145);
        }
    }

    private void styleTableAppearance(JTable table) {
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_DARK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(30, 41, 59));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));
    }

    private JPanel createKpiCard(String title, JLabel valLabel, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(14, 20, 14, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valLabel.setForeground(accent);

        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(valLabel);
        return card;
    }

    private void styleProButton(JButton btn, Color bgCol) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgCol);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tf.setPreferredSize(new Dimension(400, 38));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return tf;
    }

    private JPanel createLabeledBox(String labelText, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.add(createFieldLabel(labelText));
        p.add(Box.createRigidArea(new Dimension(0, 4)));
        p.add(field);
        return p;
    }
}