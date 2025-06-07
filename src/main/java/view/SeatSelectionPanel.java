package view;

import services.DatabaseService;
import models.Customer;
import models.Voyage;
import models.Seat;
import commands.CommandCaller;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class SeatSelectionPanel extends JPanel {
    private int voyageId;
    private int seatCount;
    private String seatArrangement;
    private Customer customer;
    private JButton[] seatButtons;
    private static ImageIcon seatIcon;
    private CommandCaller commandCaller;
    private Voyage voyage;
    public Map<Integer, Seat> seats;
    private java.util.List<SelectedSeat> selectedSeats = new ArrayList<>();
    private Map<Integer, String> reservedSeatsWithGender;
    private MainView mainView;
    private JPanel reservationsPanel;
    private JPanel seatGridPanel;
    private JLabel selectedSeatsLabel;
    private JLabel totalPriceLabel;
    private JButton reserveButton;

    public SeatSelectionPanel(int voyageId, int seatCount, String seatArrangement, Customer customer, MainView mainView, JPanel reservationsPanel) {
        this.voyageId = voyageId;
        this.seatArrangement = seatArrangement;
        this.customer = customer;
        this.commandCaller = new CommandCaller();
        this.seats = new HashMap<>();
        this.mainView = mainView;
        this.reservationsPanel = reservationsPanel;
        
        // Rezerve edilmiş koltukları bir kez al
        this.reservedSeatsWithGender = DatabaseService.getReservedSeatsWithGender(voyageId);
        
        // Initialize seats
        for (int i = 1; i <= seatCount; i++) {
            Seat seat = new Seat(voyageId + i, i);
            seats.put(i, seat);
        }
        
        // Calculate seatCount based on seatArrangement
        if (seatArrangement.equals("2+2")) {
            this.seatCount = 46; // 2+2 arrangement: 46 seats
        } else if (seatArrangement.equals("2+1")) {
            this.seatCount = 37; // 2+1 arrangement: 13 single + 12*2 double = 37 seats
        } else {
            this.seatCount = 180; // Default for flights
        }
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Koltuk ikonu bir kez yükle
        if (seatIcon == null) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/main/resources/seat.png"));
                Image scaledImage = originalImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                seatIcon = new ImageIcon(scaledImage);
            } catch (Exception e) {
                System.err.println("Failed to load seat icon: " + e.getMessage());
                seatIcon = null;
            }
        }

        JLabel title = new JLabel("Koltuk Seçimi", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // Koltuk grid paneli
        seatGridPanel = new JPanel();
        seatGridPanel.setOpaque(false);
        seatGridPanel.setLayout(new BoxLayout(seatGridPanel, BoxLayout.Y_AXIS));
        createSeatGrid();
        JScrollPane scrollPane = new JScrollPane(seatGridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Alt panel
        JPanel bottomPanel = new JPanel(new BorderLayout(20, 0));
        bottomPanel.setOpaque(false);

        // Seçili koltuklar ve toplam fiyat
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        selectedSeatsLabel = new JLabel("Seçili Koltuk: -");
        selectedSeatsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        totalPriceLabel = new JLabel("Toplam: 0 TL");
        totalPriceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoPanel.add(selectedSeatsLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(totalPriceLabel);
        bottomPanel.add(infoPanel, BorderLayout.WEST);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        buttonPanel.setOpaque(false);

        // İptal butonu
        JButton cancelButton = new JButton("İptal") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 53, 69));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setPreferredSize(new Dimension(120, 38));
        cancelButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();
        });

        // Rezervasyon butonu
        reserveButton = new JButton("Rezervasyon Yap") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(52, 152, 219).darker(),
                    getWidth(), getHeight(), new Color(52, 152, 219)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        reserveButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        reserveButton.setForeground(Color.WHITE);
        reserveButton.setBorderPainted(false);
        reserveButton.setFocusPainted(false);
        reserveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reserveButton.setPreferredSize(new Dimension(150, 38));
        reserveButton.setEnabled(false);
        reserveButton.addActionListener(e -> {
            // Admin ise hata ver
            if (customer != null && "Admin".equalsIgnoreCase(customer.getUser_type())) {
                JOptionPane.showMessageDialog(this, "Admin kullanıcı rezervasyon yapamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!selectedSeats.isEmpty()) {
                PaymentDialog paymentDialog = new PaymentDialog(selectedSeats, customer, voyageId, this, mainView, reservationsPanel);
                paymentDialog.setVisible(true);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(reserveButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void createSeatGrid() {
        seatGridPanel.removeAll();
        int seatNum = 1;
        if (seatArrangement.equals("2+1")) {
            int rows = 13;
            for (int r = 0; r < rows; r++) {
                JPanel rowPanel = new JPanel();
                rowPanel.setOpaque(false);
                rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
                boolean isExitRow = (r == 6);
                if (!isExitRow) {
                    // Sol tekli koltuk
                    if (seatNum <= seatCount) {
                        rowPanel.add(createSeatButton(seatNum, reservedSeatsWithGender.containsKey(seatNum), reservedSeatsWithGender.get(seatNum)));
                        seatNum++;
                    } else {
                        rowPanel.add(Box.createHorizontalStrut(48));
                    }
                    // Koridor boşluğu
                    rowPanel.add(Box.createHorizontalStrut(32));
                    // Sağda iki koltuk
                    for (int i = 0; i < 2; i++) {
                        if (seatNum <= seatCount) {
                            rowPanel.add(createSeatButton(seatNum, reservedSeatsWithGender.containsKey(seatNum), reservedSeatsWithGender.get(seatNum)));
                            seatNum++;
                        } else {
                            rowPanel.add(Box.createHorizontalStrut(48));
                        }
                        if (i == 0) rowPanel.add(Box.createHorizontalStrut(6));
                    }
                } else {
                    // Çıkış satırı: sadece boşluk ve çıkış etiketi
                    rowPanel.add(Box.createHorizontalStrut(48));
                    rowPanel.add(Box.createHorizontalStrut(32));
                    JLabel exitLabel = new JLabel("ÇIKIŞ");
                    exitLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    exitLabel.setForeground(Color.RED);
                    rowPanel.add(exitLabel);
                }
                seatGridPanel.add(rowPanel);
                seatGridPanel.add(Box.createVerticalStrut(12));
            }
        } else if (seatArrangement.equals("2+2")) {
            int rows = seatCount / 4 + (seatCount % 4 == 0 ? 0 : 1);
            for (int r = 0; r < rows; r++) {
                JPanel rowPanel = new JPanel();
                rowPanel.setOpaque(false);
                rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
                boolean isExitRow = (r == rows/2-1);
                if (!isExitRow) {
                    // Sol iki koltuk
                    for (int i = 0; i < 2; i++) {
                        if (seatNum <= seatCount) {
                            rowPanel.add(createSeatButton(seatNum, reservedSeatsWithGender.containsKey(seatNum), reservedSeatsWithGender.get(seatNum)));
                            seatNum++;
                        } else {
                            rowPanel.add(Box.createHorizontalStrut(48));
                        }
                        if (i == 0) rowPanel.add(Box.createHorizontalStrut(6));
                    }
                    // Koridor boşluğu
                    rowPanel.add(Box.createHorizontalStrut(32));
                    // Sağ iki koltuk
                    for (int i = 0; i < 2; i++) {
                        if (seatNum <= seatCount) {
                            rowPanel.add(createSeatButton(seatNum, reservedSeatsWithGender.containsKey(seatNum), reservedSeatsWithGender.get(seatNum)));
                            seatNum++;
                        } else {
                            rowPanel.add(Box.createHorizontalStrut(48));
                        }
                        if (i == 0) rowPanel.add(Box.createHorizontalStrut(6));
                    }
                } else {
                    // Çıkış satırı: sadece boşluk ve çıkış etiketi
                    rowPanel.add(Box.createHorizontalStrut(48+6));
                    rowPanel.add(Box.createHorizontalStrut(32));
                    JLabel exitLabel = new JLabel("ÇIKIŞ");
                    exitLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    exitLabel.setForeground(Color.RED);
                    rowPanel.add(exitLabel);
                }
                seatGridPanel.add(rowPanel);
                seatGridPanel.add(Box.createVerticalStrut(12));
            }
        } else if (seatArrangement.equals("3+3")) {
            int rows = seatCount / 6 + (seatCount % 6 == 0 ? 0 : 1);
            for (int r = 0; r < rows; r++) {
                JPanel rowPanel = new JPanel();
                rowPanel.setOpaque(false);
                rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
                boolean isExitRow = (r == 0 || r == rows/2-1 || r == rows-1);
                if (!isExitRow) {
                    // Sol üç koltuk
                    for (int i = 0; i < 3; i++) {
                        if (seatNum <= seatCount) {
                            rowPanel.add(createSeatButton(seatNum, reservedSeatsWithGender.containsKey(seatNum), reservedSeatsWithGender.get(seatNum)));
                            seatNum++;
                        } else {
                            rowPanel.add(Box.createHorizontalStrut(48));
                        }
                        if (i < 2) rowPanel.add(Box.createHorizontalStrut(6));
                    }
                    // Koridor boşluğu
                    rowPanel.add(Box.createHorizontalStrut(40));
                    // Sağ üç koltuk
                    for (int i = 0; i < 3; i++) {
                        if (seatNum <= seatCount) {
                            rowPanel.add(createSeatButton(seatNum, reservedSeatsWithGender.containsKey(seatNum), reservedSeatsWithGender.get(seatNum)));
                            seatNum++;
                        } else {
                            rowPanel.add(Box.createHorizontalStrut(48));
                        }
                        if (i < 2) rowPanel.add(Box.createHorizontalStrut(6));
                    }
                } else {
                    // Çıkış satırı: sadece boşluk ve çıkış etiketi
                    rowPanel.add(Box.createHorizontalStrut(48*3+6*2));
                    rowPanel.add(Box.createHorizontalStrut(40));
                    JLabel exitLabel = new JLabel("ÇIKIŞ");
                    exitLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    exitLabel.setForeground(Color.RED);
                    rowPanel.add(exitLabel);
                }
                seatGridPanel.add(rowPanel);
                seatGridPanel.add(Box.createVerticalStrut(12));
            }
        }
        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    private JButton createSeatButton(int seatNum, boolean isReserved, String gender) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(48, 48));
        btn.setMaximumSize(new Dimension(48, 48));
        btn.setMinimumSize(new Dimension(48, 48));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        if (isReserved) {
            if ("Kadın".equals(gender)) {
                btn.setBackground(new Color(255, 105, 180)); // PEMBE
            } else if ("Erkek".equals(gender)) {
                btn.setBackground(new Color(52, 152, 219)); // MAVİ
            } else {
                btn.setBackground(new Color(220, 53, 69)); // Kırmızı (bilinmiyorsa)
            }
            btn.setForeground(Color.WHITE);
            btn.setText("DOLU");
            btn.setToolTipText("Dolu");
            btn.setEnabled(false);
        } else {
            btn.setForeground(Color.WHITE);
            btn.setText("");
            btn.setToolTipText("Boş");
            btn.addActionListener(e -> {
                handleSeatClick(btn, seatNum);
            });
        }
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Yuvarlak arka plan
                g2.setColor(btn.getBackground());
                g2.fillOval(0, 0, c.getWidth(), c.getHeight());
                // Gölge efekti
                g2.setColor(new Color(0,0,0,20));
                g2.fillOval(4, c.getHeight()-10, c.getWidth()-8, 10);
                // İçerik
                if (isReserved) {
                    g2.setFont(new Font("Arial", Font.BOLD, 16));
                    g2.setColor(Color.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    String text = "DOLU";
                    int x = (c.getWidth() - fm.stringWidth(text)) / 2;
                    int y = (c.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(text, x, y);
                } else if (seatIcon != null) {
                    int iconW = 32, iconH = 32;
                    int x = (c.getWidth() - iconW) / 2;
                    int y = (c.getHeight() - iconH) / 2;
                    g2.drawImage(seatIcon.getImage(), x, y, iconW, iconH, null);
                } else {
                    g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                    g2.setColor(Color.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    String emoji = "\uD83D\uDCBA"; // 💺
                    int x = (c.getWidth() - fm.stringWidth(emoji)) / 2;
                    int y = (c.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(emoji, x, y);
                }
                g2.dispose();
            }
        });
        return btn;
    }

    private void handleSeatClick(JButton btn, int seatNum) {
        if (selectedSeats.stream().anyMatch(s -> s.seatNum == seatNum)) {
            // Koltuk zaten seçili, seçimi kaldır
            selectedSeats.removeIf(s -> s.seatNum == seatNum);
            btn.setBackground(new Color(52, 152, 219));
            btn.setToolTipText("Boş");
            return;
        }

        // Yan koltuk kontrolü
        boolean applyGenderRule = true;
        boolean isSingle = false;
        int seatsPerRow = getSeatsPerRow();
        int col = (seatNum - 1) % seatsPerRow;

        // Tekli koltuk kontrolü
        if (seatArrangement.equals("2+1")) {
            isSingle = (col == 1); // Orta koltuk tekli
        }

        String allowedGender = null;
        if (applyGenderRule && !isSingle) {
            int leftSeatNum = (col > 0) ? seatNum - 1 : -1;
            int rightSeatNum = (col < seatsPerRow - 1 && seatNum + 1 <= seatCount) ? seatNum + 1 : -1;
            String leftGender = (leftSeatNum > 0 && leftSeatNum <= seatCount) ? reservedSeatsWithGender.get(leftSeatNum) : null;
            String rightGender = (rightSeatNum > 0 && rightSeatNum <= seatCount) ? reservedSeatsWithGender.get(rightSeatNum) : null;
            if (leftGender != null) allowedGender = leftGender;
            if (rightGender != null) allowedGender = rightGender;
        }
        // Cinsiyet seçimi penceresi
        String[] options;
        if (allowedGender == null) {
            options = new String[]{"Kadın", "Erkek"};
        } else if (allowedGender.equals("Kadın")) {
            options = new String[]{"Kadın"};
        } else {
            options = new String[]{"Erkek"};
        }
        int genderChoice = JOptionPane.showOptionDialog(
            btn,
            allowedGender == null ? "Lütfen cinsiyet seçiniz:" : ("Yan koltukta cinsiyet kuralı: Sadece " + allowedGender + " seçebilirsiniz."),
            "Cinsiyet Seçimi",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        if (genderChoice == JOptionPane.CLOSED_OPTION) return; // Seçim yapılmazsa çık
        String selectedGender = options[genderChoice];
        // Seçili koltuklara ekle
        selectedSeats.add(new SelectedSeat(seatNum, selectedGender, btn));
        if (selectedGender.equals("Kadın")) {
            btn.setBackground(new Color(255, 105, 180)); // PEMBE
        } else {
            btn.setBackground(new Color(52, 152, 219)); // MAVİ
        }
        btn.setText("SEÇİLDİ");
        btn.setToolTipText(selectedGender + " seçildi");
        updateSelectedSeatsLabel();
        reserveButton.setEnabled(true);
    }

    private int getSeatsPerRow() {
        if (seatArrangement.equals("2+1")) {
            return 3; // 1 single + 2 double seats
        } else if (seatArrangement.equals("2+2")) {
            return 4;
        } else if (seatArrangement.equals("3+3")) {
            return 6;
        } else {
            return 3; // Default to 2+1 if unknown
        }
    }

    private Seat getSeatAt(int row, int col) {
        int seatNumber = calculateSeatNumber(row, col);
        return seats.get(seatNumber);
    }

    private int calculateSeatNumber(int row, int col) {
        int seatsPerRow = getSeatsPerRow();
        return (row - 1) * seatsPerRow + col;
    }

    private void drawSeats(Graphics2D g2d) {
        int seatWidth = 30;
        int seatHeight = 30;
        int spacing = 10;
        int startX = (getWidth() - (seatWidth * 6 + spacing * 5)) / 2;
        int startY = 50;
        
        // Draw plane exit doors for flight
        if (voyage != null && voyage.getType().equals("Flight")) {
            // Front exit
            g2d.setColor(Color.RED);
            g2d.drawString("EXIT", startX - 40, startY + 15);
            g2d.drawRect(startX - 35, startY, 20, 40);
            
            // Rear exit
            g2d.drawString("EXIT", startX - 40, startY + 200);
            g2d.drawRect(startX - 35, startY + 200, 20, 40);
            
            // Middle exits
            g2d.drawString("EXIT", startX + 200, startY + 100);
            g2d.drawRect(startX + 200, startY + 100, 20, 40);
            g2d.drawString("EXIT", startX + 200, startY + 150);
            g2d.drawRect(startX + 200, startY + 150, 20, 40);
        }

        // Draw seats
        for (int row = 0; row < 30; row++) {
            for (int col = 0; col < 6; col++) {
                int x = startX + col * (seatWidth + spacing);
                int y = startY + row * (seatHeight + spacing);
                
                // Skip seats for aisle
                if (col == 2 || col == 3) {
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.fillRect(x, y, seatWidth, seatHeight);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, seatWidth, seatHeight);
                    continue;
                }
                
                // Draw seat
                Seat seat = getSeatAt(row + 1, col + 1);
                if (seat != null) {
                    if (seat.getStatus().equals("RESERVED")) {
                        g2d.setColor(Color.RED);
                    } else {
                        g2d.setColor(Color.GREEN);
                    }
                    g2d.fillRect(x, y, seatWidth, seatHeight);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, seatWidth, seatHeight);
                    
                    // Draw seat number
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.valueOf(seat.getNumber()), x + 10, y + 20);
                }
            }
        }
    }

    public static class SelectedSeat {
        public int seatNum;
        public String gender;
        public JButton button;
        public SelectedSeat(int seatNum, String gender, JButton button) {
            this.seatNum = seatNum;
            this.gender = gender;
            this.button = button;
        }
    }

    private void updateSelectedSeatsLabel() {
        StringBuilder sb = new StringBuilder("Seçili Koltuklar: ");
        double totalPrice = 0;
        Voyage voyage = models.Voyage.getVoyageHashMap().get(voyageId);
        
        for (int i = 0; i < selectedSeats.size(); i++) {
            SelectedSeat seat = selectedSeats.get(i);
            sb.append(seat.seatNum);
            if (i < selectedSeats.size() - 1) {
                sb.append(", ");
            }
            totalPrice += voyage.getPrice();
        }
        
        selectedSeatsLabel.setText(sb.toString());
        totalPriceLabel.setText(String.format("Toplam: %.2f TL", totalPrice));
    }
} 