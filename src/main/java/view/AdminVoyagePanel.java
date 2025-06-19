package view;

import services.Admin;
import javax.swing.*;
import java.awt.*;
import commands.AddVoyageCommand;
import models.Voyage;
import com.mycompany.aoopproject.AOOPProject;
import factorys.VoyageFactory;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminVoyagePanel extends JPanel {
    private Admin admin;
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

    private MainView mainView;

    public AdminVoyagePanel(Admin admin, JFrame mainFrame, MainView mainView) {
        this(admin, mainFrame, null, mainView);
    }

    public AdminVoyagePanel(Admin admin, JFrame mainFrame, Voyage voyageToUpdate, MainView mainView) {
        this.admin = admin;
        this.mainFrame = mainFrame;
        this.voyageToUpdate = voyageToUpdate;
        this.mainView = mainView;

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
        seatArrangementCombo = new JComboBox<>(new String[]{"2+1", "2+2"}); // Default bus arrangements
        originCombo = new JComboBox<>(turkishCities);
        destCombo = new JComboBox<>(turkishCities);
        startDateChooser = new JDateChooser();
        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        arrivalDateChooser = new JDateChooser();
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
        seatCountLabel = new JLabel("Koltuk Sayısı:");
        priceLabel = new JLabel("Fiyat:");

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
        arrivalDateChooser.setPreferredSize(new Dimension(200, 30));
        arrivalDateChooser.setFont(labelFont);
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
                // Update seat arrangement options based on type
                if (isFlight) {
                    seatArrangementCombo.removeAllItems();
                    seatArrangementCombo.addItem("3+3");
                    seatArrangementCombo.setSelectedItem("3+3");
                    seatArrangementCombo.setEnabled(false);
                    if (saveButton != null) {
                        saveButton.setText(voyageToUpdate == null ? "✈ Create Voyage" : "✈ Update");
                    }
                } else {
                    seatArrangementCombo.removeAllItems();
                    seatArrangementCombo.addItem("2+1");
                    seatArrangementCombo.addItem("2+2");
                    seatArrangementCombo.setSelectedItem("2+1");
                    seatArrangementCombo.setEnabled(true);
                    if (saveButton != null) {
                        saveButton.setText(voyageToUpdate == null ? "🚌 Create Voyage" : "🚌 Update");
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
        titleLabel = new JLabel(voyageToUpdate == null ? "Create New Voyage" : "Voyage Update", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Sol tarafta boşluk bırakmak için boş panel
        JPanel leftSpacerPanel = new JPanel();
        leftSpacerPanel.setOpaque(false);
        leftSpacerPanel.setPreferredSize(new Dimension(190, 0)); // Yaklaşık sağ panel genişliği kadar
        topPanel.add(leftSpacerPanel, BorderLayout.WEST);
        
        // Sağ üstte uygulamaya dön butonu
        backButton = new JButton("🏠 Main Page") {
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
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12)); // Added vertical padding
        backButton.addActionListener(e -> {
            if (mainFrame instanceof AOOPProject) {
                SwingUtilities.invokeLater(() -> {
                    this.setVisible(false);
                    ((AOOPProject) mainFrame).showMainView(mainView);
                });
            }
        });
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10)); // Added horizontal and vertical gaps
        rightPanel.setOpaque(false);
        rightPanel.add(backButton);
        topPanel.add(rightPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Add components to the form panel
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(typeLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(firmLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(firmField, gbc);
        formPanel.add(planeNameCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(originLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(originCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(destLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(destCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(startDateLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(startDateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(startTimeLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(startTimeSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(arrivalDateLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(arrivalDateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(arrivalTimeLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(arrivalTimeSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(seatArrangementLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(seatArrangementCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        formPanel.add(seatCountLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(seatCountSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 10;
        formPanel.add(priceLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(priceSpinner, gbc);

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
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            // Parse and set the arrival time
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                java.util.Date arrivalDate = sdf.parse(voyageToUpdate.getArrivalTime());
                arrivalDateChooser.setDate(arrivalDate);
                arrivalTimeSpinner.setValue(arrivalDate);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            seatCountSpinner.setValue(voyageToUpdate.getSeatCount());
            priceSpinner.setValue(voyageToUpdate.getPrice());
            
            // Set seat arrangement based on type
            if (voyageToUpdate.getType().equals("Flight")) {
                seatArrangementCombo.removeAllItems();
                seatArrangementCombo.addItem("3+3");
                seatArrangementCombo.setSelectedItem("3+3");
                seatArrangementCombo.setEnabled(false);
            } else {
                seatArrangementCombo.removeAllItems();
                seatArrangementCombo.addItem("2+1");
                seatArrangementCombo.addItem("2+2");
                seatArrangementCombo.setSelectedItem(voyageToUpdate.getSeatArrangement());
                seatArrangementCombo.setEnabled(true);
            }
        } else {
            // For new voyages, set default values
            typeCombo.setSelectedItem("Bus");
            seatArrangementCombo.removeAllItems();
            seatArrangementCombo.addItem("2+1");
            seatArrangementCombo.addItem("2+2");
            seatArrangementCombo.setSelectedItem("2+1");
            updateSeatCount();
        }

        // Kaydet butonu
        saveButton = new JButton(voyageToUpdate == null ? "✈ Create Voyage" : "✈ Update") {
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
            String startTime = "";
            String arrivalTime = "";
            
            if (startDateChooser.getDate() != null && startTimeSpinner.getValue() != null) {
                startTime = sdf.format(startDateChooser.getDate()) + " " + 
                           sdf.format(startTimeSpinner.getValue()).split(" ")[1];
            }
            
            if (arrivalDateChooser.getDate() != null && arrivalTimeSpinner.getValue() != null) {
                arrivalTime = sdf.format(arrivalDateChooser.getDate()) + " " + 
                             sdf.format(arrivalTimeSpinner.getValue()).split(" ")[1];
            }
            
            int seatCount = (int) seatCountSpinner.getValue();
            double price = (double) priceSpinner.getValue();
            String seatArrangement = (String) seatArrangementCombo.getSelectedItem();

            // Validasyon
            if (origin.equals("Select City") || destination.equals("Select City")) {
                JOptionPane.showMessageDialog(this, "Please select departure and arrival cities!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (origin.equals(destination)) {
                JOptionPane.showMessageDialog(this, "Departure and arrival cities cannot be the same!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (startDateChooser.getDate() == null || arrivalDateChooser.getDate() == null || 
                startTimeSpinner.getValue() == null || arrivalTimeSpinner.getValue() == null ||
                price <= 0) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (type.equals("Bus") && firm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the company name!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kalkış tarihi ve saati geçmiş mi kontrolü
            try {
                Date now = new Date();
                Date startDateTime = sdf.parse(startTime);
                if (startDateTime.before(now)) {
                    JOptionPane.showMessageDialog(this, "Kalkış tarihi ve saati geçmiş olamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Tarih kontrolünde hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (voyageToUpdate == null) {
                // Create a new voyage using AddVoyageCommand
                Voyage newVoyage = VoyageFactory.createVoyage(
                    -1, // Temporary ID, will be set by the command
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
                AddVoyageCommand addCmd = new AddVoyageCommand(newVoyage, (services.Admin)mainView.getUser());
                mainView.getCommandCaller().executeCommand(addCmd);
                if (newVoyage != null) {
                    JOptionPane.showMessageDialog(this, 
                        "Trip added successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    mainView.updateTripList();
                    mainView.setTransportMode(isBusMode);
                    if (mainFrame instanceof AOOPProject) {
                        SwingUtilities.invokeLater(() -> {
                            this.setVisible(false);
                            ((AOOPProject) mainFrame).showMainView(mainView);
                        });
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "An error occurred while creating the trip!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Mevcut seferi güncelle
                try {
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
                    
                    // Show success message
                    JOptionPane.showMessageDialog(this, 
                        "Trip updated successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                        
                    // Ana ekrana dön
                    if (mainFrame instanceof AOOPProject) {
                        SwingUtilities.invokeLater(() -> {
                            this.setVisible(false);
                            ((AOOPProject) mainFrame).showMainView(mainView);
                        });
                    }
                } catch (Exception ex2) {
                    ex2.printStackTrace();
                    JOptionPane.showMessageDialog(this, 
                        "An error occurred while updating the trip!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
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
            if (arrangement != null) {
                switch (arrangement) {
                    case "2+1":
                        seatCountSpinner.setValue(37); // 13 single + 12*2 double = 37 seats
                        break;
                    case "2+2":
                        seatCountSpinner.setValue(46); // 2+2 arrangement: 46 seats
                        break;
                }
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
            
            // Update seat arrangement options based on mode
            seatArrangementCombo.removeAllItems();
            if (isBusMode) {
                seatArrangementCombo.addItem("2+1");
                seatArrangementCombo.addItem("2+2");
                seatArrangementCombo.setSelectedItem("2+1");
                seatArrangementCombo.setEnabled(true);
                firmField.setVisible(true);
                planeNameCombo.setVisible(false);
            } else {
                seatArrangementCombo.addItem("3+3");
                seatArrangementCombo.setSelectedItem("3+3");
                seatArrangementCombo.setEnabled(false);
                firmField.setVisible(false);
                planeNameCombo.setVisible(true);
            }
            
            // Update seat count based on new mode
            updateSeatCount();
        }
        updateThemeColor(isBusMode);
        revalidate();
        repaint();
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