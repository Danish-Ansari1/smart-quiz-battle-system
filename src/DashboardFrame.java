import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// --- Enterprise Leaderboard Cell Renderer (Medals, Current User Highlight & Color Coding) ---
class DashboardLeaderboardRenderer extends DefaultTableCellRenderer {
    private String currentUsername;

    public DashboardLeaderboardRenderer(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Convert view row to model row so colors stay accurate even when searching/filtering
        int modelRow = table.convertRowIndexToModel(row);
        String rowStudent = String.valueOf(table.getModel().getValueAt(modelRow, 1));
        boolean isCurrentUser = rowStudent.equalsIgnoreCase(currentUsername);

        // Alignment: Rank and numeric columns centered, Student Name & Category left-aligned
        if (column == 0 || column >= 3) {
            setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        // Format Rank Column with Medals (Column 0)
        if (column == 0 && value != null) {
            String rankStr = String.valueOf(value);
            if (modelRow == 0) setText("🥇 " + rankStr + "st");
            else if (modelRow == 1) setText("🥈 " + rankStr + "nd");
            else if (modelRow == 2) setText("🥉 " + rankStr + "rd");
            else setText("#" + rankStr);
        }

        // Add "(YOU)" badge to the logged-in student's name (Column 1)
        if (column == 1 && isCurrentUser) {
            setText(rowStudent + " ⭐ (YOU)");
        }

        // Background & Font Styling
        if (!isSelected) {
            c.setForeground(new Color(44, 62, 80));

            if (isCurrentUser) {
                // Highlight logged-in student's rows in soft cyan/blue
                c.setBackground(new Color(214, 234, 248));
                c.setFont(new Font("Arial", Font.BOLD, 13));
            } else if (modelRow == 0) {
                c.setBackground(new Color(255, 243, 176)); // Rank 1: Soft Gold
                c.setFont(new Font("Arial", Font.BOLD, 13));
            } else if (modelRow == 1) {
                c.setBackground(new Color(232, 234, 237)); // Rank 2: Soft Silver
                c.setFont(new Font("Arial", Font.BOLD, 13));
            } else if (modelRow == 2) {
                c.setBackground(new Color(245, 222, 199)); // Rank 3: Soft Bronze
                c.setFont(new Font("Arial", Font.BOLD, 13));
            } else {
                // Striped alternating rows
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                c.setFont(new Font("Arial", Font.PLAIN, 12));
            }

            // Color-code Percentage Column (Column 7) based on performance
            if (column == 7 && value != null) {
                try {
                    double pct = Double.parseDouble(String.valueOf(value).replace("%", "").trim());
                    c.setFont(new Font("Arial", Font.BOLD, 12));
                    if (pct >= 75.0) {
                        c.setForeground(new Color(39, 174, 96)); // High Score: Green
                    } else if (pct >= 50.0) {
                        c.setForeground(new Color(211, 84, 0));  // Average Score: Orange
                    } else {
                        c.setForeground(new Color(192, 57, 43)); // Low Score: Red
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return c;
    }
}

public class DashboardFrame extends JFrame {
    private Player player;
    private JComboBox<String> categoryBox;
    private JTable leaderboardTable;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // Filter Controls & KPI Labels
    private JTextField txtSearch;
    private JComboBox<String> filterCategoryBox;
    private JCheckBox chkOnlyMe;
    private JCheckBox chkAutoSync;
    private Timer autoRefreshTimer;

    private JLabel lblTotalAttemptsVal;
    private JLabel lblTopScorerVal;
    private JLabel lblMyBestVal;
    private JLabel lblAvgScoreVal;

    public DashboardFrame(Player player) {
        this.player = player;
        setTitle("Student Dashboard - Smart Quiz Battle System");
        setSize(980, 660);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        // 1. Top Navigation Header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 41, 59));
        topPanel.setBorder(new EmptyBorder(12, 24, 12, 24));

        JLabel lblWelcome = new JLabel("🎓 Welcome, " + player.getUsername() + "  |  Student Arena");
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnLogout = new JButton("Logout 🚪");
        btnLogout.setFont(new Font("Arial", Font.BOLD, 12));
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            if (autoRefreshTimer != null) autoRefreshTimer.stop();
            dispose();
            new LoginSignUpFrame().setVisible(true);
        });

        topPanel.add(lblWelcome, BorderLayout.WEST);
        topPanel.add(btnLogout, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 2. Main Center Container
        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setBackground(new Color(241, 245, 249));
        centerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // --- Upper Section: KPI Summary Cards + Quiz Launcher ---
        JPanel upperSection = new JPanel(new BorderLayout(10, 10));
        upperSection.setOpaque(false);

        // KPI Analytics Row (4 Cards)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        statsPanel.setOpaque(false);

        lblTotalAttemptsVal = new JLabel("0");
        lblTopScorerVal = new JLabel("N/A");
        lblMyBestVal = new JLabel("0 Marks");
        lblAvgScoreVal = new JLabel("0.0%");

        statsPanel.add(createStatCard("📊 Total Battles", lblTotalAttemptsVal, new Color(41, 128, 185)));
        statsPanel.add(createStatCard("🏆 Current Champion", lblTopScorerVal, new Color(243, 156, 18)));
        statsPanel.add(createStatCard("⭐ Your Best Score", lblMyBestVal, new Color(39, 174, 96)));
        statsPanel.add(createStatCard("📈 Platform Average", lblAvgScoreVal, new Color(142, 68, 173)));

        upperSection.add(statsPanel, BorderLayout.NORTH);

        // Quiz Setup Bar
        JPanel setupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        setupPanel.setBackground(Color.WHITE);
        setupPanel.setBorder(BorderFactory.createTitledBorder("🚀 Start New Quiz Battle"));

        setupPanel.add(new JLabel("Select Topic Category:"));
        categoryBox = new JComboBox<>(new String[]{"Java Programming", "Computer Science", "General Knowledge"});
        categoryBox.setFont(new Font("Arial", Font.PLAIN, 13));
        setupPanel.add(categoryBox);

        JButton btnStartQuiz = new JButton("Start Quiz Battle Now 🚀");
        btnStartQuiz.setFont(new Font("Arial", Font.BOLD, 13));
        btnStartQuiz.setBackground(new Color(39, 174, 96));
        btnStartQuiz.setForeground(Color.WHITE);
        btnStartQuiz.setFocusPainted(false);
        setupPanel.add(btnStartQuiz);

        upperSection.add(setupPanel, BorderLayout.CENTER);
        centerPanel.add(upperSection, BorderLayout.NORTH);

        // --- Center Section: Production Leaderboard ---
        JPanel leaderboardPanel = new JPanel(new BorderLayout(8, 8));
        leaderboardPanel.setBackground(Color.WHITE);
        leaderboardPanel.setBorder(BorderFactory.createTitledBorder("🏅 Live Global Leaderboard (Ranked by Score & Accuracy)"));

        // Filter & Control Bar
        JPanel subControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        subControlPanel.setBackground(new Color(248, 250, 252));

        txtSearch = new JTextField(14);
        txtSearch.setToolTipText("Search by student name...");

        filterCategoryBox = new JComboBox<>(new String[]{"All Categories", "Java Programming", "Computer Science", "General Knowledge"});
        chkOnlyMe = new JCheckBox("My Attempts Only");
        chkOnlyMe.setOpaque(false);

        chkAutoSync = new JCheckBox("⚡ Live Auto-Sync (10s)");
        chkAutoSync.setOpaque(false);

        JButton btnRefresh = new JButton("🔄 Refresh Now");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.setBackground(new Color(41, 128, 185));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);

        subControlPanel.add(new JLabel("🔍 Search:"));
        subControlPanel.add(txtSearch);
        subControlPanel.add(new JLabel("Category:"));
        subControlPanel.add(filterCategoryBox);
        subControlPanel.add(chkOnlyMe);
        subControlPanel.add(chkAutoSync);
        subControlPanel.add(btnRefresh);

        leaderboardPanel.add(subControlPanel, BorderLayout.NORTH);

        // Leaderboard JTable (Non-Editable)
        leaderboardTable = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent accidental cell editing
            }
        };
        leaderboardTable.setRowHeight(32);
        leaderboardTable.setFillsViewportHeight(true);
        leaderboardTable.setSelectionBackground(new Color(52, 152, 219));
        leaderboardTable.setSelectionForeground(Color.WHITE);
        leaderboardTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        leaderboardTable.getTableHeader().setBackground(new Color(44, 62, 80));
        leaderboardTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        leaderboardPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(leaderboardPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Initial Data Load
        refreshLeaderboardData(false);

        // --- Event Listeners for Live Filtering ---
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                applyTableFilters();
            }
        });

