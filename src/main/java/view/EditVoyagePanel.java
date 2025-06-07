package view;

import javax.swing.*;
import java.awt.*;
import models.Customer;
import models.Voyage;
import services.DatabaseService;
import com.mycompany.aoopproject.AOOPProject;

public class EditVoyagePanel extends JPanel {
    private final Customer customer;
    private final Voyage voyage;
    private final JFrame mainFrame;
    private JTextField firmField;
    private JTextField originField;
    private JTextField destField;
    private JTextField startTimeField;
    private JTextField arrivalTimeField;
    private JTextField priceField;
    private JTextField seatCountField;
    private JComboBox<String> seatArrangementCombo;

    public EditVoyagePanel(Customer customer, Voyage voyage, JFrame mainFrame) {
        System.out.println("EditVoyagePanel constructor called"); // Debug message
        this.customer = customer;
        this.voyage = voyage;
        this.mainFrame = mainFrame;
        
        System.out.println("Setting up EditVoyagePanel layout"); // Debug message
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Panel with title and back button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Sefer Düzenle", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("🏠 Ana Sayfa") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(170, 36));
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        backButton.addActionListener(e -> {
            if (mainFrame instanceof AOOPProject) {
                com.mycompany.aoopproject.AOOPProject frame = (com.mycompany.aoopproject.AOOPProject) mainFrame;
                frame.showMainView(new MainView(customer, voyage.getType().equalsIgnoreCase("Bus"), frame));
            }
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        rightPanel.setOpaque(false);
        rightPanel.add(backButton);
        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Initialize fields with current voyage data
        firmField = new JTextField(voyage.getFirm(), 20);
        originField = new JTextField(voyage.getOrigin(), 20);
        destField = new JTextField(voyage.getDestination(), 20);
        startTimeField = new JTextField(voyage.getStartTime(), 20);
        arrivalTimeField = new JTextField(voyage.getArrivalTime(), 20);
        priceField = new JTextField(String.valueOf(voyage.getPrice()), 20);
        
        // Koltuk sayısı ve düzeni için sadece görüntüleme
        seatCountField = new JTextField(String.valueOf(voyage.getSeatCount()), 20);
        seatCountField.setEditable(false);
        seatCountField.setBackground(new Color(240, 240, 240));
        
        seatArrangementCombo = new JComboBox<>(new String[]{"2+1", "2+2", "3+3"});
        seatArrangementCombo.setSelectedItem(voyage.getSeatArrangement());
        seatArrangementCombo.setEnabled(false);
        seatArrangementCombo.setBackground(new Color(240, 240, 240));

        // Add components to form
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Firma:"), gbc);
        gbc.gridx = 1;
        formPanel.add(firmField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Kalkış:"), gbc);
        gbc.gridx = 1;
        formPanel.add(originField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Varış:"), gbc);
        gbc.gridx = 1;
        formPanel.add(destField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Kalkış Zamanı:"), gbc);
        gbc.gridx = 1;
        formPanel.add(startTimeField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Varış Zamanı:"), gbc);
        gbc.gridx = 1;
        formPanel.add(arrivalTimeField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Fiyat:"), gbc);
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Koltuk Sayısı:"), gbc);
        gbc.gridx = 1;
        formPanel.add(seatCountField, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Koltuk Düzeni:"), gbc);
        gbc.gridx = 1;
        formPanel.add(seatArrangementCombo, gbc);

        // Save button
        JButton saveButton = new JButton("Kaydet") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(new Color(52, 152, 219));
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(120, 36));

        saveButton.addActionListener(e -> {
            try {
                voyage.setFirm(firmField.getText());
                voyage.setOrigin(originField.getText());
                voyage.setDestination(destField.getText());
                voyage.setStartTime(startTimeField.getText());
                voyage.setArrivalTime(arrivalTimeField.getText());
                voyage.setPrice(Double.parseDouble(priceField.getText()));
                // Koltuk sayısı ve düzeni değiştirilmiyor
                
                DatabaseService.updateVoyageInDB(voyage);
                
                JOptionPane.showMessageDialog(this, 
                    "Sefer başarıyla güncellendi!", 
                    "Başarılı", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                if (mainFrame instanceof AOOPProject) {
                    com.mycompany.aoopproject.AOOPProject frame = (com.mycompany.aoopproject.AOOPProject) mainFrame;
                    frame.showMainView(new MainView(customer, voyage.getType().equalsIgnoreCase("Bus"), frame));
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Lütfen geçerli sayısal değerler girin!", 
                    "Hata", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(saveButton, gbc);

        add(formPanel, BorderLayout.CENTER);
    }
} 