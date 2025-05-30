package view;

import services.Admin;
import javax.swing.*;
import java.awt.*;
import commands.AddVoyageCommand;
import commands.CommandCaller;
import models.Voyage;
import com.mycompany.aoopproject.AOOPProject;
import models.Customer;
import factorys.VoyageFactory;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;

public class AdminVoyagePanel extends JPanel {
    private Admin admin;
    private Customer customer;
    private JFrame mainFrame;
    private JComboBox<String> typeCombo;
    private JTextField firmField;
    private JComboBox<String> originCombo, destCombo;
    private JDateChooser startDateChooser, arrivalDateChooser;
    private JSpinner startTimeSpinner, arrivalTimeSpinner;
    private JSpinner seatCountSpinner, priceSpinner;
    private JComboBox<String> seatArrangementCombo;
    private JComboBox<String> planeNameCombo;
    private Voyage voyageToUpdate; // Güncellenecek sefer
    private boolean isBusMode = true; // Default to bus mode
    private JLabel titleLabel;
    private JButton backButton;
    private JPanel formPanel;
    private JButton saveButton;
    private Font buttonFont;
    private Font labelFont;
    private Font titleFont;

    // Declare labels as class fields
    private JLabel typeLabel;
    private JLabel firmLabel;
    private JLabel originLabel;
    private JLabel destLabel;
    private JLabel startDateLabel;
    private JLabel startTimeLabel;
    private JLabel arrivalDateLabel;
    private JLabel arrivalTimeLabel;
    private JLabel seatArrangementLabel;
    private JLabel seatCountLabel;
    private JLabel priceLabel;

    public AdminVoyagePanel(Admin admin, Customer customer, JFrame mainFrame) {
        this(admin, customer, mainFrame, null); // Yeni sefer oluşturma
    }

    public AdminVoyagePanel(Admin admin, Customer customer, JFrame mainFrame, Voyage voyageToUpdate) {
        this.admin = admin;
        this.customer = customer;
        this.mainFrame = mainFrame;
        this.voyageToUpdate = voyageToUpdate;

        // Define turkishCities here so it's available for JComboBox initialization
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
        
        // Use system default font with emoji support for buttons
        buttonFont = new Font("Dialog", Font.BOLD, 14);
        labelFont = new Font("Arial", Font.PLAIN, 12);
        titleFont = new Font("Arial", Font.BOLD, 24);

        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE); // Set panel background to white like the FlixBus listing

