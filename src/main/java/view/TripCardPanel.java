package view;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import models.Customer;
import commands.DeleteVoyageCommand;
import commands.CommandCaller;
import services.Admin;
import models.Voyage;
import java.text.SimpleDateFormat;

    // TripCardPanel: Modern card for a trip
    public class TripCardPanel extends JPanel {
        private final Customer customer;
        private final Voyage trip;

        // Custom EmojiLabel class for better emoji rendering
        private class EmojiLabel extends JLabel {
            private static final String[] EMOJI_FONTS = {
                "Segoe UI Emoji",
                "Apple Color Emoji",
                "Noto Color Emoji",
                "Android Emoji",
                "EmojiOne Color",
                "Twemoji Mozilla"
            };

            public EmojiLabel(String emoji) {
                super(emoji);
                setFont(findEmojiFont());
                setOpaque(true);
                setBackground(new Color(255, 255, 255));
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                    BorderFactory.createEmptyBorder(4, 6, 4, 6)
                ));
            }

            private Font findEmojiFont() {
                for (String fontName : EMOJI_FONTS) {
                    try {
                        Font font = new Font(fontName, Font.PLAIN, 14);
                        if (font.canDisplay('\uD83D')) { // Test if font can display emoji
                            return font;
                        }
                    } catch (Exception e) {
                        // Font not available, try next one
                    }
                }
                // Fallback to system default
                return new Font("Segoe UI Emoji", Font.PLAIN, 14);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                
                // Draw background with gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 255, 255),
                    0, getHeight(), new Color(245, 245, 245)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw border
                if (getBorder() instanceof CompoundBorder) {
                    CompoundBorder compoundBorder = (CompoundBorder) getBorder();
                    if (compoundBorder.getOutsideBorder() instanceof LineBorder) {
                        LineBorder lineBorder = (LineBorder) compoundBorder.getOutsideBorder();
                        g2.setColor(lineBorder.getLineColor());
                        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                    }
                }
                
                // Draw emoji with shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.drawString(getText(), 2, getHeight() - 8);
                g2.setColor(Color.BLACK);
                g2.drawString(getText(), 0, getHeight() - 10);
                
                g2.dispose();
            }
        }

        public TripCardPanel(Voyage trip, Customer customer) {
            this.customer = customer;
            this.trip = trip;
            setOpaque(false);
            setLayout(new BorderLayout());
            setBackground(new Color(0,0,0,0));

            // Card dimensions
            setPreferredSize(new Dimension(600, 200));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
            setMinimumSize(new Dimension(300, 200));

            // Main card panel with modern design
            JPanel card = new JPanel(new BorderLayout(15, 15));
            card.setBackground(Color.WHITE);
            card.setOpaque(true);
            card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(20, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
            ));

            // Top panel for toggle, time, and price
            JPanel topPanel = new JPanel(new BorderLayout(15, 0));
            topPanel.setOpaque(false);

            // Left: Toggle button and company info
            JPanel leftTopPanel = new JPanel();
            leftTopPanel.setLayout(new BoxLayout(leftTopPanel, BoxLayout.Y_AXIS));
            leftTopPanel.setOpaque(false);

            // Admin controls
            if (customer.getUser_type().equals("Admin")) {
                JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                adminPanel.setOpaque(false);
                
                JButton editButton = new JButton("✎") {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(52, 152, 219));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                        super.paintComponent(g2);
                        g2.dispose();
                    }
                };
                editButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
                editButton.setForeground(Color.WHITE);
                editButton.setBorderPainted(false);
                editButton.setFocusPainted(false);
                editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                editButton.setPreferredSize(new Dimension(35, 35));
                
                JButton deleteButton = new JButton("×") {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(231, 76, 60));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                        super.paintComponent(g2);
                        g2.dispose();
                    }
                };
                deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
                deleteButton.setForeground(Color.WHITE);
                deleteButton.setBorderPainted(false);
                deleteButton.setFocusPainted(false);
                deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                deleteButton.setPreferredSize(new Dimension(35, 35));
                
                adminPanel.add(editButton);
                adminPanel.add(deleteButton);
                
                // Add action listeners for admin buttons
                editButton.addActionListener(e -> {
                    if (customer.getUser_type().equals("Admin")) {
                        Admin admin = new Admin(customer.getId(), "1111", customer.getName(), customer.getEmail(), customer.getPassword());
                        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                        AdminVoyagePanel editPanel = new AdminVoyagePanel(admin, customer, topFrame, trip);
                        JDialog dialog = new JDialog(topFrame, "Sefer Düzenle", true);
                        dialog.setContentPane(editPanel);
                        dialog.pack();
                        dialog.setLocationRelativeTo(topFrame);
                        dialog.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Bu işlem için admin yetkisi gerekiyor!", 
                            "Hata", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
                
                deleteButton.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Bu seferi silmek istediğinizden emin misiniz?",
                        "Sefer Silme",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (customer.getUser_type().equals("Admin")) {
                            Admin admin = new Admin();
                            DeleteVoyageCommand deleteCmd = new DeleteVoyageCommand(trip, admin);
                            CommandCaller commandCaller = new CommandCaller();
                            commandCaller.executeCommand(deleteCmd);
                            
                            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                            if (topFrame != null) {
                                topFrame.dispose();
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                "Bu işlem için admin yetkisi gerekiyor!", 
                                "Hata", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                
                leftTopPanel.add(adminPanel);
            }

            // Company name
            JPanel companyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            companyPanel.setOpaque(false);
            JLabel companyLabel = new JLabel(trip.getFirm());
            companyLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            companyLabel.setForeground(new Color(60, 60, 60));
            companyPanel.add(companyLabel);
            leftTopPanel.add(companyPanel);

            // Seat arrangement
            JPanel seatArrangementPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            seatArrangementPanel.setOpaque(false);
            JLabel seatArrangementLabel = new JLabel("Koltuk Düzeni: " + trip.getSeatArrangement());
            seatArrangementLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            seatArrangementLabel.setForeground(new Color(80, 80, 80));
            seatArrangementPanel.add(seatArrangementLabel);
            leftTopPanel.add(seatArrangementPanel);

            topPanel.add(leftTopPanel, BorderLayout.WEST);

            // Center: Time information
            JPanel centerTopPanel = new JPanel();
            centerTopPanel.setLayout(new BoxLayout(centerTopPanel, BoxLayout.Y_AXIS));
            centerTopPanel.setOpaque(false);

            // Departure time
            JLabel timeLabel = new JLabel(trip.getStartTime().split(" ")[1]);
            timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            timeLabel.setForeground(new Color(52, 152, 219));
            timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerTopPanel.add(timeLabel);

            // Estimated duration
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                java.util.Date startDate = sdf.parse(trip.getStartTime().split(" ")[1]);
                java.util.Date arrivalDate = sdf.parse(trip.getArrivalTime().split(" ")[1]);
                long diffInMillis = arrivalDate.getTime() - startDate.getTime();
                long diffInHours = diffInMillis / (60 * 60 * 1000);
                long diffInMinutes = (diffInMillis / (60 * 1000)) % 60;
                
                JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                durationPanel.setOpaque(false);
                JLabel durationLabel = new JLabel("<html><i>Tahmini Süre: " + diffInHours + "s " + diffInMinutes + "dk</i></html>");
                durationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                durationLabel.setForeground(new Color(120,120,120));
                durationPanel.add(durationLabel);
                centerTopPanel.add(durationPanel);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            topPanel.add(centerTopPanel, BorderLayout.CENTER);

            // Right: Price
            JLabel priceLabel = new JLabel(String.format("%.2f ₺", trip.getPrice()));
            priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            priceLabel.setForeground(new Color(52, 152, 219));
            priceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            topPanel.add(priceLabel, BorderLayout.EAST);

            card.add(topPanel, BorderLayout.NORTH);

            // Main content panel
            JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
            contentPanel.setOpaque(false);
            card.add(contentPanel, BorderLayout.CENTER);

            // Bottom panel for route, features and buy button
            JPanel bottomPanel = new JPanel(new GridBagLayout());
            bottomPanel.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 15, 0, 15);
            
            // Left: Bus features
            JPanel featuresPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            featuresPanel.setOpaque(false);
            
            // Create emoji labels with custom rendering
            EmojiLabel busLabel = new EmojiLabel("🚌");
            EmojiLabel wifiLabel = new EmojiLabel("📶");
            EmojiLabel toiletLabel = new EmojiLabel("🚽");
            EmojiLabel foodLabel = new EmojiLabel("🍽️");
            
            // Set specific font for bus emoji to ensure modern version
            busLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            
            featuresPanel.add(busLabel);
            featuresPanel.add(wifiLabel);
            featuresPanel.add(toiletLabel);
            featuresPanel.add(foodLabel);
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(0, 0, 0, 15); // Align with admin buttons
            bottomPanel.add(featuresPanel, gbc);

            // Center: Route (aligned with departure time)
            JLabel routeLabel = new JLabel(trip.getOrigin() + " → " + trip.getDestination());
            routeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            routeLabel.setForeground(new Color(60, 60, 60));
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(0, 0, 0, 15);
            bottomPanel.add(routeLabel, gbc);

            // Right: Buy button
            JButton buyButton = new JButton("Satın Al") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(52, 152, 219));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            buyButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            buyButton.setForeground(Color.WHITE);
            buyButton.setBorderPainted(false);
            buyButton.setFocusPainted(false);
            buyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            buyButton.setPreferredSize(new Dimension(80, 25));
            buyButton.addActionListener(e -> {
                int voyageId = trip.getVoyageId();
                int seatCount = trip.getSeatCount();
                String seatArrangement = trip.getSeatArrangement();
                SeatSelectionPanel seatPanel = new SeatSelectionPanel(voyageId, seatCount, seatArrangement, customer);
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                JDialog dialog = new JDialog(topFrame, "Koltuk Seçimi", true);
                dialog.setContentPane(seatPanel);
                dialog.pack();
                dialog.setLocationRelativeTo(topFrame);
                dialog.setVisible(true);
            });
            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.weightx = 0.0;
            gbc.insets = new Insets(0, 15, 0, 15);
            bottomPanel.add(buyButton, gbc);

            card.add(bottomPanel, BorderLayout.SOUTH);

            add(card, BorderLayout.CENTER);
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