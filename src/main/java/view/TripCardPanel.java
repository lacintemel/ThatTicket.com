package view;
import javax.swing.*;
import java.awt.*;
import models.Customer;
import models.Voyage;
import models.User;
import services.DatabaseService;
import java.net.URL;

    // TripCardPanel: Modern card for a trip
    public class TripCardPanel extends JPanel {
        private final Customer customer;
        private final Voyage trip;
        private JLabel firmLabel;
        private JLabel routeLabel;
        private JLabel timeLabel;
        private JLabel priceLabel;
        private JLabel availableSeatsLabel;
        private JButton buyButton;
        private JButton editButton;
        private JButton deleteButton;
        private int availableSeats;
        private JPanel routePanel;
        private JFrame mainFrame;
        private MainView mainView;
        private JPanel reservationsPanel;

        // Custom EmojiLabel class for better emoji rendering
      

        public TripCardPanel(Voyage trip, Customer customer, MainView mainView, JPanel reservationsPanel) {
            this.customer = customer;
            this.trip = trip;
            this.mainView = mainView;
            this.reservationsPanel = reservationsPanel;
            this.mainFrame = mainView.getMainFrame();
            setOpaque(false);
            setLayout(new BorderLayout());
            setBackground(new Color(0,0,0,0));
            setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
            setPreferredSize(new Dimension(getPreferredSize().width, 225));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 225));
            setMinimumSize(new Dimension(0, 225));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setAlignmentY(Component.CENTER_ALIGNMENT);

            // Determine color by vehicle type
            Color mainColor;
            if (trip.getType().equalsIgnoreCase("Bus")) {
                mainColor = new Color(52, 152, 219); // Mavi
            } else {
                mainColor = new Color(220, 53, 69); // Kırmızı
            }

            // Card panel tanımı (eski yeriyle aynı şekilde)
            JPanel card = new JPanel(new BorderLayout(15, 15)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // Drop shadow
                    g2.setColor(new Color(0,0,0,30));
                    g2.fillRoundRect(12, 12, getWidth()-24, getHeight()-24, 32, 32);
                    // Card gradient
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
            // Sol grup: admin ikonları, koltuk kutusu, firma adı
            JPanel leftTop = new JPanel();
            leftTop.setOpaque(false);
            leftTop.setLayout(new BoxLayout(leftTop, BoxLayout.X_AXIS));
            // Admin ikonları
            if (mainView.isAdmin()) {
                leftTop.add(createAdminIconsPanel());
                leftTop.add(Box.createHorizontalStrut(4));
            }
            // Koltuk düzeni kutusu
            leftTop.add(createSeatPanel(mainColor));
            leftTop.add(Box.createHorizontalStrut(4));
            // Firma adı
            firmLabel = createFirmLabel(mainColor);
            leftTop.add(firmLabel);
            topRow.add(leftTop, BorderLayout.WEST);
            
            timeLabel = createTimeLabel(mainColor);
            topRow.add(timeLabel, BorderLayout.CENTER);

            priceLabel = createPriceLabel(mainColor);
            topRow.add(priceLabel, BorderLayout.EAST);

            JPanel middleRow = new JPanel(new BorderLayout());
            middleRow.setOpaque(false);
            // Sağda boş koltuk kutusu
            availableSeats = trip.getSeatCount() - DatabaseService.getReservedSeats(trip.getVoyageId()).size();
            middleRow.add(createAvailablePanel(), BorderLayout.WEST);

            // --- ALT SATIR ---
            JPanel bottomRow = new JPanel(new BorderLayout());
            bottomRow.setOpaque(false);
            // Sol: ikonlar
            bottomRow.add(createIconsPanel(), BorderLayout.WEST);
            // Orta: rota bilgisi
            routeLabel = createRouteLabel();
            bottomRow.add(routeLabel, BorderLayout.CENTER);
            // Sağ: Sadece Satın Al butonu
            bottomRow.add(createBuyButtonPanel(mainColor), BorderLayout.EAST);

            // --- ANA PANEL ---
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

        
        }

        // --- YARDIMCI PANEL FONKSİYONLARI ---
        private JPanel createAdminIconsPanel() {
            JPanel adminIconsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
            adminIconsPanel.setOpaque(false);
            editButton = new JButton();
            try {
                URL editUrl = new URL("https://img.icons8.com/ios/50/edit--v1.png");
                editButton.setIcon(new ImageIcon(new ImageIcon(editUrl).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            } catch (Exception e) {
                editButton.setText("✏");
                editButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            }
            editButton.setBorderPainted(false);
            editButton.setContentAreaFilled(false);
            editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editButton.setToolTipText("Düzenle");
            editButton.addActionListener(e -> {
                System.out.println("\n=== Edit Button Clicked ===");
                if (mainView.isAdmin()) {
                    System.out.println("4. Admin check passed");
                    JFrame frame = mainView.getMainFrame();
                    System.out.println("5. Frame type: " + (frame != null ? frame.getClass().getName() : "null"));
                    
                    if (frame instanceof com.mycompany.aoopproject.AOOPProject) {
                        System.out.println("6. Frame is AOOPProject");
                        com.mycompany.aoopproject.AOOPProject aoopFrame = (com.mycompany.aoopproject.AOOPProject) frame;
                        User currentUser = mainView.getUser();
                        System.out.println("7. Current user type: " + (currentUser != null ? currentUser.getClass().getName() : "null"));
                        
                        System.out.println("8. Creating EditVoyagePanel");
                        EditVoyagePanel editPanel = new EditVoyagePanel(currentUser, trip, aoopFrame);
                        System.out.println("9. Showing EditVoyagePanel");
                        aoopFrame.showMainView(editPanel);
                    } else {
                        System.out.println("6. Error: Frame is not AOOPProject");
                    }
                } else {
                    System.out.println("4. Error: User is not admin");
                }
                System.out.println("=== End Edit Button Click ===\n");
            });
            deleteButton = new JButton();
            try {
                URL deleteUrl = new URL("https://img.icons8.com/fluency-systems-regular/48/filled-trash.png");
                deleteButton.setIcon(new ImageIcon(new ImageIcon(deleteUrl).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            } catch (Exception e) {
                deleteButton.setText("🗑");
                deleteButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            }
            deleteButton.setBorderPainted(false);
            deleteButton.setContentAreaFilled(false);
            deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteButton.setToolTipText("Sil");
            deleteButton.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(this, "Bu seferi silmek istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    DatabaseService.deleteVoyageFromDB(trip.getVoyageId());
                    Container parent = this.getParent();
                    if (parent != null) {
                        parent.remove(this);
                        parent.revalidate();
                        parent.repaint();
                    }
                }
            });
            adminIconsPanel.add(editButton);
            adminIconsPanel.add(deleteButton);
            return adminIconsPanel;
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
        private JPanel createAvailablePanel() {
            JPanel availablePanel = new JPanel() {
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
            availablePanel.setOpaque(false);
            availablePanel.setLayout(new GridBagLayout());
            int availableSeats = trip.getSeatCount() - DatabaseService.getReservedSeats(trip.getVoyageId()).size();
            JLabel availableLabel = new JLabel(availableSeats + " Boş Koltuk");
            availableLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            availableLabel.setForeground(new Color(80, 80, 80));
            availablePanel.add(availableLabel);
            FontMetrics fm2 = availableLabel.getFontMetrics(availableLabel.getFont());
            int textHeight2 = fm2.getHeight();
            int padding2 = 2;
            int boxHeight2 = textHeight2 + padding2 * 2;
            int boxWidth2 = availableLabel.getPreferredSize().width + 10;
            availablePanel.setBorder(BorderFactory.createEmptyBorder(padding2, 5, padding2, 5));
            availablePanel.setMaximumSize(new Dimension(boxWidth2, boxHeight2));
            availablePanel.setPreferredSize(new Dimension(boxWidth2, boxHeight2));
            availablePanel.setMinimumSize(new Dimension(boxWidth2, boxHeight2));
            return availablePanel;
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
        private JPanel createBuyButtonPanel(Color mainColor) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            panel.setOpaque(false);
            JButton buyButton = new JButton("Satın Al") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // Drop shadow
                    g2.setColor(new Color(0,0,0,30));
                    g2.fillRoundRect(4, getHeight()-10, getWidth()-8, 10, 14, 14);
                    // Button gradient
                    GradientPaint gp = new GradientPaint(
                        0, 0, mainColor.darker(),
                        getWidth(), getHeight(), mainColor
                    );
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            buyButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            buyButton.setForeground(Color.WHITE);
            buyButton.setBorderPainted(false);
            buyButton.setFocusPainted(false);
            buyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            buyButton.setPreferredSize(new Dimension(120, 44));
            buyButton.addActionListener(e -> {
                if (availableSeats <= 0) {
                    JOptionPane.showMessageDialog(this, "Üzgünüz, bu seferde boş koltuk kalmamıştır.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                JFrame seatFrame = new JFrame("Koltuk Seçimi");
                seatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                seatFrame.setContentPane(new view.SeatSelectionPanel(
                    trip.getVoyageId(),
                    trip.getSeatCount(),
                    trip.getSeatArrangement(),
                    customer,
                    mainView,
                    reservationsPanel
                ));
                seatFrame.pack();
                seatFrame.setLocationRelativeTo(null);
                seatFrame.setVisible(true);
            });
            panel.add(buyButton);
            return panel;
        }

        public void setAdminControlsVisible(boolean visible) {
            if (editButton != null && deleteButton != null) {
                editButton.setVisible(visible);
                deleteButton.setVisible(visible);
                editButton.getParent().setVisible(visible);
            }
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