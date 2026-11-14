import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public class DatabaseDAO {

    public static boolean registerUser(String username, String password, String role) {
        // SECURITY CHECK: Koi bhi naya user 'admin' naam se signup nahi kar sakta
        if (username.trim().equalsIgnoreCase("admin")) {
            return false;
        }

        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static Player loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Player(rs.getInt("id"), rs.getString("username"), rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 1. Database se saari unique categories fetch karne ke liye method
    public static List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM questions ORDER BY category ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String cat = rs.getString("category");
                if (cat != null && !cat.trim().isEmpty()) {
                    categories.add(cat.trim());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (categories.isEmpty()) {
            categories.add("Java Programming");
            categories.add("Computer Science");
        }

        return categories;
    }

    // 2. Questions aur Options (A, B, C, D) dono ko Randomize/Shuffle karne wala method
    public static List<Question> getQuestionsByCategory(String category) {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE category = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String cat = rs.getString("category");
                String qText = rs.getString("question_text");
                String optA = rs.getString("opt_a");
                String optB = rs.getString("opt_b");
                String optC = rs.getString("opt_c");
                String optD = rs.getString("opt_d");
                String correctKey = rs.getString("correct_opt").trim().toUpperCase();

                // Asli correct answer ka text pata lagayein
                String correctText = optA;
                switch (correctKey) {
                    case "A": correctText = optA; break;
                    case "B": correctText = optB; break;
                    case "C": correctText = optC; break;
                    case "D": correctText = optD; break;
                }

                // Charo options ko list mein daal kar shuffle karein
                List<String> optionsPool = new ArrayList<>();
                optionsPool.add(optA);
                optionsPool.add(optB);
                optionsPool.add(optC);
                optionsPool.add(optD);
                Collections.shuffle(optionsPool);

                String newOptA = optionsPool.get(0);
                String newOptB = optionsPool.get(1);
                String newOptC = optionsPool.get(2);
                String newOptD = optionsPool.get(3);

                // Sahi answer ab kis naye option par gaya hai, wo key set karein
                String newCorrectKey = "A";
                if (newOptA.equals(correctText)) newCorrectKey = "A";
                else if (newOptB.equals(correctText)) newCorrectKey = "B";
                else if (newOptC.equals(correctText)) newCorrectKey = "C";
                else if (newOptD.equals(correctText)) newCorrectKey = "D";

                list.add(new Question(id, cat, qText, newOptA, newOptB, newOptC, newOptD, newCorrectKey));
            }

            // Poore questions ke order ko bhi shuffle karein
            Collections.shuffle(list);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addQuestion(String category, String qText, String a, String b, String c, String d, String correct) {
        String sql = "INSERT INTO questions (category, question_text, opt_a, opt_b, opt_c, opt_d, correct_opt) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            pstmt.setString(2, qText);
            pstmt.setString(3, a);
            pstmt.setString(4, b);
            pstmt.setString(5, c);
            pstmt.setString(6, d);
            pstmt.setString(7, correct);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void saveResult(String playerName, String category, int totalQ, int correct, int incorrect, int score, double percentage) {
        String sql = "INSERT INTO results (player_name, category, total_questions, correct_answers, incorrect_answers, score, percentage) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            pstmt.setString(2, category);
            pstmt.setInt(3, totalQ);
            pstmt.setInt(4, correct);
            pstmt.setInt(5, incorrect);
            pstmt.setInt(6, score);
            pstmt.setDouble(7, percentage);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. Student Dashboard ke KPI cards, Filters aur Leaderboard table ke liye method
    public static List<String[]> getLeaderboard() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT * FROM results ORDER BY score DESC, percentage DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("player_name"),
                        rs.getString("category"),
                        String.valueOf(rs.getInt("total_questions")),
                        String.valueOf(rs.getInt("correct_answers")),
                        String.valueOf(rs.getInt("incorrect_answers")),
                        String.valueOf(rs.getInt("score")),
                        String.format("%.2f%%", rs.getDouble("percentage")),
                        String.valueOf(rs.getTimestamp("attempt_date"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Admin Frame ke Leaderboard TableModel ke liye method
    public static DefaultTableModel getLeaderboardModel() {
        String[] columns = {"Rank/ID", "Student Name", "Category", "Total Q", "Correct", "Wrong", "Score", "Percentage (%)", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String sql = "SELECT * FROM results ORDER BY score DESC, percentage DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int rank = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                        rank++,
                        rs.getString("player_name"),
                        rs.getString("category"),
                        rs.getInt("total_questions"),
                        rs.getInt("correct_answers"),
                        rs.getInt("incorrect_answers"),
                        rs.getInt("score"),
                        String.format("%.2f%%", rs.getDouble("percentage")),
                        rs.getTimestamp("attempt_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    // ================= PRODUCTION METHODS FOR DETAILED QUESTION LOGS =================

    private static void ensureAnswerLogsTableExists(Connection conn) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS student_answer_logs (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "player_name VARCHAR(100), " +
                "category VARCHAR(100), " +
                "question_no INT, " +
                "question_text TEXT, " +
                "selected_ans VARCHAR(255), " +
                "correct_ans VARCHAR(255), " +
                "status VARCHAR(20), " +
                "attempt_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    private static String formatFullOption(Question q, String optKey) {
        if (optKey == null) return "Not Attempted (Skipped)";
        switch (optKey.toUpperCase()) {
            case "A": return "A (" + q.getOptA() + ")";
            case "B": return "B (" + q.getOptB() + ")";
            case "C": return "C (" + q.getOptC() + ")";
            case "D": return "D (" + q.getOptD() + ")";
            default: return optKey;
        }
    }

    public static void saveDetailedAnswers(String playerName, String category, List<Question> questions, Map<Integer, String> userAnswers) {
        String insertSQL = "INSERT INTO student_answer_logs (player_name, category, question_no, question_text, selected_ans, correct_ans, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            ensureAnswerLogsTableExists(conn);
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                for (int i = 0; i < questions.size(); i++) {
                    Question q = questions.get(i);
                    String userKey = userAnswers.get(i);
                    String corrKey = q.getCorrectOpt();

                    String status;
                    if (userKey == null) {
                        status = "SKIPPED";
                    } else if (userKey.equalsIgnoreCase(corrKey)) {
                        status = "CORRECT";
                    } else {
                        status = "WRONG";
                    }

                    pstmt.setString(1, playerName);
                    pstmt.setString(2, category);
                    pstmt.setInt(3, i + 1);
                    pstmt.setString(4, q.getQuestionText());
                    pstmt.setString(5, formatFullOption(q, userKey));
                    pstmt.setString(6, formatFullOption(q, corrKey));
                    pstmt.setString(7, status);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DefaultTableModel getStudentAnswerLogsModel() {
        String[] columns = {"Log ID", "Student Name", "Category", "Q.No", "Question Text", "Student's Answer", "Correct Answer", "Status", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String sql = "SELECT * FROM student_answer_logs ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection()) {
            ensureAnswerLogsTableExists(conn);
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("player_name"),
                            rs.getString("category"),
                            "Q" + rs.getInt("question_no"),
                            rs.getString("question_text"),
                            rs.getString("selected_ans"),
                            rs.getString("correct_ans"),
                            rs.getString("status"),
                            rs.getTimestamp("attempt_date")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }
}