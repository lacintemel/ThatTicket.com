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

public class AdminVoyagePanel extends JPanel {
    private Admin admin;
    private Customer customer;
    private JFrame mainFrame;
    private JComboBox<String> typeCombo;
    private JTextField firmField, originField, destField, startTimeField, arrivalTimeField;
    private JSpinner seatCountSpinner, priceSpinner;
    private JComboBox<String> seatArrangementCombo;
    private JComboBox<String> planeNameCombo;
    private Voyage voyageToUpdate; // Güncellenecek sefer

    public AdminVoyagePanel(Admin admin, Customer customer, JFrame mainFrame) {
        this(admin, customer, mainFrame, null); // Yeni sefer oluşturma
    }

    public AdminVoyagePanel(Admin admin, Customer customer, JFrame mainFrame, Voyage voyageToUpdate) {
        this.admin = admin;
        this.customer = customer;
        this.mainFrame = mainFrame;
        this.voyageToUpdate = voyageToUpdate;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Üst panel (başlık + sağ üstte uygulamaya dön butonu)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        // Başlık
        JLabel titleLabel = new JLabel(voyageToUpdate == null ? "Yeni Sefer Oluştur" : "Sefer Güncelle", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Sağ üstte uygulamaya dön butonu
        JButton backButton = new JButton("🏠 Uygulamaya Dön") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(25, 118, 210));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
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

        // Form paneli
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Sefer tipi
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Sefer Tipi:"), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(new String[]{"Bus", "Flight"});
        typeCombo.addActionListener(e -> {
            boolean isFlight = typeCombo.getSelectedItem().equals("Flight");
            firmField.setVisible(!isFlight);
            planeNameCombo.setVisible(isFlight);
            seatArrangementCombo.setEnabled(!isFlight);
            updateSeatCount();
        });
        formPanel.add(typeCombo, gbc);

        // Firma
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Firma/Uçak Adı:"), gbc);
        gbc.gridx = 1;
        firmField = new JTextField(20);
        formPanel.add(firmField, gbc);
        planeNameCombo = new JComboBox<>(new String[]{"Boeing 737", "Airbus A320", "Embraer 190"});
        planeNameCombo.setVisible(false);
        planeNameCombo.addActionListener(e -> updateSeatCount());
        formPanel.add(planeNameCombo, gbc);

        // Kalkış yeri
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Kalkış:"), gbc);
        gbc.gridx = 1;
        originField = new JTextField(20);
        formPanel.add(originField, gbc);

        // Varış yeri
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Varış:"), gbc);
        gbc.gridx = 1;
        destField = new JTextField(20);
        formPanel.add(destField, gbc);

        // Kalkış zamanı
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Kalkış Zamanı:"), gbc);
        gbc.gridx = 1;
        startTimeField = new JTextField(20);
        formPanel.add(startTimeField, gbc);

        // Varış zamanı
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Varış Zamanı:"), gbc);
        gbc.gridx = 1;
        arrivalTimeField = new JTextField(20);
        formPanel.add(arrivalTimeField, gbc);

        // Koltuk düzeni
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Koltuk Düzeni:"), gbc);
        gbc.gridx = 1;
        seatArrangementCombo = new JComboBox<>(new String[]{"2+1", "2+2", "3+3"});
        seatArrangementCombo.setEnabled(false);
        formPanel.add(seatArrangementCombo, gbc);

        // Koltuk sayısı (read-only)
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Koltuk Sayısı:"), gbc);
        gbc.gridx = 1;
        seatCountSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 100, 1));
        seatCountSpinner.setEnabled(false);
        formPanel.add(seatCountSpinner, gbc);

        // Fiyat
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Fiyat:"), gbc);
        gbc.gridx = 1;
        SpinnerNumberModel priceModel = new SpinnerNumberModel(100.0, 0.0, 10000.0, 10.0);
        priceSpinner = new JSpinner(priceModel);
        formPanel.add(priceSpinner, gbc);

        // Eğer güncelleme modundaysa, mevcut verileri doldur
        if (voyageToUpdate != null) {
            typeCombo.setSelectedItem(voyageToUpdate.getType());
            firmField.setText(voyageToUpdate.getFirm());
            originField.setText(voyageToUpdate.getOrigin());
            destField.setText(voyageToUpdate.getDestination());
            startTimeField.setText(voyageToUpdate.getStartTime());
            arrivalTimeField.setText(voyageToUpdate.getArrivalTime());
            seatCountSpinner.setValue(voyageToUpdate.getSeatCount());
            priceSpinner.setValue(voyageToUpdate.getPrice());
            seatArrangementCombo.setSelectedItem(voyageToUpdate.getSeatArrangement());
        }

        // Kaydet butonu
        JButton saveButton = new JButton(voyageToUpdate == null ? "Sefer Oluştur" : "Güncelle") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(46, 204, 113));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(200, 36));
        saveButton.addActionListener(e -> {
            // Form verilerini al
            String type = (String) typeCombo.getSelectedItem();
            String firm = type.equals("Bus") ? firmField.getText() : (String) planeNameCombo.getSelectedItem();
            String origin = originField.getText();
            String destination = destField.getText();
            String startTime = startTimeField.getText();
            String arrivalTime = arrivalTimeField.getText();
            int seatCount = (int) seatCountSpinner.getValue();
            double price = (double) priceSpinner.getValue();
            String seatArrangement = (String) seatArrangementCombo.getSelectedItem();

            // Validasyon
            if (origin.isEmpty() || destination.isEmpty() || 
                startTime.isEmpty() || arrivalTime.isEmpty() || price <= 0) {
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

        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(saveButton, gbc);

        add(formPanel, BorderLayout.CENTER);
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
                case "3+3":
                    seatCountSpinner.setValue(54);
                    break;
            }
        } else if (typeCombo.getSelectedItem().equals("Flight")) {
            String planeType = (String) planeNameCombo.getSelectedItem();
            if (planeType != null) {
                switch (planeType) {
                    case "Boeing 737":
                        seatCountSpinner.setValue(189);
                        seatArrangementCombo.setSelectedItem("2+1");
                        seatArrangementCombo.setEnabled(false);
                        break;
                    case "Airbus A320":
                        seatCountSpinner.setValue(180);
                        seatArrangementCombo.setSelectedItem("2+2");
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
} 