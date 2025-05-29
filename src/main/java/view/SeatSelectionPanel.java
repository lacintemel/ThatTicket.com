package view;

import services.DatabaseService;
import models.Customer;
import models.Voyage;
import commands.ReservationCommand;
import commands.CommandCaller;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.net.URL;

public class SeatSelectionPanel extends JPanel {
    private int voyageId;
    private int seatCount;
    private String seatArrangement;
    private Customer customer;
    private JButton[] seatButtons;
    private static ImageIcon seatIcon;
    private CommandCaller commandCaller;

    public SeatSelectionPanel(int voyageId, int seatCount, String seatArrangement, Customer customer) {
        this.voyageId = voyageId;
        this.seatArrangement = seatArrangement;
        this.customer = customer;
        this.commandCaller = new CommandCaller();
        // Calculate seatCount based on seatArrangement
        if (seatArrangement.equals("2+2")) {
            this.seatCount = 46;
        } else if (seatArrangement.equals("2+1")) {
            this.seatCount = 37;
        } else {
            this.seatCount = 180; // Default for flights
        }
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Koltuk ikonu bir kez yükle
        if (seatIcon == null) {
            try {
                seatIcon = new ImageIcon(new URL("https://img.icons8.com/external-goofy-flat-kerismaker/96/external-Seat-car-auto-parts-goofy-flat-kerismaker.png"));
            } catch (Exception e) {
                seatIcon = null;
            }
        }

        JLabel title = new JLabel("Koltuk Seçimi", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel seatGrid = createSeatGrid();
        add(seatGrid, BorderLayout.CENTER);

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
        for (int r = 0; r < rows; r++) {
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
            rowPanel.setOpaque(false);
            int left = seatsPerRow == 3 ? 2 : seatsPerRow / 2;
            int right = seatsPerRow - left;
            // Sol koltuklar
            for (int i = 0; i < left && seatNum <= seatCount; i++, seatNum++) {
                rowPanel.add(createSeatButton(seatNum, reserved.contains(seatNum)));
                rowPanel.add(Box.createHorizontalStrut(6));
            }
            // Koridor boşluğu
            rowPanel.add(Box.createHorizontalStrut(24));
            // Sağ koltuklar
            for (int i = 0; i < right && seatNum <= seatCount; i++, seatNum++) {
                rowPanel.add(createSeatButton(seatNum, reserved.contains(seatNum)));
                rowPanel.add(Box.createHorizontalStrut(6));
            }
            grid.add(rowPanel);
            grid.add(Box.createVerticalStrut(8));
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
                Voyage voyage = Voyage.getVoyageHashMap().get(voyageId);
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
            return 3;
        } else if (seatArrangement.equals("2+2")) {
            return 4;
        } else if (seatArrangement.equals("3+2")) {
            return 5;
        } else {
            return 3; // Default to 2+1 if unknown
        }
    }
} 