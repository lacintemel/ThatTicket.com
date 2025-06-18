package view;

import javax.swing.*;
import java.awt.*;
import models.Customer;
import models.Voyage;
import commands.CancelReservationCommand;
import commands.CommandCaller;

public class ReservationCardPanel extends JPanel {
    private final Customer customer;
    private final Voyage trip;
    private final int reservedSeatNumber;
    private final String reservedGender;
    private final String transportType;
    private final ReservationsPanel reservationsPanel;
    private final String adminEmail;
    private final String reservationDate;
    private JFrame mainFrame;
    private MainView mainView;
    private JLabel userInfoLabel;
    private CommandCaller commandCaller;

    public ReservationCardPanel(Customer customer, Voyage trip, int reservedSeatNumber, String reservedGender, String transportType, ReservationsPanel reservationsPanel, String adminEmail, MainView mainView, String reservationDate) {
        this.customer = customer;
        this.trip = trip;
        this.reservedSeatNumber = reservedSeatNumber;
        this.reservedGender = reservedGender;
        this.transportType = transportType;
        this.reservationsPanel = reservationsPanel;
        this.adminEmail = adminEmail;
        this.mainView = mainView;
        this.reservationDate = reservationDate;
        this.mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        setOpaque(false);
        setLayout(new BorderLayout());
        setBackground(new Color(0,0,0,0));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        setPreferredSize(new Dimension(getPreferredSize().width, 255));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 255));
        setMinimumSize(new Dimension(0, 255));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setAlignmentY(Component.CENTER_ALIGNMENT);

        // Determine color by vehicle type
        Color mainColor;
        if (trip.getType().equalsIgnoreCase("Bus")) {
            mainColor = new Color(52, 152, 219); // Mavi
        } else {
            mainColor = new Color(220, 53, 69); // Kırmızı
        }

        JPanel card = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(245, 247, 250),
                    0, getHeight(), new Color(230, 232, 235)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(6, 6, getWidth()-12, getHeight()-12, 32, 32);
                g2.setColor(new Color(230,230,230));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(6, 6, getWidth()-12, getHeight()-12, 32, 32);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        // --- ÜST SATIR ---
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JPanel leftTop = new JPanel();
        leftTop.setOpaque(false);
        leftTop.setLayout(new BoxLayout(leftTop, BoxLayout.X_AXIS));
        leftTop.add(createSeatPanel(mainColor));
        leftTop.add(Box.createHorizontalStrut(4));
        leftTop.add(createFirmLabel(mainColor));
        topRow.add(leftTop, BorderLayout.WEST);
        topRow.add(createTimeLabel(mainColor), BorderLayout.CENTER);
        topRow.add(createPriceLabel(mainColor), BorderLayout.EAST);

        JPanel middleRow = new JPanel(new BorderLayout());
        middleRow.setOpaque(false);
        middleRow.add(createReservedSeatPanel(), BorderLayout.WEST);

        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);
        bottomRow.add(createIconsPanel(), BorderLayout.WEST);
        bottomRow.add(createRouteLabel(), BorderLayout.CENTER);
        bottomRow.add(createCancelButtonPanel(), BorderLayout.EAST);

        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(topRow);
        mainPanel.add(Box.createVerticalStrut(12));
        mainPanel.add(middleRow);
        mainPanel.add(Box.createVerticalStrut(12));
        mainPanel.add(bottomRow);

        card.add(mainPanel, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        // User info and date panel
        JPanel bottomInfoPanel = new JPanel(new BorderLayout());
        bottomInfoPanel.setOpaque(false);
        
        // User info label (left side)
        if (userInfoLabel == null) {
            userInfoLabel = new JLabel();
            userInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            userInfoLabel.setForeground(new Color(128, 128, 128));
        }
        bottomInfoPanel.add(userInfoLabel, BorderLayout.WEST);

        // Date label (right side)
        JLabel dateLabel = new JLabel("Received Date: " + reservationDate);
        dateLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        dateLabel.setForeground(new Color(120, 120, 120));
        bottomInfoPanel.add(dateLabel, BorderLayout.EAST);

        add(bottomInfoPanel, BorderLayout.SOUTH);

    }

    private JPanel createSeatPanel(Color mainColor) {
        JPanel seatPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 14;
                g2.setColor(new Color(240, 245, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.setColor(mainColor);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);
                g2.dispose();
            }
        };
        seatPanel.setOpaque(false);
        seatPanel.setLayout(new GridBagLayout());
        JLabel seatLabel = new JLabel(trip.getSeatArrangement());
        seatLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        seatLabel.setForeground(mainColor);
        seatPanel.add(seatLabel);
        int paddingX = 8, paddingY = 2;
        FontMetrics fm = seatLabel.getFontMetrics(seatLabel.getFont());
        int textW = fm.stringWidth(seatLabel.getText());
        int textH = fm.getHeight();
        Dimension boxDim = new Dimension(textW + paddingX * 2, textH + paddingY * 2);
        seatPanel.setPreferredSize(boxDim);
        seatPanel.setMinimumSize(boxDim);
        seatPanel.setMaximumSize(boxDim);
        seatPanel.setBorder(BorderFactory.createEmptyBorder(paddingY, paddingX, paddingY, paddingX));
        return seatPanel;
    }
    private JLabel createFirmLabel(Color mainColor) {
        JLabel label = new JLabel(trip.getFirm().toUpperCase());
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(mainColor);
        return label;
    }
    private JPanel createReservedSeatPanel() {
        JPanel reservedPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 14;
                g2.setColor(new Color(240, 245, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.setColor(new Color(80, 80, 80));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);
                g2.dispose();
            }
        };
        reservedPanel.setOpaque(false);
        reservedPanel.setLayout(new GridBagLayout());
        JLabel reservedLabel = new JLabel("Rezerve Koltuk: " + reservedSeatNumber);
        reservedLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        reservedLabel.setForeground(new Color(80, 80, 80));
        reservedPanel.add(reservedLabel);
        FontMetrics fm2 = reservedLabel.getFontMetrics(reservedLabel.getFont());
        int textHeight2 = fm2.getHeight();
        int padding2 = 2;
        int boxHeight2 = textHeight2 + padding2 * 2;
        int boxWidth2 = reservedLabel.getPreferredSize().width + 10;
        reservedPanel.setBorder(BorderFactory.createEmptyBorder(padding2, 5, padding2, 5));
        reservedPanel.setMaximumSize(new Dimension(boxWidth2, boxHeight2));
        reservedPanel.setPreferredSize(new Dimension(boxWidth2, boxHeight2));
        reservedPanel.setMinimumSize(new Dimension(boxWidth2, boxHeight2));
        return reservedPanel;
    }
    private JLabel createTimeLabel(Color mainColor) {
        JLabel label = new JLabel(trip.getStartTime().split(" ")[1]);
        label.setFont(new Font("Segoe UI", Font.BOLD, 32));
        label.setForeground(mainColor);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    private JLabel createPriceLabel(Color mainColor) {
        JLabel label = new JLabel(String.format("%.2f ₺", trip.getPrice()));
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(mainColor);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }
    private JPanel createIconsPanel() {
        JPanel iconsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        iconsPanel.setOpaque(false);
        try {
            int iconSize = 24;
            java.net.URL stopwatchUrl = new java.net.URI("https://img.icons8.com/material-outlined/24/stopwatch.png").toURL();
            java.net.URL wifiUrl = new java.net.URI("https://img.icons8.com/ios-glyphs/30/wifi-logo.png").toURL();
            java.net.URL toiletUrl = new java.net.URI("https://img.icons8.com/metro/26/toilet-bowl.png").toURL();
            java.net.URL dinnerUrl = new java.net.URI("https://img.icons8.com/fluency-systems-regular/48/dinner-time.png").toURL();
            JLabel stopwatchIcon = new JLabel(new ImageIcon(new ImageIcon(stopwatchUrl).getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));
            JLabel wifiIcon = new JLabel(new ImageIcon(new ImageIcon(wifiUrl).getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));
            JLabel toiletIcon = new JLabel(new ImageIcon(new ImageIcon(toiletUrl).getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));
            JLabel dinnerIcon = new JLabel(new ImageIcon(new ImageIcon(dinnerUrl).getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));
            Dimension iconDim = new Dimension(32, 32);
            stopwatchIcon.setPreferredSize(iconDim);
            wifiIcon.setPreferredSize(iconDim);
            toiletIcon.setPreferredSize(iconDim);
            dinnerIcon.setPreferredSize(iconDim);
            iconsPanel.add(stopwatchIcon);
            iconsPanel.add(wifiIcon);
            iconsPanel.add(toiletIcon);
            iconsPanel.add(dinnerIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iconsPanel;
    }
    private JLabel createRouteLabel() {
        JLabel label = new JLabel(trip.getOrigin() + " → " + trip.getDestination());
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(new Color(30, 30, 30));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    private JPanel createCancelButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton cancelButton = new JButton("Cancel") {
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
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        cancelButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Rezervasyonunuzu iptal etmek istediğinizden emin misiniz?", "Rezervasyon İptali", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                CancelReservationCommand cmd = new CancelReservationCommand(customer, trip, reservedSeatNumber, reservedGender, mainView.isAdmin());
                mainView.getCommandCaller().executeCommand(cmd);
                JOptionPane.showMessageDialog(mainFrame, mainView.isAdmin() ? "Admin tarafından rezervasyon başarıyla iptal edildi." : "Rezervasyonunuz başarıyla iptal edildi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                if (mainView != null) {
                    mainView.updateReservationsPanel(customer, reservationsPanel);
                }
            }
        });
        
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    public void setUserInfo(String userName, String userEmail) {
        if (userInfoLabel != null) {
            userInfoLabel.setText("Kullanıcı: " + userName + " (" + userEmail + ")");
        }
    }
} 