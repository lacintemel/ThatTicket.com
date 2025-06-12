package view;

import javax.swing.*;
import java.awt.*;

public class NotificationCardPanel extends JPanel {
    private String message;
    private String timestamp;
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(230, 230, 230);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Color TIME_COLOR = new Color(128, 128, 128);

    public NotificationCardPanel(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
        setLayout(new BorderLayout(10, 5));
        setBackground(CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Message label
        JLabel messageLabel = new JLabel("<html><body style='width: 300px'>" + message + "</body></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(TEXT_COLOR);
        add(messageLabel, BorderLayout.CENTER);

        // Time label
        JLabel timeLabel = new JLabel(timestamp);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(TIME_COLOR);
        add(timeLabel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw rounded rectangle background
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        
        // Draw border
        g2d.setColor(BORDER_COLOR);
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        
        g2d.dispose();
    }
} 