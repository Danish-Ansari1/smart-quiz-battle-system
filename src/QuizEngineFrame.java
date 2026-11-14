import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizEngineFrame extends JFrame {
    private Player player;
    private String category;
    private List<Question> questions;
    private int currentIndex = 0;

    private Map<Integer, String> userAnswers = new HashMap<>();

    private JLabel lblTimer, lblQuestion, lblProgress;
    private JProgressBar progressBar;
    private JRadioButton rbA, rbB, rbC, rbD;
    private JPanel panelOptA, panelOptB, panelOptC, panelOptD;
    private ButtonGroup bg;
    private Timer countdownTimer;
    private int timeLeft = 15;

    // Production Color Palette
    private final Color BG_COLOR = new Color(240, 244, 248);
    private final Color CARD_BG = Color.WHITE;
    private final Color HEADER_BG = new Color(15, 23, 42);          // Dark Slate Header
    private final Color PRIMARY_COLOR = new Color(37, 99, 235);     // Royal Blue
    private final Color PRIMARY_LIGHT = new Color(239, 246, 255);   // Selected Option BG
    private final Color SUCCESS_COLOR = new Color(22, 163, 74);     // Green
    private final Color DANGER_COLOR = new Color(220, 38, 38);      // Red
    private final Color WARNING_COLOR = new Color(245, 158, 11);    // Amber/Gold
    private final Color PURPLE_COLOR = new Color(124, 58, 237);     // Purple for PDF Button
    private final Color TEXT_DARK = new Color(30, 41, 59);
    private final Color TEXT_MUTED = new Color(100, 116, 139);
    private final Color BORDER_COLOR = new Color(203, 213, 225);

    public QuizEngineFrame(Player player, String category, List<Question> questions) {
        this.player = player;
        this.category = category;
        this.questions = questions;

        setTitle("Smart Quiz Battle - " + category + " (Live Assessment)");
        setSize(1100, 720);
        setMinimumSize(new Dimension(850, 620));
        // Automatically opens in Full Screen (Maximized) & keeps maximize button enabled
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        // ================= 1. TOP HEADER BAR =================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(16, 35, 16, 35));
        topPanel.setBackground(HEADER_BG);

        JLabel lblCat = new JLabel("Category: " + category + "   |   Candidate: " + player.getUsername());
        lblCat.setForeground(Color.WHITE);
        lblCat.setFont(new Font("Segoe UI", Font.BOLD, 17));

        lblTimer = new JLabel("⏱ Time Left: 15s");
        lblTimer.setForeground(WARNING_COLOR);
        lblTimer.setFont(new Font("Segoe UI", Font.BOLD, 19));

        topPanel.add(lblCat, BorderLayout.WEST);
        topPanel.add(lblTimer, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ================= 2. CENTER QUESTION CARD =================
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(BG_COLOR);

        JPanel quizCard = new JPanel();
        quizCard.setLayout(new BoxLayout(quizCard, BoxLayout.Y_AXIS));
        quizCard.setBackground(CARD_BG);
        quizCard.setPreferredSize(new Dimension(820, 500));
        quizCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(30, 40, 30, 40)
        ));

        // Question Counter & Progress Bar
        JPanel progressHeader = new JPanel(new BorderLayout());
        progressHeader.setBackground(CARD_BG);
        progressHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        lblProgress = new JLabel("Question 1 of " + questions.size());
        lblProgress.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblProgress.setForeground(PRIMARY_COLOR);
        progressHeader.add(lblProgress, BorderLayout.WEST);

        quizCard.add(progressHeader);
        quizCard.add(Box.createRigidArea(new Dimension(0, 8)));

        progressBar = new JProgressBar(0, questions.size());
        progressBar.setValue(1);
        progressBar.setForeground(PRIMARY_COLOR);
        progressBar.setBackground(new Color(226, 232, 240));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(740, 6));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        quizCard.add(progressBar);
        quizCard.add(Box.createRigidArea(new Dimension(0, 22)));

        // Question Text
        lblQuestion = new JLabel("Question loads here...");
        lblQuestion.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblQuestion.setForeground(TEXT_DARK);
        lblQuestion.setAlignmentX(Component.LEFT_ALIGNMENT);
        quizCard.add(lblQuestion);
        quizCard.add(Box.createRigidArea(new Dimension(0, 25)));

        // Radio Buttons & Interactive Option Panels
        rbA = new JRadioButton();
        rbB = new JRadioButton();
        rbC = new JRadioButton();
        rbD = new JRadioButton();

        bg = new ButtonGroup();
        bg.add(rbA);
        bg.add(rbB);
        bg.add(rbC);
        bg.add(rbD);

        panelOptA = createOptionCard(rbA);
        panelOptB = createOptionCard(rbB);
        panelOptC = createOptionCard(rbC);
        panelOptD = createOptionCard(rbD);

        quizCard.add(panelOptA);
        quizCard.add(Box.createRigidArea(new Dimension(0, 12)));
        quizCard.add(panelOptB);
        quizCard.add(Box.createRigidArea(new Dimension(0, 12)));
        quizCard.add(panelOptC);
        quizCard.add(Box.createRigidArea(new Dimension(0, 12)));
        quizCard.add(panelOptD);

        centerWrapper.add(quizCard, new GridBagConstraints());
        add(centerWrapper, BorderLayout.CENTER);

        // ================= 3. BOTTOM NAVIGATION BAR =================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 16));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new LineBorder(new Color(226, 232, 240), 1));

        JButton btnPrev = createStyledButton("<< Previous", new Color(100, 116, 139));
        JButton btnNext = createStyledButton("Next >>", PRIMARY_COLOR);
        JButton btnSubmit = createStyledButton("Submit Quiz", SUCCESS_COLOR);

        bottomPanel.add(btnPrev);
        bottomPanel.add(btnNext);
        bottomPanel.add(btnSubmit);
        add(bottomPanel, BorderLayout.SOUTH);

        // ================= BUTTON ACTIONS =================
        btnPrev.addActionListener(e -> {
            saveCurrentAnswer();
            if (currentIndex > 0) {
                currentIndex--;
                loadQuestion();
            } else {
                JOptionPane.showMessageDialog(this, "This is the first question.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnNext.addActionListener(e -> {
            saveCurrentAnswer();
            if (currentIndex >= questions.size() - 1) {
                if (countdownTimer != null) countdownTimer.stop();
                finishQuiz();
            } else {
                currentIndex++;
                loadQuestion();
            }
        });

        btnSubmit.addActionListener(e -> {
            saveCurrentAnswer();
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to submit the quiz?",
                    "Submit Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                if (countdownTimer != null) countdownTimer.stop();
                finishQuiz();
            }
        });

        // ================= TIMER LOGIC =================
        countdownTimer = new Timer(1000, e -> {
            timeLeft--;
            lblTimer.setText("⏱ Time Left: " + timeLeft + "s");
            if (timeLeft <= 5) {
                lblTimer.setForeground(new Color(248, 113, 113)); // Turns red when <= 5s
            } else {
                lblTimer.setForeground(WARNING_COLOR);
            }

            if (timeLeft <= 0) {
                saveCurrentAnswer();
                if (currentIndex >= questions.size() - 1) {
                    countdownTimer.stop();
                    finishQuiz();
                } else {
                    currentIndex++;
                    loadQuestion();
                }
            }
        });

        loadQuestion();
    }

    private JPanel createOptionCard(JRadioButton rb) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        panel.setPreferredSize(new Dimension(740, 54));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));

        rb.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        rb.setForeground(TEXT_DARK);
        rb.setFocusPainted(false);
        rb.setOpaque(false);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rb.addActionListener(e -> refreshOptionStyles());

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                rb.setSelected(true);
                refreshOptionStyles();
            }
        });

        panel.add(rb, BorderLayout.CENTER);
        return panel;
    }

    private void refreshOptionStyles() {
        JRadioButton[] rbs = {rbA, rbB, rbC, rbD};
        JPanel[] panels = {panelOptA, panelOptB, panelOptC, panelOptD};

        for (int i = 0; i < rbs.length; i++) {
            if (rbs[i].isSelected()) {
                panels[i].setBackground(PRIMARY_LIGHT);
                panels[i].setBorder(new CompoundBorder(
                        new LineBorder(PRIMARY_COLOR, 2, true),
                        new EmptyBorder(9, 15, 9, 15)
                ));
                rbs[i].setFont(new Font("Segoe UI", Font.BOLD, 15));
                rbs[i].setForeground(PRIMARY_COLOR);
            } else {
                panels[i].setBackground(Color.WHITE);
                panels[i].setBorder(new CompoundBorder(
                        new LineBorder(BORDER_COLOR, 1, true),
                        new EmptyBorder(10, 16, 10, 16)
                ));
                rbs[i].setFont(new Font("Segoe UI", Font.PLAIN, 15));
                rbs[i].setForeground(TEXT_DARK);
            }
        }
    }

    private JButton createStyledButton(String text, Color bgCol) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgCol);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 42));
        return btn;
    }

    private void saveCurrentAnswer() {
        if (rbA.isSelected()) userAnswers.put(currentIndex, "A");
        else if (rbB.isSelected()) userAnswers.put(currentIndex, "B");
        else if (rbC.isSelected()) userAnswers.put(currentIndex, "C");
        else if (rbD.isSelected()) userAnswers.put(currentIndex, "D");
    }

    private void loadQuestion() {
        if (currentIndex < questions.size()) {
            bg.clearSelection();
            Question q = questions.get(currentIndex);

            lblProgress.setText("Question " + (currentIndex + 1) + " of " + questions.size());
            progressBar.setValue(currentIndex + 1);

            lblQuestion.setText("<html><body style='width: 650px;'>Q" + (currentIndex + 1) + ". " + q.getQuestionText() + "</body></html>");

            rbA.setText("A.  " + q.getOptA());
            rbB.setText("B.  " + q.getOptB());
            rbC.setText("C.  " + q.getOptC());
            rbD.setText("D.  " + q.getOptD());

            String savedAns = userAnswers.get(currentIndex);
            if (savedAns != null) {
                if (savedAns.equals("A")) rbA.setSelected(true);
                else if (savedAns.equals("B")) rbB.setSelected(true);
                else if (savedAns.equals("C")) rbC.setSelected(true);
                else if (savedAns.equals("D")) rbD.setSelected(true);
            }

            refreshOptionStyles();

            timeLeft = 15;
            lblTimer.setForeground(WARNING_COLOR);
            lblTimer.setText("⏱ Time Left: " + timeLeft + "s");
            countdownTimer.restart();
        }
    }

    // Helper method to get option full text (e.g., "A (Only 1)")
    private String getOptionWithText(Question q, String optKey) {
        if (optKey == null) return "Not Attempted (Skipped)";
        switch (optKey.toUpperCase()) {
            case "A": return "A (" + q.getOptA() + ")";
            case "B": return "B (" + q.getOptB() + ")";
            case "C": return "C (" + q.getOptC() + ")";
            case "D": return "D (" + q.getOptD() + ")";
            default: return optKey;
        }
    }

    private void finishQuiz() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        this.dispose();

        int correctCount = 0;
        int incorrectCount = 0;
        int score = 0;
        final int totalQ = questions.size();

        for (int i = 0; i < totalQ; i++) {
            String studentAns = userAnswers.get(i);
            String correctAns = questions.get(i).getCorrectOpt();

            if (studentAns != null && studentAns.equalsIgnoreCase(correctAns)) {
                correctCount++;
                score += 1;
            } else {
                incorrectCount++;
            }
        }

        final double percentage = totalQ > 0 ? ((double) correctCount / totalQ) * 100.0 : 0.0;

        // Final variables for lambda expression compatibility
        final int finalScore = score;
        final int finalCorrectCount = correctCount;
        final int finalIncorrectCount = incorrectCount;

        // Save Summary Result & Detailed Question Logs for Admin Panel
        DatabaseDAO.saveResult(player.getUsername(), category, totalQ, finalCorrectCount, finalIncorrectCount, finalScore, percentage);
        DatabaseDAO.saveDetailedAnswers(player.getUsername(), category, questions, userAnswers);

        // ================= FULL SCREEN RESULT & ANSWER REVIEW DIALOG =================
        JDialog resultDialog = new JDialog((Frame) null, "Quiz Performance & Answer Review", true);

        // Automatically set to full screen bounds (maximized, accounting for taskbar)
        Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        resultDialog.setBounds(screenBounds);
        resultDialog.setMinimumSize(new Dimension(800, 600));
        resultDialog.setResizable(true);
        resultDialog.setLayout(new BorderLayout());

        // 1. Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(new EmptyBorder(18, 30, 18, 30));

        JLabel lblTitle = new JLabel("📊 PERFORMANCE REPORT & ANSWER SHEET");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));

        JLabel lblStudentInfo = new JLabel("Student: " + player.getUsername() + "  |  Category: " + category);
        lblStudentInfo.setForeground(new Color(148, 163, 184));
        lblStudentInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblStudentInfo, BorderLayout.EAST);
        resultDialog.add(headerPanel, BorderLayout.NORTH);

        // 2. Main Center Container (Summary Stats + Detailed Question Review)
        JPanel mainCenter = new JPanel(new BorderLayout(0, 15));
        mainCenter.setBackground(BG_COLOR);
        mainCenter.setBorder(new EmptyBorder(20, 25, 15, 25));

        // 2A. Summary Metric Cards (4 Cards in a Row)
        JPanel summaryGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        summaryGrid.setBackground(BG_COLOR);
        summaryGrid.add(createStatCard("FINAL SCORE", finalScore + " Marks", PRIMARY_COLOR));
        summaryGrid.add(createStatCard("PERCENTAGE", String.format("%.2f%%", percentage), percentage >= 50 ? SUCCESS_COLOR : DANGER_COLOR));
        summaryGrid.add(createStatCard("CORRECT ANSWERS", finalCorrectCount + " / " + totalQ, SUCCESS_COLOR));
        summaryGrid.add(createStatCard("WRONG / SKIPPED", String.valueOf(finalIncorrectCount), DANGER_COLOR));

        mainCenter.add(summaryGrid, BorderLayout.NORTH);

        // 2B. Scrollable Question-by-Question Review List
        JPanel reviewListPanel = new JPanel();
        reviewListPanel.setLayout(new BoxLayout(reviewListPanel, BoxLayout.Y_AXIS));
        reviewListPanel.setBackground(BG_COLOR);

        for (int i = 0; i < totalQ; i++) {
            Question q = questions.get(i);
            String userAns = userAnswers.get(i);
            String correctAns = q.getCorrectOpt();
            boolean isCorrect = (userAns != null && userAns.equalsIgnoreCase(correctAns));
            boolean isSkipped = (userAns == null);

            JPanel qCard = new JPanel(new BorderLayout(10, 8));
            qCard.setBackground(Color.WHITE);
            qCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            qCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

            Color statusBorderColor = isCorrect ? SUCCESS_COLOR : (isSkipped ? WARNING_COLOR : DANGER_COLOR);
            qCard.setBorder(new CompoundBorder(
                    new LineBorder(statusBorderColor, 2, true),
                    new EmptyBorder(12, 18, 12, 18)
            ));

            // Top row inside card: Question Text + Status Badge
            JPanel qTopRow = new JPanel(new BorderLayout(15, 0));
            qTopRow.setBackground(Color.WHITE);

            JLabel lblQText = new JLabel("<html><body style='width: 750px;'><b>Q" + (i + 1) + ":</b> " + q.getQuestionText() + "</body></html>");
            lblQText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lblQText.setForeground(TEXT_DARK);

            String badgeText = isCorrect ? "✔ CORRECT (+1)" : (isSkipped ? "⚠ SKIPPED (0)" : "✖ WRONG (0)");
            JLabel lblBadge = new JLabel(badgeText);
            lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblBadge.setForeground(statusBorderColor);

            qTopRow.add(lblQText, BorderLayout.CENTER);
            qTopRow.add(lblBadge, BorderLayout.EAST);

            // Bottom row inside card: Student Answer vs Correct Answer
            JPanel qBottomRow = new JPanel(new GridLayout(1, 2, 15, 0));
            qBottomRow.setBackground(new Color(248, 250, 252));
            qBottomRow.setBorder(new EmptyBorder(8, 10, 8, 10));

            JLabel lblUserAns = new JLabel("Your Answer: " + getOptionWithText(q, userAns));
            lblUserAns.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblUserAns.setForeground(isCorrect ? SUCCESS_COLOR : DANGER_COLOR);

            JLabel lblCorrAns = new JLabel("Correct Answer: " + getOptionWithText(q, correctAns));
            lblCorrAns.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblCorrAns.setForeground(SUCCESS_COLOR);

            qBottomRow.add(lblUserAns);
            qBottomRow.add(lblCorrAns);

            qCard.add(qTopRow, BorderLayout.NORTH);
            qCard.add(qBottomRow, BorderLayout.SOUTH);

            reviewListPanel.add(qCard);
            reviewListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(reviewListPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                " Detailed Question-by-Question Review ",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                TEXT_DARK
        ));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BG_COLOR);

        mainCenter.add(scrollPane, BorderLayout.CENTER);
        resultDialog.add(mainCenter, BorderLayout.CENTER);

        // 3. Footer Buttons (Print/Save PDF + Export Report Card + Back to Dashboard)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new LineBorder(new Color(226, 232, 240), 1));

        JButton btnPrintPdf = createStyledButton("🖨️ Save / Print PDF", PURPLE_COLOR);
        btnPrintPdf.setPreferredSize(new Dimension(190, 40));
        btnPrintPdf.addActionListener(e -> printReportToPDF(resultDialog, mainCenter));

        JButton btnExportCard = createStyledButton("📄 Export Report Card", SUCCESS_COLOR);
        btnExportCard.setPreferredSize(new Dimension(200, 40));
        btnExportCard.addActionListener(e -> exportFormattedReportCard(resultDialog, finalScore, totalQ, finalCorrectCount, finalIncorrectCount, percentage));

        JButton btnClose = createStyledButton("Back to Dashboard", PRIMARY_COLOR);
        btnClose.setPreferredSize(new Dimension(190, 40));
        btnClose.addActionListener(e -> {
            resultDialog.dispose();
            new DashboardFrame(player).setVisible(true);
        });

        bottomPanel.add(btnPrintPdf);
        bottomPanel.add(btnExportCard);
        bottomPanel.add(btnClose);
        resultDialog.add(bottomPanel, BorderLayout.SOUTH);

        resultDialog.setVisible(true);
    }

    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(14, 18, 14, 18)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(TEXT_MUTED);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValue.setForeground(accentColor);

        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(lblValue);
        return card;
    }

    // Method 1: Native Windows "Microsoft Print to PDF" Vector Printer
    private void printReportToPDF(Component parent, JPanel reportPanel) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Quiz_Report_" + player.getUsername() + "_" + category);

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Scale report cleanly to fit A4 PDF page
            double scaleX = pageFormat.getImageableWidth() / reportPanel.getWidth();
            double scaleY = pageFormat.getImageableHeight() / reportPanel.getHeight();
            double scale = Math.min(scaleX, scaleY);
            g2d.scale(scale, scale);

            reportPanel.printAll(g2d);
            return Printable.PAGE_EXISTS;
        });

        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(parent, "PDF Report generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(parent, "Error printing PDF: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method 2: Exports an Official Multi-Page Ready Printable Report Card & Opens It
    private void exportFormattedReportCard(Component parent, int score, int totalQ, int correctCount, int incorrectCount, double percentage) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Official Exam Report Card");
        String defaultFileName = "ReportCard_" + player.getUsername().replaceAll("\\s+", "_") + "_" + category.replaceAll("\\s+", "_") + ".html";
        fileChooser.setSelectedFile(new File(defaultFileName));

        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".html")) {
                file = new File(file.getAbsolutePath() + ".html");
            }

            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            sb.append("<title>Exam Report - ").append(player.getUsername()).append("</title>");
            sb.append("<style>");
            sb.append("body { font-family: 'Segoe UI', Arial, sans-serif; background: #f1f5f9; padding: 30px; color: #1e293b; }");
            sb.append(".card { max-width: 850px; margin: auto; background: #fff; padding: 35px; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.08); }");
            sb.append(".header { background: #0f172a; color: #fff; padding: 22px 28px; border-radius: 8px; display: flex; justify-content: space-between; align-items: center; }");
            sb.append(".stats { display: flex; gap: 15px; margin: 22px 0; }");
            sb.append(".stat-box { flex: 1; background: #f8fafc; border: 1px solid #cbd5e1; padding: 15px; border-radius: 8px; text-align: center; }");
            sb.append(".stat-val { font-size: 22px; font-weight: bold; margin-top: 6px; }");
            sb.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; font-size: 14px; }");
            sb.append("th { background: #1e293b; color: #fff; padding: 12px; text-align: left; }");
            sb.append("td { padding: 12px; border-bottom: 1px solid #e2e8f0; }");
            sb.append(".correct { color: #16a34a; font-weight: bold; }");
            sb.append(".wrong { color: #dc2626; font-weight: bold; }");
            sb.append(".skipped { color: #d97706; font-weight: bold; }");
            sb.append(".print-btn { background: #2563eb; color: #fff; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: bold; float: right; margin-bottom: 15px; }");
            sb.append("@media print { .print-btn { display: none; } body { background: #fff; padding: 0; } .card { box-shadow: none; } }");
            sb.append("</style></head><body>");

            sb.append("<div class='card'>");
            sb.append("<button class='print-btn' onclick='window.print()'>🖨️ Save as PDF / Print</button>");
            sb.append("<div style='clear:both;'></div>");
            sb.append("<div class='header'><div><h2 style='margin:0;'>SMART QUIZ BATTLE - OFFICIAL REPORT CARD</h2>");
            sb.append("<p style='margin:5px 0 0 0; color:#94a3b8;'>Candidate: <b>").append(player.getUsername()).append("</b> | Category: <b>").append(category).append("</b></p></div>");
            sb.append("<div style='text-align:right;'><span style='font-size:13px; color:#cbd5e1;'>Status: ").append(percentage >= 50 ? "PASSED" : "NEEDS IMPROVEMENT").append("</span></div></div>");

            sb.append("<div class='stats'>");
            sb.append("<div class='stat-box'><div>FINAL SCORE</div><div class='stat-val' style='color:#2563eb;'>").append(score).append(" / ").append(totalQ).append("</div></div>");
            sb.append("<div class='stat-box'><div>PERCENTAGE</div><div class='stat-val' style='color:").append(percentage >= 50 ? "#16a34a" : "#dc2626").append(";'>").append(String.format("%.2f%%", percentage)).append("</div></div>");
            sb.append("<div class='stat-box'><div>CORRECT</div><div class='stat-val' style='color:#16a34a;'>").append(correctCount).append("</div></div>");
            sb.append("<div class='stat-box'><div>WRONG / SKIPPED</div><div class='stat-val' style='color:#dc2626;'>").append(incorrectCount).append("</div></div>");
            sb.append("</div>");

            sb.append("<table><thead><tr><th>Q#</th><th>Question</th><th>Your Answer</th><th>Correct Answer</th><th>Status</th></tr></thead><tbody>");
            for (int i = 0; i < totalQ; i++) {
                Question q = questions.get(i);
                String uAns = userAnswers.get(i);
                String cAns = q.getCorrectOpt();
                boolean isCorr = (uAns != null && uAns.equalsIgnoreCase(cAns));
                boolean isSkip = (uAns == null);
                String statusClass = isCorr ? "correct" : (isSkip ? "skipped" : "wrong");
                String statusText = isCorr ? "✔ CORRECT (+1)" : (isSkip ? "⚠ SKIPPED (0)" : "✖ WRONG (0)");

                sb.append("<tr>");
                sb.append("<td><b>Q").append(i + 1).append("</b></td>");
                sb.append("<td>").append(q.getQuestionText()).append("</td>");
                sb.append("<td class='").append(statusClass).append("'>").append(getOptionWithText(q, uAns)).append("</td>");
                sb.append("<td class='correct'>").append(getOptionWithText(q, cAns)).append("</td>");
                sb.append("<td class='").append(statusClass).append("'>").append(statusText).append("</td>");
                sb.append("</tr>");
            }
            sb.append("</tbody></table></div></body></html>");

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(sb.toString());
                JOptionPane.showMessageDialog(parent, "Official Report Card saved to:\n" + file.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(file.toURI()); // Automatically opens the report card
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Failed to save report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}