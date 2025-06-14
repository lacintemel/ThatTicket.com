package view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    private List<TransportModeChangeListener> listeners = new ArrayList<>();
    private final Color busColor = new Color(52, 152, 219); // Original blue
    private final Color planeColor = new Color(231, 76, 60); // Original red

    // Add new fields for smoke animation
    private List<SmokeParticle> particles;
    private Timer smokeTimer;
    private static final int NUM_PARTICLES = 30;

    private List<Cloud> clouds = new ArrayList<>();
    private Timer cloudTimer;
    private static final int NUM_CLOUDS = 5;

    public TransportPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(420, 520));
        setBackground(busColor); // Initial color

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
                Color currentColor = isBusMode ? busColor : planeColor;
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

        // Add toggle button action with immediate effect
        toggleButton.addActionListener(e -> {
            isBusMode = !isBusMode;
            toggleButton.setText(isBusMode ? "Switch to Airplane" : "Switch to Bus");
            setBackground(isBusMode ? busColor : planeColor);
            updateTransportMode();
            notifyTransportModeChanged();
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

        // Initialize smoke particles
        particles = new ArrayList<>();
        for (int i = 0; i < NUM_PARTICLES; i++) {
            particles.add(new SmokeParticle());
        }

        // Start smoke animation timer with proper visibility handling
        smokeTimer = new Timer(50, e -> {
            if (isVisible() && isShowing()) {
            updateParticles();
            repaint();
            }
        });
        smokeTimer.start();

        // Initialize clouds
        for (int i = 0; i < NUM_CLOUDS; i++) {
            clouds.add(new Cloud());
        }

        // Start cloud animation timer with proper visibility handling
        cloudTimer = new Timer(50, e -> {
            if (isVisible() && isShowing()) {
            updateClouds();
            repaint();
            }
        });
        cloudTimer.start();

        // Add component listener to handle visibility changes
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                if (!smokeTimer.isRunning()) {
                    smokeTimer.start();
                }
                if (!cloudTimer.isRunning()) {
                    cloudTimer.start();
                }
            }

            @Override
            public void componentHidden(java.awt.event.ComponentEvent e) {
                smokeTimer.stop();
                cloudTimer.stop();
            }
        });
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (smokeTimer != null) {
            smokeTimer.stop();
        }
        if (cloudTimer != null) {
            cloudTimer.stop();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (smokeTimer != null && !smokeTimer.isRunning()) {
            smokeTimer.start();
        }
        if (cloudTimer != null && !cloudTimer.isRunning()) {
            cloudTimer.start();
        }
    }

    private void updateParticles() {
        for (SmokeParticle particle : particles) {
            particle.update();
            if (particle.alpha <= 0) {
                particle.reset();
            }
        }
    }

    private class Cloud {
        float x, y;
        float speed;
        float size;
        float alpha;

        Cloud() {
            reset();
        }

        void reset() {
            size = (float) (Math.random() * 60) + 40;
            x = -size;
            y = (float) (Math.random() * (getHeight() / 3));
            speed = (float) (Math.random() * 1.5) + 0.5f;
            alpha = (float) (Math.random() * 0.3) + 0.2f;
        }

        void update() {
            x += speed;
            if (x > getWidth() + size) {
                reset();
            }
        }

        void draw(Graphics2D g2) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(Color.WHITE);
            
            // Draw cloud shape
            int[] xPoints = new int[8];
            int[] yPoints = new int[8];
            float centerX = x + size/2;
            float centerY = y + size/2;
            
            for (int i = 0; i < 8; i++) {
                double angle = Math.PI * 2 * i / 8;
                xPoints[i] = (int)(centerX + Math.cos(angle) * size/2);
                yPoints[i] = (int)(centerY + Math.sin(angle) * size/3);
            }
            
            g2.fillPolygon(xPoints, yPoints, 8);
            g2.fillOval((int)x, (int)y, (int)size, (int)(size/2));
            g2.fillOval((int)(x + size/3), (int)(y - size/4), (int)(size/2), (int)(size/2));
            g2.fillOval((int)(x + size/2), (int)(y), (int)(size/2), (int)(size/3));
        }
    }

    private void updateClouds() {
        for (Cloud cloud : clouds) {
            cloud.update();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background gradient
        Color currentColor = isBusMode ? busColor : planeColor;
        GradientPaint gradient = new GradientPaint(
            0, 0, currentColor,
            0, getHeight(), currentColor.darker()
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Get icon position
        Point iconPosition = iconLabel.getLocation();
        
        // Draw smoke particles only behind and slightly around the icon
        for (SmokeParticle particle : particles) {
            if (particle.y < iconPosition.y + iconLabel.getHeight()) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, particle.alpha));
                g2.setColor(new Color(255, 255, 255, 100));
                g2.fillOval(particle.x, particle.y, particle.size, particle.size);
            }
        }

        // Draw clouds
        for (Cloud cloud : clouds) {
            cloud.draw(g2);
        }

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

        g2.dispose();
    }

    // Add SmokeParticle inner class
    private class SmokeParticle {
        int x, y, size;
        float alpha;
        double speed;

        SmokeParticle() {
            reset();
        }

        void reset() {
            // Get icon position for particle starting point
            Point iconPosition = iconLabel.getLocation();
            int iconWidth = iconLabel.getWidth();
            int iconHeight = iconLabel.getHeight();
            
            // Start particles from the bottom of the icon
            size = (int) (Math.random() * 15) + 5;
            x = iconPosition.x + (int)(Math.random() * iconWidth);
            y = iconPosition.y + iconHeight;
            alpha = 0.3f;
            speed = Math.random() * 1.5 + 0.5;
        }

        void update() {
            y -= speed;
            alpha -= 0.008f;
            size += 0.3;
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

    public void addTransportModeChangeListener(TransportModeChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyTransportModeChanged() {
        for (TransportModeChangeListener listener : listeners) {
            listener.onTransportModeChanged(isBusMode);
        }
    }
}