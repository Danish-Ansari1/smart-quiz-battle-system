import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    public static List<Question> getQuestionsByCategory(String category) {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE category = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Question(
                        rs.getInt("id"),
                        rs.getString("category"),
                        rs.getString("question_text"),
                        rs.getString("opt_a"),
                        rs.getString("opt_b"),
                        rs.getString("opt_c"),
                        rs.getString("opt_d"),
                        rs.getString("correct_opt")
                ));
            }
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

    public static DefaultTableModel getLeaderboardModel() {
        String[] columns = {"Rank/ID", "Student Name", "Category", "Total Q", "Correct", "Wrong", "Score", "Percentage (%)", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
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
}