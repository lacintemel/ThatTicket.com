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

public class MainView extends JPanel {
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

    public MainView(Customer customer, boolean isBusMode, JFrame mainFrame) {
        this.customer = customer;
        this.isBusMode = isBusMode;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        if (isBusMode) {
            allTrips = new ArrayList<>(DatabaseService.getAllBusVoyages());
        } else {
            allTrips = new ArrayList<>(DatabaseService.getAllFlightVoyages());
        }

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
        
        // Sign out butonu
        JButton signOutBtn = new JButton("Sign Out") {
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
        signOutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signOutBtn.setForeground(Color.WHITE);
        signOutBtn.setBorderPainted(false);
        signOutBtn.setFocusPainted(false);
        signOutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signOutBtn.setPreferredSize(new Dimension(120, 38));
        signOutBtn.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        signOutBtn.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.getContentPane().removeAll();
                mainFrame.getContentPane().add(new LoginView(), BorderLayout.CENTER);
                mainFrame.revalidate();
                mainFrame.repaint();
            }
        });
        headerPanel.add(signOutBtn, BorderLayout.WEST);

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
                        frame.showMainView(new MainView(customer, !isBusMode, frame));
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

        filterPanel.add(new JLabel("Kalkış:"));
        filterPanel.add(originCombo);
        filterPanel.add(new JLabel("Varış:"));
        filterPanel.add(destCombo);
        filterPanel.add(new JLabel("Tarih:"));
        filterPanel.add(dateChooser);

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
        updateReservationsPanel(customer, reservationsPanel);

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
        
        // Admin için "Yeni Sefer Ekle" butonu
        if (customer.getUser_type().equals("Admin")) {
            JButton addVoyageBtn = new JButton("Yeni Sefer Ekle") {
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
                    Admin admin = new Admin(customer.getId(), "1111", customer.getName(), customer.getEmail(), customer.getPassword());
                    frame.showAdminPanel(admin, isBusMode);
                }
            });
            tabbar.add(btnTrips);
            tabbar.add(Box.createHorizontalStrut(12));
            tabbar.add(addVoyageBtn);
            tabbar.add(Box.createHorizontalStrut(12));
            tabbar.add(btnReservations);
        } else {
            tabbar.add(btnTrips);
            tabbar.add(btnReservations);
        }
        add(tabbar, BorderLayout.SOUTH);

        // Button actions to switch views
        btnTrips.addActionListener(e -> switchTab("TRIPS"));
        btnReservations.addActionListener(e -> switchTab("RESERVATIONS"));

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
            String message = "Seçilen kriterlere uygun " + (isBusMode ? "otobüs" : "uçak") + " seferi bulunmamaktadır.";
            noVoyagesLabel.setText(message);
            noVoyagesLabel.setVisible(true);
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
        } else {
            dynamicTitle.setText("Rezervasyonlar");
        }
        // Update tabbar button colors
        revalidate();
        repaint();
    }

    // Rezervasyonlar panelini güncelleyen fonksiyon
    public void updateReservationsPanel(Customer customer, JPanel reservationsPanel) {
        System.out.println("Panel güncelleniyor...");
        reservationsCardListPanel.removeAll();
        java.util.List<services.DatabaseService.ReservationInfo> reservations = services.DatabaseService.getReservationsForUser(Integer.parseInt(customer.getId()));
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
                    ReservationCardPanel panel = new ReservationCardPanel(voyage, customer, res.seatNumber, res.gender, this, res.reservationDate);
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
}