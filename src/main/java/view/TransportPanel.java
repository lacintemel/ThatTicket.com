package view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class TransportPanel extends JPanel {
    private JLabel iconLabel;
    private JLabel sloganLabel;
    private boolean isBusMode = true;
    private ImageIcon busIcon;
    private ImageIcon planeIcon;
    private final String[] busSlogans = {
        "Travel with comfort and style!",
        "Your journey, our priority!",
        "Safe and reliable bus service!"
    };
    private final String[] planeSlogans = {
        "Fly high with us!",
        "Your wings to the world!",
        "Experience the sky!"
    };
    private int currentSloganIndex = 0;
    private float animationProgress = 0f;
    private Timer animationTimer;

    public TransportPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(420, 520));

        // Load icons
        try {
            URL busUrl = new URL("https://img.icons8.com/dusk/64/bus--v1.png");
            URL planeUrl = new URL("https://img.icons8.com/dusk/64/airplane-take-off.png");
            busIcon = new ImageIcon(busUrl);
            planeIcon = new ImageIcon(planeUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create modern toggle button
        JToggleButton toggleButton = new JToggleButton("Switch to Airplane") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button background gradient
                GradientPaint gp = new GradientPaint(0, 0, 
                    new Color(41, 128, 185), 
                    getWidth(), getHeight(), 
                    new Color(52, 152, 219));
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
        toggleButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        toggleButton.setForeground(Color.WHITE);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setBorderPainted(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.setPreferredSize(new Dimension(200, 45));

        // Create icon label with enhanced shadow effect
        iconLabel = new JLabel(busIcon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Enhanced shadow effect
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillOval(15, 15, getWidth()-30, getHeight()-30);
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillOval(10, 10, getWidth()-20, getHeight()-20);
                
                super.paintComponent(g);
                g2.dispose();
            }
        };
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create slogan label with modern font and animation
        sloganLabel = new JLabel(busSlogans[0]);
        sloganLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        sloganLabel.setForeground(Color.WHITE);
        sloganLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add toggle button action
        toggleButton.addActionListener(e -> {
            isBusMode = !isBusMode;
            toggleButton.setText(isBusMode ? "Switch to Airplane" : "Switch to Bus");
            startTransitionAnimation();
        });

        // Create center panel for icon and slogan
        JPanel centerPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw modern pattern
                g2.setColor(new Color(255, 255, 255, 5));
                for (int i = 0; i < getWidth(); i += 20) {
                    for (int j = 0; j < getHeight(); j += 20) {
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
        };
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);
        centerPanel.add(iconLabel, gbc);
        centerPanel.add(sloganLabel, gbc);

        // Add components to panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        topPanel.setOpaque(false);
        topPanel.add(toggleButton);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // Start slogan rotation timer
        Timer timer = new Timer(5000, e -> rotateSlogan());
        timer.start();
    }

    private void startTransitionAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        
        animationProgress = 0f;
        animationTimer = new Timer(16, e -> {
            animationProgress += 0.1f;
            if (animationProgress >= 1f) {
                animationProgress = 1f;
                animationTimer.stop();
                updateTransportMode();
            }
            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Modern gradient background with animation
        Color startColor = isBusMode ? new Color(41, 128, 185) : new Color(52, 152, 219);
        Color endColor = isBusMode ? new Color(52, 152, 219) : new Color(41, 128, 185);
        
        GradientPaint gp = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
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

    private void updateTransportMode() {
        iconLabel.setIcon(isBusMode ? busIcon : planeIcon);
        currentSloganIndex = 0;
        sloganLabel.setText(isBusMode ? busSlogans[0] : planeSlogans[0]);
        repaint();
    }

    private void rotateSlogan() {
        String[] slogans = isBusMode ? busSlogans : planeSlogans;
        currentSloganIndex = (currentSloganIndex + 1) % slogans.length;
        sloganLabel.setText(slogans[currentSloganIndex]);
    }

    public boolean isBusMode() {
        return isBusMode;
    }
} 