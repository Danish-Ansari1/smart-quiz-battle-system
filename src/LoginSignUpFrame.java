import javax.swing.*;
import java.awt.*;

public class LoginSignUpFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> roleCombo;

    public LoginSignUpFrame() {
        setTitle("Smart Quiz Battle System - Login / SignUp");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel lblHeader = new JLabel("SMART QUIZ BATTLE SYSTEM", JLabel.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 20));
        lblHeader.setOpaque(true);
        lblHeader.setBackground(new Color(41, 128, 185));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setPreferredSize(new Dimension(450, 50));
        add(lblHeader, BorderLayout.NORTH);

        // Form Panel
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        panel.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        panel.add(txtUsername);

        panel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        panel.add(new JLabel("Role (For SignUp):"));
        roleCombo = new JComboBox<>(new String[]{"STUDENT", "ADMIN"});
        panel.add(roleCombo);

        JButton btnLogin = new JButton("Sign In (Login)");
        btnLogin.setBackground(new Color(39, 174, 96));
        btnLogin.setForeground(Color.WHITE);

        JButton btnSignUp = new JButton("Sign Up (Register)");
        btnSignUp.setBackground(new Color(243, 156, 18));
        btnSignUp.setForeground(Color.WHITE);

        panel.add(btnLogin);
        panel.add(btnSignUp);
        add(panel, BorderLayout.CENTER);

        // Secure & Professional Login Action
        btnLogin.addActionListener(e -> {
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();

            // 1. If any fields are empty.
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in both username and password fields!",
                        "Access Denied",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. verify database
            Player player = DatabaseDAO.loginUser(user, pass);

            if (player != null) {
                // Successful Login
                JOptionPane.showMessageDialog(this,
                        "Login Successful!\nWelcome back, " + player.getUsername() + " (" + player.getRole() + ")",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();

                if (player.getRole().equalsIgnoreCase("ADMIN")) {
                    new AdminFrame(player).setVisible(true);
                } else {
                    new DashboardFrame(player).setVisible(true);
                }
            } else {
                // Strict Professional Error Handling for Wrong Credentials
                JOptionPane.showMessageDialog(this,
                        "Invalid Username or Password!\nPlease check your credentials and try again.",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE);

                // Security ke liye password field ko turant clear kar dein
                txtPassword.setText("");
                txtPassword.requestFocus();
            }
        });

        // Professional SignUp Button Action
        btnSignUp.addActionListener(e -> {
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();
            String role = (String) roleCombo.getSelectedItem();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Username and password cannot be empty!",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = DatabaseDAO.registerUser(user, pass, role);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Registration Successful!\nYou can now sign in using your credentials.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                txtUsername.setText("");
                txtPassword.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        "This username is already taken! Please choose another.",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}