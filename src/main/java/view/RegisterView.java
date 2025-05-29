package view;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;
import models.User;
import services.DatabaseService;

public class RegisterView extends JPanel {
    public JCheckBox adminCheckBox;
    public JTextField usernameField, emailField, adminCodeField;
    public JPasswordField passwordField;
    public JButton registerBtn;
    public JLabel signInLabel;
    private JPanel contentPanel;

    public RegisterView() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setPreferredSize(new Dimension(900, 520));

        // Sol Panel
        JPanel leftPanel = new JPanel(new BorderLayout()) {
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
        cardPanel.setPreferredSize(new Dimension(400, 500));
        cardPanel.setBounds(40, 40, 400, 500);

        // Başlık
        JLabel title = new JLabel("Register");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setBounds(0, 32, 400, 40);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(title);

        // Hoş geldin mesajı
        JLabel welcome = new JLabel("Create your account!");
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        welcome.setBounds(0, 75, 400, 28);
        welcome.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(welcome);

        // Username alanı
        usernameField = modernTextField("Name");
        usernameField.setBounds(40, 120, 320, 44);
        cardPanel.add(usernameField);

        // Email alanı
        emailField = modernTextField("Email");
        emailField.setBounds(40, 180, 320, 44);
        cardPanel.add(emailField);

        // Password alanı + göz ikonu
        passwordField = modernPasswordField("Password");
        passwordField.setBounds(40, 240, 320, 44);
        cardPanel.add(passwordField);

        // Admin olarak kaydol checkbox'ı
        adminCheckBox = new JCheckBox("I want to be an admin");
        adminCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        adminCheckBox.setOpaque(false);
        adminCheckBox.setBounds(40, 300, 320, 30);
        cardPanel.add(adminCheckBox);

        // Admin Code alanı
        adminCodeField = modernTextField("Admin Code");
        adminCodeField.setBounds(40, 340, 320, 44);
        adminCodeField.setVisible(false);
        cardPanel.add(adminCodeField);

        adminCheckBox.addActionListener(e -> {
            adminCodeField.setVisible(adminCheckBox.isSelected());
            cardPanel.revalidate();
            cardPanel.repaint();
        });

        // Register butonu
        registerBtn = new RoundedButtonModern("Register", new Color(52, 152, 219));
        registerBtn.setBounds(40, 400, 320, 44);
        registerBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            boolean isAdmin = adminCheckBox.isSelected();
            String adminCode = adminCodeField.getText();

            // Boş alan kontrolü
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Email formatı kontrolü
            if (!email.endsWith("@gmail.com")) {
                JOptionPane.showMessageDialog(this, "Email adresi @gmail.com ile bitmelidir!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Şifre uzunluğu kontrolü
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this, "Şifre en az 6 karakter olmalıdır!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Admin kodu kontrolü
            if (isAdmin) {
                if (adminCode.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Admin kodu gerekli!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!adminCode.equals("1111")) {
                    JOptionPane.showMessageDialog(this, "Geçersiz admin kodu!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Kullanıcı oluştur
            User user = new User(
                String.valueOf(System.currentTimeMillis()), // Basit bir ID oluştur
                username,
                email,
                password,
                isAdmin ? "Admin" : "Customer"
            );

            // Veritabanına kaydet
            if (DatabaseService.addUser(user)) {
                JOptionPane.showMessageDialog(this, "Kayıt başarılı!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                showLogin();
            } else {
                JOptionPane.showMessageDialog(this, "Kayıt sırasında bir hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });
        cardPanel.add(registerBtn);

        // Sign In linki
        signInLabel = new JLabel("Sign In");
        signInLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        signInLabel.setForeground(new Color(236, 93, 87));
        signInLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signInLabel.setBounds(170, 460, 60, 30);
        cardPanel.add(signInLabel);

        // Kartı ortala
        JPanel cardHolder = new JPanel(null);
        cardHolder.setOpaque(false);
        cardHolder.setPreferredSize(new Dimension(480, 520));
        cardHolder.add(cardPanel);
        leftPanel.add(cardHolder, BorderLayout.CENTER);

        // Sağ Panel
        TransportPanel transportPanel = new TransportPanel();

        // Ana Panel
        add(leftPanel, BorderLayout.WEST);
        add(transportPanel, BorderLayout.CENTER);
    }

    public JLabel getSignInLabel() { return signInLabel; }

    // Modern TextField
    private JTextField modernTextField(String placeholder) {
        return new RoundedTextField(placeholder);
    }

    // Modern PasswordField
    private JPasswordField modernPasswordField(String placeholder) {
        return new RoundedPasswordField(placeholder);
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
        private final Color bgColor;
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

    private void showLogin() {
        if (contentPanel != null) {
            contentPanel.removeAll();
            LoginView loginView = new LoginView();
            contentPanel.add(loginView.getLeftPanel(), BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }

    public void setContentPanel(JPanel panel) {
        this.contentPanel = panel;
    }
}