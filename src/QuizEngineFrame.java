import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizEngineFrame extends JFrame {
    private Player player;
    private String category;
    private List<Question> questions;
    private int currentIndex = 0;

    // User ke selected answers ko yaad rakhne ke liye map (Question Index -> Selected Option 'A', 'B', 'C', 'D')
    private Map<Integer, String> userAnswers = new HashMap<>();

    private JLabel lblTimer, lblQuestion;
    private JRadioButton rbA, rbB, rbC, rbD;
    private ButtonGroup bg;
    private Timer countdownTimer;
    private int timeLeft = 15;

    public QuizEngineFrame(Player player, String category, List<Question> questions) {
        this.player = player;
        this.category = category;
        this.questions = questions;

        setTitle("Quiz Battle - " + category);
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header Panel with Timer
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        topPanel.setBackground(new Color(44, 62, 80));

        JLabel lblCat = new JLabel("Category: " + category);
        lblCat.setForeground(Color.WHITE);
        lblCat.setFont(new Font("Arial", Font.BOLD, 16));

        lblTimer = new JLabel("Time Left: 15s");
        lblTimer.setForeground(new Color(241, 196, 15));
        lblTimer.setFont(new Font("Arial", Font.BOLD, 18));

        topPanel.add(lblCat, BorderLayout.WEST);
        topPanel.add(lblTimer, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Question & Options Panel
        JPanel centerPanel = new JPanel(new GridLayout(5, 1, 8, 8));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        lblQuestion = new JLabel("Question loads here...");
        lblQuestion.setFont(new Font("Arial", Font.BOLD, 16));
        centerPanel.add(lblQuestion);

        rbA = new JRadioButton();
        rbB = new JRadioButton();
        rbC = new JRadioButton();
        rbD = new JRadioButton();

        bg = new ButtonGroup();
        bg.add(rbA); bg.add(rbB); bg.add(rbC); bg.add(rbD);

        centerPanel.add(rbA);
        centerPanel.add(rbB);
        centerPanel.add(rbC);
        centerPanel.add(rbD);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel for Separate Navigation Buttons (Previous, Next, Submit)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.setBackground(new Color(236, 240, 241));

        JButton btnPrev = new JButton("<< Previous");
        JButton btnNext = new JButton("Next >>");
        JButton btnSubmit = new JButton("Submit Quiz");

        // Styling Buttons
        styleButton(btnPrev, new Color(127, 140, 141));
        styleButton(btnNext, new Color(41, 128, 185));
        styleButton(btnSubmit, new Color(39, 174, 96));

        bottomPanel.add(btnPrev);
        bottomPanel.add(btnNext);
        bottomPanel.add(btnSubmit);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button Actions
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
            if (currentIndex < questions.size() - 1) {
                currentIndex++;
                loadQuestion();
            } else {
                JOptionPane.showMessageDialog(this, "This is the last question. Click 'Submit Quiz' to finish.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnSubmit.addActionListener(e -> {
            saveCurrentAnswer();
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to submit the quiz?", "Submit Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                countdownTimer.stop();
                finishQuiz();
            }
        });

        // Setup Swing Countdown Timer (1000ms = 1 sec)
        countdownTimer = new Timer(1000, e -> {
            timeLeft--;
            lblTimer.setText("Time Left: " + timeLeft + "s");
            if (timeLeft <= 0) {
                saveCurrentAnswer();
                if (currentIndex < questions.size() - 1) {
                    currentIndex++;
                    loadQuestion();
                } else {
                    countdownTimer.stop();
                    finishQuiz();
                }
            }
        });

        loadQuestion();
    }

    private void styleButton(JButton btn, Color bgCol) {
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bgCol);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 40));
    }

    // Current selection ko save karna taki wapas aane par yaad rahe
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
            lblQuestion.setText("Q" + (currentIndex + 1) + ": " + q.getQuestionText());
            rbA.setText("A. " + q.getOptA());
            rbB.setText("B. " + q.getOptB());
            rbC.setText("C. " + q.getOptC());
            rbD.setText("D. " + q.getOptD());

            // Agar user ne pehle is question ka answer diya tha, toh use wapas select kar do
            String savedAns = userAnswers.get(currentIndex);
            if (savedAns != null) {
                if (savedAns.equals("A")) rbA.setSelected(true);
                else if (savedAns.equals("B")) rbB.setSelected(true);
                else if (savedAns.equals("C")) rbC.setSelected(true);
                else if (savedAns.equals("D")) rbD.setSelected(true);
            }

            timeLeft = 15;
            lblTimer.setText("Time Left: " + timeLeft + "s");
            countdownTimer.restart();
        }
    }

    private void finishQuiz() {
        int correctCount = 0;
        int incorrectCount = 0;
        int score = 0;
        int totalQ = questions.size();

        // Sabhi saved answers ko check karke score calculate karna
        for (int i = 0; i < totalQ; i++) {
            String studentAns = userAnswers.get(i);
            String correctAns = questions.get(i).getCorrectOpt();

            if (studentAns != null && studentAns.equalsIgnoreCase(correctAns)) {
                correctCount++;
                score += 10;
            } else {
                incorrectCount++;
            }
        }

        double percentage = ((double) correctCount / totalQ) * 100.0;

        // Database me save karna
        DatabaseDAO.saveResult(player.getUsername(), category, totalQ, correctCount, incorrectCount, score, percentage);

        // --- Production-Level Custom Result Dialog ---
        JDialog resultDialog = new JDialog(this, "Quiz Performance Report", true);
        resultDialog.setSize(460, 460);
        resultDialog.setLocationRelativeTo(this);
        resultDialog.setLayout(new BorderLayout());

        // Top Header Banner
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(460, 65));
        JLabel lblTitle = new JLabel("📊 PERFORMANCE REPORT");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(lblTitle);
        resultDialog.add(headerPanel, BorderLayout.NORTH);

        // Details Center Panel
        JPanel contentPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 35, 20, 35));
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(new JLabel("Student Name:"));
        contentPanel.add(new JLabel("<html><b>" + player.getUsername() + "</b></html>"));

        contentPanel.add(new JLabel("Category:"));
        contentPanel.add(new JLabel("<html><b>" + category + "</b></html>"));

        contentPanel.add(new JLabel("Correct Answers:"));
        JLabel lblCorrect = new JLabel(correctCount + " / " + totalQ);
        lblCorrect.setForeground(new Color(39, 174, 96));
        contentPanel.add(lblCorrect);

        contentPanel.add(new JLabel("Incorrect / Skipped:"));
        contentPanel.add(new JLabel(String.valueOf(incorrectCount)));

        contentPanel.add(new JLabel("Final Score:"));
        JLabel lblScore = new JLabel(score + " Marks");
        lblScore.setForeground(new Color(41, 128, 185));
        contentPanel.add(lblScore);

        contentPanel.add(new JLabel("Percentage:"));
        JLabel lblPercent = new JLabel(String.format("%.2f%%", percentage));
        lblPercent.setFont(new Font("Arial", Font.BOLD, 14));
        lblPercent.setForeground(percentage >= 50 ? new Color(39, 174, 96) : new Color(192, 57, 43));
        contentPanel.add(lblPercent);

        resultDialog.add(contentPanel, BorderLayout.CENTER);

        // Bottom Button Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(236, 240, 241));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnClose = new JButton("Back to Dashboard");
        btnClose.setBackground(new Color(41, 128, 185));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(170, 38));

        btnClose.addActionListener(e -> {
            resultDialog.dispose();
            dispose();
            new DashboardFrame(player).setVisible(true);
        });

        bottomPanel.add(btnClose);
        resultDialog.add(bottomPanel, BorderLayout.SOUTH);

        resultDialog.setVisible(true);
    }
}