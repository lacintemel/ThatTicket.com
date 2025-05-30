package view;
import javax.swing.*;
import java.awt.*;
import models.Customer;
import commands.DeleteVoyageCommand;
import commands.CommandCaller;
import services.Admin;
import models.Voyage;

    // TripCardPanel: Modern card for a trip
    public class TripCardPanel extends JPanel {
        private final Customer customer;
        private final Voyage trip;
        public TripCardPanel(Voyage trip, Customer customer) {
            this.customer = customer;
            this.trip = trip;
            setOpaque(false);
            setLayout(new BorderLayout());
            setBackground(new Color(0,0,0,0));

            // Kart yüksekliğini sabit tut
            setPreferredSize(new Dimension(600, 180)); // Increased height for better spacing
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
            setMinimumSize(new Dimension(300, 180));

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setOpaque(true);
            card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
            ));
            // Responsive: min width, no max width, sabit yükseklik
            card.setPreferredSize(new Dimension(600, 160));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
            card.setMinimumSize(new Dimension(300, 160));

            // Admin kontrolleri için üst panel
            JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            adminPanel.setOpaque(false);
            
            // Sadece admin ise göster
            if (customer.getUser_type().equals("Admin")) {
                // Admin nesnesini oluştur
                Admin admin = new Admin(customer.getId(), "1111", customer.getName(), customer.getEmail(), customer.getPassword());
                
                // Silme butonu
                JButton deleteBtn = new JButton("🗑️") {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(255, 0, 0, 40));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                        super.paintComponent(g2);
                        g2.dispose();
                    }
                };
                deleteBtn.setForeground(new Color(255, 0, 0));
                deleteBtn.setFont(new Font("Arial", Font.PLAIN, 16));
                deleteBtn.setBorderPainted(false);
                deleteBtn.setFocusPainted(false);
                deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                deleteBtn.setPreferredSize(new Dimension(32, 32));
                deleteBtn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "Bu seferi silmek istediğinizden emin misiniz?",
                        "Sefer Silme",
                        JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        DeleteVoyageCommand deleteCmd = new DeleteVoyageCommand(trip, admin);
                        CommandCaller caller = new CommandCaller();
                        caller.executeCommand(deleteCmd);
                        // Veritabanından da sil
                        services.DatabaseService.deleteVoyageFromDB(trip.getVoyageId());
                        // Kartı kaldır
                        Container parent = getParent();
                        if (parent != null) {
                            parent.remove(this);
                            parent.revalidate();
                            parent.repaint();
                        }
                    }
                });
                adminPanel.add(deleteBtn);

                // Güncelleme butonu
                JButton updateBtn = new JButton("✏️") {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(0, 120, 255, 40));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                        super.paintComponent(g2);
                        g2.dispose();
                    }
                };
                updateBtn.setForeground(new Color(0, 120, 255));
                updateBtn.setFont(new Font("Arial", Font.PLAIN, 16));
                updateBtn.setBorderPainted(false);
                updateBtn.setFocusPainted(false);
                updateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                updateBtn.setPreferredSize(new Dimension(32, 32));
                updateBtn.addActionListener(e -> {
                    // Güncelleme dialogunu göster
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    AdminVoyagePanel updatePanel = new AdminVoyagePanel(admin, customer, topFrame, trip);
                    JDialog dialog = new JDialog(topFrame, "Sefer Güncelle", true);
                    dialog.setContentPane(updatePanel);
                    dialog.pack();
                    dialog.setLocationRelativeTo(topFrame);
                    dialog.setVisible(true);
                });
                adminPanel.add(updateBtn);
            }
            card.add(adminPanel, BorderLayout.NORTH);

            // Sol içerik (firma adı, ikonlar, saatler, şehirler)
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setOpaque(false);
            // Firma adı
            JLabel firmLabel = new JLabel("<html><b>" + trip.getFirm() + "</b></html>");
            firmLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            leftPanel.add(firmLabel);
            // İkonlar
            JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            iconPanel.setOpaque(false);
            
            // Create a custom font for icons
            Font iconFont = new Font("Segoe UI", Font.PLAIN, 16);
            
            JLabel tempIcon = new JLabel("❄");
            tempIcon.setFont(iconFont);
            tempIcon.setToolTipText("Air Conditioning");
            iconPanel.add(tempIcon);
            
            JLabel wifiIcon = new JLabel("📶");
            wifiIcon.setFont(iconFont);
            wifiIcon.setToolTipText("WiFi");
            iconPanel.add(wifiIcon);
            
            JLabel powerIcon = new JLabel("⚡");
            powerIcon.setFont(iconFont);
            powerIcon.setToolTipText("Power Outlet");
            iconPanel.add(powerIcon);
            
            JLabel entIcon = new JLabel("🎬");
            entIcon.setFont(iconFont);
            entIcon.setToolTipText("Entertainment");
            iconPanel.add(entIcon);
            
            JLabel restIcon = new JLabel("🚻");
            restIcon.setFont(iconFont);
            restIcon.setToolTipText("Restroom");
            iconPanel.add(restIcon);
            
            JLabel refreshIcon = new JLabel("☕");
            refreshIcon.setFont(iconFont);
            refreshIcon.setToolTipText("Refreshments");
            iconPanel.add(refreshIcon);
            
            // Seat arrangement and icon
            JPanel seatIconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            seatIconPanel.setOpaque(false);
            JLabel seatIcon = new JLabel("💺");
            seatIcon.setFont(iconFont);
            JLabel seatLabel = new JLabel(trip.getSeatArrangement());
            seatLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            seatLabel.setForeground(new Color(52, 152, 219));
            seatIconPanel.add(seatIcon);
            seatIconPanel.add(seatLabel);
            
            // Wrap seatIconPanel in a container
            JPanel seatContainerPanel = new JPanel(new BorderLayout());
            seatContainerPanel.setBackground(new Color(52, 152, 219, 10));
            seatContainerPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, new Color(52, 152, 219, 30)),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
            ));
            seatContainerPanel.add(seatIconPanel, BorderLayout.CENTER);
            iconPanel.add(seatContainerPanel);
            leftPanel.add(iconPanel);
            
            // Saatler ve şehirler
            JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
            timePanel.setOpaque(false);
            
            // Departure panel
            JPanel depPanel = new JPanel();
            depPanel.setOpaque(false);
            depPanel.setLayout(new BoxLayout(depPanel, BoxLayout.Y_AXIS));
            JLabel depLabel = new JLabel("Departure");
            depLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            depLabel.setForeground(new Color(120,120,120));
            depPanel.add(depLabel);
            JLabel depTime = new JLabel("<html><b>" + (trip.getStartTime().split(" ").length > 1 ? trip.getStartTime().split(" ")[1] : trip.getStartTime()) + "</b></html>");
            depTime.setFont(new Font("Segoe UI", Font.BOLD, 20));
            depPanel.add(depTime);
            JLabel depCity = new JLabel(trip.getOrigin());
            depCity.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            depPanel.add(depCity);
            timePanel.add(depPanel);

            // Transport icon between cities
            JLabel transportIcon = new JLabel(trip.getType().equalsIgnoreCase("Bus") ? "🚌" : "✈");
            transportIcon.setFont(iconFont);
            timePanel.add(transportIcon);

            // Arrival panel
            JPanel arrPanel = new JPanel();
            arrPanel.setOpaque(false);
            arrPanel.setLayout(new BoxLayout(arrPanel, BoxLayout.Y_AXIS));
            JLabel arrLabel = new JLabel("Arrival");
            arrLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            arrLabel.setForeground(new Color(120,120,120));
            arrPanel.add(arrLabel);
            JLabel arrTime = new JLabel("<html><b>" + (trip.getArrivalTime().split(" ").length > 1 ? trip.getArrivalTime().split(" ")[1] : trip.getArrivalTime()) + "</b></html>");
            arrTime.setFont(new Font("Segoe UI", Font.BOLD, 20));
            arrPanel.add(arrTime);
            JLabel arrCity = new JLabel(trip.getDestination());
            arrCity.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            arrPanel.add(arrCity);
            timePanel.add(arrPanel);
            leftPanel.add(timePanel);
            card.add(leftPanel, BorderLayout.CENTER);

            // Sağ panel: üstte fiyat, altta buton
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.setOpaque(false);
            // Fiyat sağ üstte
            JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            pricePanel.setOpaque(false);
            JLabel priceIcon = new JLabel("₺");
            priceIcon.setFont(iconFont);
            JLabel priceLabel = new JLabel(" " + (int)trip.getPrice());
            priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            priceLabel.setForeground(new Color(52, 152, 219));
            pricePanel.add(priceIcon);
            pricePanel.add(priceLabel);
            JLabel oldPriceLabel = new JLabel("₺" + (int)(trip.getPrice() * 1.1));
            oldPriceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            oldPriceLabel.setForeground(new Color(180,180,180));
            oldPriceLabel.setText("<html><strike>" + oldPriceLabel.getText() + "</strike></html>");
            pricePanel.add(Box.createHorizontalStrut(8));
            pricePanel.add(oldPriceLabel);
            rightPanel.add(pricePanel, BorderLayout.NORTH);
            // Buton sağ altta
            JButton buyButton = new JButton("Buy") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(52, 152, 219));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            buyButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            buyButton.setForeground(Color.WHITE);
            buyButton.setBorderPainted(false);
            buyButton.setFocusPainted(false);
            buyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            buyButton.addActionListener(e -> {
                int voyageId = trip.getVoyageId();
                int seatCount = trip.getSeatCount();
                String seatArrangement = trip.getSeatArrangement();
                SeatSelectionPanel seatPanel = new SeatSelectionPanel(voyageId, seatCount, seatArrangement, customer);
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                JDialog dialog = new JDialog(topFrame, "Seat Selection", true);
                dialog.setContentPane(seatPanel);
                dialog.pack();
                dialog.setLocationRelativeTo(topFrame);
                dialog.setVisible(true);
            });
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            btnPanel.setOpaque(false);
            btnPanel.add(buyButton);
            rightPanel.add(btnPanel, BorderLayout.SOUTH);
            card.add(rightPanel, BorderLayout.EAST);

            setLayout(new BorderLayout());
            add(Box.createVerticalStrut(6), BorderLayout.NORTH); // Kartlar arası padding
            add(card, BorderLayout.CENTER);
            add(Box.createVerticalStrut(6), BorderLayout.SOUTH);
        }
    }
        // Tam yuvarlak köşe için özel border
        class RoundedBorder extends javax.swing.border.AbstractBorder {
            private final int radius;
            private final Color color;
            public RoundedBorder(int radius, Color color) {
                this.radius = radius;
                this.color = color;
            }
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(x+1, y+1, width-3, height-3, radius, radius);
                g2.dispose();
            }
        }