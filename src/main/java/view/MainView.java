package view;

import javax.swing.*;
import java.awt.*;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;
import models.Customer;
import models.Voyage;
import services.DatabaseService;
import services.Admin;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.util.Date;
import models.User;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;

public class MainView extends JPanel {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private JList<String> busList;
    private JPanel mainPanel;
    private JPanel tripsPanel;
    private JPanel reservationsPanel;
    private JPanel reservationsCardListPanel;
    private Customer customer;
    private JLabel dynamicTitle;
    private String currentTab = "TRIPS";
    private List<Voyage> allTrips = new ArrayList<>();
    private JPanel cardListPanel;
    private boolean isBusMode;
    private JFrame mainFrame;
    private JComboBox<String> originCombo, destCombo;
    private JDateChooser dateChooser;
    private JLabel noVoyagesLabel;
    private boolean isAdmin;
    private JPanel notificationsPanel;
    private User user;  // Store the original user object

    public MainView(User user, boolean isBusMode, JFrame mainFrame) {
        this.user = user;  // Store the original user
        this.customer = (user instanceof Customer) ? (Customer) user : null;
        this.isAdmin = (user instanceof Admin) || (user instanceof Customer && "Admin".equalsIgnoreCase(((Customer) user).getUser_type()));
        this.isBusMode = isBusMode;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        if (isBusMode) {
            allTrips = new ArrayList<>(DatabaseService.getAllBusVoyages());
        } else {
            allTrips = new ArrayList<>(DatabaseService.getAllFlightVoyages());
        }
        
        // Sort voyages by date
        allTrips.sort((v1, v2) -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date1 = sdf.parse(v1.getStartTime());
                Date date2 = sdf.parse(v2.getStartTime());
                return date1.compareTo(date2);
            } catch (Exception e) {
                return 0;
            }
        });

        Color mainBg = new Color(245,247,250);
        // Üst panel: sol (sign out), orta (başlık), sağ (toggle)
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(mainBg);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBackground(mainBg);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        
        // Left panel for buttons
        JPanel leftButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftButtonsPanel.setOpaque(false);
        
        // User type label
        JLabel userTypeLabel = new JLabel(isAdmin ? "👑 Admin" : "👤 Müşteri");
        userTypeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userTypeLabel.setForeground(new Color(70, 70, 70));
        leftButtonsPanel.add(userTypeLabel);
        
        // Sign out butonu
        JButton signOutBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(231, 76, 60)); // Kırmızı arka plan
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        try {
            URL iconUrl = new URL("https://img.icons8.com/ios/50/FFFFFF/exit--v1.png");
            Image img = ImageIO.read(iconUrl);
            Image scaledImg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            signOutBtn.setIcon(new ImageIcon(scaledImg));
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL for exit icon: " + e.getMessage());
            signOutBtn.setText("🚪"); // URL hatalıysa varsayılan metni göster
        } catch (IOException ex) {
            System.err.println("Exit icon could not be loaded from URL: " + ex.getMessage());
            signOutBtn.setText("🚪"); // Yüklenemezse varsayılan metni göster
        }

        signOutBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        signOutBtn.setForeground(Color.WHITE); // Beyaz ikon
        signOutBtn.setBorderPainted(false);
        signOutBtn.setFocusPainted(false);
        signOutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signOutBtn.setPreferredSize(new Dimension(40, 38));
        signOutBtn.setToolTipText("Çıkış Yap");
        signOutBtn.addActionListener(e -> {
            if (mainFrame instanceof com.mycompany.aoopproject.AOOPProject) {
                com.mycompany.aoopproject.AOOPProject aoopProjectFrame = (com.mycompany.aoopproject.AOOPProject) mainFrame;
                aoopProjectFrame.showLogin();
            }
        });
        leftButtonsPanel.add(signOutBtn);

        // Delete Account butonu
        JButton deleteAccountBtn = new JButton("Delete Account") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(231, 76, 60).darker(),
                    getWidth(), getHeight(), new Color(231, 76, 60)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        deleteAccountBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteAccountBtn.setForeground(Color.WHITE);
        deleteAccountBtn.setBorderPainted(false);
        deleteAccountBtn.setFocusPainted(false);
        deleteAccountBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteAccountBtn.setPreferredSize(new Dimension(140, 38));
        deleteAccountBtn.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        deleteAccountBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Hesabınızı silmek istediğinizden emin misiniz? Bu işlem geri alınamaz!",
                "Hesap Silme Onayı",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String userId;
                    if (isAdmin) {
                        // Admin için kullanıcı ID'sini email ile bul
                        User adminUser = services.DatabaseService.getUserByEmail(user.getEmail());
                        if (adminUser != null) {
                            userId = adminUser.getId();
                        } else {
                            JOptionPane.showMessageDialog(this, "Admin kullanıcısı bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else {
                        userId = customer.getId();
                    }
                    
                    // Önce rezervasyonları sil
                    services.DatabaseService.deleteReservationsByUserId(Integer.parseInt(userId));
                    
                    // Sonra kullanıcıyı sil
                    services.DatabaseService.deleteUser(userId);
                    
                    JOptionPane.showMessageDialog(this, "Hesabınız başarıyla silindi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    if (mainFrame instanceof com.mycompany.aoopproject.AOOPProject) {
                        ((com.mycompany.aoopproject.AOOPProject) mainFrame).showLogin();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Hesap silinirken bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        leftButtonsPanel.add(deleteAccountBtn);
        
        headerPanel.add(leftButtonsPanel, BorderLayout.WEST);

        // Başlık
        dynamicTitle = new JLabel(isBusMode ? "Otobüs Seferleri" : "Uçak Seferleri", SwingConstants.CENTER);
        dynamicTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(dynamicTitle, BorderLayout.CENTER);

        // Transport mode toggle
        JToggleButton toggleButton = new JToggleButton(isBusMode ? "Switch to Airplane" : "Switch to Bus") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, isBusMode ? new Color(52, 152, 219).darker() : new Color(231, 76, 60).darker(),
                    getWidth(), getHeight(), !isBusMode ? new Color(52, 152, 219) : new Color(231, 76, 60)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        toggleButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        toggleButton.setForeground(Color.WHITE);
        toggleButton.setBorderPainted(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.setPreferredSize(new Dimension(150, 38));
        toggleButton.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        toggleButton.addActionListener(e -> {
            if (mainFrame instanceof com.mycompany.aoopproject.AOOPProject) {
                com.mycompany.aoopproject.AOOPProject frame = (com.mycompany.aoopproject.AOOPProject) mainFrame;
                LoadingDialog loading = new LoadingDialog(frame, "Yükleniyor, lütfen bekleyin...");
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        frame.showMainView(new MainView(customer != null ? customer : user, !isBusMode, frame));
                        return null;
                    }
                    @Override
                    protected void done() {
                        loading.dispose();
                    }
                };
                worker.execute();
                loading.setVisible(true);
            }
        });
        headerPanel.add(toggleButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main panel (center area)
        mainPanel = new JPanel(new CardLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Trips panel (with search and filters)
        tripsPanel = new JPanel(new BorderLayout(10, 10));
        tripsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filter panel (top)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Drop shadow
                g2.setColor(new Color(0,0,0,18));
                g2.fillRoundRect(4, 4, getWidth()-8, getHeight()-8, 18, 18);
                // Panel bg (match header gray)
                g2.setColor(mainBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        filterPanel.setOpaque(false);
        filterPanel.setBackground(mainBg);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        // City selection
        String[] turkishCities = {
            "Select City",
            "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin", "Aydın", "Balıkesir",
            "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli",
            "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane",
            "Hakkari", "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir", "Kars", "Kastamonu", "Kayseri", "Kırklareli",
            "Kırşehir", "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş",
            "Nevşehir", "Niğde", "Ordu", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat",
            "Trabzon", "Tunceli", "Şanlıurfa", "Uşak", "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman",
            "Kırıkkale", "Batman", "Şırnak", "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye", "Düzce"
        };

        originCombo = new JComboBox<>(turkishCities);
        destCombo = new JComboBox<>(turkishCities);
        dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(200, 30));
        
        // Set initial date to today after dateChooser is initialized
        dateChooser.setDate(new java.util.Date());

        filterPanel.add(new JLabel("Kalkış:"));
        filterPanel.add(originCombo);
        filterPanel.add(new JLabel("Varış:"));
        filterPanel.add(destCombo);
        filterPanel.add(new JLabel("Tarih:"));
        filterPanel.add(dateChooser);

        // Notify Me button
        JButton notifyButton = new JButton("Notify Me") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, isBusMode ? new Color(52, 152, 219).darker() : new Color(220, 53, 69).darker(),
                    getWidth(), getHeight(), isBusMode ? new Color(52, 152, 219) : new Color(220, 53, 69)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        notifyButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        notifyButton.setForeground(Color.WHITE);
        notifyButton.setBorderPainted(false);
        notifyButton.setFocusPainted(false);
        notifyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        notifyButton.setPreferredSize(new Dimension(120, 38));
        
        // Only show notify button for regular customers
        notifyButton.setVisible(!isAdmin && customer != null);
        
        notifyButton.addActionListener(e -> {
            String selectedOrigin = (String) originCombo.getSelectedItem();
            String selectedDest = (String) destCombo.getSelectedItem();
            Date selectedDate = dateChooser.getDate();

            if (selectedOrigin.equals("Select City") || selectedDest.equals("Select City") || selectedDate == null) {
                JOptionPane.showMessageDialog(this,
                    "Lütfen kalkış, varış ve tarih bilgilerini seçin!",
                    "Uyarı",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (customer == null) {
                JOptionPane.showMessageDialog(this,
                    "Bildirim tercihleri sadece müşteri hesapları için geçerlidir.",
                    "Bilgi",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // İstenen seferi ekle
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String schedule = sdf.format(selectedDate);

            try {
                System.out.println("\n=== Bildirim Tercihi Kaydediliyor ===");
                System.out.println("Müşteri ID: " + customer.getId());
                System.out.println("Müşteri Adı: " + customer.getName());
                System.out.println("Sefer Tipi: " + (isBusMode ? "Bus" : "Flight"));
                System.out.println("Kalkış: " + selectedOrigin);
                System.out.println("Varış: " + selectedDest);
                System.out.println("Tarih: " + schedule);

                // Müşteri nesnesine ekle
                customer.addDesiredVoyage(
                    isBusMode ? "Bus" : "Flight",
                    selectedOrigin,
                    selectedDest,
                    schedule
                );

                System.out.println("✅ Bildirim tercihi başarıyla kaydedildi!");

                JOptionPane.showMessageDialog(this,
                    "Seçtiğiniz rotaya yeni sefer eklendiğinde bildirim alacaksınız!",
                    "Bildirim Kaydı",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                System.err.println("❌ Bildirim tercihi kaydedilirken hata oluştu: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(notifyButton);

        // Add action listeners for filtering
        originCombo.addActionListener(e -> validateCities());
        destCombo.addActionListener(e -> validateCities());
        dateChooser.addPropertyChangeListener("date", e -> updateTripList());

        tripsPanel.add(filterPanel, BorderLayout.NORTH);

        // Card list panel for trips
        cardListPanel = new JPanel();
        cardListPanel.setLayout(new BoxLayout(cardListPanel, BoxLayout.Y_AXIS));
        cardListPanel.setBackground(new Color(250, 252, 255));
        JScrollPane scrollPane = new JScrollPane(cardListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        scrollPane.getViewport().setBackground(new Color(250, 252, 255));
        tripsPanel.setOpaque(false);
        tripsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JPanel tripsPanelBg = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,15));
                g2.fillRoundRect(8, 8, getWidth()-16, getHeight()-16, 24, 24);
                g2.setColor(new Color(245,247,250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tripsPanelBg.setOpaque(false);
        tripsPanelBg.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tripsPanelBg.add(filterPanel, BorderLayout.NORTH);
        tripsPanelBg.add(scrollPane, BorderLayout.CENTER);
        tripsPanel.removeAll();
        tripsPanel.add(tripsPanelBg, BorderLayout.CENTER);

        // No voyages label
        noVoyagesLabel = new JLabel("", SwingConstants.CENTER);
        noVoyagesLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        noVoyagesLabel.setForeground(new Color(100, 100, 100));
        noVoyagesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        noVoyagesLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        noVoyagesLabel.setVisible(false);
        cardListPanel.add(noVoyagesLabel);

        // Reservations panel (scrollable list)
        reservationsPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,15));
                g2.fillRoundRect(8, 8, getWidth()-16, getHeight()-16, 24, 24);
                g2.setColor(new Color(245,247,250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        reservationsPanel.setOpaque(false);
        reservationsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        reservationsCardListPanel = new JPanel();
        reservationsCardListPanel.setLayout(new BoxLayout(reservationsCardListPanel, BoxLayout.Y_AXIS));
        reservationsCardListPanel.setBackground(Color.WHITE);
        JScrollPane reservationsScrollPane = new JScrollPane(reservationsCardListPanel);
        reservationsScrollPane.setBorder(null);
        reservationsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        reservationsPanel.add(reservationsScrollPane, BorderLayout.CENTER);

        // Add both panels to mainPanel
        mainPanel.add(tripsPanel, "TRIPS");
        mainPanel.add(reservationsPanel, "RESERVATIONS");
        mainPanel.add(createNotificationsPanel(), "NOTIFICATIONS");

        // Tabbar (bottom, centered)
        JPanel tabbar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tabbar.setOpaque(false);
        Color tabGray = new Color(220, 222, 228);
        Color tabTextGray = new Color(100, 100, 100);
        Color tabActiveBg = new Color(120, 130, 145);
        Color tabActiveText = Color.WHITE;
        
        JButton btnTrips = new JButton("Seyahatler") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if ("TRIPS".equals(currentTab)) {
                    g2.setColor(tabActiveBg);
                    setForeground(tabActiveText);
                } else {
                    g2.setColor(Color.WHITE);
                    setForeground(tabTextGray);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                if (!"TRIPS".equals(currentTab)) {
                    g2.setColor(tabGray);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        btnTrips.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTrips.setBorderPainted(false);
        btnTrips.setFocusPainted(false);
        btnTrips.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTrips.setPreferredSize(new Dimension(140, 35));
        JButton btnReservations = new JButton("Rezervasyonlar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if ("RESERVATIONS".equals(currentTab)) {
                    g2.setColor(tabActiveBg);
                    setForeground(tabActiveText);
                } else {
                    g2.setColor(Color.WHITE);
                    setForeground(tabTextGray);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                if (!"RESERVATIONS".equals(currentTab)) {
                    g2.setColor(tabGray);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btnReservations.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReservations.setBorderPainted(false);
        btnReservations.setFocusPainted(false);
        btnReservations.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReservations.setPreferredSize(new Dimension(140, 35));
        
        // Notifications button
        JButton btnNotifications = new JButton("Bildirimler") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if ("NOTIFICATIONS".equals(currentTab)) {
                    g2.setColor(tabActiveBg);
                    setForeground(tabActiveText);
                } else {
                    g2.setColor(Color.WHITE);
                    setForeground(tabTextGray);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                if (!"NOTIFICATIONS".equals(currentTab)) {
                    g2.setColor(tabGray);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btnNotifications.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNotifications.setBorderPainted(false);
        btnNotifications.setFocusPainted(false);
        btnNotifications.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNotifications.setPreferredSize(new Dimension(140, 35));

        // Admin için "Yeni Sefer Ekle" butonu
        JButton addVoyageBtn = null;
        if (isAdmin) {
            addVoyageBtn = new JButton("Yeni Sefer Ekle") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // Drop shadow
                    g2.setColor(new Color(0,0,0,30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    // Gradient by mode
                    Color mainColor = isBusMode ? new Color(52, 152, 219) : new Color(220, 53, 69);
                    GradientPaint gp = new GradientPaint(0, 0, mainColor.darker(), getWidth(), getHeight(), mainColor);
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            addVoyageBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            addVoyageBtn.setForeground(Color.WHITE);
            addVoyageBtn.setBorderPainted(false);
            addVoyageBtn.setFocusPainted(false);
            addVoyageBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            addVoyageBtn.setPreferredSize(new Dimension(160, 32));
            addVoyageBtn.addActionListener(e -> {
                if (mainFrame instanceof com.mycompany.aoopproject.AOOPProject) {
                    com.mycompany.aoopproject.AOOPProject frame = (com.mycompany.aoopproject.AOOPProject) mainFrame;
                    // Ensure we pass the correct Admin instance
                    Admin admin = (Admin) user; // Use the 'user' field from MainView constructor
                    frame.showAdminPanel(admin, isBusMode);
                }
            });
            tabbar.add(btnTrips);
            tabbar.add(Box.createHorizontalStrut(12));
            tabbar.add(addVoyageBtn);
            tabbar.add(Box.createHorizontalStrut(12));
            tabbar.add(btnReservations);
            tabbar.add(Box.createHorizontalStrut(12));
            tabbar.add(btnNotifications);
        } else {
            tabbar.add(btnTrips);
            tabbar.add(Box.createHorizontalStrut(12));
            tabbar.add(btnReservations);
            tabbar.add(Box.createHorizontalStrut(12));
            tabbar.add(btnNotifications);
        }
        add(tabbar, BorderLayout.SOUTH);

        // Button actions to switch views
        btnTrips.addActionListener(e -> switchTab("TRIPS"));
        btnReservations.addActionListener(e -> switchTab("RESERVATIONS"));
        btnNotifications.addActionListener(e -> switchTab("NOTIFICATIONS"));

        // Show trips by default
        switchTab("TRIPS");
        updateTripList();

        // MainView arka planı için gradient
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, new Color(245,247,250), 0, getHeight(), new Color(220,230,245));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }

    private void validateCities() {
        String selectedOrigin = (String) originCombo.getSelectedItem();
        String selectedDest = (String) destCombo.getSelectedItem();
        
        if (selectedOrigin.equals("Select City") || selectedDest.equals("Select City")) {
            return; // Don't validate if either is not selected
        }
        
        if (selectedOrigin.equals(selectedDest)) {
            JOptionPane.showMessageDialog(this, "Kalkış ve varış şehirleri aynı olamaz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            destCombo.setSelectedItem("Select City");
        }
        updateTripList();
    }

    private void updateTripList() {
        String selectedOrigin = (String) originCombo.getSelectedItem();
        String selectedDest = (String) destCombo.getSelectedItem();
        java.util.Date selectedDate = dateChooser.getDate();

        cardListPanel.removeAll();
        boolean hasMatchingTrips = false;

        for (Voyage trip : allTrips) {
            boolean matches = true;

            // Check origin
            if (!selectedOrigin.equals("Select City") && !trip.getOrigin().equals(selectedOrigin)) {
                matches = false;
            }

            // Check destination
            if (!selectedDest.equals("Select City") && !trip.getDestination().equals(selectedDest)) {
                matches = false;
            }

            // Check date
            if (selectedDate != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String tripDate = trip.getStartTime().split(" ")[0];
                    if (!tripDate.equals(sdf.format(selectedDate))) {
                        matches = false;
                    }
                } catch (Exception e) {
                    matches = false;
                }
            }

            if (matches) {
                hasMatchingTrips = true;
                cardListPanel.add(new TripCardPanel(trip, customer, this, reservationsPanel));
                cardListPanel.add(Box.createVerticalStrut(10));
            }
        }

        // Show/hide no voyages message
        if (!hasMatchingTrips) {
            String message;
            if (selectedDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                String formattedDate = sdf.format(selectedDate);
                message = formattedDate + " tarihinde " + (isBusMode ? "otobüs" : "uçak") + " seferi bulunmamaktadır.";
            } else {
                message = "Seçilen kriterlere uygun " + (isBusMode ? "otobüs" : "uçak") + " seferi bulunmamaktadır.";
            }
            noVoyagesLabel.setText(message);
            noVoyagesLabel.setVisible(true);
            cardListPanel.add(noVoyagesLabel);
        } else {
            noVoyagesLabel.setVisible(false);
        }

        cardListPanel.revalidate();
        cardListPanel.repaint();
    }

    private void switchTab(String name) {
        currentTab = name;
        CardLayout cl = (CardLayout) (mainPanel.getLayout());
        cl.show(mainPanel, name);
        if (name.equals("TRIPS")) {
            dynamicTitle.setText(isBusMode ? "Otobüs Seferleri" : "Uçak Seferleri");
            updateTripList();
        } else if (name.equals("RESERVATIONS")) {
            dynamicTitle.setText("Rezervasyonlar");
            updateReservationsPanel(this.customer, reservationsPanel);
        } else if (name.equals("NOTIFICATIONS")) {
            dynamicTitle.setText("Bildirimler");
            updateNotifications();
        }
        
        // Update tabbar button colors
        revalidate();
        repaint();
    }

    // Rezervasyonlar panelini güncelleyen fonksiyon
    public void updateReservationsPanel(Customer customer, JPanel reservationsPanel) {
        System.out.println("Panel güncelleniyor...");
        reservationsCardListPanel.removeAll();
        
        if (customer == null && !isAdmin) {
            JLabel emptyLabel = new JLabel("Rezervasyonlar sadece müşteri hesapları için geçerlidir.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            reservationsCardListPanel.add(emptyLabel);
            reservationsCardListPanel.revalidate();
            reservationsCardListPanel.repaint();
            return;
        }

        java.util.List<services.DatabaseService.ReservationInfo> reservations;
        if (isAdmin) {
            // Admin için tüm rezervasyonları getir
            reservations = services.DatabaseService.getAllReservations();
        } else {
            // Normal kullanıcı için kendi rezervasyonlarını getir
            reservations = services.DatabaseService.getReservationsForUser(Integer.parseInt(customer.getId()));
        }

        System.out.println("Veritabanından rezervasyon sayısı: " + reservations.size());
        if (reservations.isEmpty()) {
            JLabel emptyLabel = new JLabel("Rezervasyonunuz bulunmamaktadır.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            reservationsCardListPanel.add(emptyLabel);
        } else {
            for (services.DatabaseService.ReservationInfo res : reservations) {
                Voyage voyage = Voyage.getVoyageHashMap().get(res.voyageId);
                if (voyage != null) {
                    System.out.println("Voyage: " + voyage.getVoyageId() + ", Seat: " + res.seatNumber);
                    ReservationCardPanel panel = new ReservationCardPanel(
                        customer,
                        voyage,
                        res.seatNumber,
                        res.gender,
                        voyage.getType(),
                        new ReservationsPanel(customer, user.getEmail(), isAdmin),
                        user.getEmail(),
                        this,
                        res.reservationDate
                    );
                    if (isAdmin) {
                        // Admin için kullanıcı bilgilerini ekle
                        panel.setUserInfo(res.userName, res.userEmail);
                    }
                    reservationsCardListPanel.add(panel);
                    reservationsCardListPanel.add(Box.createVerticalStrut(10));
                }
            }
        }
        reservationsCardListPanel.revalidate();
        reservationsCardListPanel.repaint();
    }

    public JList<String> getBusList() {
        return busList;
    }

    public void showSeatSelectionPanel(Voyage voyage, Customer customer) {
        mainPanel.removeAll();
        mainPanel.add(new SeatSelectionPanel(voyage.getVoyageId(), voyage.getSeatCount(), voyage.getSeatArrangement(), customer, this, reservationsPanel), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    private JPanel createNotificationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 245, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel with title and clear button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Tüm Bildirimler");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Clear notifications button
        JButton clearButton = new JButton("Bildirimleri Temizle") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        clearButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearButton.setForeground(Color.WHITE);
        clearButton.setBackground(new Color(0, 120, 212));
        clearButton.setBorderPainted(false);
        clearButton.setFocusPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(e -> {
            DatabaseService.clearNotificationsForUser(Integer.parseInt(customer.getId()));
            updateNotifications();
        });
        topPanel.add(clearButton, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Notifications panel
        JPanel notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));
        notificationsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(notificationsPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Update notifications immediately
        updateNotifications(notificationsPanel);

        return panel;
    }

    private void updateNotifications() {
        JPanel notificationsPanel = null;
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component subComp : panel.getComponents()) {
                    if (subComp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) subComp;
                        notificationsPanel = (JPanel) scrollPane.getViewport().getView();
                        break;
                    }
                }
            }
        }
        if (notificationsPanel != null) {
            updateNotifications(notificationsPanel);
        }
    }
    
    private void updateNotifications(JPanel notificationsPanel) {
        notificationsPanel.removeAll();

        if (customer != null) { // Only fetch notifications if a customer is logged in
            ArrayList<String[]> notifications = DatabaseService.getNotificationsForUser(Integer.parseInt(customer.getId()));
            if (notifications.isEmpty()) {
                JLabel noNotificationsLabel = new JLabel("Henüz bildiriminiz bulunmuyor.");
                noNotificationsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                noNotificationsLabel.setForeground(new Color(128, 128, 128));
                noNotificationsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                notificationsPanel.add(Box.createVerticalGlue());
                notificationsPanel.add(noNotificationsLabel);
                notificationsPanel.add(Box.createVerticalGlue());
            } else {
                for (String[] notification : notifications) {
                    NotificationCardPanel card = new NotificationCardPanel(notification[0], notification[1]);
                    card.setAlignmentX(Component.CENTER_ALIGNMENT);
                    notificationsPanel.add(card);
                    notificationsPanel.add(Box.createVerticalStrut(10));
                }
            }
        } else { // For Admin or if no customer is available
            JLabel noNotificationsLabel = new JLabel("Bildirimler bu hesap türü için geçerli değildir.");
            noNotificationsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noNotificationsLabel.setForeground(new Color(128, 128, 128));
            noNotificationsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            notificationsPanel.add(Box.createVerticalGlue());
            notificationsPanel.add(noNotificationsLabel);
            notificationsPanel.add(Box.createVerticalGlue());
        }

        notificationsPanel.revalidate();
        notificationsPanel.repaint();
    }

    public User getUser() {
        return user;
    }

    public boolean isBusMode() {
        return isBusMode;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    // Yeni metod: Rezervasyonlar sekmesine geri dönmek için
    public void showReservationsTab() {
        // Rezervasyonları güncelleyerek içeriğin doğru olduğundan emin ol
        updateReservationsPanel(customer, reservationsPanel);
        // CardLayout kullanarak rezervasyonlar panelini göster
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "Reservations");
    }
}