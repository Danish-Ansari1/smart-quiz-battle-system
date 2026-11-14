import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class WelcomeFrame extends JFrame {

    // ---------- Design Tokens ----------
    private static final Color NAVY_DARK   = new Color(15, 23, 42);
    private static final Color NAVY_MID    = new Color(30, 41, 99);
    private static final Color BLUE_DEEP   = new Color(30, 64, 175);
    private static final Color PRIMARY     = new Color(37, 99, 235);
    private static final Color GREEN_A     = new Color(34, 197, 94);
    private static final Color GREEN_B     = new Color(22, 163, 74);
    private static final Color TEXT_DARK   = new Color(15, 23, 42);
    private static final Color TEXT_MUTED  = new Color(100, 116, 139);
    private static final Color SOFT_BG     = new Color(248, 250, 252);
    private static final Color SOFT_BORDER = new Color(226, 232, 240);

    private static final String FONT = "Segoe UI";

    public WelcomeFrame() {
        setTitle("Smart Quiz Battle System - Enterprise Portal");
        setSize(1100, 760);
        setMinimumSize(new Dimension(900, 720));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Gradient root
        GradientBackground root = new GradientBackground();
        root.setLayout(new GridBagLayout());
        setContentPane(root);

        // Card
        RoundedPanel card = new RoundedPanel(28, Color.WHITE, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(34, 48, 30, 48));
        card.setPreferredSize(new Dimension(780, 650));

        // 1. Status row (version badge + live indicator)
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        topRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        //topRow.add(createBadge("v2.5 PRODUCTION EDITION"), BorderLayout.WEST);
        topRow.add(new LiveIndicator(), BorderLayout.EAST);
        card.add(topRow);
        card.add(Box.createVerticalStrut(22));

        // 2. Logo circle
        JLabel logo = new JLabel("🏆", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        JPanel logoWrap = new RoundedPanel(40, new Color(239, 246, 255), false);
        logoWrap.setLayout(new BorderLayout());
        logoWrap.add(logo, BorderLayout.CENTER);
        logoWrap.setPreferredSize(new Dimension(64, 64));
        logoWrap.setMaximumSize(new Dimension(64, 64));
        logoWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(logoWrap);
        card.add(Box.createVerticalStrut(14));

        // 3. Title + subtitle
        JLabel title = new JLabel("Smart Quiz Battle System");
        title.setFont(new Font(FONT, Font.BOLD, 30));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(8));

        JLabel sub = new JLabel("<html><div style='text-align:center;width:520px;'>"
                + "A next-generation multi-user assessment platform with live evaluation, "
                + "detailed answer sheets and real-time teacher analytics.</div></html>");
        sub.setFont(new Font(FONT, Font.PLAIN, 14));
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(24));

        // 4. Animated stats
        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 0));
        stats.setOpaque(false);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        stats.setAlignmentX(Component.CENTER_ALIGNMENT);
        //stats.add(createStat("Questions Bank", 500, "%,.0f+"));
        //stats.add(createStat("Active Students", 1200, "%,.0f+"));
        //stats.add(createStat("System Uptime", 99.9, "%.1f%%"));
        card.add(stats);
        card.add(Box.createVerticalStrut(22));

        // 5. Feature grid
        JPanel grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        grid.setAlignmentX(Component.CENTER_ALIGNMENT);
        grid.add(createFeature("⚡", "Live Quiz Engine", "Fullscreen mode with real-time countdown timer"));
        grid.add(createFeature("📊", "Admin Drilldown", "Inspect correct & wrong answers per student"));
        grid.add(createFeature("🛡️", "Role-Based Access", "Secure Student & Teacher authentication"));
        grid.add(createFeature("🔒", "Encrypted Storage", "SQL-backed data with live audit logs"));
        card.add(grid);
        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(20));

        // 6. CTA button
        GradientButton cta = new GradientButton("Enter Portal  (Sign In / Register)  →");
        cta.setAlignmentX(Component.CENTER_ALIGNMENT);
        cta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        cta.setPreferredSize(new Dimension(500, 52));
        cta.addActionListener(e -> {
            dispose();
            new LoginSignUpFrame().setVisible(true);
        });
        card.add(cta);
        card.add(Box.createVerticalStrut(14));

        // 7. Footer
        JLabel footer = new JLabel("© 2026 Smart Quiz Battle System  •  Secure • Fast • Reliable");
        footer.setFont(new Font(FONT, Font.PLAIN, 11));
        footer.setForeground(new Color(148, 163, 184));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(footer);

        root.add(card, new GridBagConstraints());
        getRootPane().setDefaultButton(cta);
    }

    // ---------- Component Builders ----------
    private JLabel createBadge(String text) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = AA(g);
                g2.setColor(new Color(239, 246, 255));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 24, 24));
                g2.setColor(new Color(191, 219, 254));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 24, 24));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(new Font(FONT, Font.BOLD, 11));
        l.setForeground(PRIMARY);
        l.setBorder(new EmptyBorder(5, 12, 5, 12));
        l.setOpaque(false);
        return l;
    }

    private JPanel createStat(String label, double target, String fmt) {
        RoundedPanel box = new RoundedPanel(16, SOFT_BG, false);
        box.setBorderColor(SOFT_BORDER);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(12, 10, 12, 10));

        JLabel value = new JLabel(String.format(fmt, 0.0), SwingConstants.CENTER);
        value.setFont(new Font(FONT, Font.BOLD, 22));
        value.setForeground(PRIMARY);
        value.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel name = new JLabel(label.toUpperCase(), SwingConstants.CENTER);
        name.setFont(new Font(FONT, Font.BOLD, 10));
        name.setForeground(TEXT_MUTED);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(value);
        box.add(Box.createVerticalStrut(2));
        box.add(name);

        // Ease-out count-up animation
        final int frames = 50;
        final int[] f = {0};
        Timer t = new Timer(20, null);
        t.addActionListener(e -> {
            f[0]++;
            double p = 1 - Math.pow(1 - (double) f[0] / frames, 3);
            value.setText(String.format(fmt, target * p));
            if (f[0] >= frames) {
                value.setText(String.format(fmt, target));
                t.stop();
            }
        });
        t.setInitialDelay(300);
        t.start();
        return box;
    }

    private JPanel createFeature(String icon, String head, String desc) {
        RoundedPanel c = new RoundedPanel(16, SOFT_BG, false);
        c.setBorderColor(SOFT_BORDER);
        c.setLayout(new BorderLayout(12, 0));
        c.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel ic = new JLabel(icon, SwingConstants.CENTER);
        ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        RoundedPanel icWrap = new RoundedPanel(12, new Color(219, 234, 254), false);
        icWrap.setLayout(new BorderLayout());
        icWrap.add(ic, BorderLayout.CENTER);
        icWrap.setPreferredSize(new Dimension(42, 42));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel h = new JLabel(head);
        h.setFont(new Font(FONT, Font.BOLD, 13));
        h.setForeground(TEXT_DARK);
        JLabel d = new JLabel("<html><div style='width:170px;'>" + desc + "</div></html>");
        d.setFont(new Font(FONT, Font.PLAIN, 11));
        d.setForeground(TEXT_MUTED);
        txt.add(h);
        txt.add(Box.createVerticalStrut(3));
        txt.add(d);

        JPanel iconHolder = new JPanel(new GridBagLayout());
        iconHolder.setOpaque(false);
        iconHolder.add(icWrap);

        c.add(iconHolder, BorderLayout.WEST);
        c.add(txt, BorderLayout.CENTER);

        // Hover highlight
        c.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { c.setBorderColor(PRIMARY); c.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { c.setBorderColor(SOFT_BORDER); c.repaint(); }
        });
        return c;
    }

    private static Graphics2D AA(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }

    // ---------- Custom Components ----------
    private static class GradientBackground extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = AA(g);
            int w = getWidth(), h = getHeight();
            g2.setPaint(new GradientPaint(0, 0, NAVY_DARK, w, h, BLUE_DEEP));
            g2.fillRect(0, 0, w, h);
            // Decorative translucent circles
            g2.setColor(new Color(59, 130, 246, 40));
            g2.fill(new Ellipse2D.Double(-120, -120, 420, 420));
            g2.setColor(new Color(34, 197, 94, 30));
            g2.fill(new Ellipse2D.Double(w - 300, h - 260, 480, 480));
            g2.setColor(new Color(255, 255, 255, 14));
            g2.fill(new Ellipse2D.Double(w * 0.7, -80, 260, 260));
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color fill;
        private final boolean shadow;
        private Color borderColor = null;

        RoundedPanel(int arc, Color fill, boolean shadow) {
            this.arc = arc; this.fill = fill; this.shadow = shadow;
            setOpaque(false);
        }
        void setBorderColor(Color c) { this.borderColor = c; }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = AA(g);
            int w = getWidth(), h = getHeight();
            if (shadow) {
                for (int i = 0; i < 14; i++) {
                    g2.setColor(new Color(0, 0, 0, 5));
                    g2.fill(new RoundRectangle2D.Float(i, i + 6, w - 2 * i, h - 2 * i, arc + 6, arc + 6));
                }
            }
            int inset = shadow ? 14 : 0;
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(inset, inset - (shadow ? 8 : 0),
                    w - 2 * inset - 1, h - 2 * inset - 1 + (shadow ? 8 : 0), arc, arc));
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, arc, arc));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class LiveIndicator extends JComponent {
        private float phase = 0;
        LiveIndicator() {
            setPreferredSize(new Dimension(130, 24));
            new Timer(40, e -> { phase += 0.12f; repaint(); }).start();
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = AA(g);
            float pulse = (float) (Math.sin(phase) * 0.5 + 0.5);
            int cy = getHeight() / 2;
            g2.setColor(new Color(34, 197, 94, (int) (60 * pulse)));
            g2.fill(new Ellipse2D.Float(8, cy - 9, 18, 18));
            g2.setColor(GREEN_A);
            g2.fill(new Ellipse2D.Float(13, cy - 4, 8, 8));
            g2.setColor(TEXT_MUTED);
            g2.setFont(new Font(FONT, Font.BOLD, 11));
            g2.drawString("System Online", 32, cy + 4);
            g2.dispose();
        }
    }

    private static class GradientButton extends JButton {
        private float hover = 0f;
        private boolean pressed = false;
        private final Timer anim;
        private boolean hovering = false;

        GradientButton(String text) {
            super(text);
            setFont(new Font(FONT, Font.BOLD, 15));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            anim = new Timer(15, e -> {
                hover += hovering ? 0.12f : -0.12f;
                hover = Math.max(0f, Math.min(1f, hover));
                repaint();
                if (hover == 0f || hover == 1f) ((Timer) e.getSource()).stop();
            });
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovering = true; anim.start(); }
                @Override public void mouseExited(MouseEvent e)  { hovering = false; anim.start(); }
                @Override public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = AA(g);
            int w = getWidth(), h = getHeight();
            Color a = blend(GREEN_A, GREEN_B, hover);
            Color b = blend(GREEN_B, new Color(21, 128, 61), hover);
            int off = pressed ? 2 : 0;
            // Soft glow on hover
            g2.setColor(new Color(34, 197, 94, (int) (45 * hover)));
            g2.fill(new RoundRectangle2D.Float(0, 4, w, h - 2, 18, 18));
            g2.setPaint(new GradientPaint(0, 0, a, w, 0, b));
            g2.fill(new RoundRectangle2D.Float(0, off, w, h - 4, 16, 16));
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), (w - fm.stringWidth(getText())) / 2,
                    (h - 4 - fm.getHeight()) / 2 + fm.getAscent() + off);
            g2.dispose();
        }

        private static Color blend(Color c1, Color c2, float t) {
            return new Color(
                    (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * t),
                    (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t),
                    (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * t));
        }
    }

    // Quick test
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeFrame().setVisible(true));
    }
}