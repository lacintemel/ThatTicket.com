package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LoginPanel extends JPanel {
    private JLabel titleLabel;
    private JToggleButton toggleButton;
    private boolean isLoginMode = true;
    private final Color loginColor = new Color(46, 204, 113); // Original green
    private final Color registerColor = new Color(155, 89, 182); // Original purple
    private float animationProgress = 0f;
    private Timer animationTimer;
    private List<LoginModeChangeListener> listeners = new ArrayList<>();

    public LoginPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(420, 520));
        setBackground(loginColor); // Initial color

        // Create title label
        titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);

        // Create toggle button
        toggleButton = new JToggleButton("Switch to Register") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button background gradient
                Color currentColor = isLoginMode ? loginColor : registerColor;
                GradientPaint gp = new GradientPaint(0, 0, 
                    currentColor.darker(), 
                    getWidth(), getHeight(), 
                    currentColor);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                // Button border with glow effect
                g2.setColor(new Color(255, 255, 255, 50));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 25, 25);
                
                // Glow effect
                g2.setColor(new Color(255, 255, 255, 30));
                g2.setStroke(new BasicStroke(4f));
                g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 25, 25);
                
                // Text
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(text, x, y);
                
                g2.dispose();
            }
        };

        // Add toggle button action with immediate effect
        toggleButton.addActionListener(e -> {
            isLoginMode = !isLoginMode;
            toggleButton.setText(isLoginMode ? "Switch to Register" : "Switch to Login");
            setBackground(isLoginMode ? loginColor : registerColor);
            updateLoginMode();
            notifyLoginModeChanged();
        });

        // Add components to panel
        add(titleLabel, BorderLayout.NORTH);
        add(toggleButton, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw animated circles
        g2.setColor(new Color(255, 255, 255, 5));
        for (int i = 0; i < 5; i++) {
            float size = 100 + (i * 50);
            float x = getWidth() / 2 - size / 2;
            float y = getHeight() / 2 - size / 2;
            g2.drawOval((int)x, (int)y, (int)size, (int)size);
        }
        
        // Add subtle pattern overlay
        g2.setColor(new Color(255, 255, 255, 5));
        for (int i = 0; i < getWidth(); i += 30) {
            for (int j = 0; j < getHeight(); j += 30) {
                g2.fillOval(i, j, 2, 2);
            }
        }
        
        // Draw decorative lines
        g2.setColor(new Color(255, 255, 255, 10));
        g2.setStroke(new BasicStroke(1f));
        for (int i = 0; i < getWidth(); i += 40) {
            g2.drawLine(i, 0, i, getHeight());
        }
        for (int i = 0; i < getHeight(); i += 40) {
            g2.drawLine(0, i, getWidth(), i);
        }
    }

    private void updateLoginMode() {
        titleLabel.setText(isLoginMode ? "Login" : "Register");
        repaint();
    }

    public void addLoginModeChangeListener(LoginModeChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyLoginModeChanged() {
        for (LoginModeChangeListener listener : listeners) {
            listener.onLoginModeChanged(isLoginMode);
        }
    }

    public interface LoginModeChangeListener {
        void onLoginModeChanged(boolean isLoginMode);
    }
} 