package view;

import javax.swing.*;
import java.awt.*;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
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

        // Üst panel: sol (sign out), orta (başlık), sağ (toggle)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        // Sign out butonu
        JButton signOutBtn = new JButton("Sign Out");
        signOutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signOutBtn.setForeground(Color.WHITE);
        signOutBtn.setBackground(new Color(231, 76, 60));
        signOutBtn.setBorderPainted(false);
        signOutBtn.setFocusPainted(false);
        signOutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        JToggleButton toggleButton = new JToggleButton(isBusMode ? "Switch to Airplane" : "Switch to Bus");
        toggleButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        toggleButton.setForeground(Color.WHITE);
        toggleButton.setBackground(isBusMode ? new Color(231, 76, 60) : new Color(52, 152, 219));
        toggleButton.setBorderPainted(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.addActionListener(e -> {
            if (mainFrame instanceof com.mycompany.aoopproject.AOOPProject) {
                com.mycompany.aoopproject.AOOPProject frame = (com.mycompany.aoopproject.AOOPProject) mainFrame;
                frame.showMainView(new MainView(customer, !isBusMode, frame));
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
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);

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
        cardListPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(cardListPanel);
        tripsPanel.add(scrollPane, BorderLayout.CENTER);

        // No voyages label
        noVoyagesLabel = new JLabel("", SwingConstants.CENTER);
        noVoyagesLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        noVoyagesLabel.setForeground(new Color(100, 100, 100));
        noVoyagesLabel.setVisible(false);
        cardListPanel.add(noVoyagesLabel);

        // Reservations panel (placeholder)
        reservationsPanel = new JPanel(new BorderLayout());
        JLabel reservationsLabel = new JLabel("Rezervasyonlarınız burada görünecek.", SwingConstants.CENTER);
        reservationsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        reservationsPanel.add(reservationsLabel, BorderLayout.CENTER);

        // Add both panels to mainPanel
        mainPanel.add(tripsPanel, "TRIPS");
        mainPanel.add(reservationsPanel, "RESERVATIONS");

        // Tabbar (bottom, centered)
        JPanel tabbar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnTrips = new JButton("Seyahatler");
        JButton btnReservations = new JButton("Rezervasyonlar");
        tabbar.add(btnTrips);
        
        // Admin için "Yeni Sefer Ekle" butonu
        if (customer.getUser_type().equals("Admin")) {
            JButton addVoyageBtn = new JButton("Yeni Sefer Ekle");
            addVoyageBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            addVoyageBtn.setForeground(Color.WHITE);
            addVoyageBtn.setBackground(new Color(46, 204, 113));
            addVoyageBtn.setBorderPainted(false);
            addVoyageBtn.setFocusPainted(false);
            addVoyageBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            addVoyageBtn.setPreferredSize(new Dimension(150, 40));
            addVoyageBtn.addActionListener(e -> {
                if (mainFrame instanceof com.mycompany.aoopproject.AOOPProject) {
                    com.mycompany.aoopproject.AOOPProject frame = (com.mycompany.aoopproject.AOOPProject) mainFrame;
                    Admin admin = new Admin(customer.getId(), "1111", customer.getName(), customer.getEmail(), customer.getPassword());
                    frame.showAdminPanel(admin, true);
                }
            });
            tabbar.add(addVoyageBtn);
        }
        
        tabbar.add(btnReservations);
        add(tabbar, BorderLayout.SOUTH);

        // Button actions to switch views
        btnTrips.addActionListener(e -> switchTab("TRIPS"));
        btnReservations.addActionListener(e -> switchTab("RESERVATIONS"));

        // Show trips by default
        switchTab("TRIPS");
        updateTripList();
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
                cardListPanel.add(new TripCardPanel(trip, customer));
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
    }

    public JList<String> getBusList() {
        return busList;
    }
}