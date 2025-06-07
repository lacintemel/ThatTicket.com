package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.Border;
import models.User;
import models.Customer;
import services.DatabaseService;

public class LoginView extends JPanel {
    private final Color mainBlue = new Color(52, 152, 219);
    private final Color mainGreen = new Color(46, 204, 113);
    private final Color bgGray = new Color(245, 247, 250);
    private final Color borderGray = new Color(220, 220, 220);
    private final Font mainFont = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 28);
    private final Font tabFont = new Font("Segoe UI", Font.BOLD, 18);

    private JPanel leftPanel;
    private TransportPanel rightPanel;
    public JLabel signUpLabel;
    private JTextField emailField;
    private JPasswordField passwordField;
    private boolean isAnimating = false;
    private JButton signInBtn;

    public LoginView() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(900, 520));

        // Sol Panel: Modern Login/Register
        leftPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Hafif degrade arka plan
                GradientPaint gp = new GradientPaint(0, 0, new Color(245, 247, 250), getWidth(), getHeight(), new Color(230, 236, 245));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setPreferredSize(new Dimension(480, 520));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Kart Paneli
        JPanel cardPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
                g2.setColor(new Color(220, 220, 220, 80));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 32, 32);
                // Gölge efekti
                g2.setColor(new Color(0,0,0,18));
                g2.fillRoundRect(8, 8, getWidth()-16, getHeight()-16, 32, 32);
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(400, 410));
        cardPanel.setBounds(40, 40, 400, 410);
        cardPanel.setLayout(null);

        // Başlık
        JLabel helloLabel = new JLabel("Hello Again!");
        helloLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        helloLabel.setForeground(new Color(40, 40, 40));
        helloLabel.setBounds(0, 32, 400, 40);
        helloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(helloLabel);

        // Hoş geldin mesajı
        JLabel welcomeLabel = new JLabel("Wellcome back you've been missed!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        welcomeLabel.setForeground(new Color(100, 100, 100));
        welcomeLabel.setBounds(0, 75, 400, 28);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(welcomeLabel);

        // Email alanı
        emailField = modernTextField("Email");
        emailField.setBounds(40, 120, 320, 44);
        cardPanel.add(emailField);

        // Password alanı + göz ikonu
        passwordField = modernPasswordField("Password");
        passwordField.setBounds(40, 180, 320, 44);
        cardPanel.add(passwordField);
        JButton showPassBtn = new JButton("\uD83D\uDC41");
        showPassBtn.setBounds(320, 190, 30, 28);
        showPassBtn.setBorderPainted(false);
        showPassBtn.setContentAreaFilled(false);
        showPassBtn.setFocusPainted(false);
        showPassBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        showPassBtn.addActionListener(e -> passwordField.setEchoChar(passwordField.getEchoChar() == '\u2022' ? (char)0 : '\u2022'));
        cardPanel.add(showPassBtn);

        // Alt linkler paneli
        JPanel bottomLinks = new JPanel(null);
        bottomLinks.setOpaque(false);
        bottomLinks.setBounds(40, 235, 320, 30);
        // Sign Up (sol)
        signUpLabel = new JLabel("Sign Up");
        signUpLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        signUpLabel.setForeground(new Color(52, 152, 219));
        signUpLabel.setBounds(0, 0, 80, 30);
        signUpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bottomLinks.add(signUpLabel);
        // Recovery Password (sağ)
        JLabel recoveryLabel = new JLabel("Recovery Password");
        recoveryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        recoveryLabel.setForeground(new Color(120, 120, 120));
        recoveryLabel.setBounds(180, 0, 140, 30);
        recoveryLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        recoveryLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bottomLinks.add(recoveryLabel);
        cardPanel.add(bottomLinks);

        // Sağ Panel: TransportPanel
        rightPanel = new TransportPanel();

        // Sign In butonu
        signInBtn = modernButton("Sign In", rightPanel.isBusMode() ? mainBlue : new Color(231, 76, 60));
        signInBtn.setBounds(40, 290, 320, 54);
        signInBtn.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Email formatı kontrolü
            if (!email.endsWith("@gmail.com")) {
                JOptionPane.showMessageDialog(this, "Email adresi @gmail.com ile bitmelidir!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Veritabanından kullanıcıyı kontrol et
            User user = DatabaseService.getUserByEmail(email);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Kullanıcı bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Şifre kontrolü
            if (!user.getPassword().equals(password)) {
                JOptionPane.showMessageDialog(this, "Hatalı şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Giriş başarılı
            // Loading dialog başlat
            Container topLevel = this.getTopLevelAncestor();
            Frame parentFrame = topLevel instanceof Frame ? (Frame) topLevel : null;
            LoadingDialog loading = new LoadingDialog(parentFrame, "Giriş yapılıyor, lütfen bekleyin...");

            SwingWorker<User, Void> worker = new SwingWorker<>() {
                @Override
                protected User doInBackground() {
                    User user = services.DatabaseService.getUserByEmail(email);
                    try { Thread.sleep(500); } catch (InterruptedException ex) {}
                    return user;
                }
                @Override
                protected void done() {
                    User user = null;
                    try { user = get(); } catch (Exception ex) { ex.printStackTrace(); }
                    if (user == null) {
                        loading.dispose();
                        JOptionPane.showMessageDialog(LoginView.this, "Kullanıcı bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!user.getPassword().equals(password)) {
                        loading.dispose();
                        JOptionPane.showMessageDialog(LoginView.this, "Hatalı şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Başarılıysa önce ana ekrana geçişi başlat, sonra loading'i kapat
                    if (topLevel instanceof com.mycompany.aoopproject.AOOPProject frame) {
                        if (user instanceof services.Admin) {
                            frame.showAdminPanel((services.Admin) user, rightPanel.isBusMode());
                        } else {
                            Customer customer = new Customer(user.getId(), user.getName(), "", user.getEmail(), user.getPassword());
                            if (rightPanel.isBusMode()) {
                                frame.showMainView(new MainView(customer, true, frame));
                            } else {
                                frame.showMainView(new MainView(customer, false, frame));
                            }
                        }
                    }
                    loading.dispose();
                }
            };
            worker.execute();
            loading.setVisible(true);
        });
        cardPanel.add(signInBtn);

        // Kartı ortala
        JPanel cardHolder = new JPanel(null);
        cardHolder.setOpaque(false);
        cardHolder.setPreferredSize(new Dimension(480, 520));
        cardHolder.add(cardPanel);
        leftPanel.add(cardHolder, BorderLayout.CENTER);
        
        // Add transport mode change listener
        rightPanel.addTransportModeChangeListener(new TransportModeChangeListener() {
            @Override
            public void onTransportModeChanged(boolean isBusMode) {
                if (!isAnimating) {
                    animatePanelSwap();
                }
                // Update sign in button color based on transport mode
                if (signInBtn != null) {
                    ((RoundedButtonModern)signInBtn).setButtonColor(isBusMode ? mainBlue : new Color(231, 76, 60));
                }
            }
        });

        // Ana Panel
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    private void animatePanelSwap() {
        isAnimating = true;
        
        // Get current positions
        Point leftPos = leftPanel.getLocation();
        Point rightPos = rightPanel.getLocation();
        
        // Create animation timer with smoother animation
        Timer timer = new Timer(16, new ActionListener() {
            private int step = 0;
            private final int totalSteps = 40; // More steps for smoother animation
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (step < totalSteps) {
                    // Calculate new positions using improved easing function
                    double progress = (double) step / totalSteps;
                    // Custom easing function for more natural motion
                    double easedProgress;
                    if (progress < 0.2) {
                        // Start slow
                        easedProgress = 5 * progress * progress;
                    } else if (progress > 0.8) {
                        // End slow
                        easedProgress = 1 - 5 * (1 - progress) * (1 - progress);
                    } else {
                        // Middle fast
                        easedProgress = 0.2 + 0.6 * (progress - 0.2) / 0.6;
                    }
                    
                    int newLeftX = (int) (leftPos.x + (rightPos.x - leftPos.x) * easedProgress);
                    int newRightX = (int) (rightPos.x + (leftPos.x - rightPos.x) * easedProgress);
                    
                    // Enhanced vertical movement with easing
                    double verticalEase = Math.sin(progress * Math.PI);
                    int verticalOffset = (int) (verticalEase * 15);
                    
                    // Add slight rotation effect
                    double rotation = Math.sin(progress * Math.PI) * 5;
                    
                    // Apply transformations
                    leftPanel.setLocation(newLeftX, leftPos.y + verticalOffset);
                    rightPanel.setLocation(newRightX, rightPos.y - verticalOffset);
                    
                    // Apply subtle scaling effect
                    double scale = 1.0 - Math.abs(Math.sin(progress * Math.PI)) * 0.05;
                    leftPanel.setSize((int)(leftPanel.getWidth() * scale), leftPanel.getHeight());
                    rightPanel.setSize((int)(rightPanel.getWidth() * scale), rightPanel.getHeight());
                    
                    step++;
                    repaint();
                } else {
                    // Animation complete
                    ((Timer) e.getSource()).stop();
                    isAnimating = false;
                    
                    // Reset panel sizes
                    leftPanel.setSize(leftPanel.getPreferredSize());
                    rightPanel.setSize(rightPanel.getPreferredSize());
                    
                    // Update layout
                    removeAll();
                    if (rightPanel.isBusMode()) {
                        add(leftPanel, BorderLayout.WEST);
                        add(rightPanel, BorderLayout.EAST);
                    } else {
                        add(rightPanel, BorderLayout.WEST);
                        add(leftPanel, BorderLayout.EAST);
                    }
                    revalidate();
                    repaint();
                }
            }
        });
        
        timer.start();
    }

    public JPanel getLeftPanel() { return leftPanel; }
    public TransportPanel getRightPanel() { return rightPanel; }
    public JLabel getSignUpLabel() { return signUpLabel; }

    // Modern TextField
    private JTextField modernTextField(String placeholder) {
        return new RoundedTextField(placeholder);
    }

    // Modern PasswordField
    private JPasswordField modernPasswordField(String placeholder) {
        return new RoundedPasswordField(placeholder);
    }

    // Yuvarlatılmış Modern Button
    private JButton modernButton(String text, Color bgColor) {
        return new RoundedButtonModern(text, bgColor);
    }

    // Yuvarlatılmış TextField
    static class RoundedTextField extends JTextField {
        private String placeholder;
        public RoundedTextField(String placeholder) {
            super();
            this.placeholder = placeholder;
            setFont(new Font("Segoe UI", Font.PLAIN, 16));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Arka plan ve kenarlık
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
            g2.setColor(new Color(220, 220, 220));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 28, 28);
            // Placeholder
            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(new Color(180, 180, 180));
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                Insets ins = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(placeholder, ins.left + 2, y);
            }
            g2.dispose();
            super.paintComponent(g);
        }
        @Override
        public void setBorder(Border border) {
            super.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        }
    }

    // Yuvarlatılmış PasswordField
    static class RoundedPasswordField extends JPasswordField {
        private String placeholder;
        public RoundedPasswordField(String placeholder) {
            super();
            this.placeholder = placeholder;
            setFont(new Font("Segoe UI", Font.PLAIN, 16));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
            setEchoChar('\u2022');
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Arka plan ve kenarlık
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
            g2.setColor(new Color(220, 220, 220));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 28, 28);
            // Placeholder
            if (getPassword().length == 0 && !isFocusOwner()) {
                g2.setColor(new Color(180, 180, 180));
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                Insets ins = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(placeholder, ins.left + 2, y);
            }
            g2.dispose();
            super.paintComponent(g);
        }
        @Override
        public void setBorder(Border border) {
            super.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        }
    }

    // Yuvarlatılmış Modern Button
    static class RoundedButtonModern extends JButton {
        private Color bgColor;
        public RoundedButtonModern(String text, Color bgColor) {
            super(text);
            this.bgColor = bgColor;
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 18));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        public void setButtonColor(Color newColor) {
            this.bgColor = newColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
            super.paintComponent(g2);
            g2.dispose();
        }
        @Override
        public void paint(Graphics g) {
            setOpaque(false);
            super.paint(g);
        }
    }
}

// Add this interface to TransportPanel class
interface TransportModeChangeListener {
    void onTransportModeChanged(boolean isBusMode);
}