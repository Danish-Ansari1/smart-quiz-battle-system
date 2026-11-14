import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginSignUpFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> roleCombo;
    private JButton btnLogin;
    private JButton btnSignUp;

    // Production Color Palette
    private final Color BG_COLOR = new Color(240, 244, 248);       // Soft light grey-blue background
    private final Color CARD_BG = Color.WHITE;                     // Pure white card
    private final Color PRIMARY_COLOR = new Color(37, 99, 235);    // Modern Royal Blue
    private final Color SUCCESS_COLOR = new Color(22, 163, 74);    // Clean Green
    private final Color SUCCESS_HOVER = new Color(21, 128, 61);
    private final Color WARNING_COLOR = new Color(234, 88, 12);    // Clean Orange
    private final Color WARNING_HOVER = new Color(194, 65, 12);
    private final Color TEXT_DARK = new Color(30, 41, 59);         // Slate Dark Text
    private final Color TEXT_MUTED = new Color(100, 116, 139);     // Muted Grey Text
    private final Color BORDER_DEFAULT = new Color(203, 213, 225); // Subtle input border

    public LoginSignUpFrame() {
        setTitle("Smart Quiz Battle System - Login / SignUp");
        setSize(900, 620); // Achha spacious default size (isko maximize bhi karenge toh UI kharab nahi hoga)
        setMinimumSize(new Dimension(500, 560));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);

        // GridBagLayout on main frame keeps the login card strictly centered even on full screen
        setLayout(new GridBagLayout());

        // Main Card Panel (Fixed size so components never stretch awkwardly)
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_BG);
        cardPanel.setPreferredSize(new Dimension(420, 490));
        cardPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(35, 40, 35, 40)
        ));

        // 1. App Title & Subtitle
        JLabel lblTitle = new JLabel("SMART QUIZ BATTLE");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Sign in to your account or register a new profile");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(TEXT_MUTED);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardPanel.add(lblTitle);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        cardPanel.add(lblSubtitle);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // 2. Username Field
        cardPanel.add(createFieldLabel("Username"));
        cardPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        txtUsername = new JTextField();
        styleInputField(txtUsername);
        cardPanel.add(txtUsername);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // 3. Password Field
        cardPanel.add(createFieldLabel("Password"));
        cardPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        txtPassword = new JPasswordField();
        styleInputField(txtPassword);
        cardPanel.add(txtPassword);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Show Password Checkbox
        JCheckBox chkShowPass = new JCheckBox("Show Password");
        chkShowPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkShowPass.setForeground(TEXT_MUTED);
        chkShowPass.setBackground(CARD_BG);
        chkShowPass.setFocusPainted(false);
        chkShowPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        char defaultEchoChar = txtPassword.getEchoChar();
        chkShowPass.addActionListener(e -> {
            if (chkShowPass.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar(defaultEchoChar);
            }
        });
        cardPanel.add(chkShowPass);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        // 4. Role Dropdown
        cardPanel.add(createFieldLabel("Role (Selected during Sign Up)"));
        cardPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        roleCombo = new JComboBox<>(new String[]{"STUDENT", "ADMIN"});
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleCombo.setBackground(Color.WHITE);
        roleCombo.setForeground(TEXT_DARK);
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        roleCombo.setPreferredSize(new Dimension(340, 38));
        roleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(roleCombo);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 28)));

        // 5. Buttons Panel (Side by Side, Clean Proportions)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setBackground(CARD_BG);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        buttonPanel.setPreferredSize(new Dimension(340, 42));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin = createStyledButton("Sign In", SUCCESS_COLOR, SUCCESS_HOVER);
        btnSignUp = createStyledButton("Register", WARNING_COLOR, WARNING_HOVER);

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnSignUp);
        cardPanel.add(buttonPanel);

        // Add card to center of frame
        add(cardPanel, new GridBagConstraints());

        // Enter key dabane par seedha Sign In trigger hoga
        getRootPane().setDefaultButton(btnLogin);

        // ================= ACTIONS =================
        setupActions();
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleInputField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_DARK);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setPreferredSize(new Dimension(340, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        CompoundBorder normalBorder = new CompoundBorder(
                new LineBorder(BORDER_DEFAULT, 1, true),
                new EmptyBorder(5, 12, 5, 12)
        );
        CompoundBorder focusBorder = new CompoundBorder(
                new LineBorder(PRIMARY_COLOR, 2, true),
                new EmptyBorder(4, 11, 4, 11)
        );

        field.setBorder(normalBorder);

        // Input box par click karne par blue highlight aayega
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(focusBorder);
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(normalBorder);
            }
        });
    }

    private JButton createStyledButton(String text, Color baseColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
        return button;
    }

    private void setupActions() {
        // Secure & Professional Login Action
        btnLogin.addActionListener(e -> {
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in both username and password fields!",
                        "Access Denied",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Player player = DatabaseDAO.loginUser(user, pass);

            if (player != null) {
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
                JOptionPane.showMessageDialog(this,
                        "Invalid Username or Password!\nPlease check your credentials and try again.",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE);

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