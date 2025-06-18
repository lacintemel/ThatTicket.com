package view;

import javax.swing.*;
import java.awt.*;

public class CircularProgressIndicator extends JDialog {
    public CircularProgressIndicator(Frame parent, String message) {
        super(parent, true);
        setUndecorated(true);
        setBackground(new Color(0,0,0,80)); // Yarı saydam arka plan

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(new Color(40, 40, 40));

        CircularProgressIndicatorDesign spinner = new CircularProgressIndicatorDesign();
        spinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        spinner.setPreferredSize(new Dimension(60, 60));

        panel.add(label);
        panel.add(Box.createVerticalStrut(20));
        panel.add(spinner);

        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    // Modern dönen daire göstergesi
    static class CircularProgressIndicatorDesign extends JComponent {
        private float angle = 0f;
        private final Timer timer;
        public CircularProgressIndicatorDesign() {
            setOpaque(false);
            timer = new Timer(16, e -> {
                angle += 6f;
                if (angle >= 360f) angle -= 360f;
                repaint();
            });
            timer.start();
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight()) - 8;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(52, 152, 219));
            g2.drawArc(x, y, size, size, (int)angle, 270);
            g2.setStroke(oldStroke);
            g2.dispose();
        }
        @Override
        public void addNotify() {
            super.addNotify();
            timer.start();
        }
        @Override
        public void removeNotify() {
            timer.stop();
            super.removeNotify();
        }
    }
} 