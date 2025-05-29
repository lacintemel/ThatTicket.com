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

        // Kalkış ve varış noktalarını, tarihleri ve fiyat aralığını dinamik oluştur
        Set<String> origins = new HashSet<>();
        Set<String> destinations = new HashSet<>();
        Set<String> dates = new HashSet<>();
        double minPrice = Double.MAX_VALUE, maxPrice = Double.MIN_VALUE;
        for (Voyage trip : allTrips) {
            origins.add(trip.getOrigin());
            destinations.add(trip.getDestination());
            String date = trip.getStartTime().split(" ")[0];
            dates.add(date);
            if (trip.getPrice() < minPrice) minPrice = trip.getPrice();
            if (trip.getPrice() > maxPrice) maxPrice = trip.getPrice();
        }

        // Üst panel: sol (sign out), orta (başlık), sağ (toggle)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        // Sol panel - Sign Out butonu
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        JButton signOutBtn = new JButton("Sign Out");
        signOutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        signOutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signOutBtn.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.getContentPane().removeAll();
                mainFrame.getContentPane().add(new LoginView(), BorderLayout.CENTER);
                mainFrame.revalidate();
                mainFrame.repaint();
            }
        });
        leftPanel.add(signOutBtn);
        headerPanel.add(leftPanel, BorderLayout.WEST);
        
        // Orta panel - Başlık
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        dynamicTitle = new JLabel(isBusMode ? "Otobüs Seferleri" : "Uçak Seferleri", SwingConstants.CENTER);
        dynamicTitle.setFont(new Font("Arial", Font.BOLD, 22));
        centerPanel.add(dynamicTitle);
        headerPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Sağ panel - Toggle butonu
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        JButton toggleButton = new JButton(isBusMode ? "Uçakları Göster" : "Otobüsleri Göster");
        toggleButton.setFont(new Font("Arial", Font.BOLD, 14));
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.getContentPane().removeAll();
                mainFrame.getContentPane().add(new MainView(customer, !isBusMode, mainFrame), BorderLayout.CENTER);
                mainFrame.revalidate();
                mainFrame.repaint();
            }
        });
        rightPanel.add(toggleButton);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Main panel (center area)
        mainPanel = new JPanel(new CardLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Trips panel (with search and filters)
        tripsPanel = new JPanel(new BorderLayout(10, 10));
        tripsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filter panel (top)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> originBox = new JComboBox<>();
        originBox.addItem("Tümü");
        for (String o : origins) originBox.addItem(o);
        JComboBox<String> destBox = new JComboBox<>();
        destBox.addItem("Tümü");
        for (String d : destinations) destBox.addItem(d);
        JComboBox<String> dateBox = new JComboBox<>();
        dateBox.addItem("Tümü");
        for (String d : dates) dateBox.addItem(d);
        JTextField minPriceField = new JTextField(String.valueOf((int)minPrice), 5);
        JTextField maxPriceField = new JTextField(String.valueOf((int)maxPrice), 5);
        filterPanel.add(new JLabel("Kalkış:"));
        filterPanel.add(originBox);
        filterPanel.add(new JLabel("Varış:"));
        filterPanel.add(destBox);
        filterPanel.add(new JLabel("Tarih:"));
        filterPanel.add(dateBox);
        filterPanel.add(new JLabel("Fiyat:"));
        filterPanel.add(minPriceField);
        filterPanel.add(new JLabel("-"));
        filterPanel.add(maxPriceField);
        tripsPanel.add(filterPanel, BorderLayout.NORTH);

        // Search panel (under filters)
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Ara");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        tripsPanel.add(searchPanel, BorderLayout.AFTER_LAST_LINE);

        // Card list panel for trips
        cardListPanel = new JPanel();
        cardListPanel.setLayout(new BoxLayout(cardListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(cardListPanel);
        tripsPanel.add(scrollPane, BorderLayout.CENTER);

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
        tabbar.add(btnReservations);
        add(tabbar, BorderLayout.SOUTH);

        // Button actions to switch views
        btnTrips.addActionListener(e -> switchTab("TRIPS"));
        btnReservations.addActionListener(e -> switchTab("RESERVATIONS"));

        // Filter and search actions
        Runnable updateList = () -> updateTripList(originBox, destBox, dateBox, minPriceField, maxPriceField, searchField);
        originBox.addActionListener(e -> updateList.run());
        destBox.addActionListener(e -> updateList.run());
        dateBox.addActionListener(e -> updateList.run());
        searchButton.addActionListener(e -> updateList.run());
        minPriceField.addActionListener(e -> updateList.run());
        maxPriceField.addActionListener(e -> updateList.run());

        // Show trips by default
        switchTab("TRIPS");
        updateList.run();
    }

    private void updateTripList(JComboBox<String> originBox, JComboBox<String> destBox, JComboBox<String> dateBox, JTextField minPriceField, JTextField maxPriceField, JTextField searchField) {
        String selectedOrigin = (String) originBox.getSelectedItem();
        String selectedDest = (String) destBox.getSelectedItem();
        String selectedDate = (String) dateBox.getSelectedItem();
        double minPrice = 0, maxPrice = 9999;
        try { minPrice = Double.parseDouble(minPriceField.getText().trim()); } catch (Exception ignored) {}
        try { maxPrice = Double.parseDouble(maxPriceField.getText().trim()); } catch (Exception ignored) {}
        String search = searchField.getText().trim().toLowerCase();

        cardListPanel.removeAll();
        for (Voyage trip : allTrips) {
            boolean matches = true;
            if (!selectedOrigin.equals("Tümü") && !trip.getOrigin().equals(selectedOrigin)) matches = false;
            if (!selectedDest.equals("Tümü") && !trip.getDestination().equals(selectedDest)) matches = false;
            if (!selectedDate.equals("Tümü") && !trip.getStartTime().startsWith(selectedDate)) matches = false;
            if (trip.getPrice() < minPrice || trip.getPrice() > maxPrice) matches = false;
            String tripStr = trip.getFirm() + " - " + trip.getOrigin() + " -> " + trip.getDestination() + " - " + trip.getStartTime() + " - ₺" + trip.getPrice();
            if (!search.isEmpty() && !tripStr.toLowerCase().contains(search)) matches = false;
            if (matches) {
                cardListPanel.add(new TripCardPanel(trip, customer));
                cardListPanel.add(Box.createVerticalStrut(10));
            }
        }
        cardListPanel.revalidate();
        cardListPanel.repaint();
    }

    private void switchTab(String name) {
        currentTab = name;
        CardLayout cl = (CardLayout) (mainPanel.getLayout());
        cl.show(mainPanel, name);
        if (name.equals("TRIPS")) {
            dynamicTitle.setText("Otobüs Seferleri");
        } else {
            dynamicTitle.setText("Rezervasyonlar");
        }
    }

    public JList<String> getBusList() {
        return busList;
    }

    

}