        // Initialize the form panel FIRST
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE); // Set form panel background to white
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Adjust insets for more spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Initialize all components
        typeCombo = new JComboBox<>(new String[]{"Bus", "Flight"});
        firmField = new JTextField(20);
        planeNameCombo = new JComboBox<>(new String[]{"Boeing 737", "Airbus A320", "Embraer 190"});
        seatArrangementCombo = new JComboBox<>(new String[]{"2+1", "2+2", "3+3"});
        originCombo = new JComboBox<>(turkishCities);
        destCombo = new JComboBox<>(turkishCities);
        startDateChooser = new JDateChooser();
        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        arrivalTimeSpinner = new JSpinner(new SpinnerDateModel());
        seatCountSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 100, 1));
        priceSpinner = new JSpinner(new SpinnerNumberModel(100.0, 0.0, 10000.0, 10.0));
        
        // Initialize labels
        typeLabel = new JLabel("Sefer Tipi:");
        firmLabel = new JLabel("Firma/Uçak Adı:");
        originLabel = new JLabel("Kalkış:");
        destLabel = new JLabel("Varış:");
        startDateLabel = new JLabel("Kalkış Tarihi:");
        startTimeLabel = new JLabel("Kalkış Saati:");
        arrivalDateLabel = new JLabel("Varış Tarihi:");
        arrivalTimeLabel = new JLabel("Varış Saati:");
        seatArrangementLabel = new JLabel("Koltuk Düzeni:");
        // Set initial states and fonts for components
        typeCombo.setSelectedItem(isBusMode ? "Bus" : "Flight");
        typeCombo.setFont(labelFont);
        firmField.setVisible(true);
        firmField.setFont(labelFont);
        planeNameCombo.setVisible(false);
        planeNameCombo.setFont(labelFont);
        seatArrangementCombo.setEnabled(true);
        seatArrangementCombo.setFont(labelFont);
        originCombo.setPreferredSize(new Dimension(200, 30));
        originCombo.setFont(labelFont);
        destCombo.setPreferredSize(new Dimension(200, 30));
        destCombo.setFont(labelFont);
        startDateChooser.setPreferredSize(new Dimension(200, 30));
        startDateChooser.setFont(labelFont);
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startTimeEditor);
        startTimeSpinner.setPreferredSize(new Dimension(200, 30));
        startTimeSpinner.setFont(labelFont);
        JSpinner.DateEditor arrivalTimeEditor = new JSpinner.DateEditor(arrivalTimeSpinner, "HH:mm");
        arrivalTimeSpinner.setEditor(arrivalTimeEditor);
        arrivalTimeSpinner.setPreferredSize(new Dimension(200, 30));
        arrivalTimeSpinner.setFont(labelFont);
        seatCountSpinner.setEnabled(false);
        seatCountSpinner.setFont(labelFont);
        priceSpinner.setFont(labelFont);

        // Now set up the action listener
        typeCombo.addActionListener(e -> {
            boolean isFlight = typeCombo.getSelectedItem().equals("Flight");
            if (firmField != null) firmField.setVisible(!isFlight);
            if (planeNameCombo != null) planeNameCombo.setVisible(isFlight);
            if (seatArrangementCombo != null) {
                seatArrangementCombo.setEnabled(!isFlight);
                if (isFlight) {
                    seatArrangementCombo.setSelectedItem("3+3");
                    if (saveButton != null) {
                        saveButton.setText(voyageToUpdate == null ? "✈ Sefer Oluştur" : "✈ Güncelle");
                    }
                } else {
                    seatArrangementCombo.setSelectedItem("2+1");
                    if (saveButton != null) {
                        saveButton.setText(voyageToUpdate == null ? "🚌 Sefer Oluştur" : "🚌 Güncelle");
                    }
                }
            }
            updateSeatCount();
            updateThemeColor(!isFlight); // Update theme color based on type
        });
        
        originCombo.addActionListener(e -> validateCities());
        destCombo.addActionListener(e -> validateCities());
        planeNameCombo.addActionListener(e -> updateSeatCount());
        seatArrangementCombo.addActionListener(e -> updateSeatCount());
        
        // Üst panel (başlık + sağ üstte uygulamaya dön butonu)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        // Başlık
        titleLabel = new JLabel(voyageToUpdate == null ? "Yeni Sefer Oluştur" : "Sefer Güncelle", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Sağ üstte uygulamaya dön butonu
        backButton = new JButton("🏠 Ana Sayfa") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16); // Rounded corners
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        backButton.setFont(buttonFont);
        backButton.setForeground(Color.WHITE); // White text
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(170, 36));
        backButton.addActionListener(e -> {
            if (mainFrame instanceof AOOPProject) {
                ((AOOPProject) mainFrame).showMainView(new MainView(customer, true, mainFrame));
            }
        });
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(backButton);
        topPanel.add(rightPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Add components to the form panel
        gbc.gridx = 0; gbc.gridy = 0;
        if (typeLabel != null) formPanel.add(typeLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        if (typeCombo != null) formPanel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        if (firmLabel != null) formPanel.add(firmLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        if (firmField != null) formPanel.add(firmField, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        if (planeNameCombo != null) formPanel.add(planeNameCombo, gbc); // planeNameCombo shares the same gridx, gridy as firmField

        gbc.gridx = 0; gbc.gridy = 2;
        if (originLabel != null) formPanel.add(originLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        if (originCombo != null) formPanel.add(originCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        if (destLabel != null) formPanel.add(destLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        if (destCombo != null) formPanel.add(destCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        if (startDateLabel != null) formPanel.add(startDateLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        if (startDateChooser != null) formPanel.add(startDateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        if (startTimeLabel != null) formPanel.add(startTimeLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        if (startTimeSpinner != null) formPanel.add(startTimeSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        if (arrivalDateLabel != null) formPanel.add(arrivalDateLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        if (arrivalDateChooser != null) formPanel.add(arrivalDateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        if (arrivalTimeLabel != null) formPanel.add(arrivalTimeLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 7;
        if (arrivalTimeSpinner != null) formPanel.add(arrivalTimeSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        if (seatArrangementLabel != null) formPanel.add(seatArrangementLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 8;
        if (seatArrangementCombo != null) formPanel.add(seatArrangementCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        if (seatCountLabel != null) formPanel.add(seatCountLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 9;
        if (seatCountSpinner != null) formPanel.add(seatCountSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 10;
        if (priceLabel != null) formPanel.add(priceLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 10;
        if (priceSpinner != null) formPanel.add(priceSpinner, gbc);

        // Eğer güncelleme modundaysa, mevcut verileri doldur
        if (voyageToUpdate != null) {
            typeCombo.setSelectedItem(voyageToUpdate.getType());
            firmField.setText(voyageToUpdate.getFirm());
            originCombo.setSelectedItem(voyageToUpdate.getOrigin());
            destCombo.setSelectedItem(voyageToUpdate.getDestination());
            
            // Parse and set the start time
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                java.util.Date startDate = sdf.parse(voyageToUpdate.getStartTime());
                startDateChooser.setDate(startDate);
                startTimeSpinner.setValue(startDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Parse and set the arrival time
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                java.util.Date arrivalDate = sdf.parse(voyageToUpdate.getArrivalTime());
                arrivalDateChooser.setDate(arrivalDate);
                arrivalTimeSpinner.setValue(arrivalDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            seatCountSpinner.setValue(voyageToUpdate.getSeatCount());
            priceSpinner.setValue(voyageToUpdate.getPrice());
            seatArrangementCombo.setSelectedItem(voyageToUpdate.getSeatArrangement());
            seatArrangementCombo.setEnabled(!typeCombo.getSelectedItem().equals("Flight")); // Enable/disable based on type
        } else {
            // For new voyages, set default values
            typeCombo.setSelectedItem("Bus");
            seatArrangementCombo.setSelectedItem("2+1");
            updateSeatCount();
        }

        // Kaydet butonu
        saveButton = new JButton(voyageToUpdate == null ? "✈ Sefer Oluştur" : "✈ Güncelle") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16); // Rounded corners
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        saveButton.setFont(buttonFont);
        saveButton.setForeground(Color.WHITE); // White text
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(200, 36));
        saveButton.addActionListener(e -> {
            // Form verilerini al
            String type = (String) typeCombo.getSelectedItem();
            String firm = type.equals("Bus") ? firmField.getText() : (String) planeNameCombo.getSelectedItem();
            String origin = (String) originCombo.getSelectedItem();
            String destination = (String) destCombo.getSelectedItem();
            
            // Format dates and times
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String startTime = sdf.format(startDateChooser.getDate()) + " " + 
                             sdf.format(startTimeSpinner.getValue()).split(" ")[1];
            String arrivalTime = sdf.format(arrivalDateChooser.getDate()) + " " + 
                               sdf.format(arrivalTimeSpinner.getValue()).split(" ")[1];
            
            int seatCount = (int) seatCountSpinner.getValue();
            double price = (double) priceSpinner.getValue();
            String seatArrangement = (String) seatArrangementCombo.getSelectedItem();

            // Validasyon
            if (origin.equals("Select City") || destination.equals("Select City")) {
                JOptionPane.showMessageDialog(this, "Lütfen kalkış ve varış şehirlerini seçin!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (origin.equals(destination)) {
                JOptionPane.showMessageDialog(this, "Kalkış ve varış şehirleri aynı olamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (startDateChooser.getDate() == null || arrivalDateChooser.getDate() == null || 
                price <= 0) {
                JOptionPane.showMessageDialog(this, "Lütfen gerekli alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (type.equals("Bus") && firm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen firma adını girin!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (voyageToUpdate == null) {
                // Yeni sefer oluştur
                int newVoyageId = Voyage.getVoyageHashMap().size() + 1; // Yeni ID oluştur
                Voyage newVoyage = VoyageFactory.createVoyage(
                    newVoyageId,
                    type,
                    firm,
                    origin,
                    destination,
                    startTime,
                    arrivalTime,
                    seatCount,
                    price,
                    seatArrangement
                );
                AddVoyageCommand addCmd = new AddVoyageCommand(newVoyage, admin);
                CommandCaller caller = new CommandCaller();
                caller.executeCommand(addCmd);
                // Veritabanına ekle
                services.DatabaseService.addVoyageToDB(newVoyage);
            } else {
                // Mevcut seferi güncelle
                voyageToUpdate.setType(type);
                voyageToUpdate.setFirm(firm);
                voyageToUpdate.setOrigin(origin);
                voyageToUpdate.setDestination(destination);
                voyageToUpdate.setStartTime(startTime);
                voyageToUpdate.setArrivalTime(arrivalTime);
                voyageToUpdate.setSeatCount(seatCount);
                voyageToUpdate.setPrice(price);
                voyageToUpdate.setSeatArrangement(seatArrangement);
                // Veritabanını güncelle
                services.DatabaseService.updateVoyageInDB(voyageToUpdate);
            }

            // Ana ekrana dön
            if (mainFrame instanceof AOOPProject) {
                ((AOOPProject) mainFrame).showMainView(new MainView(customer, true, mainFrame));
            }
        });

        gbc.gridx = 0; gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        if (saveButton != null) formPanel.add(saveButton, gbc);

        add(formPanel, BorderLayout.CENTER);
        
        // Set initial theme color after all components are created
        updateThemeColor(typeCombo.getSelectedItem().equals("Bus"));
    }

    private void updateSeatCount() {
        if (typeCombo.getSelectedItem().equals("Bus")) {
            String arrangement = (String) seatArrangementCombo.getSelectedItem();
            switch (arrangement) {
                case "2+1":
                    seatCountSpinner.setValue(45);
                    break;
                case "2+2":
                    seatCountSpinner.setValue(48);
                    break;
            }
        } else if (typeCombo.getSelectedItem().equals("Flight")) {
            String planeType = (String) planeNameCombo.getSelectedItem();
            if (planeType != null) {
                switch (planeType) {
                    case "Boeing 737":
                        seatCountSpinner.setValue(189);
                        seatArrangementCombo.setSelectedItem("3+3");
                        seatArrangementCombo.setEnabled(false);
                        break;
                    case "Airbus A320":
                        seatCountSpinner.setValue(180);
                        seatArrangementCombo.setSelectedItem("3+3");
                        seatArrangementCombo.setEnabled(false);
                        break;
                    case "Embraer 190":
                        seatCountSpinner.setValue(114);
                        seatArrangementCombo.setSelectedItem("3+3");
                        seatArrangementCombo.setEnabled(false);
                        break;
                }
            }
        }
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
    }

    public void setTransportMode(boolean isBusMode) {
        this.isBusMode = isBusMode;
        if (typeCombo != null) {
            typeCombo.setSelectedItem(isBusMode ? "Bus" : "Flight");
        }
    }

    private void updateThemeColor(boolean isBus) {
        Color themeColor = isBus ? new Color(25, 118, 210) : new Color(220, 53, 69); // Blue for bus, Red for plane
        
        // Update back button color
        if (backButton != null) {
            backButton.setBackground(themeColor);
        }
        
        // Update save button color
        if (saveButton != null) {
            saveButton.setBackground(themeColor);
        }
        
        // Update form labels color
        if (formPanel != null) {
            for (Component comp : formPanel.getComponents()) {
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setForeground(themeColor);
                }
            }
        }
        
        // Update title color
        if (titleLabel != null) {
            titleLabel.setForeground(themeColor);
        }
        
        // Repaint the panel
        repaint();
    }
} 