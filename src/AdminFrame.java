import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;

// --- Pro Leaderboard Table Cell Renderer for Gold, Silver, Bronze & Striped Rows ---
class ProLeaderboardRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Alignment set karna (Rank aur numbers center me, baaki left/right)
        if (column == 0 || column >= 3) {
            setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        // Agar row selected nahi hai tab professional coloring dena
        if (!isSelected) {
            if (row == 0) {
                c.setBackground(new Color(255, 215, 0, 120)); // Rank 1: Gold 🥇
                c.setFont(new Font("Arial", Font.BOLD, 13));
            } else if (row == 1) {
                c.setBackground(new Color(192, 192, 192, 120)); // Rank 2: Silver 🥈
                c.setFont(new Font("Arial", Font.BOLD, 13));
            } else if (row == 2) {
                c.setBackground(new Color(205, 127, 50, 120)); // Rank 3: Bronze 🥉
                c.setFont(new Font("Arial", Font.BOLD, 13));
            } else {
                // Baaki rows ke liye alternating colors (Striped table)
                if (row % 2 == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(new Color(245, 247, 250));
                }
                c.setFont(new Font("Arial", Font.PLAIN, 12));
            }
        }
        return c;
    }
}

public class AdminFrame extends JFrame {
    private JTable studentResultsTable;
    private TableRowSorter<javax.swing.table.TableModel> rowSorter;

    public AdminFrame(Player admin) {
        setTitle("Teacher / Admin Panel - Smart Quiz Battle System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(142, 68, 173));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("Teacher Admin Panel (Logged in: " + admin.getUsername() + ")");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginSignUpFrame().setVisible(true);
        });

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(btnLogout, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Tabs for Teacher
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Pro Live Leaderboard & Student Results
        JPanel resultsPanel = new JPanel(new BorderLayout(10, 10));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Table Setup
        studentResultsTable = new JTable(DatabaseDAO.getLeaderboardModel());
        studentResultsTable.setRowHeight(32);
        studentResultsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        studentResultsTable.getTableHeader().setBackground(new Color(41, 128, 185));
        studentResultsTable.getTableHeader().setForeground(Color.WHITE);

        // Apply Custom Pro Renderer to all columns
        for (int i = 0; i < studentResultsTable.getColumnCount(); i++) {
            studentResultsTable.getColumnModel().getColumn(i).setCellRenderer(new ProLeaderboardRenderer());
        }

        // Search/Filter Setup for Live Searching
        rowSorter = new TableRowSorter<>(studentResultsTable.getModel());
        studentResultsTable.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(studentResultsTable);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        // Top Control Panel inside Tab 1 (Search Box + Refresh Button)
        JPanel controlSubPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JTextField txtSearch = new JTextField(15);
        txtSearch.setToolTipText("Search student name or category...");

        JButton btnRefresh = new JButton("🔄 Refresh Records");
        styleProButton(btnRefresh, new Color(41, 128, 185));

        controlSubPanel.add(new JLabel("🔍 Search Student/Category:"));
        controlSubPanel.add(txtSearch);
        controlSubPanel.add(btnRefresh);
        resultsPanel.add(controlSubPanel, BorderLayout.NORTH);

        // Search Functionality (Live Filtering)
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = txtSearch.getText().trim();
                if (text.length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // Refresh Action
        btnRefresh.addActionListener(e -> {
            studentResultsTable.setModel(DatabaseDAO.getLeaderboardModel());
            // Re-apply renderer and sorter after model update
            rowSorter = new TableRowSorter<>(studentResultsTable.getModel());
            studentResultsTable.setRowSorter(rowSorter);
            for (int i = 0; i < studentResultsTable.getColumnCount(); i++) {
                studentResultsTable.getColumnModel().getColumn(i).setCellRenderer(new ProLeaderboardRenderer());
            }
            JOptionPane.showMessageDialog(this, "Record refreshed successfully!", "Live Update", JOptionPane.INFORMATION_MESSAGE);
        });

        tabbedPane.addTab("📊 Live Records", resultsPanel);

        // Tab 2: Add Question Form
        JPanel addQPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        addQPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JComboBox<String> catCombo = new JComboBox<>(new String[]{"Java Programming", "Computer Science", "General Knowledge"});
        JTextField txtQuestion = new JTextField();
        JTextField txtOptA = new JTextField();
        JTextField txtOptB = new JTextField();
        JTextField txtOptC = new JTextField();
        JTextField txtOptD = new JTextField();
        JComboBox<String> correctCombo = new JComboBox<>(new String[]{"A", "B", "C", "D"});

        addQPanel.add(new JLabel("Select Category:")); addQPanel.add(catCombo);
        addQPanel.add(new JLabel("Question Text:")); addQPanel.add(txtQuestion);
        addQPanel.add(new JLabel("Option A:")); addQPanel.add(txtOptA);
        addQPanel.add(new JLabel("Option B:")); addQPanel.add(txtOptB);
        addQPanel.add(new JLabel("Option C:")); addQPanel.add(txtOptC);
        addQPanel.add(new JLabel("Option D:")); addQPanel.add(txtOptD);
        addQPanel.add(new JLabel("Correct Option (A/B/C/D):")); addQPanel.add(correctCombo);

        JButton btnSaveQ = new JButton("Add Question to Database ➕");
        btnSaveQ.setBackground(new Color(39, 174, 96));
        btnSaveQ.setForeground(Color.WHITE);
        addQPanel.add(new JLabel(""));
        addQPanel.add(btnSaveQ);

        btnSaveQ.addActionListener(e -> {
            boolean ok = DatabaseDAO.addQuestion(
                    (String) catCombo.getSelectedItem(),
                    txtQuestion.getText().trim(),
                    txtOptA.getText().trim(),
                    txtOptB.getText().trim(),
                    txtOptC.getText().trim(),
                    txtOptD.getText().trim(),
                    (String) correctCombo.getSelectedItem()
            );
            if (ok) {
                JOptionPane.showMessageDialog(this, "Question added successfully!");
                txtQuestion.setText(""); txtOptA.setText(""); txtOptB.setText(""); txtOptC.setText(""); txtOptD.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Error adding question!");
            }
        });

        tabbedPane.addTab("➕ Add New Quiz Question", addQPanel);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void styleProButton(JButton btn, Color bgCol) {
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(bgCol);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }
}