        filterCategoryBox.addActionListener(e -> applyTableFilters());
        chkOnlyMe.addActionListener(e -> applyTableFilters());
        btnRefresh.addActionListener(e -> refreshLeaderboardData(true));

        // Auto-Sync Timer (Every 10 Seconds)
        autoRefreshTimer = new Timer(10000, e -> refreshLeaderboardData(false));
        chkAutoSync.addActionListener(e -> {
            if (chkAutoSync.isSelected()) {
                autoRefreshTimer.start();
            } else {
                autoRefreshTimer.stop();
            }
        });

        // Start Quiz Action with Safe Null/Empty Check
        btnStartQuiz.addActionListener(e -> {
            String selectedCat = (String) categoryBox.getSelectedItem();
            List<Question> questions = DatabaseDAO.getQuestionsByCategory(selectedCat);

            if (questions == null || questions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No questions found for this category! Please ask Admin to add questions.", "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                if (autoRefreshTimer != null) autoRefreshTimer.stop();
                dispose();
                new QuizEngineFrame(player, selectedCat, questions).setVisible(true);
            }
        });
    }

    // Helper method to build KPI summary cards
    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitle.setForeground(new Color(127, 140, 141));

        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(new Color(44, 62, 80));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // Combined Multi-Criteria Filter (Search Text + Category Dropdown + "My Attempts Only")
    private void applyTableFilters() {
        if (rowSorter == null) return;

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        String searchText = txtSearch.getText().trim();
        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + searchText, 1, 2)); // Search in Name & Category
        }

        String selectedCat = (String) filterCategoryBox.getSelectedItem();
        if (selectedCat != null && !selectedCat.equals("All Categories")) {
            filters.add(RowFilter.regexFilter("^" + selectedCat + "$", 2)); // Exact match on Category column
        }

        if (chkOnlyMe.isSelected()) {
            filters.add(RowFilter.regexFilter("(?i)^" + player.getUsername() + "$", 1)); // Exact match on Student Name
        }

        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    // Reloads Leaderboard Data & Dynamically Computes KPI Metrics
    private void refreshLeaderboardData(boolean showPopup) {
        DefaultTableModel model = DatabaseDAO.getLeaderboardModel();
        leaderboardTable.setModel(model);

        rowSorter = new TableRowSorter<>(model);
        leaderboardTable.setRowSorter(rowSorter);

        DashboardLeaderboardRenderer renderer = new DashboardLeaderboardRenderer(player.getUsername());
        for (int i = 0; i < leaderboardTable.getColumnCount(); i++) {
            leaderboardTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Calculate KPI Metrics from Table Model
        int rowCount = model.getRowCount();
        lblTotalAttemptsVal.setText(String.valueOf(rowCount));

        if (rowCount > 0) {
            String topStudent = String.valueOf(model.getValueAt(0, 1));
            String topScore = String.valueOf(model.getValueAt(0, 6));
            lblTopScorerVal.setText(topStudent + " (" + topScore + ")");

            int myBest = 0;
            double totalPct = 0.0;

            for (int i = 0; i < rowCount; i++) {
                String student = String.valueOf(model.getValueAt(i, 1));
                int score = Integer.parseInt(String.valueOf(model.getValueAt(i, 6)));
                String pctStr = String.valueOf(model.getValueAt(i, 7)).replace("%", "").trim();

                if (student.equalsIgnoreCase(player.getUsername()) && score > myBest) {
                    myBest = score;
                }
                try {
                    totalPct += Double.parseDouble(pctStr);
                } catch (NumberFormatException ignored) {}
            }

            lblMyBestVal.setText(myBest + " Marks");
            lblAvgScoreVal.setText(String.format("%.1f%%", totalPct / rowCount));
        } else {
            lblTopScorerVal.setText("No records yet");
            lblMyBestVal.setText("0 Marks");
            lblAvgScoreVal.setText("0.0%");
        }

        // Preserve any active search/category filters after refresh
        applyTableFilters();

        if (showPopup) {
            JOptionPane.showMessageDialog(this, "Leaderboard synced with live database!", "Live Update", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}