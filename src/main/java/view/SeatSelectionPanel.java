package view;

import services.DatabaseService;
import models.Customer;
import models.Voyage;
import models.Seat;
import commands.ReservationCommand;
import commands.CommandCaller;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SeatSelectionPanel extends JPanel {
    private int voyageId;
    private int seatCount;
    private String seatArrangement;
    private Customer customer;
    private JButton[] seatButtons;
    private static ImageIcon seatIcon;
    private CommandCaller commandCaller;
    private Voyage voyage;
    private Map<Integer, Seat> seats;

    public SeatSelectionPanel(int voyageId, int seatCount, String seatArrangement, Customer customer) {
        this.voyageId = voyageId;
        this.seatArrangement = seatArrangement;
        this.customer = customer;
        this.commandCaller = new CommandCaller();
        this.seats = new HashMap<>();
        
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
                URL iconUrl = new URL("https://img.icons8.com/external-goofy-flat-kerismaker/96/external-Seat-car-auto-parts-goofy-flat-kerismaker.png");
                ImageIcon tempIcon = new ImageIcon(iconUrl);
                if (tempIcon.getImage() != null) {
                    seatIcon = tempIcon;
                }
            } catch (Exception e) {
                System.err.println("Failed to load seat icon: " + e.getMessage());
                seatIcon = null;
            }
        }

        JLabel title = new JLabel("Koltuk Seçimi", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel seatGrid = createSeatGrid();
        JScrollPane scrollPane = new JScrollPane(seatGrid);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // İptal butonu
        JButton cancelButton = new JButton("İptal") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 53, 69));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 16));
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> {
            // Pencereyi kapat
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createSeatGrid() {
        int seatsPerRow = getSeatsPerRow();
        int rows = seatCount / seatsPerRow;
        int extra = seatCount % seatsPerRow;
        if (extra > 0) rows++;
        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        seatButtons = new JButton[seatCount];
        List<Integer> reserved = DatabaseService.getReservedSeats(voyageId);
        System.out.println("Seat count: " + seatCount + ", Reserved seats: " + reserved);
        int seatNum = 1;
        
        // For bus arrangement
        if (seatArrangement.equals("2+1")) {
            for (int r = 0; r < 13; r++) {
                JPanel rowPanel = new JPanel();
                rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
                rowPanel.setOpaque(false);
                
                // Single seat on left
                rowPanel.add(createSeatButton(seatNum, reserved.contains(seatNum)));
                rowPanel.add(Box.createHorizontalStrut(6));
                
                // Corridor space
                rowPanel.add(Box.createHorizontalStrut(24));
                
                // Double seats on right
                JPanel doubleSeatsPanel = new JPanel();
                doubleSeatsPanel.setLayout(new BoxLayout(doubleSeatsPanel, BoxLayout.Y_AXIS));
                doubleSeatsPanel.setOpaque(false);
                
                JPanel seatsRow = new JPanel();
                seatsRow.setLayout(new BoxLayout(seatsRow, BoxLayout.X_AXIS));
                seatsRow.setOpaque(false);
                
                // Add seats in 2nd and 3rd columns only up to row 6
                if (r < 6) {
                    seatsRow.add(createSeatButton(seatNum + 1, reserved.contains(seatNum + 1)));
                    seatsRow.add(Box.createHorizontalStrut(6));
                    seatsRow.add(createSeatButton(seatNum + 2, reserved.contains(seatNum + 2)));
                } else if (r == 6) {
                    // Row 7: No seats in 2nd and 3rd columns
                    seatsRow.add(Box.createHorizontalStrut(108)); // Space for two seats
                } else {
                    // From row 8 onwards: Same as 1st column
                    seatsRow.add(createSeatButton(seatNum + 1, reserved.contains(seatNum + 1)));
                    seatsRow.add(Box.createHorizontalStrut(6));
                    seatsRow.add(createSeatButton(seatNum + 2, reserved.contains(seatNum + 2)));
                }
                
                doubleSeatsPanel.add(seatsRow);
                rowPanel.add(doubleSeatsPanel);
                grid.add(rowPanel);
                grid.add(Box.createVerticalStrut(8));
                seatNum += 3;
            }
        } else {
            // Original flight arrangement code
            for (int r = 0; r < rows; r++) {
                JPanel rowPanel = new JPanel();
                rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
                rowPanel.setOpaque(false);

                // Calculate middle rows for empty space
                int middleStart = rows / 2 - 1;
                int middleEnd = rows / 2;

                if (r == middleStart || r == middleEnd) {
                    // Create exit label panel
                    JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    exitPanel.setOpaque(false);
                    
                    // Left side EXIT
                    JLabel leftExit = new JLabel("EXIT");
                    leftExit.setFont(new Font("Arial", Font.BOLD, 16));
                    leftExit.setForeground(Color.RED);
                    exitPanel.add(leftExit);
                    
                    // Corridor space
                    exitPanel.add(Box.createHorizontalStrut(24));
                    
                    // Right side EXIT
                    JLabel rightExit = new JLabel("EXIT");
                    rightExit.setFont(new Font("Arial", Font.BOLD, 16));
                    rightExit.setForeground(Color.RED);
                    exitPanel.add(rightExit);
                    
                    rowPanel.add(exitPanel);
                } else {
                    // Left side seats (3 columns)
                    for (int i = 0; i < 3 && seatNum <= seatCount; i++) {
                        rowPanel.add(createSeatButton(seatNum, reserved.contains(seatNum)));
                        rowPanel.add(Box.createHorizontalStrut(6));
                        seatNum++;
                    }

                    // Corridor space
                    rowPanel.add(Box.createHorizontalStrut(24));

                    // Right side seats (3 columns)
                    for (int i = 0; i < 3 && seatNum <= seatCount; i++) {
                        rowPanel.add(createSeatButton(seatNum, reserved.contains(seatNum)));
                        rowPanel.add(Box.createHorizontalStrut(6));
                        seatNum++;
                    }
                }
                grid.add(rowPanel);
                grid.add(Box.createVerticalStrut(8));
            }
        }
        return grid;
    }

    private JButton createSeatButton(int seatNum, boolean isReserved) {
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
            btn.setBackground(new Color(220, 53, 69)); // kırmızı
            btn.setForeground(Color.WHITE);
            btn.setText("DOLU");
            btn.setToolTipText("Dolu");
            btn.setEnabled(false);
        } else {
            btn.setForeground(Color.WHITE);
            btn.setText("");
            btn.setToolTipText("Boş");
            btn.addActionListener(e -> {
                if (customer == null) {
                    JOptionPane.showMessageDialog(this, "Giriş yapmalısınız!", "Uyarı", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Voyage nesnesini al
                voyage = Voyage.getVoyageHashMap().get(voyageId);
                if (voyage == null) {
                    JOptionPane.showMessageDialog(this, "Sefer bulunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // ReservationCommand'ı çalıştır
                ReservationCommand reservationCmd = new ReservationCommand(customer, voyage, seatNum);
                commandCaller.executeCommand(reservationCmd);
                // Veritabanına da ekle
                boolean ok = DatabaseService.addReservation(Integer.parseInt(customer.getId()), voyageId, seatNum);
                if (ok) {
                    btn.setBackground(new Color(220, 53, 69));
                    btn.setText("DOLU");
                    btn.setEnabled(false);
                    btn.setToolTipText("Dolu");
                    JOptionPane.showMessageDialog(this, "Rezervasyon başarılı!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Hata durumunda komutu geri al
                    commandCaller.undoLast();
                    JOptionPane.showMessageDialog(this, "Rezervasyon yapılamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
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
} 