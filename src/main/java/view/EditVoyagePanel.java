package view;

import javax.swing.*;
import java.awt.*;
import models.Voyage;
import models.User;
import services.DatabaseService;
import com.mycompany.aoopproject.AOOPProject;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditVoyagePanel extends JPanel {
    private final User user;
    private final Voyage voyage;
    private final JFrame mainFrame;
    private JTextField firmField;
    private JComboBox<String> originCombo;
    private JComboBox<String> destCombo;
    private JDateChooser startDateChooser;
    private JSpinner startTimeSpinner;
    private JDateChooser arrivalDateChooser;
    private JSpinner arrivalTimeSpinner;
    private JTextField priceField;
    private JTextField seatCountField;
    private JComboBox<String> seatArrangementCombo;

    public EditVoyagePanel(User user, Voyage voyage, JFrame mainFrame) {
        this.user = user;
        this.voyage = voyage;
        this.mainFrame = mainFrame;
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Panel with title and back button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Edit Trip", SwingConstants.CENTER);
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
        backButton.addActionListener(e -> {
            if (mainFrame instanceof AOOPProject) {
                ((AOOPProject) mainFrame).showMainView(new MainView(user, voyage.getType().equalsIgnoreCase("Bus"), (AOOPProject)mainFrame));
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

        firmField = new JTextField(voyage.getFirm(), 20);
        originCombo = new JComboBox<>(turkishCities);
        destCombo = new JComboBox<>(turkishCities);
        originCombo.setSelectedItem(voyage.getOrigin());
        destCombo.setSelectedItem(voyage.getDestination());
        
        startDateChooser = new JDateChooser();
        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        arrivalDateChooser = new JDateChooser();
        arrivalTimeSpinner = new JSpinner(new SpinnerDateModel());
        
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));
        arrivalTimeSpinner.setEditor(new JSpinner.DateEditor(arrivalTimeSpinner, "HH:mm"));

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startDate = sdf.parse(voyage.getStartTime());
            Date arrivalDate = sdf.parse(voyage.getArrivalTime());
            startDateChooser.setDate(startDate);
            startTimeSpinner.setValue(startDate);
            arrivalDateChooser.setDate(arrivalDate);
            arrivalTimeSpinner.setValue(arrivalDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        priceField = new JTextField(String.valueOf(voyage.getPrice()), 20);
        seatCountField = new JTextField(String.valueOf(voyage.getSeatCount()), 20);
        seatCountField.setEditable(false);
        
        seatArrangementCombo = new JComboBox<>(new String[]{"2+1", "2+2", "3+3"});
        seatArrangementCombo.setSelectedItem(voyage.getSeatArrangement());
        seatArrangementCombo.setEnabled(false);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Company:"), gbc);
        gbc.gridx = 1; formPanel.add(firmField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Origin:"), gbc);
        gbc.gridx = 1; formPanel.add(originCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1; formPanel.add(destCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Departure Date:"), gbc);
        gbc.gridx = 1; formPanel.add(startDateChooser, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Departure Time:"), gbc);
        gbc.gridx = 1; formPanel.add(startTimeSpinner, gbc);
        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(new JLabel("Arrival Date:"), gbc);
        gbc.gridx = 1; formPanel.add(arrivalDateChooser, gbc);
        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(new JLabel("Arrival Time:"), gbc);
        gbc.gridx = 1; formPanel.add(arrivalTimeSpinner, gbc);
        gbc.gridx = 0; gbc.gridy = 7; formPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; formPanel.add(priceField, gbc);
        gbc.gridx = 0; gbc.gridy = 8; formPanel.add(new JLabel("Seat Count:"), gbc);
        gbc.gridx = 1; formPanel.add(seatCountField, gbc);
        gbc.gridx = 0; gbc.gridy = 9; formPanel.add(new JLabel("Seat Layout:"), gbc);
        gbc.gridx = 1; formPanel.add(seatArrangementCombo, gbc);

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
                voyage.setOrigin((String) originCombo.getSelectedItem());
                voyage.setDestination((String) destCombo.getSelectedItem());
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String startTime = new SimpleDateFormat("yyyy-MM-dd").format(startDateChooser.getDate()) + " " + new SimpleDateFormat("HH:mm").format(startTimeSpinner.getValue());
                String arrivalTime = new SimpleDateFormat("yyyy-MM-dd").format(arrivalDateChooser.getDate()) + " " + new SimpleDateFormat("HH:mm").format(arrivalTimeSpinner.getValue());
                
                voyage.setStartTime(startTime);
                voyage.setArrivalTime(arrivalTime);
                voyage.setPrice(Double.parseDouble(priceField.getText()));
                
                DatabaseService.updateVoyageInDB(voyage);
                
                JOptionPane.showMessageDialog(this, "Trip updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (mainFrame instanceof AOOPProject) {
                   ((AOOPProject) mainFrame).showMainView(new MainView(user, voyage.getType().equalsIgnoreCase("Bus"), (AOOPProject)mainFrame));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating trip: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        formPanel.add(saveButton, gbc);
        
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
    }
} 